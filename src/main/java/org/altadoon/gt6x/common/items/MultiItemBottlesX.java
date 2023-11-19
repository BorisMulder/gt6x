package org.altadoon.gt6x.common.items;

import gregapi.data.FL;
import gregtech.items.MultiItemBottles;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.common.FLx;

public class MultiItemBottlesX extends MultiItemBottles {
    public static MultiItemBottlesX instance;

    public MultiItemBottlesX(String aModID, String aUnlocalized) {
        super(aModID, aUnlocalized);
    }

    public static void init(String modID) {
        instance = new MultiItemBottlesX(modID, "multiitembottles");
    }

    @Override
    public void addItems() {
        ILx.Thermal_Paste.set(addItem(1, "Thermal Paste", "", FL.make(FLx.ThermalPaste, 100)));
    }

    @Override
    public ItemStack getRotten(ItemStack stack) {
        return stack;
    }
}
