package net.scorgister.web.crawler.command;

import net.scorgister.web.crawler.BotConsole;
import net.scorgister.web.crawler.WebCrawler;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop");
	}

	@Override
	public void execut(BotConsole console) {
		WebCrawler crawler = console.askCrawler();
		if(crawler == null) {
			System.out.println("No crawling selected");
			return;
		}
		
		crawler.stop();
		console.removeWebCrawler(crawler);
	}

}
