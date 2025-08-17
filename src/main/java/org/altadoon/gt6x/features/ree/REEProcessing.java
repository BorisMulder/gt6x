package org.altadoon.gt6x.features.ree;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.recipes.IRecipeMapHandler;
import gregapi.recipes.Recipe;
import gregapi.recipes.handlers.RecipeMapHandlerMaterial;
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.worldgen.WorldgenObject;
import gregapi.worldgen.WorldgenOresLarge;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.*;
import org.altadoon.gt6x.features.GT6XFeature;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.data.TD.Compounds.COATED;
import static org.altadoon.gt6x.common.Log.LOG;

public class REEProcessing  extends GT6XFeature {
    public static final String FEATURE_NAME = "REEProcessing";

    @Override public String name() { return FEATURE_NAME; }
    @Override public void preInit() {
        MT.OREMATS.Bastnasite.remove(TD.Processing.ELECTROLYSER);
        MT.Monazite.remove(TD.Processing.ELECTROLYSER);
        changeByProducts();
    }

    @Override public void beforeGt6Init() {
        changeItemRecipes();
    }

    @Override
    public void init() {
        addRecipes();
    }

    @Override
    public void postInit() {
        overrideWorldgen();
    }

    @Override
    public void afterGt6PostInit() {
        changeRecipes();
    }

