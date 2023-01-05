package org.altadoon.gt6x.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.LH;
import gregapi.gui.ContainerClient;
import net.minecraft.entity.player.InventoryPlayer;
import org.altadoon.gt6x.common.EAFSmeltingRecipe;
import org.altadoon.gt6x.features.metallurgy.MultiTileEntityEAF;

import static gregapi.data.CS.SIDE_INSIDE;

@SideOnly(Side.CLIENT)
public class ContainerClientEAF extends ContainerClient {
    public ContainerClientEAF(InventoryPlayer inventoryPlayer, MultiTileEntityEAF tileEntity, int guiID, String guiTexture) {
        super(new ContainerCommonEAF(inventoryPlayer, tileEntity, guiID), guiTexture);
        ySize = 114 + 6 * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(LH.get(EAFSmeltingRecipe.FakeRecipes.mNameInternal), 8,  4, 4210752);

        if (mContainer.mTileEntity instanceof MultiTileEntityEAF) {
            fontRendererObj.drawString("T:", 8, 20, 4210752);
            fontRendererObj.drawString(((MultiTileEntityEAF)mContainer.mTileEntity).getTemperatureValue(SIDE_INSIDE) + " K", 8, 28, 4210752);

            fontRendererObj.drawString("Tmax:", 8, 40, 4210752);
            fontRendererObj.drawString(((MultiTileEntityEAF)mContainer.mTileEntity).getTemperatureMax(SIDE_INSIDE) + " K", 8, 48, 4210752);

            fontRendererObj.drawString("Weight:", 8, 60, 4210752);
            fontRendererObj.drawString(String.format("%.2f kg", ((MultiTileEntityEAF)mContainer.mTileEntity).getWeightValue(SIDE_INSIDE)), 8, 68, 4210752);

        }
    }
}
