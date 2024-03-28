package net.scorgister.web.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Compiler  {
	
	public static void compile(List<String> files) {
		Map<String, List<PageData>> web = new HashMap<String, List<PageData>>();
		
		int fileCount = 0;
		
		for(String f : files) {
			File file = null;
	    	file = new File(f);
	    	if(!file.exists()) {
	    		System.out.println(f + " not found (skipped)");
	    		continue;
	    	}else if(file.isDirectory()) {
	    		System.out.println(f + " is a directory (skipped)");
	    		continue;
	    	}
			
			JsonParser parser = new JsonParser();
			JsonElement tree = null;
			try {
				tree = parser.parse(new FileReader(file));
			}catch (JsonSyntaxException | FileNotFoundException e) {
				System.out.println(f + " have an invalid json syntax (skipped)");
				continue;
			}
	    	
			try {
		        JsonObject objs = tree.getAsJsonObject();
		        
		        for(Entry<String, JsonElement> elts : objs.entrySet()) {
		        	
		        	boolean already = false;
		        	List<PageData> datas = new ArrayList<PageData>();
		        	if(web.containsKey(elts.getKey())) {
		        		datas = web.get(elts.getKey());
		        		already = true;
		        	}
		        	
		        	for(JsonElement elt : elts.getValue().getAsJsonArray()) {
		        		if(!elt.isJsonNull()) {
		        			JsonObject jobjs = elt.getAsJsonObject();	
		        			String url = jobjs.get("url").getAsString();
		        			String title = jobjs.get("title").getAsString();
		        			String mimeType = jobjs.get("mimeType").getAsString();
		        			
		        			PageData data = null;
		        			if((data = contains(url, datas)) != null) {
		        				if((data.getTitle() == null || data.getTitle().isEmpty()) && title != null && !title.isEmpty())
		        					data.setTitle(title);
		        				
		        				if((data.getMimeType() == null || data.getMimeType().isEmpty()) && mimeType != null && !mimeType.isEmpty())
		        					data.setMimeType(mimeType);
		
		        			}else {
		        				datas.add(new PageData(url, title, mimeType));
		        			}
		        			
		        		}
		        	}
		        	
		        	if(!already)
		        		web.put(elts.getKey(), datas);
		        }
			}catch(Exception e) {
				System.out.println(f + " have an invalid sitemap syntax (skipped)");
				continue;
			}
			fileCount++;
		}
		if(fileCount < 2) {
			System.out.println("Minimum 2 complete files (not saved)");
			return;
		}
		String fileName = "compilation-" + Math.abs(new Random().nextInt());
		save(web, fileName);
		System.out.println("Compilation saved @ " + fileName + ".json");
	}
	
	private static PageData contains(String url, List<PageData> datas) {
		for(PageData data : datas)
			if(data.getURL().equals(url))
				return data;
		return null;
	}
	
	private static void save(Map<String, List<PageData>> siteMap, String fileName) {
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
	        
		 try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".json"))) {
			 writer.write(json);
			 
		 }catch(IOException e) {
			 e.printStackTrace();
		 }
	}

}
