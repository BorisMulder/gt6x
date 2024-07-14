package org.altadoon.gt6x.features.engines.blocks;

import gregapi.block.multitileentity.MultiTileEntityContainer;
import gregapi.code.TagData;
import gregapi.data.LH;
import gregapi.data.TD;
import gregapi.old.Textures;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.IIconContainer;
import gregapi.render.ITexture;
import gregapi.tileentity.data.ITileEntityTemperature;
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
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.common.rendering.Geometry;
import org.altadoon.gt6x.common.utils.RepeatedSound;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.RMx.FMx;

public class MTEEngineDiesel extends MultiTileEntityEngineBase implements ITileEntityTemperature {
	protected static final int GAS_RANGE = 3, FLAME_RANGE = 3;
	protected static final long KG_PER_ENERGY = 100;
	protected static final int WARMUP_FACTOR = 1;
	protected static final int WARMUP_DIVISOR = 2;
	protected static final int WARMUP_CONSTANT = 10;

	protected boolean containsFuel = false;
	protected long optimalTemperature = 360L;
	protected int warmupTicks = 0;
	protected int cooldownTicks = 100;
	protected long storedInputEnergy = 0;
	protected long temperature = DEF_ENV_TEMP;
	protected byte thermometerIndex = 0;
	protected byte oldThermometerIndex = 0;

	@Override
	public void readFromNBT2(NBTTagCompound nbt) {
		super.readFromNBT2(nbt);
		storedInputEnergy = nbt.getLong(NBT_INPUT);
		if (nbt.hasKey(NBT_TEMPERATURE)) {
			temperature = nbt.getLong(NBT_TEMPERATURE);
			updateThermometerIndex();
		}
	}

	@Override
	public void writeToNBT2(NBTTagCompound nbt) {
		super.writeToNBT2(nbt);
		UT.NBT.setNumber(nbt, NBT_TEMPERATURE, temperature);
		UT.NBT.setNumber(nbt, NBT_INPUT, storedInputEnergy);
	}

	@Override
	protected String getEfficiencyTooltip() {
		return LH.get(LH.EFFICIENCY) + ": " + LH.Chat.WHITE + "0 - " + LH.percent(efficiency) + "%";
	}

	@Override
	protected RepeatedSound getSound() {
		return new RepeatedSound(Gt6xMod.MOD_ID.toLowerCase() + ":" + "machines.diesel_engine", 44);
	}

	@Override
	public boolean onPlaced(ItemStack stack, EntityPlayer player, MultiTileEntityContainer container, World world, int x, int y, int z, byte side, float hitX, float hitY, float hitZ) {
		super.onPlaced(stack, player, container, world, x, y, z, side, hitX, hitY, hitZ);
		temperature = WD.envTemp(worldObj, xCoord, yCoord, zCoord);
		updateThermometerIndex();
		return true;
	}

	@Override
	public void onTick2(long timer, boolean isServerSide) {
		if (isServerSide) {
			updateFuelOptimalTemperature();
			updateTemperature();
			updateEfficiency();
		}

		super.onTick2(timer, isServerSide);
	}

	protected void updateFuelOptimalTemperature() {
		boolean empty = tanks[0].isEmpty();
		if (containsFuel && empty) {
			containsFuel = false;
			optimalTemperature = 360L;
		} else if (!containsFuel && !empty) {
			containsFuel = true;
			int fuelId = tanks[0].getFluid().getFluidID();
			if (FMx.diesel_fuel_temperatures.containsKey(fuelId)) {
				optimalTemperature = FMx.diesel_fuel_temperatures.get(fuelId);
			} else {
				throw new IllegalStateException("Diesel fuel not registered: " + tanks[0].getFluid().getUnlocalizedName());
			}

			updateTemperature();
			updateEfficiency();
		}
	}

