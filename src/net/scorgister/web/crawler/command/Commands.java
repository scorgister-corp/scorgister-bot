package net.scorgister.web.crawler.command;

public class Commands {
	
	public static final CompileCommand COMPILE_COMMAND = new CompileCommand();
	public static final ExitCommand EXIT_COMMAND = new ExitCommand();
	public static final LengthCommand LENGTH_COMMAND = new LengthCommand();
	public static final ListCommand LIST_COMMAND = new ListCommand();
	public static final RatioCommand RATIO_COMMAND = new RatioCommand();
	public static final SaveCommand SAVE_COMMAND = new SaveCommand();
	public static final SizeCommand SIZE_COMMAND = new SizeCommand();
	public static final StartCommand START_COMMAND= new StartCommand();
	public static final StopCommand STOP_COMMAND = new StopCommand();
	
	public static final HelpCommand HELP_COMMAND= new HelpCommand();

	public static void init() {}

}
