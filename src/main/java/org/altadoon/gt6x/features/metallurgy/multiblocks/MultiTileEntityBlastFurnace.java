package org.altadoon.gt6x.features.metallurgy.multiblocks;

import gregapi.data.*;
import gregapi.fluid.FluidTankGT;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.OM;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;

import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityBlastFurnace extends TileEntityBase10MultiBlockMachine implements ITileEntityCrucible {
    private int partId = MTEx.IDs.BFPartIron.get();

    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        if (aNBT.hasKey(NBT_DESIGN)) partId = aNBT.getShort(NBT_DESIGN);
    }

    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);

        int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
        if (worldObj.blockExists(tX-1, tY, tZ-1) && worldObj.blockExists(tX+1, tY, tZ-1) && worldObj.blockExists(tX-1, tY, tZ+1) && worldObj.blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = true;
            for (int i = -1; i <= 1; i++) for (int j = 0; j < 5; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j != 0 && j != 4 && k == 0) {
                    if (getAir(tX+i, tY+j, tZ+k)) worldObj.setBlockToAir(tX+i, tY+j, tZ+k); else tSuccess = false;
                } else {
                    int side_io;
                    int design = 0;
                    switch (j) {
                        case 0:
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;
                            if ((i == 0 && k != 0 && (mFacing == SIDE_X_POS || mFacing == SIDE_X_NEG)) ||
                                (i != 0 && k == 0 && (mFacing == SIDE_Z_POS || mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io &= (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE & MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT);
                            }
                            break;
                        case 1: side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN; break;
                        case 4: side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT; break;
                        default: side_io = MultiTileEntityMultiBlockPart.NOTHING; break;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, partId, mteRegID, design, side_io))
                        tSuccess = false;
                }
            }
            return tSuccess;
        }
        return mStructureOkay;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.blastfurnace.1", "3x5x3 hollow of the block you crafted this with (excl. main) with Air inside;");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.2", "Main centered at bottom-side facing outwards.");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.3", "Energy in from bottom side.");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.4", "Molten metal out at right hole in bottom layer, crucible molds, crossings, pipes, etc. usable");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.5", "Same for slag but at left hole, both liquid and solid");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.6", "Air in at second layer.");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.7", "Items in (not automatic; hoppers recommended) and gases out (automatic) at the top.");
    }
    
    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.1"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.2"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.3"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.4"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.5"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.6"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.blastfurnace.7"));
        super.addToolTips(aList, aStack, aF3_H);
    }

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.blastfurnace";
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = getOffsetXN(mFacing), tY = getOffsetYN(mFacing), tZ = getOffsetZN(mFacing);
        return aX >= tX - 1 && aY >= tY && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 4 && aZ <= tZ + 1;
    }

    @Override
    public DelegatorTileEntity<IInventory> getItemInputTarget(byte side) {
        return null;
    }

    @Override
    public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte side) {
        int tX = xCoord + OFFX[side] * 2 - OFFX[mFacing];
        int tZ = zCoord + OFFZ[side] * 2 - OFFZ[mFacing];
        return WD.te(worldObj, tX, yCoord, tZ, OPOS[side], false);
    }

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte side) {
        return null;
    }

    public DelegatorTileEntity<IFluidHandler> gasOutputTarget = null;
    public DelegatorTileEntity<IFluidHandler> metalOutputTarget = null;
    public DelegatorTileEntity<IFluidHandler> slagOutputTarget = null;

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte side, Fluid output) {
        if (output == null) return null;
        int xCenter = getOffsetXN(mFacing), zCenter = getOffsetZN(mFacing);

        if(output.isGaseous()) {
            if (gasOutputTarget != null && gasOutputTarget.exists()) return gasOutputTarget;

            for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
                DelegatorTileEntity<TileEntity> target = WD.te(worldObj, xCenter+i, yCoord+5, zCenter+j, SIDE_BOTTOM, false);
                if (target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), output)) {
                    return gasOutputTarget = new DelegatorTileEntity<>((IFluidHandler)target.mTileEntity, target);
                }
            }
            return gasOutputTarget = null;
        } else if (FL.is(output, "molten.slag")) {
            if (slagOutputTarget != null && slagOutputTarget.exists()) return slagOutputTarget;

            int i = (mFacing == SIDE_Z_POS) ? -2 : (mFacing == SIDE_Z_NEG) ?  2 : 0;
            int j = (mFacing == SIDE_X_POS) ?  2 : (mFacing == SIDE_X_NEG) ? -2 : 0;
            byte delegatorSide = FACING_TO_SIDE[mFacing][SIDE_RIGHT]; // flipped because it's the side of the output block, not the blast furnace part
            DelegatorTileEntity<TileEntity> target = WD.te(worldObj, xCenter+i, yCoord, zCenter+j, delegatorSide, false);
            if (target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), output)) {
                return slagOutputTarget = new DelegatorTileEntity<>((IFluidHandler)target.mTileEntity, target);
            }
            return slagOutputTarget = null;
        } else {
            if (metalOutputTarget != null && metalOutputTarget.exists()) return metalOutputTarget;

            int i = (mFacing == SIDE_Z_POS) ?  2 : (mFacing == SIDE_Z_NEG) ? -2 : 0;
            int j = (mFacing == SIDE_X_POS) ? -2 : (mFacing == SIDE_X_NEG) ?  2 : 0;
            byte delegatorSide = FACING_TO_SIDE[mFacing][SIDE_LEFT];
            DelegatorTileEntity<TileEntity> target = WD.te(worldObj, xCenter+i, yCoord, zCenter+j, delegatorSide, false);
            if (target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), output)) {
                return metalOutputTarget = new DelegatorTileEntity<>((IFluidHandler)target.mTileEntity, target);
            }
            return metalOutputTarget = null;
        }
    }

    @Override
    public void updateAdjacentToggleableEnergySources() {
        int xCenter = getOffsetXN(mFacing) - 1, zCenter = getOffsetZN(mFacing) - 1;

        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            DelegatorTileEntity<TileEntity> tDelegator = WD.te(worldObj, xCenter+i, yCoord-1, zCenter+j, SIDE_TOP, false);

            if (tDelegator.mTileEntity instanceof ITileEntityAdjacentOnOff && tDelegator.mTileEntity instanceof ITileEntityEnergy && ((ITileEntityEnergy)tDelegator.mTileEntity).isEnergyEmittingTo(mEnergyTypeAccepted, tDelegator.mSideOfTileEntity, true)) {
                ((ITileEntityAdjacentOnOff)tDelegator.mTileEntity).setAdjacentOnOff(getStateOnOff());
            }
        }
    }

    private boolean pour(ITileEntityMold mold, byte sideOfMold, FluidTankGT origin, OreDictMaterial material) {
        if (U % material.mLiquid.amount != 0) {
			LOG.warn("Melting amount of material {} not a divisor of U. Rounding down...", material.getLocal());
        }

        long units_per_liter = U / material.mLiquid.amount;
        long available_U = origin.amount() * units_per_liter;
        OreDictMaterialStack as_stack = OM.stack(material, available_U);

        long will_pour_U = mold.fillMold(as_stack, material.mMeltingPoint, sideOfMold);
        if (will_pour_U > 0 && available_U >= will_pour_U) {
            long will_pour_L = will_pour_U / units_per_liter;
            origin.remove(will_pour_L);
            updateInventory();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean fillMoldAtSide(ITileEntityMold mold, byte side, byte sideOfMold) {
        if (!checkStructure(false)) return false;

        byte relative_side = FACING_ROTATIONS[mFacing][side];

        for (FluidTankGT tank : mTanksOutput) {
            Fluid fluid = tank.fluid();
            if (fluid == null) continue;

            if (FL.is(fluid, "molten.slag") && relative_side == SIDE_LEFT) {
                if (pour(mold, sideOfMold, tank, MTx.Slag)) return true;

            } else if (!FL.is(fluid, "molten.slag") && !fluid.isGaseous() && relative_side == SIDE_RIGHT) {
                OreDictMaterialStack tMaterial = OreDictMaterial.FLUID_MAP.get(fluid.getName());
                if (tMaterial == null) continue;
                if (pour(mold, sideOfMold, tank, tMaterial.mMaterial)) return true;
            }
        }
        return false;
    }
}
