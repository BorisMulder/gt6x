package org.altadoon.gt6x.common;

import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.recipes.AdvancedCraftingShapeless;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

	public static void replaceItemDataPart(ItemStack stack, OreDictItemData oldPart, OreDictItemData newPart) {
		OreDictItemData data = OM.data_(stack);
		if (data == null) return;

		List<OreDictMaterialStack> remaining = new ArrayList<>();
		for (OreDictMaterialStack matStack : data.getAllMaterialStacks()) remaining.add(matStack.clone());
		if (oldPart != null) for (OreDictMaterialStack oldStack : oldPart.getAllMaterialStacks()) {
			for (OreDictMaterialStack matStack : remaining) if (matStack.mMaterial == oldStack.mMaterial) matStack.mAmount -= oldStack.mAmount;
		}
		remaining.removeIf(matStack -> matStack.mAmount <= 0);

		OreDictManager.INSTANCE.setItemData(stack, remaining.isEmpty() ? newPart : new OreDictItemData(
				new OreDictItemData(remaining.get(0), remaining.subList(1, remaining.size()).toArray(new OreDictMaterialStack[0])),
				newPart
		));
	}
}
