package org.altadoon.gt6x.common.items;

import gregapi.data.*;
import gregapi.item.IPrefixItem;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.util.CR;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.utils.Code;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.features.engines.OreDictPrefixes.tbcCoatedRotor;

public class MultiItemsX extends MultiItemRandom {
    public static MultiItemsX instance;

    public MultiItemsX(String modID, String unlocalized) { super(modID, unlocalized); }

    public static void init(String modID) {
        instance = new MultiItemsX(modID, "multiitems");
    }

    @Override
    public void addItems() {
        ILx.Display_OMStack.set(new ItemMaterialDisplay());
        ILx.Fireclay_Ball.set(addItem(0, "Fireclay", "Fire-proof clay", TC.stack(TC.TERRA, 1), new OreDictItemData(MTx.Fireclay, U))); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("digger", last());
        RM.add_smelting(ILx.Fireclay_Ball.get(1), OP.ingot.mat(MTx.Firebrick, 1), false, false, false);
        RM.add_smelting(dust.mat(MTx.Fireclay, 1), ingot.mat(MTx.Firebrick, 1));
        ILx.Rosin.set(addItem(1, "Rosin", "Solid Conifer Resin", TC.stack(TC.ARBOR, 1))); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("forester", last());

        ILx.Shape_Extruder_Catalytic_Converter.set(addItem(3, "Extruder Shape (Catalytic Converter)", "Extruder Shape for making Catalytic Converters"));
        ILx.Shape_SimpleEx_Catalytic_Converter.set(addItem(4, "Low Heat Extruder Shape (Catalytic Converter)", "Extruder Shape for making Catalytic Converters"));
        CR.shaped(ILx.Shape_Extruder_Catalytic_Converter.get(1), CR.DEF_REV, " e ", " P ", "   ", 'P', IL.Shape_Extruder_Empty);
        CR.shaped(ILx.Shape_SimpleEx_Catalytic_Converter.get(1), CR.DEF_REV, " e ", " P ", "   ", 'P', IL.Shape_SimpleEx_Empty);

        ILx.SparkPlugs.set(addItem(5, "Spark Plugs", "Used to power spark-ignition engines", TC.stack(TC.ELECTRUM, 1)));
        CR.shaped(ILx.SparkPlugs.get(1), CR.DEF_REV, "BCB", "CAC", "BMB", 'B', bolt.dat(MT.Invar), 'C', cableGt01.dat(ANY.Cu), 'M', IL.MOTORS[1], 'A', "gt:re-battery1");

        ILx.SuperCharger.set(addItem(6, "Supercharger", "Forces air into engines"));
        CR.shaped(ILx.SuperCharger.get(1), CR.DEF_REV, "PUP", "RSR", "TUT", 'P', pipeSmall.dat(MT.Al), 'U', plateCurved.dat(MT.Magnalium), 'R', tbcCoatedRotor.dat(MT.Magnalium), 'S', stick.dat(MTx.HSSM2), 'T', stick.dat(MT.Teflon));
    }

