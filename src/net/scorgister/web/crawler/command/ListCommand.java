package net.scorgister.web.crawler.command;

import net.scorgister.web.crawler.BotConsole;

public class ListCommand extends Command {

	public ListCommand() {
		super("list");
	}

	@Override
	public void execut(BotConsole console) {
		console.printCrawlerList();
	}

}
