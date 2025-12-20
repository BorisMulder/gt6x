package org.altadoon.gt6x.features.fusion;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.CR;
import gregapi.util.ST;
import gregapi.util.UT;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;

public class Fusion extends GT6XFeature {
	public static final String FEATURE_NAME = "FusionOverhaul";

	@Override public String name() { return FEATURE_NAME; }

	@Override public void preInit() {}

	@Override public void init() {}

	@Override public void afterGt6Init() {
		addMTEs();
	}

	@Override public void postInit() {
		addRecipes();
	}

	@Override
	public void afterGt6PostInit() {
		changeFusionRecipes();
	}

	private void addMTEs() {
		OreDictMaterial mat;
		mat = MT.Superconductor;
		MTEx.gt6xMTEReg.add("Large Superconductor Coil", "Multiblock Machines", MTEx.IDs.SuperconductorCoil.get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "coil", NBT_DESIGNS, 1), "WWW", "WxW", "WWW", 'W', OP.wireGt04.dat(mat));
		mat = MT.W;
		MTEx.gt6xMTEReg.add("Boronized Tungsten Wall"  , "Multiblock Machines", MTEx.IDs.BWWall            .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_TEXTURE, "metalwall", NBT_DESIGNS, 1));
		mat = MT.SteelGalvanized;
		MTEx.gt6xMTEReg.add("Fusion Reactor"           , "Multiblock Machines", MTEx.IDs.FusionReactor     .get(), 17101, MTEFusionReactor             .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_TEXTURE, "fusionreactor", NBT_INPUT, 8192, NBT_INPUT_MIN, 1, NBT_INPUT_MAX, 16384, NBT_ENERGY_ACCEPTED, TD.Energy.TU, NBT_RECIPEMAP, RM.Fusion, NBT_ENERGY_ACCEPTED_2, TD.Energy.LU, NBT_ENERGY_EMITTED, TD.Energy.EU, NBT_SPECIAL_IS_START_ENERGY, T, NBT_NO_CONSTANT_POWER, T), "CPC", "CSC", "CWC", 'C', ILx.PCs[3], 'P', IL.PUMPS[5], 'S', ILx.LCDMonitor, 'W', MTEx.gt6xMTEReg.getItem(MTEx.IDs.SuperconductorCoil.get()));
	}

	private void addRecipes() {
		// YBCO MOVPE precursors
		RM.Mixer.addRecipe0(false, 16, 128, FL.array(MTx.TMHD.liquid(U, true), MTx.NaOHSolution.liquid(6*U, true)), FL.array(MTx.NaTMHDSolution.liquid(7*U, false)));
		RM.Bath.addRecipe1(true, 0, 96, dust.mat(MTx.YNO33, 4), FL.array(MTx.NaTMHDSolution.liquid(21*U, false)), FL.array(MTx.NaNO3Solution.liquid(8*3*U, false), MT.H2O.liquid(9*U, false)), dust.mat(MTx.YTMHD, 1));
		RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.BaCl2, 1), MTx.NaTMHDSolution.liquid(14*U, false), MT.SaltWater.liquid(16*U, false), dust.mat(MTx.BaTMHD, 1));
		RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.CuCl2, 3), MTx.NaTMHDSolution.liquid(14*U, false), MT.SaltWater.liquid(16*U, false), dust.mat(MTx.CuTMHD, 1));

		for (OreDictMaterial mat : new OreDictMaterial[] {MTx.YTMHD, MTx.BaTMHD, MTx.CuTMHD}) {
			RM.Smelter.addRecipe1(false, 512, 8, dustSmall.mat(mat, 1), NF, mat.gas(U4, false), NI);
			RM.Smelter.addRecipe1(false, 512, 32, dust.mat(mat, 1), NF, mat.gas(U, false), NI);
			RM.Smelter.addRecipe1(false, 512, 32*9, blockDust.mat(mat, 1), NF, mat.gas(9*U, false), NI);
		}

		RM.Mixer.addRecipe1(true, 16, 128, ST.tag(3), FL.array(MTx.YTMHD.gas(U, true), MTx.BaTMHD.gas(2*U, true), MTx.CuTMHD.gas(3*U, true)), FL.array(MTx.YBaCuTMHD.gas(6*U, false)));

		// Superconductors
		RMx.IonBombardment.addRecipeX(false, 64, 64, ST.array(foil.mat(MTx.Hastelloy, 1), dustDiv72.mat(MTx.CeO2, 2), dustDiv72.mat(MTx.YSZ, 1)), MT.Ar.gas(U100, true), NF, ILx.HTSTape_Buffer.get(1));
		RMx.Thermolysis.addRecipe1(false, 32, 256, ILx.HTSTape_Buffer.get(1), FL.array(MTx.YBaCuTMHD.gas(U8, true), MT.O.gas(10*U, true)), FL.array(MT.H2O.liquid(9*U, false), MT.CO2.gas(9*U, false)), ILx.HTSTape_REBCO.get(1));
		RMx.IonBombardment.addRecipeX(false, 64, 64, ST.array(ILx.HTSTape_REBCO.get(1), dustTiny.mat(MT.Ag, 1), dustTiny.mat(MT.Cu, 1)), MT.Ar.gas(U100, true), NF, ILx.HTSTape_AgCu.get(1));
		RM.Bath.addRecipe2(false, 0, 128, ILx.HTSTape_AgCu.get(1), foil.mat(MTx.Kapton, 1), MTx.EpoxyResin.liquid(U4, true), NF, ILx.HTSTape_Insulated.get(1));
		/// Thermal/electrical multi-layer insulation (MLI)
		RM.Bath.addRecipe1(true, 0, 64, foil.mat(MTx.PET, 1), MT.Al.gas(U500, true), NF, foil.mat(MTx.MetallisedBoPET, 1));
		RM.Laminator.addRecipe2(true, 16, 32, foil.mat(MTx.MetallisedBoPET, 5), ILx.FiberglassScrim.get(4), ILx.MLIBlanket.get(1));
		/// Vacuum pipes around it:
		CR.shaped(ILx.SuperconductorEmpty.get(1), CR.DEF_REV, "PF ", "ATB", "xF ", 'A', pipeTiny.dat(MT.StainlessSteel), 'B', pipeSmall.dat(MT.StainlessSteel), 'F', ILx.MLIBlanket, 'P', IL.PUMPS[4], 'T', ILx.HTSTape_Insulated);
		/// Coolant:
		RM.Freezer.addRecipe1(true, 128, 32, ST.tag(0), MT.N.gas(U, true), MT.N.liquid(U, false), NI);
		RM.Canner.addRecipe1(false, 128, 16, ILx.SuperconductorEmpty.get(1), MT.N.liquid(U9, true), NF, wireGt04.mat(MT.Superconductor, 1));

		// Boronization of Tungsten Walls
		RMx.VacuumChamber.addRecipe1(false, 32, 256, MTEx.gt6MTEReg.getItem(18004), MTx.B2H6.gas(U100, true), NF, MTEx.gt6xMTEReg.getItem(MTEx.IDs.BWWall.get()));
	}

	private void changeFusionRecipes() {
		RM.Fusion.mRecipeList.clear();
		RM.Fusion.mRecipeItemMap.clear();
		RM.Fusion.mRecipeFluidMap.clear();

		// Terrestrial fusion candidates
		RM.Fusion.addRecipe1(false, -8192,  730, ST.tag(1), FL.array(MT.D     .plasma(U*2, true)                           ), FL.array(MT.He_3  .plasma(  U2, false), MT.T     .plasma(  U2, false)                                                  ), ZL_IS             ).setSpecialNumber(  730L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1130, ST.tag(1), FL.array(MT.T     .plasma(U*2, true)                           ), FL.array(MT.He    .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1130L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1290, ST.tag(1), FL.array(MT.He_3  .plasma(U*2, true)                           ), FL.array(MT.He    .plasma(  U , false), MT.H     .plasma(2*U , false)                                                  ), ZL_IS             ).setSpecialNumber( 1290L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1000, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.Li_6.plasma(U  , true)), FL.array(MT.Be_7  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1000L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  800, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.Li  .plasma(U  , true)), FL.array(MT.Be_8  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  800L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  546, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.B_11.plasma(U  , true)), FL.array(MT.He    .plasma(3*U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 8469L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  315, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.C   .plasma(U  , true)), FL.array(MT.C_13  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  315L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  754, ST.tag(2), FL.array(MT.H     .plasma(U  , true), MT.C_13.plasma(U  , true)), FL.array(MT.N     .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  754L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1404, ST.tag(2), FL.array(MT.H     .plasma(U*2, true), MT.N   .plasma(U  , true)), FL.array(MT.He    .plasma(  U2, false), MT.C     .plasma(  U2, false), MT.O     .plasma(  U2, false)                   ), ZL_IS             ).setSpecialNumber( 1404L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  455, ST.tag(2), FL.array(MT.H     .plasma(U*2, true), MT.O   .plasma(U  , true)), FL.array(MT.He    .plasma(  U2, false), MT.F     .gas   (  U2, false), MT.N     .plasma(  U2, false)                   ), ZL_IS             ).setSpecialNumber(  455L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1760, ST.tag(2), FL.array(MT.D     .plasma(U  , true), MT.T   .plasma(U  , true)), FL.array(MT.He    .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1760L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1830, ST.tag(2), FL.array(MT.D     .plasma(U  , true), MT.He_3.plasma(U  , true)), FL.array(MT.He    .plasma(  U , false), MT.H     .plasma(  U , false)                                                  ), ZL_IS             ).setSpecialNumber( 1830L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 2640, ST.tag(2), FL.array(MT.T     .plasma(U  , true), MT.He_3.plasma(U  , true)), FL.array(MT.He    .plasma(3*U4, false), MT.D     .plasma(  U4, false)                                                  ), ZL_IS             ).setSpecialNumber( 2640L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 3336, ST.tag(2), FL.array(MT.D     .plasma(U  , true), MT.Li_6.plasma(U  , true)), FL.array(MT.He    .plasma(3*U8, false), MT.He_3  .plasma(  U8, false), MT.Li    .plasma(  U8, false), MT.Be_7  .plasma(  U8, false)), ZL_IS ).setSpecialNumber( 3336L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192, 1690, ST.tag(2), FL.array(MT.He_3  .plasma(U  , true), MT.Li_6.plasma(U  , true)), FL.array(MT.He    .plasma(2*U , false), MT.H     .plasma(  U , false)                                                  ), ZL_IS             ).setSpecialNumber( 1690L*8192L*16L);

		// Triple-alpha
		RM.Fusion.addRecipe1(false, 0    , 1890, ST.tag(1), FL.array(MT.He    .plasma(U*2, true)                           ), FL.array(MT.Be_8  .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber( 1890L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  736, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Be_8.plasma(U  , true)), FL.array(MT.C     .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  736L*8192L*16L);

		// alpha capture process
		RM.Fusion.addRecipe1(false, -8192,  716, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.C   .plasma(U  , true)), FL.array(MT.O     .plasma(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  716L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  473, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.O   .plasma(U  , true)), FL.array(MT.Ne    .gas   (  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  473L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  932, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ne  .gas   (U  , true)), FL.array(MT.Mg    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  932L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  998, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Mg  .gas   (U  , true)), FL.array(MT.Si    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  998L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  695, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Si  .liquid(U  , true)), FL.array(MT.S     .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  695L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  664, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.S   .liquid(U  , true)), FL.array(MT.Ar    .gas   (  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  664L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  704, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ar  .gas   (U  , true)), FL.array(MT.Ca    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  704L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  513, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ca  .liquid(U  , true)), FL.array(MT.Ti    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  513L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  770, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Ti  .liquid(U  , true)), FL.array(MT.Cr    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  770L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  794, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Cr  .liquid(U  , true)), FL.array(MT.Fe    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  794L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  800, ST.tag(2), FL.array(MT.He    .plasma(U  , true), MT.Fe  .liquid(U  , true)), FL.array(MT.Ni    .liquid(  U , false)                                                                                 ), ZL_IS             ).setSpecialNumber(  800L*8192L*16L);

		// Burning carbon, oxygen
		RM.Fusion.addRecipe1(false, -8192,  685, ST.tag(1), FL.array(MT.C     .plasma(U*2, true)), FL.array(MT.Na.liquid(U4, false), MT.Ne.gas(U4, false), MT.Mg.gas(U4, false), MT.O.plasma(U4, false), MT.He.plasma(3*U4, false), MT.H.plasma(U4, false)), ZL_IS).setSpecialNumber( 685L*8192L*16L);
		RM.Fusion.addRecipe1(false, -8192,  807, ST.tag(1), FL.array(MT.O     .plasma(U*2, true)), FL.array(MT.Si.liquid(U4, false), MT.P .gas(U4, false), MT.Mg.gas(U4, false), MT.S.liquid(U4, false), MT.He.plasma(3*U4, false), MT.H.plasma(U4, false)), ZL_IS).setSpecialNumber( 807L*8192L*16L);

		// Vibranium
		RM.Fusion.addRecipe1(false, -8192, 1956, ST.tag(2), FL.array(MT.Ad    .liquid(U  , true), MT.Be_7.plasma(U  , true)), FL.array(MT.W     .liquid(  U , false), MT.He.plasma(16*U, false), MT.He_3.plasma(24*U, false),MT.T.plasma(24*U, false)), dust.mat(MT.Vb, 1)).setSpecialNumber(94956L*8192L*16L);
	}
}
