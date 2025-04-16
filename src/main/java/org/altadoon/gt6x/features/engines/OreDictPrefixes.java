package org.altadoon.gt6x.features.engines;

import gregapi.code.ICondition;
import gregapi.data.MD;
import gregapi.data.OP;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.util.OM;
import net.minecraft.item.ItemStack;

import static gregapi.data.CS.*;
import static gregapi.data.TD.Prefix.*;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class OreDictPrefixes {
	public static OreDictPrefix engineBlock = null;
	public static OreDictPrefix pistonHead = null;
	public static OreDictPrefix piston = null;
	public static OreDictPrefix catalyticConverter = null;
	public static OreDictPrefix tbcCoatedRotor = null;

	public static void init() {
		engineBlock = OreDictPrefix.createPrefix("engineblock")
				.setCategoryName("Engine Blocks")
				.setLocalItemName("", " Engine Block")
				.setMaterialStats(5*U)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		engineBlock.setLocalPrefixName(engineBlock.mNameCategory);

		pistonHead = OreDictPrefix.createPrefix("pistonhead")
				.setCategoryName("Piston Heads")
				.setLocalItemName("", " Piston Head")
				.setMaterialStats(U)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		pistonHead.setLocalPrefixName(pistonHead.mNameCategory);
		new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.pistonhead" , pistonHead);

		piston = OreDictPrefix.createPrefix("piston")
				.setCategoryName("Pistons")
				.setLocalItemName("", " Piston")
				.setMaterialStats(U+U2+U4+U8)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		piston.setLocalPrefixName(piston.mNameCategory);
		new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.piston" , piston);

		catalyticConverter = OreDictPrefix.createPrefix("catalyticconverter")
				.setCategoryName("Catalytic Converters")
				.setLocalItemName("", " Catalytic Converter")
				.setMaterialStats(U)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		catalyticConverter.setLocalPrefixName(catalyticConverter.mNameCategory);
		new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.catalyticconverter" , catalyticConverter);

		tbcCoatedRotor = OreDictPrefix.createPrefix("tbcrotor")
				.setCategoryName("TBC Coated Rotors")
				.setLocalItemName("Thermal Barrier Coated ", " Rotor")
				.setMaterialStats(OP.rotor.mAmount)
				.add(RECYCLABLE, TOOLTIP_MATERIAL, UNIFICATABLE)
				.setCondition(ICondition.FALSE);
		tbcCoatedRotor.setLocalPrefixName(tbcCoatedRotor.mNameCategory);
		new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.tbcrotor" , tbcCoatedRotor);
	}

	public static void registerCustomPrefixItem(OreDictPrefix prefix, OreDictMaterial material, ItemStack itemStack) {
		OM.reg(prefix, material, itemStack);
		prefix.mRegisteredItems.add(itemStack);
		OreDictManager.INSTANCE.setItemData(itemStack, new OreDictItemData(material, prefix.mAmount));
	}
}
