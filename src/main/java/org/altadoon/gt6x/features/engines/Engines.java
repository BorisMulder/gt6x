package org.altadoon.gt6x.features.engines;

import com.google.common.collect.Iterables;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.Recipe;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import org.altadoon.gt6x.common.*;

import static org.altadoon.gt6x.common.Log.LOG;
import static org.altadoon.gt6x.common.RMx.FMx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static gregapi.data.CS.*;
import static gregapi.data.CS.NBT_ENERGY_EMITTED;

public class Engines extends GT6XFeature {

    @Override
    public String name() {
        return "EngineOverhaul";
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {}

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void postInit() {
        addRecipes();
        disableGT6Engines();
    }

    @Override
    public void afterPostInit() {
        moveOldEngineRecipes();
    }

    private static final OreDictMaterial[] ENGINE_MATERIALS = new OreDictMaterial[] { MT.Bronze, MT.ArsenicCopper, MT.ArsenicBronze, MT.IronCast, MTx.Alusil, MTx.Hastelloy, MTx.Ti6Al4V, MTx.TMS196 };
    private static final long[] ENGINE_OUTPUTS = new long[] { 16, 16, 24, 32, 64, 128, 256, 512 };

    private void addMTEs() {
        MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;

        for (int i = 0; i < ENGINE_OUTPUTS.length; i++) {
            OreDictMaterial mat = ENGINE_MATERIALS[i];
            reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.PetrolEngine[i].get(), 1304, MTEEnginePetrol.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_FUELMAP, FMx.Petrol, NBT_EFFICIENCY, 10000, NBT_OUTPUT, ENGINE_OUTPUTS[i], NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
            reg.add("Diesel Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.DieselEngine[i].get(), 1304, MTEEngineDiesel.class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_FUELMAP, FMx.Diesel, NBT_EFFICIENCY, 10000, NBT_OUTPUT, ENGINE_OUTPUTS[i], NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        }
    }

    private void moveOldEngineRecipes() {
        Set<Fluid> dieselFuels = new HashSet<>(Arrays.asList(
                FL.Diesel.fluid(), MTx.DieselLowSulfur.mLiquid.getFluid(), FL.Kerosine.fluid(), MTx.KerosineLowSulfur.mLiquid.getFluid(), FL.Fuel.fluid(), MTx.FuelLowSulfur.mLiquid.getFluid(), MT.NitroFuel.mLiquid.getFluid(), MTx.Ether.mLiquid.getFluid()
        ));

        Set<Fluid> petrolFuels = new HashSet<>(Arrays.asList(
                FL.Petrol.fluid(), MT.Ethanol.mLiquid.getFluid(), MTx.Methanol.mLiquid.getFluid(), MTx.Naphtha.mLiquid.getFluid(), MTx.NaphthaLowSulfur.mLiquid.getFluid(), MTx.Benzene.mLiquid.getFluid(), MTx.Toluene.mLiquid.getFluid(), MTx.Isopropanol.mLiquid.getFluid(), MTx.Pentanol.mLiquid.getFluid()
        ));

        for (FL fl : new FL[] {FL.JetFuel, FL.BioDiesel, FL.BioFuel}) {
            if (fl.exists()) dieselFuels.add(fl.fluid());
        }
        for (FL fl : new FL[] {FL.BioEthanol, FL.Reikanol}) {
            if (fl.exists()) petrolFuels.add(fl.fluid());
        }

        if (FL.exists("kerosene")) {
            dieselFuels.add(FL.fluid("kerosene"));
        }
        for (String fluidName : Iterables.concat(FluidsGT.RUM, FluidsGT.WHISKEY, FluidsGT.VINEGAR, Arrays.asList("gasoline", "ethanol", "vodka", "binnie.vodka", "potion.dragonblood", "potion.goldencider", "potion.notchesbrew", "hootch"))) {
            if (FL.exists(fluidName)) petrolFuels.add(FL.fluid(fluidName));
        }

        // Move fuel recipes
        for (Recipe recipe : FM.Engine.mRecipeList) {
            if (recipe.mFluidInputs.length > 0) {
                Fluid fluid = recipe.mFluidInputs[0].getFluid();
                if (dieselFuels.contains(fluid)) {
                    FMx.Diesel.add(recipe);
                } else if (petrolFuels.contains(fluid)) {
                    FMx.Petrol.add(recipe);
                } else {
                    LOG.info("Engine fluid " + fluid.getName() + " not assigned to Diesel or Petrol engine");
                }
            }
        }

        // Disable old engine recipes
        FM.Engine.mRecipeList.clear();
        FM.Engine.mRecipeItemMap.clear();
        FM.Engine.mRecipeFluidMap.clear();
    }

    private void addRecipes() {
        FMx.Petrol.addRecipe0(true, - 64,  8, FL.make("propane", 1), FL.CarbonDioxide.make(1), ZL_IS);
        FMx.Petrol.addRecipe0(true, - 64,  8, FL.make("butane", 1), FL.CarbonDioxide.make(1), ZL_IS);
        if (FL.exists("lpg"))
            FMx.Petrol.addRecipe0(true, - 64,  8, FL.make("lpg", 1), FL.CarbonDioxide.make(1), ZL_IS);

        //TODO balance fuel values
        //TODO add petrol-ethanol mixtures
        //TODO add biodiesel
    }

    private void disableGT6Engines() {
        // Disable crafting recipes and hide blocks in NEI
        for (int id : new int[] { 9145, 9146, 9147, 9148, 9149, 9197, 9198, 9199 }) {
            ItemStack stack = MTEx.gt6Registry.getItem(id);
            CRx.disableGt6(stack);
            ST.hide(stack);
        }
    }
}
