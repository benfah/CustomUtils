package me.benfah.cu.listener;

import java.io.UnsupportedEncodingException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.benfah.cu.api.Initialization;
import me.benfah.cu.main.CustomUtils;
import me.benfah.cu.util.InitializationState;

public class PlayerJoinListener implements Listener
{
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e)
	{
		if(Initialization.getCurrentMethod().getInitializationState() == InitializationState.INITIALIZING)
		e.getPlayer().kickPlayer("Wait until you can join");	
		Bukkit.getScheduler().runTaskLater(CustomUtils.instance, new Runnable() {
			
			
			public void run()
			{
				if(CustomUtils.cfg.getBoolean("send-resourcepack-request"))
				e.getPlayer().setResourcePack(CustomUtils.cfg.getString("resourcepack-link"));
				if(Initialization.getCurrentMethod().getInitializationState().equals(InitializationState.INITIALIZING))
				e.getPlayer().kickPlayer("Wait until the server initialized!");	
			}
		}, 40);
	}
}
