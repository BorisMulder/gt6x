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


    public MultiItemsElectronics(String modID, String unlocalized) {
        super(modID, unlocalized);
    }

    @Override
    public void addItems() {
        String tooltip;
        // tier 0 (primitive) and 7+ (quantum) are currently not used.
        for (int tier = 1; tier < 7; tier++) {
            // size 0 are normal circuit boards, which are already in GT6, so we start from 1.
            for (int size = 1; size < 7; size++) {
                if (size + tier > 6) continue;

                ILx.Microchips[tier][size].set(addItem(
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
        ILx.PF_Board .set(addItem(130, "Phenolic Paper Board (FR-1)", tooltip), new OreDictItemData(MTx.PF, U));
        ILx.FRE_Board.set(addItem(131, "Fibreglass-reinforced Epoxy Board (FR-4)", tooltip, new OreDictItemData(MTx.Epoxy, U)));

        tooltip = "Needs to be etched to create traces";
        ILx.CCL      .set(addItem(140, "Copper-Clad Laminate"        , tooltip), new OreDictItemData(MT.Cu, U));
        ILx.CCL_SMALL.set(addItem(141, "Copper-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Cu, U2));
        ILx.CCL_TINY .set(addItem(142, "Copper-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Cu, U4));
        ILx.GCL      .set(addItem(150, "Gold-Clad Laminate"        , tooltip), new OreDictItemData(MT.Au, U, MTx.Epoxy, U));
        ILx.GCL_SMALL.set(addItem(151, "Gold-Clad Laminate (Small)", tooltip), new OreDictItemData(MT.Au, U2, MTx.Epoxy, U2));
        ILx.GCL_TINY .set(addItem(152, "Gold-Clad Laminate (Tiny)" , tooltip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U4));
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

        ILx.GlassFibres.set(addItem(200, "Glass Fibre", "Small threads of glass"), new OreDictItemData(MT.Glass, U8));

        ILx.PlatinumBushing.set(addItem(201, "Platinum Bushing", "Basically a very expensive cheese grater"), new OreDictItemData(MT.Pt, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.PlatinumBushing.get(1)), (byte)45);

        ILx.EtchMask_Trace.set(addItem(202, "Etching Mask (Circuit Trace)", "Protects parts of your PCB from etch"), new OreDictItemData(MT.PVC, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace.get(1)), (byte)45);
        ILx.EtchMask_Photomask_PMOS_IC.set(addItem(203, "Etching Mask (PMOS IC Photomask)", "Protects parts of your photomask from etch"), new OreDictItemData(MT.PVC, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Photomask_PMOS_IC.get(1)), (byte)45);

        ILx.Comp_Laser_Gas_N    .set(addItem(210, "Nitrogen Laser Emitter"        , "Purpose: Near-UV Optical Appliances"   , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_KrF  .set(addItem(211, "Krypton Fluoride Laser Emitter", "Purpose: Middle-UV Optical Appliances" , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_ArF  .set(addItem(212, "Argon Fluoride Laser Emitter"  , "Purpose: Far-UV Optical Appliances"    , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Molten_Sn.set(addItem(213, "Molten Tin Laser Emitter"      , "Purpose: Extreme-UV Optical Appliances", TC.stack(TC.LUX, 2), TC.stack(TC.METALLUM, 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));

        // Photomasks
        ILx.Photomask_Raw.set(addItem(220, "Raw Photomask", "Needs Design", new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        tooltip = "Used in Photolithography";
        ILx.Photomask_PMOS_IC   .set(addItem(221, "Photomask (PMOS IC)"           , tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_NMOS_IC   .set(addItem(222, "Photomask (NMOS IC)"           , tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_CMOS_IC_1 .set(addItem(223, "Photomask (CMOS IC, stage I)"  , tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_CMOS_IC_2 .set(addItem(224, "Photomask (CMOS IC, stage II)" , tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_CMOS_SOC_1.set(addItem(225, "Photomask (CMOS SOC, stage I)" , tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_CMOS_SOC_2.set(addItem(226, "Photomask (CMOS SOC, stage II)", tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));
        ILx.Photomask_MESFET    .set(addItem(227, "Photomask (MESFET)"            , tooltip, new OreDictItemData(MT.Glass, U, MT.Cr, U2)));

        ILx.Wafer_GaAs_SiN_layered.set(addItem(299, "SiN-capped GaN wafer", "Gallium Arsenide Wafer with Silicon Nitride cap layer", new OreDictItemData(MTx.GaAs, U)));

        // Wafers, ICs, etc.
        tooltip = "Needs developer";
        ILx.Wafer_Patterned_PMOS_IC   .set(addItem(300, "PMOS IC Wafer (patterned)"           , tooltip));
        ILx.Wafer_Patterned_NMOS_IC   .set(addItem(301, "NMOS IC Wafer (patterned)"           , tooltip));
        ILx.Wafer_Patterned_CMOS_IC_1 .set(addItem(302, "CMOS IC Wafer (patterned, stage I)"  , tooltip));
        ILx.Wafer_Patterned_CMOS_IC_2 .set(addItem(303, "CMOS IC Wafer (patterned, stage II)" , tooltip));
        ILx.Wafer_Patterned_CMOS_SOC_1.set(addItem(304, "CMOS SOC Wafer (patterned, stage I)" , tooltip));
        ILx.Wafer_Patterned_CMOS_SOC_2.set(addItem(305, "CMOS SOC Wafer (patterned, stage II)", tooltip));
        ILx.Wafer_Patterned_MESFET    .set(addItem(306, "MESFET Wafer (patterned)"            , tooltip));

        tooltip = "Ready to be etched";
        ILx.Wafer_Developed_PMOS_IC   .set(addItem(310, "PMOS IC Wafer (developed)"           , tooltip));
        ILx.Wafer_Developed_NMOS_IC   .set(addItem(311, "NMOS IC Wafer (developed)"           , tooltip));
        ILx.Wafer_Developed_CMOS_IC_1 .set(addItem(312, "CMOS IC Wafer (developed, stage I)"  , tooltip));
        ILx.Wafer_Developed_CMOS_IC_2 .set(addItem(313, "CMOS IC Wafer (developed, stage II)" , tooltip));
        ILx.Wafer_Developed_CMOS_SOC_1.set(addItem(314, "CMOS SOC Wafer (developed, stage I)" , tooltip));
        ILx.Wafer_Developed_CMOS_SOC_2.set(addItem(315, "CMOS SOC Wafer (developed, stage II)", tooltip));
        ILx.Wafer_Developed_MESFET    .set(addItem(316, "MESFET Wafer (developed)"            , tooltip));

        tooltip = "Could use some extra dopant";
        ILx.Wafer_Etched_PMOS_IC   .set(addItem(320, "PMOS IC Wafer (etched)"           , tooltip));
        ILx.Wafer_Etched_NMOS_IC   .set(addItem(321, "NMOS IC Wafer (etched)"           , tooltip));
        ILx.Wafer_Etched_CMOS_IC_1 .set(addItem(322, "CMOS IC Wafer (etched, stage I)"  , tooltip));
        ILx.Wafer_Etched_CMOS_IC_2 .set(addItem(323, "CMOS IC Wafer (etched, stage II)" , tooltip));
        ILx.Wafer_Etched_CMOS_SOC_1.set(addItem(324, "CMOS SOC Wafer (etched, stage I)" , tooltip));
        ILx.Wafer_Etched_CMOS_SOC_2.set(addItem(325, "CMOS SOC Wafer (etched, stage II)", tooltip));
        ILx.Wafer_Etched_MESFET    .set(addItem(326, "MESFET Wafer (etched)"            , tooltip));

        tooltip = "Needs conductive metal layer";
        ILx.Wafer_Doped_PMOS_IC   .set(addItem(330, "PMOS IC Wafer (doped)"         , tooltip));
        ILx.Wafer_Doped_NMOS_IC   .set(addItem(331, "NMOS IC Wafer (doped)"         , tooltip));
        ILx.Wafer_Doped_CMOS_IC_2 .set(addItem(333, "CMOS IC Wafer (pnp-junctions)" , tooltip));
        ILx.Wafer_Doped_CMOS_SOC_2.set(addItem(335, "CMOS SOC Wafer (npn-junctions)", tooltip));
        ILx.Wafer_Doped_MESFET    .set(addItem(336, "MESFET Wafer (doped)"          , tooltip));

        tooltip = "Needs additional oxide layer";
        ILx.Wafer_Doped_CMOS_IC_1 .set(addItem(332, "CMOS IC Wafer (p-wells)"       , tooltip));
        ILx.Wafer_Doped_CMOS_SOC_1.set(addItem(334, "CMOS SOC Wafer (n-wells)"      , tooltip));

        tooltip = "Ready to be etched";
        ILx.Wafer_Metal_PMOS_IC .set(addItem(340, "PMOS IC Wafer (metal-layer)" , tooltip));
        ILx.Wafer_Metal_NMOS_IC .set(addItem(341, "NMOS IC Wafer (metal-layer)" , tooltip));
        ILx.Wafer_Metal_CMOS_IC .set(addItem(343, "CMOS IC Wafer (metal-layer)" , tooltip));
        ILx.Wafer_Metal_CMOS_SOC.set(addItem(345, "CMOS SOC Wafer (metal-layer)", tooltip));

        tooltip = "Needs junctions";
        ILx.Wafer_Oxidized_CMOS_IC_1 .set(addItem(342, "CMOS IC Oxidized Wafer (p-wells)" , tooltip));
        ILx.Wafer_Oxidized_CMOS_SOC_1.set(addItem(344, "CMOS SOC Oxidized Wafer (n-wells)", tooltip));

        ILx.Wafer_Metal1_MESFET.set(addItem(346, "MESFET Wafer (metal 1-layer)", "Needs Schottky Gate Metal Layer"));
        ILx.Wafer_Metal2_MESFET.set(addItem(354, "MESFET Wafer (metal 2-layer)", "Needs cleaning"));

        tooltip = "Interconnected, can be diced";
        ILx.Wafer_PMOS_IC.set(addItem(350, "PMOS IC Wafer", tooltip));
        ILx.Wafer_NMOS_IC.set(addItem(351, "NMOS IC Wafer", tooltip));
        ILx.Wafer_CMOS_IC.set(addItem(353, "CMOS IC Wafer", tooltip));
        ILx.Wafer_SOC    .set(addItem(355, "SOC Wafer"    , tooltip));
        ILx.Wafer_MESFET .set(addItem(356, "MESFET Wafer" , "Can be diced"));

        tooltip = "Needs bonding & packaging";
        ILx.Die_PMOS_IC.set(addItem(360, "PMOS IC Die", tooltip));
        ILx.Die_NMOS_IC.set(addItem(361, "NMOS IC Die", tooltip));
        ILx.Die_CMOS_IC.set(addItem(363, "CMOS IC Die", tooltip));
        ILx.Die_SOC    .set(addItem(365, "SOC Die"    , tooltip));
        ILx.Die_MESFET .set(addItem(366, "MESFET" , "Metal-semiconductor field-effect transistor, " + tooltip));

        tooltip = "Integrated Circuit";
        ILx.PMOS_IC.set(addItem(370, "IC (Elite) "  , tooltip));
        ILx.NMOS_IC.set(addItem(371, "IC (Master)"  , tooltip));
        ILx.CMOS_IC.set(addItem(373, "IC (Ultimate)", tooltip));

        ILx.SOC    .set(addItem(375, "SOC", "System on a Chip", MT.DATA.CIRCUITS[6], OD_CIRCUITS[6], TC.stack(TC.COGNITIO, 2)));
    }
}
