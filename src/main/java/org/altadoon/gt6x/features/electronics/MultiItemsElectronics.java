package org.altadoon.gt6x.features.electronics;

import gregapi.code.ItemStackContainer;
import gregapi.data.*;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.util.CR;
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

        String throughHoleToolTip = "Can be soldered by hand onto a PCB";
        ILx.Resistor_ThroughHole.set(addItem(110, "Through-Hole Resistor", throughHoleToolTip, RESISTOR_NAME));
        ILx.Capacitor_ThroughHole.set(addItem(111, "Through-Hole Capacitor", throughHoleToolTip, CAPACITOR_NAME));
        ILx.Transistor_ThroughHole.set(addItem(112, "Through-Hole Transistor", throughHoleToolTip, TRANSISTOR_NAME));

        String smdToolTip = "Surface-Mounted Devices are smaller and can be soldered by hand or using machines";
        ILx.Resistor_SMD.set(addItem(120, "SMD-Resistor", smdToolTip, RESISTOR_NAME));
        ILx.Capacitor_SMD.set(addItem(121, "SMD-Capacitor", smdToolTip, CAPACITOR_NAME));
        ILx.Transistor_SMD.set(addItem(122, "SMD-Transistor", smdToolTip, TRANSISTOR_NAME));

        String boardToolTip = "Fire-resistant board used to make PCBs";
        ILx.PF_Board .set(addItem(130, "Phenolic Paper Board (FR-1)", boardToolTip), new OreDictItemData(MTx.PF, U));
        ILx.FRE_Board.set(addItem(131, "Fibreglass-reinforced Epoxy Board (FR-4)", boardToolTip, new OreDictItemData(MTx.Epoxy, U)));

        String cclToolTip = "Needs to be etched to create traces";
        ILx.CCL      .set(addItem(140, "Copper-Clad Laminate"        , cclToolTip), new OreDictItemData(MT.Cu, U));
        ILx.CCL_SMALL.set(addItem(141, "Copper-Clad Laminate (Small)", cclToolTip), new OreDictItemData(MT.Cu, U2));
        ILx.CCL_TINY .set(addItem(142, "Copper-Clad Laminate (Tiny)" , cclToolTip), new OreDictItemData(MT.Cu, U4));
        ILx.GCL      .set(addItem(150, "Gold-Clad Laminate"        , cclToolTip), new OreDictItemData(MT.Au, U, MTx.Epoxy, U));
        ILx.GCL_SMALL.set(addItem(151, "Gold-Clad Laminate (Small)", cclToolTip), new OreDictItemData(MT.Au, U2, MTx.Epoxy, U2));
        ILx.GCL_TINY .set(addItem(152, "Gold-Clad Laminate (Tiny)" , cclToolTip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U4));
        ILx.PCL      .set(addItem(160, "Platinum-Clad Laminate"        , cclToolTip), new OreDictItemData(MT.Pt, U, MTx.Epoxy, U));
        ILx.PCL_SMALL.set(addItem(161, "Platinum-Clad Laminate (Small)", cclToolTip), new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U2));
        ILx.PCL_TINY .set(addItem(162, "Platinum-Clad Laminate (Tiny)" , cclToolTip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U4));

        String pcbPlateToolTip = "You need to solder components onto it";
        ItemStack board;
        board = IL.Circuit_Plate_Copper.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Cu, U2)); LH.add(getUnlocalizedName(board) + ".tooltip", pcbPlateToolTip);
        ILx.Circuit_Plate_Copper_Small.set(addItem(170, "Small Circuit Plate (Copper)", pcbPlateToolTip), new OreDictItemData(MT.Cu, U4));
        ILx.Circuit_Plate_Copper_Tiny.set(addItem(171, "Tiny Circuit Plate (Copper)", pcbPlateToolTip), new OreDictItemData(MT.Cu, U8));
        board = IL.Circuit_Plate_Gold.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Au, U2, MTx.Epoxy, U)); LH.add(getUnlocalizedName(board) + ".tooltip", pcbPlateToolTip);
        ILx.Circuit_Plate_Gold_Small.set(addItem(172, "Small Circuit Plate (Gold)", pcbPlateToolTip), new OreDictItemData(MT.Au, U4, MTx.Epoxy, U2));
        ILx.Circuit_Plate_Gold_Tiny.set(addItem(173, "Tiny Circuit Plate (Gold)", pcbPlateToolTip), new OreDictItemData(MT.Cu, U8, MTx.Epoxy, U4));
        board = IL.Circuit_Plate_Platinum.get(1); OreDictManager.INSTANCE.setItemData(board, new OreDictItemData(MT.Pt, U2, MTx.Epoxy, U)); LH.add(getUnlocalizedName(board) + ".tooltip", pcbPlateToolTip);
        ILx.Circuit_Plate_Platinum_Small.set(addItem(174, "Small Circuit Plate (Platinum)", pcbPlateToolTip), new OreDictItemData(MT.Pt, U4, MTx.Epoxy, U2));
        ILx.Circuit_Plate_Platinum_Tiny.set(addItem(175, "Tiny Circuit Plate (Platinum)", pcbPlateToolTip), new OreDictItemData(MT.Pt, U8, MTx.Epoxy, U4));

        ILx.GlassFibres.set(addItem(200, "Glass Fibre", "Small threads of glass"), new OreDictItemData(MT.Glass, U8));

        ILx.PlatinumBushing.set(addItem(201, "Platinum Bushing", "Basically a very expensive cheese grater"), new OreDictItemData(MT.Pt, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.PlatinumBushing.get(1)), (byte)45);

        ILx.EtchMask_Trace.set(addItem(202, "Etching Mask (Circuit Trace)", "Protects your PCB from etch"), new OreDictItemData(MT.PVC, U));
        BooksGT.BOOK_REGISTER.put(new ItemStackContainer(ILx.EtchMask_Trace.get(1)), (byte)45);

        ILx.Comp_Laser_Gas_N    .set(addItem(210, "Nitrogen Laser Emitter"        , "Purpose: Near-UV Optical Appliances"   , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_KrF  .set(addItem(211, "Krypton Fluoride Laser Emitter", "Purpose: Middle-UV Optical Appliances" , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Gas_ArF  .set(addItem(212, "Argon Fluoride Laser Emitter"  , "Purpose: Far-UV Optical Appliances"    , TC.stack(TC.LUX, 2), TC.stack(TC.AER     , 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));
        ILx.Comp_Laser_Molten_Sn.set(addItem(213, "Molten Tin Laser Emitter"      , "Purpose: Extreme-UV Optical Appliances", TC.stack(TC.LUX, 2), TC.stack(TC.METALLUM, 1), OM.data(IL.Comp_Laser_Gas_Empty.get(1))));

    }
}
