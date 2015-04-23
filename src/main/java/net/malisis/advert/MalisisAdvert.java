package net.malisis.advert;

import static net.malisis.advert.MalisisAdvert.Blocks.*;

import java.util.Collection;
import java.util.HashMap;

import net.malisis.advert.block.AdvertBlock;
import net.malisis.advert.model.AdvertModel;
import net.malisis.advert.model.PanelModel;
import net.malisis.advert.model.PanelModel.FootType;
import net.malisis.advert.model.TriangularColumn;
import net.malisis.advert.renderer.AdvertRenderer;
import net.malisis.advert.tileentity.AdvertTileEntity;
import net.malisis.core.IMalisisMod;
import net.malisis.core.MalisisCore;
import net.malisis.core.configuration.Settings;
import net.malisis.core.network.MalisisNetwork;
import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

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

	private static HashMap<String, AdvertModel> modelRegistry = new HashMap<>();
	public static AdvertModel defaultWallModel;
	public static AdvertModel defaultModel;

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

		if (event.getSide() == Side.CLIENT)
		{
			AdvertRenderer wpr = new AdvertRenderer();
			wpr.registerFor(AdvertBlock.class, AdvertTileEntity.class);
		}
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new MalisisAdvertCommand());
	}

	//#region Model registry
	public static void registerModels()
	{
		defaultWallModel = new PanelModel(FootType.WALL, true);
		defaultModel = new PanelModel(FootType.SMALL, false);

		registerModel(defaultWallModel);
		registerModel(defaultModel);
		registerModel(new PanelModel(FootType.FULL, false));
		registerModel(new TriangularColumn());
	}

	public static void registerModel(AdvertModel model)
	{
		modelRegistry.put(model.getId(), model);
	}

	public static Collection<AdvertModel> listModels()
	{
		return modelRegistry.values();
	}

	public static AdvertModel getModel(String id)
	{
		return modelRegistry.get(id);
	}

	//#end Model registry

	public static class Blocks
	{
		public static AdvertBlock advertBlock;
	}

	public static class Items
	{

	}

}
