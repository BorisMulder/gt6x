package org.altadoon.gt6x.common;

import com.google.common.collect.Iterables;
import gregapi.data.FL;
import gregapi.lang.LanguageHandler;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import org.altadoon.gt6x.Gt6xMod;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static gregapi.data.CS.*;

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

    public static List<String> ALCOHOLS = StreamSupport.stream(Iterables.concat(
            FluidsGT.RUM, FluidsGT.WHISKEY, FluidsGT.VINEGAR,
            Arrays.asList("ethanol", "vodka", "binnie.vodka", "potion.dragonblood", "potion.goldencider", "potion.notchesbrew", "hootch", FL.BioEthanol.mName, FL.Reikanol.mName)
    ).spliterator(), false).collect(Collectors.toList());

    public static FL[] BIO_OILS = { FL.Oil_Frying, FL.Oil_Seed, FL.Oil_Plant, FL.Oil_Sunflower, FL.Oil_Olive, FL.Oil_Nut, FL.Oil_Lin, FL.Oil_Hemp, FL.Oil_Fish, FL.Oil_Whale, FL.Oil_Canola, FL.Oil_Plant };

    @SafeVarargs public static Fluid createPlasma(OreDictMaterial material, Set<String>... fluidList) {return create("plasma."+material.mNameInternal.toLowerCase(), material.mNameLocal + " Plasma", material, STATE_PLASMA, 2000, material.mPlasmaPoint, fluidList).setLuminosity(15);}

    @SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, Set<String>... fluidList) {return create(name, localized, material, state, 1000, 300, fluidList);}
    @SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, long amountPerUnit, long temperatureK, Set<String>... fluidList) {return create(name, localized, material, state, amountPerUnit, temperatureK, null, null, 0, fluidList);}
    @SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, long amountPerUnit, long temperatureK, ItemStack fullContainer, ItemStack emptyContainer, int fluidAmount, Set<String>... fluidList) {return FL.create(name, new Textures.BlockIcons.CustomIcon(Gt6xMod.MOD_ID + ":fluids/" + name.toLowerCase()), localized, material, null, state, amountPerUnit, temperatureK, fullContainer, emptyContainer, fluidAmount, fluidList);}

    public static void init() {
        LanguageHandler.set(FL.Fuel.mName, "Heavy Fuel Oil");

        if (!FL.Resin       .exists()) create("resin"      , "Resin"       , null, STATE_LIQUID).setDensity(900);
        if (!FL.Resin_Spruce.exists()) create("spruceresin", "Spruce Resin", null, STATE_LIQUID).setDensity(900);
        if (!FL.Turpentine  .exists()) create("turpentine" , "Turpentine"  , null, STATE_LIQUID);
        Varnish = create("varnish" , "Varnish", null, 1);
        ThermalPaste = create("thermalpaste", "Thermal Paste", null, STATE_LIQUID);
        LiquidCrystal5CB = create("liquidcrystal5cb", "5CB Liquid Crystal", MTx.LiquidCrystal5CB, STATE_LIQUID, 144, 300);
    }
}
