package org.altadoon.gt6x.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.ItemStackSet;
import gregapi.data.LH;
import gregapi.data.TC;
import gregapi.data.TD;
import gregapi.item.IItemEnergy;
import gregapi.item.multiitem.MultiItem;
import gregapi.old.Textures;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import org.altadoon.gt6x.common.Pair;

import java.util.HashMap;
import java.util.List;

import static gregapi.data.CS.*;

public class MultiItemRefillableTool extends MultiItem implements IFluidContainerItem {
    public final HashMap<Short, IFluidFillableToolStats> toolStats = new HashMap<>();

    public MultiItemRefillableTool(String modId, String unlocalized) {
        super(modId, unlocalized);
        setMaxStackSize(1);
    }

    @Override
    public FluidStack getFluid(ItemStack container) { return null; }

    @Override
    public int getCapacity(ItemStack container) { return Integer.MAX_VALUE; }

    // does not actually fill a tank, only repairs the durability
    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        IFluidFillableToolStats stats = getToolStats(container);
        if (container.getItem() instanceof MultiItemRefillableTool && container.stackSize == 1) {
            int usesPerUnit = stats.usesAddedPerFluidUnit(resource);
            if (usesPerUnit > 0) {
                int currentUses = getUses(container);
                int toRefill = stats.getMaxUses() - currentUses;
                int unitsToRepair = toRefill / usesPerUnit;
                if (unitsToRepair <= 0) return 0;

                // round to previous whole unit
                OreDictMaterialStack mat = OreDictMaterial.FLUID_MAP.get(resource.getFluid().getName());
                if (mat == null) return 0;
                int litersPerUnit = mat.mMaterial.mLiquid.amount;
                int units = Math.min(unitsToRepair, resource.amount / litersPerUnit);
                int liters = units * litersPerUnit;

                if (doFill && units > 0) {
                    short meta = ST.meta(container);
                    if (!isUsableMeta(meta))
                        ST.meta(container, meta - 1); // change from empty to full version

                    setUses(container, currentUses + units * usesPerUnit);
                }
                return liters;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        return null;
    }

    protected static void setUses(ItemStack stack, int uses) {
        NBTTagCompound tNBT = UT.NBT.getNBT(stack);
        UT.NBT.setNumber(tNBT, "gt6x.refillabletool", uses);
        UT.NBT.set(stack, tNBT);
    }

    protected static int getUses(ItemStack stack) {
        NBTTagCompound tNBT = UT.NBT.getNBT(stack);
        return tNBT.getInteger("gt6x.refillabletool");
    }

    public boolean isUsableMeta(short meta) {
        return meta % 2 == 0;
    }
    public boolean isUsableMeta(ItemStack stack) {
        return isUsableMeta(ST.meta(stack));
    }

    public IFluidFillableToolStats getToolStats(short meta) {
        return toolStats.get(meta);
    }

    public IFluidFillableToolStats getToolStats(ItemStack stack) {
        return stack == null ? null : getToolStats(ST.meta(stack));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) { return isUsableMeta(stack); }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (!hasContainerItem(stack)) {
            return null;
        }

        int remainingUses = getUses(stack);
        short meta = ST.meta(stack);
        if (remainingUses <= 1) {
            return make(meta + 1);
        } else {
            setUses(stack, remainingUses - 1);
            return stack;
        }
    }


    @Override
    public int getRenderPasses(int metadata) {
        IFluidFillableToolStats tStats = getToolStats((short)metadata);
        if (tStats != null) return tStats.getRenderPasses()+2;
        return 2;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        IFluidFillableToolStats tStats = getToolStats(stack);
        if (tStats != null) return UT.Code.getRGBaInt(tStats.getRGBa(stack, renderPass));
        return 16777215;
    }

