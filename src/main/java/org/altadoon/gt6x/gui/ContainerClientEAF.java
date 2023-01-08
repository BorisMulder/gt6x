package org.altadoon.gt6x.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.CS;
import gregapi.data.LH;
import gregapi.gui.ContainerClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.EAFSmeltingRecipe;
import org.altadoon.gt6x.features.metallurgy.MultiTileEntityEAF;

import static gregapi.data.CS.SIDE_INSIDE;
import static org.altadoon.gt6x.common.Log.LOG;

@SideOnly(Side.CLIENT)
public class ContainerClientEAF extends ContainerClient {
    protected double[] slotUnits = new double[MultiTileEntityEAF.GUI_SLOTS];

    public ContainerClientEAF(InventoryPlayer inventoryPlayer, MultiTileEntityEAF tileEntity, int guiID, String guiTexture) {
        super(new ContainerCommonEAF(inventoryPlayer, tileEntity, guiID), guiTexture);
        ySize = 114 + 6 * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(LH.get(EAFSmeltingRecipe.FakeRecipes.mNameInternal), 8,  4, 4210752);

        for (int i = 0; i < slotUnits.length; ++i) {
            if (slotUnits[i] != 0) {
                fontRendererObj.drawString(String.format("%.3f", slotUnits[i]), 8+18+2+(63+18)*(i/6),  14+(i%6)*18+6, 4210752);
            }
        }

        long t = ((MultiTileEntityEAF)mContainer.mTileEntity).getTemperatureValue(SIDE_INSIDE);
        fontRendererObj.drawString("Temperature: " + t + " K", 8,  14+6*18+6, 4210752);

        //TODO update more often
    }

    @Override
    public void drawScreen(int aX, int aY, float par3) {
        for (int i = 0; i < slotUnits.length; i++) {
            Slot slot = (Slot)inventorySlots.inventorySlots.get(i);
            ItemStack stack = slot.getStack();
            if (stack == null) {
                slotUnits[i] = 0;
            } else if (stack.stackSize > 1) {
                slotUnits[i] = (double)stack.stackSize * 91 / CS.U;
                stack.stackSize = 1;
            } else if(stack.stackSize != 1) {
                LOG.warn("slot {} has invalid stack size: {}", i, stack.stackSize);
            }
            if (stack != null)
                LOG.debug("slot {} has {} units of {} (from stack size {})", i, slotUnits[i], stack.getDisplayName(), stack.stackSize);
        }

        super.drawScreen(aX, aY, par3);
    }
}
