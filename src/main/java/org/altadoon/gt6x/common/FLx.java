package org.altadoon.gt6x.common;

import com.google.common.collect.Iterables;
import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.lang.LanguageHandler;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.IIconContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.Gt6xMod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static gregapi.data.CS.*;
import static gregapi.data.CS.FluidsGT.*;

public class FLx {
	public static Fluid Varnish = null;
	public static Fluid ThermalPaste = null;
	public static Fluid LiquidCrystal5CB = null;
	public static Fluid Naphtha = MTx.Naphtha.mLiquid.getFluid();
	public static Fluid NaphthaLS = MTx.NaphthaLowSulfur.mLiquid.getFluid();
	public static Fluid LAGO = MTx.LAGO.mLiquid.getFluid();
	//public static Fluid HAGO = MTx.HAGO.mLiquid.getFluid();
	public static Fluid JetFuel = MTx.JetFuel.mLiquid.getFluid();
	public static Fluid Ethane = MTx.Ethane.mGas.getFluid();
	public static Fluid LPG = MTx.LPG.mLiquid.getFluid();
	public static Fluid LNG = MTx.LNG.mLiquid.getFluid();
	public static int NG_PER_LNG = 600;
	public static Fluid Synoil = MTx.Synoil.mLiquid.getFluid();
	public static Fluid LiquidNitrogen = null;

	/*
	 * If water has a density of 1000 kg/m3 and 1 water = 160 steam then GT6 steam has a density of 1000/160 = 6.25 kg/m3 (this occurs at ~12.5 bar and ~200C, but eh)
	 * Let's use water at 25MPa and 280°C (inlet) to 500°C (supercritical steam)
	 * see https://www.nuclear-power.com/nuclear-engineering/materials-nuclear-engineering/properties-steam-what-is-steam/supercritical-fluid-supercritical-water/properties-of-supercritical-water/
	 * Then, the density of the inlet HP water would be 777kg/m3 which means we need 124 times as much steam for the same weight (lets use 120 for simplicity)
	 * The density of SC steam is lower at 90 kg/m3 which would mean 1L of HP water would produce 8.63 L of SC steam.
	 * The enthalpy of normal steam is 2675 kJ/kg, for SC steam 3165, so 1.18 times larger.
	 * 2L of steam = 1 EU = 2*6.25/1000 = 0.0125kg
	 * 0.0125kg of SC steam would be 0.14L, but because of increased enthalpy 0.12L would give 1 EU, or 1L would give about 8 EU.
	 */
	public static Fluid HPWater = null;
	public static Fluid SCSteam = null;
	public static int SC_STEAM_PER_HP_WATER = 8;
	public static int EU_PER_SC_STEAM = 8;

	public static List<String> ALCOHOLS = StreamSupport.stream(Iterables.concat(
			FluidsGT.RUM, FluidsGT.WHISKEY, FluidsGT.VINEGAR,
			Arrays.asList("ethanol", "vodka", "binnie.vodka", "potion.dragonblood", "potion.goldencider", "potion.notchesbrew", "hootch", FL.BioEthanol.mName, FL.Reikanol.mName)
	).spliterator(), false).collect(Collectors.toList());

	public static FL[] BIO_OILS = { FL.Oil_Frying, FL.Oil_Seed, FL.Oil_Plant, FL.Oil_Sunflower, FL.Oil_Olive, FL.Oil_Nut, FL.Oil_Lin, FL.Oil_Hemp, FL.Oil_Fish, FL.Oil_Whale, FL.Oil_Canola, FL.Oil_Plant };

	public static HashMap<OreDictMaterial, Fluid> REE_EDTA_SOLUTIONS = new HashMap<>();

	@SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, Set<String>... fluidList) {return create(name, localized, material, state, 1000, 300, fluidList);}
	@SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, long amountPerUnit, long temperatureK, Set<String>... fluidList) {return create(name, localized, material, state, amountPerUnit, temperatureK, new Textures.BlockIcons.CustomIcon(Gt6xMod.MOD_ID + ":fluids/" + name.toLowerCase()), null, null, 0, fluidList);}
	@SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, long amountPerUnit, long temperatureK, IIconContainer texture, ItemStack fullContainer, ItemStack emptyContainer, int fluidAmount, Set<String>... fluidList) {return FL.create(name, texture, localized, material, null, state, amountPerUnit, temperatureK, fullContainer, emptyContainer, fluidAmount, fluidList);}

	public static FluidStack ReeEdta(OreDictMaterial mat, int amount) {
		if (REE_EDTA_SOLUTIONS.containsKey(mat)) {
			return FL.make(REE_EDTA_SOLUTIONS.get(mat), amount);
		} else {
			return null;
		}
	}

	public static void init() {
		LanguageHandler.set(FL.Fuel.mName, "Heavy Fuel Oil");

		if (!FL.Resin       .exists()) create("resin"      , "Resin"       , null, STATE_LIQUID).setDensity(900);
		if (!FL.Resin_Spruce.exists()) create("spruceresin", "Spruce Resin", null, STATE_LIQUID).setDensity(900);
		if (!FL.Turpentine  .exists()) create("turpentine" , "Turpentine"  , null, STATE_LIQUID);
		Varnish = create("varnish" , "Varnish", null, 1);
		ThermalPaste = create("thermalpaste", "Thermal Paste", null, STATE_LIQUID);
		LiquidCrystal5CB = create("liquidcrystal5cb", "5CB Liquid Crystal", MTx.LiquidCrystal5CB, STATE_LIQUID, 144, 300);

		for (OreDictMaterial ree : new OreDictMaterial[]{MTx.HREE, MT.Sc, MT.Y, MT.La, MT.Ce, MT.Pr, MT.Nd, MT.Sm, MT.Eu, MT.Gd, MT.Tb, MT.Dy, MT.Ho, MT.Er, MT.Tm, MT.Yb, MT.Lu}) {
			Fluid f = FL.create(ree.mNameInternal + "EDTASolution", MT.H2O.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_MOLTEN), ree.mNameLocal + " EDTA Solution", null, new short[]{180, 220, 255, 200}, STATE_LIQUID, 1000, 300, null, null, 0);
			REE_EDTA_SOLUTIONS.put(ree, f);
			// .setMcfg(0, MT.H, U, MT.La, U, EDTA, U, MT.H2O, 9*U).heat(MT.H2O)),
		}

		OreDictMaterial mat = MT.N;
		mat.setRGBaLiquid(255, 255, 255, 200);
		LiquidNitrogen = create("liquid." + mat.mNameInternal.toLowerCase(), "Liquid " + mat.mNameLocal, mat, STATE_LIQUID, 100, mat.mBoilingPoint);

		HPWater = create("hpwater", "High-Pressure Water", MTx.HPWater, STATE_LIQUID, 1000, 300, SIMPLE, LIQUID);
		SCSteam = create("scsteam", "Supercritical Water", MTx.HPWater, STATE_GASEOUS, 8000, 500 + C, SIMPLE, GAS, STEAM, POWER_CONDUCTING);
	}
}
