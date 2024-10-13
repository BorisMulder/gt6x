package org.altadoon.gt6x.common.utils;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.CR;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.items.ILx;

import static gregapi.data.CS.*;
import static gregapi.data.CS.NBT_ACIDPROOF;

public class CrucibleUtils {
	private static void addMoldWithRecipe(OreDictMaterial mat, float hardness, float resistance, boolean acidProof, String blockName, MultiTileEntityBlock blockBase, int id, Class<? extends TileEntity> moldClass, Object recipeItem, Object... recipe) {
		Object[] recipe2 = new Object[recipe.length + 1];
		System.arraycopy(recipe, 0, recipe2, 0, recipe.length);
		recipe2[recipe.length] = recipeItem;
		addMold(mat, hardness, resistance, acidProof, blockName, blockBase, id, moldClass, recipe2);
	}
	private static void addMold(OreDictMaterial mat, float hardness, float resistance, boolean acidProof, String blockName, MultiTileEntityBlock blockBase, int id, Class<? extends TileEntity> moldClass, Object... recipe) {
		MTEx.gt6xMTEReg.add(blockName + " (" + mat.getLocal() + ")", "Molds", id, 1072, moldClass, mat.mToolQuality, 16, blockBase, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, hardness, NBT_RESISTANCE, resistance, NBT_ACIDPROOF, acidProof, NBT_HIDDEN, F), recipe);
	}

	private static int countCharsInString(String recipe, char item) {
		int result = 0;
		for (int i = 0; i < recipe.length(); i++) if (recipe.charAt(i) == item) result++;
		return result;
	}

	public static int addSpecialMolds(Class<? extends TileEntity> moldClass, String blockName, int id, ILx clayItem, ILx ceramicItem, String recipe1, String recipe2, String recipe3, char prefixItem) {
		MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;

		for (OreDictMaterial stone : new OreDictMaterial[]{ MT.Stone, MT.STONES.Basalt, MT.STONES.GraniteBlack, MT.STONES.GraniteRed, MT.NetherBrick, MT.SiC, MTx.MgOC }) {
			addMoldWithRecipe(stone, 5.0F, 5.0F, false, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(stone), recipe1, recipe2, recipe3, prefixItem);
		}
		for (OreDictMaterial metal : new OreDictMaterial[]{ MT.Bronze, MT.Invar, MT.Steel, MT.HSLA, MT.DarkIron, MT.MeteoricIron, MT.MeteoricSteel, MT.Knightmetal, MT.FierySteel, MT.Octine, MT.Ti, MT.Mo, MT.Nb, MT.Ta, MT.Os, MT.V, MT.NiobiumTitanium, MT.Ta4HfC5  }) {
			addMoldWithRecipe(metal, 9.0F, 9.0F, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(metal), recipe1, recipe2, recipe3, prefixItem);
		}
		for (OreDictMaterial acidProofMetal : new OreDictMaterial[] { MT.StainlessSteel, MT.Netherite, MT.Thaumium, MT.Cr, MT.Ir, ANY.W, MT.VoidMetal }) {
			addMoldWithRecipe(acidProofMetal, 6.0F, 6.0F, true, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(acidProofMetal), recipe1, recipe2, recipe3, prefixItem);
		}

		addMold(MT.Ceramic, 5.0F, 5.0F, false, blockName, MTEx.StoneBlock, id++, moldClass);
		ceramicItem.set(reg.getItem(), new OreDictItemData(MT.Ceramic, U * (countCharsInString(recipe1, prefixItem) + countCharsInString(recipe2, prefixItem) + countCharsInString(recipe3, prefixItem))));
		char rollingPin = prefixItem == 'R' ? 'P' : 'R';
		String clayRecipe1 = recipe1.replace('h', 'k').replace('y', rollingPin);
		String clayRecipe2 = recipe2.replace('h', 'k').replace('y', rollingPin);
		String clayRecipe3 = recipe3.replace('h', 'k').replace('y', rollingPin);
		CR.shaped(clayItem.get(1), CR.DEF_NCC, clayRecipe1, clayRecipe2, clayRecipe3, rollingPin, OreDictToolNames.rollingpin, prefixItem, ILx.Fireclay_Ball.get(1));
		RM.add_smelting(clayItem.get(1), ceramicItem.get(1), false, false, true);

		addMoldWithRecipe(MT.Stone, 5.0F, 5.0F, false, blockName, MTEx.StoneBlock, id++, moldClass, Blocks.stone, recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(ANY.Quartz, 5.0F, 5.0F, false, blockName, MTEx.StoneBlock, id++, moldClass, OP.gem.dat(ANY.Quartz), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.C, 10.0F, 10.0F, false, blockName, MTEx.StoneBlock, id++, moldClass, OP.plate.dat(MT.Graphene), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.Bedrock_HSLA_Alloy, 100.0F, 100.0F, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(MT.Bedrock_HSLA_Alloy), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.Ad, 100.0F, 100.0F, true, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(MT.Ad), recipe1, recipe2, recipe3, prefixItem);
		return id;
	}
}
