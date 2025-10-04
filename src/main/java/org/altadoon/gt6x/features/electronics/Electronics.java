package org.altadoon.gt6x.features.electronics;

import com.google.common.collect.Iterables;
import gregapi.code.ICondition;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.oredict.event.OreDictListenerEvent_Names;
import gregapi.recipes.Recipe;
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
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;
import static org.altadoon.gt6x.common.items.ILx.NUM_COMPUTER_TIERS;
import static org.altadoon.gt6x.common.items.ILx.NUM_SOLAR_STAGES_MULTI_JUNCTION;
import static org.altadoon.gt6x.features.electronics.MultiItemsElectronics.*;
import static org.altadoon.gt6x.features.electronics.MultiItemsPhotolithography.*;

public class Electronics extends GT6XFeature {
    public static final String FEATURE_NAME = "Electronics";

    private static final OreDictMaterial[] PolyGemMaterials = { MT.Si, MTx.PDopedSi, MTx.NDopedSi, MT.Ge, MTx.PDopedGe, MTx.NDopedGe, MTx.SiGe, MTx.PDopedSiGe, MTx.NDopedSiGe, MTx.GaAs, MTx.PDopedGaAs, MTx.NDopedGaAs };
    public static OreDictPrefix polyGem = OreDictPrefix.createPrefix("polyGem")
        .setCategoryName("polyGems")
        .setLocalItemName("Polycrystalline ", "")
        .setMaterialStats(U)
        .add(TD.Prefix.RECYCLABLE, TD.Prefix.TOOLTIP_MATERIAL, TD.Prefix.UNIFICATABLE)
        .setCondition(ICondition.FALSE)
        .forceItemGeneration(PolyGemMaterials);

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void preInit() {
        createPrefixes();
        MultiItemsElectronics.instance = new MultiItemsElectronics(Gt6xMod.MOD_ID, "multiitemselectronics");
        MultiItemsPhotolithography.instance = new MultiItemsPhotolithography(Gt6xMod.MOD_ID, "multiitemsphotolithography");
        Tools.addRefillable(SolderingIron.ID, OreDictToolNames.solderingiron, "Soldering Iron", "Joins together items using a solder (a filler metal)", new SolderingIron(), OreDictToolNames.solderingiron, new OreDictItemData(MT.StainlessSteel, 3*U2, MT.Wood, U2));
    }

