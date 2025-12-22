package org.altadoon.gt6x.features.fusion;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregtech.tileentity.multiblocks.MultiTileEntityFusionReactor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.altadoon.gt6x.common.MTEx;

import java.util.*;

import static gregapi.data.CS.*;

public class MTEFusionReactor extends MultiTileEntityFusionReactor {
	@Override
	public boolean checkStructure2() {
		int x = getOffsetXN(mFacing, 2), y = yCoord, z = getOffsetZN(mFacing, 2);
		if (worldObj.blockExists(x-9, y, z-9) && worldObj.blockExists(x+9, y, z-9) && worldObj.blockExists(x-9, y, z+9) && worldObj.blockExists(x+9, y, z+9)) {
			boolean success = true;

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
						if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y  , z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 2, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) success = false;
					} else {
						if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y  , z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, mActive ? 4 : 3, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) success = false;
					}
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+1, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
				}
				if (OCTAGONS[1][i][j]) {
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-2, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-1, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y  , z+j, MTEx.IDs.BWWall.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+1, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+2, z+j, 18003, MTEx.gt6MTERegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN)) success = false;
				}
				if (OCTAGONS[2][i][j]) {
					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-2, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y-1, z+j, MTEx.IDs.BWWall.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (getAir(x+i, y, z+j)) worldObj.setBlockToAir(x+i, y, z+j); else success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+1, z+j, MTEx.IDs.BWWall.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;

					if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+i, y+2, z+j, MTEx.IDs.SuperconductorCoil.get(), MTEx.gt6xMTERegId, 0, MultiTileEntityMultiBlockPart.NOTHING)) success = false;
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
		LH.add("gt6x.tooltip.multiblock.fusionreactor.5", "EU input at the electric interfaces");
		LH.add("gt6x.tooltip.multiblock.fusionreactor.7", "Nitrogen coolant in at the top coil, out at the bottom coil");
		LH.add("gt6x.tooltip.multiblock.fusionreactor.8", "Other items and fluids in at the top of the walls, out at the bottom of the walls");
	}

	@Override
	public void addToolTips(List<String> list, ItemStack stack, boolean f3_H) {
		super.addToolTips(list, stack, f3_H);

		for (ListIterator<String> i = list.listIterator(); i.hasNext();) {
			String tooltip = i.next();
			if        (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.2"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.2"));
			} else if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.3"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.3"));
			} else if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.4"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.4"));
			} else if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.5"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.5"));
			} else if (tooltip.equals(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.fusionreactor.7"))) {
				i.set(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.7"));
			}
		}

		list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.fusionreactor.8"));
	}
	
	@Override public String getTileEntityName() {return "gt6x.multitileentity.multiblock.fusionreactor";}

	@Override
	protected IFluidTank getFluidTankFillable(MultiTileEntityMultiBlockPart part, byte side, FluidStack fluidToFill) {
		if (FL.equal(fluidToFill, MT.N.mLiquid) != (part.getBlockMetadata() == MTEx.IDs.SuperconductorCoil.get())) {
			return null;
		}
		return super.getFluidTankFillable(part, side, fluidToFill);
	}

	@Override
	protected IFluidTank getFluidTankDrainable(MultiTileEntityMultiBlockPart part, byte side, FluidStack fluidToDrain) {
		if (FL.equal(fluidToDrain, MT.N.mGas) != (part.getBlockMetadata() == MTEx.IDs.SuperconductorCoil.get())) {
			return null;
		}
		return super.getFluidTankDrainable(part, side, fluidToDrain);
	}

	@Override
	public boolean isEnergyType(MultiTileEntityMultiBlockPart part, TagData energyType, byte side, boolean emitting) {
		return !emitting &&
				part.getBlockMetadata() == MTEx.IDs.SuperconductorCoil.get() && (
					part.mDesign == 2 && energyType != TD.Energy.EU ||
					(part.mDesign == 3 || part.mDesign == 4) && energyType != TD.Energy.LU
				);
	}

	@Override
	public Collection<TagData> getEnergyTypes(MultiTileEntityMultiBlockPart part, byte aSide) {
		if (part.mMode != MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN) return Collections.emptyList();

		return switch (part.mDesign) {
			case 2 -> Collections.singletonList(TD.Energy.EU);
			case 3, 4 -> Collections.singletonList(TD.Energy.LU);
			default -> Collections.emptyList();
		};
	}
}
