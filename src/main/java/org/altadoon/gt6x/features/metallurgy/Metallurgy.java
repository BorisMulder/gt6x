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
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.metallurgy.multiblocks.*;
import org.altadoon.gt6x.features.metallurgy.utils.EAFSmeltingRecipe;
import org.altadoon.gt6x.features.metallurgy.utils.RecipeMapHandlerPrefixSintering;

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
    public Recipe.RecipeMap cowperStove = null;
    public Recipe.RecipeMap basicOxygen = null;
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
        changePrefixNames();
    }

    @Override
    public void afterPostInit() {
        changeRecipes();
    }

    private void createPrefixes() {
        sinter = OreDictPrefix.createPrefix("sinter")
            .setCategoryName("Sinters")
            .setLocalItemName("", " Sinter");
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.sinter", sinter,
            MT.Empty, MT.Fe2O3, MT.OREMATS.Magnetite, MTx.FeO, MT.OREMATS.Garnierite, MT.OREMATS.Cassiterite, MT.OREMATS.Chromite, MTx.PbO, MTx.ZnO, MT.MnO2, MTx.Co3O4, MTx.CoO, MT.SiO2, MTx.Sb2O3
        );
    }

    private void changeMaterialProperties() {
        MT.PigIron.setPulver(MT.PigIron, U).setSmelting(MT.PigIron, U);
        MT.OREMATS.Garnierite.setSmelting(MT.OREMATS.Garnierite, U);
        MT.OREMATS.Cobaltite.setSmelting(MT.OREMATS.Cobaltite, U);
        MT.MnO2.setSmelting(MT.MnO2, U);

        for (OreDictMaterial removeElectro : new OreDictMaterial[] { MT.Olivine, MT.OREMATS.Garnierite, MT.OREMATS.Smithsonite, MT.OREMATS.Cassiterite, MT.OREMATS.Wollastonite, MT.Phosphorite, MT.Apatite }) {
            removeElectro.remove(TD.Processing.ELECTROLYSER);
        }
        for (OreDictMaterial removeCent : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusWhite, MT.PhosphorusRed }) {
            removeCent.remove(TD.Processing.CENTRIFUGE);
        }

        // to make smelting bloom in crucibles easier
        for (OreDictMaterial magnetite : new OreDictMaterial[] { MT.OREMATS.Magnetite, MT.OREMATS.BasalticMineralSand, MT.OREMATS.GraniticMineralSand })
            magnetite.heat(MT.Fe2O3);
    }

    private void changeByProducts() {
        MT.OREMATS.Cobaltite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Co.mID);
        MT.OREMATS.Stibnite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Sb.mID);
        MT.OREMATS.Sphalerite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Zn.mID);
        MT.OREMATS.Garnierite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Ni.mID);
        MT.OREMATS.Galena.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Pb.mID);

        for (OreDictMaterial mat : new OreDictMaterial[] {MT.OREMATS.Sperrylite, MT.OREMATS.Tetrahedrite, MT.Cu, MT.OREMATS.Cooperite, MT.MeteoricIron, MT.Cu, MT.Ga, MT.Ag, MT.Au, MT.Pt, MT.OREMATS.YellowLimonite, MT.OREMATS.Stolzite, MT.OREMATS.Pinalite, MT.OREMATS.Chalcopyrite, MT.OREMATS.Cobaltite, MT.Alduorite }) {
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
                } else if (byproduct.mID == MT.Cd.mID) {
                    it.set(MTx.CdS);
                }
            }
        }
    }

    private void addRecipeMaps() {
        blastFurnace    = new Recipe.RecipeMap(null, "gt6x.recipe.blastfurnace"   , "Blast Furnace"           , null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace"   , 6, 3, 1, 3, 3, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        cowperStove     = new Recipe.RecipeMap(null, "gt6x.recipe.cowperstove"    , "Hot Blast Preheating"    , null, 0, 1, RES_PATH_GUI+"machines/Default"        , 0, 0, 0, 1, 1, 1, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        basicOxygen     = new Recipe.RecipeMap(null, "gt6x.recipe.bop"            , "Basic Oxygen Steelmaking", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace"   , 6, 3, 1, 3, 3, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        directReduction = new Recipe.RecipeMap(null, "gt6x.recipe.directreduction", "Direct Reduction"        , null, 0, 1, RES_PATH_GUI+"machines/DirectReduction", 6, 3, 1, 3, 3, 1, 1, 1, "", 1, "", true, true, true, true, false, true, true);
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
        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.Si, MT.Fe, MT.WroughtIron, MT.Steel, MT.MeteoricSteel, MT.StainlessSteel, MT.TungstenCarbide, MT.Ta4HfC5, MT.SiC, MT.Vibramantium }) {
            removeAlloySmeltingRecipe(mat);
        }

        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 3, OM.stack(MT.Fe2O3                      , 5*U), OM.stack(MT.C, 4*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.Magnetite          , 14*U), OM.stack(MT.C, 12*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.BasalticMineralSand, 14*U), OM.stack(MT.C, 12*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.GraniticMineralSand, 14*U), OM.stack(MT.C, 12*U)));

        // Bessemer Process
        MT.Steel.addAlloyingRecipe(new OreDictConfigurationComponent( 3, OM.stack(MT.PigIron, 4*U), OM.stack(MT.CaCO3, U), OM.stack(MT.Air, 4*U)));
        MT.Steel.addAlloyingRecipe(new OreDictConfigurationComponent( 8, OM.stack(MT.PigIron, 10*U), OM.stack(MT.Quicklime, U), OM.stack(MT.Air, 10*U)));
        MTx.MeteoricCementite.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.MeteoricIron, U), OM.stack(MT.C, U2)));
        MT.MeteoricSteel.addAlloyingRecipe(new OreDictConfigurationComponent( 3, OM.stack(MTx.MeteoricCementite, 4*U), OM.stack(MT.Air, 4*U)));

        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.Steel,16*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Cr, 4*U), OM.stack(MT.Mn, 4*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.Steel,24*U), OM.stack(MT.Nichrome, 5*U), OM.stack(MT.Cr, 3*U), OM.stack(MT.Mn, 4*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.Steel,14*U), OM.stack(MTx.FeCr2, 6*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Mn, 4*U)));

        MT.Invar.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.Steel,2*U), OM.stack(MT.Ni, U)));
        MT.TinAlloy.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.Steel, U), OM.stack(MT.Sn, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.Steel, U), OM.stack(MT.Al, U), OM.stack(MT.Cr, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(6, OM.stack(MT.Steel, U), OM.stack(MT.Al, 2*U), OM.stack(MTx.FeCr2, 3*U)));
        MT.Angmallen.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.Steel, U), OM.stack(MT.Au, U)));
    }

    private void changePrefixNames() {
        LH.add("oredict." + ingot.dat(MTx.HBI) + ".name", MTx.HBI.getLocal());
    }

    private void addMTEs() {
        OreDictMaterial mat;
        mat = MT.WroughtIron;    MTEx.gt6xMTEReg.add("Blast Furnace Part ("+mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFPartIron   .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', plate.dat(mat), 'F', MTEx.gt6Registry.getItem(18000)); // fire bricks
                                 MTEx.gt6xMTEReg.add("Blast Furnace ("     +mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFIron       .get(), 17101, MultiTileEntityBlastFurnace  .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnace"     , NBT_DESIGN, MTEx.IDs.BFPartIron.get(), NBT_INPUT, 32, NBT_INPUT_MIN, 8 , NBT_INPUT_MAX, 32, NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, blastFurnace   , NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT  , NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', plateCurved.dat(mat), 'R', stickLong.dat(mat), 'F', MTEx.gt6xMTEReg.getItem(MTEx.IDs.BFPartIron.get()));
        mat = MT.Steel;          MTEx.gt6xMTEReg.add("Blast Furnace Part ("+mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFPartSteel  .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', plate.dat(mat), 'F', MTEx.gt6Registry.getItem(18000)); // fire bricks
                                 MTEx.gt6xMTEReg.add("Blast Furnace ("     +mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFSteel      .get(), 17101, MultiTileEntityBlastFurnace  .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnace"     , NBT_DESIGN, MTEx.IDs.BFPartSteel.get(), NBT_INPUT, 32, NBT_INPUT_MIN, 8 , NBT_INPUT_MAX, 32, NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, blastFurnace   , NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT  , NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', plateCurved.dat(mat), 'R', stickLong.dat(mat), 'F', MTEx.gt6xMTEReg.getItem(MTEx.IDs.BFPartSteel.get()));

        mat = MT.Al2O3;          MTEx.gt6xMTEReg.add("Cowper Stove"                           , "Multiblock Machines", MTEx.IDs.CowperStove  .get(), 17101, MultiTileEntityCowperStove   .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "cowperstove"      , NBT_INPUT, 32, NBT_INPUT_MIN, 16, NBT_INPUT_MAX, 64, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, cowperStove, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_PARALLEL, 64, NBT_PARALLEL_DURATION, true), "IPI", "PSP", "wIh", 'I', ingot.dat(mat), 'P', pipeMedium.dat(MT.StainlessSteel), 'S', MTEx.gt6xMTEReg.getItem(MTEx.IDs.AluminaCheckerBricks.get()));

        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        mat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering1   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_INPUT,   32, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateDouble   .dat(ANY.Cu), 'P', plate.dat(MT.Ceramic), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1005));
        mat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering2   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  4.0F, NBT_RESISTANCE,  4.0F, NBT_INPUT,  128, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wPh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateTriple   .dat(ANY.Cu), 'P', plateTriple.dat(MT.Ta     ), 'B', Blocks.brick_block);
        mat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering3   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  9.0F, NBT_RESISTANCE,  9.0F, NBT_INPUT,  512, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wPh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateQuadruple.dat(ANY.Cu), 'P', plateTriple.dat(MT.W      ), 'B', Blocks.brick_block);
        mat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering4   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wPh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateQuintuple.dat(ANY.Cu), 'P', plateTriple.dat(MT.Ta4HfC5), 'B', Blocks.brick_block);

        mat = MT.SiC;            MTEx.gt6xMTEReg.add("Shaft Furnace"                          , "Multiblock Machines", MTEx.IDs.ShaftFurnace .get(), 17101, MultiTileEntityShaftFurnace  .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "shaftfurnace"     , NBT_INPUT, 64, NBT_INPUT_MIN, 64, NBT_INPUT_MAX, 1024, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, directReduction, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true), "IPI", "PSP", "wIh", 'I', plate.dat(mat), 'P', pipeMedium.dat(MT.StainlessSteel), 'S', MTEx.gt6xMTEReg.getItem(MTEx.IDs.SiCWall.get()));

        mat = MT.Graphite;       MTEx.gt6xMTEReg.add("Graphite Electrodes"                    , "Multiblock Machines", MTEx.IDs.EAFElectrodes.get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "eafelectrodes"    , NBT_DESIGNS, 1), " h ", "RRR", "   ", 'R', stick.dat(mat));

        mat = MTx.MgOC;          MTEx.gt6xMTEReg.add("Electric Arc Furnace"                   , "Multiblock Machines", MTEx.IDs.EAF          .get(), 17101, MultiTileEntityEAF           .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F,                                   NBT_INPUT, 512, NBT_INPUT_MIN, 512, NBT_INPUT_MAX, 16384, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true), "TGW", "CSC", "PhP", 'C', OD_CIRCUITS[3], 'P', Blocks.piston, 'S', MTEx.gt6xMTEReg.getItem(MTEx.IDs.MgOCWall.get()), 'T', MTEx.gt6Registry.getItem(31000), 'G', Blocks.glass_pane, 'W', MTEx.gt6Registry.getItem(31012));

        mat = MT.Steel;          MTEx.gt6xMTEReg.add("Steel-lined MgO-C Wall"                 , "Multiblock Machines", MTEx.IDs.BOFWall      .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.0F, NBT_RESISTANCE, 12.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 3), "hP ", "PF ", "   ", 'P', plate.dat(mat), 'F', MTEx.gt6xMTEReg.getItem(MTEx.IDs.MgOCWall.get()));
                                 MTEx.gt6xMTEReg.add("Basic Oxygen Furnace"                   , "Multiblock Machines", MTEx.IDs.BOF          .get(), 17101, MultiTileEntityBOF           .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.0F, NBT_RESISTANCE, 12.0F, NBT_TEXTURE, "bof"              , NBT_INPUT,  32, NBT_INPUT_MIN,  16, NBT_INPUT_MAX,    64, NBT_ENERGY_ACCEPTED, TD.Energy.TU, NBT_RECIPEMAP, basicOxygen, NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_PARALLEL, 64, NBT_PARALLEL_DURATION, true), "P P", "U U", "hWw", 'P', plate.dat(mat), 'W', MTEx.gt6xMTEReg.getItem(MTEx.IDs.BOFWall.get()), 'U', pipeMedium.dat(mat));
        mat = MT.StainlessSteel; MTEx.gt6xMTEReg.add("Oxygen Lance"                           , "Multiblock Machines", MTEx.IDs.BOFLance     .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 2), " S ", "wP ", " P ", 'P', pipeTiny.dat(mat), 'S', plate.dat(mat));

    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ICondition<OreDictMaterial>
            lowHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmax(3300), fullforge()),
            highHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmin(3300), fullforge());


    private void addRecipes() {
        // slag melts @ 1540
        OreDictMaterial[][] smeltLiquidSlag = new OreDictMaterial[][] {
                {MT.Fe2O3, MT.PigIron}, {MT.OREMATS.Magnetite, MT.PigIron}, {MTx.FeO, MT.PigIron}, {MT.OREMATS.Chromite, MTx.FeCr2},
        };
        OreDictMaterial[][] smeltSolidSlag = new OreDictMaterial[][] {
                {MT.OREMATS.Garnierite, MT.Ni}, {MT.OREMATS.Cassiterite, MT.Sn}, {MTx.PbO, MT.Pb}, {MT.MnO2, MT.Mn}, {MTx.CoO, MT.Co}, {MTx.Co3O4, MT.Co}, {MT.SiO2, MT.Si}, {MTx.Sb2O3, MT.Sb}
        };

        FluidStack blast = MTx.HotBlast.gas(U1000, true);
        cowperStove.addRecipe0(false, 16, 128, FL.Air.make(1000), FL.mul(blast, 1000), ZL_IS);

        for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.CoalCoke, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.Charcoal, 2), dust.mat(MT.C, 1), gem.mat(MT.PetCoke, 1), gem.mat(MT.CoalCoke, 1), gem.mat(MT.LigniteCoke, 3), gem.mat(MT.Charcoal, 2))) {
            // ore dusts (less efficent)
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.Fe2O3, 5), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.Fe2O3, 5), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.FeO, 4), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 384, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.FeO, 4), ST.mul(2, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 192, 8, 0);
            for (OreDictMaterial magnetite : new OreDictMaterial[] { MT.OREMATS.Magnetite, MT.OREMATS.BasalticMineralSand, MT.OREMATS.GraniticMineralSand }) {
                blastFurnace.addRecipe(true, ST.array(dust.mat(magnetite, 14), ST.mul(12, coal), dust.mat(MT.CaCO3, 3)), ZL_IS, null, null, FL.array(FL.Air.make  (22500)), FL.array(MT.PigIron.liquid(6 * U, false), MTx.Slag.liquid(3 * U, false), MTx.BlastFurnaceGas.gas(36 * U, false)), 1536, 8, 0);
                blastFurnace.addRecipe(true, ST.array(dust.mat(magnetite, 14), ST.mul( 9, coal), dust.mat(MT.CaCO3, 3)), ZL_IS, null, null, FL.array(FL.mul(blast, 22500)), FL.array(MT.PigIron.liquid(6 * U, false), MTx.Slag.liquid(3 * U, false), MTx.BlastFurnaceGas.gas(36 * U, false)), 768, 8, 0);
            }
            for (OreDictMaterial limonite : new OreDictMaterial[] { MT.OREMATS.YellowLimonite, MT.OREMATS.BrownLimonite }) {
                blastFurnace.addRecipe(true, ST.array(dust.mat(limonite, 8), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15 * U, false)), 512, 8, 0);
                blastFurnace.addRecipe(true, ST.array(dust.mat(limonite, 8), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.PigIron.liquid(2 * U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15 * U, false)), 256, 8, 0);
            }
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Ni.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Ni.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Sn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Sn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Chromite, 14), ST.mul(12, coal), dust.mat(MT.CaCO3, 3)), ZL_IS, null, null, FL.array(FL.Air.make  (22500)), FL.array(MTx.FeCr2.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 1536, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.OREMATS.Chromite, 14), ST.mul( 9, coal), dust.mat(MT.CaCO3, 3)), ZL_IS, null, null, FL.array(FL.mul(blast, 22500)), FL.array(MTx.FeCr2.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.PbO, 2), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Pb.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.PbO, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Pb.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.ZnO, 2), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make  (5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(14*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.ZnO, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.mul(blast, 5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(14*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.MnO2, 2), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Mn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.MnO2, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Mn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.CoO, 4), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Co.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.CoO, 4), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ST.array(gem.mat(MTx.Slag, 1)), null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Co.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.Co3O4, 14), ST.mul(12, coal), dust.mat(MT.CaCO3, 3)), ST.array(gem.mat(MTx.Slag, 3)), null, null, FL.array(FL.Air.make  (22500)), FL.array(MT.Co.liquid(6*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 1536, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.Co3O4, 14), ST.mul( 9, coal), dust.mat(MT.CaCO3, 3)), ST.array(gem.mat(MTx.Slag, 3)), null, null, FL.array(FL.mul(blast, 22500)), FL.array(MT.Co.liquid(6*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.SiO2, 6), ST.mul(4, coal)), ZL_IS, null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MT.SiO2, 6), ST.mul(3, coal)), ZL_IS, null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.Sb2O3, 5), ST.mul(4, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.Air.make  (7500)), FL.array(MT.Sb.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 512, 8, 0);
            blastFurnace.addRecipe(true, ST.array(dust.mat(MTx.Sb2O3, 5), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)), ZL_IS, null, null, FL.array(FL.mul(blast, 7500)), FL.array(MT.Sb.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);

            // sintered pellets (more efficient)
            for (OreDictMaterial[] mats : smeltLiquidSlag) {
                blastFurnace.addRecipe2(true, 8, 256, sinter.mat(mats[0], 8), ST.mul(2, coal), FL.array(FL.Air.make  (5000)), FL.array(mats[1].liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(10*U, false)));
                blastFurnace.addRecipe2(true, 8, 128, sinter.mat(mats[0], 8), ST.mul(1, coal), FL.array(FL.mul(blast, 5000)), FL.array(mats[1].liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(10*U, false)));
            }
            for (OreDictMaterial[] mats : smeltSolidSlag) {
                blastFurnace.addRecipe2(true, 8, 256, sinter.mat(mats[0], 8), ST.mul(2, coal), FL.array(FL.Air.make  (5000)), FL.array(mats[1].liquid(2*U, false), MTx.BlastFurnaceGas.gas(10*U, false)), gem.mat(MTx.Slag, 1));
                blastFurnace.addRecipe2(true, 8, 128, sinter.mat(mats[0], 8), ST.mul(1, coal), FL.array(FL.mul(blast, 5000)), FL.array(mats[1].liquid(2*U, false), MTx.BlastFurnaceGas.gas(10*U, false)), gem.mat(MTx.Slag, 1));
            }
            blastFurnace.addRecipe2(true, 8, 256, sinter.mat(MTx.ZnO, 8), ST.mul(2, coal), FL.array(FL.Air.make  (5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(14*U, false)), gem.mat(MTx.Slag, 1));
            blastFurnace.addRecipe2(true, 8, 128, sinter.mat(MTx.ZnO, 8), ST.mul(1, coal), FL.array(FL.mul(blast, 5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(14*U, false)), gem.mat(MTx.Slag, 1));
        }

        // Pidgeon Process
        blastFurnace.addRecipe2(true, 8, 640, dust.mat(MT.Dolomite, 20), dust.mat(MT.Si, 1), ZL_FS, FL.array(MTx.MgBlastFurnaceGas.gas(14*U, false), MTx.Slag.liquid(5*U, false)), dust.mat(MT.Quicklime, 2));
        blastFurnace.addRecipeX(true, 8, 640, ST.array(dust.mat(MT.MgCO3, 10), dust.mat(MT.CaCO3, 10), dust.mat(MT.Si, 1)), ZL_FS, FL.array(MTx.MgBlastFurnaceGas.gas(14*U, false), MTx.Slag.liquid(5*U, false)), dust.mat(MT.Quicklime, 2));
        blastFurnace.addRecipe2(true, 8, 640, dust.mat(MTx.CalcinedDolomite, 8), dust.mat(MT.Si, 1), ZL_FS, FL.array(MT.Mg.gas(2*U, false), MTx.Slag.liquid(5*U, false)), dust.mat(MT.Quicklime, 2));
        blastFurnace.addRecipeX(true, 8, 384, ST.array(dust.mat(MTx.MgO, 4), dust.mat(MT.Quicklime, 2), dust.mat(MT.Si, 1)), ZL_FS, FL.array(MT.Mg.gas(2*U, false), MTx.Slag.liquid(5*U, false)));

        // blast furnace gases
        RM.CryoDistillationTower.addRecipe0(true, 64,  128, FL.array(MTx.BlastFurnaceGas.gas(4*U10, true)), FL.array(MT.N.gas(177*U1000, false), MT.CO.gas(4*20*U1000, T), MT.CO2.gas(4*30*U1000, T), MT.H.gas(4*5*U1000, T), MT.He.gas(U1000, T), MT.Ne.gas(U1000, T), MT.Ar.gas(U1000, T)));
        FM.Burn.addRecipe0(true, -16, 1, MTx.BlastFurnaceGas.gas(3*U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        RM.Distillery.addRecipe1(true, 16, 64, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Zn.gas(U, false), MTx.BlastFurnaceGas.gas(6*U, false)));
        RM.Distillery.addRecipe1(true, 16, 64, ST.tag(0), FL.array(MTx.MgBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Mg.gas(U, false), MT.CO2.gas(6*U, false)));

        // Wrought Iron
        RM.Anvil.addRecipe2(false, 64, 192, scrapGt.mat(MTx.SpongeIron, 9), scrapGt.mat(MTx.SpongeIron, 9), ingot.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.FerrousSlag, 8));

        // Precipitation of Zn, As, Mg, P gases
        RM.Freezer.addRecipe1(true, 16, 48, ST.tag(0), MT.Zn.gas(U, true), NF, OM.dust(MT.Zn, U)); // from 1180 to 300
        RM.Freezer.addRecipe1(true, 16, 32, ST.tag(0), MT.As.gas(U, true), NF, OM.dust(MT.As, U)); // from 887 to 300
        RM.Freezer.addRecipe1(true, 16, 64, ST.tag(0), MT.Mg.gas(U, true), NF, OM.dust(MT.Mg, U)); // from 1378 to 300
        RM.Bath   .addRecipe1(true, 0, 512, ST.tag(0), MT.Zn.gas(U, true), MT.Zn.liquid(U, false), NI); // from 1180 to 692
        RM.Bath   .addRecipe1(true, 0, 512, ST.tag(0), MT.As.gas(U, true), NF, OM.dust(MT.As, U)); // from 887 to 300
        RM.Bath   .addRecipe1(true, 0, 512, ST.tag(0), MT.Mg.gas(U, true), MT.Mg.liquid(U, false), NI); // from 1378 to 922
        RM.Bath   .addRecipe1(true, 0, 128, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Zn.liquid(U, false), MTx.BlastFurnaceGas.gas(6*U, false)));
        RM.Bath   .addRecipe1(true, 0, 128, ST.tag(0), FL.array(MTx.MgBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Mg.liquid(U, false), MT.CO2.gas(6*U, false)));
        RM.Bath   .addRecipe1(true, 0, 256, ST.tag(0), FL.array(MTx.P_CO_Gas.gas(6*U, true)), FL.array(MT.CO.gas(5*U, false)), dust.mat(MT.P, 1));

        // Acidic leaching for electrowinning
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.ZnO, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.WhiteVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CdO, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MTx.CdSO4Solution.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Garnierite, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.CyanVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CoO, 2), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.RedVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.MnO2, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.GrayVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false), MT.O.gas(U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Cassiterite, 1), FL.array(MT.HCl.gas(8*U, true)), FL.array(MT.StannicChloride.liquid(5*U, false), MT.H2O.liquid(6*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Fayalite, 7), MT.HCl.gas(8*U, true), MTx.FeCl2Solution.liquid(12*U, false), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.Olivine, 7), FL.array(MT.HCl.gas(8*U, true)), FL.array(MTx.FeCl2Solution.liquid(6*U, false), MTx.MgCl2Solution.liquid(6*U, false)), dust.mat(MT.SiO2, 3));

        // Reduction of As, Sb, Pb
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.As2O3, 5), dust.mat(MT.Zn, 3), ZL_FS, FL.array(MT.As.gas(2*U, false)), dust.mat(MTx.ZnO, 3));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.As2O3, 5), dust.mat(MT.Fe, 2), ZL_FS, FL.array(MT.As.gas(2*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Realgar, 2), dust.mat(MT.Fe, 1), ZL_FS, FL.array(MT.As.gas(U, false)), dust.mat(MTx.FeS, 2));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Stibnite, 5), dust.mat(MT.Fe, 3), ZL_FS, FL.array(MT.Sb.liquid(2*U, false)), dust.mat(MTx.FeS, 6));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.PbO, 1), FL.array(MT.H.gas(2 * U, true)), FL.array(MT.H2O.liquid(3 * U, false), MT.Pb.liquid(U, false)));

        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.Co3O4, 7), NF, MT.O.gas(U, false), dust.mat(MTx.CoO, 6));
        RMx.Thermolysis.addRecipe0(true, 16, 512, FL.array(MT.GreenVitriol.liquid(12*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.SO3.gas(4*U, false)), dust.mat(MT.Fe2O3, 5));
        RMx.Thermolysis.addRecipe0(true, 16, 256, FL.array(MT.PinkVitriol.liquid(6*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.O.gas(U, false)), dust.mat(MTx.MgO, 2));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.HgO, 1), ZL_FS, FL.array(MT.Hg.liquid(U2, false), MT.O.gas(U2, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MT.OREMATS.Smithsonite, 5), ZL_FS, FL.array(MT.CO2.gas(3*U, false)), dust.mat(MTx.ZnO, 1));

        // Bayer waste products processing
        RM.Centrifuge.addRecipe1(true, 16, 256, new long[] {10000, 3000, 2000, 1000, 1000, 500, 50}, dust.mat(MTx.RedMud, 1), dustTiny.mat(MT.Fe2O3, 9), dustTiny.mat(MT.Sodalite, 9), dustTiny.mat(MT.OREMATS.Wollastonite, 9), dustTiny.mat(MT.TiO2, 9), dustTiny.mat(MT.AlO3H3, 9), dustTiny.mat(MTx.Sc2O3, 9));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Sc2O3, 5), MT.HF.gas(12*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.ScF3, 8));

        RM.Drying.addRecipe0(true, 16, 6000, MTx.BayerLiquor.liquid(6*U, true), FL.DistW.make(3000), dust.mat(MT.NaOH, 3));
        RM.Electrolyzer.addRecipe1(true, 64, 512, ST.tag(1), FL.array(MTx.BayerLiquor.liquid(24*U, true), MT.Hg.liquid(9*U, true)), FL.array(MTx.GaAmalgam.liquid(10*U, false), MT.H.gas(5*U, false), FL.Water.make(4500)), dust.mat(MT.NaOH, 12), OM.dust(MT.V2O5, 7*U2));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.NaOH, 3), FL.array(MTx.GaAmalgam.liquid(10*U, true), FL.Water.make(4500), FL.Oxygen.make(1500)), FL.array(MT.Hg.liquid(9*U, false)), dust.mat(MTx.NaGaOH4, 10));
        RM.Electrolyzer.addRecipe1(true, 64, 512, dust.mat(MTx.NaGaOH4, 10), ZL_FS, FL.array(FL.Water.make(4500), FL.Oxygen.make(1500)), dust.mat(MT.Ga, 1), dust.mat(MT.NaOH, 3));

        // Sintering dusts into chunks
        RMx.sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 16, 0, 0, NF, ingot , 1, ST.tag(1), NI, true, false, false, lowHeatSintering));
        RMx.sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 16, 0, 0, NF, chunk , 1, ST.tag(1), NI, true, false, false, lowHeatSintering));
        RMx.sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 16, 0, 0, NF, nugget, 1, ST.tag(1), NI, true, false, false, lowHeatSintering));
        RMx.sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 96, 0, 0, NF, ingot , 1, ST.tag(1), NI, true, false, false, highHeatSintering));
        RMx.sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 96, 0, 0, NF, chunk , 1, ST.tag(1), NI, true, false, false, highHeatSintering));
        RMx.sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 96, 0, 0, NF, nugget, 1, ST.tag(1), NI, true, false, false, highHeatSintering));

        // misc sintering
        RMx.sintering.addRecipeX(true, 16, 64  , ST.array(ST.tag(2), dust.mat(MTx.CoO, 2), dust.mat(MT.Al2O3, 5)), dust.mat(MTx.CobaltBlue, 7));
        RMx.sintering.addRecipeX(true, 96, 2048, ST.array(ST.tag(2), dust.mat(MT.Ad, 3), dust.mat(MT.Vb, 1)), ingot.mat(MT.Vibramantium, 4));

        // BF Sinters and carbides
        for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.LigniteCoke, 3),  dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1) )) {
            RMx.sintering.addRecipeX(true, 96, 334, ST.array(ST.tag(2), dust.mat(MT.W, 1), ST.copy(coal)), ingot.mat(MT.TungstenCarbide, 2));
            RMx.sintering.addRecipeX(true, 96, 1400, ST.array(ST.tag(3), dust.mat(MT.Ta, 4), dust.mat(MT.Hf, 1), ST.mul(5, coal)), ingot.mat(MT.Ta4HfC5, 10));
            RMx.sintering.addRecipeX(true, 96, 1746, ST.array(ST.tag(3), dust.mat(MT.Ke, 6), dust.mat(MT.Nq, 2), ST.mul(1, coal)), ingot.mat(MT.Trinaquadalloy, 9));

            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.Fe2O3                      , 5 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MT.Fe2O3, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MTx.FeO                       , 4 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MTx.FeO, 8));
            RMx.sintering.addRecipeX(true, 16, 96, ST.array(ST.tag(3), dust.mat(MT.OREMATS.Magnetite          , 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), sinter.mat(MT.OREMATS.Magnetite, 24));
            RMx.sintering.addRecipeX(true, 16, 96, ST.array(ST.tag(3), dust.mat(MT.OREMATS.BasalticMineralSand, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), sinter.mat(MT.OREMATS.Magnetite, 24));
            RMx.sintering.addRecipeX(true, 16, 96, ST.array(ST.tag(3), dust.mat(MT.OREMATS.GraniticMineralSand, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), sinter.mat(MT.OREMATS.Magnetite, 24));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.OREMATS.YellowLimonite     , 8 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MT.Fe2O3, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.OREMATS.BrownLimonite      , 8 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MT.Fe2O3, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.OREMATS.Garnierite         , 2 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MT.OREMATS.Garnierite, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.OREMATS.Cassiterite        , 2 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MT.OREMATS.Cassiterite, 8));
            RMx.sintering.addRecipeX(true, 16, 96, ST.array(ST.tag(3), dust.mat(MT.OREMATS.Chromite           , 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), sinter.mat(MT.OREMATS.Chromite, 24));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MTx.PbO                       , 2 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MTx.PbO, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MTx.ZnO                       , 2 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MTx.ZnO, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.MnO2                       , 2 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MT.MnO2, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MTx.CoO                       , 4 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MTx.CoO, 8));
            RMx.sintering.addRecipeX(true, 16, 96, ST.array(ST.tag(3), dust.mat(MTx.Co3O4                     , 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)), sinter.mat(MTx.Co3O4, 24));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MTx.Sb2O3                     , 5 ), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)), sinter.mat(MTx.Sb2O3, 8));
            RMx.sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(2), dust.mat(MT.SiO2                       , 6 ), ST.mul(1, coal)), sinter.mat(MT.SiO2, 8));
        }

        // mixing from/to molten ferrochrome and steel
        for (String tIron : new String[] {"molten.iron", "molten.wroughtiron", "molten.meteoriciron", "molten.steel"}) {
            RM.Mixer.addRecipe1(true, 16, 2 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.gold",    1)), FL.make_("molten.angmallen", 2), ZL_IS);
            RM.Mixer.addRecipe1(true, 16, 2 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.tin",    1)), FL.make_("molten.tinalloy", 2), ZL_IS);
            RM.Mixer.addRecipe1(true, 16, 3 , ST.tag(2), FL.array(FL.make_(tIron, 2 ), FL.make_("molten.nickel",    1)), FL.make_("molten.invar", 3), ZL_IS);
            RM.Mixer.addRecipe1(true, 16, 3 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.chromium",    2)), FL.make_("molten.ferrochrome", 3), ZL_IS);
            RM.Mixer.addRecipe1(true, 16, 3 , ST.tag(3), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.chromium", 1), FL.make_("molten.aluminium" , 1)), FL.make("molten.kanthal", 3), ZL_IS);
            RM.Mixer.addRecipe1(true, 16, 6 , ST.tag(3), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.ferrochrome", 3), FL.make_("molten.aluminium" , 2)), FL.make("molten.kanthal", 6), ZL_IS);
        }

        // DRI and Fe3C
        directReduction.addRecipe2(true, 64, 64, ST.tag(0), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(73  * 2 * U25 , true)), FL.array(MT.H2O.liquid(73  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 64, ST.tag(1), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(81 * 2 * U100, true), MT.H.gas(243 * 2 * U100, true)), FL.array(MT.H2O.liquid(243 * 3 * U100, false), MT.CO2.gas(49 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 64, ST.tag(2), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(81 * 2 * U50 , true), MT.H.gas(81  * 2 * U50 , true)), FL.array(MT.H2O.liquid(81  * 3 * U50 , false), MT.CO2.gas(65 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 32, ST.tag(3), dust.mat(MT.Fe2O3, 5), FL.array(MT.CH4.gas(8  * 5 * U15 , true), MT.H.gas(23  * 2 * U15 , true)), FL.array(MT.H2O.liquid(39  * 3 * U15 , false)), dust.mat(MTx.ImpureCementite, 3));

        directReduction.addRecipe2(true, 64, 64, ST.tag(0), dust.mat(MTx.FeO , 4), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(48  * 2 * U25 , true)), FL.array(MT.H2O.liquid(63  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 64, ST.tag(1), dust.mat(MTx.FeO , 4), FL.array(MT.CO .gas(14 * 2 * U25 , true), MT.H.gas(42  * 2 * U25 , true)), FL.array(MT.H2O.liquid(42  * 3 * U25 , false), MT.CO2.gas(6  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 64, ST.tag(2), dust.mat(MTx.FeO , 4), FL.array(MT.CO .gas(28 * 2 * U25 , true), MT.H.gas(28  * 2 * U25 , true)), FL.array(MT.H2O.liquid(28  * 3 * U25 , false), MT.CO2.gas(20 * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        directReduction.addRecipe2(true, 64, 32, ST.tag(3), dust.mat(MTx.FeO , 4), FL.array(MT.CH4.gas(8  * 5 * U15 , true), MT.H.gas(8   * 2 * U15 , true)), FL.array(MT.H2O.liquid(24  * 3 * U15 , false)), dust.mat(MTx.ImpureCementite, 3));

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

        RM.Compressor.addRecipe1(true, 16, 32, dust.mat(MTx.SpongeIron, 1), ingot.mat(MTx.HBI, 1));

        // BOP
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), dustTiny.mat(MT.Steel         , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(209*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(274*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), scrapGt .mat(MT.Steel         , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(209*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(274*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), nugget  .mat(MT.Steel         , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(209*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(274*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), dustTiny.mat(MT.Steel         , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(209*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(274*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), scrapGt .mat(MT.Steel         , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(209*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(274*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), nugget  .mat(MT.Steel         , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(209*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(274*U18/10, false)));

        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), dustTiny.mat(MT.Fe            , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(270*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), scrapGt .mat(MT.Fe            , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(270*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), nugget  .mat(MT.Fe            , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(270*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), scrapGt .mat(MT.WroughtIron   , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(270*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), nugget  .mat(MT.WroughtIron   , 20)), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(10*U, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(270*U9 /10, false)));

        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), dustTiny.mat(MT.Fe            , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(207*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(270*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), scrapGt .mat(MT.Fe            , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(207*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(270*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), nugget  .mat(MT.Fe            , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(207*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(270*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), scrapGt .mat(MT.WroughtIron   , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(207*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(270*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), nugget  .mat(MT.WroughtIron   , 10)), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(207*U18/10, true)), FL.array(MT.Steel.liquid(5 *U, false), MTx.ConverterSlag.liquid(2*U, false), MT.CO.gas(270*U18/10, false)));

        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), dust    .mat(MTx.ConverterSlag, 4 )), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(209*U9 /10, true)), FL.array(MT.Steel.liquid(70*U9, false), MTx.ConverterSlag.liquid(8*U, false), MT.CO.gas(274*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 1), dustSmall.mat(MT.Quicklime, 6), ingot   .mat(MTx.ConverterSlag, 4 )), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(209*U9 /10, true)), FL.array(MT.Steel.liquid(70*U9, false), MTx.ConverterSlag.liquid(8*U, false), MT.CO.gas(274*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), dust    .mat(MTx.ConverterSlag, 2 )), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(209*U18/10, true)), FL.array(MT.Steel.liquid(35*U9, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(274*U18/10, false)));
        basicOxygen.addRecipeX(true, 0, 256, ST.array(dust.mat(MT.Quicklime        , 1), dustSmall.mat(MTx.MgO     , 1), ingot   .mat(MTx.ConverterSlag, 2 )), FL.array(MT.PigIron.liquid(4*U, true), MT.O.gas(209*U18/10, true)), FL.array(MT.Steel.liquid(35*U9, false), MTx.ConverterSlag.liquid(4*U, false), MT.CO.gas(274*U18/10, false)));

        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MTx.CalcinedDolomite, 2), dust     .mat(MT.Quicklime, 3), ingot   .mat(MTx.HBI          , 5 )), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(98*U9, false), MTx.ConverterSlag.liquid(8*U, false), MT.CO.gas(364*U9 /10, false)));
        basicOxygen.addRecipeX(true, 0, 512, ST.array(dust.mat(MT.Quicklime        , 4), dust     .mat(MTx.MgO     , 1), ingot   .mat(MTx.HBI          , 5 )), FL.array(MT.PigIron.liquid(8*U, true), MT.O.gas(207*U9 /10, true)), FL.array(MT.Steel.liquid(98*U9, false), MTx.ConverterSlag.liquid(8*U, false), MT.CO.gas(364*U9 /10, false)));

        RM.Centrifuge.addRecipe0(true, 512, 128, new long[]{900, 1200, 3300, 1100, 600}, MTx.ConverterSlag.liquid(U, true), NF, dust.mat(MT.Quicklime, 4), dustSmall.mat(MTx.MgO, 4), dustSmall.mat(MT.OREMATS.Wollastonite, 4), dustSmall.mat(MTx.FeO, 4), dustSmall.mat(MTx.P2O5, 4));

        // EAF steelmaking
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT .PigIron        , 1 *U), OM.stack(MT.Fe         , 19*U) }, MT.Steel.mMeltingPoint       , OM.stack(MT.Steel     , 20*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT .PigIron        , 1 *U), OM.stack(MT.WroughtIron, 19*U) }, MT.Steel.mMeltingPoint       , OM.stack(MT.Steel     , 20*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.Cementite      , 3 *U), OM.stack(MT.Fe         , 2 *U) }, MT.PigIron.mMeltingPoint     , OM.stack(MT.PigIron   , 5 *U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.Cementite      , 3 *U), OM.stack(MT.WroughtIron, 2 *U) }, MT.PigIron.mMeltingPoint     , OM.stack(MT.PigIron   , 5 *U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.ImpureCementite, 30*U), OM.stack(MT.Quicklime  , 15*U), OM.stack(MTx.MgO             , 4*U) }, MTx.FerrousSlag.mMeltingPoint, OM.stack(MTx.Cementite, 16*U), OM.stack(MTx.FeO, 5*U), OM.stack(MTx.ConverterSlag, 13*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.ImpureCementite, 30*U), OM.stack(MT.Quicklime  , 11*U), OM.stack(MTx.CalcinedDolomite, 8*U) }, MTx.FerrousSlag.mMeltingPoint, OM.stack(MTx.Cementite, 16*U), OM.stack(MTx.FeO, 5*U), OM.stack(MTx.ConverterSlag, 13*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.SpongeIron     , 30*U), OM.stack(MT.Quicklime  , 15*U), OM.stack(MTx.MgO             , 4*U) }, MTx.FerrousSlag.mMeltingPoint, OM.stack(MT.PigIron   , 16*U), OM.stack(MTx.FeO, 5*U), OM.stack(MTx.ConverterSlag, 13*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.SpongeIron     , 30*U), OM.stack(MT.Quicklime  , 11*U), OM.stack(MTx.CalcinedDolomite, 8*U) }, MTx.FerrousSlag.mMeltingPoint, OM.stack(MT.PigIron   , 16*U), OM.stack(MTx.FeO, 5*U), OM.stack(MTx.ConverterSlag, 13*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.FeO            , 2 *U), OM.stack(MTx.Cementite , 3 *U) }, MTx.FeO.mMeltingPoint        , OM.stack(MT.Fe        , 4 *U), OM.stack(MT.CO, 2*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.FeO            , 2 *U), OM.stack(MT.PigIron    , 5 *U) }, MTx.FeO.mMeltingPoint        , OM.stack(MT.Fe        , 6 *U), OM.stack(MT.CO, 2*U));

        // Aluminothermic reduction in EAF
        //TODO decide what to do with bath recipes
        new EAFSmeltingRecipe(0, 10, new OreDictMaterialStack[]{ OM.stack(MT.Al, 10*U), OM.stack(MT .V2O5 , 21*U) }, 600, OM.stack(MT.V , 6*U), OM.stack(MT.Al2O3, 25*U));
        new EAFSmeltingRecipe(0, 10, new OreDictMaterialStack[]{ OM.stack(MT.Al, 10*U), OM.stack(MT .Nb2O5, 21*U) }, 600, OM.stack(MT.Nb, 6*U), OM.stack(MT.Al2O3, 25*U));
        new EAFSmeltingRecipe(0, 10, new OreDictMaterialStack[]{ OM.stack(MT.Al, 10*U), OM.stack(MT .Ta2O5, 21*U) }, 600, OM.stack(MT.Ta, 6*U), OM.stack(MT.Al2O3, 25*U));
        new EAFSmeltingRecipe(0, 2, new OreDictMaterialStack[]{ OM.stack(MT.Al, 2 *U), OM.stack(MTx.Cr2O3, 5 *U) }, 600, OM.stack(MT.Cr, 2*U), OM.stack(MT.Al2O3, 5 *U));
        new EAFSmeltingRecipe(0, 2, new OreDictMaterialStack[]{ OM.stack(MT.Al, 2 *U), OM.stack(MTx.MoO3 , 4 *U) }, 600, OM.stack(MT.Mo, 1*U), OM.stack(MT.Al2O3, 5 *U));
        new EAFSmeltingRecipe(0, 2, new OreDictMaterialStack[]{ OM.stack(MT.Al, 2 *U), OM.stack(MT .WO3  , 4 *U) }, 600, OM.stack(MT.W , 1*U), OM.stack(MT.Al2O3, 5 *U));
        new EAFSmeltingRecipe(0, 4, new OreDictMaterialStack[]{ OM.stack(MT.Al, 4 *U), OM.stack(MT .MnO2 , 3 *U) }, 600, OM.stack(MT.Mn, 3*U), OM.stack(MT.Al2O3, 10*U));
        new EAFSmeltingRecipe(0, 2, new OreDictMaterialStack[]{ OM.stack(MT.Al, 2 *U), OM.stack(MT .Fe2O3, 5 *U) }, 600, OM.stack(MT.Fe, 2*U), OM.stack(MT.Al2O3, 5 *U));
        new EAFSmeltingRecipe(0, 8, new OreDictMaterialStack[]{ OM.stack(MT.Al, 8 *U), OM.stack(MTx.Co3O4, 21*U) }, 600, OM.stack(MT.Co, 9*U), OM.stack(MT.Al2O3, 20*U));

        // Calcinothermic reduction in EAF
        new EAFSmeltingRecipe(0, 5, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 5*U), OM.stack(MT .V2O5 , 7*U) }, 600, OM.stack(MT.V , 2*U), OM.stack(MT.Quicklime, 10*U));
        new EAFSmeltingRecipe(0, 5, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 5*U), OM.stack(MT .Nb2O5, 7*U) }, 600, OM.stack(MT.Nb, 2*U), OM.stack(MT.Quicklime, 10*U));
        new EAFSmeltingRecipe(0, 5, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 5*U), OM.stack(MT .Ta2O5, 7*U) }, 600, OM.stack(MT.Ta, 2*U), OM.stack(MT.Quicklime, 10*U));
        new EAFSmeltingRecipe(0, 3, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 3*U), OM.stack(MTx.Cr2O3, 5*U) }, 600, OM.stack(MT.Cr, 2*U), OM.stack(MT.Quicklime, 6 *U));
        new EAFSmeltingRecipe(0, 3, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 3*U), OM.stack(MTx.MoO3 , 4*U) }, 600, OM.stack(MT.Mo, 1*U), OM.stack(MT.Quicklime, 6 *U));
        new EAFSmeltingRecipe(0, 3, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 3*U), OM.stack(MT .WO3  , 4*U) }, 600, OM.stack(MT.W , 1*U), OM.stack(MT.Quicklime, 6 *U));
        new EAFSmeltingRecipe(0, 2, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 2*U), OM.stack(MT .MnO2 , 3*U) }, 600, OM.stack(MT.Mn, 1*U), OM.stack(MT.Quicklime, 4 *U));
        new EAFSmeltingRecipe(0, 3, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 3*U), OM.stack(MT .Fe2O3, 5*U) }, 600, OM.stack(MT.Fe, 2*U), OM.stack(MT.Quicklime, 6 *U));
        new EAFSmeltingRecipe(0, 4, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 4*U), OM.stack(MTx.Co3O4, 7*U) }, 600, OM.stack(MT.Co, 3*U), OM.stack(MT.Quicklime, 8 *U));
        new EAFSmeltingRecipe(0, 3, new OreDictMaterialStack[]{ OM.stack(MT.Ca, 3*U), OM.stack(MTx.ScF3 , 8*U) }, 600, OM.stack(MT.Sc, 2*U), OM.stack(MT.CaF2     , 9 *U));

        // Pure phosphorus production in EAF
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT.Apatite, 2*7*U), OM.stack(MT.SiO2, 9*U), OM.stack(MT.C, 5*U) }, 1850, OM.stack(MTx.Slag, 3*5*U), OM.stack(MT.CaCl2, U), OM.stack(MTx.P_CO_Gas, 12*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT.Phosphorite, 2*7*U), OM.stack(MT.SiO2, 3*3*U), OM.stack(MT.C, 5*U) }, 1850, OM.stack(MTx.Slag, 3*5*U), OM.stack(MT.CaF2, U), OM.stack(MTx.P_CO_Gas, 12*U));
        for (OreDictMaterial phosphorus : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusRed, MT.PhosphorusWhite}) {
            new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(phosphorus, 13*U), OM.stack(MT.SiO2, 3*3*U), OM.stack(MT.C, 5*U) }, 1850, OM.stack(MTx.Slag, 3*5*U), OM.stack(MTx.P_CO_Gas, 12*U));
        }
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MTx.P2O5, 7*U), OM.stack(MT.C, 5*U) }, 1850, OM.stack(MTx.P_CO_Gas, 12*U));

        // HSS-T1
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT.PigIron, 16*U), OM.stack(MT.Steel, 60*U), OM.stack(MT.W, 6*U), OM.stack(MT.Cr, 4*U), OM.stack(MT.VanadiumSteel, 5*U)  }, MTx.HSST1.mMeltingPoint, OM.stack(MTx.HSST1, 91*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT.PigIron, 16*U), OM.stack(MT.Steel, 58*U), OM.stack(MT.W, 6*U), OM.stack(MTx.FeCr2, 6*U), OM.stack(MT.VanadiumSteel, 5*U)  }, MTx.HSST1.mMeltingPoint, OM.stack(MTx.HSST1, 91*U));
        // HSS-M2
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT.PigIron, 8 *U), OM.stack(MT.Steel, 28*U), OM.stack(MT.W, U), OM.stack(MT.Cr, 2*U), OM.stack(MT.VanadiumSteel, 5*U), OM.stack(MT.Mo, U)  }, MTx.HSSM2.mMeltingPoint, OM.stack(MTx.HSSM2, 45*U));
        new EAFSmeltingRecipe(0, new OreDictMaterialStack[]{ OM.stack(MT.PigIron, 8 *U), OM.stack(MT.Steel, 27*U), OM.stack(MT.W, U), OM.stack(MTx.FeCr2, 3*U), OM.stack(MT.VanadiumSteel, 5*U), OM.stack(MT.Mo, U)  }, MTx.HSSM2.mMeltingPoint, OM.stack(MTx.HSSM2, 45*U));
    }

    private void addOverrideRecipes() {
        RM.Centrifuge.addRecipe0(true, 64, 16, new long[]{9640, 100, 100, 100, 100, 100}, FL.Sluice.make(100), FL.Water.make(50), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Cu, 2), dustTiny.mat(MT.OREMATS.Cassiterite, 1), dustTiny.mat(MTx.ZnO, 1), dustTiny.mat(MTx.Sb2O3, 2), dustTiny.mat(MT.OREMATS.Chromite, 3));
        RM.MagneticSeparator.addRecipe1(true, 16, 16, new long[]{9640, 72, 72, 72, 72, 72}, dustTiny.mat(MT.SluiceSand, 1), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(true, 16, 36, new long[]{9640, 162, 162, 162, 162, 162}, dustSmall.mat(MT.SluiceSand, 1), dustSmall.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{9640, 648, 648, 648, 648, 648}, dust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(true, 16, 1296, new long[]{9640, 5832, 5832, 5832, 5832, 5832}, blockDust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 9), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));

        // Roasting
        for (String tOxygen : FluidsGT.OXYGEN)
            if (FL.exists(tOxygen)) {
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Realgar), FL.make(tOxygen, 1750), MT.SO2.gas(3 * U2, false), OM.dust(MTx.As2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stibnite), FL.make(tOxygen, 1800), MT.SO2.gas(9 * U5, false), OM.dust(MTx.Sb2O3, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Tetrahedrite), FL.make(tOxygen, 1125), MT.SO2.gas(9 * U8, false), OM.dust(MT.Cu, 3 * U8), OM.dust(MTx.Sb2O3, 5 * U16), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Arsenopyrite), FL.make(tOxygen, 1667), MT.SO2.gas(3 * U3, false), OM.dust(MT.Fe2O3, 5 * U6), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MTx.CdS), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.CdO, U2));
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

        for (String tAir : FluidsGT.AIR) {
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
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.Magnetite, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15 * U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15 * U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.BasalticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15 * U2));
            }
        }

        // Advanced Bayer
        // 2*Bauxite is interpreted as AlO(OH), so the equation is AlO(OH) + (Na,K)OH -> (Na,K)AlO2 + H2O
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dust     .mat(MT.OREMATS.Bauxite, 1), OP.dustSmall.mat(MT.KOH , 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.KAlO2 , 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustSmall.mat(MT.OREMATS.Bauxite, 4), OP.dustSmall.mat(MT.KOH , 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.KAlO2 , 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustTiny .mat(MT.OREMATS.Bauxite, 9), OP.dustSmall.mat(MT.KOH , 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.KAlO2 , 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dust     .mat(MT.OREMATS.Bauxite, 2), OP.dust     .mat(MT.KOH , 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.KAlO2 , 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustSmall.mat(MT.OREMATS.Bauxite, 8), OP.dust     .mat(MT.KOH , 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.KAlO2 , 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustTiny .mat(MT.OREMATS.Bauxite,18), OP.dust     .mat(MT.KOH , 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.KAlO2 , 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dust     .mat(MT.OREMATS.Bauxite, 1), OP.dustSmall.mat(MT.NaOH, 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.NaAlO2, 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustSmall.mat(MT.OREMATS.Bauxite, 4), OP.dustSmall.mat(MT.NaOH, 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.NaAlO2, 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustTiny .mat(MT.OREMATS.Bauxite, 9), OP.dustSmall.mat(MT.NaOH, 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.NaAlO2, 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dust     .mat(MT.OREMATS.Bauxite, 2), OP.dust     .mat(MT.NaOH, 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.NaAlO2, 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustSmall.mat(MT.OREMATS.Bauxite, 8), OP.dust     .mat(MT.NaOH, 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.NaAlO2, 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustTiny .mat(MT.OREMATS.Bauxite,18), OP.dust     .mat(MT.NaOH, 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.NaAlO2, 4), crushedCentrifuged.mat(MTx.RedMud, 1));

        for (FluidStack water : FL.waters(1000)) {
            RM.Bath.addRecipe1(true, 0, 2048, OP.dust.mat(MT.KAlO2, 4), FL.mul(water, 6), NF, OP.dust.mat(MT.AlO3H3, 4), OP.dust.mat(MT.AlO3H3, 3), OP.dust.mat(MT.KOH, 3));
            RM.Bath.addRecipe1(true, 0, 2048, OP.dust.mat(MT.NaAlO2, 4), FL.mul(water, 9), MTx.BayerLiquor.liquid(6*U, false), OP.dust.mat(MT.AlO3H3, 7));
        }

        RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate   , 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.MnO2, 6), nugget.mat(MT.OREMATS.Cassiterite, 12), nugget.mat(MTx.ZnO, 3));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate   , 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.MnO2, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 6));
        RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate   , 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 3));
        RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro      , 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro      , 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
        RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro      , 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Ag, 6), nugget.mat(MTx.PbO, 12), nugget.mat(MTx.Co3O4, 7));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Ag, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 14));
        RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 7));
        if (IL.PFAA_Sands.exists()) {
            RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt , 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Au, 8), nugget.mat(MT.Cu, 16), nugget.mat(MT.OREMATS.Garnierite, 4));
            RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt , 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Au, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 8));
            RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt , 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 4));
            RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Ag, 8), nugget.mat(MTx.PbO, 16), nugget.mat(MTx.Co3O4, 9));
            RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Ag, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 18));
            RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 9));
        }
        if (IL.TROPIC_Sand_Black.exists()) {
            RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
            RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
            RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
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
            RM.Electrolyzer.addRecipe1(true, 64, 128, ST.tag(0), FL.array(MTx.CdSO4Solution.liquid(6 * U, true), tWater), FL.array(MT.H2SO4.liquid(7 * U, false), MT.O.gas(U, false)), dust.mat(MT.Cd, 1));
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
                , ST.make(gem.mat(MT.Charcoal, 6), "Add some coke.")
                , IL.Ceramic_Crucible.getWithName(1, "Heat up the crucible using a burning box and wait until it all turns into sponge iron")
                , ST.make(scrapGt.mat(MTx.SpongeIron, 1), "Get the bloom out with a shovel")
                , ST.make(MTEx.gt6Registry.getItem(32028), "Get rid of slag and excess carbon by hammering the sponge iron scrap on any anvil to make wrought iron")
        ), ST.array(nugget.mat(MT.WroughtIron, 1), ingot.mat(MT.WroughtIron, 1), plate.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.FerrousSlag, 1), stick.mat(MT.WroughtIron, 1), gearGtSmall.mat(MT.WroughtIron, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                ST.make(ingot.mat(MT.PigIron, 1), "Throw some Pig Iron into a crucible. Do not forget to leave space for air!")
                , ST.make(dust.mat(MT.CaCO3, 1), "Add some lime.")
                , ST.make(MTEx.gt6Registry.getItem(1199), "Heat up the crucible using a Burning Box")
                , ST.make(MTEx.gt6Registry.getItem(1302), "Point a running engine into the crucible to blow air")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it all turns into Steel and pour it into a mold")
                , ST.make(MTEx.gt6xMTEReg.getItem(MTEx.IDs.BOF.get()), "Build a Basic Oxygen Converter if you want this process to be more efficient")
        ), ST.array(dust.mat(MT.Steel, 1), ingot.mat(MT.Steel, 1), plate.mat(MT.Steel, 1), scrapGt.mat(MT.Steel, 1), stick.mat(MT.Steel, 1), gearGt.mat(MT.Steel, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(F, ST.array(
                ST.make(dust.mat(MT.OREMATS.Cinnabar, 3), "Throw three units of Cinnabar into a crucible")
                , ST.make(dust.mat(MTx.HgO, 2), "Or two units of Mercuric Oxide produced by roasting the cinnabar first!")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it melts into Mercury")
                , IL.Bottle_Empty.getWithName(1, "Rightclick the crucible with an empty bottle")
                , ST.make(MTEx.gt6Registry.getItem(1199), "Heat up the crucible using a Burning Box")
                , ST.make(Blocks.redstone_ore, 1, 0, "Using a Club to mine vanilla Redstone Ore gives Cinnabar")
        ), ST.array(IL.Bottle_Mercury.get(1), ST.make(ingot.mat(MT.Hg, 1), "Pouring this into molds only works with additional cooling!"), ST.make(nugget.mat(MT.Hg, 1), "Pouring this into molds only works with additional cooling!")), null, ZL_LONG, FL.array(MT.Hg.liquid(1, T)), FL.array(MT.Hg.liquid(1, T)), 0, 0, 0);
    }
}
