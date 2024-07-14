package org.altadoon.gt6x.features.basicchem;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerMaterial;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.worldgen.StoneLayer;
import gregapi.worldgen.StoneLayerOres;
import gregapi.worldgen.WorldgenObject;
import gregapi.worldgen.WorldgenOresLarge;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.FLx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ILx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.ListIterator;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Prefix.*;
import static gregapi.data.TD.Prefix.ORE_PROCESSING_BASED;
import static org.altadoon.gt6x.common.MTx.PdCl2;

/**
 * This feature contains some basic chemistry recipes shared by other features. Disabling this makes some items uncraftable.
 */
public class BasicChemistry extends GT6XFeature {
    @Override
    public String name() {
        return "BasicChemistry";
    }

    @Override
    public void preInit() {
        changeMaterialProperties();
        changeByProducts();
    }

    @Override
    public void init() {
        addWorldgen();
    }

    @Override
    public void beforeGt6PostInit() {
        addOverrideRecipes();
    }

    @Override
    public void postInit() {
        addRecipes();
        overrideWorldgen();
    }

    public void afterGt6PostInit() {
        changeRecipes();
    }

    private void changeMaterialProperties() {
        MT.Dolomite.setSmelting(MTx.CalcinedDolomite, 2*U5);
        MT.CaCO3.setSmelting(MTx.CaO, 2*U5);
        MT.MgCO3.setSmelting(MTx.MgO, 2*U5);

        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.H3BO3, MT.Bone, MT.NaOH, MT.PO4, MT.Sodalite, MT.Lazurite, MT.Glycerol, MT.Glyceryl }) {
            mat.remove(TD.Processing.ELECTROLYSER);
        }
    }

    private void changeByProducts() {
        for (OreDictMaterial mat : MT.ALL_MATERIALS_REGISTERED_HERE) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();

                // Phosphates
                if (byproduct.mID == MT.PO4.mID) {
                    it.set(MTx.Hydroxyapatite);
                } else if (byproduct.mID == MT.Phosphorus.mID) {
                    it.set(MT.Phosphorite);
                } else if (ANY.Phosphorus.mToThis.contains(byproduct)) {
                    it.remove();
                // REE, Ba, Sr
                } else if (byproduct.mID == MT.Y.mID || byproduct.mID == MT.Ce.mID || byproduct.mID == MT.La.mID) {
                    it.set(MT.OREMATS.Bastnasite);
                } else if (byproduct.mID == MT.Ba.mID) {
                    it.set(MT.OREMATS.Barite);
                } else if (byproduct.mID == MT.Sr.mID) {
                    it.set(MT.OREMATS.Celestine);
                } else if (byproduct.mID == MT.RareEarth.mID) {
                    it.set(MT.Monazite);
                // These ores should not occur native
                } else if (byproduct.containsAny(TD.Atomic.ALKALI_METAL, TD.Atomic.ALKALINE_EARTH_METAL, TD.Atomic.SCANDIUM_GROUP, TD.Atomic.LANTHANIDE, TD.Atomic.ICOSAGEN, TD.Atomic.HALOGEN)) {
                    it.remove();
                }
            }
        }

        MTx.Hydroxyapatite.addOreByProducts(MT.Apatite, MT.Phosphorite, MT.FluoriteYellow);
    }

    private void addWorldgen() {
        new WorldgenOresLarge("ore.large.apatite2", true, true, 40, 60, 60, 3, 16,
                MT.Apatite, MTx.Hydroxyapatite, MT.Phosphorite, MT.Apatite,
                ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS);
    }

    protected void addRecipes() {
        addSolutionRecipes();

        // Phosphoric Acid
        for (OreDictMaterial phosphorus : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusRed, MT.PhosphorusWhite}) {
            RM.Mixer.addRecipe1(true, 16,  16, dust.mat(phosphorus, 5), FL.array(MT.H2SO4.liquid(3*7*U, true)), FL.array(MTx.H3PO4.liquid(8*U, false)), dust.mat(MT.CaSO4, 18));
        }
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MT.Phosphorite, 9), FL.array(MT.H2SO4.liquid(5*7*U, true)), FL.array(MTx.H3PO4.liquid(12*U, false), MT.HF.gas(U*2, false)), dust.mat(MT.CaSO4, 30));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MT.Apatite, 9), FL.array(MT.H2SO4.liquid(5*7*U, true)), FL.array(MTx.H3PO4.liquid(12*U, false), MT.HCl.gas(U*2, false)), dust.mat(MT.CaSO4, 30));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MTx.Hydroxyapatite, 10), FL.array(MT.H2SO4.liquid(5*7*U, true)), FL.array(MTx.H3PO4.liquid(12*U, false), MT.H2O.liquid(U*3, false)), dust.mat(MT.CaSO4, 30));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MTx.P2O5, 7), FL.array(MT.H2O.liquid(3*3*U, true)), FL.array(MTx.H3PO4.liquid(16*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.NaOH, 9), MTx.H3PO4.liquid(8*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.Na3PO4, 8));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.Al2O3, 5), MTx.H3PO4.liquid(16*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.AlPO4, 12));

        RM.Electrolyzer.addRecipe1(true, 32, 256, ST.tag(1), FL.array(MTx.H3PO4.liquid(16*U, true)), FL.array(MT.H2O.liquid(9*U, false), MT.O.gas(5*U, false)), dust.mat(MT.P, 2));

        // Phosphates
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.AlPO4, 6), FL.array(MTx.NaOHSolution .liquid(18*U, true)), FL.array(MTx.Na3PO4Solution.liquid(14*U, false), MT.H2O.liquid(3*U, false)), dust.mat(MT.AlO3H3, 7));
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MT .NaOH , 9), FL.array(MTx.AlPO4Solution.liquid(12*U, true)), FL.array(MTx.Na3PO4Solution.liquid(14*U, false)), dust.mat(MT.AlO3H3, 7));
        for (FluidStack water : FL.waters(3000)) {
            RM.Bath.addRecipe2(true, 0, 128, dust.mat(MTx.AlPO4, 6), dust.mat(MT.NaOH, 9), FL.mul(water, 2), MTx.Na3PO4Solution.liquid(14 * U, false), dust.mat(MT.AlO3H3, 7));
        }
        RM.Mixer.addRecipe0(true, 16, 800, FL.array(MTx.Na3PO4Solution.liquid(28 * U, true), MT.H2SO4.liquid(21 * U, true)), FL.array(MTx.Na2SO4Solution.liquid(30 * U, false), MTx.H3PO4.liquid(16*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.Na3PO4, 8), MT.HCl.gas(6*U, true), MTx.H3PO4.liquid(8*U, false), dust.mat(MT.NaCl, 6));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.Na3PO4, 16), MT.H2SO4.liquid(21*U, true), MTx.H3PO4.liquid(16*U, false), dust.mat(MT.Na2SO4, 21));

        // Nitrous Acid
        RM.CryoMixer.addRecipe1(true, 16, 64, ST.tag(2), FL.array(MT.NO.gas(2*U, true), MT.NO2.gas(3*U, true)), FL.array(MTx.N2O3.liquid(5*U, false)));
        for (FluidStack water : FL.waters(3000))
            RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.N2O3.liquid(5*U, true), water), FL.array(MTx.HNO2.liquid(8*U, false)));

        // Bromine chemistry
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Pt, 0), FL.array(MT.H.gas(U, true), MT.Br.liquid(U, true)), FL.array(MTx.HBr.gas(2*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Asbestos, 0), FL.array(MT.H.gas(U, true), MT.Br.liquid(U, true)), FL.array(MTx.HBr.gas(2*U, false)));
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.HBr.gas(2*U, true), MT.Cl.gas(U, true)), FL.array(MT.Br.liquid(U, false), MT.HCl.gas(2*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.NaBr, 2), MT.Cl.gas(U, true), MT.Br.liquid(U, false), dust.mat(MT.NaCl, 2));
        RM.Mixer.addRecipe1(true, 16, 48, dust.mat(MT.NaOH, 3), MTx.HBr.gas(2*U, true), MT.H2O.liquid(3*U, false), dust.mat(MTx.NaBr, 2));
        for (FluidStack water : FL.waters(1000)) {
            RM.Electrolyzer.addRecipe1(true, 16, 640, OP.dustSmall.mat(MTx.NaBr, 1), FL.array(FL.mul(water, 3, 8, true)), FL.array(MT.Br.liquid(U8, false), MT.H.gas(U8, false), MT.O.gas(U8, false)), OM.dust(MT.NaOH, 3 * U8));
            RM.Electrolyzer.addRecipe1(true, 16, 2560, OP.dust.mat(MTx.NaBr, 1), FL.array(FL.mul(water, 3, 2, true)), FL.array(MT.Br.liquid(U2, false), MT.H.gas(U2, false)), OM.dust(MT.NaOH, 3 * U2));
        }
        RM.Electrolyzer.addRecipe1(true, 16, 2560, ST.tag(0), FL.array(MTx.NaBrSolution.liquid(5*U2, true)), FL.array(MT.Br.liquid(U2, false), MT.H.gas(U2, false)), OM.dust(MT.NaOH, 3 * U2));

        // Acid mixtures
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.H2O2.liquid(4*U, true), MT.H2SO4.liquid(7*U, true)), FL.array(MTx.PiranhaEtch.liquid(11*U, false)));

        // (Na,K)2S2O7
        RMx.Thermolysis.addRecipe1(true, 16, 100, dust.mat(MT.KHSO4, 2), ZL_FS, FL.array(MT.H2O.liquid(U, false)), dust.mat(MT.K2S2O7, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 100, dust.mat(MT.NaHSO4, 2), ZL_FS, FL.array(MT.H2O.liquid(U, false)), dust.mat(MT.Na2S2O7, 1));

        RM.Smelter.addRecipe1(true, 16, 144, blockDust.mat(MT.K2S2O7, 1), ZL_FS, MT.K2S2O7.liquid(9*U, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 16 , dust     .mat(MT.K2S2O7, 1), ZL_FS, MT.K2S2O7.liquid(U, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 4  , dustSmall.mat(MT.K2S2O7, 1), ZL_FS, MT.K2S2O7.liquid(U4, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 2  , dustTiny .mat(MT.K2S2O7, 1), ZL_FS, MT.K2S2O7.liquid(U9, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 1  , dustDiv72.mat(MT.K2S2O7, 1), ZL_FS, MT.K2S2O7.liquid(U72, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 144, blockDust.mat(MT.Na2S2O7, 1), ZL_FS, MT.Na2S2O7.liquid(9*U, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 16 , dust     .mat(MT.Na2S2O7, 1), ZL_FS, MT.Na2S2O7.liquid(U, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 4  , dustSmall.mat(MT.Na2S2O7, 1), ZL_FS, MT.Na2S2O7.liquid(U4, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 2  , dustTiny .mat(MT.Na2S2O7, 1), ZL_FS, MT.Na2S2O7.liquid(U9, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 1  , dustDiv72.mat(MT.Na2S2O7, 1), ZL_FS, MT.Na2S2O7.liquid(U72, false), ZL_IS);

        // Simple electrolysis
        RM.Electrolyzer.addRecipe2(true, 32, 256, ST.tag(2), dust.mat(MT.KF, 0), FL.array(MT.HF.gas(2*U, true)), FL.array(MT.F.gas(U, false), MT.H.gas(U, false)));
        RM.Electrolyzer.addRecipe2(true, 16, 128, ST.tag(1), dust.mat(MT.NaOH, 6), ZL_FS, FL.array(MT.H2O.liquid(3*U, false), MT.O.gas(U, false)), dust.mat(MT.Na, 2));
        RM.Electrolyzer.addRecipe2(true, 16, 128, ST.tag(1), dust.mat(MT.KOH, 6), ZL_FS, FL.array(MT.H2O.liquid(3*U, false), MT.O.gas(U, false)), dust.mat(MT.K, 2));
        RM.Electrolyzer.addRecipe1(true, 16, 128, ST.tag(1), MT.NaCl.liquid(2*U, true), MT.Cl.gas(U, false), dust.mat(MT.Na, 1));

        // Cheap Nitrogen from air
        for (OreDictMaterial coke : new OreDictMaterial[]{ MT.C, MT.CoalCoke, MT.Charcoal }) {
            RM.BurnMixer.addRecipe1(true, 16, 100, OM.dust(coke, U), FL.array(FL.Air       .make(4000)), FL.array(MT.N.gas(20*U7, false), MT.CO.gas(2*U, false)));
            RM.BurnMixer.addRecipe1(true, 16, 100, OM.dust(coke, U), FL.array(FL.Air_Nether.make(4000)), FL.array(MT.N.gas(20*U7, false), MT.CO.gas(2*U, false)));
            RM.BurnMixer.addRecipe1(true, 16, 100, OM.dust(coke, U), FL.array(FL.Air_End   .make(4000)), FL.array(MT.N.gas(20*U7, false), MT.CO.gas(2*U, false)));
        }

        // Haber-Bosch process
        RM.Mixer.addRecipe1(true, 64, 50, dust.mat(MT.Fe, 0), FL.array(MT.H.gas(3*U, true), MT.N.gas(U, true)), FL.array(MT.NH3.gas(U, false)));

        // Ostwald Process
        RM.Mixer.addRecipe1(true, 16, 500, dust.mat(MT.Pt, 0), FL.array(MT.NH3.gas(4*U, true), MT.O.gas(10*U, true)), FL.array(MT.NO.gas(8*U, false), MT.H2O.liquid(18*U, false)));

        // Koch reaction
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 64, ST.tag(4), FL.array(MT.H2SO4.liquid(U1000, true), MTx.Formaldehyde.gas(U, true), MT.CO.gas(2 * U, true), water), ZL_FS, dust.mat(MTx.GlycolicAcid, 1));
        }

        // HCl using Hydrogen
        RM.Mixer.addRecipe1(false, 16, 32, ST.tag(2), FL.array(MT.H.gas(U10, true), MT.Cl.gas(U10, true)), FL.array(MT.HCl.gas(2*U10, false)));

        // Oxalic Acid
        RM.Bath.addRecipe1(true, 0, 16, dust.mat(MT.Sugar, 1), FL.array(MT.HNO3.liquid(30*U24, true)), FL.array(MT.H2O.liquid(12*U24, false), MT.NO2.gas(18*U24, false)), dust.mat(MTx.OxalicAcid, 1));
        RM.Bath.addRecipe1(true, 0, 16, dust.mat(MTx.GlycolicAcid, 9), FL.array(MT.HNO3.liquid(10*U, true)), FL.array(MT.H2O.liquid(6*U, false), MT.NO.gas(2*U, false), MT.NO2.gas(3*U, false)), dust.mat(MTx.OxalicAcid, 8));

        // Ammonium salts
        RM.Mixer.addRecipe0(true, 16, 20, FL.array(MT.HCl.gas(2*U, false), MT.NH3.gas(U, false)), ZL_FS, dust.mat(MTx.NH4Cl, 1));
        RM.Mixer.addRecipe0(true, 16, 20, FL.array(MT.HF.gas(2*U, false), MT.NH3.gas(U, false)), ZL_FS, dust.mat(MTx.NH4F, 1));
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.H2SO4.liquid(7*U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.NH4SO4, 7));
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.HNO3.liquid(5*U, true), MT.NH3.gas(U, true)), ZL_FS, dust.mat(MTx.NH4NO3, 5));
        RM.Mixer.addRecipe0(true, 16, 256, FL.array(MT.H2SiF6.liquid(9*U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.NH4SiF6, 9));

        // Urea, Sulphamic Acid
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.NH3.gas(2*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.H2O.liquid(3*U, false)), dust.mat(MTx.Urea, 8*U));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.Urea, 8), FL.array(MT.H2SO4.liquid(7*U, true), MT.SO3.gas(4*U, true)), FL.array(MTx.H3NSO3.liquid(16*U, false), MT.CO2.gas(3*U, false)));

        // NF3 recipe
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.NH4SiF6, 0), FL.array(MT.NH3.gas(U, true), MT.F.gas(6*U, true)), FL.array(MTx.NF3.gas(U, false), MT.HF.gas(6*U, false)));

        // HCN, Phosgene
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Pt, 0), FL.array(MT.CH4.gas(U, true), MT.NH3.gas(U, true), MT.O.gas(3*U, true)), FL.array(MTx.HCN.liquid(3*U, false), MT.H2O.liquid(9*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.C, 0), FL.array(MT.CO.gas(2*U, true), MT.Cl.gas(2*U, true)), MTx.Phosgene.gas(U, false), NI);

        // NaCN, KCN, CuCN
        RM.Bath.addRecipe1(true, 0, 96, dust.mat(MT.NaOH, 3), MTx.HCN.gas(3*U, true), MTx.NaCNSolution.liquid(6*U, false), NI);
        RM.Bath.addRecipe1(true, 0, 96, dust.mat(MT.KOH , 3), MTx.HCN.gas(3*U, true), MTx.KCNSolution .liquid(6*U, false), NI);
        RM.Mixer.addRecipe0(true, 0, 96, FL.array(MTx.NaOHSolution.liquid(6*U, true), MTx.HCN.gas(3*U, true)), FL.array(MTx.NaCNSolution.liquid(6*U, false), MT.H2O.liquid(3*U, false)), NI);
        RM.Mixer.addRecipe0(true, 0, 96, FL.array(MTx.KOHSolution.liquid(6*U, true), MTx.HCN.gas(3*U, true)), FL.array(MTx.KCNSolution.liquid(6*U, false), MT.H2O.liquid(3*U, false)), NI);
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MT.BlueVitriol.liquid(6*U, true), MTx.NaCNSolution.liquid(12*U, true)), FL.array(MTx.Na2SO4Solution.liquid(10*U, false), MTx.C2N2.gas(2*U, false)), dust.mat(MTx.CuCN, 3));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Na, 1), MTx.C2N2.gas(2*U, true), NF, dust.mat(MTx.NaCN, 3));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CuBr, 2), MTx.NaCNSolution.liquid(6*U, true), MTx.NaBrSolution.liquid(5*U, false), dust.mat(MTx.CuCN, 3));

        // Sodium Silicates
        //RM.Autoclave.addRecipeX(true, 0, 15000, ST.array(ST.tag(1), dust.mat(MT.NaOH, 6), dust.mat(MT.SiO2, 3)), FL.Steam.make(3000*(long)STEAM_PER_WATER), MTx.Na2SiO3Solution.liquid(12*U, false), NI);
        RM.Autoclave.addRecipeX(true, 0, 15000, ST.array(dust.mat(MT.NaOH, 12), dust.mat(MT.SiO2, 3)), FL.Steam.make(3000*(long)STEAM_PER_WATER), MTx.Na4SiO4Solution.liquid(18*U, false), NI);
        RM.Autoclave.addRecipeX(true, 0, 15000, ST.array(dust.mat(MT.NaAlO2, 4), dust.mat(MT.SiO2, 15)), FL.Steam.make(3000*(long)STEAM_PER_WATER), NF, gem.mat(MT.OREMATS.Zeolite, 22));

        // O3
        RM.Lightning.addRecipe1(true, 64, 100, ST.tag(3), MT.O.gas(3*U, true), MTx.Ozone.gas(2*U, false), NI);

        // Roasting stuff
        for (String tOxygen : FluidsGT.OXYGEN) if (FL.exists(tOxygen)) {
            RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MTx.Chalcocite), FL.make(tOxygen, 667), MT.SO2.gas(3*U3, false), OM.dust(MT.Cu, 2*U3));
            RM.Roasting.addRecipe1(true, 16, 256, OM.dust(MT.P), FL.make(tOxygen, 2500), NF, OM.dust(MTx.P2O5, 7*U2));
        }
        final long[] tChances = new long[] {8000};
        for (String tAir : FluidsGT.AIR) if (FL.exists(tAir)) {
            RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MTx.Chalcocite), FL.make(tAir, 3000), MT.SO2.gas(3*U3, false), OM.dust(MT.Cu, 2*U3));
            RM.Roasting.addRecipe1(true, 16, 256, tChances, OM.dust(MT.P), FL.make(tAir, 7500), NF, OM.dust(MTx.P2O5, 7*U2));
        }

        // Misc ores
        OreDictMaterial tMat = MTx.Chalcocite;
        RM.Bath.addRecipe1(true,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurified    .mat(tMat, 1), FL.array(MT.H2SO4.fluid(7* U2, true)), FL.array(MT.BlueVitriol.fluid(3*U, false), MT.H.gas(U, false)), crushedCentrifuged.mat(tMat, 1), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2));
        RM.Bath.addRecipe1(true,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurifiedTiny.mat(tMat, 9), FL.array(MT.H2SO4.fluid(7* U2, true)), FL.array(MT.BlueVitriol.fluid(3*U, false), MT.H.gas(U, false)), crushedCentrifuged.mat(tMat, 1), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2));

        // coke reduction
        for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.LigniteCoke, 3),  dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1) )) {
            RM.BurnMixer.addRecipe2(true, 16, 128, dust.mat(MT.CaSO4, 12), coal, ZL_FS, FL.array(MT.CO2.gas(3 * U, false), MT.SO2.gas(6 * U, false)), dust.mat(MTx.CaO, 4));
        }

        // sodium/potassium/calcium/magnesium salts and related chemistry
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MTx.MgO, 2), MT.HCl.gas(4*U, true), MTx.MgCl2Solution.liquid(6*U, false), NI);
        RM.Bath.addRecipe1(true, 0, 64 , dust.mat(MTx.CaOH2, 5), MTx.MgCl2Solution.liquid(6*U, true), MTx.CaCl2Solution.liquid(6*U, false), dust.mat(MTx.MgOH2, 5));
        RM.Bath.addRecipe1(true, 0, 64 , dust.mat(MTx.CaOH2, 5), MT.HCl.gas(4*U, true), FL.array(MTx.CaCl2Solution.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 64 , dust.mat(MTx.CaO, 2), MT.HCl.gas(4*U, true), FL.array(MTx.CaCl2Solution.liquid(6*U, false)));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MT.OREMATS.Wollastonite, 5), MT.HCl.gas(4*U, true), MTx.CaCl2Solution.liquid(6*U, false), dust.mat(MT.SiO2, 3));

        for (FluidStack water : FL.waters(3000)) {
            RM.Bath .addRecipe2(true, 0 , 64, dust.mat(MTx.CaOH2, 5), dust.mat(MT.Na2CO3, 6), FL.mul(water, 2), MTx.NaOHSolution.liquid(12*U, false), dust.mat(MT.CaCO3, 5));
            RM.Bath .addRecipe2(true, 0 , 64, dust.mat(MTx.CaOH2, 5), dust.mat(MT.K2CO3, 6), FL.mul(water, 2), MTx.KOHSolution.liquid(12*U, false), dust.mat(MT.CaCO3, 5));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.NaHCO3, 6), FL.array(MT.HCl.gas(2*U, true), water), FL.array(MT.SaltWater.liquid(8*U, false), MT.CO2.gas(3*U, false)));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaOH2, 5), FL.array(water, MTx.Na2CO3Solution.liquid(9*U, true)), MTx.NaOHSolution.liquid(12*U, false), dust.mat(MT.CaCO3, 5));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.CaCl2, 3), FL.array(water, MT.CO2.gas(3*U, true)), FL.array(MT.HCl.gas(4*U, false)), dust.mat(MT.CaCO3, 5));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaO, 2), water, NF, dust.mat(MTx.CaOH2, 5));
            RM.Mixer.addRecipe1(true, 16, 64*3, ST.tag(3), FL.array(MTx.CaCl2Solution.liquid(6*U, true), MTx.Na2SO4Solution.liquid(10*U, true), FL.mul(water, 2)), MT.SaltWater.liquid(16*U, false), dust.mat(MT.CaSO4, 6));
            RM.Mixer.addRecipe2(true, 16, 64*3, ST.tag(3), dust.mat(MT.CaCl2, 3), FL.array(MTx.Na2SO4Solution.liquid(10*U, true), FL.mul(water, 3)), MT.SaltWater.liquid(16*U, false), dust.mat(MT.CaSO4, 6));
            RM.Mixer.addRecipe2(true, 16, 64*3, ST.tag(3), dust.mat(MT.Na2SO4, 7), FL.array(MTx.CaCl2Solution.liquid(6*U, true), FL.mul(water, 3)), MT.SaltWater.liquid(16*U, false), dust.mat(MT.CaSO4, 6));
            RM.Mixer.addRecipeX(true, 16, 64*3, ST.array(ST.tag(3), dust.mat(MT.Na2SO4, 7), dust.mat(MT.CaCl2, 3)), FL.mul(water, 4), MT.SaltWater.liquid(16*U, false), dust.mat(MT.CaSO4, 6));
        }

        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Na2S, 3), FL.array(MT.O.gas(3*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.SO2.gas(3*U, false)), dust.mat(MT.Na2CO3, 6));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.K2S, 3), FL.array(MT.O.gas(3*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.SO2.gas(3*U, false)), dust.mat(MT.K2CO3, 6));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaOH2, 5), MT.CO2.gas(3*U, true), MT.H2O.liquid(3*U, false), dust.mat(MT.CaCO3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.MgOH2, 5), MT.CO2.gas(6*U, true), MTx.MgHCO3.liquid(11*U, false), NI);
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CaCl2Solution.liquid(6*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.HCl.gas(4*U, false)), dust.mat(MT.CaCO3, 5));

        // Solvay process
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.SaltWater.liquid(8*U, true), MT.CO2.gas(3*U, true), MT.NH3.gas(U, true)), FL.array(MTx.NH4ClSolution.liquid(5*U, false)), dust.mat(MTx.NaHCO3, 6));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.CaO, 2), FL.array(MTx.NH4ClSolution.liquid(10*U, true)), FL.array(MT.NH3.gas(2*U, false), MTx.CaCl2Solution.liquid(6*U, false))); // + H2O
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.MgO, 2), FL.array(MTx.NH4ClSolution.liquid(10*U, true)), FL.array(MT.NH3.gas(2*U, false), MTx.MgCl2Solution.liquid(6*U, false))); // + H2O
        RM.Mixer.addRecipe2(true, 16, 128, dust.mat(MTx.CaO, 2), dust.mat(MTx.NH4Cl, 2), ZL_FS, FL.array(MT.NH3.gas(2*U, false), MTx.CaCl2Solution.liquid(6*U, false)));
        RM.Mixer.addRecipe2(true, 16, 128, dust.mat(MTx.MgO, 2), dust.mat(MTx.NH4Cl, 2), ZL_FS, FL.array(MT.NH3.gas(2*U, false), MTx.MgCl2Solution.liquid(6*U, false)));

        // Iron Chlorides
        RM.Bath .addRecipe1(true, 0, 64*9, OP.dust.mat(MT.Fe, 1), MTx.CuFeClSolution.liquid(18*U, true), MTx.FeCl2Solution.liquid(18*U, false), OP.dust.mat(MT.Cu, 1));
        RM.Mixer.addRecipe2(true, 16, 128, ST.tag(3), dust.mat(MT.FeCl2, 6), FL.array(MT.H2O.liquid(6*U, true), MT.O.gas(U, true)), FL.array(MT.HCl.gas(8*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.FeCl2, 3), MT.Cl.gas(U, true), NF, dust.mat(MT.FeCl3, 4));

        // Al Chloride
        RM.Mixer.addRecipe1(true, 16, 64, dust     .mat(MT.Al, 1), MT.Cl .gas(3*U , true), NF, dust.mat(MTx.AlCl3, 4));
        RM.Mixer.addRecipe1(true, 16, 16, dustSmall.mat(MT.Al, 1), MT.Cl .gas(3*U4, true), NF, dust.mat(MTx.AlCl3, 1));
        RM.Bath .addRecipe1(true, 0 , 64, dust     .mat(MT.Al, 1), MT.HCl.gas(6*U , true), MT.H.gas(3*U , false), dust.mat(MTx.AlCl3, 4));
        RM.Bath .addRecipe1(true, 0 , 16, dustSmall.mat(MT.Al, 1), MT.HCl.gas(6*U4, true), MT.H.gas(3*U4, false), dust.mat(MTx.AlCl3, 1));

        // Bi chloride
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Bi, 1), MT.Cl.gas(3*U, true), NF, dust.mat(MTx.BiCl3, 4));

        // Pd chlorination
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Pd, 1), MT.Cl.gas(2*U, true), NF, dust.mat(MTx.PdCl2, 3));

        // Sn fluoride
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.Sn, 1), MT.HF.gas(4*U, true), MT.H.gas(2*U, false), dust.mat(MTx.SnF2, 1));

        // LiCl solution
        RM.Electrolyzer.addRecipe1(true, 16, 6400, ST.tag(0), FL.array(MTx.LiClSolution.liquid(5*U, true)), FL.array(MT.Cl.gas(U, false), MT.H.gas(U, false)), OM.dust(MT.LiOH, 3*U));

        // mixing misc solutions
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe2(true, 16, 192, ST.tag(2), dust.mat(MT.FeCl3, 4), FL.mul(water, 3, 2, true), MTx.FeCl3Solution.liquid(17*U2, false), NI);
            RM.Mixer.addRecipe1(true, 16, 192, ST.tag(2), FL.array(MT.H2SO4.liquid(7*U, true), water), FL.array(MTx.DiluteH2SO4.liquid(10*U, false)));
            RM.Mixer.addRecipe1(true, 16, 192, ST.tag(2), FL.array(MT.HCl.gas(2*U, true), water), FL.array(MTx.DiluteHCl.liquid(5*U, false)));
            RM.Mixer.addRecipe0(true, 16, 128, FL.array(MTx.Phosgene.gas(4*U, true), water), FL.array(MT.CO2.gas(3*U, false), MT.HCl.gas(4*U, false)));
            RM.Mixer.addRecipe1(true, 16, 192, ST.tag(3), FL.array(MTx.FeCl2Solution.liquid(6*U, true), MT.Cl.gas(U, true), FL.mul(water, 1, 2, true)), MTx.FeCl3Solution.liquid(17*U2, true), NI);
        }
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.NaHCO3, 6), FL.array(MTx.DiluteHCl.liquid(5*U, true)), FL.array(FL.Saltwater.make(8000), MT.CO2.gas(3*U, false)));
        RM.Mixer.addRecipe0(true, 16, 600, FL.array(MT.VitriolOfClay.liquid(17*U, true), MTx.NaOHSolution.liquid(18*U, true)), FL.array(MTx.NaHSO4Solution.liquid(30*U, false)), OM.dust(MT.Al2O3, 5*U));

        // drying misc solutions
        RM.Drying.addRecipe0(true, 16, 18000, MTx.FeCl3Solution .liquid(17*U, true ), MT.DistWater.liquid(9*U, false), dust.mat(MT.FeCl3, 8));
        RM.Drying.addRecipe0(true, 16, 6000 , FL.array(MTx.DiluteH2SO4.liquid(10*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.H2SO4.liquid(7*U, false)));
        RM.Distillery.addRecipe1(true, 16, 6000, ST.tag(0), FL.array(MTx.DiluteH2SO4.liquid(10*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.H2SO4.liquid(7*U, false)));
        RM.Drying.addRecipe0(true, 16, 6000 , FL.array(MTx.DiluteHCl.liquid(5*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.HCl.gas(2*U, false)));
        RM.Distillery.addRecipe1(true, 16, 6000, ST.tag(0), FL.array(MTx.DiluteHCl.liquid(5*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.HCl.gas(2*U, false)));

        // Thermal Decomposition of some compounds
        RMx.Thermolysis.addRecipe1(true, 16, 500, OM.dust(MT.Bone, U), ZL_FS, FL.array(MT.H2O.liquid(U4, false), MT.CO2.gas(U4, false)), dustSmall.mat(MTx.Hydroxyapatite, 2));
        RMx.Thermolysis.addRecipe1(false, 64, 128, ST.tag(0), MT.HCl.gas(U, true), FL.array(MT.H.gas(U2, false), MT.Cl.gas(U2, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MT.CaCO3, 5), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.CaO, 2));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MT.MgCO3, 5), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.MgO, 2));
        //RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MT.Na2CO3, 6), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.Na2O, 3));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.NaHCO3, 12), ZL_FS, FL.array(MT.H2O.liquid(3*U, false), MT.CO2.gas(3*U, false)), dust.mat(MT.Na2CO3, 6));
        RMx.Thermolysis.addRecipe0(true, 16, 128, FL.array(MTx.MgHCO3.liquid(11*U, true)), FL.array(MT.H2O.liquid(3*U, false), MT.CO2.gas(3*U, false)), dust.mat(MT.MgCO3, 5));
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MTx.CaOH2, 5), NF, MT.H2O.liquid(3*U, false), dust.mat(MTx.CaO, 2));
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MTx.MgOH2, 5), NF, MT.H2O.liquid(3*U, false), dust.mat(MTx.MgO, 2));
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MT.OREMATS.BrownLimonite, 8), NF, MT.H2O.liquid(3*U, false), dust.mat(MT.Fe2O3, 5));
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MT.OREMATS.YellowLimonite, 8), NF, MT.H2O.liquid(3*U, false), dust.mat(MT.Fe2O3, 5));
        RMx.Thermolysis.addRecipe1(true, 512, 128, dust.mat(MT.SiC, 2), NF, MT.Si.liquid(U, false), dust.mat(MT.Graphite, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 512, dust.mat(MT.Dolomite, 10), NF, MT.CO2.gas(6*U, false), dust.mat(MTx.CalcinedDolomite, 4));
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MTx.NH4NO3, 6), NF, FL.array(FL.Water.make(6000), MTx.N2O.gas(3*U, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MTx.NH4SO4, 21), NF, FL.array(FL.Water.make(18000), MT.N.gas(2*U, false), MT.NH3.gas(4*U, false), MT.SO2.gas(9*U, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 64, dust.mat(PdCl2, 3), NF, MT.Cl.gas(2*U, false), dust.mat(MT.Pd, 1));

        // Methanol and Formaldehyde
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.Cu, 0), dust.mat(MTx.ZnO, 0), FL.array(MT.CO .gas(2*U, true), MT.H.gas(4*U, true)), FL.array(MTx.Methanol.liquid(U, false)));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.Cu, 0), dust.mat(MTx.ZnO, 0), FL.array(MT.CO2.gas(3*U, true), MT.H.gas(6*U, true)), FL.array(MTx.Methanol.liquid(U, false), MT.H2O.liquid(3*U, false)));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.Fe2O3, 0), dust.mat(MTx.MoO3, 0), FL.array(MTx.Methanol.liquid(U, true), MT.O.gas(U, true)), FL.array(MTx.Formaldehyde.gas(U, false), MT.H2O.liquid(3*U, false)));

        // Chloromethane, Diethyl ether
        RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.Al2O3, 0), FL.array(MT.Ethanol.liquid(U10, true), MT.H2SO4.liquid(U1000, true)), FL.array(MTx.Ether.liquid(U10, false), MT.H2O.liquid(3*U10, false)));
        RM.Mixer.addRecipe0(true, 16, 32, FL.array(MTx.Methanol.liquid(U, true), MT.HCl.gas(2*U, true)), FL.array(MTx.CH3Cl.gas(U, false), MT.H2O.liquid(3*U, false)));

        // Ethylene from ethanol and reverse
        RMx.Thermolysis.addRecipe1(true, 16, 64, ST.tag(1), FL.array(MTx.Ether.liquid(U10, true)), FL.array(MT.Ethylene.gas(U10, false), FL.Water.make(300)));
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.NaOH, 0), FL.array(MT.Ethylene.gas(U10, false), water), FL.array(MT.Ethanol.liquid(U10, true)));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MT.KOH , 0), FL.array(MT.Ethylene.gas(U10, false), water), FL.array(MT.Ethanol.liquid(U10, true)));
        }

        // Phosphine, Phosphorous Acid
        RM.Mixer.addRecipe1(true, 16, 16, dust.mat(MT.P, 1), MT.Cl.gas(3*U, true), MTx.PCl3.liquid(4*U, false), NI);
        RM.Mixer.addRecipe0(true, 16, 32, FL.array(MTx.PCl3.liquid(4*U, true), MT.O.gas(U, true)), MTx.POCl3.liquid(5*U, false), NI);
        for (FluidStack water : FL.waters(9000)) {
            RM.Mixer.addRecipe0(true, 16, 32, FL.array(MTx.PCl3.liquid(4 * U, true), water), MT.HCl.gas(6*U, false), dust.mat(MTx.H3PO3, 7));
        }
        RMx.Thermolysis.addRecipe1(true, 16, 128, dust.mat(MTx.H3PO3, 7), NF, FL.array(MTx.H3PO4.liquid(3*8*U4, false), MTx.PH3.gas(U4, false)));
        RM.Mixer.addRecipeX(true, 16, 3*64, ST.array(ST.tag(3), dust.mat(MT.P, 8), dust.mat(MT.NaOH, 27)), FL.Water.make(9000), MTx.PH3.gas(5*U, false), dust.mat(MTx.Na3PO4, 24));
        RM.Mixer.addRecipeX(true, 16, 3*64, ST.array(ST.tag(3), dust.mat(MT.P, 8)), FL.array(MTx.NaOHSolution.liquid(54*U, true)), FL.array(MTx.Na3PO4Solution.liquid(42*U, false), MTx.PH3.gas(5*U, false)));

        // Na2O
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.NaOH, 3), dust.mat(MT.Na, 1), NF, MT.H.gas(U, false), dust.mat(MTx.Na2O, 3));
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.Na2O, 3), water, NF, dust.mat(MT.NaOH, 6));
        }

        // Arsine, Zinc Nitrate
        RM.Bath.addRecipe2(true, 0, 3*5*64, dust.mat(MTx.As2O3, 5), dust.mat(MT.Zn, 3), MT.HNO3.liquid(30*U, true), MTx.AsH3.gas(2*U, false), dust.mat(MTx.ZnNO3, 27));
        for (FluidStack water : FL.waters(3000)) {
            RM.Electrolyzer.addRecipe1(true, 16, 512, dust.mat(MTx.ZnNO3, 9), FL.array(water), FL.array(MT.HNO3.liquid(10*U, false), MT.O.gas(U, false)), dust.mat(MT.Zn, 1));
        }

        // SiF4, H3AsO3
        RMx.Thermolysis.addRecipe0(true, 16, 512, FL.array(MT.H2SiF6.liquid(9*U10, true)), FL.array(MTx.SiF4.gas(U10, false), MT.HF.gas(4*U10, false)), NI);
        RM.Mixer.addRecipe0(true, 16, 5*128, FL.array(MTx.SiF4.gas(U, true), MT.HF.gas(4*U, true)), FL.array(MT.H2SiF6.liquid(9*U, false)));
        for (FluidStack water : FL.waters(9000)) {
            RM.Mixer.addRecipe1(true, 16, 1024, dust.mat(MTx.As2O3, 5), water, MTx.H3AsO3.liquid(14*U, false), NI);
        }
        RMx.Thermolysis.addRecipe0(true, 16, 512, MTx.H3AsO3.liquid(14*U, false), FL.Water.make(9000), dust.mat(MTx.As2O3, 5));

        // Borane and NaH/LiH
        RMx.Thermolysis.addRecipe1(true, 16, 512, dust.mat(MT.H3BO3, 14), NF, FL.Water.make(9000), dust.mat(MTx.B2O3, 5));
        RM.Drying.addRecipe1(true, 16, 18000, dust.mat(MT.H3BO3, 14), NF, FL.DistW.make(9000), dust.mat(MTx.B2O3, 5));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MTx.B2O3, 5), MT.HF.gas(12*U, true), FL.array(FL.Water.make(9000), MTx.BF3.gas(2*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Li, 1), MT.H.gas(U, true), NF, dust.mat(MTx.LiH, 2));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Na, 1), MT.H.gas(U, true), NF, dust.mat(MTx.NaH, 2));
        RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.LiH, 12), MTx.BF3.gas(2*U, true), MTx.B2H6.gas(U, false), dust.mat(MTx.LiF, 12));
        RM.Mixer.addRecipe1(true, 16, 512, dust.mat(MTx.NaH, 12), MTx.BF3.gas(2*U, true), MTx.B2H6.gas(U, false), dust.mat(MT.NaF, 12));

        // Tannin
        for (FluidStack water : FL.waters(1000)) {
            RM.Bath.addRecipe1(true, 0, 128, dust.mat(MT.Bark, 1), water, MTx.Tannin.liquid(2*U, false), NI);
            RM.Bath.addRecipe1(true, 0, 128, dustSmall.mat(MT.Bark, 4), water, MTx.Tannin.liquid(2*U, false), NI);
            RM.Bath.addRecipe1(true, 0, 128, dustTiny.mat(MT.Bark, 9), water, MTx.Tannin.liquid(2*U, false), NI);
        }
        RM.Drying.addRecipe1(true, 16, 128, ST.tag(1), MTx.Tannin.liquid(2*U, true), NF, dust.mat(MTx.TannicAcid, 1));
        RM.Distillery.addRecipe1(true, 16, 128, ST.tag(1), MTx.Tannin.liquid(2*U, true), NF, dust.mat(MTx.TannicAcid, 1));
        //TODO leather tanning
    }

    private void addOverrideRecipes() {
        RM.Mixer.addRecipe1(true, 16, 144, dust.mat(MT.CaCO3, 5), FL.array(MT.HCl.gas(4*U, true)), FL.array(MTx.CaCl2Solution.liquid(6*U, false), MT.CO2.gas(3*U, false)));
        RM.Mixer.addRecipe1(true, 16, 144, dust.mat(MT.MgCO3, 5), FL.array(MT.HCl.gas(4*U, true)), FL.array(MTx.MgCl2Solution.liquid(6*U, false), MT.CO2.gas(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.Fe2O3, 5), MT.HCl.gas(12*U, true), MTx.FeCl3Solution.liquid(17*U, false), NI);
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.YellowLimonite, 8), FL.array(MT.HCl.gas(12*U, true)), FL.array(MTx.FeCl3Solution.liquid(17*U, false), MT.H2O.liquid(3*U, false)), NI);
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.BrownLimonite , 8), FL.array(MT.HCl.gas(12*U, true)), FL.array(MTx.FeCl3Solution.liquid(17*U, false), MT.H2O.liquid(3*U, false)), NI);
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.RedSand, 9), MT.HCl.gas(12*U, true), MTx.FeCl3Solution.liquid(17*U, false), NI);
        RM.Bath.addRecipe1(true, 0, 512, blockDust.mat(MT.RedSand, 1), MT.HCl.gas(12*U, true), MTx.FeCl3Solution.liquid(17*U, false), NI);
        RM.Mixer.addRecipe1(true, 16, 48, dust.mat(MT.NaOH, 2), FL.array(MT.CO2.gas(U, true)), FL.array(MTx.Na2CO3Solution.liquid(3*U, false)));
        RM.Mixer.addRecipe0(true, 16, 272, FL.array(MT.Fe2O3.liquid(5*U, true), MT.HCl.gas(12*U, true)), MTx.FeCl3Solution.liquid(17*U, false), NI);

        RM.Electrolyzer.addRecipe2(true, 128, 1500, ST.tag(0), OM.dust(MT.Sodalite, 46*U), OM.dust(MTx.Na2SiO3, 3*6*U), OM.dust(MT.SiO2, 9*U), OM.dust(MT.Al2O3, 15*U), OM.dust(MT.NaCl, 4*U));
        RM.Electrolyzer.addRecipe2(true, 128, 1500, ST.tag(0), OM.dust(MT.Lazurite, 55*U), NF, MT.H2O.liquid(3*U, false), OM.dust(MTx.Na2SiO3, 3*6*U), OM.dust(MT.SiO2, 9*U), OM.dust(MT.Al2O3, 15*U), OM.dust(MT.CaSO4, 6*U), OM.dust(MT.Na2S, 3*U2), OM.dust(MT.S, 5*U2));

        for (FluidStack water : FL.waters(1000)) {
            RM.Mixer.addRecipe1(true, 0, 192, dust.mat(MTx.Na2SiO3, 6), FL.array(MT.HCl.gas(4*U, true), FL.mul(water, 9)), FL.array(MT.SaltWater.liquid(16*U, false)), dust.mat(MT.SiO2, 3));
            RM.Mixer.addRecipe1(true, 0, 192, dust.mat(MTx.Na2SiO3, 6), FL.array(MTx.DiluteHCl.liquid(10*U, true), FL.mul(water, 3)), FL.array(MT.SaltWater.liquid(16*U, false)), dust.mat(MT.SiO2, 3));
            RM.Mixer.addRecipe1(true, 0, 192, dust.mat(MTx.Na4SiO4, 9), FL.array(MT.HCl.gas(8*U, true), FL.mul(water, 18)), FL.array(MT.SaltWater.liquid(32*U, false)), dust.mat(MT.SiO2, 3));
            RM.Mixer.addRecipe1(true, 0, 192, dust.mat(MTx.Na4SiO4, 9), FL.array(MTx.DiluteHCl.liquid(20*U, true), FL.mul(water, 6)), FL.array(MT.SaltWater.liquid(32*U, false)), dust.mat(MT.SiO2, 3));
            RM.Mixer.addRecipe2(true, 16, 16, ST.tag(1), OM.dust(MT.Na2CO3), water, NF, OM.dust(MT.OREMATS.Trona));
            RM.Mixer.addRecipe1(true, 16, 64, ST.tag(1), FL.array(FL.mul(water, 3), MT.Cl.gas(U*2, true)), MT .HCl      .fluid (4*U, false), MT.O.gas(U, false));
            RM.Mixer.addRecipe1(true, 16, 64, ST.tag(2), FL.array(FL.mul(water, 9), MT.Cl.gas(U*2, true)), MTx.DiluteHCl.liquid(10*U, false), MT.O.gas(U, false));
        }

        // Resin/Turpentine
        RM.Distillery.addRecipe1(false, 16, 160, ST.tag(0), FL.Resin_Spruce.make(100), FL.Turpentine.make(60), ILx.Rosin.get(1));
        for (byte i = 0; i < 16; i++) for (FluidStack dye : DYE_FLUIDS[i]) {
            if (dye.getFluid() != DYE_FLUIDS_CHEMICAL[i].getFluid()) {
                RM.Mixer.addRecipe0(true, 16, 16, FL.array(FL.mul(dye, 3, 2, true), FL.Turpentine.make(20)), FL.mul(DYE_FLUIDS_CHEMICAL[i], 2), ZL_IS);
            }
        }
        RM.Mixer.addRecipe1(true, 16, 64, ILx.Rosin.get(1), MT.Ethanol.liquid(U10, true), FL.make(FLx.Varnish, 100), NI);

        // Wood treating
        @SuppressWarnings({"unchecked", "rawtypes"})
        ICondition condition = new ICondition.Nor(PREFIX_UNUSED, PLANT_DROP, IS_CONTAINER, DUST_BASED, ORE, ORE_PROCESSING_BASED, scrapGt, ingotHot);
        for (OreDictMaterial mat : ANY.WoodUntreated.mToThis) {
            RM.Bath.add(new RecipeMapHandlerMaterial(mat, MTx.Epoxy.liquid(U12, true), 0, 144, NF, MT.WoodTreated, NI, true, condition));
            RM.Bath.add(new RecipeMapHandlerMaterial(mat, FL.Resin_Spruce.make(12), 0, 144, NF, MT.WoodTreated, NI, true, condition));
            RM.Bath.add(new RecipeMapHandlerMaterial(mat, FL.Resin       .make(12), 0, 144, NF, MT.WoodTreated, NI, true, condition));
            RM.Bath.add(new RecipeMapHandlerMaterial(mat, FL.make(FLx.Varnish, 12), 0, 144, NF, MT.WoodTreated, NI, true, condition));
        }

        //TODO biomass gasification
    }

    private void overrideWorldgen() {
        for (StoneLayer layer : StoneLayer.LAYERS) {
            if (layer.mStone == BlocksGT.Granite) {
                for (StoneLayerOres ores : layer.mOres) {
                    if (ores.mMaterial.mID == MT.PO4.mID || ores.mMaterial.mID == MT.PhosphorusRed.mID)
                        ores.mMaterial = MTx.Hydroxyapatite;
                }
            }
        }

        // remove old apatite vein since it is not possible to change the layers as they are final
        for (WorldgenObject obj : CS.ORE_OVERWORLD) {
            if (obj.mName.equals("ore.large.apatite")) {
                obj.mEnabled = false;
            }
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.Bath.mRecipeList) {
            if (r.mFluidInputs.length >= 1 && (r.mFluidInputs[0].isFluidEqual(MT.CaCO3.mLiquid) || r.mFluidInputs[0].isFluidEqual(MT.MgCO3.mLiquid))) {
                r.mEnabled = false;
            }
            if (r.mInputs.length == 1 && ST.equal(r.mInputs[0], dust.mat(MT.Eudialyte, 16))) {
                r.mEnabled = false;
            }
        }

        RM.Bath.addRecipe1(true, 0, 512, OM.dust(MT.Eudialyte, U*16), FL.array(MT.H2SO4.liquid(U*14, true)), FL.array(MT.GrayVitriol.liquid(U*3, false), FL.Saltwater.make(2000), MTx.Na2SO4Solution.liquid(5*U, false)), OM.dust(MT.Zircon, U4*9), OM.dust(MT.SiO2, U*9), OM.dust(MT.Gypsum, U*6));

        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mInputs.length == 0 && r.mOutputs.length == 0 &&
                    r.mFluidInputs.length == 2 && r.mFluidInputs[1].isFluidEqual(MT.Cl.mGas) &&
                    r.mFluidOutputs.length == 2 && r.mFluidOutputs[0].isFluidEqual(MT.HCl.mGas) && r.mFluidOutputs[1].isFluidEqual(MT.O.mGas)
            ) {
                r.mEnabled = false;
            }
        }

        // Kroll process
        for (Recipe r : RM.BurnMixer.mRecipeList) {
            if (r.mFluidOutputs.length >= 1 && r.mFluidOutputs[0].isFluidEqual(MT.TiCl4.mLiquid)) {
                r.mEnabled = false;
            }
        }
        for (OreDictMaterial coke : new OreDictMaterial[] { MT.C, MT.PetCoke, MT.CoalCoke, MT.Graphite }) {
            RM.BurnMixer.addRecipeX(false, 16,  256, ST.array(OM.dust(MT.TiO2, 1*U), OM.dust(coke, 1*U), dust.mat(MT.CaCO3, 1)), FL.array(MT.Cl.gas(4*U, true)), FL.array(MT.TiCl4.liquid(U* 5, false), MT.CO2.gas(3*U + 3*U5, false)), gem.mat(MTx.Slag, 1));
            RM.BurnMixer.addRecipeX(false, 16,  256, ST.array(OM.dust(MT.TiO2, 2*U), OM.dust(coke, 2*U), dust.mat(MT.CaCO3, 2)), FL.array(MT.Cl.gas(8*U, true)), FL.array(MT.TiCl4.liquid(U*10, false), MT.CO2.gas(6*U + 6*U5, false)), gem.mat(MTx.Slag, 2));
        }
    }

    private void addSolutionRecipes() {
        for (OreDictMaterial material : OreDictMaterial.MATERIAL_MAP.values()) if (material.contains(MTx.SIMPLE_SOLUTION)) {
            OreDictMaterialStack waterStack = null, soluteStack = null;
            for (OreDictMaterialStack stack : material.mComponents.getUndividedComponents()) {
                if (stack.mMaterial.mID == MT.H2O.mID) waterStack = stack;
                else soluteStack = stack;
            }
            if (waterStack == null || soluteStack == null) {
                throw new RuntimeException("Invalid solution: " + (waterStack == null ? "no water" : "no solute"));
            }
            long waterAmount = waterStack.mAmount / U;
            long soluteAmount = soluteStack.mAmount / U;
            long totalAmount = waterAmount + soluteAmount;
            if (totalAmount != material.mComponents.getCommonDivider()) {
                throw new RuntimeException("Invalid solution, water + solute: " + totalAmount + ", divider: " + material.mComponents.getCommonDivider());
            }

            for (FluidStack water : FL.waters(1000)) {
                RM.Mixer.addRecipe2(true, 16, 64 * waterAmount, ST.tag(0), dust.mat(soluteStack.mMaterial, soluteAmount), FL.mul(water, waterAmount), material.liquid(totalAmount * U, false), NI);
            }
            RM.Drying.addRecipe1(true, 16, 2000 * waterAmount, ST.tag(0), material.liquid(totalAmount * U, true ), MT.DistWater.liquid(waterAmount * U, false), dust.mat(soluteStack.mMaterial, soluteAmount));
        }
    }
}
