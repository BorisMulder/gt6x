package org.altadoon.gt6x.features.crucibles;

import gregapi.code.ArrayListNoNulls;
import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.data.TD;
import gregapi.fluid.FluidTankGT;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.tileentity.base.TileEntityBase07Paintable;
import gregapi.tileentity.machines.ITileEntityMold;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.util.WD;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.features.crucibles.recipes.MultiSmeltingRecipe;
import org.altadoon.gt6x.features.crucibles.recipes.RecipeMapMultiSmelting;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static gregapi.data.CS.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class CrucibleInterior {
	public long storedEnergy = 0;
	public long currentTemperature = DEF_ENV_TEMP, oldTemperature = 0;
	public long maxTemperature = 1000;
	public boolean acidProof = false, gasProof = false;
	public int gasRange, flameRange;

	/** Should remain sorted from least to most dense (depending on temperature). In case of gases, sorted by atomic weight. */
	public List<OreDictMaterialStack> content = new ArrayListNoNulls<>();
	protected List<RecipeMapMultiSmelting> multiSmeltingRecipes;
	protected FluidTankGT airTank = null;
	public final int maxContentLength;
	public final long maxTotalUnits;
	public final long hullUnits;
	public double hullWeight = 0;
	public final long kgPerEnergy;
	public final long rainFactor;
	protected static final byte COOLDOWN_MAX = 20;
	protected byte cooldown = COOLDOWN_MAX;
	/// Surface area (m2)
	public final long surfaceArea;
	/// heat transfer coefficient (W/m2.K) where W = HU/t
	public static final long HEAT_TRANSFER_COEFFICIENT = U480;

	public CrucibleInterior(
		int maxContentLength,
		long maxTotalUnits,
		int hullUnits,
		long kgPerEnergy,
		long surfaceArea,
		long rainFactor,
		int gasRange,
		int flameRange,
		RecipeMapMultiSmelting... recipes
	) {
		this.maxContentLength = maxContentLength;
		this.maxTotalUnits = maxTotalUnits;
		this.hullUnits = hullUnits;
		this.kgPerEnergy = kgPerEnergy;
		this.rainFactor = rainFactor;
		this.surfaceArea = surfaceArea;
		this.gasRange = gasRange;
		this.flameRange = flameRange;
		this.multiSmeltingRecipes = new ArrayListNoNulls<>(false, recipes);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		readFromNBT(nbt, null);
	}

	public void readFromNBT(NBTTagCompound nbt, FluidTankGT airTank) {
		this.airTank = airTank;
		if (nbt.hasKey(NBT_ACIDPROOF)) acidProof = nbt.getBoolean(NBT_ACIDPROOF);
		if (nbt.hasKey(NBT_GASPROOF)) gasProof = nbt.getBoolean(NBT_GASPROOF);
		if (nbt.hasKey(NBT_TEMPERATURE)) currentTemperature = nbt.getLong(NBT_TEMPERATURE);
		if (nbt.hasKey(NBT_TEMPERATURE+".max")) maxTemperature = nbt.getLong(NBT_TEMPERATURE+".max");
		if (nbt.hasKey(NBT_TEMPERATURE+".old")) oldTemperature = nbt.getLong(NBT_TEMPERATURE+".old");
		if (nbt.hasKey(NBT_MATERIAL)) {
			OreDictMaterial mat = OreDictMaterial.get(nbt.getString(NBT_MATERIAL));
			this.hullWeight = mat.getWeight(this.hullUnits * U);

			if (!nbt.hasKey(NBT_TEMPERATURE+".max"))
				this.maxTemperature = mat.mMeltingPoint;
		}
		content = OreDictMaterialStack.loadList(NBT_MATERIALS, nbt);
		storedEnergy = nbt.getLong(NBT_ENERGY);
	}

	public void writeToNBT(NBTTagCompound aNBT) {
		UT.NBT.setNumber(aNBT, NBT_ENERGY, storedEnergy);
		UT.NBT.setNumber(aNBT, NBT_TEMPERATURE, currentTemperature);
		UT.NBT.setNumber(aNBT, NBT_TEMPERATURE+".old", oldTemperature);
		OreDictMaterialStack.saveList(NBT_MATERIALS, aNBT, content);
	}

	private static class MaterialDensityComparator implements Comparator<OreDictMaterialStack> {
		private final long temperature;

		public MaterialDensityComparator(long temperature) {
			this.temperature = temperature;
		}

		@Override
		public int compare(OreDictMaterialStack o1, OreDictMaterialStack o2) {
			if (o1.mMaterial.mID == o2.mMaterial.mID)
				return 0;

			if (getState(o1, temperature) != getState(o2, temperature)) {
				return getState(o2, temperature).compareTo(getState(o1, temperature));
			}

			if (getState(o1, temperature) == MaterialState.GAS_OR_PLASMA) {
				return Double.compare(o1.mMaterial.getMass(), o2.mMaterial.getMass());
			}

			return Double.compare(o1.mMaterial.mGramPerCubicCentimeter, o2.mMaterial.mGramPerCubicCentimeter);
		}
	}

	public enum MaterialState {
		ANY, SOLID, LIQUID, GAS_OR_PLASMA
	}

	public static class CrucibleTickResult {
		public long totalUnits = 0;
		public boolean updateClientData = false;
		public boolean meltdown = false;
		public boolean exothermic = false;
	}

	public CrucibleTickResult onTick(TileEntityBase07Paintable exterior) {
		World world = exterior.getWorld();

		if (rainFactor != 0 && SERVER_TIME % 600 == 10 && world.isRaining() && exterior.getRainOffset(0, 1, 0)) {
			BiomeGenBase biome = exterior.getBiome();
			if (biome.rainfall > 0 && biome.temperature >= 0.2) {
				addMaterialStacks(Collections.singletonList(OM.stack(MT.Water, rainFactor *
						(long) Math.max(1, biome.rainfall * 100) * (world.isThundering() ? 2 : 1))), DEF_ENV_TEMP);
			}
		}

		if (!exterior.slotHas(0)) exterior.slot(0,
				WD.suck(world, exterior.xCoord+PX_P[2], exterior.yCoord+PX_P[2], exterior.zCoord+PX_P[2], PX_N[4], 1, PX_N[4]));

		long envTemperature = WD.envTemp(world, exterior.xCoord, exterior.yCoord, exterior.zCoord);
		ItemStack inputItem = exterior.slot(0);

		CrucibleTickResult result = new CrucibleTickResult();

		long contentHash = content.hashCode();

		// Add items to the crucible
		if (ST.valid(inputItem)) {
			OreDictItemData itemData = OM.anydata_(inputItem);
			if (itemData == null) {
				exterior.slotTrash(0);
				UT.Sounds.send(SFX.MC_FIZZ, exterior);
			} else {
				long oreMultiplier = 0;
				if (itemData.mPrefix != null) {
					 if (itemData.mPrefix == OP.blockRaw) oreMultiplier = 9;
					else if (itemData.mPrefix == OP.crateGtRaw) oreMultiplier = 16;
					else if (itemData.mPrefix == OP.crateGt64Raw) oreMultiplier = 64;
					else if (itemData.mPrefix == OP.oreRaw || itemData.mPrefix.contains(TD.Prefix.STANDARD_ORE))
						oreMultiplier = 1;
					else if (itemData.mPrefix.contains(TD.Prefix.DENSE_ORE)) oreMultiplier = 2;
				}

				List<OreDictMaterialStack> list = new ArrayListNoNulls<>();
				if (oreMultiplier != 0) {
					list.add(OM.stack(
						itemData.mMaterial.mMaterial.mTargetCrushing.mMaterial,
						itemData.mMaterial.mMaterial.mTargetCrushing.mAmount * oreMultiplier
					));
				} else {
					for (OreDictMaterialStack material : itemData.getAllMaterialStacks())
						if (material.mAmount > 0) list.add(material.clone());
				}
				if (!list.isEmpty() && addMaterialStacks(list, envTemperature)) {
					exterior.decrStackSize(0, 1);
				}
			}
		}

		// Try to convert stuff
		Set<OreDictMaterial> alreadyCheckedAlloys = new HashSetNoNulls<>();
		Set<MultiSmeltingRecipe> alreadyCheckedMultiSmeltingRecipes = new HashSetNoNulls<>();

		OreDictMaterial preferredAlloy = null;
		IOreDictConfigurationComponent preferredRecipe = null;
		MultiSmeltingRecipe preferredMultiSmeltingRecipe = null;
		RecipeMapMultiSmelting preferredRecipeMap = null;
		long maxConversions = 0;
		int recipeLength = 0;
		boolean hasNewContent = (contentHash != content.hashCode());

		for (OreDictMaterialStack stack : content) {
			// check multi-smelting recipes
			for (RecipeMapMultiSmelting map : multiSmeltingRecipes) {
				ArrayListNoNulls<MultiSmeltingRecipe> targetRecipes = map.SmeltsInto.get(stack.mMaterial);
				if (targetRecipes != null) {
					for (MultiSmeltingRecipe recipe : targetRecipes) {
						if (alreadyCheckedMultiSmeltingRecipes.add(recipe) && currentTemperature >= recipe.smeltingTemperature) {
							List<OreDictMaterialStack> neededStuff = new ArrayListNoNulls<>();
							for (OreDictMaterialStack ingredient : recipe.ingredients.getUndividedComponents()) {
								neededStuff.add(OM.stack(ingredient.mMaterial, Math.max(1, ingredient.mAmount / U)));
							}

							if (!neededStuff.isEmpty()) {
								boolean ingredientNotFound = false;
								long nConversions = Long.MAX_VALUE;
								for (OreDictMaterialStack needed : neededStuff) {
									if (airTank != null && (needed.mMaterial == MT.Air || needed.mMaterial == MT.O)) {
										FluidStack gas = airTank.get();
										OreDictMaterialStack tankMaterial = OreDictMaterial.FLUID_MAP.get(gas.getFluid().getName());
										if (tankMaterial.mMaterial == needed.mMaterial) {
											nConversions = Math.min(nConversions, (gas.amount * U / tankMaterial.mAmount) / needed.mAmount);
											continue;
										}
									}

									ingredientNotFound = true;
									for (OreDictMaterialStack contained : content) {
										if (contained.mMaterial == needed.mMaterial) {
											nConversions = Math.min(nConversions, contained.mAmount / needed.mAmount);
											ingredientNotFound = false;
											break;
										}
									}
									if (ingredientNotFound) break;
								}

								// prefer the conversion with the largest number of different ingredients, second, most conversions
								if (!ingredientNotFound && nConversions > 0) {
									if (preferredMultiSmeltingRecipe == null ||
											recipe.ingredients.getUndividedComponents().size() > recipeLength ||
											(recipe.ingredients.getUndividedComponents().size() == recipeLength &&
											recipe.ingredients.getCommonDivider() > maxConversions * preferredMultiSmeltingRecipe.ingredients.getCommonDivider())) {
										maxConversions = nConversions;
										recipeLength = recipe.ingredients.getUndividedComponents().size();
										preferredMultiSmeltingRecipe = recipe;
										preferredRecipeMap = map;
									}
								}
							}
						}
					}
				}
			}

			// check normal smelting recipes if no multi-smelting recipe is present
			if (preferredMultiSmeltingRecipe == null && currentTemperature >= stack.mMaterial.mMeltingPoint) {
				for (OreDictMaterial alloy : stack.mMaterial.mAlloyComponentReferences) if (alreadyCheckedAlloys.add(alloy) && currentTemperature >= alloy.mMeltingPoint) {
					for (IOreDictConfigurationComponent alloyRecipe : alloy.mAlloyCreationRecipes) {
						List<OreDictMaterialStack> neededStuff = new ArrayListNoNulls<>();
						for (OreDictMaterialStack tComponent : alloyRecipe.getUndividedComponents()) {
							neededStuff.add(OM.stack(tComponent.mMaterial, Math.max(1, tComponent.mAmount / U)));
						}

						if (!neededStuff.isEmpty()) {
							int nonMolten = 0;

							boolean cancel = false;
							long nConversions = Long.MAX_VALUE;
							for (OreDictMaterialStack needed : neededStuff) {
								if (currentTemperature < needed.mMaterial.mMeltingPoint) nonMolten++;

								cancel = true;
								for (OreDictMaterialStack contained : content) {
									if (contained.mMaterial == needed.mMaterial) {
										nConversions = Math.min(nConversions, contained.mAmount / needed.mAmount);
										cancel = false;
										break;
									}
								}
								if (cancel) break;
							}

							if (!cancel && nonMolten <= 1 && nConversions > 0) {
								if (preferredAlloy == null || preferredRecipe == null || nConversions * alloyRecipe.getCommonDivider() > maxConversions * preferredRecipe.getCommonDivider()) {
									maxConversions = nConversions;
									preferredRecipe = alloyRecipe;
									preferredAlloy = alloy;
								}
							}
						}
					}
				}
			}
		}

		boolean contentChanged = false;

		if (preferredMultiSmeltingRecipe != null) {
			if (preferredMultiSmeltingRecipe.exothermic) {
				// execute the recipe one tick at a time.
				// here, U conversions means the whole recipe, and 1/U is the smallest unit.
				maxConversions = Math.min(maxConversions, U / preferredRecipeMap.ticksPerFuelUnit / preferredMultiSmeltingRecipe.fuelUnits);
				storedEnergy += preferredRecipeMap.exothermicEnergyPerTick;
				result.exothermic = true;
			}

			for (OreDictMaterialStack ingredient : preferredMultiSmeltingRecipe.ingredients.getUndividedComponents()) {
				if (airTank != null && (ingredient.mMaterial == MT.O || ingredient.mMaterial == MT.Air)) {
					long unitsToDrain = UT.Code.units_(maxConversions, U, ingredient.mAmount, true);
					FluidStack ingredientFluid = ingredient.mMaterial.gas(unitsToDrain, true);
					airTank.drain(ingredientFluid.amount);
				}

				for (OreDictMaterialStack stack : content) {
					if (stack.mMaterial == ingredient.mMaterial) {
						stack.mAmount -= UT.Code.units_(maxConversions, U, ingredient.mAmount, true);
						break;
					}
				}
			}

			for (OreDictMaterialStack stack : preferredMultiSmeltingRecipe.results.getUndividedComponents()) {
				OM.stack(stack.mMaterial, stack.mAmount / U * maxConversions).addToList(content);
			}

			contentChanged = true;
		} else if (preferredAlloy != null && preferredRecipe != null) {
			for (OreDictMaterialStack tComponent : preferredRecipe.getUndividedComponents()) {
				for (OreDictMaterialStack tContent : content) {
					if (tContent.mMaterial == tComponent.mMaterial) {
						tContent.mAmount -= UT.Code.units_(maxConversions, U, tComponent.mAmount, true);
						break;
					}
				}
			}
			OM.stack(preferredAlloy, preferredRecipe.getCommonDivider() * maxConversions).addToList(content);
			contentChanged = true;
		}

		List<OreDictMaterialStack> toBeAdded = new ArrayListNoNulls<>();
		for (int i = 0; i < content.size(); i++) {
			OreDictMaterialStack stack = content.get(i);
			if (stack == null || stack.mMaterial == MT.NULL || (stack.mMaterial == MT.Air && !gasProof) || stack.mAmount <= 0) {
				GarbageGT.trash(content.remove(i--));
				contentChanged = true;
			} else if (!gasProof && hasState(stack, MaterialState.GAS_OR_PLASMA)) {
				GarbageGT.trash(content.remove(i--));
				UT.Sounds.send(SFX.MC_FIZZ, exterior);
				if (stack.mMaterial.mBoilingPoint >=  320) damageEntities(exterior);
				if (stack.mMaterial.mBoilingPoint >= 2000) causeFire(exterior, stack.mAmount);
			} else if (currentTemperature > C + 40 &&
					stack.mMaterial.contains(TD.Properties.FLAMMABLE) &&
					!stack.mMaterial.containsAny(TD.Properties.UNBURNABLE, TD.Processing.MELTING))
			{
				GarbageGT.trash(content.remove(i));
				UT.Sounds.send(SFX.MC_FIZZ, exterior);
				if (stack.mMaterial.contains(TD.Properties.EXPLOSIVE))
					exterior.explode(UT.Code.scale(stack.mAmount, maxTotalUnits*U, 8, false));
				return result;
			} else if (!acidProof && stack.mMaterial.contains(TD.Properties.ACID)) {
				GarbageGT.trash(content);
				GarbageGT.trash(toBeAdded);
				UT.Sounds.send(SFX.MC_FIZZ, exterior);
				exterior.setToAir();
				return result;
			} else if (currentTemperature >= stack.mMaterial.mMeltingPoint && (oldTemperature <  stack.mMaterial.mMeltingPoint || hasNewContent)) {
				content.remove(i--);
				OM.stack(stack.mMaterial.mTargetSmelting.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSmelting.mAmount, false)).addToList(toBeAdded);
				contentChanged = true;
			} else if (currentTemperature <  stack.mMaterial.mMeltingPoint && (oldTemperature >= stack.mMaterial.mMeltingPoint || hasNewContent)) {
				content.remove(i--);
				OM.stack(stack.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSolidifying.mAmount, false)).addToList(toBeAdded);
				contentChanged = true;
			}
		}
		for (int i = 0; i < toBeAdded.size(); i++) {
			OreDictMaterialStack stack = toBeAdded.get(i);
			if (stack == null || stack.mMaterial == MT.NULL || stack.mAmount <= 0) {
				GarbageGT.trash(toBeAdded.remove(i--));
			} else {
				stack.addToList(content);
			}
		}

		// Update temperature
		oldTemperature = currentTemperature;
		long dT = currentTemperature - envTemperature;
		// Q=UAdT
		storedEnergy -= (HEAT_TRANSFER_COEFFICIENT * surfaceArea * dT) / U;

		double totalWeight = hullWeight;
		for (OreDictMaterialStack stack : content) {
			totalWeight += stack.weight();
			result.totalUnits += stack.mAmount;
		}

		long requiredEnergy = 1 + (long)(totalWeight / kgPerEnergy), conversions = storedEnergy / requiredEnergy;

		if (conversions != 0) {
			storedEnergy -= conversions * requiredEnergy;
			currentTemperature += conversions;
			cooldown = COOLDOWN_MAX;
		} else if (--cooldown == 0) {
			if (currentTemperature > envTemperature)
				currentTemperature--;
			cooldown = COOLDOWN_MAX;
		}

		currentTemperature = Math.max(currentTemperature, Math.min(200, envTemperature));

		content.sort(new MaterialDensityComparator(currentTemperature));

		// Check for meltdown
		if (currentTemperature > maxTemperature) {
			UT.Sounds.send(SFX.MC_FIZZ, exterior);
			if (currentTemperature > 320) damageEntities(exterior);
			causeFire(exterior, currentTemperature / 25);
			currentTemperature = maxTemperature;
			GarbageGT.trash(content);
			contentChanged = true;
			result.meltdown = true;
		}

		if (oldTemperature != currentTemperature || hasNewContent || contentChanged) {
			result.updateClientData = true;
		}

		return result;
	}

	public void causeFire(TileEntityBase07Paintable exterior, long amount) {
		for (int j = 0, k = Math.max(1, UT.Code.bindInt((9 * amount) / U)); j < k; j++)
			WD.fire(exterior.getWorld(),
					exterior.xCoord-flameRange+exterior.rng(2*flameRange+1),
					exterior.yCoord-1         +exterior.rng(2+flameRange  ),
					exterior.zCoord-flameRange+exterior.rng(2*flameRange+1),
					exterior.rng(3) != 0
			);
	}

	@SuppressWarnings("unchecked")
	public void damageEntities(TileEntityBase07Paintable exterior) {
		try {
			for (EntityLivingBase tLiving : (List<EntityLivingBase>)exterior.getWorld().getEntitiesWithinAABB(
					EntityLivingBase.class,
					exterior.box(-gasRange, -1, -gasRange, gasRange+1, gasRange+1, gasRange+1)
			)) {
				UT.Entities.applyTemperatureDamage(tLiving, currentTemperature, 2);
			}
		} catch(Throwable e) {
			e.printStackTrace(ERR);
		}
	}

	public void heat(long amount) {
		storedEnergy += amount;
	}

	public void cool(long amount) {
		storedEnergy -= amount;
	}

	public long injectEnergy(TagData energyType, long size, long amount, boolean doInject, boolean hasAirAbove) {
		if (doInject) {
			if (size * amount <= 0) return 0;
			cooldown = COOLDOWN_MAX;
			if (energyType.equals(TD.Energy.KU)) {
				if (size * amount > 0 && hasAirAbove)
					addMaterialStacks(new ArrayListNoNulls<>(F, OM.stack(size * amount * U1000, MT.Air)), currentTemperature);
			} else if (energyType.equals(TD.Energy.CU)) {
				cool(size * amount);
			} else {
				heat(size * amount);
			}
		}
		return amount;
	}

	public double weight() {
		double weight = 0;
		for (OreDictMaterialStack stack : content) if (stack != null && stack.mMaterial != MT.NULL && currentTemperature < stack.mMaterial.mBoilingPoint)
			weight += stack.weight();
		return weight;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canAddNewStacks(List<OreDictMaterialStack> stacks) {
		// don't allow to add more units than maxTotalUnits
		if (OM.total(content)+OM.total(stacks) > maxTotalUnits * U) return false;

		// don't allow to add more than max amount stacks
		int amountNewStacks = 0;
		for (OreDictMaterialStack stack : stacks) {
			boolean found = false;

			for (OreDictMaterialStack cnt : content) {
				if (cnt.mMaterial == stack.mMaterial) {
					found = true;
					break;
				}
			}

			if (!found) amountNewStacks++;
		}
		return content.size() + amountNewStacks <= maxContentLength;
	}

	public long tryAddStack(OreDictMaterialStack stack, long temperature) {
		if (addMaterialStacks(Collections.singletonList(stack), temperature)) return stack.mAmount;
		if (stack.mAmount > U && addMaterialStacks(Collections.singletonList(OM.stack(stack.mMaterial, U)), temperature)) return U;
		return 0;
	}

	public boolean addMaterialStacks(List<OreDictMaterialStack> stacks, long temperature) {
		if (!canAddNewStacks(stacks)) return false;

		double crucibleWeight = weight()+hullWeight, stacksWeight = OM.weight(stacks);
		if (crucibleWeight+stacksWeight > 0) currentTemperature = temperature + (currentTemperature >temperature?+1:-1)*UT.Code.units(Math.abs(currentTemperature - temperature), (long)(crucibleWeight+stacksWeight), (long)crucibleWeight, false);
		for (OreDictMaterialStack stack : stacks) {
			if (currentTemperature >= stack.mMaterial.mMeltingPoint) {
				if (temperature <  stack.mMaterial.mMeltingPoint) {
					OM.stack(stack.mMaterial.mTargetSmelting.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSmelting.mAmount, false)).addToList(content);
				} else {
					stack.addToList(content);
				}
			} else {
				if (temperature >= stack.mMaterial.mMeltingPoint) {
					OM.stack(stack.mMaterial.mTargetSolidifying.mMaterial, UT.Code.units_(stack.mAmount, U, stack.mMaterial.mTargetSolidifying.mAmount, false)).addToList(content);
				} else {
					stack.addToList(content);
				}
			}
		}
		return true;
	}

	public int addFluid(FluidStack fluid, boolean doFill) {
		OreDictMaterialStack mat = OreDictMaterial.FLUID_MAP.get(fluid.getFluid().getName());
		if (mat == null) return 0;

		// Check if we already have this fluid, if so, don't add too much
		long unitsAvailable = UT.Code.units(fluid.amount, mat.mAmount, U, false);
		long unitsToAdd = Math.min(unitsAvailable, U*maxTotalUnits - OM.total(content));
		int fluidToAdd = (int)UT.Code.units(unitsToAdd, U, mat.mAmount, true);
		if (doFill && unitsToAdd > 0) {
			fluid.amount -= fluidToAdd;
			List<OreDictMaterialStack> list = Collections.singletonList(OM.stack(mat.mMaterial, unitsToAdd));
			addMaterialStacks(list, FL.temperature(fluid));
			LOG.debug("Adding {}L to interior", fluidToAdd);
		}
		return fluidToAdd;
	}

	public void addFluidFromTank(FluidTankGT tank) {
		if (!tank.has()) return;
		int amount = addFluid(tank.get(), true);
		tank.drain(amount);
	}

	public static class CrucibleContentStack {
		protected int index;
		protected OreDictMaterialStack stack;
		protected CrucibleInterior source;

		protected CrucibleContentStack(int index, OreDictMaterialStack stack, CrucibleInterior source) {
			this.index = index;
			this.stack = stack;
			this.source = source;
		}

		public OreDictMaterialStack toOMStack() {
			return stack;
		}

		public FluidStack toFluidStack() {
			FluidStack result = stack.mMaterial.fluid(source.currentTemperature, stack.mAmount, false);
			if (result == null || FL.Error.is(result) || result.amount <= 0 || FL.temperature(result) > source.currentTemperature) {
				// note: the temperature of the result can be higher than the melting point in some corner cases
				return null;
			}
			return result;
		}

		public void clear() {
			source.content.remove(index);
		}

		public void decreaseUnits(long amount) {
			amount = Math.min(amount, stack.mAmount);
			stack.mAmount -= amount;
			if (stack.mAmount <= 0) {
				source.content.remove(index);
			}
		}

		public void decreaseFluidAmount(long amount) {
			long fluidPerUnit;
			switch(source.getState(stack)) {
				case LIQUID -> fluidPerUnit = stack.mMaterial.mLiquid.amount;
				case GAS_OR_PLASMA -> fluidPerUnit = stack.mMaterial.mGas.amount;
				default -> { return; }
			}
			long units = UT.Code.units(amount, fluidPerUnit, U, true);
			decreaseUnits(units);
		}

		public MaterialState state() {
			return source.getState(stack);
		}

		public long convertToScrap(EntityPlayer player, boolean shovel) {
			OreDictMaterialStack outputStack = toOMStack();
			ItemStack output = OP.scrapGt.mat(outputStack.mMaterial, shovel ? UT.Code.bindStack(outputStack.mAmount / OP.scrapGt.mAmount) : 1);
			if (output == null || outputStack.mAmount < OP.scrapGt.mAmount) {
				clear();
				player.addExhaustion(0.1F);
				return 500;
			} else if (UT.Inventories.addStackToPlayerInventory(player, output)) {
				decreaseUnits(output.stackSize * OP.scrapGt.mAmount);
				player.addExhaustion(0.1F * output.stackSize);
				return 1000L * output.stackSize;
			}
			return 0;
		}
	}

	public CrucibleContentStack getStack(int index) {
		if (index >= content.size()) return null;
		return new CrucibleContentStack(index, content.get(index), this);
	}

	public CrucibleContentStack getStack(MaterialState state) {
		return getStack(false, false, state);
	}

	/**
	 * get the next stack in a given state from the content
	 * @param countFromBottom if true, search from bottom to top. If false, top to bottom and skip the bottom liquid.
	 * @param skipBottom if true, skip the bottom-most stack (useful for machines with two outputs)
	 * @param state the desired MaterialState
	 * @return a pair of the index in the content and the OreDictMaterialStack
	 */
	public CrucibleContentStack getStack(boolean countFromBottom, boolean skipBottom, MaterialState state) {
		if (countFromBottom) {
			for (int i = content.size() - 1; i >= 0; i--) {
				OreDictMaterialStack bottomStack = content.get(i);
				if (hasState(bottomStack, state)) {
					return new CrucibleContentStack(i, bottomStack, this);
				}
			}
		} else if (!skipBottom || content.size() > 1) {
			for (int i = 0; i < content.size() - (skipBottom ? 1 : 0); i++) {
				OreDictMaterialStack topStack = content.get(i);
				if (hasState(topStack, state)) {
					return new CrucibleContentStack(i, topStack, this);
				}
			}
		} else if (state == MaterialState.GAS_OR_PLASMA && !content.isEmpty()) {
			OreDictMaterialStack topStack = content.get(0);
			if (hasState(topStack, state)) return new CrucibleContentStack(0, topStack, this);
		}

		return null;
	}

	public CrucibleContentStack findStack(OreDictMaterial mat) {
		for (int i = 0; i < content.size(); i++) {
			OreDictMaterialStack stack = content.get(i);
			if (stack.mMaterial == mat) return new CrucibleContentStack(i, stack, this);
		}
		return null;
	}

	protected MaterialState getState(OreDictMaterialStack stack) {
		return getState(stack, currentTemperature);
	}

	public static MaterialState getState(OreDictMaterialStack stack, long temperature) {
		if (temperature < stack.mMaterial.mMeltingPoint) return MaterialState.SOLID;
		if (temperature < stack.mMaterial.mBoilingPoint) return MaterialState.LIQUID;
		return MaterialState.GAS_OR_PLASMA;
	}

	protected boolean hasState(OreDictMaterialStack stack, MaterialState state) {
		return state == MaterialState.ANY || getState(stack) == state;
	}

	public void trashAll() {
		GarbageGT.trash(content);
	}

	public int countStacks() {
		return content.size();
	}

	public boolean tryFillMold(ITileEntityMold mold, byte sideOfMold) {
		for (int i = 0; i < content.size(); i++) {
			CrucibleContentStack stack = getStack(i);
			if (stack.state() == MaterialState.LIQUID) {
				long amount = mold.fillMold(stack.toOMStack(), currentTemperature, sideOfMold);
				if (amount > 0) {
					stack.decreaseUnits(amount);
					return true;
				}
			}
		}
		return false;
	}

	public void onBlockActivated(EntityPlayer player, TileEntityBase07Paintable exterior) {
		ItemStack playerItem = player.getCurrentEquippedItem();

		if (playerItem == null && exterior.slotHas(0)) {
			player.inventory.setInventorySlotContents(player.inventory.currentItem, exterior.slotTake(0));
			UT.Entities.applyTemperatureDamage(player, currentTemperature, 1, 5.0F);
		}

		// See if the player holds a fluid container of which the content can be added to the crucible
		if (playerItem != null) {
			FluidStack playerFluid = FL.getFluid(ST.amount(1, playerItem), true);
			if (playerFluid != null) {
				if (!FL.gas(playerFluid) && !FL.plasma(playerFluid) && (!FL.acid(playerFluid) || acidProof)) {
					if (addFluid(playerFluid, false) == playerFluid.amount) {
						addFluid(playerFluid, true);
						ItemStack emptyContainer = ST.container(ST.amount(1, playerItem), true);
						playerItem.stackSize--;
						UT.Inventories.addStackToPlayerInventoryOrDrop(player, emptyContainer, true);
					}
				}
				return;
			}
		}

		CrucibleInterior.CrucibleContentStack content = getStack(CrucibleInterior.MaterialState.ANY);
		if (content == null) {
			return;
		}

		switch(content.state()) {
			case SOLID -> content.convertToScrap(player, false);
			case LIQUID -> { // try to fill empty liquid container
				if (playerItem == null) return;

				FluidStack fluidStack = content.toFluidStack();
				if (fluidStack == null) return;

				long originalAmount = fluidStack.amount;
				ItemStack filledContainer = FL.fill(fluidStack, ST.amount(1, playerItem), true, true, true, true);
				if (ST.valid(filledContainer)) {
					playerItem.stackSize--;
					content.decreaseFluidAmount(originalAmount - fluidStack.amount);
					UT.Inventories.addStackToPlayerInventoryOrDrop(player, filledContainer, true);
				}
			}
		}
	}

	public void handleEntityCollision(Entity entity, long envTemp) {
		if (UT.Entities.applyTemperatureDamage(entity, currentTemperature, 1, 10.0F) && currentTemperature > 320) {
			if (entity instanceof EntityLivingBase && !entity.isEntityAlive()) {
				OreDictMaterialStack stack = null;
				long temperature = envTemp;

				if (entity instanceof EntityVillager || entity instanceof EntityWitch) {
					stack = OM.stack(2*U, MT.SoylentGreen); temperature = C+37;
				} else if (entity instanceof EntitySnowman) {
					stack = OM.stack(4*U, MT.Snow); temperature = C-10;
				} else if (entity instanceof EntityIronGolem) {
					stack = OM.stack(4*U, MT.Fe);
				} else if (entity instanceof EntitySkeleton) {
					addMaterialStacks(new ArrayListNoNulls<>(false,
							OM.stack(U, ((EntitySkeleton)entity).getSkeletonType() == 1 ? MT.BoneWither : MT.Bone),
							((EntitySkeleton)entity).getSkeletonType() == 1 ? OM.stack(U, MT.Coal) : null),
							envTemp
					);
				} else if (entity instanceof EntityZombie) {
					stack = OM.stack(U, MT.MeatRotten);
				} else if (entity instanceof EntityCow || entity instanceof EntityHorse) {
					stack = OM.stack(3*U, MT.MeatRaw);
					temperature = C+37;
				} else if (entity instanceof EntityPig || entity instanceof EntitySheep || entity instanceof EntityWolf || entity instanceof EntitySquid) {
					stack = OM.stack(2*U, MT.MeatRaw);
					temperature = C+37;
				} else if (entity instanceof EntityChicken || entity instanceof EntityOcelot || entity instanceof EntitySpider || entity instanceof EntitySilverfish) {
					stack = OM.stack(U, MT.MeatRaw);
					temperature = C+37;
				} else if (entity instanceof EntityCreeper) {
					stack = OM.stack(U, MT.Gunpowder);
				} else if (entity instanceof EntityEnderman) {
					stack = OM.stack(U, MT.EnderPearl);
				} else if (entity instanceof EntityPlayer && (
						"GregoriusT".equalsIgnoreCase(entity.getCommandSenderName()) ||
						"Boris201"  .equalsIgnoreCase(entity.getCommandSenderName())
				)) {
					stack = OM.stack(U, MT.Tc);
				}

				if (stack != null) {
					addMaterialStacks(new ArrayListNoNulls<>(false, stack), temperature);
				}
			}
		}
	}

	public void printContent(List<String> chat) {
		chat.add("=== This machine contains ===");
		for (OreDictMaterialStack stack : content) {
			MaterialState state = getState(stack);
			chat.add((double)stack.mAmount / U + " Units of " + stack.mMaterial.mNameLocal + " (" + (state == MaterialState.GAS_OR_PLASMA ? "Gaseous" : state) + ")");
		}
	}
}
