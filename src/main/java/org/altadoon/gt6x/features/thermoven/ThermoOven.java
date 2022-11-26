package org.altadoon.gt6x.features.thermoven;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.Recipe;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.common.MTEx;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
/**
 * @author Francisco Lobaton (Roku)
 *
 * Implementation of the thermolisis oven and its recipe map
 */

public class ThermoOven extends GT6XFeature {
    public static final String FEATURE_NAME = "Thermolisis oven";

    private static final String FEATURE_SET ="featureSet";

    public enum OvenFeatureSet {
        Simple,

        off,
    }

    private OvenFeatureSet ovenFeatures = OvenFeatureSet.off;

    @Override
    public void configure(Config cfg) {
        String configString = cfg.cfg.get(FEATURE_NAME, FEATURE_SET, OvenFeatureSet.Simple.name(), null, new String[]{OvenFeatureSet.Simple.name()}).getString();
        ovenFeatures = OvenFeatureSet.valueOf(configString);
    }

    @Override
    public String name() { return FEATURE_NAME;}

    @Override
    public void preInit() {
        addThermolisisRecipies();
    }

    @Override
    public void init() {
        addThermoOven();
    }
    @Override
    public void postInit(){



    }

    @Override
    public void postPostInit() {
        //test
    }
    public Recipe.RecipeMap thermolisis = null;
    public void addThermolisisRecipies(){
        thermolisis = new Recipe.RecipeMap(null, "gt6x.recipe.thermolisis","Thermolisis",null,0,1,RES_PATH_GUI+"machines/thermolisis",2,6,0,2,6,0,1,1,"",1,"", true, true, true, true, false, true, true);
    }
    private void addThermoOven(){
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial aMat;
        MultiTileEntityRegistry aRegistry = MultiTileEntityRegistry.getRegistry("gt.multitileentity");

        aMat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Thermolisis Oven ("+aMat.getLocal()+")","Basic Machines",51,20001, aClass, aMat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,aMat,NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_INPUT, 32, NBT_TEXTURE, "thermolisisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU,NBT_RECIPEMAP,thermolisis,NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(aMat),'M', OP.pipeMedium.dat(aMat),'C', OP.casingMachine.dat(aMat),'S',OP.pipeSmall.dat(aMat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
        aMat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Thermolisis Oven ("+aMat.getLocal()+")","Basic Machines",52,20001, aClass, aMat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,aMat,NBT_HARDNESS, 4.0F, NBT_RESISTANCE, 4.0F, NBT_INPUT, 128, NBT_TEXTURE, "thermolisisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU,NBT_RECIPEMAP,thermolisis,NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(aMat),'M', OP.pipeMedium.dat(aMat),'C', OP.casingMachine.dat(aMat),'S',OP.pipeSmall.dat(aMat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
        aMat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Thermolisis Oven ("+aMat.getLocal()+")","Basic Machines",53,20001, aClass, aMat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,aMat,NBT_HARDNESS, 9.0F, NBT_RESISTANCE, 9.0F, NBT_INPUT, 512, NBT_TEXTURE, "thermolisisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU,NBT_RECIPEMAP,thermolisis,NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(aMat),'M', OP.pipeMedium.dat(aMat),'C', OP.casingMachine.dat(aMat),'S',OP.pipeSmall.dat(aMat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
        aMat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Thermolisis Oven ("+aMat.getLocal()+")","Basic Machines",54,20001, aClass, aMat.mToolQuality,16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL,aMat,NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "thermolisisoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU,NBT_RECIPEMAP,thermolisis,NBT_INV_SIDE_IN, SBIT_B|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_B|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BACK, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D),"wQh","MCS","FPF",'Q', OP.pipeQuadruple.dat(aMat),'M', OP.pipeMedium.dat(aMat),'C', OP.casingMachine.dat(aMat),'S',OP.pipeSmall.dat(aMat),'F', Blocks.brick_block,'P', OP.plateDouble.dat(ANY.Cu));
    }
}
