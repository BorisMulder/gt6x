package org.altadoon.gt6x.common;

import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.TextureSet;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.*;
import static gregapi.data.TD.Atomic.CHALCOGEN;
import static gregapi.data.TD.Atomic.POLYATOMIC_NONMETAL;
import static gregapi.data.TD.Compounds.ALLOY;
import static gregapi.data.TD.Compounds.DECOMPOSABLE;
import static gregapi.data.TD.ItemGenerator.*;
import static gregapi.data.TD.Processing.*;
import static gregapi.data.TD.Properties.*;
import static gregapi.render.TextureSet.*;
import static gregapi.render.TextureSet.SET_FINE;

/** Materials for GT6X */
public class MTx {
    public static void touch() {}

    public static final TagData POLYMER = TagData.createTagData("PROPERTIES.Polymer", "Polymer");

    static {
        // change some properties of vanilla GT6 materials
        MT.NH3.uumMcfg(1, MT.N, U, MT.H, 3*U);
        MT.PigIron.uumMcfg(5, MT.Fe, 5*U, MT.C, U);
        MT.Steel  .uumMcfg(20, MT.Fe, 20*U, MT.C, U);

        MT.BlueSapphire.uumMcfg(6, MT.Al2O3, 5*U, MT.Fe2O3, U);
        MT.Ruby.uumMcfg(6, MT.Al2O3, 5*U, MTx.Cr2O3, U);
        MT.GreenSapphire.uumMcfg(6, MT.Al2O3, 5*U, MTx.MgO, U);
        MT.PurpleSapphire.uumMcfg(6, MT.Al2O3, 5*U, MT.V2O5, U);

        MT.As.heat(887, 887).remove(MELTING); MT.As.remove(MOLTEN);

        MT.Plastic.put(POLYMER);
        MT.Rubber.put(POLYMER);

        MT.OREMATS.Wolframite.setLocal("Magnesium Tungstate").addSourceOf(MT.Mg);
        MT.OREMATS.Tungstate.setLocal("Lithium Tungstate");
        MT.OREMATS.Huebnerite.setLocal("Hübnerite");

        FL.createMolten(MT.K2S2O7.put(MELTING, MOLTEN), 1000);
        FL.createMolten(MT.Na2S2O7.put(MELTING, MOLTEN), 1000);
        FL.createMolten(MT.Quicklime.put(MELTING, MOLTEN), 1000);
        FL.createMolten(MT.Nb.put(MELTING, MOLTEN), 1000);

        FL.createGas(MT.Zn.put(GASES));
        FL.createGas(MT.As.put(GASES));
        FL.createGas(MT.Mg.put(GASES));
    }

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
    public static OreDictMaterial machine(int aID, String aNameOreDict, TextureSet[] aSets, long aR, long aG, long aB, Object... aRandomData) { return create(aID, aNameOreDict, aR, aG, aB , 255, aRandomData).setTextures(aSets).put(DECOMPOSABLE, G_INGOT_MACHINE, SMITHABLE, MELTING, EXTRUDER); }
    public static OreDictMaterial alloymachine(int aID, String aNameOreDict, TextureSet[] aSets, long aR, long aG, long aB, Object... aRandomData) { return machine(aID, aNameOreDict, aSets, aR, aG, aB , aRandomData).put(ALLOY); }
    public static OreDictMaterial plastic(int aID, String aNameOreDict, TextureSet[] aSets, long aR, long aG, long aB, long aA, Object... aRandomData) { return create(aID, aNameOreDict, aR, aG, aB , aA, aRandomData).setTextures(aSets).put(G_INGOT_MACHINE, MELTING, EXTRUDER, EXTRUDER_SIMPLE, MORTAR, FURNACE, POLYMER); }


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
            .heat(MT.H2O)),
    PdChlorideSolution = registerLiquid(lqudaciddcmp(16005, "Palladium Chloride Solution", 255, 180, 90, 255)
            .uumMcfg(0, PalladiumChloride, U, MT.H2O, U*3*8, MT.HCl, U*2*4)
            .heat(MT.H2O)),
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
            .addSourceOf(MT.Cu)
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
            .uumMcfg(0, MT.C, U*3, MT.H, U*6, MT.Cl, U*2, MT.O, U)
            .heat(138, 318)),
    ECH = registerLiquid(lqudaciddcmp( 16040, "Epichlorohydrin", 100, 255, 100, 255)
            .uumMcfg(0, MT.C, U*3, MT.H, U*5, MT.Cl, U, MT.O, U)
            .heat(248, 391)
            .put(FLAMMABLE)),
    Epoxy = plastic( 16041, "Epoxy", SET_DULL, 120, 255, 100, 255)
            .uumMcfg(0, MT.C, U*21, MT.H, U*24, MT.O, U*4)
            .heat(400),
    VinylChloride = registerGas(gasdcmp(16042, "Vinyl Chloride", 150, 255, 150, 50)
            .uumMcfg(0, MT.C, 2*U, MT.H, U*3, MT.Cl, U)
            .heat(119, 260)
            .put(FLAMMABLE)),
    PVC = plastic( 16043, "PVC", SET_DULL, 125, 125, 125, 255)
            .setLocal("Polyvinyl Chloride")
            .uumMcfg(0, MT.C, 2*U, MT.H, U*3, MT.Cl, U)
            .heat(C+100)
            .put(PIPES),
    CHCl3 = registerLiquid(lquddcmp( 16044, "Chloroform", 150, 255, 200, 255)
            .uumMcfg(0, MT.C, U, MT.H, U, MT.Cl, U*3)
            .heat(210, 334)),
    CHClF2 = registerGas(gasdcmp( 16045, "Chlorodifluoromethane", 150, 200, 255, 255)
            .uumMcfg(0, MT.C, U, MT.H, U, MT.Cl, U, MT.F, U*2)
            .heat(97, 233)),
    C2F4 = registerGas(gasdcmp( 16046, "Tetrafluoroethylene", 150, 255, 255, 255)
            .uumMcfg(0, MT.C, U*2, MT.F, U*4)
            .heat(131, 197)),
    PTFE = plastic( 16047, "PTFE", SET_DULL, 200, 255, 255, 255)
            .setLocal("Polytetrafluoroethylene")
            .uumMcfg(0, MT.C, U*2, MT.F, U*4)
            .heat(C+327)
            .put(PIPES),
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
    ),

    // Metallurgy
    Slag = create( 16055, "Slag", 255, 240, 200, 255)
            .setMcfg( 0, MT.Quicklime, U, MT.SiO2, 3*U)
            .setTextures(SET_FLINT)
            .put(INGOTS, MORTAR, BRITTLE, GEMS)
            .heat(1810, 3000)
            .setPulver(MT.OREMATS.Wollastonite, U),
    BlastFurnaceGas = registerGas(gas(16056, "Blast Furnace Gas", 0, 20, 30, 200)
            .uumMcfg(0, MT.N, 11*U, MT.CO, 4*U, MT.CO2, 4*U, MT.H, U)
            .put(FLAMMABLE, CENTRIFUGE)
            .heat(100, 200)),
    ZnBlastFurnaceGas = registerGas(gas(16057, "Zinc-Rich Blast Furnace Gas", 0, 20, 30, 200)
            .uumMcfg(0, MT.Zn, U, BlastFurnaceGas, 6*U)
            .heat(MT.Zn)),
    PbO = dustdcmp(16058, "Lead Oxide", SET_DULL, 150, 130, 100, 255)
            .uumMcfg(1, MT.Pb, U, MT.O, U)
            .heat(1161, 1750)
            .setSmelting(MT.Pb, 3*U4),
    ZnO = dustdcmp(16059, "Zinc Oxide", SET_DULL, 255, 230, 240, 255)
            .uumMcfg(1, MT.Zn, U, MT.O, U)
            .heat(2247, 2630)
            .setSmelting(MT.Zn, 3*U4),
    FeCr2 = alloymachine(16060, "Ferrochrome", SET_SHINY, 160, 150, 150)
            .uumMcfg(0, MT.Fe, U, MT.Cr, 2*U)
            .heat(C+1500),
    Co3O4 = dustdcmp(16061, "Tricobalt Tetroxide", SET_DULL, 150, 150, 180, 255)
            .uumMcfg(0, MT.Co, 3*U, MT.O, 4*U)
            .heat(1168, 1170),
    As2O3 = dustdcmp(16062, "Arsenic Trioxide", SET_DULL, 200, 200, 200, 255)
            .uumMcfg(0, MT.As, 2*U, MT.O, 3*U)
            .heat(C+312, C+465),
    MoO3 = dustdcmp(16063, "Molybdenum Trioxide", SET_CUBE, 180, 180, 200, 255)
            .uumMcfg(0, MT.Mo, U, MT.O, 3*U)
            .heat(1075, 1428),
    HgO = dustdcmp(16064, "Mercury(II) Oxide", SET_CUBE, 255, 150, 0, 255)
            .uumMcfg(0, MT.Hg, U, MT.O, U)
            .heat(773)
            .setSmelting(MT.Hg, U2),
    CoO = dustdcmp(16065, "Cobalt(II) Oxide", SET_DULL, 50, 50, 100, 255)
            .uumMcfg(0, MT.Co, U, MT.O, U)
            .heat(2206),
    NaHCO3 = dustdcmp(16066, "Sodium Bicarbonate", SET_FINE, 255, 255, 255, 255)
            .uumMcfg(0, MT.Na, U, MT.H, U, MT.C, U, MT.O, 3*U)
            .heat(C+80)
            .setSmelting(MT.Na2CO3, U2),
    Na2CrO4 = dustdcmp(16067, "Sodium Chromate", SET_CUBE, 255, 255, 0, 255)
            .uumMcfg(0, MT.Na, 2*U, MT.Cr, U, MT.O, 4*U)
            .heat(1065),
    Na2Cr2O7 = dustdcmp(16068, "Sodium Dichromate", SET_DULL, 255, 125, 0, 255)
            .uumMcfg(0, MT.Na, 2*U, MT.Cr, 2*U, MT.O, 7*U)
            .heat(629, 673),
    Na2CrO4Solution = registerLiquid(lquddcmp(16069, "Sodium Chromate Solution", 255, 255, 0, 255)
            .uumMcfg(0, MTx.Na2CrO4, U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    DichromateSoda = registerLiquid(lquddcmp(16070, "Sodium Dichromate-Bicarbonate Solution", 255, 125, 0, 255)
            .uumMcfg(0, MTx.Na2Cr2O7, 11*U, NaHCO3, 12*U, MT.H2O, 9*U)
            .heat(MT.H2O)),
    Na2CO3Solution = registerLiquid(lquddcmp(16071, "Sodium Carbonate Solution", 100, 100, 255, 255)
            .uumMcfg(0, MT.Na2CO3, 6*U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    Cr2O3 = dustdcmp(16072, "Chromium(III) Oxide", SET_DULL, 100, 255, 100, 255)
            .uumMcfg(0, MT.Cr, 2*U, MT.O, 3*U)
            .heat(2708, 4270),
    CrSodaMixture = dustdcmp(16073, "Chromia-Soda Mixture", SET_POWDER, 50, 200, 50, 255)
            .uumMcfg(0, MTx.Cr2O3, U, MT.Na2CO3, U)
            .heat(MTx.Cr2O3),
    CrSlag = dustdcmp(16074, "Chromite Slag", SET_POWDER, 150, 150, 0, 255)
            .setMcfg(0, MTx.Na2CrO4, 4*7*U, MT.OREMATS.Wollastonite, 5*U, MT.Fe2O3, 5*U)
            .heat(MTx.Na2CrO4),
    Sb2O3 = dustdcmp(16075, "Antimony Trioxide", SET_FINE, 255, 200, 150, 255)
            .uumMcfg(0, MT.Sb, 2*U, MT.O, 3*U)
            .heat(929, 1698),
    FeS = dustdcmp(16076, "Iron(II) Sulfide", SET_SHINY, 66, 66, 66, 255)
            .uumMcfg(0, MT.Fe, U, MT.S, U)
            .heat(1467),
    Cementite = alloymachine(16077, "Cementite", SET_METALLIC, 50, 0, 0)
            .uumMcfg(3, MT.Fe, 3*U, MT.C, U)
            .heat(MT.PigIron),
    H2MoO4 = dustdcmp(16078, "Molybdic Acid", SET_DULL, 200, 200, 0, 255, ACID)
            .uumMcfg(0, MT.H, 2*U, MT.Mo, U, MT.O, 4*U)
            .heat(573),
    PbCl2 = dustdcmp(16079, "Lead Chloride", SET_CUBE, 255, 200, 255, 255, ELECTROLYSER)
            .uumMcfg(0, MT.Pb, U, MT.Cl, 2*U)
            .heat(774,1220),
    Wolframite = oredustdcmp(16080, "TrueWolframite", SET_METALLIC, 100, 100, 120, 255)
            .uumMcfg(0, MT.OREMATS.Ferberite, U, MT.OREMATS.Huebnerite, U)
            .setLocal("Wolframite")
            .addSourceOf(MT.Fe, MT.W, MT.Mn)
            .qual(3),
    Vanadinite = oredustdcmp(16081, "Vanadinite", SET_CUBE_SHINY, 153, 51, 0, 255)
            .uumMcfg(0, MT.Pb, 5*U, MT.V, 3*U, MT.O, 12*U, MT.Cl, U)
            .heat(C + 1910)
            .tooltip("Pb" + CS.NUM_SUB[5] + "(VO" + CS.NUM_SUB[4] + ")" + NUM_SUB[3] + "Cl"),
    NaVO3Solution = registerLiquid(liquid(16082, "Sodium Metavanadate Solution", 255, 200, 120, 255)
            .uumMcfg(0, MT.Na, U, MT.V, U, MT.O, 3*U, MT.H2O, 6*U)
            .heat(MT.H2O)),
    NH4VO3 = dustdcmp(16083, "Ammonium Metavanadate", SET_DULL, 255, 200, 150, 255)
            .uumMcfg(0, MT.N, U, MT.H, 4*U, MT.V, U, MT.O, 3*U)
            .heat(473),
    Wuestite = oredustdcmp(16084, "Wuestite", SET_DULL, 50, 50, 0, 255)
            .setMcfg(0, MT.Fe, U, MT.O, U)
            .heat(C+1377, C+3414)
            .setLocal("Wüstite"),
    CobaltBlue = dustdcmp(16085, "Cobalt Blue", SET_FINE, 0, 71, 171, 255, DYE_INDEX_Blue)
            .uumMcfg(0, MT.Co, U, MT.Al, 2*U, MT.O, 4*U)
            .heat((MT.Al2O3.mMeltingPoint + MTx.CoO.mMeltingPoint) / 2),
    NH4SO4 = dustdcmp(16086, "Ammonium Sulfate", SET_CUBE, 255, 255, 230, 255)
            .uumMcfg(0, MT.N, 2*U, MT.H, 8*U, MT.S, U, MT.O, 4*U)
            .heat(508)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "SO" + NUM_SUB[4]),
    SeO2 = dustdcmp(16087, "Selenium Dioxide", SET_QUARTZ, 255, 200, 240, 255)
            .uumMcfg(0, MT.Se, U, MT.O, 2*U)
            .heat(613, 623),
    ZrCl4 = registerGas(gasdcmp(16088, "Zirconium Tetrachloride", 255, 0, 255, 200, ACID)
            .uumMcfg(0, MT.Zr, U, MT.Cl, 4*U)
            .heat(604, 604)),
    HfCl4 = registerGas(gasdcmp(16089, "Hafnium Tetrachloride", 200, 0, 255, 200, ACID)
            .uumMcfg(0, MT.Hf, U, MT.Cl, 4*U)
            .heat(705, 705)),
    ZrHfCl4 = registerGas(gasdcmp(16090, "Zirconium-Hafnium Tetrachloride", 228, 0, 255, 200, ACID)
            .uumMcfg(0, ZrCl4, 49*U50, HfCl4, U50)
            .heat(654, 654)
            .setLocal("Impure Zirconium Tetrachloride")),
    MnF2 = dustdcmp(16091, "Manganese(II) Fluoride", SET_CUBE, 255, 150, 200, 255, ELECTROLYSER, ACID)
            .uumMcfg(0, MT.Mn, U, MT.F, 2*U)
            .heat(1129, 2090),
    FeF2 = dustdcmp(16092, "Iron(II) Fluoride", SET_CUBE, 150, 255, 255, 255, ELECTROLYSER, ACID)
            .uumMcfg(0, MT.Fe, U, MT.F, 2*U)
            .heat(1240, 1370),
    H2TaF7 = create(16093, "Hydrogen Heptafluorotantalate", 255, 0, 255, 255)
            .uumMcfg(0, MT.H, 2*U, MT.Ta, U, MT.F, 7*U),
    NH4F = dustdcmp(16094, "Ammonium Fluoride", SET_CUBE, 50, 255, 255, 255)
            .uumMcfg(1, MT.N, U, MT.H, 4*U, MT.F, U)
            .heat(373, 373),
    H2NbOF5 = create(16095, "Hydrogen Pentafluorooxoniobate", 255, 0, 255, 255)
            .uumMcfg(0, MT.H, 2*U, MT.Nb, U, MT.O, U, MT.F, 5*U),
    ColtanFAqSolution = registerLiquid(lquddcmp(16096, "Coltan Leaching Solution", 175, 0, 175, 255)
            .uumMcfg(0, H2TaF7, 20*U, H2NbOF5, 18*U, FeF2, 3*U, MnF2, 3*U, MT.H2O, 10*3*U)
            .heat(MT.H2O)),
    FeMnF2Solution = registerLiquid(lquddcmp(16097, "Iron-Manganese Aqueous Solution", 255, 0, 255, 255)
            .uumMcfg(0, FeF2, 3*U, MnF2, 3*U, MT.H2O, 10*3*U)
            .heat(MT.H2O)),
    MIBK = registerLiquid(lquddcmp(16098, "Methyl Isobutyl Ketone", 255, 255, 255, 100)
            .uumMcfg(0, MT.C, 6*U, MT.H, 12*U, MT.O, U)
            .heat(188, 391)),
    NbTaFMIBKSolution = registerLiquid(lquddcmp(16099, "Niobium Tantalum Fluorotantalate Organic Solution", 100, 150, 150, 255)
            .uumMcfg(0, H2TaF7, 10*U, H2NbOF5, 9*U, MIBK, 19*U)
            .heat(MIBK)),
    NH4FSolution = registerLiquid(lquddcmp(16100, "Ammonium Fluoride Solution", 100, 255, 255, 200)
            .uumMcfg(0, NH4F, 3*U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    TaFMIBKSolution = registerLiquid(lquddcmp(16101, "Hydrogen Heptafluorotantalate Organic Solution", 150, 150, 100, 255)
            .uumMcfg(0, H2TaF7, 10*U, MIBK, 19*U)
            .heat(MIBK)),
    CaOH2 = dustdcmp(16102, "Slaked Lime", SET_DULL, 255, 255, 200, 255)
            .uumMcfg(0, MT.Ca, U, MT.O, 2*U, MT.H, 2*U)
            .heat(853)
            .setSmelting(MT.Quicklime, 2*U5),
    CaCl2Solution = registerLiquid(lquddcmp(16103, "Calcium Chloride Solution", 235, 235, 255, 200)
            .setMcfg(0, MT.CaCl2, 3*U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    FeCl2Solution = registerLiquid(lquddcmp(16104, "Ferrous Chloride Solution", 235, 255, 235, 200)
            .setMcfg(0, MT.FeCl2, 3*U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    MgCl2Solution = registerLiquid(lquddcmp(16105, "Magnesium Chloride Solution", 255, 235, 255, 200)
            .setMcfg(0, MT.MgCl2, 3*U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    Fayalite = dustdcmp(16106, "Fayalite", SET_POWDER, 100, 50, 0, 255)
            .setMcfg(0, Wuestite, 4*U, MT.SiO2, 3*U)
            .heat(C + 1205),
    FerrousSlag = create( 16107, "Ferrous Slag", 255, 200, 180, 255)
            .setMcfg(0, Wuestite, 4*U, MT.SiO2, 3*U)
            .setTextures(SET_FLINT)
            .put(INGOTS, MORTAR, BRITTLE, GEMS)
            .heat(Fayalite)
            .setPulver(Fayalite, U),
    SpongeIron = dustdcmp(16108, "Sponge Iron", SET_METALLIC, 100, 50, 0, 255)
            .setMcfg(0, MT.PigIron, 8*U, FerrousSlag, 7*U)
            .heat(1250),
    MgOH2 = dustdcmp(16109, "Magnesium Hydroxide", SET_DULL, 255, 200, 255, 255)
            .uumMcfg(0, MT.Mg, U, MT.O, 2*U, MT.H, 2*U)
            .heat(623),
    MgO = dustdcmp(16110, "Magnesia", SET_FINE, 255, 255, 255, 255, "Periclase")
            .uumMcfg(0, MT.Mg, U, MT.O, U)
            .heat(3125, 3870),
    MgHCO3 = registerLiquid(lquddcmp(16111, "Magnesium Bicarbonate Solution", 235, 200, 255, 255)
            .uumMcfg(0, MT.Mg, U, MT.H, 2*U, MT.C, 2*U, MT.O,6*U, MT.H2O, 3*U))
            .heat(MT.H2O),
    MgBlastFurnaceGas = registerGas(gasdcmp(16112, "Magnesium-Rich Blast Furnace Gas", 50, 20, 30, 200)
            .uumMcfg(0, MT.Mg, U, MT.CO2, 6*U)
            .heat(MT.Mg)),
    MeteoricCementite = alloymachine(16113, "Meteoric Cementite", SET_METALLIC, 50, 50, 0, 255)
            .setMcfg(3, MT.MeteoricIron, 3*U, MT.C, U)
            .heat(MT.MeteoricSteel)
    ;

    static {
        FL.createMolten(RhodiumPotassiumSulfate.put(MELTING, MOLTEN), 1000);
        FL.createMolten(PbCl2.put(MELTING, MOLTEN), 1000);
        FL.createMolten(Slag.put(MELTING, MOLTEN), 144);
        FL.createMolten(FerrousSlag.put(MELTING, MOLTEN), 144);
        FL.createMolten(FeCr2.put(MELTING, MOLTEN), 144);

        OreDictManager.INSTANCE.addReRegistration("dustCobaltBlue", "dyeMixableBlue");
    }
}
