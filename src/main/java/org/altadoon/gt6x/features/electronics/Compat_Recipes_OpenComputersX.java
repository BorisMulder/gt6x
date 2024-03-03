package org.altadoon.gt6x.features.electronics;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import gregapi.api.Abstract_Mod;
import gregapi.code.ModData;
import gregapi.compat.CompatMods;
import gregapi.data.*;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import org.altadoon.gt6x.common.CRx;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ILx;

import static gregapi.data.CS.*;
import static gregapi.data.CS.NF;
import static org.altadoon.gt6x.features.electronics.MultiItemsElectronics.*;

public class Compat_Recipes_OpenComputersX extends CompatMods {
    public Compat_Recipes_OpenComputersX(ModData aMod, Abstract_Mod aGTMod) {super(aMod, aGTMod);}

    @Override
    public void onLoad(FMLInitializationEvent event) {
        OUT.println("GT6X_Mod: Doing Open Computers Items.");

        OM.reg(ILx.Transistor_ThroughHole.get(1), "oc:materialTransistor");
        OM.reg(ILx.Transistor_SMD        .get(1), "oc:materialTransistor");

        OM.reg(ILx.Hard_Disk         .get(1), "oc:materialDisk");
        OM.reg(ILx.Hard_Disk_Advanced.get(1), "oc:materialDisk");

        OM.reg(ILx.ICs[0].get(1), "oc:circuitChip1");
        OM.reg(ILx.ICs[1].get(1), "oc:circuitChip2");
        OM.reg(ILx.ICs[2].get(1), "oc:circuitChip3");

        OM.reg(ILx.CPUs[0].get(1), "oc:cpu1");
        OM.reg(ILx.CPUs[1].get(1), "oc:cpu2");
        OM.reg(ILx.CPUs[2].get(1), "oc:cpu3");

        OM.reg(ILx.RAMSticks[0].get(1), "oc:ram1");
        OM.reg(ILx.RAMSticks[1].get(1), "oc:ram3");
        OM.reg(ILx.RAMSticks[2].get(1), "oc:ram5");

        OM.reg(ILx.GraphicsCards[0].get(1), "oc:graphicsCard1");
        OM.reg(ILx.GraphicsCards[1].get(1), "oc:graphicsCard2");
        OM.reg(ILx.GraphicsCards[2].get(1), "oc:graphicsCard3");

        OM.reg(ILx.HDDs[0].get(1), "oc:hdd1");
        OM.reg(ILx.HDDs[1].get(1), "oc:hdd2");
        OM.reg(ILx.HDDs[2].get(1), "oc:hdd3");

        OM.reg(ILx.Motherboards[0].get(1), "oc:componentBus1");
        OM.reg(ILx.Motherboards[1].get(1), "oc:componentBus2");
        OM.reg(ILx.Motherboards[2].get(1), "oc:componentBus3");

        OM.reg(ILx.SoCs[1].get(1), "oc:apu1");
        OM.reg(ILx.SoCs[2].get(1), "oc:apu2");
    }

