package org.altadoon.gt6x.common;

import gregapi.block.MaterialMachines;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.*;

public class MTEx {
    public static void touch() {}
    public static MultiTileEntityRegistry gt6xMTEReg = new gregapi.block.multitileentity.MultiTileEntityRegistry("gt6x.multitileentity");

    public static MultiTileEntityBlock PlasticBlock = MultiTileEntityBlock.getOrCreate(Gt6xMod.MOD_ID, "redstoneLight", Material.redstoneLight, Block.soundTypeWood, TOOL_saw, 0, 0, 15, false, false);
    public static MultiTileEntityBlock MachineBlock = MultiTileEntityBlock.getOrCreate(Gt6xMod.MOD_ID, "machine", MaterialMachines.instance, Block.soundTypeMetal, TOOL_wrench, 0, 0, 15, false, false);
}
