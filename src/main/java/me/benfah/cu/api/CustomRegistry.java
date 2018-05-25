package me.benfah.cu.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.HashBiMap;

import me.benfah.cu.main.CustomUtils;
import me.benfah.cu.util.JavassistUtil;
import me.benfah.cu.util.Utils;

public class CustomRegistry
{
	public static final ArrayList<CustomBlock> CUSTOM_BLOCK_REGISTRY = new ArrayList<CustomBlock>(); 
	public static final ArrayList<CustomGUI> CUSTOM_GUI_REGISTRY = new ArrayList<CustomGUI>();
	public static final ArrayList<CustomItem> CUSTOM_ITEM_REGISTRY = new ArrayList<CustomItem>(); 
	public static final ArrayList<JavaPlugin> PLUGIN_REGISTRY = new ArrayList<JavaPlugin>();
	public static HashMap<String, Integer> ID_NAME_MAP;
	public static final HashBiMap<String, Class<? extends BlockInstance>> BLOCK_INSTANCE_MAP = HashBiMap.create();
	public static final Set<WorldStore> worldStoreList = new HashSet<WorldStore>();

	
	
	
	
	
	
	
	public static void initMaps()
	{
		CustomUtils.instance.getDataFolder().mkdirs();
		File f = new File(CustomUtils.instance.getDataFolder(), "idnames.map");
		if(f.exists())
		{
			try{
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ID_NAME_MAP = (HashMap<String, Integer>) ois.readObject();
			if(ID_NAME_MAP == null) ID_NAME_MAP = new HashMap<String, Integer>();
			ois.close();
			fis.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else
		ID_NAME_MAP = new HashMap<String, Integer>();	
		
		
		
	}
	
	public static void saveWorldStores()
	{
		for(WorldStore ws : worldStoreList)
		{
			ws.save();
		}
	}
	
	public static WorldStore getWorldStore(World w)
	{
		for(WorldStore ws : worldStoreList)
		{
			if(ws.world.getName().equals(w.getName()))
			return ws;
		}
		
		return null;
	}
	
	public static List<Material> getUsedBaseMaterials()
	{
		ArrayList<Material> al = new ArrayList<>();
		
		for(CustomBlock cb : CUSTOM_BLOCK_REGISTRY)
		if(!al.contains(cb.baseMaterial)) al.add(cb.baseMaterial);
		
		for(CustomGUI cg : CUSTOM_GUI_REGISTRY)
		if(!al.contains(cg.baseMaterial)) al.add(cg.baseMaterial);
		
		for(CustomItem ci : CUSTOM_ITEM_REGISTRY)
		if(!al.contains(ci.baseMaterial)) al.add(ci.baseMaterial);
		return al;
	}
	
	public static void saveIDNameMap()
	{
		File f = new File(CustomUtils.instance.getDataFolder(), "idnames.map");
		if(f.exists())
		f.delete();
			try{
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(ID_NAME_MAP);
			if(ID_NAME_MAP == null)
			ID_NAME_MAP = new HashMap<String, Integer>();	
			oos.close();
			fos.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		
		
		
	}
	
	public static int getIDOfCustomBlock(CustomBlock cb)
	{
		if(cb.id1 != 0)
		return cb.id1;
		
		if(ID_NAME_MAP.get(cb.getName()) != null)
		return (int) ID_NAME_MAP.get(cb.getName());
		
		Collection<Integer> c = ID_NAME_MAP.values();
		int i = 0;
		while(true)
		{
			i++;
			if(!c.contains(i))
			{
				ID_NAME_MAP.put(cb.getName(), i);
				return i;
			}
		}
		
	}
	
	public static int getIDOfCustomItem(CustomItem ci)
	{
		if(ci.id1 != 0)
		return ci.id1;
		
		if(ID_NAME_MAP.get(ci.getName()) != null)
		return (int) ID_NAME_MAP.get(ci.getName());
		
		Collection<Integer> c = ID_NAME_MAP.values();
		int i = 0;
		while(true)
		{
			i++;
			if(!c.contains(i))
			{
				ID_NAME_MAP.put(ci.getName(), i);
				return i;
			}
		}
		
	}
	
	public static int[] getIDsOfCustomGUI(CustomGUI cg)
	{
		if(cg.id1 != 0 && cg.id2 != 0)
		return new int[] {cg.id1, cg.id2};
		
		
		
		if(ID_NAME_MAP.get(cg.name) != null && ID_NAME_MAP.get(cg.name2) != null)
		return new int[] { ID_NAME_MAP.get(cg.name), ID_NAME_MAP.get(cg.name2) };
		
		Collection<Integer> c = ID_NAME_MAP.values();
		int i = 0;
		int[] intarray = new int[2];
		if(cg.pathToModel1 != null)
		while(true)
		{
			i++;
			if(!c.contains(i))
			{
				ID_NAME_MAP.put(cg.name, i);
				intarray[0] = i;
				break;
			}
		}
		else
		intarray[0] = -1;
		
		if(cg.pathToModel2 != null)
		while(true)
		{
			i++;
			if(!c.contains(i))
			{
				ID_NAME_MAP.put(cg.name2, i);
				intarray[1] = i;
			}
		}
		else
		intarray[1] = -1;
		return intarray;
		
	}
	
	public static void registerItem(CustomItem ci, JavaPlugin pl)
	{
		
		ci.plugin = pl;
		
		
		
		if(!PLUGIN_REGISTRY.contains(pl))
		PLUGIN_REGISTRY.add(pl);
		int i = getIDOfCustomItem(ci);
		ci.id1 = (short) i;
		CUSTOM_ITEM_REGISTRY.add(ci);
		if(ci.getRecipe() != null)
		Bukkit.addRecipe(ci.getRecipe());	
	}
	
	public static void registerBlock(CustomBlock cb, JavaPlugin pl)
	{
		
		cb.plugin = pl;
		
		
		
		if(!PLUGIN_REGISTRY.contains(pl))
		PLUGIN_REGISTRY.add(pl);
		int i = getIDOfCustomBlock(cb);
		cb.id1 = (short) i;
		CUSTOM_BLOCK_REGISTRY.add(cb);
		if(cb.getRecipe() != null)
		Bukkit.addRecipe(cb.getRecipe());
	}
	
	public static CustomItem getCustomItemByName(String name)
	{
		for(CustomItem ci : CUSTOM_ITEM_REGISTRY)
		{
			if(name.equals(ci.getName()))
			return ci;
		}
		return null;
	}
	
	
	public static boolean isCustomBlock(Block b)
	{
		for(CustomBlock cb : CUSTOM_BLOCK_REGISTRY)
		{
			if(Utils.compareBlocks(cb, b))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCustomGUI(Inventory inv)
	{
//		for(CustomGUI cb : CUSTOM_GUI_REGISTRY)
//		{
//			if(inv.getType() == InventoryType.CHEST)
//			if(inv.getItem(0) != null)
//			if(cb.id1 == inv.getItem(0).getDurability())
//			{
//				return  true;
//			}
			Inventory rinv = inv;
			
			
//			try
//			{
//				C
//				return true;
//			} catch (Exception e)
//			{
//				return false;
//			}
			if(JavassistUtil.getCUInventoryCustomClass().isInstance(rinv) || JavassistUtil.getCUInventoryClass().isInstance(rinv))
			{
				return true;
			}
			return false;
	}
	
	public static boolean isCustomItem(ItemStack stack)
	{
		for(CustomItem ci : CUSTOM_ITEM_REGISTRY)
		{
			if(stack.getDurability() == ci.id1)
			if(stack.getItemMeta().isUnbreakable() == true)
			{
				return true;
			}
		}
		return false;
	}
	
	public static CustomItem getCustomItem(ItemStack stack)
	{
		for(CustomItem ci : CUSTOM_ITEM_REGISTRY)
		{
			if(stack.getDurability() == ci.id1)
			{
				return ci;
			}
		}
		return null;
	}
	
	public static CustomGUI getCustomGUIByName(String name)
	{
		for(CustomGUI cg : CUSTOM_GUI_REGISTRY)
		{
			if(cg.getName().equals(name))
			return cg;
		}
		return null;
	}
	
	public static CustomGUI getCustomGUIByInventory(Inventory inv)
	{
//		for(CustomGUI cb : CUSTOM_GUI_REGISTRY)
//		{
//			if(cb.id1 == inv.getItem(0).getDurability())
//			{
//				return cb;
//			}
//		}
		if(isCustomGUI(inv))
		{
			try
			{
				Class<?> specInvClass = JavassistUtil.getCUInventoryCustomClass().isInstance(inv) ? JavassistUtil.getCUInventoryCustomClass() : JavassistUtil.getCUInventoryClass();
				return getCustomGUIByName((String) specInvClass.getField("customGui").get(inv));
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public static CustomBlock getCustomBlockByBlock(Block b)
	{
		for(CustomBlock cb : CUSTOM_BLOCK_REGISTRY)
		{
			if(Utils.compareBlocks(cb, b))
			{
				return cb;
			}
		}
		return null;
	}
	
	public static boolean isItemStackItemStackOfCB(ItemStack is)
	{
		for(CustomBlock cb : CUSTOM_BLOCK_REGISTRY)
		{
			if(cb.getBlockItem().equals(is))
			return true;	
		}
		return false;
	}
	
	public static CustomBlock getCustomBlockByStack(ItemStack is)
	{
		for(CustomBlock cb : CUSTOM_BLOCK_REGISTRY)
		{
			if(cb.getBlockItem().equals(is))
			return cb;	
		}
		return null;
	}
	
	public static CustomBase getCustomBaseById(short id)
	{
		for(CustomBlock cb : CUSTOM_BLOCK_REGISTRY)
		{
			if(cb.getId() == id)
			{
				return cb;
			}
		}
		
		for(CustomGUI gui : CUSTOM_GUI_REGISTRY)
		{
			if(gui.id1 == id || gui.id2 == id)
			{
				return gui;
			}
		}
		
		return null;
	}
	
	
	
	public static boolean registerGUI(CustomGUI cg, JavaPlugin pl)
	{
		cg.setPlugin(pl);
		if(!PLUGIN_REGISTRY.contains(pl))
		PLUGIN_REGISTRY.add(pl);
		
		int[] i = getIDsOfCustomGUI(cg);
		cg.id1 = (short) i[0];
		cg.id2 = (short) i[1];

		return CUSTOM_GUI_REGISTRY.add(cg);
	}
	
	
	
	
	
}
