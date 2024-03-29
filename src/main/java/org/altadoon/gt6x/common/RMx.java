package org.altadoon.gt6x.common;

import gregapi.recipes.Recipe.RecipeMap;

import static gregapi.data.CS.RES_PATH_GUI;

/** Common recipe maps */
public class RMx {
    public static RecipeMap sintering = null;
    public static RecipeMap Thermolysis = null;

    public static void init() {
        sintering = new RecipeMap(null, "gt6x.recipe.sintering", "Sintering", null, 0, 1, RES_PATH_GUI+"machines/Sintering", 6, 1, 1, 0, 0, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
        Thermolysis = new RecipeMap(null, "gt6x.recipe.thermolysis", "Thermal Decomposition", null, 0, 1, RES_PATH_GUI+"machines/thermolysis", 2, 6, 0, 2, 6, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
    }
}
