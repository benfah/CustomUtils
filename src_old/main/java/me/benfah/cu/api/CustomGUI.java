package me.benfah.cu.api;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.benfah.cu.util.JavassistUtil;
import me.benfah.cu.util.ReflectionUtils;
import me.benfah.cu.util.Utils;
import net.minecraft.server.v1_12_R1.ChatClickable;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.ChatHoverable;
import net.minecraft.server.v1_12_R1.ChatModifier;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenWindow;

public class CustomGUI extends CustomBase
{
	

	protected int size;
	protected String pathToModel2;
	protected String name2;
	protected short id2;
	
	
	public short getId2() {
		return id2;
	}






	public CustomGUI(String name, String pathToUpperModel, String pathToLowerModel, int size)
	{
		
		super(name + "1", pathToUpperModel, Material.DIAMOND_HOE);
		this.name2 = name + "2";
		this.pathToModel2 = pathToLowerModel;
		this.size = size;
	}
	public CustomGUI(String name, String pathToUpperModel, String pathToLowerModel, int size, Material baseMat)
	{
		
		super(name + "1", pathToUpperModel, baseMat);
		this.name2 = name + "2";
		this.pathToModel2 = pathToLowerModel;
		this.size = size;
	}
	
	
	
	
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Plugin getPlugin() {
		return plugin;
	}
	
	public void onClick(InventoryClickEvent e)
	{
		
		
	}
	
	
	protected void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	
	protected ItemStack getUpperStack()
	{
		ItemStack upper = new ItemStack(Material.DIAMOND_HOE);
		upper.setDurability(id1);
		ItemMeta im = upper.getItemMeta();
		im.setDisplayName("");
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		upper.setItemMeta(im);
		upper = Utils.updateTags(upper);
		return upper;
	}
	
	protected ItemStack getLowerStack()
	{

		ItemStack lower = new ItemStack(Material.DIAMOND_HOE);
		lower.setDurability(id2);
		ItemMeta lowerim = lower.getItemMeta();
		lowerim.setUnbreakable(true);
		lowerim.setDisplayName("");
		lowerim.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		lower.setItemMeta(lowerim);
		lower = Utils.updateTags(lower);
		return lower;
	}
	
	
	
	public CInventory createInventory()
	{
		return createInventory("");
	}
	
	
	public CInventory createInventory(String title)
	{
		
		
		Inventory inv = /*Bukkit.createInventory(null, size, title)*/ null;
		
		
		try
		{
			inv = (Inventory) JavassistUtil.getCUInventoryCustomClass().getConstructor(InventoryHolder.class, int.class, String.class).newInstance(null, size, title);
			JavassistUtil.getCUInventoryCustomClass().getField("customGui").set(inv, this.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if(id1 != -1)
		inv.setItem(0, getUpperStack());
		if(id2 != -1)
		inv.setItem(inv.getSize() - 9, getLowerStack());
		
		inv.setItem(5, new ItemStack(Material.ANVIL));
		return new CInventory(inv, this);
		
		
	}
	
	public void onClose(InventoryCloseEvent e)
	{
		
	}
	
	
	
	
	public String getName() {
		return name;
	}

	
	
}
