package org.altadoon.gt6x.features.metallurgy;

import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.HashSetNoNulls;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.tileentity.ITileEntityServerTickPost;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static gregapi.data.CS.*;

public class MultiTileEntityEAF extends TileEntityBase10MultiBlockBase implements ITileEntityCrucible, ITileEntityEnergy, ITileEntityWeight, ITileEntityTemperature, ITileEntityMold, ITileEntityServerTickPost, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockInventory, IMultiBlockFluidHandler, IFluidHandler {
    private static final int GAS_RANGE = 5;
    private static final int FLAME_RANGE = 5;
    private static final long MAX_AMOUNT = 64*3*U;
    private static final long KG_PER_ENERGY = 75;

    protected boolean mMeltDown = F;
    protected byte mCooldown = 100;
    protected long mEnergy = 0, mTemperature = DEF_ENV_TEMP, oTemperature = 0;
    protected List<OreDictMaterialStack> mContent = new ArrayListNoNulls<>();

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.eaf";
    }

    @Override
    public void readFromNBT2(NBTTagCompound aNBT) {
        super.readFromNBT2(aNBT);
        mEnergy = aNBT.getLong(NBT_ENERGY);
        if (aNBT.hasKey(NBT_TEMPERATURE)) mTemperature = aNBT.getLong(NBT_TEMPERATURE);
        if (aNBT.hasKey(NBT_TEMPERATURE+".old")) oTemperature = aNBT.getLong(NBT_TEMPERATURE+".old");
        mContent = OreDictMaterialStack.loadList(NBT_MATERIALS, aNBT);
        mMeltDown = (mTemperature+100 > getTemperatureMax(SIDE_ANY));
    }

    @Override
    public void writeToNBT2(NBTTagCompound aNBT) {
        super.writeToNBT2(aNBT);
        UT.NBT.setNumber(aNBT, NBT_ENERGY, mEnergy);
        UT.NBT.setNumber(aNBT, NBT_TEMPERATURE, mTemperature);
        UT.NBT.setNumber(aNBT, NBT_TEMPERATURE+".old", oTemperature);
        OreDictMaterialStack.saveList(NBT_MATERIALS, aNBT, mContent);
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
                        if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 70, mteRegID, design, side_io)) tSuccess = false;
                        break;
                    case 1:
                        if (shouldBeAir(i, k)) {
                            if (getAir(tX+i, tY+j, tZ+k))
                                worldObj.setBlockToAir(tX + i, tY + j, tZ + k);
                            else tSuccess = false;
                        } else {
                            if ((i ==  0 && k == -2 && (mFacing == SIDE_X_POS)) ||
                                (i ==  0 && k ==  2 && (mFacing == SIDE_X_NEG)) ||
                                (i == -2 && k ==  0 && (mFacing == SIDE_Z_POS)) ||
                                (i ==  2 && k ==  0 && (mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io &= (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE & MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT);
                            }
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 70, mteRegID, design, side_io)) tSuccess = false;
                        }
                        break;
                    case 2:
                        if (i == 0 && k == 0) {
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 68, mteRegID, design, side_io)) tSuccess = false;
                        } else if (shouldBeAir(i, k)) {
                            if (getAir(tX+i, tY+j, tZ+k))
                                worldObj.setBlockToAir(tX + i, tY + j, tZ + k);
                            else tSuccess = false;
                        } else {
                            side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX+i, tY+j, tZ+k, 70, mteRegID, design, side_io)) tSuccess = false;
                        }
                        break;
                    case 3:
                        if (i == 2 || k == 2) {
                            continue;
                        } else if (i == 0 && k == 0) {
                            design = 1;
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY + j, tZ + k, 68, mteRegID, design, side_io))
                                tSuccess = false;
                        } else {
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, tX + i, tY + j, tZ + k, 69, mteRegID, design, side_io))
                                tSuccess = false;
                        }
                }
            }
            return tSuccess;
        }
        return mStructureOkay;
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        return aX >= xCoord - 1 && aY >= yCoord && aZ >= zCoord - 1 && aX <= xCoord + 1 && aY <= yCoord + 2 && aZ <= zCoord + 1;
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
    }

    @Override
    public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
        aList.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.1"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.2"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.3"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.4"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.5"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.6"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.7"));
        aList.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.8"));
        aList.add(LH.getToolTipEfficiency(7500));
        aList.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X) + " 1 " + TD.Energy.EU.getLocalisedNameShort() + " " + LH.get(LH.CONVERTS_TO_Y) + " +1 K " + LH.get(LH.CONVERTS_PER_Z) + " "+ KG_PER_ENERGY + "kg (at least 512 Units per Tick required!)");
        aList.add(LH.Chat.YELLOW   + LH.get(LH.TOOLTIP_THERMALMASS) + mMaterial.getWeight(U*200) + " kg");
        aList.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + getTemperatureMax(SIDE_ANY) + " K)");
        aList.add(LH.Chat.ORANGE   + LH.get(LH.TOOLTIP_ACIDPROOF));
        aList.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_REMOVE_SHOVEL));
    }

    private boolean mHasToAddTimer = T;

    @Override public void onUnregisterPost() {mHasToAddTimer = T;}

    @Override
    public void onCoordinateChange() {
        super.onCoordinateChange();
        GT_API_Proxy.SERVER_TICK_POST.remove(this);
        onUnregisterPost();
    }

    @Override
    public void onTick2(long aTimer, boolean aIsServerSide) {
        if (aIsServerSide && mHasToAddTimer) {
            GT_API_Proxy.SERVER_TICK_POST.add(this);
            mHasToAddTimer = F;
        }
    }

    @Override
    public void onServerTickPost(boolean aFirst) {
        long tTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord), tHash = mContent.hashCode();

        if (!checkStructure(F)) {
            if (mInventoryChanged || SERVER_TIME % 1200 == 5) {
                if (checkStructure(T)) return;
            }

            if (SERVER_TIME % 10 == 0) {if (mTemperature > tTemperature) mTemperature--; if (mTemperature < tTemperature) mTemperature++;}
            mTemperature = Math.max(mTemperature, Math.min(200, tTemperature));
            return;
        }

        if (SERVER_TIME % 600 == 10 && worldObj.isRaining() && getRainOffset(0, 1, 0)) {
            BiomeGenBase tBiome = getBiome();
            if (tBiome.rainfall > 0 && tBiome.temperature >= 0.2) {
                addMaterialStacks(Collections.singletonList(OM.stack(MT.Water, U100 * (long) Math.max(1, tBiome.rainfall * 100) * (worldObj.isThundering() ? 2 : 1))), tTemperature);
            }
        }

        if (!slotHas(0)) slot(0, WD.suck(worldObj, xCoord-0.5, yCoord+PX_P[2], zCoord-0.5, 2, 3, 2));

        ItemStack tStack = slot(0);

        if (ST.valid(tStack)) {
            OreDictItemData tData = OM.anydata_(tStack);
            if (tData == null) {
                slotTrash(0);
                UT.Sounds.send(SFX.MC_FIZZ, this);
            } else if (tData.mPrefix == null) {
                List<OreDictMaterialStack> tList = new ArrayListNoNulls<>();
                for (OreDictMaterialStack tMaterial : tData.getAllMaterialStacks()) if (tMaterial.mAmount > 0) tList.add(tMaterial.clone());
                if (addMaterialStacks(tList, tTemperature)) decrStackSize(0, 1);
            } else if (tData.mPrefix == OP.oreRaw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(tData.mMaterial.mMaterial.mTargetCrushing.mMaterial, tData.mMaterial.mMaterial.mTargetCrushing.mAmount * tData.mMaterial.mMaterial.mOreMultiplier)), tTemperature)) decrStackSize(0, 1);
            } else if (tData.mPrefix == OP.blockRaw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(tData.mMaterial.mMaterial.mTargetCrushing.mMaterial, tData.mMaterial.mMaterial.mTargetCrushing.mAmount * tData.mMaterial.mMaterial.mOreMultiplier * 9)), tTemperature)) decrStackSize(0, 1);
            } else if (tData.mPrefix == OP.crateGtRaw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(tData.mMaterial.mMaterial.mTargetCrushing.mMaterial, tData.mMaterial.mMaterial.mTargetCrushing.mAmount * tData.mMaterial.mMaterial.mOreMultiplier * 16)), tTemperature)) decrStackSize(0, 1);
            } else if (tData.mPrefix == OP.crateGt64Raw) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(tData.mMaterial.mMaterial.mTargetCrushing.mMaterial, tData.mMaterial.mMaterial.mTargetCrushing.mAmount * tData.mMaterial.mMaterial.mOreMultiplier * 64)), tTemperature)) decrStackSize(0, 1);
            } else if (tData.mPrefix.contains(TD.Prefix.STANDARD_ORE)) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(tData.mMaterial.mMaterial.mTargetCrushing.mMaterial, tData.mMaterial.mMaterial.mTargetCrushing.mAmount * tData.mMaterial.mMaterial.mOreMultiplier)), tTemperature)) decrStackSize(0, 1);
            } else if (tData.mPrefix.contains(TD.Prefix.DENSE_ORE)) {
                if (addMaterialStacks(Collections.singletonList(OM.stack(tData.mMaterial.mMaterial.mTargetCrushing.mMaterial, tData.mMaterial.mMaterial.mTargetCrushing.mAmount * tData.mMaterial.mMaterial.mOreMultiplier * 2)), tTemperature)) decrStackSize(0, 1);
            } else {
                List<OreDictMaterialStack> tList = new ArrayListNoNulls<>();
                for (OreDictMaterialStack tMaterial : tData.getAllMaterialStacks()) if (tMaterial.mAmount > 0) tList.add(tMaterial.clone());
                if (addMaterialStacks(tList, tTemperature)) decrStackSize(0, 1);
            }
        }

        Set<OreDictMaterial> tAlreadyCheckedAlloys = new HashSetNoNulls<>();

        OreDictMaterial tPreferredAlloy = null;
        IOreDictConfigurationComponent tPreferredRecipe = null;
        long tMaxConversions = 0;
        boolean tNewContent = (tHash != mContent.hashCode());

        for (OreDictMaterialStack tMaterial : mContent) {
            if (mTemperature >= tMaterial.mMaterial.mMeltingPoint) {
                for (OreDictMaterial tAlloy : tMaterial.mMaterial.mAlloyComponentReferences) if (tAlreadyCheckedAlloys.add(tAlloy) && mTemperature >= tAlloy.mMeltingPoint) {
                    for (IOreDictConfigurationComponent tAlloyRecipe : tAlloy.mAlloyCreationRecipes) {
                        List<OreDictMaterialStack> tNeededStuff = new ArrayListNoNulls<>();
                        for (OreDictMaterialStack tComponent : tAlloyRecipe.getUndividedComponents()) {
                            tNeededStuff.add(OM.stack(tComponent.mMaterial, Math.max(1, tComponent.mAmount / U)));
                        }

                        if (!tNeededStuff.isEmpty()) {
                            int tNonMolten = 0;

                            boolean tBreak = F;
                            long tConversions = Long.MAX_VALUE;
                            for (OreDictMaterialStack tComponent : tNeededStuff) {
                                if (mTemperature < tComponent.mMaterial.mMeltingPoint) tNonMolten++;

                                tBreak = T;
                                for (OreDictMaterialStack tContent : mContent) {
                                    if (tContent.mMaterial == tComponent.mMaterial) {
                                        tConversions = Math.min(tConversions, tContent.mAmount / tComponent.mAmount);
                                        tBreak = F;
                                        break;
                                    }
                                }
                                if (tBreak) break;
                            }

                            if (!tBreak && tNonMolten <= 1 && tConversions > 0) {
                                if (tPreferredAlloy == null || tPreferredRecipe == null || tConversions * tAlloyRecipe.getCommonDivider() > tMaxConversions * tPreferredRecipe.getCommonDivider()) {
                                    tMaxConversions = tConversions;
                                    tPreferredRecipe = tAlloyRecipe;
                                    tPreferredAlloy = tAlloy;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (tPreferredAlloy != null && tPreferredRecipe != null) {
            for (OreDictMaterialStack tComponent : tPreferredRecipe.getUndividedComponents()) {
                for (OreDictMaterialStack tContent : mContent) {
                    if (tContent.mMaterial == tComponent.mMaterial) {
                        tContent.mAmount -= UT.Code.units_(tMaxConversions, U, tComponent.mAmount, T);
                        break;
                    }
                }
            }
            OM.stack(tPreferredAlloy, tPreferredRecipe.getCommonDivider() * tMaxConversions).addToList(mContent);
        }

        List<OreDictMaterialStack> tToBeAdded = new ArrayListNoNulls<>();
        for (int i = 0; i < mContent.size(); i++) {
            OreDictMaterialStack tMaterial = mContent.get(i);
            if (tMaterial == null || tMaterial.mMaterial == MT.NULL || tMaterial.mMaterial == MT.Air || tMaterial.mAmount <= 0) {
                GarbageGT.trash(mContent.remove(i--));
            } else if (tMaterial.mMaterial.mGramPerCubicCentimeter <= WEIGHT_AIR_G_PER_CUBIC_CENTIMETER) {
                GarbageGT.trash(mContent.remove(i--));
                UT.Sounds.send(SFX.MC_FIZZ, this);
            } else if (mTemperature >= tMaterial.mMaterial.mBoilingPoint || (mTemperature > C + 40 && tMaterial.mMaterial.contains(TD.Properties.FLAMMABLE) && !tMaterial.mMaterial.containsAny(TD.Properties.UNBURNABLE, TD.Processing.MELTING))) {
                GarbageGT.trash(mContent.remove(i--));
                UT.Sounds.send(SFX.MC_FIZZ, this);
                if (tMaterial.mMaterial.mBoilingPoint >=  320) try {for (EntityLivingBase tLiving : (List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box(-GAS_RANGE, -1, -GAS_RANGE, GAS_RANGE+1, GAS_RANGE+1, GAS_RANGE+1))) UT.Entities.applyTemperatureDamage(tLiving, tMaterial.mMaterial.mBoilingPoint, 4);} catch(Throwable e) {e.printStackTrace(ERR);}
                if (tMaterial.mMaterial.mBoilingPoint >= 2000) for (int j = 0, k = Math.max(1, UT.Code.bindInt((9 * tMaterial.mAmount) / U)); j < k; j++) WD.fire(worldObj, xCoord-FLAME_RANGE+rng(2*FLAME_RANGE+1), yCoord-1+rng(2+FLAME_RANGE), zCoord-FLAME_RANGE+rng(2*FLAME_RANGE+1), rng(3) != 0);
                if (tMaterial.mMaterial.contains(TD.Properties.EXPLOSIVE)) explode(UT.Code.scale(tMaterial.mAmount, MAX_AMOUNT, 8, F));
                return;
            } else if (mTemperature >= tMaterial.mMaterial.mMeltingPoint && (oTemperature <  tMaterial.mMaterial.mMeltingPoint || tNewContent)) {
                mContent.remove(i--);
                OM.stack(tMaterial.mMaterial.mTargetSmelting.mMaterial, UT.Code.units_(tMaterial.mAmount, U, tMaterial.mMaterial.mTargetSmelting.mAmount, F)).addToList(tToBeAdded);
            } else if (mTemperature <  tMaterial.mMaterial.mMeltingPoint && (oTemperature >= tMaterial.mMaterial.mMeltingPoint || tNewContent)) {
                mContent.remove(i--);
                OM.stack(tMaterial.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units_(tMaterial.mAmount, U, tMaterial.mMaterial.mTargetSolidifying.mAmount, F)).addToList(tToBeAdded);
            }
        }
        for (int i = 0; i < tToBeAdded.size(); i++) {
            OreDictMaterialStack tMaterial = tToBeAdded.get(i);
            if (tMaterial == null || tMaterial.mMaterial == MT.NULL || tMaterial.mMaterial == MT.Air || tMaterial.mAmount <= 0) {
                GarbageGT.trash(tToBeAdded.remove(i--));
            } else {
                tMaterial.addToList(mContent);
            }
        }

        double tWeight = mMaterial.getWeight(U*100);
        long tTotal = 0;
        OreDictMaterialStack tLightest = null;

        for (OreDictMaterialStack tMaterial : mContent) {
            if (tLightest == null || tMaterial.mMaterial.mGramPerCubicCentimeter < tLightest.mMaterial.mGramPerCubicCentimeter) tLightest = tMaterial;
            tWeight += tMaterial.weight();
            tTotal += tMaterial.mAmount;
        }

        oTemperature = mTemperature;

        short tDisplayedFluid = mDisplayedFluid, tDisplayedHeight = mDisplayedHeight;
        mDisplayedHeight = (byte)UT.Code.scale(tTotal, MAX_AMOUNT, 255, F);
        mDisplayedFluid = (tLightest == null || tLightest.mMaterial.mMeltingPoint > mTemperature ? -1 : tLightest.mMaterial.mID);
        if (mDisplayedFluid != tDisplayedFluid || mDisplayedHeight != tDisplayedHeight) updateClientData();

        long tRequiredEnergy = 1 + (long)(tWeight / KG_PER_ENERGY), tConversions = mEnergy / tRequiredEnergy;

        if (mCooldown > 0) mCooldown--;

        if (tConversions != 0) {
            mEnergy -= tConversions * tRequiredEnergy;
            mTemperature += tConversions;
            mCooldown = 100;
        }

        if (mCooldown <= 0) {mCooldown = 10; if (mTemperature > tTemperature) mTemperature--; if (mTemperature < tTemperature) mTemperature++;}

        mTemperature = Math.max(mTemperature, Math.min(200, tTemperature));

        if (mTemperature > getTemperatureMax(SIDE_INSIDE)) {
            UT.Sounds.send(SFX.MC_FIZZ, this);
            GarbageGT.trash(mContent);
            if (mTemperature >=  320) try {for (EntityLivingBase tLiving : (List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box(-GAS_RANGE, -1, -GAS_RANGE, GAS_RANGE+1, GAS_RANGE+1, GAS_RANGE+1))) UT.Entities.applyTemperatureDamage(tLiving, mTemperature, 4);} catch(Throwable e) {e.printStackTrace(ERR);}
            for (int j = 0, k = UT.Code.bindInt(mTemperature / 25); j < k; j++) WD.fire(worldObj, xCoord-FLAME_RANGE+rng(2*FLAME_RANGE+1), yCoord-1+rng(2+FLAME_RANGE), zCoord-FLAME_RANGE+rng(2*FLAME_RANGE+1), rng(3) != 0);
            for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++) {
                worldObj.setBlock(xCoord+i, yCoord  , zCoord+j, Blocks.flowing_lava, 1, 3);
                worldObj.setBlock(xCoord+i, yCoord+1, zCoord+j, Blocks.flowing_lava, 1, 3);
                worldObj.setBlock(xCoord+i, yCoord+2, zCoord+j, Blocks.flowing_lava, 1, 3);
            }
            return;
        }

        if (mMeltDown != (mTemperature+100 > getTemperatureMax(SIDE_ANY))) {
            mMeltDown = !mMeltDown;
            updateClientData();
        }
    }

    public boolean addMaterialStacks(List<OreDictMaterialStack> aList, long aTemperature) {
        if (checkStructure(F) && OM.total(mContent)+OM.total(aList) <= MAX_AMOUNT) {
            double tWeight1 = OM.weight(mContent)+mMaterial.getWeight(U*100), tWeight2 = OM.weight(aList);
            if (tWeight1+tWeight2 > 0) mTemperature = aTemperature + (mTemperature>aTemperature?+1:-1)*UT.Code.units(Math.abs(mTemperature - aTemperature), (long)(tWeight1+tWeight2), (long)tWeight1, F);
            for (OreDictMaterialStack tMaterial : aList) {
                if (mTemperature >= tMaterial.mMaterial.mMeltingPoint) {
                    if (aTemperature <  tMaterial.mMaterial.mMeltingPoint) {
                        OM.stack(tMaterial.mMaterial.mTargetSmelting.mMaterial, UT.Code.units_(tMaterial.mAmount, U, tMaterial.mMaterial.mTargetSmelting.mAmount, F)).addToList(mContent);
                    } else {
                        tMaterial.addToList(mContent);
                    }
                } else {
                    if (aTemperature >= tMaterial.mMaterial.mMeltingPoint) {
                        OM.stack(tMaterial.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units_(tMaterial.mAmount, U, tMaterial.mMaterial.mTargetSolidifying.mAmount, F)).addToList(mContent);
                    } else {
                        tMaterial.addToList(mContent);
                    }
                }
            }
            return T;
        }
        return F;
    }

    @Override
    public long getTemperatureValue(byte aSide) {
        return mTemperature;
    }

    @Override
    public long getTemperatureMax(byte aSide) {
        return (mMaterial.mMeltingPoint);
    }

    @Override
    public boolean isMoldInputSide(byte aSide) {
        return SIDES_TOP[aSide] && checkStructure(F);
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
    public long fillMold(OreDictMaterialStack aMaterial, long aTemperature, byte aSide) {
        if (isMoldInputSide(aSide)) {
            if (addMaterialStacks(Collections.singletonList(aMaterial), aTemperature)) return aMaterial.mAmount;
            if (aMaterial.mAmount > U && addMaterialStacks(Collections.singletonList(OM.stack(aMaterial.mMaterial, U)), aTemperature)) return U;
        }
        return 0;
    }

    @Override
    public double getWeightValue(byte aSide) {return OM.weight(mContent);}

    @Override
    public boolean breakBlock() {
        GarbageGT.trash(mContent);
        return super.breakBlock();
    }

    @Override
    public boolean fillMoldAtSide(ITileEntityMold aMold, byte aSide, byte aSideOfMold) {
        if (checkStructure(false)) {
            byte relative_side = FACING_ROTATIONS[mFacing][aSide];

            for (OreDictMaterialStack tContent : mContent) if (tContent != null &&
                    mTemperature >= tContent.mMaterial.mMeltingPoint &&
                    mTemperature < tContent.mMaterial.mBoilingPoint &&
                    tContent.mMaterial.mTargetSmelting.mMaterial == tContent.mMaterial) {
                boolean pour = false;
                if ((tContent.mMaterial.mID == MTx.Slag.mID || tContent.mMaterial.mID == MTx.FerrousSlag.mID) && relative_side == SIDE_LEFT)
                    pour = true;
                else if (relative_side == SIDE_RIGHT)
                    pour = true;

                if (pour) {
                    long tAmount = aMold.fillMold(tContent, mTemperature, aSideOfMold);
                    if (tAmount > 0) {
                        tContent.mAmount -= tAmount;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onPlaced(ItemStack aStack, EntityPlayer aPlayer, MultiTileEntityContainer aMTEContainer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {
        mTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
        return T;
    }
}
