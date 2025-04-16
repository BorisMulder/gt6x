package org.altadoon.gt6x.features.engines.blocks;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.LH;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregtech.tileentity.multiblocks.MultiTileEntityLargeTurbineGas;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.utils.RepeatedSound;
import org.altadoon.gt6x.common.utils.Sound;
import org.altadoon.gt6x.features.engines.Engines;

import java.util.List;
import java.util.ListIterator;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.MTEx.NBT_MTE_MULTIBLOCK_PART_REG;

public class MultiTileEntityLargeTurbineGasX extends MultiTileEntityLargeTurbineGas {
	@Override public String getTileEntityName() {return "gt6x.multitileentity.multiblock.turbine.gas";}

	protected         Sound START_SOUND   = new         Sound(Gt6xMod.MOD_ID.toLowerCase() + ":" + "machines.gasturbine_starting", 300);
	protected         Sound STOP_SOUND    = new         Sound(Gt6xMod.MOD_ID.toLowerCase() + ":" + "machines.gasturbine_stopping", 30);
	protected RepeatedSound RUNNING_SOUND = new RepeatedSound(Gt6xMod.MOD_ID.toLowerCase() + ":" + "machines.gasturbine_running" , 60);
	protected Sound currentSound = null;

	protected enum State {
		UNKNOWN, OFF, STARTING, RUNNING, STOPPING
	}
	protected State state = State.UNKNOWN;
	protected long idleTime = 0;
	protected int partRegId = Block.getIdFromBlock(MTEx.gt6MTEReg.mBlock);

	@Override
	public void readFromNBT2(NBTTagCompound nbt) {
		super.readFromNBT2(nbt);
		if (nbt.hasKey(NBT_MTE_MULTIBLOCK_PART_REG)) {
			partRegId = nbt.getInteger(NBT_MTE_MULTIBLOCK_PART_REG);
		}
	}

	@Override
	public void addToolTips(List<String> list, ItemStack stack, boolean F3_H) {
		super.addToolTips(list, stack, F3_H);
		ListIterator<String> iter = list.listIterator();
		while (iter.hasNext()) {
			if (iter.next().startsWith(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.gasturbine.1"))) {
				iter.set(LH.Chat.WHITE + LH.get("gt.tooltip.multiblock.gasturbine.1") + MultiTileEntityRegistry.getRegistry(partRegId).getLocal(mTurbineWalls));
				break;
			}
		}
	}

	@Override
	public boolean checkStructure2() {
		int
				minX = xCoord-(SIDE_X_NEG==mFacing?0:SIDE_X_POS==mFacing?3:1),
				minY = yCoord-(SIDE_Y_NEG==mFacing?0:SIDE_Y_POS==mFacing?3:1),
				minZ = zCoord-(SIDE_Z_NEG==mFacing?0:SIDE_Z_POS==mFacing?3:1),
				maxX = xCoord+(SIDE_X_POS==mFacing?0:SIDE_X_NEG==mFacing?3:1),
				maxY = yCoord+(SIDE_Y_POS==mFacing?0:SIDE_Y_NEG==mFacing?3:1),
				maxZ = zCoord+(SIDE_Z_POS==mFacing?0:SIDE_Z_NEG==mFacing?3:1),
				outX = getOffsetXN(mFacing, 3),
				outY = getOffsetYN(mFacing, 3),
				outZ = getOffsetZN(mFacing, 3);

		if (worldObj.blockExists(minX, minY, minZ) && worldObj.blockExists(maxX, maxY, maxZ)) {
			mEmitter = null;
			boolean success = true;
			for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++) for (int z = minZ; z <= maxZ; z++) {
				int bits;
				if (x == outX && y == outY && z == outZ) {
					bits = MultiTileEntityMultiBlockPart.ONLY_ENERGY_OUT;
				} else {
					if ((SIDES_AXIS_X[mFacing] && x == xCoord) || (SIDES_AXIS_Y[mFacing] && y == yCoord) || (SIDES_AXIS_Z[mFacing] && z == zCoord)) {
						bits = (y == minY ? MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID     : MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_IN);
					} else {
						bits = (y == minY ? MultiTileEntityMultiBlockPart.ONLY_ITEM_FLUID_OUT : MultiTileEntityMultiBlockPart.NOTHING);
					}
				}
				if (!ITileEntityMultiBlockController.Util.checkAndSetTarget(this, x, y, z, mTurbineWalls, partRegId, x == outX && y == outY && z == outZ ? 3 : 0, bits)) success = false;
			}
			return success;
		}
		return mStructureOkay;
	}

	protected void startSound() {
		state = State.STARTING;
		currentSound = START_SOUND;
		currentSound.play(2.0F, xCoord, yCoord, zCoord);
	}

	protected void stopSound() {
		state = State.STOPPING;
		currentSound = STOP_SOUND;
		currentSound.play(2.0F, xCoord, yCoord, zCoord);
	}

	protected void runSound() {
		state = State.RUNNING;
		currentSound = RUNNING_SOUND;
		RUNNING_SOUND.start(2.0F, xCoord, yCoord, zCoord);
	}

	protected void soundStateMachine() {
		if (currentSound != null) currentSound.run();

		if (mActivity.mState > 0) {
			idleTime = 0;
			switch(state) {
				case STOPPING:
					if (STOP_SOUND.finished()) startSound();
					break;
				case OFF:
					startSound();
					break;
				case STARTING:
					if (START_SOUND.finished()) runSound();
					break;
				case UNKNOWN:
					runSound();
					break;
				case RUNNING:
					RUNNING_SOUND.unstop();
			}
		} else {
			switch(state) {
				case UNKNOWN:
					state = State.OFF;
					break;
				case STARTING:
					if (START_SOUND.finished()) {
						if (idleTime >= RUNNING_SOUND.duration) {
							stopSound();
						} else {
							runSound();
						}
					}
					break;
				case RUNNING:
					if (idleTime >= RUNNING_SOUND.duration) {
						RUNNING_SOUND.stop();
						if (RUNNING_SOUND.finished()) {
							stopSound();
						}
					}
					break;
				case STOPPING:
					if (STOP_SOUND.finished()) {
						state = State.OFF;
					}
					break;
			}
			idleTime++;
		}

		RUNNING_SOUND.run();
	}

	@Override
	public void onTick2(long timer, boolean isServerSide) {
		super.onTick2(timer, isServerSide);

		if (!isServerSide && Engines.ENGINE_SOUNDS_ENABLED) {
			soundStateMachine();
		}
	}
}
