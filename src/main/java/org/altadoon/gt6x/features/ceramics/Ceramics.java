package org.altadoon.gt6x.features.ceramics;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.oredict.configurations.OreDictConfigurationComponent;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerPrefixShredding;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.tileentity.tools.MultiTileEntityBasin;
import gregtech.tileentity.tools.MultiTileEntityCrossing;
import gregtech.tileentity.tools.MultiTileEntityFaucet;
import gregtech.tileentity.tools.MultiTileEntityMold;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.ceramics.crucibles.MultiTileEntityCrucibleX;
import org.altadoon.gt6x.features.ceramics.crucibles.MultiTileEntitySmelteryX;

import java.util.ArrayList;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.OP.dust;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Ceramics extends GT6XFeature {
    public OreDictPrefix clinker = null;

    @Override public String name() { return "Ceramics"; }
    @Override public void configure(Config config) {}

    @Override
    public void preInit() {
        createPrefixes();
        addAlloyingRecipes();
    }

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void beforePostInit() {
        changeCraftingRecipes();
    }

    @Override
    public void postInit() {
        changePrefixNames();
        addRecipes();
    }

    @Override
    public void afterPostInit() {
        changeRecipes();
    }

    private void createPrefixes() {
        clinker = OreDictPrefix.createPrefix("clinker")
                .setCategoryName("Clinkers")
                .setLocalItemName("", " Clinker")
                .setMaterialStats(U)
                .add(TD.Prefix.RECYCLABLE)
                .setCondition(ICondition.FALSE)
                .forceItemGeneration(MTx.Cement, MTx.CaAlCement);
        PrefixItem item = new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.clinker" , clinker); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("builder", ST.make(item, 1, W));
    }

    private void addAlloyingRecipes() {
        MT.SiC.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.SiO2, 3*U), OM.stack(MT.C, 2*U)));
        MTx.Cement.addAlloyingRecipe(new OreDictConfigurationComponent(4, OM.stack(MT.Gypsum, U9), OM.stack(MT.Quicklime, 5*U), OM.stack(MT.Ash, 2*U)));
        MTx.Cement.addAlloyingRecipe(new OreDictConfigurationComponent(4, OM.stack(MT.Gypsum, U9), OM.stack(MT.Quicklime, 5*U), OM.stack(MT.DarkAsh, 2*U)));
        MTx.Cement.addAlloyingRecipe(new OreDictConfigurationComponent(4, OM.stack(MT.Gypsum, U9), OM.stack(MT.Quicklime, 4*U), OM.stack(MT.STONES.Shale, 3*U)));
    }

    private void changePrefixNames() {
        LH.add("oredict." + ingot.dat(MTx.Firebrick) + ".name", "Fire Brick");
        LH.add("oredict." + ingot.dat(MTx.MgOC) + ".name", MTx.MgOC.getLocal() + " Brick");
        LH.add("oredict." + dust.dat(MTx.Mortar) + ".name", MTx.Mortar.getLocal());
        LH.add("oredict." + dust.dat(MTx.RefractoryMortar) + ".name", MTx.RefractoryMortar.getLocal());
    }

    private void addMTEs() {
        OreDictMaterial mat;
        mat = MT.Al2O3;
        MTEx.gt6xMTEReg.add("Alumina Checker Bricks"                 , "Multiblock Machines", MTEx.IDs.AluminaBricks       .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "checkerbricks"    , NBT_DESIGNS, 1), "CIC", "IrI", "CIC", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));
        MTEx.gt6xMTEReg.add("Alumina Refractory Bricks"              , "Multiblock Machines", MTEx.IDs.AluminaCheckerBricks.get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "refractorybricks" , NBT_DESIGNS, 1), "ICI", "CrC", "ICI", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));

        mat = MT.SiC;
        MTEx.gt6xMTEReg.add("Silicon Carbide Refractory Bricks"      , "Multiblock Machines", MTEx.IDs.SiCBricks           .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "refractorybricks" , NBT_DESIGNS, 1), "ICI", "CrC", "ICI", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));
        MTEx.gt6xMTEReg.add(mat.getLocal()+" Wall"                   , "Multiblock Machines", MTEx.IDs.SiCWall             .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "metalwall"        , NBT_DESIGNS, 7), "wPP", "hPP", 'P', OP.plate.dat(mat)); RM.Welder.addRecipe2(F, 16, 256, OP.plate.mat(mat, 4), ST.tag(10), MTEx.gt6xMTEReg.getItem());
        MTEx.gt6xMTEReg.add("Large "+mat.getLocal()+" Crucible"      , "Multiblock Machines", MTEx.IDs.SiCCrucibleLarge    .get(), 17101, MultiTileEntityCrucibleX.class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "crucible"         , NBT_DESIGN, MTEx.IDs.SiCWall.get(), NBT_ACIDPROOF, false), "hMy", 'M', MTEx.gt6xMTEReg.getItem(MTEx.IDs.SiCWall.get()));
        MTEx.gt6xMTEReg.add("Smelting Crucible ("+mat.getLocal()+")" , "Smelting Crucibles" , MTEx.IDs.SiCCrucible         .get(), 1022 , MultiTileEntitySmelteryX.class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_RECIPEMAP, RM.CrucibleAlloying , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ACIDPROOF, false, NBT_HIDDEN, false), "BhB", "ByB", "BBB", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Crucible Faucet ("+mat.getLocal()+")"   , "Crucibles Faucets"  , MTEx.IDs.SicFaucet           .get(), 1722 , MultiTileEntityFaucet   .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_ACIDPROOF, false, NBT_HIDDEN, false), "h y", "B B", " B ", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Mold ("+mat.getLocal()+")"              , "Molds"              , MTEx.IDs.SiCMold             .get(), 1072 , MultiTileEntityMold     .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_ACIDPROOF, false, NBT_HIDDEN, false), "h y", "B B", "BBB", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Basin ("+mat.getLocal()+")"             , "Molds"              , MTEx.IDs.SiCBasin            .get(), 1072 , MultiTileEntityBasin    .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_ACIDPROOF, false, NBT_HIDDEN, false), "BhB", "ByB", " B ", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Crucible Crossing ("+mat.getLocal()+")" , "Molds"              , MTEx.IDs.SiCCrossing         .get(), 1072 , MultiTileEntityCrossing .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_ACIDPROOF, false, NBT_HIDDEN, false), "hBy", "BBB", " B ", 'B', plate.dat(mat));

        mat = MTx.MgOC;
        MTEx.gt6xMTEReg.add("MgO-C Refractory Bricks"                , "Multiblock Machines", MTEx.IDs.MgOCBricks          .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "refractorybricks" , NBT_DESIGNS, 1), "ICI", "CrC", "ICI", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));
        MTEx.gt6xMTEReg.add(mat.getLocal()+" Wall"                   , "Multiblock Machines", MTEx.IDs.MgOCWall            .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "metalwall"        , NBT_DESIGNS, 7), "wPP", "hPP", 'P', OP.plate.dat(mat)); RM.Welder.addRecipe2(F, 16, 256, OP.plate.mat(mat, 4), ST.tag(10), MTEx.gt6xMTEReg.getItem());
        MTEx.gt6xMTEReg.add("Large "+mat.getLocal()+" Crucible"      , "Multiblock Machines", MTEx.IDs.MgOCCrucibleLarge   .get(), 17101, MultiTileEntityCrucibleX     .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "crucible"         , NBT_DESIGN, MTEx.IDs.MgOCWall.get(), NBT_ACIDPROOF, true), "hMy", 'M', MTEx.gt6xMTEReg.getItem(MTEx.IDs.MgOCWall.get()));
        MTEx.gt6xMTEReg.add("Smelting Crucible ("+mat.getLocal()+")" , "Smelting Crucibles" , MTEx.IDs.MgOCCrucible        .get(), 1022 , MultiTileEntitySmelteryX     .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_RECIPEMAP, RM.CrucibleAlloying , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ACIDPROOF, true , NBT_HIDDEN, false), "BhB", "ByB", "BBB", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Crucible Faucet ("+mat.getLocal()+")"   , "Crucibles Faucets"  , MTEx.IDs.MgOCFaucet          .get(), 1722 , MultiTileEntityFaucet        .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_ACIDPROOF, true , NBT_HIDDEN, false), "h y", "B B", " B ", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Mold ("+mat.getLocal()+")"              , "Molds"              , MTEx.IDs.MgOCMold            .get(), 1072 , MultiTileEntityMold          .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_ACIDPROOF, true , NBT_HIDDEN, false), "h y", "B B", "BBB", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Basin ("+mat.getLocal()+")"             , "Molds"              , MTEx.IDs.MgOCBasin           .get(), 1072 , MultiTileEntityBasin         .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_ACIDPROOF, true , NBT_HIDDEN, false), "BhB", "ByB", " B ", 'B', plate.dat(mat));
        MTEx.gt6xMTEReg.add("Crucible Crossing ("+mat.getLocal()+")" , "Molds"              , MTEx.IDs.MgOCCrossing        .get(), 1072 , MultiTileEntityCrossing      .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_ACIDPROOF, true , NBT_HIDDEN, false), "hBy", "BBB", " B ", 'B', plate.dat(mat));

        IL.Ceramic_Crucible.set(MTEx.gt6Registry.getItem(1005), new OreDictItemData(MTx.RefractoryCeramic, U*7));
    }

    private void addRecipes() {
        // Fire clay
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RM.Mixer.addRecipe2(true, 16, 192, dust.mat(clay, 2), dust.mat(MT.Graphite, 1), dust.mat(MTx.Fireclay, 3));
        }
        for (FluidStack water : FL.waters(125, 100)) {
            for (ItemStack clay : ST.array(ST.make(Items.clay_ball, 2, 0), IL.Clay_Ball_Blue.get(2), IL.Clay_Ball_Brown.get(2), IL.Clay_Ball_Red.get(2), IL.Clay_Ball_White.get(2), IL.Clay_Ball_Yellow.get(2))) {
                RM.Mixer.addRecipe2(true, 16, 192, clay, dust.mat(MT.Graphite, 1), FL.mul(water, 5), NF, ILx.Fireclay_Ball.get(3));
            }
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.RefractoryCeramic, 1), FL.mul(water, 5), NF, ILx.Fireclay_Ball.get(1));
            RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.Fireclay, 1), water, NF, ILx.Fireclay_Ball.get(1));
        }
        RM.Furnace.addRecipe1(true, 16, 64, dust.mat(MTx.Fireclay, 1), ingot.mat(MTx.Firebrick, 1));

        // Magnesia-Carbon
        RMx.sintering.addRecipeX(true, 64, 256, ST.array(ST.tag(2), dust.mat(MTx.MgO, 1), dust.mat(MT.Graphite, 1)), ingot.mat(MTx.MgOC, 2));

        // Refractory Cement
        RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(2), dust.mat(MT.Quicklime, 1), dust.mat(MT.Al2O3, 5)), clinker.mat(MTx.CaAlCement, 6));

        // Portland Cement
        RMx.sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MT.Quicklime, 5), dust.mat(MT.Ash            , 2)), clinker.mat(MTx.Cement, 7));
        RMx.sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MT.Quicklime, 5), dust.mat(MT.DarkAsh        , 2)), clinker.mat(MTx.Cement, 7));
        RMx.sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MT.Quicklime, 5), dust.mat(MT.OREMATS.Bauxite, 2)), clinker.mat(MTx.Cement, 7));
        RMx.sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MT.Quicklime, 4), dust.mat(MT.STONES.Shale   , 3)), clinker.mat(MTx.Cement, 7));
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RMx.sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MT.Quicklime, 5), dust.mat(clay          , 2)), clinker.mat(MTx.Cement, 7));
        }
        for (OreDictMaterial calcite : ANY.Calcite.mToThis) {
            RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(MT.Ash            , 4)), clinker.mat(MTx.Cement, 14));
            RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(MT.DarkAsh        , 4)), clinker.mat(MTx.Cement, 14));
            RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(MT.OREMATS.Bauxite, 4)), clinker.mat(MTx.Cement, 14));
            RMx.sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(calcite, 10), dust.mat(MT.STONES.Shale   , 3)), clinker.mat(MTx.Cement, 7));
            for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(clay              , 4)), clinker.mat(MTx.Cement, 14));
            }
        }

        // GGBFS Cement
        RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MT.Quicklime, 5), dust.mat(MT.Ash            , 2), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MT.Quicklime, 5), dust.mat(MT.DarkAsh        , 2), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MT.Quicklime, 5), dust.mat(MT.OREMATS.Bauxite, 2), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MT.Quicklime, 4), dust.mat(MT.STONES.Shale   , 3), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MT.Quicklime, 5), dust.mat(clay          , 2), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        }
        for (OreDictMaterial calcite : ANY.Calcite.mToThis) {
            RMx.sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(MT.Ash            , 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
            RMx.sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(MT.DarkAsh        , 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
            RMx.sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(MT.OREMATS.Bauxite, 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
            RMx.sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 10), dust.mat(MT.STONES.Shale   , 3), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
            for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RMx.sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(clay              , 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
            }
        }

        // grinding clinkers
        RM.Mortar.add(new RecipeMapHandlerPrefixShredding(clinker, 1, NF, 16, 0, 0, NF, dust , 1, NI, NI, true, false, false, null));

        // Concrete
        for (OreDictMaterial stone : ANY.Stone.mToThis) if (stone != MT.Concrete && stone != MTx.Cement && !stone.mReRegistrations.contains(ANY.Calcite)) for (FluidStack tWater : FL.waters(1000)) {
            RM.Mixer.addRecipe2(true, 16, 144, blockDust.mat(stone, 5), blockDust.mat(MTx.Cement, 1), FL.mul(tWater, 9, 2, true), FL.Concrete.make(6 * 9 * L), ZL_IS);
            RM.Mixer.addRecipe2(true, 16, 16 ,      dust.mat(stone, 5), dust     .mat(MTx.Cement, 1), FL.mul(tWater, 1, 2, true), FL.Concrete.make(6 * L), ZL_IS);
        }

        // Mortar
        for (FluidStack tWater : FL.waters(1000)) {
            for (OreDictMaterial sand : ANY.SiO2.mToThis) {
                RM.Mixer.addRecipe2(true, 16, 144, OM.dust(sand, 27 * U), blockDust.mat(MTx.Cement    , 1), FL.mul(tWater, 9, 2, true), NF, blockDust.mat(MTx.Mortar          , 5));
                RM.Mixer.addRecipe2(true, 16, 16 , OM.dust(sand, 3  * U), dust     .mat(MTx.Cement    , 1), FL.mul(tWater, 1, 2, true), NF, dust     .mat(MTx.Mortar          , 5));
                RM.Mixer.addRecipeX(true, 16, 144, ST.array(OM.dust(sand, 27 * U), blockDust.mat(MTx.CaAlCement, 1), dust    .mat(MT.Kaolinite, 1)), FL.mul(tWater, 9, 2, true), NF, blockDust.mat(MTx.RefractoryMortar, 5));
                RM.Mixer.addRecipeX(true, 16, 16 , ST.array(OM.dust(sand, 3  * U), dust     .mat(MTx.CaAlCement, 1), dustTiny.mat(MT.Kaolinite, 1)), FL.mul(tWater, 1, 2, true), NF, dust     .mat(MTx.RefractoryMortar, 5));
            }
            RM.Mixer.addRecipe2(true, 16, 144, ST.make(Blocks.sand, 3, 0), blockDust.mat(MTx.Cement    , 1), FL.mul(tWater, 9, 2, true), NF, blockDust.mat(MTx.Mortar          , 5));
            RM.Mixer.addRecipeX(true, 16, 144, ST.array(ST.make(Blocks.sand, 3, 0), blockDust.mat(MTx.CaAlCement, 1), dust.mat(MT.Kaolinite, 1), dust    .mat(MT.Gypsum, 1)), FL.mul(tWater, 9, 2, true), NF, blockDust.mat(MTx.RefractoryMortar, 5));
        }
    }

    private void changeClayCruciblePart(IL raw_part, IL baked_part, long clayCount, String recipeA, String recipeB, String recipeC) {
        OreDictManager.INSTANCE.setItemData(raw_part.get(1), new OreDictItemData(MTx.Fireclay, U*clayCount));
        OreDictManager.INSTANCE.setItemData(baked_part.get(1), new OreDictItemData(MTx.RefractoryCeramic, U*clayCount));
        CRx.overrideGT6ShapedCraftingRecipe(raw_part.get(1), recipeA, recipeB, recipeC, 'C', ILx.Fireclay_Ball.get(1), 'R', OreDictToolNames.rollingpin);
        CRx.overrideGT6SingleShapelessCraftingRecipe(raw_part.get(1), ILx.Fireclay_Ball.get(clayCount));
    }

    private void changeCraftingRecipes() {
        // Masonry
        CR.shaped(ST.make(Blocks.brick_block, 1, 0), CR.DEF_REM, "BMB", "M M", "BMB", 'B', ST.make(Items.brick, 1, 0), 'M', OM.dust(MTx.Mortar));
        CR.shaped(ST.make(Blocks.nether_brick, 1, 0), CR.DEF_REM, "BMB", "M M", "BMB", 'B', ST.make(Items.netherbrick, 1, 0), 'M', OM.dust(MTx.Mortar));

        CRx.overrideGT6ShapedCraftingRecipe(MTEx.gt6Registry.getItem(1199), "BBB", "BBB", "BFB", 'B', OP.ingot.dat(MTx.Firebrick), 'F', OD.craftingFirestarter);
        OreDictManager.INSTANCE.setItemData(MTEx.gt6Registry.getItem(1199), new OreDictItemData(MTx.Firebrick, 8*U));

        CRx.overrideGT6ShapedCraftingRecipe(MTEx.gt6Registry.getItem(18000), "MBM", "B B", "MBM", 'B', ingot.mat(MTx.Firebrick, 1), 'M', OM.dust(MTx.Mortar));
        OreDictManager.INSTANCE.setItemData(MTEx.gt6Registry.getItem(18000), new OreDictItemData(MTx.Firebrick, 4*U));

        // Crucible parts
        changeClayCruciblePart(IL.Ceramic_Tap_Raw, IL.Ceramic_Tap, 3,"CCR", "kC ", "   ");
        changeClayCruciblePart(IL.Ceramic_Funnel_Raw, IL.Ceramic_Funnel, 3,"CRC", "kC ", "   ");
        changeClayCruciblePart(IL.Ceramic_Crucible_Raw, IL.Ceramic_Crucible, 7,"CkC", "CRC", "CCC");
        changeClayCruciblePart(IL.Ceramic_Basin_Raw, IL.Ceramic_Basin, 5,"CkC", "CRC", " C ");
        changeClayCruciblePart(IL.Ceramic_Mold_Raw, IL.Ceramic_Mold, 5,"kCR", "CCC", "   ");
        changeClayCruciblePart(IL.Ceramic_Faucet_Raw, IL.Ceramic_Faucet, 3,"C C", "kCR", "   ");
        changeClayCruciblePart(IL.Ceramic_Crossing_Raw, IL.Ceramic_Crossing, 5,"kCR", "CCC", " C ");

        for (IL mold : new IL[] {
                IL.Ceramic_Ingot_Mold_Raw,
                IL.Ceramic_Chunk_Mold_Raw,
                IL.Ceramic_Plate_Mold_Raw,
                IL.Ceramic_Tiny_Plate_Mold_Raw,
                IL.Ceramic_Bolt_Mold_Raw,
                IL.Ceramic_Rod_Mold_Raw,
                IL.Ceramic_Long_Rod_Mold_Raw,
                IL.Ceramic_Item_Casing_Mold_Raw,
                IL.Ceramic_Ring_Mold_Raw,
                IL.Ceramic_Gear_Mold_Raw,
                IL.Ceramic_Small_Gear_Mold_Raw,
                IL.Ceramic_Sword_Mold_Raw,
                IL.Ceramic_Pickaxe_Mold_Raw,
                IL.Ceramic_Spade_Mold_Raw,
                IL.Ceramic_Shovel_Mold_Raw,
                IL.Ceramic_Universal_Spade_Mold_Raw,
                IL.Ceramic_Axe_Mold_Raw,
                IL.Ceramic_Double_Axe_Mold_Raw,
                IL.Ceramic_Saw_Mold_Raw,
                IL.Ceramic_Hammer_Mold_Raw,
                IL.Ceramic_File_Mold_Raw,
                IL.Ceramic_Screwdriver_Mold_Raw,
                IL.Ceramic_Chisel_Mold_Raw,
                IL.Ceramic_Arrow_Mold_Raw,
                IL.Ceramic_Hoe_Mold_Raw,
                IL.Ceramic_Sense_Mold_Raw,
                IL.Ceramic_Plow_Mold_Raw,
                IL.Ceramic_Builderwand_Mold_Raw,
                IL.Ceramic_Nugget_Mold_Raw,
                IL.Ceramic_Billet_Mold_Raw,
        }) {
            OreDictManager.INSTANCE.setItemData(mold.get(1), new OreDictItemData(MTx.Fireclay, U*5));
            CRx.overrideGT6SingleShapelessCraftingRecipe(mold.get(1), ILx.Fireclay_Ball.get(5));
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mOutputs.length == 1 && r.mOutputs[0] != null && (
                    r.mOutputs[0].isItemEqual(dust.mat(MT.Concrete, 11)) ||
                    r.mOutputs[0].isItemEqual(blockDust.mat(MT.Concrete, 11))
            )) { // need to make concrete from cement dust instead of concrete dust
                r.mEnabled = false;
            }
        }

        Recipe x = RM.Boxinator.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, ST.make(Items.brick, 4, 0), ST.tag(4)); if (x != null) x.mEnabled = false;
               x = RM.Boxinator.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, ST.make(Items.netherbrick, 4, 0), ST.tag(4)); if (x != null) x.mEnabled = false;
    }
}
