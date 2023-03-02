package org.altadoon.gt6x.features.basicchem;

import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.data.RM;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.Recipe;
import gregapi.util.OM;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;

/**
 * This feature contains some basic chemistry recipes shared by other features. Disabling this makes some items uncraftable.
 */
public class BasicChemistry extends GT6XFeature {
    @Override
    public String name() {
        return "BasicChemistry";
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {
        changeMaterialProperties();
    }

    @Override
    public void init() {}

    @Override
    public void beforePostInit() {
        addOverrideRecipes();
    }

    @Override
    public void postInit() { addRecipes(); }

    public void afterPostInit() {
        changeRecipes();
    }

    private void changeMaterialProperties() {
        MT.Dolomite.setSmelting(MTx.CalcinedDolomite, 2*U5);
        MT.CaCO3.setSmelting(MT.Quicklime, 2*U5);
        MT.MgCO3.setSmelting(MTx.MgO, 2*U5);
    }

    protected void addRecipes() {
        // Phosphoric Acid
        for (OreDictMaterial phosphorus : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusRed, MT.PhosphorusWhite}) {
            RM.Mixer.addRecipe1(true, 16,  16, dust.mat(phosphorus, 5), FL.array(MT.H2SO4.liquid(3*7*U, true)), FL.array(MTx.PhosphoricAcid.liquid(8*U, false)), dust.mat(MT.CaSO4, 18));
        }
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.Phosphorite, 9), FL.array(MT.H2SO4.liquid(5*7*U, true)), FL.array(MTx.PhosphoricAcid.liquid(12*U, false), MT.HF.gas(U*2, false)), dust.mat(MT.CaSO4, 30));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MT.Apatite, 9), FL.array(MT.H2SO4.liquid(5*7*U, true)), FL.array(MTx.PhosphoricAcid.liquid(12*U, false), MT.HCl.gas(U*2, false)), dust.mat(MT.CaSO4, 30));
        RM.Mixer.addRecipe1(true, 16,  16, dust.mat(MTx.P2O5, 7), FL.array(MT.H2O.liquid(3*3*U, true)), FL.array(MTx.PhosphoricAcid.liquid(16*U, false)));

        // (Na,K)2S2O7
        RM.Drying.addRecipe1(true, 16, 100, dust.mat(MT.KHSO4, 2), ZL_FS, FL.array(MT.DistWater.liquid(U, false)), dust.mat(MT.K2S2O7, 1));
        RM.Drying.addRecipe1(true, 16, 100, dust.mat(MT.NaHSO4, 2), ZL_FS, FL.array(MT.DistWater.liquid(U, false)), dust.mat(MT.Na2S2O7, 1));
        RM.Smelter.addRecipe1(true, 16, 16, dust.mat(MT.K2S2O7, 1), ZL_FS, MT.K2S2O7.liquid(U, false), ZL_IS);
        RM.Smelter.addRecipe1(true, 16, 16, dust.mat(MT.Na2S2O7, 1), ZL_FS, MT.Na2S2O7.liquid(U, false), ZL_IS);

        // HCL to H + Cl (needed for some chains)
        RM.Electrolyzer.addRecipe1(false, 64, 64, ST.tag(0), MT.HCl.gas(U, true), FL.array(MT.H.gas(U2, false), MT.Cl.gas(U2, false)));
        // NH3 recipe
        RM.Mixer.addRecipe1(true, 64, 50, dust.mat(MT.OREMATS.Magnetite, 0), FL.array(MT.H.gas(3*U, true), MT.N.gas(U, true)), FL.array(MT.NH3.gas(U, false)));
        // Ammonium salts
        RM.Mixer.addRecipe0(true, 16, 20, FL.array(MT.HCl.gas(U, false), MT.NH3.gas(U, false)), ZL_FS, dust.mat(MTx.NH4Cl, 2));
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.H2SO4.liquid(7*U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.NH4SO4, 9));

        // O3 recipe
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
        RM.Bath.addRecipe1(T,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurified    .mat(tMat, 1), FL.array(MT.H2SO4.fluid(7* U2, true)), FL.array(MT.BlueVitriol.fluid(3*U, false), MT.H.gas(U, false)), crushedCentrifuged.mat(tMat, 1), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2));
        RM.Bath.addRecipe1(T,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurifiedTiny.mat(tMat, 9), FL.array(MT.H2SO4.fluid(7* U2, true)), FL.array(MT.BlueVitriol.fluid(3*U, false), MT.H.gas(U, false)), crushedCentrifuged.mat(tMat, 1), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2), crushedCentrifugedTiny.mat(tMat, 2));

        // coke reduction
        for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.LigniteCoke, 3),  dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1) )) {
            RM.BurnMixer.addRecipe2(true, 16, 128, dust.mat(MT.CaSO4, 12), coal, ZL_FS, FL.array(MT.CO2.gas(3 * U, false), MT.SO2.gas(6 * U, false)), dust.mat(MT.Quicklime, 4));
        }

        // sodium/calcium/magnesium salts and related chemistry
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.MgO, 2), MT.HCl.gas(4*U, true), MTx.MgCl2Solution.liquid(6*U, false), NI);
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Wollastonite, 5), MT.HCl.gas(4*U, true), MTx.CaCl2Solution.liquid(6*U, false), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.NaHCO3, 6), FL.array(MT.HCl.gas(2*U, true)), FL.array(MT.SaltWater.liquid(4*U, false), MT.CO2.gas(3*U, false)), OM.dust(MT.NaCl, U2));

        for (FluidStack tWater : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.CaCl2, 3), FL.array(tWater, MT.CO2.gas(3*U, true)), FL.array(MT.HCl.gas(4*U, false)), dust.mat(MT.CaCO3, 5));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Quicklime, 2), tWater, NF, dust.mat(MTx.CaOH2, 5));
            // dissolve salts
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.CaCl2, 3), tWater, MTx.CaCl2Solution.liquid(6*U, false), NI);
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.MgCl2, 3), tWater, MTx.MgCl2Solution.liquid(6*U, false), NI);
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.NH4Cl, 4), tWater, MTx.NH4ClSolution.liquid(7*U, false), NI);
            RM.Mixer.addRecipe2(true, 16, 64, ST.tag(2), dust.mat(MT.FeCl2, 3), tWater, MTx.FeCl2Solution.liquid(6*U, false), NI);
            RM.Mixer.addRecipe1(true, 16, 96, dust.mat(MT.Na2CO3, 6), tWater, MTx.Na2CO3Solution.liquid(9*U, false), NI);
        }

        RM.Mixer.addRecipe2(true, 16, 128, ST.tag(3), dust.mat(MT.FeCl2, 6), FL.array(MT.H2O.liquid(6*U, true), MT.O.gas(U, true)), FL.array(MT.HCl.gas(8*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaOH2, 5), MT.CO2.gas(3*U, true), MT.H2O.liquid(3*U, false), dust.mat(MT.CaCO3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.MgOH2, 5), MT.CO2.gas(6*U, true), MTx.MgHCO3.liquid(11*U, false), NI);
        RM.Mixer.addRecipe0(true, 16, 64, FL.array(MTx.CaCl2Solution.liquid(6*U, true), MT.CO2.gas(3*U, true)), FL.array(MT.HCl.gas(4*U, false)), dust.mat(MT.CaCO3, 5));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.CaOH2, 5), FL.array(MTx.MgCl2Solution.liquid(6*U, true)), FL.array(MTx.CaCl2Solution.liquid(6*U, false)), dust.mat(MTx.MgOH2, 5));

        // solvay process
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.SaltWater.liquid(8*U, true), MT.CO2.gas(3*U, true), MT.NH3.gas(2*U, true)), FL.array(MTx.NH4ClSolution.liquid(7*U, false)), dust.mat(MTx.NaHCO3, 6));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MT.Quicklime, 2), FL.array(MTx.NH4ClSolution.liquid(14*U, true)), FL.array(MT.NH3.gas(4*U, false), MTx.CaCl2Solution.liquid(6*U, false)));
        RM.Mixer.addRecipe1(true, 16, 128, dust.mat(MTx.MgO, 2), FL.array(MTx.NH4ClSolution.liquid(14*U, true)), FL.array(MT.NH3.gas(4*U, false), MTx.MgCl2Solution.liquid(6*U, false)));

        // drying solutions
        RM.Drying.addRecipe0(true, 16, 2000, MTx.CaCl2Solution .liquid(2*U, true ), MT.DistWater.liquid(U, false), dust.mat(MT.CaCl2, 1));
        RM.Drying.addRecipe0(true, 16, 2000, MTx.FeCl2Solution .liquid(2*U, true ), MT.DistWater.liquid(U, false), dust.mat(MT.FeCl2, 1));
        RM.Drying.addRecipe0(true, 16, 2000, MTx.MgCl2Solution .liquid(2*U, true ), MT.DistWater.liquid(U, false), dust.mat(MT.MgCl2, 1));
        RM.Drying.addRecipe0(true, 16, 6000, MTx.NH4ClSolution .liquid(7*U, true ), MT.DistWater.liquid(3*U, false), dust.mat(MTx.NH4Cl, 4));
        RM.Drying.addRecipe0(true, 16, 6000, MTx.Na2CO3Solution.liquid(9*U, false), MT.DistWater.liquid(3*U, false), dust.mat(MT.Na2CO3, 6));

        // TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MT.CaCO3, 5), NF, MT.CO2.gas(3*U, false), dust.mat(MT.Quicklime, 2));
        RM.Drying.addRecipe1(true, 16, 256, dust.mat(MT.MgCO3, 5), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.MgO, 2));
        //RM.Drying.addRecipe1(true, 16, 256, dust.mat(MT.Na2CO3, 6), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.Na2O, 3));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MTx.NaHCO3, 12), ZL_FS, FL.array(MT.DistWater.liquid(3*U, false), MT.CO2.gas(3*U, false)), dust.mat(MT.Na2CO3, 6));
        RM.Drying.addRecipe0(true, 16, 6000, FL.array(MTx.MgHCO3.liquid(11*U, true)), FL.array(MT.DistWater.liquid(3*U, false), MT.CO2.gas(3*U, false)), dust.mat(MT.MgCO3, 5));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MTx.CaOH2, 5), NF, MT.DistWater.liquid(3*U, false), dust.mat(MT.Quicklime, 2));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MTx.MgOH2, 5), NF, MT.DistWater.liquid(3*U, false), dust.mat(MTx.MgO, 2));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MT.OREMATS.BrownLimonite, 8), NF, MT.DistWater.liquid(3*U, false), dust.mat(MT.Fe2O3, 5));
        RM.Drying.addRecipe1(true, 16, 6000, dust.mat(MT.OREMATS.YellowLimonite, 8), NF, MT.DistWater.liquid(3*U, false), dust.mat(MT.Fe2O3, 5));
        RM.Drying.addRecipe1(true, 512, 128, dust.mat(MT.SiC, 2), NF, MT.Si.liquid(U, false), dust.mat(MT.Graphite, 1));
        RM.Drying.addRecipe1(true, 16, 512, dust.mat(MT.Dolomite, 10), NF, MT.CO2.gas(6*U, false), dust.mat(MTx.CalcinedDolomite, 4));
    }

    private void addOverrideRecipes() {
        RM.Mixer.addRecipe1(true, 16, 144, dust.mat(MT.CaCO3, 5), FL.array(MT.HCl.gas(4*U, true)), FL.array(MTx.CaCl2Solution.liquid(6*U, false), MT.CO2.gas(3*U, false)));
        RM.Mixer.addRecipe1(true, 16, 144, dust.mat(MT.MgCO3, 5), FL.array(MT.HCl.gas(4*U, true)), FL.array(MTx.MgCl2Solution.liquid(6*U, false), MT.CO2.gas(3*U, false)));
        RM.Mixer.addRecipe1(true, 16, 48, dust.mat(MT.NaOH, 2), FL.array(MT.CO2.gas(U, true)), FL.array(MTx.Na2CO3Solution.liquid(3*U, false)));
    }

    private void changeRecipes() {
        for (Recipe r : RM.Bath.mRecipeList) {
            if (r.mFluidInputs.length >= 1 && (r.mFluidInputs[0].isFluidEqual(MT.CaCO3.mLiquid) || r.mFluidInputs[0].isFluidEqual(MT.MgCO3.mLiquid))) {
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
}
