package org.altadoon.gt6x.features.electronics;

import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerPrefix;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.common.items.Tools;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.electronics.tools.SolderingIron;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Electronics extends GT6XFeature {
    public static final String FEATURE_NAME = "Electronics";

    public OreDictPrefix polyGem = null;

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void configure(Config config) {

    }

    @Override
    public void preInit() {
        createPrefixes();
        MultiItemsElectronics.instance = new MultiItemsElectronics(Gt6xMod.MOD_ID, "multiitemselectronics");
        Tools.addRefillable(SolderingIron.ID, OreDictToolNames.solderingiron, "Soldering Iron", "Joins together items using a solder (a filler metal)", new SolderingIron(), OreDictToolNames.solderingiron, new OreDictItemData(MT.StainlessSteel, 3*U2, MT.Wood, U2));
    }

    @Override
    public void init() {

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
        polyGem = OreDictPrefix.createPrefix("polyGem")
                .setCategoryName("polyGems")
                .setLocalItemName("Polycrystalline ", "")
                .setMaterialStats(U)
                .add(TD.Prefix.RECYCLABLE);
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.polyGem", polyGem,
                MT.Empty, MT.Si, MT.Ge, MTx.GaAs, MTx.SiGe
        );
    }

    private void addRecipes() {
        // electron tube stuff
        RM.Press.addRecipeX(true, 16, 64, ST.array(OP.stick.mat(MT.Mo, 2), OP.bolt.mat(MT.Mo, 2), OP.dustSmall.mat(MT.Redstone, 2)), ILx.Electrode_Molybdenum.get(1));
        RM.Press.addRecipeX(true, 16, 64, ST.array(OP.stick.mat(MT.Mo, 4), OP.bolt.mat(MT.Mo, 4), OP.dust.mat(MT.Redstone, 1)), ILx.Electrode_Molybdenum.get(2));

        for (OreDictMaterial mat : ANY.W.mToThis) {
            RM.Press.addRecipeX(true, 16, 64, ST.array(OP.stick.mat(mat, 2), OP.bolt.mat(mat, 2), OP.dustSmall.mat(MT.Redstone, 2)), ILx.Electrode_Tungsten.get(1));
            RM.Press.addRecipeX(true, 16, 64, ST.array(OP.stick.mat(mat, 4), OP.bolt.mat(mat, 4), OP.dust.mat(MT.Redstone, 1)), ILx.Electrode_Tungsten.get(2));
        }

        RM.Laminator.addRecipe2(true, 16,  128, OP.plateGem.mat(MT.Glass, 1), ILx.Electrode_Molybdenum.get(8), ILx.ElectronTube_Molybdenum.get(8));
        RM.Laminator.addRecipe2(true, 16,   64, OP.casingSmall.mat(MT.Glass, 1), ILx.Electrode_Molybdenum.get(4), ILx.ElectronTube_Molybdenum.get(4));
        RM.Laminator.addRecipe2(true, 16,   48, ST.make(Blocks.glass_pane,1, W), ILx.Electrode_Molybdenum.get(1), ILx.ElectronTube_Molybdenum.get(1));
        RM.Laminator.addRecipe2(true, 16,  128, OP.plateGem.mat(MT.Glass, 1), ILx.Electrode_Tungsten.get(8), ILx.ElectronTube_Tungsten.get(8));
        RM.Laminator.addRecipe2(true, 16,   64, OP.casingSmall.mat(MT.Glass, 1), ILx.Electrode_Tungsten.get(4), ILx.ElectronTube_Tungsten.get(4));
        RM.Laminator.addRecipe2(true, 16,   48, ST.make(Blocks.glass_pane,1, W), ILx.Electrode_Tungsten.get(1), ILx.ElectronTube_Tungsten.get(1));

        // soldering iron
        CR.shaped(Tools.refillableMetaTool.make(SolderingIron.ID_EMPTY), CR.DEF_MIR, "Ph ", "fC ", " sS", 'P', OP.pipeTiny.mat(MT.StainlessSteel, 1), 'C', OP.plateCurved.mat(MT.StainlessSteel, 1), 'S', OD.stickAnyWood);

        // glass fibres
        CR.shaped(ILx.PlatinumBushing.get(1), CR.DEF_REV, "   ", " P ", "x  ", 'P', OP.plate.mat(MT.Pt, 1));

        final long EUt = 16, durationPerUnit = 64*6;
        for (OreDictPrefix tPrefix : OreDictPrefix.VALUES) if (tPrefix != null && tPrefix.containsAny(TD.Prefix.EXTRUDER_FODDER, TD.Prefix.INGOT_BASED, TD.Prefix.GEM_BASED, TD.Prefix.DUST_BASED) && U % tPrefix.mAmount == 0) {
            ItemStack stack = tPrefix.mat(MT.Glass, U / tPrefix.mAmount);
            if (stack != null && stack.stackSize <= stack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(T, F, F, F, T, EUt, durationPerUnit, stack, ILx.PlatinumBushing.get(0), ILx.GlassFibres.get(1));
            }
        }

        // boards
        RM.Laminator.addRecipe2(true, 16, 128, OP.foil.mat(MTx.PF, 4), ST.make(Items.paper, 1, W), ILx.PF_Board.get(1));
        RM.Bath.addRecipe1(true, 16, 128, ILx.GlassFibres.get(1), MTx.Epoxy.liquid(U, true), NF, ILx.FRE_Board.get(1));

        // Copper-clad laminates e.a.
        RM.Laminator.addRecipe2(true, 16, 128, ILx.PF_Board.get(1), OP.foil.mat(MT.Cu, 4), ILx.CCL.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FRE_Board.get(1), OP.foil.mat(MT.Cu, 4), ILx.CCL.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FRE_Board.get(1), OP.foil.mat(MT.Au, 4), ILx.GCL.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FRE_Board.get(1), OP.foil.mat(MT.Pt, 4), ILx.PCL.get(1));
        CR.shapeless(ILx.CCL_SMALL.get(2), new Object[]{ILx.CCL.get(1), OreDictToolNames.saw});
        CR.shapeless(ILx.GCL_SMALL.get(2), new Object[]{ILx.GCL.get(1), OreDictToolNames.saw});
        CR.shapeless(ILx.PCL_SMALL.get(2), new Object[]{ILx.PCL.get(1), OreDictToolNames.saw});
        CR.shapeless(ILx.CCL_TINY.get(2), new Object[]{ILx.CCL_SMALL.get(1), OreDictToolNames.saw});
        CR.shapeless(ILx.GCL_TINY.get(2), new Object[]{ILx.GCL_SMALL.get(1), OreDictToolNames.saw});
        CR.shapeless(ILx.GCL_TINY.get(2), new Object[]{ILx.PCL_SMALL.get(1), OreDictToolNames.saw});

        FluidStack[] cutterFluids = FL.array(FL.Water.make(1000), FL.SpDew.make(1000), FL.DistW.make(1000), FL.Lubricant.make(1000), FL.LubRoCant.make(1000));
        long[] multipliers = new long[] {4, 4, 3, 1, 1};
        for (int i = 0; i < 4; i++) if (cutterFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.CCL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, T), NF, ILx.CCL_SMALL.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.GCL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, T), NF, ILx.GCL_SMALL.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.PCL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, T), NF, ILx.PCL_TINY.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.CCL_SMALL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, T), NF, ILx.CCL_TINY.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.GCL_SMALL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, T), NF, ILx.GCL_TINY.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.PCL_SMALL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, T), NF, ILx.PCL_TINY.get(2));
        }

        // Trace etching
        CR.shaped(ILx.EtchMask_Trace.get(1), CR.DEF_REV, "   ", " P ", " x ", 'P', OP.plate.mat(MTx.PVC, 1));

        RM.Bath.addRecipeX(true, 0, 256, ST.array(ILx.CCL.get(1), ILx.EtchMask_Trace.get(0), OP.dust.mat(MT.FeCl3, 4)), MT.DistWater.liquid(9*U2, true), MTx.CuFeClSolution.liquid(9*U, false), IL.Circuit_Plate_Copper.get(1));
        RM.Bath.addRecipe2(true, 0, 256, ILx.GCL.get(1), ILx.EtchMask_Trace.get(0), FL.array(MT.AquaRegia.liquid(13*U2, true)), FL.array(MT.ChloroauricAcid.liquid(3*U, false), MT.NO.gas(U, false), MT.H2O.liquid(3*U, false)), IL.Circuit_Plate_Gold.get(1));
        RM.Bath.addRecipe2(true, 0, 256, ILx.PCL.get(1), ILx.EtchMask_Trace.get(0), FL.array(MT.AquaRegia.liquid(78*U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(36*U8, false), MT.NO.gas(12*U8, F), FL.Water.make(4125)), IL.Circuit_Plate_Platinum.get(1));

        RM.Mixer.addRecipe1(true, 16, 64*9, OP.dust.mat(MT.Fe, 1), MTx.CuFeClSolution.liquid(18*U, true), MTx.FeCl2Solution.liquid(18*U, false), OP.dust.mat(MT.Cu, 1));

        /// Semiconductors

        // Hydrides & Polycrystallines
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.rod.mat(MT.Ge, 1), OM.dust(MT.Mo, U3), FL.Water.make(3000), MTx.Germane.gas(5*U2, false), OM.dust(MTx.MoO3, 4*U3));
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.rod.mat(MT.Ge, 1), OM.dust(MT.Cd, U), FL.Water.make(3000), MTx.Germane.gas(5*U2, false), OM.dust(MTx.CdO, 2*U));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Mg2Si, 3), MT.HCl.gas(8*U, true), MTx.Silane.gas(5*U, false), dust.mat(MT.MgCl2, 6));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.Silane.gas(U, true), MTx.Germane.gas(U, true)), FL.array(MTx.SiGeH8.gas(2*U, false)));

        //TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.Germane.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MT.Ge, 1));
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.Silane.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MT.Si, 1));
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.SiGeH8.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MTx.SiGe, 1));

        RM.CrystallisationCrucible.addRecipe1(true, 16, 18000, ST.tag(0), FL.array(MT.Ga.liquid(U2, true), MT.As.gas(U2, true)), ZL_FS, polyGem.mat(MTx.GaAs, 1));
        RM.CrystallisationCrucible.addRecipe1(true, 16, 72000, plateGemTiny.mat(MTx.GaAs, 1), FL.array(MT.Ga.liquid(35*U18, true), MT.As.gas(35*U18, true)), ZL_FS, bouleGt.mat(MTx.GaAs, 1));
        RM.CrystallisationCrucible.addRecipe1(true, 16, 72000, plateGemTiny.mat(MTx.SiGe, 1), FL.array(MTx.SiGe.liquid(35*U9, true)), ZL_FS, bouleGt.mat(MTx.SiGe, 1));

        RM.Cutter.add(new RecipeMapHandlerPrefix(polyGem, 1, NF, 16, 256, 0, NF, plateGemTiny, 6, ST.tag(1), NI, true, true, false, null));

        // components
        CR.shaped(ILx.Resistor_ThroughHole.get(2), CR.DEF_REV, " W ", "iPC", " W ", 'W', OP.wireFine.dat(MT.Cu), 'P', OP.plateGemTiny.dat(MT.Ceramic), 'C', OP.dustTiny.dat(ANY.Coal));
        CR.shaped(ILx.Resistor_ThroughHole.get(2), CR.DEF_REV, " W ", "iPC", " W ", 'W', OP.wireFine.dat(MT.Brass), 'P', OP.plateGemTiny.dat(MT.Ceramic), 'C', OP.dustTiny.dat(ANY.Coal));

        CR.shaped(ILx.Capacitor_ThroughHole.get(2), CR.DEF_REV, " i ", "PC ", "W W", 'W', OP.wireFine.dat(MT.Cu), 'C', OP.plateGemTiny.dat(MT.Ceramic), 'P', OP.plateTiny.dat(MT.Paper));
        CR.shaped(ILx.Capacitor_ThroughHole.get(2), CR.DEF_REV, " i ", "PC ", "W W", 'W', OP.wireFine.dat(MT.Brass), 'C', OP.plateGemTiny.dat(MT.Ceramic), 'P', OP.plateTiny.dat(MT.Paper));

        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Cu), 'S', OP.plateGemTiny.dat(MT.Si), 'P', OP.plateTiny.dat(MT.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Brass), 'S', OP.plateGemTiny.dat(MT.Si), 'P', OP.plateTiny.dat(MT.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Cu), 'S', OP.plateGemTiny.dat(MT.Ge), 'P', OP.plateTiny.dat(MT.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Brass), 'S', OP.plateGemTiny.dat(MT.Ge), 'P', OP.plateTiny.dat(MT.Plastic));

        //TODO doping, dicing, wafers, ...

        // circuits
        CR.shaped(IL.Circuit_Basic    .get(1), CR.DEF_REM, "iE ", "CBR", "   ", 'B', IL.Circuit_Plate_Copper.get(1), 'E', MultiItemsElectronics.ELECTRONTUBE_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Basic    .get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', IL.Circuit_Plate_Copper.get(1), 'E', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Good     .get(1), CR.DEF_REM, "iT ", "CBR", " T ", 'B', IL.Circuit_Plate_Copper.get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Advanced .get(1), CR.DEF_REM, "TiT", "CBR", "T T", 'B', IL.Circuit_Plate_Gold  .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
    }

    private void changeRecipes() {
        for (Recipe r : RM.CrystallisationCrucible.mRecipeList) {
            if (ST.equal(r.mInputs[0], OM.dust(MT.Ge, U9))) {
                r.mInputs[0] = plateGemTiny.mat(MT.Ge, 1);
            } else if (ST.equal(r.mInputs[0], OM.dust(MT.Si, U9))) {
                r.mInputs[0] = plateGemTiny.mat(MT.Si, 1);
            } else if (ST.equal(r.mInputs[0], OM.dust(MT.Ge, U)) || ST.equal(r.mInputs[0], OM.dust(MT.Si, U))) {
                r.mEnabled = false;
            }
        }
    }
}
