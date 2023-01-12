package org.altadoon.gt6x.features.metallurgy;

import gregapi.GT_API;
import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.old.Textures;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityServerTickPost;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.tileentity.data.ITileEntityWeight;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.tileentity.multiblocks.*;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.EAFSmeltingRecipe;
import org.altadoon.gt6x.common.ItemMaterialDisplay;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.gui.ContainerClientEAF;
import org.altadoon.gt6x.gui.ContainerCommonEAF;

import java.nio.ByteBuffer;
import java.util.*;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityEAF extends TileEntityBase10MultiBlockBase implements ITileEntityCrucible, ITileEntityEnergy, ITileEntityWeight, ITileEntityTemperature, ITileEntityGibbl, ITileEntityMold, ITileEntityServerTickPost, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockInventory, IMultiBlockFluidHandler, IFluidHandler {
    private static final int GAS_RANGE = 5;
    private static final int FLAME_RANGE = 5;
    public static final long MAX_UNITS = 64*3;
    private static final long KG_PER_ENERGY = 75;

    public static final int GUI_SLOTS = 12;

    protected boolean isActive = false;
    protected byte cooldown = 100;
    protected long storedEnergy = 0, currentTemperature = DEF_ENV_TEMP, oldTemperature = 0;
    protected double currentWeight = 0.0;

    /** Should remain sorted from least to most dense (depending on temperature). In case of gases, sorted by atomic weight. */
    protected List<OreDictMaterialStack> content = new ArrayListNoNulls<>();

    public IIconContainer[]
            texturesMaterial = L6_IICONCONTAINER,
            texturesInactive = L6_IICONCONTAINER,
            texturesActive = L6_IICONCONTAINER;

    public static final String GUI_TEXTURE = RES_PATH_GUI + "machines/EAF.png";

    private static class MaterialDensityComparator implements Comparator<OreDictMaterialStack> {
        private final long temperature;

        public MaterialDensityComparator(long temperature) {
            this.temperature = temperature;
        }

        @Override
        public int compare(OreDictMaterialStack o1, OreDictMaterialStack o2) {
            if (o1.mMaterial.mID == o2.mMaterial.mID)
                return 0;

            if (temperature >= o1.mMaterial.mBoilingPoint) {
                if (temperature >= o2.mMaterial.mBoilingPoint) {
                    return Double.compare(o1.mMaterial.getMass(), o2.mMaterial.getMass());
                } else {
                    return -1;
                }
            } else if (temperature >= o2.mMaterial.mBoilingPoint) {
                return 1;
            } else {
                return Double.compare(o1.mMaterial.mGramPerCubicCentimeter, o2.mMaterial.mGramPerCubicCentimeter);
            }
        }
    }

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.eaf";
    }

    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        storedEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_TEMPERATURE)) currentTemperature = aNBT.getLong(NBT_TEMPERATURE);
        if (aNBT.hasKey(NBT_TEMPERATURE+".old")) oldTemperature = aNBT.getLong(NBT_TEMPERATURE+".old");
        if (aNBT.hasKey(NBT_ACTIVE)) isActive = aNBT.getBoolean(NBT_ACTIVE);
        content = OreDictMaterialStack.loadList(NBT_MATERIALS, aNBT);

        if (CODE_CLIENT) {
            if (GT_API.sBlockIcons == null) {
                texturesMaterial = new IIconContainer[] {
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/colored/bottom"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/colored/top"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/colored/left"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/colored/front"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/colored/right"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/colored/back")};
                texturesInactive = new IIconContainer[] {
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay/bottom"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay/top"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay/left"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay/front"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay/right"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay/back")};
                texturesActive = new IIconContainer[] {
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay_active/bottom"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay_active/top"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay_active/left"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay_active/front"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay_active/right"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/eaf/overlay_active/back")};
            } else {
                TileEntity canonicalTileEntity = MultiTileEntityRegistry.getCanonicalTileEntity(getMultiTileEntityRegistryID(), getMultiTileEntityID());
                if (canonicalTileEntity instanceof MultiTileEntityEAF) {
                    texturesMaterial = ((MultiTileEntityEAF)canonicalTileEntity).texturesMaterial;
                    texturesInactive = ((MultiTileEntityEAF)canonicalTileEntity).texturesInactive;
                    texturesActive = ((MultiTileEntityEAF)canonicalTileEntity).texturesActive;
                }
            }
        }
    }

    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, NBT_ENERGY, storedEnergy);
        UT.NBT.setNumber(aNBT, NBT_TEMPERATURE, currentTemperature);
        UT.NBT.setNumber(aNBT, NBT_TEMPERATURE+".old", oldTemperature);
        UT.NBT.setBoolean(aNBT, NBT_ACTIVE, isActive);
        OreDictMaterialStack.saveList(NBT_MATERIALS, aNBT, content);
    }

    private boolean shouldBeAir(int i, int k) { return (i == 0 && Math.abs(k) != 2) || (k == 0 && Math.abs(i) != 2); }

    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);
        int tX = getOffsetXN(mFacing, 2), tY = yCoord - 1, tZ = getOffsetZN(mFacing, 2);
        if (worldObj.blockExists(tX-2, tY, tZ) && worldObj.blockExists(tX+2, tY, tZ) && worldObj.blockExists(tX, tY, tZ-2) && worldObj.blockExists(tX, tY, tZ+2)) {
            boolean tSuccess = true;
            for (int i = -2; i <= 2; i++) for (int j = 0; j < 4; j++) for (int k = -2; k <= 2; k++) {
                if (Math.abs(i) == 2 && Math.abs(k) == 2) continue;

                int side_io = MultiTileEntityMultiBlockPart.NOTHING;
                int design = 0;

                switch (j) {
                    case 0:
                        if ((i ==  0 && k ==  2 && (mFacing == SIDE_X_POS)) ||
                            (i ==  0 && k == -2 && (mFacing == SIDE_X_NEG)) ||
                            (i ==  2 && k ==  0 && (mFacing == SIDE_Z_POS)) ||
                            (i == -2 && k ==  0 && (mFacing == SIDE_Z_NEG))
                        ) {
                            design = 1;
                            side_io &= (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE & MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT);
                        }
                        if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 70, mteRegID, design, side_io)) {
                            tSuccess = false;
                            LOG.error("expected bricks at {},{},{}", tX+i, tY+j, tZ+k);
                        }
                        break;
                    case 1:
                        if (shouldBeAir(i, k)) {
                            if (getAir(tX+i, tY+j, tZ+k))
                                worldObj.setBlockToAir(tX + i, tY + j, tZ + k);
                            else {
                                LOG.error("expected air at {},{},{}", tX+i, tY+j, tZ+k);
                                tSuccess = false;
                            }
                        } else {
                            if ((i ==  0 && k == -2 && (mFacing == SIDE_X_POS)) ||
                                (i ==  0 && k ==  2 && (mFacing == SIDE_X_NEG)) ||
                                (i == -2 && k ==  0 && (mFacing == SIDE_Z_POS)) ||
                                (i ==  2 && k ==  0 && (mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io &= (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE & MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT);
                            }
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 70, mteRegID, design, side_io)) {
                                tSuccess = false;
                                LOG.error("expected bricks at {},{},{}", tX+i, tY+j, tZ+k);
                            }
                        }
                        break;
                    case 2:
                        if (i == 0 && k == 0) {
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 68, mteRegID, design, side_io)) {
                                tSuccess = false;
                                LOG.error("expected electrode at {},{},{}", tX+i, tY+j, tZ+k);
                            }
                        } else if (shouldBeAir(i, k)) {
                            if (getAir(tX+i, tY+j, tZ+k))
                                worldObj.setBlockToAir(tX + i, tY + j, tZ + k);
                            else {
                                tSuccess = false;
                                LOG.error("expected air at {},{},{}", tX+i, tY+j, tZ+k);
                            }
                        } else {
                            side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 70, mteRegID, design, side_io)) {
                                tSuccess = false;
                                LOG.error("expected bricks at {},{},{}", tX+i, tY+j, tZ+k);
                            }
                        }
                        break;
                    case 3:
                        if (Math.abs(i) == 2 || Math.abs(k) == 2) {
                            continue;
                        } else if (i == 0 && k == 0) {
                            design = 1;
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY + j, tZ + k, 68, mteRegID, design, side_io)) {
                                tSuccess = false;
                                LOG.error("expected electrode at {},{},{}", tX+i, tY+j, tZ+k);
                            }
                        } else {
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY + j, tZ + k, 69, mteRegID, design, side_io)) {
                                tSuccess = false;
                                LOG.error("expected roof at {},{},{}", tX+i, tY+j, tZ+k);
                            }
                        }
                }
            }
            return tSuccess;
        }
        return mStructureOkay;
    }

    @Override
    public boolean isInsideStructure(int x, int y, int z) {
        int xCenter = getOffsetXN(mFacing, 2), yBottom = yCoord - 1, zCenter = getOffsetZN(mFacing, 2);

        if (y >= yBottom && y <= yBottom + 2) {
            return Math.abs(x - xCenter) != 2 || Math.abs(z - zCenter) != 2;
        } else if (y == yBottom + 3) {
            return Math.abs(x - xCenter) != 2 && Math.abs(z - zCenter) != 2;
        }
        return false;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.eaf.1", "Bottom layer: 5x5 of 21 MgO-C Refractory Bricks without the four corners");
        LH.add("gt6x.tooltip.multiblock.eaf.2", "Second layer: similar circle of 15+1 MgO-C Refractory Bricks, but with a plus-shaped hole of Air in the middle.");
        LH.add("gt6x.tooltip.multiblock.eaf.3", "Main replaces one of those blocks at the middle of the second layer facing outwards.");
        LH.add("gt6x.tooltip.multiblock.eaf.4", "Third layer: same circle of 16 MgO-C Refractory Bricks, but with one block of Graphite Electrodes at the center.");
        LH.add("gt6x.tooltip.multiblock.eaf.5", "Fourth layer: 3x3 of 8 Alumina Refractory Bricks, one block of Graphite Electrodes at the center.");
        LH.add("gt6x.tooltip.multiblock.eaf.6", "Items in and gases out at the top, molten metal out at the hole in the bottom layer to the right of the main");
        LH.add("gt6x.tooltip.multiblock.eaf.7", "slag out at the hole in the second layer to the left of the main");
        LH.add("gt6x.tooltip.multiblock.eaf.8", "Energy in at the electrode on the top, fluids in at the third layer");
        LH.add("gt6x.tooltip.multiblock.eaf.9", "Use Gibbl-o-meter to measure the total amount of gases inside");
    }

    @Override
    public void addToolTips(List<String> tooltips, ItemStack stack, boolean modeF3_H) {
        tooltips.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.1"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.2"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.3"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.4"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.5"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.6"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.7"));
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.8"));
        tooltips.add(LH.getToolTipEfficiency(7500));
        tooltips.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X) + " 1 " + TD.Energy.EU.getLocalisedNameShort() + " " + LH.get(LH.CONVERTS_TO_Y) + " +1 K " + LH.get(LH.CONVERTS_PER_Z) + " "+ KG_PER_ENERGY + "kg (at least 512 Units per Tick required!)");
        tooltips.add(LH.Chat.YELLOW   + LH.get(LH.TOOLTIP_THERMALMASS) + String.format("%.2f", mMaterial.getWeight(U*200)) + " kg");
        tooltips.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + getTemperatureMax(SIDE_ANY) + " K)");
        tooltips.add(LH.Chat.ORANGE   + LH.get(LH.TOOLTIP_ACIDPROOF));
        tooltips.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_REMOVE_SHOVEL));
        tooltips.add(LH.Chat.DGRAY    + LH.get("gt6x.tooltip.multiblock.eaf.9"));
    }

    private boolean hasToAddTimer = T;

    @Override public void onUnregisterPost() {
        hasToAddTimer = T;
    }

    @Override
    public void onCoordinateChange() {
        super.onCoordinateChange();
        GT_API_Proxy.SERVER_TICK_POST.remove(this);
        onUnregisterPost();
    }

    @Override
    public void onTick2(long timer, boolean isServerSide) {
        if (isServerSide && hasToAddTimer) {
            GT_API_Proxy.SERVER_TICK_POST.add(this);
            hasToAddTimer = false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServerTickPost(boolean aFirst) {
        long temperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord), tHash = content.hashCode();

        if (!checkStructure(F)) {
            if (mInventoryChanged || SERVER_TIME % 1200 == 5) {
                if (checkStructure(T)) return;
            }

            if (SERVER_TIME % 10 == 0) {if (currentTemperature > temperature) currentTemperature--; if (currentTemperature < temperature) currentTemperature++;}
            currentTemperature = Math.max(currentTemperature, Math.min(200, temperature));
            return;
        }

        ItemStack itemStack = slot(0);

        if (ST.valid(itemStack)) {
            OreDictItemData itemData = OM.anydata_(itemStack);
            if (itemData == null) {
                slotTrash(0);
                UT.Sounds.send(SFX.MC_FIZZ, this);
            } else if (itemData.mPrefix == null) {
                List<OreDictMaterialStack> tList = new ArrayListNoNulls<>();
                for (OreDictMaterialStack tMaterial : itemData.getAllMaterialStacks()) if (tMaterial.mAmount > 0) tList.add(tMaterial.clone());
                if (addMaterialStacks(tList, temperature)) decrStackSize(0, 1);
            } else if (itemData.mPrefix == OP.oreRaw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial, itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * itemData.mMaterial.mMaterial.mOreMultiplier)), temperature)) decrStackSize(0, 1);
            } else if (itemData.mPrefix == OP.blockRaw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial, itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * itemData.mMaterial.mMaterial.mOreMultiplier * 9)), temperature)) decrStackSize(0, 1);
            } else if (itemData.mPrefix == OP.crateGtRaw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial, itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * itemData.mMaterial.mMaterial.mOreMultiplier * 16)), temperature)) decrStackSize(0, 1);
            } else if (itemData.mPrefix == OP.crateGt64Raw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial, itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * itemData.mMaterial.mMaterial.mOreMultiplier * 64)), temperature)) decrStackSize(0, 1);
            } else if (itemData.mPrefix.contains(TD.Prefix.STANDARD_ORE)) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial, itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * itemData.mMaterial.mMaterial.mOreMultiplier)), temperature)) decrStackSize(0, 1);
            } else if (itemData.mPrefix.contains(TD.Prefix.DENSE_ORE)) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial, itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * itemData.mMaterial.mMaterial.mOreMultiplier * 2)), temperature)) decrStackSize(0, 1);
            } else {
                List<OreDictMaterialStack> tList = new ArrayListNoNulls<>();
                for (OreDictMaterialStack tMaterial : itemData.getAllMaterialStacks()) if (tMaterial.mAmount > 0) tList.add(tMaterial.clone());
                if (addMaterialStacks(tList, temperature)) decrStackSize(0, 1);
            }
        }

        Set<OreDictMaterial> tAlreadyCheckedAlloys = new HashSetNoNulls<>();
        Set<EAFSmeltingRecipe> tAlreadyCheckedEAFRecipes = new HashSetNoNulls<>();

        OreDictMaterial preferredAlloy = null;
        IOreDictConfigurationComponent preferredRecipe = null;
        EAFSmeltingRecipe preferredEAFRecipe = null;
        long maxConversions = 0;
        boolean hasNewContent = (tHash != content.hashCode());

        for (OreDictMaterialStack stack : content) {
            // check EAF-specific recipes
            ArrayListNoNulls<EAFSmeltingRecipe> targetRecipes = EAFSmeltingRecipe.SmeltsInto.get(stack.mMaterial);
            if (targetRecipes != null) {
                for (EAFSmeltingRecipe recipe : targetRecipes)
                    if (tAlreadyCheckedEAFRecipes.add(recipe) && currentTemperature >= recipe.smeltingTemperature) {
                        List<OreDictMaterialStack> neededStuff = new ArrayListNoNulls<>();
                        for (OreDictMaterialStack ingredient : recipe.ingredients.getUndividedComponents()) {
                            neededStuff.add(OM.stack(ingredient.mMaterial, Math.max(1, ingredient.mAmount / U)));
                        }

                        if (!neededStuff.isEmpty()) {
                            boolean ingredientNotFound = false;
                            long nConversions = Long.MAX_VALUE;
                            for (OreDictMaterialStack needed : neededStuff) {
                                ingredientNotFound = true;
                                for (OreDictMaterialStack contained : content) {
                                    if (contained.mMaterial == needed.mMaterial) {
                                        nConversions = Math.min(nConversions, contained.mAmount / needed.mAmount);
                                        ingredientNotFound = false;
                                        break;
                                    }
                                }
                                if (ingredientNotFound) break;
                            }

                            // prefer the conversion with the largest amount of units converted
                            if (!ingredientNotFound && nConversions > 0) {
                                if (preferredEAFRecipe == null || nConversions * recipe.ingredients.getCommonDivider() > maxConversions * preferredEAFRecipe.ingredients.getCommonDivider()) {
                                    maxConversions = nConversions;
                                    preferredEAFRecipe = recipe;
                                }
                            }
                        }
                    }
            }

            // check normal smelting recipes if no EAF recipe is present
            if (preferredEAFRecipe == null && currentTemperature >= stack.mMaterial.mMeltingPoint) {
                for (OreDictMaterial alloy : stack.mMaterial.mAlloyComponentReferences) if (tAlreadyCheckedAlloys.add(alloy) && currentTemperature >= alloy.mMeltingPoint) {
                    for (IOreDictConfigurationComponent alloyRecipe : alloy.mAlloyCreationRecipes) {
                        List<OreDictMaterialStack> neededStuff = new ArrayListNoNulls<>();
                        for (OreDictMaterialStack tComponent : alloyRecipe.getUndividedComponents()) {
                            neededStuff.add(OM.stack(tComponent.mMaterial, Math.max(1, tComponent.mAmount / U)));
                        }

                        if (!neededStuff.isEmpty()) {
                            int nonMolten = 0;

                            boolean cancel = false;
                            long nConversions = Long.MAX_VALUE;
                            for (OreDictMaterialStack tComponent : neededStuff) {
                                if (currentTemperature < tComponent.mMaterial.mMeltingPoint) nonMolten++;

                                cancel = true;
                                for (OreDictMaterialStack tContent : content) {
                                    if (tContent.mMaterial == tComponent.mMaterial) {
                                        nConversions = Math.min(nConversions, tContent.mAmount / tComponent.mAmount);
                                        cancel = false;
                                        break;
                                    }
                                }
                                if (cancel) break;
                            }

                            if (!cancel && nonMolten <= 1 && nConversions > 0) {
                                if (preferredAlloy == null || preferredRecipe == null || nConversions * alloyRecipe.getCommonDivider() > maxConversions * preferredRecipe.getCommonDivider()) {
                                    maxConversions = nConversions;
                                    preferredRecipe = alloyRecipe;
                                    preferredAlloy = alloy;
                                }
                            }
                        }
                    }
                }
            }
        }

        boolean contentChanged = false;

        if (preferredEAFRecipe != null) {
            for (OreDictMaterialStack ingredient : preferredEAFRecipe.ingredients.getUndividedComponents()) {
                for (OreDictMaterialStack tContent : content) {
                    if (tContent.mMaterial == ingredient.mMaterial) {
                        tContent.mAmount -= UT.Code.units_(maxConversions, U, ingredient.mAmount, T);
                        break;
                    }
                }
            }
            for (OreDictMaterialStack result : preferredEAFRecipe.results.getUndividedComponents()) {
                OM.stack(result.mMaterial, preferredEAFRecipe.results.getCommonDivider() * maxConversions).addToList(content);
            }
            contentChanged = true;
        } else if (preferredAlloy != null && preferredRecipe != null) {
            for (OreDictMaterialStack tComponent : preferredRecipe.getUndividedComponents()) {
                for (OreDictMaterialStack tContent : content) {
                    if (tContent.mMaterial == tComponent.mMaterial) {
                        tContent.mAmount -= UT.Code.units_(maxConversions, U, tComponent.mAmount, T);
                        break;
                    }
                }
            }
            OM.stack(preferredAlloy, preferredRecipe.getCommonDivider() * maxConversions).addToList(content);
            contentChanged = true;
        }

        List<OreDictMaterialStack> toBeAdded = new ArrayListNoNulls<>();
        for (int i = 0; i < content.size(); i++) {
            OreDictMaterialStack stack = content.get(i);
            if (stack == null || stack.mMaterial == MT.NULL || stack.mMaterial == MT.Air || stack.mAmount <= 0) {
                GarbageGT.trash(content.remove(i--));
                contentChanged = true;
            } else if (currentTemperature > C + 40 && stack.mMaterial.contains(TD.Properties.FLAMMABLE) && !stack.mMaterial.containsAny(TD.Properties.UNBURNABLE, TD.Processing.MELTING)) {
                GarbageGT.trash(content.remove(i--));
                contentChanged = true;
                UT.Sounds.send(SFX.MC_FIZZ, this);
                if (stack.mMaterial.contains(TD.Properties.EXPLOSIVE)) explode(UT.Code.scale(stack.mAmount, MAX_UNITS*U, 8, F));
                return;
            } else if (currentTemperature >= stack.mMaterial.mMeltingPoint && (oldTemperature <  stack.mMaterial.mMeltingPoint || hasNewContent)) {
                content.remove(i--);
                OM.stack(stack.mMaterial.mTargetSmelting.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSmelting.mAmount, F)).addToList(toBeAdded);
                contentChanged = true;
            } else if (currentTemperature <  stack.mMaterial.mMeltingPoint && (oldTemperature >= stack.mMaterial.mMeltingPoint || hasNewContent)) {
                content.remove(i--);
                OM.stack(stack.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSolidifying.mAmount, F)).addToList(toBeAdded);
                contentChanged = true;
            }
        }
        for (int i = 0; i < toBeAdded.size(); i++) {
            OreDictMaterialStack stack = toBeAdded.get(i);
            if (stack == null || stack.mMaterial == MT.NULL || stack.mAmount <= 0) {
                GarbageGT.trash(toBeAdded.remove(i--));
            } else {
                stack.addToList(content);
            }
        }

        double tWeight = mMaterial.getWeight(U*100);

        oldTemperature = currentTemperature;

        long requiredEnergy = 1 + (long)(tWeight / KG_PER_ENERGY), conversions = storedEnergy / requiredEnergy;

        if (cooldown > 0) cooldown--;

        if (conversions != 0) {
            storedEnergy -= conversions * requiredEnergy;
            currentTemperature += conversions;
            cooldown = 100;
            isActive = true;
        }

        if (cooldown <= 0) {
            cooldown = 10;
            if (currentTemperature > temperature) currentTemperature--;
            if (currentTemperature < temperature) currentTemperature++;
            isActive = false;
        }

        currentTemperature = Math.max(currentTemperature, Math.min(200, temperature));

        content.sort(new MaterialDensityComparator(currentTemperature));

        //TODO remove
        LOG.debug("EAF content:");
        for (OreDictMaterialStack stack : content) {
            LOG.debug("{} units of {}", (double)stack.mAmount / U, stack.mMaterial.mNameInternal);
        }


        if (currentTemperature > getTemperatureMax(SIDE_INSIDE)) {
            UT.Sounds.send(SFX.MC_FIZZ, this);
            GarbageGT.trash(content);
            contentChanged = true;
            if (currentTemperature >= 320) try {
                for (EntityLivingBase tLiving : (List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box(-GAS_RANGE, -1, -GAS_RANGE, GAS_RANGE+1, GAS_RANGE+1, GAS_RANGE+1)))
                    UT.Entities.applyTemperatureDamage(tLiving, currentTemperature, 4);
            } catch(Throwable e) {
                e.printStackTrace(ERR);
            }
            int xCenter = getOffsetXN(mFacing, 2);
            int zCenter = getOffsetZN(mFacing, 2);

            for (int j = 0, k = UT.Code.bindInt(currentTemperature / 25); j < k; j++)
                WD.fire(worldObj,
                        xCenter-FLAME_RANGE+rng(2*FLAME_RANGE+1),
                        yCoord-2+rng(2+FLAME_RANGE),
                        zCenter-FLAME_RANGE+rng(2*FLAME_RANGE+1),
                        rng(3) != 0
                );
            for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++) {
                worldObj.setBlock(xCenter+i, yCoord-1, zCenter+j, Blocks.flowing_lava, 1, 3);
                worldObj.setBlock(xCenter+i, yCoord  , zCenter+j, Blocks.flowing_lava, 1, 3);
                worldObj.setBlock(xCenter+i, yCoord+1, zCenter+j, Blocks.flowing_lava, 1, 3);
            }
        }

        if (hasNewContent || contentChanged) {
            currentWeight = OM.weight(content);
        }

        if (oldTemperature != currentTemperature || hasNewContent || contentChanged) {
            updateClientData();
        }
    }

    private boolean canAddNewStacks(List<OreDictMaterialStack> stacks) {
        // don't allow to add more than max amount stacks
        int amountNewStacks = 0;
        for (OreDictMaterialStack stack : stacks) {
            boolean found = false;

            for (OreDictMaterialStack cnt : content) {
                if (cnt.mMaterial == stack.mMaterial) {
                    found = true;
                    break;
                }
            }

            if (!found) amountNewStacks++;
        }
        return content.size() + amountNewStacks <= GUI_SLOTS;
    }

    public boolean addMaterialStacks(List<OreDictMaterialStack> stacks, long temperature) {
        if (checkStructure(F) && OM.total(content)+OM.total(stacks) <= MAX_UNITS*U && canAddNewStacks(stacks)) {
            double crucibleWeight = OM.weight(content)+mMaterial.getWeight(U*100), stacksWeight = OM.weight(stacks);
            if (crucibleWeight+stacksWeight > 0) currentTemperature = temperature + (currentTemperature >temperature?+1:-1)*UT.Code.units(Math.abs(currentTemperature - temperature), (long)(crucibleWeight+stacksWeight), (long)crucibleWeight, false);
            for (OreDictMaterialStack stack : stacks) {
                LOG.debug("trying to add {} units of {}", (double)stack.mAmount / U, stack.mMaterial.mNameInternal);

                if (currentTemperature >= stack.mMaterial.mMeltingPoint) {
                    if (temperature <  stack.mMaterial.mMeltingPoint) {
                        OM.stack(stack.mMaterial.mTargetSmelting.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSmelting.mAmount, F)).addToList(content);
                    } else {
                        stack.addToList(content);
                    }
                } else {
                    if (temperature >= stack.mMaterial.mMeltingPoint) {
                        OM.stack(stack.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSolidifying.mAmount, F)).addToList(content);
                    } else {
                        stack.addToList(content);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isMoldInputSide(byte side) {
        return SIDES_TOP[side] && checkStructure(false);
    }

    @Override
    public long getMoldMaxTemperature() {
        return getTemperatureMax(SIDE_INSIDE);
    }

    @Override
    public long getMoldRequiredMaterialUnits() {
        return 1;
    }

    @Override
    public long fillMold(OreDictMaterialStack material, long temperature, byte side) {
        if (isMoldInputSide(side)) {
            if (addMaterialStacks(Collections.singletonList(material), temperature)) return material.mAmount;
            if (material.mAmount > U && addMaterialStacks(Collections.singletonList(OM.stack(material.mMaterial, U)), temperature)) return U;
        }
        return 0;
    }

    @Override
    public boolean breakBlock() {
        GarbageGT.trash(content);
        return super.breakBlock();
    }

    private boolean doPour(OreDictMaterialStack stack, ITileEntityMold mold, byte sideOfMold) {
        if (stack != null &&
                currentTemperature >= stack.mMaterial.mMeltingPoint &&
                currentTemperature < stack.mMaterial.mBoilingPoint &&
                stack.mMaterial.mTargetSmelting.mMaterial == stack.mMaterial) {
            long amount = mold.fillMold(stack, currentTemperature, sideOfMold);
            if (amount > 0) {
                stack.mAmount -= amount;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean fillMoldAtSide(ITileEntityMold mold, byte sideOfMachine, byte sideOfMold) {
        if (checkStructure(false)) {
            byte relative_side = FACING_ROTATIONS[mFacing][sideOfMachine];
            switch (relative_side) {
                case SIDE_LEFT:
                    if (content.size() > 1) {
                        for (int i = 0; i < content.size() - 1; i++) {
                            OreDictMaterialStack stack = content.get(i);
                            if (doPour(stack, mold, sideOfMold)) {
                                return true;
                            }
                        }
                    }
                    break;
                case SIDE_RIGHT:
                    if (content.size() > 0) {
                        OreDictMaterialStack stack = content.get(content.size() - 1);
                        return doPour(stack, mold, sideOfMold);
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
        super.onPlaced(stack, player, container, world, x, y, z, side, hitX, hitY, hitZ);

        currentTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);

        return true;
    }

    @Override
    public ITexture getTexture2(Block aBlock, int aRenderPass, byte side, boolean[] aShouldSideBeRendered) {
        return aShouldSideBeRendered[side]
                ? BlockTextureMulti.get(
                        BlockTextureDefault.get(texturesMaterial[FACING_ROTATIONS[mFacing][side]], mRGBa),
                        BlockTextureDefault.get((isActive ? texturesActive : texturesInactive)[FACING_ROTATIONS[mFacing][side]])
                )
                : null;
    }

    @Override
    public boolean onBlockActivated3(EntityPlayer aPlayer, byte aSide, float aHitX, float aHitY, float aHitZ) {
        if (isServerSide()) openGUI(aPlayer, aSide);
        return true;
    }

    @Override public byte getVisualData() { return (byte)(isActive?1:0); }
    @Override public void setVisualData(byte aData) { isActive=((aData&1)!=0); }

    @Override
    public IPacket getClientDataPacket(boolean aSendAll) {
        ByteBuffer buf = ByteBuffer.allocate(5 + Long.BYTES);
        buf.put(new byte[]{(byte)UT.Code.getR(mRGBa), (byte)UT.Code.getG(mRGBa), (byte)UT.Code.getB(mRGBa), getVisualData(), getDirectionData()});
        buf.putLong(currentTemperature);

        return getClientDataPacketByteArray(true, buf.array());
    }

    @Override
    public boolean receiveDataByteArray(byte[] aData, INetworkHandler aNetworkHandler) {
        ByteBuffer buf = ByteBuffer.wrap(aData);

        try {
            for (int i = 0; i < 5; i++) buf.get();

            currentTemperature = buf.getLong();
        } catch (Exception e) {
            LOG.error("failed to parse byte array", e);
        }
        return super.receiveDataByteArray(aData, aNetworkHandler);
    }

    @Override public byte getDefaultSide() { return SIDE_FRONT; }
    @Override public boolean[] getValidSides() {return isActive ? SIDES_THIS[mFacing] : SIDES_HORIZONTAL;}


    @Override public boolean allowCovers(byte side) {return false;}

    @Override public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {return new ItemStack[1];}
    @Override public int[] getAccessibleSlotsFromSide2(byte side) {return UT.Code.getAscendingArray(1);}
    @Override public boolean canInsertItem2(int aSlot, ItemStack aStack, byte side) {return !slotHas(0);}
    @Override public boolean canExtractItem2(int aSlot, ItemStack aStack, byte side) {return false;}
    @Override public int getInventoryStackLimit() {return (int)MAX_UNITS;}


    @Override public Object getGUIClient2(int aGUIID, EntityPlayer aPlayer) {return new ContainerClientEAF(aPlayer.inventory, this, aGUIID, GUI_TEXTURE);}
    @Override public Object getGUIServer2(int aGUIID, EntityPlayer aPlayer) {return new ContainerCommonEAF(aPlayer.inventory, this, aGUIID);}

    @Override public int getSizeInventoryGUI() {return GUI_SLOTS;}
    @Override public ItemStack decrStackSizeGUI(int aSlot, int aDecrement) {return null;}
    @Override public ItemStack getStackInSlotOnClosingGUI(int aSlot) {return null;}
    @Override public int getInventoryStackLimitGUI(int aSlot) {return getInventoryStackLimit();}

    private String getSide() {
        return isClientSide() ? "Client" : isServerSide() ? "Server" : "Unknown";
    }

    private final ItemStack[] clientGuiSlotContent = new ItemStack[GUI_SLOTS];

    @Override public ItemStack getStackInSlotGUI(int slot) {
        LOG.debug("getStackInSlotGUI called from {} on slot {}", getSide(), slot);

        if (isServerSide()) {
            if (slot >= content.size())
                return null;

            OreDictMaterialStack stack = content.get(slot);

            LOG.debug("it contains a stack of {} units of {}", (double) stack.mAmount / U, stack.mMaterial.getLocal());

            if (currentTemperature >= stack.mMaterial.mMeltingPoint) {
                FluidStack fluid = stack.mMaterial.fluid(currentTemperature, stack.mAmount, false);
                if (!FL.Error.is(fluid)) {
                    return FL.display(fluid, true, false, false);
                }
            }
            return ItemMaterialDisplay.display(stack, currentTemperature);
        } else {
            return clientGuiSlotContent[slot];
        }
    }

    @Override public void setInventorySlotContentsGUI(int slot, ItemStack stack) {
        if(stack == null) {
            return;
        }

        LOG.debug("setInventorySlotContentsGUI called from {} on slot {} with a stack of {} {}", getSide(), slot, stack.stackSize, stack.getDisplayName());

        if (isClientSide()) {
            clientGuiSlotContent[slot] = stack;
            mInventoryChanged = true;
        }
    }

    public static final List<TagData> ENERGYTYPES = new ArrayListNoNulls<>(false, TD.Energy.EU, TD.Energy.CU);

    @Override public boolean isEnergyType(TagData aEnergyType, byte side, boolean aEmitting) {return !aEmitting && ENERGYTYPES.contains(aEnergyType);}
    @Override public boolean isEnergyCapacitorType(TagData aEnergyType, byte side) {return ENERGYTYPES.contains(aEnergyType);}
    @Override public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte side, boolean aTheoretical) {return ENERGYTYPES.contains(aEnergyType);}
    @Override public long doInject(TagData aEnergyType, byte side, long aSize, long aAmount, boolean aDoInject) {
        if (aDoInject) {
            if (aEnergyType == TD.Energy.CU)
                storedEnergy -= Math.abs(aAmount * aSize);
            else
                storedEnergy += Math.abs(aAmount * aSize);
        }
        return aAmount;
    }
    @Override public long getEnergyDemanded(TagData aEnergyType, byte side, long aSize) {return Long.MAX_VALUE - storedEnergy;}
    @Override public long getEnergySizeInputMin(TagData aEnergyType, byte side) {return 512;}
    @Override public long getEnergySizeInputRecommended(TagData aEnergyType, byte side) {return 512;}
    @Override public long getEnergySizeInputMax(TagData aEnergyType, byte side) {return Long.MAX_VALUE;}
    @Override public Collection<TagData> getEnergyTypes(byte side) {return ENERGYTYPES;}

    @Override
    public long getTemperatureValue(byte side) {
        return currentTemperature;
    }

    @Override
    public long getTemperatureMax(byte side) {
        return (mMaterial.mMeltingPoint);
    }

    @Override
    public double getWeightValue(byte side) {return currentWeight;}

    @Override
    public long getGibblValue(byte side) {
        long result = 0;

        for (OreDictMaterialStack stack : content) {
            if (currentTemperature >= stack.mMaterial.mBoilingPoint) {
                result += UT.Code.units(stack.mAmount, U, 1000, true);
            }
        }

        return result;
    }

    @Override
    public long getGibblMax(byte side) {
        return UT.Code.units(MAX_UNITS, 1, 1000, true);
    }
}
