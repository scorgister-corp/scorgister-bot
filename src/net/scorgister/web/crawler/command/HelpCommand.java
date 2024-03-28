package net.scorgister.web.crawler.command;

import java.util.List;

import net.scorgister.web.crawler.BotConsole;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help");
	}

	@Override
	public void execut(BotConsole console) {
		List<Command> commands = getCommands();
		System.out.println("----------HELP----------");
		for(Command com : commands) {
			System.out.println("\t" + com.getName());
		}
	}

}
