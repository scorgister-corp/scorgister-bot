package net.scorgister.web.crawler;

import java.io.IOException;

public class MainBot {
	
	private static BotConsole console;
	
    public static void main(String[] args) throws SecurityException, IOException {
        console = new BotConsole();
        
        console.start();
    }

}
