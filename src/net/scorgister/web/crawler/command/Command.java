package net.scorgister.web.crawler.command;

import java.util.ArrayList;
import java.util.List;

import net.scorgister.web.crawler.BotConsole;

public abstract class Command {
	
	private static List<Command> commands = new ArrayList<Command>();
	
	private String name;
	
	public Command(String name) {
		this.name = name;
		commands.add(this);
	}
	
	public static Command getCommand(String name) {
		for(Command com : commands) 
			if(com.getName().equals(name))
				return com;
		return null;
	}
	
	public static void execut(String name, BotConsole console) {
		Command com = getCommand(name);
		if(com == null) {
			System.out.println("Command not found");
			return;
		}
		
		com.execut(console);
	}
	
	public abstract void execut(BotConsole console);
	
	public String getName() {
		return name;
	}
	
	public static List<Command> getCommands() {
		return commands;
	}

}
