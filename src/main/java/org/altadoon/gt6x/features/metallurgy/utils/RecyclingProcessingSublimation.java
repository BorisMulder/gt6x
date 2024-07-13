package org.altadoon.gt6x.features.metallurgy.utils;

import gregapi.data.FL;
import gregapi.data.RM;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.event.IOreDictListenerRecyclable;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.loaders.b.Loader_OreProcessing;
import net.minecraftforge.fluids.FluidStack;

import static gregapi.data.CS.*;
import static gregapi.data.TD.Prefix.ORE;
import static gregapi.data.TD.Prefix.ORE_PROCESSING_DIRTY;

public class RecyclingProcessingSublimation extends Loader_OreProcessing.RecyclingProcessing implements IOreDictListenerRecyclable {
    @Override
    public void onRecycleableRegistration(OreDictRecyclingContainer container) {
        if (container.mItemData == null || !ST.ingredable(container.mStack)) return;

        for (OreDictMaterialStack originalStack : container.mItemData.getAllMaterialStacks()) {
            OreDictMaterialStack smeltingStack = originalStack.mMaterial.mTargetSmelting;
            if (smeltingStack.mMaterial.mMeltingPoint == smeltingStack.mMaterial.mBoilingPoint && smeltingStack.mAmount > 0 && smeltingStack.mMaterial.mGas != null) {
                // skip ores
                if (container.mItemData.mPrefix != null && container.mItemData.mPrefix.containsAny(ORE_PROCESSING_DIRTY, ORE)) return;

                OreDictMaterialStack targetStack = OM.stack(UT.Code.units(originalStack.mAmount, U, smeltingStack.mAmount, F), smeltingStack.mMaterial);
                FluidStack targetFluidStack = targetStack.mMaterial.gas(targetStack.mAmount, false);
                RM.Smelter.addRecipe1(true, 16, (long)Math.max(16, (OM.weight(container.mItemData.getAllMaterialStacks()) * (Math.max(originalStack.mMaterial.mMeltingPoint, targetFluidStack.getFluid().getTemperature())-DEF_ENV_TEMP))/1600), container.mStack, NF, targetFluidStack, ZL_IS);
            }
        }
    }
}
