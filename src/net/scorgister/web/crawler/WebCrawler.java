package net.scorgister.web.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebCrawler {
	
	private boolean running = false;
	
	private int length;
    
    private List<String> visitedURLs;
    private Map<String, List<PageData>> siteMap;
    
    private ExecutorService executor;
    private AutoSaver autoSaver;		

    private String rootURL;
    
    private Logger logger = Logger.getGlobal();
    
    public WebCrawler(String rootURL, int maxThreads) {
    	this.rootURL = rootURL;
        visitedURLs = new ArrayList<String>();
        siteMap = new HashMap<String, List<PageData>>();
        
        executor = Executors.newFixedThreadPool(maxThreads);
        autoSaver = new AutoSaver(this);
       /* 
        try {
			loadSiteMapToJson();
		}catch(FileNotFoundException e) {logger.log(Level.WARNING, "The sitemap is not loaded");}*/
    }
    
    public void crawl() {
    	running = true;
    	autoSaver.start();
    	
        visitedURLs.add(rootURL);
        
        executor.execute(new PageIndexer(rootURL, executor));
        log("Start crawling from: " + rootURL);
    }
    
    public void loadSiteMapToJson() throws FileNotFoundException {
    	File savedData = null;
    	try {
    		savedData = new File(new URL(rootURL).getHost() + ".json");
    	}catch(MalformedURLException e) {
    		logger.log(Level.WARNING, "Invalid URL");
    		return;
    	}
    	
    	if(!savedData.exists())
    		return;
    	
    	JsonParser parser = new JsonParser();
    	JsonElement tree = parser.parse(new FileReader(savedData));
    	
        JsonObject objs = tree.getAsJsonObject();
        
        for(Entry<String, JsonElement> elts : objs.entrySet()) {
        	List<String> listElt = new ArrayList<String>();
        	
        	for(JsonElement elt : elts.getValue().getAsJsonArray())
        		if(!elt.isJsonNull()) {
        			visitedURLs.add(elt.getAsString());
        			listElt.add(elt.getAsString());
        		}
        
        	//siteMap.put(elts.getKey(), listElt);
        }

    }
    
    
    public synchronized void saveSiteMapToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = null;
        do {
	        try {
	        	json = gson.toJson(siteMap);
	        	break;
	        }catch(ConcurrentModificationException e) {
	        	try {Thread.sleep(250);}
	        	catch(InterruptedException e1) {e1.printStackTrace();}
	        }
        }while(true);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new URL(rootURL).getHost() + ".json"))) {
            writer.write(json);
            
			log("SiteMap saved");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void log(String str) {
        logger.log(Level.INFO, str);
    }

    public List<String> getVisitedURLs() {
		return visitedURLs;
	}
    
    public Map<String, List<PageData>> getSiteMap() {
		return siteMap;
	}
    
    public String getRootURL() {
		return rootURL;
	}
    
    public ExecutorService getExecutor() {
		return executor;
	}
    
    public AutoSaver getAutoSaver() {
		return autoSaver;
	}
    
    public int getLength() {
		return length;
	}
    
    public boolean isRunning() {
		return running;
	}
    

    public void stop() {
    	saveSiteMapToJson();
    	
    	executor.shutdown();
    	
    	autoSaver.stopAutoSaver();
    }
    
    private class PageIndexer implements Runnable {
    	
    	private final String url;
        
        private ExecutorService executor;
        
        public PageIndexer(String url, ExecutorService executor) {
            this.url = url;
            this.executor = executor;
        }
        
        @Override
        public void run() {
        	String rawHTML = "";
        	String baseUrl = "";
        	
        	String title = "";
        	String mime = "";
        	
        	//int code = -1;
        	try {
        	    URL urlObj = new URL(url);
        	    
        	    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        	    conn.setRequestProperty("User-Agent", "Scorgisterbot");
        	   // code = conn.getResponseCode();

        	    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        	    String inputLine;

        	    while ((inputLine = in.readLine()) != null)
        	        rawHTML += inputLine;

        	    in.close();
        	    baseUrl = urlObj.getProtocol() + "://" + urlObj.getHost(); // Get base URL for the current page

        	    Pattern pattern = Pattern.compile("<title>(.*?)</title>");
            	Matcher matcher = pattern.matcher(rawHTML);
            	
            	String contentType = conn.getContentType();
            	if(contentType != null) {
            		if(contentType.contains(";"))
            			mime = contentType.substring(0, contentType.indexOf(';'));
            		else
            			mime = contentType;
            	}
        	    
        	    if(matcher.find()) {
        	    	title = matcher.group(1);
        	    }
        	    
        	  /*  if(code == 301) {
        	        try {
            	        if(!executor.isShutdown())
            	            executor.execute(new PageIndexer(conn.getHeaderField("location"), executor));
            	    }catch(RejectedExecutionException e) {}
        	    	return;
        	    }*/
        	    
        	    String siteName = getSiteName(url);
    	        if(!siteMap.containsKey(siteName))
    	            siteMap.put(siteName, new ArrayList<>());

    	        siteMap.get(siteName).add(new PageData(url, title, mime));
    	        length++;
        	    
        	}catch(IOException e) {
        		String siteName = getSiteName(url);
        		if(!siteMap.containsKey(siteName))
     	            siteMap.put(siteName, new ArrayList<>());

     	        siteMap.get(siteName).add(new PageData(url, title, mime));
         	    length++;
        		return;
        	}


        	// Regular expression pattern to match complete URLs
        	String urlPattern = "(https?|http):\\/\\/[a-z0-9\\/:%_+.,#?!@&=-]+";
        	Pattern pattern = Pattern.compile(urlPattern);
        	Matcher matcher = pattern.matcher(rawHTML);

        	while(matcher.find()) {
        	    String actualURL = matcher.group();

        	    if(!visitedURLs.contains(actualURL)) {
        	        visitedURLs.add(actualURL);
        	    
	        	    try {
	        	        if(!executor.isShutdown())
	        	            executor.execute(new PageIndexer(actualURL, executor));
	        	    }catch(RejectedExecutionException e) {}
        	    }
        	}

        	// Regular expression pattern to match links within the same site
        	String sameSiteUrlPattern = "<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1";
        	Pattern sameSitePattern = Pattern.compile(sameSiteUrlPattern);
        	Matcher sameSiteMatcher = sameSitePattern.matcher(rawHTML);

        	while(sameSiteMatcher.find()) {
        	    String link = sameSiteMatcher.group(2);

        	    // Handling relative URLs
        	    if(!link.startsWith("http")) {
        	        try {
        	            URL absoluteUrl = new URL(url);
        	            URL resolvedUrl = new URL(absoluteUrl, link);
        	            
        	            link = resolvedUrl.toString();
        	        }catch(MalformedURLException e) {continue;}
        	    }
        	    // Checking if the link is within the same site
        	    if(link.startsWith(baseUrl)) {
        	        if(!visitedURLs.contains(link)) {
        	            visitedURLs.add(link);
        	            
	        	        try {
	        	            if(!executor.isShutdown())
	        	                executor.execute(new PageIndexer(link, executor));
	        	            
	        	        }catch(RejectedExecutionException e) {}
        	        }
        	    }
        	}
        }
        
        private String getSiteName(String url) {
            String siteName = url.replaceAll("^(https?://)?(www\\.)?", "");
            int index = siteName.indexOf('/');
            if (index != -1)
                siteName = siteName.substring(0, index);
            
            return siteName;
        }
    }
    
}