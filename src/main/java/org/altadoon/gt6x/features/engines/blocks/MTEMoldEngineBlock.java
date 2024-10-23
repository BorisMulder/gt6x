package org.altadoon.gt6x.features.engines.blocks;

import gregapi.oredict.OreDictPrefix;
import gregtech.tileentity.tools.MultiTileEntityBasin;
import org.altadoon.gt6x.features.engines.OreDictPrefixes;

public class MTEMoldEngineBlock extends MultiTileEntityBasin {
	@Override public OreDictPrefix getMoldRecipe(int aShape) {
		return OreDictPrefixes.engineBlock;
	}
	@Override public String getTileEntityName() {return "gt6x.multitileentity.moldEngineBlock";}
}
