package org.altadoon.gt6x.features.ree;

import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.data.RM;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import static gregapi.data.CS.*;
import static gregapi.data.OP.dust;
import static gregapi.data.OP.dustSmall;

public class REEProcessing  extends GT6XFeature {
    public static final String FEATURE_NAME = "REEProcessing";

    @Override public String name() { return FEATURE_NAME; }
    @Override public void configure(Config config) {}
    @Override public void preInit() {}

    @Override
    public void init() {
        addRecipes();
    }

    @Override
    public void postInit() {}

    private void addRecipes() {
        RM.Roasting.addRecipe1(true, 16, 5*128, dust.mat(MT.Y, 1), MT.O.gas(3*U2, true), NF, dustSmall.mat(MTx.Y2O3, 10));
        RM.Roasting.addRecipe1(true, 16, 3*128, dust.mat(MT.Ce, 1), MT.O.gas(2*U, true), NF, dust.mat(MTx.CeO2, 3));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.CeO2, 3), FL.array(MT.HNO3.liquid(30*U, true)), FL.array(MTx.NitratoCericAcid.liquid(27*U, false), MT.H2O.liquid(6*U, false)));
    }
}
