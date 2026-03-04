package org.altadoon.gt6x.features.fusion;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.Recipe;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.common.FLx.EU_PER_SC_STEAM;
import static org.altadoon.gt6x.common.FLx.SC_STEAM_PER_HP_WATER;

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

		// Centrifugal Pumps
		mat = MT.DATA.Kinetic_T[1]; MTEx.gt6xMTEReg.add("Centrifugal Pump ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.CentPump1.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   7.0F, NBT_RESISTANCE,   7.0F, NBT_INPUT,   32, NBT_TEXTURE, "centrifugalpump", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RMx.CentrifugalPump, NBT_TANK_SIDE_IN, SBIT_F, NBT_TANK_SIDE_AUTO_IN, SIDE_FRONT, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_UP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 1, NBT_PARALLEL_DURATION, true, NBT_CHEAP_OVERCLOCKING, true), "RRw", "SMP", "GXh", 'M', OP.casingMachineQuadruple.dat(mat), 'R', OP.rotor.dat(mat), 'P', OP.pipeLarge.dat(mat), 'G', OP.gearGt.dat(mat), 'X', gearGtSmall.dat(mat), 'S', OP.stick.dat(mat));
		mat = MT.DATA.Kinetic_T[2]; MTEx.gt6xMTEReg.add("Centrifugal Pump ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.CentPump2.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,  128, NBT_TEXTURE, "centrifugalpump", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RMx.CentrifugalPump, NBT_TANK_SIDE_IN, SBIT_F, NBT_TANK_SIDE_AUTO_IN, SIDE_FRONT, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_UP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 2, NBT_PARALLEL_DURATION, true, NBT_CHEAP_OVERCLOCKING, true), "RRw", "SMP", "GXh", 'M', OP.casingMachineQuadruple.dat(mat), 'R', OP.rotor.dat(mat), 'P', OP.pipeLarge.dat(mat), 'G', OP.gearGt.dat(mat), 'X', gearGtSmall.dat(mat), 'S', OP.stick.dat(mat));
		mat = MT.DATA.Kinetic_T[3]; MTEx.gt6xMTEReg.add("Centrifugal Pump ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.CentPump3.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "centrifugalpump", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RMx.CentrifugalPump, NBT_TANK_SIDE_IN, SBIT_F, NBT_TANK_SIDE_AUTO_IN, SIDE_FRONT, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_UP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 4, NBT_PARALLEL_DURATION, true, NBT_CHEAP_OVERCLOCKING, true), "RRw", "SMP", "GXh", 'M', OP.casingMachineQuadruple.dat(mat), 'R', OP.rotor.dat(mat), 'P', OP.pipeLarge.dat(mat), 'G', OP.gearGt.dat(mat), 'X', gearGtSmall.dat(mat), 'S', OP.stick.dat(mat));
		mat = MT.DATA.Kinetic_T[4]; MTEx.gt6xMTEReg.add("Centrifugal Pump ("+mat.getLocal()+")", "Basic Machines", MTEx.IDs.CentPump4.get(), 20001, MultiTileEntityBasicMachine.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "centrifugalpump", NBT_ENERGY_ACCEPTED, TD.Energy.RU, NBT_RECIPEMAP, RMx.CentrifugalPump, NBT_TANK_SIDE_IN, SBIT_F, NBT_TANK_SIDE_AUTO_IN, SIDE_FRONT, NBT_TANK_SIDE_OUT, SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_UP, NBT_ENERGY_ACCEPTED_SIDES, SBIT_B, NBT_PARALLEL, 8, NBT_PARALLEL_DURATION, true, NBT_CHEAP_OVERCLOCKING, true), "RRw", "SMP", "GXh", 'M', OP.casingMachineQuadruple.dat(mat), 'R', OP.rotor.dat(mat), 'P', OP.pipeLarge.dat(mat), 'G', OP.gearGt.dat(mat), 'X', gearGtSmall.dat(mat), 'S', OP.stick.dat(mat));
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

		// Fluid Compression
		RMx.CentrifugalPump.addRecipe0(false, 64, 32, FL.DistW.make(100), FL.make(FLx.HPWater, 99), ZL_IS);

		// Fusion Recipes
		/*
		1000MW = 2.7 billion kg coal/y or 250kg D+T/y (7.9 mg/s) (https://www.iter.org/machine/supporting-systems/fuelling)
		so a factor of 10 million
		1 unit coal = 8 smelts = 1600 ticks = 40k HU
		U1000 coal = 40HU
		U1000 D + U1000 T = 80EU * 10mil = 800mil HU
		1L D + 1L T ~= 0.16+0.24=0.4g, @8 mg/GJ produces 50 GJ
		at a rate of 196608 HU/t (convenient for top tier VbAd steam turbine) it would take just over 4000 ticks (say 4096) to produce ~800M HU
		that's 50 GJ over 200 seconds, so 250MW (2mg/s) would equal 200K HU/t, so 1 HU would equal 1250J.
		Let's just nerf the fuel by 100 times to get it closer to MJ/J from Mekanism/Voltz. What is the use of deuterium otherwise...
		*/

		// Terrestrial fusion candidates
		addFusionRecipes(  14, MT.D   .gas   (U1000*4, true), null                         , FL.array(MT.He_3.gas   (  U1000, false), MT.T     .gas(  U1000, false), MT.H.gas(  U1000, false)                                      ), 730L, false);
		addFusionRecipes(  13, MT.He_3.gas   (U1000*2, true), null                         , FL.array(MT.He  .gas   (  U1000, false), MT.H     .gas(2*U1000, false)                                                                ), 1290L, false);
		addFusionRecipes(  72, MT.H   .gas   (U144   , true), MT.Li_6.liquid(U144   , true), FL.array(MT.Be_7.liquid(  U144 , false)                                                                                               ), 1000L, false);
		addFusionRecipes(  50, MT.H   .gas   (U144   , true), MT.Li  .liquid(U144   , true), FL.array(MT.He  .gas   (2*U144 , false)                                                                                               ),  800L, false);
		addFusionRecipes(  80, MT.H   .gas   (U144   , true), MT.B_11.liquid(U144   , true), FL.array(MT.He  .gas   (3*U144 , false)                                                                                               ), 8469L, false);
		addFusionRecipes(   3, MT.H   .gas   (U1000  , true), MT.C   .gas   (U1000  , true), FL.array(MT.C_13.gas   (  U1000, false)                                                                                               ),  315L, false);
		addFusionRecipes(  14, MT.H   .gas   (U1000*2, true), MT.N   .gas   (U1000  , true), FL.array(MT.He  .gas   (  U1000, false), MT.C     .gas(  U1000, false)                                                                ), 1404L, false);
		addFusionRecipes(   4, MT.H   .gas   (U1000*2, true), MT.O   .gas   (U1000  , true), FL.array(MT.He  .gas   (  U1000, false), MT.N     .gas(  U1000, false)                                                                ),  455L, false);
		addFusionRecipes(  40, MT.D   .gas   (U1000  , true), MT.T   .gas   (U1000  , true), FL.array(MT.He  .gas   (  U1000, false)                                                                                               ), 1760L, false);
		addFusionRecipes(  40, MT.D   .gas   (U1000  , true), MT.He_3.gas   (U1000  , true), FL.array(MT.He  .gas   (  U1000, false), MT.H     .gas(  U1000, false)                                                                ), 1830L, false);
		addFusionRecipes(4*26, MT.T   .gas   (U1000*4, true), MT.He_3.gas   (U1000*4, true), FL.array(MT.He  .gas   (3*U1000, false), MT.D     .gas(  U1000, false)                                                                ), 2640L, false);
		addFusionRecipes( 180, MT.D   .gas   (U144 *8, true), MT.Li_6.liquid(U144 *8, true), FL.array(MT.He  .gas   (3*U144 , false), MT.He_3  .gas(  U144 , false), MT.Li  .liquid(  U144 , false), MT.Be_7.liquid(  U144 , false)), 3336L, false);
		addFusionRecipes( 160, MT.He_3.gas   (U144   , true), MT.Li_6.liquid(U144   , true), FL.array(MT.He  .gas   (2*U144 , false), MT.H     .gas(  U144 , false)                                                                ), 1690L, false);

		// Triple-alpha
		addFusionRecipes(   7, MT.He  .gas   (U1000*3, true), null                         , FL.array(MT.C   .gas   (  U1000, false)                                                                                               ),  736L, true );

		// alpha capture process
		addFusionRecipes(   7, MT.He  .gas   (U1000  , true), MT.C   .gas   (U1000  , true), FL.array(MT.O   .gas   (  U1000, false)                                                                                               ),  716L, true );
		addFusionRecipes(   5, MT.He  .gas   (U1000  , true), MT.O   .gas   (U1000  , true), FL.array(MT.Ne  .gas   (  U1000, false)                                                                                               ),  473L, true );
		addFusionRecipes(   9, MT.He  .gas   (U1000  , true), MT.Ne  .gas   (U1000  , true), FL.array(MT.Mg  .gas   (  U1000, false)                                                                                               ),  932L, true );
		addFusionRecipes(7*10, MT.He  .gas   (U144   , true), MT.Mg  .liquid(U144   , true), FL.array(MT.Si  .liquid(  U144 , false)                                                                                               ),  998L, true );
		addFusionRecipes(7* 7, MT.He  .gas   (U144   , true), MT.Si  .liquid(U144   , true), FL.array(MT.S   .liquid(  U144 , false)                                                                                               ),  695L, true );
		addFusionRecipes(7* 6, MT.He  .gas   (U144   , true), MT.S   .liquid(U144   , true), FL.array(MT.Ar  .gas   (  U144 , false) /* Ar-36 */                                                                                   ),  664L, true );
		addFusionRecipes(7* 7, MT.He  .gas   (U144   , true), MT.Ar  .gas   (U144   , true), FL.array(MT.Ca  .liquid(  U144 , false)                                                                                               ),  704L, true );
		addFusionRecipes(7*13, MT.He  .gas   (U144 *2, true), MT.Ca  .liquid(U144   , true), FL.array(MT.Ti  .liquid(  U144 , false) /* Ti-44 -> Sc-44 -> Ca-44 + He-4 -> Cr-48 -> V-48 -> Ti-48 */                                ),  513L, true );
		addFusionRecipes(7* 8, MT.He  .gas   (U144   , true), MT.Ti  .liquid(U144   , true), FL.array(MT.Cr  .liquid(  U144 , false) /* Fe-52 -> Mn-52 -> Cr-52 */                                                                 ),  770L, true );
		addFusionRecipes(7* 8, MT.He  .gas   (U144   , true), MT.Cr  .liquid(U144   , true), FL.array(MT.Fe  .liquid(  U144 , false) /* Ni-56 -> Co-56 -> Fe-56 */                                                                 ),  794L, true );

		// Burning carbon, oxygen
		addFusionRecipes(7* 4, MT.C   .gas   (U24    , true), null                         , FL.array(MT.Na  .liquid(  U144 , false), MT.Ne.gas(U144, false), MT.Mg.gas   (U144, false), MT.He.gas(  U144, false)                  ),  685L, true );
		addFusionRecipes(7*16, MT.O   .gas   (U18    , true), null                         , FL.array(MT.Si  .liquid(  U144 , false), MT.P .gas(U144, false), MT.S .liquid(U144, false), MT.H .gas(3*U144, false)                  ),  807L, true );

		// Vibranium
		addFusionRecipes(  28, MT.Ad  .liquid(U72    , true), MT.Be_7.liquid(U72    , true), FL.array(MT.W   .liquid(  U72  , false), MT.He.gas(16*U72, false), MT.He_3.gas(24*U72, false), MT.T.gas(24*U72, false)                ), 94956L, true , dustDiv72.mat(MT.Vb, 1));
	}

	private void addFusionRecipes(long duration, FluidStack input1, FluidStack input2, FluidStack[] outputs, long LUInputMultiplier, boolean lowOutput, ItemStack... outputItems) {
		long totalEnergy = lowOutput ? duration * 8192 * 2 : duration * 196608;

		addFusionRecipe(duration, FL.DistW.make(                      totalEnergy / EU_PER_WATER                             ), FL.Steam               .make(totalEnergy * STEAM_PER_EU           ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(FLx.HPWater,                (totalEnergy / EU_PER_SC_STEAM) / SC_STEAM_PER_HP_WATER), FL.make(FLx.SCSteam,         totalEnergy / EU_PER_SC_STEAM        ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.Coolant_IC2.make(                totalEnergy / EU_PER_COOLANT                           ), FL.Coolant_IC2_Hot     .make(totalEnergy / EU_PER_COOLANT         ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.HDO .mLiquid.getFluid(), totalEnergy / EU_PER_SEMI_HEAVY_WATER                  ), FL.Hot_Semi_Heavy_Water.make(totalEnergy / EU_PER_SEMI_HEAVY_WATER), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.D2O .mLiquid.getFluid(), totalEnergy / EU_PER_HEAVY_WATER                       ), FL.Hot_Heavy_Water     .make(totalEnergy / EU_PER_HEAVY_WATER     ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.T2O .mLiquid.getFluid(), totalEnergy / EU_PER_TRITIATED_WATER                   ), FL.Hot_Tritiated_Water .make(totalEnergy / EU_PER_TRITIATED_WATER ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.Na  .mLiquid.getFluid(), totalEnergy / EU_PER_SODIUM                            ), FL.Hot_Molten_Sodium   .make(totalEnergy / EU_PER_SODIUM          ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.Sn  .mLiquid.getFluid(), totalEnergy / EU_PER_TIN                               ), FL.Hot_Molten_Tin      .make(totalEnergy / EU_PER_TIN             ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.CO2 .mGas   .getFluid(), totalEnergy / EU_PER_CO2                               ), FL.Hot_Carbon_Dioxide  .make(totalEnergy / EU_PER_SODIUM          ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.He  .mGas   .getFluid(), totalEnergy / EU_PER_HELIUM                            ), FL.Hot_Helium          .make(totalEnergy / EU_PER_HELIUM          ), input1, input2, outputs, LUInputMultiplier, outputItems);
		addFusionRecipe(duration, FL.make(MT.LiCl.mLiquid.getFluid(), totalEnergy / EU_PER_LICL                              ), FL.Hot_Molten_LiCl     .make(totalEnergy / EU_PER_LICL            ), input1, input2, outputs, LUInputMultiplier, outputItems);
	}

	private void addFusionRecipe(long duration, FluidStack coolant, FluidStack heatant, FluidStack input1, FluidStack input2, FluidStack[] outputs, long LUInputMultiplier, ItemStack... outputItems) {
		if (input1 == null) throw new IllegalArgumentException("No input fuel for fusion recipe");

		ArrayList<FluidStack> inputList = new ArrayList<>();
		inputList.add(input1);
		if (input2 != null) inputList.add(input2);
		inputList.add(MT.N.liquid(U100*duration, true));

		boolean coolantIsReagent = false;
		for (ListIterator<FluidStack> i = inputList.listIterator(); i.hasNext();) {
			FluidStack reagent = i.next();
			if (reagent.isFluidEqual(coolant)) {
				coolantIsReagent = true;
				coolant.amount += reagent.amount;
				i.remove();
				break;
			}
		}
		inputList.add(coolant);

		ArrayList<FluidStack> outputList = new ArrayList<>();
		outputList.add(heatant);
		outputList.add(MT.N.gas(U100*duration, true));
		outputList.addAll(Arrays.asList(outputs));

		RMx.Fusion.addRecipe1(true, 8192, duration, ST.tag(inputList.size() + (coolantIsReagent ? 4 : 0)), inputList.toArray(ZL_FS), outputList.toArray(ZL_FS), outputItems)
			.setSpecialNumber(LUInputMultiplier);
	}

	private void removeGt6Fusion() {
		RMx.clearGt6(RM.Fusion);
		RM.Fusion.mRecipeItemMap.clear();
		RM.Fusion.mRecipeFluidMap.clear();
		ItemStack fusionReactor = MTEx.gt6MTEReg.getItem(17198);
		CRx.disableGt6(fusionReactor);
		ST.hide(fusionReactor);
	}
}
