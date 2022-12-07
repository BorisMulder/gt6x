package org.altadoon.gt6x.features.metallurgy;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialCondition;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.oredict.configurations.OreDictConfigurationComponent;
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

import java.util.*;

import static gregapi.data.CS.*;
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
    public void afterPreInit() {
        changeAlloySmeltingRecipes();
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
    public void afterPostInit() {
        changeRecipes();
    }

    private void changeMaterialProperties() {
        MT.PigIron.setPulver(MT.PigIron, U).setSmelting(MT.PigIron, U);
        MT.OREMATS.Chromite.setSmelting(MT.OREMATS.Chromite, U);
        MT.OREMATS.Garnierite.setSmelting(MT.OREMATS.Garnierite, U);
        MT.OREMATS.Cobaltite.setSmelting(MT.OREMATS.Cobaltite, U);
        MT.MnO2.setSmelting(MT.MnO2, U);

        ListIterator<OreDictMaterial> it = MT.OREMATS.Cobaltite.mByProducts.listIterator();
        while (it.hasNext()) {
            OreDictMaterial byproduct = it.next();
            if (byproduct.mID == MT.Co.mID) {
                it.remove();
                it.set(MT.OREMATS.Cobaltite);
            }
        }
    }

    private void addRecipeMaps() {
        blastFurnace = new Recipe.RecipeMap(null, "gt6x.recipe.blastfurnace", "Blast Furnace", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace",6 , 3, 1, 3, 3, 0, 1, 1, "", 1, "", true, true, true, true, false, true, T);
        sintering = new Recipe.RecipeMap(null, "gt6x.recipe.sintering", "Sintering", null, 0, 1, RES_PATH_GUI+"machines/Sintering",3 , 1, 1, 0, 0, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
    }

    private void removeAlloySmeltingRecipe(OreDictMaterial mat) {
        mat.remove(TD.Processing.CRUCIBLE_ALLOY);

        for (IOreDictConfigurationComponent comp : mat.mAlloyCreationRecipes) {
            for (OreDictMaterialStack tMaterial : comp.getUndividedComponents()) {
                tMaterial.mMaterial.mAlloyComponentReferences.remove(mat);
            }
        }
        mat.mAlloyCreationRecipes.clear();
    }

    private void changeAlloySmeltingRecipes() {
        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.Si, MT.Fe, MT.Steel, MT.MeteoricSteel, MT.StainlessSteel }) {
            removeAlloySmeltingRecipe(mat);
        }

        MTx.Bloom.addAlloyingRecipe(new OreDictConfigurationComponent( 2, OM.stack(MT.Fe2O3                         , 5*U), OM.stack(MT.C, 1*U), OM.stack(MT.CaCO3, U)));
        MTx.Bloom.addAlloyingRecipe(new OreDictConfigurationComponent( 6, OM.stack(MT.OREMATS.Magnetite             ,14*U), OM.stack(MT.C, 3*U), OM.stack(MT.CaCO3, U)));
        MTx.Bloom.addAlloyingRecipe(new OreDictConfigurationComponent( 6, OM.stack(MT.OREMATS.BasalticMineralSand   ,14*U), OM.stack(MT.C, 3*U), OM.stack(MT.CaCO3, U)));
        MTx.Bloom.addAlloyingRecipe(new OreDictConfigurationComponent( 6, OM.stack(MT.OREMATS.GraniticMineralSand   ,14*U), OM.stack(MT.C, 3*U), OM.stack(MT.CaCO3, U)));
        MTx.Bloom.addAlloyingRecipe(new OreDictConfigurationComponent( 6, OM.stack(MT.OREMATS.Ferrovanadium         ,28*U), OM.stack(MT.C, 3*U), OM.stack(MT.CaCO3, U)));

        // pig iron C content: around 4% (weight), Fe: 96% * 12u / 56u = 20,5 ==> 2 Fe20C + Fe2O3 -> 42 Fe + CO + CO2
        MT.Fe.addAlloyingRecipe(new OreDictConfigurationComponent(21, OM.stack(MT.PigIron,20*U), OM.stack(MT.Fe2O3, 5*U2)));

        MT.Steel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.PigIron, U), OM.stack(MT.Air, U)));
        // TODO add steel recipe from DRI (MT.Fe) with O2 to EAF

        //MT.Steel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.Fe, U), OM.stack(MT.C, U72), OM.stack(MT.Air, U)));
        //MT.MeteoricSteel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.MeteoricIron, U), OM.stack(MT.C, U72), OM.stack(MT.Air, U)));
        // TODO add these two recipes to EAF (maybe use CO instead of O?)

        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,16*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Cr, 4*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,24*U), OM.stack(MT.Nichrome, 5*U), OM.stack(MT.Cr, 3*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,14*U), OM.stack(MTx.FeCr2, 6*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));

        MT.Invar.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.PigIron,2*U), OM.stack(MT.Ni, U)));
        MT.TinAlloy.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.PigIron, U), OM.stack(MT.Sn, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.PigIron, U), OM.stack(MT.Al, U), OM.stack(MT.Cr, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(6, OM.stack(MT.PigIron, U), OM.stack(MT.Al, 2*U), OM.stack(MTx.FeCr2, 3*U)));
        MT.Angmallen.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.PigIron, U), OM.stack(MT.Au, U)));
        // TODO add these two recipes to EAF (with and without DRI)

        //TODO Iron Carbide Process (
        //TODO iron/wrought iron + CO -> Cementite or Steel (roasting oven?)
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
        for (ItemStack coal : new ItemStack[]{OP.dust.mat(MT.Charcoal, 1), OP.dust.mat(MT.CoalCoke, 1), OP.dust.mat(MT.LigniteCoke, 3), OP.dust.mat(MT.C, 1), OP.gem.mat(MT.Charcoal, 1), OP.gem.mat(MT.CoalCoke, 1), OP.gem.mat(MT.LigniteCoke, 3)}) {
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.Fe2O3, 5), ST.mul(3, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(6 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Magnetite, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(3000)), FL.array(MT.PigIron.liquid(3 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(3000)), FL.array(MT.PigIron.liquid(3 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.YellowLimonite, 8), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.BrownLimonite, 8), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.Ni.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.Sn.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.OREMATS.Chromite, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(3000)), FL.array(MTx.FeCr2.liquid(3 * U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MTx.PbO, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.Pb.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(2 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MTx.ZnO, 2), ST.mul(1, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(2000)), FL.array(MTx.ZnBlastFurnaceGas.gas(4 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.MnO2, 2), ST.mul(2, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(2000)), FL.array(MT.Mn.liquid(2 * U, false), MTx.BlastFurnaceGas.gas(4 * U, false)), 128, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MTx.Co3O4, 7), ST.mul(4, coal), OP.dust.mat(MT.CaCO3, 1)}, new ItemStack[]{OP.gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(3000)), FL.array(MT.Co.liquid(3 * U, false), MTx.BlastFurnaceGas.gas(8 * U, false)), 192, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.SiO2, 3), ST.mul(2, coal)}, ZL_IS, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.Si.liquid(U, false), MTx.BlastFurnaceGas.gas(4 * U, false)), 64, 8, 0);
            //TODO sintered pellets?
        }

        /* TODO check
        RM.Shredder.addRecipe1(false, 16, 64, OP.nugget.mat(MTx.Slag, 1), OP.dustTiny.mat(MT.OREMATS.Wollastonite, 1));
        RM.Shredder.addRecipe1(false, 16, 64, OP.chunk.mat(MTx.Slag, 1), OP.dustSmall.mat(MT.OREMATS.Wollastonite, 1));
        RM.Shredder.addRecipe1(false, 16, 64, OP.ingot.mat(MTx.Slag, 1), OP.dust.mat(MT.OREMATS.Wollastonite, 1));
        RM.Mortar.addRecipe1(false, 16, 64, OP.nugget.mat(MTx.Slag, 1), OP.dustTiny.mat(MT.OREMATS.Wollastonite, 1));
        RM.Mortar.addRecipe1(false, 16, 64, OP.chunk.mat(MTx.Slag, 1), OP.dustSmall.mat(MT.OREMATS.Wollastonite, 1));
        RM.Mortar.addRecipe1(false, 16, 64, OP.ingot.mat(MTx.Slag, 1), OP.dust.mat(MT.OREMATS.Wollastonite, 1));
         */

        // misc stuff
        RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(U, true)), FL.array(MT.Zn.liquid(U2, false), MTx.BlastFurnaceGas.gas(U2, false)));
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

        // mixing from/to molten ferrochrome and pig iron
        for (String tIron : new String[] {"molten.iron", "molten.wroughtiron", "molten.pigiron", "molten.meteoriciron"}) {
            RM.Mixer.addRecipe1(T, 16, 2 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.gold",    1)), FL.make_("molten.angmallen", 2), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 2 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.tin",    1)), FL.make_("molten.tinalloy", 2), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 3 , ST.tag(2), FL.array(FL.make_(tIron, 2 ), FL.make_("molten.nickel",    1)), FL.make_("molten.invar", 3), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 3 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.chromium",    2)), FL.make_("molten.ferrochrome", 3), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 3 , ST.tag(3), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.chromium", 1), FL.make_("molten.aluminium" , 1)), FL.make("molten.kanthal", 3), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 6 , ST.tag(3), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.ferrochrome", 3), FL.make_("molten.aluminium" , 2)), FL.make("molten.kanthal", 6), ZL_IS);
        }
    }

    private void changeRecipes() {
        Set<ItemStack> toDisableRoasting = new HashSet<>();
        for (OreDictMaterial mat : new OreDictMaterial[] {
                MT.OREMATS.Cobaltite, MT.OREMATS.Cooperite, MT.OREMATS.Galena, MT.OREMATS.Kesterite, MT.OREMATS.Molybdenite, MT.OREMATS.Pentlandite, MT.OREMATS.Sphalerite, MT.OREMATS.Stannite, MT.OREMATS.Cinnabar
        }) {
            toDisableRoasting.add(OP.dust.mat(mat, 1));
        }

        // make Stainless using crucible or EAF as you need to inject oxygen (it is a kind of steel after all)
        for (Recipe r : RM.Roasting.mRecipeList) if (r.mInputs.length >= 1) {
            for (ItemStack stack : toDisableRoasting) {
                if (r.mInputs[0].isItemEqual(stack)) {
                    r.mEnabled = false;
                    break;
                }
            }
        }

        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mFluidOutputs.length >= 1 && r.mFluidOutputs[0].getFluid().getName().equals("molten.stainlesssteel"))
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
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cinnabar), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.HgO, U));
        }

        final long[] tChances = new long[] {8000, 8000, 8000};

        for (String tAir : FluidsGT.AIR) if (FL.exists(tAir)) {
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Sphalerite), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Molybdenite), FL.make(tAir, 6000), MT.SO2.gas(6 * U3, false), OM.dust(MTx.MoO3, 4*U3));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Pentlandite), FL.make(tAir, 4000), MT.SO2.gas(24 * U17, false), OM.dust(MT.OREMATS.Garnierite, 9 * U17));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cobaltite), FL.make(tAir, 3000), MT.SO2.gas(3 * U3, false), OM.dust(MTx.Co3O4, 7*U9), OM.dust(MT.As, U3));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Galena), FL.make(tAir, 2000), MT.SO2.gas(6 * U8, false), OM.dust(MT.Ag, 3 * U8), OM.dust(MTx.PbO, 3 * U8));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cooperite), FL.make(tAir, 2000), MT.SO2.gas(3 * U6, false), OM.dust(MT.PlatinumGroupSludge, 4 * U6), OM.dust(MT.OREMATS.Garnierite, U6));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Stannite), FL.make(tAir, 5000), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MT.OREMATS.Cassiterite, U8), OM.dust(MT.Fe2O3, 5 * U16));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Kesterite), FL.make(tAir, 4000), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MTx.ZnO, U8), OM.dust(MT.OREMATS.Cassiterite, U8));
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cinnabar), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.HgO, U));
        }

        Recipe r = RM.Centrifuge.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, OM.dust(MT.OREMATS.Cinnabar)); if (r != null) r.mEnabled = false;
        //TODO decompose HgO using Thermolysis for full value


    }
}
