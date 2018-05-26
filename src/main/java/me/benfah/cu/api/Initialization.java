package me.benfah.cu.api;

import me.benfah.cu.init.IInitializationMethod;


public class Initialization
{
	private static IInitializationMethod currentMethod;

	public static IInitializationMethod getCurrentMethod()
	{
		return currentMethod;
	}

	public static void setCurrentMethod(IInitializationMethod currentMethod)
	{
		Initialization.currentMethod = currentMethod;
	}
	
	public static void init(boolean forceUpload)
	{
		currentMethod.init(forceUpload);
	}
	
	public static void initRP(boolean forceUpload)
	{
		currentMethod.initResourcePack(forceUpload);
	}
	
	
}
