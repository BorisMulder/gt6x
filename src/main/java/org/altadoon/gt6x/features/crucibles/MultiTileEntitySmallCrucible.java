package org.altadoon.gt6x.features.crucibles;

import gregapi.GT_API_Proxy;
import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.code.ArrayListNoNulls;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.network.INetworkHandler;
import gregapi.network.IPacket;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.render.BlockTextureDefault;
import gregapi.render.ITexture;
import gregapi.tileentity.ITileEntityServerTickPost;
import gregapi.tileentity.base.TileEntityBase07Paintable;
import gregapi.tileentity.data.ITileEntityGibbl;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.tileentity.data.ITileEntityWeight;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.machines.ITileEntityCrucible;
import gregapi.tileentity.machines.ITileEntityMold;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.altadoon.gt6x.common.RMx;

import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;

public class MultiTileEntitySmallCrucible extends TileEntityBase07Paintable implements ITileEntityCrucible, ITileEntityEnergy, ITileEntityGibbl, ITileEntityWeight, ITileEntityTemperature, ITileEntityMold, ITileEntityServerTickPost, IMultiTileEntity.IMTE_RemovedByPlayer, IMultiTileEntity.IMTE_OnEntityCollidedWithBlock, IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool, IMultiTileEntity.IMTE_AddToolTips, IMultiTileEntity.IMTE_OnPlaced {
    protected boolean almostMeltDown = false;
    protected byte displayedHeight = 0, oldDisplayedHeight = 0;
    protected short displayedFluid = -1, oldDisplayedFluid = -1;

    protected CrucibleInterior interior = new CrucibleInterior(64, 16, 7,
            200, 6, U1000, 3, 3, RMx.Thermite, RMx.Bessemer, RMx.SSS);

    @Override public String getTileEntityName() {return "gt6x.multitileentity.smeltery";}

    @Override
    public void readFromNBT2(NBTTagCompound nbt) {
        super.readFromNBT2(nbt);
        interior.readFromNBT(nbt);
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
    public void addToolTips(List<String> list, ItemStack stack, boolean f3_h) {
        list.add(LH.Chat.CYAN     + LH.get(LH.CONVERTS_FROM_X) + " 1 " + TD.Energy.HU.getLocalisedNameShort() + " " + LH.get(LH.CONVERTS_TO_Y) + " +1 K " + LH.get(LH.CONVERTS_PER_Z) + " "+ interior.kgPerEnergy + "kg (at least "+getEnergySizeInputMin(TD.Energy.HU, SIDE_ANY)+" Units per Tick required!)");
        list.add(LH.Chat.YELLOW   + LH.get(LH.TOOLTIP_THERMALMASS) + mMaterial.getWeight(U*7) + " kg");
        list.add(LH.Chat.DRED     + LH.get(LH.HAZARD_MELTDOWN) + " (" + getTemperatureMax(SIDE_INSIDE) + " K)");
        list.add(LH.Chat.WHITE    + LH.get("gt.tooltip.crucible.1"));
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
        CrucibleInterior.CrucibleTickResult result = interior.onTick(this);

        displayedHeight = (byte)UT.Code.scale(result.totalUnits, interior.maxTotalUnits * U, 255, false);
        CrucibleInterior.CrucibleContentStack topStack = interior.getStack(CrucibleInterior.MaterialState.LIQUID);
        if (topStack != null) {
            displayedFluid = topStack.stack.mMaterial.mID;
        } else if (result.totalUnits > 0) {
            displayedFluid = -1;
        }

        if (almostMeltDown != (interior.currentTemperature - 200 > getTemperatureMax(SIDE_INSIDE))) {
            almostMeltDown = !almostMeltDown;
            result.updateClientData = true;
        }

        if (result.meltdown) {
            worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.flowing_lava, 1, 3);
        }

        if (result.updateClientData) updateClientData();
    }

