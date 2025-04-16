package org.altadoon.gt6x.features.engines;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.config.ConfigCategories;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.loaders.b.Loader_OreProcessing;
import gregtech.tileentity.tanks.MultiTileEntityBarrelMetal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;

import static gregapi.data.CS.*;
import static gregapi.data.OP.dust;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static org.altadoon.gt6x.common.MTEx.*;
import static org.altadoon.gt6x.common.RMx.FMx;

import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.features.crucibles.recipes.CrucibleUtils;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.engines.blocks.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.altadoon.gt6x.features.engines.OreDictPrefixes.*;

public class Engines extends GT6XFeature {
    private static final OreDictMaterial[] ENGINE_MATERIALS = new OreDictMaterial[] { MT.Bronze, MT.ArsenicCopper, MT.ArsenicBronze, MT.IronCast, MTx.Alusil, MTx.Hastelloy, MTx.Ti6Al4V, MTx.TMS196 };
    private static final long[] ENGINE_OUTPUTS = new long[] { 16, 16, 24, 32, 64, 128, 256, 512 };
    private static final OreDictMaterial[] CATALYTIC_CONVERTER_MATERIALS = new OreDictMaterial[] { MT.Pt, MT.Pd, MT.Rh };

    public static final String FEATURE_NAME = "EngineOverhaul";
    private static final String CONFIG_ENGINE_SOUNDS_ENABLED = "engineSoundsEnabled";
    public static boolean ENGINE_SOUNDS_ENABLED = true;

    @Override
    public void configure(Config cfg) {
        ENGINE_SOUNDS_ENABLED = cfg.cfg.getBoolean(CONFIG_ENGINE_SOUNDS_ENABLED, FEATURE_NAME, true, "Enable engine sounds");
    }

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void preInit() {
        FMx.engineOverhaulEnabled = true;
        OreDictPrefixes.init();
        pistonHead.forceItemGeneration(ENGINE_MATERIALS).forceItemGeneration(MTx.A6061);
        piston.forceItemGeneration(ENGINE_MATERIALS).forceItemGeneration(MTx.A6061);
        catalyticConverter.forceItemGeneration(CATALYTIC_CONVERTER_MATERIALS);
        tbcCoatedRotor.forceItemGeneration(MT.Magnalium, MTx.Hastelloy, MTx.Ti6Al4V, MTx.TMS196, MT.Vibramantium);
    }

    @Override public void init() {}

    @Override
    public void afterGt6Init() {
        addMTEs();
    }

    @Override
    public void beforeGt6PostInit() {
        addFuels();
    }

    @Override
    public void postInit() {
        addRecipes();
        disableGT6Engines();
    }

    @Override
    public void afterGt6PostInit() {
        removeOldEngineRecipes();
    }

