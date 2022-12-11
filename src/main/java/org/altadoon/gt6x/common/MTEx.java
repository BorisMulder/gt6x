package org.altadoon.gt6x.common;

import gregapi.block.MaterialMachines;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.MD;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.*;

public class MTEx {
    public static void touch() {}
    public static MultiTileEntityRegistry gt6xMTEReg = new MultiTileEntityRegistry("gt6x.multitileentity");
    public static MultiTileEntityRegistry gt6Registry = MultiTileEntityRegistry.getRegistry("gt.multitileentity");

    public static MultiTileEntityBlock PlasticBlock = MultiTileEntityBlock.getOrCreate(Gt6xMod.MOD_ID, "redstoneLight", Material.redstoneLight, Block.soundTypeWood, TOOL_saw, 0, 0, 15, false, false);
    public static MultiTileEntityBlock MachineBlock = MultiTileEntityBlock.getOrCreate(Gt6xMod.MOD_ID, "machine", MaterialMachines.instance, Block.soundTypeMetal, TOOL_wrench, 0, 0, 15, false, false);
    public static MultiTileEntityBlock StoneBlock = MultiTileEntityBlock.getOrCreate(MD.GT.mID, "rock", Material.rock, Block.soundTypeStone, TOOL_pickaxe, 0, 0, 15, false, false);
}