	@SuppressWarnings("unchecked")
	protected void updateTemperature() {
		long previousTemp = temperature;

		// Convert stored energy to temperature
		long requiredEnergy = 1 + (long)(mMaterial.getWeight(22*U) / KG_PER_ENERGY), conversions = storedInputEnergy / requiredEnergy;
		if (conversions != 0) {
			storedInputEnergy -= conversions * requiredEnergy;
			temperature += conversions;
			cooldownTicks = 100;
		}

		if (activity.mState > 0) { // if running, approach optimal temperature
			long tempDiff = optimalTemperature - temperature;
			if (tempDiff != 0) {
				warmupTicks++;
				int next_temp_change = (int)((100 - Math.abs(tempDiff)) * WARMUP_FACTOR) / WARMUP_DIVISOR + WARMUP_CONSTANT;
				if (warmupTicks >= next_temp_change) {
					warmupTicks = 0;
					if (tempDiff > 0) temperature++;
					else temperature--;
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

		if (temperature < 50) {
			temperature = 50;
		}

		if (temperature != previousTemp) {
			updateThermometerIndex();
		}

		if (temperature > getTemperatureMax(SIDE_INSIDE)) {
			// melt down
			UT.Sounds.send(SFX.MC_FIZZ, this);

			if (temperature >= 320) try {
				for (EntityLivingBase tLiving : (List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box(-GAS_RANGE, -1, -GAS_RANGE, GAS_RANGE+1, GAS_RANGE+1, GAS_RANGE+1)))
					UT.Entities.applyTemperatureDamage(tLiving, temperature, 2);
			} catch(Throwable e) {
				e.printStackTrace(ERR);
			}
			for (int j = 0, k = UT.Code.bindInt(temperature / 25); j < k; j++)
				WD.fire(worldObj, xCoord-FLAME_RANGE+rng(2*FLAME_RANGE+1), yCoord-1+rng(2+FLAME_RANGE), zCoord-FLAME_RANGE+rng(2*FLAME_RANGE+1), rng(3) != 0);

			worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.flowing_lava, 1, 3);
		}
	}

	@Override
	public long onToolClick2(String tool, long remainingDurability, long quality, Entity player, List<String> chatReturn, IInventory playerInventory, boolean sneaking, ItemStack stack, byte side, float hitX, float hitY, float hitZ) {
		if (tool.equals(TOOL_thermometer)) {if (chatReturn != null) chatReturn.add("Temperature: " + temperature + "K"); return 10000;}
		return super.onToolClick2(tool, remainingDurability, quality, player, chatReturn, playerInventory, sneaking, stack, side, hitX, hitY, hitZ);
	}

	protected void updateThermometerIndex() {
		long temp_diff = temperature - optimalTemperature;
		thermometerIndex = (byte)Math.ceil(temp_diff / (100.0 / 7.0) + 9.5);
		if (thermometerIndex < 0) thermometerIndex = 0;
		if (thermometerIndex >= thermometerFillTextures.length) thermometerIndex = (byte)(thermometerFillTextures.length - 1);
	}

	protected void updateEfficiency() {
		if (temperature > optimalTemperature + 100 || temperature < optimalTemperature - 100) {
			efficiency = 0;
		} else {
			long temp_diff = Math.abs(optimalTemperature - temperature);
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
		return mMaterial.mMeltingPoint;
	}

	@Override
	public boolean isEnergyType(TagData energyType, byte side, boolean emitting) {
		if (emitting) return super.isEnergyType(energyType, side, true);
		else return energyType == TD.Energy.HU || energyType == TD.Energy.CU;
	}

	@Override
	public Collection<TagData> getEnergyTypes(byte side) {
		if (side == SIDE_INSIDE) return Arrays.asList(energyTypeEmitted, TD.Energy.HU, TD.Energy.CU);
		else if (side == mFacing) return super.getEnergyTypes(side);
		else return Arrays.asList(TD.Energy.HU, TD.Energy.CU);
	}

	@Override
	public boolean isEnergyAcceptingFrom(TagData energyType, byte side, boolean theoretical) {
		return side != mFacing && isEnergyType(energyType, side, false);
	}

	@Override public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {return Long.MAX_VALUE - storedInputEnergy;}
	@Override public long getEnergySizeInputMin(TagData energyType, byte side) {return 1;}
	@Override public long getEnergySizeInputRecommended(TagData energyType, byte side) {return 8;}
	@Override public long getEnergySizeInputMax(TagData energyType, byte side) {return Long.MAX_VALUE;}
	@Override public long getEnergySizeOutputMin(TagData energyType, byte side) { return 0; }

	@Override
	public long doInject(TagData energyType, byte side, long size, long amount, boolean doInject) {
		if (doInject) {
			long total = Math.abs(amount * size);
			if (energyType == TD.Energy.CU) storedInputEnergy -= total;
			else storedInputEnergy += total;
		}
		return amount;
	}

	@Override
	public int getRenderPasses2(Block block, boolean[] shouldSideBeRendered) {
		return super.getRenderPasses2(block, shouldSideBeRendered) + 8;
	}

	@Override
	public boolean setBlockBounds2(Block block, int renderPass, boolean[] shouldSideBeRendered) {
		return switch (renderPass - super.getRenderPasses2(block, shouldSideBeRendered)) {
			// middle pipes
			case 0 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[5], PX_N[3], PX_P[1], PX_P[7], PX_N[2], PX_N[1]));
			case 1 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[7], PX_N[3], PX_P[1], PX_N[5], PX_N[2], PX_N[1]));
			// top pipes
			case 2 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[4], PX_N[3], PX_P[1], PX_N[2], PX_N[1], PX_N[1]));
			case 3 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[1], PX_N[3], PX_P[5], PX_N[4], PX_N[1], PX_P[7]));
			// small vertical pipe
			case 4 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_P[1], PX_N[7], PX_P[5], PX_P[3], PX_N[3], PX_P[7]));
			// bottom pipes
			case 5 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[2], PX_P[1], PX_P[4], PX_N[0], PX_P[4], PX_P[6]));
			case 6 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[2], PX_P[1], PX_N[6], PX_N[0], PX_P[4], PX_N[4]));
			// display
			case 7 -> box(block, Geometry.rotateTwice(mFacing, mSecondFacing, PX_N[1], PX_P[4], PX_P[4], PX_N[0], PX_N[4], PX_N[4]));
			default -> super.setBlockBounds2(block, renderPass, shouldSideBeRendered);
		};
	}

	protected ITexture getThermometerTexture() {
		return BlockTextureMulti.get(
				BlockTextureDefault.get(thermometerBase, mRGBa),
				BlockTextureDefault.get(thermometerOverlay),
				BlockTextureDefault.get(thermometerFillTextures[thermometerIndex])
		);
	}

	@Override
	public boolean onTickCheck(long aTimer) {
		return thermometerIndex != oldThermometerIndex || super.onTickCheck(aTimer);
	}

	@Override
	public void onTickResetChecks(long aTimer, boolean aIsServerSide) {
		super.onTickResetChecks(aTimer, aIsServerSide);
		oldThermometerIndex = thermometerIndex;
	}

	@Override
	public void setVisualData(byte data) {
		activity.mState = (byte)(data & 3);
		thermometerIndex = (byte) ((data & 124) >>2);
	}

	@Override public byte getVisualData() {
		return  (byte)(activity.mState | (thermometerIndex <<2));
	}

	@Override
	public ITexture getTexture2(Block block, int renderPass, byte side, boolean[] shouldSideBeRendered) {
		return switch (renderPass - super.getRenderPasses2(block, shouldSideBeRendered)) {
			case 7 -> {
				if (side == mFacing || side == OPOS[mFacing] || side == mSecondFacing || side == OPOS[mSecondFacing]) {
					yield super.getTexture2(block, renderPass, side, shouldSideBeRendered);
				} else {
					yield getThermometerTexture();
				}
			}
			default -> super.getTexture2(block, renderPass, side, shouldSideBeRendered);
		};
	}

	public static IIconContainer
			thermometerBase    = new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/colored/thermometer"),
			thermometerOverlay = new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/overlay/thermometer");
	public static IIconContainer[] thermometerFillTextures = new IIconContainer[20];

	static {
		for (int i = 0; i < thermometerFillTextures.length; i++) {
			thermometerFillTextures[i] = new Textures.BlockIcons.CustomIcon("machines/generators/engine_diesel/thermometer_scale/" + (i < 10 ? "0" : "") + i);
		}
	}
}
