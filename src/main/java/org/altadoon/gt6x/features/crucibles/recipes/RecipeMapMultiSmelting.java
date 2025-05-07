package org.altadoon.gt6x.features.crucibles.recipes;

import gregapi.code.ArrayListNoNulls;
import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.recipes.Recipe.RecipeMap;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

import static gregapi.data.CS.*;

public class RecipeMapMultiSmelting {
	public final RecipeMap fakeRecipes;

	public Map<OreDictMaterial, ArrayListNoNulls<MultiSmeltingRecipe>> SmeltsInto = new HashMap<>();
	/** map of all recipes that have the key as result, except for byproducts such as slag */
	public Map<OreDictMaterial, ArrayListNoNulls<MultiSmeltingRecipe>> SmeltsFrom = new HashMap<>();

	public final long ticksPerFuelUnit, exothermicEnergyPerTick;

	public RecipeMapMultiSmelting(String nameInternal, String nameLocal, long ticksPerFuelUnit, long exothermicEnergyPerTick) {
		this.ticksPerFuelUnit = ticksPerFuelUnit;
		this.exothermicEnergyPerTick = exothermicEnergyPerTick;
		this.fakeRecipes = new RecipeMap(null, nameInternal, nameLocal, null, 0, 1, RES_PATH_GUI+"machines/Multismelting", 9,9, 1, 3, 3, 0, 0, 1, "Temperature: ", 1, " K", false, false, true, false, true, false, true, true);
	}

	public void addRecipe(long commonDivider, OreDictMaterialStack[] ingredients, long smeltingTemperature, OreDictMaterialStack result, OreDictMaterialStack... byProducts) {
		addRecipe(commonDivider, 0, ingredients, smeltingTemperature, result, byProducts);
	}

	public void addRecipe(long commonDivider, long exothermicFuelUnits, OreDictMaterialStack[] ingredients, long smeltingTemperature, OreDictMaterialStack result, OreDictMaterialStack... byProducts) {
		if (exothermicFuelUnits != 0 && (U / ticksPerFuelUnit) % exothermicFuelUnits != 0) {
			ERR.println("WARNING: " + fakeRecipes.mNameInternal + " Recipe for '"+result.mMaterial.mNameInternal+"' has an amount of " + exothermicFuelUnits + " fuel units, which is not divisible by " + ticksPerFuelUnit);
		}

		MultiSmeltingRecipe recipe = new MultiSmeltingRecipe(commonDivider, exothermicFuelUnits, ingredients, smeltingTemperature, result, byProducts);

		for(OreDictMaterialStack stack : recipe.ingredients.getComponents()) {
			if (SmeltsInto.containsKey(stack.mMaterial)) {
				SmeltsInto.get(stack.mMaterial).add(recipe);
			} else {
				SmeltsInto.put(stack.mMaterial, new ArrayListNoNulls<>(false, recipe));
			}
		}

		if (SmeltsFrom.containsKey(result.mMaterial)) {
			SmeltsInto.get(result.mMaterial).add(recipe);
		} else {
			SmeltsInto.put(result.mMaterial, new ArrayListNoNulls<>(false, recipe));
		}

		addFakeRecipe(recipe);
	}

