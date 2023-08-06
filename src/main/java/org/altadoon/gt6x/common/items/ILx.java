package org.altadoon.gt6x.common.items;

import gregapi.api.Abstract_Mod;
import gregapi.code.IItemContainer;
import gregapi.code.TagData;
import gregapi.data.MT;
import gregapi.item.IItemEnergy;
import gregapi.oredict.OreDictItemData;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static gregapi.data.CS.*;
import static gregapi.data.CS.ERR;

// well let's reinvent the wheel, but don't use enums since they cannot be extended
// "yes i made a mistake, you gotta deal with it" - Greg, March 15, 2023
public class ILx implements IItemContainer {
    public static ILx Display_OMStack = new ILx("Display_OMStack");
    public static ILx Fireclay_Ball = new ILx("Fireclay_Ball");

    public static final int CIRCUIT_TIERS = 10;
    public static final String[] CIRCUIT_TIER_NAMES = new String[] {
            MT.Primitive.getLocal(),
            MT.Basic.getLocal(),
            MT.Good.getLocal(),
            MT.Advanced.getLocal(),
            MT.Elite.getLocal(),
            MT.Master.getLocal(),
            MT.Ultimate.getLocal(),
            MT.Quantum.getLocal(),
            MT.Superconductor.getLocal(),
            MT.Infinite.getLocal(),
    };
    public static final String[] CIRCUIT_SIZE_NAMES = new String[] {
            "Circuit",
            "Small Circuit",
            "Tiny Circuit",
            "Fine Circuit",
            "Microchip",
            "Nanochip",
            "Picochip", // from here on, it is unused for now, but just uses SI prefixes
            "Femtochip",
            "Attochip",
            "Zeptochip"
    };
    public static ILx[][] Microchips = new ILx[CIRCUIT_TIERS][CIRCUIT_TIERS];
    static {
        for (int tier = 0; tier < CIRCUIT_TIERS; tier++) {
            for (int size = 0; size < CIRCUIT_TIERS; size++) {
                Microchips[tier][size] = new ILx(CIRCUIT_SIZE_NAMES[size] + "_" + CIRCUIT_TIER_NAMES[tier]);
            }
        }
    }

    public static ILx Electrode_Molybdenum = new ILx("Electrode_Molybdenum");
    public static ILx Electrode_Tungsten = new ILx("Electrode_Tungsten");
    public static ILx ElectronTube_Molybdenum = new ILx("ElectronTube_Molybdenum");
    public static ILx ElectronTube_Tungsten = new ILx("ElectronTube_Tungsten");
    public static ILx Transistor_ThroughHole = new ILx("Transistor_ThroughHole");
    public static ILx Capacitor_ThroughHole = new ILx("Capacitor_ThroughHole");
    public static ILx Resistor_ThroughHole = new ILx("Resistor_ThroughHole");
    public static ILx Transistor_SMD = new ILx("Transistor_SMD");
    public static ILx Capacitor_SMD = new ILx("Capacitor_SMD");
    public static ILx Resistor_SMD = new ILx("Resistor_SMD");
    public static ILx PlatinumBushing = new ILx("PlatinumBushing");
    public static ILx GlassFibres = new ILx("GlassFibres");

    // Boards
    public static ILx PF_Board = new ILx("PF_Board");
    public static ILx FRE_Board = new ILx("FRE_Board");
    public static ILx CCL = new ILx("CCL");
    public static ILx CCL_SMALL = new ILx("CCL_SMALL");
    public static ILx CCL_TINY = new ILx("CCL_TINY");
    public static ILx GCL = new ILx("GCL");
    public static ILx GCL_SMALL = new ILx("GCL_SMALL");
    public static ILx GCL_TINY = new ILx("GCL_TINY");
    public static ILx PCL = new ILx("PCL");
    public static ILx PCL_SMALL = new ILx("PCL_SMALL");
    public static ILx PCL_TINY = new ILx("PCL_TINY");
    public static ILx Circuit_Plate_Copper_Small = new ILx("Circuit_Plate_Copper_Small");
    public static ILx Circuit_Plate_Copper_Tiny = new ILx("Circuit_Plate_Copper_Tiny");
    public static ILx Circuit_Plate_Gold_Small = new ILx("Circuit_Plate_Gold_Small");
    public static ILx Circuit_Plate_Gold_Tiny = new ILx("Circuit_Plate_Gold_Tiny");
    public static ILx Circuit_Plate_Platinum_Small = new ILx("Circuit_Plate_Platinum_Small");
    public static ILx Circuit_Plate_Platinum_Tiny = new ILx("Circuit_Plate_Platinum_Tiny");
    public static ILx EtchMask_Trace = new ILx("EtchMask_Trace");
    public static ILx Comp_Laser_Gas_N = new ILx("Comp_Laser_Gas_N");
    public static ILx Comp_Laser_Gas_KrF = new ILx("Comp_Laser_Gas_KrF");
    public static ILx Comp_Laser_Gas_ArF = new ILx("Comp_Laser_Gas_ArF");
    public static ILx Comp_Laser_Molten_Sn = new ILx("Comp_Laser_Molten_Sn");


    public String name;
    private ItemStack mStack;
    private boolean mHasNotBeenSet = T;

    public ILx(String name) {
        this.name = name;
    }

    private boolean check() {
        if (mHasNotBeenSet && Abstract_Mod.sFinalized < Abstract_Mod.sModCountUsingGTAPI) ERR.println("The Item '" + name + "' has not been set to an Item at this time!");
        return !ST.invalid(mStack);
    }

