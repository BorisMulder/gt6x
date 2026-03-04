package org.altadoon.gt6x.features.engines.blocks;

import gregapi.data.FL;
import gregtech.tileentity.multiblocks.MultiTileEntityLargeTurbineSteam;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;

import java.util.List;

import static gregapi.data.CS.SIDE_ANY;
import static org.altadoon.gt6x.common.FLx.EU_PER_SC_STEAM;
import static org.altadoon.gt6x.common.FLx.SC_STEAM_PER_HP_WATER;
import static org.altadoon.gt6x.common.MTEx.NBT_MTE_MULTIBLOCK_PART_REG;

public class MTELargeTurbineSteamSC extends MultiTileEntityLargeTurbineSteam {
	@Override public String getTileEntityName() {return "gt6x.multitileentity.multiblock.turbine.steamsc";}

	protected int partRegId = Block.getIdFromBlock(MTEx.gt6MTEReg.mBlock);
	protected boolean checkParts = false;

	@Override
	public void readFromNBT2(NBTTagCompound nbt) {
		super.readFromNBT2(nbt);
		if (nbt.hasKey(NBT_MTE_MULTIBLOCK_PART_REG)) {
			partRegId = nbt.getInteger(NBT_MTE_MULTIBLOCK_PART_REG);
		}
	}

	@Override
	public boolean checkStructure2() {
		checkParts = true;
		boolean result = super.checkStructure2();
		checkParts = false;
		return result;
	}

	@Override
	public void addToolTips(List<String> list, ItemStack stack, boolean f3_H) {
		checkParts = true;
		super.addToolTips(list, stack, f3_H);
		checkParts = false;
	}

	@Override public short getMultiTileEntityRegistryID() {
		if (checkParts) return (short)partRegId;
		return super.getMultiTileEntityRegistryID();
	}

	public static final int SC_STEAM_PER_WATER = SC_STEAM_PER_HP_WATER * 100 * 19 / (99 * 20); // 100 water = 99 HP water, emit 95% (19/20)

	@Override
	public void doConversion(long aTimer) {
		if (mEnergyProducedNextTick > 0) {
			mStorage.mEnergy += mEnergyProducedNextTick;
			mEnergyProducedNextTick = 0;
		} else if (!mStopped && mTanks[0].has(getEnergySizeInputMin(mEnergyIN.mType, SIDE_ANY) / EU_PER_SC_STEAM)) {
			// Turn steam to energy
			long tSteam = mTanks[0].amount();
			mSteamCounter += tSteam;
			mStorage.mEnergy += tSteam * EU_PER_SC_STEAM;
			mEnergyProducedNextTick += tSteam * EU_PER_SC_STEAM;
			mTanks[0].setEmpty();

			// Emit water if we have used up enough steam
			if (mSteamCounter >= SC_STEAM_PER_WATER) {
				mTanks[1].fillAll(FL.DistW.make(mSteamCounter / SC_STEAM_PER_WATER));
				mSteamCounter %= SC_STEAM_PER_WATER;
			}
		}
		super.doConversion(aTimer);
	}

	@Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return !mStopped && FL.equal(aFluidToFill, MTx.HPWater.mGas) ? mTanks[0] : null;}
}