    @Override public IIcon getIconIndex(ItemStack stack) {return getIcon(stack, 0);}
    @Override public IIcon getIconFromDamageForRenderPass(int metaData, int renderPass) {return getIconFromDamage(metaData);}
    @Override public IIcon getIconFromDamage(int metaData) {return getIconIndex(ST.make(this, 1, metaData));}
    @Override public IIcon getIcon(ItemStack stack, int renderPass) {return getIcon(stack, renderPass, null, null, 0);}
    @Override public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usedStack, int usesRemaining) {
        IFluidFillableToolStats stats = getToolStats(stack);
        if (stats == null) return Textures.ItemIcons.VOID.getIcon(0);
        if (renderPass < stats.getRenderPasses()) {
            IIcon icon = stats.getIcon(stack, renderPass);
            return icon == null ? Textures.ItemIcons.VOID.getIcon(0) : icon;
        }
        if (player == null) {
            renderPass -= stats.getRenderPasses();
            if (renderPass == 0 && isUsableMeta(stack)) {
                int uses = getUses(stack), maxUses = stats.getMaxUses();
                if (maxUses <= 0) return Textures.ItemIcons.VOID.getIcon(0);
                if (uses <= 0) return Textures.ItemIcons.DURABILITY_BAR[0].getIcon(0);
                if (uses >= maxUses) return Textures.ItemIcons.DURABILITY_BAR[8].getIcon(0);
                return Textures.ItemIcons.DURABILITY_BAR[(int)Math.max(0, Math.min(7, uses * 8L / maxUses))].getIcon(0);
            }
            if (renderPass == 1) {
                IItemEnergy electric = getEnergyStats(stack);
                if (electric != null) {
                    long stored = electric.getEnergyStored(TD.Energy.EU, stack), capacity = electric.getEnergyCapacity(TD.Energy.EU, stack);
                    if (stored <= 0) return Textures.ItemIcons.ENERGY_BAR[0].getIcon(0);
                    if (stored >= capacity) return Textures.ItemIcons.ENERGY_BAR[8].getIcon(0);
                    return Textures.ItemIcons.ENERGY_BAR[7-(int)Math.max(0, Math.min(6, ((capacity-stored)*7L) / capacity))].getIcon(0);
                }
            }
        }
        return Textures.ItemIcons.VOID.getIcon(0);
    }

    @Override public final boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {return false;}
    @Override public final int getItemStackLimit(ItemStack stack) {return 1;}
    @Override public boolean isFull3D() {return true;}
    @Override public int getSpriteNumber() {return 1;}
    @Override public boolean requiresMultipleRenderPasses() {return true;}
    @Override @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister aIconRegister) {/**/}
    @Override @SuppressWarnings("deprecation") public boolean hasEffect(ItemStack stack) {return false;}
    @Override public boolean hasEffect(ItemStack stack, int renderPass) {return false;}
    @Override public int getItemEnchantability() {return 0;}
    @Override public boolean isBookEnchantable(ItemStack stack, ItemStack aBook) {return false;}
    @Override public boolean getIsRepairable(ItemStack stack, ItemStack aMaterial) {return false;}
    @Override public Long[] getFluidContainerStats(ItemStack stack) {return null;}
    @Override public IItemEnergy getEnergyStats(ItemStack stack) { return null; }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public final void getSubItems(Item var1, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < 32766; i++) {
            ItemStack stack = ST.make(this, 1, i);
            IFluidFillableToolStats stats = getToolStats(stack);
            if (stats != null) {
                setUses(stack, stats.getMaxUses());
                list.add(stack);
            }
        }
    }

    static {
        LH.add("gt6x.refillabletool.uses", "Remaining Uses: ");
    }

    @Override
    public void addAdditionalToolTips(List<String> tooltips, ItemStack stack, boolean f3_H) {
        IFluidFillableToolStats stats = getToolStats(stack);
        tooltips.add(LH.Chat.WHITE + LH.get("gt6x.refillabletool.uses") + LH.Chat.GREEN + UT.Code.makeString(getUses(stack)) + " / " + UT.Code.makeString(stats.getMaxUses()));
        stats.addAdditionalToolTips(tooltips, stack, f3_H);
    }

    /**
     * Register a refillable tool
     * @param id the item meta ID
     * @param english the english name
     * @param tooltip the english tooltip
     * @param toolStats the stats of the fillable tool
     * @param randomParameters Other parameters, such as ore dict names or ItemStackSets
     * @return a Pair containing the full and the empty version of the tool
     */
    public Pair<ItemStack, ItemStack> addTool(short id, String english, String tooltip, IFluidFillableToolStats toolStats, Object... randomParameters) {
        if (tooltip == null) tooltip = "";
        if (id >= 0 && id < Short.MAX_VALUE - 1 && isUsableMeta(id)) {
            LH.add(getUnlocalizedName() + "." +  id                    , english);
            LH.add(getUnlocalizedName() + "." +  id    + ".tooltip"    , tooltip);
            LH.add(getUnlocalizedName() + "." + (id+1)                 , english + " (Empty)");
            LH.add(getUnlocalizedName() + "." + (id+1) + ".tooltip"    , "You need to fill it with fluid (e.g. at a tap or crucible)");
            this.toolStats.put(id   , toolStats);
            this.toolStats.put((short) (id+1), toolStats);
            Pair<ItemStack, ItemStack> stacks = new Pair<>(ST.make(this, 1, id), ST.make(this, 1, id+1));
            setUses(stacks.key, toolStats.getMaxUses());

            for (ItemStack stack : new ItemStack[]{stacks.getKey(), stacks.getValue()}) {
                List<TC.TC_AspectStack> tAspects = new ArrayListNoNulls<>();
                for (Object randomParameter : randomParameters) {
                    if (randomParameter instanceof TC.TC_AspectStack)
                        ((TC.TC_AspectStack)randomParameter).addToAspectList(tAspects);
                    else if (randomParameter instanceof ItemStackSet)
                        ((ItemStackSet<?>)randomParameter).add(stack.copy());
                    else if (randomParameter instanceof OreDictItemData) {
                        OreDictManager.INSTANCE.addItemData_(stack, (OreDictItemData)randomParameter);
                    }
                    else if (isUsableMeta(stack))
                        OM.reg(randomParameter, stack);
                }
                if (COMPAT_TC != null) COMPAT_TC.registerThaumcraftAspectsToItem(stack, tAspects, F);
            }

            return stacks;
        }
        return null;
    }
}
