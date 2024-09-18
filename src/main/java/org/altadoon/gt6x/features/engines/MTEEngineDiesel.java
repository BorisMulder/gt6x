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
	public static final String NBT_EFFICIENCY_MIN = "gt6x.eff.min";
	public static final String NBT_EFFICIENCY_MAX = "gt6x.eff.max";
	public static final String NBT_WARMUP_TIME = "gt6x.warmup.time";

	public short efficiencyMin = 0;
	public short efficiencyMax = 10000;
	private long envTemperature = DEF_ENV_TEMP;
	public long temperature = DEF_ENV_TEMP;
	private static final long OPTIMAL_TEMPERATURE = 355;
	public int warmupTime = 0;

	@Override
	public void readFromNBT2(NBTTagCompound nbt) {
		super.readFromNBT2(nbt);
		if (nbt.hasKey(NBT_EFFICIENCY_MIN)) efficiencyMin = (short) UT.Code.bind_(0, 10000, nbt.getShort(NBT_EFFICIENCY_MIN));
		if (nbt.hasKey(NBT_EFFICIENCY_MAX)) efficiencyMax = (short) UT.Code.bind_(0, 10000, nbt.getShort(NBT_EFFICIENCY_MAX));
		if (nbt.hasKey(NBT_WARMUP_TIME)) warmupTime = nbt.getInteger(NBT_WARMUP_TIME);
		if (nbt.hasKey(NBT_TEMPERATURE)) temperature = nbt.getLong(NBT_TEMPERATURE);
		if (!nbt.hasKey(NBT_EFFICIENCY)) efficiency = efficiencyMin;
	}

	@Override
	public void writeToNBT2(NBTTagCompound nbt) {
		super.writeToNBT2(nbt);
		UT.NBT.setNumber(nbt, NBT_EFFICIENCY, efficiency);
		UT.NBT.setNumber(nbt, NBT_TEMPERATURE, temperature);
	}

	@Override
	protected String getEfficiencyTooltip() {
		StringBuilder builder = new StringBuilder(LH.get(LH.EFFICIENCY));
		builder.append(": ").append(LH.Chat.WHITE).append(LH.percent(efficiencyMin));
		if (efficiencyMin != efficiencyMax) {
			builder.append(" - ").append(LH.percent(efficiencyMax));
		}
		return builder.append("%").toString();
	}


	@Override
	public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
		super.onPlaced(stack, player, container, world, x, y, z, side, hitX, hitY, hitZ);

		envTemperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
		temperature = envTemperature;

		return true;
	}

	private void updateTemperature() {
		long tempDiff;
		if (activity.mState > 0) {
			tempDiff = OPTIMAL_TEMPERATURE - temperature;
		} else {
			tempDiff = envTemperature - temperature;
		}

		if (temperature > getTemperatureMax(SIDE_ANY) || temperature < -OPTIMAL_TEMPERATURE) {
			efficiency = 0;
		} else {
			efficiency = (Math.abs(OPTIMAL_TEMPERATURE - temperature) / OPTIMAL_TEMPERATURE)
		}
	}

	@Override
	public String getTileEntityName() {return "gt6x.multitileentity.generator.engine_diesel";}

	@Override
	public long getTemperatureValue(byte aSide) {
		return temperature;
	}

	@Override
	public long getTemperatureMax(byte aSide) {
		return OPTIMAL_TEMPERATURE * 2;
	}
}
