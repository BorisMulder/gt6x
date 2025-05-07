package org.altadoon.gt6x.features.ceramics;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerPrefixShredding;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.common.items.MultiItemsX;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.crucibles.recipes.CrucibleUtils;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.OP.dust;
import static gregapi.data.TD.Prefix.*;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Ceramics extends GT6XFeature {
    public OreDictPrefix clinker = null;

    @Override public String name() { return "Ceramics"; }

    @Override
    public void preInit() {
        changeMaterialProperties();
        createPrefixes();
        addAlloyingRecipes();
    }

    @Override
    public void init() {}

    @Override
    public void afterGt6Init() {
        addMTEs();
    }

    @Override
    public void beforeGt6PostInit() {
        changeCraftingRecipes();
        MultiItemsX.instance.addClayItems();
    }

    @Override
    public void postInit() {
        changePrefixNames();
        addRecipes();
    }

    @Override
    public void afterGt6PostInit() {
        changeRecipes();
    }

    private void changeMaterialProperties() {
        MT.Ceramic.heat(MT.Clay);
        MT.Brick.heat(MT.Clay);
    }

    private void createPrefixes() {
        clinker = OreDictPrefix.createPrefix("clinker")
                .setCategoryName("Clinkers")
                .setLocalItemName("", " Clinker")
                .setMaterialStats(U)
                .add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
                .setCondition(ICondition.FALSE)
                .forceItemGeneration(MTx.Cement, MTx.CaAlCement);
        PrefixItem item = new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.clinker" , clinker); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("builder", ST.make(item, 1, W));
    }

    private void addAlloyingRecipes() {
        RMx.SSS.addRecipe(0, OMx.stacks(OM.stack(MT.SiO2, 3*U), OM.stack(MT.C, 2*U)), 1700+C, OM.stack(MT.SiC, U));

        RMx.SSS.addRecipe(0, OMx.stacks(OM.stack(MT.Gypsum, U), OM.stack(MTx.CaO, 4*9*U), OM.stack(MT.STONES.Shale, 3*9*U)), 1450+C, OM.stack(MTx.Cement, 4*9*U+U));
        for (OreDictMaterial ash : ANY.Ash.mToThis)
            RMx.SSS.addRecipe(0, OMx.stacks(OM.stack(MT.Gypsum, U), OM.stack(MTx.CaO, 5*9*U), OM.stack(ash, 2*9*U)), 1450+C, OM.stack(MTx.Cement, 4*9*U+U));
    }

    private void changePrefixNames() {
        LH.add("oredict." + ingot.dat(MTx.Firebrick) + ".name", "Fire Brick");
        LH.add("oredict." + ingot.dat(MTx.MgOC) + ".name", MTx.MgOC.getLocal() + " Brick");
        LH.add("oredict." + dust.dat(MTx.Mortar) + ".name", MTx.Mortar.getLocal());
        LH.add("oredict." + dust.dat(MTx.RefractoryMortar) + ".name", MTx.RefractoryMortar.getLocal());
    }

    private void addMTEs() {
        OreDictMaterial mat = MT.Ceramic;
        MTEx.gt6xMTEReg.add("Mortar", "Misc Tool Blocks", MTEx.IDs.Mortars.get()  , 32720, MultiTileEntityMortarX.class, 0, 16, MTEx.StoneBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   5.0F, NBT_RECIPEMAP, RM.Mortar , NBT_DESIGN, 0), "P", "B", 'B', IL.Ceramic_Bowl, 'P', OP.ingot.dat(MT.Bronze));
        MTEx.gt6xMTEReg.add("Mortar", "Misc Tool Blocks", MTEx.IDs.Mortars.get()+1, 32720, MultiTileEntityMortarX.class, 0, 16, MTEx.StoneBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   5.0F, NBT_RECIPEMAP, RM.Mortar , NBT_DESIGN, 1), "P", "B", 'B', IL.Ceramic_Bowl, 'P', OP.ingot.dat(MT.BismuthBronze));
        MTEx.gt6xMTEReg.add("Mortar", "Misc Tool Blocks", MTEx.IDs.Mortars.get()+2, 32720, MultiTileEntityMortarX.class, 0, 16, MTEx.StoneBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   5.0F, NBT_RECIPEMAP, RM.Mortar , NBT_DESIGN, 2), "P", "B", 'B', IL.Ceramic_Bowl, 'P', OP.ingot.dat(MT.BlackBronze));
        MTEx.gt6xMTEReg.add("Mortar", "Misc Tool Blocks", MTEx.IDs.Mortars.get()+3, 32720, MultiTileEntityMortarX.class, 0, 16, MTEx.StoneBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   5.0F, NBT_RECIPEMAP, RM.Mortar , NBT_DESIGN, 3), "P", "B", 'B', IL.Ceramic_Bowl, 'P', OP.ingot.dat(MT.ArsenicBronze));
        MTEx.gt6xMTEReg.add("Mortar", "Misc Tool Blocks", MTEx.IDs.Mortars.get()+4, 32720, MultiTileEntityMortarX.class, 0, 16, MTEx.StoneBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   1.0F, NBT_RESISTANCE,   5.0F, NBT_RECIPEMAP, RM.Mortar , NBT_DESIGN, 4), "P", "B", 'B', IL.Ceramic_Bowl, 'P', OP.ingot.dat(MT.ArsenicCopper));

        mat = MT.Al2O3;
        MTEx.gt6xMTEReg.add("Alumina Refractory Bricks"              , "Multiblock Machines", MTEx.IDs.AluminaBricks       .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "refractorybricks" , NBT_DESIGNS, 1), "ICI", "CrC", "ICI", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));
        MTEx.gt6xMTEReg.add("Alumina Checker Bricks"                 , "Multiblock Machines", MTEx.IDs.AluminaCheckerBricks.get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "checkerbricks"    , NBT_DESIGNS, 1), "CIC", "IrI", "CIC", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));

        mat = MTx.RefractoryCeramic;
        CrucibleUtils.addCrucibleMaterial(mat, 12, 6.0F, false, true, MTEx.StoneBlock, null, ILx.FireclayParts);

        mat = MT.SiC;
        MTEx.gt6xMTEReg.add("Silicon Carbide Refractory Bricks"      , "Multiblock Machines", MTEx.IDs.SiCBricks           .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "refractorybricks" , NBT_DESIGNS, 1), "ICI", "CrC", "ICI", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));
        MTEx.gt6xMTEReg.add(mat.getLocal()+" Wall"                   , "Multiblock Machines", MTEx.IDs.SiCWall             .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "metalwall"        , NBT_DESIGNS, 7), "wPP", "hPP", 'P', OP.plate.dat(mat)); RM.Welder.addRecipe2(false, 16, 256, OP.plate.mat(mat, 4), ST.tag(10), MTEx.gt6xMTEReg.getItem());
        CrucibleUtils.addCrucibleMaterial(mat, 13, 6.0F, false, false, MTEx.StoneBlock, OP.plate);

        mat = MTx.MgOC;
        MTEx.gt6xMTEReg.add("MgO-C Refractory Bricks"                , "Multiblock Machines", MTEx.IDs.MgOCBricks          .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "refractorybricks" , NBT_DESIGNS, 1), "ICI", "CrC", "ICI", 'I', ingot.dat(mat), 'C', dust.dat(MTx.RefractoryMortar));
        MTEx.gt6xMTEReg.add(mat.getLocal()+" Wall"                   , "Multiblock Machines", MTEx.IDs.MgOCWall            .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "metalwall"        , NBT_DESIGNS, 7), "wPP", "hPP", 'P', OP.plate.dat(mat)); RM.Welder.addRecipe2(false, 16, 256, OP.plate.mat(mat, 4), ST.tag(10), MTEx.gt6xMTEReg.getItem());
        CrucibleUtils.addCrucibleMaterial(mat, 14, 8.0F, true, false, MTEx.StoneBlock, OP.plate);
    }

    private void addRecipes() {
        // Fire clay
        RM.Mixer.addRecipe2(true, 16, 192, dust.mat(MT.Kaolinite, 2), dust.mat(MT.Graphite, 1), dust.mat(MTx.Fireclay, 3));
        for (FluidStack water : FL.waters(125, 100)) {
            RM.Mixer.addRecipe2(true, 16, 192, IL.Clay_Ball_White.get(2), dust.mat(MT.Graphite, 1), FL.mul(water, 5), NF, ILx.Fireclay_Ball.get(3));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.RefractoryCeramic, 1), FL.mul(water, 5), NF, ILx.Fireclay_Ball.get(1));
            RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.Fireclay, 1), water, NF, ILx.Fireclay_Ball.get(1));
        }
        RM.Furnace.addRecipe1(true, 16, 64, dust.mat(MTx.Fireclay, 1), ingot.mat(MTx.Firebrick, 1));

        // Magnesia-Carbon
        RMx.Sintering.addRecipeX(true, 64, 256, ST.array(ST.tag(2), dust.mat(MTx.MgO, 1), dust.mat(MT.Graphite, 1)), ingot.mat(MTx.MgOC, 2));

        // Refractory Cement
        RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(2), dust.mat(MTx.CaO, 1), dust.mat(MT.Al2O3, 5)), clinker.mat(MTx.CaAlCement, 6));

        // Portland Cement
        for (OreDictMaterial ash : ANY.Ash.mToThis) {
        RMx.Sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MTx.CaO, 5), dust.mat(ash               , 2)), clinker.mat(MTx.Cement, 7));
        }
        RMx.Sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MTx.CaO, 5), dust.mat(MT.OREMATS.Bauxite, 2)), clinker.mat(MTx.Cement, 7));
        RMx.Sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MTx.CaO, 4), dust.mat(MT.STONES.Shale   , 3)), clinker.mat(MTx.Cement, 7));
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
        RMx.Sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(MTx.CaO, 5), dust.mat(clay              , 2)), clinker.mat(MTx.Cement, 7));
        }
        for (OreDictMaterial calcite : ANY.Calcite.mToThis) {
            for (OreDictMaterial ash : ANY.Ash.mToThis) {
            RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(ash               , 4)), clinker.mat(MTx.Cement, 14));
            }
            RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(MT.OREMATS.Bauxite, 4)), clinker.mat(MTx.Cement, 14));
            RMx.Sintering.addRecipeX(true, 16, 64 , ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 1), dust.mat(calcite, 10), dust.mat(MT.STONES.Shale   , 3)), clinker.mat(MTx.Cement, 7));
            for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(3), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 25), dust.mat(clay              , 4)), clinker.mat(MTx.Cement, 14));
            }
        }

        // GGBFS Cement
        for (OreDictMaterial ash : ANY.Ash.mToThis) {
        RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MTx.CaO, 5), dust.mat(ash               , 2), dust.mat(MT.OREMATS.Wollastonite, 7)), clinker.mat(MTx.Cement, 14));
        }
        RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MTx.CaO, 5), dust.mat(MT.OREMATS.Bauxite, 2), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MTx.CaO, 4), dust.mat(MT.STONES.Shale   , 3), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
        RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(MTx.CaO, 5), dust.mat(clay              , 2), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
        }
        for (OreDictMaterial calcite : ANY.Calcite.mToThis) {
            for (OreDictMaterial ash : ANY.Ash.mToThis) {
            RMx.Sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(ash               , 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
            }
            RMx.Sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(MT.OREMATS.Bauxite, 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
            RMx.Sintering.addRecipeX(true, 16, 128, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 2), dust.mat(calcite, 10), dust.mat(MT.STONES.Shale   , 3), dust.mat(MT.OREMATS.Wollastonite, 7 )), clinker.mat(MTx.Cement, 14));
            for (OreDictMaterial clay : ANY.Clay.mToThis) {
            RMx.Sintering.addRecipeX(true, 16, 256, ST.array(ST.tag(4), dustTiny.mat(MT.Gypsum, 4), dust.mat(calcite, 25), dust.mat(clay              , 4), dust.mat(MT.OREMATS.Wollastonite, 14)), clinker.mat(MTx.Cement, 28));
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
            RM.Mixer.addRecipeX(true, 16, 144, ST.array(ST.make(Blocks.sand, 3, 0), blockDust.mat(MTx.CaAlCement, 1), dust.mat(MT.Kaolinite, 1)), FL.mul(tWater, 9, 2, true), NF, blockDust.mat(MTx.RefractoryMortar, 5));
        }
    }

    private void changeCraftingRecipes() {
        // Masonry
        CR.shaped(ST.make(Blocks.brick_block, 1, 0), CR.DEF_REM, "BMB", "M M", "BMB", 'B', ST.make(Items.brick, 1, 0), 'M', OM.dust(MTx.Mortar));
        CR.shaped(ST.make(Blocks.nether_brick, 1, 0), CR.DEF_REM, "BMB", "M M", "BMB", 'B', ST.make(Items.netherbrick, 1, 0), 'M', OM.dust(MTx.Mortar));

        // Brick burning box
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(1199), CR.DEF_REV_NCC, "BBB", "BBB", "BFB", 'B', OP.ingot.dat(MTx.Firebrick), 'F', OD.craftingFirestarter);
        OreDictManager.INSTANCE.setItemData(MTEx.gt6MTEReg.getItem(1199), new OreDictItemData(MTx.Firebrick, 8*U));

        // Fire bricks block
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(18000), CR.DEF_REV_NCC, "MBM", "B B", "MBM", 'B', ingot.mat(MTx.Firebrick, 1), 'M', OM.dust(MTx.Mortar));
        OreDictManager.INSTANCE.setItemData(MTEx.gt6MTEReg.getItem(18000), new OreDictItemData(MTx.Firebrick, 4*U));
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
