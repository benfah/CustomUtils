package me.benfah.cu.cmd;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import me.benfah.cu.api.Initialization;

public class IRepackageCommand implements ISubCommand
{

	@Override
	public String commandName()
	{
		return "repackage";
	}

	@Override
	public String[] argumentNames()
	{
		return new String[] {};
	}

	@Override
	public String description()
	{
		return "Repacks the resource pack and uploads it to Dropbox.";
	}

	@Override
	public boolean playerOnly()
	{
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender.hasPermission(new Permission("cu.repackage", PermissionDefault.OP)))
			try {
				Initialization.initRP(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
		sender.sendMessage("No permission!");
		return false;
	}

}
