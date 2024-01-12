package org.altadoon.gt6x.features.pgm;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.Recipe;
import gregapi.util.ST;
import gregapi.worldgen.StoneLayer;
import gregapi.worldgen.StoneLayerOres;
import gregapi.worldgen.WorldgenObject;
import gregapi.worldgen.WorldgenOresLarge;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.common.MTx.*;
import static org.altadoon.gt6x.common.Log.LOG;

public class PgmProcessing extends GT6XFeature {
    public static final String FEATURE_NAME = "PGMProcessing";
    private static final String FEATURE_SET = "featureSet";
    private static final String FEATURE_OREGEN = "overrideOregen";

    public enum PgmFeatureSet {
        Simple,
        Complex,
        Off,
    }

    private boolean overrideWorldgen = false;

    private PgmFeatureSet pgmFeatures = PgmFeatureSet.Off;

    @Override
    public void configure(Config cfg) {
        String configString = cfg.cfg.get(FEATURE_NAME, FEATURE_SET, PgmFeatureSet.Complex.name(), null, new String[]{PgmFeatureSet.Complex.name(), PgmFeatureSet.Simple.name()}).getString();
        pgmFeatures = PgmFeatureSet.valueOf(configString);
        overrideWorldgen = cfg.cfg.getBoolean(FEATURE_OREGEN, FEATURE_NAME, true, "Override some ore veins from gt6 default configs (for platinum ores). Does nothing if Simple mode is enabled. Disable if you provide your own config for platinum veins (ore.large.platinum), if you have no idea leave this as is.");
    }

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void preInit() {
        changeByProducts();
    }

    @Override
    public void init() {
        switch(pgmFeatures) {
            case Simple:
                addSimpleRecipes();
                break;
            case Complex:
                addWorldgen();
                addComplexRecipes();
        }
    }

    @Override
    public void postInit() {
        if (pgmFeatures == PgmFeatureSet.Complex && overrideWorldgen) {
            overrideWorldgen();
        }
    }

    @Override
    public void afterPostInit() {
        //TODO move some to prePostInit instead of disabling
        changeSludgeRecipes();

        for (OreDictMaterial tMat : new OreDictMaterial[] {MT.Ru, MT.Rh, MT.Pd, MT.Os, MT.Ir, MT.Pt, MT.Ni, MT.Mithril, MT.MeteoricIron, MT.OREMATS.Cooperite, MT.OREMATS.Sperrylite}) {
            disableOldLeachingRecipe(tMat);
            if (pgmFeatures == PgmFeatureSet.Simple) {
                addPtGroupLeachingRecipeSimple(tMat);
            }
        }

        if (pgmFeatures == PgmFeatureSet.Complex) {
            for (OreDictMaterial tMat : new OreDictMaterial[] {MT.Pt, MT.OREMATS.Cooperite, MT.OREMATS.Sperrylite}) {
                addPtGroupLeachingRecipeComplex(tMat);
            }
            disableElectrolysis();
        }
    }

    private void overrideWorldgen() {
        // I don't bother with the small ores
        for (StoneLayer layer : StoneLayer.LAYERS) {
            if (layer.mStone == BlocksGT.GraniteBlack) {
                for (StoneLayerOres ores : layer.mOres) {
                    if (ores.mMaterial.mID == MT.Ir.mID)
                        ores.mMaterial = MT.Pt;
                }
            }
        }

        // remove old platinum vein since it is not possible to change the layers as they are final
        for (WorldgenObject obj : CS.ORE_OVERWORLD) {
            if (obj.mName.equals("ore.large.platinum")) {
                obj.mEnabled = false;
            }
        }
    }

    private void addWorldgen() {
        new WorldgenOresLarge("ore.large.chalcocite", true, 10, 120, 100, 4, 24,
                Chalcocite, MT.OREMATS.Chalcopyrite, MT.OREMATS.Pentlandite, MT.OREMATS.Cooperite,
                ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS);
        new WorldgenOresLarge("ore.large.platinum2", true, true, 40,  50,   5, 3, 16,
                MT.OREMATS.Cooperite, MT.Pt, MT.OREMATS.Sperrylite, MT.Pt,
                ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS, ORE_END, ORE_ASTEROIDS);
    }

