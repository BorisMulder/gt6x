package org.altadoon.gt6x.common.recipe;

import gregapi.data.FL;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.util.ST;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.utils.OD_Utils;

import java.util.ArrayList;
import java.util.List;

import static gregapi.data.CS.*;

public class RecipeBuilder {

	List<RecipeBuilderInternal> builderList = new ArrayList<>();

	public static RecipeBuilder create(Recipe.RecipeMap recipeMap){
		return new RecipeBuilder(recipeMap);
	}
	public RecipeBuilder(Recipe.RecipeMap aRecipeMap) {
		RecipeBuilderInternal builder = new RecipeBuilderInternal(aRecipeMap);
		builderList.add(builder);
	}

	/**
	 * @param aStacks an OreDictionary List of Items
	 *                Warning: Outputs do not inTake OreDictionary
	 */
	public RecipeBuilder item(List<ItemStack> aStacks, boolean isInput, int aChance){
		if(aStacks.isEmpty()) return this;
		List<RecipeBuilderInternal> aList = new ArrayList<>();
		for(var s:aStacks) {
			builderList.forEach(builder -> {
					if (isInput) aList.add(new RecipeBuilderInternal(builder).input(s));
					if (!isInput && aChance > -1) aList.add(new RecipeBuilderInternal(builder).output(s));
					if (!isInput && aChance < 0) aList.add(new RecipeBuilderInternal(builder).chancedOutput(s,aChance));
			});
		}
		builderList.clear();
		builderList.addAll(aList);
		return this;
	}

	public RecipeBuilder item(List<ItemStack> aStacks, boolean isInput){
		return item(aStacks,isInput,-1);
	}

	public RecipeBuilder item(String aOreDictionary,int aAmount, boolean isInput, int aChances){
		return item(OD_Utils.getOres(aOreDictionary,aAmount,false),isInput,aChances);
	}

	public RecipeBuilder item(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount, boolean isInput,boolean aOreDict,int aChance){
		if(aOreDict) return item(OD_Utils.getOres(aPrefix,aMaterial,aAmount,false),isInput,aChance);
		return item(aPrefix.mat(aMaterial,aAmount),isInput,aChance);
	}

	/**
	 * @param aStacks an OreDictionary List of Items
	 */
	public RecipeBuilder input(List<ItemStack> aStacks){
		return item(aStacks,true);
	}

	/**
	 * @param aStacks an OreDictionary List of Items
	 */
	@Deprecated()
	public RecipeBuilder output(List<ItemStack> aStacks){
		return item(aStacks,false);
	}

	public RecipeBuilder item(ItemStack aStack,boolean isInput,int aChance){
		builderList.forEach(builder->builder.item(aStack,isInput,aChance));
		return this;
	}
	public RecipeBuilder item(ItemStack aStack,boolean isInput){
		return item(aStack,isInput,-1);
	}

	public RecipeBuilder input(ItemStack aStack){
		return item(aStack,true);
	}

	public RecipeBuilder output(ItemStack aStack){
		return item(aStack,false);
	}

	public RecipeBuilder input(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount){
		return item(aPrefix,aMaterial,aAmount,true,true,-1);
	}

	//TODO: fixone item sometimes randomally get stackSize of 1 while rest are correct
	public RecipeBuilder output(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount){
		return item(aPrefix,aMaterial,aAmount,false,false,-1);
	}

	public RecipeBuilder input(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount,boolean aUseOreDict){
		return item(aPrefix,aMaterial,aAmount,true,aUseOreDict,-1);
	}

//	@Deprecated()
//	public RecipeBuilder output(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount,boolean aUseOreDict){
//		return item(aPrefix,aMaterial,aAmount,false,aUseOreDict,-1);
//	}

	public RecipeBuilder input(String aOreDictionary,int aAmount){
		return item(aOreDictionary,aAmount,true,-1);
	}
	@Deprecated()
	public RecipeBuilder output(String aOreDictionary,int aAmount){
		return item(aOreDictionary,aAmount,false,-1);
	}
	@Deprecated()
	public RecipeBuilder chancedOutput(String aOreDictionary,int aAmount, int aChances){
		return item(aOreDictionary,aAmount,false,aChances);
	}

	/**
	 * @param aStacks oreDict Output, this is one item in a recipe!
	 * @param aChance anything less than 0 is considered 100%
	 */
	@Deprecated()
	public RecipeBuilder chancedOutput(List<ItemStack> aStacks, int aChance){
		return item(aStacks,false,aChance);
	}

	public RecipeBuilder chancedOutput(ItemStack aStack, int aChances){
		List<ItemStack> aStacks = new ArrayList<>();aStacks.add(aStack); return chancedOutput(aStacks,aChances);
	}

