package org.altadoon.gt6x.features.fusion;

import gregapi.data.LH;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregtech.tileentity.multiblocks.MultiTileEntityFusionReactor;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.MTEx;

import java.util.List;
import java.util.ListIterator;

import static gregapi.data.CS.*;

public class MTEFusionReactor extends MultiTileEntityFusionReactor {
	private boolean success;

	@Override
	public boolean checkStructure2() {
		int x = getOffsetXN(mFacing, 2), y = yCoord, z = getOffsetZN(mFacing, 2);
		if (worldObj.blockExists(x-9, y, z-9) && worldObj.blockExists(x+9, y, z-9) && worldObj.blockExists(x-9, y, z+9) && worldObj.blockExists(x+9, y, z+9)) {
			success = true;

			// Central Solenoid
			for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) for (int k = -2; k <= 2; k++) {
				if (Math.abs(i) == 2 && Math.abs(k) == 2) continue; // corner of cylinder
				if (Math.abs(i) + Math.abs(k) < 2) continue; // hole inside cylinder
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+j, z+k, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
			}

			// Connecting stainless walls
			if (mFacing != SIDE_X_NEG) {
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-3, y, z  , 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-4, y, z  , 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
			}
			if (mFacing != SIDE_X_POS) {
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+3, y, z  , 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+4, y, z  , 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
			}
			if (mFacing != SIDE_Z_NEG) {
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y, z-3, 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y, z-4, 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
			}
			if (mFacing != SIDE_Z_POS) {
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y, z+3, 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y, z+4, 18002, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
			}

			// Outside toroid
			x -= 9; z -= 9;

			for (int i = 0; i < 19; i++) for (int j = 0; j < 19; j++) {
				if (OCTAGONS[0][i][j]) {
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-1, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
					if ((i == 9 && (j == 0 || j == 18)) || (j == 9 && (i == 0 || i == 18))) {
						if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y  , z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 2, MultiTileEntityMultiBlockPart.ONLY_ENERGY_OUT)) success = false;
					} else {
						if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y  , z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, mActive ? 4 : 3, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) success = false;
					}
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+1, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
				}
				if (OCTAGONS[1][i][j]) {
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-2, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-1, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y  , z+j, MTEx.IDs.BWWall.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+1, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+2, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
				}
				if (OCTAGONS[2][i][j]) {
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-2, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-1, z+j, MTEx.IDs.BWWall.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (getAir(x+i, y, z+j)) worldObj.setBlockToAir(x+i, y, z+j); else success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+1, z+j, MTEx.IDs.BWWall.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+2, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
				}
			}
			return success;
		}
		return mStructureOkay;
	}

	static {
		LH.add("gt6x.tooltip.multiblock.fusionreactor.2", "Central solenoid: 80 Superconductor Coils (including controller).");
		LH.add("gt6x.tooltip.multiblock.fusionreactor.3", "In-between: 6 Stainless Steel Walls.");
		LH.add("gt6x.tooltip.multiblock.fusionreactor.4", "Outer toroid: 288 Tungstensteel Walls, 144 Boronized Tungsten Walls, 288 additional Superconductor Coils.");
	}

	@Override
	public void addToolTips(List<String> list, ItemStack stack, boolean f3_H) {
		super.addToolTips(list, stack, f3_H);

		for (ListIterator<String> i = list.listIterator(); i.hasNext();) {
			String tooltip = i.next();
			if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.2"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.2"));
			} else if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.3"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.3"));
			} else if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.4"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.4"));
			}
		}
	}
	
	@Override public String getTileEntityName() {return "gt6x.multitileentity.multiblock.fusionreactor";}
}
