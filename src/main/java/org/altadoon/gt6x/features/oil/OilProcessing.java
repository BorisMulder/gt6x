package org.altadoon.gt6x.features.oil;

import gregapi.data.*;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;

public class OilProcessing extends GT6XFeature {
    public ItemStack pvcCan = null;
    public ItemStack ptfeCan = null;

    public Recipe.RecipeMap hydroCracking = null;

    @Override
    public String name() {
        return "OilProcessing";
    }

    @Override
    public void preInit() {
        addRecipeMaps();
    }

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void postInit() {
        addRecipes();
    }

    @Override
    public void afterPostInit() {
        // override recipes
        //TODO move some to prePostInit instead of disabling
        changeDTowerRecipes();
        changeCrackingRecipes();
        changeElectrolysisRecipes();
        addFuelValues();
        overrideMiscRecipes();
    }

    @Override
    public void configure(Config config) {}

    private void addRecipeMaps() {
        hydroCracking = new Recipe.RecipeMap(null, "gt6x.recipe.hydrocracking", "Hydrocracking", null, 0, 1, RES_PATH_GUI+"machines/HydroCracking", 1, 3, 0, 2, 9, 1, 2, 1, "", 1, "", T, T, T, T, F, T, T);
    }

    private void changeDTowerRecipes() {
        for (FL oil : new FL[]{ FL.Oil_Light, FL.Oil_Light2, FL.Oil_Normal, FL.Oil_Medium, FL.Oil_HotCrude, FL.Oil_Soulsand, FL.Oil_Heavy, FL.Oil_Heavy2, FL.Oil_ExtraHeavy }) {
            if (oil.exists()) {
                Recipe recipe = RM.DistillationTower.findRecipe(null, null, true, Long.MAX_VALUE, null,
                        FL.array(oil.make(25))
                );
                if (recipe == null) continue;

                for (int i = 0; i < recipe.mFluidOutputs.length; i++) {
                    if (recipe.mFluidOutputs[i].getFluid().getName().equals("petrol")) {
                        int amount = recipe.mFluidOutputs[i].amount;
                        recipe.mFluidOutputs[i] = MTx.Naphtha.liquid(amount*U1000, false);
                    }
                }

                recipe.mOutputs = new ItemStack[]{ dustTiny.mat(MT.WaxParaffin, 1), dustTiny.mat(MT.Asphalt, 1) };
            }
        }
        RM.Distillery.addRecipe1(true, 16, 16, ST.tag(0), MTx.Synoil.liquid(25*U1000, true), MTx.DieselLowSulfur.liquid(30*U1000, false), FL.lube(40));
        RM.DistillationTower.addRecipe0(false, 64, 196, new long[] {4000, 4000}, FL.array(MTx.Synoil.liquid(25*U1000, true)), FL.array(MTx.FuelLowSulfur.liquid(30*U1000, false), MTx.DieselLowSulfur.liquid(20*U1000, false), MTx.KerosineLowSulfur.liquid(20*U1000, false), MTx.NaphthaLowSulfur.liquid(15*U1000, false), FL.Propane.make(10), FL.Butane.make(10), FL.lube(40)), dustTiny.mat(MT.WaxParaffin, 1), dustTiny.mat(MT.Asphalt, 1));
    }

