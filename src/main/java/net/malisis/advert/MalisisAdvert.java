package net.malisis.advert;

import static net.malisis.advert.MalisisAdvert.Blocks.*;
import net.malisis.advert.block.AdvertBlock;
import net.malisis.advert.model.AdvertModel;
import net.malisis.advert.model.BillboardModel;
import net.malisis.advert.model.PanelModel;
import net.malisis.advert.model.TriangularColumn;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.IMalisisMod;
import net.malisis.core.MalisisCore;
import net.malisis.core.configuration.Settings;
import net.malisis.core.network.MalisisNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MalisisAdvert.modid, name = MalisisAdvert.modname, version = MalisisAdvert.version, dependencies = "required-after:malisiscore")
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

		registerModels();
	}

	private void registerModels()
	{
		AdvertModel.register(new PanelModel());
		AdvertModel.register(new TriangularColumn());
		AdvertModel.register(new BillboardModel());
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new MalisisAdvertCommand());
	}

	public static class Blocks
	{
		public static AdvertBlock advertBlock;
	}

	public static class Items
	{

	}

}