    @Override
    public void afterGt6PreInit() {
        addOredictReRegistrations();
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
    public void afterGt6PostInit() {
        changeRecipes();
        addFusionRecipes();
    }

    private void createPrefixes() {
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.polyGem" , polyGem);
    }

    private void addOredictReRegistrations() {
        OreDictManager.INSTANCE.addReRegistration(IC_NAMES[1], IC_NAMES[0]);
        OreDictManager.INSTANCE.addReRegistration(IC_NAMES[2], IC_NAMES[1]);
        OreDictManager.INSTANCE.addReRegistration(PC_NAMES[1], PC_NAMES[0]);
        OreDictManager.INSTANCE.addReRegistration(PC_NAMES[2], PC_NAMES[1]);
        OreDictManager.INSTANCE.addReRegistration(SOC_NAMES[1], SOC_NAMES[0]);
        OreDictManager.INSTANCE.addReRegistration(SOC_NAMES[2], SOC_NAMES[1]);
        OreDictManager.INSTANCE.addReRegistration(FLASH_NAMES[1], FLASH_NAMES[0]);
        OreDictManager.INSTANCE.addReRegistration(FLASH_NAMES[2], FLASH_NAMES[1]);
        OreDictManager.INSTANCE.addReRegistration(SCREEN_NAMES[1], SCREEN_NAMES[0]);
        OreDictManager.INSTANCE.addReRegistration(SCREEN_NAMES[2], SCREEN_NAMES[1]);
    }

    private void addMTEs() {
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial mat;

        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.YAlOTubes.get(), 26142, 100, true, false, true, true, false, false, true, true, MTEx.gt6xMTEReg, MTEx.StoneBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MTx.YAlO3.mMeltingPoint, MTx.YAlO3);

        mat = MT.DATA.Electric_T[1]; MTEx.gt6xMTEReg.add("Photolithography Machine"       , "Basic Machines", MTEx.IDs.Photolithography[1].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[1], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[1], NBT_INPUT, V[1], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, RMx.Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[3], 'F', plateGem.dat(MT.Glass), 'E', IL .Comp_Laser_Gas_Ar   , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Blue  ], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[1]);
        mat = MT.DATA.Electric_T[2]; MTEx.gt6xMTEReg.add("Photolithography Machine (N-UV)", "Basic Machines", MTEx.IDs.Photolithography[2].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[2], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[2], NBT_INPUT, V[2], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, RMx.Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', OD_CIRCUITS[4], 'F', plate   .dat(MT.Ag   ), 'E', ILx.Comp_Laser_Gas_N    , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[2]);
        mat = MT.DATA.Electric_T[3]; MTEx.gt6xMTEReg.add("Photolithography Machine (M-UV)", "Basic Machines", MTEx.IDs.Photolithography[3].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[3], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[3], NBT_INPUT, V[3], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, RMx.Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', PC_NAMES   [0], 'F', plate   .dat(MT.Al   ), 'E', ILx.Comp_Laser_Gas_KrF  , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[3]);
        mat = MT.DATA.Electric_T[4]; MTEx.gt6xMTEReg.add("Photolithography Machine (F-UV)", "Basic Machines", MTEx.IDs.Photolithography[4].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[4], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[4], NBT_INPUT, V[4], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, RMx.Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', PC_NAMES   [1], 'F', plate   .dat(MT.Mo   ), 'E', ILx.Comp_Laser_Gas_ArF  , 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[4]);
        mat = MT.DATA.Electric_T[5]; MTEx.gt6xMTEReg.add("Photolithography Machine (E-UV)", "Basic Machines", MTEx.IDs.Photolithography[5].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[5], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[5], NBT_INPUT, V[5], NBT_TEXTURE, "photolithography", NBT_ENERGY_ACCEPTED, TD.Energy.LU, NBT_RECIPEMAP, RMx.Photolithography, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM, NBT_TANK_SIDE_OUT, SBIT_R, NBT_ENERGY_ACCEPTED_SIDES, SBIT_U), "ELP", "FMF", "OBC", 'P', PC_NAMES   [2], 'F', plateGem.dat(MT.Si   ), 'E', ILx.Comp_Laser_Molten_Sn, 'O', gearGtSmall.dat(mat), 'M', OP.casingMachineDouble.dat(mat), 'L', DYE_OREDICTS_LENS[DYE_INDEX_Purple], 'B', bolt.dat(mat), 'C', IL.CONVEYERS[5]);

        for (int tier = 1; tier < 6; tier++) {
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Ion Acceleration Chamber (" + mat.getLocal() + ")", "Basic Machines", MTEx.IDs.IonBombardment[tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "ionbombardment", NBT_ENERGY_ACCEPTED, TD.Energy.MU, NBT_RECIPEMAP, RMx.IonBombardment  , NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP   , NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"TPT","wMh","TST", 'M', casingMachine.dat(mat), 'S', plate.dat(mat), 'T', screw.dat(mat), 'P', pipeMedium.dat(MTx.YAlO3));
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Soldering Machine (" + VN[tier] + ")"             , "Basic Machines", MTEx.IDs.Soldering     [tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "soldering"     , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RMx.Soldering       , NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_BOTTOM,                                                                        NBT_ENERGY_ACCEPTED_SIDES, SBIT_U, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"WRW","CMC","PBw", 'M', casingMachineDouble.dat(mat), 'B', IL.CONVEYERS[tier], 'R', IL.ROBOT_ARMS[tier], 'C', OD_CIRCUITS[tier], 'W', MT.DATA.CABLES_01[tier], 'P', pipeTiny.dat(mat));
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Ionizer (" + VN[tier] + ")"                       , "Basic Machines", MTEx.IDs.Ionizer       [tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "ionizer"       , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RMx.Ionizer         ,                                                                                                                                      NBT_TANK_SIDE_IN, SBIT_D|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP   , NBT_TANK_SIDE_OUT, SBIT_D|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BOTTOM, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"PWI","WTW","wWO", 'P', IL.PUMPS[tier], 'w', MT.DATA.CABLES_04[tier + 1], 'I', pipeMedium.dat(mat), 'T', MTEx.gt6MTEReg.getItem(10040 + tier), 'O', pipeMedium.dat(MTx.YAlO3));
            mat = MT.DATA.Electric_T[tier]; MTEx.gt6xMTEReg.add("Vacuum Chamber (" + mat.getLocal() + ")"          , "Basic Machines", MTEx.IDs.VacuumChamber [tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_ELECTRIC[tier], NBT_RESISTANCE, MTEx.HARDNESS_ELECTRIC[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "vacuumchamber" , NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RMx.VacuumChamber, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_D|SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_TOP   , NBT_TANK_SIDE_OUT, SBIT_B|SBIT_R, NBT_TANK_SIDE_AUTO_OUT, SIDE_BACK  , NBT_ENERGY_ACCEPTED_SIDES, SBIT_D, NBT_PARALLEL, 2 << tier , NBT_PARALLEL_DURATION, T),"TwT","RMR","VPV", 'M', casingMachineQuadruple.dat(mat), 'T', pipeSmall.dat(mat), 'R', ring.dat(ANY.Rubber), 'V', IL.PUMPS[tier], 'P', pipeMedium.dat(mat));
        }

        // Solar Panels
        aClass = MultiTileEntitySolarPanelX.class;
        mat = MT.DATA.Electric_T[0]; MTEx.gt6xMTEReg.add("Solar Panel (Polycrystalline Silicon)", "Solar Panels", MTEx.IDs.SolarPanelPolySi.get(), 10050, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_OUTPUT, MultiTileEntitySolarPanelX.polySiOutput, NBT_ENERGY_EMITTED, TD.Energy.EU), "APA", "WMW", "FCF", 'M', OP.casingMachine.dat(mat), 'C', OP.cableGt01.dat(ANY.Cu           ), 'P', OD_CIRCUITS[1], 'A', dustSmall.dat(MT.Ag), 'W', ILx.SolarWafers[0][0], 'F', foil.dat(MT.Al));
        mat = MT.DATA.Electric_T[1]; MTEx.gt6xMTEReg.add("Solar Panel (Monocrystalline Silicon)", "Solar Panels", MTEx.IDs.SolarPanelMonoSi.get(), 10050, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_OUTPUT, MultiTileEntitySolarPanelX.monoSiOutput, NBT_ENERGY_EMITTED, TD.Energy.EU), "APA", "WMW", "FCF", 'M', OP.casingMachine.dat(mat), 'C', OP.cableGt01.dat(ANY.Cu           ), 'P', OD_CIRCUITS[2], 'A', dustSmall.dat(MT.Ag), 'W', ILx.SolarWafers[1][3], 'F', foil.dat(MT.Al));
        mat = MT.DATA.Electric_T[2]; MTEx.gt6xMTEReg.add("Solar Panel (Multi-Junction)"         , "Solar Panels", MTEx.IDs.SolarPanelMJ    .get(), 10050, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_OUTPUT, MultiTileEntitySolarPanelX.GaAsOutput  , NBT_ENERGY_EMITTED, TD.Energy.EU), " P ", "WMW", " C ", 'M', OP.casingMachine.dat(mat), 'C', OP.cableGt01.dat(MT.AnnealedCopper), 'P', OD_CIRCUITS[6], 'W', ILx.SolarWafers[4][NUM_SOLAR_STAGES_MULTI_JUNCTION - 1]);
    }

    private void addRecipes() {
        // Y2O3-Al2O3
        RMx.Sintering.addRecipeX(true, 64, 29, ST.array(ST.tag(2), dustTiny.mat(MT.Al2O3, 5), dustTiny.mat(MTx.Y2O3, 2)), nugget.mat(MTx.YAlO3, 7));
        RMx.Sintering.addRecipeX(true, 64, 64, ST.array(ST.tag(2), dustSmall.mat(MT.Al2O3, 5), dustSmall.mat(MTx.Y2O3, 2)), chunkGt.mat(MTx.YAlO3, 7));
        RMx.Sintering.addRecipeX(true, 64, 256, ST.array(ST.tag(2), dust.mat(MT.Al2O3, 5), dust.mat(MTx.Y2O3, 2)), ingot.mat(MTx.YAlO3, 7));

        // electron tube stuff
        RM.Mixer.addRecipeX(true, 16, 64, ST.array(dust    .mat(MTx.BaCO3, 6), dust    .mat(MT.CaCO3, 2), dust    .mat(MTx.SrCO3, 1)), dust.mat(MTx.BaSrCaCO3, 9));
        RM.Mixer.addRecipeX(true, 16, 16, ST.array(dustTiny.mat(MTx.BaCO3, 6), dustTiny.mat(MT.CaCO3, 2), dustTiny.mat(MTx.SrCO3, 1)), dust.mat(MTx.BaSrCaCO3, 1));

        RM.Press.addRecipeX(true, 16, 64, ST.array(OP.wireFine.mat(MT.Mo, 1), OP.bolt.mat(MT.Mo, 2), OP.wireGt01.mat(MT.Constantan, 2)), ILx.Filament_Molybdenum.get(1));
        for (OreDictMaterial mat : ANY.W.mToThis) {
        RM.Press.addRecipeX(true, 16, 64, ST.array(OP.wireFine.mat(mat, 1), OP.bolt.mat(mat, 2), OP.wireGt01.mat(MT.Nichrome, 2)), ILx.Filament_Tungsten.get(1));
        }

        RMx.Thermolysis.addRecipe2(true, 16, 128, ILx.Filament_Molybdenum.get(1), dustSmall.mat(MTx.BaCO3    , 1), NF, MT.CO2.gas(3*U20, false), ILx.Cathode_Molybdenum.get(1));
        RMx.Thermolysis.addRecipe2(true, 16, 128, ILx.Filament_Molybdenum.get(1), dustSmall.mat(MTx.BaSrCaCO3, 1), NF, MT.CO2.gas(3*U20, false), ILx.Cathode_Molybdenum.get(1));
        RMx.Thermolysis.addRecipe2(true, 16, 128, ILx.Filament_Tungsten  .get(1), dustSmall.mat(MTx.BaSrCaCO3, 1), NF, MT.CO2.gas(3*U20, false), ILx.Cathode_Tungsten  .get(1));

        RM.Laminator.addRecipe2(true, 16, 128, OP.plateGem.mat(MT.Glass   , 1), ILx.Cathode_Molybdenum.get(8), ILx.ElectronTube_Molybdenum.get(8));
        RM.Laminator.addRecipe2(true, 16, 64 , OP.casingSmall.mat(MT.Glass, 1), ILx.Cathode_Molybdenum.get(4), ILx.ElectronTube_Molybdenum.get(4));
        RM.Laminator.addRecipe2(true, 16, 48 , ST.make(Blocks.glass_pane  , 1, W), ILx.Cathode_Molybdenum.get(1), ILx.ElectronTube_Molybdenum.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, OP.plateGem.mat(MT.Glass   , 1), ILx.Cathode_Tungsten.get(8), ILx.ElectronTube_Tungsten.get(8));
        RM.Laminator.addRecipe2(true, 16, 64 , OP.casingSmall.mat(MT.Glass, 1), ILx.Cathode_Tungsten.get(4), ILx.ElectronTube_Tungsten.get(4));
        RM.Laminator.addRecipe2(true, 16, 48 , ST.make(Blocks.glass_pane  , 1, W), ILx.Cathode_Tungsten.get(1), ILx.ElectronTube_Tungsten.get(1));

        // soldering iron
        CR.shaped(Tools.refillableMetaTool.make(SolderingIron.ID_EMPTY), CR.DEF_MIR, "Ph ", "fC ", " sS", 'P', OP.pipeTiny.mat(MT.StainlessSteel, 1), 'C', OP.plateCurved.mat(MT.StainlessSteel, 1), 'S', OD.stickAnyWood);

        // pastes
        RM.Mixer.addRecipe2(true, 16, 64, ILx.Rosin.get(1), dust.mat(MT.SolderingAlloy, 2), MTx.Isopropanol.liquid(2 * U, true), MTx.SolderingPaste.liquid(4 * U, false), NI);
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.ZnO, 1), MTx.Epoxy.liquid(U, true), FL.make(FLx.ThermalPaste, 288), NI);

        // glass fibres
        CR.shaped(ILx.PlatinumBushing.get(1), CR.DEF_REV, " e ", " P ", "   ", 'P', OP.plate.mat(MT.Pt, 1));

        final long EUt = 16, durationPerUnit = 64 * 6;
        for (OreDictPrefix tPrefix : OreDictPrefix.VALUES)
            if (tPrefix != null && tPrefix.containsAny(TD.Prefix.EXTRUDER_FODDER, TD.Prefix.INGOT_BASED, TD.Prefix.GEM_BASED, TD.Prefix.DUST_BASED) && U % tPrefix.mAmount == 0) {
                ItemStack stack = tPrefix.mat(MT.Glass, U / tPrefix.mAmount);
                if (stack != null && stack.stackSize <= stack.getMaxStackSize()) {
                    RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, stack, ILx.PlatinumBushing.get(0), ILx.GlassFibres.get(8));
                }
            }

        // boards
        CR.shapeless(ILx.Plywood.get(3), new Object[]{ plate.dat(ANY.Wood), plate.dat(ANY.Wood), plate.dat(ANY.Wood), IL.Bottle_Glue });
        CR.shaped(ILx.Circuit_Plate_Wood.get(1), CR.DEF_REV, "WWW", "WBW", "WWW", 'W', wireFine.dat(ANY.Cu), 'B', ILx.Plywood);
        RM.Laminator.addRecipe2(true, 16, 128, OP.foil.mat(MTx.PF, 4), ST.make(Items.paper, 1, W), ILx.FR1_Board.get(1));
        RM.Bath.addRecipe1(true, 16, 128, ILx.GlassFibres.get(1), MTx.Epoxy.liquid(U, true), NF, ILx.FR4_Board.get(1));

        // Copper-clad laminates e.a.
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FR1_Board.get(1), OP.foil.mat(MT.Cu, 4), ILx.CCL.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FR4_Board.get(1), OP.foil.mat(MT.Cu, 4), ILx.CCL.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FR4_Board.get(1), OP.foil.mat(MT.Au, 4), ILx.GCL.get(1));
        RM.Laminator.addRecipe2(true, 16, 128, ILx.FR4_Board.get(1), OP.foil.mat(MT.Pt, 4), ILx.PCL.get(1));

        CR.shaped(ILx.CCL_SMALL.get(2), CR.DEF_REV, " s ", " P ", "   ", 'P', ILx.CCL.get(1));
        CR.shaped(ILx.GCL_SMALL.get(2), CR.DEF_REV, " s ", " P ", "   ", 'P', ILx.GCL.get(1));
        CR.shaped(ILx.PCL_SMALL.get(2), CR.DEF_REV, " s ", " P ", "   ", 'P', ILx.PCL.get(1));
        CR.shaped(ILx.CCL_LONG.get(2), CR.DEF_REV, "   ", "sP ", "   ", 'P', ILx.CCL.get(1));
        CR.shaped(ILx.GCL_LONG.get(2), CR.DEF_REV, "   ", "sP ", "   ", 'P', ILx.GCL.get(1));
        CR.shaped(ILx.PCL_LONG.get(2), CR.DEF_REV, "   ", "sP ", "   ", 'P', ILx.PCL.get(1));
        CR.shaped(ILx.CCL_TINY.get(2), CR.DEF_REV, " s ", " P ", "   ", 'P', ILx.CCL_SMALL.get(1));
        CR.shaped(ILx.GCL_TINY.get(2), CR.DEF_REV, " s ", " P ", "   ", 'P', ILx.GCL_SMALL.get(1));
        CR.shaped(ILx.PCL_TINY.get(2), CR.DEF_REV, " s ", " P ", "   ", 'P', ILx.PCL_SMALL.get(1));

        for (int i = 0; i < RMx.CuttingFluids.length; i++)
            if (RMx.CuttingFluids[i] != null) {
                RM.Cutter.addRecipe1(true, 16, 16 * RMx.CuttingMultiplier[i], ILx.CCL.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.CCL_SMALL.get(2));
                RM.Cutter.addRecipe1(true, 16, 16 * RMx.CuttingMultiplier[i], ILx.GCL.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.GCL_SMALL.get(2));
                RM.Cutter.addRecipe1(true, 16, 16 * RMx.CuttingMultiplier[i], ILx.PCL.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.PCL_TINY.get(2));
                RM.Cutter.addRecipe1(true, 16, 16 * RMx.CuttingMultiplier[i], ILx.CCL_SMALL.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.CCL_TINY.get(2));
                RM.Cutter.addRecipe1(true, 16, 16 * RMx.CuttingMultiplier[i], ILx.GCL_SMALL.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.GCL_TINY.get(2));
                RM.Cutter.addRecipe1(true, 16, 16 * RMx.CuttingMultiplier[i], ILx.PCL_SMALL.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.PCL_TINY.get(2));
            }

        // Trace etching
        CR.shaped(ILx.EtchMask_Trace.get(1), CR.DEF_REV, "x  ", " P ", "   ", 'P', OP.plate.mat(MT.PVC, 1));
        lens.addListener(event -> new OreDictListenerEvent_Names() {
            @Override
            public void addAllListeners() {
                addListener(DYE_OREDICTS_LENS[DYE_INDEX_White], lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace.get(1)));
                addListener(DYE_OREDICTS_LENS[DYE_INDEX_Yellow], lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace_Small.get(1)));
                addListener(DYE_OREDICTS_LENS[DYE_INDEX_Orange], lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace_Tiny.get(1)));
                addListener(DYE_OREDICTS_LENS[DYE_INDEX_Red], lens -> RM.LaserEngraver.addRecipe2(true, 16, 128, plate.mat(MT.PVC, 1), ST.amount(0, lens.mStack), ILx.EtchMask_Trace_Long.get(1)));
            }
        });

        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL.get(1), ILx.EtchMask_Trace.get(0), MTx.FeCl3Solution.liquid(17 * U2, true), MTx.CuFeClSolution.liquid(9 * U, false), IL.Circuit_Plate_Copper.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL_SMALL.get(1), ILx.EtchMask_Trace_Small.get(0), MTx.FeCl3Solution.liquid(17 * U4, true), MTx.CuFeClSolution.liquid(9 * U2, false), ILx.Circuit_Plate_Copper_Small.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL_LONG.get(1), ILx.EtchMask_Trace_Long.get(0), MTx.FeCl3Solution.liquid(17 * U4, true), MTx.CuFeClSolution.liquid(9 * U2, false), ILx.Circuit_Plate_Copper_Long.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.CCL_TINY.get(1), ILx.EtchMask_Trace_Tiny.get(0), MTx.FeCl3Solution.liquid(17 * U8, true), MTx.CuFeClSolution.liquid(9 * U4, false), ILx.Circuit_Plate_Copper_Tiny.get(1));

        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL.get(1), ILx.EtchMask_Trace.get(0), FL.array(MT.AquaRegia.liquid(13 * U2, true)), FL.array(MT.ChloroauricAcid.liquid(3 * U, false), MT.NO.gas(U, false), MT.H2O.liquid(3 * U, false)), IL.Circuit_Plate_Gold.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL_SMALL.get(1), ILx.EtchMask_Trace_Small.get(0), FL.array(MT.AquaRegia.liquid(13 * U4, true)), FL.array(MT.ChloroauricAcid.liquid(3 * U2, false), MT.NO.gas(U2, false), MT.H2O.liquid(3 * U2, false)), ILx.Circuit_Plate_Gold_Small.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL_LONG.get(1), ILx.EtchMask_Trace_Long.get(0), FL.array(MT.AquaRegia.liquid(13 * U4, true)), FL.array(MT.ChloroauricAcid.liquid(3 * U2, false), MT.NO.gas(U2, false), MT.H2O.liquid(3 * U2, false)), ILx.Circuit_Plate_Gold_Long.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.GCL_TINY.get(1), ILx.EtchMask_Trace_Tiny.get(0), FL.array(MT.AquaRegia.liquid(13 * U8, true)), FL.array(MT.ChloroauricAcid.liquid(3 * U4, false), MT.NO.gas(U4, false), MT.H2O.liquid(3 * U4, false)), ILx.Circuit_Plate_Gold_Tiny.get(1));

        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL.get(1), ILx.EtchMask_Trace.get(0), FL.array(MT.AquaRegia.liquid(78 * U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78 * U8, false), MT.NO.gas(12 * U8, false), MT.H2O.liquid(33 * U8, false)), IL.Circuit_Plate_Platinum.get(1));
        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL_SMALL.get(2), ILx.EtchMask_Trace_Small.get(0), FL.array(MT.AquaRegia.liquid(78 * U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78 * U8, false), MT.NO.gas(12 * U8, false), MT.H2O.liquid(33 * U8, false)), ILx.Circuit_Plate_Platinum_Small.get(2));
        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL_LONG.get(2), ILx.EtchMask_Trace_Long.get(0), FL.array(MT.AquaRegia.liquid(78 * U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78 * U8, false), MT.NO.gas(12 * U8, false), MT.H2O.liquid(33 * U8, false)), ILx.Circuit_Plate_Platinum_Long.get(2));
        RM.Bath.addRecipe2(true, 0, 128, ILx.PCL_TINY.get(4), ILx.EtchMask_Trace_Tiny.get(0), FL.array(MT.AquaRegia.liquid(78 * U8, true)), FL.array(MT.ChloroplatinicAcid.liquid(78 * U8, false), MT.NO.gas(12 * U8, false), MT.H2O.liquid(33 * U8, false)), ILx.Circuit_Plate_Platinum_Tiny.get(4));

        /// Semiconductors

        // Hydrides & Polycrystallines
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.stick.mat(MT.Ge, 1), OM.dust(MT.Mo, U3), FL.Water.make(3000), MTx.GeH4.gas(U2, false), OM.dust(MTx.MoO3, 4 * U3));
        RM.Electrolyzer.addRecipe2(true, 32, 256, OP.stick.mat(MT.Ge, 1), OM.dust(MT.Cd, U), FL.Water.make(3000), MTx.GeH4.gas(U2, false), OM.dust(MTx.CdO, 2 * U));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Mg2Si, 3), MT.HCl.gas(8 * U, true), MTx.SiH4.gas(U, false), dust.mat(MT.MgCl2, 6));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MT.Si, 1), FL.array(MT.HCl.gas(6*U, true)), FL.array(MTx.HCl3Si.gas(U, false), MT.H.gas(2*U, false)));

        RMx.Thermolysis.addRecipe1(true, 16, 16, ST.tag(1), MTx.GaH3.gas(U, true), MT.H.gas(3*U, false), dust.mat(MT.Ga, 1));

        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(1), MTx.GeH4  .gas(U, true), MT.H.gas(4 * U, false), polyGem.mat(MT.Ge, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(1), MTx.SiH4  .gas(U, true), MT.H.gas(4 * U, false), polyGem.mat(MT.Si, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(2), FL.array(MTx.SiH4.gas(U2, true), MTx.GeH4.gas(U2, true)), MT.H.gas(4 * U, false), polyGem.mat(MTx.SiGe, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 512, ST.tag(1), FL.array(MTx.HCl3Si.gas(U, true), MT.H.gas(2*U, true)), FL.array(MT.HCl.gas(6*U, false)), polyGem.mat(MT.Si, 1));

        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(4), FL.array(MTx.SiH4  .gas(U, true), MTx.B2H6.gas(U1000, true)), MT.H.gas(4 * U + 6 * U1000, false), polyGem.mat(MTx.PDopedSi, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(4), FL.array(MTx.GeH4  .gas(U, true), MTx.B2H6.gas(U1000, true)), MT.H.gas(4 * U + 6 * U1000, false), polyGem.mat(MTx.PDopedGe, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(5), FL.array(MTx.SiH4.gas(U2, true), MTx.GeH4.gas(U2, true), MTx.B2H6.gas(U1000, true)), MT.H.gas(4 * U + 6 * U1000, false), polyGem.mat(MTx.PDopedSiGe, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 512, ST.tag(4), FL.array(MTx.HCl3Si.gas(U, true), MTx.B2H6.gas(U1000, true) , MT.H.gas(2 * U - 6 * U1000, true)), FL.array(MT.HCl.gas(6*U, false)), polyGem.mat(MTx.PDopedSi, 1));

        for (OreDictMaterial nDopant : new OreDictMaterial[] {MTx.PH3, MTx.AsH3}) {
            RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(6), FL.array(MTx.SiH4  .gas(U, true), nDopant.gas(U500, true)), MT.H.gas(4 * U + 3 * U500, false), polyGem.mat(MTx.NDopedSi, 1));
            RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(6), FL.array(MTx.GeH4  .gas(U, true), nDopant.gas(U500, true)), MT.H.gas(4 * U + 3 * U500, false), polyGem.mat(MTx.NDopedGe, 1));
            RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(7), FL.array(MTx.SiH4.gas(U2, true), MTx.GeH4.gas(U2, true), nDopant.gas(U500, true)), MT.H.gas(4 * U + 3 * U500, false), polyGem.mat(MTx.NDopedSiGe, 1));
            RMx.Thermolysis.addRecipe1(true, 16, 512, ST.tag(6), FL.array(MTx.HCl3Si.gas(U, true), nDopant.gas(U500, true) , MT.H.gas(2 * U - 3 * U500, true)), FL.array(MT.HCl.gas(6*U, false)), polyGem.mat(MTx.NDopedSi, 1));
        }

        RMx.Thermolysis.addRecipe1(true, 16, 256, ST.tag(2), FL.array(MT.Ga.liquid(U, true), MT.As.gas(U, true)), ZL_FS, polyGem.mat(MTx.GaAs, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 256, ST.tag(5), FL.array(MT.Ga.liquid(U, true), MT.As.gas(U, true), MT.Zn.liquid(U144, true)), ZL_FS, polyGem.mat(MTx.PDopedGaAs, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 256, ST.tag(7), FL.array(MT.Ga.liquid(U, true), MT.As.gas(U, true), MT.Si.liquid(U144, true)), ZL_FS, polyGem.mat(MTx.NDopedGaAs, 1));

        // RecipeMapHandlerPrefix not working here
        for (OreDictMaterial mat : PolyGemMaterials)
            for (int i = 0; i < RMx.CuttingFluids.length; i++)
                if (RMx.CuttingFluids[i] != null) {
                    RM.Cutter.addRecipe1(true, 16, 64 * RMx.CuttingMultiplier[i], polyGem.mat(mat, 1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, plateGemTiny.mat(mat, 6), dustTiny.mat(mat, 3));
                }

        RM.Mixer.addRecipe1(true, 16, 2, ST.tag(2), FL.array(FL.make_("molten.silicon", 1), FL.make_("molten.germanium", 1)), FL.make_("molten.silicongermanium", 2), ZL_IS);
        RM.Mixer.addRecipe1(true, 16, 288, ST.tag(2), FL.array(FL.make_("molten.gallium", 144), MT.As.gas(U, true)), FL.make_("molten.galliumarsenide", 288), ZL_IS);

        // Boules using Czochralski process
        for (FluidStack nobleGas : FL.array(MT.He.gas(U, true), MT.Ne.gas(U, true), MT.Ar.gas(U, true), MT.Kr.gas(U, true), MT.Xe.gas(U, true), MT.Rn.gas(U, true)))
            if (nobleGas != null) {
                // i-type semiconductors
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.GaAs, 1), FL.array(FL.mul(nobleGas, 1), MT.Ga.liquid(35 * U18, true), MT.As.gas(35 * U18, true)), ZL_FS, bouleGt.mat(MTx.GaAs, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.GaAs, 1), FL.array(FL.mul(nobleGas, 9), MT.Ga.liquid(35 * U2 , true), MT.As.gas(35 * U2 , true)), ZL_FS, bouleGt.mat(MTx.GaAs, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.GaAs, 1), FL.array(FL.mul(nobleGas, 1), MTx.GaAs.liquid(35 * U9, true)), ZL_FS, bouleGt.mat(MTx.GaAs, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.GaAs, 1), FL.array(FL.mul(nobleGas, 9), MTx.GaAs.liquid(35 * U , true)), ZL_FS, bouleGt.mat(MTx.GaAs, 9));

                // n-type semiconductors
                for (OreDictMaterial nDopant : new OreDictMaterial[] {MTx.PH3, MTx.AsH3}) {
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MT.Si         , 1), FL.array(FL.mul(nobleGas, 1), MT .Si  .liquid(35 * U9, true), nDopant.gas(4  * U500, true)), NF, bouleGt.mat(MTx.NDopedSi  , 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MT.Si         , 1), FL.array(FL.mul(nobleGas, 9), MT .Si  .liquid(35 * U , true), nDopant.gas(36 * U500, true)), NF, bouleGt.mat(MTx.NDopedSi  , 9));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.NDopedSi  , 1), FL.array(FL.mul(nobleGas, 1), MT .Si  .liquid(35 * U9, true), nDopant.gas(4  * U500, true)), NF, bouleGt.mat(MTx.NDopedSi  , 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.NDopedSi  , 1), FL.array(FL.mul(nobleGas, 9), MT .Si  .liquid(35 * U , true), nDopant.gas(36 * U500, true)), NF, bouleGt.mat(MTx.NDopedSi  , 9));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MT.Ge         , 1), FL.array(FL.mul(nobleGas, 1), MT .Ge  .liquid(35 * U9, true), nDopant.gas(4  * U500, true)), NF, bouleGt.mat(MTx.NDopedGe  , 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MT.Ge         , 1), FL.array(FL.mul(nobleGas, 9), MT .Ge  .liquid(35 * U , true), nDopant.gas(36 * U500, true)), NF, bouleGt.mat(MTx.NDopedGe  , 9));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.NDopedGe  , 1), FL.array(FL.mul(nobleGas, 1), MT .Ge  .liquid(35 * U9, true), nDopant.gas(4  * U500, true)), NF, bouleGt.mat(MTx.NDopedGe  , 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.NDopedGe  , 1), FL.array(FL.mul(nobleGas, 9), MT .Ge  .liquid(35 * U , true), nDopant.gas(36 * U500, true)), NF, bouleGt.mat(MTx.NDopedGe  , 9));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35 * U9, true), nDopant.gas(4  * U500, true)), NF, bouleGt.mat(MTx.NDopedSiGe, 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35 * U , true), nDopant.gas(36 * U500, true)), NF, bouleGt.mat(MTx.NDopedSiGe, 9));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.NDopedSiGe, 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35 * U9, true), nDopant.gas(4  * U500, true)), NF, bouleGt.mat(MTx.NDopedSiGe, 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.NDopedSiGe, 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35 * U , true), nDopant.gas(36 * U500, true)), NF, bouleGt.mat(MTx.NDopedSiGe, 9));
                }
                for (OreDictMaterial nDopant : new OreDictMaterial[] {MT.Si, MT.Ge}) {
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.GaAs      , 1), FL.array(FL.mul(nobleGas, 1), MTx.GaAs.liquid(35 * U9, true), nDopant.liquid(4  * U144, true)), NF, bouleGt.mat(MTx.NDopedGaAs, 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.GaAs      , 1), FL.array(FL.mul(nobleGas, 9), MTx.GaAs.liquid(35 * U , true), nDopant.liquid(36 * U144, true)), NF, bouleGt.mat(MTx.NDopedGaAs, 9));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.NDopedGaAs, 1), FL.array(FL.mul(nobleGas, 1), MTx.GaAs.liquid(35 * U9, true), nDopant.liquid(4  * U144, true)), NF, bouleGt.mat(MTx.NDopedGaAs, 1));
                    RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.NDopedGaAs, 1), FL.array(FL.mul(nobleGas, 9), MTx.GaAs.liquid(35 * U , true), nDopant.liquid(36 * U144, true)), NF, bouleGt.mat(MTx.NDopedGaAs, 9));
                }

                // p-type semiconductors
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MT.Si         , 1), FL.array(FL.mul(nobleGas, 1), MT.Si   .liquid(35 * U9, true), MTx.B2H6.gas(4  * U1000, true)), NF, bouleGt.mat(MTx.PDopedSi, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MT.Si         , 1), FL.array(FL.mul(nobleGas, 9), MT.Si   .liquid(35 * U , true), MTx.B2H6.gas(36 * U1000, true)), NF, bouleGt.mat(MTx.PDopedSi, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.PDopedSi  , 1), FL.array(FL.mul(nobleGas, 1), MT.Si   .liquid(35 * U9, true), MTx.B2H6.gas(4  * U1000, true)), NF, bouleGt.mat(MTx.PDopedSi, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.PDopedSi  , 1), FL.array(FL.mul(nobleGas, 9), MT.Si   .liquid(35 * U , true), MTx.B2H6.gas(36 * U1000, true)), NF, bouleGt.mat(MTx.PDopedSi, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MT.Ge         , 1), FL.array(FL.mul(nobleGas, 1), MT.Ge   .liquid(35 * U9, true), MTx.B2H6.gas(4  * U1000, true)), NF, bouleGt.mat(MTx.PDopedGe, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MT.Ge         , 1), FL.array(FL.mul(nobleGas, 9), MT.Ge   .liquid(35 * U , true), MTx.B2H6.gas(36 * U1000, true)), NF, bouleGt.mat(MTx.PDopedGe, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.PDopedGe  , 1), FL.array(FL.mul(nobleGas, 1), MT.Ge   .liquid(35 * U9, true), MTx.B2H6.gas(4  * U1000, true)), NF, bouleGt.mat(MTx.PDopedGe, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.PDopedGe  , 1), FL.array(FL.mul(nobleGas, 9), MT.Ge   .liquid(35 * U , true), MTx.B2H6.gas(36 * U1000, true)), NF, bouleGt.mat(MTx.PDopedGe, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35 * U9, true), MTx.B2H6.gas(4  * U1000, true)), NF, bouleGt.mat(MTx.PDopedSiGe, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.SiGe      , 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35 * U , true), MTx.B2H6.gas(36 * U1000, true)), NF, bouleGt.mat(MTx.PDopedSiGe, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.PDopedSiGe, 1), FL.array(FL.mul(nobleGas, 1), MTx.SiGe.liquid(35 * U9, true), MTx.B2H6.gas(4  * U1000, true)), NF, bouleGt.mat(MTx.PDopedSiGe, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.PDopedSiGe, 1), FL.array(FL.mul(nobleGas, 9), MTx.SiGe.liquid(35 * U , true), MTx.B2H6.gas(36 * U1000, true)), NF, bouleGt.mat(MTx.PDopedSiGe, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.GaAs      , 1), FL.array(FL.mul(nobleGas, 1), MTx.GaAs.liquid(35 * U9, true), MT.Be.liquid(4  * U144, true)), NF, bouleGt.mat(MTx.PDopedGaAs, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.GaAs      , 1), FL.array(FL.mul(nobleGas, 9), MTx.GaAs.liquid(35 * U , true), MT.Be.liquid(36 * U144, true)), NF, bouleGt.mat(MTx.PDopedGaAs, 9));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 72000 , plateGemTiny.mat(MTx.PDopedGaAs, 1), FL.array(FL.mul(nobleGas, 1), MTx.GaAs.liquid(35 * U9, true), MT.Be.liquid(4  * U144, true)), NF, bouleGt.mat(MTx.PDopedGaAs, 1));
                RM.CrystallisationCrucible.addRecipe1(true, 16, 648000, plateGem    .mat(MTx.PDopedGaAs, 1), FL.array(FL.mul(nobleGas, 9), MTx.GaAs.liquid(35 * U , true), MT.Be.liquid(36 * U144, true)), NF, bouleGt.mat(MTx.PDopedGaAs, 9));
            }

        // Photoresist
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.DNQ, 1), dust.mat(MTx.PF, 1), MTx.Toluene.liquid(2 * U, true), MTx.DnqNovolacResist.liquid(2 * U, false), NI);

        // Lasers
        RM.Mixer.addRecipe0(true, 16, 16, FL.array(MT.Kr.gas(U200, true), MT.F.gas(U200, true)), MTx.KrF.gas(U100, false), NI);
        RM.Mixer.addRecipe0(true, 16, 16, FL.array(MT.Ar.gas(U200, true), MT.F.gas(U200, true)), MTx.ArF.gas(U100, false), NI);
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MT.N.gas(U, true), NF, ILx.Comp_Laser_Gas_N.get(1));
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MTx.KrF.gas(U, true), NF, ILx.Comp_Laser_Gas_KrF.get(1));
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MTx.ArF.gas(U, true), NF, ILx.Comp_Laser_Gas_ArF.get(1));
        RM.Canner.addRecipe1(true, 16, 128, IL.Comp_Laser_Gas_Empty.get(1), MT.Sn.liquid(U, true), NF, ILx.Comp_Laser_Molten_Sn.get(1));

        // BJT Epitaxy
        RMx.VacuumChamber.addRecipe1(false, 16, 128, plateGemTiny.mat(MTx.NDopedGe, 1), FL.array(MTx.GeH4.gas(U40, true), MTx.B2H6.gas(U1000, true)), MT.H.gas(U10 + 6 * U1000, false), ILx.BJT_Ge_Base.get(1));
        RMx.VacuumChamber.addRecipe1(false, 16, 128, plateGemTiny.mat(MTx.NDopedSi, 1), FL.array(MTx.SiH4.gas(U40, true), MTx.B2H6.gas(U1000, true)), MT.H.gas(U10 + 6 * U1000, false), ILx.BJT_Si_Base.get(1));
        for (OreDictMaterial nDopant : new OreDictMaterial[] {MTx.PH3, MTx.AsH3}) {
            RMx.VacuumChamber.addRecipe1(false, 16, 128, ILx.BJT_Ge_Base.get(1), FL.array(MTx.GeH4.gas(U40, true), nDopant.gas(U500, true)), MT.H.gas(U10 + 6 * U1000, false), ILx.BJT_Ge.get(1));
            RMx.VacuumChamber.addRecipe1(false, 16, 128, ILx.BJT_Si_Base.get(1), FL.array(MTx.SiH4.gas(U40, true), nDopant.gas(U500, true)), MT.H.gas(U10 + 6 * U1000, false), ILx.BJT_Si.get(1));
        }

        // components
        CR.shaped(ILx.Resistor_ThroughHole  .get(2), CR.DEF_REV, " W ", " CP", " W ", 'W', OP.wireFine.dat(ANY.Cu), 'P', plateTiny.dat(MT.Ceramic), 'C', dustTiny.dat(ANY.Coal));
        CR.shaped(ILx.Capacitor_ThroughHole .get(2), CR.DEF_REV, "   ", "PC ", "W W", 'W', OP.wireFine.dat(ANY.Cu), 'C', plateTiny.dat(MT.Ceramic), 'P', plateTiny.dat(MT.Paper));
        CR.shaped(ILx.Transistor_ThroughHole.get(4), CR.DEF_REV, " P ", " S ", "WWW", 'W', OP.wireFine.dat(ANY.Cu), 'S', BJT_NAME, 'P', plateTiny.dat(ANY.Plastic));
        CR.shaped(ILx.Transistor_ThroughHole.get(4), CR.DEF_REV, " P ", " S ", "WWW", 'W', OP.wireFine.dat(ANY.Cu), 'S', ILx.Wafers[MESFET_IDX][0][7].get(1), 'P', plateTiny.dat(MT.Plastic));

        for (OreDictMaterial cu : ANY.Cu.mToThis) {
            for (OreDictMaterial coal : ANY.Coal.mToThis) {
                RM.Press.addRecipeX(false, 16, 32, ST.array(plateTiny.mat(MT.Ceramic, 1), dustTiny.mat(coal, 1), wireFine.mat(cu, 2)), ILx.Resistor_ThroughHole.get(1));
            }
            RM.Press.addRecipeX(false, 16, 32, ST.array(plateTiny.mat(MT.Ceramic, 1), plateTiny.mat(MT.Paper, 1), wireFine.mat(cu, 2)), ILx.Capacitor_ThroughHole.get(1));
            RM.Press.addRecipeX(false, 16, 32, ST.array(plateTiny.mat(MT.Plastic, 1), ILx.BJT_Ge.get(1), wireFine.mat(cu, 3)), ILx.Transistor_ThroughHole.get(1));
            RM.Press.addRecipeX(false, 16, 32, ST.array(plateTiny.mat(MT.Plastic, 1), ILx.BJT_Si.get(1), wireFine.mat(cu, 3)), ILx.Transistor_ThroughHole.get(1));
        }

        RM.RollingMill.addRecipe1(true, 16, 256, ingot.mat(MT.Al2O3, 1), plate.mat(MT.Al2O3, 1));
        RM.RollingMill.addRecipe1(true, 16, 29, nugget.mat(MT.Al2O3, 1), plateTiny.mat(MT.Al2O3, 1));

        RMx.Sintering.addRecipeX(true, 16, 64, ST.array(wireFine.mat(MT.Ta, 4), dustSmall.mat(MT.MnO2, 1), dust.mat(MT.Ta2O5, 1)), ILx.Capacitor_Tantalum.get(16));
        RMx.Sintering.addRecipeX(true, 16, 256, ST.array(wireFine.mat(MT.Ta, 16), dust.mat(MT.MnO2, 1), dust.mat(MT.Ta2O5, 4)), ILx.Capacitor_Tantalum.get(64));
        RM.Press.addRecipeX(true, 16, 64, ST.array(plate.mat(MT.Al2O3, 1), foil.mat(MTx.PdAg, 1), foil.mat(MT.Nichrome, 1)), ILx.Resistor_Metal_Film.get(1));

        // Cr Etching
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.NitratoCericAcid.liquid(U, true), MT.NH3.gas(2 * U, true)), ZL_FS, dust.mat(MTx.CAN, 1));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CAN, 1), MT.HNO3.liquid(5 * U, true), MTx.ChromeEtch.liquid(6 * U, false), NI);
        RMx.Thermolysis.addRecipe0(true, 16, 1024, FL.array(MTx.CrNO3Solution.liquid(14 * U, true)), FL.array(MT.H2O.liquid(3 * U, false), MT.NO.gas(4 * U, false)), dust.mat(MTx.Cr2O3, 5), dust.mat(MTx.CAN, 2));

        // Al Etching
        RM.Mixer.addRecipe1(true, 16, 64, ST.tag(2), FL.array(MTx.H3PO4.liquid(8*U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.AlEtch.liquid(13*U, false)));

        // Plasmas
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.S, 1), MT.F.gas(6 * U, true), MTx.SF6.gas(U, false), NI);

        for (OreDictMaterial mat : Iterables.concat(MT.ALL_MATERIALS_REGISTERED_HERE, MTx.ALL_MATERIALS_REGISTERED_HERE)) {
            if (mat.contains(MTx.IONIZING) && mat.mGas != null) {
                RMx.Ionizer.addRecipe0(true, 64, 2000, mat.gas(U, true), mat.plasma(U, false), NI);
                RM.Freezer.addRecipe1(true, 16, 256, ST.tag(0), mat.plasma(U, true), mat.gas(U, false), NI);
            }
        }

        // Ru Electroplating
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MT.Ru, 1), MT.Cl.gas(3 * U, true), NF, dust.mat(MTx.RuCl3, 4));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.RuCl3, 8), FL.array(MTx.H3NSO3.liquid(8 * U, true), MTx.DiluteHCl.liquid(10 * U, true)), FL.array(MTx.H3Ru2NCl8H4O2.liquid(20 * U, false), MT.SO2.gas(3 * U, false)));
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.RuCl3, 8), FL.array(MTx.H3NSO3.liquid(8 * U, true), MT.HCl.gas(4 * U, true), water), FL.array(MTx.H3Ru2NCl8H4O2.liquid(20 * U, false), MT.SO2.gas(3 * U, false)));
        }
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.NH4Cl, 3), FL.array(MTx.H3Ru2NCl8H4O2.liquid(20 * U, true)), FL.array(MTx.RuElectrolyte.liquid(20 * U, false), MT.HCl.gas(6 * U, false)));

        // bonding/packaging
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Au, 1), ILx.Wafers[0][0][7].get(4)), ILx.ICs[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[0][1][7].get(4)), ILx.ICs[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[0][2][13].get(4)), ILx.ICs[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[1][0][7].get(4)), ILx.CPUs[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[1][1][7].get(4)), ILx.CPUs[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[1][2][13].get(4)), ILx.CPUs[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[2][0][7].get(4)), ILx.DRAMChips[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[2][1][7].get(4)), ILx.DRAMChips[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[2][2][13].get(4)), ILx.DRAMChips[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Au, 1), ILx.Wafers[3][0][7].get(4)), ILx.GPUs[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[3][1][7].get(4)), ILx.GPUs[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[3][2][13].get(4)), ILx.GPUs[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[4][0][7].get(4)), ILx.FlashChips[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[4][1][7].get(4)), ILx.FlashChips[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[4][2][13].get(4)), ILx.FlashChips[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[4][0][7].get(4)), ILx.FlashChips[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[4][1][7].get(4)), ILx.FlashChips[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[4][2][13].get(4)), ILx.FlashChips[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Cu, 1), ILx.Wafers[5][0][7].get(4)), ILx.SoCs[0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Al, 1), ILx.Wafers[5][1][7].get(4)), ILx.SoCs[1].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), wireFine.mat(MT.Au, 1), ILx.Wafers[5][2][13].get(4)), ILx.SoCs[2].get(4));

        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), foil.mat(MT.Cu, 1), ILx.Wafers[MESFET_IDX][0][7].get(16)), ILx.Transistor_SMD.get(16));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), foil.mat(MT.Cu, 1), ILx.Capacitor_Tantalum.get(16)), ILx.Capacitor_SMD.get(16));
        RM.Press.addRecipeX(true, 16, 64, ST.array(casingSmall.mat(MTx.PF, 1), foil.mat(MT.Sn, 1), ILx.Resistor_Metal_Film.get(1)), ILx.Resistor_SMD.get(16));

        // hand-soldering PCBs
        CR.shaped(ILx.PCBs[0][0].get(1), CR.DEF_REM, "iE ", "CBR", "   ", 'B', ILx.Circuit_Plate_Wood, 'E', MultiItemsElectronics.ELECTRONTUBE_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[0][0].get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', ILx.Circuit_Plate_Wood, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[1][0].get(1), CR.DEF_REM, "iE ", "CBR", "   ", 'B', IL.Circuit_Plate_Copper, 'E', MultiItemsElectronics.ELECTRONTUBE_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[1][0].get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', IL.Circuit_Plate_Copper, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[1][1].get(1), CR.DEF_REM, "iT ", "CBR", "   ", 'B', ILx.Circuit_Plate_Copper_Small, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[2][0].get(1), CR.DEF_REM, "iT ", "CBR", " T ", 'B', IL.Circuit_Plate_Copper, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[2][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Copper_Small, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', IC_NAMES[0]);
        CR.shaped(ILx.PCBs[3][0].get(1), CR.DEF_REM, "TiT", "CBR", "T T", 'B', IL.Circuit_Plate_Gold, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME);
        CR.shaped(ILx.PCBs[3][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Gold_Small, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.ICs[0]);
        CR.shaped(ILx.PCBs[4][0].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL.Circuit_Plate_Gold, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', IC_NAMES[0]);
        CR.shaped(ILx.PCBs[4][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Gold_Small, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', IC_NAMES[1]);
        CR.shaped(ILx.PCBs[5][0].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL.Circuit_Plate_Platinum, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', ILx.ICs[1]);
        CR.shaped(ILx.PCBs[5][1].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', ILx.Circuit_Plate_Platinum_Small, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', IC_NAMES[2]);
        CR.shaped(ILx.PCBs[6][0].get(1), CR.DEF_REM, "iI ", "CBR", " T ", 'B', IL.Circuit_Plate_Platinum, 'T', MultiItemsElectronics.TRANSISTOR_NAME, 'C', MultiItemsElectronics.CAPACITOR_NAME, 'R', MultiItemsElectronics.RESISTOR_NAME, 'I', IC_NAMES[2]);

        // auto-soldering PCBs
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(1), IL.Circuit_Plate_Copper.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[1][0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(1), ILx.Circuit_Plate_Copper_Small.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[1][1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(1), ILx.Circuit_Plate_Copper_Tiny.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[1][2].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(2), IL.Circuit_Plate_Copper.get(1), ILx.Transistor_SMD.get(2), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(2), ILx.Circuit_Plate_Copper_Small.get(1), ILx.Transistor_SMD.get(2), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Copper_Tiny.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[0].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][2].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Copper_Tiny.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[1].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][2].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Copper_Tiny.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[2][2].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(4), IL.Circuit_Plate_Gold.get(1), ILx.Transistor_SMD.get(4), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[3][0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Small.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[0].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[3][1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Tiny.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[1].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[3][2].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), IL.Circuit_Plate_Gold.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[0].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Small.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[1].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Small.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Gold_Tiny.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[4][2].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), IL.Circuit_Plate_Platinum.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[1].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[5][0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), ILx.Circuit_Plate_Platinum_Small.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[5][1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(0), IL.Circuit_Plate_Platinum.get(1), ILx.Transistor_SMD.get(1), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.PCBs[6][0].get(1));

        /// Computer Parts
        // HDDs
        RM.Lathe.addRecipe1(true, 16, 16, plateTiny.mat(MT.Al, 1), ILx.Al_Disk.get(1), dustDiv72.mat(MT.Al, 2));
        RMx.IonBombardment.addRecipe2(true, 16, 64, ILx.Al_Disk.get(1), dustSmall.mat(MT.Fe2O3  , 1), MT.Ar.gas(U10, true), NF, ILx.Hard_Disk.get(1));
        RMx.IonBombardment.addRecipe2(true, 32, 64, ILx.Al_Disk.get(1), foil     .mat(MTx.CoPtCr, 1), MT.Ar.gas(U10, true), NF, ILx.Hard_Disk_Advanced.get(1));
        CR.shaped(ILx.HDDs[0].get(1), CR.DEF_REM, "MDD", "CDD", "PSB", 'M', bolt.dat(MT.SteelMagnetic), 'C', casingSmall.dat(MT.Al), 'D', ILx.Hard_Disk, 'P', OD_CIRCUITS[4], 'S', IL.MOTORS[0], 'B', bolt.dat(MT.Ti));
        CR.shaped(ILx.HDDs[1].get(1), CR.DEF_REM, "MDD", "CDD", "PSB", 'M', bolt.dat(MT.NeodymiumMagnetic), 'C', casingSmall.dat(MT.Al), 'D', ILx.Hard_Disk_Advanced, 'P', OD_CIRCUITS[5], 'S', IL.MOTORS[1], 'B', bolt.dat(MT.Ti));
        CR.shaped(ILx.HDDs[2].get(1), CR.DEF_REM, "ICD", "FPF", "FiF", 'I', ILx.ICs[2], 'C', casingSmall.dat(MT.Polycarbonate), 'D', ILx.DRAMChips[2], 'P', ILx.Circuit_Plate_Platinum_Tiny, 'F', ILx.FlashChips[2]);
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), ILx.Circuit_Plate_Platinum_Tiny.get(1), casingSmall.mat(MT.Polycarbonate, 1), ILx.FlashChips[2].get(4), ILx.DRAMChips[2].get(1), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.HDDs[2].get(1));

        // RAM
        CR.shaped(ILx.RAMSticks[0].get(1), CR.DEF_REM, "R R", "R R", "iPI", 'R', ILx.DRAMChips[0], 'P', ILx.Circuit_Plate_Copper_Long, 'I', IC_NAMES[0]);
        CR.shaped(ILx.RAMSticks[1].get(1), CR.DEF_REM, "R R", "R R", "iPI", 'R', ILx.DRAMChips[1], 'P', ILx.Circuit_Plate_Gold_Long, 'I', IC_NAMES[1]);
        CR.shaped(ILx.RAMSticks[2].get(1), CR.DEF_REM, "R R", "R R", "iPI", 'R', ILx.DRAMChips[2], 'P', ILx.Circuit_Plate_Platinum_Long, 'I', IC_NAMES[2]);
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), ILx.Circuit_Plate_Copper_Long.get(1), ILx.DRAMChips[0].get(4), ILx.ICs[0].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.RAMSticks[0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), ILx.Circuit_Plate_Gold_Long.get(1), ILx.DRAMChips[1].get(4), ILx.ICs[1].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.RAMSticks[1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), ILx.Circuit_Plate_Platinum_Long.get(1), ILx.DRAMChips[2].get(4), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.RAMSticks[2].get(1));

        // GPUs
        CR.shaped(ILx.GraphicsCards[0].get(1), CR.DEF_REM, "iF ", "IGR", "APR", 'F', ILx.CPU_Fan, 'I', ILx.ICs[0], 'G', ILx.GPUs[0], 'R', ILx.DRAMChips[0], 'A', plateTiny.dat(MT.Al), 'P', ILx.Circuit_Plate_Copper_Long);
        CR.shaped(ILx.GraphicsCards[1].get(1), CR.DEF_REM, "iF ", "IGR", "APR", 'F', ILx.CPU_Fan, 'I', ILx.ICs[1], 'G', ILx.GPUs[1], 'R', ILx.DRAMChips[1], 'A', plateTiny.dat(MT.Al), 'P', ILx.Circuit_Plate_Gold_Long);
        CR.shaped(ILx.GraphicsCards[2].get(1), CR.DEF_REM, "FiF", "IGR", "APR", 'F', ILx.CPU_Fan, 'I', ILx.ICs[2], 'G', ILx.GPUs[2], 'R', ILx.DRAMChips[2], 'A', plateTiny.dat(MT.Al), 'P', ILx.Circuit_Plate_Platinum_Long);
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(6), ILx.Circuit_Plate_Copper_Long.get(1), ILx.DRAMChips[0].get(2), ILx.ICs[0].get(1), ILx.GPUs[0].get(1), ILx.CPU_Fan.get(1), plateTiny.mat(MT.Al, 1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.GraphicsCards[0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(6), ILx.Circuit_Plate_Gold_Long.get(1), ILx.DRAMChips[1].get(2), ILx.ICs[1].get(1), ILx.GPUs[0].get(1), ILx.CPU_Fan.get(1), plateTiny.mat(MT.Al, 1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.GraphicsCards[1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(6), ILx.Circuit_Plate_Platinum_Long.get(1), ILx.DRAMChips[2].get(2), ILx.ICs[2].get(1), ILx.GPUs[0].get(1), ILx.CPU_Fan.get(2), plateTiny.mat(MT.Al, 1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.GraphicsCards[2].get(1));

        // MoBo
        CR.shaped(ILx.Motherboards[0].get(1), CR.DEF_REM, "SCB", "UPO", "OWi", 'U', IC_NAMES[0], 'S', IL.Processor_Crystal_Empty, 'O', casingSmall.mat(MT.Polycarbonate, 1), 'C', CAPACITOR_NAME, 'W', cableGt08.mat(MT.Cu, 1), 'P', IL.Circuit_Plate_Copper, 'B', IL.Battery_Alkaline_Cell_Filled);
        CR.shaped(ILx.Motherboards[1].get(1), CR.DEF_REM, "SCB", "UPO", "OWi", 'U', IC_NAMES[1], 'S', IL.Processor_Crystal_Empty, 'O', casingSmall.mat(MT.Polycarbonate, 1), 'C', CAPACITOR_NAME, 'W', cableGt08.mat(MT.Au, 1), 'P', IL.Circuit_Plate_Gold, 'B', IL.Battery_Alkaline_Cell_Filled);
        CR.shaped(ILx.Motherboards[2].get(1), CR.DEF_REM, "SCB", "UPO", "OWi", 'U', IC_NAMES[2], 'S', IL.Processor_Crystal_Empty, 'O', casingSmall.mat(MT.Polycarbonate, 1), 'C', CAPACITOR_NAME, 'W', cableGt08.mat(MT.Pt, 1), 'P', IL.Circuit_Plate_Platinum, 'B', IL.Battery_Alkaline_Cell_Filled);
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), IL.Circuit_Plate_Copper.get(1), IL.Processor_Crystal_Empty.get(1), ILx.ICs[0].get(1), casingSmall.mat(MT.Polycarbonate, 2), cableGt08.mat(MT.Cu, 1), ILx.Capacitor_SMD.get(1), IL.Battery_Alkaline_Cell_Filled.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.Motherboards[0].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), IL.Circuit_Plate_Gold.get(1), IL.Processor_Crystal_Empty.get(1), ILx.ICs[1].get(1), casingSmall.mat(MT.Polycarbonate, 2), cableGt08.mat(MT.Au, 1), ILx.Capacitor_SMD.get(1), IL.Battery_Alkaline_Cell_Filled.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.Motherboards[1].get(1));
        RMx.Soldering.addRecipeX(true, 16, 48, ST.array(ST.tag(8), IL.Circuit_Plate_Platinum.get(1), IL.Processor_Crystal_Empty.get(1), ILx.ICs[2].get(1), casingSmall.mat(MT.Polycarbonate, 2), cableGt08.mat(MT.Pt, 1), ILx.Capacitor_SMD.get(1), IL.Battery_Alkaline_Cell_Filled.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ILx.Motherboards[2].get(1));

        // Misc
        RM.Welder.addRecipe2(true, 16, 64, plateCurved.mat(MT.Polycarbonate, 4), ring.mat(MT.Polycarbonate, 1), rotor.mat(MT.Polycarbonate, 1));
        RM.Welder.addRecipe2(true, 16, 64, plateCurved.mat(MT.Plastic, 4), ring.mat(MT.Plastic, 1), rotor.mat(MT.Plastic, 1));

        CR.shaped(ILx.CPU_Fan.get(1), CR.DEF_REV, "PFL", "CMC", " RW", 'P', casingSmall.mat(MT.Polycarbonate, 1), 'F', rotor.mat(MT.Polycarbonate, 1), 'L', OD.itemLubricant, 'C', casingSmall.mat(MT.StainlessSteel, 1), 'M', IL.MOTORS[0], 'W', cableGt02.mat(MT.Cu, 1), 'R', ring.mat(MT.Polycarbonate, 1));
        CR.shaped(ILx.ComputerCase.get(1), CR.DEF_REM, "TFS", "DCB", "WdS", 'T', MTEx.gt6MTEReg.getItem(10040), 'F', ILx.CPU_Fan, 'S', screw.mat(MT.StainlessSteel, 1), 'D', DYE_OREDICTS[DYE_INDEX_Black], 'C', casingMachine.mat(MT.SteelGalvanized, 1), 'B', MTEx.gt6MTEReg.getItem(32711), 'W', cableGt08.mat(MT.Cu, 1));

        // PCs
        for (int tier = 0; tier < NUM_COMPUTER_TIERS; tier++) {
            CR.shaped(ILx.PCs[tier].get(1), CR.DEF_REM, "dCS", "GPH", "RMO", 'O', ILx.ComputerCase, 'R', ILx.RAMSticks[tier], 'C', ILx.CPUs[tier], 'P', ILx.Thermal_Paste, 'H', ILx.HDDs[tier], 'G', ILx.GraphicsCards[tier], 'M', ILx.Motherboards[tier], 'S', screw.dat(MT.StainlessSteel));
        }

        // Phosphors
        RM.BurnMixer.addRecipeX(true, 16, 32, ST.array(dust.mat(MTx.Y2O3, 1), dust.mat(MT.S, 1), dustTiny.mat(MTx.Eu2O3, 1)), MT.H.gas(2*U, true), MT.H2O.liquid(3*U, false), dust.mat(MTx.RedPhosphor, 5));
        RM.BurnMixer.addRecipeX(true, 16, 32, ST.array(dust.mat(MTx.Y2O3, 1), dust.mat(MT.S, 1), dustTiny.mat(MTx.Eu2O3, 1)), MT.CO.gas(2*U, true), MT.CO2.gas(3*U, false), dust.mat(MTx.RedPhosphor, 5));
        RM.Mixer.addRecipe2(true, 16, 16, dust.mat(MT.OREMATS.Sphalerite, 1), dustTiny.mat(MT.Ag, 1), dust.mat(MTx.BluePhosphor, 1));
        RM.Mixer.addRecipe2(true, 16, 16, dust.mat(MT.OREMATS.Sphalerite, 1), dustTiny.mat(MT.Cu, 1), dust.mat(MTx.GreenPhosphor, 1));
        RM.Mixer.addRecipeX(true, 16, 16, ST.array(dust.mat(MT.OREMATS.Sphalerite, 1), dust.mat(MTx.CdS, 1), dustTiny.mat(MT.Ag, 2)), dust.mat(MTx.YellowPhosphor, 2));
        RM.Mixer.addRecipeX(true, 16, 16, ST.array(dustSmall.mat(MT.OREMATS.Sphalerite, 2), dustSmall.mat(MTx.CdS, 2), dustTiny.mat(MT.Ag, 1)), dust.mat(MTx.YellowPhosphor, 1));
        RM.Mixer.addRecipe2(true, 16, 16, dust.mat(MTx.RedPhosphor, 1), dust.mat(MTx.GreenPhosphor, 1), dust.mat(MTx.YellowPhosphor, 2));
        RM.Mixer.addRecipe2(true, 16, 16, dustSmall.mat(MTx.RedPhosphor, 2), dustSmall.mat(MTx.GreenPhosphor, 2), dust.mat(MTx.YellowPhosphor, 1));
        RM.Mixer.addRecipe2(true, 16, 16, dust.mat(MTx.BluePhosphor, 1), dust.mat(MTx.YellowPhosphor, 1), dust.mat(MTx.WhitePhosphor, 2));
        RM.Mixer.addRecipe2(true, 16, 16, dustSmall.mat(MTx.BluePhosphor, 2), dustSmall.mat(MTx.YellowPhosphor, 2), dust.mat(MTx.WhitePhosphor, 1));
        RM.Mixer.addRecipeX(true, 16, 16, ST.array(dust.mat(MTx.RedPhosphor, 1), dust.mat(MTx.GreenPhosphor, 1), dust.mat(MTx.BluePhosphor, 1)), dust.mat(MTx.WhitePhosphor, 3));
        RM.Mixer.addRecipeX(true, 16, 16, ST.array(dustTiny.mat(MTx.RedPhosphor, 3), dustTiny.mat(MTx.GreenPhosphor, 3), dustTiny.mat(MTx.BluePhosphor, 3)), dust.mat(MTx.WhitePhosphor, 1));

        /// MOVPE precursors
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Li, 2), FL.array(MTx.Ether.liquid(2*U, true), MTx.CH3Cl.gas(U, true)), FL.array(MTx.CH3Li.liquid(3*U, false)), dust.mat(MT.LiCl, 2));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.Al, 1), dust.mat(MT.Na, 3), FL.array(MTx.CH3Cl.gas(3*U, true), MTx.Ether.liquid(6*U, true)), MTx.AlMe3.liquid(7*U, false), dust.mat(MT.NaCl, 6));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.Ga, 1), MT.Cl.gas(3*U, true), NF, dust.mat(MTx.GaCl3, 1));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.In, 1), MT.Cl.gas(3*U, true), NF, dust.mat(MTx.InCl3, 1));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.Be, 1), MT.Cl.gas(2*U, true), NF, dust.mat(MTx.BeCl2, 1));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.In2O3, 5), MT.HCl.gas(12*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.InCl3, 2));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.Ga2O3, 5), MT.HCl.gas(12*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.GaCl3, 2));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.Se, 1), MT.H.gas(2*U, true), MTx.H2Se.gas(U, false), NI);
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.GaCl3, 1), MTx.AlMe3.liquid(7*U, true), MTx.GaMe3.liquid(7*U, false), dust.mat(MTx.AlCl3, 1));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.GaCl3, 1), MTx.CH3Li.liquid(9*U, true), MTx.GaMe3.liquid(7*U, false), dust.mat(MT.LiCl, 6));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.InCl3, 1), MTx.CH3Li.liquid(9*U, true), MTx.InMe3.liquid(7*U, false), dust.mat(MT.LiCl, 6));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.BeCl2, 1), MTx.CH3Li.liquid(6*U, true), MTx.BeMe2.liquid(5*U, false), dust.mat(MT.LiCl, 4));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Mg, 1), MTx.Cyclopentadiene.liquid(2*U, true), MT.H.gas(2*U, false), dust.mat(MTx.Magnesocene, 1));

        /// LED MOVPE
        // Red = GaAsP on GaAs
        RMx.VacuumChamber.addRecipe1(true, 16, 128, plateGem.mat(MT.Sapphire, 1), FL.array(MTx.GaMe3.liquid(7*U8, true), MTx.AsH3.gas(U8, true), MTx.SiH4.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[0][0].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[0][0].get(1), FL.array(MTx.GaMe3.liquid(7*U8, true), MTx.AsH3.gas(U16, true), MTx.PH3  .gas(U16  , true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[0][1].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[0][0].get(2), FL.array(MTx.GaMe3.liquid(7*U4, true), MTx.AsH3.gas(U8 , true), MTx.PH3  .gas(U8   , true)), FL.array(MTx.Ether.liquid(6*U4, false), MT.CH4.gas(3*U4, false)), ILx.LEDWafers[0][1].get(2));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[0][1].get(1), FL.array(MTx.GaMe3.liquid(7*U8, true), MTx.AsH3.gas(U8 , true), MTx.BeMe2.liquid(5*U1000, true)), FL.array(MTx.Ether.liquid(6*U8+4*U1000, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[0][2].get(1));

        // Green = AlGaP on GaP
        RMx.VacuumChamber.addRecipe1(true, 16, 128, plateGem.mat(MT.Sapphire, 1), FL.array(MTx.GaMe3.liquid(7*U8, true), MTx.PH3.gas(U8, true), MTx.H2Se.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[1][0].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[1][0].get(1), FL.array(MTx.GaMe3.liquid(7*U16, true), MTx.AlMe3.liquid(7*U16, true), MTx.PH3.gas(U8, true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[1][1].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[1][0].get(2), FL.array(MTx.GaMe3.liquid(7*U8 , true), MTx.AlMe3.liquid(7*U8 , true), MTx.PH3.gas(U4, true)), FL.array(MTx.Ether.liquid(6*U4, false), MT.CH4.gas(3*U4, false)), ILx.LEDWafers[1][1].get(2));
        RMx.VacuumChamber.addRecipe2(true, 16, 128, ILx.LEDWafers[1][1].get(1), dustDiv72.mat(MTx.Magnesocene, 1), FL.array(MTx.GaMe3.liquid(7*U8 , true), MTx.PH3.gas(U8 , true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[1][2].get(1));

        // Blue  = InGaN on GaN
        RMx.VacuumChamber.addRecipe1(true, 16, 128, plateGem.mat(MT.Sapphire, 1), FL.array(MTx.GaMe3.liquid(7*U8, true), MT.NH3.gas(U8, true), MTx.SiH4.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[2][0].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[2][0].get(1), FL.array(MTx.GaMe3.liquid(7*U16, true), MTx.InMe3.liquid(7*U16, true), MT.NH3.gas(U8, true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[2][1].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.LEDWafers[2][0].get(2), FL.array(MTx.GaMe3.liquid(7*U8, true), MTx.InMe3.liquid(7*U8, true), MT.NH3.gas(U4, true)), FL.array(MTx.Ether.liquid(6*U4, false), MT.CH4.gas(3*U4, false)), ILx.LEDWafers[2][1].get(2));
        RMx.VacuumChamber.addRecipe2(true, 16, 128, ILx.LEDWafers[2][1].get(1), dustDiv72.mat(MTx.Magnesocene, 1), FL.array(MTx.GaMe3.liquid(7*U8, true), MT.NH3.gas(U8, true)), FL.array(MTx.Ether.liquid(6*U8, false), MT.CH4.gas(3*U8, false)), ILx.LEDWafers[2][2].get(1));

        // Dicing
        for (int color = 0; color < ILx.LEDWaferColors.length; color++) {
            for (int i = 0; i < RMx.CuttingFluids.length; i++)
                if (RMx.CuttingFluids[i] != null) {
                    RM.Cutter.addRecipe1(true, 16, 64 * RMx.CuttingMultiplier[i], ILx.LEDWafers[color][2].get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.LEDWafers[color][3].get(64));
                }
        }

        // LED packaging
        for (int color = 0; color < ILx.LEDWaferColors.length; color++) {
            RM.Press.addRecipeX(true, 16, 32, ST.array(ILx.LEDWafers[color][3].get(16), plateTiny.mat(MT.Invar, 2), wireFine.mat(MT.Au, 1)), ILx.LEDs[color][0].get(16));
        }
        // White LED
        RM.Press.addRecipeX(true, 16, 16, ST.array(ILx.LEDs[2][0].get(4 ), dustSmall.mat(MTx.YellowPhosphor, 1)), ILx.LEDs[3][0].get(4));
        RM.Press.addRecipeX(true, 16, 64, ST.array(ILx.LEDs[2][0].get(16), dust     .mat(MTx.YellowPhosphor, 1)), ILx.LEDs[3][0].get(16));
        // LED Encapsulation
        for (int color = 0; color < ILx.LEDColors.length; color++) {
            RM.Bath.addRecipe1(true, 0, 16, ILx.LEDs[color][0].get(1), MTx.Epoxy.liquid(U16, true), NF, ILx.LEDs[color][1].get(1));
        }

        // CRTs
        RM.Mixer.addRecipe2(true, 16, 16, ST.tag(2), dust.mat(MT.Graphite, 1), FL.DistW.make(1000), NF, dust.mat(MTx.Aquadag, 2));
        RM.Mixer.addRecipe2(true, 16, 16, ST.tag(2), dustSmall.mat(MT.Graphite, 2), FL.DistW.make(500), NF, dust.mat(MTx.Aquadag, 1));

        CR.shaped(ILx.CRT_Black_White.get(1), CR.DEF_REV, " WP", "CAS", " WP", 'W', wireGt02.dat(ANY.Cu), 'P', dust.dat(MTx.WhitePhosphor), 'C', ILx.Cathode_Tungsten, 'A', dust.dat(MT.Al), 'S', block.dat(MT.Glass));
        CR.shaped(ILx.CRT_RGB.get(1), CR.DEF_REV, "CWR", "CSG", "CAB", 'W', wireGt04.dat(ANY.Cu), 'C', ILx.Cathode_Tungsten, 'A', dust.dat(MTx.Aquadag), 'S', block.dat(MT.Glass), 'R', dust.dat(MTx.RedPhosphor), 'G', dust.dat(MTx.GreenPhosphor), 'B', dust.dat(MTx.BluePhosphor));

        // LCDs
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.AlCl3, 0), dust.mat(MTx.Biphenyl, 1), FL.array(MTx.Chloropentane.liquid(U, true), MT.Br.liquid(U, true)), FL.array(MT.HCl.gas(2 * U, false)), dust.mat(MTx.Bromo4pentylbiphenyl, 1));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.Bromo4pentylbiphenyl, 1), dust.mat(MTx.CuCN, 1), NF, FL.make(FLx.LiquidCrystal5CB, 144), dust.mat(MTx.CuBr, 1));
        RMx.VacuumChamber.addRecipe2(true, 16, 128, foil.mat(MTx.PVA, 1), OM.dust(MT.I, U8), ILx.PolaroidFilter.get(1));
        for (int i = 0; i < RMx.CuttingFluids.length; i++) if (RMx.CuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, 8 * RMx.CuttingMultiplier[i], ILx.PolaroidFilter.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 8, 1000, true), NF, ILx.PolaroidFilterTiny.get(9));
            //RM.Cutter.addRecipe1(true, 16, 8 * RMx.CuttingMultiplier[i], ILx.TFTGlass.get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 8, 1000, true), NF, ILx.TCFGlassTiny.get(9));
        }
        CR.shaped(ILx.LEDStrip.get(1), CR.DEF_REV, "LeL", "WRx", "LGL", 'L', ILx.LEDs[3][1], 'W', cableGt01.dat(ANY.Cu), 'R', stick.dat(MT.Al), 'G', IL.Bottle_Glue);
        CR.shaped(ILx.LEDBacklight.get(1), CR.DEF_REV, "L C", "LGP", "L W", 'L', ILx.LEDStrip, 'C', OD_CIRCUITS[5], 'G', IL.Bottle_Glue, 'P', plate.dat(MT.Polycarbonate), 'W', wireGt01.dat(ANY.Cu));
        CR.shaped(ILx.LCDElectrodes.get(1), CR.DEF_REV, " C ", " G ", " T ", 'C', ILx.Wafers[LCD_COLOR_IDX][0][6].get(1), 'G', IL.Bottle_Glue, 'T', ILx.Wafers[TFT_IDX][0][10].get(1));
        RM.Injector.addRecipe1(true, 16, 128, ILx.LCDElectrodes.get(1), FL.make(FLx.LiquidCrystal5CB, 1), NF, ILx.LCDElectrodesCrystal.get(1));
        CR.shaped(ILx.LCD.get(1), CR.DEF_REV, "RG ", "PLP", 'R', OreDictToolNames.rollingpin, 'G', IL.Bottle_Glue, 'P', ILx.PolaroidFilter, 'L', ILx.LCDElectrodesCrystal);
        CR.shaped(ILx.LCDMonitor.get(1), CR.DEF_REV, " IF", "GLB", "OIW", 'I', casingSmall.dat(MT.Polycarbonate), 'F', foil.dat(MT.Polycarbonate), 'G', OP.foil.dat(MT.Glass), 'L', ILx.LCD, 'B', ILx.LEDBacklight, 'O', MTEx.gt6MTEReg.getItem(32711), 'W', cableGt01.dat(ANY.Cu));

        // Casings
        for (OreDictMaterial mat : new OreDictMaterial[] { MT.Plastic, MT.Polycarbonate }) {
            CR.shaped(casingMachine.mat(mat, 1), CR.DEF, "SPP", "PwP", "PPS", 'S', stickLong.dat(mat), 'P', plate.dat(mat));
        }

        /// Solar Panels
        // Poly-Si
        RMx.Thermolysis.addRecipe1(true, 16, 128, polyGem.mat(MTx.PDopedSi, 1), FL.array(MTx.POCl3.liquid(U1000, true)), FL.array(MT.HCl.gas(U1000, false), MT.O.gas(U1000, false)), ILx.SolarWafers[0][0].get(1));

        // Mono-Si
        RM.Bath.addRecipe1(true, 0, 200, plateGem.mat(MTx.PDopedSi, 1), FL.array(MTx.KOHSolution.liquid(12*U10, true)), FL.array(MT.H.gas(4*U10, false), MT.H2O.liquid(3*U10, false)), ILx.SolarWafers[1][0].get(1));
        RM.Bath.addRecipe1(true, 0, 200, ILx.SolarWafers[1][0].get(1), FL.array(MT.HF.gas(16*U10, true)), FL.array(MT.H2SiF6.liquid(9*U10, false), MTx.KFSolution.liquid(10*U10, false), MT.H2O.liquid(3*U10, false)), ILx.SolarWafers[1][1].get(1));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ILx.SolarWafers[1][1].get(1), FL.array(MTx.POCl3.liquid(U1000, true)), FL.array(MT.HCl.gas(U1000, false), MT.O.gas(U1000, false)), ILx.SolarWafers[1][2].get(1));
        RMx.Thermolysis.addRecipe1(true, 64, 256, ILx.SolarWafers[1][2].get(1), FL.array(MTx.SiH4.gas(6*U100, true), MT.NH3.gas(8*U100, true)), MT.H.gas(48*U100, false), ILx.SolarWafers[1][3].get(1));

        // Thin-Film precursors
        RM.Mixer.addRecipe2(true, 16, 32, dust.mat(MT.OREMATS.Cassiterite, 1), dustDiv72.mat(MTx.SnF2, 2), dust.mat(MTx.FTO, 1));
        RM.Mixer.addRecipe2(true, 16, 32, dust.mat(MTx.CdS, 1), dustDiv72.mat(MT.Ga, 1), dust.mat(MTx.NDopedCdS, 1));
        RM.Mixer.addRecipe2(true, 16, 32, dust.mat(MT.Cd, 1), dust.mat(MT.Te, 1), dust.mat(MTx.CdTe, 2));
        RM.Mixer.addRecipe2(true, 16, 32, dust.mat(MTx.CdTe, 1), dustDiv72.mat(MT.Cu, 1), dust.mat(MTx.PDopedCdTe, 1));
        RM.Mixer.addRecipeX(true, 16, 32, ST.array(dust.mat(MT.Cd, 1), dust.mat(MT.Te, 1), dustDiv72.mat(MT.Cu, 2)), dust.mat(MTx.PDopedCdTe, 2));

        // CdTe
        RMx.IonBombardment.addRecipe2(false, 32, 64, foil.mat(MT.Glass, 1), dustSmall.mat(MTx.FTO, 1), MT.Ar.gas(U100, true), NF, ILx.SolarWafers[2][0].get(1));
        RMx.VacuumChamber.addRecipe2(false, 16, 64, ILx.SolarWafers[2][0].get(1), dustDiv72.mat(MTx.NDopedCdS, 2), ILx.SolarWafers[2][1].get(1));
        RMx.VacuumChamber.addRecipe2(false, 16, 64, ILx.SolarWafers[2][1].get(1), dustDiv72.mat(MTx.PDopedCdTe, 9), ILx.SolarWafers[2][2].get(1));
        RMx.VacuumChamber.addRecipe2(false, 16, 128, ILx.SolarWafers[2][1].get(2), dustSmall.mat(MTx.PDopedCdTe, 1), ILx.SolarWafers[2][2].get(2));
        RMx.VacuumChamber.addRecipe2(false, 16, 512, ILx.SolarWafers[2][1].get(8), dust.mat(MTx.PDopedCdTe, 1), ILx.SolarWafers[2][2].get(8));
        RMx.IonBombardment.addRecipe2(false, 32, 64, ILx.SolarWafers[2][2].get(1), dustTiny.mat(MT.Electrum, 1), MT.Ar.gas(U100, true), NF, ILx.SolarPanelCdTe.get(1));

        // CIGS
        RMx.IonBombardment.addRecipe2(false, 32, 128, foil.mat(MT.Glass, 1), dustSmall.mat(MT.Mo, 1), MT.Ar.gas(U100, true), NF, ILx.SolarWafers[3][0].get(1));
        RMx.VacuumChamber.addRecipeX(false, 16, 128, ST.array(ILx.SolarWafers[3][0].get(1), dustTiny.mat(MTx.CuInGa, 1), dustTiny.mat(MT.Se, 1)), ILx.SolarWafers[3][1].get(1));
        RMx.VacuumChamber.addRecipeX(false, 16, 128*9, ST.array(ILx.SolarWafers[3][0].get(9), dust.mat(MTx.CuInGa, 1), dust.mat(MT.Se, 1)), ILx.SolarWafers[3][1].get(9));
        RMx.VacuumChamber.addRecipeX(false, 16, 64, ST.array(ILx.SolarWafers[3][0].get(1), dustDiv72.mat(MTx.CIGS, 9)), ILx.SolarWafers[3][1].get(1));
        RMx.VacuumChamber.addRecipeX(false, 16, 128, ST.array(ILx.SolarWafers[3][0].get(2), dustSmall.mat(MTx.CIGS, 1)), ILx.SolarWafers[3][1].get(2));
        RMx.VacuumChamber.addRecipeX(false, 16, 512, ST.array(ILx.SolarWafers[3][0].get(8), dust.mat(MTx.CIGS, 1)), ILx.SolarWafers[3][1].get(8));
        RMx.VacuumChamber.addRecipe2(false, 16, 64, ILx.SolarWafers[3][1].get(1), dustDiv72.mat(MTx.NDopedCdS, 2), ILx.SolarWafers[3][2].get(1));
        RMx.IonBombardment.addRecipe2(false, 32, 64, ILx.SolarWafers[3][2].get(1), dustTiny.mat(MTx.ITO, 1), MT.Ar.gas(U100, true), NF, ILx.SolarPanelCIGS.get(1));

        // Multi-Junction GaAs
        /// Substrate + back contact
        RMx.VacuumChamber.addRecipe1(true, 16, 128, plateGem.mat(MTx.PDopedGe, 1), MT.Al.liquid(U4, true), NF, ILx.SolarWafers[4][0].get(1));
        /// MOVPE of bottom cell
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][0].get(1), FL.array(MTx.GeH4.gas(U8, true), MTx.AsH3.gas(U1000, true)), ZL_FS, ILx.SolarWafers[4][1].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][1].get(1), FL.array(MTx.InMe3.liquid(7*U100, true), MTx.GaMe3.liquid(7*U100, true), MTx.PH3.gas(U50, true), MTx.H2Se.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][2].get(1));
        /// bottom tunnel junction
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][2].get(1), FL.array(MTx.GaMe3.liquid(7*U50, true), MTx.AsH3.gas(U50, true), MTx.GeH4.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][3].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][3].get(1), FL.array(MTx.GaMe3.liquid(7*U50, true), MTx.AsH3.gas(U50, true), MTx.BeMe2.liquid(5*U1000, true)), FL.array(MTx.Ether.liquid(6*U50+4*U1000, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][4].get(1));
        /// middle cell
        RMx.VacuumChamber.addRecipe2(true, 16, 128, ILx.SolarWafers[4][4].get(1), dustDiv72.mat(MTx.Magnesocene, 1), FL.array(MTx.InMe3.liquid(7*U100, true), MTx.GaMe3.liquid(7*U100, true), MTx.PH3.gas(U50, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][5].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][5].get(1), FL.array(MTx.GaMe3.liquid(7*U8, true), MTx.AsH3.gas(U8, true), MTx.BeMe2.liquid(5*U1000, true)), FL.array(MTx.Ether.liquid(6*U8+4*U1000, false), MT.CH4.gas(3*U8, false)), ILx.SolarWafers[4][6].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][6].get(1), FL.array(MTx.GaMe3.liquid(7*U50, true), MTx.AsH3.gas(U50, true), MTx.GeH4.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][7].get(1));
        /// top tunnel junction
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][7].get(1), FL.array(MTx.AlMe3.liquid(7*U100, true), MTx.GaMe3.liquid(7*U100, true), MTx.AsH3.gas(U50, true), MTx.GeH4.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][8].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][8].get(1), FL.array(MTx.AlMe3.liquid(7*U100, true), MTx.GaMe3.liquid(7*U100, true), MTx.AsH3.gas(U50, true), MTx.BeMe2.liquid(5*U1000, true)), FL.array(MTx.Ether.liquid(6*U50+4*U1000, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][9].get(1));
        /// top cell
        RMx.VacuumChamber.addRecipe2(true, 16, 128, ILx.SolarWafers[4][9].get(1), dustDiv72.mat(MTx.Magnesocene, 1), FL.array(MTx.AlMe3.liquid(7*U100, true), MTx.InMe3.liquid(7*U100, true), MTx.PH3.gas(U50, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][10].get(1));
        RMx.VacuumChamber.addRecipe2(true, 16, 128, ILx.SolarWafers[4][10].get(1), dustDiv72.mat(MTx.Magnesocene, 1), FL.array(MTx.InMe3.liquid(7*U50, true), MTx.GaMe3.liquid(7*U50, true), MTx.PH3.gas(U25, true)), FL.array(MTx.Ether.liquid(6*U25, false), MT.CH4.gas(3*U25, false)), ILx.SolarWafers[4][11].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][11].get(1), FL.array(MTx.InMe3.liquid(7*U100, true), MTx.GaMe3.liquid(7*U100, true), MTx.PH3.gas(U50, true), MTx.H2Se.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][12].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][12].get(1), FL.array(MTx.AlMe3.liquid(7*U100, true), MTx.InMe3.liquid(7*U100, true), MTx.PH3.gas(U50, true), MTx.H2Se.gas(U1000, true)), FL.array(MTx.Ether.liquid(6*U50, false), MT.CH4.gas(3*U50, false)), ILx.SolarWafers[4][13].get(1));
        /// ARC
        RMx.IonBombardment.addRecipe2(true, 64, 128, ILx.SolarWafers[4][13].get(1), dustDiv72.mat(MT.TiO2, 27), MT.Ar.gas(U100, true), NF, ILx.SolarWafers[4][14].get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.SolarWafers[4][14].get(1), FL.array(MTx.NaOHSolution.liquid(12*U8, true)), FL.array(MT.H2O.liquid(9*U8, false)), ILx.SolarWafers[4][15].get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.SolarWafers[4][15].get(1), FL.array(MTx.DiluteHCl.liquid(4*5*U8, true)), FL.array(MT.SaltWater.liquid(2*8*U8, false), MT.HCl.gas(2*2*U8, false)), ILx.SolarWafers[4][16].get(1), dustDiv72.mat(MT.TiO2, 9));
        /// Front contact
        RMx.Photolithography.addRecipe2(true, 16, 128, ILx.SolarWafers[4][16].get(1), ILx.Photomasks[MJ_PV_IDX][0][0][PM_FINISHED].get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.SolarWafers[4][17].get(1));
        for (FluidStack developer : DEVELOPERS)
            RM.Bath.addRecipe1(false, 0, 128, ILx.SolarWafers[4][17].get(1), developer, NF, ILx.SolarWafers[4][18].get(1));
        RMx.IonBombardment.addRecipe1(true, 64, 128, ILx.SolarWafers[4][18].get(1), FL.array(MT.Cl.gas(4*U50, true)), FL.array(MT.TiCl4.liquid(5*U50, false), MT.O.gas(2*U50, false)), ILx.SolarWafers[4][19].get(1));
        RMx.VacuumChamber.addRecipe1(true, 16, 128, ILx.SolarWafers[4][19].get(1), MT.Al.liquid(U8, true), NF, ILx.SolarWafers[4][20].get(1));
        RM.Bath.addRecipe1(true, 0, 128, ILx.SolarWafers[4][20].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.SolarWafers[4][21].get(1));

        // Semiconductor Recycling
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.GaN, 1), FL.array(MT.HCl.gas(6 * U, true)), FL.array(MT.NH3.gas(U, false)), OM.dust(MTx.GaCl3, U));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.GaP, 1), FL.array(MT.HCl.gas(6 * U, true)), FL.array(MTx.PH3.gas(U, false)), OM.dust(MTx.GaCl3, U));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.GaAs, 1), FL.array(MT.HCl.gas(6 * U, true)), FL.array(MTx.AsH3.gas(U, false)), OM.dust(MTx.GaCl3, U));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.GaAsP, 1), FL.array(MT.HCl.gas(6 * U, true)), FL.array(MTx.AsH3.gas(U2, false), MTx.PH3.gas(U2, false)), OM.dust(MTx.GaCl3, U));
        for (FluidStack water : FL.waters(1000)) {
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.InGaN, 2), FL.array(MT.HCl.gas(12 * U, true), water), FL.array(MT.NH3.gas(2 * U, false), MTx.GaCl3Solution.liquid(2*U, false)), OM.dust(MTx.InCl3, U));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.InGaP, 2), FL.array(MT.HCl.gas(12 * U, true), water), FL.array(MTx.PH3.gas(2 * U, false), MTx.GaCl3Solution.liquid(2*U, false)), OM.dust(MTx.InCl3, U));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.InGaAs, 2), FL.array(MT.HCl.gas(12 * U, true), water), FL.array(MTx.AsH3.gas(2 * U, false), MTx.GaCl3Solution.liquid(2*U, false)), OM.dust(MTx.InCl3, U));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.AlGaP, 2), FL.array(MT.HCl.gas(12 * U, true), water), FL.array(MTx.PH3.gas(2 * U, false), MTx.GaCl3Solution.liquid(2*U, false)), OM.dust(MTx.AlCl3, U));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.AlGaAs, 2), FL.array(MT.HCl.gas(12 * U, true), water), FL.array(MTx.AsH3.gas(2 * U, false), MTx.GaCl3Solution.liquid(2*U, false)), OM.dust(MTx.AlCl3, U));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.AlInP, 2), FL.array(MT.HCl.gas(12 * U, true), water), FL.array(MTx.PH3.gas(2 * U, false), MTx.InCl3Solution.liquid(2*U, false)), OM.dust(MTx.AlCl3, U));
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.CrystallisationCrucible.mRecipeList) {
            if (ST.equal(r.mInputs[0], OM.dust(MT.Ge, U9))) {
                r.mInputs[0] = plateGemTiny.mat(MT.Ge, 1);
            } else if (ST.equal(r.mInputs[0], OM.dust(MT.Si, U9))) {
                r.mInputs[0] = plateGemTiny.mat(MT.Si, 1);
            } else if (ST.equal(r.mInputs[0], OM.dust(MT.Ge, U))) {
                r.mInputs[0] = plateGem.mat(MT.Ge, 1);
            } else if (ST.equal(r.mInputs[0], OM.dust(MT.Si, U))) {
                r.mInputs[0] = plateGem.mat(MT.Si, 1);
            }
        }

        // Disable old ways of crafting circuits & computers
        List<ItemStack> circuit_boards = Arrays.asList(IL.Circuit_Board_Basic.get(1), IL.Circuit_Board_Good.get(1), IL.Circuit_Board_Advanced.get(1), IL.Circuit_Board_Elite.get(1), IL.Circuit_Board_Master.get(1), IL.Circuit_Board_Ultimate.get(1));
        List<ItemStack> circuit_parts = Arrays.asList(IL.Circuit_Part_Basic.get(1), IL.Circuit_Part_Good.get(1), IL.Circuit_Part_Advanced.get(1), IL.Circuit_Part_Elite.get(1), IL.Circuit_Part_Master.get(1), IL.Circuit_Part_Ultimate.get(1));
        List<ItemStack> circuit_plates = Arrays.asList(IL.Circuit_Plate_Copper.get(1), IL.Circuit_Plate_Gold.get(1), IL.Circuit_Plate_Platinum.get(1));
        List<ItemStack> circuit_wires = Arrays.asList(IL.Circuit_Wire_Copper.get(1), IL.Circuit_Wire_Gold.get(1), IL.Circuit_Wire_Platinum.get(1));

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

        CRx.disableGt6(IL.Circuit_Wire_Copper.get(1));

        // Old solar panels
        for (int id : new int[]{10050, 10051}) {
            ItemStack stack = MTEx.gt6MTEReg.getItem(id);
            CRx.disableGt6(stack);
            ST.hide(stack);
        }

        CRx.overrideShaped(IL.Processor_Crystal_Empty.get(1), CR.DEF_REV, "WWW", "WCW", "WWW", 'W', wireFine.dat(MT.Pt), 'C', casingSmall.dat(MT.Polycarbonate));
        OM.data(IL.Processor_Crystal_Empty.get(1), MT.Polycarbonate, U2, MT.Pt, U);

        CRx.overrideShaped(IL.USB_Stick_1.get(1), CR.DEF_REV, "iCF", "PBP", "TdT", 'C', ILx.ICs[0], 'F', ILx.FlashChips[0], 'B', ILx.Circuit_Plate_Copper_Tiny  , 'P', plateTiny.dat(MT.Al            ), 'T', screw.dat(MT.Al            ));
        CRx.overrideShaped(IL.USB_Stick_2.get(1), CR.DEF_REV, "iCF", "PBP", "TdT", 'C', ILx.ICs[1], 'F', ILx.FlashChips[1], 'B', ILx.Circuit_Plate_Gold_Tiny    , 'P', plateTiny.dat(MT.StainlessSteel), 'T', screw.dat(MT.StainlessSteel));
        CRx.overrideShaped(IL.USB_Stick_3.get(1), CR.DEF_REV, "iCF", "PBP", "TdT", 'C', ILx.ICs[1], 'F', ILx.FlashChips[2], 'B', ILx.Circuit_Plate_Gold_Tiny    , 'P', plateTiny.dat(MT.Cr            ), 'T', screw.dat(MT.Cr            ));
        CRx.overrideShaped(IL.USB_Stick_4.get(1), CR.DEF_REV, "iCF", "PBP", "TdT", 'C', ILx.ICs[2], 'F', ILx.FlashChips[2], 'B', ILx.Circuit_Plate_Platinum_Tiny, 'P', plateTiny.dat(MT.Ti            ), 'T', screw.dat(MT.Ti            ));

        CRx.overrideShaped(IL.USB_HDD_1.get(1), CR.DEF_REV,"   ", "CxH", "   ", 'C', IL.USB_Cable_1, 'H', ILx.HDDs[0]);
        CRx.overrideShaped(IL.USB_HDD_2.get(1), CR.DEF_REV,"   ", "CxH", "   ", 'C', IL.USB_Cable_2, 'H', ILx.HDDs[0]);
        CRx.overrideShaped(IL.USB_HDD_3.get(1), CR.DEF_REV,"   ", "CxH", "   ", 'C', IL.USB_Cable_3, 'H', ILx.HDDs[1]);
        CRx.overrideShaped(IL.USB_HDD_4.get(1), CR.DEF_REV,"   ", "CxH", "   ", 'C', IL.USB_Cable_4, 'H', ILx.HDDs[1]);

        /// Replace crystalprocessors with computers in some cases

        // Crystal Chargers
        for (int tier = 0; tier < 7; tier++) {
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10130 + tier), CR.DEF_REV_NCC, "FCF", "FCF", "PMC", 'C', OD_CIRCUITS[tier], 'P', PC_NAMES[(tier - 1) / 2], 'F', IL.FIELD_GENERATORS[tier], 'M', OP.casingMachine.dat(MT.DATA.Electric_T[tier]));
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10140 + tier), CR.DEF_REV_NCC, "FCF", "FCF", "PMC", 'C', OD_CIRCUITS[tier], 'P', PC_NAMES[(tier - 1) / 2], 'F', IL.FIELD_GENERATORS[tier], 'M', MTEx.gt6MTEReg.getItem(10130+tier));
        }

        // Quantum Energizers, Laser Absorbers
        for (int tier = 1; tier < 6; tier++) {
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10120 + tier), CR.DEF_REV_NCC, "PFC", "SME", "CFC", 'M', OP.casingMachine.dat(MT.Osmiridium), 'F', IL.FIELD_GENERATORS[tier], 'S', IL.SENSORS[tier], 'E', IL.EMITTERS[tier], 'P', PC_NAMES[(tier - 1) / 2], 'C', OD_CIRCUITS[tier]);
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10150 + tier), CR.DEF_REV_NCC, "SCW", "SMC", "SCW", 'M', OP.casingMachine.dat(MT.DATA.Electric_T[tier]), 'S', OP.plateGem.dat(ANY.Si), 'W', MT.DATA.CABLES_01[tier], 'C', OD_CIRCUITS[tier]);
        }

        // ZPM dechargers
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(11170), CR.DEF_REV_NCC, "PCE", "CMC", "FCF", 'C', OD_CIRCUITS[6], 'P', PC_NAMES[2], 'F', IL.FIELD_GENERATORS[6], 'M', OP.casingMachineDense.dat(MT.Osmiridium), 'E', IL.EMITTERS[6]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(11171), CR.DEF_REV_NCC, "PCS", "CMC", "FCF", 'C', OD_CIRCUITS[6], 'P', PC_NAMES[2], 'F', IL.FIELD_GENERATORS[6], 'M', OP.casingMachineDense.dat(MT.Osmiridium), 'S', IL.SENSORS[6]);

        // Large machines
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17100), CR.DEF_REV_NCC, "CMC", " R "       , 'M', MTEx.gt6MTEReg.getItem(18100), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17102), CR.DEF_REV_NCC, "PSP", "PSP", "RMC", 'M', MTEx.gt6MTEReg.getItem(18002), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.StainlessSteel), 'S', OP.stickLong.dat(MT.StainlessSteel));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17103), CR.DEF_REV_NCC, "CMC", " R "       , 'M', MTEx.gt6MTEReg.getItem(18105), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17104), CR.DEF_REV_NCC, "CRC", "PMP", "APA", 'M', MTEx.gt6MTEReg.getItem(18002), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.StainlessSteel), 'A', IL.ROBOT_ARMS[2]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17105), CR.DEF_REV_NCC, "CRC", "PMP", "PPP", 'M', MTEx.gt6MTEReg.getItem(18002), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.StainlessSteel));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17106), CR.DEF_REV_NCC, "PPP", "PwP", "RMC", 'M', MTEx.gt6MTEReg.getItem(18007), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.Invar));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17107), CR.DEF_REV_NCC, "GGG", "SwS", "RMC", 'M', MTEx.gt6MTEReg.getItem(18006), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(MT.Ti), 'S', OP.stick.dat(MT.Ti));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17108), CR.DEF_REV_NCC, "GSG", "SGS", "RMC", 'M', MTEx.gt6MTEReg.getItem(18003), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(MT.TungstenSteel), 'S', OP.gearGtSmall.dat(MT.TungstenSteel));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17109), CR.DEF_REV_NCC, "SGS", "GSG", "RMC", 'M', MTEx.gt6MTEReg.getItem(18003), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(MT.TungstenSteel), 'S', OP.gearGtSmall.dat(MT.TungstenSteel));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17110), CR.DEF_REV_NCC, " P ", "PAP", "RMC", 'M', MTEx.gt6MTEReg.getItem(18023), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.TungstenSteel), 'A', IL.ROBOT_ARMS[2]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17112), CR.DEF_REV_NCC, "CRC", "PMP", "PPP", 'M', MTEx.gt6MTEReg.getItem(18022), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.StainlessSteel));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17113), CR.DEF_REV_NCC, "PPP", "CRC", "PMP", 'M', MTEx.gt6MTEReg.getItem(18002), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'P', OP.plateDense.dat(MT.StainlessSteel));
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17114), CR.DEF_REV_NCC, "GSG", "GSG", "RMC", 'M', MTEx.gt6MTEReg.getItem(18009), 'R', PC_NAMES[0], 'C', OD_CIRCUITS[6], 'G', OP.gearGt.dat(ANY.Steel), 'S', OP.gearGtSmall.dat(ANY.Steel));

        // Logistics, Lightning rod, Bedrock Drill
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17997), CR.DEF_REV_NCC, "CCC", "PSP", "CMC", 'M', OP.casingMachine.dat(MT.SteelGalvanized), 'P', PC_NAMES[1], 'C', OD_CIRCUITS[6], 'S', SCREEN_NAMES[1]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17998), CR.DEF_REV_NCC, "CWC", "PMP", "CWC", 'M', OP.casingMachine.dat(ANY.W), 'W', OP.wireGt16.dat(MT.NiobiumTitanium), 'P', PC_NAMES[1], 'C', OD_CIRCUITS[6]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(17999), CR.DEF_REV_NCC, "PYP", "CMC", "GIG", 'M', OP.casingMachineDense.dat(MT.Ti), 'G', OP.gearGt.dat(MT.TungstenSteel), 'I', OP.toolHeadDrill.dat(MT.TungstenSteel), 'P', PC_NAMES[1], 'Y', IL.CONVEYERS[5], 'C', OD_CIRCUITS[6]);

        // Quad Core blocks
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(18200), CR.DEF_REV_NCC,"GCH", "RPH", "RMO", 'M', ILx.Motherboards[2], 'O', ILx.ComputerCase,  'R', ILx.RAMSticks[2], 'C', ILx.CPUs[2], 'P', ILx.Thermal_Paste, 'H', ILx.HDDs[2], 'G', ILx.GraphicsCards[2]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(18201), CR.DEF_REV_NCC,"CCC", "PPP", "RMO", 'M', ILx.Motherboards[2], 'O', ILx.ComputerCase,  'R', ILx.RAMSticks[2], 'C', ILx.CPUs[2], 'P', ILx.Thermal_Paste);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(18202), CR.DEF_REV_NCC,"GC ", "GPH", "RMO", 'M', ILx.Motherboards[2], 'O', ILx.ComputerCase,  'R', ILx.RAMSticks[2], 'C', ILx.CPUs[2], 'P', ILx.Thermal_Paste, 'H', ILx.HDDs[2], 'G', ILx.GraphicsCards[2]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(18203), CR.DEF_REV_NCC,"HCH", "HPH", "RMO", 'M', ILx.Motherboards[2], 'O', ILx.ComputerCase,  'R', ILx.RAMSticks[2], 'C', ILx.CPUs[2], 'P', ILx.Thermal_Paste, 'H', ILx.HDDs[2]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(18204), CR.DEF_REV_NCC,"RCR", "RPR", " MO", 'M', ILx.Motherboards[2], 'O', ILx.ComputerCase,  'R', ILx.RAMSticks[2], 'C', ILx.CPUs[2], 'P', ILx.Thermal_Paste);

        // USB Switches
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(19000), CR.DEF_REV_NCC, "UCU", "TMT", "UdU", 'M', OP.casingMachine.dat(MT.SteelGalvanized), 'T', OP.screw.dat(MT.SteelGalvanized), 'U', OD_USB_CABLES[3], 'C', OD_CIRCUITS[3]);
        CRx.overrideShaped(MTEx.gt6MTEReg.getItem(19001), CR.DEF_REV_NCC, "CWC", "TMT", "UdU", 'M', OP.casingMachine.dat(MT.SteelGalvanized), 'T', OP.screw.dat(MT.SteelGalvanized), 'U', OD_USB_CABLES[3], 'C', OD_CIRCUITS[3], 'W', cableGt01.dat(MT.Cu));

        // Logistics Buses
        CRx.overrideShaped(IL.Cover_Logistics_Fluid_Export   .get(1), CR.DEF_REV, "  w", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Fluid_Import   .get(1), CR.DEF_REV, " w ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Fluid_Storage  .get(1), CR.DEF_REV, "w  ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Item_Export    .get(1), CR.DEF_REV, "  r", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Item_Import    .get(1), CR.DEF_REV, " r ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Item_Storage   .get(1), CR.DEF_REV, "r  ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Generic_Export .get(1), CR.DEF_REV, "  d", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Generic_Import .get(1), CR.DEF_REV, " d ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Generic_Storage.get(1), CR.DEF_REV, "d  ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));
        CRx.overrideShaped(IL.Cover_Logistics_Dump           .get(1), CR.DEF_REV, "   ", "WQW", "CPC", 'Q', IL.Cover_Blank, 'P', SOC_NAMES[1], 'C', OD_CIRCUITS[4], 'W', OP.wireFine.dat(MT.Os));

        // Matter fabs, Mol. Scanners, Matter Reps, Nanoscale Fabricators, Plantalyzers, Bumblelyzers
        for (int tier = 1; tier < 6; tier++) {
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(20410 + tier), CR.DEF_REV_NCC, "PXY", "FMF", "YXY", 'M', OP.casingMachine.dat(MT.Osmiridium), 'P', PC_NAMES[2], 'X', IL.EMITTERS[tier], 'Y', IL.SENSORS[tier], 'F', IL.FIELD_GENERATORS[tier]);
            if (tier == 3)
                CRx.overrideShaped(MTEx.gt6MTEReg.getItem(20420 + tier), CR.DEF_REV_NCC, "PFY", "FMF", "YFY", 'M', OP.casingMachine.dat(MT.Osmiridium), 'P', PC_NAMES[2], 'F', IL.FIELD_GENERATORS[tier], 'Y', IL.SENSORS[tier]);
            
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(20430 + tier), CR.DEF_REV_NCC, "PXH", "FMF", "HXH", 'M', OP.casingMachine.dat(MT.Osmiridium), 'P', PC_NAMES[2], 'H', OD_USB_DRIVES[4], 'X', IL.EMITTERS[tier], 'F', IL.FIELD_GENERATORS[tier]);
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(20440 + tier), CR.DEF_REV_NCC, "KAX", "ZMY", "CSC", 'M', OP.casingMachine.dat(MT.DATA.Electric_T[tier]), 'C', OD_CIRCUITS[6], 'A', IL.Comp_Laser_Gas_Ar, 'K', IL.Comp_Laser_Gas_Kr, 'X', IL.Comp_Laser_Gas_Xe, 'S', PC_NAMES[(tier - 1) / 2], 'Y', IL.EMITTERS[tier], 'Z', IL.SENSORS[tier]);
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(20530 + tier), CR.DEF_REV_NCC, "WXW", "ZMS", "PYC", 'M', OP.casingMachine.dat(MT.DATA.Electric_T[tier]), 'C', OD_CIRCUITS[tier], 'W', MT.DATA.CABLES_01[tier], 'P', PC_NAMES[0], 'S', SCREEN_NAMES[0], 'X', IL.EMITTERS[tier], 'Y', IL.SENSORS[tier], 'Z', OP.treeSapling);
            CRx.overrideShaped(MTEx.gt6MTEReg.getItem(20540 + tier), CR.DEF_REV_NCC, "WXW", "ZMS", "PYC", 'M', OP.casingMachine.dat(MT.DATA.Electric_T[tier]), 'C', OD_CIRCUITS[tier], 'W', MT.DATA.CABLES_01[tier], 'P', PC_NAMES[0], 'S', SCREEN_NAMES[0], 'X', IL.EMITTERS[tier], 'Y', IL.SENSORS[tier], 'Z', OD.container1000honey);
        }

        // Misc
        CRx.overrideShaped(IL.Tool_Scanner           .get(1), CR.DEF_REV, "EXR", "CPU", "BXB", 'B', IL.Battery_Alkaline_HV, 'X', OP.plate.dat(MT.Cr), 'U', OD_USB_STICKS[0], 'C', OD_USB_CABLES[0], 'E', IL.EMITTERS[4], 'R', IL.SENSORS[4], 'P', SOC_NAMES[0]);
        CRx.overrideShaped(IL.Aneutronic_Fusion_Empty.get(1), CR.DEF_REV, "VPV", "GFG", "VGV", 'P', SOC_NAMES[2], 'V', OP.plateGemTiny.dat(MT.Vb), 'F', IL.FIELD_GENERATORS[5], 'G', OP.foil.dat(MT.Graphene));
    }

    private void addFusionRecipes() {
        RM.Fusion.mRecipeList.clear();
        RM.Fusion.mRecipeItemMap.clear();
        RM.Fusion.mRecipeFluidMap.clear();

        // Terrestrial fusion candidates
        RM.Fusion.addRecipe1(false, -8192,  730, ST.tag(1), FL.array(MT.D     .plasma(U*2, true)                           ), FL.array(MT.He_3  .plasma(  U2, false), MT.T     .plasma(  U2, false)                                                  ), ZL_IS             ).setSpecialNumber(  730L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1130, ST.tag(1), FL.array(MT.T     .plasma(U*2, true)                           ), FL.array(MT.He    .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1130L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1290, ST.tag(1), FL.array(MT.He_3  .plasma(U*2, true)                           ), FL.array(MT.He    .plasma(  U , false), MT.H     .plasma(2*U , false)                                                  ), ZL_IS             ).setSpecialNumber( 1290L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1000, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.Li_6.plasma(U  , true)), FL.array(MT.Be_7  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1000L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  800, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.Li  .plasma(U  , true)), FL.array(MT.Be_8  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  800L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  546, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.B_11.plasma(U  , true)), FL.array(MT.He    .plasma(3*U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 8469L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  315, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.C   .plasma(U  , true)), FL.array(MT.C_13  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  315L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  754, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.C_13.plasma(U  , true)), FL.array(MT.N     .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  754L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1404, ST.tag(2), FL.array(MT.H     .plasma(U*2, true), MT.N   .plasma(U  , true)), FL.array(MT.He    .plasma(  U2, false), MT.C     .plasma(  U2, false), MT.O     .plasma(  U2, false)                   ), ZL_IS             ).setSpecialNumber( 1404L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  455, ST.tag(2), FL.array(MT.H     .plasma(U*2, true), MT.O   .plasma(U  , true)), FL.array(MT.He    .plasma(  U2, false), MT.F     .gas   (  U2, false), MT.N     .plasma(  U2, false)                   ), ZL_IS             ).setSpecialNumber(  455L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1760, ST.tag(2), FL.array(MT.D     .plasma(U  , true), MT.T   .plasma(U  , true)), FL.array(MT.He    .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1760L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1830, ST.tag(2), FL.array(MT.D     .plasma(U  , true), MT.He_3.plasma(U  , true)), FL.array(MT.He    .plasma(  U , false), MT.H     .plasma(  U , false)                                                  ), ZL_IS             ).setSpecialNumber( 1830L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 2640, ST.tag(2), FL.array(MT.T     .plasma(U  , true), MT.He_3.plasma(U  , true)), FL.array(MT.He    .plasma(3*U4, false), MT.D     .plasma(  U4, false)                                                  ), ZL_IS             ).setSpecialNumber( 2640L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 3336, ST.tag(2), FL.array(MT.D     .plasma(U  , true), MT.Li_6.plasma(U  , true)), FL.array(MT.He    .plasma(3*U8, false), MT.He_3  .plasma(  U8, false), MT.Li    .plasma(  U8, false), MT.Be_7  .plasma(  U8, false)), ZL_IS ).setSpecialNumber( 3336L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192, 1690, ST.tag(2), FL.array(MT.He_3  .plasma(U  , true), MT.Li_6.plasma(U  , true)), FL.array(MT.He    .plasma(2*U , false), MT.H     .plasma(  U , false)                                                  ), ZL_IS             ).setSpecialNumber( 1690L*8192L*16L);

        // Triple-alpha
        RM.Fusion.addRecipe1(false, 0    , 1890, ST.tag(1), FL.array(MT.He    .plasma(U*2, true)                           ), FL.array(MT.Be_8  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1890L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  736, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Be_8.plasma(U  , true)), FL.array(MT.C     .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  736L*8192L*16L);

        // alpha capture process
        RM.Fusion.addRecipe1(false, -8192,  716, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.C   .plasma(U  , true)), FL.array(MT.O     .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  716L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  473, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.O   .plasma(U  , true)), FL.array(MT.Ne    .gas   (  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  473L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  932, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ne  .gas   (U  , true)), FL.array(MT.Mg    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  932L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  998, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Mg  .gas   (U  , true)), FL.array(MT.Si    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  998L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  695, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Si  .liquid(U  , true)), FL.array(MT.S     .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  695L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  664, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.S   .liquid(U  , true)), FL.array(MT.Ar    .gas   (  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  664L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  704, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ar  .gas   (U  , true)), FL.array(MT.Ca    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  704L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  513, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ca  .liquid(U  , true)), FL.array(MT.Ti    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  513L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  770, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ti  .liquid(U  , true)), FL.array(MT.Cr    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  770L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  794, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Cr  .liquid(U  , true)), FL.array(MT.Fe    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  794L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  800, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Fe  .liquid(U  , true)), FL.array(MT.Ni    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  800L*8192L*16L);

        // Burning carbon, oxygen
        RM.Fusion.addRecipe1(false, -8192,  685, ST.tag(1), FL.array(MT.C     .plasma(U*2, true)), FL.array(MT.Na.liquid(U4, false), MT.Ne.gas(U4, false), MT.Mg.gas(U4, false), MT.O.plasma(U4, false), MT.He.plasma(3*U4, false), MT.H.plasma(U4, false)), ZL_IS).setSpecialNumber( 685L*8192L*16L);
        RM.Fusion.addRecipe1(false, -8192,  807, ST.tag(1), FL.array(MT.O     .plasma(U*2, true)), FL.array(MT.Si.liquid(U4, false), MT.P .gas(U4, false), MT.Mg.gas(U4, false), MT.S.liquid(U4, false), MT.He.plasma(3*U4, false), MT.H.plasma(U4, false)), ZL_IS).setSpecialNumber( 807L*8192L*16L);

        // Vibranium
        RM.Fusion.addRecipe1(false, -8192, 1956, ST.tag(2), FL.array(MT.Ad    .liquid(U  , true), MT.Be_7.plasma(U  , true)), FL.array(MT.W     .liquid(  U , false), MT.He.plasma(16*U, false), MT.He_3.plasma(24*U, false),MT.T.plasma(24*U, false)), dust.mat(MT.Vb, 1)).setSpecialNumber(94956L*8192L*16L);
    }
}
