package org.altadoon.gt6x.common.items;

import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;

import static gregapi.data.CS.NUM_SUB;

public class PrefixItemCation extends PrefixItem {
	public PrefixItemCation(String modID, String mID, String name, OreDictPrefix prefix) {
		super(modID, mID, name, prefix);
	}

	@Override
	public String getLocalName(OreDictPrefix prefix, OreDictMaterial material) {
		String ionName = null;
		if (material == MT.Lu) {
			ionName = "Lu - Tb";
		} else if (material == MT.Tb) {
			ionName = "Tb, NH" + NUM_SUB[4];
		} else if (material == MT.Gd) {
			ionName = "Gd - La";
		} else if (material == MT.La) {
			ionName = "La, NH" + NUM_SUB[4];
		} else if (material == MT.Y || (material.contains(TD.Atomic.LANTHANIDE) && material.getProtons() >= MT.Tb.getProtons())) {
			ionName = material.mTooltipChemical + " - Tb, NH" + NUM_SUB[4];
		} else if (material.contains(TD.Atomic.LANTHANIDE)) {
			ionName = material.mTooltipChemical + " - La, NH" + NUM_SUB[4];
		}

		if (ionName != null)
			return prefix.mMaterialPre + ionName + prefix.mMaterialPost;
		else
			return super.getLocalName(prefix, material);
	}
}
