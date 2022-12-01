package org.altadoon.gt6x.features.steelmaking;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.fluid.FluidTankGT;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.OreDictPrefix;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import gregapi.util.OM;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.Log;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;

import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityBlastFurnace extends TileEntityBase10MultiBlockMachine implements ITileEntityCrucible {
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
                                LOG.debug("crucible output side at (" + tX+i + "," + tY+j + "," + tZ+k + ")");
                                design = 1;
                                side_io &= MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE;
                            }
                            break;
                        case 1: side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN; break;
                        case 4: side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT; break;
                        default: side_io = MultiTileEntityMultiBlockPart.NOTHING; break;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 60, mteRegID, design, side_io))
                        tSuccess = false;
                }
            }
            return tSuccess;
        }
        return mStructureOkay;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.blastfurnace.1", "3x5x3 hollow of 41 Blast Furnace Parts (excl. main) with Air inside");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.2", "Main centered at bottom-side facing outwards.");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.3", "Energy in from bottom layer;");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.4", "Molten metal and slag out holes in bottom layer: metal from right and slag from left;");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.5", "Air in at second layer;");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.6", "Stuff in and fluids out at the top.");
    }
    
    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.blastfurnace.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.blastfurnace.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.blastfurnace.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.blastfurnace.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.blastfurnace.5"));
        aList.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.blastfurnace.6"));
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
    public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {
        return null;
    }

    @Override
    public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
        return null;
    }

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidInputTarget(byte aSide) {
        return null;
    }

    @Override
    public DelegatorTileEntity<IFluidHandler> getFluidOutputTarget(byte aSide, Fluid aOutput) {
        return getAdjacentTank(SIDE_TOP);
    }

    @Override
    public void doOutputFluids() {
        for (FluidTankGT tTank : mTanksOutput) {
            Fluid tFluid = tTank.fluid();
            if (tFluid != null && tTank.has() && FL.is(tFluid, "blastfurnacegas")) {
                if (FL.move(tTank, getFluidOutputTarget(SIDE_UNDEFINED, null)) > 0) updateInventory();
            }
        }
    }

    private void dummyFillMoldWithIngot(ITileEntityMold aMold, OreDictMaterialStack aMaterial, long aTemperature, byte aSide) {
        OreDictPrefix tPrefix = OreDictPrefix.get("ingot");
        if (tPrefix != null && aMold.isMoldInputSide(aSide) && aMaterial.mAmount > 0) {
            if (tPrefix.mat(aMaterial.mMaterial.mTargetSolidifying.mMaterial, 1) != null) {
                long tRequiredAmount = aMold.getMoldRequiredMaterialUnits(), rAmount = UT.Code.units(tRequiredAmount, U, aMaterial.mMaterial.mTargetSolidifying.mAmount, T);
                if (aMaterial.mAmount > 0) {
                    LOG.debug("checks succeeded");
                } else {
                    LOG.debug("checks failed: want " + rAmount + ", got " + aMaterial.mAmount + " required amount: " + tRequiredAmount + ", solidifying amount: " + aMaterial.mMaterial.mTargetSolidifying.mAmount);
                }
            } else {
                LOG.debug("checks failed: solidifying material = " + aMaterial.mMaterial.mTargetSolidifying.mMaterial);
            }
        } else {
            LOG.debug("checks failed: tPrefix " + tPrefix + " is input side: " + aMold.isMoldInputSide(aSide) + " amount: " + aMaterial.mAmount);
        }
    }

    private boolean pour(ITileEntityMold aMold, byte aSideOfMold, FluidTankGT origin, OreDictMaterial material) {
        if (U % material.mLiquid.amount != 0) {
            LOG.warn("Melting amount of material " + material.getLocal() + " not a divisor of U. Rounding down...");
        }

        long units_per_liter = U / material.mLiquid.amount;
        long available_U = origin.amount() * units_per_liter;
        OreDictMaterialStack as_stack = OM.stack(material, available_U);

        dummyFillMoldWithIngot(aMold, as_stack, material.mMeltingPoint, aSideOfMold);

        long will_pour_U = aMold.fillMold(as_stack, material.mMeltingPoint, aSideOfMold);
        LOG.debug("trying to fill mold with " + will_pour_U + "U of " + material.getLocal() + " (available: " + available_U + "U or " + origin.amount() + "L)");
        if (will_pour_U > 0 && available_U >= will_pour_U) {
            long will_pour_L = will_pour_U / units_per_liter;
            origin.remove(will_pour_L);
            updateInventory();
            LOG.debug("successfully poured ");
            return true;
        } else {
            LOG.debug("failed to pour");
            return false;
        }
    }

    @Override
    public boolean fillMoldAtSide(ITileEntityMold aMold, byte aSide /* north/east */, byte aSideOfMold) {
        if (!checkStructure(false)) return false;

        byte relative_side = FACING_ROTATIONS[mFacing][aSide];

        for (FluidTankGT tTank : mTanksOutput) {
            Fluid tFluid = tTank.fluid();
            LOG.debug("trying to fill mold with " + tFluid.getName() +", side = " + relative_side + ", side of mold = " + aSideOfMold + ", left = " + SIDE_LEFT,  ", right = " + SIDE_RIGHT);
            if (FL.is(tFluid, "molten.slag") && relative_side == SIDE_LEFT) {
                if (pour(aMold, aSideOfMold, tTank, MTx.Slag)) return true;
            } else if (!FL.is(tFluid, "molten.slag") && !FL.is(tFluid, "blastfurnacegas") && relative_side == SIDE_RIGHT) {
                OreDictMaterialStack tMaterial = OreDictMaterial.FLUID_MAP.get(tFluid.getName());
                if (pour(aMold, aSideOfMold, tTank, tMaterial.mMaterial)) return true;
            }
        }
        LOG.debug("failed to pour");
        return false;
    }
}
