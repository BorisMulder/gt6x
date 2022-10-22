package org.altadoon.gt6x.common;

import gregapi.data.CS;
import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.TextureSet;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.U;
import static gregapi.data.CS.U9;
import static gregapi.data.TD.Atomic.CHALCOGEN;
import static gregapi.data.TD.Atomic.POLYATOMIC_NONMETAL;
import static gregapi.data.TD.Compounds.DECOMPOSABLE;
import static gregapi.data.TD.ItemGenerator.*;
import static gregapi.data.TD.Processing.*;
import static gregapi.data.TD.Properties.*;
import static gregapi.render.TextureSet.*;
import static gregapi.render.TextureSet.SET_FINE;

/** Materials for GT6X */
public class MTx {
    public static void touch() {}

    public static OreDictMaterial create(int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) {
        if (aID <= 16000 || aID > 16999) {
            throw new IllegalArgumentException(name + ": GT6X materials should have IDs in the 16001-16999 range");
        }

        return OreDictMaterial.createMaterial(aID, name, name).setRGBa(aR, aG, aB, aA).put(aRandomData).setOriginalMod(Gt6xMod.MOD_DATA);
    }

    public static OreDictMaterial liquid(int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) { return create(aID, name, aR, aG, aB, aA, LIQUID, aRandomData).setTextures(SET_FLUID).put(G_CONTAINERS, CONTAINERS_FLUID); }
    public static OreDictMaterial gas (int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) { return create(aID, name, aR, aG, aB, aA, GASES, aRandomData).setTextures(SET_GAS).put(G_CONTAINERS, CONTAINERS_GAS); }
    public static OreDictMaterial dustdcmp(int aID, String name, TextureSet[] aSets, long aR, long aG, long aB, long aA, Object... aRandomData) { return create(aID, name, aR, aG, aB, aA, aRandomData).setTextures(aSets).put(DECOMPOSABLE, G_DUST, MORTAR); }
    public static OreDictMaterial oredustdcmp(int aID, String name, TextureSet[] aSets, long aR, long aG, long aB, long aA, Object... aRandomData) { return create(aID, name, aR, aG, aB, aA, aRandomData).setTextures(aSets).put(DECOMPOSABLE, G_DUST_ORES, MORTAR); }
    public static OreDictMaterial lquddcmp (int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) { return liquid(aID, name, aR, aG, aB, aA, aRandomData).put(DECOMPOSABLE); }
    public static OreDictMaterial lqudaciddcmp (int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) { return lquddcmp(aID, name, aR, aG, aB, aA, aRandomData).put(ACID); }
    public static OreDictMaterial gasdcmp(int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) { return gas(aID, name, aR, aG, aB, aA, aRandomData).put(DECOMPOSABLE); }

    public static OreDictMaterial registerLiquid(OreDictMaterial mat) {
        FL.createLiquid(mat);
        return mat;
    }

    public static OreDictMaterial registerGas(OreDictMaterial mat) {
        FL.createGas(mat);
        return mat;
    }

