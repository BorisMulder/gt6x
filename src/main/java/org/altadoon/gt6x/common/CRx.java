package org.altadoon.gt6x.common;

import gregapi.recipes.AdvancedCraftingShapeless;
import gregapi.util.CR;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CRx {
    public static void overrideGT6ShapedCraftingRecipe(ItemStack output, Object... recipe) {
        CR.BUFFER.removeIf(r -> ST.equal(r.getRecipeOutput(), output));
        CR.shaped(output, CR.DEF_REM, recipe);
    }

    public static void overrideGT6SingleShapelessCraftingRecipe(ItemStack input, ItemStack output) {
        CR.BUFFER.removeIf(r ->  {
            if (r instanceof AdvancedCraftingShapeless) {
                AdvancedCraftingShapeless sl = (AdvancedCraftingShapeless)r;
                ArrayList<Object> inputs = sl.getInput();
                return inputs.size() == 1 &&
                        inputs.get(0) instanceof ItemStack &&
                        ST.equal((ItemStack) inputs.get(0), input);
            } else {
                return false;
            }
        });
        CR.shapeless(output, CR.DEF_REM, new Object[] { input });
    }
}
