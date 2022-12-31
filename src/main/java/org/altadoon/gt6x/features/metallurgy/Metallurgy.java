package org.altadoon.gt6x.features.metallurgy;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.*;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.oredict.configurations.OreDictConfigurationComponent;
import gregapi.recipes.Recipe;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.*;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.data.TD.Processing.EXTRUDER;
import static gregapi.oredict.OreDictMaterialCondition.fullforge;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Metallurgy extends GT6XFeature {
    public static final String FEATURE_NAME = "Metallurgy";

    public Recipe.RecipeMap blastFurnace = null;
    public Recipe.RecipeMap sintering = null;
    public Recipe.RecipeMap directReduction = null;
    public OreDictPrefix sinter = null;

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {
        createPrefixes();
        changeMaterialProperties();
        changeByProducts();
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
    public void beforePostInit() {
        addOverrideRecipes();
    }

    @Override
    public void postInit() {
        addRecipes();
    }

    @Override
    public void afterPostInit() {
        changeRecipes();
    }

    private void createPrefixes() {
        sinter = OreDictPrefix.createPrefix("sinter")
            .setCategoryName("Sinters")
            .setLocalItemName("", " Sinter")
            .aspects(gregapi.data.TC.FABRICO, 1); // Thaumcraft Aspects related to this Prefix.
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.sinter", sinter,
            MT.Empty, MT.Fe2O3, MT.OREMATS.Magnetite, MT.OREMATS.Garnierite, MT.OREMATS.Cassiterite, MT.OREMATS.Chromite, MTx.PbO, MTx.ZnO, MT.MnO2, MTx.Co3O4, MTx.CoO, MT.SiO2, MTx.Sb2O3
        );
    }

    private void changeMaterialProperties() {
        MT.PigIron.setPulver(MT.PigIron, U).setSmelting(MT.PigIron, U);
        MT.OREMATS.Garnierite.setSmelting(MT.OREMATS.Garnierite, U).remove(TD.Processing.ELECTROLYSER);
        MT.OREMATS.Cobaltite.setSmelting(MT.OREMATS.Cobaltite, U);
        MT.MnO2.setSmelting(MT.MnO2, U);
        MT.OREMATS.Smithsonite.remove(TD.Processing.ELECTROLYSER);
        MT.OREMATS.Cassiterite.remove(TD.Processing.ELECTROLYSER);
        MT.OREMATS.Wollastonite.remove(TD.Processing.ELECTROLYSER);
        MT.Olivine.remove(TD.Processing.ELECTROLYSER);

        // to make smelting bloom in crucibles easier
        for (OreDictMaterial magnetite : new OreDictMaterial[] { MT.OREMATS.Magnetite, MT.OREMATS.BasalticMineralSand, MT.OREMATS.GraniticMineralSand })
            magnetite.heat(MT.Fe2O3);

        // TODO HSS
    }

    private void changeByProducts() {
        MT.OREMATS.Cobaltite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Co.mID);
        MT.OREMATS.Stibnite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Sb.mID);
        MT.OREMATS.Sphalerite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Zn.mID);
        MT.OREMATS.Garnierite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Ni.mID);
        MT.OREMATS.Galena.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Pb.mID);

        for (OreDictMaterial mat : new OreDictMaterial[] {MT.OREMATS.Sperrylite, MT.OREMATS.Tetrahedrite, MT.Cu, MT.OREMATS.Cooperite, MT.MeteoricIron, MT.Cu, MT.Ga, MT.Ag, MT.Au, MT.Pt, MT.OREMATS.YellowLimonite, MT.OREMATS.Stolzite, MT.OREMATS.Pinalite }) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (byproduct.mID == MT.Ni.mID) {
                    it.set(MT.OREMATS.Garnierite);
                } else if (byproduct.mID == MT.Pb.mID) {
                    it.set(MT.OREMATS.Galena);
                } else if (byproduct.mID == MT.Zn.mID) {
                    it.set(MT.OREMATS.Sphalerite);
                } else if (byproduct.mID == MT.Sb.mID) {
                    it.set(MT.OREMATS.Stibnite);
                } else if (byproduct.mID == MT.Co.mID) {
                    it.set(MT.OREMATS.Cobaltite);
                }
            }
        }
    }

    private void addRecipeMaps() {
        blastFurnace = new Recipe.RecipeMap(null, "gt6x.recipe.blastfurnace", "Blast Furnace", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace",6 , 3, 1, 3, 3, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        sintering = new Recipe.RecipeMap(null, "gt6x.recipe.sintering", "Sintering", null, 0, 1, RES_PATH_GUI+"machines/Sintering",3 , 1, 1, 0, 0, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        directReduction = new Recipe.RecipeMap(null, "gt6x.recipe.directreduction", "Direct Reduction", null, 0, 1, RES_PATH_GUI+"machines/DirectReduction",6 , 3, 1, 3, 3, 1, 1, 1, "", 1, "", true, true, true, true, false, true, true);
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
        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.Si, MT.Fe, MT.Steel, MT.MeteoricSteel, MT.StainlessSteel, MT.TungstenCarbide }) {
            removeAlloySmeltingRecipe(mat);
        }

        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 3, OM.stack(MT.Fe2O3                      , 5*U), OM.stack(MT.C, 3*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.Magnetite          , 14*U), OM.stack(MT.C, 6*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.BasalticMineralSand, 14*U), OM.stack(MT.C, 6*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.GraniticMineralSand, 14*U), OM.stack(MT.C, 6*U)));

        // pig iron C content: around 4% (weight), Fe: 96% * 56u * 12u = 4,5 ==> 2 Fe5C + Fe2O3 + O -> 12 Fe + 2 CO2
        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(12, OM.stack(MT.PigIron,10*U), OM.stack(MT.Fe2O3, 5*U), OM.stack(MT.Air, 12*U)));

        // Bessemer Process
        MT.Steel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.PigIron, U), OM.stack(MT.Air, U)));
        MTx.MeteoricCementite.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.MeteoricIron, U), OM.stack(MT.C, U3)));
        MT.MeteoricSteel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MTx.MeteoricCementite, U), OM.stack(MT.Air, U)));

        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,16*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Cr, 4*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,24*U), OM.stack(MT.Nichrome, 5*U), OM.stack(MT.Cr, 3*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,14*U), OM.stack(MTx.FeCr2, 6*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));

        MT.Invar.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.PigIron,2*U), OM.stack(MT.Ni, U)));
        MT.TinAlloy.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.PigIron, U), OM.stack(MT.Sn, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.PigIron, U), OM.stack(MT.Al, U), OM.stack(MT.Cr, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(6, OM.stack(MT.PigIron, U), OM.stack(MT.Al, 2*U), OM.stack(MTx.FeCr2, 3*U)));
        MT.Angmallen.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.PigIron, U), OM.stack(MT.Au, U)));

        MT.SiC.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.SiO2, 3*U), OM.stack(MT.C, 2*U)));

        // TODO EAF
    }

    private void addMTEs() {
        OreDictMaterial aMat;
        aMat = MT.WroughtIron; MTEx.gt6xMTEReg.add("Blast Furnace Part", "Multiblock Machines", 60, 17101, MultiTileEntityMultiBlockPart.class, aMat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', plate.dat(aMat), 'F', MTEx.gt6Registry.getItem(18000)); // fire bricks
                               MTEx.gt6xMTEReg.add("Blast Furnace"     , "Multiblock Machines", 61, 17101, MultiTileEntityBlastFurnace  .class, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_INPUT, 32, NBT_INPUT_MIN, 8 , NBT_INPUT_MAX, 32  , NBT_TEXTURE, "blastfurnace", NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, blastFurnace   , NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT  , NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', plateCurved.dat(aMat), 'R', stickLong.dat(aMat), 'F', MTEx.gt6xMTEReg.getItem(60));
        aMat = MT.SiC;         MTEx.gt6xMTEReg.add("Shaft Furnace Part", "Multiblock Machines", 66, 17101, MultiTileEntityMultiBlockPart.class, aMat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "ICI", "CWC", "ICI", 'I', ingot.dat(aMat), 'C', dust.dat(ANY.Clay), 'W', OD.container1000water);
                               MTEx.gt6xMTEReg.add("Shaft Furnace"     , "Multiblock Machines", 67, 17101, MultiTileEntityShaftFurnace  .class, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_INPUT, 64, NBT_INPUT_MIN, 64, NBT_INPUT_MAX, 1024, NBT_TEXTURE, "shaftfurnace", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, directReduction,                                     NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true                          ), "IPI", "PSP", "wIh", 'I', plate.dat(aMat), 'P', pipe.dat(MT.StainlessSteel), 'S', MTEx.gt6xMTEReg.getItem(66));
        //TODO fix recipe

        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        aMat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 62, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateDouble   .dat(ANY.Cu), 'P', plate.dat(MT.Ceramic), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1005));
        aMat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 63, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateTriple   .dat(ANY.Cu), 'P', plate.dat(MT.Ta     ), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1036));
        aMat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 64, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateQuadruple.dat(ANY.Cu), 'P', plate.dat(MT.W      ), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1024));
        aMat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 65, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateQuintuple.dat(ANY.Cu), 'P', plate.dat(MT.Ta4HfC5), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1043));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ICondition<OreDictMaterial>
            lowHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmax(3300), fullforge()),
            highHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmin(3300), fullforge());


    private void addRecipes() {
        // slag melts @ 1540
        OreDictMaterial[][] smeltLiquidSlag = new OreDictMaterial[][] {
                {MT.Fe2O3, MT.PigIron}, {MT.OREMATS.Magnetite, MT.PigIron}, {MT.OREMATS.Chromite, MTx.FeCr2},
        };
        OreDictMaterial[][] smeltSolidSlag = new OreDictMaterial[][] {
                {MT.OREMATS.Garnierite, MT.Ni}, {MT.OREMATS.Cassiterite, MT.Sn}, {MTx.PbO, MT.Pb}, {MT.MnO2, MT.Mn}, {MTx.CoO, MT.Co}, {MTx.Co3O4, MT.Co}, {MT.SiO2, MT.Si}, {MTx.Sb2O3, MT.Sb}
        };

        for (ItemStack coal : ST.array(dust.mat(MT.Charcoal, 1), dust.mat(MT.CoalCoke, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.C, 1), gem.mat(MT.Charcoal, 1), gem.mat(MT.CoalCoke, 1), gem.mat(MT.LigniteCoke, 3))) {
            // ore dusts (less efficent)
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.Fe2O3, 5), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            for (OreDictMaterial magnetite : new OreDictMaterial[] { MT.OREMATS.Magnetite, MT.OREMATS.BasalticMineralSand, MT.OREMATS.GraniticMineralSand })
                blastFurnace.addRecipe(true, ST.array(dust.mat(magnetite, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)), ZL_IS, null, null, FL.array(FL.Air.make(22500)), FL.array(MT.PigIron.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            for (OreDictMaterial limonite : new OreDictMaterial[] { MT.OREMATS.YellowLimonite, MT.OREMATS.BrownLimonite })
                blastFurnace.addRecipe(true, ST.array(dust.mat(limonite, 8), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);

            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Ni.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Sn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Chromite, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)), ZL_IS, null, null, FL.array(FL.Air.make(22500)), FL.array(MTx.FeCr2.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.PbO, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Pb.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.ZnO, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make(5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(14*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.MnO2, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Mn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.CoO, 4), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Co.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.Co3O4, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)), ST.array(gem.mat(MTx.Slag, 3)), null, null, FL.array(FL.Air.make(22500)), FL.array(MT.Co.liquid(6*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.SiO2, 6), ST.mul(3, coal)), ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.Sb2O3, 5), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Sb.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);

            // sintered pellets (more efficient)
            for (OreDictMaterial[] mats : smeltLiquidSlag) {
                blastFurnace.addRecipe2(true, 8, 128, sinter.mat(mats[0], 8), ST.mul(1, coal), FL.array(FL.Air.make(5000)), FL.array(mats[1].liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(10*U, false)));
            }
            for (OreDictMaterial[] mats : smeltSolidSlag) {
                blastFurnace.addRecipe2(true, 8, 128, sinter.mat(mats[0], 8), ST.mul(1, coal), FL.array(FL.Air.make(5000)), FL.array(mats[1].liquid(2*U, false), MTx.BlastFurnaceGas.gas(10*U, false)), gem.mat(MTx.Slag, 1));
            }
            blastFurnace.addRecipe2(true, 8, 128, sinter.mat(MTx.ZnO, 8), ST.mul(1, coal), FL.array(FL.Air.make(5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(14*U, false)), gem.mat(MTx.Slag, 1));
        }

        // Pidgeon Process
        blastFurnace.addRecipe2(true, 8, 640, dust.mat(MT.Dolomite, 20), dust.mat(MT.Si, 1), ZL_FS, FL.array(MTx.MgBlastFurnaceGas.gas(14*U, false), MTx.Slag.liquid(5*U, false)), dust.mat(MT.Quicklime, 2));
        blastFurnace.addRecipeX(true, 8, 640, ST.array(dust.mat(MT.MgCO3, 10), dust.mat(MT.CaCO3, 10), dust.mat(MT.Si, 1)), ZL_FS, FL.array(MTx.MgBlastFurnaceGas.gas(14*U, false), MTx.Slag.liquid(5*U, false)), dust.mat(MT.Quicklime, 2));
        blastFurnace.addRecipeX(true, 8, 384, ST.array(dust.mat(MTx.MgO, 4), dust.mat(MT.Quicklime, 2), dust.mat(MT.Si, 1)), ZL_FS, FL.array(MT.Mg.gas(2*U, false), MTx.Slag.liquid(5*U, false)));

        // blast furnace gases
        RM.CryoDistillationTower.addRecipe0(true, 64,  128, FL.array(MTx.BlastFurnaceGas.gas(4*U10, true)), FL.array(MT.N.gas(177*U1000, false), MT.CO.gas(4*20*U1000, T), MT.CO2.gas(4*30*U1000, T), MT.H.gas(4*5*U1000, T), MT.He.gas(U1000, T), MT.Ne.gas(U1000, T), MT.Ar.gas(U1000, T)));
        FM.Burn.addRecipe0(true, -16, 1, MTx.BlastFurnaceGas.gas(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        RM.Distillery.addRecipe1(true, 16, 64, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Zn.gas(U, false), MTx.BlastFurnaceGas.gas(6*U, false)));
        RM.Distillery.addRecipe1(true, 16, 64, ST.tag(0), FL.array(MTx.MgBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Mg.gas(U, false), MT.CO2.gas(6*U, false)));

        // Misc metal processing
        RM.Anvil.addRecipe2(false, 64, 192, scrapGt.mat(MTx.SpongeIron, 9), scrapGt.mat(MTx.SpongeIron, 9), ingot.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.FerrousSlag, 8));

        // Precipitation of Zn, As, Mg gases
        RM.Freezer.addRecipe1(true, 16, 48, ST.tag(0), MT.Zn.gas(U, true), NF, OM.dust(MT.Zn, U)); // from 1180 to 300
        RM.Freezer.addRecipe1(true, 16, 32, ST.tag(0),MT.As.gas(U, true), NF, OM.dust(MT.As, U)); // from 887 to 300
        RM.Freezer.addRecipe1(true, 16, 64, ST.tag(0),MT.Mg.gas(U, true), NF, OM.dust(MT.Mg, U)); // from 1378 to 300
        RM.Bath   .addRecipe1(true, 0, 512, ST.tag(0),MT.Zn.gas(U, true), MT.Zn.liquid(U, false), NI); // from 1180 to 692
        RM.Bath   .addRecipe1(true, 0, 512, ST.tag(0),MT.As.gas(U, true), NF, OM.dust(MT.As, U)); // from 887 to 300
        RM.Bath   .addRecipe1(true, 0, 512, ST.tag(0),MT.Mg.gas(U, true), MT.Mg.liquid(U, false), NI); // from 1378 to 922

        // Acidic leaching for electrowinning
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.ZnO, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.WhiteVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Garnierite, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.CyanVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CoO, 2), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.RedVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.MnO2, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.GrayVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false), MT.O.gas(U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Cassiterite, 1), FL.array(MT.HCl.gas(8*U, true)), FL.array(MT.StannicChloride.liquid(5*U, false), MT.H2O.liquid(6*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Wollastonite, 5), MT.HCl.gas(4*U, true), MTx.CaCl2Solution.liquid(6*U, false), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Fayalite, 7), MT.HCl.gas(8*U, true), MTx.FeCl2Solution.liquid(12*U, false), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.Olivine, 7), FL.array(MT.HCl.gas(8*U, true)), FL.array(MTx.FeCl2Solution.liquid(6*U, false), MTx.MgCl2Solution.liquid(6*U, false)), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.MgO, 2), MT.HCl.gas(4*U, true), MTx.MgCl2Solution.liquid(6*U, false), NI);

        // Misc stuff
        RM.Mixer.addRecipe2(true, 16, 128, ST.tag(3), dust.mat(MT.FeCl2, 6), FL.array(MT.H2O.liquid(6*U, true), MT.O.gas(U, true)), FL.array(MT.HCl.gas(8*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.H2SO4.liquid(7*U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.NH4SO4, 9));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.As2O3, 5), dust.mat(MT.Zn, 3), ZL_FS, FL.array(MT.As.gas(2*U, false)), dust.mat(MTx.ZnO, 3));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.As2O3, 5), dust.mat(MT.Fe, 2), ZL_FS, FL.array(MT.As.gas(2*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Realgar, 2), dust.mat(MT.Fe, 1), ZL_FS, FL.array(MT.As.gas(U, false)), dust.mat(MTx.FeS, 2));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Stibnite, 5), dust.mat(MT.Fe, 3), ZL_FS, FL.array(MT.Sb.liquid(2*U, false)), dust.mat(MTx.FeS, 6));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaOH2, 5), MT.CO2.gas(3*U, true), MT.H2O.liquid(3*U, false), dust.mat(MT.CaCO3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.MgOH2, 5), MT.CO2.gas(6*U, true), MTx.MgHCO3.liquid(11*U, false), NI);
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CaCl2Solution.liquid(6*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.HCl.gas(4*U, false)), dust.mat(MT.CaCO3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaOH2, 5), FL.array(MTx.MgCl2Solution.liquid(6*U, true)), FL.array(MTx.CaCl2Solution.liquid(6*U, false)), dust.mat(MTx.MgOH2, 5));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.PbO, 1), FL.array(MT.H.gas(2 * U, true)), FL.array(MT.H2O.liquid(3 * U, false), MT.Pb.liquid(U, false)));

        //TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MTx.Co3O4, 7), NF, MT.O.gas(U, false), dust.mat(MTx.CoO, 6));
        RM.Drying.addRecipe0(true, 16, 512, FL.array(MT.GreenVitriol.liquid(12*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.SO3.gas(4*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Drying.addRecipe0(true, 16, 256, FL.array(MT.PinkVitriol.liquid(6*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.O.gas(U, false)), dust.mat(MTx.MgO, 2));
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MTx.HgO, 1), ZL_FS, FL.array(MT.Hg.liquid(U2, false), MT.O.gas(U2, false)));
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MT.OREMATS.Smithsonite, 5), ZL_FS, FL.array(MT.CO2.gas(3*U, false)), dust.mat(MTx.ZnO, 1));
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MT.CaCO3, 5), NF, MT.CO2.gas(3*U, false), dust.mat(MT.Quicklime, 2));
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MT.MgCO3, 5), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.MgO, 2));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MTx.CaOH2, 5), NF, MT.DistWater.liquid(3*U, false), dust.mat(MT.Quicklime, 2));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MTx.MgOH2, 5), NF, MT.DistWater.liquid(3*U, false), dust.mat(MTx.MgO, 2));
        RM.Drying.addRecipe0(true, 16, 6000, FL.array(MTx.MgHCO3.liquid(11*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.CO2.gas(3*U, false)), dust.mat(MT.MgCO3, 5));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MT.OREMATS.BrownLimonite, 8), NF, MT.DistWater.liquid(3*U, false), dust.mat(MT.Fe2O3, 5));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MT.OREMATS.YellowLimonite, 8), NF, MT.DistWater.liquid(3*U, false), dust.mat(MT.Fe2O3, 5));
        RM.Drying.addRecipe1(true, 512, 128, dust.mat(MT.SiC, 2), NF, MT.Si.liquid(U, false), dust.mat(MT.Graphite, 1));
        RM.Drying.addRecipe1(true, 16, 512, dust.mat(MT.Dolomite, 10), NF, MT.CO2.gas(6*U, false), dust.mat(MT.Quicklime, 2), dust.mat(MTx.MgO, 2));

        // Keep dryer, 1 distWater is normally made in 2 ticks
        RM.Drying.addRecipe0(true, 16, 2000, MTx.CaCl2Solution.liquid(2*U, true), MT.DistWater.liquid(U, false), dust.mat(MT.CaCl2, 1));
        RM.Drying.addRecipe0(true, 16, 2000, MTx.FeCl2Solution.liquid(2*U, true), MT.DistWater.liquid(U, false), dust.mat(MT.FeCl2, 1));
        RM.Drying.addRecipe0(true, 16, 2000, MTx.MgCl2Solution.liquid(2*U, true), MT.DistWater.liquid(U, false), dust.mat(MT.MgCl2, 1));

        for (FluidStack tWater : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.CaCl2, 3), FL.array(tWater, MT.CO2.gas(3*U, true)), FL.array(MT.HCl.gas(4*U, false)), dust.mat(MT.CaCO3, 5));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Quicklime, 2), tWater, NF, dust.mat(MTx.CaOH2, 5));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.CaCl2, 3), tWater, MTx.CaCl2Solution.liquid(6*U, false), NI);
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.MgCl2, 3), tWater, MTx.MgCl2Solution.liquid(6*U, false), NI);
            RM.Mixer.addRecipe2(true, 16, 64, ST.tag(2), dust.mat(MT.FeCl2, 3), tWater, MTx.FeCl2Solution.liquid(6*U, false), NI);
        }

        // Sintering dusts into chunks
        sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 16, 0, 0, NF, ingot , 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 16, 0, 0, NF, chunk , 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 16, 0, 0, NF, nugget, 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 96, 0, 0, NF, ingot , 1, NI, NI, true, false, false, highHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 96, 0, 0, NF, chunk , 1, NI, NI, true, false, false, highHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 96, 0, 0, NF, nugget, 1, NI, NI, true, false, false, highHeatSintering));

        // misc sintering
        sintering.addRecipe2(true, 16, 64, dust.mat(MTx.CoO, 2), dust.mat(MT.Al2O3, 5), dust.mat(MTx.CobaltBlue, 7));

        for (ItemStack coal : ST.array(dust.mat(MT.Charcoal, 1), dust.mat(MT.LigniteCoke, 3),  dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1) )) {
            sintering.addRecipe(true, ST.array(dust.mat(MT.W, 1), ST.copy(coal)), ST.array(ingot.mat(MT.TungstenCarbide, 2)), null, null, ZL_FS, ZL_FS, 256, 96, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.Ta, 4), dust.mat(MT.Hf, 1), ST.mul(5, coal)), ST.array(ingot.mat(MT.Ta4HfC5, 10)), null, null, ZL_FS, ZL_FS, 512, 96, 0);

            sintering.addRecipe(true, ST.array(dust.mat(MT.Fe2O3, 5), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MT.Fe2O3, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Magnetite, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), ST.array(sinter.mat(MT.OREMATS.Magnetite, 24)), null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.BasalticMineralSand, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), ST.array(sinter.mat(MT.OREMATS.Magnetite, 24)), null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.GraniticMineralSand, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), ST.array(sinter.mat(MT.OREMATS.Magnetite, 24)), null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.YellowLimonite, 8), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MT.Fe2O3, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.BrownLimonite, 8), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MT.Fe2O3, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MT.OREMATS.Garnierite, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MT.OREMATS.Cassiterite, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Chromite, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), ST.array(sinter.mat(MT.OREMATS.Chromite, 24)), null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MTx.PbO, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MTx.PbO, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MTx.ZnO, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MTx.ZnO, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.MnO2, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MT.MnO2, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MTx.CoO, 4), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MTx.CoO, 8)), null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MTx.Co3O4, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), ST.array(sinter.mat(MTx.Co3O4, 24)), null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MT.SiO2, 6), ST.mul(1, coal)), ST.array(sinter.mat(MT.SiO2, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, ST.array(dust.mat(MTx.Sb2O3, 5), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), ST.array(sinter.mat(MTx.Sb2O3, 8)), null, null, ZL_FS, ZL_FS, 32, 16, 0);

            RM.BurnMixer.addRecipe2(true, 16, 128, dust.mat(MT.CaSO4, 12), coal, ZL_FS, FL.array(MT.CO2.gas(3*U, false), MT.SO2.gas(6*U, false)), dust.mat(MT.Quicklime, 4));
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

        // DRI and Fe3C
        directReduction.addRecipe2(true, 64, 64, ST.tag(0), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(73  * 2 * U25 , true)), FL.array(MT.H2O.liquid(73  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 64, ST.tag(1), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(81 * 2 * U100, true), MT.H.gas(243 * 2 * U100, true)), FL.array(MT.H2O.liquid(243 * 3 * U100, false), MT.CO2.gas(49 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 64, ST.tag(2), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(81 * 2 * U50 , true), MT.H.gas(81  * 2 * U50 , true)), FL.array(MT.H2O.liquid(81  * 3 * U50 , false), MT.CO2.gas(65 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 32, ST.tag(3), dust.mat(MT.Fe2O3, 5), FL.array(MT.CH4.gas(8  * 5 * U15 , true), MT.H.gas(23  * 2 * U15 , true)), FL.array(MT.H2O.liquid(39  * 3 * U15 , false)), dust.mat(MTx.ImpureCementite, 3));

        for(OreDictMaterial limonite : new OreDictMaterial[] {MT.OREMATS.BrownLimonite, MT.OREMATS.YellowLimonite }) {
            directReduction.addRecipe2(true, 64, 96, ST.tag(0), dust.mat(limonite, 8), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(73  * 2 * U25 , true)), FL.array(MT.H2O.liquid(73  * 3 * U25  + 3 * U, false)), dust.mat(MTx.SpongeIron, 3));
            directReduction.addRecipe2(true, 64, 96, ST.tag(1), dust.mat(limonite, 8), FL.array(MT.CO .gas(81 * 2 * U100, true), MT.H.gas(243 * 2 * U100, true)), FL.array(MT.H2O.liquid(243 * 3 * U100 + 3 * U, false), MT.CO2.gas(49 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 3));
            directReduction.addRecipe2(true, 64, 96, ST.tag(2), dust.mat(limonite, 8), FL.array(MT.CO .gas(81 * 2 * U50 , true), MT.H.gas(81  * 2 * U50 , true)), FL.array(MT.H2O.liquid(81  * 3 * U50  + 3 * U, false), MT.CO2.gas(65 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 3));
            directReduction.addRecipe2(true, 64, 48, ST.tag(3), dust.mat(limonite, 8), FL.array(MT.CH4.gas(8  * 5 * U15 , true), MT.H.gas(23  * 2 * U15 , true)), FL.array(MT.H2O.liquid(39  * 3 * U15  + 3 * U, false)), dust.mat(MTx.ImpureCementite, 3));
        }

        for(OreDictMaterial magnetite : new OreDictMaterial[] {MT.OREMATS.Magnetite, MT.OREMATS.GraniticMineralSand, MT.OREMATS.BasalticMineralSand }) {
            directReduction.addRecipe2(true, 64, 64*3, ST.tag(0), dust.mat(magnetite, 14), FL.array(MT.CO .gas(24  * 2 * U25 , true), MT.H.gas(254 * 2 * U25 , true)), FL.array(MT.H2O.liquid(254 * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 9));
            directReduction.addRecipe2(true, 64, 64*3, ST.tag(1), dust.mat(magnetite, 14), FL.array(MT.CO .gas(218 * 2 * U100, true), MT.H.gas(654 * 2 * U100, true)), FL.array(MT.H2O.liquid(654 * 3 * U100, false), MT.CO2.gas(122 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 9));
            directReduction.addRecipe2(true, 64, 64*3, ST.tag(2), dust.mat(magnetite, 14), FL.array(MT.CO .gas(218 * 2 * U50 , true), MT.H.gas(218 * 2 * U50 , true)), FL.array(MT.H2O.liquid(218 * 3 * U50 , false), MT.CO2.gas(170 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 9));
            directReduction.addRecipe2(true, 64, 32*3, ST.tag(3), dust.mat(magnetite, 14), FL.array(MT.CH4.gas(8   * 5 * U5  , true), MT.H.gas(18  * 2 * U5  , true)), FL.array(MT.H2O.liquid(34  * 3 * U5  , false)), dust.mat(MTx.ImpureCementite, 9));
        }
    }

    private void addOverrideRecipes() {
        RM.Centrifuge.addRecipe0(T, 64, 16, new long[]{9640, 100, 100, 100, 100, 100}, FL.Sluice.make(100), FL.Water.make(50), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Cu, 2), dustTiny.mat(MT.OREMATS.Cassiterite, 1), dustTiny.mat(MTx.ZnO, 1), dustTiny.mat(MT.Sb, 1), dustTiny.mat(MT.OREMATS.Chromite, 3));
        RM.MagneticSeparator.addRecipe1(T, 16, 16, new long[]{9640, 72, 72, 72, 72, 72}, dustTiny.mat(MT.SluiceSand, 1), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(T, 16, 36, new long[]{9640, 162, 162, 162, 162, 162}, dustSmall.mat(MT.SluiceSand, 1), dustSmall.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{9640, 648, 648, 648, 648, 648}, dust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(T, 16, 1296, new long[]{9640, 5832, 5832, 5832, 5832, 5832}, blockDust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 9), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));


        // Roasting
        for (String tOxygen : FluidsGT.OXYGEN)
            if (FL.exists(tOxygen)) {
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Realgar), FL.make(tOxygen, 1750), MT.SO2.gas(3 * U2, false), OM.dust(MTx.As2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stibnite), FL.make(tOxygen, 1800), MT.SO2.gas(9 * U5, false), OM.dust(MTx.Sb2O3, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Tetrahedrite), FL.make(tOxygen, 1125), MT.SO2.gas(9 * U8, false), OM.dust(MT.Cu, 3 * U8), OM.dust(MTx.Sb2O3, 5 * U16), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Arsenopyrite), FL.make(tOxygen, 1667), MT.SO2.gas(3 * U3, false), OM.dust(MT.Fe2O3, 5 * U6), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Molybdenite), FL.make(tOxygen, 2334), MT.SO2.gas(6 * U3, false), OM.dust(MTx.MoO3, 4 * U3));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Pentlandite), FL.make(tOxygen, 1471), MT.SO2.gas(24 * U17, false), OM.dust(MT.OREMATS.Garnierite, 9 * U17));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cobaltite), FL.make(tOxygen, 1611), MT.SO2.gas(3 * U3, false), OM.dust(MTx.Co3O4, 7 * U9), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Galena), FL.make(tOxygen, 875), MT.SO2.gas(6 * U8, false), OM.dust(MT.Ag, 3 * U8), OM.dust(MTx.PbO, 3 * U8));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cooperite), FL.make(tOxygen, 500), MT.SO2.gas(3 * U6, false), OM.dust(MT.PlatinumGroupSludge, 4 * U6), OM.dust(MT.OREMATS.Garnierite, U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stannite), FL.make(tOxygen, 1313), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MT.OREMATS.Cassiterite, U8), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Kesterite), FL.make(tOxygen, 1250), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MTx.ZnO, U8), OM.dust(MT.OREMATS.Cassiterite, U8));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cinnabar), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.HgO, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MTx.FeS), FL.make(tOxygen, 1083), MT.SO2.gas(3 * U2, false), OM.dust(MT.Fe2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, OP.dust.mat(MT.OREMATS.Magnetite          , 7), FL.make(tOxygen, 500), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), FL.make(tOxygen, 500), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, OP.dust.mat(MT.OREMATS.BasalticMineralSand, 7), FL.make(tOxygen, 500), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.Se), FL.make(tOxygen, 2000), NF, OM.dust(MTx.SeO2, 3*U));
            }

        final long[] tChances = new long[]{8000, 8000, 8000};

        for (String tAir : FluidsGT.AIR)
            if (FL.exists(tAir)) {
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Realgar), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.As2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Stibnite), FL.make(tAir, 6000), MT.SO2.gas(9 * U5, false), OM.dust(MTx.Sb2O3, U));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Tetrahedrite), FL.make(tAir, 4000), MT.SO2.gas(9 * U8, false), OM.dust(MT.Cu, 3 * U8), OM.dust(MTx.Sb2O3, 5 * U16), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Arsenopyrite), FL.make(tAir, 4000), MT.SO2.gas(3 * U3, false), OM.dust(MT.Fe2O3, 5 * U6), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Sphalerite), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Molybdenite), FL.make(tAir, 6000), MT.SO2.gas(6 * U3, false), OM.dust(MTx.MoO3, 4 * U3));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Pentlandite), FL.make(tAir, 4000), MT.SO2.gas(24 * U17, false), OM.dust(MT.OREMATS.Garnierite, 9 * U17));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cobaltite), FL.make(tAir, 4000), MT.SO2.gas(3 * U3, false), OM.dust(MTx.Co3O4, 7 * U9), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Galena), FL.make(tAir, 2000), MT.SO2.gas(6 * U8, false), OM.dust(MT.Ag, 3 * U8), OM.dust(MTx.PbO, 3 * U8));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cooperite), FL.make(tAir, 2000), MT.SO2.gas(3 * U6, false), OM.dust(MT.PlatinumGroupSludge, 4 * U6), OM.dust(MT.OREMATS.Garnierite, U6));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Stannite), FL.make(tAir, 5000), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MT.OREMATS.Cassiterite, U8), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Kesterite), FL.make(tAir, 4000), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MTx.ZnO, U8), OM.dust(MT.OREMATS.Cassiterite, U8));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cinnabar), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.HgO, U));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MTx.FeS), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MT.Fe2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.Magnetite          , 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.BasalticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15*U2));
            }

        RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.MnO2, 6), nugget.mat(MT.OREMATS.Cassiterite, 12), nugget.mat(MTx.ZnO, 3));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.MnO2, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 6));
        RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 3));
        RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
        RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Ag, 6), nugget.mat(MTx.PbO, 12), nugget.mat(MTx.Co3O4, 7));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Ag, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 14));
        RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 7));
        if (IL.PFAA_Sands.exists()) {
            RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt, 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Au, 8), nugget.mat(MT.Cu, 16), nugget.mat(MT.OREMATS.Garnierite, 4));
            RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt, 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Au, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 8));
            RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt, 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 4));
            RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Ag, 8), nugget.mat(MTx.PbO, 16), nugget.mat(MTx.Co3O4, 9));
            RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Ag, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 18));
            RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 9));
        }
        if (IL.TROPIC_Sand_Black.exists()) {
            RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
            RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
            RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mFluidOutputs.length >= 1 && r.mFluidOutputs[0].getFluid().getName().equals("molten.stainlesssteel"))
                r.mEnabled = false;
            else if (r.mFluidInputs.length == 1 && r.mOutputs.length == 1 && (
                    (r.mFluidInputs[0].getFluid().getName().equals("chlorine") && r.mOutputs[0].isItemEqual(dust.mat(MT.FeCl3, 4)) && r.mFluidOutputs.length == 0) ||
                    (r.mFluidInputs[0].getFluid().getName().equals("hydrochloricacid") && r.mOutputs[0].isItemEqual(dust.mat(MT.FeCl2, 3)) && r.mFluidOutputs.length == 1 && r.mFluidOutputs[0].getFluid().getName().equals("hydrogen"))
            )) { // fixes infinite silica from Fe -> FeCl2/FeCl3 -> Fe2O3 -> Fe + slag
                r.mEnabled = false;
            }
        }

        List<String> disableElectrolysisFluids = Arrays.asList(MT.GrayVitriol.mLiquid.getUnlocalizedName(), MT.GreenVitriol.mLiquid.getUnlocalizedName(), MT.PinkVitriol.mLiquid.getUnlocalizedName(), MT.BlackVitriol.mLiquid.getUnlocalizedName());

        for (Recipe r : RM.Electrolyzer.mRecipeList) {
            if (r.mFluidInputs.length > 1) {
                for (FluidStack input : r.mFluidInputs) {
                    if (disableElectrolysisFluids.contains(input.getUnlocalizedName()))
                        r.mEnabled = false;
                }
            }
        }
        for (FluidStack tWater : FL.waters(3000)) {
            RM.Electrolyzer.addRecipe2(true, 64, 128, dust.mat(MTx.NH4SO4, 9), dust.mat(MTx.SeO2, 3), FL.array(MT.GrayVitriol.liquid(6 * U, true), tWater), FL.array(MT.H2SO4.liquid(14 * U, false), MT.NH3.gas(2 * U, false), MT.O.gas(3 * U, false)), dust.mat(MT.Mn, 1), dust.mat(MT.Se, 1));
        }
        Recipe x = RM.Centrifuge.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, OM.dust(MT.OREMATS.Cinnabar)); if (x != null) x.mEnabled = false;

        // Hints
        for (Recipe r : RM.DidYouKnow.mRecipeList) {
            if (r.mOutputs.length >= 1 && r.mOutputs[0] != null && (
                r.mOutputs[0].isItemEqual(dust.mat(MT.Steel, 1)) ||
                r.mOutputs[0].isItemEqual(IL.Bottle_Mercury.get(1))
            ))
                r.mEnabled = false;
        }

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                ST.make(dust.mat(MT.Fe2O3, 5), "Throw some Iron Ore into a crucible.")
                , ST.make(dust.mat(MT.CaCO3, 1), "Add some flux.")
                , ST.make(gem.mat(MT.Charcoal, 6), "And some coke.")
                , IL.Ceramic_Crucible.getWithName(1, "Heat up the crucible using a burning box and wait until it all turns into sponge iron")
                , ST.make(scrapGt.mat(MTx.SpongeIron, 1), "Get the bloom out with a shovel")
                , ST.make(MTEx.gt6Registry.getItem(32028), "Get rid of slag and excess carbon by hammering the sponge iron scrap on any anvil to make wrought iron")
        ), ST.array(nugget.mat(MT.WroughtIron, 1), ingot.mat(MT.WroughtIron, 1), plate.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.FerrousSlag, 1), stick.mat(MT.WroughtIron, 1), gearGtSmall.mat(MT.WroughtIron, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                IL.Ceramic_Mold.getWithName(1, "Don't forget to shape the Mold to pour it")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it all turns into Steel")
                , ST.make(MTEx.gt6Registry.getItem(1302), "Point a running Engine into the Crucible to blow Air")
                , ST.make(ingot.mat(MT.PigIron, 1), "Throw some Pig Iron into Crucible. Do not forget to leave space for Air!")
                , ST.make(MTEx.gt6Registry.getItem(1199), "Heat up the Crucible using a Burning Box")
        ), ST.array(dust.mat(MT.Steel, 1), ingot.mat(MT.Steel, 1), plate.mat(MT.Steel, 1), scrapGt.mat(MT.Steel, 1), stick.mat(MT.Steel, 1), gearGt.mat(MT.Steel, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(F, ST.array(
                ST.make(dust.mat(MT.OREMATS.Cinnabar, 3), "Throw three Units of Cinnabar into Crucible")
                , ST.make(dust.mat(MTx.HgO, 2), "Or two Units of Mercuric Oxide produced by roasting the cinnabar first!")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it melts into Mercury")
                , IL.Bottle_Empty.getWithName(1, "Rightclick the Crucible with an Empty Bottle")
                , ST.make(MTEx.gt6Registry.getItem(1199), "Heat up the Crucible using a Burning Box")
                , ST.make(Blocks.redstone_ore, 1, 0, "Using a Club to mine Vanilla Redstone Ore gives Cinnabar")
        ), ST.array(IL.Bottle_Mercury.get(1), ST.make(ingot.mat(MT.Hg, 1), "Pouring this into Molds only works with additional Cooling!"), ST.make(nugget.mat(MT.Hg, 1), "Pouring this into Molds only works with additional Cooling!")), null, ZL_LONG, FL.array(MT.Hg.liquid(1, T)), FL.array(MT.Hg.liquid(1, T)), 0, 0, 0);
    }
}
