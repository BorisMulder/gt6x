package org.altadoon.gt6x.features.metallurgy;

import gregapi.code.ArrayListNoNulls;
import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.oredict.configurations.OreDictConfigurationComponent;
import gregapi.recipes.Recipe;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;

import java.util.*;

import static gregapi.data.CS.*;

public class EAFSmeltingRecipe {
    /** map of all recipes that have the key as ingredient */
    public static Map<OreDictMaterial, ArrayListNoNulls<EAFSmeltingRecipe>> SmeltsInto = new HashMap<>();
    /** map of all recipes that have the key as result, except for byproducts such as slag */
    public static Map<OreDictMaterial, ArrayListNoNulls<EAFSmeltingRecipe>> SmeltsFrom = new HashMap<>();

    public static Recipe.RecipeMap FakeRecipes = new Recipe.RecipeMap(null, "gt6x.recipe.eafsmelting", "Electric Arc Furnace", null, 0, 1, RES_PATH_GUI+"machines/Alloying", 12,12, 1, 0, 0, 0, 0, 1, "Temperature: ", 1, " K", false, true, false, true, false, true, true);

    public long smeltingTemperature;

    public boolean exothermic;
    public static long EXOTHERMIC_ENERGY_GAIN = 512; // GU/t

    public IOreDictConfigurationComponent ingredients;
    public IOreDictConfigurationComponent results;


    public EAFSmeltingRecipe(long commonDivider, boolean exothermic, OreDictMaterialStack[] ingredients, long smeltingTemperature, OreDictMaterialStack result, OreDictMaterialStack... byProducts) {
        if (ingredients.length == 0 || result == null)
            throw new IllegalArgumentException("EAF Recipe must have at least one input and output");

        if (commonDivider == 0) {
            long amount = 0;
            for (OreDictMaterialStack stack : ingredients) {
                amount += stack.mAmount;
            }
            commonDivider = amount / U;
            if (amount % U != 0) ERR.println("WARNING: EAF Recipe for '"+result.mMaterial.mNameInternal+"' has an Amount of " + amount + " Components and automatically generates a divider, that is leaving a tiny rest after the division, breaking some Material Amounts. Manual setting of Variables is required.");
        }

        ArrayList<OreDictMaterialStack> allResults = new ArrayList<>(Arrays.asList(byProducts));
        allResults.add(result);
        this.results = new OreDictConfigurationComponent(commonDivider, allResults.toArray(new OreDictMaterialStack[]{}));
        this.ingredients = new OreDictConfigurationComponent(commonDivider, ingredients);
        this.smeltingTemperature = smeltingTemperature;
        this.exothermic = exothermic;

        for(OreDictMaterialStack stack : this.ingredients.getComponents()) {
            if (SmeltsInto.containsKey(stack.mMaterial)) {
                SmeltsInto.get(stack.mMaterial).add(this);
            } else {
                SmeltsInto.put(stack.mMaterial, new ArrayListNoNulls<>(false, this));
            }
        }

        if (SmeltsFrom.containsKey(result.mMaterial)) {
            SmeltsInto.get(result.mMaterial).add(this);
        } else {
            SmeltsInto.put(result.mMaterial, new ArrayListNoNulls<>(false, this));
        }

        addFakeRecipe();
    }

    private void addFakeRecipe() {
        // input
        boolean doAdd = true, addSpecial = false;
        ArrayListNoNulls<ItemStack> dustInputs = new ArrayListNoNulls<>(), ingotInputs = new ArrayListNoNulls<>(), specialInputs = new ArrayListNoNulls<>();
        for (OreDictMaterialStack stack : ingredients.getUndividedComponents()) {
            boolean addedSpecial = false;
            if (stack.mMaterial.mHidden) {doAdd = false; break;}
            if (stack.mMaterial == MT.O) {
                dustInputs .add(FL.Oxygen.display(UT.Code.units(stack.mAmount, U, 1000, true)));
                ingotInputs.add(FL.Oxygen.display(UT.Code.units(stack.mAmount, U, 1000, true)));
                continue;
            }
            if (stack.mMaterial == MT.OREMATS.Magnetite          ) {addedSpecial = specialInputs.add(ST.make(BlocksGT.Sands, UT.Code.divup(stack.mAmount, U*9), 0, "You probably want to craft it into Dust"));} else
            if (stack.mMaterial == MT.OREMATS.BasalticMineralSand) {addedSpecial = specialInputs.add(ST.make(BlocksGT.Sands, UT.Code.divup(stack.mAmount, U*9), 1, "You probably want to craft it into Dust"));} else
            if (stack.mMaterial == MT.OREMATS.GraniticMineralSand) {addedSpecial = specialInputs.add(ST.make(BlocksGT.Sands, UT.Code.divup(stack.mAmount, U*9), 2, "You probably want to craft it into Dust"));} else
            if (stack.mMaterial == MT.C                          ) {addedSpecial = specialInputs.add(OM.dustOrIngot(MT.Coal            , stack.mAmount * 2));}
            if (stack.mMaterial == MT.CaCO3                      ) {addedSpecial = specialInputs.add(OM.dustOrIngot(MT.STONES.Limestone, stack.mAmount * 2));}

            ItemStack dust = OM.dustOrIngot(stack.mMaterial, stack.mAmount);
            if (!dustInputs.add(dust)) {doAdd = false; break;}
            ingotInputs.add(OM.ingotOrDust(stack.mMaterial, stack.mAmount));
            if (addedSpecial) addSpecial = true; else specialInputs.add(dust);
        }
        ArrayListNoNulls<ItemStack> outputs = new ArrayListNoNulls<>();
        for (OreDictMaterialStack stack : results.getUndividedComponents()) {
            if (smeltingTemperature >= stack.mMaterial.mBoilingPoint) {
                outputs.add(FL.display(stack.mMaterial.gas(stack.mAmount, false), UT.Code.units(stack.mAmount, U, 1000, true), false, false));
            } else {
                outputs.add(OM.ingotOrDust(stack.mMaterial, stack.mAmount));
            }
        }

        if (doAdd) {
            long energy_output = exothermic ? -EXOTHERMIC_ENERGY_GAIN : 0;
            long duration = exothermic ? 1 : 0;
            FakeRecipes.addFakeRecipe(F, dustInputs  .toArray(ZL_IS), outputs.toArray(ZL_IS), null, null, null, null, duration, energy_output, smeltingTemperature);
            FakeRecipes.addFakeRecipe(F, ingotInputs .toArray(ZL_IS), outputs.toArray(ZL_IS), null, null, null, null, duration, energy_output, smeltingTemperature);
            if (addSpecial)
                FakeRecipes.addFakeRecipe(F, specialInputs.toArray(ZL_IS), outputs.toArray(ZL_IS), null, null, null, null, duration, energy_output, smeltingTemperature);
        }
    }
}
