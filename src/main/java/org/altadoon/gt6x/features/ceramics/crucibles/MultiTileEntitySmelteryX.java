package org.altadoon.gt6x.features.ceramics.crucibles;

public class MultiTileEntitySmelteryX extends gregtech.tileentity.tools.MultiTileEntitySmeltery {
    @Override public String getTileEntityName() {return "gt6x.multitileentity.smeltery";}
    @Override public long getTemperatureMax(byte aSide) { return mMaterial.mMeltingPoint;}
}
