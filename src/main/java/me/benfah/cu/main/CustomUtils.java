package me.benfah.cu.main;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import me.benfah.cu.api.BlockInstance;
import me.benfah.cu.api.CustomBlock;
import me.benfah.cu.api.CustomItem;
import me.benfah.cu.api.CustomRegistry;
import me.benfah.cu.api.Initialization;
import me.benfah.cu.cmd.CommandRegistry;
import me.benfah.cu.cmd.CustomUtilsCommandExecutor;
import me.benfah.cu.init.impl.DropBoxInitializationMethod;
import me.benfah.cu.init.impl.MinePackInitializationMethod;
import me.benfah.cu.listener.BlockBreakListener;
import me.benfah.cu.listener.InventoryClickListener;
import me.benfah.cu.listener.InventoryCloseListener;
import me.benfah.cu.listener.ItemCraftListener;
import me.benfah.cu.listener.PlayerInteractEntityListener;
import me.benfah.cu.listener.PlayerInteractListener;
import me.benfah.cu.listener.PlayerJoinListener;
import me.benfah.cu.listener.SlotChangeListener;
import me.benfah.cu.util.JavassistUtil;
import me.benfah.cu.util.TickRunnable;
public class CustomUtils extends JavaPlugin
{
	public static CustomUtils instance;
	public static YamlConfiguration cfg;
	public static File cfgFile;
	
	
	
	
	
	@Override
	public void onEnable()
	{

		JavassistUtil.getCUInventoryClass();
		JavassistUtil.getCUInventoryCustomClass();
		JavassistUtil.getContainerClass();
		instance = this;
		CustomRegistry.initMaps();
		Initialization.setCurrentMethod(new DropBoxInitializationMethod());
		Bukkit.getPluginManager().registerEvents(new SlotChangeListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemCraftListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
		CommandRegistry.initSubCommands();
		Bukkit.getPluginCommand("customutils").setExecutor(new CustomUtilsCommandExecutor());
		System.out.println("####### CUSTOMUTILS #######");
		cfgFile = new File(getDataFolder(), "cfg.yml");
		if(!cfgFile.exists())
			try {
				cfgFile.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			}
		cfg = YamlConfiguration.loadConfiguration(cfgFile);
		
		setStandardConfigValues();
		
		Bukkit.getScheduler().runTask(instance, new Runnable() {
				
			@Override
			public void run() {
				Initialization.init(false);
				
				
			}
		});
		

        Bukkit.getScheduler().runTaskTimer(instance, new TickRunnable(), 1, 1);
	}
	
	
	
	
	
	@Override
	public void onDisable()
	{
		
		
		for(Entry<Location, BlockInstance> entr : BlockInstance.getBlockInstanceMap().entrySet())
		{
			if(entr.getValue() != null)
			entr.getValue().write();
		}
		
		CustomRegistry.saveIDNameMap();
		CustomRegistry.saveWorldStores();
		
		try {
			cfg.save(cfgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void setStandardConfigValues()
	{
		setDefault("send-resourcepack-request", true);
		
		try {
			cfg.save(cfgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void setDefault(String key, Object value)
	{
		if(!cfg.contains(key))
		cfg.set(key, value);
	}
	
}
