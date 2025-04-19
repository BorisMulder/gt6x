package org.altadoon.gt6x.common;

import gregapi.code.ICondition;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictPrefix;
import org.altadoon.gt6x.common.items.PrefixItemAnion;

import static gregapi.data.CS.U;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class OPx {
	public static OreDictPrefix anionXResin = null;
	public static OreDictPrefix cationXResin = null;

	public static void init() {
		anionXResin = OreDictPrefix.createPrefix("anionxresin")
				.setCategoryName("Anion Exchange Resins")
				.setLocalItemName("Anion Exchange Resin (", " Ions)")
				.setCondition(new ICondition.Or<>(TD.Atomic.HALOGEN, TD.Atomic.CHALCOGEN))
				.forceItemGeneration(MT.N, MT.P, MT.As, MTx.OH, MTx.SO4, MTx.NO3, MT.CO3)
				.disableItemGeneration(MTx.Ozone);
		new PrefixItemAnion(MOD_ID, MD.GT.mID, "gt6x.meta.anionxresin" , anionXResin);
		cationXResin = OreDictPrefix.createPrefix("cationxresin")
				.setCategoryName("Cation Exchange Resins")
				.setLocalItemName("Cation Exchange Resin (", " Ions)")
				.setCondition(new ICondition.And<>(TD.Atomic.ELEMENT, TD.Atomic.METAL))
				.forceItemGeneration(MT.H, MT.Ge, MT.Sb, MTx.NH4);
		new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.cationxresin" , cationXResin);
	}
}
