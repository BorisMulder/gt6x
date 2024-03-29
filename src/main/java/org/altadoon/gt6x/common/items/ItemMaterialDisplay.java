package org.altadoon.gt6x.common.items;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.*;
import gregapi.gui.ContainerClient;
import gregapi.item.IItemGT;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import org.altadoon.gt6x.Gt6xMod;

import java.util.Collections;
import java.util.List;

import static gregapi.data.CS.*;

public class ItemMaterialDisplay extends Item implements IItemGT {
    //TODO fix
    private IIcon icon;

    private final String name = "gt6x.display.oredictmaterialstack";

    public static ItemStack display(OreDictMaterial mat) {
        return display(mat, 1);
    }

    public static ItemStack display(OreDictMaterial mat, long amount) {
        return display(new OreDictMaterialStack(mat, amount), 300);
    }

    public static ItemStack display(OreDictMaterial mat, long amount, long temperature) {
        return display(new OreDictMaterialStack(mat, amount), temperature);
    }

    public static ItemStack display(OreDictMaterialStack OMStack, long temperature) {
        ItemStack stack = ILx.Display_OMStack.getWithMeta(1, OMStack.mMaterial.mID);
        if (stack == null) return null;
        NBTTagCompound tNBT = UT.NBT.makeShort("m", OMStack.mMaterial.mID);
        if (OMStack.mAmount != 0) UT.NBT.setNumber(tNBT, "a", OMStack.mAmount);
        UT.NBT.setNumber(tNBT, "T", temperature);
        return UT.NBT.set(stack, tNBT);
    }

    protected ItemMaterialDisplay() {
        super();
        LH.add(name + ".name", "OreDictMaterialStack Display");
        GameRegistry.registerItem(this, name, Gt6xMod.MOD_ID);
        ST.hide(this);
        CS.ItemsGT.DEBUG_ITEMS.add(this);
        CS.ItemsGT.ILLEGAL_DROPS.add(this);
        CS.GarbageGT.BLACKLIST.add(this);
    }

    public static OreDictMaterialStack UnDisplay(ItemStack stack) {
        NBTTagCompound NBT = stack.getTagCompound();
        long amount = 0;
        OreDictMaterial mat = MT.NULL;
        if (NBT != null && stack.getItem() instanceof ItemMaterialDisplay) {
            amount = NBT.getLong("a");
            short matId = NBT.getShort("m");
            mat = OreDictMaterial.get(matId);
        }
        return new OreDictMaterialStack(mat, amount);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean F3_H) {
        OreDictMaterialStack mat = UnDisplay(stack);
        NBTTagCompound NBT = stack.getTagCompound();
        long temperature = -1;
        if (NBT != null) {
            temperature = NBT.getLong("T");
        }

        if (mat.mMaterial == MT.NULL) {
            list.add(LH.Chat.BLINKING_RED + "CLIENTSIDE MATERIAL IS NULL!!!");
        }

        list.add(LH.Chat.BLUE + String.format("Content: %.3f Units of %s", (double)mat.mAmount / U, mat.mMaterial.getLocal()));

        if (mat.mMaterial.mTooltipChemical != null) {
            list.add(LH.Chat.YELLOW + mat.mMaterial.mTooltipChemical);
        }

        if (temperature >= 0) {
            list.add(LH.Chat.GREEN + String.format("Temperature: %d K", temperature));
            list.add(LH.Chat.WHITE + "State: " + (
                temperature < mat.mMaterial.mMeltingPoint ? "solid" :
                temperature < mat.mMaterial.mBoilingPoint ? "liquid" :
                temperature < mat.mMaterial.mPlasmaPoint  ? "gas" :
                        "plasma"
            ));
        }
        list.add(LH.Chat.WHITE + String.format("Melting point: %d K", mat.mMaterial.mMeltingPoint));
        list.add(LH.Chat.WHITE + String.format("Boiling point: %d K", mat.mMaterial.mBoilingPoint));
        list.add(LH.Chat.WHITE + String.format("Weight: %.3f kg", OM.weight(Collections.singletonList(mat))));
        list.add(LH.Chat.WHITE + String.format("Density: %.3f g/cm\u00B3", mat.mMaterial.mGramPerCubicCentimeter));

        if (mat.mMaterial.contains(TD.Properties.UNBURNABLE)) {
            list.add(LH.Chat.GREEN + LH.get(LH.TOOLTIP_UNBURNABLE));
        } else if (mat.mMaterial.contains(TD.Properties.FLAMMABLE)) {
            if (mat.mMaterial.contains(TD.Properties.EXPLOSIVE)) {
                list.add(LH.Chat.BLINKING_RED + LH.get(LH.TOOLTIP_FLAMMABLE_AND_EXPLOSIVE));
            } else {
                list.add(LH.Chat.RED + LH.get(LH.TOOLTIP_FLAMMABLE));
            }
        } else if (mat.mMaterial.contains(TD.Properties.EXPLOSIVE)) {
            list.add(LH.Chat.RED + LH.get(LH.TOOLTIP_EXPLOSIVE));
        }

        if (mat.mMaterial.mOriginalMod == null) {
            list.add(LH.Chat.BLUE + "Material from an Unknown Mod");
        } else if (mat.mMaterial.mOriginalMod == MD.MC) {
            list.add(LH.Chat.BLUE + "Vanilla Material");
        } else if (mat.mMaterial.mOriginalMod == MD.GAPI) {
            if (mat.mMaterial.mID > 0 && mat.mMaterial.mID < 8000) {
                list.add(LH.Chat.BLUE + "Material from the Periodic Table of Elements");
            } else {
                list.add(LH.Chat.BLUE + "Random Material handled by Greg API");
            }
        } else {
            list.add(LH.Chat.BLUE + "Material from " + mat.mMaterial.mOriginalMod.mName);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aIconRegister) {
        icon = aIconRegister.registerIcon(CS.ModIDs.GT + ":" + "ItemMaterialDisplay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMeta) {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        OreDictMaterialStack mat = UnDisplay(stack);
        if (renderPass == 0)
            return UT.Code.getRGBInt(mat.mMaterial.mRGBaSolid);
        else
            return 16777215;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack == null) return "";
        OreDictMaterialStack mat = UnDisplay(stack);
        return mat.mMaterial.mNameInternal;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack == null) return "";
        OreDictMaterialStack mat = UnDisplay(stack);
        return mat.mMaterial.getLocal();
    }