    private void addRecipes() {
        RM.Roasting.addRecipe1(true, 16, 128, dust.mat(MT.Ce, 1), MT.O.gas(2*U, true), NF, dust.mat(MTx.CeO2, 1));
        RMx.Thermolysis.addRecipe1(true, 128, 64, dust.mat(MTx.CeO2, 1), NF, MT.O.gas(U2, false), dust.mat(MTx.Ce2O3, 1));
        RM.Roasting.addRecipe1(true, 16, 16, dust.mat(MTx.Ce2O3, 1), MT.O.gas(U2, true), NF, dust.mat(MTx.CeO2, 1));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.La2O3, 2), MT.HNO3.liquid(30*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.LaNO3, 20));
        RM.Bath.addRecipe1(true, 0, 3*128, dust.mat(MTx.CeO2, 1), FL.array(MT.HNO3.liquid(30*U, true)), FL.array(MTx.NitratoCericAcid.liquid(27*U, false), MT.H2O.liquid(6*U, false)));
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.Ce2O3, 2), MT.H2S.gas(9*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.Ce2S3, 2));

        // Bastnasite
        RMx.Thermolysis.addRecipe2(true, 32, 128, dust.mat(MT.OREMATS.Bastnasite, 12), dust.mat(MTx.CaO, 2), ZL_FS, FL.array(MT.CaF2.liquid(3*U, false), MT.CO2.gas(6*U, false)), dust.mat(MTx.REE2O3, 2));
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MTx.REE2O3, 1), MT.O.gas(U10, true), NF, dust.mat(MTx.REORoasted, 1));
        RM.Bath.addRecipe1(true, 0, 200, dust.mat(MTx.REORoasted, 5), FL.array(MTx.ConcHCl.liquid(30*U, true)), FL.array(MTx.REECl3Solution.liquid(26*U, false), MT.H2O.liquid(6*U, false)), dust.mat(MTx.CeO2, 3));

        // Monazite, Xenotime
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MT .Monazite, 5*6), FL.array(MTx.NaOHSolution.liquid(15*6*U, true)), FL.array(MTx.Na3PO4Solution.liquid(5*14*U, false), MT.H2O.liquid(15*U, false), MT.He.gas(15*U, false)), dust.mat(MTx.REEHydroxide, 35));
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.Xenotime, 5*6), FL.array(MTx.NaOHSolution.liquid(15*6*U, true)), FL.array(MTx.Na3PO4Solution.liquid(5*14*U, false), MT.H2O.liquid(15*U, false), MT.He.gas(15*U, false)), dust.mat(MTx.HREEHydroxide, 35));
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.REEHydroxide, 35), FL.array(MTx.ConcHCl.liquid(60*U, true)), FL.array(MTx.REECl3Solution.liquid(52*U, false), MT.H2O.liquid(13*3*U, false)), dust.mat(MTx.ThO2, 1)); // + 1 H from Th oxidation
        RM.Bath.addRecipe1(true, 0, 600, dust.mat(MTx.HREEHydroxide, 35), FL.array(MTx.ConcHCl.liquid(60*U, true)), FL.array(MTx.HREECl3Solution.liquid(52*U, false), MT.H2O.liquid(13*3*U, false)), dust.mat(MT.OREMATS.Uraninite, 1)); // + 1 H from U oxidation

        // EDTA
        for (FluidStack water : FL.waters(12000)) {
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.NaOH, 6), FL.array(MTx.EDC.liquid(U, true), water, MT.NH3.gas(2 * U, true)), FL.array(MTx.EthyleneDiamine.liquid(U, false), MT.SaltWater.liquid(16 * U, false)));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.NaCN, 12), FL.array(water, MTx.EthyleneDiamine.liquid(U, true), MTx.Formaldehyde.gas(4*U, true)), FL.array(MT.NH3.gas(4*U, false)), dust.mat(MTx.Na4EDTA, 1));
        }
        RM.Mixer.addRecipe1(true, 16, 64, ST.tag(3), FL.array(MTx.NaCNSolution.liquid(24*U, true), MTx.EthyleneDiamine.liquid(U, true), MTx.Formaldehyde.gas(4*U, true)), FL.array(MT.NH3.gas(4*U, false)), dust.mat(MTx.Na4EDTA, 1));
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.Na4EDTA, 1), MTx.DiluteHCl.liquid(32*U, true), MT.SaltWater.liquid(32*U, false), dust.mat(MTx.EDTA, 1));
        RM.Mixer.addRecipe1(true, 16, 32, ST.tag(2), FL.array(MTx.EDTASolution.liquid(4*U, true), MT.NH3.gas(3*U, true)), FL.array(MTx.NH4EDTASolution.liquid(4*U, false)));
        for (FluidStack water : FL.waters(3000))
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.EDTA, 1), FL.array(water, MT.NH3.gas(3*U, true)), FL.array(MTx.NH4EDTASolution.liquid(4*U, false)));

        // IX of REE
        RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.H, 3), FL.array(MTx.REECl3Solution.liquid(13*U, true)), FL.array(MTx.ConcHCl.liquid(15*U, false)), OPx.cationXResin.mat(MT.RareEarth, 3));
        RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.H, 3), FL.array(MTx.HREECl3Solution.liquid(13*U, true)), FL.array(MTx.ConcHCl.liquid(15*U, false)), OPx.cationXResin.mat(MTx.HREE, 3));

        RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.RareEarth, 20), MTx.NH4EDTASolution.liquid(4*U, true), FLx.ReeEdta(MTx.HREE, 4000), OPx.cationXResin.mat(MT.Gd, 20));
        RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(MT.H, 3), FLx.ReeEdta(MTx.HREE, 4000), MTx.EDTASolution.liquid(4*U, false), OPx.cationXResin.mat(MT.Lu, 3));

        OreDictMaterial[] ree = {MT.Lu, MT.Yb, MT.Tm, MT.Er, MT.Ho, MT.Y, MT.Dy, MT.Tb, MT.Gd, MT.Eu, MT.Sm, MT.Nd, MT.Pr, MT.Ce, MT.La};
        int[] ratios = {1, 1, 1, 2, 1, 10, 3, 1, 1, 1, 1, 4, 1, 7, 5};
        for (int i = 0; i < ree.length; i++) {
            OreDictMaterial next;
            if (ree[i] == MT.Tb || ree[i] == MT.La) {
                next = MTx.NH4;
            } else {
                next = ree[i+1];
            }
            int n = ratios[i];
            // Ion Exchange Chromatography - Elution using ammonium EDTA
            RM.Bath.addRecipe1(true, 0, 256, OPx.cationXResin.mat(ree[i], 20), MTx.NH4EDTASolution.liquid(n*4*U, true), FLx.ReeEdta(ree[i], n*4000), OPx.cationXResin.mat(next, 20));
            // Precipitation
            RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.OxalicAcid, 24), FLx.ReeEdta(ree[i], 8000), MTx.EDTASolution.liquid(8*U, false), dust.mat(MTx.REE_OXALATES.get(ree[i]), 20));
            // Decomposition of oxalate to oxide
            RMx.Thermolysis.addRecipe1(true, 16, 96, dust.mat(MTx.REE_OXALATES.get(ree[i]), 20), ZL_FS, FL.array(MT.CO.gas(6*U, false), MT.CO2.gas(9*U, false)), dust.mat(MTx.REE_TRIOXIDES.get(ree[i]), 2));
            // Oxidation
            if (ree[i] != MT.Ce) {
                RM.Roasting.addRecipe1(true, 16, 128, dust.mat(ree[i], 1), MT.O.gas(3*U2, true), NF, dust.mat(MTx.REE_TRIOXIDES.get(ree[i]), 1));
            }
            // Reduction
            if (ree[i] == MT.Sm || ree[i] == MT.Eu || ree[i] == MT.Tm || ree[i] == MT.Yb) {
                // Lanthanothermic reduction of Sm, Eu, Tm, Yb (produces gases)
                RMx.Thermite.addRecipe(0, 2, OMx.stacks(MTx.REE_TRIOXIDES.get(ree[i]), U, MT.La, U), 1500+C, OM.stack(ree[i], U), OM.stack(MTx.La2O3, U));
            } else {
                // Calcinothermic reduction of other REE
                RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.REE_TRIOXIDES.get(ree[i]), 2), MT.HF.gas(12*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.REE_FLUORIDES.get(ree[i]), 2));
                RMx.Thermite.addRecipe(0, 3, OMx.stacks(MTx.REE_FLUORIDES.get(ree[i]), 2*U, MT.Ca, 3*U), 1500+C, OM.stack(ree[i], 2*U), OM.stack(MT.CaF2, 9*U));
            }
        }

        //TODO alloyed Nd/Sm-Co magnets
        //TODO LuAG lenses for high end (DUV) immersion lithography
    }

    private void changeItemRecipes() {
        CRx.overrideShaped(IL.MOTORS[5].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stick    .dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[5]), 'R', OP.stick.dat(MT.DATA.Electric_T[5]), 'W', OP.wireGt05.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[5]);
        CRx.overrideShaped(IL.MOTORS[6].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stick    .dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[6]), 'R', OP.stick.dat(MT.DATA.Electric_T[6]), 'W', OP.wireGt06.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[6]);
        CRx.overrideShaped(IL.MOTORS[7].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stickLong.dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[7]), 'R', OP.stick.dat(MT.DATA.Electric_T[7]), 'W', OP.wireGt07.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[7]);
        CRx.overrideShaped(IL.MOTORS[8].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stickLong.dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[8]), 'R', OP.stick.dat(MT.DATA.Electric_T[8]), 'W', OP.wireGt08.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[8]);
        CRx.overrideShaped(IL.MOTORS[9].get(1), CR.DEF_REM_REV, "CWR", "WIW", "PWC", 'I', OP.stickLong.dat(MTx.SmCo5Magnetic)  , 'P', OP.plateCurved.dat(MT.DATA.Electric_T[9]), 'R', OP.stick.dat(MT.DATA.Electric_T[9]), 'W', OP.wireGt09.dat(MT.AnnealedCopper), 'C', MT.DATA.CABLES_01[9]);

        for (int i = 5; i < 10; i++) {
            CRx.overrideItemData(IL.PUMPS      [i].get(1), "TXO", "dPw", "OMT", 'M', IL.MOTORS[i], 'O', OP.ring.dat(ANY.Rubber), 'X', OP.rotor.dat(MT.DATA.Electric_T[i]), 'T', OP.screw.dat(MT.DATA.Electric_T[i]), 'P', OP.plateCurved.dat(MT.DATA.Electric_T[i]));
            CRx.overrideItemData(IL.CONVEYERS  [i].get(1), "RRR", "MCM", "RRR", 'M', IL.MOTORS[i], 'C', MT.DATA.CABLES_01[i], 'R', OP.plate.dat(ANY.Rubber));
            CRx.overrideItemData(IL.PISTONS    [i].get(1), "TPP", "dSS", "TMG", 'M', IL.MOTORS[i], 'P', OP.plate.dat(MT.DATA.Electric_T[i]), 'S', OP.stick.dat(MT.DATA.Electric_T[i]), 'G', OP.gearGtSmall.dat(MT.DATA.Electric_T[i]), 'T', OP.screw.dat(MT.DATA.Electric_T[i]));
            CRx.overrideItemData(IL.ROBOT_ARMS [i].get(1), "CCC", "MSM", "PES", 'M', IL.MOTORS[i], 'C', MT.DATA.CABLES_01[i], 'E', OD_CIRCUITS[i], 'S', OP.stick.dat(MT.DATA.Electric_T[i]), 'P', IL.PISTONS[i]);
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.Smelter.mRecipeList) {
            if (r.mOutputs.length == 1 && (
                r.mOutputs[0].isItemEqual(dustTiny.mat(MT.RareEarth, 1)) ||
                r.mOutputs[0].isItemEqual(dustSmall.mat(MT.RareEarth, 1)) ||
                r.mOutputs[0].isItemEqual(dust.mat(MT.RareEarth, 1))
            )) {
                r.mEnabled = false;
            }
        }

        // Get rid of polarizer recipes for pure Nd
        for (ListIterator<IRecipeMapHandler> it = RM.Polarizer.mRecipeMapHandlers.listIterator(); it.hasNext(); ) {
            IRecipeMapHandler handler = it.next();
            if (handler instanceof RecipeMapHandlerMaterial handlerMaterial) {
                try {
                    Field f = handlerMaterial.getClass().getDeclaredField("mInputMaterial");
                    f.setAccessible(true);
                    OreDictMaterial mat = (OreDictMaterial) f.get(handlerMaterial);
                    if (mat.mID == MT.Nd.mID) {
                        it.remove();
                    }
                } catch (NoSuchFieldException e) {
                    LOG.error("mInputMaterial not in RecipeMapHandlerMaterial");
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    LOG.error("mInputMaterial not accessible in RecipeMapHandlerMaterial");
                    throw new RuntimeException(e);
                }
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        ICondition condition = new ICondition.And(ANTIMATTER.NOT, COATED.NOT);

        RM.Polarizer.add(new RecipeMapHandlerMaterial(MTx.SmCo5, NF, 128, 144, NF, MTx.SmCo5Magnetic, NI, true, condition));
        RM.Polarizer.add(new RecipeMapHandlerMaterial(MTx.Nd2Fe14B, NF, 128, 144, NF, MT.NeodymiumMagnetic, NI, true, condition));
    }

    private void changeByProducts() {
        MT.Monazite.addOreByProducts(MTx.Xenotime);
        MTx.Xenotime.addOreByProducts(MT.Monazite, MT.OREMATS.Pitchblende, MT.OREMATS.Bastnasite, MT.Biotite, MT.Apatite);

        for (OreDictMaterial mat : MT.ALL_MATERIALS_REGISTERED_HERE) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (byproduct.mID == MT.Nd.mID) {
                    it.set(MT.OREMATS.Bastnasite);
                } else if (byproduct.mID == MT.RareEarth.mID) {
                    it.set(MT.Monazite);
                } else if (byproduct.mID == MT.Th.mID) {
                    it.set(MTx.ThO2);
                }
            }
        }
    }

    private void overrideWorldgen() {
        HashSet<String> toDisable = new HashSet<>(Arrays.asList(
            "ore.large.monazite"
        ));

        for (WorldgenObject obj : CS.ORE_OVERWORLD) {
            if (toDisable.contains(obj.mName)) {
                obj.mEnabled = false;
            }
        }

        new WorldgenOresLarge("ore.large.monazite2", true, true, 20,  40,  30, 3, 16, MT.OREMATS.Bastnasite, MT.OREMATS.Bastnasite, MT.Monazite, MTx.Xenotime, ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS);

    }
}
