package org.altadoon.gt6x.features.crucibles.recipes;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.IItemContainer;
import gregapi.data.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.util.UT;
import gregtech.tileentity.tools.*;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.common.utils.Code;
import org.apache.commons.lang3.ArrayUtils;

import static gregapi.data.CS.*;
import static gregapi.data.CS.NBT_ACIDPROOF;

public class CrucibleUtils {
	private static void addPartWithRecipe(OreDictMaterial mat, float hardness, boolean acidProof, boolean hidden, String blockName, String categoryName, MultiTileEntityBlock blockBase, int id, Class<? extends TileEntity> moldClass, Object[] additionalTags, Object recipeItem, Object... recipe) {
		if (recipeItem != null) {
			Object[] recipe2 = new Object[recipe.length + 1];
			System.arraycopy(recipe, 0, recipe2, 0, recipe.length);
			recipe2[recipe.length] = recipeItem;
			addPart(mat, hardness, acidProof, hidden, blockName, categoryName, blockBase, id, moldClass, additionalTags, recipe2);
		} else {
			addPart(mat, hardness, acidProof, hidden, blockName, categoryName, blockBase, id, moldClass, additionalTags);
		}
	}

	private static void addPart(OreDictMaterial mat, float hardness, boolean acidProof, boolean hidden, String blockName, String categoryName, MultiTileEntityBlock blockBase, int id, Class<? extends TileEntity> moldClass, Object[] additionalTags, Object... recipe) {
		Object[] tags = ArrayUtils.addAll(additionalTags, NBT_HARDNESS, hardness, NBT_RESISTANCE, hardness, NBT_ACIDPROOF, acidProof, NBT_HIDDEN, hidden, additionalTags);

		MTEx.gt6xMTEReg.add(blockName + " (" + mat.getLocal() + ")", categoryName, id, 1072, moldClass, mat.mToolQuality, 16, blockBase, UT.NBT.make(NBT_MATERIAL, mat, tags), recipe);
	}

