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
        ILx.GCL      .set(addItem(150, "Gold-Clad Laminate"        , tooltip), new OreDictItemData(MT.Au, U, MTx.Epoxy, U));
        ILx.GCL_SMALL.set(addItem(151, "Gold-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Au, U2, MTx.Epoxy, U2));
        ILx.GCL_TINY .set(addItem(152, "Gold-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U4));
        ILx.GCL_LONG .set(addItem(153, "Gold-Clad Laminate (Long)" , tooltip), new OreDictItemData(MT.Au, U2, MTx.Epoxy, U2));
        ILx.PCL      .set(addItem(160, "Platinum-Clad Laminate"        , tooltip), new OreDictItemData(MT.Pt, U, MTx.Epoxy, U));
        ILx.PCL_SMALL.set(addItem(161, "Platinum-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U2));
        ILx.PCL_TINY .set(addItem(162, "Platinum-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U4));

        tooltip = "You need to solder components onto it";
        ItemStack board;
        board = IL.Circuit_Plate_Copper.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Cu, U2)); LH.add(getUnlocalizedName(board) + ".tooltip", tooltip);
        ILx.Circuit_Plate_Copper_Small.set(addItem(170, "Small Circuit Plate (Copper)", tooltip), new OreDictItemData(MT.Cu, U4));
        ILx.Circuit_Plate_Copper_Tiny.set(addItem(171, "Tiny Circuit Plate (Copper)", tooltip), new OreDictItemData(MT.Cu, U8));
        board = IL.Circuit_Plate_Gold.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Au, U2, MTx.Epoxy, U)); LH.add(getUnlocalizedName(board) + ".tooltip", tooltip);
        ILx.Circuit_Plate_Gold_Small.set(addItem(172, "Small Circuit Plate (Gold)", tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U2));
        ILx.Circuit_Plate_Gold_Tiny.set(addItem(173, "Tiny Circuit Plate (Gold)", tooltip), new OreDictItemData(MT.Cu, U8, MTx.Epoxy, U4));
        board = IL.Circuit_Plate_Platinum.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U)); LH.add(getUnlocalizedName(board) + ".tooltip", tooltip);
        ILx.Circuit_Plate_Platinum_Small.set(addItem(174, "Small Circuit Plate (Platinum)", tooltip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U2));
        ILx.Circuit_Plate_Platinum_Tiny.set(addItem(175, "Tiny Circuit Plate (Platinum)", tooltip), new OreDictItemData(MT.Pt, U8, MTx.Epoxy, U4));
        ILx.Circuit_Plate_Gold_RAM.set(addItem(176, "RAM Circuit Plate (Gold)", tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U2));

        ILx.Capacitor_Tantalum.set(addItem(180, "Tantalum Capacitor", "Accumulates Charge"));
        ILx.Resistor_Metal_Film.set(addItem(181, "Metal Film Resistor Board", "Needs Overcoat, Plating and Dicing"));

        ILx.GlassFibres.set(addItem(200, "Glass Fibre", "Small threads of glass"), new OreDictItemData(MT.Glass, U8));

        ILx.PlatinumBushing.set(addItem(201, "Platinum Bushing", "Basically a very expensive cheese grater"), new OreDictItemData(MT.Pt, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.PlatinumBushing.get(1)), (byte)45);

        ILx.EtchMask_Trace      .set(addItem(202, "Etching Mask (Circuit Trace)"      , "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        ILx.EtchMask_Trace_Small.set(addItem(203, "Etching Mask (Small Circuit Trace)", "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        ILx.EtchMask_Trace_Tiny .set(addItem(204, "Etching Mask (Tiny Circuit Trace)" , "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        ILx.EtchMask_Trace_RAM  .set(addItem(205, "Etching Mask (RAM Circuit Board)"  , "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));

        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace.get(1)), (byte)45);
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace_Small.get(1)), (byte)45);
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace_Tiny.get(1)), (byte)45);

        ILx.Comp_Laser_Gas_N    .set(addItem(210, "Nitrogen Laser Emitter"        , "Purpose: Near-UV Optical Appliances"   , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_KrF  .set(addItem(211, "Krypton Fluoride Laser Emitter", "Purpose: Middle-UV Optical Appliances" , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_ArF  .set(addItem(212, "Argon Fluoride Laser Emitter"  , "Purpose: Far-UV Optical Appliances"    , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Molten_Sn.set(addItem(213, "Molten Tin Laser Emitter"      , "Purpose: Extreme-UV Optical Appliances", TC.stack(TC.LUX, 2), TC.stack(TC.METALLUM, 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));

        // Photomasks
        ILx.Photomask_Raw.set(addItem(220, "Raw Photomask", "Needs Design", new OreDictItemData(MT.Glass, U, MT.Cr, U)));

        String tooltipPatterned = "Needs developer";
        String tooltipDeveloped = "Ready to be etched";
        String tooltipEtched = "Needs cleaning";
        String tooltipCleaned = "Used in Photolithography";

        ILx.Photomask_Patterned_PMOS_IC   .set(addItem(221, "Photomask (PMOS IC, patterned)"          , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_PMOS_IC   .set(addItem(222, "Photomask (PMOS IC, developed)"          , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_PMOS_IC      .set(addItem(223, "Photomask (PMOS IC, etched)"             , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_PMOS_IC             .set(addItem(224, "Photomask (PMOS IC)"                     , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_NMOS_IC   .set(addItem(225, "Photomask (NMOS IC, patterned)"          , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_NMOS_IC   .set(addItem(226, "Photomask (NMOS IC, developed)"          , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_NMOS_IC      .set(addItem(227, "Photomask (NMOS IC, etched)"             , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_NMOS_IC             .set(addItem(228, "Photomask (NMOS IC)"                     , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_CMOS_IC_1 .set(addItem(229, "Photomask (CMOS IC, stage I, patterned)" , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_CMOS_IC_1 .set(addItem(230, "Photomask (CMOS IC, stage I, developed)" , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_CMOS_IC_1    .set(addItem(231, "Photomask (CMOS IC, stage I, etched)"    , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_CMOS_IC_1           .set(addItem(232, "Photomask (CMOS IC, stage I)"            , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_CMOS_IC_2 .set(addItem(233, "Photomask (CMOS IC, stage II, patterned)", tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_CMOS_IC_2 .set(addItem(234, "Photomask (CMOS IC, stage II, developed)", tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_CMOS_IC_2    .set(addItem(235, "Photomask (CMOS IC, stage II, etched)"   , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_CMOS_IC_2           .set(addItem(236, "Photomask (CMOS IC, stage II)"           , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_VLSI_1    .set(addItem(237, "Photomask (VLSI, stage I, patterned)"    , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_VLSI_1    .set(addItem(238, "Photomask (VLSI, stage I, developed)"    , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_VLSI_1       .set(addItem(239, "Photomask (VLSI, stage I, etched)"       , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_VLSI_1              .set(addItem(240, "Photomask (VLSI, stage I)"               , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_VLSI_2    .set(addItem(241, "Photomask (VLSI, stage II, patterned)"   , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_VLSI_2    .set(addItem(242, "Photomask (VLSI, stage II, developed)"   , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_VLSI_2       .set(addItem(243, "Photomask (VLSI, stage II, etched)"      , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_VLSI_2              .set(addItem(244, "Photomask (VLSI, stage II)"              , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_MESFET    .set(addItem(245, "Photomask (MESFET, patterned)"           , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_MESFET    .set(addItem(246, "Photomask (MESFET, developed)"           , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_MESFET       .set(addItem(247, "Photomask (MESFET, etched)"              , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_MESFET              .set(addItem(248, "Photomask (MESFET)"                      , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_DRAM_1    .set(addItem(249, "Photomask (DRAM, stage I, patterned)"    , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_DRAM_1    .set(addItem(250, "Photomask (DRAM, stage I, developed)"    , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_DRAM_1       .set(addItem(251, "Photomask (DRAM, stage I, etched)"       , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_DRAM_1              .set(addItem(252, "Photomask (DRAM, stage I)"               , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Photomask_Patterned_DRAM_2    .set(addItem(253, "Photomask (DRAM, stage II, patterned)"   , tooltipPatterned, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Developed_DRAM_2    .set(addItem(254, "Photomask (DRAM, stage II, developed)"   , tooltipDeveloped, new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        ILx.Photomask_Etched_DRAM_2       .set(addItem(255, "Photomask (DRAM, stage II, etched)"      , tooltipEtched   , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_DRAM_2              .set(addItem(256, "Photomask (DRAM, stage II)"              , tooltipCleaned  , new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Wafer_GaAs_SiN_layered.set(addItem(299, "SiN-capped GaAs wafer", "Gallium Arsenide Wafer with Silicon Nitride cap layer", new OreDictItemData(MTx.GaAs, U)));

        // Wafers, ICs, etc.
        tooltip = "Needs developer";
        ILx.Wafer_Patterned_PMOS_IC   .set(addItem(300, "PMOS IC Wafer (patterned)"           , tooltip));
        ILx.Wafer_Patterned_NMOS_IC   .set(addItem(310, "NMOS IC Wafer (patterned)"           , tooltip));
        ILx.Wafer_Patterned_CMOS_IC_1 .set(addItem(320, "CMOS IC Wafer (patterned, stage I)"  , tooltip));
        ILx.Wafer_Patterned_CMOS_IC_2 .set(addItem(326, "CMOS IC Wafer (patterned, stage II)" , tooltip));
        ILx.Wafer_Patterned_VLSI_1    .set(addItem(340, "VLSI Wafer (patterned, stage I)"     , tooltip));
        ILx.Wafer_Patterned_VLSI_2    .set(addItem(346, "VLSI Wafer (patterned, stage II)"    , tooltip));
        ILx.Wafer_Patterned_MESFET    .set(addItem(360, "MESFET Wafer (patterned)"            , tooltip));
        ILx.Wafer_Patterned_DRAM_1    .set(addItem(370, "DRAM Wafer (patterned, stage I)"     , tooltip));
        ILx.Wafer_Patterned_DRAM_2    .set(addItem(376, "DRAM Wafer (patterned, stage II)"    , tooltip));

        tooltip = "Ready to be etched";
        ILx.Wafer_Developed_PMOS_IC   .set(addItem(301, "PMOS IC Wafer (developed)"           , tooltip));
        ILx.Wafer_Developed_NMOS_IC   .set(addItem(311, "NMOS IC Wafer (developed)"           , tooltip));
        ILx.Wafer_Developed_CMOS_IC_1 .set(addItem(321, "CMOS IC Wafer (developed, stage I)"  , tooltip));
        ILx.Wafer_Developed_CMOS_IC_2 .set(addItem(327, "CMOS IC Wafer (developed, stage II)" , tooltip));
        ILx.Wafer_Developed_VLSI_1    .set(addItem(341, "VLSI Wafer (developed, stage I)"     , tooltip));
        ILx.Wafer_Developed_VLSI_2    .set(addItem(347, "VLSI Wafer (developed, stage II)"    , tooltip));
        ILx.Wafer_Developed_MESFET    .set(addItem(361, "MESFET Wafer (developed)"            , tooltip));
        ILx.Wafer_Developed_DRAM_1    .set(addItem(371, "DRAM Wafer (developed, stage I)"     , tooltip));
        ILx.Wafer_Developed_DRAM_2    .set(addItem(377, "DRAM Wafer (developed, stage II)"    , tooltip));

        tooltip = "Could use some extra dopant";
        ILx.Wafer_Etched_PMOS_IC   .set(addItem(302, "PMOS IC Wafer (etched)"           , tooltip));
        ILx.Wafer_Etched_NMOS_IC   .set(addItem(312, "NMOS IC Wafer (etched)"           , tooltip));
        ILx.Wafer_Etched_CMOS_IC_1 .set(addItem(322, "CMOS IC Wafer (etched, stage I)"  , tooltip));
        ILx.Wafer_Etched_CMOS_IC_2 .set(addItem(328, "CMOS IC Wafer (etched, stage II)" , tooltip));
        ILx.Wafer_Etched_VLSI_1    .set(addItem(342, "VLSI Wafer (etched, stage I)"     , tooltip));
        ILx.Wafer_Etched_VLSI_2    .set(addItem(348, "VLSI Wafer (etched, stage II)"    , tooltip));
        ILx.Wafer_Etched_MESFET    .set(addItem(362, "MESFET Wafer (etched)"            , tooltip));
        ILx.Wafer_Etched_DRAM_1    .set(addItem(372, "DRAM Wafer (etched, stage I)"     , tooltip));
        ILx.Wafer_Etched_DRAM_2    .set(addItem(378, "DRAM Wafer (etched, stage II)"    , tooltip));

        tooltip = "Needs cleaning";
        ILx.Wafer_Doped_PMOS_IC   .set(addItem(303, "PMOS IC Wafer (doped)"         , tooltip));
        ILx.Wafer_Doped_NMOS_IC   .set(addItem(313, "NMOS IC Wafer (doped)"         , tooltip));
        ILx.Wafer_Doped_CMOS_IC_1 .set(addItem(323, "CMOS IC Wafer (p-wells)"       , tooltip));
        ILx.Wafer_Doped_CMOS_IC_2 .set(addItem(329, "CMOS IC Wafer (pnp-junctions)" , tooltip));
        ILx.Wafer_Doped_VLSI_1    .set(addItem(343, "VLSI Wafer (n-wells)"          , tooltip));
        ILx.Wafer_Doped_VLSI_2    .set(addItem(349, "VLSI Wafer (npn-junctions)"    , tooltip));
        ILx.Wafer_Metal2_MESFET   .set(addItem(365, "MESFET Wafer (metal 2-layer)"  , tooltip));
        ILx.Wafer_Doped_DRAM_1    .set(addItem(373, "DRAM Wafer (n-wells)"          , tooltip));
        ILx.Wafer_Doped_DRAM_2    .set(addItem(379, "DRAM Wafer (npn-junctions)"    , tooltip));

        tooltip = "Needs conductive metal layer";
        ILx.Wafer_Doped_MESFET    .set(addItem(363, "MESFET Wafer (doped)"          , tooltip));

        ILx.Wafer_Cleaned_PMOS_IC   .set(addItem(304, "PMOS IC Wafer (cleaned)"           , tooltip));
        ILx.Wafer_Cleaned_NMOS_IC   .set(addItem(314, "NMOS IC Wafer (cleaned)"           , tooltip));
        ILx.Wafer_Cleaned_CMOS_IC_2 .set(addItem(330, "CMOS IC Wafer (cleaned, stage II)" , tooltip));
        ILx.Wafer_Cleaned_VLSI_2    .set(addItem(350, "VLSI Wafer (cleaned, stage II)"    , tooltip));
        ILx.Wafer_Cleaned_DRAM_2    .set(addItem(380, "DRAM Wafer (cleaned, stage II)"    , tooltip));

        tooltip = "Needs additional oxide layer";
        ILx.Wafer_Cleaned_CMOS_IC_1 .set(addItem(324, "CMOS IC Wafer (cleaned, stage I)"  , tooltip));
        ILx.Wafer_Cleaned_VLSI_1    .set(addItem(344, "VLSI Wafer (cleaned, stage I)"     , tooltip));
        ILx.Wafer_Cleaned_DRAM_1    .set(addItem(374, "DRAM Wafer (cleaned, stage I)"     , tooltip));

        ILx.Wafer_Metal1_MESFET     .set(addItem(364, "MESFET Wafer (metal 1-layer)"      , "Needs Schottky Gate Metal Layer"));

        tooltip = "Ready to be etched";
        ILx.Wafer_Metal_PMOS_IC .set(addItem(305, "PMOS IC Wafer (metal-layer)" , tooltip));
        ILx.Wafer_Metal_NMOS_IC .set(addItem(315, "NMOS IC Wafer (metal-layer)" , tooltip));
        ILx.Wafer_Metal_CMOS_IC .set(addItem(331, "CMOS IC Wafer (metal-layer)" , tooltip));
        ILx.Wafer_Metal_VLSI    .set(addItem(351, "VLSI Wafer (metal-layer)"    , tooltip));
        ILx.Wafer_Metal_DRAM    .set(addItem(381, "DRAM Wafer (metal-layer)"    , tooltip));

        tooltip = "Needs junctions";
        ILx.Wafer_Oxidized_CMOS_IC_1 .set(addItem(325, "CMOS IC Oxidized Wafer (p-wells)" , tooltip));
        ILx.Wafer_Oxidized_VLSI_1    .set(addItem(345, "VLSI Oxidized Wafer (n-wells)"    , tooltip));
        ILx.Wafer_Oxidized_DRAM_1    .set(addItem(375, "DRAM Oxidized Wafer (n-wells)"    , tooltip));

        tooltip = "Interconnected, can be diced";
        ILx.Wafer_PMOS_IC.set(addItem(306, "PMOS IC Wafer", tooltip));
        ILx.Wafer_NMOS_IC.set(addItem(316, "NMOS IC Wafer", tooltip));
        ILx.Wafer_CMOS_IC.set(addItem(332, "CMOS IC Wafer", tooltip));
        ILx.Wafer_VLSI   .set(addItem(352, "VLSI Wafer"   , tooltip));
        ILx.Wafer_MESFET .set(addItem(366, "MESFET Wafer" , "Can be diced"));
        ILx.Wafer_DRAM   .set(addItem(382, "DRAM Wafer"   , tooltip));

        tooltip = "Needs bonding & packaging";
        ILx.Die_PMOS_IC.set(addItem(307, "PMOS IC Die", tooltip));
        ILx.Die_NMOS_IC.set(addItem(317, "NMOS IC Die", tooltip));
        ILx.Die_CMOS_IC.set(addItem(333, "CMOS IC Die", tooltip));
        ILx.Die_VLSI   .set(addItem(353, "CPU Die"    , tooltip));
        ILx.Die_MESFET .set(addItem(367, "MESFET" , "MEtal-Semiconductor Field-Effect Transistor, " + tooltip));
        ILx.Die_DRAM   .set(addItem(383, "DRAM Die"    , tooltip));

        tooltip = "Integrated Circuit";
        ILx.PMOS_IC.set(addItem(308, "IC (Elite) "  , tooltip, IC_NAME));
        ILx.NMOS_IC.set(addItem(318, "IC (Master)"  , tooltip, IC_NAME));
        ILx.CMOS_IC.set(addItem(334, "IC (Ultimate)", tooltip, IC_NAME));
        ILx.CPU.set(addItem(354, "CPU", "Central Processing Unit"));
        ILx.Chip_DRAM.set(addItem(384, "DRAM chip", "Dynamic Random Access Memory chip"));
        ILx.RAM_Stick.set(addItem(385, "RAM stick", "A computer's memory module"));

    }
}
