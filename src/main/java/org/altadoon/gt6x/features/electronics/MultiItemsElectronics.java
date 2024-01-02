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

    public static final String[] IC_NAMES = { "gt6x:ic0", "gt6x:ic1", "gt6x:ic2" };
    public static final String[] RAM_NAMES = { "gt6x:ramstick0", "gt6x:ramstick1", "gt6x:ramstick2" };
    public static final String[] GRAPHICS_CARD_NAMES = { "gt6x:graphicscard0", "gt6x:graphicscard1", "gt6x:graphicscard2" };
    public static final String[] CPU_NAMES = { "gt6x:cpu0", "gt6x:cpu1", "gt6x:cpu2" };
    public static final String[] HDD_NAMES = { "gt6x:hdd0", "gt6x:hdd1", "gt6x:hdd2" };
    public static final String[] PC_NAMES = { "gt6x:PC0", "gt6x:PC1", "gt6x:PC2" };
    public static final String[] SOC_NAMES = { "gt6x:SoC0", "gt6x:SoC1", "gt6x:SoC2" };
    public static final String[] FLASH_NAMES = { "gt6x:Flash0", "gt6x:Flash1", "gt6x:Flash2" };


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
        ILx.ICs[0].set(addItem(300, "IC (Elite) "  , tooltip, IC_NAMES[0]));
        ILx.ICs[1].set(addItem(301, "IC (Master)"  , tooltip, IC_NAMES[1]));
        ILx.ICs[2].set(addItem(302, "IC (Ultimate)", tooltip, IC_NAMES[2]));

        tooltip = "Needs to be soldered onto a RAM stick or GPU";
        ILx.DRAMChips[0].set(addItem(320, "DRAM chip (48 KB)", tooltip));
        ILx.DRAMChips[1].set(addItem(321, "DRAM chip (8 MB)", tooltip));
        ILx.DRAMChips[2].set(addItem(322, "DRAM chip (1 GB)", tooltip));

        tooltip = "Graphics Processing Unit";
        ILx.GPUs[0].set(addItem(330, "GPU (Tier 1)", tooltip));
        ILx.GPUs[1].set(addItem(331, "GPU (Tier 2)", tooltip));
        ILx.GPUs[2].set(addItem(332, "GPU (Tier 3)", tooltip));

        tooltip = "Needs to be soldered onto a USB stick or SSD";
        ILx.FlashChips[0].set(addItem(340, "Flash memory (16 MB)", tooltip, FLASH_NAMES[0]));
        ILx.FlashChips[1].set(addItem(341, "Flash memory (1 GB)", tooltip, FLASH_NAMES[1]));
        ILx.FlashChips[2].set(addItem(342, "Flash memory (256 GB)", tooltip, FLASH_NAMES[2]));

        tooltip = "System on a Chip";
        ILx.SoCs[0].set(addItem(350, "SoC (Tier 1)", tooltip, SOC_NAMES[0]));
        ILx.SoCs[1].set(addItem(351, "SoC (Tier 2)", tooltip, SOC_NAMES[1]));
        ILx.SoCs[2].set(addItem(352, "SoC (Tier 3)", tooltip, SOC_NAMES[2]));

        // Computer parts
        tooltip = "Central Processing Unit";
        ILx.CPUs[0].set(addItem(310, "CPU (1 MHz)", tooltip, CPU_NAMES[0]));
        ILx.CPUs[1].set(addItem(311, "CPU (50 MHz Dual Core)", tooltip, CPU_NAMES[1]));
        ILx.CPUs[2].set(addItem(312, "CPU (2 GHz Quad Core)", tooltip, CPU_NAMES[2]));

        tooltip = "A computer's Random Access Memory module";
        ILx.RAMSticks[0].set(addItem(400, "RAM stick (192 KB)", tooltip, RAM_NAMES[0]));
        ILx.RAMSticks[1].set(addItem(401, "RAM stick (32 MB)", tooltip, RAM_NAMES[1]));
        ILx.RAMSticks[2].set(addItem(402, "RAM stick (4 GB)", tooltip, RAM_NAMES[2]));

        ILx.GraphicsCards[0].set(addItem(410, "Graphics Card (Tier 1)", "Can run Doom", GRAPHICS_CARD_NAMES[0]));
        ILx.GraphicsCards[1].set(addItem(411, "Graphics Card (Tier 2)", "Can run Minecraft", GRAPHICS_CARD_NAMES[1]));
        ILx.GraphicsCards[2].set(addItem(412, "Graphics Card (Tier 3)", "Can run Crysis", GRAPHICS_CARD_NAMES[2]));

        ILx.Al_Disk.set(addItem(420, "Aluminium Disk", "", new OreDictItemData(MT.Al, U12)));

        tooltip = "Records data magnetically";
        ILx.Hard_Disk.set(addItem(430, "Hard Disk Platter", tooltip, new OreDictItemData(MT.Al, U12, MT.Fe2O3, U24)));
        ILx.Hard_Disk_Advanced.set(addItem(431, "Hard Disk Platter (Advanced)", tooltip), new OreDictItemData(MT.Al, U12, MTx.CoPtCr, U24));

        tooltip = "Hard Disk Drive, stores data";
        ILx.HDDs[0].set(addItem(440, "HDD (100 MB)", tooltip, HDD_NAMES[0]));
        ILx.HDDs[1].set(addItem(441, "HDD (32 GB)", tooltip, HDD_NAMES[1]));
        ILx.HDDs[2].set(addItem(442, "SSD (1 TB)", "Solid State Drive, stores data", HDD_NAMES[2]));

        ILx.CPU_Fan.set(addItem(450, "Computer Fan", "I'm not a big fan of overheating my PC"));
        ILx.ComputerCase.set(addItem(452, "Computer Case", "Base of a computer"));

        tooltip = "Used to connect computer parts";
        ILx.Motherboards[0].set(addItem(460, "Motherboard (Tier 1)", tooltip));
        ILx.Motherboards[1].set(addItem(461, "Motherboard (Tier 2)", tooltip));
        ILx.Motherboards[2].set(addItem(462, "Motherboard (Tier 3)", tooltip));

        tooltip = "Used for running Minecraft and other software";
        ILx.PCs[0].set(addItem(490, "Computer (Tier 1)", tooltip, PC_NAMES[0]));
        ILx.PCs[1].set(addItem(491, "Computer (Tier 2)", tooltip, PC_NAMES[1]));
        ILx.PCs[2].set(addItem(492, "Computer (Tier 3)", tooltip, PC_NAMES[2]));
    }
}
