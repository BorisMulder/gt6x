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
            Filament_Molybdenum = new ILx("Filament_Molybdenum"),
            Cathode_Molybdenum = new ILx("Cathode_Molybdenum"),
            ElectronTube_Molybdenum = new ILx("ElectronTube_Molybdenum"),
            Filament_Tungsten = new ILx("Filament_Tungsten"),
            Cathode_Tungsten = new ILx("Cathode_Tungsten"),
            BJT_Ge_Base = new ILx("BJT_Ge_Base"),
            BJT_Si_Base = new ILx("BJT_Si_Base"),
            BJT_Ge = new ILx("BJT_Ge"),
            BJT_Si = new ILx("BJT_Si"),
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
            Plywood = new ILx("Plywood"),
            CCL = new ILx("CCL"),
            CCL_SMALL = new ILx("CCL_SMALL"),
            CCL_TINY = new ILx("CCL_TINY"),
            CCL_LONG = new ILx("CCL_LONG"),
            GCL = new ILx("GCL"),
            GCL_SMALL = new ILx("GCL_SMALL"),
            GCL_LONG = new ILx("GCL_LONG"),
            GCL_TINY = new ILx("GCL_TINY"),
            PCL = new ILx("PCL"),
            PCL_SMALL = new ILx("PCL_SMALL"),
            PCL_TINY = new ILx("PCL_TINY"),
            PCL_LONG = new ILx("PCL_LONG"),
            Circuit_Plate_Wood = new ILx(""),
            Circuit_Plate_Copper_Small = new ILx("Circuit_Plate_Copper_Small"),
            Circuit_Plate_Copper_Long = new ILx("Circuit_Plate_Copper_Long"),
            Circuit_Plate_Copper_Tiny = new ILx("Circuit_Plate_Copper_Tiny"),
            Circuit_Plate_Gold_Small = new ILx("Circuit_Plate_Gold_Small"),
            Circuit_Plate_Gold_Long = new ILx("Circuit_Plate_Gold_Long"),
            Circuit_Plate_Gold_Tiny = new ILx("Circuit_Plate_Gold_Tiny"),
            Circuit_Plate_Platinum_Small = new ILx("Circuit_Plate_Platinum_Small"),
            Circuit_Plate_Platinum_Long = new ILx("Circuit_Plate_Platinum_Long"),
            Circuit_Plate_Platinum_Tiny = new ILx("Circuit_Plate_Platinum_Tiny"),
            EtchMask_Trace = new ILx("EtchMask_Trace"),
            EtchMask_Trace_Long = new ILx("EtchMask_Trace_Long"),
            EtchMask_Trace_Small = new ILx("EtchMask_Trace_Small"),
            EtchMask_Trace_Tiny = new ILx("EtchMask_Trace_Tiny"),

            Comp_Laser_Gas_N = new ILx("Comp_Laser_Gas_N"),
            Comp_Laser_Gas_KrF = new ILx("Comp_Laser_Gas_KrF"),
            Comp_Laser_Gas_ArF = new ILx("Comp_Laser_Gas_ArF"),
            Comp_Laser_Molten_Sn = new ILx("Comp_Laser_Molten_Sn"),

            Capacitor_Tantalum = new ILx("Capacitor_Tantalum"),
            Resistor_Metal_Film = new ILx("Resistor_Metal_Film"),

            Photomask_Raw = new ILx("Photomask_Raw"),
            Wafer_GaAs_SiN_layered = new ILx("Wafer_GaAs_SiN_layered"),
            Wafer_Oxidized_P_Si = new ILx("Wafer_Oxidized_P_Si"),
            Wafer_Oxidized_N_Si = new ILx("Wafer_Oxidized_N_Si"),
            Wafer_Oxidized_P_SiGe = new ILx("Wafer_Oxidized_P_SiGe"),

            Rosin = new ILx("Rosin"),

            Thermal_Paste = new ILx("Thermal_Paste"),
            Al_Disk = new ILx("Al_Disk"),
            Hard_Disk = new ILx("Hard_Disk"),
            Hard_Disk_Advanced = new ILx("Hard_Disk_Advanced"),
            CPU_Fan = new ILx("CPU_Fan"),
            ComputerCase = new ILx("ComputerCase"),
            CRT_Black_White = new ILx("CRT_Black_White"),
            CRT_RGB = new ILx("CRT_RGB"),
            PolaroidFilter = new ILx("PolaroidFilter"),
            PolaroidFilterTiny = new ILx("PolaroidFilterTiny"),
            LEDStrip = new ILx("LEDStrip"),
            LEDBacklight = new ILx("LEDBacklight"),
            LCDElectrodes = new ILx("LCDElectrodes"),
            LCDElectrodesCrystal = new ILx("LCDElectrodesCrystal"),
            LCD = new ILx("LCD"),
            LCDMonitor = new ILx("LCDMonitor"),
            SolarPanelCdTe = new ILx("SolarPanelCdTe"),
            SolarPanelCIGS = new ILx("SolarPanelCIGS");

    // Photomasks & Wafers
    public static final int NUM_WAFER_TIERS = 3; // PMOS, NMOS, CMOS
    public static final int NUM_WAFER_TYPES = 9; // IC, CPU, RAM, GPU, Flash, SoC, FET, TFT, ColorFilter
    public static final int NUM_WAFER_STAGES_SINGLE = 8; // PL, Dev, Etch, Dope, Clean, Metal, Etch, Die
    public static final int NUM_WAFER_STAGES_DOUBLE = 14; // PL, Dev, Etch, Dope, Clean, Ox, PL2, Dev, Etch, Dope, Clean, Metal, Etch, Die
    public static final int NUM_WAFER_STAGES_MESFET = 8; // PL, Dev, Etch, Dope, Metal1, Metal2, Clean, Die
    public static final int NUM_WAFER_STAGES_TFT = 11; // Gate, PL, Dev, Etch, Clean, SiO2, IGZO, PL, Dev, ITO, Liftoff
    public static final int NUM_WAFER_STAGES_LCD_COLOR = 7; // PL, Dev, PL, Dev, PL, Dev, ITO
    public static final int NUM_PHOTOMASK_STAGES = 4; // PL, Dev, Etch, Clean

    public static IItemContainer[] ICs = new IItemContainer[NUM_WAFER_TIERS];
    public static IItemContainer[][][][] Photomasks = new IItemContainer[NUM_WAFER_TYPES][NUM_WAFER_TIERS][2][NUM_PHOTOMASK_STAGES];
    public static IItemContainer[][][] Wafers = new IItemContainer[NUM_WAFER_TYPES][NUM_WAFER_TIERS][NUM_WAFER_STAGES_DOUBLE];
    static {
        for (int tier = 0; tier < NUM_WAFER_TIERS; tier++) {
            ICs[tier] = new ILx(String.format("IC_%d", tier));
        }
        for (int type = 0; type < NUM_WAFER_TYPES; type++) for (int tier = 0; tier < NUM_WAFER_TIERS; tier++) {
            for (int plStage = 0; plStage < 2; plStage++) for (int stage = 0; stage < NUM_PHOTOMASK_STAGES; stage++) {
                Photomasks[type][tier][plStage][stage] = new ILx(String.format("Photomask_Type%d_Tier%d_Stage%d_%d", type, tier, plStage, stage));
            }
            for (int stage = 0; stage < NUM_WAFER_STAGES_DOUBLE; stage++) {
                Wafers[type][tier][stage] = new ILx(String.format("Wafer_Type%d_Tier%d_Stage%d", type, tier, stage));
            }
        }
    }

    // Computer tiers
    public static final int NUM_COMPUTER_TIERS = NUM_WAFER_TIERS;

    public static IItemContainer[] CPUs = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] DRAMChips = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] RAMSticks = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] GPUs = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] FlashChips = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] SoCs = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] GraphicsCards = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] HDDs = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] Motherboards = new IItemContainer[NUM_COMPUTER_TIERS];
    public static IItemContainer[] PCs = new IItemContainer[NUM_COMPUTER_TIERS];

    static {
        for (int tier = 0; tier < NUM_COMPUTER_TIERS; tier++) {
            CPUs[tier] = new ILx(String.format("CPU_%d", tier));
            DRAMChips[tier] = new ILx(String.format("DRAM_Chip_%d", tier));
            RAMSticks[tier] = new ILx(String.format("RAM_Stick_%d", tier));
            GPUs[tier] = new ILx(String.format("GPU_Chip_%d", tier));
            FlashChips[tier] = new ILx(String.format("Flash_Chip_%d", tier));
            SoCs[tier] = new ILx(String.format("SoC_%d", tier));
            GraphicsCards[tier] = new ILx(String.format("GPU_%d", tier));
            HDDs[tier] = new ILx(String.format("HDD_%d", tier));
            Motherboards[tier] = new ILx(String.format("MoBo_%d", tier));
            PCs[tier] = new ILx(String.format("PC_%d", tier));
        }
    }


    public static final int LEDWaferSteps = 4; // n-layer, active layer, p-layer, die
    public static final int LEDPackagingSteps = 2; // no cap, finished
    public static final String[] LEDWaferColors = { "Red", "Green", "Blue" };
    public static final String[] LEDColors = { "Red", "Green", "Blue", "White" };

    public static ILx[][] LEDWafers = new ILx[LEDWaferColors.length][LEDWaferSteps];
    static {
        for (int color = 0; color < LEDWaferColors.length; color++) {
            for (int step = 0; step < LEDWaferSteps; step++) {
                LEDWafers[color][step] = new ILx(String.format("WaferLED_%s_%d", LEDWaferColors[color], step));
            }
        }
    }

    public static ILx[][] LEDs = new ILx[LEDColors.length][LEDPackagingSteps];
    static {
        for (int color = 0; color < LEDColors.length; color++) {
            for (int step = 0; step < LEDPackagingSteps; step++) {
                LEDs[color][step] = new ILx(String.format("LED_%s_%d", LEDColors[color], step));
            }
        }
    }

    /// internal

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