    private void changeCrackingRecipes() {
        for (Recipe recipe : RM.SteamCracking.mRecipeList) recipe.mEnabled = false;
        RM.Generifier.findRecipe(null, null, false, 0, null, FL.array(FL.Gas_Natural.make(1))).mEnabled = false;

        // olefins
        RM.SteamCracking.addRecipe0(false, 16,  64*4, new long[]{100}, FL.array(FL.Steam.make(8192), MTx.Ethane.gas(320*U1000, true)), FL.array(FL.Methane.make(250), FL.Ethylene.make(60)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(false, 16,  64*6, new long[]{100}, FL.array(FL.Steam.make(12288), FL.Propane.make(660)), FL.array(FL.Methane.make(450), FL.Ethylene.make(120), FL.Propylene.make(180)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(false, 16,  64*5, new long[]{100}, FL.array(FL.Steam.make(10240), FL.Butane.make(560)), FL.array(FL.Methane.make(200), FL.Ethylene.make(180), FL.Propylene.make(180)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(false, 16,  64, new long[]{100}, FL.array(FL.Steam.make(2048), MTx.NaphthaLowSulfur.liquid(U10, true)), FL.array(MTx.SCNaphtha.liquid(U10, false)), dustTiny.mat(MT.PetCoke, 9));

        // syngas
        RM.SteamCracking.addRecipe0(false, 16,  375, new long[]{800}, FL.array(FL.Steam.make(300*(long)STEAM_PER_WATER), FL.Gas_Natural.make(500)), FL.array(MT.H.gas(540*U1000, false), MT.CO.gas(180*U1000, false)), dustTiny.mat(MT.PetCoke, 9));
        RM.SteamCracking.addRecipe0(false, 16,  375, FL.array(FL.Steam.make(300*(long)STEAM_PER_WATER), FL.Methane.make(500)), FL.array(MT.H.gas(600*U1000, false), MT.CO.gas(200*U1000, false)), ZL_IS);
        for (OreDictMaterial coal : new OreDictMaterial[] { MT.Coal, MT.Charcoal, MT.CoalCoke, MT.C, MT.Graphite }) { // for some reason ANY does not work here
            // runs as long as a boiler @256 steam/t needs to run to produce an equal amount of steam
            RM.SteamCracking.addRecipe1(false, 16,  1875, dust.mat(coal, 1), FL.array(FL.Steam.make(3000*(long)STEAM_PER_WATER)), FL.array(MT.H.gas(U*2, false), MT.CO.gas(U*2, false)), ZL_IS);
        }

        for (Recipe recipe : RM.CatalyticCracking.mRecipeList) recipe.mEnabled = false;

        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT.Al2O3, 0), FL.array(MTx.NaphthaLowSulfur.liquid(U10, true)), FL.array(FL.Petrol.make(75), MTx.FccOffgas.gas(25*U1000, false)), dustTiny.mat(MT.PetCoke, 1));
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT.Al2O3, 0), FL.array(MTx.KerosineLowSulfur.liquid(U10, true)), FL.array(FL.Petrol.make(100), MTx.FccOffgas.gas(25*U1000, false)), dustTiny.mat(MT.PetCoke, 1));
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT.Al2O3, 0), FL.array(MTx.DieselLowSulfur.liquid(U10, true)), FL.array(FL.Petrol.make(125), MTx.FccOffgas.gas(25*U1000, false)), dustTiny.mat(MT.PetCoke, 1));
        RM.CatalyticCracking.addRecipe1(false, 16,  64, new long[]{1000}, dust.mat(MT.Al2O3, 0), FL.array(MTx.FuelLowSulfur.liquid(U10, true)), FL.array(FL.Petrol.make(150), MTx.FccOffgas.gas(25*U1000, false)), dustTiny.mat(MT.PetCoke, 1));
    }

    private void changeElectrolysisRecipes() {
        Recipe r;
        // change CO2/CO electrolysis
        r = RM.Electrolyzer.findRecipe(null, null, true, Long.MAX_VALUE, null, FL.array(MT.CO2.gas(3*U, true)), ST.tag(0)); if (r != null) r.mEnabled = false;
        r = RM.Electrolyzer.findRecipe(null, null, true, Long.MAX_VALUE, null, FL.array(MT.CO.gas(2*U, true)), ST.tag(0)); if (r != null) r.mEnabled = false;

        RM.Electrolyzer.addRecipe1(true, 512, 256, dust.mat(MT.Ce, 0), FL.array(MT.CO2.gas(3*U, true)), FL.array(MT.CO.gas(2*U, false), MT.O.gas(U, false)));
    }