	public RecipeBuilder chancedOutput(OreDictPrefix aPrefix,OreDictMaterial aMaterial,int aAmount, int aChances){
		return item(aPrefix,aMaterial,aAmount,false,false,aChances);
	}

	public RecipeBuilder notConsumed(OreDictPrefix aPrefix,OreDictMaterial aMaterial){
		return input(aPrefix,aMaterial,0,false);
	}

	@Deprecated
	public RecipeBuilder notConsumed(String aOreDict){
		return input(aOreDict,0);
	}

	public RecipeBuilder notConsumed(Item aItem){
		return notConsumed(aItem,W);
	}

	public RecipeBuilder notConsumed(Item aItem,short aMeta){
		return input(ST.make(aItem,0,aMeta));
	}

	public RecipeBuilder notConsumed(ItemStack aStack){
		return input(ST.amount(0,aStack));
	}

	@Deprecated()
	public RecipeBuilder notConsumed(List<ItemStack> aStacks){
		aStacks.forEach(itemStack -> itemStack.stackSize=0);
		return input(aStacks);
	}

	public RecipeBuilder fluid(boolean aInput,FluidStack... aFluidStack){
		builderList.forEach(builder -> builder.fluid(aInput,aFluidStack));
		return this;
	}

	public RecipeBuilder inputFluid(FluidStack... aFluidStack){
		return fluid(true,aFluidStack);
	}

	public RecipeBuilder outputFluid(FluidStack... aFluidStack){
		return fluid(true,aFluidStack);
	}

	public RecipeBuilder inputFluid(Fluid aFluid, int aAmount){
		return fluid(true, FL.make(aFluid,aAmount));
	}

	public RecipeBuilder outputFluid(Fluid aFluid, int aAmount){
		return fluid(false, FL.make(aFluid,aAmount));
	}

	public RecipeBuilder circuit(int aCircuit){
		builderList.forEach(builder -> builder.circuit(aCircuit));
		return this;
	}

	public RecipeBuilder fake(){
		builderList.forEach(RecipeBuilderInternal::fake);
		return this;
	}

	public RecipeBuilder eut(int aEUt) {
		builderList.forEach(builder -> builder.eut(aEUt));
		return this;
	}

	public RecipeBuilder duration(int aDuration) {
		builderList.forEach(builder -> builder.duration(aDuration));
		return this;
	}

	/**
	 * Warning: We check for collision which basically removes oreDict Possibilities from outputs
	 * this is necessary because outputs cannot be different!
	 */
	public void build() {
		builderList.forEach(RecipeBuilderInternal::optimizedBuild);
	}

	final class RecipeBuilderInternal {
		Recipe.RecipeMap recipeMap;
		ItemStack[] mInputs,mOutputs;
		FluidStack[] mFluidInputs, mFluidOutputs;

		long[] mChances, mMaxChances;
		/** An Item that needs to be inside the Special Slot, like for example the Copy Slot inside the Printer. This is only useful for Fake Recipes in NEI, since findRecipe() and containsInput() don't give a shit about this Field. Lists are also possible. */
		Object mSpecialItems = null;
		/** Use this to just disable a specific Recipe, but the Configuration enables that already for every single Recipe. */
		public boolean mEnabled = T;
		/** If this Recipe is hidden from NEI */
		public boolean mHidden = F;
		/** If this Recipe is Fake and therefore doesn't get found by the findRecipe Function (It is still in the HashMaps, so that containsInput does return T on those fake Inputs) */
		public boolean mFakeRecipe = F;
		/** If this Recipe can be stored inside a Machine in order to make Recipe searching more Efficient by trying the previously used Recipe first. In case you have a Recipe Map overriding things and returning one time use Recipes, you have to set this to F. */
		public boolean mCanBeBuffered = T;
		/** If this Recipe needs the Output Slots to be completely empty. Needed in case you have randomised Outputs */
		public boolean mNeedsEmptyOutput = F;

		long mDuration, mEUt, mSpecialValue;

		public RecipeBuilderInternal(Recipe.RecipeMap recipeMap){
			this.recipeMap = recipeMap; mDuration=100; mEUt = 0;
			mInputs = new ItemStack[recipeMap.mInputItemsCount];
			mOutputs = new ItemStack[recipeMap.mOutputItemsCount];
			mFluidInputs = new FluidStack[recipeMap.mInputFluidCount];
			mFluidOutputs = new FluidStack[recipeMap.mOutputFluidCount];
			mChances = new long[mOutputs.length];
		}

