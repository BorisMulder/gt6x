package org.altadoon.gt6x.features.electronics;

import gregapi.data.MT;
import gregapi.data.TC;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.oredict.OreDictItemData;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.items.ILx;

import static gregapi.data.CS.*;

public class MultiItemsElectronics extends MultiItemRandom {
    public static MultiItemsElectronics instance;

    public MultiItemsElectronics(String modID, String unlocalized) {
        super(modID, unlocalized);
    }

    @Override
    public void addItems() {
        int itemId = 0;
        // tier 0 (primitive) and 7+ (quantum) are currently not used.
        for (int tier = 1; tier < 7; tier++) {
            // size 0 are normal circuit boards, which are already in GT6, so we start from 1.
            for (int size = 1; size < 7; size++) {
                if (size + tier > 6) continue;

                ILx.Microchips[tier][size].set(addItem(
                    itemId++, ILx.CIRCUIT_SIZE_NAMES[size] + "T" + tier + " (" + ILx.CIRCUIT_TIER_NAMES[tier] + ")", null, MT.DATA.CIRCUITS[tier], OD_CIRCUITS[tier], TC.stack(TC.COGNITIO, 2)
                ));
            }
        }

        ILx.Fireclay_Ball.set(addItem(0, "Fireclay", "Fire-proof clay", TC.stack(TC.TERRA, 1), new OreDictItemData(MTx.Fireclay, U))); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("digger", last());
    }
}
