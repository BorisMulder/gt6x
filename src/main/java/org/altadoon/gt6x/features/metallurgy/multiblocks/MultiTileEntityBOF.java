package org.altadoon.gt6x.features.metallurgy.multiblocks;

import gregapi.GT_API;
import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityServerTickPost;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.tileentity.data.ITileEntityWeight;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.tileentity.machines.ITileEntityRunningActively;
import gregapi.tileentity.multiblocks.*;
import gregapi.util.OM;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.features.crucibles.CrucibleInterior;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static gregapi.data.CS.SIDE_Z_NEG;

public class MultiTileEntityBOF extends TileEntityBase10MultiBlockBase implements ITileEntityCrucible, ITileEntityEnergy, ITileEntityGibbl, ITileEntityWeight, ITileEntityTemperature, ITileEntityMold, ITileEntityServerTickPost, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockInventory, IMultiBlockFluidHandler, IFluidHandler, ITileEntityRunningActively {
    public IIconContainer[]
            texturesMaterial = ZL_IICONCONTAINER,
            texturesInactive = ZL_IICONCONTAINER,
            texturesActive = ZL_IICONCONTAINER;

    protected boolean isActive = false;
    protected static final int COOLDOWN_MAX = 30;
    protected int cooldown = COOLDOWN_MAX;
    protected static final int MASS_UNITS = 1;

    public FluidTankGT oxygenTank = new FluidTankGT();
    public FluidTankGT miscTank = new FluidTankGT();

    protected CrucibleInterior interior = new CrucibleInterior(64, 16*27, MASS_UNITS,
            200, 3, 9*U1000, 5, 5, RMx.Bessemer, RMx.BOF, RMx.Thermite);

    @Override public String getTileEntityName() { return "gt6x.multitileentity.multiblock.bof"; }
    @Override public short getMultiTileEntityRegistryID() { return (short) MTEx.gt6xMTERegId; }

