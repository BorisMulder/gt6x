package org.altadoon.gt6x.features.ree;

import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.data.RM;
import gregapi.data.TD;
import gregapi.util.ST;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.OPx;
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
        RMx.Thermolysis.addRecipe1(true, 128, 64, dust.mat(MTx.CeO2, 1), NF, MT.O.gas(U2, false), dust.mat(MTx.Ce2O3, 1));
        RM.Roasting.addRecipe1(true, 16, 16, dust.mat(MTx.Ce2O3, 1), MT.O.gas(U2, true), NF, dust.mat(MTx.CeO2, 1));
        RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.Eu, 1), MT.O.gas(3*U2, true), NF, dust.mat(MTx.Eu2O3, 1));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.La2O3, 2), MT.HNO3.liquid(30*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.LaNO3, 20));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.CeO2, 1), FL.array(MT.HNO3.liquid(30*U, true)), FL.array(MTx.NitratoCericAcid.liquid(27*U, false), MT.H2O.liquid(6*U, false)));
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.Ce2O3, 2), MT.H2S.gas(9*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.Ce2S3, 2));

        // Bastnasite
        RMx.Thermolysis.addRecipe2(true, 32, 128, dust.mat(MT.OREMATS.Bastnasite, 12), dust.mat(MTx.CaO, 2), ZL_FS, FL.array(MT.CaF2.liquid(3*U, false), MT.CO2.gas(6*U, false)), dust.mat(MTx.REE2O3, 2));
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.REE2O3, 1), MT.O.gas(U10, true), NF, dust.mat(MTx.REORoasted, 1));
        RM.Bath.addRecipe1(true, 0, 200, dust.mat(MTx.REORoasted, 5), FL.array(MTx.ConcHCl.liquid(30*U, true)), FL.array(MTx.REECl3Solution.liquid(26*U, false), MT.H2O.liquid(6*U, false)), dust.mat(MTx.CeO2, 1));

        // Monazite
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MT.Monazite, 5*6), FL.array(MTx.NaOHSolution.liquid(15*6*U, true)), FL.array(MTx.Na3PO4Solution.liquid(5*14*U, false), MT.H2O.liquid(15*U, false)), dust.mat(MTx.REEHydroxide, 35));
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.REEHydroxide, 35), FL.array(MTx.ConcHCl.liquid(60*U, true)), FL.array(MTx.REECl3Solution.liquid(52*U, false), MT.H2O.liquid(13*3*U, false)), dust.mat(MTx.ThO2, 3)); // + 1 H from Th oxidation

        // EDTA
        for (FluidStack water : FL.waters(12000)) {
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.NaOH, 6), FL.array(MTx.EDC.liquid(U, true), water, MT.NH3.gas(2 * U, true)), FL.array(MTx.EthyleneDiamine.liquid(U, false), MT.SaltWater.liquid(16 * U, false)));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.NaCN, 12), FL.array(water, MTx.EthyleneDiamine.liquid(U, true), MTx.Formaldehyde.gas(4*U, true)), FL.array(MT.NH3.gas(4*U, false)), dust.mat(MTx.Na4EDTA, 1));
        }
        RM.Mixer.addRecipe1(true, 16, 64, ST.tag(3), FL.array(MTx.NaCNSolution.liquid(24*U, true), MTx.EthyleneDiamine.liquid(U, true), MTx.Formaldehyde.gas(4*U, true)), FL.array(MT.NH3.gas(4*U, false)), dust.mat(MTx.Na4EDTA, 1));
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.Na4EDTA, 1), MTx.DiluteHCl.liquid(32*U, true), MT.SaltWater.liquid(32*U, false), dust.mat(MTx.EDTA, 1));

        // IX of REE
        RM.Bath.addRecipe2(true, 0, 256, OPx.anionXResin.mat(MTx.OH, 1), dust.mat(MTx.EDTA, 1), FL.array(MTx.REECl3Solution.liquid(13*U, true)), FL.array(MTx.ConcHCl.liquid(15*U, false), MT.H2O.liquid(3*U, false)), OPx.anionXResin.mat(MTx.REEEDTA, 1));

        // Y, Eu, Nd, Ce, La
        //TODO allow to extract other lanthanides optionally.
        // RM.Bath.addRecipe2(true, 0, 256, ST.tag(1), OPx.anionXResin.mat(MTx.REEEDTA, 1), MTx.NH4OH.liquid(4*U, true), MTx.NH4LuEDTASolution.liquid(4*U, false), OPx.anionXResin.mat(MTx.OH, 1));
    }
}