    private void addMTEs() {
        MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;
        OreDictMaterial mat;

        for (int i = 0; i < ENGINE_OUTPUTS.length; i++) {
            mat = ENGINE_MATERIALS[i];
            reg.add("Engine Block ("+mat.getLocal()+")" , "Engines",  MTEx.IDs.EngineBlock[i].get(), 1304, MTEEngineBlockRaw.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F));
            OreDictPrefixes.registerCustomPrefixItem(OreDictPrefixes.engineBlock, mat, reg.getItem());
            reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.PetrolEngine[i].get(), 1304, MTEEnginePetrol.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_FUELMAP, FMx.Petrol, NBT_EFFICIENCY, 10000, NBT_OUTPUT, ENGINE_OUTPUTS[i], NBT_ENERGY_EMITTED, TD.Energy.RU), "PUP", "PMP", "LSC", 'M', OreDictPrefixes.engineBlock.dat(mat), 'P', piston.dat(mat), 'S', OP.stick.dat(mat), 'C', catalyticConverter.dat(MT.Pd), 'L', OD.itemLubricant, 'U', ILx.SparkPlugs);
            reg.add("Diesel Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.DieselEngine[i].get(), 1304, MTEEngineDiesel.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_FUELMAP, FMx.Diesel, NBT_EFFICIENCY, 10000, NBT_OUTPUT, ENGINE_OUTPUTS[i], NBT_ENERGY_EMITTED, TD.Energy.RU), "PNP", "PMP", "LSC", 'M', OreDictPrefixes.engineBlock.dat(mat), 'P', piston.dat(mat), 'S', OP.stick.dat(mat), 'C', catalyticConverter.dat(MT.Pt), 'L', OD.itemLubricant, 'N', OP.pipeNonuple.dat(mat));
        }

        // Some nitro engine blocks are made from A6061. Source: https://sbj.net/stories/2024-coolest-things-made-in-the-ozarks-billet-aluminum-engine-block,93860
        mat = MTx.A6061;
        reg.add("Engine Block ("+mat.getLocal()+")", "Engines",  MTEx.IDs.EngineBlockA6061.get(), 1304, MTEEngineBlockRaw.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.0F, NBT_RESISTANCE, 12.0F));
        OreDictPrefixes.registerCustomPrefixItem(OreDictPrefixes.engineBlock, mat, reg.getItem());
        reg.add("Nitro Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.NitroEngine     .get(), 1304, MTEEngineNitro   .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.0F, NBT_RESISTANCE, 12.0F, NBT_FUELMAP, FMx.Nitro, NBT_EFFICIENCY, 10000, NBT_OUTPUT, 4096, NBT_ENERGY_EMITTED, TD.Energy.RU), "PUP", "PMP", "LSC", 'M', OreDictPrefixes.engineBlock.dat(mat), 'P', piston.dat(mat), 'S', OP.stick.dat(mat), 'C', ILx.SuperCharger, 'L', OD.itemLubricant, 'U', ILx.SparkPlugs);

        // Gas turbines
        String category = "Multiblock Machines";
        mat = MT.StainlessSteel; reg.add("Magnalium Gas Turbine Main Housing"   , category, IDs.GasTurbine1       .get(), 17101, MultiTileEntityLargeTurbineGasX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "gasturbine", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18022                       , NBT_INPUT,   3072, NBT_OUTPUT,   2048, NBT_WASTE_ENERGY, false, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas), "RPR", "RMS", "RCR", 'R', tbcCoatedRotor.dat(MT .Magnalium  ), 'S', OP.stickLong.dat(MT .Magnalium  ), 'P', ILx.SparkPlugs, 'C', catalyticConverter.dat(MT.Rh), 'M', gt6MTEReg.getItem(18022));

        mat = MTx.Hastelloy;     reg.add("Dense Hastelloy Wall"                 , category, IDs.DenseWallHastelloy.get(), 17101, MultiTileEntityMultiBlockPart  .class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_TEXTURE, "metalwalldense", NBT_DESIGNS, 7)); OM.data(reg.getItem(), mat, U*36); RM.Welder.addRecipe2(false, 64, 512, OP.plateDense.mat(mat, 4), ST.tag(10), reg.getItem());
                                 reg.add("Hastelloy Gas Turbine Main Housing"   , category, IDs.GasTurbine2       .get(), 17101, MultiTileEntityLargeTurbineGasX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_TEXTURE, "gasturbine", NBT_MTE_MULTIBLOCK_PART_REG, gt6xMTERegId, NBT_DESIGN, IDs.DenseWallHastelloy.get(), NBT_INPUT,   6144, NBT_OUTPUT,   4096, NBT_WASTE_ENERGY, false, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas), "RPR", "RMS", "RCR", 'R', tbcCoatedRotor.dat(MTx.Hastelloy  ), 'S', OP.stickLong.dat(MTx.Hastelloy  ), 'P', ILx.SparkPlugs, 'C', catalyticConverter.dat(MT.Rh), 'M', reg.getItem());

        mat = MTx.Ti6Al4V;       reg.add("Dense Ti6Al4V Wall"                   , category, IDs.DenseWallTi6Al4V  .get(), 17101, MultiTileEntityMultiBlockPart  .class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_TEXTURE, "metalwalldense", NBT_DESIGNS, 7)); OM.data(reg.getItem(), mat, U*36); RM.Welder.addRecipe2(false, 64, 512, OP.plateDense.mat(mat, 4), ST.tag(10), reg.getItem());
                                 reg.add("Ti6Al4V Gas Turbine Main Housing"     , category, IDs.GasTurbine3       .get(), 17101, MultiTileEntityLargeTurbineGasX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_TEXTURE, "gasturbine", NBT_MTE_MULTIBLOCK_PART_REG, gt6xMTERegId, NBT_DESIGN, IDs.DenseWallTi6Al4V  .get(), NBT_INPUT,  12288, NBT_OUTPUT,   8192, NBT_WASTE_ENERGY, false, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas), "RPR", "RMS", "RCR", 'R', tbcCoatedRotor.dat(MTx.Ti6Al4V    ), 'S', OP.stickLong.dat(MTx.Ti6Al4V    ), 'P', ILx.SparkPlugs, 'C', catalyticConverter.dat(MT.Rh), 'M', reg.getItem());

        mat = MTx.TMS196;        reg.add("Dense TMS-196 Wall"                   , category, IDs.DenseWallTMS196   .get(), 17101, MultiTileEntityMultiBlockPart  .class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "metalwalldense", NBT_DESIGNS, 7)); OM.data(reg.getItem(), mat, U*36); RM.Welder.addRecipe2(false, 64, 512, OP.plateDense.mat(mat, 4), ST.tag(10), reg.getItem());
                                 reg.add("TMS-196 Gas Turbine Main Housing"     , category, IDs.GasTurbine4       .get(), 17101, MultiTileEntityLargeTurbineGasX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "gasturbine", NBT_MTE_MULTIBLOCK_PART_REG, gt6xMTERegId, NBT_DESIGN, IDs.DenseWallTMS196   .get(), NBT_INPUT,  24576, NBT_OUTPUT,  16384, NBT_WASTE_ENERGY, false, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas), "RPR", "RMS", "RCR", 'R', tbcCoatedRotor.dat(MTx.TMS196     ), 'S', OP.stickLong.dat(MTx.TMS196     ), 'P', ILx.SparkPlugs, 'C', catalyticConverter.dat(MT.Rh), 'M', reg.getItem());

        mat = MT.Ad;             reg.add("Vibramantium Gas Turbine Main Housing", category, IDs.GasTurbine5       .get(), 17101, MultiTileEntityLargeTurbineGasX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 100.0F, NBT_RESISTANCE, 100.0F, NBT_TEXTURE, "gasturbine", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18025                       , NBT_INPUT, 196608, NBT_OUTPUT, 131072, NBT_WASTE_ENERGY, false, NBT_LIMIT_CONSUMPTION, T, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_ENERGY_EMITTED, TD.Energy.RU, NBT_FUELMAP, FM.Gas), "RPR", "RMS", "RCR", 'R', tbcCoatedRotor.dat(MT.Vibramantium), 'S', OP.stickLong.dat(MT.Vibramantium), 'P', ILx.SparkPlugs, 'C', catalyticConverter.dat(MT.Rh), 'M', gt6MTEReg.getItem(18025));

        CrucibleUtils.addCruciblePart(MTEMoldEngineBlock.class, "Basin", "Molds", MTEx.IDs.EngineBlockMolds.get(), ILx.Ceramic_Engine_Block_Mold, ILx.Fireclay_Engine_Block_Mold, "PPP", "P P", "hPw", 'P');

        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PipesCastIron .get(), 26142, 150, true, false, false, false, true, false, true, true, MTEx.gt6xMTEReg, MTEx.MachineBlock, MultiTileEntityPipeFluid.class, MT.IronCast);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PipesAlusil   .get(), 26142, 150, true, false, false, false, true, false, true, true, MTEx.gt6xMTEReg, MTEx.MachineBlock, MultiTileEntityPipeFluid.class, MTx.Alusil);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PipesHastelloy.get(), 26142, 300, true, false, false, false, true, false, true, true, MTEx.gt6xMTEReg, MTEx.MachineBlock, MultiTileEntityPipeFluid.class, MTx.Hastelloy);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PipesTi6Al4V  .get(), 26142, 450, true, true , false, false, true, false, true, true, MTEx.gt6xMTEReg, MTEx.MachineBlock, MultiTileEntityPipeFluid.class, MTx.Ti6Al4V);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PipesTMS196   .get(), 26142, 600, true, true , true , false, true, false, true, true, MTEx.gt6xMTEReg, MTEx.MachineBlock, MultiTileEntityPipeFluid.class, MTx.TMS196);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PipesA6061    .get(), 26142, 200, true, false, false, false, true, false, true, true, MTEx.gt6xMTEReg, MTEx.MachineBlock, MultiTileEntityPipeFluid.class, MTx.A6061);

        mat = MT.IronCast  ; reg.add("Cast Iron Drum"         , "Fluid Containers", MTEx.IDs.DrumCastIron .get(), 32719, MultiTileEntityBarrelMetal.class, 0, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE,   5.0F, NBT_TANK_CAPACITY, 64000L , NBT_PLASMAPROOF, false, NBT_GASPROOF, true , NBT_ACIDPROOF, false, NBT_MAGICPROOF, false), " h ", "PSP", "PSP", 'P', OP.plateCurved.dat(mat), 'S', OP.stickLong.dat(mat));
        mat = MTx.Hastelloy; reg.add("Hastelloy Drum"         , "Fluid Containers", MTEx.IDs.DrumHastelloy.get(), 32719, MultiTileEntityBarrelMetal.class, 0, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE,   8.0F, NBT_TANK_CAPACITY, 128000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true , NBT_ACIDPROOF, false, NBT_MAGICPROOF, false), " h ", "PSP", "PSP", 'P', OP.plateCurved.dat(mat), 'S', OP.stickLong.dat(mat));
        mat = MTx.Ti6Al4V  ; reg.add("Ti-6Al-4V Drum"         , "Fluid Containers", MTEx.IDs.DrumTi6Al4V  .get(), 32719, MultiTileEntityBarrelMetal.class, 0, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE,  10.0F, NBT_TANK_CAPACITY, 256000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true , NBT_ACIDPROOF, true , NBT_MAGICPROOF, false), " h ", "PSP", "PSP", 'P', OP.plateCurved.dat(mat), 'S', OP.stickLong.dat(mat));
        mat = MTx.TMS196   ; reg.add("TMS-196 Superalloy Drum", "Fluid Containers", MTEx.IDs.DrumTMS196   .get(), 32719, MultiTileEntityBarrelMetal.class, 0, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 1.0F, NBT_RESISTANCE,  15.0F, NBT_TANK_CAPACITY, 512000L, NBT_PLASMAPROOF, true , NBT_GASPROOF, true , NBT_ACIDPROOF, true , NBT_MAGICPROOF, false), " h ", "PSP", "PSP", 'P', OP.plateCurved.dat(mat), 'S', OP.stickLong.dat(mat));
    }

    private void removeOldEngineRecipes() {
        // Disable old engine recipes
        FM.Engine.mRecipeList.clear();
        FM.Engine.mRecipeItemMap.clear();
        FM.Engine.mRecipeFluidMap.clear();

        // Replace nitro fuel recipe
        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mFluidOutputs.length == 1 && r.mFluidOutputs[0].isFluidEqual(MT.NitroFuel.liquid(U, true)))
                r.mEnabled = false;
        }
        RM.Mixer.addRecipe0(true, 16, 16, FL.array(MTx.Methanol.liquid(U100, true), MTx.Nitromethane.liquid(9*U100, true)), MT.NitroFuel.liquid(U10, false), ZL_IS);
    }

    private void addFuels() {
        // Gas fuels. Assumed to be compressed at +/- 100 bar for their fuel values per liter to be somewhat realistic
        FMx.gas(16, 1, FL.Hydrogen.make(2), 0, STEAM_PER_EU * 16L);
        FMx.gas(16, 3, FL.Gas_Natural.make(1), 1, STEAM_PER_EU * 32L);
        FMx.gas(16, 3, FL.Methane.make(1), 1, STEAM_PER_EU * 32L);
        FMx.gas(16, 4, FL.Ethylene.make(1), 2, STEAM_PER_EU * 32L);
        FMx.gas(16, 5, FL.make(FLx.Ethane, 1), 2, STEAM_PER_EU * 48L);
        FMx.gas(16, 6, FL.Propylene.make(1), 3, STEAM_PER_EU * 48L);
        FMx.gas(16, 7, FL.Propane.make(1), 3, STEAM_PER_EU * 64L);
        FMx.gas(16, 8, MTx.Butylene.gas(U1000, true), 4, STEAM_PER_EU * 64L);
        FMx.gas(16, 9, FL.Butane.make(1), 4, STEAM_PER_EU * 80L);

        FMx.gas(16, 6, MTx.CrackerGas.gas(U1000, true), 2, STEAM_PER_EU * 48L);

        // Aromatics
        FMx.petrol(16, 18, MTx.Phenol.liquid(U1000, true), 6, 1);
        FMx.petrol(16, 18, MTx.Benzene.liquid(U1000, true), 6, 1);
        FMx.petrol(16, 18, MTx.Pygas.liquid(U1000, true), 7, 1);
        FMx.petrol(16, 19, MTx.Toluene.liquid(U1000, true), 7, 1);
        FMx.petrol(16, 19, MTx.Reformate.liquid(U1000, true), 7, 1);
        FMx.petrol(16, 19, MTx.AromaticsMix.liquid(U1000, true), 7, 1);
        FMx.petrol(16, 20, MTx.Ethylbenzene.liquid(U1000, true), 8, 1);
        FMx.petrol(16, 20, MTx.Xylene.liquid(U1000, true), 8, 1);
        FMx.petrol(16, 21, MTx.Cumene.liquid(U1000, true), 9, 1);

        // Petroleum fractions
        FMx.petrol(32, 8, FL.make(FLx.LPG, 1), 7, 1);
        for (Fluid naphtha : new Fluid[]{FLx.Naphtha, MTx.NaphthaLowSulfur.mLiquid.getFluid()}) if (naphtha != null) {
            FMx.petrol(32, 9, FL.make(naphtha, 1), 8, 1);
        }
        for (String petrol : new String[]{FL.Petrol.mName, "gasoline"}) if (FL.exists(petrol)) {
            FMx.petrol(32, 10, FL.make(petrol, 1), 9, 1);
        }
        for (String kerosine : new String[]{FL.Kerosine.mName, FLx.JetFuel.getName(), "kerosene"}) if (FL.exists(kerosine)) {
            FMx.diesel(32, 11, FL.make(kerosine, 1), 10, 1, 370);
        }
        for (Fluid diesel : new Fluid[]{FL.Diesel.fluid(), FLx.LAGO, FL.BioDiesel.fluid(), FL.BioFuel.fluid()}) if (diesel != null) {
            FMx.diesel(32, 12, FL.make(diesel, 1), 11, 1, 360);
        }
        for (Fluid hfo : new Fluid[]{FL.Fuel.fluid()}) if (hfo != null) {
            FMx.diesel(32, 13, FL.make(hfo, 1), 12, 1, 420);
        }

        // Alcohols
        FMx.petrol(16, 10, MTx.Methanol.liquid(U1000, true), 1, 1);
        for (String ethanol : FLx.ALCOHOLS) if (FL.exists(ethanol)) {
            FMx.petrol(16, 14, FL.make(ethanol, 1), 2, 1);
        }
        FMx.petrol(16, 15, MTx.Isopropanol.liquid(U1000, true), 3, 1);
        FMx.petrol(16, 17, MTx.Pentanol.liquid(U1000, true), 5, 1);

        // Misc
        FMx.petrol(16, 15, MTx.Ether    .liquid(U1000, true), 4 , 1);
        FMx.petrol(16, 15, MTx.Acetone  .liquid(U1000, true), 3 , 1);

        for (FL oil : FLx.BIO_OILS) if (oil.exists()) {
            FMx.burn(32, 10, oil.make(1), 10, 1);
        }
    }

    private void addRecipes() {
        List<OreDictMaterial> allEngineMats = new ArrayList<>(Arrays.asList(ENGINE_MATERIALS));
        allEngineMats.add(MTx.A6061);
        for (OreDictMaterial mat : allEngineMats) {
            RM.Lathe.addRecipe1(false, 16, 48, OP.ingotDouble.mat(mat, 1), pistonHead.mat(mat, 1), dust.mat(mat, 1));
        }
        piston.addListener(new Loader_OreProcessing.OreProcessing_CraftFrom( 1, ConfigCategories.Recipes.gregtechrecipes + ".piston", new String[][] {{"XY ", "hS ", " Z "}}, OP.bolt, pistonHead, OP.ring, null, null, ANTIMATTER.NOT));

        final long EUt = 64, durationPerUnit = 64 * 6;
        for (OreDictPrefix tPrefix : OreDictPrefix.VALUES) {
            if (tPrefix != null && tPrefix.containsAny(TD.Prefix.EXTRUDER_FODDER, TD.Prefix.INGOT_BASED, TD.Prefix.GEM_BASED, TD.Prefix.DUST_BASED) && U % tPrefix.mAmount == 0) {
                for (OreDictMaterial mat : CATALYTIC_CONVERTER_MATERIALS) {
                    ItemStack inputStack = tPrefix.mat(mat, U / tPrefix.mAmount);
                    if (inputStack != null && inputStack.stackSize <= inputStack.getMaxStackSize()) {
                        RM.Extruder.addRecipe2(true, EUt, durationPerUnit, inputStack, ILx.Shape_SimpleEx_Catalytic_Converter.get(0), catalyticConverter.mat(mat, 1));
                        RM.Extruder.addRecipe2(true, EUt, durationPerUnit, inputStack, ILx.Shape_Extruder_Catalytic_Converter.get(0), catalyticConverter.mat(mat, 1));
                    }
                }
            }
        }

        // Special fuels
        RM.Mixer.addRecipe1(false, 16, 32, ST.tag(10), FL.array(FL.Petrol.make(90), FL.BioEthanol.make(10)), MTx.Super95E10.liquid(U10, false), ZL_IS);
        RM.Mixer.addRecipe1(false, 16, 32, ST.tag(8 ), FL.array(FL.Petrol.make(15), FL.BioEthanol.make(85)), MTx.E85.liquid(U10, false), ZL_IS);
        RM.Mixer.addRecipe1(false, 16, 32, ST.tag(5 ), FL.array(FL.Petrol.make(85), MTx.MTBE   .liquid(U100, true), FL.BioEthanol.make(5)), MTx.SuperPlus98E5.liquid(U10, false), ZL_IS);
        RM.Mixer.addRecipe1(false, 16, 32, ST.tag(5 ), FL.array(FL.Petrol.make(85), MTx.Toluene.liquid(U100, true), FL.BioEthanol.make(5)), MTx.SuperPlus98E5.liquid(U10, false), ZL_IS);
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.CH4.gas(U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.Nitromethane.liquid(U, false), MT.H2O.liquid(3*U, false)));

        FMx.petrol(16, 19, MTx.Super95E10   .liquid(U1000, true), 7, 1);
        FMx.petrol(16, 22, MTx.SuperPlus98E5.liquid(U1000, true), 8, 1);
        FMx.petrol(16, 15, MTx.E85          .liquid(U1000, true), 4, 1);
        FMx.nitro (128,1 , MT .NitroFuel    .liquid(U1000, true), 1, 1, 1);
        FMx.burn  (16, 4 , MTx.Nitromethane .liquid(U1000, true), 1, 1);

        // Biodiesel 3:1 molar ratio of MeOH/EtOH:oil (transesterification of triglycerides), produces 3 moles of biodiesel as well as 1 mole of glycerol
        // vegetable oil: ~875 g/mol, 918 g/l -> 0.95 l/mol
        // ethanol: 46.1 g/mol, 789 g/l -> 0.0584 l/mol
        // methanol: 32.04 g/mol, 792 g/l -> 0.0405 l/mol
        // biodiesel: 295 g/mol, 880 g/l -> 0.3352 l/mol
        // glycerol: 92.1 g/mol, 1260 g/l -> 0.0731 l/mol
        // inputs: volume ratio for 3 moles of EtOH and one mole of oil is 0.175L ethanol for 0.95L oil, simplified to 15 L ethanol, 100 L oil
        // inputs alt: 0.1215 L methanol, 0.95 L oil, simplified to 15 L methanol, 100 L oil
        // outputs: 1 L biodiesel, 0.0731 L glycerol, simplified to 200 L biodiesel, 15 L glycerol
        for (FL oil : FLx.BIO_OILS) if (oil.exists()) {
            RM.Mixer.addRecipe1(true, 16, 150, dust.mat(MT.NaOH, 0), FL.array(oil.make(200), MT .Ethanol .liquid(30*U1000, true)), FL.array(FL.BioFuel.make(200), MT.Glycerol.liquid(15*U1000, false)));
            RM.Mixer.addRecipe1(true, 16, 150, dust.mat(MT.KOH , 0), FL.array(oil.make(200), MT .Ethanol .liquid(30*U1000, true)), FL.array(FL.BioFuel.make(200), MT.Glycerol.liquid(15*U1000, false)));
            RM.Mixer.addRecipe1(true, 16, 150, dust.mat(MT.NaOH, 0), FL.array(oil.make(200), MTx.Methanol.liquid(30*U1000, true)), FL.array(FL.BioFuel.make(200), MT.Glycerol.liquid(15*U1000, false)));
            RM.Mixer.addRecipe1(true, 16, 150, dust.mat(MT.KOH , 0), FL.array(oil.make(200), MTx.Methanol.liquid(30*U1000, true)), FL.array(FL.BioFuel.make(200), MT.Glycerol.liquid(15*U1000, false)));
        }

        // Thermal cracking of glycerol
        RMx.Thermolysis.addRecipe2(true, 32, 100, dust.mat(MT.Ni, 0), dust.mat(MT.Al2O3, 0), MT.Glycerol.liquid(14*U, true), FL.array(MT.CO.gas(6*U, false), MT.H.gas(8*U, false)));

        // Thermal Barrier Coatings
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipeX(true, 16, 500, ST.array(ST.tag(5 ), dust.mat(MTx.ZrOCl2, 40), dust.mat(MTx.Y2O3 , 2 )), FL.array(FL.mul(water, 44), MT .HCl      .gas   (4 *U, true), MT.NH3.gas(22*U, true)), FL.array(MTx.NH4ClSolution.liquid(4*22*U, false)), dust.mat(MTx.YZrOH, 30));
            RM.Mixer.addRecipeX(true, 16, 500, ST.array(ST.tag(10), dust.mat(MTx.ZrOCl2, 40), dust.mat(MTx.Y2O3 , 2 )), FL.array(FL.mul(water, 42), MTx.DiluteHCl.liquid(10*U, true), MT.NH3.gas(22*U, true)), FL.array(MTx.NH4ClSolution.liquid(4*22*U, false)), dust.mat(MTx.YZrOH, 30));
            RM.Mixer.addRecipeX(true, 16, 500, ST.array(ST.tag(5 ), dust.mat(MTx.ZrOCl2, 8 ), dust.mat(MTx.LaNO3, 20)), FL.array(FL.mul(water, 42), MT.NH3.gas(10*U, true)), FL.array(MTx.NH4ClSolution.liquid(4*4*U, false), MTx.NH4NO3Solution.liquid(4*30*U, false)), dust.mat(MTx.LaZrOH, 16));
        }
        RMx.Thermolysis.addRecipe1(true, 32, 200, dust.mat(MTx.YZrOH , 30), NF, MT.H2O.liquid(3*23*U, false), dust.mat(MTx.YSZ     , 15));
        RMx.Thermolysis.addRecipe1(true, 32, 500, dust.mat(MTx.LaZrOH, 16), NF, MT.H2O.liquid(3*7 *U, false), dust.mat(MTx.La2Zr2O7, 8 ));

        RMx.IonBombardment.addRecipeX(false, 16, 128, ST.array(OP.rotor.mat(MT .Magnalium  , 1), dust.mat(MTx.YSZ, 2)                                     ), MT.Ar.gas(U10, true), NF, tbcCoatedRotor.mat(MT .Magnalium, 1));
        RMx.IonBombardment.addRecipeX(false, 16, 128, ST.array(OP.rotor.mat(MTx.Hastelloy  , 1), dust.mat(MTx.YSZ, 2)                                     ), MT.Ar.gas(U10, true), NF, tbcCoatedRotor.mat(MTx.Hastelloy, 1));
        RMx.IonBombardment.addRecipeX(false, 16, 128, ST.array(OP.rotor.mat(MTx.Ti6Al4V    , 1), dust.mat(MTx.YSZ, 2), dust.mat(MTx.CeO2    , 1)), MT.Ar.gas(U10, true), NF, tbcCoatedRotor.mat(MTx.Ti6Al4V, 1));
        RMx.IonBombardment.addRecipeX(false, 16, 128, ST.array(OP.rotor.mat(MTx.TMS196     , 1), dust.mat(MTx.YSZ, 2), dust.mat(MTx.La2Zr2O7, 1)), MT.Ar.gas(U10, true), NF, tbcCoatedRotor.mat(MTx.TMS196 , 1));
        RMx.IonBombardment.addRecipeX(false, 16, 128, ST.array(OP.rotor.mat(MT.Vibramantium, 1), dust.mat(MTx.YSZ, 2), dust.mat(MTx.La2Zr2O7, 1)), MT.Ar.gas(U10, true), NF, tbcCoatedRotor.mat(MT .Vibramantium, 1));
    }

    private void disableGT6Engines() {
        // Disable crafting recipes and hide blocks in NEI
        for (short id : new short[] { 9145, 9146, 9147, 9148, 9149, 9197, 9198, 9199, 17231, 17232, 17233, 17234 }) {
            MTEx.disableGT6MTE(id);
        }
    }
}
