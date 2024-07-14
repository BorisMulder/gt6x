package org.altadoon.gt6x.features.engines;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.Recipe;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.*;

import static org.altadoon.gt6x.common.RMx.FMx;
import org.altadoon.gt6x.features.GT6XFeature;

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
        disableGT6Engines();
    }

    private static final OreDictMaterial[] ENGINE_MATERIALS = new OreDictMaterial[] { MT.Bronze, MT.ArsenicCopper, MT.ArsenicBronze, MT.IronCast, MTx.Alusil, MTx.Hastelloy, MTx.Ti6Al4V, MTx.TMS196 };
    private static final long[] ENGINE_OUTPUTS = new long[] { 16, 16, 24, 32, 64, 128, 256, 512 };

    private void addMTEs() {
        Class<? extends TileEntity> clazz;
        MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;

        // Petrol Engines
        clazz = MultiTileEntityMotorLiquid.class;

        for (int i = 0; i < ENGINE_OUTPUTS.length; i++) {
            OreDictMaterial mat = ENGINE_MATERIALS[i];
            reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.PetrolEngine[i].get(), 1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_FUELMAP, FMx.Petrol, NBT_EFFICIENCY, 10000, NBT_OUTPUT, ENGINE_OUTPUTS[i], NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
            reg.add("Diesel Engine ("+mat.getLocal()+")", "Engines",  MTEx.IDs.DieselEngine[i].get(), 1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_FUELMAP, FMx.Diesel, NBT_EFFICIENCY, 10000, NBT_OUTPUT, ENGINE_OUTPUTS[i], NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        }
    }

    private void disableGT6Engines() {
        // Disable crafting recipes and hide blocks in NEI
        for (int id : new int[] { 9145, 9146, 9147, 9148, 9149, 9197, 9198, 9199 }) {
            ItemStack stack = MTEx.gt6Registry.getItem(id);
            CRx.disableGt6(stack);
            ST.hide(stack);
        }

        // Disable fuel recipes
        for (Recipe recipe : FM.Engine.mRecipeList) {
            recipe.mEnabled = false;
            recipe.mHidden = true;
            //TODO not hidden
        }
    }
}
