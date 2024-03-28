package net.scorgister.web.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.scorgister.web.crawler.command.Command;
import net.scorgister.web.crawler.command.Commands;

public class BotConsole extends Thread {
	
	public static final String HELLO_WORLD = "  /$$$$$$                                          /$$             /$$                               /$$$$$$$              /$$    \r\n" + 
			" /$$__  $$                                        |__/            | $$                              | $$__  $$            | $$    \r\n" + 
			"| $$  \\__/  /$$$$$$$  /$$$$$$   /$$$$$$   /$$$$$$  /$$  /$$$$$$$ /$$$$$$    /$$$$$$   /$$$$$$       | $$  \\ $$  /$$$$$$  /$$$$$$  \r\n" + 
			"|  $$$$$$  /$$_____/ /$$__  $$ /$$__  $$ /$$__  $$| $$ /$$_____/|_  $$_/   /$$__  $$ /$$__  $$      | $$$$$$$  /$$__  $$|_  $$_/  \r\n" + 
			" \\____  $$| $$      | $$  \\ $$| $$  \\__/| $$  \\ $$| $$|  $$$$$$   | $$    | $$$$$$$$| $$  \\__/      | $$__  $$| $$  \\ $$  | $$    \r\n" + 
			" /$$  \\ $$| $$      | $$  | $$| $$      | $$  | $$| $$ \\____  $$  | $$ /$$| $$_____/| $$            | $$  \\ $$| $$  | $$  | $$ /$$\r\n" + 
			"|  $$$$$$/|  $$$$$$$|  $$$$$$/| $$      |  $$$$$$$| $$ /$$$$$$$/  |  $$$$/|  $$$$$$$| $$            | $$$$$$$/|  $$$$$$/  |  $$$$/\r\n" + 
			" \\______/  \\_______/ \\______/ |__/       \\____  $$|__/|_______/    \\___/   \\_______/|__/            |_______/  \\______/    \\___/  \r\n" + 
			"                                         /$$  \\ $$                                                                                \r\n" + 
			"                                        |  $$$$$$/                                                                                \r\n" + 
			"                                         \\______/                                                                                 ";
	private Scanner scan;
	private List<WebCrawler> crawlers = new ArrayList<WebCrawler>();
	
	public BotConsole() {
		this.scan = new Scanner(System.in);
		Commands.init();
	}
	
	@Override
	public void run() {
		System.out.println(HELLO_WORLD);
		
		while(true) {
			String cmd = askString(">>");
			
			Command.execut(cmd, this);
		}
	}
	
	public void addWebCrawler(WebCrawler crawler) {
		crawlers.add(crawler);
	}
	
	public void removeWebCrawler(WebCrawler crawler) {
		crawlers.remove(crawler);
	}
	
	public void printCrawlerList() {
		System.out.println();
		for(int i = 0; i < crawlers.size(); i++)
			System.out.println(i + ": " + crawlers.get(i).getRootURL());
		System.out.println();
	}
	
	public String askString(String txt) {
		System.out.print(txt + ": ");
		return scan.nextLine();
	}
	
	public WebCrawler askCrawler() {
		if(crawlers.size()-1 < 0)
			return null;
		
		printCrawlerList();
		
		int i = askInt("Crawler index", 0, crawlers.size()-1>=0?crawlers.size()-1:0);
		if(i == -1)
			return null;
		
		return crawlers.get(i);
	}
	
	/**
	 * i in [min; max]
	 * @param txt
	 * @param min
	 * @param max
	 * @return
	 */
	public int askInt(String txt, int min, int max) {
		while(true) {
			int i = askInt(txt + " [" + min + "; " + max + "]");
			if(min <= i && i <= max)
				return i;
		}
	}
	
	public int askInt(String txt) {
		while(true)
			try {
				String str = askString(txt);
				if(str.equals("q"))
					return -1;
				
				return Integer.valueOf(str);
			}catch (Exception e) {
				System.err.println("Type error (int)");
			}
	}
	
	public float askFloat(String txt) {
		while(true)
			try {
				String str = askString(txt);
				if(str.equals("q"))
					return -1;
				
				return Float.valueOf(str);
			}catch (Exception e) {
				System.err.println("Type error (float)");
			}
	}

	public boolean askBoolean(String txt) {
		while(true)
			try {
				String str = askString(txt);
				if(str.equals("q"))
					return false;
				
				return Boolean.valueOf(str);
			}catch (Exception e) {
				System.err.println("Type error (boolean)");
			}
	}

}
