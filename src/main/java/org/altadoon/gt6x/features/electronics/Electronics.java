package org.altadoon.gt6x.features.electronics;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.oredict.event.OreDictListenerEvent_Names;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerPrefix;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.common.items.Tools;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.electronics.tools.SolderingIron;

import java.util.Arrays;
import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.data.TD.Compounds.COATED;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Electronics extends GT6XFeature {
    public static final String FEATURE_NAME = "Electronics";

    public static OreDictPrefix polyGem = OreDictPrefix.createPrefix("polyGem")
        .setCategoryName("polyGems")
        .setLocalItemName("Polycrystalline ", "")
        .setMaterialStats(U)
        .add(TD.Prefix.RECYCLABLE)
        .setCondition(ICondition.FALSE)
        .forceItemGeneration(MT.Si, MT.Ge, MTx.GaAs, MTx.SiGe);
    public static OreDictPrefix oxidizedWafer = OreDictPrefix.createPrefix("oxidizedWafer")
        .setCategoryName("oxidizedWafers")
        .setLocalItemName("Oxidized ", " Wafer")
        .setMaterialStats(U)
        .add(TD.Prefix.RECYCLABLE)
        .setCondition(ICondition.FALSE)
        .forceItemGeneration(MT.Si, MT.Ge, MTx.SiGe);

    public Recipe.RecipeMap Photolithography = null;
    public Recipe.RecipeMap IonBombardment = null;
    public Recipe.RecipeMap Soldering = null;

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
        addRecipeMaps();
        MultiItemsElectronics.instance = new MultiItemsElectronics(Gt6xMod.MOD_ID, "multiitemselectronics");
        Tools.addRefillable(SolderingIron.ID, OreDictToolNames.solderingiron, "Soldering Iron", "Joins together items using a solder (a filler metal)", new SolderingIron(), OreDictToolNames.solderingiron, new OreDictItemData(MT.StainlessSteel, 3*U2, MT.Wood, U2));
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

    private void createPrefixes() {
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.polyGem" , polyGem);
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.oxidizedWafer" , oxidizedWafer);
    }

    private void addRecipeMaps() {
        Photolithography = new Recipe.RecipeMap(null, "gt6x.recipe.photolithography", "Photolithography", null, 0, 1, RES_PATH_GUI+"machines/Photolithography", 2, 1, 2, 1, 1, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        IonBombardment = new Recipe.RecipeMap(null, "gt6x.recipe.ionbombardment", "Ion Acceleration", null, 0, 1, RES_PATH_GUI+"machines/IonBombardment", 1, 1, 1, 1, 2, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        Soldering = new Recipe.RecipeMap(null, "gt6x.recipe.soldering", "Soldering Machine", null, 0, 1, RES_PATH_GUI+"machines/Soldering", 9, 1, 1, 1, 0, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
    }

    private void addMTEs() {
        // Photolithography Machine
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial mat;

        mat = MT.DATA.Electric_T[1]; MTEx.gt6xMTEReg.add("Photolithography Machine"       , "Basic Machines", MTEx.IDs.Photolithography[1].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[1], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[1], NBT_INPUT, V[1], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[3], 'F', plateGem.dat(MT.Glass), 'E', IL .Comp_Laser_Gas_Ar   , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Blue  ], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[1]);
        mat = MT.DATA.Electric_T[2]; MTEx.gt6xMTEReg.add("Photolithography Machine (N-UV)", "Basic Machines", MTEx.IDs.Photolithography[2].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[2], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[2], NBT_INPUT, V[2], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[4], 'F', plate   .dat(MT.Ag   ), 'E', ILx.Comp_Laser_Gas_N    , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[2]);
        mat = MT.DATA.Electric_T[3]; MTEx.gt6xMTEReg.add("Photolithography Machine (M-UV)", "Basic Machines", MTEx.IDs.Photolithography[3].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[3], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[3], NBT_INPUT, V[3], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[5], 'F', plate   .dat(MT.Al   ), 'E', ILx.Comp_Laser_Gas_KrF  , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[3]);
        mat = MT.DATA.Electric_T[4]; MTEx.gt6xMTEReg.add("Photolithography Machine (F-UV)", "Basic Machines", MTEx.IDs.Photolithography[4].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[4], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[4], NBT_INPUT, V[4], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[6], 'F', plate   .dat(MT.Mo   ), 'E', ILx.Comp_Laser_Gas_ArF  , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[4]);
        mat = MT.DATA.Electric_T[5]; MTEx.gt6xMTEReg.add("Photolithography Machine (E-UV)", "Basic Machines", MTEx.IDs.Photolithography[5].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[5], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[5], NBT_INPUT, V[5], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', ILx.CPU       , 'F', plateGem.dat(MT.Si   ), 'E', ILx.Comp_Laser_Molten_Sn, 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[5]);

        for (int tier = 1; tier < 6; tier++) {
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Ion Acceleration Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment[tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, IonBombardment, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP   , NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', OP.casingMachine.dat(mat), 'S', OP.plate.dat(mat), 'T', OP.screw.dat(mat), 'P', pipe.dat(MTx.YAlO3));
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Soldering Machine (" + VN[1] + ")"                , "Basic Machines", MTEx.IDs.Soldering     [tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "soldering"     , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, Soldering     , NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM,                                                                        NBT_ENERGY_ACCEPTED_SIDES, SBIT_U, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"WRW","CMC","PBw", 'M', OP.casingMachineDouble.dat(mat), 'B', IL.CONVEYERS[tier], 'R', IL.ROBOT_ARMS[tier], 'C', OD_CIRCUITS[tier], 'W', MT.DATA.CABLES_01[tier], 'P', pipeTiny.dat(mat));
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Ionizer (" + VN[1] + ")"                          , "Basic Machines", MTEx.IDs.Ionizer       [tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "ionizer"       , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RMx.Ionizer,                                                                                                                                      NBT_TANK_SIDE_IN, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP   , NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"PWI","WTW","wWO", 'P', IL.PUMPS[tier], 'w', MT.DATA.CABLES_04[tier + 1], 'I', pipe.dat(mat), 'T', MTEx.gt6Registry.getItem(10040 + tier), 'O', pipe.dat(MTx.YAlO3));
        }

        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.YAlOTubes.get(), 0, 100, true, false, true, true, false, false, true, MTEx.gt6xMTEReg, MTEx.StoneBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MTx.YAlO3.mMeltingPoint, MTx.YAlO3);
    }

    private void addRecipes() {
        // Y2O3-Al2O3
        RMx.Sintering.addRecipeX(true, 64, 29 , ST.array(ST.tag(2), dustTiny.mat(MT.Al2O3, 1), dustTiny.mat(MTx.Y2O3, 1)), nugget.mat(MTx.YAlO3, 2));
        RMx.Sintering.addRecipeX(true, 64, 64 , ST.array(ST.tag(2), dustSmall.mat(MT.Al2O3, 1), dustSmall.mat(MTx.Y2O3, 1)), chunkGt.mat(MTx.YAlO3, 2));
        RMx.Sintering.addRecipeX(true, 64, 256, ST.array(ST.tag(2), dust.mat(MT.Al2O3, 1), dust.mat(MTx.Y2O3, 1)), ingot.mat(MTx.YAlO3, 2));

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

        // pastes
        RM.Mixer.addRecipe2(true, 16, 64, ILx.Rosin.get(1), dust.mat(MT.SolderingAlloy, 2), MTx.Isopropanol.liquid(2*U, true), MTx.SolderingPaste.liquid(4*U, false), NI);
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.ZnO, 1), MTx.Epoxy.liquid(U, true), FL.make(FLx.ThermalPaste, 288), NI);

        // glass fibres
        CR.shaped(ILx.PlatinumBushing.get(1), CR.DEF_REV, " e ", " P ", "   ", 'P', OP.plate.mat(MT.Pt, 1));

        final long EUt = 16, durationPerUnit = 64*6;
        for (OreDictPrefix tPrefix : OreDictPrefix.VALUES) if (tPrefix != null && tPrefix.containsAny(TD.Prefix.EXTRUDER_FODDER, TD.Prefix.INGOT_BASED, TD.Prefix.GEM_BASED, TD.Prefix.DUST_BASED) && U % tPrefix.mAmount == 0) {
            ItemStack stack = tPrefix.mat(MT.Glass, U / tPrefix.mAmount);
            if (stack != null && stack.stackSize <= stack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, stack, ILx.PlatinumBushing.get(0), ILx.GlassFibres.get(8));
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
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.CCL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, true), NF, ILx.CCL_SMALL.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.GCL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, true), NF, ILx.GCL_SMALL.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.PCL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, true), NF, ILx.PCL_TINY.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.CCL_SMALL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, true), NF, ILx.CCL_TINY.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.GCL_SMALL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, true), NF, ILx.GCL_TINY.get(2));
            RM.Cutter.addRecipe1(true, 16, 16 * multipliers[i], ILx.PCL_SMALL.get(1), FL.mul(cutterFluids[i], multipliers[i] * 16, 1000, true), NF, ILx.PCL_TINY.get(2));
        }

        // Trace etching
        CR.shaped(ILx.EtchMask_Trace.get(1), CR.DEF_REV, "x  ", " P ", "   ", 'P', OP.plate.mat(MT.PVC, 1));
        lens.addListener(event -> new OreDictListenerEvent_Names() { @Override public void addAllListeners() {
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_White] , lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace.get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Yellow], lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace_Small.get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Orange], lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace_Tiny.get(1)));
        }});

        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL      .get(1), ILx.EtchMask_Trace      .get(0), MTx.FeCl3Solution.liquid(17*U2, true), MTx.CuFeClSolution.liquid(9 * U , false), IL .Circuit_Plate_Copper.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL_SMALL.get(1), ILx.EtchMask_Trace_Small.get(0), MTx.FeCl3Solution.liquid(17*U4, true), MTx.CuFeClSolution.liquid(9 * U2, false), ILx.Circuit_Plate_Copper_Small.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL_TINY .get(1), ILx.EtchMask_Trace_Tiny .get(0), MTx.FeCl3Solution.liquid(17*U8, true), MTx.CuFeClSolution.liquid(9 * U4, false), ILx.Circuit_Plate_Copper_Tiny.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL      .get(1), ILx.EtchMask_Trace      .get(0), FL.array(MT.AquaRegia.liquid(13*U2, true)), FL.array(MT.ChloroauricAcid.liquid(3*U , false), MT.NO.gas(U , false), MT.H2O.liquid(3*U , false)), IL .Circuit_Plate_Gold.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL_SMALL.get(1), ILx.EtchMask_Trace_Small.get(0), FL.array(MT.AquaRegia.liquid(13*U4, true)), FL.array(MT.ChloroauricAcid.liquid(3*U2, false), MT.NO.gas(U2, false), MT.H2O.liquid(3*U2, false)), ILx.Circuit_Plate_Gold_Small.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL_TINY .get(1), ILx.EtchMask_Trace_Tiny .get(0), FL.array(MT.AquaRegia.liquid(13*U8, true)), FL.array(MT.ChloroauricAcid.liquid(3*U4, false), MT.NO.gas(U4, false), MT.H2O.liquid(3*U4, false)), ILx.Circuit_Plate_Gold_Tiny.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL      .get(1), ILx.EtchMask_Trace      .get(0), FL.array(MT.AquaRegia.liquid(78*U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78*U8, false), MT.NO.gas(12*U8, false), MT.H2O.liquid(33*U8, false)), IL .Circuit_Plate_Platinum.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL_SMALL.get(2), ILx.EtchMask_Trace_Small.get(0), FL.array(MT.AquaRegia.liquid(78*U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78*U8, false), MT.NO.gas(12*U8, false), MT.H2O.liquid(33*U8, false)), ILx.Circuit_Plate_Platinum_Small.get(2));
        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL_TINY .get(4), ILx.EtchMask_Trace_Tiny .get(0), FL.array(MT.AquaRegia.liquid(78*U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78*U8, false), MT.NO.gas(12*U8, false), MT.H2O.liquid(33*U8, false)), ILx.Circuit_Plate_Platinum_Tiny.get(4));

        /// Semiconductors

        // Hydrides & Polycrystallines
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.stick.mat(MT.Ge, 1), OM.dust(MT.Mo, U3), FL.Water.make(3000), MTx.GeH4.gas(5*U2, false), OM.dust(MTx.MoO3, 4*U3));
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.stick.mat(MT.Ge, 1), OM.dust(MT.Cd, U), FL.Water.make(3000), MTx.GeH4.gas(5*U2, false), OM.dust(MTx.CdO, 2*U));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Mg2Si, 3), MT.HCl.gas(8*U, true), MTx.SiH4.gas(5*U, false), dust.mat(MT.MgCl2, 6));
        RM.Mixer.addRecipe0(true, 16, 60, FL.array(MTx.SiH4.gas(U, true), MTx.GeH4.gas(U, true)), FL.array(MTx.SiGeH8.gas(2*U, false)));

        //TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.GeH4.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MT.Ge, 1));
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.SiH4.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MT.Si, 1));
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.SiGeH8.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MTx.SiGe, 1));
        RM.CrystallisationCrucible.addRecipe1(true, 16, 1800, ST.tag(0), FL.array(MT.Ga.liquid(U2, true), MT.As.gas(U2, true)), ZL_FS, polyGem.mat(MTx.GaAs, 1));

        //TODO NEI only visible from Recipe, not from Usage key
        FluidStack[] cuttingFluids = FL.array(FL.Water.make(1000), FL.SpDew.make(1000), FL.DistW.make(1000), FL.Lubricant.make(1000), FL.LubRoCant.make(1000));
        long[] cuttingMultiplier = new long[] {4, 4, 3, 1, 1};
        @SuppressWarnings({"unchecked", "rawtypes"})
        ICondition cuttingCondition = new ICondition.And(ANTIMATTER.NOT, COATED.NOT);

        for (int i = 0; i < 4; i++) if (cuttingFluids[i] != null) {
            RM.Cutter.add(new RecipeMapHandlerPrefix(polyGem , 1, FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), 64, cuttingMultiplier[i] * 4 * 16, 0, NF, plateGemTiny, 6, NI, NI, true, true, false, cuttingCondition));
        }

        RM.Mixer.addRecipe1(true, 16, 2  , ST.tag(2), FL.array(FL.make_("molten.silicon", 1), FL.make_("molten.germanium",1)), FL.make_("molten.silicongermanium", 2), ZL_IS);
        RM.Mixer.addRecipe1(true, 16, 288, ST.tag(2), FL.array(FL.make_("molten.gallium", 144), MT.As.gas(U, true)), FL.make_("molten.galliumarsenide", 288), ZL_IS);

        // Boules
        for (FluidStack nobleGas : FL.array(MT.He.gas(U, true), MT.Ne.gas(U, true), MT.Ar.gas(U, true), MT.Kr.gas(U, true), MT.Xe.gas(U, true), MT.Rn.gas(U, true))) if (nobleGas != null) {
            // i-type semiconductors
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.GaAs      , 1), FL.array(FL.mul(nobleGas, 1), MT .Ga  .liquid(35*U18,true), MT.As.gas(35*U18, true)), ZL_FS, bouleGt.mat(MTx.GaAs, 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.GaAs      , 1), FL.array(FL.mul(nobleGas, 9), MT .Ga  .liquid(35*U2 ,true), MT.As.gas(35*U2 , true)), ZL_FS, bouleGt.mat(MTx.GaAs, 9));

            // n-type semiconductors
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MT. Si        , 1), FL.array(FL.mul(nobleGas, 1), MT. Si  .liquid(35*U9, true), MTx.PH3.gas(U18, true)), NF, bouleGt.mat(MTx.NDopedSi  , 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MT. Si        , 1), FL.array(FL.mul(nobleGas, 9), MT. Si  .liquid(35*U , true), MTx.PH3.gas(U2 , true)), NF, bouleGt.mat(MTx.NDopedSi  , 9));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.NDopedSi  , 1), FL.array(FL.mul(nobleGas, 1), MT. Si  .liquid(35*U9, true), MTx.PH3.gas(U18, true)), NF, bouleGt.mat(MTx.NDopedSi  , 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.NDopedSi  , 1), FL.array(FL.mul(nobleGas, 9), MT. Si  .liquid(35*U , true), MTx.PH3.gas(U2 , true)), NF, bouleGt.mat(MTx.NDopedSi  , 9));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35*U9, true), MTx.PH3.gas(U18, true)), NF, bouleGt.mat(MTx.NDopedSiGe, 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35*U , true), MTx.PH3.gas(U2 , true)), NF, bouleGt.mat(MTx.NDopedSiGe, 9));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.NDopedSiGe, 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35*U9, true), MTx.PH3.gas(U18, true)), NF, bouleGt.mat(MTx.NDopedSiGe, 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.NDopedSiGe, 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35*U , true), MTx.PH3.gas(U2 , true)), NF, bouleGt.mat(MTx.NDopedSiGe, 9));

            // p-type semiconductors
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MT .Si        , 1), FL.array(FL.mul(nobleGas, 1), MT. Si  .liquid(35*U9, true), MTx.Diborane .gas(U18, true)), NF, bouleGt.mat(MTx.PDopedSi  , 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MT .Si        , 1), FL.array(FL.mul(nobleGas, 9), MT .Si  .liquid(35*U , true), MTx.Diborane .gas(U2 , true)), NF, bouleGt.mat(MTx.PDopedSi  , 9));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.PDopedSi  , 1), FL.array(FL.mul(nobleGas, 1), MT. Si  .liquid(35*U9, true), MTx.Diborane .gas(U18, true)), NF, bouleGt.mat(MTx.PDopedSi  , 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.PDopedSi  , 1), FL.array(FL.mul(nobleGas, 9), MT .Si  .liquid(35*U , true), MTx.Diborane .gas(U2 , true)), NF, bouleGt.mat(MTx.PDopedSi  , 9));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35*U9, true), MTx.Diborane .gas(U18, true)), NF, bouleGt.mat(MTx.PDopedSiGe, 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35*U , true), MTx.Diborane .gas(U2 , true)), NF, bouleGt.mat(MTx.PDopedSiGe, 9));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.PDopedSiGe, 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35*U9, true), MTx.Diborane .gas(U18, true)), NF, bouleGt.mat(MTx.PDopedSiGe, 1));
            RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.PDopedSiGe, 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35*U , true), MTx.Diborane .gas(U2 , true)), NF, bouleGt.mat(MTx.PDopedSiGe, 9));
        }

        // Photoresist
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.DNQ, 1), dust.mat(MTx.PF, 1), MTx.Toluene.liquid(2*U, true), MTx.DnqNovolacResist.liquid(4*U, false), NI);

        // Lasers
        RM.Mixer.addRecipe0(true, 16, 16, FL.array(MT.Kr.gas(U200, true), MT.F.gas(U200, true)), MTx.KrF.gas(U100, false), NI);
        RM.Mixer.addRecipe0(true, 16, 16, FL.array(MT.Ar.gas(U200, true), MT.F.gas(U200, true)), MTx.ArF.gas(U100, false), NI);
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MT.N.gas(U, true), NF, ILx.Comp_Laser_Gas_N.get(1));
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MTx.KrF.gas(U, true), NF, ILx.Comp_Laser_Gas_KrF.get(1));
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MTx.ArF.gas(U, true), NF, ILx.Comp_Laser_Gas_ArF.get(1));
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MT.Sn.liquid(U, true), NF, ILx.Comp_Laser_Molten_Sn.get(1));

        // components
        CR.shaped(ILx.Resistor_ThroughHole.get(2), CR.DEF_REV, " W ", "iPC", " W ", 'W', OP.wireFine.dat(MT.Cu), 'P', plateTiny.dat(MT.Ceramic), 'C', OP.dustTiny.dat(ANY.Coal));
        CR.shaped(ILx.Resistor_ThroughHole.get(2), CR.DEF_REV, " W ", "iPC", " W ", 'W', OP.wireFine.dat(MT.Brass), 'P', OP.plateTiny.dat(MT.Ceramic), 'C', OP.dustTiny.dat(ANY.Coal));

        CR.shaped(ILx.Capacitor_ThroughHole.get(2), CR.DEF_REV, " i ", "PC ", "W W", 'W', OP.wireFine.dat(MT.Cu), 'C', OP.plateTiny.dat(MT.Ceramic), 'P', OP.plateTiny.dat(MT.Paper));
        CR.shaped(ILx.Capacitor_ThroughHole.get(2), CR.DEF_REV, " i ", "PC ", "W W", 'W', OP.wireFine.dat(MT.Brass), 'C', OP.plateTiny.dat(MT.Ceramic), 'P', OP.plateTiny.dat(MT.Paper));

        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Cu), 'S', OP.plateGemTiny.dat(MT.Si), 'P', OP.plateTiny.dat(MT.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Brass), 'S', OP.plateGemTiny.dat(MT.Si), 'P', OP.plateTiny.dat(MT.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Cu), 'S', OP.plateGemTiny.dat(MT.Ge), 'P', OP.plateTiny.dat(MT.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(2), CR.DEF_REV, " P ", "iS ", "WWW", 'W', OP.wireFine.dat(MT.Brass), 'S', OP.plateGemTiny.dat(MT.Ge), 'P', OP.plateTiny.dat(MT.Plastic));

        RMx.Sintering.addRecipeX(true, 16, 64, ST.array(wireFine.mat(MT.Ta, 4), dustSmall.mat(MT.MnO2, 1), dust.mat(MT.Ta2O5, 1)), ILx.Capacitor_Tantalum.get(16));
        RMx.Sintering.addRecipeX(true, 16, 256, ST.array(wireFine.mat(MT.Ta, 16), dust.mat(MT.MnO2, 1), dust.mat(MT.Ta2O5, 4)), ILx.Capacitor_Tantalum.get(64));
        RM.Press.addRecipeX(true, 16, 64, ST.array(plate.mat(MT.Al2O3, 1), foil.mat(MTx.PdAg, 1), foil.mat(MT.Nichrome, 1)), ILx.Resistor_Metal_Film.get(1));

        // Etchants
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.NitratoCericAcid.liquid(U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.CAN, 1));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CAN, 1), MT.HNO3.liquid(5*U, true), MTx.ChromeEtch.liquid(6*U, false), NI);
        //TODO use Thermolyzer
        RM.Drying.addRecipe0(true, 16, 1024, FL.array(MTx.CrNO3Solution.liquid(14*U, true)), FL.array(MT.H2O.liquid(3*U, false), MT.NO.gas(4*U, false)), dust.mat(MTx.Cr2O3, 5), dust.mat(MTx.CAN, 2));

        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.S, 1), MT.F.gas(6*U, true), MTx.SF6.gas(U, false), NI);

        for (OreDictMaterial mat : new OreDictMaterial[] { MTx.CF4, MTx.NF3, MTx.SF6 }) {
            RMx.Ionizer.addRecipe0(true, 64, 20, mat.gas(U100, true), mat.plasma(U100, false), NI);
        }

        // Ru Electroplating
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MT.Ru, 1), MT.Cl.gas(3*U, true), NF, dust.mat(MTx.RuCl3, 4));
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.RuCl3, 8), FL.array(MTx.H3NSO3.liquid(8 * U, true), MT.HCl.gas(4*U, true), water), FL.array(MTx.H3Ru2NCl8H4O2.liquid(20*U, false), MT.SO2.gas(3*U, false)));
        }
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.NH4Cl, 6), FL.array(MTx.H3Ru2NCl8H4O2.liquid(20*U, true)), FL.array(MTx.RuElectrolyte.liquid(20*U, false), MT.HCl.gas(6*U, false)));

        /// Photomasks
        RM.Laminator.addRecipe2(true, 16, 128, plate.mat(MT.Glass, 1), plate.mat(MT.Cr, 1), ILx.Photomask_Raw.get(1));

        // Maskless Lithography
        lens.addListener(event -> new OreDictListenerEvent_Names() { @Override public void addAllListeners() {
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Green]    , lens -> Photolithography.addRecipe2(false, 32  , 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_PMOS_IC.get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Cyan]     , lens -> Photolithography.addRecipe2(false, 128 , 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_NMOS_IC.get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Pink]     , lens -> Photolithography.addRecipe2(false, 512 , 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_CMOS_IC_1.get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Blue]     , lens -> Photolithography.addRecipe2(false, 512 , 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_CMOS_IC_2.get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Red]      , lens -> Photolithography.addRecipe2(false, 2048, 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_VLSI_1    .get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Purple]   , lens -> Photolithography.addRecipe2(false, 2048, 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_VLSI_2    .get(1)));
            addListener(DYE_OREDICTS_LENS[DYE_INDEX_Yellow]   , lens -> Photolithography.addRecipe2(false, 128 , 2000, ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Photomask_Patterned_MESFET.get(1)));
        }});

        // Development
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_PMOS_IC   .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_PMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_PMOS_IC   .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_PMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_NMOS_IC   .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_NMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_NMOS_IC   .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_NMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_CMOS_IC_1 .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_CMOS_IC_1 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_CMOS_IC_1 .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_CMOS_IC_1 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_CMOS_IC_2 .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_CMOS_IC_2 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_CMOS_IC_2 .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_CMOS_IC_2 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_VLSI_1    .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_VLSI_1    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_VLSI_1    .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_VLSI_1    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_VLSI_2    .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_VLSI_2    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_VLSI_2    .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_VLSI_2    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_MESFET    .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Photomask_Developed_MESFET    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Patterned_MESFET    .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Photomask_Developed_MESFET    .get(1));

        // Etching
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_PMOS_IC   .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_PMOS_IC  .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_NMOS_IC   .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_NMOS_IC  .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_CMOS_IC_1 .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_CMOS_IC_1.get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_CMOS_IC_2 .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_CMOS_IC_2.get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_VLSI_1    .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_VLSI_1   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_VLSI_2    .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_VLSI_2   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomask_Developed_MESFET    .get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_Etched_MESFET   .get(1));

        // Cleaning
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_PMOS_IC   .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_PMOS_IC.get(1));
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_NMOS_IC   .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_NMOS_IC.get(1));
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_CMOS_IC_1 .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_CMOS_IC_1.get(1));
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_CMOS_IC_2 .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_CMOS_IC_2.get(1));
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_VLSI_1    .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_VLSI_1    .get(1));
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_VLSI_2    .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_VLSI_2    .get(1));
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomask_Etched_MESFET    .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomask_MESFET.get(1));

        /// Wafers
        // Oxidation
        //TODO not showing up with NEI recipes
        RM.Roasting.add(new RecipeMapHandlerPrefix(plateGem, 1, MT.O.gas(U10, true), 16, 128, 0, NF, oxidizedWafer, 1, NI, NI, true, false, false, cuttingCondition));
        RM.Roasting.addRecipe1(true, 16, 256, ILx.Wafer_Cleaned_CMOS_IC_1.get(1), MT.O.gas(U10, true), NF, ILx.Wafer_Oxidized_CMOS_IC_1.get(1));
        RM.Roasting.addRecipe1(true, 16, 256, ILx.Wafer_Cleaned_VLSI_1   .get(1), MT.O.gas(U10, true), NF, ILx.Wafer_Oxidized_VLSI_1.get(1));

        // SiN CVD on GaAs Wafer
        //TODO use Thermolyzer
        RM.Drying.addRecipe1(true, 64, 256, plateGem.mat(MTx.GaAs, 1), FL.array(MTx.SiH4.gas(15*U1000, true), MT.NH3.gas(4*U1000, true)), MT.H.gas(24*U1000, false), ILx.Wafer_GaAs_SiN_layered.get(1));

        // PL
        Photolithography.addRecipe2(false, 32  , 1024, oxidizedWafer.mat(MTx.PDopedSi, 1), ILx.Photomask_PMOS_IC.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_PMOS_IC.get(1));
        Photolithography.addRecipe2(false, 128 , 1024, oxidizedWafer.mat(MTx.NDopedSi, 1), ILx.Photomask_NMOS_IC.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_NMOS_IC.get(1));
        Photolithography.addRecipe2(false, 512 , 1024, oxidizedWafer.mat(MTx.NDopedSiGe, 1), ILx.Photomask_CMOS_IC_1.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_CMOS_IC_1.get(1));
        Photolithography.addRecipe2(false, 512 , 1024, ILx.Wafer_Oxidized_CMOS_IC_1.get(1), ILx.Photomask_CMOS_IC_2.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_CMOS_IC_2.get(1));
        Photolithography.addRecipe2(false, 2048, 1024, oxidizedWafer.mat(MTx.PDopedSiGe, 1), ILx.Photomask_VLSI_1    .get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_VLSI_1    .get(1));
        Photolithography.addRecipe2(false, 2048, 1024, ILx.Wafer_Oxidized_VLSI_1    .get(1), ILx.Photomask_VLSI_2    .get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_VLSI_2    .get(1));
        Photolithography.addRecipe2(false, 128 , 1024, ILx.Wafer_GaAs_SiN_layered.get(1), ILx.Photomask_MESFET.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_MESFET.get(1));

        // Development
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_PMOS_IC   .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_PMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_PMOS_IC   .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_PMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_NMOS_IC   .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_NMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_NMOS_IC   .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_NMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_CMOS_IC_1 .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_IC_1 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_CMOS_IC_1 .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_IC_1 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_CMOS_IC_2 .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_IC_2 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_CMOS_IC_2 .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_IC_2 .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_VLSI_1    .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_VLSI_1    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_VLSI_1    .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_VLSI_1    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_VLSI_2    .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_VLSI_2    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_VLSI_2    .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_VLSI_2    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_MESFET    .get(1), MTx.NaOHSolution  .liquid(U10, true), NF, ILx.Wafer_Developed_MESFET    .get(1));
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Patterned_MESFET    .get(1), MTx.Na2CO3Solution.liquid(U10, true), NF, ILx.Wafer_Developed_MESFET    .get(1));

        // Oxide Layer Etching
        RM.Bath       .addRecipe1(false, 0, 128, ILx.Wafer_Developed_PMOS_IC   .get(1), FL.array(MT. HF .gas   (12*U1000, true)), FL.array(MT.H2SiF6.liquid(9*U1000, false), MT.H2O.liquid(6*U1000, false)), ILx.Wafer_Etched_PMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Developed_NMOS_IC   .get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafer_Etched_NMOS_IC  .get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Developed_CMOS_IC_1 .get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafer_Etched_CMOS_IC_1.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Developed_CMOS_IC_2 .get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafer_Etched_CMOS_IC_2.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Developed_VLSI_1    .get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafer_Etched_VLSI_1   .get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Developed_VLSI_2    .get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafer_Etched_VLSI_2   .get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Developed_MESFET    .get(1), FL.array(MTx.NF3.plasma(16*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.N  .gas(8*U1000, false)), ILx.Wafer_Etched_MESFET   .get(1));

        // Post-growth doping
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_PMOS_IC   .get(1), MTx.PH3 .gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_PMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_PMOS_IC   .get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_PMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_NMOS_IC   .get(1), MTx.BF3 .gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafer_Doped_NMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_CMOS_IC_1 .get(1), MTx.BF3 .gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_IC_1.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_CMOS_IC_2 .get(1), MTx.PH3 .gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_IC_2.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_CMOS_IC_2 .get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_IC_2.get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_VLSI_1    .get(1), MTx.PH3 .gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_VLSI_1    .get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_VLSI_1    .get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_VLSI_1    .get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_VLSI_2    .get(1), MTx.BF3 .gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafer_Doped_VLSI_2    .get(1));
        IonBombardment.addRecipe1(true, 16, 128, ILx.Wafer_Etched_MESFET    .get(1), MTx.SiF4.gas(30*U1000, true), MTx.AsF3.liquid(32*U1000, false), ILx.Wafer_Doped_MESFET.get(1));

        // Photoresist cleaning
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Doped_PMOS_IC   .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_Cleaned_PMOS_IC  .get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Doped_NMOS_IC   .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_Cleaned_NMOS_IC  .get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Doped_CMOS_IC_1 .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_Cleaned_CMOS_IC_1.get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Doped_CMOS_IC_2 .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_Cleaned_CMOS_IC_2.get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Doped_VLSI_1    .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_Cleaned_VLSI_1   .get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Doped_VLSI_2    .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_Cleaned_VLSI_2   .get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Metal2_MESFET   .get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafer_MESFET           .get(1));

        // Metallization (using physical vapour deposition or electroplating)
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Cleaned_PMOS_IC.get(1), MT.Al.gas(U4, true), NF, ILx.Wafer_Metal_PMOS_IC.get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Metal1_MESFET  .get(1), MT.Al.gas(U4, true), NF, ILx.Wafer_Metal2_MESFET.get(1));

        for (FluidStack water : FL.waters(1000)) {
            RM.Electrolyzer.addRecipe1(true, 32, 16, ILx.Wafer_Cleaned_NMOS_IC.get(1), FL.array(MT.BlueVitriol.liquid(3*U2, true), water), FL.array(MT.H2SO4.liquid(7*U4, false), MT.O.gas(U4, false)), ILx.Wafer_Metal_NMOS_IC.get(1));
            RM.Electrolyzer.addRecipe1(true, 32, 22, ILx.Wafer_Doped_MESFET.get(1), FL.array(MTx.NaAuC2N2.liquid(3*U2, true), FL.mul(water, 3, 2, true)), FL.array(MTx.HCN.gas(3*U2, false), MT.H.gas(U4, false), MT.O.gas(U4, false)), ILx.Wafer_Metal1_MESFET.get(1), dustSmall.mat(MT.NaOH, 3));
        }
        RM.Electrolyzer.addRecipe1(true, 64, 32, ILx.Wafer_Cleaned_CMOS_IC_2.get(1), FL.array(MTx.RuElectrolyte.liquid(20*U8, true)), FL.array(MT.NH3.gas(4*U8, false), MT.Cl.gas(8*U8, false), MT.H2O.liquid(6*U8, false)), ILx.Wafer_Metal_CMOS_IC.get(1));
        RM.Electrolyzer.addRecipe1(true, 64, 32, ILx.Wafer_Cleaned_VLSI_2   .get(1), FL.array(MTx.RuElectrolyte.liquid(20*U8, true)), FL.array(MT.NH3.gas(4*U8, false), MT.Cl.gas(8*U8, false), MT.H2O.liquid(6*U8, false)), ILx.Wafer_Metal_VLSI.get(1));

        // Metal etching
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.HNO3.liquid(U10, true), MTx.H3PO4.liquid(16*U10, true)), MTx.AlEtch.liquid(17*U10, false), NI);
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Metal_PMOS_IC.get(1), FL.array(MTx.AlEtch.liquid(13*U8, true)), FL.array(MT.H2O.liquid(6*U8, false), MT.NO.gas(2*U8, false)), ILx.Wafer_PMOS_IC.get(1), OM.dust(MTx.AlPO4, 6*U8));

        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Metal_NMOS_IC.get(1), MTx.FeCl3Solution.liquid(17*U8, true), MTx.CuFeClSolution.liquid(18*U8, false), ILx.Wafer_NMOS_IC.get(1));

        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Metal_CMOS_IC.get(1), MTx.Ozone.gas(U3, true), MTx.RuO4.gas(5*U8, false), ILx.Wafer_CMOS_IC.get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafer_Metal_VLSI   .get(1), MTx.Ozone.gas(U3, true), MTx.RuO4.gas(5*U8, false), ILx.Wafer_VLSI   .get(1));

        // Dicing
        for (int i = 0; i < 4; i++) if (cuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_PMOS_IC.get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_PMOS_IC.get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_NMOS_IC.get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_NMOS_IC.get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_CMOS_IC.get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_CMOS_IC.get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_VLSI   .get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_VLSI   .get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_MESFET .get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_MESFET .get(64));
        }

        // bonding/packaging
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Au, 1), ILx.Die_PMOS_IC.get(4)), ILx.PMOS_IC.get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Die_NMOS_IC.get(4)), ILx.NMOS_IC.get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Die_CMOS_IC.get(4)), ILx.CMOS_IC.get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Die_VLSI    .get(4)), ILx.CPU.get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), foil.mat(MT.Cu, 1), ILx.Die_MESFET .get(16)), ILx.Transistor_SMD.get(16));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), foil.mat(MT.Cu, 1), ILx.Capacitor_Tantalum.get(16)), ILx.Capacitor_SMD.get(16));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), foil.mat(MT.Sn, 1), ILx.Resistor_Metal_Film.get(1)), ILx.Resistor_SMD.get(16));

        CR.shapeless(ILx.CPU.get(1), new Object[]{ IL.Processor_Crystal_Empty.get(1), ILx.Thermal_Paste.get(1), ILx.CPU.get(1) });

        // hand-soldering PCBs
        CR.shaped(ILx.PCBs[1][0].get(1), CR.DEF_REM, "iE ", "CBR", "   ", 'B', IL .Circuit_Plate_Copper        .get(1), 'E', MultiItemsElectronics.ELECTRONTUBE_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[1][0].get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', IL .Circuit_Plate_Copper        .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[1][1].get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', ILx.Circuit_Plate_Copper_Small  .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[2][0].get(1), CR.DEF_REM, "iT ", "CBR", " T ", 'B', IL .Circuit_Plate_Copper        .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[2][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Copper_Small  .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.PMOS_IC.get(1));
        CR.shaped(ILx.PCBs[3][0].get(1), CR.DEF_REM, "TiT", "CBR", "T T", 'B', IL .Circuit_Plate_Gold          .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[3][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Gold_Small    .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.PMOS_IC.get(1));
        CR.shaped(ILx.PCBs[4][0].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL .Circuit_Plate_Gold          .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.PMOS_IC.get(1));
        CR.shaped(ILx.PCBs[4][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Gold_Small    .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.NMOS_IC.get(1));
        CR.shaped(ILx.PCBs[5][0].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL .Circuit_Plate_Platinum      .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.NMOS_IC.get(1));
        CR.shaped(ILx.PCBs[5][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Platinum_Small.get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.CMOS_IC.get(1));
        CR.shaped(ILx.PCBs[6][0].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL .Circuit_Plate_Platinum      .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.CMOS_IC.get(1));

        // auto-soldering
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(1), IL .Circuit_Plate_Copper        .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[1][0].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(1), ILx.Circuit_Plate_Copper_Small  .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[1][1].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(1), ILx.Circuit_Plate_Copper_Tiny   .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[1][2].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(2), IL .Circuit_Plate_Copper        .get(1), ILx.Transistor_SMD.get(2), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][0].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(2), ILx.Circuit_Plate_Copper_Small  .get(1), ILx.Transistor_SMD.get(2), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][1].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Copper_Tiny   .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.PMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][2].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Copper_Tiny   .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.NMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][2].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Copper_Tiny   .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.CMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][2].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(4), IL .Circuit_Plate_Gold          .get(1), ILx.Transistor_SMD.get(4), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[3][0].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Small    .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.PMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[3][1].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Tiny     .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.NMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[3][2].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), IL .Circuit_Plate_Gold          .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.PMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][0].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Small    .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.NMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][1].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Small    .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.CMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][1].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Tiny     .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.CMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][2].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), IL .Circuit_Plate_Platinum      .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.NMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[5][0].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Platinum_Small.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.CMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[5][1].get(1));
        Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), IL .Circuit_Plate_Platinum      .get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.CMOS_IC.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[6][0].get(1));
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

        // Disable old ways of crafting circuits
        List<ItemStack> circuit_boards = Arrays.asList(IL.Circuit_Board_Basic.get(1), IL.Circuit_Board_Good.get(1), IL.Circuit_Board_Advanced.get(1), IL.Circuit_Board_Elite.get(1), IL.Circuit_Board_Master.get(1), IL.Circuit_Board_Ultimate.get(1));
        List<ItemStack> circuit_parts = Arrays.asList(IL.Circuit_Part_Basic.get(1), IL.Circuit_Part_Good.get(1), IL.Circuit_Part_Advanced.get(1), IL.Circuit_Part_Elite.get(1), IL.Circuit_Part_Master.get(1), IL.Circuit_Part_Ultimate.get(1));
        List<ItemStack> circuit_plates = Arrays.asList(IL.Circuit_Plate_Copper.get(1), IL.Circuit_Plate_Gold.get(1), IL.Circuit_Plate_Platinum.get(1));
        List<ItemStack> circuit_wires = Arrays.asList(IL.Circuit_Wire_Copper.get(1), IL.Circuit_Wire_Gold.get(1), IL.Circuit_Wire_Platinum.get(1));

        CR.BUFFER.removeIf(r ->
            ST.equal(r.getRecipeOutput(), IL.Circuit_Wire_Copper.get(1)) ||
            ST.equal(r.getRecipeOutput(), IL.Processor_Crystal_Empty.get(1))
        );

        for (Recipe r : RM.Bath.mRecipeList) {
            if (r.mOutputs.length < 1) continue;
            for (ItemStack board : circuit_boards) if (ST.equal(board, r.mInputs [0], true)) { r.mEnabled = false; }
        }
        for (Recipe r : RM.Press.mRecipeList) {
            if (r.mOutputs.length < 1) continue;
            for (ItemStack board : circuit_boards) if (ST.equal(board, r.mOutputs[0], true)) { r.mEnabled = false; }
            for (ItemStack part  : circuit_parts ) if (ST.equal(part , r.mOutputs[0], true)) { r.mEnabled = false; }
            for (ItemStack plate : circuit_plates) if (ST.equal(plate, r.mOutputs[0], true)) { r.mEnabled = false; }
        }
        for (Recipe r : RM.LaserEngraver.mRecipeList) {
            if (r.mOutputs.length < 1) continue;
            for (ItemStack wire  : circuit_wires ) if (ST.equal(wire , r.mOutputs[0], true)) { r.mEnabled = false; }
        }
        circuit_boards.forEach(ST::hide);
        circuit_parts.forEach(ST::hide);
        circuit_wires.forEach(ST::hide);
    }
}
