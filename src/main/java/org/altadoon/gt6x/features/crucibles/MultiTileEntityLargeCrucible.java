package org.altadoon.gt6x.features.crucibles;

import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
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
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.RMx;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.MTEx.NBT_MTE_MULTIBLOCK_PART_REG;

public class MultiTileEntityLargeCrucible extends TileEntityBase10MultiBlockBase implements ITileEntityCrucible, ITileEntityEnergy, ITileEntityGibbl, ITileEntityWeight, ITileEntityTemperature, ITileEntityMold, ITileEntityServerTickPost, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockInventory, IMultiBlockFluidHandler, IFluidHandler {
    protected boolean almostMeltDown = false;
    protected byte displayedHeight = 0;
    protected short displayedFluid = -1;

    protected CrucibleInterior interior = new CrucibleInterior(64, 16*27, 4*26,
            200, 36, 9*U1000, 5, 5, RMx.Thermite, RMx.Bessemer, RMx.SSS);

    public short wallId = 18002;
    protected int partRegId = Block.getIdFromBlock(MTEx.gt6MTEReg.mBlock);

    @Override public String getTileEntityName() {return "gt6x.multitileentity.multiblock.crucible";}
    @Override public short getMultiTileEntityRegistryID() { return (short) MTEx.gt6xMTERegId; }

    @Override
    public void readFromNBT2(NBTTagCompound nbt) {
        super.readFromNBT2(nbt);
        interior.readFromNBT(nbt);
        if (nbt.hasKey(NBT_DESIGN)) wallId = nbt.getShort(NBT_DESIGN);
        if (nbt.hasKey(NBT_MTE_MULTIBLOCK_PART_REG)) partRegId = nbt.getInteger(NBT_MTE_MULTIBLOCK_PART_REG);
        almostMeltDown = (interior.currentTemperature > interior.maxTemperature);
    }

    @Override
    public void writeToNBT2(NBTTagCompound nbt) {
        super.writeToNBT2(nbt);
        interior.writeToNBT(nbt);
    }

    static {
        LH.add("gt.tooltip.crucible.1", "KU Input will turn into Air for Bessemer Steelmaking");
    }

