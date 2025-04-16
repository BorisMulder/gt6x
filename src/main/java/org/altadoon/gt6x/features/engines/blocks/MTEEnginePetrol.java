package org.altadoon.gt6x.features.engines.blocks;

import gregapi.old.Textures;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import net.minecraft.block.Block;
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.common.rendering.Geometry;
import org.altadoon.gt6x.common.utils.RepeatedSound;

import static gregapi.data.CS.*;

public class MTEEnginePetrol extends MultiTileEntityEngineBase {
	@Override
	public String getTileEntityName() {return "gt6x.multitileentity.generator.engine_petrol";}

	@Override
	protected RepeatedSound getSound() {
		return new RepeatedSound(Gt6xMod.MOD_ID.toLowerCase() + ":" + "machines.petrol_engine", 38);
	}

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

	@Override
	public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		if (renderPass - super.getRenderPasses2(block, shouldSideBeRendered) == 0 && side == OPOS[mSecondFacing]) {
			return getRotatedFlippedTexture(sparkPlugsIconBase, sparkPlugsIconOverlay, side);
		}
		return super.getTexture2(block, renderPass, side, shouldSideBeRendered);
	}

	public static IIconContainer sparkPlugsIconBase = new Textures.BlockIcons.CustomIcon("machines/generators/engine_petrol/colored/sparkplugs");
	public static IIconContainer sparkPlugsIconOverlay = new Textures.BlockIcons.CustomIcon("machines/generators/engine_petrol/overlay/sparkplugs");
}
