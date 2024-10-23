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
	private static void addMoldWithRecipe(OreDictMaterial mat, float hardness, boolean acidProof, boolean hidden, String blockName, MultiTileEntityBlock blockBase, int id, Class<? extends TileEntity> moldClass, Object recipeItem, Object... recipe) {
		Object[] recipe2 = new Object[recipe.length + 1];
		System.arraycopy(recipe, 0, recipe2, 0, recipe.length);
		recipe2[recipe.length] = recipeItem;
		addMold(mat, hardness, acidProof, hidden, blockName, blockBase, id, moldClass, recipe2);
	}
	private static void addMold(OreDictMaterial mat, float hardness, boolean acidProof, boolean hidden, String blockName, MultiTileEntityBlock blockBase, int id, Class<? extends TileEntity> moldClass, Object... recipe) {
		MTEx.gt6xMTEReg.add(blockName + " (" + mat.getLocal() + ")", "Molds", id, 1072, moldClass, mat.mToolQuality, 16, blockBase, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, hardness, NBT_RESISTANCE, hardness, NBT_ACIDPROOF, acidProof, NBT_HIDDEN, hidden), recipe);
	}

	private static int countCharsInString(String recipe, char item) {
		int result = 0;
		for (int i = 0; i < recipe.length(); i++) if (recipe.charAt(i) == item) result++;
		return result;
	}

	public static int addSpecialMolds(Class<? extends TileEntity> moldClass, String blockName, int id, ILx clayItem, ILx ceramicItem, String recipe1, String recipe2, String recipe3, char prefixItem) {
		MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;
		OreDictMaterial mat;

		// Stones
		addMoldWithRecipe(MT.Stone, 5.0F, false, false, blockName, MTEx.StoneBlock, id++, moldClass, Blocks.stone, recipe1, recipe2, recipe3, prefixItem);
		for (OreDictMaterial stone : new OreDictMaterial[]{ MT.STONES.Basalt, MT.STONES.GraniteBlack, MT.STONES.GraniteRed }) {
			addMoldWithRecipe(stone, 15.0F, false, true, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(stone), recipe1, recipe2, recipe3, prefixItem);
		}
		addMoldWithRecipe(MT.NetherBrick, 5.0F, false, true, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(MT.NetherBrick), recipe1, recipe2, recipe3, prefixItem);

		// Clay/Ceramics
		addMold(MT.Ceramic, 5.0F, false, false, blockName, MTEx.StoneBlock, id++, moldClass);
		ceramicItem.set(reg.getItem(), new OreDictItemData(MT.Ceramic, U * (countCharsInString(recipe1, prefixItem) + countCharsInString(recipe2, prefixItem) + countCharsInString(recipe3, prefixItem))));
		char rollingPin = prefixItem == 'R' ? 'P' : 'R';
		String clayRecipe1 = recipe1.replace('h', 'k').replace('y', rollingPin);
		String clayRecipe2 = recipe2.replace('h', 'k').replace('y', rollingPin);
		String clayRecipe3 = recipe3.replace('h', 'k').replace('y', rollingPin);
		CR.shaped(clayItem.get(1), CR.DEF_NCC, clayRecipe1, clayRecipe2, clayRecipe3, rollingPin, OreDictToolNames.rollingpin, prefixItem, ILx.Fireclay_Ball.get(1));
		RM.add_smelting(clayItem.get(1), ceramicItem.get(1), false, false, true);

		// Mod stones
		addMoldWithRecipe(MT.STONES.Umber, 5.0F, false, !MD.ERE.mLoaded, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(MT.STONES.Umber), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.STONES.Livingrock, 5.0F, false, !MD.BOTA.mLoaded, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(MT.STONES.Livingrock), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.STONES.Holystone, 5.0F, false, !MD.AETHER.mLoaded, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(MT.STONES.Holystone), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.STONES.Betweenstone, 5.0F, false, !MD.BTL.mLoaded, blockName, MTEx.StoneBlock, id++, moldClass, OP.stone.dat(MT.STONES.Betweenstone), recipe1, recipe2, recipe3, prefixItem);

		// Misc
		addMoldWithRecipe(ANY.Quartz, 5.0F, false, false, blockName, MTEx.StoneBlock, id++, moldClass, OP.gem.dat(ANY.Quartz), recipe1, recipe2, recipe3, prefixItem);
		addMoldWithRecipe(MT.C, 10.0F, false, false, blockName, MTEx.StoneBlock, id++, moldClass, OP.plate.dat(MT.Graphene), recipe1, recipe2, recipe3, prefixItem);

		// Metals
		mat = MT.Bronze; addMoldWithRecipe(mat, 7.0F, false, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);
		mat = MT.Invar; addMoldWithRecipe(mat, 4.0F, false, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);
		for (OreDictMaterial metal : new OreDictMaterial[]{ MT.Steel, MT.HSLA, MT.DarkIron, MT.MeteoricIron, MT.MeteoricSteel, MT.Knightmetal, MT.FierySteel, MT.Octine }) {
			addMoldWithRecipe(metal, 6.0F, false, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(metal), recipe1, recipe2, recipe3, prefixItem);
		}
		for (OreDictMaterial metal : new OreDictMaterial[]{ MT.Ti, MT.Mo, MT.Nb, MT.Ta, MT.Os, MT.V, MT.NiobiumTitanium, MT.Ta4HfC5  }) {
			addMoldWithRecipe(metal, 9.0F, false, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(metal), recipe1, recipe2, recipe3, prefixItem);
		}
		// Acid proof metals
		for (OreDictMaterial metal : new OreDictMaterial[]{ MT.StainlessSteel, MT.Netherite, MT.Thaumium }) {
			addMoldWithRecipe(metal, 6.0F, true, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(metal), recipe1, recipe2, recipe3, prefixItem);
		}
		for (OreDictMaterial acidProofMetal : new OreDictMaterial[] { MT.Cr, MT.Ir }) {
			addMoldWithRecipe(acidProofMetal, 9.0F, true, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(acidProofMetal), recipe1, recipe2, recipe3, prefixItem);
		}
		for (OreDictMaterial acidProofMetal : new OreDictMaterial[] { ANY.W, MT.VoidMetal }) {
			addMoldWithRecipe(acidProofMetal, 10.0F, true, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(acidProofMetal), recipe1, recipe2, recipe3, prefixItem);
		}
		mat = MT.Bedrock_HSLA_Alloy; addMoldWithRecipe(mat, 100.0F, false, false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);
		mat = MT.Ad                ; addMoldWithRecipe(mat, 100.0F, true , false, blockName, MTEx.MachineBlock, id++, moldClass, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);

		// New ceramics
		for (OreDictMaterial ceramic : new OreDictMaterial[]{ MT.SiC, MTx.MgOC }) {
			addMoldWithRecipe(ceramic, 10.0F, false, false, blockName, MTEx.StoneBlock, id++, moldClass, OP.plate.dat(ceramic), recipe1, recipe2, recipe3, prefixItem);
		}

		return id;
	}
}
