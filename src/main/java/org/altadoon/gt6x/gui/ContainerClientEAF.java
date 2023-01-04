package org.altadoon.gt6x.gui;

import gregapi.data.LH;
import gregapi.gui.ContainerClient;
import gregapi.tileentity.ITileEntityInventoryGUI;
import net.minecraft.entity.player.InventoryPlayer;
import org.altadoon.gt6x.common.EAFSmeltingRecipe;

public class ContainerClientEAF extends ContainerClient {
    public ContainerClientEAF(InventoryPlayer aInventoryPlayer, ITileEntityInventoryGUI aTileEntity, int aGUIID, String aGUITexture) {
        super(new ContainerCommonEAF(aInventoryPlayer, aTileEntity, aGUIID), aGUITexture);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(LH.get(EAFSmeltingRecipe.FakeRecipes.mNameInternal), 8,  4, 4210752);
    }
}
