package org.altadoon.gt6x.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.LH;
import gregapi.gui.ContainerClient;
import net.minecraft.entity.player.InventoryPlayer;
import org.altadoon.gt6x.common.EAFSmeltingRecipe;
import org.altadoon.gt6x.features.metallurgy.MultiTileEntityEAF;

@SideOnly(Side.CLIENT)
public class ContainerClientEAF extends ContainerClient {
    public ContainerClientEAF(InventoryPlayer inventoryPlayer, MultiTileEntityEAF tileEntity, int guiID, String guiTexture) {
        super(new ContainerCommonEAF(inventoryPlayer, tileEntity, guiID), guiTexture);
        ySize = 114 + 6 * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(LH.get(EAFSmeltingRecipe.FakeRecipes.mNameInternal), 8,  4, 4210752);
    }
}
