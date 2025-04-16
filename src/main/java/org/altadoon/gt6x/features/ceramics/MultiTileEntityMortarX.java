package org.altadoon.gt6x.features.ceramics;

import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregtech.tileentity.tools.MultiTileEntityMortar;
import net.minecraft.block.Block;

import static gregapi.data.CS.*;
import static gregapi.data.CS.SIDE_TOP;

public class MultiTileEntityMortarX extends MultiTileEntityMortar {
	@Override public String getTileEntityName() {return "gt6x.multitileentity.mortar";}

	public static final OreDictMaterial[] MORTAR_MATERIALS_X = {MT.Bronze, MT.BismuthBronze, MT.BlackBronze, MT.ArsenicBronze, MT.ArsenicCopper};

	@Override
	public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		if (renderPass == 5) {
			return switch (side) {
				case SIDE_TOP -> BlockTextureMulti.get(
						BlockTextureDefault.get(sTextureMiddleTop, MORTAR_MATERIALS[mStyle % MORTAR_MATERIALS_X.length].fRGBaSolid),
						BlockTextureDefault.get(sOverlayMiddleTop)
				);
				case SIDE_BOTTOM -> null;
				default -> BlockTextureMulti.get(
						BlockTextureDefault.get(sTextureMiddleSide, MORTAR_MATERIALS[mStyle % MORTAR_MATERIALS_X.length].fRGBaSolid),
						BlockTextureDefault.get(sOverlayMiddleSide)
				);
			};
		} else return super.getTexture2(block, renderPass, side, shouldSideBeRendered);
	}
}
