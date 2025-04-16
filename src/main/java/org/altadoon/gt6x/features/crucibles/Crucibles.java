package org.altadoon.gt6x.features.crucibles;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.CR;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.tileentity.tools.MultiTileEntityFaucet;
import gregtech.tileentity.tools.MultiTileEntityMold;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.CRx;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.crucibles.recipes.CrucibleUtils;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.MTEx.*;

public class Crucibles extends GT6XFeature {
	@Override public String name() { return "Crucibles"; }
	@Override public void preInit() {}
	@Override public void init() {}
	@Override public void postInit() {}

	@Override
	public void afterGt6Init() {
		RM.CrucibleSmelting.mRecipeMachineList.clear();
		RM.CrucibleAlloying.mRecipeMachineList.clear();
		RMx.Bessemer.fakeRecipes.mRecipeMachineList.clear();

		MultiTileEntityMold.HEAT_RESISTANCE_BONUS = 1.0;
		MultiTileEntityFaucet.HEAT_RESISTANCE_BONUS = 1.0;

		// Change all crucibles to the new MTE class
		CrucibleUtils.addCruciblePart(
			MultiTileEntitySmallCrucible.class,
			"Crucible",
			"Smelting Crucibles",
			MTEx.IDs.Crucibles.get(),
			IL.Ceramic_Crucible, ILx.Fireclay_Crucible,
			"PhP", "PwP", "PPP", 'P',
			NBT_RECIPEMAP, RM.CrucibleAlloying , NBT_ENERGY_ACCEPTED, TD.Energy.HU
		);

		// Large crucibles
		OreDictMaterial mat; int id = MTEx.IDs.LargeCrucibles.get();
		mat = MT.Steel;          MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+9 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18009                  , NBT_ACIDPROOF, false, NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18009));
		mat = MT.StainlessSteel; MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+2 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18002                  , NBT_ACIDPROOF, true , NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18002));
		mat = MT.Invar;          MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+7 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18007                  , NBT_ACIDPROOF, false, NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18007));
		mat = MT.Ti;             MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+6 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18006                  , NBT_ACIDPROOF, false, NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18006));
		mat = MT.TungstenSteel;  MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+3 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18003                  , NBT_ACIDPROOF, false, NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18003));
		mat = ANY.W;             MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+4 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18004                  , NBT_ACIDPROOF, true , NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18004));
		mat = MT.Ta4HfC5;        MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+12, 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18012                  , NBT_ACIDPROOF, false, NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18012));
		mat = MT.Ad;             MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+5 , 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 100.0F, NBT_RESISTANCE, 100.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6MTERegId , NBT_DESIGN, 18005                  , NBT_ACIDPROOF, true , NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6MTEReg.getItem(18005));
		mat = MT.SiC;            MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+15, 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6xMTERegId, NBT_DESIGN, MTEx.IDs.SiCWall .get(), NBT_ACIDPROOF, false, NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6xMTEReg.getItem(MTEx.IDs.SiCWall.get()));
		mat = MTx.MgOC;          MTEx.gt6xMTEReg.add("Large " + mat.getLocal() + " Crucible", "Multiblock Machines", id+16, 17101, MultiTileEntityLargeCrucible.class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   8.0F, NBT_RESISTANCE,   8.0F, NBT_TEXTURE, "crucible", NBT_MTE_MULTIBLOCK_PART_REG, gt6xMTERegId, NBT_DESIGN, MTEx.IDs.MgOCWall.get(), NBT_ACIDPROOF, true , NBT_RECIPEMAP, RM.CrucibleAlloying), "hMy", 'M', MTEx.gt6xMTEReg.getItem(MTEx.IDs.MgOCWall.get()));

		RM.CrucibleAlloying     .mRecipeMachineList.addAll(RMx.EAF.fakeRecipes.mRecipeMachineList);
		RM.CrucibleSmelting     .mRecipeMachineList.addAll(RM.CrucibleAlloying.mRecipeMachineList);
		RMx.SSS     .fakeRecipes.mRecipeMachineList.addAll(RM.CrucibleAlloying.mRecipeMachineList);
		RMx.Thermite.fakeRecipes.mRecipeMachineList.addAll(RM.CrucibleAlloying.mRecipeMachineList);
		RMx.Bessemer.fakeRecipes.mRecipeMachineList.addAll(RM.CrucibleAlloying.mRecipeMachineList);
		RMx.Bessemer.fakeRecipes.mRecipeMachineList.addAll(RMx.BOF.fakeRecipes.mRecipeMachineList);
	}

	private void disableGt6Crucible(short id) {
		MTEx.disableGT6MTE(id);
		ItemStack crucible = gt6MTEReg.getItem(id);
		if (ST.valid(crucible)) {
			CR.shapeless(gt6xMTEReg.getItem(id), new Object[]{crucible});
		}
	}

	@Override public void afterGt6PostInit() {
		// Disable gt6 crucibles, allow to craft into gt6x
		for (short id = 1000; id < 1050; id++) disableGt6Crucible(id);
		for (short id = 17300; id < 17313; id++) disableGt6Crucible(id);

		RM.rem_smelting(IL.Ceramic_Crucible_Raw.get(1), gt6MTEReg.getItem(1005));
		RM.add_smelting(IL.Ceramic_Crucible_Raw.get(1), gt6xMTEReg.getItem(IDs.Crucibles.get()+5));

		// Override recipes requiring crucibles
		OreDictMaterial mat;
		// Crystallisation crucible
		mat = MT.DATA.Heat_T[1]; CRx.overrideShaped(gt6MTEReg.getItem(20251), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+18), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
		mat = MT.DATA.Heat_T[2]; CRx.overrideShaped(gt6MTEReg.getItem(20252), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+39), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
		mat = MT.DATA.Heat_T[3]; CRx.overrideShaped(gt6MTEReg.getItem(20253), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+39), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
		mat = MT.DATA.Heat_T[4]; CRx.overrideShaped(gt6MTEReg.getItem(20254), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachineDouble.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+39), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));

		// Smelter
		mat = MT.DATA.Heat_T[1]; CRx.overrideShaped(gt6MTEReg.getItem(20241), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachine.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+24), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
		mat = MT.DATA.Heat_T[2]; CRx.overrideShaped(gt6MTEReg.getItem(20242), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachine.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+19), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
		mat = MT.DATA.Heat_T[3]; CRx.overrideShaped(gt6MTEReg.getItem(20243), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachine.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+19), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
		mat = MT.DATA.Heat_T[4]; CRx.overrideShaped(gt6MTEReg.getItem(20244), CR.DEF, "wUh", "PMP", "BCB", 'M', OP.casingMachine.dat(mat), 'U', gt6xMTEReg.getItem(IDs.Crucibles.get()+43), 'C', OP.plateDouble.dat(ANY.Cu), 'B', Blocks.brick_block, 'P', OP.pipeMedium.dat(mat));
	}
}
