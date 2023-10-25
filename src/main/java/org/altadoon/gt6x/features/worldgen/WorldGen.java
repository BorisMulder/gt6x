package org.altadoon.gt6x.features.worldgen;

import gregapi.data.MD;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.init.Blocks;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;

public class WorldGen extends GT6XFeature {
	public static boolean enableMeteorite = true;

	@Override
	public void configure(Config config) {
		enableMeteorite = config.cfg.get("meotorite","enable", enableMeteorite).getBoolean();
	}

	@Override
	public void preInit() {

	}

	@Override
	public void init() {
		if(enableMeteorite){
			new WorldgenMeteor("meotorite.1"      , T, GEN_OVERWORLD, GEN_GT, GEN_PFAA, GEN_TFC,GEN_PLANETS);
			if(MD.GC.mLoaded){
				new WorldgenMeteor("meotorite.2", BlocksGT.GraniteBlack,  GCBlocks.blockMoon, T, GEN_MOON);
				new WorldgenMeteor("meotorite.3", BlocksGT.GraniteBlack, T, GEN_MARS);
				new WorldgenMeteor("meotorite.4", BlocksGT.GraniteBlack,  GCBlocks.blockMoon, T, GEN_ASTEROIDS);
			}

			if(MD.TF.mLoaded){
				new WorldgenMeteor("meotorite.5", BlocksGT.GraniteBlack,  BlocksGT.Granite, T, GEN_TWILIGHT);
			}

			if(MD.ATUM.mLoaded){
				new WorldgenMeteor("meotorite.6", BlocksGT.GraniteBlack, Blocks.sandstone, T, GEN_ATUM);
			}
		}
	}

	@Override
	public void postInit() {

	}
}
