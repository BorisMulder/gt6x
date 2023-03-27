package org.altadoon.gt6x.features.verticalmixers;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.tileentity.machines.MultiTileEntityBasicMachineElectric;
import gregapi.util.CR;
import gregapi.util.UT;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.CS.T;

public class VerticalMixers extends GT6XFeature {
    public static final String FEATURE_NAME ="Vertical Mixers";
    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {}

    @Override
    public void init() {
        Class<? extends TileEntity> aClass;OreDictMaterial mat;

        /* RU */
        aClass = MultiTileEntityBasicMachine.class;
        mat = MT.DATA.Kinetic_T[1]; MTEx.gt6xMTEReg.add("Vertical Mixer ("                         +mat.getLocal()+")", "Basic Machines"                      , 107, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   7.0F, NBT_RESISTANCE,   7.0F, NBT_INPUT,   32, NBT_TEXTURE, "mixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RM.Mixer, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 4, NBT_PARALLEL_DURATION, T), "hPP","SRM","wPP",'M', OP.casingMachine.dat(mat), 'S', OP.stick.dat(mat), 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plate.dat(MT.StainlessSteel));
        mat = MT.DATA.Kinetic_T[2]; MTEx.gt6xMTEReg.add("Vertical Mixer ("                         +mat.getLocal()+")", "Basic Machines"                      , 108, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   7.0F, NBT_RESISTANCE,   7.0F, NBT_INPUT,   128, NBT_TEXTURE, "mixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RM.Mixer, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 8, NBT_PARALLEL_DURATION, T), "hPP","SRM","wPP",'M', OP.casingMachine.dat(mat), 'S', OP.stick.dat(mat), 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateDouble.dat(MT.StainlessSteel));
        mat = MT.DATA.Kinetic_T[3]; MTEx.gt6xMTEReg.add("Vertical Mixer ("                         +mat.getLocal()+")", "Basic Machines"                      , 109, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   7.0F, NBT_RESISTANCE,   7.0F, NBT_INPUT,   512, NBT_TEXTURE, "mixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RM.Mixer, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 16, NBT_PARALLEL_DURATION, T), "hPP","SRM","wPP",'M', OP.casingMachine.dat(mat), 'S', OP.stick.dat(mat), 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateTriple.dat(MT.StainlessSteel));
        mat = MT.DATA.Kinetic_T[4]; MTEx.gt6xMTEReg.add("Vertical Mixer ("                         +mat.getLocal()+")", "Basic Machines"                      , 110, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   7.0F, NBT_RESISTANCE,   7.0F, NBT_INPUT,   2048, NBT_TEXTURE, "mixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RM.Mixer, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 32, NBT_PARALLEL_DURATION, T), "hPP","SRM","wPP",'M', OP.casingMachine.dat(mat), 'S', OP.stick.dat(mat), 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateQuadruple.dat(MT.StainlessSteel));

        CR.shapeless(MTEx.gt6xMTEReg.getItem(107),new Object[]{MTEx.gt6Registry.getItem(20181), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(108),new Object[]{MTEx.gt6Registry.getItem(20182), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(109),new Object[]{MTEx.gt6Registry.getItem(20183), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(110),new Object[]{MTEx.gt6Registry.getItem(20184), OreDictToolNames.wrench});

        CR.shapeless(MTEx.gt6Registry.getItem(20181),new Object[]{MTEx.gt6xMTEReg.getItem(107), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20182),new Object[]{MTEx.gt6xMTEReg.getItem(108), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20183),new Object[]{MTEx.gt6xMTEReg.getItem(109), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20184),new Object[]{MTEx.gt6xMTEReg.getItem(110), OreDictToolNames.wrench});

        /* EU */
        aClass = MultiTileEntityBasicMachineElectric.class;
        mat = MT.DATA.Electric_T[1]; MTEx.gt6xMTEReg.add("Vertical Electric Mixer ("                          +VN[1]+")", "Basic Machines"                      , 111, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   32, NBT_TEXTURE, "electricmixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Mixer, NBT_EFFICIENCY, 5000, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 4, NBT_PARALLEL_DURATION, T),"hPP","SRM","wPP", 'M', OP.casingMachine.dat(mat), 'S', IL.MOTORS[1], 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plate.dat(MT.StainlessSteel));
        mat = MT.DATA.Electric_T[2]; MTEx.gt6xMTEReg.add("Vertical Electric Mixer ("                          +VN[2]+")", "Basic Machines"                      , 112, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   128, NBT_TEXTURE, "electricmixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Mixer, NBT_EFFICIENCY, 5000, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 8, NBT_PARALLEL_DURATION, T),"hPP","SRM","wPP", 'M', OP.casingMachine.dat(mat), 'S', IL.MOTORS[2], 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateDouble.dat(MT.StainlessSteel));
        mat = MT.DATA.Electric_T[3]; MTEx.gt6xMTEReg.add("Vertical Electric Mixer ("                          +VN[3]+")", "Basic Machines"                      , 113, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   512, NBT_TEXTURE, "electricmixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Mixer, NBT_EFFICIENCY, 5000, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 16, NBT_PARALLEL_DURATION, T),"hPP","SRM","wPP", 'M', OP.casingMachine.dat(mat), 'S', IL.MOTORS[3], 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateTriple.dat(MT.StainlessSteel));
        mat = MT.DATA.Electric_T[4]; MTEx.gt6xMTEReg.add("Vertical Electric Mixer ("                          +VN[4]+")", "Basic Machines"                      , 114, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   2048, NBT_TEXTURE, "electricmixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Mixer, NBT_EFFICIENCY, 5000, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 32, NBT_PARALLEL_DURATION, T),"hPP","SRM","wPP", 'M', OP.casingMachine.dat(mat), 'S', IL.MOTORS[4], 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateQuadruple.dat(MT.StainlessSteel));
        mat = MT.DATA.Electric_T[5]; MTEx.gt6xMTEReg.add("Vertical Electric Mixer ("                          +VN[5]+")", "Basic Machines"                      , 115, 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock     , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,   8192, NBT_TEXTURE, "electricmixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Mixer, NBT_EFFICIENCY, 5000, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_R, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 64, NBT_PARALLEL_DURATION, T),"hPP","SRM","wPP", 'M', OP.casingMachine.dat(mat), 'S', IL.MOTORS[5], 'R', OP.rotor.dat(MT.StainlessSteel), 'P', OP.plateQuadruple.dat(MT.StainlessSteel));


        CR.shapeless(MTEx.gt6xMTEReg.getItem(111),new Object[]{MTEx.gt6Registry.getItem(20351), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(112),new Object[]{MTEx.gt6Registry.getItem(20352), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(113),new Object[]{MTEx.gt6Registry.getItem(20353), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(114),new Object[]{MTEx.gt6Registry.getItem(20354), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6xMTEReg.getItem(115),new Object[]{MTEx.gt6Registry.getItem(20355), OreDictToolNames.wrench});

        CR.shapeless(MTEx.gt6Registry.getItem(20351),new Object[]{MTEx.gt6Registry.getItem(111), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20352),new Object[]{MTEx.gt6Registry.getItem(112), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20353),new Object[]{MTEx.gt6Registry.getItem(113), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20354),new Object[]{MTEx.gt6Registry.getItem(114), OreDictToolNames.wrench});
        CR.shapeless(MTEx.gt6Registry.getItem(20355),new Object[]{MTEx.gt6Registry.getItem(115), OreDictToolNames.wrench});
    }

    @Override
    public void postInit() {}
}
