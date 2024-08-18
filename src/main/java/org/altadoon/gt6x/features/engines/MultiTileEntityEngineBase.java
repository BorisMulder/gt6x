package org.altadoon.gt6x.features.engines;

import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.FM;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.old.Textures;
import gregapi.recipes.Recipe;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregapi.tileentity.base.TileEntityBase10FacingDouble;
import gregapi.tileentity.behavior.TE_Behavior_Active_Trinary;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityAdjacentOnOff;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.altadoon.gt6x.common.rendering.Geometry;
import org.altadoon.gt6x.common.rendering.IconRotated;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.rendering.Geometry.*;

public class MultiTileEntityEngineBase extends TileEntityBase10FacingDouble implements IFluidHandler, ITileEntityFunnelAccessible, ITileEntityTapAccessible, ITileEntityEnergy, ITileEntityRunningActively, ITileEntityAdjacentOnOff {
    public boolean stopped = false;
    public short efficiency = 10000;
    public long energy = 0, rate = 32;
    public TagData energyTypeEmitted = TD.Energy.RU;
    public Recipe.RecipeMap recipes = FM.Engine;
    public Recipe lastRecipe = null;
    public FluidTankGT[] tanks = {new FluidTankGT(1000), new FluidTankGT(1000)};
    public TE_Behavior_Active_Trinary activity = null;

    @Override
    public void readFromNBT2(NBTTagCompound nbt) {
        super.readFromNBT2(nbt);
        energy = nbt.getLong(NBT_ENERGY);
        activity = new TE_Behavior_Active_Trinary(this, nbt);
        if (nbt.hasKey(NBT_STOPPED)) stopped = nbt.getBoolean(NBT_STOPPED);
        if (nbt.hasKey(NBT_OUTPUT)) rate = nbt.getLong(NBT_OUTPUT);
        if (nbt.hasKey(NBT_FUELMAP)) recipes = Recipe.RecipeMap.RECIPE_MAPS.get(nbt.getString(NBT_FUELMAP));
        if (nbt.hasKey(NBT_EFFICIENCY)) efficiency = (short) UT.Code.bind_(0, 10000, nbt.getShort(NBT_EFFICIENCY));
        if (nbt.hasKey(NBT_ENERGY_EMITTED)) energyTypeEmitted = TagData.createTagData(nbt.getString(NBT_ENERGY_EMITTED));
        tanks[0].readFromNBT(nbt, NBT_TANK+".0").setCapacity(rate * 10);
        tanks[1].readFromNBT(nbt, NBT_TANK+".1").setCapacity(rate * 10);
    }

    @Override
    public void writeToNBT2(NBTTagCompound nbt) {
        super.writeToNBT2(nbt);
        UT.NBT.setNumber(nbt, NBT_ENERGY, energy);
        UT.NBT.setBoolean(nbt, NBT_STOPPED, stopped);
        activity.save(nbt);
        tanks[0].writeToNBT(nbt, NBT_TANK+".0");
        tanks[1].writeToNBT(nbt, NBT_TANK+".1");
    }

