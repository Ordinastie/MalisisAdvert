package net.malisis.advert;

import net.malisis.advert.block.WallPanel;
import net.malisis.core.IMalisisMod;
import net.malisis.core.MalisisCore;
import net.malisis.core.configuration.Settings;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

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

	//#region IMalisisMod
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

	//#end IMalisisMod

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Registers.init();
		if (event.getSide() == Side.CLIENT)
		{
			Registers.initRenderers();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{

	}

	public static class Blocks
	{
		public static WallPanel wallPanel;
	}

	public static class Items
	{

	}

}
