package org.altadoon.gt6x.features.engines;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import net.minecraft.tileentity.TileEntity;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.CS.NBT_ENERGY_EMITTED;

public class Engines extends GT6XFeature {

    @Override
    public String name() {
        return null;
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
    public void postInit() {}

    private void addMTEs() {
        Class<? extends TileEntity> clazz;
        OreDictMaterial mat;
        MultiTileEntityRegistry reg = MTEx.gt6xMTEReg;

        // Petrol Engines
        clazz = MultiTileEntityMotorLiquid.class;
        mat = MT.Bronze;               reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9147,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,   16, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MT.ArsenicCopper;        reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9146,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,   16, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MT.ArsenicBronze;        reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9145,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,   24, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MT.IronCast;             reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9148,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,   32, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MTx.Alusil;              reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9149,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,   64, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MTx.Hastelloy;           reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9197,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,  128, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MTx.Ti6Al4V;             reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9198,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,  256, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        mat = MT.Ir;                   reg.add("Petrol Engine ("+mat.getLocal()+")", "Engines",  9199,  1304, clazz, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_FUELMAP, FM.Engine, NBT_EFFICIENCY, 10000, NBT_OUTPUT,  512, NBT_ENERGY_EMITTED, TD.Energy.RU), "PLP", "SMS", "GPC", 'M', OP.casingMachineDouble.dat(mat), 'P', OP.plateCurved.dat(mat), 'S', OP.stick.dat(mat), 'G', OP.gearGt.dat(mat), 'C', OP.gearGtSmall.dat(mat), 'L', OD.itemLubricant);
        //TODO
    }
}
