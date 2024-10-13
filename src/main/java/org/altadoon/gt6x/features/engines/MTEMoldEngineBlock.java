package org.altadoon.gt6x.features.engines;

import gregapi.data.OP;
import gregapi.oredict.OreDictPrefix;
import gregtech.tileentity.tools.MultiTileEntityBasin;

public class MTEMoldEngineBlock extends MultiTileEntityBasin {
	@Override public OreDictPrefix getMoldRecipe(int aShape) {
		return OP.blockSolid;
	}
	@Override public String getTileEntityName() {return "gt6x.multitileentity.moldEngineBlock";}
}
