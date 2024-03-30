package org.altadoon.gt6x.common;

import gregapi.data.FL;
import gregapi.recipes.Recipe.RecipeMap;
import net.minecraftforge.fluids.FluidStack;

import static gregapi.data.CS.RES_PATH_GUI;

/** Common recipe maps */
public class RMx {
    public static RecipeMap BasicOxygen = null;
    public static RecipeMap BlastFurnace = null;
    public static RecipeMap CowperStove = null;
    public static RecipeMap DirectReduction = null;
    public static RecipeMap IonBombardment = null;
    public static RecipeMap Ionizer = null;
    public static RecipeMap Photolithography = null;
    public static RecipeMap Sintering = null;
    public static RecipeMap Soldering = null;
    public static RecipeMap Thermolysis = null;
    public static RecipeMap VacuumDeposition = null;

    public static final FluidStack[] CuttingFluids = { FL.Water.make(1000), FL.SpDew.make(1000), FL.DistW.make(1000), FL.Lubricant.make(1000), FL.LubRoCant.make(1000) };
    public static final long[] CuttingMultiplier = {4, 4, 3, 1, 1};

    public static void init() {
        Sintering        = new RecipeMap(null, "gt6x.recipe.sintering"       , "Sintering"               , null, 0, 1, RES_PATH_GUI+"machines/Sintering"       , 6, 1, 1, 0, 0, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        Thermolysis      = new RecipeMap(null, "gt6x.recipe.thermolysis"     , "Thermal Decomposition"   , null, 0, 1, RES_PATH_GUI+"machines/thermolysis"     , 3, 6, 0, 3, 6, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        Ionizer          = new RecipeMap(null, "gt6x.recipe.ionizer"         , "Ionization"              , null, 0, 1, RES_PATH_GUI+"machines/Ionizer"         , 0, 0, 0, 1, 1, 1, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        Photolithography = new RecipeMap(null, "gt6x.recipe.photolithography", "Photolithography"        , null, 0, 1, RES_PATH_GUI+"machines/Photolithography", 3, 1, 2, 3, 3, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        IonBombardment   = new RecipeMap(null, "gt6x.recipe.ionbombardment"  , "Ion Acceleration"        , null, 0, 1, RES_PATH_GUI+"machines/IonBombardment"  , 2, 1, 1, 1, 2, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        VacuumDeposition = new RecipeMap(null, "gt6x.recipe.vacuumdeposition", "Vacuum Deposition"       , null, 0, 1, RES_PATH_GUI+"machines/VacuumDeposition", 3, 1, 1, 3, 2, 0, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        Soldering        = new RecipeMap(null, "gt6x.recipe.soldering"       , "Soldering Machine"       , null, 0, 1, RES_PATH_GUI+"machines/Soldering"       , 9, 1, 1, 1, 0, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        BlastFurnace     = new RecipeMap(null, "gt6x.recipe.blastfurnace"    , "Blast Furnace"           , null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace"    , 6, 3, 1, 3, 3, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        CowperStove      = new RecipeMap(null, "gt6x.recipe.cowperstove"     , "Hot Blast Preheating"    , null, 0, 1, RES_PATH_GUI+"machines/Default"         , 0, 0, 0, 1, 1, 1, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        BasicOxygen      = new RecipeMap(null, "gt6x.recipe.bop"             , "Basic Oxygen Steelmaking", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace"    , 6, 3, 1, 3, 3, 1, 2, 1, "", 1, "", true, true, true, true, false, true, true);
        DirectReduction  = new RecipeMap(null, "gt6x.recipe.directreduction" , "Direct Reduction"        , null, 0, 1, RES_PATH_GUI+"machines/DirectReduction" , 6, 3, 1, 3, 3, 1, 1, 1, "", 1, "", true, true, true, true, false, true, true);
    }
}
