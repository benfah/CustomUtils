package me.benfah.cu.api;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;


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
	
	public Material getBaseMaterial()
	{
		return baseMaterial;
	}
}
