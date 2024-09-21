package org.altadoon.gt6x.features.engines;

import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.data.LH;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import static gregapi.data.CS.*;

public class MTEEngineDiesel extends MultiTileEntityEngineBase implements ITileEntityTemperature {
	public static final long OPTIMAL_TEMPERATURE = 360;
	public static final int WARMUP_FACTOR = 10;
	public static final int WARMUP_CONSTANT = 10;
	protected int warmupTicks = 0;
	protected int cooldownTicks = 100;
	protected long temperature = DEF_ENV_TEMP;

	@Override
	public void readFromNBT2(NBTTagCompound nbt) {
		super.readFromNBT2(nbt);
		if (nbt.hasKey(NBT_TEMPERATURE)) temperature = nbt.getLong(NBT_TEMPERATURE);
	}

	@Override
	public void writeToNBT2(NBTTagCompound nbt) {
		super.writeToNBT2(nbt);
		UT.NBT.setNumber(nbt, NBT_TEMPERATURE, temperature);
	}

	@Override
	protected String getEfficiencyTooltip() {
		return LH.get(LH.EFFICIENCY) + ": " + LH.Chat.WHITE + "0 - " + LH.percent(efficiency) + "%";
	}

	@Override
	public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
		super.onPlaced(stack, player, container, world, x, y, z, side, hitX, hitY, hitZ);
		temperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	protected void updateTemperature() {
		long tempDiff;
		if (activity.mState > 0) { // if running, approach optimal temperature
			tempDiff = OPTIMAL_TEMPERATURE - temperature;
			if (tempDiff != 0) {
				warmupTicks++;
				int next_temp_change = (int)(100 - Math.abs(tempDiff)) * WARMUP_FACTOR + WARMUP_CONSTANT;
				if (warmupTicks >= next_temp_change) {
					warmupTicks = 0;
					if (tempDiff > 0) temperature++;
					else temperature--;
					updateEfficiency();
				}
			}
			cooldownTicks = 100;
		} else { // if not running, go back to environment temperature
			long envTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
			if (temperature != envTemperature) {
				if (cooldownTicks > 0) cooldownTicks--;
				if (cooldownTicks <= 0) {
					cooldownTicks = 10;
					if (temperature > envTemperature) temperature--;
					else temperature++;
				}
			}
		}
	}

	protected void updateEfficiency() {
		if (temperature > OPTIMAL_TEMPERATURE + 100 || temperature < OPTIMAL_TEMPERATURE - 100) {
			efficiency = 0;
		} else {
			efficiency = (short)(10000 - (Math.abs(OPTIMAL_TEMPERATURE - temperature) * 100));
		}
	}

	@Override
	public String getTileEntityName() {return "gt6x.multitileentity.generator.engine_diesel";}

	@Override
	public long getTemperatureValue(byte side) {
		return temperature;
	}

	@Override
	public long getTemperatureMax(byte side) {
		return OPTIMAL_TEMPERATURE;
	}
}
