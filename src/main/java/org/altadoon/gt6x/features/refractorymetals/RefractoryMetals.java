package org.altadoon.gt6x.features.refractorymetals;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.worldgen.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;

public class RefractoryMetals extends GT6XFeature {
    public static final String FEATURE_NAME = "RFMProcessing";
    private static final String CHROMIUM_CHEM = "complexChromiumRefining";
    private static final String COLTAN_CHEM = "complexColtanRefining";
    private static final String FEATURE_OREGEN = "overrideOregen";
    private boolean complexChromiumRefining = true;
    private boolean complexColtanRefining = true;
    private boolean overrideWorldgen = false;

    @Override
    public void configure(Config cfg) {
        complexChromiumRefining = cfg.cfg.getBoolean(CHROMIUM_CHEM, FEATURE_NAME, true, "Refine pure chromium using aluminothermic reaction of chromium(III) oxide");
        complexColtanRefining = cfg.cfg.getBoolean(COLTAN_CHEM, FEATURE_NAME, true, "Refine Coltan ores in a more realistic way");
        overrideWorldgen = cfg.cfg.getBoolean(FEATURE_OREGEN, FEATURE_NAME, true, "Override some ore veins from gt6 default configs. Does nothing if Simple mode is enabled. Disable if you provide your own config for molybdenum veins (ore.large.molybdenum), if you have no idea leave this as is.");
    }

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void preInit() {
        changeMaterialProperties();
        changeByProducts();
    }

    @Override
    public void init() {
        if (overrideWorldgen) {
            addWorldgen();
        }

    }

    @Override
    public void postInit() {
        if (overrideWorldgen) {
            changeWorldgen();
        }

        addRecipes();
    }

    @Override
    public void afterPostInit() {

    }

    private void changeMaterialProperties() {
        MT.OREMATS.Molybdenite.setSmelting(MT.OREMATS.Molybdenite, U);
        MT.OREMATS.Stolzite.setSmelting(MT.OREMATS.Stolzite, U);
        MT.OREMATS.Pinalite.setSmelting(MT.OREMATS.Pinalite, U);
        MT.OREMATS.Powellite.setSmelting(MT.OREMATS.Powellite, U).remove(TD.Processing.CENTRIFUGE);
        MT.OREMATS.Wulfenite.setSmelting(MT.OREMATS.Wulfenite, U).remove(TD.Processing.CENTRIFUGE);
        MT.V2O5.setSmelting(MT.V2O5, U).remove(TD.Processing.ELECTROLYSER);
        MT.Zircon.setSmelting(MT.Zircon, U).remove(TD.Processing.ELECTROLYSER);

        if (complexColtanRefining) {
            MT.OREMATS.Coltan.uumMcfg(0, MT.Fe, U, MT.Mn, U, MT.Ta, 2 * U, MT.Nb, 2*U, MT.O, 12 * U)
                    .tooltip("(Fe, Mn)(Ta, Nb)" + NUM_SUB[2] + "O" + NUM_SUB[6])
                    .addSourceOf(MT.Fe)
                    .remove(TD.Processing.CENTRIFUGE);
            MT.OREMATS.Tantalite.uumMcfg(0, MT.Fe, U, MT.Mn, U, MT.Ta, 4 * U, MT.O, 12 * U)
                    .tooltip("(Fe, Mn)Ta" + NUM_SUB[2] + "O" + NUM_SUB[6])
                    .addSourceOf(MT.Fe)
                    .remove(TD.Processing.ELECTROLYSER);
            MT.OREMATS.Columbite.uumMcfg(0, MT.Fe, U, MT.Mn, U, MT.Nb, 4 * U, MT.O, 12 * U)
                    .tooltip("(Fe, Mn)Nb" + NUM_SUB[2] + "O" + NUM_SUB[6])
                    .addSourceOf(MT.Fe)
                    .remove(TD.Processing.ELECTROLYSER);
            MT.Ta2O5.remove(TD.ItemGenerator.ORES);
            MT.Nb2O5.remove(TD.ItemGenerator.ORES);
        }

        if (complexChromiumRefining) {
            MT.OREMATS.Chromite.setSmelting(MT.OREMATS.Chromite, U);
            MT.OREMATS.Chromite.remove(TD.Processing.ELECTROLYSER);
            MT.StainlessSteel.remove(TD.Processing.CENTRIFUGE);
            MT.Kanthal.remove(TD.Processing.CENTRIFUGE);
            MT.Ruby.uumMcfg(6, MT.Al2O3, 5*U, MTx.Cr2O3, U);
        }
    }