    public void addClayItems() {
        addFireclayRawItem(991, ILx.Fireclay_Mold_Raw, ILx.Fireclay_Mold.get(1), "Mold", "C C", "CCC", "k R", true);

        addFireclayRawMold(900, ILx.Fireclay_Ingot_Mold_Raw          , 0b0_01110_01110_01110_01110_01110, ingot);
        addFireclayRawMold(901, ILx.Fireclay_Chunk_Mold_Raw          , 0b0_11000_11000_00000_00000_00000, chunk);
        addFireclayRawMold(902, ILx.Fireclay_Plate_Mold_Raw          , 0b0_11111_11111_11111_11111_11111, plate);
        addFireclayRawMold(903, ILx.Fireclay_Tiny_Plate_Mold_Raw     , 0b0_00000_01110_01110_01110_00000, plateTiny);
        addFireclayRawMold(904, ILx.Fireclay_Bolt_Mold_Raw           , 0b0_00000_00000_00100_00100_00000, bolt);
        addFireclayRawMold(905, ILx.Fireclay_Rod_Mold_Raw            , 0b0_00000_00000_11111_00000_00000, stick);
        addFireclayRawMold(906, ILx.Fireclay_Long_Rod_Mold_Raw       , 0b0_10000_01000_00100_00010_00001, stickLong);
        addFireclayRawMold(907, ILx.Fireclay_Item_Casing_Mold_Raw    , 0b0_11101_11101_11101_00001_11100, casingSmall);
        addFireclayRawMold(908, ILx.Fireclay_Ring_Mold_Raw           , 0b0_00000_01110_01010_01110_00000, ring);
        addFireclayRawMold(909, ILx.Fireclay_Gear_Mold_Raw           , 0b0_10101_01110_11011_01110_10101, gearGt);
        addFireclayRawMold(910, ILx.Fireclay_Small_Gear_Mold_Raw     , 0b0_01010_11111_01010_11111_01010, gearGtSmall);
        addFireclayRawMold(911, ILx.Fireclay_Sword_Mold_Raw          , 0b0_00100_01110_01110_01110_01110, toolHeadRawSword);
        addFireclayRawMold(912, ILx.Fireclay_Pickaxe_Mold_Raw        , 0b0_00000_01110_10001_00000_00000, toolHeadRawPickaxe);
        addFireclayRawMold(913, ILx.Fireclay_Spade_Mold_Raw          , 0b0_01110_01110_01110_01010_00000, toolHeadRawSpade);
        addFireclayRawMold(914, ILx.Fireclay_Shovel_Mold_Raw         , 0b0_00100_01110_01110_01110_00000, toolHeadRawShovel);
        addFireclayRawMold(915, ILx.Fireclay_Universal_Spade_Mold_Raw, 0b0_00100_01110_01100_01110_00000, toolHeadRawUniversalSpade);
        addFireclayRawMold(916, ILx.Fireclay_Axe_Mold_Raw            , 0b0_00000_01110_01110_01000_00000, toolHeadRawAxe);
        addFireclayRawMold(917, ILx.Fireclay_Double_Axe_Mold_Raw     , 0b0_00000_11111_11111_10001_00000, toolHeadRawAxeDouble);
        addFireclayRawMold(918, ILx.Fireclay_Saw_Mold_Raw            , 0b0_00000_11111_11111_00000_00000, toolHeadRawSaw);
        addFireclayRawMold(919, ILx.Fireclay_Hammer_Mold_Raw         , 0b0_01110_01110_01010_01110_01110, toolHeadHammer);
        addFireclayRawMold(920, ILx.Fireclay_File_Mold_Raw           , 0b0_01110_01110_01110_00100_00100, toolHeadFile);
        addFireclayRawMold(921, ILx.Fireclay_Screwdriver_Mold_Raw    , 0b0_00000_00100_00100_00100_00100, toolHeadScrewdriver);
        addFireclayRawMold(922, ILx.Fireclay_Chisel_Mold_Raw         , 0b0_01110_00100_00100_00100_00100, toolHeadRawChisel);
        addFireclayRawMold(923, ILx.Fireclay_Arrow_Mold_Raw          , 0b0_00000_00100_00100_01110_00000, toolHeadRawArrow);
        addFireclayRawMold(924, ILx.Fireclay_Hoe_Mold_Raw            , 0b0_00000_00110_01110_00000_00000, toolHeadRawHoe);
        addFireclayRawMold(925, ILx.Fireclay_Sense_Mold_Raw          , 0b0_00000_01111_11111_00000_00000, toolHeadRawSense);
        addFireclayRawMold(926, ILx.Fireclay_Plow_Mold_Raw           , 0b0_11111_11111_11111_11111_00100, toolHeadRawPlow);
        addFireclayRawMold(927, ILx.Fireclay_Builderwand_Mold_Raw    , 0b0_00000_00100_11111_01110_01010, toolHeadBuilderwand);
        addFireclayRawMold(928, ILx.Fireclay_Nugget_Mold_Raw         , 0b0_00000_00000_00100_00000_00000, nugget);
        addFireclayRawMold(929, ILx.Fireclay_Billet_Mold_Raw         , 0b0_01100_11110_11110_01100_00000, billet);

        addFireclayRawItem(987, ILx.Fireclay_Tap_Raw              , ILx.Fireclay_Tap                 .get(1), "Tap"              , "CCR", "kC ", "   ", true);
        addFireclayRawItem(988, ILx.Fireclay_Funnel_Raw           , ILx.Fireclay_Funnel              .get(1), "Funnel"           , "CRC", "kC ", "   ", true);
        addFireclayRawItem(989, ILx.Fireclay_Crucible_Raw         , ILx.Fireclay_Crucible            .get(1), "Crucible"         , "CkC", "CRC", "CCC", true);
        addFireclayRawItem(990, ILx.Fireclay_Basin_Raw            , ILx.Fireclay_Basin               .get(1), "Basin"            , "CkC", "CRC", " C ", true);

        addFireclayRawItem(992, ILx.Fireclay_Faucet_Raw           , ILx.Fireclay_Faucet              .get(1), "Faucet"           , "C C", "kCR", "   ", true);
        addFireclayRawItem(993, ILx.Fireclay_Crossing_Raw         , ILx.Fireclay_Crossing            .get(1), "Crossing"         , "kCR", "CCC", " C ", true);
        addFireclayRawItem(979, ILx.Fireclay_Engine_Block_Mold_Raw, ILx.Fireclay_Engine_Block_Mold   .get(1), "Engine Block Mold", "CCC", "C C", "kCR", true);
        addClayRawItem    (998, ILx.Ceramic_Engine_Block_Mold_Raw , ILx.Ceramic_Engine_Block_Mold    .get(1), "Engine Block Mold", "CCC", "C C", "kCR", true);

        addMiscFireclayMoldRecipes();
    }

