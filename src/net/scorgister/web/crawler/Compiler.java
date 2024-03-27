package net.scorgister.web.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Compiler  {
	
	public static void compile(List<String> files) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Map<String, PageData> web = new HashMap<String, PageData>();
		
		for(String f : files) {
			
			File file = null;
	    	file = new File(f);
			
			JsonParser parser = new JsonParser();
	    	JsonElement tree = parser.parse(new FileReader(file));
	    	
	        JsonObject objs = tree.getAsJsonObject();
	        
	        for(Entry<String, JsonElement> elts : objs.entrySet()) {
	        	List<String> listElt = new ArrayList<String>();
	        	
	        	for(JsonElement elt : elts.getValue().getAsJsonArray())
	        		if(!elt.isJsonNull()) {
	        			JsonObject jobjs = new JsonObject();
	        			if(web.containsKey(elts.getKey())) {
	        				jobjs = elt.getAsJsonObject();	        				
	        			}
	        			
	        			for(Entry<String, JsonElement> e : jobjs.entrySet()) {
	        				System.out.println(e.getKey());
	        				
	        			}
	        			//visitedURLs.add(elt.getAsString());
	        			//listElt.add(elt.getAsString());
	        		}
	        
	        	//siteMap.put(elts.getKey(), listElt);
	        }
		}
	}

}
