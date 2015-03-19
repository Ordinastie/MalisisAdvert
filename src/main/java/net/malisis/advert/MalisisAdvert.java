package net.malisis.advert;

import static net.malisis.advert.MalisisAdvert.Blocks.*;
import net.malisis.advert.block.AdvertBlock;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.IMalisisMod;
import net.malisis.core.MalisisCore;
import net.malisis.core.configuration.Settings;
import net.malisis.core.network.MalisisNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.ClientCommandHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = MalisisAdvert.modid, name = MalisisAdvert.modname, version = MalisisAdvert.version)
public class MalisisAdvert implements IMalisisMod
{
	public static final String modid = "malisisadvert";
	public static final String modname = "Malisis Advert";
	public static final String version = "${version}";

	public static MalisisAdvert instance;
	public static Logger log = LogManager.getLogger(modid);
	public static MalisisNetwork network;

	public static CreativeTabs tab = new MalisisAdvertTab();

	public MalisisAdvert()
	{
		instance = this;
		network = new MalisisNetwork(this);
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
		advertBlock = new AdvertBlock();
		advertBlock.register();
		GameRegistry.registerTileEntity(AdvertTileEntity.class, "advertTileEntity");

		if (event.getSide() == Side.CLIENT)
		{
			AdvertRenderer wpr = new AdvertRenderer();
			wpr.registerFor(AdvertBlock.class, AdvertTileEntity.class);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ClientCommandHandler.instance.registerCommand(new MalisisAdvertCommand());
	}

	public static class Blocks
	{
		public static AdvertBlock advertBlock;
	}

	public static class Items
	{

	}

}
