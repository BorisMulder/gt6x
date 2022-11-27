package org.altadoon.gt6x.features.metallurgy.crucibles;

import net.minecraft.block.Block;
import org.altadoon.gt6x.common.MTEx;

/**
 * A large crucible that has walls from the GT6X MTE registry.
 */
public class MultiTileEntityCrucibleX extends gregtech.tileentity.multiblocks.MultiTileEntityCrucible {
    @Override public String getTileEntityName() {return "gt6x.multitileentity.multiblock.crucible";}
    @Override public short getMultiTileEntityRegistryID() { return (short) Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock); }
    @Override public long getTemperatureMax(byte aSide) { return mMaterial.mMeltingPoint;}
}
