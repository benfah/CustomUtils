package me.benfah.cu.cmd;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import me.benfah.cu.api.CustomBlock;
import me.benfah.cu.api.CustomItem;
import me.benfah.cu.api.CustomRegistry;
import me.benfah.cu.api.InitializationMethodProvider;
import me.benfah.cu.main.CustomUtils;

public class CustomUtilsCommandExecutor implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(label.equalsIgnoreCase("customutils"))
		{
			if(args.length != 0)
			{	
				for(ISubCommand cmd : CommandRegistry.getSubCommands())
				{
					if(args[0].equals(cmd.commandName()))
					{
						if(cmd.playerOnly())
						{
							if(sender instanceof Player)
							return cmd.onCommand(sender, command, label, args);
						}
						else
						return cmd.onCommand(sender, command, label, args);
					}
				}
			}
			else
			{
				for(ISubCommand cmd : CommandRegistry.getSubCommands())
				{
					String printCmd = "/customutils " + cmd.commandName();
					for(String arg : cmd.argumentNames())
					{
						printCmd = printCmd + " " + arg;
					}
					printCmd = printCmd + " - " + cmd.description();
					sender.sendMessage(ChatColor.GOLD + printCmd);
				}
			}
		}
			return false;
	}

}
