package net.scorgister.web.crawler.command;

import net.scorgister.web.crawler.BotConsole;
import net.scorgister.web.crawler.WebCrawler;

public class SizeCommand extends Command {

	public SizeCommand() {
		super("size");
	}

	@Override
	public void execut(BotConsole console) {
		WebCrawler crawler = console.askCrawler();
		if(crawler == null) {
			System.out.println("No crawling selected");
			return;
		}
		
		System.out.println(crawler.getVisitedURLs().size());
	}

}
