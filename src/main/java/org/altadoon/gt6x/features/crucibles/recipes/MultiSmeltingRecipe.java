package org.altadoon.gt6x.features.crucibles.recipes;

import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.oredict.configurations.OreDictConfigurationComponent;

import java.util.ArrayList;
import java.util.Arrays;

import static gregapi.data.CS.ERR;
import static gregapi.data.CS.U;

public class MultiSmeltingRecipe {
	public long smeltingTemperature;
	public boolean exothermic;
	public long fuelUnits;
	public IOreDictConfigurationComponent ingredients;
	public IOreDictConfigurationComponent results;

	MultiSmeltingRecipe(long commonDivider, long exothermicFuelUnits, OreDictMaterialStack[] ingredients, long smeltingTemperature, OreDictMaterialStack result, OreDictMaterialStack... byProducts) {
		if (ingredients.length == 0 || result == null)
			throw new IllegalArgumentException("Multi-smelting recipe must have at least one input and output");

		if (commonDivider == 0) {
			long amount = 0;
			for (OreDictMaterialStack stack : ingredients) {
				amount += stack.mAmount;
			}
			commonDivider = amount / U;
			if (amount % U != 0) ERR.println("WARNING: Multi-smelting recipe for '"+result.mMaterial.mNameInternal+"' has an amount of " + amount + " components and automatically generates a divider, that is leaving a tiny rest after the division, breaking some material amounts. Manual setting of variables is required.");
		}

		this.smeltingTemperature = smeltingTemperature;
		this.exothermic = exothermicFuelUnits != 0;
		this.fuelUnits = exothermicFuelUnits;
		this.ingredients = new OreDictConfigurationComponent(commonDivider, ingredients);

		ArrayList<OreDictMaterialStack> allResults = new ArrayList<>();
		allResults.add(result);
		allResults.addAll(Arrays.asList(byProducts));
		this.results = new OreDictConfigurationComponent(commonDivider, allResults.toArray(new OreDictMaterialStack[]{}));
	}
}
