package org.altadoon.gt6x.common;

import gregapi.data.*;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.TextureSet;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.*;
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
    public static OreDictMaterial lqudexpl(int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) {return liquid(aID, name, aR, aG, aB, aA, aRandomData).put(FLAMMABLE, EXPLOSIVE);}
    public static OreDictMaterial lqudflam(int aID, String name, long aR, long aG, long aB, long aA, Object... aRandomData) {return liquid(aID, name, aR, aG, aB, aA, aRandomData).put(FLAMMABLE);}

    public static OreDictMaterial registerLiquid(OreDictMaterial mat) {
        FL.createLiquid(mat);
        return mat;
    }

    public static OreDictMaterial registerGas(OreDictMaterial mat) {
        FL.createGas(mat);
        return mat;
    }

    public static final OreDictMaterial
    // PGM
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
            .heat(CS.C-192, CS.C-112)),
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
            .heat(338+CS.C, 520+CS.C ),
    RhodiumPotassiumSulfate = dustdcmp(16018, "Rhodium-Potassium Sulfate Mixture", SET_CUBE_SHINY, 255, 100, 150, 255)
            .uumMcfg(0, RhodiumSulfate, U, MT.K2SO4, U*6*7)
            .heat(500),
    RhodiumSulfateSolution = registerLiquid(lquddcmp(16019, "Rhodium Sulfate Solution", 255, 50, 10, 255)
            .uumMcfg(0, RhodiumSulfate, U, MT.H2O, U)
            .heat(CS.C, 373)),
    Chalcocite = oredustdcmp(16020, "Chalcocite", SET_CUBE_SHINY, 50, 30, 30, 255)
            .uumMcfg(0, MT.Cu, U*2, MT.S, U)
            .setSmelting(MT.Cu, U9*5)
            .heat(1400)
            .put(FURNACE, G_GEM_ORES),

    // Oil industry
    Naphtha = registerLiquid(lqudexpl( 16025, "Naphtha", 255, 200, 0, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    NaphthaLowSulfur = registerLiquid(lqudexpl( 16026, "Low-Sulfur Naphtha", 255, 255, 0, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    KerosineLowSulfur = registerLiquid(lqudflam( 16027, "Low-Sulfur Kerosine", 0, 0, 255, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    DieselLowSulfur = registerLiquid(lqudflam( 16028, "Low-Sulfur Diesel", 255, 255, 0, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    FuelLowSulfur = registerLiquid(lqudflam( 16029, "Low-Sulfur Fuel Oil", 255, 255, 0, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    Ethane = registerGas(gasdcmp( 16030, "Ethane", 255, 0, 100, 25)
            .uumMcfg(0, MT.C, U*2, MT.H, U*6)
            .heat(90, 185)
            .put(FLAMMABLE)),
    Benzene = registerLiquid(lqudflam( 16031, "Benzene", 150, 150, 100, 255)
            .uumMcfg(0, MT.C, U*6, MT.H, U*6)
            .heat(278, 353)),
    Toluene = registerLiquid(lqudflam( 16032, "Toluene", 150, 150, 150, 255)
            .uumMcfg(0, MT.C, U*7, MT.H, U*8)
            .heat(178, 384)),
    Isoprene = registerLiquid(lqudflam( 16033, "Isoprene", 200, 200, 200, 255)
            .uumMcfg(0, MT.C, U*5, MT.H, U*8)
            .heat(129, 307)),
    Cumene = registerLiquid(lqudflam( 16034, "Cumene", 200, 150, 100, 255)
            .uumMcfg(0, MT.C, U*9, MT.H, U*12)
            .heat(177, 425)),
    Phenol = dustdcmp( 16035, "Phenol", SET_CUBE_SHINY, 200, 150, 100, 255)
            .uumMcfg(0, MT.C, U*6, MT.H, U*6, MT.O, U)
            .heat(314, 455),
    Acetone = registerLiquid(lqudflam( 16036, "Acetone", 200, 150, 100, 255)
            .uumMcfg(0, MT.C, U*3, MT.H, U*6, MT.O, U)
            .heat(179, 329)),
    BPA = dustdcmp( 16037, "Bisphenol A", SET_CUBE_SHINY, 200, 200, 200, 255)
            .uumMcfg(0, MT.C, U*15, MT.H, U*16, MT.O, U*2)
            .heat(428, 524),
    AllylChloride = registerLiquid(lqudflam( 16038, "Allyl Chloride", 100, 200, 100, 255)
            .uumMcfg(0, MT.C, U*3, MT.H, U*5, MT.Cl, U)
            .heat(138, 318)),
    Dichloropropanol = registerLiquid(lqudflam( 16039, "Dichloropropanol", 100, 200, 100, 255)
            .uumMcfg(0, MT.C, U*3, MT.H, U*5, MT.Cl, U)
            .heat(138, 318)),
    ECH = registerLiquid(lqudaciddcmp( 16040, "Epichlorohydrin", 100, 255, 100, 255)
            .uumMcfg(0, MT.C, U*3, MT.H, U*5, MT.Cl, U, MT.O, U)
            .heat(248, 391)
            .put(FLAMMABLE)),
    Epoxy = dustdcmp( 16041, "Epoxy", SET_DULL, 120, 255, 100, 255)
            .uumMcfg(0, MT.C, U*21, MT.H, U*24, MT.O, U*4)
            .put(G_INGOT_MACHINE, EXTRUDER, EXTRUDER_SIMPLE, MORTAR, BRITTLE, FURNACE)
            .heat(400),
    VinylChloride = registerGas(gasdcmp(16042, "Vinyl Chloride", 150, 255, 150, 50)
            .uumMcfg(0, MT.C, 2*U, MT.H, U*3, MT.Cl, U)
            .heat(119, 260)
            .put(FLAMMABLE)),
    PVC = dustdcmp( 16043, "PVC", SET_DULL, 125, 125, 125, 255)
            .setLocal("Polyvinyl Chloride")
            .uumMcfg(0, MT.C, 2*U, MT.H, U*3, MT.Cl, U)
            .heat(C+100)
            .put(G_INGOT_MACHINE, EXTRUDER, EXTRUDER_SIMPLE, MORTAR, PIPES, FURNACE),
    CHCl3 = registerLiquid(lquddcmp( 16044, "Chloroform", 150, 255, 200, 255)
            .uumMcfg(0, MT.C, U, MT.H, U, MT.Cl, U*3)
            .heat(210, 334)),
    CHClF2 = registerGas(gasdcmp( 16045, "Chlorodifluoromethane", 150, 200, 255, 255)
            .uumMcfg(0, MT.C, U, MT.H, U, MT.Cl, U, MT.F, U*2)
            .heat(97, 233)),
    C2F4 = registerGas(gasdcmp( 16046, "Tetrafluoroethylene", 150, 255, 255, 255)
            .uumMcfg(0, MT.C, U*2, MT.F, U*4)
            .heat(131, 197)),
    PTFE = dustdcmp( 16047, "PTFE", SET_DULL, 200, 255, 255, 255)
            .setLocal("Polytetrafluoroethylene")
            .uumMcfg(0, MT.C, U*2, MT.F, U*4)
            .heat(C+327)
            .put(G_INGOT_MACHINE, EXTRUDER, EXTRUDER_SIMPLE, MORTAR, PIPES, FURNACE),
    Synoil = registerLiquid(lqudflam( 16048, "Synthetic Oil", 210, 210, 0, 255)
            .heat(100, 400)),
    SCNaphtha = registerLiquid(lqudflam( 16049, "Steam-Cracked Naphtha", 255, 255, 100, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    EthyleneDichloride = registerLiquid(lqudflam( 16050, "Ethylene Dichloride", 100, 255, 100, 255)
            .uumMcfg(0, MT.C, 2*U, MT.H, U*4, MT.Cl, U*2)
            .heat( 238,  357)),
    PhosphoricAcid = registerLiquid(lqudaciddcmp(16051, "Phosphoric Acid", 150, 200, 0, 255)
            .uumMcfg(0, MT.H, 3*U, MT.PO4, U)
            .heat(290, CS.C + 212)),
    TNT = dustdcmp(16052, "Trinitrotoluene", SET_DULL, 225, 198, 153, 255)
            .uumMcfg(0, MT.C, 7*U, MT.H, 5*U, MT.N, 3*U, MT.O, 6*U)
            .put(FLAMMABLE, EXPLOSIVE, MD.MC)
            .heat(354, 513),
    LNG = registerLiquid(liquid(16053, "LNG", 250, 250, 250, 200)
            .put(FLAMMABLE, EXPLOSIVE)
            .heat(91, 112)
            .setLocal("Liquefied Natural Gas")),
    FccOffgas = registerGas(gas(16054, "FCC Offgas", 125, 0, 150, 200)
            .put(FLAMMABLE, EXPLOSIVE)
            .heat(100, 200)
            .setLocal("Catalytic Cracker Offgas")
    )
    ;


    static {
        FL.createMolten(MT.K2S2O7.put(MELTING, MOLTEN), 1000);
        FL.createMolten(MT.Na2S2O7.put(MELTING, MOLTEN), 1000);
        FL.createMolten(RhodiumPotassiumSulfate.put(MELTING, MOLTEN), 1000);
    }
}
