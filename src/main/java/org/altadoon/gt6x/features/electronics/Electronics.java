package org.altadoon.gt6x.features.electronics;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
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
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.common.items.Tools;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.electronics.tools.SolderingIron;

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
        IonBombardment = new Recipe.RecipeMap(null, "gt6x.recipe.ionbombardment", "Ion Bombardment", null, 0, 1, RES_PATH_GUI+"machines/IonBombardment", 1, 1, 1, 1, 1, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
    }

    private void addMTEs() {
        // Photolithography Machine
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial mat;
        mat = MT.DATA.Electric_T[1]; MTEx.gt6xMTEReg.add("Photolithography Machine"       , "Basic Machines", MTEx.IDs.Photolithography1.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[3], 'F', plateGem.dat(MT.Glass), 'E', IL.Comp_Laser_Gas_Ar    , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Blue  ], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[1]);
        mat = MT.DATA.Electric_T[2]; MTEx.gt6xMTEReg.add("Photolithography Machine (N-UV)", "Basic Machines", MTEx.IDs.Photolithography2.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[4], 'F', plate   .dat(MT.Ag   ), 'E', ILx.Comp_Laser_Gas_N    , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[2]);
        mat = MT.DATA.Electric_T[3]; MTEx.gt6xMTEReg.add("Photolithography Machine (M-UV)", "Basic Machines", MTEx.IDs.Photolithography3.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[5], 'F', plate   .dat(MT.Al   ), 'E', ILx.Comp_Laser_Gas_KrF  , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[3]);
        mat = MT.DATA.Electric_T[4]; MTEx.gt6xMTEReg.add("Photolithography Machine (F-UV)", "Basic Machines", MTEx.IDs.Photolithography4.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[6], 'F', plate   .dat(MT.Mo   ), 'E', ILx.Comp_Laser_Gas_ArF  , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[4]);
        mat = MT.DATA.Electric_T[5]; MTEx.gt6xMTEReg.add("Photolithography Machine (E-UV)", "Basic Machines", MTEx.IDs.Photolithography5.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 8192, NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', ILx.SOC       , 'F', plateGem.dat(MT.Si   ), 'E', ILx.Comp_Laser_Molten_Sn, 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[5]);

        mat = MT.DATA.Electric_T[1]; MTEx.gt6xMTEReg.add("Ion Bombardment Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment1.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 4.0F, NBT_RESISTANCE, 4.0F, NBT_INPUT,   32, NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, IonBombardment, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 4 , NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', OP.casingMachine.dat(mat), 'S', OP.plate.dat(mat), 'T', OP.screw.dat(mat), 'P', pipeLarge.dat(MTx.YAlO3));
        mat = MT.DATA.Electric_T[2]; MTEx.gt6xMTEReg.add("Ion Bombardment Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment2.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 4.0F, NBT_RESISTANCE, 4.0F, NBT_INPUT,  128, NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, IonBombardment, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 8 , NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', OP.casingMachine.dat(mat), 'S', OP.plate.dat(mat), 'T', OP.screw.dat(mat), 'P', pipeLarge.dat(MTx.YAlO3));
        mat = MT.DATA.Electric_T[3]; MTEx.gt6xMTEReg.add("Ion Bombardment Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment3.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 4.0F, NBT_RESISTANCE, 4.0F, NBT_INPUT,  512, NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, IonBombardment, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 16, NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', OP.casingMachine.dat(mat), 'S', OP.plate.dat(mat), 'T', OP.screw.dat(mat), 'P', pipeLarge.dat(MTx.YAlO3));
        mat = MT.DATA.Electric_T[4]; MTEx.gt6xMTEReg.add("Ion Bombardment Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment4.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 4.0F, NBT_RESISTANCE, 4.0F, NBT_INPUT, 2048, NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, IonBombardment, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 32, NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', OP.casingMachine.dat(mat), 'S', OP.plate.dat(mat), 'T', OP.screw.dat(mat), 'P', pipeLarge.dat(MTx.YAlO3));
        mat = MT.DATA.Electric_T[5]; MTEx.gt6xMTEReg.add("Ion Bombardment Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment5.get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 4.0F, NBT_RESISTANCE, 4.0F, NBT_INPUT, 8192, NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, IonBombardment, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP, NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 64, NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', OP.casingMachine.dat(mat), 'S', OP.plate.dat(mat), 'T', OP.screw.dat(mat), 'P', pipeLarge.dat(MTx.YAlO3));

        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.YAlOTubes.get(), 0, 100, true, false, true, true, false, false, true, MTEx.gt6xMTEReg, MTEx.StoneBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MTx.YAlO3.mMeltingPoint, MTx.YAlO3);
    }

    private void addRecipes() {
        // Y2O3-Al2O3
        RMx.sintering.addRecipeX(true, 64, 29 , ST.array(ST.tag(2), dustTiny.mat(MT.Al2O3, 1), dustTiny.mat(MTx.Y2O3, 1)), nugget.mat(MTx.YAlO3, 2));
        RMx.sintering.addRecipeX(true, 64, 64 , ST.array(ST.tag(2), dustSmall.mat(MT.Al2O3, 1), dustSmall.mat(MTx.Y2O3, 1)), chunkGt.mat(MTx.YAlO3, 2));
        RMx.sintering.addRecipeX(true, 64, 256, ST.array(ST.tag(2), dust.mat(MT.Al2O3, 1), dust.mat(MTx.Y2O3, 1)), ingot.mat(MTx.YAlO3, 2));

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
        CR.shaped(ILx.EtchMask_Trace.get(1), CR.DEF_REV, "   ", " P ", " x ", 'P', OP.plate.mat(MT.PVC, 1));

        RM.Bath.addRecipeX(true, 0, 256, ST.array(ILx.CCL.get(1), ILx.EtchMask_Trace.get(0), OP.dust.mat(MT.FeCl3, 4)), MT.DistWater.liquid(9*U2, true), MTx.CuFeClSolution.liquid(9*U, false), IL.Circuit_Plate_Copper.get(1));
        RM.Bath.addRecipe2(true, 0, 256, ILx.GCL.get(1), ILx.EtchMask_Trace.get(0), FL.array(MT.AquaRegia.liquid(13*U2, true)), FL.array(MT.ChloroauricAcid.liquid(3*U, false), MT.NO.gas(U, false), MT.H2O.liquid(3*U, false)), IL.Circuit_Plate_Gold.get(1));
        RM.Bath.addRecipe2(true, 0, 256, ILx.PCL.get(1), ILx.EtchMask_Trace.get(0), FL.array(MT.AquaRegia.liquid(78*U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(36*U8, false), MT.NO.gas(12*U8, F), FL.Water.make(4125)), IL.Circuit_Plate_Platinum.get(1));

        RM.Mixer.addRecipe1(true, 16, 64*9, OP.dust.mat(MT.Fe, 1), MTx.CuFeClSolution.liquid(18*U, true), MTx.FeCl2Solution.liquid(18*U, false), OP.dust.mat(MT.Cu, 1));

        /// Semiconductors

        // Hydrides & Polycrystallines
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.stick.mat(MT.Ge, 1), OM.dust(MT.Mo, U3), FL.Water.make(3000), MTx.GeH4.gas(5*U2, false), OM.dust(MTx.MoO3, 4*U3));
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.stick.mat(MT.Ge, 1), OM.dust(MT.Cd, U), FL.Water.make(3000), MTx.GeH4.gas(5*U2, false), OM.dust(MTx.CdO, 2*U));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Mg2Si, 3), MT.HCl.gas(8*U, true), MTx.SiH4.gas(5*U, false), dust.mat(MT.MgCl2, 6));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.SiH4.gas(U, true), MTx.GeH4.gas(U, true)), FL.array(MTx.SiGeH8.gas(2*U, false)));

        //TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.GeH4.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MT.Ge, 1));
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.SiH4.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MT.Si, 1));
        RM.Drying.addRecipe1(true, 16, 64000, ST.tag(0), MTx.SiGeH8.gas(5*U, true), MT.H.gas(4*U, false), polyGem.mat(MTx.SiGe, 1));
        RM.CrystallisationCrucible.addRecipe1(true, 16, 1800, ST.tag(0), FL.array(MT.Ga.liquid(U2, true), MT.As.gas(U2, true)), ZL_FS, polyGem.mat(MTx.GaAs, 1));

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

        // Chrome Etching
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.NitratoCericAcid.liquid(U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.CAN, 1));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CAN, 1), MT.HNO3.liquid(5*U, true), MTx.ChromeEtch.liquid(6*U, false), NI);
        //TODO use Thermolyzer
        RM.Drying.addRecipe0(true, 16, 1024, FL.array(MTx.CrNO3Solution.liquid(14*U, true)), FL.array(MT.H2O.liquid(3*U, false), MT.NO.gas(4*U, false)), dust.mat(MTx.Cr2O3, 5), dust.mat(MTx.CAN, 2));

        // Photomasks
        RM.Laminator.addRecipe2(true, 16, 128, plate.mat(MT.Glass, 1), plate.mat(MT.Cr, 1), ILx.Photomask_Raw.get(1));

        CR.shaped(ILx.EtchMask_Photomask_PMOS_IC.get(1), CR.DEF_REV, "   ", " Px", "   ", 'P', OP.plate.mat(MT.PVC, 1));
        RM.Bath.addRecipe2(false, 0, 256, ILx.Photomask_Raw.get(1), ILx.EtchMask_Photomask_PMOS_IC.get(0), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomask_PMOS_IC.get(1));
        //TODO pattern other masks using PL?

        // Wafer oxidation
        RM.Roasting.add(new RecipeMapHandlerPrefix(plateGem, 1, MT.O.gas(U10, true), 16, 1024, 0, NF, oxidizedWafer, 1, NI, NI, true, false, false, cuttingCondition));
        RM.Roasting.addRecipe1(true, 16, 256, ILx.Wafer_Doped_CMOS_IC_1.get(1), MT.O.gas(U10, true), NF, ILx.Wafer_Oxidized_CMOS_IC_1.get(1));
        RM.Roasting.addRecipe1(true, 16, 256, ILx.Wafer_Doped_CMOS_SOC_1.get(1), MT.O.gas(U10, true), NF, ILx.Wafer_Oxidized_CMOS_SOC_1.get(1));

        // SiN CVD on GaAs Wafer
        //TODO use Thermolyzer
        RM.Drying.addRecipe1(true, 64, 256, plateGem.mat(MTx.GaAs, 1), FL.array(MTx.SiH4.gas(15*U1000, true), MT.NH3.gas(4*U1000, true)), MT.H.gas(24*U1000, false), ILx.Wafer_GaAs_SiN_layered.get(1));

        // PL
        Photolithography.addRecipe2(false, 32  , 1024, oxidizedWafer.mat(MTx.PDopedSi, 1), ILx.Photomask_PMOS_IC.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_PMOS_IC.get(1));
        Photolithography.addRecipe2(false, 128 , 1024, oxidizedWafer.mat(MTx.NDopedSi, 1), ILx.Photomask_NMOS_IC.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_NMOS_IC.get(1));
        Photolithography.addRecipe2(false, 512 , 1024, oxidizedWafer.mat(MTx.NDopedSiGe, 1), ILx.Photomask_CMOS_IC_1.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_CMOS_IC_1.get(1));
        Photolithography.addRecipe2(false, 512 , 1024, ILx.Wafer_Oxidized_CMOS_IC_1.get(1), ILx.Photomask_CMOS_IC_2.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_CMOS_IC_2.get(1));
        Photolithography.addRecipe2(false, 2048, 1024, oxidizedWafer.mat(MTx.PDopedSiGe, 1), ILx.Photomask_CMOS_SOC_1.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_CMOS_SOC_1.get(1));
        Photolithography.addRecipe2(false, 2048, 1024, ILx.Wafer_Oxidized_CMOS_SOC_1.get(1), ILx.Photomask_CMOS_SOC_2.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_CMOS_SOC_2.get(1));
        Photolithography.addRecipe2(false, 128 , 1024, ILx.Wafer_GaAs_SiN_layered.get(1), ILx.Photomask_MESFET.get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafer_Patterned_MESFET.get(1));

        // Development
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_PMOS_IC   .get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_PMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_NMOS_IC   .get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_NMOS_IC   .get(1));
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_CMOS_IC_1 .get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_IC_1 .get(1));
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_CMOS_IC_2 .get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_IC_2 .get(1));
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_CMOS_SOC_1.get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_SOC_1.get(1));
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_CMOS_SOC_2.get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_CMOS_SOC_2.get(1));
        RM.Bath.addRecipe1(false, 0, 1024, ILx.Wafer_Patterned_MESFET    .get(1), MTx.NaOHSolution.liquid(U10, true), NF, ILx.Wafer_Developed_MESFET    .get(1));

        // Oxide Etching
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafer_Developed_PMOS_IC.get(1), MT.HF.gas(U200, true), NF, ILx.Wafer_Etched_PMOS_IC.get(1));
        //TODO plasma etching

        // Post-growth doping
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_PMOS_IC.get(1), MTx.PH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_PMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_PMOS_IC.get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_PMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_NMOS_IC.get(1), MTx.BF3.gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafer_Doped_NMOS_IC.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_CMOS_IC_1.get(1), MTx.BF3.gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_IC_1.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_CMOS_IC_2.get(1), MTx.PH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_IC_2.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_CMOS_IC_2.get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_IC_2.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_CMOS_SOC_1.get(1), MTx.PH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_SOC_1.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_CMOS_SOC_1.get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_SOC_1.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_CMOS_SOC_2.get(1), MTx.BF3.gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafer_Doped_CMOS_SOC_2.get(1));
        IonBombardment.addRecipe1(true, 32, 128, ILx.Wafer_Etched_MESFET.get(1), MTx.SiF4.gas(30*U1000, true), MTx.AsF3.liquid(32*U1000, false), ILx.Wafer_Doped_MESFET.get(1));

        //TODO Metallization (using metal vapour deposition)

        //TODO Metal etching
        RM.Bath.addRecipe1(false, 0, 256, ILx.Wafer_Metal2_MESFET.get(1), FL.Water.make(1000), NF, ILx.Wafer_MESFET.get(1));

        // Dicing
        for (int i = 0; i < 4; i++) if (cuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_PMOS_IC.get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_PMOS_IC.get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_NMOS_IC.get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_NMOS_IC.get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_CMOS_IC.get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_CMOS_IC.get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_SOC    .get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_SOC    .get(16));
            RM.Cutter.addRecipe1(true, 16, cuttingMultiplier[i] * 64, ILx.Wafer_MESFET .get(1), FL.mul(cuttingFluids[i], cuttingMultiplier[i] * 16, 1000, true), NF, ILx.Die_MESFET .get(64));
        }

        //TODO bonding/packaging

        // circuits
        CR.shaped(IL.Circuit_Basic   .get(1), CR.DEF_REM, "iE ", "CBR", "   ", 'B', IL.Circuit_Plate_Copper  .get(1), 'E', MultiItemsElectronics.ELECTRONTUBE_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Basic   .get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', IL.Circuit_Plate_Copper  .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Good    .get(1), CR.DEF_REM, "iT ", "CBR", " T ", 'B', IL.Circuit_Plate_Copper  .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Advanced.get(1), CR.DEF_REM, "TiT", "CBR", "T T", 'B', IL.Circuit_Plate_Gold    .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(IL.Circuit_Elite   .get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL.Circuit_Plate_Gold    .get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.PMOS_IC.get(1));
        CR.shaped(IL.Circuit_Master  .get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL.Circuit_Plate_Platinum.get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.NMOS_IC.get(1));
        CR.shaped(IL.Circuit_Ultimate.get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL.Circuit_Plate_Platinum.get(1), 'T', MultiItemsElectronics.TRANSISTOR_NAME  , 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.CMOS_IC.get(1));
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
