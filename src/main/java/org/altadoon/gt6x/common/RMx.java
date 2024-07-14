package org.altadoon.gt6x.common;

import gregapi.data.FL;
import gregapi.data.FM;
import gregapi.data.MT;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.recipes.maps.RecipeMapFuel;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import static gregapi.data.CS.*;

/** Common recipe maps */
public class RMx {
    public static RecipeMap BlastFurnace = null;
    public static RecipeMap BOF = null;
    public static RecipeMap CowperStove = null;
    public static RecipeMap DirectReduction = null;
    public static RecipeMap HydroCracking = null;
    public static RecipeMap IonBombardment = null;
    public static RecipeMap Ionizer = null;
    public static RecipeMap Photolithography = null;
    public static RecipeMap Sintering = null;
    public static RecipeMap Soldering = null;
    public static RecipeMap Thermolysis = null;
    public static RecipeMap VacuumChamber = null;

    public static final FluidStack[] CuttingFluids = { FL.Water.make(1000), FL.SpDew.make(1000), FL.DistW.make(1000), FL.Lubricant.make(1000), FL.LubRoCant.make(1000) };
    public static final long[] CuttingMultiplier = {4, 4, 3, 1, 1};

    public static class FMx {
        public static RecipeMapFuel Diesel = null;
        public static RecipeMapFuel Petrol = null;
        public static RecipeMapFuel Nitro = null;

        public static HashMap<Integer, Long> diesel_fuel_temperatures = new HashMap<>();

        public static boolean engineOverhaulEnabled = false;

        private static FluidStack[] fuelOutputs(long co2, long water, FluidStack... misc) {
            ArrayList<FluidStack> outputs = new ArrayList<>();
            if (co2 > 0) outputs.add(FL.CarbonDioxide.make(co2));
            if (water > 0) outputs.add(FL.Water.make(water));
            outputs.addAll(Arrays.asList(misc));
            return outputs.toArray(ZL_FS);
        }

        private static FluidStack[] hotFuelOutputs(long co2, long steam) {
            ArrayList<FluidStack> outputs = new ArrayList<>();
            if (co2 > 0) outputs.add(FL.Hot_Carbon_Dioxide.make(co2));
            if (steam > 0) outputs.add(FL.Steam.make(steam));
            return outputs.toArray(ZL_FS);
        }

        public static void burn(long EUt, long duration, FluidStack input, long co2, long water) {
            FM.Burn.addRecipe0(true, -EUt, duration, input, fuelOutputs(co2, water));
        }

        private static void engine(RecipeMap map, long EUt, long duration, FluidStack input, long co2, long water, FluidStack... misc) {
            FluidStack[] outputs = fuelOutputs(co2, water, misc);
            FluidStack[] firstTwoOutputs = Arrays.copyOfRange(outputs, 0, 2);
            FM.Burn.addRecipe0(true, -EUt, duration, input, firstTwoOutputs);

            if (!engineOverhaulEnabled && (map == FMx.Petrol || map == FMx.Diesel || map == FMx.Nitro)) {
                FM.Engine.addRecipe0(true, -EUt, duration, input, firstTwoOutputs);
            } else {
                map.addRecipe0(true, -EUt, duration, input, outputs);
            }
        }

        public static void diesel(long EUt, long duration, FluidStack input, long co2, long water, long operationTemp) {
            engine(Diesel, EUt, duration, input, co2, water);
            diesel_fuel_temperatures.put(input.getFluidID(), operationTemp);
        }

        public static void petrol(long EUt, long duration, FluidStack input, long co2, long water) {
            engine(Petrol, EUt, duration, input, co2, water);
        }

        public static void gas(long EUt, long duration, FluidStack input, long co2, long steam) {
            burn(EUt, duration, input, co2, (long) Math.ceil((double)steam / STEAM_PER_WATER));
            FM.Gas.addRecipe0(true, -EUt, duration, input, hotFuelOutputs(co2, steam));
        }

        public static void nitro(long EUt, long duration, FluidStack input, long co2, long water, long no) {
            engine(Nitro, EUt, duration, input, co2, water, MT.NO.gas(no*U1000, false));
        }
    }