    @Override
    public IItemContainer set(Item aItem) {
        mHasNotBeenSet = F;
        if (aItem == null) {
//          new Exception().printStackTrace(GT_Log.deb);
            return this;
        }
        mStack = ST.amount(1, ST.make(aItem, 1, 0));
        return this;
    }

    public IItemContainer set(Item aItem, long aMeta) {
        mHasNotBeenSet = F;
        if (aItem == null) {
//          new Exception().printStackTrace(GT_Log.deb);
            return this;
        }
        mStack = ST.amount(1, ST.make(aItem, 1, aMeta));
        return this;
    }

    @Override
    public IItemContainer set(ItemStack aStack) {
        mHasNotBeenSet = F;
        if (ST.invalid(aStack)) {
//          new Exception().printStackTrace(GT_Log.deb);
            return this;
        }
        mStack = ST.amount(1, aStack);
        return this;
    }

    public IItemContainer set(Item aItem, OreDictItemData aData, Object... aOreDict) {
        mHasNotBeenSet = F;
        if (aItem == null) {
//          new Exception().printStackTrace(GT_Log.deb);
            return this;
        }
        ItemStack aStack = ST.make(aItem, 1, 0);
        mStack = ST.amount(1, aStack);
        if (aData != null && !OM.reg(aData.toString(), ST.make(aItem, 1, W))) OM.data(ST.make(aItem, 1, W), aData);
        for (Object tOreDict : aOreDict) OM.reg(tOreDict, ST.make(aItem, 1, W));
        return this;
    }

    public IItemContainer set(ItemStack aStack, OreDictItemData aData, Object... aOreDict) {
        mHasNotBeenSet = F;
        if (ST.invalid(aStack)) {
//          new Exception().printStackTrace(DEB);
            return this;
        }
        mStack = ST.amount(1, aStack);
        if (aData != null && !OM.reg(aData.toString(), ST.amount(1, aStack))) OM.data(ST.amount(1, aStack), aData);
        for (Object tOreDict : aOreDict) OM.reg(tOreDict, ST.amount(1, aStack));
        return this;
    }

    @Override
    public Item item() {
        if (!check()) return null;
        return mStack.getItem();
    }

    @Override
    public Block block() {
        return ST.block(get(0));
    }

    @Override
    public boolean exists() {
        return ST.valid(mStack);
    }

    @Override
    public final boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    @Override
    public boolean equal(Object aStackOrBlock) {
        return mStack != null && (aStackOrBlock instanceof Block ? aStackOrBlock != NB && ST.block_(mStack) == aStackOrBlock : equal(aStackOrBlock, F, F));
    }

    @Override
    public boolean equal(Object aStack, boolean aWildcard, boolean aIgnoreNBT) {
        return mStack != null && (aWildcard ? ST.item((ItemStack)aStack) == ST.item_(mStack) : ST.equal((ItemStack)aStack, mStack, aIgnoreNBT));
    }


    @Override
    public ItemStack get(long aAmount, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        return ST.amount(aAmount, OM.get_(mStack));
    }

    @SuppressWarnings("deprecation") @Override
    public ItemStack getWildcard(long aAmount, Object... aReplacements) {
        return wild(aAmount, aReplacements);
    }

    @Override
    public ItemStack wild(long aAmount, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        return ST.copyAmountAndMeta(aAmount, W, OM.get_(mStack));
    }

    @Override
    public ItemStack getUndamaged(long aAmount, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        return ST.copyAmountAndMeta(aAmount, 0, OM.get_(mStack));
    }

    @Override
    public ItemStack getAlmostBroken(long aAmount, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        return ST.copyAmountAndMeta(aAmount, mStack.getMaxDamage()-1, OM.get_(mStack));
    }

    @Override
    public ItemStack getWithName(long aAmount, String aDisplayName, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (ST.invalid(rStack)) return null;
        rStack.setStackDisplayName(aDisplayName);
        return ST.amount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithNameAndNBT(long aAmount, String aDisplayName, NBTTagCompound aNBT, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (ST.invalid(rStack)) return null;
        UT.NBT.set(rStack, aNBT);
        if (aDisplayName != null) rStack.setStackDisplayName(aDisplayName);
        return ST.amount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithCharge(long aAmount, long aEnergy, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (ST.invalid(rStack)) return null;
        if (rStack.getItem() instanceof IItemEnergy) for (TagData tEnergyType : ((IItemEnergy)rStack.getItem()).getEnergyTypes(rStack)) ((IItemEnergy)rStack.getItem()).setEnergyStored(tEnergyType, rStack, aEnergy);
        return ST.amount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithMeta(long aAmount, long aMetaValue, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        return ST.copyAmountAndMeta(aAmount, aMetaValue, OM.get_(mStack));
    }

    @Override
    public ItemStack getWithDamage(long aAmount, long aMetaValue, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        return ST.copyAmountAndMeta(aAmount, aMetaValue, OM.get_(mStack));
    }

    @Override
    public ItemStack getWithNBT(long aAmount, NBTTagCompound aNBT, Object... aReplacements) {
        if (!check()) return ST.copyFirst(aReplacements);
        ItemStack rStack = ST.amount(aAmount, OM.get_(mStack));
        UT.NBT.set(rStack, aNBT);
        return rStack;
    }

    @Override
    public IItemContainer registerOre(Object... aOreNames) {
        check();
        for (Object tOreName : aOreNames) OM.reg(tOreName, get(1));
        return this;
    }

    @Override
    public IItemContainer registerWildcardAsOre(Object... aOreNames) {
        check();
        for (Object tOreName : aOreNames) OM.reg(tOreName, wild(1));
        return this;
    }

    @SuppressWarnings("deprecation") @Override public Item getItem() {return item();}
    @SuppressWarnings("deprecation") @Override public Block getBlock() {return block();}
}
