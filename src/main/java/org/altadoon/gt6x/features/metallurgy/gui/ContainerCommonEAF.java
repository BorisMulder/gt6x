package org.altadoon.gt6x.features.metallurgy.gui;

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
        int i = 0;

        for (int y = 0; y < 2; y++)
            for (int x = 0; x < 9; x++)
                addSlotToContainer(new Slot_Holo(mTileEntity, mOffset+i++, 8 + 18 * x, 18 + x * 3 + y * 47, false, false, 1));

        return 139;
    }

    @Override public int getSlotCount() { return MultiTileEntityEAF.GUI_SLOTS; }
}
