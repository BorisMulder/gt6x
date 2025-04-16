package org.altadoon.gt6x.common;

import gregapi.recipes.AdvancedCraftingShapeless;
import gregapi.util.CR;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import static gregapi.util.CR.DEL_OTHER_RECIPES;

public class CRx {
    public static void disableGt6(ItemStack output) {
        CR.BUFFER.removeIf(r -> ST.equal(r.getRecipeOutput(), output));
    }

    public static void overrideShaped(ItemStack output, long bitMask, Object... recipe) {
        disableGt6(output);
        CR.shaped(output, bitMask | DEL_OTHER_RECIPES, recipe);
    }

    public static void overrideShapeless(ItemStack input, ItemStack output) {
        CR.BUFFER.removeIf(r ->  {
            if (r instanceof AdvancedCraftingShapeless sl) {
				ArrayList<Object> inputs = sl.getInput();
                return inputs.size() == 1 &&
                        inputs.get(0) instanceof ItemStack &&
                        ST.equal((ItemStack) inputs.get(0), input);
            } else {
                return false;
            }
        });
        CR.shapeless(output, new Object[] { input });
    }

    public static void overrideShapelessCompat(ItemStack output, Object... recipe) {
        CR.delate(output);
        CR.shapeless(output, recipe);
    }
}
