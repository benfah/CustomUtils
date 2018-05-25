package me.benfah.cu.api;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.v1_12_R1.Item;


public class CustomBase
{
	protected Plugin plugin;
	protected String name;
	
	protected short id1 = 0;
	
	
	public short getId() {
		return id1;
	}

	
	protected String pathToModel1;
	protected Material baseMaterial;

	
	
	public String getName()
	{
		return name;
	}
	
	
	
	public CustomBase(String name, String modelPath, Material baseMaterial)
	{
		this.pathToModel1 = modelPath;
		this.name = name;	
		this.baseMaterial = baseMaterial;
		
	}
}
