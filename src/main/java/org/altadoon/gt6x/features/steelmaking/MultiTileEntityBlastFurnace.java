package org.altadoon.gt6x.features.steelmaking;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.CS;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.fluid.FluidTankGT;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.Log;
import org.altadoon.gt6x.common.MTx;

import java.util.List;

import static gregapi.data.CS.*;

public class MultiTileEntityBlastFurnace extends TileEntityBase10MultiBlockMachine implements ITileEntityCrucible {
    @Override
    public boolean checkStructure2() {
        Item bricks = MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(18000).getItem();

        int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
        if (worldObj.blockExists(tX-1, tY, tZ-1) && worldObj.blockExists(tX+1, tY, tZ-1) && worldObj.blockExists(tX-1, tY, tZ+1) && worldObj.blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = true;
            for (int i = -1; i <= 1; i++) for (int j = 0; j < 5; j++) for (int k = -1; k <= 1; k++) {
                Item it = Item.getItemFromBlock(worldObj.getBlock(tX + 1, tY + j, tZ + k));

                if (i == 0 && j != 0 && j != 4 && k == 0) {
                    if (getAir(tX+i, tY+j, tZ+k)) worldObj.setBlockToAir(tX+i, tY+j, tZ+k); else {
                        CS.OUT.println("Invalid block at (" + tX+1 + "," + tY+j + "," + (tZ+k) + ") - expected air, got " + it.getUnlocalizedName() + " - " + Item.getIdFromItem(it));
                        tSuccess = false;
                    }
                } else {
                    int side_type;
                    switch (j) {
                        case 0: side_type = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN; break;
                        case 1: side_type = MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE; break;
                        case 2: side_type = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN; break;
                        case 4: side_type = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT; break;
                        default: side_type = MultiTileEntityMultiBlockPart.NOTHING; break;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 18000, MultiTileEntityRegistry.getRegistry("gt.multitileentity"), 0, side_type)) {
                        CS.OUT.println("Invalid block at (" + tX+1 + "," + tY+j + "," + (tZ+k) + ") - expected " + bricks.getUnlocalizedName() + " - " + Item.getIdFromItem(bricks) + " got " + it.getUnlocalizedName() + " - " + Item.getIdFromItem(it));
                        tSuccess = false;
                    }
                }
            }
            return tSuccess;
        }
        return mStructureOkay;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.blastfurnace.1", "3x5x3 hollow of 41 Fire Bricks (excl. main) with Air inside");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.2", "Main centered at bottom-side facing outwards.");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.3", "Energy in from bottom layer;");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.4", "Molten metal and slag out at second layer: metal from left and slag from right;");
        LH.add("gt6x.tooltip.multiblock.blastfurnace.5", "Air in at third layer;");
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

    @Override
    public boolean fillMoldAtSide(ITileEntityMold aMold, byte aSide, byte aSideOfMold) {
        for (FluidTankGT tTank : mTanksOutput) {
            Fluid tFluid = tTank.fluid();
            if (FL.is(tFluid, "molten.slag") && aSide == SIDE_LEFT) {
                long tAmount = aMold.fillMold(new OreDictMaterialStack(MTx.Slag, tTank.amount()), MTx.Slag.mMeltingPoint, aSideOfMold);
                if (tAmount > 0 && tTank.amount() >= tAmount) {
                    tTank.remove(tAmount);
                    updateInventory();
                    return true;
                }
            } else if (!FL.is(tFluid, "molten.slag") && !FL.is(tFluid, "blastfurnacegas") && aSide == SIDE_RIGHT) {
                OreDictMaterialStack tMaterial = OreDictMaterial.FLUID_MAP.get(tFluid.getName());

                long tAmount = aMold.fillMold(tMaterial, tMaterial.mMaterial.mMeltingPoint, aSideOfMold);
                if (tAmount > 0 && tTank.amount() >= tAmount) {
                    tTank.remove(tAmount);
                    updateInventory();
                    return true;
                }
            }
        }
        return false;
    }
}
