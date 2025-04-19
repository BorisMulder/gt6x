package org.altadoon.gt6x.common;

import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;

public class OMx {
	public static OreDictMaterialStack[] mul(long factor, OreDictMaterialStack... stacks) {
		OreDictMaterialStack[] result = new OreDictMaterialStack[stacks.length];
		for (int i = 0; i < stacks.length; i++) {
			result[i] = stacks[i].copy(stacks[i].mAmount * factor);
		}
		return result;
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterialStack... stacks) {
		return stacks;
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1) {
		return stacks(OM.stack(mat1, amount1));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3, OreDictMaterial mat4, long amount4) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3), OM.stack(mat4, amount4));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3, OreDictMaterial mat4, long amount4, OreDictMaterial mat5, long amount5) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3), OM.stack(mat4, amount4), OM.stack(mat5, amount5));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3, OreDictMaterial mat4, long amount4, OreDictMaterial mat5, long amount5, OreDictMaterial mat6, long amount6) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3), OM.stack(mat4, amount4), OM.stack(mat5, amount5), OM.stack(mat6, amount6));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3, OreDictMaterial mat4, long amount4, OreDictMaterial mat5, long amount5, OreDictMaterial mat6, long amount6, OreDictMaterial mat7, long amount7) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3), OM.stack(mat4, amount4), OM.stack(mat5, amount5), OM.stack(mat6, amount6), OM.stack(mat7, amount7));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3, OreDictMaterial mat4, long amount4, OreDictMaterial mat5, long amount5, OreDictMaterial mat6, long amount6, OreDictMaterial mat7, long amount7, OreDictMaterial mat8, long amount8) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3), OM.stack(mat4, amount4), OM.stack(mat5, amount5), OM.stack(mat6, amount6), OM.stack(mat7, amount7), OM.stack(mat8, amount8));
	}

	public static OreDictMaterialStack[] stacks(OreDictMaterial mat1, long amount1, OreDictMaterial mat2, long amount2, OreDictMaterial mat3, long amount3, OreDictMaterial mat4, long amount4, OreDictMaterial mat5, long amount5, OreDictMaterial mat6, long amount6, OreDictMaterial mat7, long amount7, OreDictMaterial mat8, long amount8, OreDictMaterial mat9, long amount9) {
		return stacks(OM.stack(mat1, amount1), OM.stack(mat2, amount2), OM.stack(mat3, amount3), OM.stack(mat4, amount4), OM.stack(mat5, amount5), OM.stack(mat6, amount6), OM.stack(mat7, amount7), OM.stack(mat8, amount8), OM.stack(mat9, amount9));
	}
}
