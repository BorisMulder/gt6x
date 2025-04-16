package org.altadoon.gt6x.features.engines.blocks;

import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import net.minecraft.block.Block;
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.common.rendering.Geometry;
import org.altadoon.gt6x.common.utils.RepeatedSound;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.rendering.Geometry.NOT_ALONG_AXIS;

public class MTEEngineNitro extends MultiTileEntityEngineBase {
	@Override
	public String getTileEntityName() {
		return "gt6x.multitileentity.generator.engine_nitro";
	}

	@Override
	protected RepeatedSound getSound() {
		return new RepeatedSound(Gt6xMod.MOD_ID.toLowerCase() + ":" + "machines.nitro_engine", 36);
	}

	@Override
	public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
		return super.getRenderPasses2(block, shouldSideBeRendered) + 3;
	}

	@Override
	public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
		return switch (renderPass - super.getRenderPasses2(block, shouldSideBeRendered)) {
			case 0 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[6], PX_N[4], PX_P[3], PX_N[6], PX_N[2], PX_N[2]));
			case 1 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[6], PX_N[2], PX_N[7], PX_N[6], PX_N[0], PX_N[3]));
			case 2 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[5], PX_N[2], PX_P[4], PX_N[5], PX_N[0], PX_N[7]));
			default -> super.setBlockBounds2(block, renderPass, shouldSideBeRendered);
		};
	}

	@Override
	public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		int nitroRenderPass = renderPass - super.getRenderPasses2(block, shouldSideBeRendered);
		if (side == mFacing && (nitroRenderPass == 0 || nitroRenderPass == 2)) {
			return BlockTextureMulti.get(BlockTextureDefault.get(baseIcons[nitroRenderPass], mRGBa), BlockTextureDefault.get(overlayIcons[nitroRenderPass]));
		} else if ((renderPass == 6 || renderPass == 7) && NOT_ALONG_AXIS[side][mSecondFacing])  {
			return getRotatedFlippedTexture(baseIcons[1], overlayIcons[1], side);
		} else {
			return super.getTexture2(block, renderPass, side, shouldSideBeRendered);
		}
	}

	// Icons
	public static IIconContainer[] baseIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_nitro/colored/supercharger_axle"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_nitro/colored/flames"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_nitro/colored/air_intake"),
	}, overlayIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_nitro/overlay/supercharger_axle"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_nitro/overlay/flames"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_nitro/overlay/air_intake"),
	};
}