    public static final OreDictMaterial
    PGMResidue = oredustdcmp(16001, "Platinum Group Leaching Residue", SET_SHINY, 160, 170, 200, 255)
            .uumMcfg(0, MT.Ru, U, MT.Rh, U, MT.Os, U, MT.Ir, U)
            .heat(2900),
    AmmoniumHexachloroplatinate = dustdcmp(16002, "Ammonium Hexachloroplatinate", SET_FINE, 255, 220, 10, 255)
            .uumMcfg(0, MT.N, U*2, MT.H, U*8, MT.Pt, U, MT.Cl, U*6)
            .heat(653)
            .tooltip("(NH" + CS.NUM_SUB[4] + ")" + CS.NUM_SUB[2] + "PtCl" + CS.NUM_SUB[6]),
    PalladiumChloride = dustdcmp(16003, "Palladium Chloride", SET_FINE, 90, 70, 50, 255)
            .uumMcfg(0, MT.Pd, U, MT.Cl, U*2)
            .heat(952),
    PtPdLeachingSolution = registerLiquid(lqudaciddcmp(16004, "Platinum Palladium Leaching Solution", 255, 100, 70, 255)
            .uumMcfg(0, MT.ChloroplatinicAcid, 5*9*U, PalladiumChloride, 2*3*U, MT.H2O, U*3*16)
            .heat( 200,  400)),
    PdChlorideSolution = registerLiquid(lqudaciddcmp(16005, "Palladium Chloride Solution", 255, 180, 90, 255)
            .uumMcfg(0, PalladiumChloride, U, MT.H2O, U*3*8, MT.HCl, U*2*4)
            .heat( 200,  400)),
    TetraamminepalladiumChloride = dustdcmp(16006, "Tetraamminepalladium Chloride", SET_DULL, 255, 200, 25, 255)
            .uumMcfg(0, MT.Pd, U, MT.NH3, U*4*4, MT.Cl, U*2),
    RhodiumSulfate = dustdcmp(16007, "Rhodium Sulfate", SET_CUBE_SHINY, 255, 70, 10, 255)
            .uumMcfg(0, MT.Rh, U*2, MT.S, U*3, MT.O, U*12)
            .heat(500)
            .tooltip("Rh" + CS.NUM_SUB[2] + "(SO" + CS.NUM_SUB[4] + ")" + CS.NUM_SUB[3]),
    RuOsIrResidue = dustdcmp(16008, "Ruthenium Osmium Iridium Residue", SET_SHINY, 90, 150, 200, 15)
            .uumMcfg(0, MT.Ru, U, MT.Os, U, MT.Ir, U)
            .heat(3000),
    Ozone = registerGas(gas(16009, "Ozone", 0, 150, 255, 25)
            .uumMcfg(0, MT.O, U*3)
            .put(POLYATOMIC_NONMETAL, CHALCOGEN)
            .heat(273-192, 273-112)),
    IrO2 = dustdcmp(16010, "Iridium Dioxide", SET_CUBE, 60, 60, 60, 255)
            .uumMcfg(0, MT.Ir, U, MT.O, U*2)
            .heat(1370),
    OsO4 = dustdcmp(16011, "Osmium Tetroxide", SET_CUBE_SHINY, 40, 60, 100, 255)
            .uumMcfg(0, MT.Os, U, MT.O, U*4)
            .heat(313, 403),
    RuO4 = registerGas(gasdcmp(16012, "Ruthenium Tetroxide", 25, 40, 80, 255)
            .uumMcfg(0, MT.Ru, U, MT.O, U*4)
            .heat(298, 313)),
    RuOsO4 = registerGas(gasdcmp(16013, "Ruthenium Osmium Tetroxide", 100, 140, 180, 255)
            .uumMcfg(0, RuO4, U, OsO4, U)
            .heat(305, 358)),
    ChlororuthenicAcid = registerLiquid(lqudaciddcmp(16014, "Hexachlororuthenic Acid", 255, 150, 90, 255)
            .uumMcfg(0, MT.H, U*2, MT.Ru, U, MT.Cl, U*6, MT.H2O, U*6)
            .heat( 200,  400)),
    AmmoniumHexachlororuthenate = dustdcmp(16015,  "Ammonium Hexachlororuthenate", SET_FINE, 255, 120, 10, 255)
            .uumMcfg(0, MT.N, U*2, MT.H, U*8, MT.Ru, U, MT.Cl, U*6)
            .tooltip("(NH" + CS.NUM_SUB[4] + ")" + CS.NUM_SUB[2] + "RuCl" + CS.NUM_SUB[6]),
    IrRhOxide = dustdcmp(16016,  "Iridium-Rhodium Oxide Mixture", SET_FINE, 200, 200, 200, 255)
            .uumMcfg(0, MT.Ir, U, MT.Rh, U, MT.O, U*8),
    NH4Cl = dustdcmp(16017, "Ammonium Chloride", SET_FINE, 250, 250, 250, 255)
            .uumMcfg(0, MT.N, U, MT.H, U*4, MT.Cl, U)
            .heat(338+273, 520+273 ),
    RhodiumPotassiumSulfate = dustdcmp(16018, "Rhodium-Potassium Sulfate Mixture", SET_CUBE_SHINY, 255, 100, 150, 255)
            .uumMcfg(0, RhodiumSulfate, U, MT.K2SO4, U*6*7)
            .heat(500),
    RhodiumSulfateSolution = registerLiquid(lquddcmp(16019, "Rhodium Sulfate Solution", 255, 50, 10, 255)
            .uumMcfg(0, RhodiumSulfate, U, MT.H2O, U)
            .heat(273, 373)),
    Chalcocite = oredustdcmp(16020, "Chalcocite", SET_CUBE_SHINY, 50, 30, 30, 255)
            .uumMcfg(0, MT.Cu, U*2, MT.S, U)
            .setSmelting(MT.Cu, U9*5)
            .heat(1400)
            .put(FURNACE, G_GEM_ORES);


    static {
        FL.createMolten(MT.K2S2O7.put(MELTING, MOLTEN), 1000);
        FL.createMolten(MT.Na2S2O7.put(MELTING, MOLTEN), 1000);
        FL.createMolten(RhodiumPotassiumSulfate.put(MELTING, MOLTEN), 1000);
    }
}
