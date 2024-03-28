package net.scorgister.web.crawler.command;

import java.util.ArrayList;
import java.util.List;

import net.scorgister.web.crawler.BotConsole;
import net.scorgister.web.crawler.Compiler;

public class CompileCommand extends Command {

	public CompileCommand() {
		super("compile");
	}

	@Override
	public void execut(BotConsole console) {
		List<String> files = new ArrayList<String>();
		while(true) {
			String f = console.askString("Path " + (files.size() + 1));
			if(f == null || f.isEmpty()) 
				break;
			
			files.add(f);
		}
		
		if(files.size() < 2) {
			System.out.println("Minimum 2 files (exited)");
			return;
		}
		
		Compiler.compile(files);
	}

}
