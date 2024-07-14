package org.altadoon.gt6x.features.distillationtowers;

import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.recipes.Recipe;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.WD;
import gregtech.tileentity.multiblocks.MultiTileEntityDistillationTower;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.MTEx;

import java.util.Arrays;
import java.util.Comparator;

import static gregapi.data.CS.*;
import static gregapi.oredict.OreDictMaterial.FLUID_MAP;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityDistillationTowerX extends MultiTileEntityDistillationTower {
	protected int numFluidLayers = 0;

	@Override public String getTileEntityName() {return "gt6x.multitileentity.distillationtower";}
	@Override public short getMultiTileEntityRegistryID() { return (short) MTEx.gt6xMTERegId; }

	static {
		LH.add("gt.tooltip.multiblock.distillationtower.2", "3x3xN (4 <= N <= 10) of Distillation Tower Parts");
	}

	@Override
	public boolean checkStructure2() {
		int x = getOffsetXN(mFacing), y = yCoord, z = getOffsetZN(mFacing);
		if (worldObj.blockExists(x-1, y, z-1) && worldObj.blockExists(x+1, y, z-1) && worldObj.blockExists(x-1, y, z+1) && worldObj.blockExists(x+1, y, z+1)) {
			int gt6RegId = Block.getIdFromBlock(MTEx.gt6Registry.mBlock);

			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y-1, z-1, 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y-1, z-1, 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y-1, z-1, 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y-1, z  , 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y-1, z  , 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y-1, z  , 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y-1, z+1, 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y-1, z+1, 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y-1, z+1, 18101, gt6RegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) return false;

			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y  , z-1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y  , z-1, 18102, gt6RegId,  mFacing == SIDE_Z_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y  , z-1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y  , z  , 18102, gt6RegId,  mFacing == SIDE_X_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y  , z  , 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y  , z  , 18102, gt6RegId,  mFacing == SIDE_X_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y  , z+1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y  , z+1, 18102, gt6RegId,  mFacing == SIDE_Z_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;
			if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y  , z+1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) return false;

			int currentHeight = 0;
			for (int i = 1; i <= 9; i++) {
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y+i, z-1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y+i, z-1, 18102, gt6RegId,  mFacing == SIDE_Z_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y+i, z-1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y+i, z  , 18102, gt6RegId,  mFacing == SIDE_X_POS ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y+i, z  , 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y+i, z  , 18102, gt6RegId,  mFacing == SIDE_X_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x-1, y+i, z+1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x  , y+i, z+1, 18102, gt6RegId,  mFacing == SIDE_Z_NEG ? 1 : 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x+1, y+i, z+1, 18102, gt6RegId,                              0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) break;
				currentHeight++;
			}

			LOG.debug("Distillation tower height: {}", currentHeight);

			if (currentHeight < 3) return false;
			numFluidLayers = currentHeight;

			return true;
		}
		return mStructureOkay;
	}

	@Override
	public boolean isInsideStructure(int aX, int aY, int aZ) {
		int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
		return aX >= tX - 1 && aY >= tY - 1 && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + numFluidLayers && aZ <= tZ + 1;
	}

	@Override
	public int canOutput(Recipe recipe) {
		int emptyOutputTanks = 0, requiredEmptyTanks = recipe.mFluidOutputs.length;
		if (requiredEmptyTanks > numFluidLayers) return 0;

		for (FluidTankGT fluidTankGT : mTanksOutput) {
			if (fluidTankGT.isEmpty()) emptyOutputTanks++;
			else if (recipe.mNeedsEmptyOutput || (mMode & 1) != 0) return 0;
		}
		emptyOutputTanks -= (mTanksOutput.length - numFluidLayers);
		// This optimisation would not work! The Tanks would not have an Output Amount Limiter if this was in the Code!
		//if (requiredEmptyTanks <= emptyOutputTanks) {
		for (FluidStack output : recipe.mFluidOutputs) {
			if (output == null) {
				requiredEmptyTanks--;
			} else for (FluidTankGT fluidTankGT : mTanksOutput)
				if (fluidTankGT.contains(output)) {
					if (fluidTankGT.has(Math.max(16000, 1 + output.amount * mParallel)) && !FluidsGT.VOID_OVERFLOW.contains(output.getFluid().getName()))
						return 0;
					requiredEmptyTanks--;
					break;
				}
		}
		if (requiredEmptyTanks > emptyOutputTanks) return 0;

		return super.canOutput(recipe);
	}

	@Override public void onProcessFinished() {
		Arrays.sort(mTanksOutput, Comparator.comparingLong((FluidTankGT tank) -> {
			Fluid fluid = tank.fluid();
			if (fluid == null || !tank.has()) return Long.MIN_VALUE;
			OreDictMaterialStack mat = FLUID_MAP.get(fluid.getName());
			if (mat == null) return 0;
			return mat.mMaterial.mBoilingPoint;
		}).reversed());
	}

	@Override
	public void doOutputFluids() {
		int currentFluidOutputNum = 1;

		for (FluidTankGT tTank : mTanksOutput) {
			Fluid fluid = tTank.fluid();
			if (fluid != null && tTank.has()) {
				DelegatorTileEntity<TileEntity> delegator = WD.te(worldObj, getOffsetXN(mFacing, 3), yCoord+currentFluidOutputNum, getOffsetZN(mFacing, 3), mFacing, false);
				if (FL.move(tTank, delegator) > 0) updateInventory();

				if (++currentFluidOutputNum > numFluidLayers) break;
			}
		}
	}

}
