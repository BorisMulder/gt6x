package org.altadoon.gt6x.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.item.ItemFluidDisplay;
import gregapi.recipes.Recipe;
import gregapi.recipes.maps.RecipeMapFuel;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.U;
import static org.altadoon.gt6x.common.RMx.FMx;

public class ItemFluidDisplayX extends ItemFluidDisplay {
    public ItemFluidDisplayX() {
        super();
        ST.hide(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List info_lines, boolean f3_H) {
        super.addInformation(stack, player, info_lines, f3_H);

        Fluid fluid = FL.fluid(ST.meta_(stack));
        if (fluid == null || FL.Error.is(fluid)) return;

        String name = fluid.getName();
        NBTTagCompound nbt = stack.getTagCompound();
        long amount = 0;
        if (nbt != null) {
            amount = nbt.getLong("a");
        }

        for (RecipeMapFuel recipeMap : new RecipeMapFuel[]{ FMx.Petrol, FMx.Diesel }) {
            Collection<Recipe> recipes = recipeMap.mRecipeFluidMap.get(name);
            if (recipes != null && !recipes.isEmpty()) {
                long fuelValue = 0;
                for (Recipe recipe : recipes) if (recipe.mEnabled && recipe.mFluidInputs[0] != null) fuelValue = Math.max(fuelValue, (recipe.getAbsoluteTotalPower() * U) / recipe.mFluidInputs[0].amount);
                if (fuelValue > 0) {
                    StringBuilder line = new StringBuilder(LH.Chat.RED).append(recipeMap.mNameLocal.replace(" Fuels", "")).append(": ").append(LH.Chat.WHITE).append(UT.Code.makeString(fuelValue / U)).append(LH.Chat.YELLOW).append(" GU/L");
                    if (amount > 1) {
                        line.append("; ").append(LH.Chat.WHITE).append(UT.Code.makeString((fuelValue * amount) / U)).append(LH.Chat.YELLOW).append(" GU total");
                    }
                    info_lines.add(line.toString());
                }
            }
        }
    }

    /*
     * This is emptied to prevent displaying each fluid twice in NEI (the base class is registered twice, once in GT6, once in GT6X by calling super()
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aTab, List aList) {}
}
