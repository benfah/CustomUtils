package me.benfah.cu.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.benfah.cu.api.CustomBlock;
import me.benfah.cu.api.CustomItem;
import me.benfah.cu.api.CustomRegistry;


public class Utils
{
	public static boolean compareBlocks(CustomBlock cb, Block b)
	{
		try {
		
		Class<?> craftCreatureSpawnerClass = ReflectionUtils.getRefClass("{cb}.block.CraftCreatureSpawner");
		Class<?> mobSpawnerAbstractClass = ReflectionUtils.getRefClass("{nms}.MobSpawnerAbstract");
		ReflectionUtils.getRefClass("{nms}.MobSpawnerData");
		ReflectionUtils.getRefClass("{nms}.NBTTagList");

		CreatureSpawner cs = (CreatureSpawner) b.getState();
		
		Object tileEntity = setAccessible(craftCreatureSpawnerClass.getSuperclass().getDeclaredMethod("getTileEntity")).invoke(cs);
		Object msa = tileEntity.getClass().getDeclaredMethod("getSpawner").invoke(tileEntity);
		Field f = mobSpawnerAbstractClass.getDeclaredField("spawnData");
		
		f.setAccessible(true);
		Object msd = f.get(msa);
		Object nbtTagCompound = msd.getClass().getDeclaredMethod("b").invoke(msd);
		Object nbtList = nbtTagCompound.getClass().getMethod("get", String.class).invoke(nbtTagCompound, "ArmorItems");
		Object nbtTagCompound2 = nbtList.getClass().getDeclaredMethod("get", int.class).invoke(nbtList, 3);
		String itemtype = (String) nbtTagCompound2.getClass().getMethod("getString", String.class).invoke(nbtTagCompound2, "id");
		short damage = (short) nbtTagCompound2.getClass().getMethod("getShort", String.class).invoke(nbtTagCompound2, "Damage");
		if(itemtype.contains(Utils.getUnlocalizedName(cb.getBaseMaterial())))
		{
			if(damage == cb.getId())
			{
				return true;
			}
		}
		
		} catch (Exception e) {
			return false;
		} 
		 
		return false;
	}
	public static CustomItem getMainOrOffHandCI(PlayerEvent e)
	{
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInMainHand()))
		{
			return CustomRegistry.getCustomItem(e.getPlayer().getInventory().getItemInMainHand());
		}
		else
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInOffHand()))
		{
			return CustomRegistry.getCustomItem(e.getPlayer().getInventory().getItemInOffHand());
		}
		return null;
	}
	public static ItemStack getMainOrOffHandStack(PlayerEvent e)
	{
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInMainHand()))
		{
			return e.getPlayer().getInventory().getItemInMainHand();
		}
		else
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInOffHand()))
		{
			return e.getPlayer().getInventory().getItemInOffHand();
		}
		return null;
	}
	public static void setMainOrOffHandStack(PlayerEvent e, ItemStack stack)
	{
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInMainHand()))
		{
			e.getPlayer().getInventory().setItemInMainHand(stack);
		}
		else
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInOffHand()))
		{
			e.getPlayer().getInventory().setItemInOffHand(stack);
		}
	}
	
	public static ItemStack updateTags(ItemStack stack)
	{
		ItemMeta meta = stack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static EquipmentSlot getEquipmentSlot(PlayerEvent e)
	{
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInMainHand()))
		{
			return EquipmentSlot.HAND;
		}
		else
		if(CustomRegistry.isCustomItem(e.getPlayer().getInventory().getItemInOffHand()))
		{
			return EquipmentSlot.OFF_HAND;
		}
		return null;
	}
	
	public static String getUnlocalizedName(Material mat)
	{
		Class<?> matClass = ReflectionUtils.getRefClass("{cb}.util.CraftMagicNumbers");
		Class<?> itemClass = ReflectionUtils.getRefClass("{nms}.Item");
		try {
			Object item = matClass.getMethod("getItem", Material.class).invoke(null, mat);
			Object registryMaterials = itemClass.getField("REGISTRY").get(null);
			Object mcKey = registryMaterials.getClass().getMethod("b", Object.class).invoke(registryMaterials, item);
			return (String) mcKey.getClass().getMethod("getKey").invoke(mcKey);
			
		} catch (ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getMaxDurability(Material mat)
	{
		Class<?> matClass = ReflectionUtils.getRefClass("{cb}.util.CraftMagicNumbers");
		Class<?> itemClass = ReflectionUtils.getRefClass("{nms}.Item");
		try {
			Object item = matClass.getMethod("getItem", Material.class).invoke(null, mat);
			return (int) itemClass.getMethod("getMaxDurability").invoke(item);
			
			
		} catch (ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Method setAccessible(Method f)
	{
		f.setAccessible(true);
		return f;
	}
}
