package org.altadoon.gt6x.common.items;

import gregapi.data.*;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.oredict.OreDictItemData;
import gregapi.util.CR;
import org.altadoon.gt6x.common.MTx;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.features.engines.OreDictPrefixes.tbcCoatedRotor;

public class MultiItemsX extends MultiItemRandom {
    public static MultiItemsX instance;

    public MultiItemsX(String modID, String unlocalized) {
        super(modID, unlocalized);
    }

    public static void init(String modID) {
        instance = new MultiItemsX(modID, "multiitems");
    }

    @Override
    public void addItems() {
        ILx.Display_OMStack.set(new ItemMaterialDisplay());
        ILx.Fireclay_Ball.set(addItem(0, "Fireclay", "Fire-proof clay", TC.stack(TC.TERRA, 1), new OreDictItemData(MTx.Fireclay, U))); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("digger", last());
        RM.add_smelting(ILx.Fireclay_Ball.get(1), OP.ingot.mat(MTx.Firebrick, 1), false, false, false);
        RM.add_smelting(dust.mat(MTx.Fireclay, 1), ingot.mat(MTx.Firebrick, 1));
        ILx.Rosin.set(addItem(1, "Rosin", "Solid Conifer Resin", TC.stack(TC.ARBOR, 1))); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("forester", last());

        ILx.Ceramic_Engine_Block_Mold_Raw.set(addItem(2, "Clay Engine Block Mold", "Put in Furnace to harden", new OreDictItemData(MTx.Fireclay, 6*U)));
        ILx.Shape_Extruder_Catalytic_Converter.set(addItem(3, "Extruder Shape (Catalytic Converter)", "Extruder Shape for making Catalytic Converters"));
        ILx.Shape_SimpleEx_Catalytic_Converter.set(addItem(4, "Low Heat Extruder Shape (Catalytic Converter)", "Extruder Shape for making Catalytic Converters"));
        CR.shaped(ILx.Shape_Extruder_Catalytic_Converter.get(1), CR.DEF_REV, " e ", " P ", "   ", 'P', IL.Shape_Extruder_Empty);
        CR.shaped(ILx.Shape_SimpleEx_Catalytic_Converter.get(1), CR.DEF_REV, " e ", " P ", "   ", 'P', IL.Shape_SimpleEx_Empty);

        ILx.SparkPlugs.set(addItem(5, "Spark Plugs", "Used to power spark-ignition engines", TC.stack(TC.ELECTRUM, 1)));
        CR.shaped(ILx.SparkPlugs.get(1), CR.DEF_REV, "BCB", "CAC", "BMB", 'B', bolt.dat(MT.Invar), 'C', cableGt01.dat(ANY.Cu), 'M', IL.MOTORS[1], 'A', "gt:re-battery1");

        ILx.SuperCharger.set(addItem(6, "Supercharger", "Forces air into engines"));
        CR.shaped(ILx.SuperCharger.get(1), CR.DEF_REV, "PUP", "RSR", "TUT", 'P', pipeSmall.dat(MT.Al), 'U', plateCurved.dat(MT.Magnalium), 'R', tbcCoatedRotor.dat(MT.Magnalium), 'S', stick.dat(MTx.HSSM2), 'T', stick.dat(MT.Teflon));
    }
}
