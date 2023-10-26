package org.altadoon.gt6x.common.utils;

import gregapi.code.ArrayListNoNulls;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.OreDictPrefix;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.CS.F;

public class OD_Utils {
	/*
		just a reWrite of OreDictManager.getOres that also handles amounts
	 */
	public static List<ItemStack> getOres(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount,boolean aTransformWildcardBlocksTo16) {
		return getOres(aPrefix.mNameInternal + aMaterial.mNameInternal, aAmount, aTransformWildcardBlocksTo16);
	}
	/** @return a Copy of the OreDictionary.getOres() List */
	public static List<ItemStack> getOres(OreDictPrefix aPrefix, OreDictMaterialStack aMaterial, int aAmount, boolean aTransformWildcardBlocksTo16) {
		return getOres(aPrefix.mNameInternal + aMaterial.mMaterial.mNameInternal, aAmount, aTransformWildcardBlocksTo16);
	}

	/** @return a Copy of the OreDictionary.getOres() List */
	public static List<ItemStack> getOres(Object aOreName,int aAmount, boolean aTransformWildcardBlocksTo16) {
		String aName = aOreName==null?"":aOreName.toString();
		List<ItemStack> rList = new ArrayListNoNulls<>(), tList;
		if (UT.Code.stringValid(aName)) {
			if (aTransformWildcardBlocksTo16) {
				tList = OreDictionary.getOres(aName, F);
				for (ItemStack tStack : tList) {
					if (ST.meta_(tStack) == W && ST.block(tStack) != NB) {
						for (int i = 0; i < 16; i++) rList.add(ST.make(tStack.getItem(), aAmount, i));
					} else {
						rList.add(tStack);
					}
				}
			} else {
				rList.addAll(OreDictionary.getOres(aName, F));
			}
		}
		return rList;
	}
}
