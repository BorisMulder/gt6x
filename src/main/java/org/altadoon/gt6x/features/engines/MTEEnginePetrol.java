package org.altadoon.gt6x.features.engines;

import net.minecraft.block.Block;
import org.altadoon.gt6x.common.rendering.Geometry;

import static gregapi.data.CS.PX_N;
import static gregapi.data.CS.PX_P;

public class MTEEnginePetrol extends MultiTileEntityEngineBase {

	@Override
	public String getTileEntityName() {return "gt6x.multitileentity.generator.engine_petrol";}

	@Override
	public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
		return super.getRenderPasses2(block, shouldSideBeRendered) + 1;
	}

	@Override
	public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
		return switch (renderPass - super.getRenderPasses2(block, shouldSideBeRendered)) {
			case 0 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[6], PX_N[4], PX_P[1], PX_P[6], PX_N[2], PX_N[1]));
			default -> super.setBlockBounds2(block, renderPass, shouldSideBeRendered);
		};
	}
}
