package org.altadoon.gt6x.features.steelmaking;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.Recipe;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.CS.T;

public class Steelmaking extends GT6XFeature {
    public Recipe.RecipeMap blastFurnace = null;

    @Override
    public String name() {
        return "Steelmaking";
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {
        addRecipeMaps();
    }

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void postInit() {
        addRecipes();
    }

    @Override
    public void postPostInit() {

    }

    private void addRecipeMaps() {
        blastFurnace = new Recipe.RecipeMap(null, "gt6x.recipe.blastfurnace", "Blast Furnace", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace",6 , 3, 1, 3, 3, 0, 1, 1, "", 1, "", T, T, T, T, F, T, T);
    }

    private void addMTEs() {
        OreDictMaterial aMat;
        aMat = MT.Ceramic; MTEx.gt6xMTEReg.add("Blast Furnace", "Multiblock Machines", 61, 17101, MultiTileEntityBlastFurnace.class, aMat.mToolQuality, 16, MTEx.StoneBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 5.0F, NBT_RESISTANCE, 5.0F, NBT_INPUT, 32, NBT_INPUT_MIN, 8, NBT_INPUT_MAX, 64, NBT_TEXTURE, "blastfurnace", NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, blastFurnace, NBT_CHEAP_OVERCLOCKING, true, NBT_PARALLEL, 16, NBT_PARALLEL_DURATION, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', OP.plateCurved.dat(MT.WroughtIron), 'R', OP.stickLong.dat(MT.WroughtIron), 'F', MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(18000));
    }

    private void addRecipes() {
        for (ItemStack coal : new ItemStack[]{OP.dust.mat(MT.Charcoal, 2), OP.dust.mat(MT.CoalCoke, 1), OP.gem.mat(MT.Charcoal, 2), OP.gem.mat(MT.CoalCoke, 1)}) {
            blastFurnace.addRecipe(true, new ItemStack[]{OP.dust.mat(MT.Fe2O3, 5), ST.mul(3, coal), OP.dust.mat(MT.CaCO3, 1)}, null, null, null, FL.array(FL.Air.make(1000)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(7*U, false)), 64, 8, 0);
        }
    }
}