    private void changeByProducts() {
        MTx.Vanadinite.addOreByProducts(MT.OREMATS.Galena, MT.OREMATS.Wulfenite, MT.OREMATS.Barite, MT.OREMATS.Stolzite);

        for (OreDictMaterial mat : new OreDictMaterial[] { MT.Zircon, MT.Eudialyte, MT.Azurite, MT.Zr }) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (byproduct.mID == MT.Hf.mID) {
                    it.set(MT.Zircon);
                }
            }
        }

        if (complexColtanRefining) {
            MT.OREMATS.Coltan.mByProducts.clear();
            MT.OREMATS.Coltan.addOreByProducts(MT.MnO2, MT.OREMATS.Ilmenite, MT.Fe2O3);
        }
    }

    private void changeWorldgen() {
        for (WorldgenObject obj : ORE_OVERWORLD) {
            disableWorldgen(obj);
        }
        for (WorldgenObject obj : ORE_END) {
            disableWorldgen(obj);
        }
        for (List<WorldgenObject> list : GEN_FLOOR) {
            for (WorldgenObject obj : list)
                if (Objects.equals(obj.mName, "ore.bedrock.vanadium"))
                    obj.mEnabled = false;
        }

        if (complexColtanRefining) {
            for (StoneLayer layer : StoneLayer.LAYERS) {
                if (layer.mStone == BlocksGT.GraniteRed) {
                    colTanToColtan(layer.mOres);
                }
            }
            colTanToColtan(StoneLayer.MAP.get(MT.Dolomite).get(MT.STONES.Quartzite));
            colTanToColtan(StoneLayer.MAP.get(MT.STONES.Quartzite).get(MT.Dolomite));
        }
    }

    private void colTanToColtan(List<StoneLayerOres> layer) {
        // Q: why remove Columbite and Tantalite from worldgen?
        // A: Because we would need multiple solutions with different concentrations of Nb/Ta compounds during processing.
        //    this would make it a mess. Anyway, in practice columbite/tantalite are usually mixed.
        for (StoneLayerOres ores : layer) {
            if (ores.mMaterial.mID == MT.OREMATS.Columbite.mID || ores.mMaterial.mID == MT.OREMATS.Tantalite.mID)
                ores.mMaterial = MT.OREMATS.Coltan;
        }
    }

    private void disableWorldgen(WorldgenObject obj) {
        switch (obj.mName) {
            case "ore.large.molybdenum":
            case "ore.large.tungstate":
                obj.mEnabled = false;
                break;
        }
    }

    private void addWorldgen() {
        new WorldgenOresLarge("ore.large.stolzite", true, true, 20,  50,   5, 3, 16,
                MTx.Vanadinite, MT.OREMATS.Stolzite, MT.OREMATS.Pinalite, MT.OREMATS.Wulfenite,
                ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS, ORE_END);
        new WorldgenOresLarge("ore.large.alkalineearthtungstate", true, true, 20,  50,   5, 3, 16,
                MT.OREMATS.Scheelite, MT.OREMATS.Tungstate, MT.OREMATS.Wolframite, MT.OREMATS.Powellite,
                ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS, ORE_END);
        new WorldgenOresLarge("ore.large.wolframite", true, true, 20,  50,   5, 3, 16,
                MT.OREMATS.Huebnerite, MT.OREMATS.Ferberite, MTx.Wolframite, MT.OREMATS.Molybdenite,
                ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS, ORE_END);
        new WorldgenOresBedrock("ore.bedrock.vanadinite", true, true, 6000, MTx.Vanadinite, BlocksGT.FlowersA, 7, GEN_FLOOR); // Vanadium Flower
    }

    private void addRecipes() {
        // Common
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.HF.gas(U, true), MT.NH3.gas(U, true)), ZL_FS, dust.mat(MTx.NH4F, 2));
        //TODO use thermolysis oven
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MTx.NH4F, 2), ZL_FS, FL.array(MT.HF.gas(U, true), MT.NH3.gas(U, true)));
        RM.Mixer.addRecipe1(true, 16, 256, dust.mat(MT.Pd, 0), FL.array(MTx.Acetone.liquid(20*U, true), MT.H.gas(2*U, true)), FL.array(MTx.MIBK.liquid(19*U, false), MT.H2O.liquid(3*U, false)));

        // Mo, W
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.Powellite, 6), FL.array(MT.HCl.gas(4*U, true)), ZL_FS, dust.mat(MTx.H2MoO4, 7), dust.mat(MT.CaCl2, 3));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.Wulfenite, 6), FL.array(MT.HCl.gas(4*U, true)), ZL_FS, dust.mat(MTx.H2MoO4, 7), dust.mat(MTx.PbCl2, 3));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.Pinalite, 11), FL.array(MT.HCl.gas(8*U, true)), FL.Water.make(3000), dust.mat(MT.H2WO4, 7), dust.mat(MTx.PbCl2, 9));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.Stolzite,  6), FL.array(MT.HCl.gas(4*U, true)), ZL_FS, dust.mat(MT.H2WO4, 7), dust.mat(MTx.PbCl2, 3));
        RM.Bath.addRecipe1(true, 0, 1024, dust.mat(MTx.Wolframite,  12), FL.array(MT.HCl.gas(8*U, true)), ZL_FS, dust.mat(MT.H2WO4, 14), dust.mat(MT.MnCl2, 3), dust.mat(MT.FeCl2, 3));

        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MTx.H2MoO4, 7), NF, FL.DistW.make( 3000), dust.mat(MTx.MoO3, 4));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.MoO3, 4), FL.array(MT.H.gas(6*U, true)), FL.array(MT.H2O.liquid(9*U, false)), dust.mat(MT.Mo, 1));

        // V
        RM.Bath.addRecipe1(true, 0, 512*3, dust.mat(MTx.Vanadinite, 21), FL.array(MT.SaltWater.liquid(4*6*U, true)), FL.array(MTx.NaVO3Solution.liquid(3*11*U, false)), dust.mat(MTx.PbCl2, 6), dust.mat(MTx.PbO, 3));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MTx.NH4Cl, 2), MTx.NaVO3Solution.liquid(11*U, true), MT.SaltWater.liquid(8*U, false), dust.mat(MTx.NH4VO3, 5));
        //TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 128, dust.mat(MTx.NH4VO3, 10), ZL_FS, FL.array(MT.NH3.gas(2*U, false), MT.H2O.liquid(U, false)), dust.mat(MT.V2O5, 7));
        RM.Bath.addRecipe1(true, 0, 512*3, dust.mat(MT.V2O5, 21), FL.array(MT.Al.liquid(10*U, true)), FL.array(MT.V.liquid(6*U, false), MT.Al2O3.liquid(25*U, false)));

        // Cr
        if (complexChromiumRefining) {
            // we assume SiO2 is present in Chromite which comes out as slag. Part of it remains in the hematite which can be used in a blast furnace. 6 units of SiO2 are added to the left hand of the equation.
            //TODO fix?
            RM.Mixer.addRecipe(true, new ItemStack[]{OM.dust(MT.OREMATS.Chromite, 28*U), OM.dust(MT.CaCO3, 5*U2), OM.dust(MT.Na2CO3, 48*U)}, ST.array(dust.mat(MTx.CrSlag, 76)), null, null, FL.array(FL.Air.make(14*4000)), FL.array(MT.CO2.gas(10*3*U, false)), 3*512, 16, 0);
            RM.Mixer.addRecipe(true, new ItemStack[]{OM.dust(MT.OREMATS.Chromite, 28*U), OM.dust(MT.CaCO3, 5*U2), OM.dust(MT.Na2CO3, 48*U)}, ST.array(dust.mat(MTx.CrSlag, 76)), null, null, FL.array(MT.O.gas(7*2*U, true)), FL.array(MT.CO2.gas(10*3*U, false)), 3*512, 16, 0);

            for (FluidStack tWater : FL.waters(3000)) {
                RM.Bath.addRecipe1(true, 0, 3*256, dust.mat(MTx.CrSlag, 76), FL.mul(tWater, 8), MTx.Na2CrO4Solution.liquid(8*10*U, false), dust.mat(MT.Fe2O3, 10), gem.mat(MTx.Slag, 10));
                RM.Mixer.addRecipe1(true, 16, 3*32, dust.mat(MT.Na2CO3, 6), FL.array(FL.copy(tWater)), MTx.Na2CO3Solution.liquid(9*U, false), ZL_IS);
                RM.Mixer.addRecipe0(true, 16, 3*128, FL.array(MTx.Na2CrO4Solution.liquid(20*U, true), MT.CO2.gas(6*U, true), FL.mul(tWater, 2)), MTx.DichromateSoda.liquid(32*U, true), ZL_IS);
                RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.CrSodaMixture, 11), tWater, MTx.Na2CO3Solution.liquid(9*U, false), dust.mat(MTx.Cr2O3, 5));
            }
            //TODO use thermolysis oven
            RM.Drying.addRecipe0(true, 16, 3*128, FL.array(MTx.DichromateSoda.liquid(32*U, true)), FL.array(MTx.Na2CO3Solution.liquid(9*U, false), MT.DistWater.liquid(9*U, false), MT.CO2.gas(3*U, false)), dust.mat(MTx.Na2Cr2O7, 11));
            RM.Drying.addRecipe0(true, 16, 3*128, FL.array(MTx.Na2CO3Solution.liquid(9*U, false)), FL.array(MT.DistWater.liquid(3*U, false)), dust.mat(MT.Na2CO3, 6));

            for (ItemStack coal : new ItemStack[]{dust.mat(MT.Charcoal, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1)}) {
                RM.BurnMixer.addRecipe2(true, 16, 64, ST.mul(2, coal), dust.mat(MTx.Na2Cr2O7, 11), ZL_FS, MT.CO.gas(2*U, false), dust.mat(MTx.CrSodaMixture, 11));
            }
            RM.Bath.addRecipe1(true, 0, 512, dust.mat(MTx.Cr2O3, 5), MT.Al.liquid(2*U, true), MT.Cr.liquid(2*U, false), dust.mat(MT.Al2O3, 5));
        }

        // Zr,Hf
        for (ItemStack coal : new ItemStack[]{dust.mat(MT.Charcoal, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1), dust.mat(MT.Graphite, 1)}) {
            RM.BurnMixer.addRecipe2(true, 16, 256, ST.mul(2, coal), dust.mat(MT.Zircon, 12), FL.array(MT.Cl.gas(8*U, true), MT.CaCO3.liquid(10*U, true)), FL.array(MTx.ZrHfCl4.gas(10*U, false), MT.CO2.gas(12*U, false)), gem.mat(MTx.Slag, 10));
            RM.BurnMixer.addRecipe2(true, 16, 256, coal, dust.mat(MT.Zircon, 6), FL.array(MT.Cl.gas(4*U, true), MT.CaCO3.liquid(5*U, true)), FL.array(MTx.ZrHfCl4.gas(5*U, false), MT.CO2.gas(6*U, false)), gem.mat(MTx.Slag, 5));
        }
        RM.Distillery.addRecipe1(true, 64, 256, ST.tag(0), FL.array(MTx.ZrHfCl4.gas(5*U, true)), FL.array(MTx.ZrCl4.gas(49*U10, false), MTx.HfCl4.gas(U10, false)));
        RM.Bath.addRecipe1(true, 0, 512, OM.dust(MT.Na, U*4), MTx.ZrCl4.gas(5*U, T), NF, OM.dust(MT.Zr), OM.dust(MT.NaCl , U*2), OM.dust(MT.NaCl , U*2), OM.dust(MT.NaCl , U*2), OM.dust(MT.NaCl , U*2));
        RM.Bath.addRecipe1(true, 0, 512, OM.dust(MT.Mg, U*2), MTx.ZrCl4.gas(5*U, T), NF, OM.dust(MT.Zr), OM.dust(MT.MgCl2, U*2), OM.dust(MT.MgCl2, U*2), OM.dust(MT.MgCl2, U*2));
        RM.Bath.addRecipe1(true, 0, 512, OM.dust(MT.Na, U*4), MTx.HfCl4.gas(5*U, T), NF, OM.dust(MT.Hf), OM.dust(MT.NaCl , U*2), OM.dust(MT.NaCl , U*2), OM.dust(MT.NaCl , U*2), OM.dust(MT.NaCl , U*2));
        RM.Bath.addRecipe1(true, 0, 512, OM.dust(MT.Mg, U*2), MTx.HfCl4.gas(5*U, T), NF, OM.dust(MT.Hf), OM.dust(MT.MgCl2, U*2), OM.dust(MT.MgCl2, U*2), OM.dust(MT.MgCl2, U*2));

        // Nb, Ta
        if (complexColtanRefining) {
            RM.Bath.addRecipe1(true, 0, 1024, OM.dust(MT.OREMATS.Coltan, 18*U), MT.HF.gas(28*2*U, true), MTx.ColtanFAqSolution.liquid(74*U, false), NI);
            RM.Mixer.addRecipe0(true, 16, 1024, FL.array(MTx.ColtanFAqSolution.liquid(74*U, true), MTx.MIBK.liquid(38*U, true)), FL.array(MTx.NbTaFOrganicSolution.liquid(76*U, false), MTx.FeMnF2Solution.liquid(36*U, false)));
            RM.Electrolyzer.addRecipe1(true, 64, 1024, ST.tag(0), FL.array(MTx.FeMnF2Solution.liquid(36*U, true)), FL.array(MT.HF.gas(8*U, false), MT.H.gas(20*U, false), MT.O.gas(10*U, false)), OM.dust(MT.Fe, U), OM.dust(MT.Mn, U));
            for (FluidStack tWater : FL.waters(3000)) {
                RM.Mixer.addRecipe0(true, 16, 1024, FL.array(MTx.NbTaFOrganicSolution.liquid(76*U, true), MT.NH3.gas(10*U, true), FL.mul(tWater, 13)), FL.array(MTx.TaFOrganicSolution.liquid(58*U, false), MTx.NH4FSolution.liquid(10*6*U, false)), OM.dust(MT.Nb2O5, 7*U));
                RM.Mixer.addRecipe0(true, 16, 1024, FL.array(MTx.TaFOrganicSolution.liquid(58*U, true), MT.NH3.gas(14*U, true), FL.mul(tWater, 19)), FL.array(MTx.MIBK.liquid(38*U, false), MTx.NH4FSolution.liquid(14*6*U, false)), OM.dust(MT.Ta2O5, 7*U));
            }
            //TODO use thermolysis
            RM.Drying.addRecipe1(true, 16, 128, ST.tag(0), FL.array(MTx.NH4FSolution.liquid(6*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.HF.gas(2*U, false), MT.NH3.gas(U, false)));
            RM.Mixer.addRecipe1(true, 16, 1024, dust.mat(MT.KF, 8), FL.array(MTx.TaFOrganicSolution.liquid(58*U, true)), FL.array(MTx.MIBK.liquid(38*U, false), MT.HF.gas(8*U, false)), OM.dust(MT.K2TaF7, 20*U));
            RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.Nb2O5, 7), MT.Ca.liquid(5*U, true), MT.Nb.liquid(2*U, false), dust.mat(MT.Quicklime, 10));
            RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.Ta2O5, 7), MT.Ca.liquid(5*U, true), MT.Quicklime.liquid(10*U, false), dust.mat(MT.Ta, 2));
            RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.Ta2O5, 7), MT.Al.liquid(10*U3, true), MT.Al2O3.liquid(25*U3, false), dust.mat(MT.Ta, 2));
        }
    }

}