    private void changeSludgeRecipes() {
        for (OreDictPrefix size : new OreDictPrefix[]{dust, dustSmall, dustTiny, crushedCentrifuged, crushedCentrifugedTiny}) {
            RM.Centrifuge.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, size.mat(MT.PlatinumGroupSludge, 1)).mEnabled = false;
        }

        RM.Bath.addRecipe1(true, 0, 64, new long[]{2500, 2500},
                dust.mat(MT.PlatinumGroupSludge, 2),
                FL.array(MT.AquaRegia.liquid(13*8*U8, false)),
                FL.array(
                    PtPdLeachingSolution.liquid(95*U8, false),
                    MT.NO.gas(2*8*U8, false)
                ),
                dustTiny.mat(PGMResidue, 9), dustTiny.mat(PGMResidue, 9)
        );
    }

    private void changeByProducts() {
        List<Short> undesiredByproducts = Arrays.asList(MT.Pd.mID, MT.Rh.mID, MT.Ir.mID, MT.Ru.mID, MT.Os.mID);

        for (OreDictMaterial mat : new OreDictMaterial[] {MT.Pt, MT.OREMATS.Sperrylite, MT.OREMATS.Cooperite, MT.MeteoricIron, MT.Eximite, MT.Vyroxeres, MT.Kalendrite, MT.Pd, MT.Rh, MT.Ir, MT.Ru, MT.Os }) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (undesiredByproducts.contains(byproduct.mID)) {
                    it.set(MT.PlatinumGroupSludge);
                }
            }
        }

        for (OreDictMaterial mat : new OreDictMaterial[] {MT.OREMATS.Pentlandite, MT.OREMATS.Chalcopyrite, MT.MeteoricIron, MT.Ni }) {
            mat.addOreByProducts(MT.PlatinumGroupSludge);
        }

        Chalcocite.addOreByProducts(MT.OREMATS.Chalcopyrite, MT.OREMATS.Cooperite, MT.OREMATS.Pentlandite, MT.OREMATS.Cobaltite, MT.PlatinumGroupSludge, MT.Au);
    }

    private void disableOldLeachingRecipe(OreDictMaterial inputOre) {
        Recipe rLarge = RM.Bath.findRecipe(null, null, true, Long.MAX_VALUE, null,
                FL.array(MT.AquaRegia.liquid(78*U8, true)),
                crushedPurified.mat(inputOre, 1)
        );
        Recipe rSmall = RM.Bath.findRecipe(null, null, true, Long.MAX_VALUE, null,
                FL.array(MT.AquaRegia.liquid(78*U8, true)),
                crushedPurifiedTiny.mat(inputOre, 9)
        );
        if (rLarge == null || rSmall == null) {
            LOG.error("missing recipe for mat {}: large: {}, small: {}\n", inputOre.mNameLocal, rLarge != null, rSmall != null);
            return;
        }
        rLarge.mEnabled = false;
        rSmall.mEnabled = false;
    }

    private void addPtGroupLeachingRecipeSimple(OreDictMaterial inputOre) {
        FluidStack[] fluidInputs = FL.array(MT.AquaRegia.liquid(13*8*U4, false));
        FluidStack[] fluidOutputs = FL.array(
                PtPdLeachingSolution.liquid(95*U4, false),
                MT.NO.gas(8*2*U4, false)
        );
        ItemStack[] itemOutputs = new ItemStack[6];
        itemOutputs[0] = crushedCentrifuged.mat(inputOre, 1);
        for (int i = 1; i < 6; i++) {
            itemOutputs[i] = crushedCentrifugedTiny.mat(PGMResidue, 8);
        }
        long[] chances = new long[] {10000, 500, 500, 500, 500, 500};

        RM.Bath.addRecipe1(true, 0, 256, chances, crushedPurified.mat(inputOre, 1), fluidInputs, fluidOutputs, itemOutputs);
        RM.Bath.addRecipe1(true, 0, 256, chances, crushedPurifiedTiny.mat(inputOre, 9), fluidInputs, fluidOutputs, itemOutputs);
    }

    private void addPtGroupLeachingRecipeComplex(OreDictMaterial inputOre) {
        FluidStack[] fluidInputs;
        FluidStack[] fluidOutputs;
        long residue_chance;

        if (inputOre.mID == MT.Pt.mID) { // "Native" Pt ore contains more Pt/Pd
            fluidInputs = FL.array(MT.AquaRegia.liquid(13*8*U4, true));
            fluidOutputs = FL.array(
                    PtPdLeachingSolution.liquid(95*U4, false),
                    MT.NO.gas(8*2*U4, false)
            );
            residue_chance = 1000;
        } else if (inputOre.mID == MT.OREMATS.Sperrylite.mID) { // 1/3 pt, but changed to 2/5 for rounding
            fluidInputs = FL.array(MT.AquaRegia.liquid(13*8*U10, true));
            fluidOutputs = FL.array(
                    PtPdLeachingSolution.liquid(95*U10, false),
                    MT.NO.gas(8*2*U10, false)
            );
            residue_chance = 333;
        } else if (inputOre.mID == MT.OREMATS.Cooperite.mID) { // 1/2 PGM (PtS)
            fluidInputs = FL.array(MT.AquaRegia.liquid(13*8*U8, true));
            fluidOutputs = FL.array(
                    PtPdLeachingSolution.liquid(95*U8, false),
                    MT.NO.gas(8*2*U8, false)
            );
            residue_chance = 500;
        } else {
            LOG.warn("invalid PGM ore");
            return;
        }

        ItemStack[] itemOutputs = new ItemStack[6];
        long[] itemChances = new long[6];
        for (int i = 0; i < 6; i++) {
            itemOutputs[i] = crushedCentrifugedTiny.mat(PGMResidue, 8);
            itemChances[i] = residue_chance;
        }

        RM.Bath.addRecipe1(true, 0, 256, itemChances, crushedPurified.mat(inputOre, 1), fluidInputs, fluidOutputs, itemOutputs);
        RM.Bath.addRecipe1(true, 0, 256, itemChances, crushedPurifiedTiny.mat(inputOre, 9), fluidInputs, fluidOutputs, itemOutputs);
    }

    public void addComplexRecipes() {
        // Pt/Pd separation
        RM.Bath.addRecipe1(true, 0, 200, dust.mat(NH4Cl, 20), PtPdLeachingSolution.liquid(95*U, false), PdChlorideSolution.liquid(70*U, false), dust.mat(AmmoniumHexachloroplatinate, 45));
        RM.Bath.addRecipe1(true, 0, 200, dust.mat(NH4Cl, 4), MT.ChloroplatinicAcid.liquid(9*U, true), MT.HCl.gas(2, false), dust.mat(AmmoniumHexachloroplatinate, 9));
        RM.Mixer.addRecipe0(true, 16, 100, FL.array(PdChlorideSolution.liquid(35*U, true), MT.NH3.gas(4*U, true)), FL.array(MT.H2O.liquid(8*3*U, false), MT.HCl.gas(4*2*U, false)), dust.mat(TetraamminepalladiumChloride, 7));
        RMx.Thermolysis.addRecipe1(true, 16, 50, dust.mat(AmmoniumHexachloroplatinate, 9), NF, MT.Cl.gas(4*U, false), dust.mat(NH4Cl, 4), dust.mat(MT.Pt, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 50, dust.mat(TetraamminepalladiumChloride, 7), ZL_FS, FL.array(MT.Cl.gas(2*U, false), MT.NH3.gas(4*U, false)), dust.mat(MT.Pd, 1));

        // Rh separation (Yo Greg, wanna buy some international units of amount of substance? I divided some amounts by three here (and the PGMs by 2) because it would become rather annoying otherwise). But it adds up to a closed-loop process now.
        RM.Bath.addRecipe1(true, 0, 100, dust.mat(PGMResidue, 4), FL.array(MT.K2S2O7.liquid(14*U, true)), FL.array(RhodiumPotassiumSulfate.liquid(13*U, false), MT.SO2.gas(3*U, false)), dust.mat(RuOsIrResidue, 3));
        for (FluidStack tWater : FL.waters(8000)) {
            RM.Mixer.addRecipe0(true, 16, 100, FL.array(RhodiumPotassiumSulfate.liquid(13*U, true) , tWater), FL.array(RhodiumSulfateSolution.liquid(7*U, false)), dust.mat(MT.K2SO4, 14));
        }
        RM.Bath.addRecipe1(true, 0, 100, dust.mat(MT.Zn, 1), FL.array(RhodiumSulfateSolution.liquid(7*U, true)), FL.array(MT.WhiteVitriol.liquid(6*U, false)), dust.mat(MT.Rh, 1));

        // Ir separation
        RM.Roasting.addRecipe1(true, 16, 150, dust.mat(RuOsIrResidue, 9), Ozone.gas(20*U, true), RuOsO4.gas(30*U, false), dust.mat(IrO2, 9));
        RM.Roasting.addRecipe1(true, 64, 150, dust.mat(IrO2, 3), MT.H.gas(4*U, true), MT.H2O.liquid(6*U, false), dust.mat(MT.Ir, 1));

        // Ru/Os separation
        RM.Mixer.addRecipe0(true, 16, 100, FL.array(RuOsO4.gas(10*U, true), MT.HCl.gas(12*U, true)), FL.array(ChlororuthenicAcid.liquid(15*U, false), MT.O.gas(2*U, false)), dust.mat(OsO4, 5));
        RM.Bath.addRecipe1(true, 0, 100, dust.mat(NH4Cl, 4), FL.array(ChlororuthenicAcid.liquid(15*U, true)), FL.array(MT.H2O.liquid(6*U, false), MT.HCl.gas(4*U, false)), dust.mat(AmmoniumHexachlororuthenate, 9));
        RM.Roasting.addRecipe1(true, 64, 150, dust.mat(AmmoniumHexachlororuthenate, 9), MT.H.gas(4*U, true), MT.HCl.gas(8*U, false), dust.mat(NH4Cl, 4), dust.mat(MT.Ru, 1));
        RM.Roasting.addRecipe1(true, 64, 150, dust.mat(OsO4, 5), MT.H.gas(8*U, true), MT.H2O.liquid(12*U, false), dust.mat(MT.Os, 1));
        RM.Mixer.addRecipe0(true, 16, 150, FL.array(RuO4.gas(5*U, true), MT.H.gas(8*U, true)), MT.H2O.liquid(12*U, false), dust.mat(MT.Ru, 1));
    }

    public void addSimpleRecipes() {
        RM.Electrolyzer.addRecipe1(true, 64, 200, ST.tag(0), FL.array(PtPdLeachingSolution.liquid(95*U, false)), FL.array(MT.HCl.gas(32*2*U, false), MT.H2O.liquid(4*3*U, false), MT.O.gas(6*2*U, false)), dustSmall.mat(MT.Pt, 5*4), dustSmall.mat(MT.Pd, 2*4));
        RM.Roasting.addRecipe1(true, 64, 250, dust.mat(PGMResidue, 4), FL.array(Ozone.gas(8 * U, true)), FL.array(RuOsO4.gas(10*U, false)), dust.mat(IrRhOxide, 6));
        RM.Distillery.addRecipe1(true, 64, 150, ST.tag(0), FL.array(RuOsO4.gas(10 * U, false)), FL.array(MT.O.gas(8*U, false)), dust.mat(MT.Os, 1), dust.mat(MT.Ru, 1));
        RM.Roasting.addRecipe1(true, 64, 150, dust.mat(IrRhOxide, 6), MT.H.gas(8*U, true), MT.H2O.liquid(12*U, false), dust.mat(MT.Rh, 1), dust.mat(MT.Ir, 1));
    }

    private void disableElectrolysis() {
        for (Recipe r : RM.Electrolyzer.mRecipeList) {
            if (r.mFluidInputs.length >= 1 && (r.mFluidInputs[0].isFluidEqual(MT.ChloroplatinicAcid.mLiquid))) {
                r.mEnabled = false;
            }
        }
    }
}
