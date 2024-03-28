package net.scorgister.web.crawler.command;

import net.scorgister.web.crawler.BotConsole;
import net.scorgister.web.crawler.WebCrawler;

public class StartCommand extends Command {

	public StartCommand() {
		super("start");
	}

	@Override
	public void execut(BotConsole console) {
		String rootURL = console.askString("Root URL");
		int maxThreads = console.askInt("Max threads");
		
		WebCrawler crawler = new WebCrawler(rootURL, maxThreads);
		console.addWebCrawler(crawler);
		
		crawler.crawl();
	}

}