    @Override
    public boolean checkStructure2() {
        boolean success = true;

        if (getAir(xCoord, yCoord+1, zCoord)) worldObj.setBlockToAir(xCoord, yCoord+1, zCoord); else success = false;
        if (getAir(xCoord, yCoord+2, zCoord)) worldObj.setBlockToAir(xCoord, yCoord+2, zCoord); else success = false;

        for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++) if (i != 0 || j != 0) {
            if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 0, j, wallId, partRegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) success = false;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 1, j, wallId, partRegId, 0, MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE)) success = false;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 2, j, wallId, partRegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
        }

        if (success) for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++) if (i != 0 || j != 0) {
            if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 0, j, wallId, partRegId, 4, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN)) success = false;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 1, j, wallId, partRegId, 4, MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE)) success = false;
            if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 2, j, wallId, partRegId, 4, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID)) success = false;
        }

        return success;
    }

    @Override
    public boolean isInsideStructure(int x, int y, int z) {
        return x >= xCoord - 1 && y >= yCoord && z >= zCoord - 1 && x <= xCoord + 1 && y <= yCoord + 2 && z <= zCoord + 1;
    }

    @Override
    public boolean breakBlock() {
        for (int i = -1; i < 2; i++) for (int j = -1; j < 2; j++) if (i != 0 || j != 0) {
            ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 0, j, wallId, partRegId, 0, MultiTileEntityMultiBlockPart.ONLY_ENERGY_IN);
            ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 1, j, wallId, partRegId, 0, MultiTileEntityMultiBlockPart.ONLY_CRUCIBLE);
            ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, i, 2, j, wallId, partRegId, 0, MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID);
        }
        return super.breakBlock();
    }

    static {
        LH.add("gt.tooltip.multiblock.crucible.1", "3x3x3 Hollow cube of walls with opening on Top.");
        LH.add("gt.tooltip.multiblock.crucible.2", "Main at bottom-center.");
        LH.add("gt.tooltip.multiblock.crucible.3", "Energy IN from bottom layer, stuff IN from top layer.");
        LH.add("gt.tooltip.multiblock.crucible.4", "Molds usable at second layer of walls");
        LH.add("gt.tooltip.multiblock.crucible.5", "KU at bottom layer will turn into air for Bessemer steelmaking");
    }

    @Override
    public void addToolTips(List<String> list, ItemStack stack, boolean f3_h) {
        list.add(LH.Chat.CYAN     + LH.get(LH.STRUCTURE) + ":");
        list.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.1"));
        list.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.2"));
        list.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.3"));
        list.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.4"));
        list.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X) + " 1 " + TD.Energy.HU.getLocalisedNameShort() + " " + LH.get(LH.CONVERTS_TO_Y) + " +1 K " + LH.get(LH.CONVERTS_PER_Z) + " "+ interior.kgPerEnergy + "kg (at least "+getEnergySizeInputMin(TD.Energy.HU, SIDE_ANY)+" Units per Tick required!)");
        list.add(LH.Chat.YELLOW   + LH.get(LH.TOOLTIP_THERMALMASS) + mMaterial.getWeight(U*100) + " kg");
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + getTemperatureMax(SIDE_ANY) + " K)");
        list.add(LH.Chat.WHITE    + LH.get("gt.tooltip.multiblock.crucible.5"));
        if (interior.acidProof) list.add(LH.Chat.ORANGE + LH.get(LH.TOOLTIP_ACIDPROOF));
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_FIRE) + " ("+(interior.flameRange+1)+"m)");
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_CONTACT));
        list.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_MEASURE_THERMOMETER));
        list.add(LH.Chat.DGRAY    + LH.get(LH.TOOL_TO_REMOVE_SHOVEL));
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

        CrucibleInterior.CrucibleTickResult result = interior.onTick(this);

        // Update content height
        short oldDisplayedHeight = displayedHeight, oldDisplayedFluid = displayedFluid;
        displayedHeight = (byte) UT.Code.scale(result.totalUnits, interior.maxTotalUnits * U, 255, false);
        CrucibleInterior.CrucibleContentStack topStack = interior.getStack(CrucibleInterior.MaterialState.LIQUID);
        if (topStack != null) {
            displayedFluid = topStack.stack.mMaterial.mID;
        } else if (result.totalUnits > 0) {
            displayedFluid = -1;
        }
        if (oldDisplayedHeight != displayedHeight || oldDisplayedFluid != displayedFluid)
            result.updateClientData = true;

        // Change color if almost melting down
        if (almostMeltDown != (interior.currentTemperature - 200 > getTemperatureMax(SIDE_INSIDE))) {
            almostMeltDown = !almostMeltDown;
            result.updateClientData = true;
        }

        // Melt down into lava
        if (result.meltdown) {
            for (int i = -1; i < 2; i++) for (int j = 0; j < 3; j++) for (int k = -1; k < 2; k++) {
                worldObj.setBlock(xCoord+i, yCoord+j, zCoord+k, Blocks.flowing_lava, 1, 3);
            }
        }

        if (result.updateClientData) updateClientData();
    }

    // ITileEntityTemperature
    @Override public long getTemperatureValue(byte side) { return interior.currentTemperature; }
    @Override public long getTemperatureMax(byte side) { return interior.maxTemperature; }

    // ITileEntityMold
    @Override public boolean isMoldInputSide(byte side) { return SIDES_TOP[side] && checkStructure(false); }
    @Override public long getMoldMaxTemperature() { return getTemperatureMax(SIDE_INSIDE); }
    @Override public long getMoldRequiredMaterialUnits() { return 1; }
    @Override public long fillMold(OreDictMaterialStack stack, long temperature, byte side) { return isMoldInputSide(side) ? interior.tryAddStack(stack, temperature) : 0; }
    @Override public boolean fillMoldAtSide(ITileEntityMold mold, byte side, byte sideOfMold) { return interior.tryFillMold(mold, sideOfMold); }

    @Override public double getWeightValue(byte side) { return interior.weight(); }

    @Override public boolean attachCoversFirst(byte aSide) { return false; }

    @Override
    public boolean onBlockActivated3(EntityPlayer player, byte side, float hitX, float hitY, float hitZ) {
        if (SIDES_TOP[side]) {
            if (isServerSide() && player != null) {
                interior.onBlockActivated(player, this);
            }
            return true;
        }
        return false;
    }

    @Override
    public long onToolClick2(String tool, long remainingDurability, long quality, Entity player, List<String> chatReturn, IInventory playerInventory, boolean isSneaking, ItemStack stack, byte side, float hitX, float hitY, float hitZ) {
        if (isClientSide()) return super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, hitZ);

        if (tool.equals(TOOL_thermometer)) {if (chatReturn != null) chatReturn.add("Temperature: " + interior.currentTemperature + (interior.currentTemperature >= 1300 ? "K (too hot to pick it up right now!)" : "K")); return 10000;}
        if (tool.equals(TOOL_shovel) && SIDES_TOP[side] && player instanceof EntityPlayer) {
            CrucibleInterior.CrucibleContentStack content = interior.getStack(CrucibleInterior.MaterialState.SOLID);
            if (content != null) return content.convertToScrap((EntityPlayer)player, true);
        }
        //TODO remove
        if (tool.equals(TOOL_magnifyingglass)) {
            interior.printContent(chatReturn);
        }

        return super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
        interior.currentTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public IPacket getClientDataPacket(boolean sendAll) {
        return getClientDataPacketByteArray(true,
                (byte)UT.Code.getR(mRGBa), (byte)UT.Code.getG(mRGBa), (byte)UT.Code.getB(mRGBa),
                getVisualData(), getDirectionData(), displayedHeight,
                UT.Code.toByteS(displayedFluid, 0), UT.Code.toByteS(displayedFluid, 1),
                (byte)(almostMeltDown ? 1 : 0)
        );
    }

    @Override
    public boolean receiveDataByteArray(byte[] data, INetworkHandler networkHandler) {
        displayedHeight = data[5];
        displayedFluid = UT.Code.combine(data[6], data[7]);
        if (data.length >= 9) almostMeltDown = (data[8] != 0);
        return super.receiveDataByteArray(data, networkHandler);
    }

    private ITexture textureMolten;
    public int renderedRGBA = UNCOLORED;

    @Override
    public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
        short[] RGBaArray = UT.Code.getRGBaArray(mRGBa);
        if (almostMeltDown) {
            RGBaArray[0] = UT.Code.bind8(RGBaArray[0]*2+50);
            RGBaArray[1] = UT.Code.bind8(RGBaArray[1]*2+50);
            RGBaArray[2] = UT.Code.bind8(RGBaArray[2]/2+50);
        }
        renderedRGBA = UT.Code.getRGBaInt(RGBaArray);

        if (UT.Code.exists(displayedFluid, OreDictMaterial.MATERIAL_ARRAY)) {
            textureMolten = OreDictMaterial.MATERIAL_ARRAY[displayedFluid].getTextureMolten();
        } else {
            textureMolten = BlockTextureDefault.get(MT.NULL, OP.blockRaw, CA_GRAY_64, false);
        }
        return 6;
    }

    @Override
    public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
        if (mStructureOkay) switch(renderPass) {
            case  0: box(block,-0.999, 0.0,-0.999,-0.500, 3.000, 1.999); break;
            case  1: box(block,-0.999, 0.0,-0.999, 1.999, 3.000,-0.500); break;
            case  2: box(block, 1.500, 0.0,-0.999, 1.999, 3.000, 1.999); break;
            case  3: box(block,-0.999, 0.0, 1.500, 1.999, 3.000, 1.999); break;
            case  4: box(block,-0.999, 0.0,-0.999, 1.999, 1.125, 1.999); break;
            case  5: box(block,-0.999, 0.0,-0.999, 1.999, 1.125+(UT.Code.unsignB(displayedHeight) / 150.0), 1.999); break;
        }
        return true;
    }

    private ITexture getDefaultTexture(byte side) {
        IIconContainer[] textureSet = (side == mFacing) ? mTexturesFront : mTextures;
        return BlockTextureMulti.get(
                BlockTextureDefault.get(
                        textureSet[FACES_TBS[side]],
                        renderedRGBA,
                        true),
                BlockTextureDefault.get(
                        textureSet[FACES_TBS[side]+3], T)
        );
    }

    @Override
    public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		return switch (renderPass) {
			case 0, 2 -> SIDES_AXIS_Z[side] || side == SIDE_BOTTOM ? null : getDefaultTexture(side);
			case 1, 3 -> SIDES_AXIS_X[side] || side == SIDE_BOTTOM ? null : getDefaultTexture(side);
			case 4 -> SIDES_VERTICAL[side] ? getDefaultTexture(side) : null;
			case 5 -> displayedHeight != 0 && SIDES_TOP[side] ? textureMolten : null;
			default -> getDefaultTexture(side);
		};
    }

    @Override
    public void onWalkOver2(EntityLivingBase entity) {
        interior.handleEntityCollision(entity, WD.envTemp(worldObj, xCoord, yCoord, zCoord));
    }

    @Override public long getGibblValue(byte side) {return UT.Code.divup(OM.total(interior.content)*1000, U9);}
    @Override public long getGibblMax  (byte side) {return UT.Code.divup(interior.maxTotalUnits*1000, U9);}

    @Override public boolean[] getValidSides() {return SIDES_NONE;}
    @Override public boolean allowCovers(byte side) {return false;}

    @Override public ItemStack[] getDefaultInventory(NBTTagCompound nbt) {return new ItemStack[1];}
    @Override public int[] getAccessibleSlotsFromSide2(byte side) {return UT.Code.getAscendingArray(1);}
    @Override public boolean canInsertItem2(int slot, ItemStack stack, byte side) {return SIDES_TOP[side] && !slotHas(0);}
    @Override public boolean canExtractItem2(int slot, ItemStack stack, byte side) {return false;}

    public static final List<TagData> ENERGY_TYPES = new ArrayListNoNulls<>(false, TD.Energy.KU, TD.Energy.HU, TD.Energy.CU, TD.Energy.VIS_IGNIS);

    // ITileEntityEnergy
    @Override public boolean isEnergyType(TagData energyType, byte side, boolean aEmitting) {return !aEmitting && ENERGY_TYPES.contains(energyType);}
    @Override public boolean isEnergyCapacitorType(TagData energyType, byte side) {return ENERGY_TYPES.contains(energyType);}
    @Override public boolean isEnergyAcceptingFrom(TagData energyType, byte side, boolean aTheoretical) {return ENERGY_TYPES.contains(energyType);}
    @Override public long getEnergyDemanded(TagData energyType, byte side, long aSize) {return Long.MAX_VALUE - interior.storedEnergy;}
    @Override public long getEnergySizeInputMin(TagData energyType, byte side) {return 1;}
    @Override public long getEnergySizeInputRecommended(TagData energyType, byte side) {return 2048;}
    @Override public long getEnergySizeInputMax(TagData energyType, byte side) {return Long.MAX_VALUE;}
    @Override public Collection<TagData> getEnergyTypes(byte side) {return ENERGY_TYPES;}

    @Override public long doInject(TagData energyType, byte side, long size, long amount, boolean doInject) {
        return interior.injectEnergy(energyType, size, amount, doInject, WD.oxygen(worldObj, xCoord, yCoord + 1, zCoord));
    }
}
