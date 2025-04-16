package org.altadoon.gt6x.features.verticalmixers;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.tileentity.machines.MultiTileEntityBasicMachineElectric;
import gregapi.util.CR;
import gregapi.util.UT;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;

public class VerticalMixers extends GT6XFeature {
    public static final String FEATURE_NAME = "VerticalMixers";
    private boolean enableElectricMixer, enableRotationalMixer;
    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void configure(Config config) {
        enableElectricMixer = config.cfg.getBoolean("enableElectricMixerVertical", FEATURE_NAME,true,"enable electric version of vertical mixers.");
        enableRotationalMixer = config.cfg.getBoolean("enableRotationalMixerVertical", FEATURE_NAME,true,"enable rotational version of vertical mixers.");
    }

    @Override
    public void preInit() {}

    private static final int MIXER_ID = 20180;
    private static final int MIXER_ELECTRIC_ID = 20350;
    private static final OreDictPrefix[] plates = { OP.plate, OP.plate, OP.plateDouble, OP.plateTriple, OP.plateQuadruple, OP.plateQuadruple };

    @Override
    public void init() {
        Class<? extends TileEntity> aClass; OreDictMaterial mat;
        /* RU */
        if(enableRotationalMixer){
            aClass = MultiTileEntityBasicMachine.class;
            for (int tier = 1; tier < 5; tier++) {
                mat = MT.DATA.Kinetic_T[tier]; MTEx.gt6xMTEReg.add("Mixer (" + mat.getLocal()+", Vertical)", "Basic Machines", MTEx.IDs.VertMixer[tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_KINETIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_KINETIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "mixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RM.Mixer, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2 << tier, NBT_PARALLEL_DURATION, true), "hPP","SRM","wPP",'M', OP.casingMachine.dat(mat), 'S', OP.stick.dat(mat), 'R', OP.rotor.dat(MT.StainlessSteel), 'P', plates[tier].dat(MT.StainlessSteel));
                CR.shapeless(MTEx.gt6xMTEReg.getItem(MTEx.IDs.VertMixer[tier].get()),new Object[]{MTEx.gt6MTEReg.getItem(MIXER_ID + tier), OreDictToolNames.wrench});
                CR.shapeless(MTEx.gt6MTEReg.getItem(MIXER_ID + tier),new Object[]{MTEx.gt6xMTEReg.getItem(MTEx.IDs.VertMixer[tier].get()), OreDictToolNames.wrench});
            }
        }
        /* EU */
        if(enableElectricMixer){
            aClass = MultiTileEntityBasicMachineElectric.class;
            for (int tier = 1; tier < 6; tier++) {
                mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Electric Mixer (" + VN[tier] + ", Vertical)", "Basic Machines", MTEx.IDs.VertMixerElectric[tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "electricmixer_vertical", NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RM.Mixer, NBT_EFFICIENCY, 5000, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2 << tier, NBT_PARALLEL_DURATION, true),"hPP","SRM","wPP", 'M', OP.casingMachine.dat(mat), 'S', IL.MOTORS[tier], 'R', OP.rotor.dat(MT.StainlessSteel), 'P', plates[tier].dat(MT.StainlessSteel));
                CR.shapeless(MTEx.gt6xMTEReg.getItem(MTEx.IDs.VertMixerElectric[tier].get()),new Object[]{MTEx.gt6MTEReg.getItem(MIXER_ELECTRIC_ID + tier), OreDictToolNames.wrench});
                CR.shapeless(MTEx.gt6MTEReg.getItem(MIXER_ELECTRIC_ID + tier),new Object[]{MTEx.gt6xMTEReg.getItem(MTEx.IDs.VertMixerElectric[tier].get()), OreDictToolNames.wrench});
            }
        }
    }

    @Override
    public void postInit() {}
}
