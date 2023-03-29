package org.altadoon.gt6x.common.items;

import gregapi.data.*;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.oredict.OreDictItemData;
import org.altadoon.gt6x.common.MTx;

import static gregapi.data.CS.*;
import static gregapi.data.OP.dust;
import static gregapi.data.OP.ingot;

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
    }
}
