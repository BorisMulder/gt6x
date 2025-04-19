package org.altadoon.gt6x.common;

import gregapi.data.MD;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictPrefix;
import org.altadoon.gt6x.common.items.PrefixItemAnion;

import static gregapi.data.TD.Prefix.TOOLTIP_MATERIAL;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class OPx {
	public static OreDictPrefix anionXResin = null;
	public static OreDictPrefix cationXResin = null;

	public static void init() {
		anionXResin = OreDictPrefix.createPrefix("anionxresin")
				.setCategoryName("Anion Exchange Resins")
				.setLocalItemName("Anion Exchange Resin (", " Ions)")
				.setMaterialStats(-1, -1)
				.add(TOOLTIP_MATERIAL)
				.setCondition(MTx.ANION);
		new PrefixItemAnion(MOD_ID, MD.GT.mID, "gt6x.meta.anionxresin" , anionXResin);
		cationXResin = OreDictPrefix.createPrefix("cationxresin")
				.setCategoryName("Cation Exchange Resins")
				.setLocalItemName("Cation Exchange Resin (", " Ions)")
				.setMaterialStats(-1, -1)
				.add(TOOLTIP_MATERIAL)
				.setCondition(MTx.CATION);
		new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.cationxresin" , cationXResin);
	}
}
