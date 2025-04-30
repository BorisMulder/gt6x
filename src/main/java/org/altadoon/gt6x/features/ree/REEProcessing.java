package org.altadoon.gt6x.features.ree;

import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.data.RM;
import gregapi.data.TD;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.OP.dust;

public class REEProcessing  extends GT6XFeature {
    public static final String FEATURE_NAME = "REEProcessing";

    @Override public String name() { return FEATURE_NAME; }
    @Override public void preInit() {
        MT.OREMATS.Bastnasite.remove(TD.Processing.ELECTROLYSER);
        MT.Monazite.remove(TD.Processing.ELECTROLYSER);
    }

    @Override
    public void init() {
        addRecipes();
    }

    @Override
    public void postInit() {}

    private void addRecipes() {
        RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.Y, 1), MT.O.gas(3*U2, true), NF, dust.mat(MTx.Y2O3, 1));
        RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.La, 1), MT.O.gas(3*U2, true), NF, dust.mat(MTx.La2O3, 1));
        RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.Ce, 1), MT.O.gas(2*U, true), NF, dust.mat(MTx.CeO2, 1));
        RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.Eu, 1), MT.O.gas(3*U2, true), NF, dust.mat(MTx.Eu2O3, 1));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.La2O3, 2), MT.HNO3.liquid(30*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.LaNO3, 20));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.CeO2, 1), FL.array(MT.HNO3.liquid(30*U, true)), FL.array(MTx.NitratoCericAcid.liquid(27*U, false), MT.H2O.liquid(6*U, false)));

        // Bastnasite
        RMx.Thermolysis.addRecipe2(true, 32, 128, dust.mat(MT.OREMATS.Bastnasite, 12), dust.mat(MTx.CaO, 2), ZL_FS, FL.array(MT.CaF2.liquid(3*U, false), MT.CO2.gas(6*U, false)), dust.mat(MTx.REE2O3, 5));
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.REE2O3, 1), MT.O.gas(U10, true), NF, dust.mat(MTx.REORoasted, 1));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MTx.REORoasted, 10), MT.HCl.gas(12*U, true), MTx.REECl3Solution.liquid(14*U, false), dust.mat(MTx.CeO2, 6));

        // Monazite
        //TODO separate Cerium
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MT.Monazite, 5*6), FL.array(MTx.NaOHSolution.liquid(15*6*U, true)), FL.array(MTx.Na3PO4Solution.liquid(5*14*U, false), MT.H2O.liquid(15*U, false)), dust.mat(MTx.REEHydroxide, 35));
        RM.Bath.addRecipe1(true, 0, 500, dust.mat(MTx.REEHydroxide, 35), FL.array(MT.HCl.gas(24*U, true)), FL.array(MTx.REECl3Solution.liquid(28*U, false), MT.H2O.liquid(9*3*U, false)), dust.mat(MTx.ThO2, 3)); // + 1 H from Th oxidation
    }
}
