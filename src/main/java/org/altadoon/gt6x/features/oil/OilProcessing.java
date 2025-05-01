package org.altadoon.gt6x.features.oil;

import com.google.common.collect.Iterables;
import gregapi.data.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.common.RMx.FMx;

public class OilProcessing extends GT6XFeature {
    public static final String FEATURE_NAME = "OilProcessing";
    private static final String VACUUM_DIST = "vacuumDistillation";
    protected boolean vacuumDistillation;

    public ItemStack pvcCan = null;
    public ItemStack ptfeCan = null;

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void configure(Config cfg) {
        vacuumDistillation = cfg.cfg.getBoolean(VACUUM_DIST, FEATURE_NAME, true, "Splits the distillation of crude oil into an atmospheric and a vacuum distillation process");
    }

    @Override
    public void preInit() {}

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void beforeGt6PostInit() {
        overrideDistillationRecipes();
        overrideMiscRecipes();
    }

    @Override
    public void postInit() {
        addRecipes();
    }

    @Override
    public void afterGt6PostInit() {
        // delete old recipes and add new
        changeDTowerRecipes();
        changeCrackingRecipes();
        changeElectrolysisRecipes();
    }

    private static final List<FL> LIGHT_OILS = Arrays.asList(FL.Oil_Light, FL.Oil_Light2);
    private static final List<FL> MEDIUM_OILS = Arrays.asList(FL.Oil_Medium, FL.Oil_Normal, FL.Oil_HotCrude);
    private static final List<FL> HEAVY_OILS = Arrays.asList(FL.Oil_Heavy, FL.Oil_Heavy2);
    private static final List<FL> EXTRA_HEAVY_OILS = Collections.singletonList(FL.Oil_ExtraHeavy);
    private static final List<FL> BIOMASS = Arrays.asList(FL.Biomass, FL.BiomassIC2);

