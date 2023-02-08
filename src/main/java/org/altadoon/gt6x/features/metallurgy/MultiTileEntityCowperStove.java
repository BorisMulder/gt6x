package org.altadoon.gt6x.features.metallurgy;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.*;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.MTEx;

import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityCowperStove extends TileEntityBase10MultiBlockMachine {
    @Override
    public boolean checkStructure2() {
        int tX = xCoord, tY = yCoord, tZ = zCoord;
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);

        if (worldObj.blockExists(tX-1, tY, tZ-1) && worldObj.blockExists(tX+1, tY, tZ-1) && worldObj.blockExists(tX-1, tY, tZ+1) && worldObj.blockExists(tX+1, tY, tZ+1)) {
            boolean success = true;

            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY, tZ + j, 65, mteRegID, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) {
                        LOG.debug("failed at {}, {}, {}", tX + i, tY, tZ + j);
                        success = false;
                    }
                }
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) for (int k = 1; k <= 2; k++) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY + k, tZ + j, 64, mteRegID, 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_IN)) {
                    LOG.debug("failed at {}, {}, {}", tX + i, tY + k, tZ + j);
                    success = false;
                }
            }
            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
                if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY + 3, tZ + j, (i == 0 && j == 0) ? 64 : 65, mteRegID, 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) {
                    LOG.debug("failed at {}, {}, {}", tX + i, tY, tZ + j);
                    success = false;
                }
            }
            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX, tY + 4, tZ, 65, mteRegID, 0, MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT)) {
                LOG.debug("failed at {}, {}, {}", tX, tY + 4, tZ);
                success = false;
            }

            return success;
        }
        return mStructureOkay;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.cowperstove.1", "3x3 base of 8 Alumina Refractory Bricks, main in the center");
        LH.add("gt6x.tooltip.multiblock.cowperstove.2", "3x3x2 of 18 Alumina Checker Bricks");
        LH.add("gt6x.tooltip.multiblock.cowperstove.3", "3x3 of 8 Alumina Refractory Bricks with 1 Alumina Checker Bricks in the center");
        LH.add("gt6x.tooltip.multiblock.cowperstove.4", "1x1 Alumina Refractory Bricks centered on top");
        LH.add("gt6x.tooltip.multiblock.cowperstove.5", "Energy in from bottom, fluids in at the Checker Bricks, fluids out at the top");
    }

    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.cowperstove.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.cowperstove.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.cowperstove.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.cowperstove.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.cowperstove.5"));
        super.addToolTips(aList, aStack, aF3_H);
    }

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.cowperstove";
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        if (aY >= yCoord && aY <= yCoord + 3) {
            return aX >= xCoord - 1 && aX <= xCoord + 1 && aZ >= zCoord - 1 && aZ <= zCoord + 1;
        } else {
            return aY == yCoord + 4 && aX == xCoord && aZ == zCoord;
        }
    }

    @Override
    public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {
        return null;
    }

    @Override
    public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
        return getAdjacentTileEntity(SIDE_BOTTOM);
    }

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {
        return null;
    }

    public DelegatorTileEntity<IFluidHandler> mFluidOutputTarget = null;

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
        if (aOutput == null) return null;

        if (mFluidOutputTarget != null && mFluidOutputTarget.exists()) return mFluidOutputTarget;

        int tX = xCoord, tY = yCoord+4, tZ = zCoord;
        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
            if (i == 0 && j == 0) continue;

            DelegatorTileEntity<TileEntity> target = WD.te(worldObj, tX+i, tY, tZ+j, SIDE_BOTTOM, false);
            if (target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), aOutput)) {
                return mFluidOutputTarget = new DelegatorTileEntity<>((IFluidHandler)target.mTileEntity, target);
            }
        }
        DelegatorTileEntity<TileEntity> target = WD.te(worldObj, tX, tY+1, tZ, SIDE_BOTTOM, false);
        if (target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), aOutput)) {
            return mFluidOutputTarget = new DelegatorTileEntity<>((IFluidHandler) target.mTileEntity, target);
        }

        return mFluidOutputTarget = null;
    }

    @Override
    public void updateAdjacentToggleableEnergySources() {
        int tX = getOffsetXN(mFacing) - 1, tZ = getOffsetZN(mFacing) - 1;

        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            DelegatorTileEntity<TileEntity> tDelegator = WD.te(worldObj, tX+i, yCoord-1, tZ+j, SIDE_TOP, false);

            if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, true)) {
                ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
            }
        }
    }
}
