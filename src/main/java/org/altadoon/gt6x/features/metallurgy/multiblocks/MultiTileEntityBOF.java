package org.altadoon.gt6x.features.metallurgy.multiblocks;

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
import gregapi.util.OM;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;

import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.CS.SIDE_Z_NEG;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityBOF extends TileEntityBase10MultiBlockMachine implements ITileEntityCrucible {
    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);

        int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
        if (worldObj.blockExists(tX-1, tY, tZ-1) && worldObj.blockExists(tX+1, tY, tZ-1) && worldObj.blockExists(tX-1, tY, tZ+1) && worldObj.blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = true;
            for (int i = -1; i <= 1; i++) for (int j = 0; j < 3; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j == 1 && k == 0) {
                    if (getAir(tX+i, tY+j, tZ+k)) worldObj.setBlockToAir(tX+i, tY+j, tZ+k); else tSuccess = false;
                } else {
                    int side_io = MultiTileEntityMultiBlockPart.NOTHING;
                    int design = 0;
                    int partMeta = MTEx.IDs.BOFWall.get();
                    switch (j) {
                        case 1:
                            if ((i == 0 && (mFacing == SIDE_X_POS || mFacing == SIDE_X_NEG)) ||
                                (k == 0 && (mFacing == SIDE_Z_POS || mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io = (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE & MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT);
                            }
                            break;
                        case 2:
                            if (i == 0 && k == 0) {
                                design = 2;
                                partMeta = MTEx.IDs.BOFLance.get();
                                side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            } else if ((i == 0 && (mFacing == SIDE_X_POS || mFacing == SIDE_X_NEG)) ||
                                       (k == 0 && (mFacing == SIDE_Z_POS || mFacing == SIDE_Z_NEG))
                            ) {
                                design = 3;
                                side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT;
                            } else {
                                side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN;
                            }
                            break;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, partMeta, mteRegID, design, side_io))
                        tSuccess = false;
                }
            }
            return tSuccess;
        }

        return mStructureOkay;
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = getOffsetXN(mFacing), tY = getOffsetYN(mFacing), tZ = getOffsetZN(mFacing);
        return aX >= tX - 1 && aY >= tY && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 2 && aZ <= tZ + 1;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.bof.1", "3x3x3 hollow of 24 Steel-lined MgO-C Walls (excl. main and top center) with Air inside;");
        LH.add("gt6x.tooltip.multiblock.bof.2", "Main centered at bottom-side facing outwards. Oxygen Lance centered at the top layer");
        LH.add("gt6x.tooltip.multiblock.bof.3", "Molten metal out at right hole in bottom layer, crucible molds, crossings, pipes, etc. usable");
        LH.add("gt6x.tooltip.multiblock.bof.4", "Same for slag but at left hole. Does not automatically output molten metal and slag into pipes");
        LH.add("gt6x.tooltip.multiblock.bof.5", "Oxygen in at the Lance. Items and other fluids in at the top (not the holes)");
        LH.add("gt6x.tooltip.multiblock.bof.6", "Gases out at the two large holes to the left and right of the lance (automatic).");
    }

    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.1"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.2"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.3"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.4"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.5"));
        aList.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.6"));
        super.addToolTips(aList, aStack, aF3_H);
    }

    @Override
    public DelegatorTileEntity<IInventory> getItemInputTarget(byte aSide) {
        return null;
    }

    @Override
    public DelegatorTileEntity<TileEntity> getItemOutputTarget(byte aSide) {
        int tX = xCoord + OFFX[aSide] * 2 - OFFX[mFacing];
        int tZ = zCoord + OFFZ[aSide] * 2 - OFFZ[mFacing];
        return WD.te(worldObj, tX, yCoord+1, tZ, OPOS[aSide], false);
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

        int tX = getOffsetXN(mFacing), tY = yCoord + 3, tZ = getOffsetZN(mFacing);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i == 0 && j != 0 && (mFacing == SIDE_X_POS || mFacing == SIDE_X_NEG)) ||
                    (j == 0 && i != 0 && (mFacing == SIDE_Z_POS || mFacing == SIDE_Z_NEG)))
                {
                    DelegatorTileEntity<TileEntity> tTarget = WD.te(worldObj, tX + i, tY, tZ + j, SIDE_BOTTOM, false);
                    if (tTarget.mTileEntity instanceof IFluidHandler && ((IFluidHandler) tTarget.mTileEntity).canFill(tTarget.getForgeSideOfTileEntity(), aOutput)) {
                        return mFluidOutputTarget = new DelegatorTileEntity<>((IFluidHandler) tTarget.mTileEntity, tTarget);
                    }
                }
            }
        }
        return mFluidOutputTarget = null;
    }

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.bof";
    }

    private boolean pour(ITileEntityMold aMold, byte aSideOfMold, FluidTankGT origin, OreDictMaterial material) {
        if (U % material.mLiquid.amount != 0) {
            LOG.warn("Melting amount of material " + material.getLocal() + " not a divisor of U. Rounding down...");
        }

        long units_per_liter = U / material.mLiquid.amount;
        long available_U = origin.amount() * units_per_liter;
        OreDictMaterialStack as_stack = OM.stack(material, available_U);

        long will_pour_U = aMold.fillMold(as_stack, material.mMeltingPoint, aSideOfMold);
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
    public boolean fillMoldAtSide(ITileEntityMold aMold, byte aSide /* north/east */, byte aSideOfMold) {
        if (!checkStructure(false)) return false;

        byte relative_side = FACING_ROTATIONS[mFacing][aSide];

        for (FluidTankGT tTank : mTanksOutput) {
            Fluid tFluid = tTank.fluid();
            if (tFluid == null) continue;

            if (FL.is(tFluid, "molten.converterslag") && relative_side == SIDE_LEFT) {
                if (pour(aMold, aSideOfMold, tTank, MTx.ConverterSlag)) return true;

            } else if (!FL.is(tFluid, "molten.converterslag") && !tFluid.isGaseous() && relative_side == SIDE_RIGHT) {
                OreDictMaterialStack tMaterial = OreDictMaterial.FLUID_MAP.get(tFluid.getName());
                if (tMaterial == null) continue;
                if (pour(aMold, aSideOfMold, tTank, tMaterial.mMaterial)) return true;
            }
        }
        return false;
    }

    private boolean isRightFluidInput(MultiTileEntityMultiBlockPart part, Fluid fluid) {
        int x = getOffsetXN(mFacing), y = yCoord + 2, z = getOffsetZN(mFacing);

        if (FL.Oxygen.is(fluid)) {
            return part.xCoord == x && part.yCoord == y && part.zCoord == z;
        } else {
            return part.yCoord == y && (
                   (part.xCoord != x && (mFacing == SIDE_X_POS || mFacing == SIDE_X_NEG)) ||
                   (part.zCoord != z && (mFacing == SIDE_Z_POS || mFacing == SIDE_Z_NEG))
            );
        }
    }

    @Override
    public boolean canFill(MultiTileEntityMultiBlockPart part, byte side, Fluid fluid) {
        return isRightFluidInput(part, fluid) && super.canFill(part, side, fluid);
    }

    @Override
    public int fill(MultiTileEntityMultiBlockPart part, byte side, FluidStack stack, boolean doFill) {
        if (isRightFluidInput(part, stack.getFluid())) {
            return super.fill(part, side, stack, doFill);
        } else {
            return 0;
        }
    }
}