    private void overrideDistillationRecipes() {
        FluidStack residue = vacuumDistillation ?
                MTx.ResidueOil.liquid(U1000, false) :
                FL.lube(1);

        for (FL oil : LIGHT_OILS) if (oil.exists())
            RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), oil.make(25), FL.make(FLx.Naphtha,15), FL.mul(residue, 5));
        for (FL oil : MEDIUM_OILS) if (oil.exists())
            RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), oil.make(25), FL.make(FLx.Naphtha,10), FL.mul(residue, 10));
        for (FL oil : HEAVY_OILS) if (oil.exists())
            RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), oil.make(25), FL.make(FLx.LAGO   ,10), FL.mul(residue, 10));
        for (FL oil : EXTRA_HEAVY_OILS) if (oil.exists())
            RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), oil.make(25), FL.make(FLx.LAGO   ,5 ), FL.mul(residue, 15));

        RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), FL.make(FLx.Synoil,  25), FL.Diesel.make( 15), FL.mul(residue, 5));
        RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), FL.Oil_Soulsand.make(25), FL.make(FLx.LAGO,5), FL.mul(residue, 10));
    }

    private void changeDTowerRecipes() {
        @SuppressWarnings("unchecked")
        Set<Integer> inputIds = StreamSupport.stream(
            Iterables.concat(LIGHT_OILS, MEDIUM_OILS, HEAVY_OILS, EXTRA_HEAVY_OILS, BIOMASS, Collections.singletonList(FL.Oil_Soulsand)).spliterator(), false)
                .map(FL::fluid).filter(Objects::nonNull).map(FluidRegistry::getFluidID).collect(Collectors.toSet());

        for (Recipe recipe : RM.DistillationTower.mRecipeList) {
            if (recipe.mFluidInputs.length == 1) {
                if(inputIds.contains(recipe.mFluidInputs[0].getFluidID())) recipe.mEnabled = false;
            }
        }

        // see e.g. https://www.researchgate.net/figure/Process-flow-diagram-of-a-typical-refinery_fig3_355678568
        if (vacuumDistillation) {
            for (FL oil : LIGHT_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 256 , oil.make(100), FL.make(FLx.LPG, 10), FL.make(FLx.Naphtha, 35), FL.Kerosine.make(15), FL.make(FLx.LAGO, 25), MTx.ResidueOil.liquid(15*U1000, false));
            for (FL oil : MEDIUM_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 512 , oil.make(100), FL.make(FLx.LPG, 5), FL.make(FLx.Naphtha, 25), FL.Kerosine.make(15), FL.make(FLx.LAGO, 25), MTx.ResidueOil.liquid(30*U1000, false));
            for (FL oil : HEAVY_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 784 , oil.make(100), FL.make(FLx.LPG, 2), FL.make(FLx.Naphtha, 18), FL.Kerosine.make(10), FL.make(FLx.LAGO, 20), MTx.ResidueOil.liquid(50*U1000, false));
            for (FL oil : EXTRA_HEAVY_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 1024, oil.make(100), FL.make(FLx.LPG, 1), FL.make(FLx.Naphtha, 12), FL.Kerosine.make(5), FL.make(FLx.LAGO, 17), MTx.ResidueOil.liquid(65*U1000, false));

            RM.DistillationTower.addRecipe0(false, 64, 256, FL.Oil_Soulsand.make(100), FL.make(FLx.LPG, 1), FL.make(FLx.Naphtha  , 6 ), FL.Kerosine.make(5), FL.make(FLx.LAGO, 10), MTx.ResidueOil.liquid(78*U1000, false));
            RM.DistillationTower.addRecipe0(false, 64, 784, FL.make(FLx.Synoil,  100), FL.make(FLx.LPG, 2), FL.make(FLx.NaphthaLS, 18), FL.make(FLx.JetFuel, 10), FL.Diesel.make(25), MTx.ResidueOil.liquid(50*U1000, false));
        } else {
            for (FL oil : LIGHT_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 256, new long[]{2000, 2000}, FL.array(oil.make(100)),      FL.array(FL.make(FLx.LPG, 10), FL.make(FLx.Naphtha  , 35), FL.Kerosine.make(   15), FL.make(FLx.LAGO, 25), FL.Fuel.make(15), FL.lube(20)), dustTiny.mat(MT.WaxParaffin, 4), dustTiny.mat(MT.Asphalt, 4));
            for (FL oil : MEDIUM_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 512, new long[]{3000, 3000}, FL.array(oil.make(100)),      FL.array(FL.make(FLx.LPG, 5 ), FL.make(FLx.Naphtha  , 25), FL.Kerosine.make(   15), FL.make(FLx.LAGO, 25), FL.Fuel.make(20), FL.lube(25)), dustTiny.mat(MT.WaxParaffin, 4), dustTiny.mat(MT.Asphalt, 4));
            for (FL oil : HEAVY_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 784, new long[]{4000, 4000}, FL.array(oil.make(100)),      FL.array(FL.make(FLx.LPG, 2 ), FL.make(FLx.Naphtha  , 18), FL.Kerosine.make(   10), FL.make(FLx.LAGO, 20), FL.Fuel.make(25), FL.lube(40)), dustTiny.mat(MT.WaxParaffin, 4), dustTiny.mat(MT.Asphalt, 4));
            for (FL oil : EXTRA_HEAVY_OILS) if (oil.exists())
                RM.DistillationTower.addRecipe0(false, 64, 1024, new long[]{5000, 5000}, FL.array(oil.make(100)),     FL.array(FL.make(FLx.LPG, 1 ), FL.make(FLx.Naphtha  , 12), FL.Kerosine.make(    5), FL.make(FLx.LAGO, 17), FL.Fuel.make(30), FL.lube(55)), dustTiny.mat(MT.WaxParaffin, 4), dustTiny.mat(MT.Asphalt, 4));

            RM.DistillationTower.addRecipe0(false, 64, 256 , new long[]{1000, 1000}, FL.array(FL.Oil_Soulsand.make(100)), FL.array(FL.make(FLx.LPG, 1 ), FL.make(FLx.Naphtha  , 6 ), FL.Kerosine.make(    5), FL.make(FLx.LAGO, 10), FL.Fuel.make(13), FL.lube(40)), dustTiny.mat(MT.WaxParaffin, 4), dustTiny.mat(MT.Asphalt, 4));
            RM.DistillationTower.addRecipe0(false, 64, 784 , new long[]{1000, 1000}, FL.array(FL.make(FLx.Synoil,  100)), FL.array(FL.make(FLx.LPG, 2 ), FL.make(FLx.NaphthaLS, 12), FL.make(FLx.JetFuel, 5), FL.Diesel.make(   17), FL.Fuel.make(30), FL.lube(55)), dustTiny.mat(MT.WaxParaffin, 4), dustTiny.mat(MT.Asphalt, 4));
        }

        for (FL biomass: BIOMASS) if (biomass.exists())
            RM.DistillationTower.addRecipe0(false, 64,  16, new long[]{500}, FL.array(FL.Biomass.make( 80)), FL.array(FL.Reikanol.make(20, FL.BioEthanol), FL.Methane.make(4), FL.DistW.make(50)), dustTiny.mat(MTx.DistillersGrains, 9));
    }

    private void changeCrackingRecipes() {
        for (Recipe recipe : RM.SteamCracking.mRecipeList) recipe.mEnabled = false;
        RM.Generifier.findRecipe(null, null, false, 0, null, FL.array(FL.Gas_Natural.make(1))).mEnabled = false;

        /// Steam cracking
        // see p20, 32 of https://www.fkit.unizg.hr/_download/repository/PRPP_2013_Steam_cracking_Olefins.pdf for ratios
        RM.SteamCracking.addRecipe0(true, 64, 32 , new long[]{50}, FL.array(FL.Steam.make(1024), FL.make(FLx.Ethane, 100)), FL.array(FL.Hydrogen.make(130), FL.Methane.make(25 ), FL.Ethylene.make(100)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(true, 64, 32 , new long[]{50}, FL.array(FL.Steam.make(1024), FL.Propane.make(    100)), FL.array(MTx.CrackerGas.gas(93 *U1000, false), MTx.Pygas.liquid(7 *U1000, false)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(true, 64, 32 , new long[]{50}, FL.array(FL.Steam.make(1024), FL.Butane.make(     100)), FL.array(MTx.CrackerGas.gas(100*U1000, false), MTx.Pygas.liquid(15*U1000, false)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(true, 64, 64 , new long[]{50}, FL.array(FL.Steam.make(2048), FL.make(FLx.LPG,    100)), FL.array(MTx.CrackerGas.gas(193*U1000, false), MTx.Pygas.liquid(22*U1000, false)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(true, 64, 64 , new long[]{50}, FL.array(FL.Steam.make(2048), MTx.NaphthaLowSulfur.liquid(U10, true)), FL.array(MTx.CrackerGas.gas(12*U100, false), MTx.Pygas.liquid(5*U100, false), FL.Fuel.make(5 )), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(true, 64, 96 , new long[]{50}, FL.array(FL.Steam.make(3072), MTx.JetFuel         .liquid(U10, true)), FL.array(MTx.CrackerGas.gas(10*U100, false), MTx.Pygas.liquid(4*U100, false), FL.Fuel.make(30)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(true, 64, 128, new long[]{50}, FL.array(FL.Steam.make(4096), MT .Diesel          .liquid(U10, true)), FL.array(MTx.CrackerGas.gas(9 *U100, false), MTx.Pygas.liquid(4*U100, false), FL.Fuel.make(45)), dustTiny.mat(MT.PetCoke, 9));

        /// Steam reforming
        RM.SteamCracking.addRecipe1(false, 16,  375, new long[]{50}, dust.mat(MT.Ni, 0), FL.array(FL.Steam.make(300*(long)STEAM_PER_WATER), FL.Gas_Natural.make(100)), FL.array(MT.H.gas(540*U1000, false), MT.CO.gas(180*U1000, false)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe1(false, 16,  375,                 dust.mat(MT.Ni, 0), FL.array(FL.Steam.make(300*(long)STEAM_PER_WATER), FL.Methane    .make(100)), FL.array(MT.H.gas(600*U1000, false), MT.CO.gas(200*U1000, false)), ZL_IS);
        for (FL biomass : new FL[] {FL.Biomass, FL.BiomassIC2}) {
            // Biomass gasification, runs as long as a boiler @128 steam/t needs to run to produce an equal amount of steam
            RM.SteamCracking.addRecipe1(false, 16, 375, dust.mat(MT.Ni, 0), FL.array(FL.Steam.make(300 * (long)STEAM_PER_WATER), biomass.make(800)), FL.array(MT.H.gas(6*U10, false), MT.CO.gas(2*U10, false), MT.CO2.gas(3*U10, false)), ZL_IS);
        }
        for (OreDictMaterial coal : new OreDictMaterial[] { MT.Coal, MT.Charcoal, MT.CoalCoke, MT.C, MT.Graphite, MT.PetCoke }) { // for some reason ANY does not work here
            // runs as long as a boiler @256 steam/t needs to run to produce an equal amount of steam
            RM.SteamCracking.addRecipe1(false, 16,  1875, dust.mat(coal, 1), FL.array(FL.Steam.make(3000*(long)STEAM_PER_WATER)), FL.array(MT.H.gas(U*2, false), MT.CO.gas(U*2, false)));
        }

        for (Recipe recipe : RM.CatalyticCracking.mRecipeList) recipe.mEnabled = false;

        // Catalytic reforming
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT .Pt  , 0), FL.array(MTx.NaphthaLowSulfur.liquid(U10, true)), FL.array(MTx.Reformate.liquid(8*U100, false), MTx.CrackerGas.gas(U100, false), FL.Hydrogen.make(100)), dustTiny.mat(MT.PetCoke, 1));
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{ 800}, dust.mat(MTx.PtRe, 0), FL.array(MTx.NaphthaLowSulfur.liquid(U10, true)), FL.array(MTx.Reformate.liquid(9*U100, false), MTx.CrackerGas.gas(U100, false), FL.Hydrogen.make(100)), dustTiny.mat(MT.PetCoke, 1));

        // Catalytic cracking of atmospheric fractions
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{ 500}, dust.mat(MT.Al2O3, 0), FL.array(FL.make(FLx.JetFuel, 100)), FL.array(MTx.CrackerGas.gas(20*U1000, false), FL.make(FLx.LPG, 20), FL.Petrol.make(65), FL.Fuel.make(20)), dustTiny.mat(MT.PetCoke, 1));
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT.Al2O3, 0), FL.array(FL.Diesel.make(      100)), FL.array(MTx.CrackerGas.gas(15*U1000, false), FL.make(FLx.LPG, 15), FL.Petrol.make(65), FL.Fuel.make(30)), dustTiny.mat(MT.PetCoke, 1));

        // Catalytic cracking of vacuum fractions
        if (vacuumDistillation) {
            RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT.Al2O3, 0), FL.array(MTx.LVGO.liquid(U10, true)), FL.array(MTx.CrackerGas.gas(15*U1000, false), FL.make(FLx.LPG, 15), FL.Petrol.make(65), FL.Fuel.make(25)), dustTiny.mat(MT.PetCoke, 1));
            RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1500}, dust.mat(MT.Al2O3, 0), FL.array(MTx.HVGO.liquid(U10, true)), FL.array(MTx.CrackerGas.gas(15*U1000, false), FL.make(FLx.LPG, 15), FL.Petrol.make(65), FL.Fuel.make(30)), dustTiny.mat(MT.PetCoke, 1));
            RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1500}, dust.mat(MT.Al2O3, 0), FL.array(MTx.DAO .liquid(U10, true)), FL.array(MTx.CrackerGas.gas(15*U1000, false), FL.make(FLx.LPG, 15), FL.Petrol.make(65), FL.Fuel.make(40)), dustTiny.mat(MT.PetCoke, 1));
        } else {
            RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1500}, dust.mat(MT.Al2O3, 0), FL.array(MT .Fuel.liquid(U10, true)), FL.array(MTx.CrackerGas.gas(10*U1000, false), FL.make(FLx.LPG, 10), FL.Petrol.make(65), FL.Diesel.make(25)), dustTiny.mat(MT.PetCoke, 1));
        }
    }

    private void changeElectrolysisRecipes() {
        Recipe r;
        // change CO2/CO electrolysis
        r = RM.Electrolyzer.findRecipe(null, null, true, Long.MAX_VALUE, null, FL.array(MT.CO2.gas(3*U, true)), ST.tag(0)); if (r != null) r.mEnabled = false;
        r = RM.Electrolyzer.findRecipe(null, null, true, Long.MAX_VALUE, null, FL.array(MT.CO.gas(2*U, true)), ST.tag(0)); if (r != null) r.mEnabled = false;

        RM.Electrolyzer.addRecipe1(true, 512, 256, dust.mat(MT.Ce, 0), FL.array(MT.CO2.gas(3*U, true)), FL.array(MT.CO.gas(2*U, false), MT.O.gas(U, false)));
    }

    private void addRecipes() {
        // some fuels
        FMx.burn(16, 36, MTx.Synoil.liquid(U1000, true), 12, 2);
        FMx.burn(64, 4, MTx.Cyclopentadiene.liquid(U1000, true), 5, 1);
        FMx.burn(8, 1, MT.CO.gas(2*U1000, true), 3, 0);
        FMx.burn(32, 13, MTx.ResidueOil.liquid(U1000, true), 12, 2);
        FMx.burn(32, 11, MTx.LVGO.liquid(U1000, true), 11, 2);
        FMx.burn(32, 12, MTx.CGO .liquid(U1000, true), 12, 2);
        FMx.burn(32, 13, MTx.HVGO.liquid(U1000, true), 13, 2);
        FMx.burn(32, 14, MTx.DAO .liquid(U1000, true), 14, 2);

        // Natural Gas distillation and liquefaction
        RM.CryoDistillationTower.addRecipe0(true, 64, 32, new long[] {5000}, FL.array(FL.Gas_Natural.make(100)), FL.array(MTx.Naphtha.liquid(U500, false), MT.Butane.gas(U500, false), MT.Propane.gas(U500, false), MTx.Ethane.gas(U200, false), MT.CH4.gas(8*U100, false), MT.N.gas(U500, false), MT.He.gas(U1000, false)), dustTiny.mat(MT.Ice, 1));
        RM.Freezer.addRecipe1(false, 64, 64*3, ST.tag(0), FL.array(FL.Gas_Natural.make(FLx.NG_PER_LNG)), FL.make(FLx.LNG, 1), dustTiny.mat(MT.Ice, 6));
        RM.DistillationTower.addRecipe0(false, 64, 32, new long[] {5000}, FL.array(FL.make(FLx.LNG, 1)), FL.array(MTx.Naphtha.liquid(FLx.NG_PER_LNG/100*U500, false), MT.Butane.gas(FLx.NG_PER_LNG/100*U500, false), MT.Propane.gas(FLx.NG_PER_LNG/100*U500, false), MTx.Ethane.gas(FLx.NG_PER_LNG/100*U200, false), MT.CH4.gas((FLx.NG_PER_LNG/100)*8*U100, false), MT.N.gas(FLx.NG_PER_LNG/100*U500, false), MT.He.gas(FLx.NG_PER_LNG/100*U1000, false)));
        RM.Distillery.addRecipe1(false, 16, 64, ST.tag(0), FL.array(FL.make(FLx.LNG, 1)), FL.array(MT.CH4.gas((FLx.NG_PER_LNG/100)*8*U100, false), MTx.Ethane.gas(FLx.NG_PER_LNG/100*U200, false)));

        // LPG distillation and mixing
        RM.Distillery.addRecipe1(true , 16, 32, ST.tag(0), FL.make(FLx.LPG, 1), FL.Propane.make(1), FL.Butane.make(1));
        RM.CryoMixer.addRecipe0(true, 16, 32, FL.array(FL.Butane.make(1), FL.Propane.make(1)), FL.make(FLx.LPG, 1), ZL_IS);

        // Vacuum distillation
        if (vacuumDistillation) {
            RMx.VacuumChamber.addRecipe1(false, 16, 100, ST.tag(1), MTx.ResidueOil.liquid(U10, true), MTx.VDUFeed.liquid(2*U, false), ZL_IS);

            // HFO blends
            RM.Mixer.addRecipe1(false, 16, 16, ST.tag(1), FL.array(MTx.ResidueOil.liquid(75*U1000, true), FL.Diesel      .make(25)), FL.Fuel.make(100), ZL_IS);
            RM.Mixer.addRecipe1(false, 16, 16, ST.tag(1), FL.array(MTx.ResidueOil.liquid(85*U1000, true), FL.make(FLx.JetFuel, 15)), FL.Fuel.make(100), ZL_IS);
            RM.Mixer.addRecipe1(false, 16, 16, ST.tag(2), FL.array(MTx.ResidueOil.liquid(80*U1000, true), FL.make(FLx.JetFuel, 10), FL.Diesel.make(10)), FL.Fuel.make(100), ZL_IS);

            // Vacuum distillation
            //TODO use item instead of prefix to prevent shredder errors
            RM.Distillery.addRecipe1(false, 32, 200, ST.tag(0), FL.array(MTx.VDUFeed.liquid(2*U, true)), FL.array(MTx.LVGO.liquid(25*U1000, false), MTx.HVGO.liquid(50*U1000, false)), chunkGt.mat(MTx.VacuumResidue, 1));

            // Asphalt blowing
            for (FL air : new FL[] {FL.Air, FL.Air_Nether, FL.Air_End}) {
                RM.Roasting.addRecipe1(true, 16, 72, ingot  .mat(MTx.VacuumResidue, 1), air.make(360), NF, dust     .mat(MT.Asphalt, 1));
                RM.Roasting.addRecipe1(true, 16, 18, chunkGt.mat(MTx.VacuumResidue, 1), air.make(90 ), NF, dustSmall.mat(MT.Asphalt, 1));
                RM.Roasting.addRecipe1(true, 16, 8 , nugget .mat(MTx.VacuumResidue, 1), air.make(40 ), NF, dustTiny .mat(MT.Asphalt, 1));
            }

            // Solvent Deasphalting
            // The lighter the solvent, the cleaner the DAO but the lower the recovery (The Role of Solvent Deasphalting (SDA) In Refining, 2020)
            RM.Mixer.addRecipe1(false, 16, 16, new long[]{6000}, chunkGt.mat(MTx.VacuumResidue, 1), FL.Propane .make(50), MTx.DAO.liquid(40*U1000, false), dustSmall.mat(MT.Asphalt, 4));
            RM.Mixer.addRecipe1(false, 16, 16, new long[]{5000}, chunkGt.mat(MTx.VacuumResidue, 1), FL.make(FLx.LPG, 25), MTx.DAO.liquid(50*U1000, false), dustSmall.mat(MT.Asphalt, 4));
            RM.Mixer.addRecipe1(false, 16, 16, new long[]{4000}, chunkGt.mat(MTx.VacuumResidue, 1), FL.Butane  .make(50), MTx.DAO.liquid(60*U1000, false), dustSmall.mat(MT.Asphalt, 4));

            // Dewaxing
            RM.CryoMixer .addRecipe0(false, 16, 32, FL.array(MTx.HVGO.liquid(U10, true), MTx.MEK.liquid(U, true)), MTx.DewaxedMEKSolution.liquid(11*U10, false), dustSmall.mat(MT.WaxParaffin, 1));
            RM.CryoMixer .addRecipe0(false, 16, 32, FL.array(MTx.DAO     .liquid(U10, true), MTx.MEK.liquid(U, true)), MTx.DewaxedMEKSolution.liquid(11*U10, false), dustSmall.mat(MT.WaxParaffin, 1));
            RM.Distillery.addRecipe1(false, 16, 100, ST.tag(0), FL.array(MTx.DewaxedMEKSolution.liquid(11*U10, true)), FL.array(MTx.MEK.liquid(U, false), FL.lube(100)));

            // Coker
            RM.CokeOven.addRecipe1(false, 0, 3600, ingot    .mat(MTx.VacuumResidue, 1), NF, MTx.CGO.liquid(75*U1000, false), gem.mat(MT.PetCoke, 1));
            RM.CokeOven.addRecipe1(false, 0, 3600, chunkGt  .mat(MTx.VacuumResidue, 4), NF, MTx.CGO.liquid(75*U1000, false), gem.mat(MT.PetCoke, 1));
            RM.CokeOven.addRecipe1(false, 0, 3600, nugget   .mat(MTx.VacuumResidue, 9), NF, MTx.CGO.liquid(75*U1000, false), gem.mat(MT.PetCoke, 1));
            RM.CokeOven.addRecipe1(false, 0, 3600, dust     .mat(MT .Asphalt      , 1), NF, MTx.CGO.liquid(25*U1000, false), gem.mat(MT.PetCoke, 1));
            RM.CokeOven.addRecipe1(false, 0, 3600, dustSmall.mat(MT .Asphalt      , 4), NF, MTx.CGO.liquid(25*U1000, false), gem.mat(MT.PetCoke, 1));
            RM.CokeOven.addRecipe1(false, 0, 3600, dustTiny .mat(MT .Asphalt      , 9), NF, MTx.CGO.liquid(25*U1000, false), gem.mat(MT.PetCoke, 1));

            // Coker gas distillation
            RM.DistillationTower.addRecipe0(false, 64, 256, FL.array(MTx.CGO.liquid(U10, true)), FL.array(FL.make(FLx.LPG, 5), FL.make(FLx.Naphtha,  25), FL.Kerosine.make(15), FL.make(FLx.LAGO, 30)));
        }

        // cracker outputs
        RM.CryoDistillationTower.addRecipe0(false, 64, 64, FL.array(MTx.CrackerGas.gas(U10, true)), FL.array(FL.Hydrogen.make(30), FL.Methane.make(20), FL.Ethylene.make(40), FL.Propylene.make(20), MTx.Butylene.gas(U100, false), MTx.Butadiene.gas(U100, false)));
        RM.DistillationTower.addRecipe0(false, 64, 64, FL.array(MTx.Pygas.liquid(U10, true)), FL.array(MTx.Cyclopentadiene.liquid(2*U1000, false), MTx.Isoprene.liquid(10*U1000, false), MTx.AromaticsMix.liquid(75*U1000, false), MTx.Ethylbenzene.liquid(5*U1000, false), MTx.Styrene.liquid(5*U1000, false), MTx.Naphthalene.liquid(3*U1000, false)));
        RM.Mixer.addRecipe0(false, 16, 64, FL.array(MTx.AromaticsMix.liquid(U10, true), MTx.Sulfolane.liquid(13*U10, true)), FL.array(MTx.BTXSolution.liquid(1365*U1000, false), FL.Petrol.make(35)));
        RM.Mixer.addRecipe0(false, 16, 64, FL.array(MTx.Reformate     .liquid(U10, true), MTx.Sulfolane.liquid(9*U10, true)), FL.array(MTx.BTXSolution.liquid(945*U1000, false), FL.Petrol.make(55)));
        RM.DistillationTower.addRecipe0(false, 64, 32, FL.array(MTx.BTXSolution.liquid(1050*U1000, true)), FL.array(MTx.Sulfolane.liquid(U, false), MTx.Benzene.liquid(20*U1000, false), MTx.Toluene.liquid(15*U1000, false), MTx.Xylene.liquid(15*U1000, false)));

        // distillation of creosote
        RM.DistillationTower.addRecipe0(false, 64, 32, new long[] {500}, FL.array(FL.Oil_Creosote.make(50)), FL.array(MTx.AromaticsMix.liquid(5*U1000, false), MTx.Phenol.liquid(18*U144, false), MTx.Naphthalene.liquid(12*U1000, false), MTx.Anthracene.liquid(15*U1000, false), MTx.Cyclopentadiene.liquid(U1000, false)), dustTiny.mat(MT.Asphalt, 9));
        RM.Distillery.addRecipe1(false, 16, 32, ST.tag(0), FL.array(FL.Oil_Creosote.make(50)), FL.array(MTx.Phenol.liquid(18*U144, false), MTx.Naphthalene.liquid(12*U1000, false)));
        RM.Distillery.addRecipe1(false, 16, 32, ST.tag(1), FL.array(FL.Oil_Creosote.make(50)), FL.array(MTx.Phenol.liquid(18*U144, false), MTx.Anthracene.liquid(15*U1000, false)));

        // Desulfurisation
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MTx.Naphtha .liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), FL.make(FLx.NaphthaLS, 100)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT. Kerosine.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), FL.make(FLx.JetFuel, 100)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MTx.LAGO    .liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), FL.Diesel.make(100)));
        //RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MTx.HAGO    .liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), FL.Fuel.make(100)));

        if (FL.JetFuel.exists()) {
            RM.Generifier.addRecipe0(true, 0, 1, FL.make(FLx.JetFuel, 1), FL.JetFuel.make(1), NI);
        }

        // Merox (for now, handling disulfide outputs is too much of a bother)
        // RM.Mixer.addRecipe2(true, 16, 20, dust.mat(MT.NaOH, 0), dust.mat(MTx.CoO, 0), FL.array(MT.O.gas(U1000, true), MT.Kerosine.liquid(U10, true)), FL.array(MTx.JetFuel.liquid(U1000*99, false)));

        // Polybutylene
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.MgCl2, 0), FL.array(MT.TiCl4.liquid(U1000, true), MTx.Butylene.gas(U10, true)), ZL_FS, OP.dust.mat(MT.Plastic, 1));

        // PVC
        RM.Mixer.addRecipe1(true, 16, 64, ST.tag(2), FL.array(MTx.Ethane.gas(U, true), MT.Cl.gas(4*U, true)), FL.array(MTx.VinylChloride.gas(U, false), MT.HCl.gas(6*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, ST.tag(3), FL.array(MTx.Ethane.gas(U, true), MT.HCl.gas(2*U, true), MT.O.gas(2*U, true)), FL.array(MTx.VinylChloride.gas(U, false), MT.H2O.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.FeCl3, 0), FL.array(MT.Ethylene.gas(U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.EDC.liquid(U, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(0), MTx.EDC.liquid(U, true), FL.array(MTx.VinylChloride.gas(U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.OREMATS.Galena, 0), FL.array(MTx.VinylChloride.gas(U10, false)), ZL_FS, dust.mat(MT.PVC, 1));

        // PTFE
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.CH4.gas(U, true), MT.Cl.gas(6*U, true)), FL.array(MTx.CHCl3.liquid(U, false), MT.HCl.gas(6*U, false)));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CHCl3.liquid(U, true), MT.HF.gas(4*U, true)), FL.array(MTx.CHClF2.gas(U, false), MT.HCl.gas(4*U, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 128, ST.tag(0), MTx.CHClF2.gas(2*U, true), FL.array(MTx.C2F4.gas(U, false), MT.HCl.gas(4*U, false)));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.KSO4, 0), FL.array(MTx.C2F4.gas(U10, false)), ZL_FS, dust.mat(MT.PTFE, 1));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.NaSO4, 0), FL.array(MTx.C2F4.gas(U10, false)), ZL_FS, dust.mat(MT.PTFE, 1));

        // Carbon Tetrafluoride
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CHCl3.liquid(U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.CCl4.liquid(U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CCl4.liquid(U, true), MT.HF.gas(8*U, true)), FL.array(MTx.CF4.gas(U, false), MT.HCl.gas(8*U, false)));

        // (Poly)Styrene, Sty-DVB, Ion-exchange resins
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.AlCl3         , 0), FL.array(MTx.Benzene.liquid(U, true), MT.Ethylene.gas(U, true)), FL.array(MTx.Ethylbenzene.liquid(8*U10, false), MTx.Diethylbenzene.liquid(2*U10, false)));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.OREMATS.Zeolite, 0), FL.array(MTx.Benzene.liquid(U, true), MT.Ethylene.gas(U, true)), FL.array(MTx.Ethylbenzene.liquid(6*U10, false), MTx.Diethylbenzene.liquid(4*U10, false)));
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.OREMATS.Zeolite, 0), FL.array(MTx.Benzene.liquid(U, true), MTx.Diethylbenzene.liquid(U, true)), FL.array(MTx.Ethylbenzene.liquid(2*U, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 32, dust.mat(MT.Fe2O3, 0), MTx.Ethylbenzene  .liquid(U, true), FL.array(MTx.Styrene.liquid(U, false), MT.H.gas(2*U, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 48, dust.mat(MT.Fe2O3, 0), MTx.Diethylbenzene.liquid(U, true), FL.array(MTx.DVB    .liquid(U, false), MT.H.gas(4*U, false)));

        RM.Mixer.addRecipe2(true, 16,  16, ST.tag(1), dust.mat(MT.KSO4 , 0), FL.array(MTx.Styrene.liquid(U10, true)), NF, dust.mat(MTx.Polystyrene, 1));
        RM.Mixer.addRecipe2(true, 16,  16, ST.tag(1), dust.mat(MT.NaSO4, 0), FL.array(MTx.Styrene.liquid(U10, true)), NF, dust.mat(MTx.Polystyrene, 1));
        RM.Mixer.addRecipe2(true, 16,  16, ST.tag(2), dust.mat(MT.KSO4 , 0), FL.array(MTx.Styrene.liquid(U10, true), MTx.DVB.liquid(U100, true)), NF, dust.mat(MTx.StyDVB, 1));
        RM.Mixer.addRecipe2(true, 16,  16, ST.tag(2), dust.mat(MT.NaSO4, 0), FL.array(MTx.Styrene.liquid(U10, true), MTx.DVB.liquid(U100, true)), NF, dust.mat(MTx.StyDVB, 1));

        RM.Bath.addRecipe1(true, 16, 48, dust.mat(MTx.StyDVB, 1), MT.H2SO4.liquid(7*U, true), MT.H2O.liquid(3*U, false), OPx.cationXResin.mat(MT.H, 1));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.ZnCl2, 0), dust.mat(MTx.StyDVB, 1), FL.array(MT.HCl.gas(2*U, true), MTx.Formaldehyde.gas(U, true)), FL.array(MT.H2O.liquid(3*U, false)), dust.mat(MTx.ChloromethylStyDVB, 1));
        RM.Bath.addRecipe1(true, 16, 48, dust.mat(MTx.ChloromethylStyDVB, 1), MTx.Trimethylamine.gas(U, true), NF, OPx.anionXResin.mat(MT.Cl, 1));

        // Sulfolane
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Ni, 0), FL.array(MTx.Butadiene.gas(U, true), MT.SO2.gas(3*U, true), MT.H.gas(2*U, true)), FL.array(MTx.Sulfolane.liquid(U, false)));

        /// Rubber
        // Polyisoprene
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.MgCl2, 0), FL.array(MT.TiCl4.liquid(U1000, true), MTx.Isoprene.liquid(U10, false)), ZL_FS, dust.mat(MT.Rubber, 1));
        // SBR
        RM.Mixer.addRecipe2(true, 16,  16, ST.tag(2), dust.mat(MT.KSO4 , 0), FL.array(MTx.Styrene.liquid(U20, true), MTx.Butadiene.gas(U20, true)), ZL_FS, dust.mat(MT.Rubber, 1));
        RM.Mixer.addRecipe2(true, 16,  16, ST.tag(2), dust.mat(MT.NaSO4, 0), FL.array(MTx.Styrene.liquid(U20, true), MTx.Butadiene.gas(U20, true)), ZL_FS, dust.mat(MT.Rubber, 1));

        // BPA, PF, Bakelite, Polycarbonate
        RM.Mixer.addRecipe0(true, 16,  64*3, FL.array(MT.Propylene.gas(U, true), MTx.Benzene.liquid(U, true), MTx.H3PO4.liquid(U1000, true)), FL.array(MTx.Cumene.liquid(U, false)));
        RM.Mixer.addRecipe0(true, 16,  64*3, FL.array(MTx.Cumene.liquid(U, true), MT.O.gas(2*U, true)), FL.array(MTx.Acetone.liquid(U, false)), dust.mat(MTx.Phenol, 1));
        RM.Mixer.addRecipe1(true, 16,  512, dust.mat(MTx.Phenol, 2), FL.array(MTx.Acetone.liquid(U, true), MT.H2SO4.liquid(U1000, true)), MT.H2O.liquid(3*U, false), dust.mat(MTx.BPA, 1));
        RM.Mixer.addRecipe2(true, 16,  512, dust.mat(MT.NaOH, 0), dust.mat(MTx.Phenol, 1), MTx.Formaldehyde.gas(U, true), MT.H2O.liquid(3*U, false), dust.mat(MTx.PF, 10));
        RM.Autoclave.addRecipe2(true, 16, 500, ST.tag(0), dust.mat(MTx.PF, 1), FL.Steam.make(16000), FL.DistW.make(100), chunkGt.mat(MT.Bakelite, 1));
        RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.BPA, 1), FL.array(MTx.NaOHSolution.liquid(12*U, true), MTx.Phosgene.gas(U, true)), FL.Saltwater.make(16000), dust.mat(MT.Polycarbonate, 10));

        // ECH
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.Propylene.gas(U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.AllylChloride.liquid(U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.HCl.gas(2*U, true), MTx.AllylChloride.liquid(U, true), MT.O.gas(U, true)), FL.array(MTx.Dichloropropanol.liquid(U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.HCl.gas(4*U, true), MT.Glycerol.liquid(U, true)), FL.array(MTx.Dichloropropanol.liquid(U, false), MT.H2O.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16,  64, dust.mat(MT.NaOH, 3), FL.array(MTx.Dichloropropanol.liquid(U, true), MT.H2O.liquid(3*U, true)), FL.array(MTx.ECH.liquid(U, false), MT.SaltWater.liquid(8*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MTx.Dichloropropanol.liquid(U, true), MTx.NaOHSolution.liquid(6*U, true)), FL.array(MTx.ECH.liquid(U, false), MT.SaltWater.liquid(8*U, false)));

        // Epoxy
        RM.Mixer.addRecipe2(true, 16,  64, dust.mat(MT.NaOH, 6), dust.mat(MTx.BPA, 1), FL.array(MTx.ECH.liquid(U, true), MT.H2O.liquid(U*6, true)), FL.array(MT.SaltWater.liquid(16*U, false), MTx.Epoxy.liquid(10*U, false)));
        RM.Mixer.addRecipe1(true, 16,  64, dust.mat(MTx.BPA, 1), FL.array(MTx.ECH.liquid(U, true), MTx.NaOHSolution.liquid(U*12, true)), FL.array(MT.SaltWater.liquid(16*U, false), MTx.Epoxy.liquid(10*U, false)));

        // Ketones
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MT.Pd, 0), FL.array(MTx.Acetone.liquid(20*U, true), MT.H.gas(2*U, true)), FL.array(MTx.MIBK.liquid(19*U, false), MT.H2O.liquid(3*U, false)));
        RM.Mixer.addRecipe2(true, 16, 32, dust.mat(MTx.PdCl2, 0), dust.mat(MTx.CuCl2, 0), FL.array(MTx.Butylene.gas(U, true), MT.O.gas(U, true)), MTx.Butanone.liquid(U, false), NI);

        // TNT
        RM.Mixer.addRecipe0(true, 32, 64*3, FL.array(MTx.Toluene.liquid(U, true), MT.HNO3.liquid(5*U, true), MT.H2SO4.liquid(7*U, true)), MTx.DiluteH2SO4.liquid(10*U, false), dust.mat(MTx.TNT, 3));
        RM.Boxinator.addRecipe2(true, 16, 20, ST.make(Items.paper, 1, 0), dust.mat(MTx.TNT, 1), ST.make(Blocks.tnt, 1, 0));

        // ANFO
        RM.Mixer.addRecipe1(true, 16, 64 , dust.mat(MTx.NH4NO3, 1), FL.Diesel  .make(100), NF, dust.mat(MTx.ANFO, 1));
        RM.Mixer.addRecipe1(true, 16, 64 , dust.mat(MTx.NH4NO3, 1), FL.Kerosine.make(100), NF, dust.mat(MTx.ANFO, 1));
        RM.Mixer.addRecipe1(true, 16, 64 , dust.mat(MTx.NH4NO3, 1), FL.Fuel    .make(100), NF, dust.mat(MTx.ANFO, 1));
        RM.Mixer.addRecipe1(true, 16, 64 , dust.mat(MTx.NH4NO3, 1), MTx.LAGO   .liquid(U10, true), NF, dust.mat(MTx.ANFO, 1));
        RM.Mixer.addRecipe1(true, 16, 64 , dust.mat(MTx.NH4NO3, 1), MTx.JetFuel.liquid(U10, true), NF, dust.mat(MTx.ANFO, 1));

        RM.Press.addRecipeX(true, 16, 16 , ST.array(ST.tag(1), dust     .mat(MTx.ANFO , 1), ST.make(Items.string                                    , 1, W)), IL.Dynamite       .get(1));
        RM.Press.addRecipeX(true, 16, 16 , ST.array(ST.tag(1), dust     .mat(MTx.ANFO , 1), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 1, W)), IL.Dynamite       .get(1));
        RM.Press.addRecipeX(true, 16, 144, ST.array(ST.tag(1), blockDust.mat(MTx.ANFO , 1), ST.make(Items.string                                    , 9, W)), IL.Dynamite       .get(9));
        RM.Press.addRecipeX(true, 16, 144, ST.array(ST.tag(1), blockDust.mat(MTx.ANFO , 1), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 9, W)), IL.Dynamite       .get(9));
        RM.Press.addRecipeX(true, 16, 64 , ST.array(ST.tag(2), dust     .mat(MTx.ANFO , 2), ST.make(Items.string                                    , 1, W)), IL.Dynamite_Strong.get(1));
        RM.Press.addRecipeX(true, 16, 64 , ST.array(ST.tag(2), dust     .mat(MTx.ANFO , 2), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 1, W)), IL.Dynamite_Strong.get(1));
        RM.Press.addRecipeX(true, 16, 576, ST.array(ST.tag(2), blockDust.mat(MTx.ANFO , 2), ST.make(Items.string                                    , 9, W)), IL.Dynamite_Strong.get(9));
        RM.Press.addRecipeX(true, 16, 576, ST.array(ST.tag(2), blockDust.mat(MTx.ANFO , 2), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 9, W)), IL.Dynamite_Strong.get(9));

        // Production of toluene from benzene
        RM.Mixer.addRecipe1(true, 16, 192, dust.mat(MT.OREMATS.Zeolite, 0), FL.array(MTx.Benzene.liquid(U, true), MTx.Methanol.liquid(U, true)), FL.array(MTx.Toluene.liquid(U, false), MT.H2O.liquid(3*U, false)));

        // MTBE
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT .FeCl3, 0), FL.array(MTx.Methanol.liquid(U, true), MTx.Butylene.gas(U, true)), FL.array(MTx.MTBE.liquid(U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.AlCl3, 0), FL.array(MTx.Methanol.liquid(U, true), MTx.Butylene.gas(U, true)), FL.array(MTx.MTBE.liquid(U, false)));

        // Isopropyl Alcohol
        RM.Mixer.addRecipe1(true, 16, 200, dust.mat(MT.Ni, 0), FL.array(MTx.Acetone.liquid(U, true), MT.H.gas(2*U, true)), FL.array(MTx.Isopropanol.liquid(U, false)));
        for (FluidStack water : FL.waters(3000))
            RM.Mixer.addRecipe1(true, 16, 200, dust.mat(MT.Al2O3, 0), FL.array(MT.Propylene.gas(U, true), water), FL.array(MTx.Isopropanol.liquid(U, false)));

        // Hydrodealkylation of xylene and toluene in case you don't need it
        RM.Mixer.addRecipe1(true, 16, 64, new long[]{500}, dust.mat(MT.Cr, 0), FL.array(MTx.Toluene.liquid(U, true), MT.H.gas(2*U, true)), FL.array(MTx.Benzene.liquid(U, false), MT.CH4.gas(U, false)), dust.mat(MTx.Biphenyl, 1));
        RM.Mixer.addRecipe1(true, 16, 64, new long[]{500}, dust.mat(MT.Mo, 0), FL.array(MTx.Toluene.liquid(U, true), MT.H.gas(2*U, true)), FL.array(MTx.Benzene.liquid(U, false), MT.CH4.gas(U, false)), dust.mat(MTx.Biphenyl, 1));
        RM.Mixer.addRecipe1(true, 16, 64, new long[]{500}, dust.mat(MT.Cr, 0), FL.array(MTx.Xylene .liquid(U, true), MT.H.gas(4*U, true)), FL.array(MTx.Benzene.liquid(U, false), MT.CH4.gas(2*U, false)), dust.mat(MTx.Biphenyl, 1));
        RM.Mixer.addRecipe1(true, 16, 64, new long[]{500}, dust.mat(MT.Mo, 0), FL.array(MTx.Xylene .liquid(U, true), MT.H.gas(4*U, true)), FL.array(MTx.Benzene.liquid(U, false), MT.CH4.gas(2*U, false)), dust.mat(MTx.Biphenyl, 1));

        // Vinyl Acetate e.a.
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 2048, OM.dust(MT.WO3, 48 * U), FL.array(MT.HCl.gas(8 * U, true), MTx.Na4SiO4Solution.liquid(18 * U, true), FL.mul(water, 18)), FL.array(FL.Saltwater.make(32000)), dust.mat(MTx.SilicoTungsticAcid, 96));
            RM.Mixer.addRecipe1(true, 16, 2048, OM.dust(MT.WO3, 48 * U), FL.array(MTx.ConcHCl.liquid(20 * U, true), MTx.Na4SiO4Solution.liquid(18 * U, true), FL.mul(water, 6)), FL.array(FL.Saltwater.make(32000)), dust.mat(MTx.SilicoTungsticAcid, 96));
            RM.Mixer.addRecipe1(true, 16, 2048, OM.dust(MT.WO3, 48 * U), FL.array(MTx.DiluteHCl.liquid(32 * U, true), MTx.Na4SiO4Solution.liquid(18 * U, true), FL.mul(water, 2)), FL.array(FL.Saltwater.make(32000)), dust.mat(MTx.SilicoTungsticAcid, 96));
        }
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.SilicoTungsticAcid, 0), FL.array(MT.Ethylene.gas(U, true), MT.O.gas(2*U, true)), FL.array(MTx.AceticAcid.liquid(U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Pd, 0), FL.array(MT.Ethylene.gas(U, true), MTx.AceticAcid.liquid(U, true), MT.O.gas(U, true)), FL.array(MTx.VinylAcetate.liquid(U, false), MT.H2O.liquid(3*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.NaSO4, 0), MTx.VinylAcetate.liquid(U, true), NF, dust.mat(MTx.PolyvinylAcetate, 1));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.NaOH, 0), dust.mat(MTx.PolyvinylAcetate, 1), MTx.Methanol.liquid(U, true), MTx.MethylAcetate.liquid(U, false), dust.mat(MTx.PVA, 1));
        for (FluidStack water : FL.waters(1000)) {
            RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.PolyvinylAcetate, 1), water, FL.Glue.make(1000), NI);
            // Hydrolysis of Methyl Acetate
            RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.MethylAcetate.liquid(U, true), FL.mul(water, 3), MTx.ConcHCl.liquid(U1000, true)), FL.array(MTx.AceticAcid.liquid(U, false), MTx.Methanol.liquid(U, false)));
            // Biphenyl via Gomberg-Bachmann
            RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.NaOH, 3), dust.mat(MTx.BenzenediazoniumChloride, 1), FL.array(MTx.Benzene.liquid(U, true), FL.mul(water, 3)), FL.array(MT.N.gas(2 * U, false), FL.Saltwater.make(8000)), dust.mat(MTx.Biphenyl, 1));
        }

        // Pentanol, Chloropentane
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Rh, 0), FL.array(MTx.Butylene.gas(U, true), MT.CO.gas(2*U, true), MT.H.gas(4*U, true)), FL.array(MTx.Pentanol.liquid(U, false)));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.Pentanol.liquid(U, true), MT.HCl.gas(2*U, true)), FL.array(MTx.Chloropentane.liquid(U, false), MT.H2O.liquid(3*U, false)));

        // Water-gas shift reaction
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Fe2O3, 0), FL.array(FL.Steam.make(3000*(long)STEAM_PER_WATER), MT.CO.gas(2*U, true)), FL.array(MT.H.gas(2*U, false), MT.CO2.gas(3*U, false)));

        // CO2 reforming
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Ni, 0), FL.array(MT.CH4.gas(5*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.H.gas(4*U, true), MT.CO.gas(4*U, true)));

        // Fischer-Tropsch process
        RM.Mixer.addRecipe1(true, 16, 50000, dust.mat(MT.Ru, 0), FL.array(MT.CO.gas(2*40*U, true), MT.H.gas(4*40*U, true)), FL.array(MTx.Synoil.liquid(U, false), MT.H2O.liquid(3*40*U, false)));

        // Naphthalene, Anthraquinone-, Azo-, and other dye precursors
        RM.Mixer.addRecipe1(true, 16, 256, ST.tag(3), FL.array(MTx.Naphthalene.liquid(U, true), MT.H2SO4.liquid(7*U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.DiluteH2SO4.liquid(10*U, false)), dust.mat(MTx.Nitronaphthalene, 1));
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MT.Ni, 0), dust.mat(MTx.Nitronaphthalene, 1), MT.H.gas(6*U, true), MT.H2O.liquid(6*U, false), dust.mat(MTx.Aminonaphthalene, 1));
        RM.Mixer.addRecipe1(true, 16, 256, ST.tag(2), FL.array(MTx.Naphthalene.liquid(U, true), MT.H2SO4.liquid(7*U, true)), FL.array(MT.H2O.liquid(3*U, false)), dust.mat(MTx.NaphthaleneSulfonicAcid, 1));
        RM.Bath.addRecipe2(true, 0, 256, dust.mat(MT.NaOH, 3), dust.mat(MTx.NaphthaleneSulfonicAcid, 1), MT.O.gas(U, true), NF, dust.mat(MT.NaHSO4, 7), dust.mat(MTx.Naphthol, 1));

        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CrO3, 8), MTx.Anthracene.liquid(U, true), FL.Water.make(3000), dust.mat(MTx.Anthraquinone, 1), dust.mat(MTx.Cr2O3, 5));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.Anthraquinone, 1), MT.H2SO4.liquid(14*U, true), FL.Water.make(6000), dust.mat(MTx.AnthraquinoneDisulfonicAcid, 1));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.AnthraquinoneDisulfonicAcid, 1), FL.array(MT.NH3.gas(2*U, true), MT.O.gas(2*U, true)), MT.H2SO4.liquid(14*U, false), dust.mat(MTx.Diaminoanthraquinone, 1));
        RM.Mixer.addRecipe0(true, 16, 256, FL.array(MTx.Toluene.liquid(U, true), MT.Cl.gas(2*U, true)), MT.HCl.gas(2*U, false), dust.mat(MTx.Chlorotoluene, 1));
        RM.Mixer.addRecipe0(true, 16, 256, FL.array(MTx.Benzene.liquid(U, true), MT.H2SO4.liquid(7*U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.DiluteH2SO4.liquid(10*U, false), MTx.Nitrobenzene.liquid(U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Ni, 0), FL.array(MTx.Nitrobenzene.liquid(U, true), MT.H.gas(6*U, true)), FL.array(MTx.Aniline.liquid(U, false), MT.H2O.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.Phenol, 1), FL.array(MT.H2SO4.liquid(14*U, true), MT.HNO3.liquid(10*U, true)), FL.array(MTx.DiluteH2SO4.liquid(20*U, false)), dust.mat(MTx.DNP, 1));
        RM.Mixer.addRecipe2(true, 16, 128, dust.mat(MT.Pd, 0), dust.mat(MTx.Phenol, 1), FL.array(MT.NH3.gas(U, true)), FL.array(MTx.Aniline.liquid(U, false), MT.H2O.liquid(3*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, ST.tag(3), FL.array(MTx.Aniline.liquid(U, true), MTx.HNO2.liquid(4*U, true), MT.HCl.gas(2*U, true)), FL.array(FL.Water.make(6000)), dust.mat(MTx.BenzenediazoniumChloride, 1));

        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe0(true, 16, 256, FL.array(MTx.Aniline.liquid(U, true), MTx.Formaldehyde.gas(U, true), MTx.HCN.gas(3 * U, true), water), FL.array(MT.NH3.gas(U, false)), dust.mat(MTx.NPhenylGlycine, 1));
            RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.Aminonaphthalene, 2), FL.array(MT.H2SO4.liquid(7 * U, true), FL.mul(water, 3)), FL.array(MTx.NH4SO4Solution.liquid(10 * U, false)), dust.mat(MTx.Naphthol, 2));
            RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.Aminonaphthalene, 2), FL.array(MTx.DiluteH2SO4.liquid(10 * U, true), FL.mul(water, 2)), FL.array(MTx.NH4SO4Solution.liquid(10 * U, false)), dust.mat(MTx.Naphthol, 2));
        }

        // H2O2 with Anthraquinone
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.Anthraquinone, 0), FL.array(MT.H.gas(2*U, true), MT.O.gas(2*U, true)), FL.array(MT.H2O2.liquid(4*U, false)));

        // Synthetic dyes
        /// Bleach (white dye)
        Fluid chemicalWhiteDye = DYE_FLUIDS_CHEMICAL[DYE_INDEX_White].getFluid();
        for (FluidStack water : FL.waters(1)) {
            RM.Mixer.addRecipe2(true, 16, 64, ST.tag(3), dust.mat(MT.NaOH, 6), FL.array(MT.Cl.gas(2 * U, true), FL.mul(water, 12000)), FL.make(chemicalWhiteDye, 18000), dust.mat(MT.NaCl, 2));
            RM.Mixer.addRecipe1(true, 16, 64, ST.tag(3), FL.array(MT.Cl.gas(2 * U, true), MTx.NaOHSolution.liquid(12 * U, true), FL.mul(water, 6000)), FL.make(chemicalWhiteDye, 18000), dust.mat(MT.NaCl, 2));
            RM.Mixer.addRecipe0(true, 16, 8, FL.array(MT.H2O2.liquid(9 * U1000, true), FL.mul(water, 9)), FL.make(chemicalWhiteDye, 18), NF);
        }
        /// Anthraquinone Dyes
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.Chlorotoluene, 2), dust.mat(MTx.Diaminoanthraquinone, 1), NF, MT.HCl.gas(4*U, false), dust.mat(MTx.QuinizarineGreen, 3));
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.AnthraquinoneDisulfonicAcid, 1), dust.mat(MT.NaOH, 12), MT.O.gas(2*U, true), MTx.Na2SO4Solution.liquid(20*U, false), dust.mat(MTx.AlizarinRed, 5));
        /// Azo Dyes
        // Solvent Yellow 1
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 1), FL.array(MTx.Aniline.liquid(U, true), MTx.NaOHSolution.liquid(6*U, true)), FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 5));
        // Solvent Yellow 7
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 1), dust.mat(MTx.Phenol, 1), MTx.NaOHSolution.liquid(6*U, true), FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 5));
        // Organol Brown
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 1), dust.mat(MTx.Naphthol, 1), MTx.NaOHSolution.liquid(6*U, true), FL.Saltwater.make(8000), dust.mat(MTx.OrganolBrown, 5));

        for (FluidStack water : FL.waters(3000)) {
            // Solvent Yellow 1
            RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 1), dust.mat(MT.NaOH, 3), FL.array(MTx.Aniline.liquid(U, true), water), FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 5));
            // Solvent Yellow 7
            RM.Mixer.addRecipeX(true, 16, 256, ST.array(dust.mat(MTx.BenzenediazoniumChloride, 1), dust.mat(MT.NaOH, 3), dust.mat(MTx.Phenol, 1)), water, FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 5));
            // Organol Brown
            RM.Mixer.addRecipeX(true, 16, 256, ST.array(dust.mat(MTx.BenzenediazoniumChloride, 1), dust.mat(MT.NaOH, 3), dust.mat(MTx.Naphthol, 1)), water, FL.Saltwater.make(8000), dust.mat(MTx.OrganolBrown, 5));
            /// Sulfur Black
            RM.Mixer.addRecipe2(true, 16, 512, dust.mat(MTx.DNP, 4), dust.mat(MT.Na2S, 24), FL.array(FL.mul(water, 8)), FL.array(MTx.NaOHSolution.liquid(48 * U, false), MT.NO2.gas(6 * U, false)), dust.mat(MTx.SulfurBlack, 12));
        }

        /// Indigo Dye
        RM.Mixer.addRecipeX(true, 16, 512, ST.array(dust.mat(MT.NaOH, 0), dust.mat(MT.KOH, 0), dust.mat(MTx.NPhenylGlycine, 2)), MT.O.gas(2*U, true), FL.Water.make(12000), dust.mat(MT.Indigo, 1));

        // DNQ (precursor of photoresist)
        RM.Mixer.addRecipe2(true, 16, 256, ST.tag(3), dust.mat(MTx.Naphthol, 1), FL.array(MT.H2SO4.liquid(7*U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.DiluteH2SO4.liquid(10*U, false)), dust.mat(MTx.Nitronaphthol, 1));
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MT.Ni, 0), dust.mat(MTx.Nitronaphthol, 1), MT.H.gas(6*U, true), MT.H2O.liquid(6*U, false), dust.mat(MTx.Aminonaphthol, 1));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.Aminonaphthol, 1), MTx.HNO2.liquid(4*U, true), FL.Water.make(6000), dust.mat(MTx.DNQ, 1));

        // Hydro-cracking
        // Diesel: 10-20 C (avg C12H26) - 3 Diesel -> 2 Kerosine + 2 Naphtha
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(102), MTx.LAGO  .liquid(U10, true)), FL.array(FL.make(FLx.JetFuel, 70), FL.make(FLx.NaphthaLS, 50), MT.H2S.gas(U1000*3, false)));
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(100), MT .Diesel.liquid(U10, true)), FL.array(FL.make(FLx.JetFuel, 70), FL.make(FLx.NaphthaLS, 50)));

        // Kerosine: 9-12 C (avg C10H22) - 3 Kerosine -> 2 Naphtha + 1 Butane * 1.5
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(102), MT.Kerosine.liquid(U10, true)), FL.array(FL.make(FLx.NaphthaLS, 75), FL.make(FLx.LPG, 60), MT.H2S.gas(U1000*3, false)));
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(100), MTx.JetFuel.liquid(U10, true)), FL.array(FL.make(FLx.NaphthaLS, 75), FL.make(FLx.LPG, 60)));

        // Naphtha: 5-12 C (avg C8H18) - 2 Naphtha -> 2 Butane + 2 Propane + 1 Ethane * 2
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(102), MTx.Naphtha         .liquid(U10, true)), FL.array(FL.make(FLx.LPG, 75), MTx.Ethane.gas(3*U40, false), MT.CH4.gas(11*U100, false), MT.H2S.gas(U1000*3, false)));
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(100), MTx.NaphthaLowSulfur.liquid(U10, true)), FL.array(FL.make(FLx.LPG, 75), MTx.Ethane.gas(3*U40, false), MT.CH4.gas(11*U100, false)));

        // Butane: 2 Butane -> 2 Ethane + 1 Propane + 1 Methane * 4
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(100), MT.Butane.gas(U10, true)), FL.array(MT.Propane.gas(U20, false), MTx.Ethane.gas(U10, false), MT.CH4.gas(U20, false)));
        // Propane: 1 Propane -> 1 Ethane + 1 Methane * 10
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(100), MT.Propane.gas(U10, true)), FL.array(MTx.Ethane.gas(U10, false), MT.CH4.gas(U10, false)));
        // LPG: in-between butane and propane
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(200), MTx.LPG.liquid(U10, true)), FL.array(MT.Propane.gas(U20, false), MTx.Ethane.gas(U5, false), MT.CH4.gas(3*U20, false)));

        // Ethane: 1 Ethane -> 2 Methane * 14
        RMx.HydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(200), MTx.Ethane.gas(U10, true)), FL.array(MT.CH4.gas(200*U1000, false)));

        // Oil residue fractions
        if (vacuumDistillation) {
            RMx.HydroCracking.addRecipe0(false, 16, 64, FL.array(FL.Hydrogen.make(102), MTx.LVGO.liquid(U10, true)), FL.array(FL.Diesel.make(50), FL.make(FLx.JetFuel, 30), FL.make(FLx.NaphthaLS, 20), MT.H2S.gas(3*U1000, false)));
            RMx.HydroCracking.addRecipe0(false, 16, 64, FL.array(FL.Hydrogen.make(102), MTx.HVGO.liquid(U10, true)), FL.array(FL.Fuel.make(30), FL.Diesel.make(50), FL.make(FLx.JetFuel, 25), MT.H2S.gas(3*U1000, false)));
            RMx.HydroCracking.addRecipe0(false, 16, 64, FL.array(FL.Hydrogen.make(100), MTx.DAO .liquid(U10, true)), FL.array(FL.Fuel.make(50), FL.Diesel.make(40), FL.make(FLx.JetFuel, 25)));
        } else {
            RMx.HydroCracking.addRecipe0(false, 16, 64, FL.array(FL.Hydrogen.make(102), MT .Fuel.liquid(U10, true)), FL.array(FL.Diesel.make(50), FL.make(FLx.JetFuel, 40), FL.make(FLx.NaphthaLS, 25), MT.H2S.gas(3*U1000, false)));
        }

        // LPG in lighters
        RM.Canner.addRecipe1(true, 16, 16, IL.Tool_Lighter_Invar_Empty.get(1), FL.make(FLx.LPG, 1), NF, IL.Tool_Lighter_Invar_Full.get(1));
        RM.Canner.addRecipe1(true, 16, 16, IL.Tool_Lighter_Platinum_Empty.get(1), FL.make(FLx.LPG, 1), NF, IL.Tool_Lighter_Platinum_Full.get(1));
        RM.Canner.addRecipe1(true, 16, 16, IL.Tool_Lighter_Plastic_Empty.get(1), FL.make(FLx.LPG, 1), NF, IL.Tool_Lighter_Plastic_Full.get(1));

        // extruder recipes for cans
        final long EUt = 16, durationPerUnit = 64*6;
        for (OreDictPrefix tPrefix : OreDictPrefix.VALUES) if (tPrefix != null && tPrefix.containsAny(TD.Prefix.EXTRUDER_FODDER, TD.Prefix.INGOT_BASED, TD.Prefix.GEM_BASED, TD.Prefix.DUST_BASED) && U % tPrefix.mAmount == 0) {
            ItemStack pvcStack = tPrefix.mat(MT.PVC, U / tPrefix.mAmount);
            if (pvcStack != null && pvcStack.stackSize * 6 <= pvcStack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, pvcStack), IL.Shape_Extruder_Cell.get(0), ST.amount(1, pvcCan));
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, pvcStack), IL.Shape_SimpleEx_Cell.get(0), ST.amount(1, pvcCan));
            }
            ItemStack ptfeStack = tPrefix.mat(MT.PTFE, U / tPrefix.mAmount);
            if (ptfeStack != null && ptfeStack.stackSize * 6 <= ptfeStack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, ptfeStack), IL.Shape_Extruder_Cell.get(0), ST.amount(1, ptfeCan));
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, ptfeStack), IL.Shape_SimpleEx_Cell.get(0), ST.amount(1, ptfeCan));
            }
        }

    }

    private static final OreDictPrefix[] CRACKER_PLATES = { plate, plateDouble, plateTriple, plateQuadruple, plateQuintuple };

    private void addMTEs() {
        // pipes
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PVCTubes.get(), 26142, 200, true, true, false, true, false, false, false, true, MTEx.gt6xMTEReg, MTEx.PlasticBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MT.PVC.mMeltingPoint, MT.PVC);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PTFETubes.get(), 26142, 1000, true, true, false, true, false, false, false, true, MTEx.gt6xMTEReg, MTEx.PlasticBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MT.PTFE.mMeltingPoint, MT.PTFE);

        // PVC can
        pvcCan = MTEx.gt6xMTEReg.add("PVC Canister", "Fluid Containers", MTEx.IDs.PVCCan.get(), 32719, MultiTileEntityBarrelPlasticAdvanced.class, 0, 16, MTEx.PlasticBlock, gregapi.util.UT.NBT.make(NBT_MATERIAL, MT.PVC, gregapi.data.CS.NBT_HARDNESS, 1.0F, gregapi.data.CS.NBT_RESISTANCE, 3.0F, NBT_TANK_CAPACITY, 64000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true, NBT_ACIDPROOF, true));
        OM.data(pvcCan, new OreDictItemData(MT.PVC, U*6));

        // PTFE can
        ptfeCan = MTEx.gt6xMTEReg.add("PTFE Canister", "Fluid Containers", MTEx.IDs.PTFECan.get(), 32719, MultiTileEntityBarrelPlasticAdvanced.class, 0, 16, MTEx.PlasticBlock, gregapi.util.UT.NBT.make(NBT_MATERIAL, MT.PTFE, gregapi.data.CS.NBT_HARDNESS, 1.0F, gregapi.data.CS.NBT_RESISTANCE, 3.0F, NBT_TANK_CAPACITY, 256000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true, NBT_ACIDPROOF, true));
        OM.data(ptfeCan, new OreDictItemData(MT.PTFE, U*6));

        // Hydro cracker
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial mat;

        for (int tier = 1; tier < 5; tier++) {
            mat = MT.DATA.Heat_T[tier]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.Hydrocracker[tier].get(), 20001, aClass, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, MTEx.HARDNESS_HEAT[tier], NBT_RESISTANCE,MTEx.HARDNESS_HEAT[tier], NBT_INPUT, V[tier], NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.HydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(mat), 'C', CRACKER_PLATES[tier].dat(ANY.Cu), 'I', CRACKER_PLATES[tier].dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        }
    }

    private void overrideMiscRecipes() {
        // burning crude oil
        for (FL oil : EXTRA_HEAVY_OILS) if (oil.exists())
            FMx.burn(16, 24, oil.make(1), 1, 1);
        for (FL oil : HEAVY_OILS) if (oil.exists())
            FMx.burn(16, 22, oil.make(1), 1, 1);
        for (FL oil : MEDIUM_OILS) if (oil.exists())
            FMx.burn(16, 20, oil.make(1), 1, 1);
        for (FL oil : LIGHT_OILS) if (oil.exists())
            FMx.burn(16, 18, oil.make(1), 1, 1);
        FMx.burn(16, 24, FL.Oil_Soulsand.make(1), 1, 1);

        // nitroglycerin
        RM.Mixer.addRecipe0(true, 16, 800, FL.array(MT.Glycerol.liquid(14*U, true), MT.HNO3.liquid(15*U, true), MT.H2SO4.liquid(21*U, true)), MT.Glyceryl.liquid(20*U, false), MTx.DiluteH2SO4.liquid(U*30, false));
    }
}
