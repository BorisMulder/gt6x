package org.altadoon.gt6x.common.items;

import gregapi.api.Abstract_Mod;
import gregapi.code.IItemContainer;
import gregapi.code.TagData;
import gregapi.data.IL;
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
            // from here on, it is unused for now, and at some point uses SI prefixes
            "Fine Circuit",
            "Microchip",
            "Nanochip",
            "Picochip",
            "Femtochip",
            "Attochip",
            "Zeptochip"
    };

    private static final IL[] GT6Circuits = new IL[] {IL.Circuit_Primitive, IL.Circuit_Basic, IL.Circuit_Good, IL.Circuit_Advanced, IL.Circuit_Elite, IL.Circuit_Master, IL.Circuit_Ultimate};

    public static IItemContainer[][] PCBs = new IItemContainer[CIRCUIT_TIERS][CIRCUIT_TIERS];
    static {
        for (int tier = 0; tier < CIRCUIT_TIERS; tier++) {
            if (tier < GT6Circuits.length) {
                PCBs[tier][0] = GT6Circuits[tier];
            }

            for (int size = 1; size < CIRCUIT_TIERS; size++) {
                PCBs[tier][size] = new ILx(CIRCUIT_SIZE_NAMES[size] + "_" + CIRCUIT_TIER_NAMES[tier]);
            }
        }
    }

    public static ILx
            Display_OMStack = new ILx("Display_OMStack"),
            Fireclay_Ball = new ILx("Fireclay_Ball"),
            Electrode_Molybdenum = new ILx("Electrode_Molybdenum"),
            Electrode_Tungsten = new ILx("Electrode_Tungsten"),
            ElectronTube_Molybdenum = new ILx("ElectronTube_Molybdenum"),
            ElectronTube_Tungsten = new ILx("ElectronTube_Tungsten"),
            Transistor_ThroughHole = new ILx("Transistor_ThroughHole"),
            Capacitor_ThroughHole = new ILx("Capacitor_ThroughHole"),
            Resistor_ThroughHole = new ILx("Resistor_ThroughHole"),
            Transistor_SMD = new ILx("Transistor_SMD"),
            Capacitor_SMD = new ILx("Capacitor_SMD"),
            Resistor_SMD = new ILx("Resistor_SMD"),
            PlatinumBushing = new ILx("PlatinumBushing"),
            GlassFibres = new ILx("GlassFibres"),

            // Boards
            FR1_Board = new ILx("FR1_Board"),
            FR4_Board = new ILx("FR4_Board"),
            CCL = new ILx("CCL"),
            CCL_SMALL = new ILx("CCL_SMALL"),
            CCL_TINY = new ILx("CCL_TINY"),
            GCL = new ILx("GCL"),
            GCL_SMALL = new ILx("GCL_SMALL"),
            GCL_LONG = new ILx("GCL_LONG"),
            GCL_TINY = new ILx("GCL_TINY"),
            PCL = new ILx("PCL"),
            PCL_SMALL = new ILx("PCL_SMALL"),
            PCL_TINY = new ILx("PCL_TINY"),
            Circuit_Plate_Copper_Small = new ILx("Circuit_Plate_Copper_Small"),
            Circuit_Plate_Copper_Tiny = new ILx("Circuit_Plate_Copper_Tiny"),
            Circuit_Plate_Gold_Small = new ILx("Circuit_Plate_Gold_Small"),
            Circuit_Plate_Gold_RAM = new ILx("Circuit_Plate_Gold_RAM"),
            Circuit_Plate_Gold_Tiny = new ILx("Circuit_Plate_Gold_Tiny"),
            Circuit_Plate_Platinum_Small = new ILx("Circuit_Plate_Platinum_Small"),
            Circuit_Plate_Platinum_Tiny = new ILx("Circuit_Plate_Platinum_Tiny"),
            EtchMask_Trace = new ILx("EtchMask_Trace"),
            EtchMask_Trace_Small = new ILx("EtchMask_Trace_Small"),
            EtchMask_Trace_Tiny = new ILx("EtchMask_Trace_Tiny"),
            EtchMask_Trace_RAM = new ILx("EtchMask_Trace_RAM"),

            Comp_Laser_Gas_N = new ILx("Comp_Laser_Gas_N"),
            Comp_Laser_Gas_KrF = new ILx("Comp_Laser_Gas_KrF"),
            Comp_Laser_Gas_ArF = new ILx("Comp_Laser_Gas_ArF"),
            Comp_Laser_Molten_Sn = new ILx("Comp_Laser_Molten_Sn"),

            Capacitor_Tantalum = new ILx("Capacitor_Tantalum"),
            Resistor_Metal_Film = new ILx("Resistor_Metal_Film"),

            Photomask_Raw = new ILx("Photomask_Raw"),

            Photomask_Patterned_PMOS_IC = new ILx("Photomask_Patterned_PMOS_IC"),
            Photomask_Developed_PMOS_IC = new ILx("Photomask_Developed_PMOS_IC"),
            Photomask_Etched_PMOS_IC = new ILx("Photomask_Etched_PMOS_IC"),
            Photomask_PMOS_IC = new ILx("Photomask_PMOS_IC"),

            Photomask_Patterned_NMOS_IC = new ILx("Photomask_Patterned_NMOS_IC"),
            Photomask_Developed_NMOS_IC = new ILx("Photomask_Developed_NMOS_IC"),
            Photomask_Etched_NMOS_IC = new ILx("Photomask_Etched_NMOS_IC"),
            Photomask_NMOS_IC = new ILx("Photomask_NMOS_IC"),

            Photomask_Patterned_CMOS_IC_1 = new ILx("Photomask_Patterned_CMOS_IC_1"),
            Photomask_Developed_CMOS_IC_1 = new ILx("Photomask_Developed_CMOS_IC_1"),
            Photomask_Etched_CMOS_IC_1 = new ILx("Photomask_Etched_CMOS_IC_1"),
            Photomask_CMOS_IC_1 = new ILx("Photomask_CMOS_IC_1"),

            Photomask_Patterned_CMOS_IC_2 = new ILx("Photomask_Patterned_CMOS_IC_2"),
            Photomask_Developed_CMOS_IC_2 = new ILx("Photomask_Developed_CMOS_IC_2"),
            Photomask_Etched_CMOS_IC_2 = new ILx("Photomask_Etched_CMOS_IC_2"),
            Photomask_CMOS_IC_2 = new ILx("Photomask_CMOS_IC_2"),

            Photomask_Patterned_VLSI_1 = new ILx("Photomask_Patterned_VLSI_1"),
            Photomask_Developed_VLSI_1 = new ILx("Photomask_Developed_VLSI_1"),
            Photomask_Etched_VLSI_1 = new ILx("Photomask_Etched_VLSI_1"),
            Photomask_VLSI_1 = new ILx("Photomask_VLSI_1"),

            Photomask_Patterned_VLSI_2 = new ILx("Photomask_Patterned_VLSI_2"),
            Photomask_Developed_VLSI_2 = new ILx("Photomask_Developed_VLSI_2"),
            Photomask_Etched_VLSI_2 = new ILx("Photomask_Etched_VLSI_2"),
            Photomask_VLSI_2 = new ILx("Photomask_VLSI_2"),

            Photomask_Patterned_MESFET = new ILx("Photomask_Patterned_MESFET"),
            Photomask_Developed_MESFET = new ILx("Photomask_Developed_MESFET"),
            Photomask_Etched_MESFET = new ILx("Photomask_Etched_MESFET"),
            Photomask_MESFET = new ILx("Photomask_MESFET"),

            Photomask_Patterned_DRAM_1 = new ILx("Photomask_Patterned_DRAM_1"),
            Photomask_Developed_DRAM_1 = new ILx("Photomask_Developed_DRAM_1"),
            Photomask_Etched_DRAM_1 = new ILx("Photomask_Etched_DRAM_1"),
            Photomask_DRAM_1 = new ILx("Photomask_DRAM_1"),

            Photomask_Patterned_DRAM_2 = new ILx("Photomask_Patterned_DRAM_2"),
            Photomask_Developed_DRAM_2 = new ILx("Photomask_Developed_DRAM_2"),
            Photomask_Etched_DRAM_2 = new ILx("Photomask_Etched_DRAM_2"),
            Photomask_DRAM_2 = new ILx("Photomask_DRAM_2"),

            Wafer_Patterned_PMOS_IC = new ILx("Wafer_Patterned_PMOS_IC"),
            Wafer_Developed_PMOS_IC = new ILx("Wafer_Developed_PMOS_IC"),
            Wafer_Etched_PMOS_IC = new ILx("Wafer_Etched_PMOS_IC"),
            Wafer_Doped_PMOS_IC = new ILx("Wafer_Doped_PMOS_IC"),
            Wafer_Cleaned_PMOS_IC = new ILx("Wafer_Cleaned_PMOS_IC"),
            Wafer_Metal_PMOS_IC = new ILx("Wafer_Metal_PMOS_IC"),
            Wafer_PMOS_IC = new ILx("Wafer_PMOS_IC"),
            Die_PMOS_IC = new ILx("Die_PMOS_IC"),
            PMOS_IC = new ILx("PMOS_IC"),

            Wafer_Patterned_NMOS_IC = new ILx("Wafer_Patterned_NMOS_IC"),
            Wafer_Developed_NMOS_IC = new ILx("Wafer_Developed_NMOS_IC"),
            Wafer_Etched_NMOS_IC = new ILx("Wafer_Etched_NMOS_IC"),
            Wafer_Doped_NMOS_IC = new ILx("Wafer_Doped_NMOS_IC"),
            Wafer_Cleaned_NMOS_IC = new ILx("Wafer_Cleaned_NMOS_IC"),
            Wafer_Metal_NMOS_IC = new ILx("Wafer_Metal_NMOS_IC"),
            Wafer_NMOS_IC = new ILx("Wafer_NMOS_IC"),
            Die_NMOS_IC = new ILx("Die_NMOS_IC"),
            NMOS_IC = new ILx("NMOS_IC"),

            Wafer_Patterned_CMOS_IC_1 = new ILx("Wafer_Patterned_CMOS_IC_1"),
            Wafer_Developed_CMOS_IC_1 = new ILx("Wafer_Developed_CMOS_IC_1"),
            Wafer_Etched_CMOS_IC_1 = new ILx("Wafer_Etched_CMOS_IC_1"),
            Wafer_Doped_CMOS_IC_1 = new ILx("Wafer_Doped_CMOS_IC_1"),
            Wafer_Cleaned_CMOS_IC_1 = new ILx("Wafer_Cleaned_CMOS_IC_1"),
            Wafer_Oxidized_CMOS_IC_1 = new ILx("Wafer_Oxidized_CMOS_IC_1"),
            Wafer_Patterned_CMOS_IC_2 = new ILx("Wafer_Patterned_CMOS_IC_2"),
            Wafer_Developed_CMOS_IC_2 = new ILx("Wafer_Developed_CMOS_IC_2"),
            Wafer_Etched_CMOS_IC_2 = new ILx("Wafer_Etched_CMOS_IC_2"),
            Wafer_Doped_CMOS_IC_2 = new ILx("Wafer_Doped_CMOS_IC_2"),
            Wafer_Cleaned_CMOS_IC_2 = new ILx("Wafer_Cleaned_CMOS_IC_2"),
            Wafer_Metal_CMOS_IC = new ILx("Wafer_Metal_CMOS_IC"),
            Wafer_CMOS_IC = new ILx("Wafer_CMOS_IC"),
            Die_CMOS_IC = new ILx("Die_CMOS_IC"),
            CMOS_IC = new ILx("CMOS_IC"),

            Wafer_Patterned_VLSI_1 = new ILx("Wafer_Patterned_VLSI_1"),
            Wafer_Developed_VLSI_1 = new ILx("Wafer_Developed_VLSI_1"),
            Wafer_Etched_VLSI_1 = new ILx("Wafer_Etched_VLSI_1"),
            Wafer_Doped_VLSI_1 = new ILx("Wafer_Doped_VLSI_1"),
            Wafer_Cleaned_VLSI_1 = new ILx("Wafer_Cleaned_VLSI_1"),
            Wafer_Oxidized_VLSI_1 = new ILx("Wafer_Oxidized_VLSI_1"),
            Wafer_Patterned_VLSI_2 = new ILx("Wafer_Patterned_VLSI_2"),
            Wafer_Developed_VLSI_2 = new ILx("Wafer_Developed_VLSI_2"),
            Wafer_Etched_VLSI_2 = new ILx("Wafer_Etched_VLSI_2"),
            Wafer_Doped_VLSI_2 = new ILx("Wafer_Doped_VLSI_2"),
            Wafer_Cleaned_VLSI_2 = new ILx("Wafer_Cleaned_VLSI_2"),
            Wafer_Metal_VLSI = new ILx("Wafer_Metal_VLSI"),
            Wafer_VLSI = new ILx("Wafer_VLSI"),
            Die_VLSI = new ILx("Die_VLSI"),
            CPU = new ILx("CPU"),

            Wafer_GaAs_SiN_layered = new ILx("Wafer_GaAs_SiN_layered"),
            Wafer_Patterned_MESFET = new ILx("Wafer_Patterned_MESFET"),
            Wafer_Developed_MESFET = new ILx("Wafer_Developed_MESFET"),
            Wafer_Etched_MESFET = new ILx("Wafer_Etched_MESFET"),
            Wafer_Doped_MESFET = new ILx("Wafer_Doped_MESFET"),
            Wafer_Metal1_MESFET = new ILx("Wafer_Metal1_MESFET"),
            Wafer_Metal2_MESFET = new ILx("Wafer_Metal2_MESFET"),
            Wafer_MESFET = new ILx("Wafer_MESFET"),
            Die_MESFET = new ILx("Die_MESFET"),

            Wafer_Patterned_DRAM_1 = new ILx("Wafer_Patterned_DRAM_1"),
            Wafer_Developed_DRAM_1 = new ILx("Wafer_Developed_DRAM_1"),
            Wafer_Etched_DRAM_1 = new ILx("Wafer_Etched_DRAM_1"),
            Wafer_Doped_DRAM_1 = new ILx("Wafer_Doped_DRAM_1"),
            Wafer_Cleaned_DRAM_1 = new ILx("Wafer_Cleaned_DRAM_1"),
            Wafer_Oxidized_DRAM_1 = new ILx("Wafer_Oxidized_DRAM_1"),
            Wafer_Patterned_DRAM_2 = new ILx("Wafer_Patterned_DRAM_2"),
            Wafer_Developed_DRAM_2 = new ILx("Wafer_Developed_DRAM_2"),
            Wafer_Etched_DRAM_2 = new ILx("Wafer_Etched_DRAM_2"),
            Wafer_Doped_DRAM_2 = new ILx("Wafer_Doped_DRAM_2"),
            Wafer_Cleaned_DRAM_2 = new ILx("Wafer_Cleaned_DRAM_2"),
            Wafer_Metal_DRAM = new ILx("Wafer_Metal_DRAM"),
            Wafer_DRAM = new ILx("Wafer_DRAM"),
            Die_DRAM = new ILx("Die_DRAM"),
            Chip_DRAM = new ILx("Chip_DRAM"),
            RAM_Stick = new ILx("RAM_Stick"),

            Rosin = new ILx("Rosin"),

            Thermal_Paste = new ILx("Thermal_Paste");

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

    @Override
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

    @Override public Item getItem() {return item();}
    @Override public Block getBlock() {return block();}
}
