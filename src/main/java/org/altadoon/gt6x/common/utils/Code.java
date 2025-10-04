package org.altadoon.gt6x.common.utils;

import java.util.Arrays;

public class Code {
	public static int countItemsInRecipe(String recipe1, String recipe2, String recipe3, char item) {
		return countCharsInString(recipe1, item) + countCharsInString(recipe2, item) + countCharsInString(recipe3, item);
	}

	public static int countCharsInString(String recipe, char item) {
		int result = 0;
		for (int i = 0; i < recipe.length(); i++) if (recipe.charAt(i) == item) result++;
		return result;
	}

	public static <T> T[] concatArrays(T[] array1, T[] array2) {
		T[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}
}