	public static void addCruciblePart(Class<? extends TileEntity> partClass, String blockName, String categoryName, int id, IItemContainer ceramicItem, ILx refractoryItem, String recipe1, String recipe2, String recipe3, char prefixItem, Object... additionalTags) {
		MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;
		OreDictMaterial mat;

		String recipe1Stone = recipe1.replace('w', 'y');
		String recipe2Stone = recipe2.replace('w', 'y');
		String recipe3Stone = recipe3.replace('w', 'y');

		// Stones
		addPartWithRecipe(MT.Stone, 5.0F, false, false, blockName, categoryName, MTEx.StoneBlock, id, partClass, additionalTags, Blocks.stone, recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		int currId = id+1;
		for (OreDictMaterial stone : new OreDictMaterial[]{ MT.STONES.Basalt, MT.STONES.GraniteBlack, MT.STONES.GraniteRed }) {
			addPartWithRecipe(stone, 15.0F, false, true, blockName, categoryName, MTEx.StoneBlock, currId++, partClass, additionalTags, OP.stone.dat(stone), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		}
		addPartWithRecipe(MT.NetherBrick, 5.0F, false, true, blockName, categoryName, MTEx.StoneBlock, id+4, partClass, additionalTags, OP.stone.dat(MT.NetherBrick), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);

		// Ceramic
		long numUnitsInRecipe = Code.countItemsInRecipe(recipe1, recipe2, recipe3, prefixItem);
		OreDictItemData components = new OreDictItemData(MT.Ceramic, U * numUnitsInRecipe);
		addPart(MT.Ceramic, 5.0F, false, false, blockName, categoryName, MTEx.StoneBlock, id+5, partClass, additionalTags);
		if (ceramicItem instanceof IL) {
			((IL)ceramicItem).set(reg.getItem(), components);
		} else if (ceramicItem instanceof ILx) {
			((ILx)ceramicItem).set(reg.getItem(), components);
		}

		// Mod stones
		addPartWithRecipe(MT.STONES.Umber, 5.0F, false, !MD.ERE.mLoaded, blockName, categoryName, MTEx.StoneBlock, id+6, partClass, additionalTags, OP.stone.dat(MT.STONES.Umber), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		addPartWithRecipe(MT.STONES.Livingrock, 5.0F, false, !MD.BOTA.mLoaded, blockName, categoryName, MTEx.StoneBlock, id+7, partClass, additionalTags, OP.stone.dat(MT.STONES.Livingrock), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		addPartWithRecipe(MT.STONES.Holystone, 5.0F, false, !MD.AETHER.mLoaded, blockName, categoryName, MTEx.StoneBlock, id+8, partClass, additionalTags, OP.stone.dat(MT.STONES.Holystone), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		addPartWithRecipe(MT.STONES.Betweenstone, 5.0F, false, !MD.BTL.mLoaded, blockName, categoryName, MTEx.StoneBlock, id+9, partClass, additionalTags, OP.stone.dat(MT.STONES.Betweenstone), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);

		// Refractory ceramic
		addPart(MTx.RefractoryCeramic, 6.0F, false, false, blockName, categoryName, MTEx.StoneBlock, id+12, partClass, additionalTags);
		refractoryItem.set(reg.getItem(), new OreDictItemData(MTx.RefractoryCeramic, U * numUnitsInRecipe));

		// Other new ceramics
		currId = id+13;
		for (OreDictMaterial ceramic : new OreDictMaterial[] { MT.SiC, MTx.MgOC }) {
			addPartWithRecipe(ceramic, 10.0F, false, false, blockName, categoryName, MTEx.StoneBlock, currId++, partClass, additionalTags, OP.plate.dat(ceramic), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		}

		// Misc
		addPartWithRecipe(ANY.Quartz, 5.0F , false, false, blockName, categoryName, MTEx.StoneBlock, id+18, partClass, additionalTags, OP.gem.dat(ANY.Quartz), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);
		addPartWithRecipe(MT .C     , 10.0F, false, false, blockName, categoryName, MTEx.StoneBlock, id+19, partClass, additionalTags, OP.plate.dat(MT.Graphene), recipe1Stone, recipe2Stone, recipe3Stone, prefixItem);

		// Metals
		mat = MT.Bronze; addPartWithRecipe(mat, 7.0F, false, false, blockName, categoryName, MTEx.MachineBlock, id+20, partClass, additionalTags, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);
		mat = MT.Invar; addPartWithRecipe(mat, 4.0F, false, false, blockName, categoryName, MTEx.MachineBlock, id+21, partClass, additionalTags, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);

		OreDictMaterial[] mats = new OreDictMaterial[]{ MT.Steel, MT.HSLA, MT.DarkIron, MT.MeteoricIron, MT.MeteoricSteel, MT.Knightmetal, MT.FierySteel, MT.Octine };
		int[] ids = new int[]{ 22, 41, 26, 31, 32, 27, 28, 42 };
		for (int i = 0; i < mats.length; i++) {
			addPartWithRecipe(mats[i], 6.0F, false, false, blockName, categoryName, MTEx.MachineBlock, id+ids[i], partClass, additionalTags, OP.plate.dat(mats[i]), recipe1, recipe2, recipe3, prefixItem);
		}

		mats = new OreDictMaterial[]{ MT.Ti, MT.Mo, MT.Nb, MT.Ta, MT.Os, MT.V, MT.NiobiumTitanium, MT.Ta4HfC5 };
		ids = new int[]{ 23, 34, 35, 36, 37, 38, 40, 43 };
		for (int i = 0; i < mats.length; i++) {
			addPartWithRecipe(mats[i], 9.0F, false, false, blockName, categoryName, MTEx.MachineBlock, id+ids[i], partClass, additionalTags, OP.plate.dat(mats[i]), recipe1, recipe2, recipe3, prefixItem);
		}
		// Acid proof metals
		mats = new OreDictMaterial[]{ MT.StainlessSteel, MT.Netherite, MT.Thaumium };
		ids = new int[]{ 25, 44, 29 };
		for (int i = 0; i < mats.length; i++) {
			addPartWithRecipe(mats[i], 6.0F, true, false, blockName, categoryName, MTEx.MachineBlock, id+ids[i], partClass, additionalTags, OP.plate.dat(mats[i]), recipe1, recipe2, recipe3, prefixItem);
		}

		mats = new OreDictMaterial[] { MT.Cr, MT.Ir };
		ids = new int[] { 33, 39 };
		for (int i = 0; i < mats.length; i++) {
			addPartWithRecipe(mats[i], 9.0F, true, false, blockName, categoryName, MTEx.MachineBlock, id+ids[i], partClass, additionalTags, OP.plate.dat(mats[i]), recipe1, recipe2, recipe3, prefixItem);
		}

		mats = new OreDictMaterial[] { ANY.W, MT.VoidMetal };
		ids = new int[] { 24, 30 };
		for (int i = 0; i < mats.length; i++) {
			addPartWithRecipe(mats[i], 10.0F, true, false, blockName, categoryName, MTEx.MachineBlock, id+ids[i], partClass, additionalTags, OP.plate.dat(mats[i]), recipe1, recipe2, recipe3, prefixItem);
		}
		mat = MT.Bedrock_HSLA_Alloy; addPartWithRecipe(mat, 100.0F, false, false, blockName, categoryName, MTEx.MachineBlock, id+48, partClass, additionalTags, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);
		mat = MT.Ad                ; addPartWithRecipe(mat, 100.0F, true , false, blockName, categoryName, MTEx.MachineBlock, id+49, partClass, additionalTags, OP.plate.dat(mat), recipe1, recipe2, recipe3, prefixItem);
	}

	public static void addCrucibleMaterial(OreDictMaterial mat, int id, float hardness, boolean acidProof, boolean tapAndFunnel, MultiTileEntityBlock base, OreDictPrefix recipeItem, ILx... containers) {
		OreDictItemData item = recipeItem != null ? recipeItem.dat(mat) : null;
		MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;

		addPartWithRecipe(mat, hardness, acidProof, false, "Crucible Faucet"  , "Crucibles Faucets", base, MTEx.IDs.CrucibleFaucets.get() + id, MultiTileEntityFaucet  .class, new Object[]{}, item, "h y", "P P", " P ", 'P'); if (containers.length > 0) containers[0].set(reg.getItem());
		addPartWithRecipe(mat, hardness, acidProof, false, "Mold"             , "Molds"            , base, MTEx.IDs.Molds          .get() + id, MultiTileEntityMold    .class, new Object[]{}, item, "h y", "P P", "PPP", 'P'); if (containers.length > 1) containers[1].set(reg.getItem());
		addPartWithRecipe(mat, hardness, acidProof, false, "Basin"            , "Molds"            , base, MTEx.IDs.Basins         .get() + id, MultiTileEntityBasin   .class, new Object[]{}, item, "PhP", "PyP", " P ", 'P'); if (containers.length > 2) containers[2].set(reg.getItem());
		addPartWithRecipe(mat, hardness, acidProof, false, "Crucible Crossing", "Molds"            , base, MTEx.IDs.Crossings      .get() + id, MultiTileEntityCrossing.class, new Object[]{}, item, "hPy", "PPP", " P ", 'P'); if (containers.length > 3) containers[3].set(reg.getItem());

		if (tapAndFunnel) {
			item = recipeItem != null ? OP.plateCurved.dat(mat) : null;
			addPartWithRecipe(mat, hardness, acidProof, false, "Funnel"  , "Misc Tool Blocks", base, MTEx.IDs.Taps   .get() + id, MultiTileEntityFluidFunnel.class, new Object[]{}, item, " s ", "PxP", " P ", 'P'); if (containers.length > 4) containers[4].set(reg.getItem());
			addPartWithRecipe(mat, hardness, acidProof, false, "Tap"     , "Misc Tool Blocks", base, MTEx.IDs.Funnels.get() + id, MultiTileEntityFluidTap   .class, new Object[]{}, item, "  s", "PP ", "xP ", 'P'); if (containers.length > 5) containers[5].set(reg.getItem());
		}
	}
}
