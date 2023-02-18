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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

        FM.Burn.addRecipe0(true, - 16, 36, MTx.Synoil.liquid(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
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

        // Desulfurisation
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MTx.Naphtha.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.NaphthaLowSulfur.liquid(U1000*99, false)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT.Kerosine.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.KerosineLowSulfur.liquid(U1000*99, false)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT.Diesel.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.DieselLowSulfur.liquid(U1000*99, false)));
        RM.Mixer.addRecipe1(true, 16, 20, dust.mat(MT.OREMATS.Molybdenite, 0), FL.array(MT.H.gas(U1000*2, true), MT.Fuel.liquid(U10, true)), FL.array(MT.H2S.gas(U1000*3, false), MTx.FuelLowSulfur.liquid(U1000*99, false)));

        // PVC
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.FeCl3, 0), FL.array(MT.Ethylene.gas(6*U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.EthyleneDichloride.liquid(8*U, false)));
        //TODO use thermolyzer
        RM.Distillery.addRecipe1(true, 16, 128, ST.tag(0), MTx.EthyleneDichloride.liquid(8*U, true), FL.array(MTx.VinylChloride.gas(6*U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.OREMATS.Galena, 0), FL.array(MTx.VinylChloride.gas(U10, false)), ZL_FS, dust.mat(MTx.PVC, 1));

        // PTFE
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.CH4.gas(5*U, true), MT.Cl.gas(6*U, true)), FL.array(MTx.CHCl3.liquid(5*U, false), MT.HCl.gas(6*U, false)));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CHCl3.liquid(5*U, true), MT.HF.gas(4*U, true)), FL.array(MTx.CHClF2.gas(5*U, false), MT.HCl.gas(4*U, false)));
        //TODO use thermolyzer
        RM.Distillery.addRecipe1(true, 16, 128, ST.tag(0), MTx.CHClF2.gas(10*U, true), FL.array(MTx.C2F4.gas(6*U, false), MT.HCl.gas(4*U, false)));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.KSO4, 0), FL.array(MTx.C2F4.gas(U10, false)), ZL_FS, dust.mat(MTx.PTFE, 1));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.NaSO4, 0), FL.array(MTx.C2F4.gas(U10, false)), ZL_FS, dust.mat(MTx.PTFE, 1));

        // Rubber
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.MgCl2, 0), FL.array(MT.TiCl4.liquid(U1000, true), MTx.Isoprene.liquid(U10, false)), ZL_FS, dust.mat(MT.Rubber, 1));

        // BPA
        RM.Mixer.addRecipe0(true, 16,  64*3, FL.array(MT.Propylene.gas(9*U, true), MTx.Benzene.liquid(12*U, true), MTx.PhosphoricAcid.liquid(3*U1000, true)), FL.array(MTx.Cumene.liquid(21*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64*3, FL.array(MTx.Cumene.fluid(21*U, true), MT.O.gas(2*U, true)), FL.array(MTx.Acetone.liquid(10*U, false)), dust.mat(MTx.Phenol, 13));
        RM.Mixer.addRecipe1(true, 16,  64, dust.mat(MTx.Phenol, 26), FL.array(MTx.Acetone.fluid(10*U, true), MT.H2SO4.liquid(U1000, true)), MT.H2O.liquid(3*U, false), dust.mat(MTx.BPA, 33));

        // ECH
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.Propylene.gas(9*U, true), MT.Cl.gas(2*U, true)), FL.array(MTx.AllylChloride.liquid(9*U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.HCl.gas(2*U, true), MTx.AllylChloride.liquid(9*U, true), MT.O.gas(U, true)), FL.array(MTx.Dichloropropanol.liquid(12*U, false)));
        RM.Mixer.addRecipe0(true, 16,  64, FL.array(MT.HCl.gas(4*U, true), MT.Glycerol.liquid(14*U, true)), FL.array(MTx.Dichloropropanol.liquid(12*U, false), MT.H2O.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16,  64, dust.mat(MT.NaOH, 3), FL.array(MTx.Dichloropropanol.liquid(12*U, true), MT.H2O.liquid(3*U, true)), FL.array(MTx.ECH.liquid(10*U, false), MT.SaltWater.liquid(8*U, false)));

        // Epoxy
        RM.Mixer.addRecipe2(true, 16,  64, dust.mat(MT.NaOH, 6), dust.mat(MTx.BPA, 33), FL.array(MTx.ECH.liquid(20*U, true), MT.H2O.liquid(U*6, true)), FL.array(MT.SaltWater.liquid(16*U, false)), dust.mat(MTx.Epoxy, 49));

        // TNT
        RM.Mixer.addRecipe0(true, 32, 64*3, FL.array(MTx.Toluene.liquid(15*U, true), MT.HNO3.liquid(15*U, true), MT.H2SO4.liquid(21*U, true)), FL.array(MT.H2O.liquid(9*U, false), MT.H2SO4.liquid(21*U, true)), dust.mat(MTx.TNT, 21));
        RM.Boxinator.addRecipe2(true, 16, 20, ST.make(Items.paper, 1, 0), dust.mat(MTx.TNT, 1), ST.make(Blocks.tnt, 1, 0));

        // Hydrodealkylation of toluene in case you don't need TNT
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Cr, 0), FL.array(MTx.Toluene.liquid(15*U, true), MT.H.gas(2*U, true)), FL.array(MTx.Benzene.liquid(12*U, false), MT.CH4.gas(5*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Mo, 0), FL.array(MTx.Toluene.liquid(15*U, true), MT.H.gas(2*U, true)), FL.array(MTx.Benzene.liquid(12*U, false), MT.CH4.gas(5*U, false)));

        // Water-gas shift reaction
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Fe2O3, 0), FL.array(FL.Steam.make(3000*(long)STEAM_PER_WATER), MT.CO.gas(2*U, true)), FL.array(MT.H.gas(2*U, false), MT.CO2.gas(3*U, false)));

        // Fischer-Tropsch process
        RM.Mixer.addRecipe1(true, 16, 512*15, dust.mat(MT.Ru, 0), FL.array(MT.CO.gas(15*2*U, true), MT.H.gas(30*2*U, true)), FL.array(MTx.Synoil.liquid(15*U, false), MT.H2O.liquid(15*3*U, false)));

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
            ItemStack pvcStack = tPrefix.mat(MTx.PVC, U / tPrefix.mAmount);
            if (pvcStack != null && pvcStack.stackSize * 6 <= pvcStack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(T, F, F, F, T, EUt, durationPerUnit, ST.mul_( 6, pvcStack), IL.Shape_Extruder_Cell.get(0), ST.amount(1, pvcCan));
                RM.Extruder.addRecipe2(T, F, F, F, T, EUt, durationPerUnit, ST.mul_( 6, pvcStack), IL.Shape_SimpleEx_Cell.get(0), ST.amount(1, pvcCan));
            }
            ItemStack ptfeStack = tPrefix.mat(MTx.PTFE, U / tPrefix.mAmount);
            if (ptfeStack != null && ptfeStack.stackSize * 6 <= ptfeStack.getMaxStackSize()) {
                RM.Extruder.addRecipe2(T, F, F, F, T, EUt, durationPerUnit, ST.mul_( 6, ptfeStack), IL.Shape_Extruder_Cell.get(0), ST.amount(1, ptfeCan));
                RM.Extruder.addRecipe2(T, F, F, F, T, EUt, durationPerUnit, ST.mul_( 6, ptfeStack), IL.Shape_SimpleEx_Cell.get(0), ST.amount(1, ptfeCan));
            }
        }

    }

    private void addMTEs() {
        // pipes
        MultiTileEntityPipeFluid.addFluidPipes(0, 0, 200, true, true, false, true, false, false, true, MTEx.gt6xMTEReg, MTEx.PlasticBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MTx.PVC.mMeltingPoint, MTx.PVC);
        MultiTileEntityPipeFluid.addFluidPipes(20, 0, 1000, true, true, false, true, false, false, true, MTEx.gt6xMTEReg, MTEx.PlasticBlock, gregapi.tileentity.connectors.MultiTileEntityPipeFluid.class, MTx.PTFE.mMeltingPoint, MTx.PTFE);

        // PVC can
        pvcCan = MTEx.gt6xMTEReg.add("PVC Canister", "Fluid Containers", 41, 32719, MultiTileEntityBarrelPlasticAdvanced.class, 0, 16, MTEx.PlasticBlock, gregapi.util.UT.NBT.make(NBT_MATERIAL, MTx.PVC, gregapi.data.CS.NBT_HARDNESS, 1.0F, gregapi.data.CS.NBT_RESISTANCE, 3.0F, NBT_TANK_CAPACITY, 64000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true, NBT_ACIDPROOF, true));
        OM.data(pvcCan, new OreDictItemData(MTx.PVC, U*6));

        // PTFE can
        ptfeCan = MTEx.gt6xMTEReg.add("PTFE Canister", "Fluid Containers", 42, 32719, MultiTileEntityBarrelPlasticAdvanced.class, 0, 16, MTEx.PlasticBlock, gregapi.util.UT.NBT.make(NBT_MATERIAL, MTx.PTFE, gregapi.data.CS.NBT_HARDNESS, 1.0F, gregapi.data.CS.NBT_RESISTANCE, 3.0F, NBT_TANK_CAPACITY, 256000L, NBT_PLASMAPROOF, false, NBT_GASPROOF, true, NBT_ACIDPROOF, true));
        OM.data(ptfeCan, new OreDictItemData(MTx.PTFE, U*6));

        // Hydro cracker
        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        OreDictMaterial aMat;
        aMat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", 50, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateDouble   .dat(ANY.Cu), 'I', OP.plateDouble   .dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        aMat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", 51, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateTriple   .dat(ANY.Cu), 'I', OP.plateTriple   .dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        aMat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", 52, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuadruple.dat(ANY.Cu), 'I', OP.plateQuadruple.dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
        aMat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Hydro Cracker ("+aMat.getLocal()+")", "Basic Machines", 53, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "hydrocracker", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, hydroCracking, NBT_INV_SIDE_IN, SBIT_U|SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_TOP, NBT_INV_SIDE_OUT, SBIT_R|SBIT_B, NBT_INV_SIDE_AUTO_OUT, SIDE_BACK, NBT_TANK_SIDE_IN, SBIT_U|SBIT_L, NBT_TANK_SIDE_AUTO_IN, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_R|SBIT_B, NBT_TANK_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "PwP", "ZMZ", "ICI", 'M', OP.casingMachineDouble.dat(aMat), 'C', OP.plateQuintuple.dat(ANY.Cu), 'I', OP.plateQuintuple.dat(MT.Invar), 'P', OP.pipeMedium.dat(MT.StainlessSteel), 'Z', dust.dat(MT.OREMATS.Zeolite));
    }
}
