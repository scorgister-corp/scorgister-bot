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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	
	private boolean running = false;;
    
    private Queue<String> urlQueue;
    private List<String> visitedURLs;
    private Map<String, List<String>> siteMap;
    
    private ExecutorService executor;
    private AutoSaver autoSaver;		

    private String rootURL;
    
    private Logger logger = Logger.getGlobal();
    
    public WebCrawler(String rootURL, int maxThreads) {
    	this.rootURL = rootURL;
        urlQueue = new ConcurrentLinkedQueue<String>();
        visitedURLs = new ArrayList<String>();
        siteMap = new HashMap<String, List<String>>();
        
        executor = Executors.newFixedThreadPool(maxThreads);
        autoSaver = new AutoSaver(this);
        
        try {
			loadSiteMapToJson();
		}catch(FileNotFoundException e) {logger.log(Level.WARNING, "The sitemap is not loaded");}
    }
    
    public void crawl() {
    	running = true;
    	autoSaver.start();
    	
        urlQueue.add(rootURL);
        visitedURLs.add(rootURL);
        
        String url = urlQueue.poll();
        executor.execute(new PageIndexer(url, executor));
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
        
        	siteMap.put(elts.getKey(), listElt);
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
    
    public Queue<String> getURLQueue() {
		return urlQueue;
	}
    
    public List<String> getVisitedURLs() {
		return visitedURLs;
	}
    
    public Map<String, List<String>> getSiteMap() {
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
            
            try {
                URL urlObj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setRequestProperty("User-Agent", "Scorgisterbot");
                
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                
                while((inputLine = in.readLine()) != null)
                    rawHTML += inputLine;
                
                in.close();
            }catch(IOException e) {
                return;
            }
            
            String urlPattern = "(https?):\\/\\/[a-z0-9\\/:%_+.,#?!@&=-]+";
            Pattern pattern = Pattern.compile(urlPattern);
            Matcher matcher = pattern.matcher(rawHTML);
            
            while(matcher.find()) {
                String actualURL = matcher.group();

                if(!visitedURLs.contains(actualURL)) {
                    visitedURLs.add(actualURL);
                    
                    urlQueue.add(actualURL);
                    
                    String siteName = getSiteName(actualURL);
                    if(!siteMap.containsKey(siteName))
                        siteMap.put(siteName, new ArrayList<>());
                    
                    siteMap.get(siteName).add(actualURL);
                }
                try {
                	if(!executor.isShutdown())
                		executor.execute(new PageIndexer(actualURL, executor));
                }catch(RejectedExecutionException e) {}
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