    @Override public void onPostLoad(FMLPostInitializationEvent event) {
        OUT.println("GT6X_Mod: Doing Open Computers Recipes.");

        // This thing is broken already, just disable it
        CR.delate(ST.make(MD.OC, "disassembler", 1));

        for (int meta : new int[]{ 19, 23, 24, 25, 26, 29, 42, 43 }) {
            CR.delate(MD.OC, "item", meta);
        }

        CRx.overrideShapelessCompat(ST.make(MD.OC, "cable", 4, 0), OP.cableGt01.dat(ANY.Cu), OP.cableGt01.dat(ANY.Cu), OP.cableGt01.dat(ANY.Cu), OP.cableGt01.dat(ANY.Cu));
        CRx.overrideShapelessCompat(ST.make(MD.OC, "powerConverter", 1, 0), MTEx.gt6Registry.getItem(10041), ST.make(MD.OC, "cable", 4, 0));

        // Solar generator upgrade
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 34), MTEx.gt6Registry.getItem(10050));

        // Remove shaped raw crafting recipes of storage mediums, but not shapeless (for formatting, copying, dyeing, ...)
        CR.remout(ST.make(MD.OC, "eeprom", 1, 0), true, true, false, true);
        CR.remout(ST.make(MD.OC, "item", 1, 4), true, true, false, true); // Floppy
        CR.remout(ST.make(MD.OC, "item", 1, 5), true, true, false, true); // HDD 1-3
        CR.remout(ST.make(MD.OC, "item", 1, 6), true, true, false, true);
        CR.remout(ST.make(MD.OC, "item", 1, 7), true, true, false, true);

        CR.shapeless(ST.make(MD.OC, "eeprom", 1, 0), new Object[]{ FLASH_NAMES[0] });
        CR.shaped(ST.make(MD.OC, "item", 1, 4), CR.DEF, "APS", "CDC", " P ", 'A', OP.plateTiny.dat(ANY.Plastic), 'P', OD.paperEmpty, 'S', OP.springSmall.dat(MT.Al), 'C', OP.casingSmall.dat(ANY.Plastic), 'D', "oc:materialDisk"); // Floppy
        CR.shapeless(ST.make(MD.OC, "item", 1, 5), new Object[]{ ILx.HDDs[0] });
        CR.shapeless(ST.make(MD.OC, "item", 1, 6), new Object[]{ ILx.HDDs[1] });
        CR.shapeless(ST.make(MD.OC, "item", 1, 7), new Object[]{ ILx.HDDs[2] });

        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 29), ILx.CPUs[0]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 42), ILx.CPUs[1]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 43), ILx.CPUs[2]);

        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 8 ), ILx.GraphicsCards[0]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 9 ), ILx.GraphicsCards[1]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 10), ILx.GraphicsCards[2]);

        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 1 ), ILx.RAMSticks[0]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 2 ), ILx.RAMSticks[1]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 38), ILx.RAMSticks[2]);

        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 101), ILx.SoCs[1]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 102), ILx.SoCs[2]);

        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 70), ILx.Motherboards[0]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 71), ILx.Motherboards[1]);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "item", 1, 72), ILx.Motherboards[2]);

        CR.shaped(ST.make(MD.OC, "capacitor", 1, 0), CR.DEF_REM, "W W", "COC", "CPC", 'W', OP.wireGt01.dat(MT.Cu), 'C', CAPACITOR_NAME, 'O', OP.casingMachine.dat(MT.Al), 'P', OD_CIRCUITS[2]);

        // MCU case
        CR.shaped(ST.make(MD.OC, "item", 1, 82), CR.DEF_REM, "iUW", "QOQ", "CPR", 'U', IC_NAMES[0], 'W', OP.wireGt01.dat(MT.Cu), 'C', CAPACITOR_NAME, 'R', RESISTOR_NAME, 'Q', TRANSISTOR_NAME, 'O', OP.casingMachine.dat(MT.SteelGalvanized), 'P', IL.Circuit_Plate_Copper);
        CR.shaped(ST.make(MD.OC, "item", 1, 86), CR.DEF_REM, "iUW", "QOQ", "CPR", 'U', IC_NAMES[1], 'W', OP.wireGt01.dat(MT.Au), 'C', CAPACITOR_NAME, 'R', RESISTOR_NAME, 'Q', TRANSISTOR_NAME, 'O', OP.casingMachine.dat(MT.Al), 'P', IL.Circuit_Plate_Gold);
        RMx.Soldering.addRecipeX(true, 16, 128, ST.array(ST.tag(7), OP.casingMachine.mat(MT.SteelGalvanized, 1), IL.Circuit_Plate_Copper.get(1), OP.wireGt01.mat(MT.Cu, 1), ILx.ICs[0].get(1), ILx.Transistor_SMD.get(2), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ST.make(MD.OC, "item", 1, 82));
        RMx.Soldering.addRecipeX(true, 16, 128, ST.array(ST.tag(7), OP.casingMachine.mat(MT.Al             , 1), IL.Circuit_Plate_Gold  .get(1), OP.wireGt01.mat(MT.Au, 1), ILx.ICs[1].get(1), ILx.Transistor_SMD.get(2), ILx.Capacitor_SMD.get(1), ILx.Resistor_SMD.get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ST.make(MD.OC, "item", 1, 86));

        // Drone case
        CR.shaped(ST.make(MD.OC, "item", 1, 83), CR.DEF_REM, "RUR", "MCM", "RBR", 'R', OP.rotor.dat(ANY.Plastic), 'U', IC_NAMES[0], 'M', IL.MOTORS[1], 'C', ST.make(MD.OC, "item", 1, 82), 'B', IL.Battery_LiCoO2_LV);
        CR.shaped(ST.make(MD.OC, "item", 1, 87), CR.DEF_REM, "RUR", "MCM", "RBR", 'R', OP.rotor.dat(ANY.Plastic), 'U', IC_NAMES[1], 'M', IL.MOTORS[2], 'C', ST.make(MD.OC, "item", 1, 86), 'B', IL.Battery_LiCoO2_MV);

        // Wireless network cards
        CR.shaped(ST.make(MD.OC, "item", 1, 113), CR.DEF_REM, " i ", "URN", "APL", 'U', IC_NAMES[0], 'R', RESISTOR_NAME, 'N', OP.nugget.dat(MT.Al), 'A', OP.plateTiny.dat(MT.Al), 'P', ILx.Circuit_Plate_Copper_Long, 'L', OP.stick.dat(MT.Al));
        CR.shaped(ST.make(MD.OC, "item", 1, 13 ), CR.DEF_REM, " i ", "URN", "APL", 'U', IC_NAMES[1], 'R', RESISTOR_NAME, 'N', OP.nugget.dat(MT.Al), 'A', OP.plateTiny.dat(MT.Al), 'P', ILx.Circuit_Plate_Gold_Long, 'L', OP.stick.dat(MT.Al));

        // Ram tier X.5
        CR.shaped(ST.make(MD.OC, "item", 1, 50), CR.DEF_REM, "RRR", "RRR", "iPI", 'R', ILx.DRAMChips[0], 'P', ILx.Circuit_Plate_Copper_Long, 'I', IC_NAMES[0]);
        CR.shaped(ST.make(MD.OC, "item", 1, 3 ), CR.DEF_REM, "RRR", "RRR", "iPI", 'R', ILx.DRAMChips[1], 'P', ILx.Circuit_Plate_Gold_Long, 'I', IC_NAMES[1]);
        CR.shaped(ST.make(MD.OC, "item", 1, 39), CR.DEF_REM, "RRR", "RRR", "iPI", 'R', ILx.DRAMChips[2], 'P', ILx.Circuit_Plate_Platinum_Long, 'I', IC_NAMES[2]);
        RMx.Soldering.addRecipeX(true, 16, 72, ST.array(ST.tag(12), ILx.Circuit_Plate_Copper_Long   .get(1), ILx.DRAMChips[0].get(6), ILx.ICs[0].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ST.make(MD.OC, "item", 1, 50));
        RMx.Soldering.addRecipeX(true, 16, 72, ST.array(ST.tag(12), ILx.Circuit_Plate_Gold_Long     .get(1), ILx.DRAMChips[1].get(6), ILx.ICs[1].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ST.make(MD.OC, "item", 1, 3 ));
        RMx.Soldering.addRecipeX(true, 16, 72, ST.array(ST.tag(12), ILx.Circuit_Plate_Platinum_Long .get(1), ILx.DRAMChips[2].get(6), ILx.ICs[2].get(1)), MTx.SolderingPaste.liquid(U2, true), NF, ST.make(MD.OC, "item", 1, 39));

        // Computer cases
        CR.shaped(ST.make(MD.OC, "case1", 1, 0), CR.DEF_REM, " dS", "SPS", "SMC", 'M', ILx.Motherboards[0], 'P', ILx.Thermal_Paste, 'C', ILx.ComputerCase, 'S', OP.screw.dat(MT.StainlessSteel));
        CR.shaped(ST.make(MD.OC, "case2", 1, 0), CR.DEF_REM, " dS", "SPS", "SMC", 'M', ILx.Motherboards[1], 'P', ILx.Thermal_Paste, 'C', ILx.ComputerCase, 'S', OP.screw.dat(MT.StainlessSteel));
        CR.shaped(ST.make(MD.OC, "case3", 1, 0), CR.DEF_REM, " dS", "SPS", "SMC", 'M', ILx.Motherboards[2], 'P', ILx.Thermal_Paste, 'C', ILx.ComputerCase, 'S', OP.screw.dat(MT.StainlessSteel));
        // Servers
        CR.shaped(ST.make(MD.OC, "item", 1, 45), CR.DEF_REM, "dP ", "SMS", "SCS", 'M', ILx.Motherboards[0], 'P', ILx.Thermal_Paste, 'C', ILx.ComputerCase, 'S', OP.screw.dat(MT.StainlessSteel));
        CR.shaped(ST.make(MD.OC, "item", 1, 46), CR.DEF_REM, "dP ", "SMS", "SCS", 'M', ILx.Motherboards[1], 'P', ILx.Thermal_Paste, 'C', ILx.ComputerCase, 'S', OP.screw.dat(MT.StainlessSteel));
        CR.shaped(ST.make(MD.OC, "item", 1, 40), CR.DEF_REM, "dP ", "SMS", "SCS", 'M', ILx.Motherboards[2], 'P', ILx.Thermal_Paste, 'C', ILx.ComputerCase, 'S', OP.screw.dat(MT.StainlessSteel));
        // Card base
        CR.shaped(ST.make(MD.OC, "item", 1, 33), CR.DEF_REM, "AB ", 'A', OP.plateTiny.dat(MT.Al), 'B', ILx.Circuit_Plate_Copper_Long);

        // Screens
        CR.shaped(ST.make(MD.OC, "screen1", 1, 0), CR.DEF_REM, "CBC", "PSP", "WMT", 'B', MTEx.gt6Registry.getItem(32711), 'P', OD_CIRCUITS[2], 'S', ILx.CRT_Black_White, 'M', OP.casingMachine.dat(ANY.Plastic)     , 'T', TRANSISTOR_NAME, 'C', CAPACITOR_NAME);
        CR.shaped(ST.make(MD.OC, "screen2", 1, 0), CR.DEF_REM, "CBC", "PSP", "WMT", 'B', MTEx.gt6Registry.getItem(32711), 'P', OD_CIRCUITS[3], 'S', ILx.CRT_RGB        , 'M', OP.casingMachine.dat(MT.Polycarbonate), 'T', TRANSISTOR_NAME, 'C', CAPACITOR_NAME);
        CRx.overrideShapelessCompat(ST.make(MD.OC, "screen3", 1, 0), ILx.LCDMonitor);
    }
}