		public RecipeBuilderInternal(RecipeBuilderInternal aCopy){
			recipeMap=aCopy.recipeMap;
			mDuration=aCopy.mDuration;
			mChances=aCopy.mChances;
			mMaxChances=aCopy.mMaxChances;
			mEUt=aCopy.mEUt;
			mInputs=aCopy.mInputs.clone();
			mOutputs= aCopy.mOutputs.clone();
			mFluidInputs=aCopy.mFluidInputs.clone();
			mFluidOutputs= aCopy.mFluidOutputs.clone();
			mSpecialItems=aCopy.mSpecialItems;
			mSpecialValue=aCopy.mSpecialValue;
			mFakeRecipe=aCopy.mFakeRecipe;
			mEnabled=aCopy.mEnabled;
			mHidden=aCopy.mHidden;
			mCanBeBuffered=aCopy.mCanBeBuffered;
			mNeedsEmptyOutput=aCopy.mNeedsEmptyOutput;
		}

		public RecipeBuilderInternal eut(int aEUt){
			mEUt=aEUt;
			return this;
		}

		public RecipeBuilderInternal duration(int aDuration){
			mDuration=aDuration;
			return this;
		}

		/* INPUTS */

		/**
		 *              puts items one by one, first item goes on next availble index, dose nothing if it excedes maximum numbers of items allowed
		 */
		public RecipeBuilderInternal input(ItemStack... aItemStack){
			return item(true,aItemStack);
		}

		public RecipeBuilderInternal output(ItemStack... aItemStack){
			return item(false,aItemStack);
		}

		public RecipeBuilderInternal input(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount){
			return item(aPrefix,aMaterial,aAmount,true);
		}

		public RecipeBuilderInternal output(OreDictPrefix aPrefix, OreDictMaterial aMaterial, int aAmount){
			return item(aPrefix,aMaterial,aAmount,false);
		}

		public RecipeBuilderInternal item(boolean aInput, ItemStack... aItemStack){
			for (int i = 0; i < aItemStack.length; i++) {
				if(aInput)
					for (int j = 0; j < mInputs.length; j++) {
						if(mInputs[j]==null) {
							mInputs[j] = aItemStack[i];
							break;
						}
					}
				else {
					for (int j = 0; j < mOutputs.length; j++) {
						if(mOutputs[j]==null) {
							mOutputs[j] = aItemStack[i];
							break;
						}
					}
				}
			}
			return this;
		}

		public RecipeBuilderInternal item(ItemStack aStack, boolean aInput ,int aChance){
			if(!aInput){
				for (int j = 0; j < mOutputs.length; j++) {
					if(mOutputs[j]==null) {
						mOutputs[j] = aStack;
						mChances[j]=aChance;
						break;
					}
				}
				return this;
			}
			return item(aInput,aStack);
		}

		public RecipeBuilderInternal item(ItemStack aStack, boolean aInput){
			return item(aStack,aInput,-1);
		}

		public RecipeBuilderInternal item(OreDictPrefix aPrefix,OreDictMaterial aMaterial,int aAmount, boolean isInput){
			return item(aPrefix.mat(aMaterial,aAmount),isInput);
		}

		public RecipeBuilderInternal item(OreDictPrefix aPrefix,OreDictMaterial aMaterial,int aAmount, boolean isInput,int aChance){
			return item(aPrefix.mat(aMaterial,aAmount),isInput,aChance);
		}

		public RecipeBuilderInternal chancedOutput(ItemStack aStack, int aChance){
			return item(aStack,false,aChance);
		}



		public RecipeBuilderInternal fluid(boolean aInput, FluidStack... aFluidStack){
			for (int i = 0; i < aFluidStack.length; i++) {
				if(aInput)
					for (int j = 0; j < mFluidInputs.length; j++) {
						if(mFluidInputs[j]==null) {
							mFluidInputs[j] = aFluidStack[i];
							break;
						}
					}
				else {
					for (int j = 0; j < mFluidOutputs.length; j++) {
						if(mFluidOutputs[j]==null) {
							mFluidOutputs[j] = aFluidStack[i];
							break;
						}
					}
				}
			}
			return this;
		}

		public RecipeBuilderInternal inputFluid(FluidStack... aFluidStacks){
			return fluid(true,aFluidStacks);
		}

		public RecipeBuilderInternal outputFluid(FluidStack... aFluidStacks){
			return fluid(false,aFluidStacks);
		}

		public RecipeBuilderInternal circuit(int aCircuit){
			input(ST.tag(aCircuit));
			return this;
		}

		public RecipeBuilderInternal fake(){
			mFakeRecipe=true;
			return this;
		}

		public RecipeBuilderInternal hide(){
			mHidden=true;
			return this;
		}

		/* BUILD BLOCK */
		public void build(boolean aOptimize, boolean aUnification){
			var ret = new Recipe(aOptimize,aUnification,mInputs,mOutputs,mSpecialItems,mChances,mFluidInputs,mFluidOutputs,mDuration,mEUt,mSpecialValue);
			recipeMap.addRecipe(ret,true,mFakeRecipe,mHidden,true);
		}

		public void build(){
			build(false,false);
		}

		public void optimizedBuild(){
			build(true,true);
		}
	}

}
