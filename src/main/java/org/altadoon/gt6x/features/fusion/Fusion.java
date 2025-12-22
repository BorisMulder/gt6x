package org.altadoon.gt6x.features.fusion;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.*;
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
		removeGt6Fusion();
	}

	private void addMTEs() {
		OreDictMaterial mat;
		mat = MT.Superconductor;
		MTEx.gt6xMTEReg.add("Large Superconductor Coil", "Multiblock Machines", MTEx.IDs.SuperconductorCoil.get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "coil", NBT_DESIGNS, 4), "WWW", "WxW", "WWW", 'W', OP.wireGt04.dat(mat));
		mat = MTx.BoronizedW;
		MTEx.gt6xMTEReg.add("Boronized Tungsten Wall"  , "Multiblock Machines", MTEx.IDs.BWWall            .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_TEXTURE, "metalwall", NBT_DESIGNS, 1));
		OM.data(MTEx.gt6xMTEReg.getItem(), mat, U*4);
		mat = MT.SteelGalvanized;
		MTEx.gt6xMTEReg.add("Fusion Reactor"           , "Multiblock Machines", MTEx.IDs.FusionReactor     .get(), 17101, MTEFusionReactor             .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "fusionreactor", NBT_INPUT, 8192, NBT_INPUT_MIN, 1, NBT_INPUT_MAX, 16384, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RMx.Fusion, NBT_ENERGY_ACCEPTED_2, TD.Energy.LU, NBT_SPECIAL_IS_START_ENERGY, T, NBT_NO_CONSTANT_POWER, T), "CPC", "CSC", "CWC", 'C', ILx.PCs[2], 'P', IL.PUMPS[5], 'S', ILx.LCDMonitor, 'W', MTEx.gt6xMTEReg.getItem(MTEx.IDs.SuperconductorCoil.get()));
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
		CR.shaped(ILx.SuperconductorEmpty.get(1), CR.DEF_REV, " F ", "ATB", "xF ", 'A', pipeTiny.dat(MT.StainlessSteel), 'B', pipeSmall.dat(MT.StainlessSteel), 'F', ILx.MLIBlanket, 'T', ILx.HTSTape_Insulated);
		/// Coolant:
		RM.Freezer.addRecipe1(true, 128, 32, ST.tag(0), MT.N.gas(U, true), MT.N.liquid(U, false), NI);
		RM.Canner.addRecipe1(false, 128, 16, ILx.SuperconductorEmpty.get(1), MT.N.liquid(U9, true), NF, wireGt04.mat(MT.Superconductor, 1));

		// Boronization of Tungsten Walls
		RMx.VacuumChamber.addRecipe1(false, 32, 256, MTEx.gt6MTEReg.getItem(18004), MTx.B2H6.gas(U100, true), NF, MTEx.gt6xMTEReg.getItem(MTEx.IDs.BWWall.get()));

		// Fusion Recipes

		// Terrestrial fusion candidates
		RMx.Fusion.addRecipe1(false, 8192, 2* 7, ST.tag(1), FL.array(MT.N.liquid(U100* 14, true), MT.D   .gas   (U1000*4, true)                               ), FL.array(MT.N.gas(U100* 14, true), MT.He_3.gas   (  U1000, false), MT.T     .gas(  U1000, false), MT.H.gas(  U1000, false)                                       ), ZL_IS).setSpecialNumber(  730L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   11, ST.tag(1), FL.array(MT.N.liquid(U100* 11, true), MT.T   .gas   (U1000*2, true)                               ), FL.array(MT.N.gas(U100* 11, true), MT.He  .gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber( 1130L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   13, ST.tag(1), FL.array(MT.N.liquid(U100* 13, true), MT.He_3.gas   (U1000*2, true)                               ), FL.array(MT.N.gas(U100* 13, true), MT.He  .gas   (  U1000, false), MT.H     .gas(2*U1000, false)                                                                 ), ZL_IS).setSpecialNumber( 1290L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   72, ST.tag(2), FL.array(MT.N.liquid(U100* 72, true), MT.H   .gas   (U144   , true), MT.Li_6.liquid(U144   , true)), FL.array(MT.N.gas(U100* 72, true), MT.Be_7.liquid(  U144 , false)                                                                                                ), ZL_IS).setSpecialNumber( 1000L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   50, ST.tag(2), FL.array(MT.N.liquid(U100* 50, true), MT.H   .gas   (U144   , true), MT.Li  .liquid(U144   , true)), FL.array(MT.N.gas(U100* 50, true), MT.He  .gas   (2*U144 , false)                                                                                                ), ZL_IS).setSpecialNumber(  800L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   80, ST.tag(2), FL.array(MT.N.liquid(U100* 80, true), MT.H   .gas   (U144   , true), MT.B_11.liquid(U144   , true)), FL.array(MT.N.gas(U100* 80, true), MT.He  .gas   (3*U144 , false)                                                                                                ), ZL_IS).setSpecialNumber( 8469L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,    3, ST.tag(2), FL.array(MT.N.liquid(U100*  3, true), MT.H   .gas   (U1000  , true), MT.C   .gas   (U1000  , true)), FL.array(MT.N.gas(U100*  3, true), MT.C_13.gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber(  315L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   14, ST.tag(2), FL.array(MT.N.liquid(U100* 14, true), MT.H   .gas   (U1000*2, true), MT.N   .gas   (U1000  , true)), FL.array(MT.N.gas(U100* 14, true), MT.He  .gas   (  U1000, false), MT.C     .gas(  U1000, false)                                                                 ), ZL_IS).setSpecialNumber( 1404L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,    4, ST.tag(2), FL.array(MT.N.liquid(U100*  4, true), MT.H   .gas   (U1000*2, true), MT.O   .gas   (U1000  , true)), FL.array(MT.N.gas(U100*  4, true), MT.He  .gas   (  U1000, false), MT.N     .gas(  U1000, false)                                                                 ), ZL_IS).setSpecialNumber(  455L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   18, ST.tag(2), FL.array(MT.N.liquid(U100* 18, true), MT.D   .gas   (U1000  , true), MT.T   .gas   (U1000  , true)), FL.array(MT.N.gas(U100* 18, true), MT.He  .gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber( 1760L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,   18, ST.tag(2), FL.array(MT.N.liquid(U100* 18, true), MT.D   .gas   (U1000  , true), MT.He_3.gas   (U1000  , true)), FL.array(MT.N.gas(U100* 18, true), MT.He  .gas   (  U1000, false), MT.H     .gas(  U1000 , false)                                                                ), ZL_IS).setSpecialNumber( 1830L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192, 4*26, ST.tag(2), FL.array(MT.N.liquid(U100*104, true), MT.T   .gas   (U1000*4, true), MT.He_3.gas   (U1000*4, true)), FL.array(MT.N.gas(U100*104, true), MT.He  .gas   (3*U1000, false), MT.D     .gas(  U1000, false)                                                                 ), ZL_IS).setSpecialNumber( 2640L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,  180, ST.tag(2), FL.array(MT.N.liquid(U100*180, true), MT.D   .gas   (U144 *8, true), MT.Li_6.liquid(U144 *8, true)), FL.array(MT.N.gas(U100*180, true), MT.He  .gas   (3*U144 , false), MT.He_3  .gas(  U144 , false), MT.Li  .liquid(  U144 , false), MT.Be_7.liquid(  U144 , false) ), ZL_IS).setSpecialNumber( 3336L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,  160, ST.tag(2), FL.array(MT.N.liquid(U100*160, true), MT.He_3.gas   (U144   , true), MT.Li_6.liquid(U144   , true)), FL.array(MT.N.gas(U100*160, true), MT.He  .gas   (2*U144 , false), MT.H     .gas(  U144 , false)                                                                 ), ZL_IS).setSpecialNumber( 1690L*8192L*16L);

		// Triple-alpha
		RMx.Fusion.addRecipe1(false, 8192,    7, ST.tag(1), FL.array(MT.N.liquid(U100*  7, true), MT.He  .gas   (U1000*3, true)                               ), FL.array(MT.C   .gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber(736L*8192L*16L);

		// alpha capture process
		RMx.Fusion.addRecipe1(false, 8192,    7, ST.tag(2), FL.array(MT.N.liquid(U100*   7, true), MT.He  .gas   (U1000  , true), MT.C   .gas   (U1000  , true)), FL.array(MT.N.gas(U100*  7, true), MT.O   .gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber(  716L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,    5, ST.tag(2), FL.array(MT.N.liquid(U100*   5, true), MT.He  .gas   (U1000  , true), MT.O   .gas   (U1000  , true)), FL.array(MT.N.gas(U100*  5, true), MT.Ne  .gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber(  473L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,    9, ST.tag(2), FL.array(MT.N.liquid(U100*   9, true), MT.He  .gas   (U1000  , true), MT.Ne  .gas   (U1000  , true)), FL.array(MT.N.gas(U100*  9, true), MT.Mg  .gas   (  U1000, false)                                                                                                ), ZL_IS).setSpecialNumber(  932L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192, 1250, ST.tag(2), FL.array(MT.N.liquid(U8  *1000, true), MT.He  .gas   (U8     , true), MT.Mg  .liquid(U8     , true)), FL.array(MT.N.gas(U8  *1000, true), MT.Si  .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  998L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,  875, ST.tag(2), FL.array(MT.N.liquid(U8  * 700, true), MT.He  .gas   (U8     , true), MT.Si  .liquid(U8     , true)), FL.array(MT.N.gas(U8  * 700, true), MT.S   .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  695L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,  875, ST.tag(2), FL.array(MT.N.liquid(U8  * 700, true), MT.He  .gas   (U8     , true), MT.S   .liquid(U8     , true)), FL.array(MT.N.gas(U8  * 700, true), MT.Ar  .gas   (  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  664L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,  875, ST.tag(2), FL.array(MT.N.liquid(U8  * 700, true), MT.He  .gas   (U8     , true), MT.Ar  .gas   (U8     , true)), FL.array(MT.N.gas(U8  * 700, true), MT.Ca  .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  704L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192,  625, ST.tag(2), FL.array(MT.N.liquid(U8  * 500, true), MT.He  .gas   (U8     , true), MT.Ca  .liquid(U8     , true)), FL.array(MT.N.gas(U8  * 500, true), MT.Ti  .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  513L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192, 1000, ST.tag(2), FL.array(MT.N.liquid(U   *  10, true), MT.He  .gas   (U8     , true), MT.Ti  .liquid(U8     , true)), FL.array(MT.N.gas(U   *  10, true), MT.Cr  .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  770L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192, 1000, ST.tag(2), FL.array(MT.N.liquid(U   *  10, true), MT.He  .gas   (U8     , true), MT.Cr  .liquid(U8     , true)), FL.array(MT.N.gas(U   *  10, true), MT.Fe  .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  794L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192, 1000, ST.tag(2), FL.array(MT.N.liquid(U   *  10, true), MT.He  .gas   (U8     , true), MT.Fe  .liquid(U8     , true)), FL.array(MT.N.gas(U   *  10, true), MT.Ni  .liquid(  U8   , false)                                                                                                ), ZL_IS).setSpecialNumber(  800L*8192L*16L);

		// Burning carbon, oxygen
		RMx.Fusion.addRecipe1(false, 8192, 3500, ST.tag(1), FL.array(MT.N.liquid(U2  *  70, true), MT.C   .gas   (U      , true)), FL.array(MT.N.gas(U2  *  70, true), MT.Na.liquid(U8, false), MT.Ne.gas(U8, false), MT.Mg.gas(U8, false), MT.O.gas   (U8, false), MT.He.gas(3*U8, false)), ZL_IS).setSpecialNumber(  685L*8192L*16L);
		RMx.Fusion.addRecipe1(false, 8192, 4000, ST.tag(1), FL.array(MT.N.liquid(U   *  40, true), MT.O   .gas   (U      , true)), FL.array(MT.N.gas(U2  *  70, true), MT.Si.liquid(U8, false), MT.P .gas(U8, false), MT.Mg.gas(U8, false), MT.S.liquid(U8, false), MT.He.gas(3*U8, false)), ZL_IS).setSpecialNumber(  807L*8192L*16L);

		// Vibranium
		RMx.Fusion.addRecipe1(false, 8192, 2000, ST.tag(2), FL.array(MT.N.liquid(U    * 20, true), MT.Ad  .liquid(U      , true), MT.Be_7.liquid(U      , true)), FL.array(MT.W   .liquid(U, false), MT.He.gas(16*U, false), MT.He_3.gas(24*U, false), MT.T.gas(24*U, false)), dust.mat(MT.Vb, 1)).setSpecialNumber(94956L*8192L*16L);
	}

	private void removeGt6Fusion() {
		RM.Fusion.mRecipeList.clear();
		RM.Fusion.mRecipeItemMap.clear();
		RM.Fusion.mRecipeFluidMap.clear();
		ItemStack fusionReactor = MTEx.gt6MTEReg.getItem(17198);
		CRx.disableGt6(fusionReactor);
		ST.hide(fusionReactor);
	}
}
