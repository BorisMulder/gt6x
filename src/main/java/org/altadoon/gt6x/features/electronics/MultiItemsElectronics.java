package org.altadoon.gt6x.features.electronics;

import gregapi.code.ItemStackContainer;
import gregapi.data.*;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.util.OM;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.items.ILx;

import static gregapi.data.CS.*;

public class MultiItemsElectronics extends MultiItemRandom {
    public static MultiItemsElectronics instance;

    public static final String ELECTRONTUBE_NAME = "gt6x:electrontube";
    public static final String CAPACITOR_NAME = "gt6x:capacitor";
    public static final String RESISTOR_NAME = "gt6x:resistor";
    public static final String TRANSISTOR_NAME = "gt6x:transistor";
    public static final String IC_NAME = "gt6x:integratedcircuit";


    public MultiItemsElectronics(String modID, String unlocalized) {
        super(modID, unlocalized);
    }

    @Override
    public void addItems() {
        String tooltip;
        // tier 0 (primitive) and 7+ (quantum) are currently not used.
        for (int tier = 1; tier < 7; tier++) {
            // size 0 are normal circuit boards, which are already in GT6, so we start from 1.
            for (int size = 1; size < 3; size++) {
                ILx.PCBs[tier][size].set(addItem(
                    tier + 10 * size, ILx.CIRCUIT_SIZE_NAMES[size] + " T" + tier + " (" + ILx.CIRCUIT_TIER_NAMES[tier] + ")", null, MT.DATA.CIRCUITS[tier], OD_CIRCUITS[tier], TC.stack(TC.COGNITIO, 2)
                ));
            }
        }
        // item IDs 0-100 are reserved for chips of tier 0-9

        ILx.Electrode_Molybdenum.set(addItem(101, "Electrode (Molybdenum)", "Needs Glass Tube"), new OreDictItemData(MT.Mo, 5*U4, MT.Redstone, U2));
        ILx.Electrode_Tungsten.set(addItem(102, "Electrode (Tungsten)", "Needs Glass Tube"), new OreDictItemData(MT.W, 5*U4, MT.Redstone, U2));
        ILx.ElectronTube_Molybdenum.set(addItem(103, "Electron Tube (Molybdenum)", "An old-fashoned Vacuum Tube", ELECTRONTUBE_NAME, new OreDictItemData(MT.Mo, 5*U4, MT.Redstone, U2, MT.Glass, U8)));
        ILx.ElectronTube_Tungsten.set(addItem(104, "Electron Tube (Tungsten)", "An old-fashoned Vacuum Tube", ELECTRONTUBE_NAME, new OreDictItemData(ANY.W, 5*U4, MT.Redstone, U2, MT.Glass, U8)));

        tooltip = "Can be soldered by hand onto a PCB";
        ILx.Resistor_ThroughHole.set(addItem(110, "Through-Hole Resistor", tooltip, RESISTOR_NAME));
        ILx.Capacitor_ThroughHole.set(addItem(111, "Through-Hole Capacitor", tooltip, CAPACITOR_NAME));
        ILx.Transistor_ThroughHole.set(addItem(112, "Through-Hole Transistor", tooltip, TRANSISTOR_NAME));

        ILx.Capacitor_Tantalum.set(addItem(117, "Tantalum Capacitor", "Accumulates Charge"));
        ILx.Resistor_Metal_Film.set(addItem(116, "Metal Film Resistor Board", "Needs Overcoat, Plating and Dicing"));

        tooltip = "Surface-Mounted Devices are smaller and can be soldered by hand or using machines";
        ILx.Resistor_SMD.set(addItem(120, "SMD-Resistor", tooltip, RESISTOR_NAME));
        ILx.Capacitor_SMD.set(addItem(121, "SMD-Capacitor", tooltip, CAPACITOR_NAME));
        ILx.Transistor_SMD.set(addItem(122, "SMD-Transistor", tooltip, TRANSISTOR_NAME));

        tooltip = "Fire-resistant board used to make PCBs";
        ILx.FR1_Board.set(addItem(130, "Phenolic Paper Board (FR-1)", tooltip), new OreDictItemData(MTx.PF, U));
        ILx.FR4_Board.set(addItem(131, "Fibreglass-reinforced Epoxy Board (FR-4)", tooltip, new OreDictItemData(MTx.Epoxy, U)));

        tooltip = "Needs to be etched to create traces";
        ILx.CCL      .set(addItem(140, "Copper-Clad Laminate"        , tooltip), new OreDictItemData(MT.Cu, U));
        ILx.CCL_SMALL.set(addItem(141, "Copper-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Cu, U2));
        ILx.CCL_TINY .set(addItem(142, "Copper-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Cu, U4));
        ILx.CCL_LONG .set(addItem(143, "Copper-Clad Laminate (Long)" , tooltip), new OreDictItemData(MT.Cu, U2));
        ILx.GCL      .set(addItem(150, "Gold-Clad Laminate"        , tooltip), new OreDictItemData(MT.Au, U, MTx.Epoxy, U));
        ILx.GCL_SMALL.set(addItem(151, "Gold-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Au, U2, MTx.Epoxy, U2));
        ILx.GCL_TINY .set(addItem(152, "Gold-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U4));
        ILx.GCL_LONG .set(addItem(153, "Gold-Clad Laminate (Long)" , tooltip), new OreDictItemData(MT.Au, U2, MTx.Epoxy, U2));
        ILx.PCL      .set(addItem(160, "Platinum-Clad Laminate"        , tooltip), new OreDictItemData(MT.Pt, U, MTx.Epoxy, U));
        ILx.PCL_SMALL.set(addItem(161, "Platinum-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U2));
        ILx.PCL_TINY .set(addItem(162, "Platinum-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U4));
        ILx.PCL_LONG .set(addItem(163, "Platinum-Clad Laminate (Long)" , tooltip), new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U2));

        tooltip = "Components can be soldered onto this";
        ItemStack board;
        board = IL.Circuit_Plate_Copper.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Cu, U2)); LH.add(getUnlocalizedName(board) + ".tooltip", tooltip);
        ILx.Circuit_Plate_Copper_Small.set(addItem(170, "Small Circuit Plate (Copper)", tooltip), new OreDictItemData(MT.Cu, U4));
        ILx.Circuit_Plate_Copper_Tiny.set(addItem(171, "Tiny Circuit Plate (Copper)", tooltip), new OreDictItemData(MT.Cu, U8));
        ILx.Circuit_Plate_Copper_Long.set(addItem(172, "Expansion Card Base (Copper)", tooltip), new OreDictItemData(MT.Cu, U4));

        board = IL.Circuit_Plate_Gold.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Au, U2, MTx.Epoxy, U)); LH.add(getUnlocalizedName(board) + ".tooltip", tooltip);
        ILx.Circuit_Plate_Gold_Small.set(addItem(180, "Small Circuit Plate (Gold)", tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U2));
        ILx.Circuit_Plate_Gold_Tiny.set(addItem(181, "Tiny Circuit Plate (Gold)", tooltip), new OreDictItemData(MT.Cu, U8, MTx.Epoxy, U4));
        ILx.Circuit_Plate_Gold_Long.set(addItem(182, "Expansion Card Base (Gold)", tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U2));

        board = IL.Circuit_Plate_Platinum.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U)); LH.add(getUnlocalizedName(board) + ".tooltip", tooltip);
        ILx.Circuit_Plate_Platinum_Small.set(addItem(190, "Small Circuit Plate (Platinum)", tooltip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U2));
        ILx.Circuit_Plate_Platinum_Tiny.set(addItem(191, "Tiny Circuit Plate (Platinum)", tooltip), new OreDictItemData(MT.Pt, U8, MTx.Epoxy, U4));
        ILx.Circuit_Plate_Platinum_Long.set(addItem(192, "Expansion Card Base (Platinum)", tooltip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U2));

        ILx.GlassFibres.set(addItem(200, "Glass Fibre", "Small threads of glass"), new OreDictItemData(MT.Glass, U8));

        ILx.PlatinumBushing.set(addItem(201, "Platinum Bushing", "Basically a very expensive cheese grater"), new OreDictItemData(MT.Pt, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.PlatinumBushing.get(1)), (byte)45);

        ILx.EtchMask_Trace      .set(addItem(202, "Etching Mask (Circuit Trace)"      , "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        ILx.EtchMask_Trace_Small.set(addItem(203, "Etching Mask (Small Circuit Trace)", "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        ILx.EtchMask_Trace_Tiny .set(addItem(204, "Etching Mask (Tiny Circuit Trace)" , "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        ILx.EtchMask_Trace_Long .set(addItem(205, "Etching Mask (Expansion Card)"     , "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));

        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace.get(1)), (byte)45);
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace_Small.get(1)), (byte)45);
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace_Tiny.get(1)), (byte)45);
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace_Long.get(1)), (byte)45);

