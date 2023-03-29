package org.altadoon.gt6x.common.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public interface IFluidFillableToolStats {
    int usesAddedPerFluidUnit(FluidStack fluidStack);

    int getMaxUses();

    int getRenderPasses();
    IIcon getIcon(ItemStack aStack, int aRenderPass);
    short[] getRGBa(ItemStack aStack, int aRenderPass);

    void addAdditionalToolTips(List<String> tooltips, ItemStack stack, boolean f3_H);
}