    public static void init() {
        HydroCracking    = new RecipeMap    (null, "gt6x.recipe.hydrocracking"   , "Hydrocracking"           , null, 0, 1, RES_PATH_GUI+"machines/HydroCracking"   , 1, 3, 0, 2, 9, 1, 2, 1, "", 1, "", false, true, true, true, true, false, true, true);
        Sintering        = new RecipeMap    (null, "gt6x.recipe.sintering"       , "Sintering"               , null, 0, 1, RES_PATH_GUI+"machines/Sintering"       , 6, 1, 1, 0, 0, 0, 1, 1, "", 1, "", false, true, true, true, true, false, true, true);
        Thermolysis      = new RecipeMap    (null, "gt6x.recipe.thermolysis"     , "Thermal Decomposition"   , null, 0, 1, RES_PATH_GUI+"machines/thermolysis"     , 3, 6, 0, 3, 6, 0, 1, 1, "", 1, "", false, true, true, true, true, false, true, true);
        Ionizer          = new RecipeMap    (null, "gt6x.recipe.ionizer"         , "Ionization"              , null, 0, 1, RES_PATH_GUI+"machines/Ionizer"         , 0, 0, 0, 1, 1, 1, 1, 1, "", 1, "", false, true, true, true, true, false, true, true);
        Photolithography = new RecipeMap    (null, "gt6x.recipe.photolithography", "Photolithography"        , null, 0, 1, RES_PATH_GUI+"machines/Photolithography", 3, 1, 2, 3, 3, 1, 2, 1, "", 1, "", false, true, true, true, true, false, true, true);
        IonBombardment   = new RecipeMap    (null, "gt6x.recipe.ionbombardment"  , "Ion Acceleration"        , null, 0, 1, RES_PATH_GUI+"machines/IonBombardment"  , 3, 1, 1, 1, 2, 1, 2, 1, "", 1, "", false, true, true, true, true, false, true, true);
        VacuumChamber    = new RecipeMap    (null, "gt6x.recipe.vacuumdeposition", "Vacuum Chamber"          , null, 0, 1, RES_PATH_GUI+"machines/VacuumDeposition", 3, 1, 1, 6, 2, 0, 2, 1, "", 1, "", false, true, true, true, true, false, true, true);
        Soldering        = new RecipeMap    (null, "gt6x.recipe.soldering"       , "Soldering Machine"       , null, 0, 1, RES_PATH_GUI+"machines/Soldering"       , 9, 1, 1, 1, 0, 1, 2, 1, "", 1, "", false, true, true, true, true, false, true, true);
        BlastFurnace     = new RecipeMap    (null, "gt6x.recipe.blastfurnace"    , "Blast Furnace"           , null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace"    , 6, 3, 1, 3, 3, 0, 1, 1, "", 1, "", false, true, true, true, true, false, true, true);
        CowperStove      = new RecipeMap    (null, "gt6x.recipe.cowperstove"     , "Hot Blast Preheating"    , null, 0, 1, RES_PATH_GUI+"machines/Default"         , 0, 0, 0, 1, 1, 1, 1, 1, "", 1, "", false, true, true, true, true, false, true, true);
        BOF              = new RecipeMap    (null, "gt6x.recipe.bof"             , "Basic Oxygen Steelmaking", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace"    , 6, 3, 1, 3, 3, 1, 2, 1, "", 1, "", false, true, true, true, true, false, true, true);
        DirectReduction  = new RecipeMap    (null, "gt6x.recipe.directreduction" , "Direct Reduction"        , null, 0, 1, RES_PATH_GUI+"machines/DirectReduction" , 6, 3, 1, 3, 3, 1, 1, 1, "", 1, "", false, true, true, true, true, false, true, true);

        FMx.Diesel       = new RecipeMapFuel(null, "gt6x.recipe.fuels.diesel"    , "Diesel Engine Fuels"       , null, 0, 1, RES_PATH_GUI+"machines/Default"         , 1, 2, 0, 1, 3, 0, 1, 1, "", 1, "", true, true, true, false, true, false, false);
        FMx.Petrol       = new RecipeMapFuel(null, "gt6x.recipe.fuels.petrol"    , "Petrol Engine Fuels"       , null, 0, 1, RES_PATH_GUI+"machines/Default"         , 1, 2, 0, 1, 3, 0, 1, 1, "", 1, "", true, true, true, false, true, false, false);
        FMx.Nitro        = new RecipeMapFuel(null, "gt6x.recipe.fuels.nitro"     , "Nitro Fuels"               , null, 0, 1, RES_PATH_GUI+"machines/Default"         , 1, 2, 0, 1, 3, 0, 1, 1, "", 1, "", true, true, true, false, true, false, false);
    }
}
