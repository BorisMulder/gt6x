package org.altadoon.gt6x.gui;

import gregapi.data.CS;
import gregapi.gui.ContainerCommon;
import gregapi.gui.Slot_Holo;
import net.minecraft.entity.player.InventoryPlayer;
import org.altadoon.gt6x.features.metallurgy.MultiTileEntityEAF;

public class ContainerCommonEAF extends ContainerCommon {
    public ContainerCommonEAF(InventoryPlayer aInventoryPlayer, MultiTileEntityEAF aTileEntity, int aGUIID) {
        super(aInventoryPlayer, aTileEntity, aGUIID, 0, MultiTileEntityEAF.GUI_SLOTS);
    }

    @Override
    public int addSlots(InventoryPlayer aPlayerInventory) {
        for (int x = 0, i = 0; x < 2; x++)
            for (int y = 0; y < 6; y++)
                addSlotToContainer(new Slot_Holo(mTileEntity, mOffset+i++, 8 + (63+18) * x, 18 + y * 18, false, false, (int)MultiTileEntityEAF.MAX_UNITS));

        return 18+18*6+13;
    }

    @Override public int getSlotCount() { return MultiTileEntityEAF.GUI_SLOTS; }
}
