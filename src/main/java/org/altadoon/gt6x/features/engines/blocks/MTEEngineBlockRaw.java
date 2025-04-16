package org.altadoon.gt6x.features.engines.blocks;

import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.altadoon.gt6x.common.rendering.Geometry;
import org.altadoon.gt6x.common.rendering.IconRotated;

import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.rendering.Geometry.NOT_ALONG_AXIS;
import static org.altadoon.gt6x.common.rendering.Geometry.ROLL_INDEXES;

public class MTEEngineBlockRaw extends TileEntityBase09FacingSingle {
	@Override
	public String getTileEntityName() {return "gt6x.multitileentity.generator.engine_block_raw";}

	@Override
	public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
		return 8;
	}

	protected ITexture getRotatedFlippedTexture(int idx, byte side1, byte side2) {
		IIconContainer baseIcon = new IconRotated.RotatableIconContainer(baseIcons[idx], ROLL_INDEXES[side1][side2]);
		IIconContainer overlayIcon = new IconRotated.RotatableIconContainer(overlayIcons[idx], ROLL_INDEXES[side1][side2]);

		return BlockTextureMulti.get(BlockTextureDefault.get(baseIcon, mRGBa), BlockTextureDefault.get(overlayIcon));
	}

	@Override
	public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		if (renderPass == 2) {
			if (side == SIDE_TOP)
				return getRotatedFlippedTexture(1, SIDE_BOTTOM, mFacing);
			else if (ALONG_AXIS[side][mFacing])
				return getRotatedFlippedTexture(2, mFacing, SIDE_BOTTOM);
		} else if ((renderPass == 3 || renderPass == 7) && ALONG_AXIS[side][mFacing]) {
			return getRotatedFlippedTexture(3, mFacing, SIDE_BOTTOM);
		}
		return BlockTextureMulti.get(BlockTextureDefault.get(baseIcons[0], mRGBa), BlockTextureDefault.get(overlayIcons[0]));
	}

	@Override
	public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
		return switch (renderPass) {
			// lower, middle, upper engine block
			case 0 -> box(block, Geometry.rotateOnce(mFacing, PX_P[4], PX_P[0], PX_P[1], PX_N[4], PX_P[3], PX_N[1]));
			case 1 -> box(block, Geometry.rotateOnce(mFacing, PX_P[3], PX_P[3], PX_P[1], PX_N[3], PX_P[7], PX_N[1]));
			case 2 -> box(block, Geometry.rotateOnce(mFacing, PX_P[5], PX_P[7], PX_P[1], PX_N[5], PX_N[4], PX_N[1]));
			// 4+1 sides
			case 3 -> box(block, Geometry.rotateOnce(mFacing, PX_P[4], PX_P[7], PX_P[1], PX_N[4], PX_N[5], PX_P[2]));
			case 4 -> box(block, Geometry.rotateOnce(mFacing, PX_P[4], PX_P[7], PX_P[4], PX_N[4], PX_N[5], PX_P[5]));
			case 5 -> box(block, Geometry.rotateOnce(mFacing, PX_P[4], PX_P[7], PX_P[7], PX_N[4], PX_N[5], PX_N[7]));
			case 6 -> box(block, Geometry.rotateOnce(mFacing, PX_P[4], PX_P[7], PX_N[5], PX_N[4], PX_N[5], PX_N[4]));
			case 7 -> box(block, Geometry.rotateOnce(mFacing, PX_P[4], PX_P[7], PX_N[2], PX_N[4], PX_N[5], PX_N[1]));
			default -> false;
		};
	}

	public static IIconContainer[] baseIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/pipes"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/cylinders"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/cylinders_side"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/block_side"),
	}, overlayIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/pipes"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/cylinders"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/cylinders_side"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/block_side"),
	};

	@Override public boolean[] getValidSides() { return NOT_ALONG_AXIS[SIDE_TOP]; }
	@Override public byte getDefaultSide() { return SIDE_FRONT; }
	@Override public boolean isSideSolid2(byte side) { return false; }
	@Override public boolean isSurfaceOpaque2(byte side) { return false; }
	@Override public boolean allowCovers(byte aSide) { return false; }
	@Override public boolean canDrop(int aSlot) { return false; }
	@Override public boolean addDefaultCollisionBoxToList() {return false;}

	@Override
	public void addCollisionBoxesToList2(AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity) {
		box(aabb, list, Geometry.rotateOnce(mFacing, PX_P[3], PX_P[0], PX_P[1], PX_N[3], PX_N[4], PX_N[1]));
	}
}
