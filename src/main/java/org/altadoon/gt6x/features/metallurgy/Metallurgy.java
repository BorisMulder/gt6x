package org.altadoon.gt6x.features.metallurgy;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialCondition;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerPrefixForging;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;
import scala.actors.threadpool.Arrays;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static gregapi.data.CS.*;
import static gregapi.data.CS.T;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.oredict.OreDictMaterialCondition.fullforge;

public class Metallurgy extends GT6XFeature {
    public Recipe.RecipeMap blastFurnace = null;
    public Recipe.RecipeMap sintering = null;

    @Override
    public String name() {
        return "Steelmaking";
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {
        changeMaterialProperties();
        addRecipeMaps();
    }

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void postInit() {
        addRecipes();
    }

    @Override
    public void postPostInit() {
        changeRecipes();
    }

    private void changeMaterialProperties() {
        MT.PigIron.setPulver(MT.PigIron, U).setSmelting(MT.PigIron, U);
        MT.OREMATS.Chromite.setSmelting(MT.OREMATS.Chromite, U);
        MT.OREMATS.Garnierite.setSmelting(MT.OREMATS.Garnierite, U);
        MT.OREMATS.Cobaltite.setSmelting(MT.OREMATS.Cobaltite, U);
        MT.MnO2.setSmelting(MT.MnO2, U);
    }

    private void addRecipeMaps() {
        blastFurnace = new Recipe.RecipeMap(null, "gt6x.recipe.blastfurnace", "Blast Furnace", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace",6 , 3, 1, 3, 3, 0, 1, 1, "", 1, "", true, true, true, true, false, true, T);
        sintering = new Recipe.RecipeMap(null, "gt6x.recipe.sintering", "Sintering", null, 0, 1, RES_PATH_GUI+"machines/Sintering",3 , 1, 1, 0, 0, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
    }

    private void addMTEs() {
        OreDictMaterial aMat;
        aMat = MT.WroughtIron; MTEx.gt6xMTEReg.add("Blast Furnace Part", "Multiblock Machines", 60, 17101, MultiTileEntityMultiBlockPart.class, aMat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', OP.plate.dat(MT.WroughtIron), 'F', MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(18000));
        aMat = MT.WroughtIron; MTEx.gt6xMTEReg.add("Blast Furnace", "Multiblock Machines", 61, 17101, MultiTileEntityBlastFurnace.class, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_INPUT, 32, NBT_INPUT_MIN, 8, NBT_INPUT_MAX, 64, NBT_TEXTURE, "blastfurnace", NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, blastFurnace, NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', OP.plateCurved.dat(MT.WroughtIron), 'R', OP.stickLong.dat(MT.WroughtIron), 'F', MTEx.gt6xMTEReg.getItem(60));

        OM.data(MTEx.gt6xMTEReg.getItem(60), new OreDictItemData(MT.WroughtIron, U*2, MT.Clay, U*4, MT.Brick, U*4));

        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        aMat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 62, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateDouble   .dat(ANY.Cu), 'P', OP.plate.dat(MT.Ceramic), 'B', Blocks.brick_block, 'U', MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(1005));
        aMat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 63, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateTriple   .dat(ANY.Cu), 'P', OP.plate.dat(MT.Ta     ), 'B', Blocks.brick_block, 'U', MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(1036));
        aMat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 64, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuadruple.dat(ANY.Cu), 'P', OP.plate.dat(MT.W      ), 'B', Blocks.brick_block, 'U', MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(1024));
        aMat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 65, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuintuple.dat(ANY.Cu), 'P', OP.plate.dat(MT.Ta4HfC5), 'B', Blocks.brick_block, 'U', MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(1043));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ICondition<OreDictMaterial>
            lowHeatSintering = new ICondition.And(ANTIMATTER.NOT, OreDictMaterialCondition.meltmax(3300), fullforge()),
            highHeatSintering = new ICondition.And(ANTIMATTER.NOT, OreDictMaterialCondition.meltmin(3300), fullforge());


    private void addRecipes() {
        for (ItemStack coal : new ItemStack[]{OP.dust.mat(MT.Charcoal, 1), OP.dust.mat(MT.CoalCoke, 1), OP.gem.mat(MT.Charcoal, 1), OP.gem.mat(MT.CoalCoke, 1)}) {
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.Fe2O3, 5), ST.mul(3, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(6 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Magnetite, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.PigIron.liquid(3 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.PigIron.liquid(3 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.YellowLimonite, 8), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.BrownLimonite, 8), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Ni.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Sn.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Chromite, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MTx.FeCr2.liquid(3 * U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MTx.PbO, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Pb.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MTx.ZnO, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Zn.gas(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.MnO2, 2), ST.mul(2, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Mn.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(4 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MTx.Co3O4, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Co.liquid(3 * U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);

            //TODO sintered pellets?
        }

        RM.Shredder.addRecipe1(false, 16, 64, OP.nugget.mat(MTx.Slag, 1), OP.dustTiny.mat(MT.OREMATS.Wollastonite, 1));
        RM.Shredder.addRecipe1(false, 16, 64, OP.chunk.mat(MTx.Slag, 1), OP.dustSmall.mat(MT.OREMATS.Wollastonite, 1));
        RM.Shredder.addRecipe1(false, 16, 64, OP.ingot.mat(MTx.Slag, 1), OP.dust.mat(MT.OREMATS.Wollastonite, 1));
        RM.Mortar.addRecipe1(false, 16, 64, OP.nugget.mat(MTx.Slag, 1), OP.dustTiny.mat(MT.OREMATS.Wollastonite, 1));
        RM.Mortar.addRecipe1(false, 16, 64, OP.chunk.mat(MTx.Slag, 1), OP.dustSmall.mat(MT.OREMATS.Wollastonite, 1));
        RM.Mortar.addRecipe1(false, 16, 64, OP.ingot.mat(MTx.Slag, 1), OP.dust.mat(MT.OREMATS.Wollastonite, 1));

        // misc stuff
        RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), FL.array(MT.Zn.gas(U, true)), FL.array(MT.Zn.liquid(U, true)));
        RM.Mixer.addRecipe1(true, 16, 160, OP.dust.mat(MTx.MoO3, 4*U), FL.array(MT.H.gas(6*U, true)), FL.array(MT.H2O.liquid(9*U, false)), OP.dust.mat(MT.Mo, U));

        // Sintering dusts into chunks
        sintering.add(new RecipeMapHandlerPrefixForging(OP.dust, 1, NF, 16, 0, 0, NF, OP.chunk, 4, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixForging(OP.dustSmall, 1, NF, 16, 0, 0, NF, OP.chunk, 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixForging(OP.dustTiny, 9, NF, 16, 0, 0, NF, OP.chunk, 4, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixForging(OP.dust, 1, NF, 96, 0, 0, NF, OP.chunk, 4, NI, NI, true, false, false, highHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixForging(OP.dustSmall, 1, NF, 96, 0, 0, NF, OP.chunk, 1, NI, NI, true, false, false, highHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixForging(OP.dustTiny, 9, NF, 96, 0, 0, NF, OP.chunk, 4, NI, NI, true, false, false, highHeatSintering));

        // misc sintering
        for (ItemStack coal : new ItemStack[]{OP.dust.mat(MT.Charcoal, 1), OP.dust.mat(MT.CoalCoke, 1), OP.dust.mat(MT.Graphite, 1), OP.dust.mat(MT.C, 1) }) {
            sintering.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.W, 1), ST.copy(coal)}, new ItemStack[]{OP.ingot.mat(MT.TungstenCarbide, 2)}, null, null, ZL_FS, ZL_FS, 256, 96, 0);
            sintering.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.Ta, 4), OP.dust.mat(MT.Hf, 1), ST.mul(5, coal)}, new ItemStack[]{OP.ingot.mat(MT.Ta4HfC5, 10)}, null, null, ZL_FS, ZL_FS, 512, 96, 0);
        }


        //TODO pig iron + iron oxide -> wrought iron
        //TODO remove wrought iron + air -> steel
        //TODO add pig iron + air -> steel
        //TODO ore + coke + flux in crucible -> bloom
        //TODO iron/wrought iron + CO -> Cementite or Steel (roasting oven?)
        //TODO crucible max temp = 100% instead of 125%
        //TODO roasting -> oxides for Zn, Fe (exists?), Co, Ni, Sn, Pb, Mn
    }

    private void changeRecipes() {
        // roasting
        final Set<String> disabled = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{
                MT.OREMATS.Cobaltite.mNameInternal, MT.OREMATS.Cooperite.mNameInternal, MT.OREMATS.Galena.mNameInternal, MT.OREMATS.Kesterite.mNameInternal, MT.OREMATS.Molybdenite.mNameInternal, MT.OREMATS.Pentlandite.mNameInternal, MT.OREMATS.Sphalerite.mNameInternal, MT.OREMATS.Stannite.mNameInternal,
        })));

        for (Recipe r : RM.Roasting.mRecipeList) {
            if (r.mInputs.length == 1 && disabled.contains(r.mInputs[0].getUnlocalizedName()))
                r.mEnabled = false;
        }

        for (String tOxygen : FluidsGT.OXYGEN) if (FL.exists(tOxygen)) {
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Molybdenite), FL.make(tOxygen, 2334), MT.SO2.gas(6 * U3, false), OM.dust(MTx.MoO3, 4*U3));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Pentlandite), FL.make(tOxygen, 1471), MT.SO2.gas(24 * U17, false), OM.dust(MT.OREMATS.Garnierite, 9 * U17));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cobaltite), FL.make(tOxygen, 1111), MT.SO2.gas(3 * U3, false), OM.dust(MTx.Co3O4, 7*U9), OM.dust(MT.As, U3));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Galena), FL.make(tOxygen, 875), MT.SO2.gas(6 * U8, false), OM.dust(MT.Ag, 3 * U8), OM.dust(MTx.PbO, 3 * U8));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cooperite), FL.make(tOxygen, 500), MT.SO2.gas(3 * U6, false), OM.dust(MT.PlatinumGroupSludge, 4 * U6), OM.dust(MT.OREMATS.Garnierite, U6));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stannite), FL.make(tOxygen, 1313), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MT.OREMATS.Cassiterite, U8), OM.dust(MT.Fe2O3, 5 * U16));
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Kesterite), FL.make(tOxygen, 1250), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MTx.ZnO, U8), OM.dust(MT.OREMATS.Cassiterite, U8));
        }
    }
}
