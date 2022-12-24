package org.altadoon.gt6x.common;
import gregapi.recipes.Recipe;


import static gregapi.data.CS.RES_PATH_GUI;


public class RMx {
    public static void touch() {}
    public static Recipe.RecipeMap thermolysis = null;
    public static void addThermolysisRecipies(){
        thermolysis = new Recipe.RecipeMap(null, "gt6x.recipe.thermolysis","Thermolysis",null,0,1,RES_PATH_GUI+"machines/thermolysis",2,6,0,2,6,0,1,1,"",1,"", true, true, true, true, false, true, true);
    }
}
