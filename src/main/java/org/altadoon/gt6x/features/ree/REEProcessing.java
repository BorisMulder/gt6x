package org.altadoon.gt6x.features.ree;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.IRecipeMapHandler;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerMaterial;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.worldgen.WorldgenObject;
import gregapi.worldgen.WorldgenOresLarge;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorRodBreeder;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorRodDepleted;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorRodNuclear;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorRodProduct;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.features.GT6XFeature;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.data.TD.Compounds.COATED;
import static org.altadoon.gt6x.common.Log.LOG;

public class REEProcessing  extends GT6XFeature {
	public static final String FEATURE_NAME = "REEProcessing";

	@Override public String name() { return FEATURE_NAME; }
	@Override public void preInit() {}
	@Override public void afterGt6PreInit() {
		changeMaterialProperties();
		changeByProducts();
	}

	@Override public void beforeGt6Init() {
		changeItemRecipes();
	}

	@Override
	public void init() {
		addRecipes();
	}

	@Override public void afterGt6Init() {
		changeMTEs();
	}

	@Override
	public void postInit() {
		overrideWorldgen();
	}

	@Override
	public void afterGt6PostInit() {
		changeRecipes();
	}

	private void changeMaterialProperties() {
		MT.OREMATS.Bastnasite.remove(TD.Processing.ELECTROLYSER);
		MT.Monazite.remove(TD.Processing.ELECTROLYSER);

		MT.OREMATS.Uraninite.setSmelting(MT.OREMATS.Uraninite, U);
		MT.OREMATS.Pitchblende.setSmelting(MT.OREMATS.Pitchblende, U);
	}

