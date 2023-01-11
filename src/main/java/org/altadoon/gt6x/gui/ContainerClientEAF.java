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
    int last_x = Integer.MIN_VALUE, last_y = Integer.MIN_VALUE; float last_a3 = Float.MIN_VALUE;

    protected double[] slotUnits = new double[MultiTileEntityEAF.GUI_SLOTS];

    public ContainerClientEAF(InventoryPlayer inventoryPlayer, MultiTileEntityEAF tileEntity, int guiID, String guiTexture) {
        super(new ContainerCommonEAF(inventoryPlayer, tileEntity, guiID), guiTexture);
        ySize = 114 + 6 * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(LH.get(EAFSmeltingRecipe.FakeRecipes.mNameInternal), 8,  4, 4210752);
        drawTextValues();
    }

    protected void drawTextValues() {
        for (int i = 0; i < slotUnits.length; ++i) {
            if (slotUnits[i] != 0) {
                fontRendererObj.drawString(String.format("%.3f", slotUnits[i]), 8+18+2+(63+18)*(i/6),  14+(i%6)*18+6, 4210752);
            }
        }

        long t = ((MultiTileEntityEAF)mContainer.mTileEntity).getTemperatureValue(SIDE_INSIDE);
        fontRendererObj.drawString("Temperature: " + t + " K", 8,  14+6*18+6, 4210752);
    }

    @Override
    public void drawScreen(int x, int y, float arg3) {
        // hack to redraw the screen with the same arguments some other time
        if (x == Integer.MIN_VALUE) {
            if (last_x != Integer.MIN_VALUE) {
                x = last_x; y = last_y; arg3 = last_a3;
            } else {
                return;
            }
        } else {
            last_x = x; last_y = y; last_a3 = arg3;
        }

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

        super.drawScreen(x, y, arg3);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        LOG.debug("updateScreen called");

        drawScreen(Integer.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
    }
}
