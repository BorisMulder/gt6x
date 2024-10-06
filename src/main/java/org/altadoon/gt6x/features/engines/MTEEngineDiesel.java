package org.altadoon.gt6x.features.engines;

import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.data.LH;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.data.ITileEntityTemperature;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.altadoon.gt6x.common.rendering.Geometry;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class MTEEngineDiesel extends MultiTileEntityEngineBase implements ITileEntityTemperature {
	public static final long OPTIMAL_TEMPERATURE = 360;
	public static final int WARMUP_FACTOR = 10;
	public static final int WARMUP_CONSTANT = 10;
	protected int warmupTicks = 0;
	protected int cooldownTicks = 100;
	protected long temperature = DEF_ENV_TEMP;
	protected int thermometerIndex = 0;

	@Override
	public void readFromNBT2(NBTTagCompound nbt) {
		super.readFromNBT2(nbt);
		if (nbt.hasKey(NBT_TEMPERATURE)) {
			temperature = nbt.getLong(NBT_TEMPERATURE);
			updateThermometerIndex();
		}
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
		updateThermometerIndex();
		return true;
	}

	protected void updateTemperature() {
		long tempDiff;
		long previousTemp = temperature;
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
		if (temperature != previousTemp) {
			updateThermometerIndex();
		}
	}

	protected void updateThermometerIndex() {
		long temp_diff = temperature - OPTIMAL_TEMPERATURE;
		thermometerIndex = (int)Math.ceil(temp_diff / (100.0 / 7.0) + 9.5);
		if (thermometerIndex < 0) thermometerIndex = 0;
		if (thermometerIndex >= dieselThermometerIcons.length) thermometerIndex = dieselThermometerIcons.length - 1;
	}

	protected void updateEfficiency() {
		if (temperature > OPTIMAL_TEMPERATURE + 100 || temperature < OPTIMAL_TEMPERATURE - 100) {
			efficiency = 0;
		} else {
			long temp_diff = Math.abs(OPTIMAL_TEMPERATURE - temperature);
			efficiency = (short)(10000 - (temp_diff * 100));
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

	@Override
	public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
		return super.getRenderPasses2(block, shouldSideBeRendered) + 11;
	}

	@Override
	public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
		return switch (renderPass - super.getRenderPasses2(block, shouldSideBeRendered)) {
			// top blocks
			case 0 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[6], PX_N[4], PX_P[1], PX_N[6], PX_N[2], PX_N[1]));
			case 1 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[5], PX_N[2], PX_P[1], PX_N[5], PX_N[0], PX_N[5]));
			// side pipes
			case 2 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[4], PX_N[3], PX_P[1], PX_N[2], PX_N[1], PX_N[1]));
			case 3 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[2], PX_N[4], PX_P[1], PX_P[4], PX_N[2], PX_N[1]));
			// top pipes
			case 4 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[2], PX_N[2], PX_N[5], PX_N[3], PX_N[0], PX_N[3]));
			case 5 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[1], PX_N[3], PX_P[5], PX_N[4], PX_N[1], PX_P[7]));
			// small perpendicular pipes
			case 6 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[1], PX_N[7], PX_P[5], PX_P[3], PX_N[3], PX_P[7]));
			case 7 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[2], PX_N[7], PX_N[5], PX_P[4], PX_N[4], PX_N[3]));
			// bottom pipes
			case 8 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[2], PX_P[1], PX_P[4], PX_N[0], PX_P[4], PX_P[6]));
			case 9 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[2], PX_P[1], PX_N[6], PX_N[0], PX_P[4], PX_N[4]));
			// display
			case 10 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[1], PX_P[4], PX_P[4], PX_N[0], PX_N[4], PX_N[4]));
			default -> super.setBlockBounds2(block, renderPass, shouldSideBeRendered);
		};
	}

	protected ITexture getDieselTextureByIndex(int idx) {
		return BlockTextureMulti.get(BlockTextureDefault.get(dieselBaseIcons[idx], mRGBa), BlockTextureDefault.get((activity.mState>0? dieselActiveIcons : dieselPassiveIcons)[idx]));
	}

	protected ITexture getThermometerTexture() {
		return BlockTextureMulti.get(
				BlockTextureDefault.get(dieselBaseIcons[1], mRGBa),
				BlockTextureDefault.get(dieselPassiveIcons[1]),
				BlockTextureDefault.get(dieselThermometerIcons[thermometerIndex])
		);
	}

	@Override
	public void setVisualData(byte data) {
		activity.mState = (byte)(data & 3);
		thermometerIndex = (data & 124) >>2;
	}

	@Override public byte getVisualData() {
		return  (byte)(activity.mState | (thermometerIndex <<2));
	}

	@Override
	public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		return switch (renderPass - super.getRenderPasses2(block, shouldSideBeRendered)) {
			case 2, 3, 4 -> getDieselTextureByIndex(0);
			case 10 -> {
				if (side == mFacing || side == OPOS[mFacing] || side == mSecondFacing || side == OPOS[mSecondFacing]) {
					yield super.getTexture2(block, renderPass, side, shouldSideBeRendered);
				} else {
					yield getThermometerTexture();
				}
			}
			default -> super.getTexture2(block, renderPass, side, shouldSideBeRendered);
		};
	}

	public static IIconContainer[] dieselBaseIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/colored/pipes_colored"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/colored/thermometer"),
	}, dieselPassiveIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/overlay/pipes_colored"),
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/overlay/thermometer"),
	}, dieselActiveIcons = new IIconContainer[] {
			new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/overlay_active/pipes_colored"),
			null,
	}, dieselThermometerIcons = new IIconContainer[20];

	static {
		for (int i = 0; i < dieselThermometerIcons.length; i++) {
			dieselThermometerIcons[i] = new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/thermometer_scale/" + (i < 10 ? "0" : "") + i);
		}
	}
}