	private void addRecipes() {
		RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.Ce, 1), MT.O.gas(2*U, true), NF, dust.mat(MTx.CeO2, 1));
		RMx.Thermolysis.addRecipe1(true, 128, 64, dust.mat(MTx.CeO2, 1), NF, MT.O.gas(U2, false), dust.mat(MTx.Ce2O3, 1));
		RM.Roasting.addRecipe1(true, 16, 16, dust.mat(MTx.Ce2O3, 1), MT.O.gas(U2, true), NF, dust.mat(MTx.CeO2, 1));
		RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.La2O3, 2), MT.HNO3.liquid(30*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.LaNO3, 20));
		RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.CeO2, 1), FL.array(MT.HNO3.liquid(30*U, true)), FL.array(MTx.NitratoCericAcid.liquid(27*U, false), MT.H2O.liquid(6*U, false)));
		RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.Ce2O3, 2), MT.H2S.gas(9*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.Ce2S3, 2));
		RM.Bath.addRecipe1(true, 0, 128, dust.mat(MTx.Y2O3, 1), MT.HNO3.liquid(3*5*U, true), MT.H2O.liquid(3*U2, false), dust.mat(MTx.YNO33, 4));

		// Bastnasite
		RMx.Thermolysis.addRecipe2(true, 32, 128, dust.mat(MT.OREMATS.Bastnasite, 12), dust.mat(MTx.CaO, 2), ZL_FS, FL.array(MT.CaF2.liquid(3*U, false), MT.CO2.gas(6*U, false)), dust.mat(MTx.REE2O3, 2));
		RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.REE2O3, 1), MT.O.gas(U10, true), NF, dust.mat(MTx.REORoasted, 1));
		RM.Bath.addRecipe1(true, 0, 200, dust.mat(MTx.REORoasted, 5), FL.array(MTx.ConcHCl.liquid(30*U, true)), FL.array(MTx.REECl3Solution.liquid(26*U, false), MT.H2O.liquid(6*U, false)), dust.mat(MTx.CeO2, 3));

		// Monazite, Xenotime
		RM.Bath.addRecipe1(true, 0, 600, dust.mat(MT .Monazite, 5*6), FL.array(MTx.NaOHSolution.liquid(15*6*U, true)), FL.array(MTx.Na3PO4Solution.liquid(5*14*U, false), MT.H2O.liquid(15*U, false), MT.He.gas(15*U, false)), dust.mat(MTx.REEHydroxide, 35));
		RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.Xenotime, 5*6), FL.array(MTx.NaOHSolution.liquid(15*6*U, true)), FL.array(MTx.Na3PO4Solution.liquid(5*14*U, false), MT.H2O.liquid(15*U, false), MT.He.gas(15*U, false)), dust.mat(MTx.HREEHydroxide, 35));
		RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.REEHydroxide, 35), FL.array(MTx.ConcHCl.liquid(60*U, true)), FL.array(MTx.REECl3Solution.liquid(52*U, false), MT.H2O.liquid(13*3*U, false)), dust.mat(MTx.ThO2, 1)); // + 1 H from Th oxidation
		RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.HREEHydroxide, 35), FL.array(MTx.ConcHCl.liquid(60*U, true)), FL.array(MTx.HREECl3Solution.liquid(52*U, false), MT.H2O.liquid(13*3*U, false)), dust.mat(MT.OREMATS.Uraninite, 1)); // + 1 H from U oxidation

		// EDTA
		for (FluidStack water : FL.waters(12000)) {
			RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.NaOH, 6), FL.array(MTx.EDC.liquid(U, true), water, MT.NH3.gas(2 * U, true)), FL.array(MTx.EthyleneDiamine.liquid(U, false), MT.SaltWater.liquid(16 * U, false)));
			RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.NaCN, 12), FL.array(water, MTx.EthyleneDiamine.liquid(U, true), MTx.Formaldehyde.gas(4*U, true)), FL.array(MT.NH3.gas(4*U, false)), dust.mat(MTx.Na4EDTA, 1));
		}
		RM.Mixer.addRecipe1(true, 16, 64, ST.tag(3), FL.array(MTx.NaCNSolution.liquid(24*U, true), MTx.EthyleneDiamine.liquid(U, true), MTx.Formaldehyde.gas(4*U, true)), FL.array(MT.NH3.gas(4*U, false)), dust.mat(MTx.Na4EDTA, 1));
		RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.Na4EDTA, 1), MTx.DiluteHCl.liquid(32*U, true), MT.SaltWater.liquid(32*U, false), dust.mat(MTx.EDTA, 1));
		RM.Mixer.addRecipe1(true, 16, 32, ST.tag(2), FL.array(MTx.EDTASolution.liquid(4*U, true), MT.NH3.gas(3*U, true)), FL.array(MTx.NH4EDTASolution.liquid(4*U, false)));
		for (FluidStack water : FL.waters(3000))
			RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.EDTA, 1), FL.array(water, MT.NH3.gas(3*U, true)), FL.array(MTx.NH4EDTASolution.liquid(4*U, false)));

		// IX of REE
		RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.H, 3), FL.array(MTx.REECl3Solution.liquid(13*U, true)), FL.array(MTx.ConcHCl.liquid(15*U, false)), OPx.cationXResin.mat(MT.RareEarth, 3));
		RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.H, 3), FL.array(MTx.HREECl3Solution.liquid(13*U, true)), FL.array(MTx.ConcHCl.liquid(15*U, false)), OPx.cationXResin.mat(MTx.HREE, 3));

		RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.RareEarth, 20), MTx.NH4EDTASolution.liquid(4*U, true), FLx.ReeEdta(MTx.HREE, 4000), OPx.cationXResin.mat(MT.Gd, 20));
		RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.H, 3), FLx.ReeEdta(MTx.HREE, 4000), MTx.EDTASolution.liquid(4*U, false), OPx.cationXResin.mat(MT.Lu, 3));

		OreDictMaterial[] ree = {MT.Lu, MT.Yb, MT.Tm, MT.Er, MT.Ho, MT.Y, MT.Dy, MT.Tb, MT.Gd, MT.Eu, MT.Sm, MT.Nd, MT.Pr, MT.Ce, MT.La};
		int[] ratios = {1, 1, 1, 2, 1, 10, 3, 1, 1, 1, 1, 4, 1, 7, 5};
		for (int i = 0; i < ree.length; i++) {
			OreDictMaterial next;
			if (ree[i] == MT.Tb || ree[i] == MT.La) {
				next = MTx.NH4;
			} else {
				next = ree[i+1];
			}
			int n = ratios[i];
			// Ion Exchange Chromatography - Elution using ammonium EDTA
			RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(ree[i], 20), MTx.NH4EDTASolution.liquid(n*4*U, true), FLx.ReeEdta(ree[i], n*4000), OPx.cationXResin.mat(next, 20));
			// Precipitation
			RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.OxalicAcid, 24), FLx.ReeEdta(ree[i], 8000), MTx.EDTASolution.liquid(8*U, false), dust.mat(MTx.REE_OXALATES.get(ree[i]), 20));
			// Decomposition of oxalate to oxide
			RMx.Thermolysis.addRecipe1(true, 16, 96, dust.mat(MTx.REE_OXALATES.get(ree[i]), 20), ZL_FS, FL.array(MT.CO.gas(6*U, false), MT.CO2.gas(9*U, false)), dust.mat(MTx.REE_TRIOXIDES.get(ree[i]), 2));
			// Oxidation
			if (ree[i] != MT.Ce) {
				RM.Roasting.addRecipe1(true, 16, 128, dust.mat(ree[i], 1), MT.O.gas(3*U2, true), NF, dust.mat(MTx.REE_TRIOXIDES.get(ree[i]), 1));
			}
			// Reduction
			if (ree[i] == MT.Sm || ree[i] == MT.Eu || ree[i] == MT.Tm || ree[i] == MT.Yb) {
				// Lanthanothermic reduction of Sm, Eu, Tm, Yb (produces gases)
				RMx.Thermite.addRecipe(0, 2, OMx.stacks(MTx.REE_TRIOXIDES.get(ree[i]), U, MT.La, U), 1500+C, OM.stack(ree[i], U), OM.stack(MTx.La2O3, U));
			} else {
				// Calcinothermic reduction of other REE
				RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.REE_TRIOXIDES.get(ree[i]), 2), MT.HF.gas(12*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.REE_FLUORIDES.get(ree[i]), 2));
				RMx.Thermite.addRecipe(0, 3, OMx.stacks(MTx.REE_FLUORIDES.get(ree[i]), 2*U, MT.Ca, 3*U), 1500+C, OM.stack(ree[i], 2*U), OM.stack(MT.CaF2, 9*U));
			}
		}

		//TODO LuAG lenses for high end (DUV) immersion lithography
		//TODO La-using Ni-MH batteries
		// TODO LaF3-fiberglass (ZBLAN), Er doped
		// TODO Tb-based green phosphors
		//TODO Solid-State lasers for higher LU generation

		// Uranium processing
		RM.Bath.addRecipe1(false, 0, 128, new long[]{10000, 500, 100}, dust.mat(MT.OREMATS.Uraninite, 3), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MTx.UO2SO4Solution.liquid(11*U, false), MT.He.gas(U1000, false)), dustTiny.mat(MTx.PbSO4, 1), dustTiny.mat(MTx.RaSO4, 1));
		RM.Bath.addRecipe1(false, 0, 128, new long[]{10000, 2000, 100}, dust.mat(MT.OREMATS.Pitchblende, 3), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MTx.UO2SO4Solution.liquid(11*U, false), MT.He.gas(U1000, false)), dustTiny.mat(MTx.PbSO4, 1), dustTiny.mat(MTx.RaSO4, 1));
		RM.Mixer.addRecipe0(true, 16, 128, new long[]{10000, 250}, FL.array(MTx.UO2SO4Solution.liquid(22*U, true), MT.NH3.gas(6*U, true), MT.H2O.liquid(9*U, true)), FL.array(MTx.NH4SO4Solution.liquid(20*U, false)), dust.mat(MTx.Yellowcake, 12*U), dustTiny.mat(MTx.ThOH4, 1));
		RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.Yellowcake, 12), FL.array(MT.H.gas(4*U, true)), FL.array(MT.NH3.gas(2*U, false), MT.H2O.liquid(9*U, false)), dust.mat(MTx.UO2, 2));
		// uranium fluoride recipes are added in changeRecipes(), after GT6's versions are disabled
	}

	/** Centrifuge ticks per full uranium unit of depleted (tails) output */
	private static final long TICKS_PER_DEPLETED_U = 1024;

	private static class UraniumTier {
		final OreDictMaterial metal, f6, f4, o2;
		/** U-235 content in 1/10000 */
		final long u235;
		UraniumTier(OreDictMaterial metal, OreDictMaterial f6, OreDictMaterial f4, OreDictMaterial o2, long u235) {
			this.metal = metal; this.f6 = f6; this.f4 = f4; this.o2 = o2; this.u235 = u235;

			if(this.u235 >= 250) {
				MTx.makeRadioactive(this.metal, this.u235 < 5000 ? 1 : 2);
			}
		}
	}

	private void addUraniumRecipes() {
		UraniumTier
			depleted = new UraniumTier(MT.U_238 , MT.U238F6  , MT.U238F4  , MTx.DUO2   ,     0),
			natural  = new UraniumTier(MTx.NU   , MT.UF6     , MT.UF4     , MTx.UO2    ,    72),
			leu      = new UraniumTier(MTx.LEU  , MTx.LEUF6  , MTx.LEUF4  , MTx.LEUO2  ,   400),
			haleu    = new UraniumTier(MTx.HALEU, MTx.HALEUF6, MTx.HALEUF4, MTx.HALEUO2,  1500),
			heu      = new UraniumTier(MTx.HEU  , MTx.HEUF6  , MTx.HEUF4  , MTx.HEUO2  ,  5000),
			wgu      = new UraniumTier(MTx.WGU  , MTx.WGUF6  , MTx.WGUF4  , MTx.WGUO2  ,  8500),
			u235     = new UraniumTier(MT.U_235 , MT.U235F6  , MT.U235F4  , MTx.U235O2 , 10000);

		// Enrichment cascade: feed -> enriched product + tails
		addEnrichmentRecipe(natural, leu  , depleted);
		addEnrichmentRecipe(leu    , haleu, natural );
		addEnrichmentRecipe(haleu  , heu  , leu     );
		addEnrichmentRecipe(heu    , wgu  , haleu   );
		addEnrichmentRecipe(wgu    , u235 , heu     );

		for (UraniumTier tier : new UraniumTier[] {depleted, natural, leu, haleu, heu, wgu, u235}) {
			// UO2 -> UF4 (replacing GT6's Uraninite -> UF4)
			RM.Mixer.addRecipe1(true, 16, 144, OM.dust(tier.o2, U), MT.HF.gas(U*8, true), MT.H2O.liquid(U*6, false), OM.dust(tier.f4, U));
			// UF4 -> UF6
			RM.Mixer.addRecipe1(true, 16, 112, OM.dust(tier.f4, U), MT.F.gas(U*2, true), NF, tier.f6.gas(U, false));
			// UF6 -> UF4
			RM.Mixer.addRecipe0(true, 16, 144, FL.array(MT.H.gas(U*2, true), tier.f6.gas(U, true)), MT.HF.gas(U*4, false), OM.dust(tier.f4, U));
			// UF4 -> UO2 (pyrohydrolysis)
			RMx.Thermolysis.addRecipe1(true, 16, 186, OM.dust(tier.f4, U), MT.H2O.liquid(U*6, true), MT.HF.gas(U*8, false), OM.dust(tier.o2, U));
			// Molten UF4 + Ca (replacing GT6's versions)
			RM.Mixer.addRecipe0(true, 16, 112, FL.array(MT.Ca.liquid(U*2, true), tier.f4.liquid(U, true)), FL.array(MT.CaF2.liquid(U*6, false), tier.metal.liquid(U, false)));
			// UO2 -> U (aluminothermic/calcinothermic)
			RMx.Thermite.addRecipe(0, 4, OMx.stacks(MT.Al, 4*U, tier.o2, 3*U), 600, OM.stack(tier.metal, 3*U), OM.stack(MT.Al2O3, 10*U));
			RMx.Thermite.addRecipe(0, 2, OMx.stacks(MT.Ca, 2*U, tier.o2,   U), 600, OM.stack(tier.metal,   U), OM.stack(MTx.CaO, 4*U));
			// U -> UO2 (roasting)
			RM.Roasting.addRecipe1(true, 16, 56, OM.dust(tier.metal, U), MT.O.gas(2*U, false), NF, OM.dust(tier.o2, U));

			// PUREX LLE for waste products
			RM.Mixer.addRecipe0(true, 16, 96, FL.array(FLx.spentUranylNitrateAqueous(tier.metal, 10000), FL.Kerosine.make(3000), MTx.TBP.liquid(2*U, true)), FL.array(FLx.spentUranylNitrateOrganic(tier.metal, 15000 - tier.u235), FL.make(FLx.PUREXWaste, tier.u235)));
		}
		RM.Bath.addRecipe1(true, 0, 160, ST.tag(0), FL.array(FL.make(FLx.PUREXWaste, 15000)), FL.array(FL.make(FLx.PUREXWasteConc, 10000), FL.Kerosine.make(3000), MTx.TBP.liquid(2*U, false))); //TODO start some extraction already?
	}

	/** Fixed input of one unit of hexafluoride; outputs are mass-balanced on U-235 and sum to the input. */
	private void addEnrichmentRecipe(UraniumTier feed, UraniumTier product, UraniumTier tails) {
		FluidStack input = feed.f6.gas(U, true);
		long in = input.amount;
		long productAmount = in * (feed.u235 - tails.u235) / (product.u235 - tails.u235);
		long tailsAmount = in - productAmount;
		long duration = Math.max(1, TICKS_PER_DEPLETED_U * tailsAmount / in);

		RM.Centrifuge.addRecipe0(true, 512, duration, input,
			FL.array(
				FL.make(product.f6.mGas.getFluid(), (int) productAmount),
				FL.make(tails  .f6.mGas.getFluid(), (int) tailsAmount)
			)
		);
	}

	/** Disables GT6's uranium fluoride recipes, since their amounts assume a common divider of 0. */
	private void disableGt6UraniumFluorideRecipes() {
		// Centrifuge: only the single-input UF6 -> U238F6 + U235F6 recipe
		Fluid uf6 = MT.UF6.mGas.getFluid();
		for (Recipe r : RM.Centrifuge.mRecipeList) {
			if (r.mFluidInputs.length == 1 && r.mFluidInputs[0] != null && r.mFluidInputs[0].getFluid() == uf6) {
				r.mEnabled = false;
			} else if (r.mInputs.length == 1 && r.mFluidInputs.length == 0 && (
				ST.equal(r.mInputs[0], MTEx.gt6MTEReg.getItem(9320)) ||
				ST.equal(r.mInputs[0], MTEx.gt6MTEReg.getItem(9321)) ||
				ST.equal(r.mInputs[0], MTEx.gt6MTEReg.getItem(9322))
			)) {
				r.mEnabled = false;
			}
		}

		// Mixer: every recipe consumes or produces a tetrafluoride; as dust (Uraninite -> UF4, UF4 -> UF6, UF6 -> UF4) or molten (UF4 + Ca)
		OreDictMaterial[] tetrafluorides = {MT.UF4, MT.U238F4, MT.U235F4};
		ItemStack[] dusts = new ItemStack[tetrafluorides.length];
		Set<Fluid> molten = new HashSet<>();
		for (int i = 0; i < tetrafluorides.length; i++) {
			dusts[i] = OM.dust(tetrafluorides[i]);
			molten.add(tetrafluorides[i].mLiquid.getFluid());
		}

		for (Recipe r : RM.Mixer.mRecipeList) {
			if (containsAny(r.mInputs, dusts) || containsAny(r.mOutputs, dusts) || containsAny(r.mFluidInputs, molten)) {
				r.mEnabled = false;
			}
		}
	}

	private static boolean containsAny(ItemStack[] stacks, ItemStack[] items) {
		for (ItemStack stack : stacks) {
			for (ItemStack item : items) {
				if (ST.equal(stack, item)) return true;
			}
		}
		return false;
	}

	private static boolean containsAny(FluidStack[] stacks, Set<Fluid> fluids) {
		for (FluidStack stack : stacks) {
			if (stack != null && fluids.contains(stack.getFluid())) return true;
		}
		return false;
	}

	private void changeItemRecipes() {
		CRx.overrideShaped(IL.MOTORS[5].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stick    .dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[5]), 'R', OP.stick.dat(MT.DATA.Electric_T[5]), 'W', OP.wireGt05.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[5]);
		CRx.overrideShaped(IL.MOTORS[6].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stick    .dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[6]), 'R', OP.stick.dat(MT.DATA.Electric_T[6]), 'W', OP.wireGt06.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[6]);
		CRx.overrideShaped(IL.MOTORS[7].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stickLong.dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[7]), 'R', OP.stick.dat(MT.DATA.Electric_T[7]), 'W', OP.wireGt07.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[7]);
		CRx.overrideShaped(IL.MOTORS[8].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stickLong.dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[8]), 'R', OP.stick.dat(MT.DATA.Electric_T[8]), 'W', OP.wireGt08.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[8]);
		CRx.overrideShaped(IL.MOTORS[9].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stickLong.dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[9]), 'R', OP.stick.dat(MT.DATA.Electric_T[9]), 'W', OP.wireGt09.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[9]);

		for (int i = 5; i < 10; i++) {
			CRx.overrideItemData(IL.PUMPS      [i].get(1), "TXO", "dPw", "OMT", 'M', IL.MOTORS[i], 'O', OP.ring.dat(ANY.Rubber), 'X', OP.rotor.dat(MT.DATA.Electric_T[i]), 'T', OP.screw.dat(MT.DATA.Electric_T[i]), 'P', OP.plateCurved.dat(MT.DATA.Electric_T[i]));
			CRx.overrideItemData(IL.CONVEYERS  [i].get(1), "RRR", "MCM", "RRR", 'M', IL.MOTORS[i], 'C', MT.DATA.CABLES_01[i], 'R', OP.plate.dat(ANY.Rubber));
			CRx.overrideItemData(IL.PISTONS    [i].get(1), "TPP", "dSS", "TMG", 'M', IL.MOTORS[i], 'P', OP.plate.dat(MT.DATA.Electric_T[i]), 'S', OP.stick.dat(MT.DATA.Electric_T[i]), 'G', OP.gearGtSmall.dat(MT.DATA.Electric_T[i]), 'T', OP.screw.dat(MT.DATA.Electric_T[i]));
			CRx.overrideItemData(IL.ROBOT_ARMS [i].get(1), "CCC", "MSM", "PES", 'M', IL.MOTORS[i], 'C', MT.DATA.CABLES_01[i], 'E', OD_CIRCUITS[i], 'S', OP.stick.dat(MT.DATA.Electric_T[i]), 'P', IL.PISTONS[i]);
		}
	}

	private void changeMTEs() {
		OreDictMaterial mat = MT.DATA.Electric_T[5];
		// E. Engine + Motor + Dynamo (IV)
		CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10015), CR.DEF_REM_REV, "PhP", "CIC", "PwP", 'P', OP.plateTriple.dat(mat), 'I', OP.stickLong.dat(MTx.SmCo5Magnetic), 'C', OP.wireGt16.dat(MT.AnnealedCopper));
		CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10025), CR.DEF_REM_REV, "TIT", "CMC", "TGd", 'M', OP.casingMachineDouble.dat(mat), 'G', OP.gearGt.dat(mat), 'T', OP.screw.dat(mat), 'I', OP.stickLong.dat(MTx.SmCo5Magnetic), 'C', OP.wireGt16.dat(MT.AnnealedCopper));
		CRx.overrideShaped(MTEx.gt6MTEReg.getItem(10115), CR.DEF_REM_REV, "TGT", "CMC", "TId", 'M', OP.casingMachineDouble.dat(mat), 'T', OP.screw.dat(mat), 'G', OP.gearGt.dat(mat), 'I', OP.stickLong.dat(MTx.SmCo5Magnetic), 'C', OP.wireGt16.dat(MT.AnnealedCopper));
		// Flux Engine + Motor + Dynamo (IV)
		mat = MT.DATA.Flux_T[5];
		CRx.overrideShaped(MTEx.gt6MTEReg.getItem(11015), CR.DEF_REM_REV, "G", "M", "G", 'M', MTEx.gt6MTEReg.getItem(10015), 'G', OP.gearGt.dat(mat));
		CRx.overrideShaped(MTEx.gt6MTEReg.getItem(11025), CR.DEF_REM_REV, "GMG", 'M', MTEx.gt6MTEReg.getItem(10025), 'G', OP.gearGt.dat(mat));
		CRx.overrideShaped(MTEx.gt6MTEReg.getItem(11115), CR.DEF_REM_REV, "SMS", "SGS", 'M', MTEx.gt6MTEReg.getItem(10115), 'G', OP.gearGt.dat(mat), 'S', OP.stickLong.dat(mat));

		// Old fuel rods
		for (short id : new short[]{9210, 9220, 9221, 9222, 9230, 9231, 9232, 9233, 9240, 9241, 941, 942, 9410, 9411, 9420, 9421}) {
			MTEx.disableGT6MTE(id);
		}

		// Th-232 and U-238 are non-fissile; they should be bred rather than made critical. NU can only be made critical in HWRs or using lots of moderators
		mat = MTx.NU     ; MTEx.gt6xMTEReg.add("Natural Uranium Fuel Rod"              , "Reactors", 9220,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 100000L , NBT_NUCLEAR_SELF,   2, NBT_NUCLEAR_OTHER,   4, NBT_NUCLEAR_DIV, 32, NBT_NUCLEAR_MAX,      512, NBT_VALUE, 9320)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9220, 4));
		mat = MTx.UO2    ; MTEx.gt6xMTEReg.add("Natural Uranium Oxide Fuel Rod"        , "Reactors", 9221,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 100000L , NBT_NUCLEAR_SELF,   1, NBT_NUCLEAR_OTHER,   4, NBT_NUCLEAR_DIV, 32, NBT_NUCLEAR_MAX,     1024, NBT_VALUE, 9321)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9221, 4));
		mat = MTx.LEU    ; MTEx.gt6xMTEReg.add("Low-Enriched Uranium Fuel Rod"         , "Reactors", 9222,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 300000L , NBT_NUCLEAR_SELF,   4, NBT_NUCLEAR_OTHER,   6, NBT_NUCLEAR_DIV, 16, NBT_NUCLEAR_MAX,      640, NBT_VALUE, 9322)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9222, 4));
		mat = MTx.LEUO2  ; MTEx.gt6xMTEReg.add("Low-Enriched Uranium Oxide Fuel Rod"   , "Reactors", 9223,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 300000L , NBT_NUCLEAR_SELF,   2, NBT_NUCLEAR_OTHER,   6, NBT_NUCLEAR_DIV, 16, NBT_NUCLEAR_MAX,     1280, NBT_VALUE, 9323)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9223, 4));
		mat = MTx.HALEU  ; MTEx.gt6xMTEReg.add("High-Assay LEU Fuel Rod"               , "Reactors", 9224,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 900000L , NBT_NUCLEAR_SELF,   8, NBT_NUCLEAR_OTHER,   8, NBT_NUCLEAR_DIV, 12, NBT_NUCLEAR_MAX,      768, NBT_VALUE, 9324)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9224, 4));
		mat = MTx.HALEUO2; MTEx.gt6xMTEReg.add("High-Assay LEU Oxide Fuel Rod"         , "Reactors", 9225,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 900000L , NBT_NUCLEAR_SELF,   4, NBT_NUCLEAR_OTHER,   8, NBT_NUCLEAR_DIV, 12, NBT_NUCLEAR_MAX,     1536, NBT_VALUE, 9325)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9225, 4));
		mat = MTx.HEU    ; MTEx.gt6xMTEReg.add("Highly-Enriched Uranium Fuel Rod"      , "Reactors", 9226,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 3000000L, NBT_NUCLEAR_SELF,  16, NBT_NUCLEAR_OTHER,  16, NBT_NUCLEAR_DIV,  8, NBT_NUCLEAR_MAX,     1024, NBT_VALUE, 9326)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9226, 4));
		mat = MTx.HEUO2  ; MTEx.gt6xMTEReg.add("Highly-Enriched Uranium Oxide Fuel Rod", "Reactors", 9227,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 3000000L, NBT_NUCLEAR_SELF,   8, NBT_NUCLEAR_OTHER,  16, NBT_NUCLEAR_DIV,  8, NBT_NUCLEAR_MAX,     2048, NBT_VALUE, 9327)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9227, 4));
		mat = MTx.WGU    ; MTEx.gt6xMTEReg.add("Weapons-Grade Uranium Fuel Rod"        , "Reactors", 9228,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 5000000L, NBT_NUCLEAR_SELF,  24, NBT_NUCLEAR_OTHER,  24, NBT_NUCLEAR_DIV,  6, NBT_NUCLEAR_MAX,     1536, NBT_VALUE, 9328)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9228, 4));
		mat = MTx.WGUO2  ; MTEx.gt6xMTEReg.add("Weapons-Grade Uranium Oxide Fuel Rod"  , "Reactors", 9229,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 5000000L, NBT_NUCLEAR_SELF,  12, NBT_NUCLEAR_OTHER,  24, NBT_NUCLEAR_DIV,  6, NBT_NUCLEAR_MAX,     3072, NBT_VALUE, 9329)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9229, 4));
		mat = MT.U_235   ; MTEx.gt6xMTEReg.add("Uranium-235 Fuel Rod"                  , "Reactors", 9230,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 6000000L, NBT_NUCLEAR_SELF,  32, NBT_NUCLEAR_OTHER,  32, NBT_NUCLEAR_DIV,  4, NBT_NUCLEAR_MAX,     2048, NBT_VALUE, 9330)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9230, 4));
		mat = MTx.U235O2 ; MTEx.gt6xMTEReg.add("Uranium-235 Oxide Fuel Rod"            , "Reactors", 9231,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 6000000L, NBT_NUCLEAR_SELF,  16, NBT_NUCLEAR_OTHER,  32, NBT_NUCLEAR_DIV,  4, NBT_NUCLEAR_MAX,     4096, NBT_VALUE, 9331)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9231, 4));
		mat = MT.U_233   ; MTEx.gt6xMTEReg.add("Uranium-233 Fuel Rod"                  , "Reactors", 9232,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 6000000L, NBT_NUCLEAR_SELF,  32, NBT_NUCLEAR_OTHER,  32, NBT_NUCLEAR_DIV,  4, NBT_NUCLEAR_MAX,     2048, NBT_VALUE, 9332)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9232, 4));
		mat = MTx.U_233O2; MTEx.gt6xMTEReg.add("Uranium-233 Oxide Fuel Rod"            , "Reactors", 9233,  9200, MultiTileEntityReactorRodNuclear.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY, 6000000L, NBT_NUCLEAR_SELF,  16, NBT_NUCLEAR_OTHER,  32, NBT_NUCLEAR_DIV,  4, NBT_NUCLEAR_MAX,     4096, NBT_VALUE, 9333)); RM.Canner.addRecipe2(false, 16, 16, OP.billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9233, 4));

		mat = MTx.NU     ; MTEx.gt6xMTEReg.add("Depleted Natural Uranium Fuel Rod"              , "Reactors", 9320,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500,   5,   5,   5}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.NU, 10000), MT.Kr.gas(  U1000, false), MT.Xe.gas(U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.UO2    ; MTEx.gt6xMTEReg.add("Depleted Natural Uranium Oxide Fuel Rod"        , "Reactors", 9321,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500,   5,   5,   5}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.NU, 10000), MT.Kr.gas(  U1000, false), MT.Xe.gas(U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.LEU    ; MTEx.gt6xMTEReg.add("Depleted Low-Enriched Uranium Fuel Rod"         , "Reactors", 9322,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500,  10,  10,  10}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.LEU, 10000), MT.Kr.gas(2*U1000, false), MT.Xe.gas(2*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.LEUO2  ; MTEx.gt6xMTEReg.add("Depleted Low-Enriched Uranium Oxide Fuel Rod"   , "Reactors", 9323,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500,  10,  10,  10}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.LEU, 10000), MT.Kr.gas(2*U1000, false), MT.Xe.gas(2*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.HALEU  ; MTEx.gt6xMTEReg.add("Depleted High-Assay LEU Fuel Rod"               , "Reactors", 9324,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500,  30,  30,  30}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.HALEU, 10000), MT.Kr.gas(6*U1000, false), MT.Xe.gas(6*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.HALEUO2; MTEx.gt6xMTEReg.add("Depleted High-Assay LEU Oxide Fuel Rod"         , "Reactors", 9325,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500,  30,  30,  30}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.HALEU, 10000), MT.Kr.gas(6*U1000, false), MT.Xe.gas(6*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.HEU    ; MTEx.gt6xMTEReg.add("Depleted Highly-Enriched Uranium Fuel Rod"      , "Reactors", 9326,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 100, 100, 100}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.HEU, 10000), MT.Kr.gas(20*U1000, false), MT.Xe.gas(20*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.HEUO2  ; MTEx.gt6xMTEReg.add("Depleted Highly-Enriched Uranium Oxide Fuel Rod", "Reactors", 9327,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 100, 100, 100}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.HEU, 10000), MT.Kr.gas(20*U1000, false), MT.Xe.gas(20*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.WGU    ; MTEx.gt6xMTEReg.add("Depleted Weapons-Grade Uranium Fuel Rod"        , "Reactors", 9328,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 175, 175, 175}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.WGU, 10000), MT.Kr.gas(35*U1000, false), MT.Xe.gas(35*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.WGUO2  ; MTEx.gt6xMTEReg.add("Depleted Weapons-Grade Uranium Oxide Fuel Rod"  , "Reactors", 9329,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 175, 175, 175}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MTx.WGU, 10000), MT.Kr.gas(35*U1000, false), MT.Xe.gas(35*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MT.U_235   ; MTEx.gt6xMTEReg.add("Depleted Uranium-235 Fuel Rod"                  , "Reactors", 9330,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 200, 200, 200}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MT.U_235, 10000), MT.Kr.gas(40*U1000, false), MT.Xe.gas(40*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.U235O2 ; MTEx.gt6xMTEReg.add("Depleted Uranium-235 Oxide Fuel Rod"            , "Reactors", 9331,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 200, 200, 200}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MT.U_235, 10000), MT.Kr.gas(40*U1000, false), MT.Xe.gas(40*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MT.U_233   ; MTEx.gt6xMTEReg.add("Depleted Uranium-233 Fuel Rod"                  , "Reactors", 9332,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 200, 200, 200}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MT.U_233, 10000), MT.Kr.gas(40*U1000, false), MT.Xe.gas(40*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));
		mat = MTx.U_233O2; MTEx.gt6xMTEReg.add("Depleted Uranium-233 Oxide Fuel Rod"            , "Reactors", 9333,  9200, MultiTileEntityReactorRodDepleted.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F)); RM.Bath.addRecipe1(true, 0, 512, new long[]{9500, 200, 200, 200}, MTEx.gt6xMTEReg.getItem(), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(FLx.spentUranylNitrateAqueous(MT.U_233, 10000), MT.Kr.gas(40*U1000, false), MT.Xe.gas(40*U1000, false)), scrapGt.mat(MT.Zr, 9), dustTiny.mat(MT.Ru, 1), dustTiny.mat(MT.Rh, 1), dustTiny.mat(MT.Pd, 1));

		mat = MT.Th;       MTEx.gt6xMTEReg.add("Thorium-232 Breeder Rod"                        , "Reactors", 9410,  9200, MultiTileEntityReactorRodBreeder.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY,   64000000L, NBT_NUCLEAR_LOSS,  1000, NBT_VALUE, 9411)); RM.Canner.addRecipe2(F, 16, 16, billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9410, 4));
		mat = MTx.ThO2;    MTEx.gt6xMTEReg.add("Thorium-232 Oxide Breeder Rod"                  , "Reactors", 9412,  9200, MultiTileEntityReactorRodBreeder.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY,   64000000L, NBT_NUCLEAR_LOSS,  1000, NBT_VALUE, 9411)); RM.Canner.addRecipe2(F, 16, 16, billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9410, 4));
		mat = MT.U_238;    MTEx.gt6xMTEReg.add("Uranium-238 Breeder Rod"                        , "Reactors", 9420,  9200, MultiTileEntityReactorRodBreeder.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY,  256000000L, NBT_NUCLEAR_LOSS,  2500, NBT_VALUE, 9421)); RM.Canner.addRecipe2(F, 16, 16, billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9420, 4));
		mat = MTx.DUO2;    MTEx.gt6xMTEReg.add("Uranium-238 Oxide Breeder Rod"                  , "Reactors", 9422,  9200, MultiTileEntityReactorRodBreeder.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_MAXDURABILITY,  256000000L, NBT_NUCLEAR_LOSS,  2500, NBT_VALUE, 9421)); RM.Canner.addRecipe2(F, 16, 16, billet.mat(mat, 3), IL.Reactor_Rod_Empty.get(4), MTEx.gt6xMTEReg.getItem(9420, 4));
		mat = MTx.Pa_233;  MTEx.gt6xMTEReg.add("Protactinium-233 Enriched Rod"                  , "Reactors", 9411,  9200, MultiTileEntityReactorRodProduct.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_VALUE, 9410));
		mat = MTx.Pa_233O2; MTEx.gt6xMTEReg.add("Protactinium-233 Oxide Enriched Rod"            , "Reactors", 9413,  9200, MultiTileEntityReactorRodProduct.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_VALUE, 9412));
		mat = MT.Pu_239;   MTEx.gt6xMTEReg.add("Plutonium-239 Enriched Rod"                     , "Reactors", 9421,  9200, MultiTileEntityReactorRodProduct.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_VALUE, 9420));
		mat = MTx.Pu_239O2; MTEx.gt6xMTEReg.add("Plutonium-239 Oxide Enriched Rod"               , "Reactors", 9423,  9200, MultiTileEntityReactorRodProduct.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  10.0F, NBT_RESISTANCE,  10.0F, NBT_VALUE, 9422));
	}

	private void changeRecipes() {
		for (Recipe r : RM.Smelter.mRecipeList) {
			if (r.mOutputs.length == 1 && (
				r.mOutputs[0].isItemEqual(dustTiny.mat(MT.RareEarth, 1)) ||
				r.mOutputs[0].isItemEqual(dustSmall.mat(MT.RareEarth, 1)) ||
				r.mOutputs[0].isItemEqual(dust.mat(MT.RareEarth, 1))
			)) {
				r.mEnabled = false;
			}
		}

		// Replace GT6's uranium fluoride recipes (incl. Uraninite -> UF4 and the one-step UF6 centrifuging)
		disableGt6UraniumFluorideRecipes();
		addUraniumRecipes();

		// Get rid of polarizer recipes for pure Nd
		for (ListIterator<IRecipeMapHandler> it = RM.Polarizer.mRecipeMapHandlers.listIterator(); it.hasNext(); ) {
			IRecipeMapHandler handler = it.next();
			if (handler instanceof RecipeMapHandlerMaterial handlerMaterial) {
				try {
					Field f = handlerMaterial.getClass().getDeclaredField("mInputMaterial");
					f.setAccessible(true);
					OreDictMaterial mat = (OreDictMaterial) f.get(handlerMaterial);
					if (mat.mID == MT.Nd.mID) {
						it.remove();
					}
				} catch (NoSuchFieldException e) {
					LOG.error("mInputMaterial not in RecipeMapHandlerMaterial");
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					LOG.error("mInputMaterial not accessible in RecipeMapHandlerMaterial");
					throw new RuntimeException(e);
				}
			}
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		ICondition condition = new ICondition.And(ANTIMATTER.NOT, COATED.NOT);

		RM.Polarizer.add(new RecipeMapHandlerMaterial(MTx.SmCo5, NF, 128, 144, NF, MTx.SmCo5Magnetic, NI, true, condition));
		RM.Polarizer.add(new RecipeMapHandlerMaterial(MTx.Nd2Fe14B, NF, 128, 144, NF, MT.NeodymiumMagnetic, NI, true, condition));
	}

	private void changeByProducts() {
		MT.Monazite.addOreByProducts(MTx.Xenotime);
		MTx.Xenotime.addOreByProducts(MT.Monazite, MT.OREMATS.Pitchblende, MT.OREMATS.Bastnasite, MT.Biotite, MT.Apatite);

		for (OreDictMaterial mat : MT.ALL_MATERIALS_REGISTERED_HERE) {
			ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
			while (it.hasNext()) {
				OreDictMaterial byproduct = it.next();
				if (byproduct.mID == MT.Nd.mID) {
					it.set(MT.OREMATS.Bastnasite);
				} else if (byproduct.mID == MT.RareEarth.mID) {
					it.set(MT.Monazite);
				} else if (byproduct.mID == MT.Th.mID) {
					it.set(MTx.ThO2);
				}
			}
		}
	}

	private void overrideWorldgen() {
		HashSet<String> toDisable = new HashSet<>(Arrays.asList(
			"ore.large.monazite"
		));

		for (WorldgenObject obj : CS.ORE_OVERWORLD) {
			if (toDisable.contains(obj.mName)) {
				obj.mEnabled = false;
			}
		}

		new WorldgenOresLarge("ore.large.monazite2", true, true, 20,  40,  30, 3, 16, MT.OREMATS.Bastnasite, MTx.Xenotime, MT.Monazite, MT.OREMATS.Bastnasite, ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS);

	}
}
