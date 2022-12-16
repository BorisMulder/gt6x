package org.altadoon.gt6x.features.metallurgy;

import gregapi.code.ICondition;
import gregapi.data.CS;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.handlers.RecipeMapHandlerPrefix;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author Gregorius Techneticies
 */
public class RecipeMapHandlerPrefixSintering extends RecipeMapHandlerPrefix {
    public RecipeMapHandlerPrefixSintering(OreDictPrefix aInputPrefix, long aInputAmount, FluidStack aFluidInputPerUnit, long aEUt, long aDuration, long aMultiplier, FluidStack aFluidOutputPerUnit, OreDictPrefix aOutputPrefix, long aOutputAmount, ItemStack aAdditionalInput, ItemStack aAdditionalOutput, boolean aAllowToGenerateAllRecipesAtOnce, boolean aOutputPulverizedRemains, boolean aFlatFluidCosts, ICondition aCondition) {
        super(aInputPrefix, aInputAmount, aFluidInputPerUnit, aEUt, aDuration, aMultiplier, aFluidOutputPerUnit, aOutputPrefix, aOutputAmount, aAdditionalInput, aAdditionalOutput, aAllowToGenerateAllRecipesAtOnce, aOutputPulverizedRemains, aFlatFluidCosts, aCondition);
    }

    public RecipeMapHandlerPrefixSintering(OreDictPrefix aInputPrefix1, long aInputAmount1, OreDictPrefix aInputPrefix2, long aInputAmount2, FluidStack aFluidInputPerUnit, long aEUt, long aDuration, long aMultiplier, FluidStack aFluidOutputPerUnit, OreDictPrefix aOutputPrefix1, long aOutputAmount1, OreDictPrefix aOutputPrefix2, long aOutputAmount2, ItemStack aAdditionalInput, ItemStack aAdditionalOutput, boolean aAllowToGenerateAllRecipesAtOnce, boolean aOutputPulverizedRemains, boolean aFlatFluidCosts, ICondition aCondition) {
        super(aInputPrefix1, aInputAmount1, aInputPrefix2, aInputAmount2, aFluidInputPerUnit, aEUt, aDuration, aMultiplier, aFluidOutputPerUnit, aOutputPrefix1, aOutputAmount1, aOutputPrefix2, aOutputAmount2, aAdditionalInput, aAdditionalOutput, aAllowToGenerateAllRecipesAtOnce, aOutputPulverizedRemains, aFlatFluidCosts, aCondition);
    }

    public RecipeMapHandlerPrefixSintering(OreDictPrefix[] aInputPrefixes, long[] aInputAmount, FluidStack aFluidInputPerUnit, long aEUt, long aDuration, long aMultiplier, FluidStack aFluidOutputPerUnit, OreDictPrefix[] aOutputPrefixes, long[] aOutputAmount, ItemStack aAdditionalInput, ItemStack aAdditionalOutput, boolean aAllowToGenerateAllRecipesAtOnce, boolean aOutputPulverizedRemains, boolean aFlatFluidCosts, ICondition aCondition) {
        super(aInputPrefixes, aInputAmount, aFluidInputPerUnit, aEUt, aDuration, aMultiplier, aFluidOutputPerUnit, aOutputPrefixes, aOutputAmount, aAdditionalInput, aAdditionalOutput, aAllowToGenerateAllRecipesAtOnce, aOutputPulverizedRemains, aFlatFluidCosts, aCondition);
    }

    @Override
    public OreDictMaterial getOutputMaterial(OreDictMaterial aMaterial) {
        return aMaterial.mTargetForging.mMaterial;
    }

    @Override
    public long getCosts(OreDictMaterial aMaterial) {
        return Math.max(16, 1+(long)Math.abs(((getOutputMaterial(aMaterial).mMeltingPoint - CS.DEF_ENV_TEMP) * aMaterial.getWeight(mUnitsInputted)) / (300*mEUt)));
    }
}
