package org.altadoon.gt6x.features.metallurgy;

import gregapi.data.LH;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
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

public class MultiTileEntityShaftFurnace extends TileEntityBase10MultiBlockMachine {
    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);
        int tX = getOffsetXN(mFacing), tY = yCoord - 2, tZ = getOffsetZN(mFacing);
        LOG.debug("Centering at {}, {}, {}", tX, tY, tZ);
        if (worldObj.blockExists(tX-1, tY+2, tZ) && worldObj.blockExists(tX+1, tY+2, tZ) && worldObj.blockExists(tX, tY+2, tZ-1) && worldObj.blockExists(tX, tY+2, tZ+1)) {
            boolean tSuccess = true;
            for (int i = -1; i <= 1; i++) for (int j = 0; j < 6; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j >= 1 && j < 5 && k == 0) {
                    if (getAir(tX+i, tY+j, tZ+k)) worldObj.setBlockToAir(tX+i, tY+j, tZ+k); else {
                        tSuccess = false;
                        LOG.warn("Expected air at {}, {}, {}", tX+i, tY+j, tZ+k);
                    }
                } else {
                    int side_io = MultiTileEntityMultiBlockPart.NOTHING;
                    switch (j) {
                        case 0:
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_OUT;
                            if (i != 0 || k != 0) continue;
                            break;
                        case 1:
                        case 2:
                            if (i == k || i == -k) continue;
                            break;
                        case 3:
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            break;
                        case 5:
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 66, mteRegID, 0, side_io)) {
                        tSuccess = false;
                        LOG.warn("Expected part at {}, {}, {}", tX+i, tY+j, tZ+k);
                    }
                }
            }
            return tSuccess;
        }
        LOG.warn("blockExists checks failed");
        return mStructureOkay;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.1", "1x1x1 Base of 1 Shaft Furnace Part (centered on a 3x3 grid)");
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.2", "on top of that two layers of 4 diagonally adjacent Shaft Furnace Parts (incl. main) with one block of Air in-between;");
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.3", "Main at side of third layer facing outwards;");
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.4", "Three 3x3x3 layers of 25 Shaft Furnace Parts with Air in the middle except at the top layer (total 33 parts + 1 main).");
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.5", "Energy and fluids in at the fourth layer (bottom and sides, resp.)");
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.6", "Items in at the top, out at the bottom (not automatic; hoppers recommended)");
        LH.add("gt6x.tooltip.multiblock.shaftfurnace.7", "Fluids out automatically at the top.");
    }
    
    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.1"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.2"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.3"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.4"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.5"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.6"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.shaftfurnace.7"));
        super.addToolTips(aList, aStack, aF3_H);
    }

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.shaftfurnace";
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = getOffsetXN(mFacing), tY = yCoord - 2, tZ = getOffsetZN(mFacing);
        switch (aY - tY) {
            case 0:
                return aX == tX && aZ == tZ;
            case 1:
            case 2:
                return (aX == tX && aZ >= tZ - 1 && aZ <= tZ + 1) || (aZ == tZ && aX >= tX - 1 && aX <= tX + 1);
            case 3:
            case 4:
            case 5:
                return aX >= tX - 1 && aX <= tX + 1 && aZ >= tZ - 1 && aZ <= tZ + 1;
            default:
                return false;
        }
    }

    @Override
    public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {
        return null;
    }

    @Override
    public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
        int x = getOffsetXN(mFacing), y = yCoord - 3, z = getOffsetZN(mFacing);
        return WD.te(worldObj, x, y, z, SIDE_TOP, false);
    }

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {
        return null;
    }

    public DelegatorTileEntity<IFluidHandler> mFluidOutputTarget = null;

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
        if (aOutput == null || !aOutput.isGaseous()) return null;

        if (mFluidOutputTarget != null && mFluidOutputTarget.exists()) return mFluidOutputTarget;

        int tX = getOffsetXN(mFacing), tY = yCoord+5, tZ = getOffsetZN(mFacing);
        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
            DelegatorTileEntity<TileEntity> tTarget = WD.te(worldObj, tX+i, tY, tZ+j, SIDE_BOTTOM, false);
            if (tTarget.mTileEntity instanceof IFluidHandler && ((IFluidHandler)tTarget.mTileEntity).canFill(tTarget.getForgeSideOfTileEntity(), aOutput)) {
                return mFluidOutputTarget = new DelegatorTileEntity<>((IFluidHandler)tTarget.mTileEntity, tTarget);
            }
        }
        return mFluidOutputTarget = null;
    }

    @Override
    public void updateAdjacentToggleableEnergySources() {
        int tX = getOffsetXN(mFacing) - 1, tZ = getOffsetZN(mFacing) - 1;

        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            DelegatorTileEntity<TileEntity> tDelegator = WD.te(worldObj, tX+i, yCoord-1, tZ+j, SIDE_TOP, false);

            if (!(i == 1 && j == 1) && tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, true)) {
                ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
            }
        }
    }
}