    @Override public long getTemperatureValue(byte side) { return interior.currentTemperature; }
    @Override public long getTemperatureMax(byte side) { return interior.maxTemperature; }
    @Override public boolean isMoldInputSide(byte side) { return SIDES_TOP[side]; }
    @Override public long getMoldMaxTemperature() { return getTemperatureMax(SIDE_INSIDE); }
    @Override public long getMoldRequiredMaterialUnits() { return 1; }
    @Override public long fillMold(OreDictMaterialStack stack, long temperature, byte side) { return isMoldInputSide(side) ? interior.tryAddStack(stack, temperature) : 0; }
    @Override public boolean fillMoldAtSide(ITileEntityMold mold, byte side, byte sideOfMold) { return interior.tryFillMold(mold, sideOfMold); }

    @Override public double getWeightValue(byte side) {return interior.weight(); }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, boolean willHarvest) {
        if (interior.currentTemperature >= 1300 && isServerSide() && !UT.Entities.isCreative(player)) {
            UT.Sounds.send(SFX.MC_FIZZ, this);
            interior.trashAll();
            interior.damageEntities(this);
            interior.causeFire(this, interior.currentTemperature / 25);
            return worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.flowing_lava, 1, 3);
        }
        return worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean breakBlock() {
        interior.trashAll();
        return super.breakBlock();
    }

    @Override public boolean attachCoversFirst(byte side) {return false;}

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

        return super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, isSneaking, stack, side, hitX, hitY, hitZ);
    }

    @Override
    public void onEntityCollidedWithBlock(Entity entity) {
        interior.handleEntityCollision(entity, WD.envTemp(worldObj, xCoord, yCoord, zCoord));
    }

    @Override
    public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
        interior.currentTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public boolean onTickCheck(long timer) {
        return super.onTickCheck(timer) || displayedHeight != oldDisplayedHeight || displayedFluid != oldDisplayedFluid;
    }

    @Override
    public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
        super.onTickResetChecks(aTimer, aIsServerSide);
        oldDisplayedFluid = displayedFluid;
        oldDisplayedHeight = displayedHeight;
    }

    @Override
    public IPacket getClientDataPacket(boolean sendAll) {
        if (sendAll) return getClientDataPacketByteArray(true, displayedHeight, UT.Code.toByteS(displayedFluid, 0), UT.Code.toByteS(displayedFluid, 1), (byte)UT.Code.getR(mRGBa), (byte)UT.Code.getG(mRGBa), (byte)UT.Code.getB(mRGBa), (byte)(almostMeltDown ? 1 : 0));
        if (displayedFluid != oldDisplayedFluid) return getClientDataPacketByteArray(false, displayedHeight, UT.Code.toByteS(displayedFluid, 0), UT.Code.toByteS(displayedFluid, 1));
        return getClientDataPacketByteArray(false, displayedHeight);
    }

    @Override
    public boolean receiveDataByteArray(byte[] data, INetworkHandler networkHandler) {
        displayedHeight = data[0];
        if (data.length >= 3) displayedFluid = UT.Code.combine(data[1], data[2]);
        if (data.length >= 6) mRGBa = UT.Code.getRGBInt(new short[] {UT.Code.unsignB(data[3]), UT.Code.unsignB(data[4]), UT.Code.unsignB(data[5])});
        if (data.length >= 7) almostMeltDown = (data[6] != 0);
        return true;
    }

    private ITexture texture, textureMolten;

    @Override
    public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
        short[] rgbaArray = UT.Code.getRGBaArray(mRGBa);
        boolean glow;
        if (almostMeltDown) {
            rgbaArray[0] = UT.Code.bind8(rgbaArray[0]*2+50);
            rgbaArray[1] = UT.Code.bind8(rgbaArray[1]*2+50);
            rgbaArray[2] = UT.Code.bind8(rgbaArray[2]/2+50);
            glow = true;
        } else {
            glow = mMaterial.contains(TD.Properties.GLOWING);
        }
        texture = BlockTextureDefault.get(mMaterial, OP.blockSolid, rgbaArray, glow);

        if (UT.Code.exists(displayedFluid, OreDictMaterial.MATERIAL_ARRAY)) {
            textureMolten = OreDictMaterial.MATERIAL_ARRAY[displayedFluid].getTextureMolten();
        } else {
            textureMolten = BlockTextureDefault.get(MT.NULL, OP.blockRaw, CA_GRAY_64, false);
        }
        return 6;
    }

    @Override
    public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
        return switch(renderPass) {
            case 0, 2 -> SIDES_AXIS_Z[side] || side == SIDE_BOTTOM ? null : texture;
            case 1, 3 -> SIDES_AXIS_X[side] || side == SIDE_BOTTOM ? null : texture;
            case 4 -> SIDES_VERTICAL[side] ? texture : null;
            case 5 -> displayedHeight != 0 && SIDES_TOP[side] ? textureMolten : null;
            default -> texture;
        };
    }

    @Override
    public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
        return switch(aRenderPass) {
            case 0 -> box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[14], PX_N[ 0], PX_N[ 0]);
            case 1 -> box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[ 0], PX_N[ 0], PX_N[14]);
            case 2 -> box(aBlock, PX_P[14], PX_P[ 0], PX_P[ 0], PX_N[ 0], PX_N[ 0], PX_N[ 0]);
            case 3 -> box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[14], PX_N[ 0], PX_N[ 0], PX_N[ 0]);
            case 4 -> box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[ 0], PX_N[14], PX_N[ 0]);
			case 5 -> {
                float height = 0.125F + (UT.Code.unsignB(displayedHeight) / 292.571428F);
			    yield box(aBlock, PX_P[ 0], PX_P[ 0], PX_P[ 0], PX_N[ 0], height  , PX_N[ 0]);
			}
			default -> false;
        };
    }

    @Override public AxisAlignedBB getCollisionBoundingBoxFromPool() { return box(0.125, 0.125, 0.125, 0.875, 0.875, 0.875); }
    @Override public boolean addDefaultCollisionBoxToList() {return false;}

    @Override
    public void addCollisionBoxesToList2(AxisAlignedBB AABB, List<AxisAlignedBB> list, Entity entity) {
        box(AABB, list, PX_P[14], PX_P[ 1], PX_P[ 1], PX_N[ 1], PX_N[ 1], PX_N[ 1]);
        box(AABB, list, PX_P[ 1], PX_P[ 1], PX_P[14], PX_N[ 1], PX_N[ 1], PX_N[ 1]);
        box(AABB, list, PX_P[ 1], PX_P[ 1], PX_P[ 1], PX_N[14], PX_N[ 1], PX_N[ 1]);
        box(AABB, list, PX_P[ 1], PX_P[ 1], PX_P[ 1], PX_N[ 1], PX_N[ 1], PX_N[14]);
        box(AABB, list, PX_P[ 1], PX_P[ 1], PX_P[ 1], PX_N[ 1], PX_N[14], PX_N[ 1]);
    }

    @Override
    public boolean checkObstruction(EntityPlayer player, byte side, float hitX, float hitY, float hitZ) {
        return SIDES_BOTTOM_HORIZONTAL[side] && super.checkObstruction(player, side, hitX, hitY, hitZ);
    }

    @Override public float getSurfaceSize           (byte side) {return 1.0F;}
    @Override public float getSurfaceSizeAttachable (byte side) {return 1.0F;}
    @Override public float getSurfaceDistance       (byte side) {return 0.0F;}
    @Override public boolean isSurfaceSolid         (byte side) {return !SIDES_TOP[side];}
    @Override public boolean isSurfaceOpaque2       (byte side) {return !SIDES_TOP[side];}
    @Override public boolean isSideSolid2           (byte side) {return !SIDES_TOP[side];}

    @Override public long getGibblValue(byte side) {return UT.Code.divup(OM.total(interior.content)*1000, U9);}
    @Override public long getGibblMax  (byte side) {return UT.Code.divup(interior.maxTotalUnits*1000, U9);}

    @Override public boolean canDrop(int inventorySlot) {return true;}
    @Override public boolean allowCovers(byte side) {return false;}

    @Override public ItemStack[] getDefaultInventory(NBTTagCompound nbt) {return new ItemStack[1];}
    @Override public int[] getAccessibleSlotsFromSide2(byte side) {return UT.Code.getAscendingArray(1);}
    @Override public boolean canInsertItem2(int slot, ItemStack stack, byte side) {return SIDES_TOP[side] && !slotHas(0);}
    @Override public boolean canExtractItem2(int slot, ItemStack stack, byte side) {return false;}

    public static final List<TagData> ENERGY_TYPES = new ArrayListNoNulls<>(F, TD.Energy.KU, TD.Energy.HU, TD.Energy.CU, TD.Energy.VIS_IGNIS);

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
