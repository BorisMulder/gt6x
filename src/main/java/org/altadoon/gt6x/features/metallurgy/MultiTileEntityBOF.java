package org.altadoon.gt6x.features.metallurgy;

import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockMachine;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.MTEx;

import static gregapi.data.CS.*;
import static gregapi.data.CS.SIDE_Z_NEG;

public class MultiTileEntityBOF extends TileEntityBase10MultiBlockMachine implements ITileEntityCrucible {
    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);

        int tX = getOffsetXN(mFacing), tY = yCoord, tZ = getOffsetZN(mFacing);
        if (worldObj.blockExists(tX-1, tY, tZ-1) && worldObj.blockExists(tX+1, tY, tZ-1) && worldObj.blockExists(tX-1, tY, tZ+1) && worldObj.blockExists(tX+1, tY, tZ+1)) {
            boolean tSuccess = true;
            for (int i = -1; i <= 1; i++) for (int j = 0; j < 3; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j != 0 && k == 0) {
                    if (getAir(tX+i, tY+j, tZ+k)) worldObj.setBlockToAir(tX+i, tY+j, tZ+k); else tSuccess = false;
                } else {
                    int side_io = MultiTileEntityMultiBlockPart.NOTHING;
                    int design = 0;
                    switch (j) {
                        case 0:
                            if ((i == 0 && k != 0 && (mFacing == SIDE_X_POS || mFacing == SIDE_X_NEG)) ||
                                (i != 0 && k == 0 && (mFacing == SIDE_Z_POS || mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io = (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE & MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT);
                            }
                            break;
                        case 2: side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN & MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN & MultiTileEntityMultiBlockPart.ONLY_ITEM_IN; break;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 86, mteRegID, design, side_io))
                        tSuccess = false;
                }
            }
            return tSuccess;
        }

        return mStructureOkay;
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return false;
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
        return null;
    }

    @Override
    public String getTileEntityName() {
        return null;
    }

    @Override
    public boolean fillMoldAtSide(ITileEntityMold aMold, byte aSide, byte aSideOfMold) {
        return false;
    }
}
