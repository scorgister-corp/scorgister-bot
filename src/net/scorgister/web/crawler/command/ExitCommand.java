package net.scorgister.web.crawler.command;

import net.scorgister.web.crawler.BotConsole;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit");
	}

	@Override
	public void execut(BotConsole console) {
		System.exit(0);

	}

}