    @Override public final Item setUnlocalizedName(String aName) {return this;}
    @Override public final String getUnlocalizedName() {return name;}
    @Override public ItemStack getContainerItem(ItemStack aStack) {
        return null;
    }
    @Override public final boolean hasContainerItem(ItemStack aStack) {
        return false;
    }

    static {
        GuiContainerManager.addInputHandler(new ItemMaterialDisplayNeiHandler());
    }

    public static class ItemMaterialDisplayNeiHandler implements IContainerInputHandler {
        protected boolean canHandle(GuiContainer gui) {
            return gui instanceof ContainerClient;
        }

        protected boolean showUsages(ItemStack stackover) {
            OreDictMaterial mat = ItemMaterialDisplay.UnDisplay(stackover).mMaterial;
            if (mat != MT.NULL)
                return GuiUsageRecipe.openRecipeGui("item", OP.dust.mat(mat, 1));

            return false;
        }

        protected boolean showRecipes(ItemStack stackover) {
            OreDictMaterial mat = ItemMaterialDisplay.UnDisplay(stackover).mMaterial;
            if (mat != MT.NULL)
                return GuiCraftingRecipe.openRecipeGui("item", OP.ingot.mat(mat, 1));

            return false;
        }

        @Override
        public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
            ItemStack stackover = GuiContainerManager.getStackMouseOver(gui);
            if (stackover == null)
                return false;
            if(keyCode == NEIClientConfig.getKeyBinding("gui.usage") || (keyCode == NEIClientConfig.getKeyBinding("gui.recipe") && NEIClientUtils.shiftKey()))
                return showRecipes(stackover);
            if(keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
                return showUsages(stackover);

            return false;
        }

        @Override
        public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
            if (!canHandle(gui))
                return false;
            ItemStack stackover = GuiContainerManager.getStackMouseOver(gui);
            if (stackover == null)
                return false;
            if (button == 0)
                return showRecipes(stackover);
            if (button == 1)
                return showUsages(stackover);

            return false;
        }


        @Override public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {}
        @Override public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) { return false; }
        @Override public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {}
        @Override public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {}
        @Override public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) { return false; }
        @Override public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {}
        @Override public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {}
    }
}
