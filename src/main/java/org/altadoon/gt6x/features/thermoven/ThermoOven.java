package org.altadoon.gt6x.features.thermoven;
import org.altadoon.gt6x.common.RMx;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.common.MTEx;

import static gregapi.data.CS.*;
/**
 * @author Francisco Lobaton (Roku)
 *
 * Implementation of the thermolysis oven and its recipe map
 */

public class ThermoOven extends GT6XFeature {
    public static final String FEATURE_NAME = "ThermolysisOven";

    @Override
    public String name() { return FEATURE_NAME;}

    @Override public void preInit() {}
    @Override public void init() {}
    @Override public void postInit() {}

    @Override public void afterGt6Init() {
        OreDictMaterial mat;
        mat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Thermolysis Oven ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.ThermolysisOven1.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,mat,NBT_HARDNESS, 6.0F , NBT_RESISTANCE, 6.0F , NBT_INPUT, 32  , NBT_TEXTURE, "thermolysisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Thermolysis, NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(mat),'M', OP.pipeMedium.dat(mat),'C', OP.casingMachine.dat(mat),'S',OP.pipeSmall.dat(mat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
        mat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Thermolysis Oven ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.ThermolysisOven2.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,mat,NBT_HARDNESS, 4.0F , NBT_RESISTANCE, 4.0F , NBT_INPUT, 128 , NBT_TEXTURE, "thermolysisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Thermolysis, NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(mat),'M', OP.pipeMedium.dat(mat),'C', OP.casingMachine.dat(mat),'S',OP.pipeSmall.dat(mat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
        mat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Thermolysis Oven ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.ThermolysisOven3.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,mat,NBT_HARDNESS, 9.0F , NBT_RESISTANCE, 9.0F , NBT_INPUT, 512 , NBT_TEXTURE, "thermolysisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Thermolysis, NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(mat),'M', OP.pipeMedium.dat(mat),'C', OP.casingMachine.dat(mat),'S',OP.pipeSmall.dat(mat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
        mat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Thermolysis Oven ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.ThermolysisOven4.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,mat,NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "thermolysisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Thermolysis, NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(mat),'M', OP.pipeMedium.dat(mat),'C', OP.casingMachine.dat(mat),'S',OP.pipeSmall.dat(mat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
    }
}
