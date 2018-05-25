package me.benfah.cu.util;
 
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView;
import org.bukkit.inventory.Inventory;

import com.google.common.reflect.Reflection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
 
public class JavassistUtil
{
    public static Class<?> injInventory;
    public static Class<?> injInventory1;
    public static Class<?> injInventory2;

    private static Class<?> getCUICClass()
    {
         
        try {
            try {
            return Class.forName("me.benfah.gen.CUInventoryCustom");
            } catch (ClassNotFoundException e) {
                CtClass ctInjected = ClassPool.getDefault().makeClass("me.benfah.gen.CUInventoryCustom");
                 
                ctInjected.setSuperclass(ClassPool.getDefault().get(ReflectionUtils.getRefClass("{cb}.inventory.CraftInventoryCustom").getName()));
                ctInjected.addField(CtField.make("public String customGui;", ctInjected));
                return ctInjected.toClass();
            }
             
        } catch (CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    
    private static Class<?> getInjectedClass()
    {
         
        try {
            try {
            return Class.forName("me.benfah.gen.CUInventory");
            } catch (ClassNotFoundException e) {
                CtClass ctInjected = ClassPool.getDefault().makeClass("me.benfah.gen.CUInventory");
                 
                ctInjected.setSuperclass(ClassPool.getDefault().get(ReflectionUtils.getRefClass("{cb}.inventory.CraftInventory").getName()));
                ctInjected.addField(CtField.make("public String customGui;", ctInjected));
                return ctInjected.toClass();
            }
             
        } catch (CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Class<?> getGUIConClass()
    {
    	try {
            try {
            return Class.forName("me.benfah.gen.GUIContainer");
            } catch (ClassNotFoundException e) {
                CtClass ctInjected = ClassPool.getDefault().makeClass("me.benfah.gen.GUIContainer");
                 
                ctInjected.setSuperclass(ClassPool.getDefault().get(ReflectionUtils.getRefClass("{nms}.ContainerChest").getName()));
                
                Class<?> craftInventoryViewClass = ReflectionUtils.getRefClass("{cb}.inventory.CraftInventoryView");
                Class<?> IInventoryClass = ReflectionUtils.getRefClass("{nms}.IInventory");
                Class<?> entityHumanClass = ReflectionUtils.getRefClass("{nms}.EntityHuman");
                
                
                
                ctInjected.addField(CtField.make("public Object customGui;", ctInjected));

                ctInjected.addField(CtField.make("public " + IInventoryClass.getName() + " iinventory;", ctInjected));
                ctInjected.addField(CtField.make("public " + IInventoryClass.getName() + " iinventory1;", ctInjected));
                
                ctInjected.addField(CtField.make("public " + entityHumanClass.getName() + " player;", ctInjected));

                
                
                ctInjected.addField(CtField.make("public " + craftInventoryViewClass.getName() + " view;", ctInjected));
                
                
                ctInjected.addConstructor(CtNewConstructor.make(""
                		+ "public GUIContainer(" + IInventoryClass.getName() + " iinventory, " + IInventoryClass.getName() + " iinventory1, " + entityHumanClass.getName() + " entityhuman, String guiInstance)"
                		+ "{"
                		+ "super(iinventory, iinventory1, entityhuman);"
                		+ "this.iinventory = iinventory;"
                		+ "this.iinventory1 = iinventory1;"
                		+ "this.player = entityhuman;"
                		+ "this.customGui = guiInstance;"
                		+ "}", ctInjected));
                
                
                ctInjected.addMethod(CtMethod.make("public " + craftInventoryViewClass.getName() + " getBukkitView()" + 
                		"	{" + 
                		"		if(view != null)" + 
                		"		return view;" + 
                		"		" + 
                		"		try {" + 
                		"			Object ci = new me.benfah.gen.CUInventory(iinventory1);" + 
                		"			view = new " + craftInventoryViewClass.getName() + "(this.player.getBukkitEntity(), (org.bukkit.inventory.Inventory) ci, this);" + 
                		"			ci.getClass().getField(\"customGui\").set(ci, customGui);" + 
                		"			return view;" + 
                		"		}" + 
                		"		catch (java.lang.ReflectiveOperationException e)" + 
                		"		{" + 
                		"			e.printStackTrace();" + 
                		"		}" + 
                		"		return null;" + 
                		"		" + 
                		"	}", ctInjected));
                
                return ctInjected.toClass();
            }
             
        } catch (CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    
    
    public static Class<?> getContainerClass()
    {
    	ClassPool cp = ClassPool.getDefault();
        cp.importPackage("me.benfah.cu.api");
         
         
         
        if(injInventory2 == null)
        injInventory2 = JavassistUtil.getGUIConClass();
        return injInventory2;
    }
    
    
    
    public static Class<?> getCUInventoryCustomClass()
    {
        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("me.benfah.cu.api");
         
         
         
        if(injInventory == null)
        injInventory = JavassistUtil.getCUICClass();
        return injInventory;
    }
    
    public static Class<?> getCUInventoryClass()
    {
        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("me.benfah.cu.api");
         
         
         
        if(injInventory1 == null)
        injInventory1 = JavassistUtil.getInjectedClass();
        return injInventory1;
    }
}