    private void addFuelValues() {
        FM.Burn.addRecipe0(true, -64, 24, MTx.FccOffgas.gas(U200, true), FL.Water.make(6), FL.CarbonDioxide.make(3));
        FM.Gas.addRecipe0(true, -64, 30, MTx.FccOffgas.gas(U200, true), FL.Water.make(6), FL.CarbonDioxide.make(3));

        FM.Burn.addRecipe0(true, -64, 25, MTx.Ethane.gas(U200, true), FL.Water.make(6), FL.CarbonDioxide.make(4));
        FM.Gas.addRecipe0(true, -64, 35, MTx.Ethane.gas(U200, true), FL.Water.make(6), FL.CarbonDioxide.make(4));

        FM.Burn.addRecipe0(true, -64, 3, MTx.Naphtha.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 5, MTx.Naphtha.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -64, 4, MTx.NaphthaLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 6, MTx.NaphthaLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -64, 6, MTx.KerosineLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 8, MTx.KerosineLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -64, 6, MTx.DieselLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 8, MTx.DieselLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -64, 7, MTx.FuelLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 9, MTx.FuelLowSulfur.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -64, 6, MTx.Benzene.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 8, MTx.Benzene.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -64, 6, MTx.Toluene.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -64, 8, MTx.Toluene.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -8,  5, MTx.Methanol.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Engine.addRecipe0(true, -8,  7, MTx.Methanol.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);

        FM.Burn.addRecipe0(true, -16, 36, MTx.Synoil.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        FM.Burn.addRecipe0(true, -16, 4, MTx.Acetone.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
    }

    private void addRecipes() {
        // Natural Gas distillation and liquefaction
        RM.CryoDistillationTower.addRecipe0(true, 64, 32, new long[] {5000}, FL.array(FL.Gas_Natural.make(100)), FL.array(MTx.Naphtha.liquid(U500, false), MT.Butane.gas(U500, false), MT.Propane.gas(U500, false), MTx.Ethane.gas(U200, false), MT.CH4.gas(U10, false), MT.N.gas(U500, false), MT.He.gas(U1000, false)), dustTiny.mat(MT.Ice, 1));
        RM.Freezer.addRecipe1(false, 64, 64*3, ST.tag(0), FL.array(FL.Gas_Natural.make(600)), MTx.LNG.liquid(U1000, false), dustTiny.mat(MT.Ice, 6));
        RM.DistillationTower.addRecipe0(false, 64, 32, new long[] {5000}, FL.array(MTx.LNG.liquid(U1000, true)), FL.array(MTx.Naphtha.liquid(6*U500, false), MT.Butane.gas(6*U500, false), MT.Propane.gas(6*U500, false), MTx.Ethane.gas(6*U200, false), MT.CH4.gas(6*U10, false), MT.N.gas(6*U500, false), MT.He.gas(6*U1000, false)));
        RM.Distillery.addRecipe1(false, 16, 64, ST.tag(0), FL.array(MTx.LNG.liquid(U1000, true)), FL.array(FL.Methane.make(600), MTx.Ethane.gas(30*U1000, false)));

        // distillation of cracker outputs
        RM.DistillationTower.addRecipe0(false, 64, 64, FL.array(MTx.SCNaphtha.liquid(U10, true)), FL.array(FL.Methane.make(25), FL.Ethylene.make(50), MTx.Ethane.liquid(20*U1000, false), FL.Propylene.make(40), MTx.Benzene.liquid(15*U1000, false), MTx.Toluene.liquid(10*U1000, false), MTx.Isoprene.liquid(10*U1000, false)));
        RM.DistillationTower.addRecipe0(false, 64, 64, FL.array(MTx.FccOffgas.gas(U10, true)), FL.array(FL.Methane.make(20), FL.Ethylene.make(12), MTx.Ethane.liquid(10*U1000, false), FL.Propylene.make(8), FL.Propane.make(30), FL.Butane.make(20)));

        // distillation of creosote
        RM.DistillationTower.addRecipe0(false, 64, 32, new long[] {500}, FL.array(FL.Oil_Creosote.make(100)), FL.array(MTx.Benzene.liquid(3*U1000, false), MTx.Toluene.liquid(2*U1000, false), MTx.Phenol.liquid(18*U1000, false), MTx.Naphthalene.liquid(9*U1000, false), MTx.Anthracene.liquid(18*U1000, false)), dustTiny.mat(MT.Asphalt, 9));

        // Desulfurisation
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MTx.Naphtha.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.NaphthaLowSulfur.liquid(U1000*99, false)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT.Kerosine.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.KerosineLowSulfur.liquid(U1000*99, false)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT.Diesel.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.DieselLowSulfur.liquid(U1000*99, false)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT.Fuel.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.FuelLowSulfur.liquid(U1000*99, false)));

        // PVC
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.FeCl3, 0), FL.array(MT.Ethylene.gas(6*U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.EthyleneDichloride.liquid(8*U, false)));
        //TODO use thermolyzer
        RM.Distillery.addRecipe1(true, 16, 128, ST.tag(0), MTx.EthyleneDichloride.liquid(8*U, true), FL.array(MTx.VinylChloride.gas(6*U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.OREMATS.Galena, 0), FL.array(MTx.VinylChloride.gas(U10, false)), ZL_FS, dust.mat(MT.PVC, 1));

        // PTFE
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.CH4.gas(5*U, true), MT.Cl.gas(6*U, true)), FL.array(MTx.CHCl3.liquid(5*U, false), MT.HCl.gas(6*U, false)));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CHCl3.liquid(5*U, true), MT.HF.gas(4*U, true)), FL.array(MTx.CHClF2.gas(5*U, false), MT.HCl.gas(4*U, false)));
        //TODO use thermolyzer
        RM.Distillery.addRecipe1(true, 16, 128, ST.tag(0), MTx.CHClF2.gas(10*U, true), FL.array(MTx.C2F4.gas(6*U, false), MT.HCl.gas(4*U, false)));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.KSO4, 0), FL.array(MTx.C2F4.gas(U10, false)), ZL_FS, dust.mat(MT.PTFE, 1));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.NaSO4, 0), FL.array(MTx.C2F4.gas(U10, false)), ZL_FS, dust.mat(MT.PTFE, 1));

        // Rubber
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.MgCl2, 0), FL.array(MT.TiCl4.liquid(U1000, true), MTx.Isoprene.liquid(U10, false)), ZL_FS, dust.mat(MT.Rubber, 1));

        // BPA, PF, Bakelite
        RM.Mixer.addRecipe0(true, 16,  64*3, FL.array(MT.Propylene.gas(9*U, true), MTx.Benzene.liquid(12*U, true), MTx.PhosphoricAcid.liquid(3*U1000, true)), FL.array(MTx.Cumene.liquid(21*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64*3, FL.array(MTx.Cumene.fluid(21*U, true), MT.O.gas(2*U, true)), FL.array(MTx.Acetone.liquid(10*U, false)), dust.mat(MTx.Phenol, 13));
        RM.Mixer.addRecipe1(true, 16,  512, dust.mat(MTx.Phenol, 26), FL.array(MTx.Acetone.fluid(10*U, true), MT.H2SO4.liquid(U1000, true)), MT.H2O.liquid(3*U, false), dust.mat(MTx.BPA, 33));
        RM.Mixer.addRecipe2(true, 16,  512, dust.mat(MT.NaOH, 0), dust.mat(MTx.Phenol, 26), MTx.Formaldehyde.gas(8*U, true), MT.H2O.liquid(3*U, false), dust.mat(MTx.PF, 31));
        RM.Autoclave.addRecipe1(true, 16, 500, dust.mat(MTx.PF, 1), FL.Steam.make(16000), FL.DistW.make(100), chunkGt.mat(MT.Bakelite, 4));

        // ECH
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.Propylene.gas(9*U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.AllylChloride.liquid(9*U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.HCl.gas(2*U, true), MTx.AllylChloride.liquid(9*U, true), MT.O.gas(U, true)), FL.array(MTx.Dichloropropanol.liquid(12*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.HCl.gas(4*U, true), MT.Glycerol.liquid(14*U, true)), FL.array(MTx.Dichloropropanol.liquid(12*U, false), MT.H2O.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16,  64, dust.mat(MT.NaOH, 3), FL.array(MTx.Dichloropropanol.liquid(12*U, true), MT.H2O.liquid(3*U, true)), FL.array(MTx.ECH.liquid(10*U, false), MT.SaltWater.liquid(8*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MTx.Dichloropropanol.liquid(12*U, true), MTx.NaOHSolution.liquid(6*U, true)), FL.array(MTx.ECH.liquid(10*U, false), MT.SaltWater.liquid(8*U, false)));

        // Epoxy
        RM.Mixer.addRecipe2(true, 16,  64, dust.mat(MT.NaOH, 6), dust.mat(MTx.BPA, 33), FL.array(MTx.ECH.liquid(20*U, true), MT.H2O.liquid(U*6, true)), FL.array(MT.SaltWater.liquid(16*U, false)), dust.mat(MTx.Epoxy, 49));
        RM.Mixer.addRecipe1(true, 16,  64, dust.mat(MTx.BPA, 33), FL.array(MTx.ECH.liquid(20*U, true), MTx.NaOHSolution.liquid(U*12, true)), FL.array(MT.SaltWater.liquid(16*U, false)), dust.mat(MTx.Epoxy, 49));

        // TNT
        RM.Mixer.addRecipe0(true, 32, 64*3, FL.array(MTx.Toluene.liquid(15*U, true), MT.HNO3.liquid(15*U, true), MT.H2SO4.liquid(21*U, true)), FL.array(MT.H2O.liquid(9*U, false), MT.H2SO4.liquid(21*U, true)), dust.mat(MTx.TNT, 21));
        RM.Boxinator.addRecipe2(true, 16, 20, ST.make(Items.paper, 1, 0), dust.mat(MTx.TNT, 1), ST.make(Blocks.tnt, 1, 0));

        // ANFO
        RM.Mixer.addRecipe1(true, 16, 64 , dust.mat(MTx.NH4NO3, 3), FL.Fuel.make(180), NF, dust.mat(MTx.ANFO, 3));

        RM.Press.addRecipeX(true, 16, 16 , ST.array(ST.tag(1), dust     .mat(MTx.ANFO , 1), ST.make(Items.string                                    , 1, W)), IL.Dynamite       .get(1));
        RM.Press.addRecipeX(true, 16, 16 , ST.array(ST.tag(1), dust     .mat(MTx.ANFO , 1), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 1, W)), IL.Dynamite       .get(1));
        RM.Press.addRecipeX(true, 16, 144, ST.array(ST.tag(1), blockDust.mat(MTx.ANFO , 1), ST.make(Items.string                                    , 9, W)), IL.Dynamite       .get(9));
        RM.Press.addRecipeX(true, 16, 144, ST.array(ST.tag(1), blockDust.mat(MTx.ANFO , 1), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 9, W)), IL.Dynamite       .get(9));
        RM.Press.addRecipeX(true, 16, 64 , ST.array(ST.tag(2), dust     .mat(MTx.ANFO , 2), ST.make(Items.string                                    , 1, W)), IL.Dynamite_Strong.get(1));
        RM.Press.addRecipeX(true, 16, 64 , ST.array(ST.tag(2), dust     .mat(MTx.ANFO , 2), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 1, W)), IL.Dynamite_Strong.get(1));
        RM.Press.addRecipeX(true, 16, 576, ST.array(ST.tag(2), blockDust.mat(MTx.ANFO , 2), ST.make(Items.string                                    , 9, W)), IL.Dynamite_Strong.get(9));
        RM.Press.addRecipeX(true, 16, 576, ST.array(ST.tag(2), blockDust.mat(MTx.ANFO , 2), ST.make((Item)plantGtFiber.mRegisteredPrefixItems.get(0), 9, W)), IL.Dynamite_Strong.get(9));

        // Hydrodealkylation of toluene in case you don't need TNT
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Cr, 0), FL.array(MTx.Toluene.liquid(15*U, true), MT.H.gas(2*U, true)), FL.array(MTx.Benzene.liquid(12*U, false), MT.CH4.gas(5*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Mo, 0), FL.array(MTx.Toluene.liquid(15*U, true), MT.H.gas(2*U, true)), FL.array(MTx.Benzene.liquid(12*U, false), MT.CH4.gas(5*U, false)));

        // Water-gas shift reaction
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Fe2O3, 0), FL.array(FL.Steam.make(3000*(long)STEAM_PER_WATER), MT.CO.gas(2*U, true)), FL.array(MT.H.gas(2*U, false), MT.CO2.gas(3*U, false)));

        // Fischer-Tropsch process
        RM.Mixer.addRecipe1(true, 16, 512*15, dust.mat(MT.Ru, 0), FL.array(MT.CO.gas(15*2*U, true), MT.H.gas(30*2*U, true)), FL.array(MTx.Synoil.liquid(15*U, false), MT.H2O.liquid(15*3*U, false)));

        // Naphthalene, Anthraquinone-, Azo-, and other dye precursors
        RM.Mixer.addRecipe1(true, 16, 256, ST.tag(3), FL.array(MTx.Naphthalene.liquid(18*U, true), MT.H2SO4.liquid(7*U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.DiluteH2SO4.liquid(10*U, false)), dust.mat(MTx.Nitronaphthalene, 20));
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MT.Ni, 0), dust.mat(MTx.Nitronaphthalene, 20), MT.H.gas(6*U, true), MT.H2O.liquid(6*U, false), dust.mat(MTx.Aminonaphthalene, 20));
        RM.Mixer.addRecipe1(true, 16, 256, ST.tag(2), FL.array(MTx.Naphthalene.liquid(18*U, true), MT.H2SO4.liquid(7*U, true)), FL.array(MT.H2O.liquid(3*U, false)), dust.mat(MTx.NaphthaleneSulfonicAcid, 22));
        RM.Bath.addRecipe2(true, 16, 256, dust.mat(MT.NaOH, 3), dust.mat(MTx.NaphthaleneSulfonicAcid, 22), MT.O.gas(U, true), NF, dust.mat(MT.NaHSO4, 7), dust.mat(MTx.Naphthol, 19));

        RM.Bath.addRecipe1(true, 16, 256, dust.mat(MTx.CrO3, 8*U), MTx.Anthracene.liquid(24*U, true), FL.Water.make(3000), dust.mat(MTx.Anthraquinone, 24), dustSmall.mat(MTx.Cr2O3, 20));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.Anthraquinone, 24), MT.H2SO4.liquid(14*U, true), FL.Water.make(6000), dust.mat(MTx.AnthraquinoneDisulfonicAcid, 32));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.AnthraquinoneDisulfonicAcid, 32), FL.array(MT.NH3.gas(2*U, true), MT.O.gas(2*U, true)), MT.H2SO4.liquid(14*U, false), dust.mat(MTx.Diaminoanthraquinone, 28));
        RM.Mixer.addRecipe0(true, 16, 256, FL.array(MTx.Toluene.liquid(15*U, true), MT.Cl.gas(2*U, true)), MT.HCl.gas(2*U, false), dust.mat(MTx.Chlorotoluene, 15));
        RM.Mixer.addRecipe0(true, 16, 256, FL.array(MTx.Benzene.liquid(12*U, true), MT.H2SO4.liquid(7*U, true), MT.HNO3.liquid(5*U, true)), FL.array(MTx.DiluteH2SO4.liquid(10*U, false), MTx.Nitrobenzene.liquid(14*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Ni, 0), FL.array(MTx.Nitrobenzene.liquid(14*U, true), MT.H.gas(6*U, true)), FL.array(MTx.Aniline.liquid(14*U, false), MT.H2O.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.Phenol, 13), FL.array(MT.H2SO4.liquid(14*U, true), MT.HNO3.liquid(10*U, true)), FL.array(MTx.DiluteH2SO4.liquid(20*U, false)), dust.mat(MTx.DNP, 17));
        RM.Mixer.addRecipe2(true, 16, 128, dust.mat(MT.Pd, 0), dust.mat(MTx.Phenol, 13), FL.array(MT.NH3.gas(U, true)), FL.array(MTx.Aniline.liquid(14*U, false), MT.H2O.liquid(3*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, ST.tag(3), FL.array(MTx.Aniline.liquid(14*U, true), MTx.HNO2.liquid(4*U, true), MT.HCl.gas(2*U, true)), FL.array(FL.Water.make(6000)), dust.mat(MTx.BenzenediazoniumChloride, 14));
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe0(true, 16, 256, FL.array(MTx.Aniline.liquid(14 * U, true), MTx.Formaldehyde.liquid(4 * U, true), MTx.HCN.liquid(3 * U, true), water), FL.array(MT.NH3.gas(U, false)), dust.mat(MTx.NPhenylGlycine, 20));
            RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.Aminonaphthalene, 40), FL.array(MT.H2SO4.liquid(7*U, true), FL.mul(water, 4)), FL.array(MTx.NH4SO4Solution.liquid(21*U, false)), dust.mat(MTx.Naphthol, 38));
            RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.Aminonaphthalene, 40), FL.array(MTx.DiluteH2SO4.liquid(10*U, true), FL.mul(water, 3)), FL.array(MTx.NH4SO4Solution.liquid(21*U, false)), dust.mat(MTx.Naphthol, 38));
        }

        // H2O2 with Anthraquinone
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.Anthraquinone, 0), FL.array(MT.H.gas(2*U, true), MT.O.gas(2*U, true)), FL.array(MT.H2O2.liquid(4*U, false)));

        // Synthetic dyes
        /// Bleach (white dye)
        Fluid chemicalWhiteDye = DYE_FLUIDS_CHEMICAL[DYE_INDEX_White].getFluid();
        for (FluidStack water : FL.waters(1)) {
            RM.Mixer.addRecipe2(true, 16, 64, ST.tag(3), dust.mat(MT.NaOH, 6), FL.array(MT.Cl.gas(2*U, true), FL.mul(water, 12000)), FL.make(chemicalWhiteDye, 18000), dust.mat(MT.NaCl, 2));
            RM.Mixer.addRecipe1(true, 16, 64, ST.tag(3), FL.array(MT.Cl.gas(2*U, true), MTx.NaOHSolution.liquid(12*U, true), FL.mul(water, 6000)), FL.make(chemicalWhiteDye, 18000), dust.mat(MT.NaCl, 2));
            RM.Mixer.addRecipe0(true, 16, 8, FL.array(MT.H2O2.liquid(9 * U1000, true), FL.mul(water, 9)), FL.make(chemicalWhiteDye, 18), NF);
        }
        /// Anthraquinone Dyes
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.Chlorotoluene, 30), dust.mat(MTx.Diaminoanthraquinone, 28), NF, MT.HCl.gas(4*U, false), dust.mat(MTx.QuinizarineGreen, 54));
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.AnthraquinoneDisulfonicAcid, 32), dust.mat(MT.NaOH, 12), MT.O.gas(2*U, true), FL.Water.make(6000), dust.mat(MTx.AlizarinRed, 26), dust.mat(MT.Na2SO4, 14));
        /// Azo Dyes
        // Solvent Yellow 1
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 14), FL.array(MTx.Aniline.liquid(14 * U, true), MTx.NaOHSolution.liquid(6*U, true)), FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 26));
        // Solvent Yellow 7
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 14), dust.mat(MTx.Phenol, 13), MTx.NaOHSolution.liquid(6*U, true), FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 25));
        // Organol Brown
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 14), dust.mat(MTx.Naphthol, 19), MTx.NaOHSolution.liquid(6*U, true), FL.Saltwater.make(8000), dust.mat(MTx.OrganolBrown, 31));

        for (FluidStack water : FL.waters(3000)) {
            // Solvent Yellow 1
            RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.BenzenediazoniumChloride, 14), dust.mat(MT.NaOH, 3), FL.array(MTx.Aniline.liquid(14 * U, true), water), FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 26));
            // Solvent Yellow 7
            RM.Mixer.addRecipeX(true, 16, 256, ST.array(dust.mat(MTx.BenzenediazoniumChloride, 14), dust.mat(MT.NaOH, 3), dust.mat(MTx.Phenol, 13)), water, FL.Saltwater.make(8000), dust.mat(MTx.SolventYellow, 25));
            // Organol Brown
            RM.Mixer.addRecipeX(true, 16, 256, ST.array(dust.mat(MTx.BenzenediazoniumChloride, 14), dust.mat(MT.NaOH, 3), dust.mat(MTx.Naphthol, 19)), water, FL.Saltwater.make(8000), dust.mat(MTx.OrganolBrown, 31));
            // Organol Brown
            RM.Mixer.addRecipeX(true, 16, 256, ST.array(dust.mat(MTx.BenzenediazoniumChloride, 14), dust.mat(MT.NaOH, 3), dust.mat(MTx.Naphthol, 19)), water, FL.Saltwater.make(8000), dust.mat(MTx.OrganolBrown, 31));
            /// Sulfur Black
            RM.Mixer.addRecipe2(true, 16, 512, dust.mat(MTx.DNP, 68), dust.mat(MT.Na2S, 24), FL.array(FL.mul(water, 8)), FL.array(MTx.NaOHSolution.liquid(48*U, false), MT.NO2.gas(6*U, false)), dust.mat(MTx.SulfurBlack, 62));
        }

        /// Indigo Dye
        RM.Mixer.addRecipeX(true, 16, 512, ST.array(dust.mat(MT.NaOH, 0), dust.mat(MT.KOH, 0), dust.mat(MTx.NPhenylGlycine, 40)), MT.O.gas(2*U, true), FL.Water.make(12000), dust.mat(MT.Indigo, 30));

        // Hydro-cracking
        // Fuel: 20-50 C (avg C34H70) - 3 fuel -> 2 Diesel + 1 Kerosine * 1.5
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(6), MT.Fuel.liquid(105*U1000, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.DieselLowSulfur.liquid(U1000*76, false), MTx.KerosineLowSulfur.liquid(U1000*32, false)));
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(4), MTx.FuelLowSulfur.liquid(104*U1000, true)), FL.array(MTx.DieselLowSulfur.liquid(U1000*76, false), MTx.KerosineLowSulfur.liquid(U1000*32, false)));

        // Diesel: 10-20 C (avg C12H26) - 3 Diesel -> 2 Kerosine + 2 Naphtha
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(4), MT.Diesel.liquid(115*U1000, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.KerosineLowSulfur.liquid(U1000*64, false), MTx.NaphthaLowSulfur.liquid(U1000*52, false)));
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(2), MTx.DieselLowSulfur.liquid(114*U1000, true)), FL.array(MTx.KerosineLowSulfur.liquid(U1000*64, false), MTx.NaphthaLowSulfur.liquid(U1000*52, false)));

        // Kerosine: 9-12 C (avg C10H22) - 3 Kerosine -> 2 Naphtha + 1 Butane * 1.5
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(5), MT.Kerosine.liquid(97*U1000, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.NaphthaLowSulfur.liquid(U1000*78, false), MT.Butane.gas(U1000*21, false)));
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(3), MTx.KerosineLowSulfur.liquid(96*U1000, true)), FL.array(MTx.NaphthaLowSulfur.liquid(U1000*78, false), MT.Butane.gas(U1000*21, false)));

        // Naphtha: 5-12 C (avg C8H18) - 2 Naphtha -> 2 Butane + 2 Propane + 1 Ethane * 2
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(14), MTx.Naphtha.liquid(105*U1000, true)), FL.array(MT.H2S.gas(U1000*3, false), MT.Butane.gas(U1000*56, false), MT.Propane.gas(U1000*44, false), MTx.Ethane.gas(U1000*16, false)));
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(12), MTx.NaphthaLowSulfur.liquid(104*U1000, true)), FL.array(MT.Butane.gas(U1000*56, false), MT.Propane.gas(U1000*44, false), MTx.Ethane.gas(U1000*16, false)));

        // Butane: 2 Butane -> 2 Ethane + 1 Propane + 1 Methane * 4
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(16), MT.Butane.gas(112*U1000, true)), FL.array(MT.Propane.gas(U1000*44, false), MTx.Ethane.gas(U1000*64, false), MT.CH4.gas(U1000*20, false)));
        // Propane: 1 Propane -> 1 Ethane + 1 Methane * 10
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(20), MT.Propane.gas(110*U1000, true)), FL.array(MTx.Ethane.gas(U1000*80, false), MT.CH4.gas(U1000*50, false)));
        // Ethane: 1 Ethane -> 2 Methane * 14
        hydroCracking.addRecipe0(false, 16,  64, FL.array(FL.Hydrogen.make(28), MTx.Ethane.gas(112*U1000, true)), FL.array(MT.CH4.gas(U1000*140, false)));

        // extruder recipes for cans
        final long EUt = 16, durationPerUnit = 64*6;
        for (OreDictPrefix tPrefix : OreDictPrefix.VALUES) if (tPrefix != null && tPrefix.containsAny(TD.Prefix.EXTRUDER_FODDER, TD.Prefix.INGOT_BASED, TD.Prefix.GEM_BASED, TD.Prefix.DUST_BASED) && U % tPrefix.mAmount == 0) {
            ItemStack pvcStack = tPrefix.mat(MT.PVC, U / tPrefix.mAmount);
            if (pvcStack != null && pvcStack.stackSize * 6 <= pvcStack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, pvcStack), IL.Shape_Extruder_Cell.get(0), ST.amount(1, pvcCan));
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, pvcStack), IL.Shape_SimpleEx_Cell.get(0), ST.amount(1, pvcCan));
            }
            ItemStack ptfeStack = tPrefix.mat(MT.PTFE, U / tPrefix.mAmount);
            if (ptfeStack != null && ptfeStack.stackSize * 6 <= ptfeStack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, ptfeStack), IL.Shape_Extruder_Cell.get(0), ST.amount(1, ptfeCan));
                RM.Extruder.addRecipe2(true, false, false, false, true, EUt, durationPerUnit, ST.mul_( 6, ptfeStack), IL.Shape_SimpleEx_Cell.get(0), ST.amount(1, ptfeCan));
            }
        }

    }

    private void addMTEs() {
        // pipes
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PVCTubes.get(), 0, 200, true, true, false, true, false, false, true, MTEx.gt6xMTEReg, MTEx.PlasticBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MT.PVC.mMeltingPoint, MT.PVC);
        MultiTileEntityPipeFluid.addFluidPipes(MTEx.IDs.PTFETubes.get(), 0, 1000, true, true, false, true, false, false, true, MTEx.gt6xMTEReg, MTEx.PlasticBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MT.PTFE.mMeltingPoint, MT.PTFE);

        // PVC can
        pvcCan = MTEx.gt6xMTEReg.add("PVC Canister", "Fluid Containers", MTEx.IDs.PVCCan.get(), 32719, MultiTileEntityBarrelPlasticAdvanced.class, 0, 16, MTEx.PlasticBlock, gregapi.util.UT.NBT.make(NBT_MATERIAL, MT.PVC, gregapi.data.CS.NBT_HARDNESS, 1.0F, gregapi.data.CS.NBT_RESISTANCE, 3.0F, NBT_TANK_CAPACITY, 64000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true, NBT_ACIDPROOF, true));
        OM.data(pvcCan, new OreDictItemData(MT.PVC, U*6));

        // PTFE can
        ptfeCan = MTEx.gt6xMTEReg.add("PTFE Canister", "Fluid Containers", MTEx.IDs.PTFECan.get(), 32719, MultiTileEntityBarrelPlasticAdvanced.class, 0, 16, MTEx.PlasticBlock, gregapi.util.UT.NBT.make(NBT_MATERIAL, MT.PTFE, gregapi.data.CS.NBT_HARDNESS, 1.0F, gregapi.data.CS.NBT_RESISTANCE, 3.0F, NBT_TANK_CAPACITY, 256000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true, NBT_ACIDPROOF, true));
        OM.data(ptfeCan, new OreDictItemData(MT.PTFE, U*6));

        // Hydro cracker
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial aMat;
        aMat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", MTEx.IDs.Hydrocracker1.get(), 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateDouble   .dat(ANY.Cu), 'I', OP.plateDouble   .dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        aMat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", MTEx.IDs.Hydrocracker2.get(), 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateTriple   .dat(ANY.Cu), 'I', OP.plateTriple   .dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        aMat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", MTEx.IDs.Hydrocracker3.get(), 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuadruple.dat(ANY.Cu), 'I', OP.plateQuadruple.dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        aMat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", MTEx.IDs.Hydrocracker4.get(), 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuintuple.dat(ANY.Cu), 'I', OP.plateQuintuple.dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
    }

    private void overrideMiscRecipes() {
        RM.Mixer.addRecipe0(true, 16, 800, FL.array(MT.Glycerol.fluid(U*14, true), MT.HNO3.liquid(U*15, true), MT.H2SO4.liquid(U*21, true)), MT.Glyceryl.fluid(U*20, false), MTx.DiluteH2SO4.liquid(U*30, false));
    }
}
