package net.malisis.advert;

import net.malisis.core.IMalisisMod;
import net.malisis.core.MalisisCore;
import net.malisis.core.configuration.Settings;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MalisisAdvert.modid, name = MalisisAdvert.modname, version = MalisisAdvert.version)
public class MalisisAdvert implements IMalisisMod
{
	public static final String modid = "malisisadvert";
	public static final String modname = "Malisis Advert";
	public static final String version = "${version}";

	public static MalisisAdvert instance;

	public static CreativeTabs tab = new MalisisAdvertTab();

	public MalisisAdvert()
	{
		instance = this;
		MalisisCore.registerMod(this);
	}

	@Override
	public String getModId()
	{
		return modid;
	}

	@Override
	public String getName()
	{
		return modname;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public Settings getSettings()
	{
		return null;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		//		settings = new MalisisAdvertSettings(event.getSuggestedConfigurationFile());
		//
		//		Registers.init();
		//
		//		proxy.initRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		//NetworkHandler.init(modid);
	}

	public static class Blocks
	{

	}

	public static class Items
	{

	}

}