    @Override
    public void addToolTips(List<String> list, ItemStack stack, boolean f3_H) {
        list.add(LH.Chat.CYAN     + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE + LH.get(recipes.mNameInternal));
        list.add(LH.getToolTipEfficiency(efficiency));
        LH.addEnergyToolTips(this, list, null, energyTypeEmitted, null, LH.get(LH.FACE_FRONT));
        list.add(LH.Chat.ORANGE   + LH.get(LH.NO_GUI_FUNNEL_TAP_TO_TANK));
        list.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_DETAIL_MAGNIFYINGGLASS));
        super.addToolTips(list, stack, f3_H);
    }

    @Override
    public void onTick2(long timer, boolean isServerSide) {
        if (isServerSide) {
            if (energy >= rate) {
                ITileEntityEnergy.Util.emitEnergyToNetwork(energyTypeEmitted, rate, 1, this);
                energy -= rate;
            }
            if (energy < rate * 2 && !stopped) {
                activity.mActive = F;
                Recipe tRecipe = recipes.findRecipe(this, lastRecipe, T, Long.MAX_VALUE, NI, tanks[0].AS_ARRAY, ZL_IS);
                if (tRecipe != null) {
                    if (tRecipe.mFluidOutputs.length == 0 || tanks[1].canFillAll(tRecipe.mFluidOutputs[0])) {
                        if (tRecipe.isRecipeInputEqual(T, F, tanks[0].AS_ARRAY, ZL_IS)) {
                            activity.mActive = T;
                            lastRecipe = tRecipe;
                            energy += UT.Code.units(tRecipe.getAbsoluteTotalPower(), 10000, efficiency, F);
                            if (tRecipe.mFluidOutputs.length > 0) tanks[1].fill(tRecipe.mFluidOutputs[0]);
                            while (energy < rate * 2 && (tRecipe.mFluidOutputs.length == 0 || tanks[1].canFillAll(tRecipe.mFluidOutputs[0])) && tRecipe.isRecipeInputEqual(T, F, tanks[0].AS_ARRAY, ZL_IS)) {
                                energy += UT.Code.units(tRecipe.getAbsoluteTotalPower(), 10000, efficiency, F);
                                if (tRecipe.mFluidOutputs.length > 0) tanks[1].fill(tRecipe.mFluidOutputs[0]);
                                if (tanks[0].isEmpty()) break;
                            }
                        } else {
                            // set remaining Fluid to null, in case the Fuel Type needs to be swapped out. But only if it was inactive for 64 ticks.
                            if (activity.mData == 0) tanks[0].setEmpty();
                        }
                    }
                } else {
                    // set remaining Fluid to null, because it is not valid Fuel anymore for whatever reason. MineTweaker happens to live Modpacks too sometimes. ;)
                    tanks[0].setEmpty();
                }
            }
            if (energy < 0) energy = 0;

            if (tanks[1].has()) {
                FL.move(tanks[1], getAdjacentTank(OPOS[mFacing]));
                if (FL.gas(tanks[1]) && !WD.hasCollide(worldObj, getOffset(OPOS[mFacing], 1))) {
                    tanks[1].setEmpty();
                }
            }
        }
    }

    @Override
    public long onToolClick2(String tool, long remainingDurability, long quality, Entity player, List<String> chatReturn, IInventory playerInventory, boolean sneaking, ItemStack stack, byte side, float hitX, float hitY, float hitZ) {
        long rReturn = super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, sneaking, stack, side, hitX, hitY, hitZ);
        if (rReturn > 0) return rReturn;

        if (isClientSide()) return 0;

        if (tool.equals(TOOL_plunger)) {
            if (tanks[1].has()) return GarbageGT.trash(tanks[1]);
            return GarbageGT.trash(tanks[0]);
        }

        if (tool.equals(TOOL_magnifyingglass)) {
            if (chatReturn != null) {
                chatReturn.add("Input: "  + tanks[0].content());
                chatReturn.add("Output: " + tanks[1].content());
            }
            return 1;
        }
        return 0;
    }

    @Override
    public boolean onTickCheck(long timer) {
        return activity.check(stopped) || super.onTickCheck(timer);
    }

    @Override
    public void setVisualData(byte data) {
        activity.mState = (byte)(data & 127);
    }

    @Override public byte getVisualData() {return activity.mState;}

    @Override
    protected IFluidTank getFluidTankFillable2(byte side, FluidStack fluidToFill) {
        return recipes.containsInput(fluidToFill, this, NI) ? tanks[0] : null;
    }

    @Override
    protected IFluidTank getFluidTankDrainable2(byte side, FluidStack fluidToDrain) {
        return tanks[1];
    }

    @Override
    protected IFluidTank[] getFluidTanks2(byte side) {
        return tanks;
    }

    @Override
    public int funnelFill(byte side, FluidStack fluid, boolean doFill) {
        if (!recipes.containsInput(fluid, this, NI)) return 0;
        updateInventory();
        return tanks[0].fill(fluid, doFill);
    }

    @Override
    public FluidStack tapDrain(byte side, int maxDrain, boolean doDrain) {
        updateInventory();
        return tanks[tanks[1].has() ? 1 : 0].drain(maxDrain, doDrain);
    }

    @Override public ItemStack[] getDefaultInventory(NBTTagCompound nbt) {return ZL_IS;}

    @Override public boolean isEnergyType(TagData energyType, byte side, boolean emitting) {return emitting && energyType == energyTypeEmitted;}
    @Override public boolean isEnergyEmittingTo(TagData energyType, byte side, boolean theoretical) {return side == mFacing && super.isEnergyEmittingTo(energyType, side, theoretical);}
    @Override public long getEnergyOffered(TagData energyType, byte side, long size) {return Math.min(rate, energy);}
    @Override public long getEnergySizeOutputRecommended(TagData energyType, byte side) {return rate;}
    @Override public long getEnergySizeOutputMin(TagData energyType, byte side) {return rate;}
    @Override public long getEnergySizeOutputMax(TagData energyType, byte side) {return rate;}
    @Override public Collection<TagData> getEnergyTypes(byte side) {return energyTypeEmitted.AS_LIST;}

    @Override public boolean getStateRunningPassively() {return activity.mActive;}
    @Override public boolean getStateRunningPossible() {return activity.mActive || (tanks[0].has() && !tanks[1].isFull());}
    @Override public boolean getStateRunningActively() {return activity.mActive;}
    @Override public boolean setAdjacentOnOff(boolean onOff) {stopped = !onOff; return !stopped;}
    @Override public boolean setStateOnOff(boolean onOff) {stopped = !onOff; return !stopped;}
    @Override public boolean getStateOnOff() {return !stopped;}

    @Override public float getSurfaceSizeAttachable (byte side) {return 0.25F;}
    @Override public boolean isSideSolid2           (byte side) {return ALONG_AXIS[side][mFacing] || side == mSecondFacing;}
    @Override public boolean isSurfaceOpaque2       (byte side) {return ALONG_AXIS[side][mFacing] || side == mSecondFacing;}
    @Override public boolean allowCovers            (byte side) {return ALONG_AXIS[side][mFacing] || side == mSecondFacing;}

    @Override public boolean[] getValidSides() { return NOT_ALONG_AXIS[mSecondFacing]; }
    @Override public boolean[] getValidSecondSides() { return NOT_ALONG_AXIS[mFacing]; }
    @Override public byte getDefaultSide() { return SIDE_FRONT; }
    @Override public byte getDefaultSecondSide() { return SIDE_BOTTOM; }

    @Override
    public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
        return 19;
    }

    @Override
    public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
        return switch (renderPass) {
            // front, back, bottom panes
            case 0 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[0], PX_P[0], PX_P[0], PX_N[0], PX_N[0], PX_P[1]));
            case 1 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[0], PX_P[0], PX_N[1], PX_N[0], PX_N[0], PX_N[0]));
            case 2 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[0], PX_P[0], PX_P[0], PX_N[0], PX_P[1], PX_N[0]));
            // frame rods
            case 3 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[0], PX_N[1], PX_P[1], PX_P[1], PX_N[0], PX_N[1]));
            case 4 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[1], PX_N[1], PX_P[1], PX_N[0], PX_N[0], PX_N[1]));
            // lower, middle engine block, cylinder cap block
            case 5 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[3], PX_P[1], PX_P[1], PX_N[3], PX_P[4], PX_N[1]));
            case 6 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[4], PX_P[4], PX_P[1], PX_N[4], PX_P[8], PX_N[1]));
            case 7 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[5], PX_P[8], PX_P[1], PX_N[5], PX_N[3], PX_N[1]));
            // 4+1 sides
            case 8 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[4], PX_P[8], PX_P[1], PX_N[4], PX_N[4], PX_P[2]));
            case 9 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[4], PX_P[8], PX_P[4], PX_N[4], PX_N[4], PX_P[5]));
            case 10-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[4], PX_P[8], PX_P[7], PX_N[4], PX_N[4], PX_N[7]));
            case 11-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[4], PX_P[8], PX_N[5], PX_N[4], PX_N[4], PX_N[4]));
            case 12-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[4], PX_P[8], PX_N[2], PX_N[4], PX_N[4], PX_N[1]));
            // intake, exhaust pipes
            case 13-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[1], PX_P[7], PX_P[1], PX_P[3], PX_N[7], PX_N[1]));
            case 14-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[3], PX_P[7], PX_P[1], PX_N[1], PX_N[7], PX_N[1]));
            // 4 connector pipes
            case 15-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[3], PX_N[8], PX_P[3], PX_N[3], PX_N[7], PX_P[4]));
            case 16-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[3], PX_N[8], PX_P[6], PX_N[3], PX_N[7], PX_P[7]));
            case 17-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[3], PX_N[8], PX_N[7], PX_N[3], PX_N[7], PX_N[6]));
            case 18-> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[3], PX_N[8], PX_N[4], PX_N[3], PX_N[7], PX_N[3]));
            default -> false;
        };
    }

    private ITexture getRotatedFlippedTexture(int idx, boolean mirror) {
        IIconContainer baseIcon = new IconRotated.RotatableIconContainer(baseIcons[idx], ROLL_INDEXES[mFacing][mSecondFacing], mirror, false);
        IIconContainer overlayIcon = new IconRotated.RotatableIconContainer((activity.mState>0? activeIcons : passiveIcons)[idx], ROLL_INDEXES[mFacing][mSecondFacing], mirror, false);

        return BlockTextureMulti.get(BlockTextureDefault.get(baseIcon, mRGBa), BlockTextureDefault.get(overlayIcon));
    }

    private ITexture getTextureByIndex(int idx) {
        return BlockTextureMulti.get(BlockTextureDefault.get(baseIcons[idx], mRGBa), BlockTextureDefault.get((activity.mState>0? activeIcons : passiveIcons)[idx]));
    }

    @Override
    public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
        switch(renderPass) {
            case 0, 1:
                if (side == mFacing || side == OPOS[mFacing]) {
                    if (renderPass == 0) return getTextureByIndex(renderPass);
                    else return getRotatedFlippedTexture(renderPass, side == mFacing);
                }
                if (side == mSecondFacing) return null;
                return getTextureByIndex(2);
            case 2:
                if (side == mFacing || side == OPOS[mFacing]) return null;
                return getTextureByIndex(2);
            default:
                return getTextureByIndex(3);
        }

    }

    // Icons
    public static IIconContainer[] baseIcons = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/bottom"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/colored/pipes"),
    }, passiveIcons = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/bottom"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay/pipes"),
    }, activeIcons = new IIconContainer[] {
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay_active/front"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay_active/back"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay_active/bottom"),
            new Textures.BlockIcons.CustomIcon("machines/generators/engine_base/overlay_active/pipes"),
    };

    @Override
    public String getTileEntityName() {return "gt6x.multitileentity.generator.engine_base";}

    @Override
    public boolean canDrop(int slot) {
        return false;
    }
}