	private void addFakeRecipe(MultiSmeltingRecipe recipe) {
		// input
		boolean doAdd = true, addSpecial = false;
		ArrayListNoNulls<ItemStack> dustInputs = new ArrayListNoNulls<>(), ingotInputs = new ArrayListNoNulls<>(), specialInputs = new ArrayListNoNulls<>(), outputs = new ArrayListNoNulls<>();
		ArrayListNoNulls<FluidStack> fluidInputs = new ArrayListNoNulls<>(), fluidOutputs = new ArrayListNoNulls<>();
		for (OreDictMaterialStack stack : recipe.ingredients.getUndividedComponents()) {
			boolean addedSpecial = false;
			if (stack.mMaterial.mHidden) {doAdd = false; break;}
			if (stack.mMaterial == MT.OREMATS.Magnetite          ) {addedSpecial = specialInputs.add(ST.make(BlocksGT.Sands, UT.Code.divup(stack.mAmount, U*9), 0, "You probably want to craft it into Dust"));} else
			if (stack.mMaterial == MT.OREMATS.BasalticMineralSand) {addedSpecial = specialInputs.add(ST.make(BlocksGT.Sands, UT.Code.divup(stack.mAmount, U*9), 1, "You probably want to craft it into Dust"));} else
			if (stack.mMaterial == MT.OREMATS.GraniticMineralSand) {addedSpecial = specialInputs.add(ST.make(BlocksGT.Sands, UT.Code.divup(stack.mAmount, U*9), 2, "You probably want to craft it into Dust"));} else
			if (stack.mMaterial == MT.C                          ) {addedSpecial = specialInputs.add(OM.dustOrIngot(MT.Coal            , stack.mAmount * 2));}
			if (stack.mMaterial == MT.CaCO3                      ) {addedSpecial = specialInputs.add(OM.dustOrIngot(MT.STONES.Limestone, stack.mAmount * 2));}

			ItemStack dust = OM.dustOrIngot(stack.mMaterial, stack.mAmount);
			if (dust == null && recipe.smeltingTemperature >= stack.mMaterial.mBoilingPoint) {
				fluidInputs.add(stack.mMaterial.gas(stack.mAmount, false));
			} else if (dust == null && recipe.smeltingTemperature >= stack.mMaterial.mMeltingPoint) {
				fluidInputs.add(stack.mMaterial.liquid(stack.mAmount, false));
			} else if (dust != null) {
				if (!dustInputs.add(dust)) {
					doAdd = false;
					break;
				}
				ingotInputs.add(OM.ingotOrDust(stack.mMaterial, stack.mAmount));
				if (addedSpecial) addSpecial = true;
				else specialInputs.add(dust);
			} else {
				doAdd = false;
				ERR.println("WARNING: " + fakeRecipes.mNameInternal + " Recipe has an invalid input component: " + stack.mMaterial.mNameInternal + " does not have a dust, ingot or fluid form");
			}
		}

		for (OreDictMaterialStack stack : recipe.results.getUndividedComponents()) {
			if (recipe.smeltingTemperature >= stack.mMaterial.mBoilingPoint) {
				fluidOutputs.add(stack.mMaterial.gas(stack.mAmount, false));
			} else {
				ItemStack ingot = OM.ingotOrDust(stack.mMaterial, stack.mAmount);
				if (ingot != null) {
					outputs.add(ingot);
				} else if (recipe.smeltingTemperature >= stack.mMaterial.mMeltingPoint) {
					fluidOutputs.add(stack.mMaterial.liquid(stack.mAmount, false));
				} else {
					doAdd = false;
					ERR.println("WARNING: " + fakeRecipes.mNameInternal + " Recipe has an invalid output component: " + stack.mMaterial.mNameInternal + " does not have a dust, ingot or fluid form");
				}
			}
		}

		if (doAdd) {
			long energy_output = recipe.exothermic ? -exothermicEnergyPerTick : 0;
			long duration = recipe.exothermic ? ticksPerFuelUnit * recipe.fuelUnits : 0;
			if (!dustInputs.isEmpty())
				fakeRecipes.addFakeRecipe(false, dustInputs .toArray(ZL_IS), outputs.toArray(ZL_IS), null, null, fluidInputs.toArray(ZL_FS), fluidOutputs.toArray(ZL_FS), duration, energy_output, recipe.smeltingTemperature);
			if (!ingotInputs.isEmpty())
				fakeRecipes.addFakeRecipe(false, ingotInputs.toArray(ZL_IS), outputs.toArray(ZL_IS), null, null, fluidInputs.toArray(ZL_FS), fluidOutputs.toArray(ZL_FS), duration, energy_output, recipe.smeltingTemperature);
			if (addSpecial)
				fakeRecipes.addFakeRecipe(false, specialInputs.toArray(ZL_IS), outputs.toArray(ZL_IS), null, null, fluidInputs.toArray(ZL_FS), fluidOutputs.toArray(ZL_FS), duration, energy_output, recipe.smeltingTemperature);
		}
	}
}
