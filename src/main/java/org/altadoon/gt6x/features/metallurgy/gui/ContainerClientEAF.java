package org.altadoon.gt6x.features.metallurgy.gui;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.RecipeInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.LH;
import gregapi.gui.ContainerClient;
import gregapi.util.UT;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.features.metallurgy.EAFSmeltingRecipe;
import org.altadoon.gt6x.features.metallurgy.MultiTileEntityEAF;

import java.awt.*;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ContainerClientEAF extends ContainerClient {
    public String customNeiButtonName;

    public ContainerClientEAF(InventoryPlayer inventoryPlayer, MultiTileEntityEAF tileEntity, int guiID, String guiTexture) {
        super(new ContainerCommonEAF(inventoryPlayer, tileEntity, guiID), guiTexture);
        ySize = 114 + 6 * 18;
        this.customNeiButtonName = EAFSmeltingRecipe.FakeRecipes.mNameNEI;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(LH.get(EAFSmeltingRecipe.FakeRecipes.mNameInternal), 8,  4, 4210752);
    }

    static {
        GuiContainerManager.addInputHandler(new EAF_NEIRectHandler());
        GuiContainerManager.addTooltipHandler(new EAF_NEIRectHandler());
    }

    public static class EAF_NEIRectHandler implements IContainerInputHandler, IContainerTooltipHandler {
        public EAF_NEIRectHandler() {
        }

        public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
            if (this.canHandle(gui)) {
                if (button == 0) {
                    return this.transferRect(gui, false);
                }

                if (button == 1) {
                    return this.transferRect(gui, true);
                }
            }

            return false;
        }

        public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {
            return false;
        }

        public boolean canHandle(GuiContainer gui) {
            return gui instanceof ContainerClientEAF && UT.Code.stringValid(((ContainerClientEAF)gui).customNeiButtonName);
        }

        public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
            if (this.canHandle(gui) && currenttip.isEmpty() && (new Rectangle(2, 95, 28, 16)).contains(new Point(GuiDraw.getMousePosition().x - ((ContainerClientEAF)gui).getLeft() - RecipeInfo.getGuiOffset(gui)[0], GuiDraw.getMousePosition().y - ((ContainerClientEAF)gui).getTop() - RecipeInfo.getGuiOffset(gui)[1]))) {
                currenttip.add("Recipes");
            }

            return currenttip;
        }

        private boolean transferRect(GuiContainer gui, boolean usage) {
            boolean var10000;
            label27: {
                if (this.canHandle(gui) && (new Rectangle(2, 95, 28, 16)).contains(new Point(GuiDraw.getMousePosition().x - ((ContainerClientEAF)gui).getLeft() - RecipeInfo.getGuiOffset(gui)[0], GuiDraw.getMousePosition().y - ((ContainerClientEAF)gui).getTop() - RecipeInfo.getGuiOffset(gui)[1]))) {
                    if (usage) {
                        if (GuiUsageRecipe.openRecipeGui(((ContainerClientEAF)gui).customNeiButtonName)) {
                            break label27;
                        }
                    } else if (GuiCraftingRecipe.openRecipeGui(((ContainerClientEAF)gui).customNeiButtonName)) {
                        break label27;
                    }
                }

                var10000 = false;
                return var10000;
            }

            var10000 = true;
            return var10000;
        }

        public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
            return currenttip;
        }

        public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey, List<String> currenttip) {
            return currenttip;
        }

        public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
            return false;
        }

        public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {
        }

        public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        }

        public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {
        }

        public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
            return false;
        }

        public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        }

        public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {
        }
    }
}
