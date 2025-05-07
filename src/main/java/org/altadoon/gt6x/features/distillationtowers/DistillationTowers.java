package org.altadoon.gt6x.features.distillationtowers;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.CS.T;

public class DistillationTowers extends GT6XFeature {
	@Override
	public String name() {
		return "DistillationTowers";
	}

	@Override
	public void preInit() {

	}

	@Override
	public void init() {
		OreDictMaterial mat = MT.StainlessSteel;
		MTEx.gt6xMTEReg.add("Distillation Tower"     , "Multiblock Machines", MTEx.IDs.DistTower    .get(), 17101, MultiTileEntityDistillationTowerX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "distillationtower"    , NBT_INPUT, 512, NBT_INPUT_MIN, 1, NBT_INPUT_MAX, 1024, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RM.DistillationTower    , NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_AUTO_OUT, SIDE_BACK, NBT_CHEAP_OVERCLOCKING, T), "PPP", "PMP", "PPP", 'M', MTEx.gt6MTEReg.getItem(18102), 'P', OP.pipeNonuple.dat(mat));
		MTEx.gt6xMTEReg.add("Cryo Distillation Tower", "Multiblock Machines", MTEx.IDs.CryoDistTower.get(), 17101, MultiTileEntityDistillationTowerX.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "cryodistillationtower", NBT_INPUT, 512, NBT_INPUT_MIN, 1, NBT_INPUT_MAX, 1024, NBT_ENERGY_ACCEPTED, TD.Energy.CU, NBT_RECIPEMAP, RM.CryoDistillationTower, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_AUTO_OUT, SIDE_BACK, NBT_CHEAP_OVERCLOCKING, T), "PPP", "PMP", "PPP", 'M', MTEx.gt6MTEReg.getItem(18102), 'P', OP.pipeNonuple.dat(ANY.Cu));
	}

	@Override
	public void postInit() {
		for (short id : new short []{17101, 17111}) {
			MTEx.disableGT6MTE(id);
		}
	}
}
