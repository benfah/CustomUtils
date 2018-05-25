package me.benfah.cu.main;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import me.benfah.cu.api.BlockInstance;
import me.benfah.cu.api.CustomBlock;
import me.benfah.cu.api.CustomGUI;
import me.benfah.cu.api.CustomItem;
import me.benfah.cu.api.CustomRegistry;
import me.benfah.cu.api.Initialization;
import me.benfah.cu.listener.BlockBreakListener;
import me.benfah.cu.listener.InventoryClickListener;
import me.benfah.cu.listener.InventoryCloseListener;
import me.benfah.cu.listener.ItemCraftListener;
import me.benfah.cu.listener.PlayerInteractEntityListener;
import me.benfah.cu.listener.PlayerInteractListener;
import me.benfah.cu.listener.PlayerJoinListener;
import me.benfah.cu.listener.SlotChangeListener;
import me.benfah.cu.util.JavassistUtil;
import me.benfah.cu.util.ReflectionUtils;
import me.benfah.cu.util.TickRunnable;
import me.benfah.cu.util.Utils;
public class CustomUtils extends JavaPlugin
{
	public static CustomUtils instance;
	public static YamlConfiguration cfg;
	public static File cfgFile;
	
	@Override
	public void onLoad() {
		super.onLoad();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(label.equalsIgnoreCase("customutils"))
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
				if(args.length == 0)
				{
					p.sendMessage("/customutils item {ITEM_ID} - Gives the player the specific custom item.");
					p.sendMessage("/customutils block {BLOCK_ID} - Gives the player the specific custom block.");
					p.sendMessage("/customutils reload - Reloads the config.");
					p.sendMessage("/customutils repackage - Repacks the resource pack and uploads it to Dropbox.");
					

				}
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("repackage"))
					{
						if(p.hasPermission(new Permission("cu.repackage", PermissionDefault.OP)))
						try {
							Initialization.initRP(true);
						} catch (IOException e) {
							e.printStackTrace();
						}
						else
						p.sendMessage("No permission!");	
					}
					else
					if(args[0].equalsIgnoreCase("reload"))
					{
						if(p.hasPermission(new Permission("cu.reload", PermissionDefault.OP)))
						{
							cfg = YamlConfiguration.loadConfiguration(cfgFile);
							p.sendMessage(ChatColor.GREEN + "Config reloaded!");
						}
					}
				}
				if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("block"))
					{
						for(CustomBlock cb : CustomRegistry.CUSTOM_BLOCK_REGISTRY)
						{
							if(args[1].equals(cb.getName()))
							{
								if(p.hasPermission(new Permission("cu.block." + cb.getName(), PermissionDefault.OP)))
								p.getInventory().addItem(cb.getBlockItem());
								else
								p.sendMessage("No permission!");
							}
						}
					}
					else
					if(args[0].equalsIgnoreCase("item"))
					{
						for(CustomItem ci : CustomRegistry.CUSTOM_ITEM_REGISTRY)
						{
							if(args[1].equals(ci.getName()))
							{
								if(p.hasPermission(new Permission("cu.item." + ci.getName(), PermissionDefault.OP)))
								p.getInventory().addItem(ci.getItem());
								else
								p.sendMessage("No permission!");
							}
						}
					}
					
				}
			
		}
		return false;
	}
	
	@Override
	public void onEnable()
	{

		JavassistUtil.getCUInventoryClass();
		JavassistUtil.getCUInventoryCustomClass();
		JavassistUtil.getContainerClass();
		instance = this;
		CustomRegistry.initMaps();

		Bukkit.getPluginManager().registerEvents(new SlotChangeListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemCraftListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
		
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
				try {
					Initialization.init(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
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