    @Override
    public void readFromNBT2(NBTTagCompound nbt) {
        super.readFromNBT2(nbt);
        oxygenTank.readFromNBT(nbt, NBT_TANK+"."+0);
        oxygenTank.setCapacity(16000);
        miscTank.readFromNBT(nbt, NBT_TANK+"."+1);
        miscTank.setCapacity(1000);
        nbt.setString(NBT_MATERIAL, MTx.MgOC.toString());
        interior.readFromNBT(nbt, oxygenTank);
        if (nbt.hasKey(NBT_ACTIVE)) isActive = nbt.getBoolean(NBT_ACTIVE);

        if (CODE_CLIENT) {
            if (GT_API.sBlockIcons == null) {
                texturesMaterial = new IIconContainer[] {
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/bof/colored/front"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/bof/colored/side")};
                texturesInactive = new IIconContainer[] {
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/bof/overlay/front"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/bof/overlay/side")};
                texturesActive = new IIconContainer[] {
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/bof/overlay_active/front"),
                        new Textures.BlockIcons.CustomIcon("machines/multiblockmains/bof/overlay_active/side")};
            } else {
                TileEntity canonicalTileEntity = MultiTileEntityRegistry.getCanonicalTileEntity(getMultiTileEntityRegistryID(), getMultiTileEntityID());
                if (canonicalTileEntity instanceof MultiTileEntityBOF) {
                    texturesMaterial = ((MultiTileEntityBOF)canonicalTileEntity).texturesMaterial;
                    texturesInactive = ((MultiTileEntityBOF)canonicalTileEntity).texturesInactive;
                    texturesActive = ((MultiTileEntityBOF)canonicalTileEntity).texturesActive;
                }
            }
        }
    }

    @Override
    public void writeToNBT2(NBTTagCompound nbt) {
        super.writeToNBT2(nbt);
        oxygenTank.writeToNBT(nbt, NBT_TANK+"."+0);
        miscTank.writeToNBT(nbt, NBT_TANK+"."+1);
        nbt.setString(NBT_MATERIAL, MTx.MgOC.toString());
        interior.writeToNBT(nbt);
        nbt.setString(NBT_MATERIAL, mMaterial.toString());
        UT.NBT.setBoolean(nbt, NBT_ACTIVE, isActive);
    }

    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);

        int xCenter = getOffsetXN(mFacing), yBottom = yCoord, zCenter = getOffsetZN(mFacing);
        if (worldObj.blockExists(xCenter-1, yBottom, zCenter-1) &&
            worldObj.blockExists(xCenter+1, yBottom, zCenter-1) &&
            worldObj.blockExists(xCenter-1, yBottom, zCenter+1) &&
            worldObj.blockExists(xCenter+1, yBottom, zCenter+1)
        ) {
            boolean tSuccess = true;
            for (int i = -1; i <= 1; i++) for (int j = 0; j < 3; j++) for (int k = -1; k <= 1; k++) {
                if (i == 0 && j == 1 && k == 0) {
                    if (getAir(xCenter+i, yBottom+j, zCenter+k)) worldObj.setBlockToAir(xCenter+i, yBottom+j, zCenter+k); else tSuccess = false;
                } else {
                    int side_io = MultiTileEntityMultiBlockPart.NOTHING;
                    int design = 0;
                    int partMeta = MTEx.IDs.BOFWall.get();
                    switch (j) {
                        case 0:
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;

                            if ((i ==  0 && k ==  1 && (mFacing == SIDE_X_POS)) ||
                                (i ==  0 && k == -1 && (mFacing == SIDE_X_NEG)) ||
                                (i ==  1 && k ==  0 && (mFacing == SIDE_Z_POS)) ||
                                (i == -1 && k ==  0 && (mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io &= (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE);
                            }
                            break;
                        case 1:
                            if ((i ==  0 && k == -1 && (mFacing == SIDE_X_POS)) ||
                                (i ==  0 && k ==  1 && (mFacing == SIDE_X_NEG)) ||
                                (i == -1 && k ==  0 && (mFacing == SIDE_Z_POS)) ||
                                (i ==  1 && k ==  0 && (mFacing == SIDE_Z_NEG))
                            ) {
                                design = 1;
                                side_io = (MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE);
                            }
                            break;
                        case 2:
                            if (i == 0 && k == 0) {
                                design = 2;
                                partMeta = MTEx.IDs.BOFLance.get();
                                side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            } else if (xCenter+i == xCoord && zCenter+k == zCoord) {
                                design = 2;
                                side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT;
                            } else {
                                side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            }
                            break;
                    }

                    if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter+i, yBottom+j, zCenter+k, partMeta, mteRegID, design, side_io))
                        tSuccess = false;
                }
            }
            return tSuccess;
        }

        return mStructureOkay;
    }

    @Override
    public boolean isInsideStructure(int aX, int aY, int aZ) {
        int tX = getOffsetXN(mFacing), tY = getOffsetYN(mFacing), tZ = getOffsetZN(mFacing);
        return aX >= tX - 1 && aY >= tY && aZ >= tZ - 1 && aX <= tX + 1 && aY <= tY + 2 && aZ <= tZ + 1;
    }

    static {
        LH.add("gt6x.tooltip.multiblock.bof.1", "3x3x3 hollow of 24 Steel-lined MgO-C Walls (excl. main and top center) with air inside;");
        LH.add("gt6x.tooltip.multiblock.bof.2", "Main centered at bottom-side facing outwards. Oxygen lance centered at the top layer");
        LH.add("gt6x.tooltip.multiblock.bof.3", "Molten metal (or most dense liquid) out at right hole in bottom layer, crucible molds, crossings, etc. usable");
        LH.add("gt6x.tooltip.multiblock.bof.4", "Same for slag (or least dense liquid) but at left hole.");
        LH.add("gt6x.tooltip.multiblock.bof.5", "Air or Oxygen in at the Lance. Gases out at the hole above the controller.");
        LH.add("gt6x.tooltip.multiblock.bof.6", "Items/Other fluids in at the other blocks on top.");
        LH.add("gt6x.tooltip.multiblock.bof.7", "Use activity detector to check if the process is still ongoing.");
    }

    @Override
    public void addToolTips(List<String> list, ItemStack stack, boolean f3_h) {
        list.add(LH.Chat.CYAN  + LH.get(LH.STRUCTURE) + ":");
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.1"));
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.2"));
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.3"));
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.4"));
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.5"));
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.6"));
        list.add(LH.Chat.WHITE + LH.get("gt6x.tooltip.multiblock.bof.7"));
        list.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X) + " 1 " + TD.Energy.HU.getLocalisedNameShort() + " " + LH.get(LH.CONVERTS_TO_Y) + " +1 K " + LH.get(LH.CONVERTS_PER_Z) + " "+ interior.kgPerEnergy + "kg (at least "+getEnergySizeInputMin(TD.Energy.HU, SIDE_ANY)+" Units per Tick required!)");
        list.add(LH.Chat.YELLOW   + LH.get(LH.TOOLTIP_THERMALMASS) + MTx.MgOC.getWeight(U*MASS_UNITS) + " kg");
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + getTemperatureMax(SIDE_ANY) + " K)");
        if (interior.acidProof) list.add(LH.Chat.ORANGE + LH.get(LH.TOOLTIP_ACIDPROOF));
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_FIRE) + " ("+(interior.flameRange+1)+"m)");
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_CONTACT));
        list.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_MEASURE_THERMOMETER));
        list.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_REMOVE_SHOVEL));
        super.addToolTips(list, stack, f3_h);
    }

    private boolean hasToAddOnServerTickPost = true;

    @Override public void onUnregisterPost() {
        hasToAddOnServerTickPost = true;
    }

    @Override
    public void onCoordinateChange() {
        super.onCoordinateChange();
        GT_API_Proxy.SERVER_TICK_POST.remove(this);
        onUnregisterPost();
    }

    @Override
    public void onTick2(long timer, boolean isServerSide) {
        if (isServerSide && hasToAddOnServerTickPost) {
            GT_API_Proxy.SERVER_TICK_POST.add(this);
            hasToAddOnServerTickPost = false;
        }
    }

    @Override
    public void onServerTickPost(boolean aFirst) {
        if (!checkStructure(false) && (mInventoryChanged || SERVER_TIME % 1200 == 5)) {
            if (checkStructure(true)) return;
        }

        interior.addFluidFromTank(miscTank);

        CrucibleInterior.CrucibleTickResult result = interior.onTick(this);

        if (result.exothermic) {
            isActive = true;
            cooldown = COOLDOWN_MAX;
        } else if (--cooldown == 0) {
            isActive = false;
        }

        // Melt down into lava
        if (result.meltdown) {
            for (int i = -1; i < 2; i++) for (int j = 0; j < 3; j++) for (int k = -1; k < 2; k++) {
                worldObj.setBlock(xCoord+i, yCoord+j, zCoord+k, Blocks.flowing_lava, 1, 3);
            }
        }

        if (emitGases() || result.updateClientData) updateClientData();
    }

    protected boolean emitGases() {
        boolean result = false;

        CrucibleInterior.CrucibleContentStack stack;
        int idx = 0;
        do {
            stack = interior.getStack(idx++);
            if (stack != null && stack.state() == CrucibleInterior.MaterialState.GAS_OR_PLASMA &&
                stack.toOMStack().mMaterial != MT.O && stack.toOMStack().mMaterial != MT.Air
            ) {
                FluidStack gas = stack.toFluidStack();
                if (gas == null || gas.amount <= 0) continue;

                DelegatorTileEntity<TileEntity> target = WD.te(worldObj, xCoord, yCoord+3, zCoord, SIDE_BOTTOM, false);
                if (target != null && target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), gas.getFluid())) {
                    long transferred = FL.fill(target, gas, true);
                    if (transferred > 0) {
                        stack.decreaseFluidAmount(transferred);
                        result = true;
                    }
                }
            }
        } while (stack != null);

        return result;
    }

    // ITileEntityTemperature
    @Override public long getTemperatureValue(byte side) { return interior.currentTemperature; }
    @Override public long getTemperatureMax(byte side) { return interior.maxTemperature; }

    // ITileEntityMold
    @Override public boolean isMoldInputSide(byte side) { return SIDES_TOP[side] && checkStructure(false); }
    @Override public long getMoldMaxTemperature() { return getTemperatureMax(SIDE_INSIDE); }
    @Override public long getMoldRequiredMaterialUnits() { return 1; }
    @Override public long fillMold(OreDictMaterialStack stack, long temperature, byte side) { return isMoldInputSide(side) ? interior.tryAddStack(stack, temperature) : 0; }

    protected byte getRelativeSide(byte side) {
        return FACING_ROTATIONS[mFacing][side];
    }

    // ITileEntityCrucible
    @Override public boolean fillMoldAtSide(ITileEntityMold mold, byte sideOfMachine, byte sideOfMold) {
        if (checkStructure(false)) {
            boolean countFromBottom = false;
            switch (getRelativeSide(sideOfMachine)) {
                case SIDE_RIGHT: // pour the bottom-most material
                    countFromBottom = true; // fallthrough
                case SIDE_LEFT: // pour the top-most material
                    CrucibleInterior.CrucibleContentStack stack = interior.getStack(countFromBottom, true, CrucibleInterior.MaterialState.LIQUID);
                    if (stack != null) {
                        long amount = mold.fillMold(stack.toOMStack(), interior.currentTemperature, sideOfMold);
                        stack.decreaseUnits(amount);
                        return true;
                    }
            }
        }
        return false;
    }

    @Override public double getWeightValue(byte side) { return interior.weight(); }

    @Override
    public long onToolClick2(String tool, long remainingDurability, long quality, Entity player, List<String> chatReturn, IInventory playerInventory, boolean isSneaking, ItemStack stack, byte side, float hitX, float hitY, float hitZ) {
        if (isClientSide()) return super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, hitZ);

        if (tool.equals(TOOL_thermometer)) {if (chatReturn != null) chatReturn.add("Temperature: " + interior.currentTemperature + (interior.currentTemperature >= 1300 ? "K (too hot to pick it up right now!)" : "K")); return 10000;}
        if (tool.equals(TOOL_shovel) && checkStructure(false) && player instanceof EntityPlayer) {
            CrucibleInterior.CrucibleContentStack content = switch (getRelativeSide(side)) {
                case SIDE_RIGHT -> interior.getStack(true , true, CrucibleInterior.MaterialState.SOLID); // shovel the bottom-most material
                case SIDE_LEFT  -> interior.getStack(false, true, CrucibleInterior.MaterialState.SOLID); // shovel the top-most material
                default -> null;
            };
            if (content != null) return content.convertToScrap((EntityPlayer)player, true);
        }

        if (tool.equals(TOOL_magnifyingglass)) {
            interior.printContent(chatReturn);
        }

        return super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
        super.onPlaced(stack, player, container, world, x, y, z, side, hitX, hitY, hitZ);
        interior.currentTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] aShouldSideBeRendered) {
        return aShouldSideBeRendered[side] ?
            BlockTextureMulti.get(
                BlockTextureDefault.get(texturesMaterial[side == mFacing ? 0 : 1], mRGBa),
                BlockTextureDefault.get((isActive ? texturesActive : texturesInactive)[side == mFacing ? 0 : 1])
            ) : null;
    }

    @Override public byte getVisualData() { return (byte)(isActive?1:0); }
    @Override public void setVisualData(byte data) { isActive=((data&1)!=0); }

    protected boolean isRightFluidInput(MultiTileEntityMultiBlockPart part, Fluid fluid) {
        int xLance = getOffsetXN(mFacing), yLance = yCoord + 2, zLance = getOffsetZN(mFacing);
        if (part.yCoord != yLance) return false;

        if (FL.Oxygen.is(fluid) || FluidsGT.AIR.contains(fluid.getName())) {
            return part.xCoord == xLance && part.zCoord == zLance;
        } else {
            return !(part.xCoord == xCoord && part.yCoord == yCoord) && !(part.xCoord == xLance && part.zCoord == zLance);
        }
    }

    @Override
    public boolean canFill(MultiTileEntityMultiBlockPart part, byte side, Fluid fluid) {
        return isRightFluidInput(part, fluid) && super.canFill(part, side, fluid);
    }

    @Override
    public int fill(MultiTileEntityMultiBlockPart part, byte side, FluidStack stack, boolean doFill) {
        if (isRightFluidInput(part, stack.getFluid())) {
            return super.fill(part, side, stack, doFill);
        } else {
            return 0;
        }
    }

    @Override protected IFluidTank[] getFluidTanks2(byte side) {return new IFluidTank[]{oxygenTank, miscTank};}
    @Override protected IFluidTank getFluidTankFillable2(byte side, FluidStack fluid) {
        if (FL.Oxygen.is(fluid) || FL.Air.is(fluid)) {
            return oxygenTank;
        } else {
            return miscTank;
        }
    }

    // ITileEntityGibbl
    @Override public long getGibblValue(byte side) {return UT.Code.divup(OM.total(interior.content)*1000, U9);}
    @Override public long getGibblMax  (byte side) {return UT.Code.divup(interior.maxTotalUnits*1000, U9);}

    // Parent
    @Override public byte getDefaultSide() { return SIDE_FRONT; }
    @Override public boolean[] getValidSides() {return isActive ? SIDES_THIS[mFacing] : SIDES_HORIZONTAL;}

    @Override public ItemStack[] getDefaultInventory(NBTTagCompound nbt) {return new ItemStack[1];}
    @Override public int[] getAccessibleSlotsFromSide2(byte side) {return UT.Code.getAscendingArray(1);}
    @Override public boolean canInsertItem2(int slot, ItemStack stack, byte side) {return SIDES_TOP[side] && !slotHas(0);}
    @Override public boolean canExtractItem2(int slot, ItemStack stack, byte side) {return false;}

    public static final List<TagData> ENERGY_TYPES = new ArrayListNoNulls<>(false, TD.Energy.HU, TD.Energy.CU);

    // ITileEntityEnergy
    @Override public boolean isEnergyType(TagData energyType, byte side, boolean aEmitting) {return !aEmitting && ENERGY_TYPES.contains(energyType);}
    @Override public boolean isEnergyCapacitorType(TagData energyType, byte side) {return ENERGY_TYPES.contains(energyType);}
    @Override public boolean isEnergyAcceptingFrom(TagData energyType, byte side, boolean aTheoretical) {return ENERGY_TYPES.contains(energyType);}
    @Override public long getEnergyDemanded(TagData energyType, byte side, long aSize) {return Long.MAX_VALUE - interior.storedEnergy;}
    @Override public long getEnergySizeInputMin(TagData energyType, byte side) {return 1;}
    @Override public long getEnergySizeInputRecommended(TagData energyType, byte side) {return 32;}
    @Override public long getEnergySizeInputMax(TagData energyType, byte side) {return 1024;}
    @Override public Collection<TagData> getEnergyTypes(byte side) {return ENERGY_TYPES;}

    @Override public long doInject(TagData energyType, byte side, long size, long amount, boolean doInject) {
        isActive = true;
        cooldown = COOLDOWN_MAX;
        return interior.injectEnergy(energyType, size, amount, doInject, false);
    }

    @Override
    public boolean getStateRunningActively() {
        return isActive;
    }

    @Override
    public boolean getStateRunningPassively() {
        return isActive;
    }

    @Override
    public boolean getStateRunningPossible() {
        return true;
    }
}
