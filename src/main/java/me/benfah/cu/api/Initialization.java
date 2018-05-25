package me.benfah.cu.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.Request;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import com.google.common.collect.HashBiMap;

import me.benfah.cu.main.CustomUtils;
import me.benfah.cu.util.InitializationState;
import me.benfah.cu.util.ReflectionUtils;
import me.benfah.cu.util.Utils;
import me.benfah.cu.util.ZipCompare;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;


public class Initialization
{
	

	
	
	public static PrintWriter pw;
	public static InitializationState initialize;
	public static void init(boolean forceUpload) throws IOException
	{
		initialize = InitializationState.INITIALIZING;
		try
		{
		System.out.println(CustomRegistry.PLUGIN_REGISTRY.size() + " plugins registered.");
		System.out.println(CustomRegistry.CUSTOM_BLOCK_REGISTRY.size() + " blocks registered.");
		System.out.println(CustomRegistry.CUSTOM_GUI_REGISTRY.size() + " guis registered.");
		System.out.println(CustomRegistry.CUSTOM_ITEM_REGISTRY.size() + " guis registered.");

		System.out.println(CustomRegistry.CUSTOM_GUI_REGISTRY.size() + CustomRegistry.CUSTOM_BLOCK_REGISTRY.size() + CustomRegistry.CUSTOM_ITEM_REGISTRY.size() + " in total.");
		
		setup();
		
		initRP(forceUpload);
		
		
		
		System.out.println("Loading WorldStores...");
		
		for(World w : Bukkit.getWorlds())
		{
			File f = new File(w.getWorldFolder(), "blocks.meta");
			if(f.exists())
			{
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				try {
					Object values = ois.readObject();
					try
					{
						CustomRegistry.worldStoreList.add(new WorldStore(w,  (Map<Map<String, Object>, Map<String, Object>>) values));
					}
					catch (Exception e)
					{
						System.out.println("[CustomUtils] An entry of a world store could not be read!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ois.close();
				fis.close();
			}
			else
			{
				CustomRegistry.worldStoreList.add(new WorldStore(w));
			}
		}
		
		
		
		
		
		
		System.out.println("Done!");
		
		
		initialize = InitializationState.SUCCESS;
		
		}
		catch(Exception e)
		{
			initialize = InitializationState.FAILED;
			e.printStackTrace();
		}
		
		
	}
	
	public static void initRP(boolean forceUpload) throws IOException
	{
		System.out.println("Unzipping the resourcepack out of the plugins...");
		
		if(!CustomUtils.instance.getDataFolder().isDirectory())
		CustomUtils.instance.getDataFolder().mkdirs();
		
		File resourcepacks = new File(CustomUtils.instance.getDataFolder(), "resourcepacks");
		if(!resourcepacks.exists())
		resourcepacks.mkdir();
		
		
		
		File finalrp = new File(resourcepacks, "finalrp");
		if(!finalrp.exists())
			finalrp.mkdir();
		
				
			
		for(JavaPlugin pl : CustomRegistry.PLUGIN_REGISTRY)
		{
			File f = new File(resourcepacks, pl.getName());
			try {
			System.out.println(pl.getClass().getSimpleName());
			Method m = JavaPlugin.class.getDeclaredMethod("getFile");
			m.setAccessible(true);
			File f2 = (File) m.invoke(pl);
	        System.gc();

			File resource = new File(f, "assets");
			if(f.exists())
			FileUtils.forceDelete(f);
			
			File assetsfolder = new File(finalrp, "assets");
			if(!assetsfolder.exists())
			assetsfolder.mkdirs();
			
			
			if(!f.exists())
			f.mkdirs();
			
			ZipFile zf = new ZipFile(f2);
			//zf.extractAll(f.getPath());
//			FileHeader fh = zf.getFileHeader("assets");
			
			
//		    zf.extractFile(fh, f3.toString()); 
			FileHeader fh = zf.getFileHeader("assets.zip");
			zf.extractFile(fh, f.getAbsolutePath());
			new ZipFile(new File(f, "assets.zip")).extractAll(f.getAbsolutePath());
			
			
			
//			zf.extractFile("assets.zip", f.getPath() + "/assets.zip");
			//ZipUtils.unzip(f, f2);
			FileUtils.copyDirectory(resource, assetsfolder);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		System.out.println("Writing blocks from registry into model file...");
		File modelsfolder = new File(finalrp, "assets/minecraft/models/item");
		
		if(!modelsfolder.exists())
		modelsfolder.mkdirs();
		
		List<Material> base = CustomRegistry.getUsedBaseMaterials();
		List<String> baseStr = new ArrayList<>();
		
		HashBiMap<Material, String> map = HashBiMap.create();
		
		
		
		for(Material s : base)
		baseStr.add(Utils.getUnlocalizedName(s));
		
		Iterator<Material> i1 = base.iterator();
		Iterator<String> i2 = baseStr.iterator();
		while (i1.hasNext() && i2.hasNext())
		{
		    map.put(i1.next(), i2.next());
		}
		
		for(String s : baseStr)
		{
			
			File diamondhoe = new File(modelsfolder, s + ".json");
			
			if(diamondhoe.exists())
			diamondhoe.delete();
			
			try {
				FileOutputStream fos = new FileOutputStream(diamondhoe);
				pw = new PrintWriter(fos);
				pw.write("{ \"parent\": \"item/handheld\", \"textures\": { \"layer0\": \"items/" + s + "\" }, \"overrides\": [ { \"predicate\": {\"damaged\": 0, \"damage\": 0}, \"model\": \"item/" + s + "\"}");
				Material mat = map.inverse().get(s);
				System.out.println(mat);
				writeModels(new AbstractMap.SimpleEntry<String, Material>(s, mat), Utils.getMaxDurability(mat));
				pw.println("]}");
				
				pw.close();
				try {
					fos.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		
			
			
		
		
		System.out.println("Copying needed resources out of plugin...");
		CustomUtils.instance.saveResource("pack.mcmeta", true);
		CustomUtils.instance.saveResource("pack.png", true);
		CustomUtils.instance.saveResource("mob_spawner.png", true);
		try {
			new File(finalrp, "pack.mcmeta").delete();
			new File(finalrp, "pack.png").delete();
			File blockstextures = new File(finalrp, "assets/minecraft/textures/blocks");
			if(!blockstextures.exists())
			blockstextures.mkdirs();
			
			File mobspawnerfile = new File(blockstextures, "mob_spawner.png");
			if(mobspawnerfile.exists())
			mobspawnerfile.delete();
			
			FileUtils.moveFile(new File(CustomUtils.instance.getDataFolder(), "pack.mcmeta"), new File(finalrp, "pack.mcmeta"));
			FileUtils.moveFile(new File(CustomUtils.instance.getDataFolder(), "pack.png"), new File(finalrp, "pack.png"));
			FileUtils.moveFile(new File(CustomUtils.instance.getDataFolder(), "mob_spawner.png"), mobspawnerfile);

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Compressing resourcepack...");
		File resourcepack = new File(CustomUtils.instance.getDataFolder(), "c-resourcepack.zip");
		
		if(resourcepack.exists())
		resourcepack.delete();
			
			
		try {
			ZipFile zf = new ZipFile(resourcepack);
			ZipParameters zp = new ZipParameters();
			zp.setCompressionMethod(Zip4jConstants.COMP_STORE);
			
			zf.addFolder(new File(finalrp, "assets"), zp);
			zf.addFile(new File(finalrp, "pack.mcmeta"), zp);
			zf.addFile(new File(finalrp, "pack.png"), zp);
			
			zf.getFile();
		} catch (ZipException e) {
			e.printStackTrace();
		}
		System.out.println("Comparing latest and current resourcepack...");
		File finalrpzip = new File(CustomUtils.instance.getDataFolder(), "final-resourcepack.zip");
		if(!forceUpload)
		{
		
		if(!finalrpzip.exists())
		{
			System.out.println("No current resourcepack. Uploading...");
			FileUtils.copyFile(resourcepack, finalrpzip);
			uploadResourcePack(finalrpzip);

			
			
			
			
		}
		else
		{
			if(!CustomUtils.cfg.contains("resourcepack-link"))
			uploadResourcePack(finalrpzip);
			else
			if(/*resourcepack.length() == finalrpzip.length()*/ZipCompare.compare(resourcepack, finalrpzip))
			{
				
				System.out.println("resourcepack content is equal. Skipping upload!");
			}
			else
			{
				finalrpzip.delete();
				FileUtils.copyFile(resourcepack, finalrpzip);

				System.out.println("resourcepack content is not equal. Uploading...");
				uploadResourcePack(finalrpzip);
			}
		}
		}
		else
		{
			finalrpzip.delete();
			FileUtils.copyFile(resourcepack, finalrpzip);
			PrintWriter pw = new PrintWriter(new FileWriter(finalrpzip, true));
			pw.print(new Random().nextInt());
			pw.close();
			uploadResourcePack(finalrpzip);
			for(Player p : Bukkit.getOnlinePlayers())
			p.setResourcePack(CustomUtils.cfg.getString("resourcepack-link"));
		}
	}
	
	private static void writeModels(Entry<String, Material> mat, int size)
	{
//		int i = 1;
		
		CustomBase[] cbarray = new CustomBase[size + 2];
		
		for(CustomBlock cb : CustomRegistry.CUSTOM_BLOCK_REGISTRY)
		{
			cbarray[cb.id1] = cb;
		}
		for(CustomItem ci : CustomRegistry.CUSTOM_ITEM_REGISTRY)
		{
			cbarray[ci.id1] = ci;
		}
		for(CustomGUI cg : CustomRegistry.CUSTOM_GUI_REGISTRY)
		{
			if(cg.id1 != -1)
			cbarray[cg.id1] = cg;
			if(cg.id2 != -1)
			cbarray[cg.id2] = cg;
		}
		
		for(CustomBase cb : cbarray)
		{
			if(cb != null)
			if(cb instanceof CustomGUI)
			{
				CustomGUI cg = (CustomGUI) cb;
				if(cg.pathToModel1 != null)
				{	
				pw.println(",");
				pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(cg.id1, size + 1) + "}, \"model\": \"" + cg.pathToModel1 + "\"}");
				}
				
				
				if(cg.pathToModel2 != null)
				{	
				pw.println(",");
				pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(cg.id2, size + 1) + "}, \"model\": \"" + cg.pathToModel2 + "\"}");
				}
			}
			else
			{
				pw.println(",");
				pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(cb.id1, size + 1) + "}, \"model\": \"" + cb.pathToModel1 + "\"}");
			}
		}
		pw.println(",");
		pw.println("{ \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + mat.getKey() + "\"}");
		
//		for(CustomBlock cb : CustomRegistry.CUSTOM_BLOCK_REGISTRY)
//		{
//			pw.println(",");
//			pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(cb.id1) + "}, \"model\": \"" + cb.getModelPath() + "\"}");
////			cb.id1 = (short) i;
////			i++;
//		}
//		
//		for(CustomGUI cg : CustomRegistry.CUSTOM_GUI_REGISTRY)
//		{
//			if(cg.pathToModel1 != null)
//			{	
//			pw.println(",");
//			pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(cg.id1) + "}, \"model\": \"" + cg.pathToModel1 + "\"}");
//			}
//			
//			
//			if(cg.pathToModel2 != null)
//			{	
//			pw.println(",");
//			pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(cg.id2) + "}, \"model\": \"" + cg.pathToModel2 + "\"}");
//			}
//			
//		}
//		
//		for(CustomItem ci : CustomRegistry.CUSTOM_ITEM_REGISTRY)
//		{
//			pw.println(",");
//			pw.println("{ \"predicate\": {\"damaged\": 0, \"damage\": " + getDamageForTool(ci.id1) + "}, \"model\": \"" + ci.pathToModel1 + "\"}");
//		}
//		pw.println(",");
//		pw.println("{ \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/diamond_hoe\"}");
	}
	
	public static String getDamageForTool(double dmg, double maxSize)
	{
		return new BigDecimal(1D/1562D*dmg).toPlainString();
	}
	
	public static void setPropertyInPropertyManager(String k, Object v)
	{
		Class<?> mserver = ReflectionUtils.getRefClass("{nms}.MinecraftServer");
		try
		{
		Object obj = mserver.getMethod("getServer").invoke(null);
		Object propertyManager = obj.getClass().getMethod("getPropertyManager").invoke(obj);
		propertyManager.getClass().getMethod("setProperty", String.class, Object.class).invoke(propertyManager, k, v);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveProperties()
	{
		Class<?> mserver = ReflectionUtils.getRefClass("{nms}.MinecraftServer");
		try
		{
		Object obj = mserver.getMethod("getServer").invoke(null);
		Object propertyManager = obj.getClass().getMethod("getPropertyManager").invoke(obj);
		propertyManager.getClass().getMethod("savePropertiesFile").invoke(propertyManager);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setup()
	{
		if(!CustomUtils.cfg.contains("dropbox.key") || CustomUtils.cfg.getString("dropbox.key").equals("null"))
		{	
			
			System.out.println("DROPBOX UPLOAD SETUP:");	
			System.out.println("1. Go to https://www.dropbox.com/developers/apps/create (create a dropbox account if necessary).");		
			System.out.println("2. Choose \"Dropbox API\", then choose \"App folder\", then name it somehow.");
			System.out.println("3. Look for \"Development users\" and hit \"Enable additional users\".");
			System.out.println("4. Enter the App key and the App secret into the cfg.yml of CustomUtils"); //TODO: <-
			CustomUtils.cfg.set("dropbox.key", "null");
			CustomUtils.cfg.set("dropbox.secret", "null");
			try {
				CustomUtils.cfg.save(CustomUtils.cfgFile);
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			while(CustomUtils.cfg.getString("dropbox.key").equals("null") && CustomUtils.cfg.getString("dropbox.secret").equals("null"))
			{
				try {
					System.out.println("Hit enter when you're done.");
					System.in.read();
					CustomUtils.cfg.load(CustomUtils.cfgFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
			System.out.println("Now authorize your account and paste the code to dropbox.code in the cfg.yml.");
			
			CustomUtils.cfg.set("dropbox.code", "null");
			try {
				CustomUtils.cfg.save(CustomUtils.cfgFile);
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			DbxAppInfo appInfo = new DbxAppInfo(CustomUtils.cfg.getString("dropbox.key"), CustomUtils.cfg.getString("dropbox.secret"));
	        
	        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0");
	        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
	        String url = webAuth.authorize(Request.newBuilder().withNoRedirect().build());
	        System.out.println("Authorize: " + url);
	        while(CustomUtils.cfg.getString("dropbox.code").equals("null"))
	        {
				System.out.println("Hit enter when you're done.");
				try {
					System.in.read();
					CustomUtils.cfg.load(CustomUtils.cfgFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
			try {
				DbxAuthFinish daf = webAuth.finishFromCode(CustomUtils.cfg.getString("dropbox.code"));
		        CustomUtils.cfg.set("dropbox.token", daf.getAccessToken());
			} catch (DbxException e) {
				e.printStackTrace();
			}
			
	       
		}
	}
	
	
	public static void uploadResourcePack(File f)
	{
//		WebDriver driver = new HtmlUnitDriver();
//		
//		driver.get("https://minepack.net");
//		
//        WebElement element = driver.findElement(By.name("resourcepack"));
//		element.sendKeys(f.getAbsolutePath());
//
//		
//		
//		
//		
//		WebElement element2 = driver.findElement(By.name("submit"));
//		element2.submit();
//		
//		
//		
//		
//		
//		List<WebElement> wel = driver.findElements(By.className("select"));
//		setPropertyInPropertyManager("resource-pack", wel.get(0).getAttribute("value"));
//		setPropertyInPropertyManager("resource-pack-hash", wel.get(1).getAttribute("value"));
//		CustomUtils.cfg.set("resourcepack-link", wel.get(0).getAttribute("value").replace("https", "http"));
//		CustomUtils.cfg.set("resourcepack-hash", wel.get(1).getAttribute("value"));
//		try {
//			CustomUtils.cfg.save(CustomUtils.cfgFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		saveProperties();
		
		try {
			
		
		DbxAppInfo appInfo = new DbxAppInfo(CustomUtils.cfg.getString("dropbox.key"), CustomUtils.cfg.getString("dropbox.secret"));
        
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0");
        DbxClientV2 c2 = new DbxClientV2(config, CustomUtils.cfg.getString("dropbox.token"));
        c2.files().listFolder("").getEntries().forEach((x) -> {
        	try {
				c2.files().deleteV2(x.getPathLower());
			} catch (Exception e)
        	{
				e.printStackTrace();
			}
        });
        String strippedUid = UUID.randomUUID().toString().substring(0, 7);
        FileMetadata fm = c2.files().uploadBuilder("/resourcepack-" + strippedUid + ".zip").withMode(WriteMode.OVERWRITE).uploadAndFinish(new FileInputStream(f));
        String link = null;
//        for(SharedLinkMetadata slm : c2.sharing().listSharedLinks().getLinks())
//        {
//        	if(slm.getId().equals(fm.getId()))
//        	link = slm.getUrl();
//        }
        
        if(link == null)
        link = c2.sharing().createSharedLinkWithSettings("/resourcepack-" + strippedUid + ".zip", SharedLinkSettings.newBuilder().withRequestedVisibility(RequestedVisibility.PUBLIC).build()).getUrl();
        link = link.replace("www", "dl");
        
        CustomUtils.cfg.set("resourcepack-link", link);
		CustomUtils.cfg.save(CustomUtils.cfgFile);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void mergeTwoDirectories(File dir1, File dir2){
		String targetDirPath = dir1.getAbsolutePath();
		File[] files = dir2.listFiles();
		for (File file : files) {
			file.renameTo(new File(targetDirPath+File.separator+file.getName()));
			System.out.println(file.getName() + " is moved!");
		}
	}
}
