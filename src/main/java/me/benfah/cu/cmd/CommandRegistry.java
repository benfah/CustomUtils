package me.benfah.cu.cmd;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry
{
	private static List<ISubCommand> subCommands = new ArrayList<>();
	
	
	public static void initSubCommands()
	{
		subCommands.clear();
		subCommands.add(new IBlockCommand());
		subCommands.add(new IItemCommand());
		subCommands.add(new IReloadCommand());
		subCommands.add(new IRepackageCommand());
	}
	
	public static List<ISubCommand> getSubCommands()
	{
		return subCommands;
	}
}
