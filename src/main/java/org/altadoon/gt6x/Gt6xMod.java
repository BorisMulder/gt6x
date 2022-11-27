package org.altadoon.gt6x;

import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.common.items.ItemMaterialDisplay;
import org.altadoon.gt6x.common.items.MultiItemsX;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.basicchem.BasicChemistry;
import org.altadoon.gt6x.features.oil.OilProcessing;
import org.altadoon.gt6x.features.pgm.PgmProcessing;
import org.altadoon.gt6x.features.metallurgy.Metallurgy;
import org.altadoon.gt6x.features.refractorymetals.RefractoryMetals;

import java.util.ArrayList;

import static gregapi.data.CS.GT;

/**
 * @author Boris Mulder (Altadoon)
 *
 * The main mod class for GT6X
 */
@cpw.mods.fml.common.Mod(modid= Gt6xMod.MOD_ID, name= Gt6xMod.MOD_NAME, version= Gt6xMod.VERSION, dependencies="required-after:gregapi_post")
public final class Gt6xMod extends gregapi.api.Abstract_Mod {
	/** Your Mod-ID has to be LOWERCASE and without Spaces. Uppercase Chars and Spaces can create problems with Resource Packs. This is a vanilla forge "Issue". */
	public static final String MOD_ID = Log.MOD_ID;
	/** This is your Mods Name */
	public static final String MOD_NAME = "GregTech 6 eXtended";
	/** This is your Mods Version */
	public static final String VERSION = "0.0-gt6.15.05-MC1710";
	/** Contains a ModData Object for ID and Name. Doesn't have to be changed. */
	public static gregapi.code.ModData MOD_DATA = new gregapi.code.ModData(MOD_ID, MOD_NAME);

	@cpw.mods.fml.common.SidedProxy(modId = MOD_ID, clientSide = "gregapi.api.example.Example_Proxy_Client", serverSide = "gregapi.api.example.Example_Proxy_Server")
	public static gregapi.api.Abstract_Proxy PROXY;

	@Override public String getModID() {return MOD_ID;}
	@Override public String getModName() {return MOD_NAME;}
	@Override public String getModNameForLog() {return "GT6X";}
	@Override public gregapi.api.Abstract_Proxy getProxy() {return PROXY;}

	// Do not change these 7 Functions. Just keep them this way.
	@cpw.mods.fml.common.Mod.EventHandler public final void onPreLoad           (cpw.mods.fml.common.event.FMLPreInitializationEvent    aEvent) {onModPreInit(aEvent);}
	@cpw.mods.fml.common.Mod.EventHandler public final void onLoad              (cpw.mods.fml.common.event.FMLInitializationEvent       aEvent) {onModInit(aEvent);}
	@cpw.mods.fml.common.Mod.EventHandler public final void onPostLoad          (cpw.mods.fml.common.event.FMLPostInitializationEvent   aEvent) {onModPostInit(aEvent);}
	@cpw.mods.fml.common.Mod.EventHandler public final void onServerStarting    (cpw.mods.fml.common.event.FMLServerStartingEvent       aEvent) {onModServerStarting(aEvent);}
	@cpw.mods.fml.common.Mod.EventHandler public final void onServerStarted     (cpw.mods.fml.common.event.FMLServerStartedEvent        aEvent) {onModServerStarted(aEvent);}
	@cpw.mods.fml.common.Mod.EventHandler public final void onServerStopping    (cpw.mods.fml.common.event.FMLServerStoppingEvent       aEvent) {onModServerStopping(aEvent);}
	@cpw.mods.fml.common.Mod.EventHandler public final void onServerStopped     (cpw.mods.fml.common.event.FMLServerStoppedEvent        aEvent) {onModServerStopped(aEvent);}

	private Config modConfig = null;

	@SuppressWarnings("unchecked")
	private static final Class<? extends GT6XFeature>[] allFeatures = new Class[]{
		BasicChemistry.class,
		PgmProcessing.class,
		OilProcessing.class,
		Metallurgy.class,
		RefractoryMetals.class,
	};
	private final ArrayList<GT6XFeature> enabledFeatures;

	public Gt6xMod() {
		MTx.touch();

		this.modConfig = new Config(allFeatures);
		this.enabledFeatures = modConfig.getEnabledFeatures();

		final Gt6xMod copy = this;
		GT.mBeforePreInit.add(copy::prePreInit);
		GT.mAfterPreInit.add(copy::postPreInit);
		GT.mBeforePostInit.add(copy::prePostInit);
		GT.mAfterPostInit.add(copy::postPostInit);
	}

	@Override
	public void onModPreInit2(cpw.mods.fml.common.event.FMLPreInitializationEvent aEvent) {
		MTEx.touch();
		MTx.touch();
		MultiItemsX.instance = new MultiItemsX(MOD_ID, "gt6x.multiitems");

		for (GT6XFeature feature : enabledFeatures) {
			feature.preInit();
		}
	}

	@Override
	public void onModInit2(cpw.mods.fml.common.event.FMLInitializationEvent aEvent) {
		for (GT6XFeature feature : enabledFeatures) {
			feature.init();
		}
	}

	@Override
	public void onModPostInit2(cpw.mods.fml.common.event.FMLPostInitializationEvent aEvent) {
		for (GT6XFeature feature : enabledFeatures) {
			feature.postInit();
		}
	}

	private void prePreInit() {
		for (GT6XFeature feature : enabledFeatures) {
			feature.beforePreInit();
		}
	}

	private void postPreInit() {
		for (GT6XFeature feature : enabledFeatures) {
			feature.afterPreInit();
		}
	}

	private void prePostInit() {
		for (GT6XFeature feature : enabledFeatures) {
			feature.beforePostInit();
		}
	}

	private void postPostInit() {
		for (GT6XFeature feature : enabledFeatures) {
			feature.afterPostInit();
		}
	}

	@Override
	public void onModServerStarting2(cpw.mods.fml.common.event.FMLServerStartingEvent aEvent) {
		// Insert your ServerStarting Code here and not above
	}

	@Override
	public void onModServerStarted2(cpw.mods.fml.common.event.FMLServerStartedEvent aEvent) {
		// Insert your ServerStarted Code here and not above
	}

	@Override
	public void onModServerStopping2(cpw.mods.fml.common.event.FMLServerStoppingEvent aEvent) {
		// Insert your ServerStopping Code here and not above
	}

	@Override
	public void onModServerStopped2(cpw.mods.fml.common.event.FMLServerStoppedEvent aEvent) {
		// Insert your ServerStopped Code here and not above
	}
}
