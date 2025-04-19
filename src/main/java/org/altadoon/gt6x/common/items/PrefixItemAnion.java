package org.altadoon.gt6x.common.items;

import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;

public class PrefixItemAnion extends PrefixItem {
	public PrefixItemAnion(String modID, String mID, String name, OreDictPrefix prefix) {
		super(modID, mID, name, prefix);
	}

	@Override
	public String getLocalName(OreDictPrefix prefix, OreDictMaterial material) {
		String ionName = null;
		if (material.contains(TD.Atomic.HALOGEN)) ionName = material.mNameLocal.replace("ine", "ide");
		if (material == MT.Ts) ionName = "Tennesside";
		if (material == MT.O) ionName = "Oxide";
		if (material == MT.S) ionName = "Sulfide";
		if (material == MT.Se) ionName = "Selenide";
		if (material == MT.Te) ionName = "Telluride";
		if (material == MT.N) ionName = "Nitride";
		if (material == MT.P) ionName = "Phosphide";
		if (material == MT.As) ionName = "Arsenide";

		if (material == MT.CO3) ionName = "Carbonate";

		if (ionName != null)
			return prefix.mMaterialPre + ionName + prefix.mMaterialPost;
		else
			return super.getLocalName(prefix, material);
	}
}
