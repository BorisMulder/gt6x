package org.altadoon.gt6x.common;

import gregapi.code.IItemContainer;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.recipes.AdvancedCraftingShapeless;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static gregapi.data.CS.F;
import static gregapi.data.CS.W;
import static gregapi.util.CR.DEL_OTHER_RECIPES;
import static gregapi.util.CR.REV;

public class CRx {
    public static void disableGt6(ItemStack output) {
        CR.BUFFER.removeIf(r -> ST.equal(r.getRecipeOutput(), output));
    }

    public static void overrideItemData(ItemStack output, Object ... recipe) {
        int i = 0;
        StringBuilder shape = new StringBuilder();
        while (recipe[i] instanceof String s) {
            i++;
            shape.append(s);
        }

        HashMap<Character, OreDictItemData> itemDataMap = new HashMap<>();
        for (; i < recipe.length; i += 2) {
            Object input = recipe[i + 1];
            if (input instanceof IItemContainer container) {
                input = container.get(1);
                if (input == null) continue;
            } else if (input instanceof Enum e) {
                input = e.name();
            } else if (input instanceof Item item) {
                input = ST.make(item, 1, W);
            } else if (input instanceof Block block) {
                input = ST.make(block, 1, W);
            }

            if (input instanceof ItemStack stack) {
                itemDataMap.put((char)recipe[i], OM.data_(stack));
            } else if (input instanceof OreDictItemData dat) {
                itemDataMap.put((char)recipe[i], dat);
            } else {
                String inputString = input.toString();
                if (UT.Code.stringValid(inputString))
                    itemDataMap.put((char)recipe[i], OreDictManager.INSTANCE.getAutomaticItemData(inputString));
            }
        }

        ArrayList<OreDictItemData> data = new ArrayList<>();
        for (i = 0; i < shape.length(); i++) {
            data.add(itemDataMap.get(shape.charAt(i)));
        }

        OreDictManager.INSTANCE.setItemData(output, new OreDictItemData(data));
    }

    public static void overrideShaped(ItemStack output, long bitMask, Object... recipe) {
        disableGt6(output);

        boolean success = CR.shaped(output, bitMask | DEL_OTHER_RECIPES, recipe);
        if (success && (bitMask & REV) != 0) {
            overrideItemData(output, recipe);
        }
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