        ILx.Comp_Laser_Gas_N    .set(addItem(210, "Nitrogen Laser Emitter"        , "Purpose: Near-UV Optical Appliances"   , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_KrF  .set(addItem(211, "Krypton Fluoride Laser Emitter", "Purpose: Middle-UV Optical Appliances" , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_ArF  .set(addItem(212, "Argon Fluoride Laser Emitter"  , "Purpose: Far-UV Optical Appliances"    , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Molten_Sn.set(addItem(213, "Molten Tin Laser Emitter"      , "Purpose: Extreme-UV Optical Appliances", TC.stack(TC.LUX, 2), TC.stack(TC.METALLUM, 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));

        // Packaged ICs
        tooltip = "Integrated Circuit";
        ILx.ICs[0].set(addItem(300, "IC (Elite) "  , tooltip, IC_NAME));
        ILx.ICs[1].set(addItem(301, "IC (Master)"  , tooltip, IC_NAME));
        ILx.ICs[2].set(addItem(302, "IC (Ultimate)", tooltip, IC_NAME));

        tooltip = "Central Processing Unit";
        ILx.CPUs[0].set(addItem(310, "CPU (1 MHz)", tooltip));
        ILx.CPUs[1].set(addItem(311, "CPU (50 MHz)", tooltip));
        ILx.CPUs[2].set(addItem(312, "CPU (2 GHz)", tooltip));

        tooltip = "Needs to be soldered onto a RAM stick";
        ILx.DRAMChips[0].set(addItem(320, "DRAM chip (48 KB)", tooltip));
        ILx.DRAMChips[1].set(addItem(321, "DRAM chip (8 MB)", tooltip));
        ILx.DRAMChips[2].set(addItem(322, "DRAM chip (1 GB)", tooltip));

        tooltip = "Needs to be soldered onto a GPU";
        ILx.GPUChips[0].set(addItem(330, "GPU chip (Tier 1)", tooltip));
        ILx.GPUChips[1].set(addItem(331, "GPU chip (Tier 2)", tooltip));
        ILx.GPUChips[2].set(addItem(332, "GPU chip (Tier 3)", tooltip));

        // Computer parts
        tooltip = "A computer's memory module";
        ILx.RAMSticks[0].set(addItem(400, "RAM stick (192 KB)", tooltip));
        ILx.RAMSticks[1].set(addItem(401, "RAM stick (32 MB)", tooltip));
        ILx.RAMSticks[2].set(addItem(402, "RAM stick (4 GB)", tooltip));

        ILx.GPUs[0].set(addItem(410, "GPU (Tier 1)", "Graphics Processing Unit, can run Space Invaders"));
        ILx.GPUs[1].set(addItem(411, "GPU (Tier 2)", "Graphics Processing Unit, can run Doom"));
        ILx.GPUs[2].set(addItem(412, "GPU (Tier 3)", "Graphics Processing Unit, can run Crysis"));


        ILx.Al_Disk.set(addItem(420, "Aluminium Disk", "", new OreDictItemData(MT.Al, U12)));
        ILx.Hard_Disk.set(addItem(430, "Hard Disk", "A HDD Platter", new OreDictItemData(MT.Al, U12, MTx.CoPtCr, U24)));

        tooltip = "Hard Disk Drive, stores data";
        ILx.HDDs[0].set(addItem(440, "HDD (100 MB)", tooltip));
        ILx.HDDs[1].set(addItem(441, "HDD (32 GB)", tooltip));
        ILx.HDDs[2].set(addItem(442, "HDD (1 TB)", tooltip));

        ILx.CPU_Fan.set(addItem(450, "Computer Fan", "For preventing overheating your computer"));
        ILx.Motherboard.set(addItem(451, "Motherboard", "Used to mount and connect computer parts"));
        ILx.ComputerCase.set(addItem(452, "Computer Case", "Heart of a computer"));

        tooltip = "Can run minecraft";
        ILx.PCs[0].set(addItem(490, "Computer (Tier 1)", tooltip));
        ILx.PCs[1].set(addItem(491, "Computer (Tier 2)", tooltip));
        ILx.PCs[2].set(addItem(492, "Computer (Tier 3)", tooltip));


    }
}
