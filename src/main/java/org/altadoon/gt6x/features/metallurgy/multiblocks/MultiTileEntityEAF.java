package org.altadoon.gt6x.features.metallurgy.multiblocks;

import gregapi.GT_API;
import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.fluid.FluidTankGT;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.old.Textures;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityFunnelAccessible;
import gregapi.tileentity.ITileEntityServerTickPost;
import gregapi.tileentity.ITileEntityTapAccessible;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.tileentity.data.ITileEntityWeight;
import gregapi.tileentity.delegate.DelegatorTileEntity;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
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
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ItemMaterialDisplay;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.features.crucibles.CrucibleInterior;
import org.altadoon.gt6x.features.metallurgy.gui.ContainerClientEAF;
import org.altadoon.gt6x.features.metallurgy.gui.ContainerCommonEAF;

import java.nio.ByteBuffer;
import java.util.*;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class MultiTileEntityEAF extends TileEntityBase10MultiBlockBase implements ITileEntityCrucible, ITileEntityEnergy, ITileEntityGibbl, ITileEntityWeight, ITileEntityTemperature, ITileEntityMold, ITileEntityServerTickPost, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockInventory, IMultiBlockFluidHandler, IFluidHandler, ITileEntityTapAccessible, ITileEntityFunnelAccessible {
    public IIconContainer[]
            texturesMaterial = L6_IICONCONTAINER,
            texturesInactive = L6_IICONCONTAINER,
            texturesActive = L6_IICONCONTAINER;

    public static final String GUI_TEXTURE = RES_PATH_GUI + "machines/EAF.png";
    public static final int GUI_SLOTS = 18;

    protected boolean isActive = false;
    protected static final int COOLDOWN_MAX = 30;
    protected int cooldown = COOLDOWN_MAX;

    public FluidTankGT oxygenTank = new FluidTankGT();
    public FluidTankGT miscTank = new FluidTankGT();

    protected CrucibleInterior interior = new CrucibleInterior(GUI_SLOTS, 64*3, 100, 150, 36, 0, 5, 5, RMx.EAF, RMx.BOF, RMx.Bessemer, RMx.SSS, RMx.Thermite);

    @Override
    public String getTileEntityName() {
        return "gt6x.multitileentity.multiblock.eaf";
    }

    @Override
    public void readFromNBT2(NBTTagCompound nbt) {
        super.readFromNBT2(nbt);
        oxygenTank.readFromNBT(nbt, NBT_TANK+"."+0);
        oxygenTank.setCapacity(16000);
        interior.readFromNBT(nbt, oxygenTank);
        if (nbt.hasKey(NBT_ACTIVE)) isActive = nbt.getBoolean(NBT_ACTIVE);

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
    public void writeToNBT2(NBTTagCompound nbt) {
        super.writeToNBT2(nbt);
        oxygenTank.writeToNBT(nbt, NBT_TANK+"."+0);
        interior.writeToNBT(nbt);
        UT.NBT.setBoolean(nbt, NBT_ACTIVE, isActive);
    }

    private boolean shouldBeAir(int i, int k) { return (i == 0 && Math.abs(k) != 2) || (k == 0 && Math.abs(i) != 2); }

    @Override
    public boolean checkStructure2() {
        int mteRegID = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);
        int xCenter = getOffsetXN(mFacing, 2), yCenter = yCoord - 1, zCenter = getOffsetZN(mFacing, 2);
        if (worldObj.blockExists(xCenter-2, yCenter, zCenter) && worldObj.blockExists(xCenter+2, yCenter, zCenter) && worldObj.blockExists(xCenter, yCenter, zCenter-2) && worldObj.blockExists(xCenter, yCenter, zCenter+2)) {
            boolean success = true;
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
                        if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter+i, yCenter+j, zCenter+k, MTEx.IDs.MgOCBricks.get(), mteRegID, design, side_io)) {
                            success = false;
                        }
                        break;
                    case 1:
                        if (shouldBeAir(i, k)) {
                            if (getAir(xCenter+i, yCenter+j, zCenter+k))
                                worldObj.setBlockToAir(xCenter + i, yCenter + j, zCenter + k);
                            else {
                                success = false;
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
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter+i, yCenter+j, zCenter+k, MTEx.IDs.MgOCBricks.get(), mteRegID, design, side_io)) {
                                success = false;
                            }
                        }
                        break;
                    case 2:
                        if (i == 0 && k == 0) {
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter+i, yCenter+j, zCenter+k, MTEx.IDs.EAFElectrodes.get(), mteRegID, design, side_io)) {
                                success = false;
                            }
                        } else if (shouldBeAir(i, k)) {
                            if (getAir(xCenter+i, yCenter+j, zCenter+k))
                                worldObj.setBlockToAir(xCenter + i, yCenter + j, zCenter + k);
                            else {
                                success = false;
                            }
                        } else {
                            side_io = MultiTileEntityMultiBlockPart.ONLY_FLUID_IN;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter+i, yCenter+j, zCenter+k, MTEx.IDs.MgOCBricks.get(), mteRegID, design, side_io)) {
                                success = false;
                            }
                        }
                        break;
                    case 3:
                        if (Math.abs(i) == 2 || Math.abs(k) == 2) {
                            continue;
                        } else if (i == 0 && k == 0) {
                            design = 1;
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter + i, yCenter + j, zCenter + k, MTEx.IDs.EAFElectrodes.get(), mteRegID, design, side_io)) {
                                success = false;
                            }
                        } else {
                            side_io = MultiTileEntityMultiBlockPart.ONLY_ITEM_IN & MultiTileEntityMultiBlockPart.ONLY_FLUID_OUT;
                            if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, xCenter + i, yCenter + j, zCenter + k, MTEx.IDs.AluminaBricks.get(), mteRegID, design, side_io)) {
                                success = false;
                            }
                        }
                }
            }
            return success;
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
        LH.add("gt6x.tooltip.multiblock.eaf.6", "molten metal (or most dense liquid) out at the hole in the bottom layer to the right of the main");
        LH.add("gt6x.tooltip.multiblock.eaf.7", "slag (or least dense liquid) out at the hole in the second layer to the left of the main");
        LH.add("gt6x.tooltip.multiblock.eaf.8", "Air or oxygen for steelmaking in at the third layer");
        LH.add("gt6x.tooltip.multiblock.eaf.9", "Energy in at the electrode on the top, Items in and gases automatically out at the top layer");
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
        tooltips.add(LH.Chat.WHITE    + LH.get("gt6x.tooltip.multiblock.eaf.9"));

        tooltips.add(LH.Chat.CYAN    + LH.get(LH.RECIPES) + ": " + LH.Chat.WHITE
                + LH.get(RMx.EAF.fakeRecipes.mNameInternal) + ", "
                + LH.get(RM.CrucibleSmelting.mNameInternal) + ", "
                + LH.get(RM.CrucibleAlloying.mNameInternal) + ", "
                + LH.get(RMx.Thermite.fakeRecipes.mNameInternal) + ", "
                + LH.get(RM.Smelter.mNameInternal));

        tooltips.add(LH.getToolTipEfficiency(7500));
        tooltips.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X) + " 1 " + TD.Energy.EU.getLocalisedNameShort() + " " + LH.get(LH.CONVERTS_TO_Y) + " +1 K " + LH.get(LH.CONVERTS_PER_Z) + " " + interior.kgPerEnergy + "kg (at least " + getEnergySizeInputMin(TD.Energy.EU, SIDE_ANY) + " Units per Tick required!)");
        tooltips.add(LH.Chat.YELLOW   + LH.get(LH.TOOLTIP_THERMALMASS) + String.format("%.2f", mMaterial.getWeight(U*200)) + " kg");
        tooltips.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + getTemperatureMax(SIDE_ANY) + " K)");
        tooltips.add(LH.Chat.ORANGE   + LH.get(LH.TOOLTIP_ACIDPROOF));
        tooltips.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_REMOVE_SHOVEL));
        super.addToolTips(tooltips, stack, modeF3_H);
    }

    private boolean hasToAddTimer = true;

    @Override public void onUnregisterPost() {
        hasToAddTimer = true;
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

    @Override
    public void onServerTickPost(boolean aFirst) {
        if (!checkStructure(false) && (mInventoryChanged || SERVER_TIME % 1200 == 5)) {
            if (checkStructure(true)) return;
        }

        CrucibleInterior.CrucibleTickResult result = interior.onTick(this);

        if (--cooldown == 0) {
            isActive = false;
        }

        if (result.meltdown) {
            int xCenter = getOffsetXN(mFacing, 2);
            int zCenter = getOffsetZN(mFacing, 2);

            for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++) {
                worldObj.setBlock(xCenter+i, yCoord-1, zCenter+j, Blocks.flowing_lava, 1, 3);
                worldObj.setBlock(xCenter+i, yCoord  , zCenter+j, Blocks.flowing_lava, 1, 3);
                worldObj.setBlock(xCenter+i, yCoord+1, zCenter+j, Blocks.flowing_lava, 1, 3);
            }
        }

        if (emitGases() || result.updateClientData) {
            updateClientData();
        }
    }

    protected boolean emitGases() {
        CrucibleInterior.CrucibleContentStack stack = interior.getStack(CrucibleInterior.MaterialState.GAS_OR_PLASMA);
        if (stack == null)
            return false;
        FluidStack gas = stack.toFluidStack();

        int tX = getOffsetXN(mFacing, 2), tY = yCoord+3, tZ = getOffsetZN(mFacing, 2);
        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
            DelegatorTileEntity<TileEntity> target = WD.te(worldObj, tX+i, tY, tZ+j, SIDE_BOTTOM, false);
            if (target.mTileEntity instanceof IFluidHandler && ((IFluidHandler)target.mTileEntity).canFill(target.getForgeSideOfTileEntity(), gas.getFluid())) {
                long transferred = FL.fill(target, gas, true);
                if (transferred > 0) {
                    stack.decreaseFluidAmount(transferred);
                    emitGases();
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean breakBlock() {
        interior.trashAll();
        return super.breakBlock();
    }

    protected byte getRelativeSide(byte side) {
        return FACING_ROTATIONS[mFacing][side];
    }

    // ITileEntityCrucible
    @Override
    public boolean fillMoldAtSide(ITileEntityMold mold, byte sideOfMachine, byte sideOfMold) {
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

    // ITileEntityMold
    @Override public boolean isMoldInputSide(byte side) {
        return SIDES_TOP[side] && checkStructure(false);
    }
    @Override public long getMoldMaxTemperature() {
        return getTemperatureMax(SIDE_INSIDE);
    }
    @Override public long getMoldRequiredMaterialUnits() {
        return 1;
    }
    @Override public long fillMold(OreDictMaterialStack stack, long temperature, byte side) { return isMoldInputSide(side) ? interior.tryAddStack(stack, temperature) : 0; }

    // IMultiBlockFluidHandler
    @Override
    public boolean canFill(MultiTileEntityMultiBlockPart part, byte side, Fluid fluid) {
        return FL.Oxygen.is(fluid) || FL.Air.is(fluid);
    }

    @Override
    public int fill(MultiTileEntityMultiBlockPart part, byte side, FluidStack stack, boolean doFill) {
        if (canFill(part, side, stack.getFluid())) {
            return super.fill(part, side, stack, doFill);
        } else {
            return 0;
        }
    }

    @Override protected IFluidTank[] getFluidTanks2(byte aSide) {return new IFluidTank[]{oxygenTank};}
    @Override protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {return oxygenTank;}

    // ITileEntityTapAccessible
    @Override
    public FluidStack nozzleDrain(byte side, int maxDrain, boolean doDrain) {
        CrucibleInterior.CrucibleContentStack stack = interior.getStack(CrucibleInterior.MaterialState.GAS_OR_PLASMA);
        if (stack != null) {
            FluidStack result = stack.toFluidStack();
            result.amount = Math.min(result.amount, maxDrain);
            if (doDrain) {
                stack.decreaseFluidAmount(result.amount);
            }
            return result;
        }

        return NF;
    }

    @Override
    public FluidStack tapDrain(byte side, int maxDrain, boolean doDrain) {
        boolean countFromBottom;
        switch (getRelativeSide(side)) {
            case SIDE_RIGHT -> countFromBottom = true;
            case SIDE_LEFT -> countFromBottom = false;
            default -> { return NF; }
        }

        CrucibleInterior.CrucibleContentStack stack = interior.getStack(countFromBottom, true, CrucibleInterior.MaterialState.LIQUID);
        if (stack != null) {
            FluidStack result = stack.toFluidStack();
            result.amount = Math.min(result.amount, maxDrain);
            if (doDrain) {
                stack.decreaseFluidAmount(result.amount);
            }
            return result;
        }

        return NF;
    }

    // ITileEntityFunnelAccessible
    @Override
    public int funnelFill(byte aSide, FluidStack fluid, boolean doFill) {
        return interior.addFluid(fluid, doFill);
    }

    @Override
    public int capnozzleFill(byte aSide, FluidStack aFluid, boolean aDoFill) {
        return interior.addFluid(aFluid, aDoFill);
    }

    // Parent functions
    @Override
    public long onToolClick2(String tool, long remainingDurability, long toolQuality, Entity player, List<String> chatReturn, IInventory playerInventory, boolean isSneaking, ItemStack stack, byte side, float hitX, float hitY, float ditZ) {
        if (isClientSide()) return super.onToolClick2(tool, remainingDurability, toolQuality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, ditZ);
        if (tool.equals(TOOL_thermometer)) {if (chatReturn != null) chatReturn.add("Temperature: " + interior.currentTemperature + "K"); return 10000;}
        if (tool.equals(TOOL_shovel) && checkStructure(false) && player instanceof EntityPlayer) {
            CrucibleInterior.CrucibleContentStack content = switch (getRelativeSide(side)) {
				case SIDE_RIGHT -> interior.getStack(true , true, CrucibleInterior.MaterialState.SOLID); // shovel the bottom-most material
				case SIDE_LEFT  -> interior.getStack(false, true, CrucibleInterior.MaterialState.SOLID); // shovel the top-most material
				default -> null;
			};
            if (content != null) return content.convertToScrap((EntityPlayer)player, true);
        }

        return super.onToolClick2(tool, remainingDurability, toolQuality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, ditZ);
    }

    @Override
    public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
        super.onPlaced(stack, player, container, world, x, y, z, side, hitX, hitY, hitZ);
        interior.currentTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] aShouldSideBeRendered) {
        return aShouldSideBeRendered[side]
                ? BlockTextureMulti.get(
                        BlockTextureDefault.get(texturesMaterial[FACING_ROTATIONS[mFacing][side]], mRGBa),
                        BlockTextureDefault.get((isActive ? texturesActive : texturesInactive)[FACING_ROTATIONS[mFacing][side]])
                )
                : null;
    }

    @Override
    public boolean onBlockActivated3(EntityPlayer player, byte side, float aHitX, float aHitY, float aHitZ) {
        if (isServerSide()) openGUI(player, side);
        return true;
    }

    @Override public byte getVisualData() { return (byte)(isActive?1:0); }
    @Override public void setVisualData(byte data) { isActive=((data&1)!=0); }

    @Override
    public IPacket getClientDataPacket(boolean aSendAll) {
        ByteBuffer buf = ByteBuffer.allocate(5 + Long.BYTES);
        buf.put(new byte[]{(byte)UT.Code.getR(mRGBa), (byte)UT.Code.getG(mRGBa), (byte)UT.Code.getB(mRGBa), getVisualData(), getDirectionData()});
        buf.putLong(interior.currentTemperature);

        return getClientDataPacketByteArray(true, buf.array());
    }

    @Override
    public boolean receiveDataByteArray(byte[] data, INetworkHandler networkHandler) {
        ByteBuffer buf = ByteBuffer.wrap(data);

        try {
            for (int i = 0; i < 5; i++) buf.get(); // skip the first 5 bytes, they are only used by super

            interior.currentTemperature = buf.getLong();
        } catch (Exception e) {
            LOG.error("failed to parse byte array", e);
        }
        return super.receiveDataByteArray(data, networkHandler);
    }

    @Override public byte getDefaultSide() { return SIDE_FRONT; }
    @Override public boolean[] getValidSides() {return isActive ? SIDES_THIS[mFacing] : SIDES_HORIZONTAL;}

    @Override public boolean allowCovers(byte side) {return false;}

    @Override public ItemStack[] getDefaultInventory(NBTTagCompound nbt) {return new ItemStack[1];}
    @Override public int[] getAccessibleSlotsFromSide2(byte side) {return UT.Code.getAscendingArray(1);}
    @Override public boolean canInsertItem2(int slot, ItemStack stack, byte side) {return !slotHas(0);}
    @Override public boolean canExtractItem2(int slot, ItemStack stack, byte side) {return false;}
    @Override public int getInventoryStackLimit() {return (int)interior.maxTotalUnits;}

    @Override public Object getGUIClient2(int GUIID, EntityPlayer player) {return new ContainerClientEAF(player.inventory, this, GUIID, GUI_TEXTURE);}
    @Override public Object getGUIServer2(int GUIID, EntityPlayer player) {return new ContainerCommonEAF(player.inventory, this, GUIID);}

    @Override public int getSizeInventoryGUI() {return GUI_SLOTS;}

    @Override public ItemStack decrStackSizeGUI(int slot, int decrement) {return null;}
    @Override public ItemStack getStackInSlotOnClosingGUI(int slot) {return null;}
    @Override public int getInventoryStackLimitGUI(int slot) {return getInventoryStackLimit();}

    private final ItemStack[] clientGuiSlotContent = new ItemStack[GUI_SLOTS];

    @Override public ItemStack getStackInSlotGUI(int slot) {
        if (isServerSide()) {
            if (slot >= interior.countStacks())
                return null;

            OreDictMaterialStack stack = interior.getStack(slot).toOMStack();

            if (interior.currentTemperature >= stack.mMaterial.mMeltingPoint) {
                FluidStack fluid = stack.mMaterial.fluid(interior.currentTemperature, stack.mAmount, false);
                if (!FL.Error.is(fluid)) {
                    return FL.display(fluid, true, false, true);
                }
            }
            return ItemMaterialDisplay.display(stack, interior.currentTemperature);
        } else {
            return clientGuiSlotContent[slot];
        }
    }

    @Override public void setInventorySlotContentsGUI(int slot, ItemStack stack) {
        if (isClientSide()) {
            clientGuiSlotContent[slot] = stack;
            mInventoryChanged = true;
        }
    }

    public static final List<TagData> ENERGY_TYPES = new ArrayListNoNulls<>(false, TD.Energy.EU);

    @Override public long doInject(TagData energyType, byte side, long size, long amount, boolean doInject) {
        isActive = true;
        cooldown = COOLDOWN_MAX;
        return interior.injectEnergy(energyType, size, amount, doInject, false);
    }

    // ITileEntityEnergy
    @Override public boolean isEnergyType(TagData energyType, byte side, boolean emitting) {return !emitting && ENERGY_TYPES.contains(energyType);}
    @Override public boolean isEnergyCapacitorType(TagData energyType, byte side) {return ENERGY_TYPES.contains(energyType);}
    @Override public boolean isEnergyAcceptingFrom(TagData energyType, byte side, boolean aTheoretical) {return ENERGY_TYPES.contains(energyType);}
    @Override public long getEnergyDemanded(TagData energyType, byte side, long aSize) {return Long.MAX_VALUE - interior.storedEnergy;}
    @Override public long getEnergySizeInputMin(TagData energyType, byte side) {return 512;}
    @Override public long getEnergySizeInputRecommended(TagData energyType, byte side) {return 512;}
    @Override public long getEnergySizeInputMax(TagData energyType, byte side) {return Long.MAX_VALUE;}
    @Override public Collection<TagData> getEnergyTypes(byte side) {return ENERGY_TYPES;}

    // ITileEntityTemperature
    @Override
    public long getTemperatureValue(byte side) {
        return interior.currentTemperature;
    }
    @Override
    public long getTemperatureMax(byte side) {
        return mMaterial.mMeltingPoint;
    }

    // ITileEntityWeight
    @Override
    public double getWeightValue(byte side) {return interior.weight();}

    // ITileEntityGibbl
    @Override public long getGibblValue(byte side) {return UT.Code.divup(OM.total(interior.content)*1000, U9);}
    @Override public long getGibblMax  (byte side) {return UT.Code.divup(interior.maxTotalUnits*1000, U9);}
}
