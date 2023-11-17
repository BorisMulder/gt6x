package org.altadoon.gt6x.common;

import gregapi.data.FL;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.IIconContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import org.altadoon.gt6x.Gt6xMod;

import java.util.Set;

import static gregapi.data.CS.*;

public class FLx {
    public static Fluid Varnish = null;

    @SafeVarargs public static Fluid createPlasma(OreDictMaterial material, Set<String>... fluidList) {return create("plasma."+material.mNameInternal.toLowerCase(), material.mNameLocal + " Plasma", material, STATE_PLASMA, 2000, material.mPlasmaPoint, fluidList).setLuminosity(15);}

    @SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, Set<String>... fluidList) {return create(name, localized, material, state, 1000, 300, fluidList);}
    @SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, long amountPerUnit, long temperatureK, Set<String>... fluidList) {return create(name, localized, material, state, amountPerUnit, temperatureK, null, null, 0, fluidList);}
    @SafeVarargs public static Fluid create(String name, String localized, OreDictMaterial material, int state, long amountPerUnit, long temperatureK, ItemStack fullContainer, ItemStack emptyContainer, int fluidAmount, Set<String>... fluidList) {return FL.create(name, new Textures.BlockIcons.CustomIcon(Gt6xMod.MOD_ID + ":fluids/" + name.toLowerCase()), localized, material, null, state, amountPerUnit, temperatureK, fullContainer, emptyContainer, fluidAmount, fluidList);}

    public static void init() {
        if (!FL.Resin       .exists()) create("resin"      , "Resin"       , null, STATE_LIQUID).setDensity(900);
        if (!FL.Resin_Spruce.exists()) create("spruceresin", "Spruce Resin", null, STATE_LIQUID).setDensity(900);
        if (!FL.Turpentine  .exists()) create("turpentine" , "Turpentine"  , null, STATE_LIQUID);
        Varnish = create("varnish" , "Varnish", null, 1);
    }
}