    private void addFireclayRawMold(int id, ILx rawMold, int code, OreDictPrefix prefix) {
        ItemStack firedItem = ILx.Fireclay_Mold.getWithNBT(1, UT.NBT.make("gt.mold", code));
        String prefixName = prefix.mNameLocal.endsWith("s") ? prefix.mNameLocal.substring(0, prefix.mNameLocal.length()-1) : prefix.mNameLocal;
        addFireclayRawItem(id, rawMold, firedItem, prefixName + " Mold", "C C", "CCC", "k R", false);

        for (IPrefixItem item : prefix.mRegisteredPrefixItems)
            CR.shapeless(rawMold.get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, item});
    }

    private void addFireclayRawItem(int id, ILx rawItem, ItemStack firedItem, String name, String recipe1, String recipe2, String recipe3, boolean addRecipe) {
        addRawItem(id, rawItem, firedItem, name, recipe1, recipe2, recipe3, addRecipe, MTx.Fireclay, ILx.Fireclay_Ball.get(1), TC.IGNIS);
    }

    private void addClayRawItem(int id, ILx rawItem, ItemStack firedItem, String name, String recipe1, String recipe2, String recipe3, boolean addRecipe) {
        addRawItem(id, rawItem, firedItem, name, recipe1, recipe2, recipe3, addRecipe, MT.Clay, ST.make(Items.clay_ball, 1, 0), TC.GELUM);
    }

    private void addRawItem(int id, ILx rawItem, ItemStack firedItem, String name, String recipe1, String recipe2, String recipe3, boolean addRecipe, OreDictMaterial mat, ItemStack clayBall, TC thaumCraftStack) {
        int units = Code.countItemsInRecipe(recipe1, recipe2, recipe3, 'C');
        rawItem.set(addItem(id, mat.mNameLocal + " " + name, "Put in Furnace to harden", new OreDictItemData(mat, U*units), TC.stack(TC.TERRA, 2), TC.stack(thaumCraftStack, 1)));

        if (addRecipe)
            CR.shaped(rawItem.get(1), CR.DEF_REV, recipe1, recipe2, recipe3, 'C', clayBall, 'R', OreDictToolNames.rollingpin);

        CR.shapeless(ST.mul(units, clayBall), CR.DEF_NCC, new Object[] {last()});
        RM.add_smelting(rawItem.get(1), firedItem, false, false, true);
    }

    private void addMiscFireclayMoldRecipes() {
        CR.shapeless(ILx.Fireclay_Ingot_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OD.itemMudBrick});
        CR.shapeless(ILx.Fireclay_Ingot_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OP.ingot.dat(MT.Brick)});
        CR.shapeless(ILx.Fireclay_Plate_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OD.paneGlass});
        CR.shapeless(ILx.Fireclay_Plate_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OD.plankAnyWood});
        CR.shapeless(ILx.Fireclay_Arrow_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OD.itemFlint});
        CR.shapeless(ILx.Fireclay_Arrow_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.arrow});
        CR.shapeless(ILx.Fireclay_Sword_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.wooden_sword});
        CR.shapeless(ILx.Fireclay_Pickaxe_Mold_Raw        .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.wooden_pickaxe});
        CR.shapeless(ILx.Fireclay_Shovel_Mold_Raw         .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.wooden_shovel});
        CR.shapeless(ILx.Fireclay_Axe_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.wooden_axe});
        CR.shapeless(ILx.Fireclay_Hoe_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.wooden_hoe});
        CR.shapeless(ILx.Fireclay_Sword_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.stone_sword});
        CR.shapeless(ILx.Fireclay_Pickaxe_Mold_Raw        .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.stone_pickaxe});
        CR.shapeless(ILx.Fireclay_Shovel_Mold_Raw         .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.stone_shovel});
        CR.shapeless(ILx.Fireclay_Axe_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.stone_axe});
        CR.shapeless(ILx.Fireclay_Hoe_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, Items.stone_hoe});
        CR.shapeless(ILx.Fireclay_File_Mold_Raw           .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OD.paneGlass, OD.paneGlass});
        CR.shapeless(ILx.Fireclay_File_Mold_Raw           .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, OD.plankAnyWood, OD.plankAnyWood});
        CR.shapeless(ILx.Fireclay_File_Mold_Raw           .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.FILE)});
        CR.shapeless(ILx.Fireclay_Sword_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SWORD)});
        CR.shapeless(ILx.Fireclay_Pickaxe_Mold_Raw        .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.PICKAXE)});
        CR.shapeless(ILx.Fireclay_Pickaxe_Mold_Raw        .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.GEM_PICK)});
        CR.shapeless(ILx.Fireclay_Pickaxe_Mold_Raw        .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.CONSTRUCTION_PICK)});
        CR.shapeless(ILx.Fireclay_Spade_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SPADE)});
        CR.shapeless(ILx.Fireclay_Shovel_Mold_Raw         .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SHOVEL)});
        CR.shapeless(ILx.Fireclay_Universal_Spade_Mold_Raw.get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.UNIVERSALSPADE)});
        CR.shapeless(ILx.Fireclay_Axe_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.AXE)});
        CR.shapeless(ILx.Fireclay_Double_Axe_Mold_Raw     .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.DOUBLE_AXE)});
        CR.shapeless(ILx.Fireclay_Saw_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SAW)});
        CR.shapeless(ILx.Fireclay_Hammer_Mold_Raw         .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SOFTHAMMER)});
        CR.shapeless(ILx.Fireclay_Hammer_Mold_Raw         .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.HARDHAMMER)});
        CR.shapeless(ILx.Fireclay_Screwdriver_Mold_Raw    .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SCREWDRIVER)});
        CR.shapeless(ILx.Fireclay_Chisel_Mold_Raw         .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.CHISEL)});
        CR.shapeless(ILx.Fireclay_Hoe_Mold_Raw            .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.HOE)});
        CR.shapeless(ILx.Fireclay_Sense_Mold_Raw          .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.SENSE)});
        CR.shapeless(ILx.Fireclay_Plow_Mold_Raw           .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.PLOW)});
        CR.shapeless(ILx.Fireclay_Builderwand_Mold_Raw    .get(1), CR.DEF_NCC, new Object[] {ILx.Fireclay_Mold_Raw, ToolsGT.sMetaTool.make(ToolsGT.BUILDERWAND)});
    }
}
