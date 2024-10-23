package org.altadoon.gt6x.features.engines;

import gregapi.code.ICondition;
import gregapi.data.TD;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.util.OM;
import net.minecraft.item.ItemStack;

import static gregapi.data.CS.U;
import static gregapi.data.TD.Prefix.*;

public class OreDictPrefixes {
	public static OreDictPrefix engineBlock = null;
	public static OreDictPrefix pistonHead = null;
	public static OreDictPrefix piston = null;


	public static void init() {
		engineBlock = OreDictPrefix.createPrefix("engineblock")
				.setCategoryName("Engine Blocks")
				.setLocalItemName("", " Engine Block")
				.setMaterialStats(5*U)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		engineBlock.setLocalPrefixName(engineBlock.mNameCategory);

		pistonHead = OreDictPrefix.createPrefix("pistonHead")
				.setCategoryName("Piston Heads")
				.setLocalItemName("", " Piston Head")
				.setMaterialStats(U)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		pistonHead.setLocalPrefixName(pistonHead.mNameCategory);

		piston = OreDictPrefix.createPrefix("piston")
				.setCategoryName("Pistons")
				.setLocalItemName("", " Piston")
				.setMaterialStats(2*U)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		piston.setLocalPrefixName(piston.mNameCategory);
	}

	public static void registerCustomPrefixItem(OreDictPrefix prefix, OreDictMaterial material, ItemStack itemStack) {
		OM.reg(prefix, material, itemStack);
		prefix.mRegisteredItems.add(itemStack);
		OreDictManager.INSTANCE.setItemData(itemStack, new OreDictItemData(material, prefix.mAmount));
	}
}
