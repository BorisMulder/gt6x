package org.altadoon.gt6x.common;

import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.render.TextureSet;
import net.minecraft.enchantment.Enchantment;
import org.altadoon.gt6x.Gt6xMod;
import org.altadoon.gt6x.features.electronics.Electronics;

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
    public static final TagData SIMPLE_SOLUTION = TagData.createTagData("PROPERTIES.SimpleSolution", "SimpleSolution");

    private static void addMolten(OreDictMaterial mat, long litersPerUnit) {
        FL.createMolten(mat.put(MELTING, MOLTEN), litersPerUnit);
    }

    private static void addVapour(OreDictMaterial mat) {
        FL.createVapour(mat.put(VAPORS));
    }

    private static void addPlasma(OreDictMaterial mat) {
        FLx.createPlasma(mat.put(PLASMA));
    }

    static {
        // change some properties of vanilla GT6 materials
        MT.NH3    .uumMcfg(1, MT.N, U, MT.H, 3*U);
        MT.Ethanol.setMcfg(0, MT.C, 2*U, MT.H, 6*U, MT.O, U);
        MT.PigIron.uumMcfg(5, MT.Fe, 5*U, MT.C, U).heat(MT.WroughtIron).qual(3, 4.0, 128, 2);
        MT.Steel  .uumMcfg(100, MT.Fe, 100*U, MT.C, U).heat(MT.WroughtIron);
        MT.Olivine.uumMcfg(0, MT.Mg, U, MT.Fe, U, MT.Si, U, MT.O, 4*U);
        MT.Apatite.uumMcfg( 0, MT.Ca, 5*U, MT.PO4, 3*5*U, MT.Cl, U).heat(1900).setLocal("Chlorapatite");
        MT.Phosphorite.uumMcfg( 0, MT.Ca, 5*U, MT.PO4, 3*5*U, MT.F, U).heat(1900).setLocal("Fluorapatite");
        for (OreDictMaterial phosphorus : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusRed, MT.PhosphorusWhite}) {
            phosphorus.uumMcfg( 0, MT.Ca, 3*U, MT.PO4, 2*5*U).heat(1940);
        }
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
            clay.heat(1550);
        }
        MT.Kaolinite.heat(2000);
        MT.As.heat(887, 887).remove(MELTING); MT.As.remove(MOLTEN);

        MT.WroughtIron.qual(3, 6.0, 640, 2);

        MT.AquaRegia.setRGBa(255, 150, 64, 255);

        MT.Plastic.put(POLYMER);
        MT.Rubber.put(POLYMER);
        MT.Teflon.put(POLYMER, PIPES).uumMcfg(0, MT.C, U, MT.F, 2*U)
                .heat(C+327).setRGBa(200, 255, 255, 255)
                .hide(false);
        MT.PVC.put(POLYMER, PIPES).uumMcfg(0, MT.C, 2*U, MT.H, 3*U, MT.Cl, U)
                .heat(C+100).setRGBa(125, 125, 125, 255)
                .hide(false);
        MT.Polycarbonate.put(POLYMER).uumMcfg( 0, MT.C, 16*U, MT.H, 18*U, MT.O, 3*U)
                        .setLocal("Polycarbonate");
        MT.Indigo.uumMcfg(0, MT.C, 16*U, MT.H, 10*U, MT.N, 2*U, MT.O, 2*U)
                .heat(391).setRGBa(75, 0, 130, 255);

        MT.OREMATS.Wolframite.setLocal("Magnesium Tungstate").addSourceOf(MT.Mg);
        MT.OREMATS.Tungstate.setLocal("Lithium Tungstate");
        MT.OREMATS.Huebnerite.setLocal("Hübnerite");
        MT.Glyceryl.setLocal("Nitroglycerin");
        MT.H3BO3.setLocal("Boric Acid");
        MT.FeO3H3.setLocal("Ferric Hydroxide");

        addMolten(MT.K2S2O7, 1000);
        addMolten(MT.Na2S2O7, 1000);
        addMolten(MT.Quicklime, 1000);
        addMolten(MT.Ga, 144);
        addMolten(MT.Nb, 144);

        addVapour(MT.Al);
        addVapour(MT.Zn);
        addVapour(MT.As);
        addVapour(MT.Mg);
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
    public static OreDictMaterial dopedSemiconductor(int aID, String aNameOreDict, OreDictMaterial mainMaterial, Object... aRandomData) {
        OreDictMaterial mat = create(aID, aNameOreDict, 0, 0, 0, 0, aRandomData)
            .setMcfg(0, mainMaterial, U)
            .setPulver(mainMaterial, U).setSmelting(mainMaterial, U)
            .steal(mainMaterial)
            .stealLooks(mainMaterial);
        OP.bouleGt.forceItemGeneration(mat);
        OP.plateGem.forceItemGeneration(mat);
        OP.plateGemTiny.forceItemGeneration(mat);
        Electronics.oxidizedWafer.forceItemGeneration(mat);
        return mat;
    }

    public static OreDictMaterial solution(int id, String name, long r, long g, long b, long a, OreDictMaterial solute, long waterUnits , Object... randomData) {
        OreDictMaterial mat = lquddcmp(id, name, r, g, b, a, randomData);
        mat.setMcfg(0, solute, solute.mComponents.getCommonDivider() * U, MT.H2O, waterUnits * U);
        mat.heat(MT.H2O);
        registerLiquid(mat);
        return mat;
    }

    public static OreDictMaterial simpleSolution(int id, String name, long r, long g, long b, long a, OreDictMaterial solute, long waterUnits , Object... randomData) {
        return solution(id, name, r, g, b, a, solute, waterUnits, SIMPLE_SOLUTION, randomData);
    }

    public static OreDictMaterial registerLiquid(OreDictMaterial mat) {
        FL.createLiquid(mat);
        return mat;
    }

    public static OreDictMaterial registerLiquid(OreDictMaterial mat, int temperature) {
        FL.create(mat.mNameInternal.toLowerCase(), mat.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_MOLTEN), mat.mNameLocal, mat, mat.mRGBaLiquid, STATE_LIQUID, 1000, temperature, null, null, 0);
        return mat;
    }

    public static OreDictMaterial registerGas(OreDictMaterial mat) {
        FL.createGas(mat);
        return mat;
    }

    public static OreDictMaterial registerGas(OreDictMaterial mat, int temperature) {
        FL.create(mat.mNameInternal.toLowerCase(), mat.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_GAS), mat.mNameLocal, mat, mat.mRGBaGas, STATE_GASEOUS, 1000, temperature, null, null, 0);
        return mat;
    }

    public static final OreDictMaterial
    // PGM
    NH4 = create(16001, "Ammonium", 0, 100, 255, 255)
            .setMcfg(1, MT.N, U, MT.H, 4*U),
    AmmoniumHexachloroplatinate = dustdcmp(16002, "Ammonium Hexachloroplatinate", SET_FINE, 255, 220, 10, 255)
            .setMcfg(0, NH4, U*2, MT.Pt, U, MT.Cl, U*6)
            .heat(653)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "PtCl" + NUM_SUB[6]),
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
            .uumMcfg(0, MT.Pd, U, MT.NH3, U*4, MT.Cl, U*2),
    RhodiumSulfate = dustdcmp(16007, "Rhodium Sulfate", SET_CUBE_SHINY, 255, 70, 10, 255)
            .uumMcfg(0, MT.Rh, U*2, MT.S, U*3, MT.O, U*12)
            .heat(500)
            .tooltip("Rh" + NUM_SUB[2] + "(SO" + NUM_SUB[4] + ")" + NUM_SUB[3]),
    RuOsIrResidue = dustdcmp(16008, "Ruthenium Osmium Iridium Residue", SET_SHINY, 90, 150, 200, 15)
            .uumMcfg(0, MT.Ru, U, MT.Os, U, MT.Ir, U)
            .heat(3000),
    Ozone = registerGas(gas(16009, "Ozone", 0, 150, 255, 25)
            .uumMcfg(0, MT.O, U*3)
            .put(POLYATOMIC_NONMETAL, CHALCOGEN)
            .heat(C-192, C-112)),
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
            .setMcfg(0, NH4, U*2, MT.Ru, U, MT.Cl, U*6)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "RuCl" + NUM_SUB[6]),
    IrRhOxide = dustdcmp(16016,  "Iridium-Rhodium Oxide Mixture", SET_FINE, 200, 200, 200, 255)
            .uumMcfg(0, MT.Ir, U, MT.Rh, U, MT.O, U*8),
    NH4Cl = dustdcmp(16017, "Ammonium Chloride", SET_CUBE, 250, 250, 250, 255)
            .setMcfg(0, NH4, U, MT.Cl, U)
            .tooltip("NH" + NUM_SUB[4] + "Cl")
            .heat(338+C, 520+C ),
    RhodiumPotassiumSulfate = dustdcmp(16018, "Rhodium-Potassium Sulfate Mixture", SET_CUBE_SHINY, 255, 100, 150, 255)
            .uumMcfg(0, RhodiumSulfate, 17*U, MT.K2SO4, U*6*7)
            .heat(500),
    RhodiumSulfateSolution = registerLiquid(lquddcmp(16019, "Rhodium Sulfate Solution", 255, 50, 10, 255)
            .uumMcfg(0, RhodiumSulfate, U, MT.H2O, U)
            .heat(MT.H2O)),
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
    Phenol = dustdcmp( 16035, "Phenol", SET_CUBE_SHINY, 200, 150, 100, 255, INGOTS)
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
    Epoxy = plastic( 16041, "Epoxy", SET_DULL, 9, 86, 0, 255)
            .heat(400),
    VinylChloride = registerGas(gasdcmp(16042, "Vinyl Chloride", 150, 255, 150, 50)
            .uumMcfg(0, MT.C, 2*U, MT.H, U*3, MT.Cl, U)
            .heat(119, 260)
            .put(FLAMMABLE)),
    Phosgene = registerGas(gasdcmp(16043, "Phosgene", 255, 255, 255, 50, "Carbonyl Dichloride")
            .setMcfg(0, MT.C, U, MT.O, U, MT.Cl, 2*U)
            .heat(155, 281)),
    CHCl3 = registerLiquid(lquddcmp( 16044, "Chloroform", 150, 255, 200, 255)
            .uumMcfg(0, MT.C, U, MT.H, U, MT.Cl, U*3)
            .heat(210, 334)),
    CHClF2 = registerGas(gasdcmp( 16045, "Chlorodifluoromethane", 150, 200, 255, 255)
            .uumMcfg(0, MT.C, U, MT.H, U, MT.Cl, U, MT.F, U*2)
            .heat(97, 233)),
    C2F4 = registerGas(gasdcmp( 16046, "Tetrafluoroethylene", 150, 255, 255, 255)
            .uumMcfg(0, MT.C, U*2, MT.F, U*4)
            .heat(131, 197)),
    PGMResidue = oredustdcmp(16047, "Platinum Group Leaching Residue", SET_SHINY, 160, 170, 200, 255)
            .uumMcfg(0, MT.Ru, U, MT.Rh, U, MT.Os, U, MT.Ir, U)
            .heat(2900),
    Synoil = registerLiquid(lqudflam( 16048, "Synthetic Oil", 210, 210, 0, 255)
            .heat(100, 400)),
    SCNaphtha = registerLiquid(lqudflam( 16049, "Steam-Cracked Naphtha", 255, 255, 100, 255)
            .heat( 100,  400)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    EthyleneDichloride = registerLiquid(lqudflam( 16050, "Ethylene Dichloride", 100, 255, 100, 255)
            .uumMcfg(0, MT.C, 2*U, MT.H, U*4, MT.Cl, U*2)
            .heat( 238,  357)),
    H3PO4 = registerLiquid(lqudaciddcmp(16051, "Phosphoric Acid", 150, 200, 0, 255)
            .uumMcfg(0, MT.H, 3*U, MT.P, U, MT.O, 4*U)
            .heat(290, C + 212)),
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
            .uumMcfg(0, MT.N, 9*U, MT.CO, 4*U, MT.CO2, 6*U, MT.H, U)
            .put(FLAMMABLE, CENTRIFUGE)
            .heat(100, 200),
            1650),
    ZnBlastFurnaceGas = registerGas(gas(16057, "Zinc-Rich Blast Furnace Gas", 0, 20, 30, 200)
            .uumMcfg(0, MT.Zn, U, BlastFurnaceGas, 6*U)
            .heat(MT.Zn),
            (int)MT.Zn.mBoilingPoint),
    PbO = dustdcmp(16058, "Lead Oxide", SET_DULL, 150, 130, 100, 255)
            .uumMcfg(1, MT.Pb, U, MT.O, U)
            .heat(1161, 1750)
            .setSmelting(MT.Pb, 3*U4),
    ZnO = oredustdcmp(16059, "Zinc Oxide", SET_DULL, 255, 180, 100, 255)
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
    Na2CrO4Solution = simpleSolution(16069, "Sodium Chromate Solution", 255, 255, 0, 255, Na2CrO4, 3),
    DichromateSoda = registerLiquid(lquddcmp(16070, "Sodium Dichromate-Bicarbonate Solution", 255, 125, 0, 255)
            .uumMcfg(0, Na2Cr2O7, 11*U, NaHCO3, 12*U, MT.H2O, 9*U)
            .heat(MT.H2O)),
    Na2CO3Solution = simpleSolution(16071, "Sodium Carbonate Solution", 100, 100, 255, 255, MT.Na2CO3, 3),
    Cr2O3 = dustdcmp(16072, "Chromia", SET_DULL, 100, 255, 100, 255, "Chromium(III) Oxide")
            .uumMcfg(0, MT.Cr, 2*U, MT.O, 3*U)
            .heat(2708, 4270),
    CrSodaMixture = dustdcmp(16073, "Chromia-Soda Mixture", SET_POWDER, 50, 200, 50, 255)
            .uumMcfg(0, Cr2O3, U, MT.Na2CO3, U)
            .heat(Cr2O3),
    CrSlag = dustdcmp(16074, "Chromite Slag", SET_POWDER, 150, 150, 0, 255)
            .setMcfg(0, Na2CrO4, 4*7*U, MT.OREMATS.Wollastonite, 2*U, MT.Fe2O3, 5*U)
            .heat(Na2CrO4),
    Sb2O3 = dustdcmp(16075, "Antimony Trioxide", SET_FINE, 255, 200, 150, 255, "Antimony(III) Oxide")
            .uumMcfg(0, MT.Sb, 2*U, MT.O, 3*U)
            .heat(929, 1698),
    FeS = dustdcmp(16076, "Ferrous Sulfide", SET_SHINY, 66, 66, 66, 255, "Iron(II) Sulfide")
            .uumMcfg(0, MT.Fe, U, MT.S, U)
            .heat(1467),
    FeO = oredustdcmp(16077, "Wuestite", SET_DULL, 50, 0, 0, 255, "Ferrous Oxide", "Iron(II) Oxide")
            .put(INGOTS)
            .setMcfg(0, MT.Fe, U, MT.O, U)
            .heat(C+1377, C+3414)
            .setLocal("Wüstite"),
    H2MoO4 = dustdcmp(16078, "Molybdic Acid", SET_DULL, 200, 200, 0, 255, ACID)
            .uumMcfg(0, MT.H, 2*U, MT.Mo, U, MT.O, 4*U)
            .heat(573),
    PbCl2 = dustdcmp(16079, "Lead Chloride", SET_CUBE, 255, 200, 255, 255, ELECTROLYSER)
            .uumMcfg(0, MT.Pb, U, MT.Cl, 2*U)
            .heat(774,1220),
    Wolframite = oredustdcmp(16080, "TrueWolframite", SET_METALLIC, 100, 100, 120, 255)
            .uumMcfg(0, MT.OREMATS.Ferberite, U, MT.OREMATS.Huebnerite, U)
            .tooltip("(Fe, Mn)WO"+ NUM_SUB[4])
            .setLocal("Wolframite")
            .addSourceOf(MT.Fe, MT.W, MT.Mn)
            .qual(3),
    Vanadinite = oredustdcmp(16081, "Vanadinite", SET_CUBE_SHINY, 153, 51, 0, 255)
            .uumMcfg(0, MT.Pb, 5*U, MT.V, 3*U, MT.O, 12*U, MT.Cl, U)
            .heat(C + 1910)
            .tooltip("Pb" + NUM_SUB[5] + "(VO" + NUM_SUB[4] + ")" + NUM_SUB[3] + "Cl"),
    NaVO3Solution = registerLiquid(liquid(16082, "Sodium Metavanadate Solution", 255, 200, 120, 255)
            .uumMcfg(0, MT.Na, U, MT.V, U, MT.O, 3*U, MT.H2O, 6*U)
            .heat(MT.H2O)),
    NH4VO3 = dustdcmp(16083, "Ammonium Metavanadate", SET_DULL, 255, 200, 150, 255)
            .setMcfg(0, NH4, U, MT.V, U, MT.O, 3*U)
            .tooltip("NH" + NUM_SUB[4] + "VO" + NUM_SUB[3])
            .heat(473),
    NH4ClSolution = simpleSolution(16084, "Ammonium Chloride Solution", 230, 230, 255, 255, NH4Cl, 3),
    CobaltBlue = dustdcmp(16085, "Cobalt Blue", SET_FINE, 0, 71, 171, 255, DYE_INDEX_Blue)
            .uumMcfg(0, MT.Co, U, MT.Al, 2*U, MT.O, 4*U)
            .heat((MT.Al2O3.mMeltingPoint + CoO.mMeltingPoint) / 2),
    NH4SO4 = dustdcmp(16086, "Ammonium Sulfate", SET_CUBE, 255, 255, 230, 255)
            .setMcfg(0, NH4, 2*U, MT.S, U, MT.O, 4*U)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "SO" + NUM_SUB[4])
            .heat(508),
    SeO2 = dustdcmp(16087, "Selenium Dioxide", SET_QUARTZ, 255, 200, 240, 255)
            .uumMcfg(0, MT.Se, U, MT.O, 2*U)
            .heat(613, 623),

    // Refractory Metals
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
    MnF2 = create(16091, "Manganese Fluoride", 255, 150, 200, 255, ELECTROLYSER, ACID)
            .uumMcfg(0, MT.Mn, U, MT.F, 2*U)
            .heat(1129, 2090),
    FeF2 = create(16092, "Ferrous Fluoride", 150, 255, 255, 255, ELECTROLYSER, ACID, "Iron(II) Fluoride")
            .uumMcfg(0, MT.Fe, U, MT.F, 2*U)
            .heat(1240, 1370),
    H2TaF7 = create(16093, "Hydrogen Heptafluorotantalate", 255, 0, 255, 255)
            .uumMcfg(0, MT.H, 2*U, MT.Ta, U, MT.F, 7*U),
    NH4F = dustdcmp(16094, "Ammonium Fluoride", SET_DULL, 50, 255, 255, 255)
            .setMcfg(1, NH4, U, MT.F, U)
            .tooltip("NH" + NUM_SUB[4] + "F")
            .heat(373, 373),
    H2NbOF5 = create(16095, "Hydrogen Pentafluorooxoniobate", 255, 0, 255, 255)
            .uumMcfg(0, MT.H, 2*U, MT.Nb, U, MT.O, U, MT.F, 5*U),
    ColtanFAqSolution = registerLiquid(lquddcmp(16096, "Coltan Leaching Solution", 175, 0, 175, 255)
            .uumMcfg(0, H2TaF7, 20*U, H2NbOF5, 18*U, FeF2, 3*U, MnF2, 3*U, MT.H2O, 10*3*U)
            .heat(MT.H2O)),
    FeMnF2Solution = registerLiquid(lquddcmp(16097, "Iron-Manganese Fluoride Solution", 255, 0, 255, 255)
            .uumMcfg(0, FeF2, 3*U, MnF2, 3*U, MT.H2O, 10*3*U)
            .heat(MT.H2O)),
    MIBK = registerLiquid(lquddcmp(16098, "Methyl Isobutyl Ketone", 255, 255, 255, 100)
            .uumMcfg(0, MT.C, 6*U, MT.H, 12*U, MT.O, U)
            .heat(188, 391)),
    NbTaFMIBKSolution = registerLiquid(lquddcmp(16099, "Niobium Tantalum Fluorotantalate Organic Solution", 100, 150, 150, 255)
            .uumMcfg(0, H2TaF7, 10*U, H2NbOF5, 9*U, MIBK, 19*U)
            .heat(MIBK)),
    NH4FSolution = simpleSolution(16100, "Ammonium Fluoride Solution", 100, 255, 255, 200, NH4F, 3),
    TaFMIBKSolution = registerLiquid(lquddcmp(16101, "Hydrogen Heptafluorotantalate Organic Solution", 150, 150, 100, 255)
            .uumMcfg(0, H2TaF7, 10*U, MIBK, 19*U)
            .heat(MIBK)),

    // Metallurgy
    CaOH2 = dustdcmp(16102, "Slaked Lime", SET_DULL, 255, 255, 200, 255)
            .uumMcfg(0, MT.Ca, U, MT.O, 2*U, MT.H, 2*U)
            .heat(853)
            .setSmelting(MT.Quicklime, 2*U5),
    CaCl2Solution = simpleSolution(16103, "Calcium Chloride Solution", 235, 235, 255, 200, MT.CaCl2, 3),
    FeCl2Solution = solution(16104, "Ferrous Chloride Solution", 235, 255, 235, 200, MT.FeCl2, 3),
    MgCl2Solution = simpleSolution(16105, "Magnesium Chloride Solution", 255, 235, 255, 200, MT.MgCl2, 3),
    Fayalite = dustdcmp(16106, "Fayalite", SET_DULL, 100, 50, 0, 255)
            .setMcfg(0, FeO, 4*U, MT.SiO2, 3*U)
            .tooltip("Fe" + NUM_SUB[2] + "SiO" + NUM_SUB[4])
            .heat(C + 1205),
    FerrousSlag = create( 16107, "Ferrous Slag", 255, 200, 180, 255)
            .setMcfg(0, Fayalite, U)
            .setTextures(SET_FLINT)
            .put(INGOTS, MORTAR, BRITTLE, GEMS)
            .heat(Fayalite)
            .setPulver(Fayalite, U),
    SpongeIron = dustdcmp(16108, "Sponge Iron", SET_METALLIC, 100, 50, 0, 255, "Bloom")
            .setMcfg(0, MT.PigIron, 8*5*U, FerrousSlag, 7*5*U)
            .heat(1250),
    MgOH2 = dustdcmp(16109, "Magnesium Hydroxide", SET_DULL, 255, 200, 255, 255)
            .uumMcfg(0, MT.Mg, U, MT.O, 2*U, MT.H, 2*U)
            .heat(623),
    MgO = dustdcmp(16110, "Magnesia", SET_FINE, 255, 255, 255, 255, "Periclase", "Magnesium Oxide")
            .uumMcfg(0, MT.Mg, U, MT.O, U)
            .heat(3125, 3870),
    MgHCO3 = registerLiquid(lquddcmp(16111, "Magnesium Bicarbonate Solution", 235, 200, 255, 255)
            .uumMcfg(0, MT.Mg, U, MT.H, 2*U, MT.C, 2*U, MT.O,6*U, MT.H2O, 3*U))
            .heat(MT.H2O),
    MgBlastFurnaceGas = registerGas(gasdcmp(16112, "Magnesium-Rich Blast Furnace Gas", 50, 20, 30, 200)
            .uumMcfg(0, MT.Mg, U, MT.CO2, 6*U)
            .heat(MT.Mg),
            (int)MT.Mg.mBoilingPoint),
    Cementite = alloymachine(16113, "Cementite", SET_METALLIC, 50, 50, 0, "Iron Carbide")
            .uumMcfg(3, MT.Fe, 3*U, MT.C, U)
            .heat(MT.WroughtIron),
    MeteoricCementite = alloymachine(16114, "Meteoric Cementite", SET_METALLIC, 50, 50, 0, 255, "Meteoric Iron Carbide")
            .setMcfg(3, MT.MeteoricIron, 3*U, MT.C, U)
            .heat(MT.MeteoricIron),
    ImpureCementite = dustdcmp(16115, "Slag-rich Cementite", SET_METALLIC, 100, 50, 0, 255)
            .setLocal("Impure Cementite")
            .setMcfg(0, Cementite, 8*3*U, FerrousSlag, 7*3*U)
            .heat(Cementite),
    MgOC = machine(16116, "MgO-C", SET_QUARTZ, 100, 100, 100, 255, UNBURNABLE)
            .setMcfg(0, MgO, U, MT.Graphite, U)
            .heat(MgO),
    HotBlast = registerGas(gas(16117, "BlastHot", 225, 208, 245,  15, TRANSPARENT)
            .setLocal("Hot Blast")
            .setMcfg( 0, MT.Air, U),
            1650),
    HBI = create( 16118, "HBI", 150, 150, 150, 255)
            .setLocal("Hot-Briquetted Iron")
            .setMcfg( 0, SpongeIron, U)
            .setTextures(SET_DULL)
            .put(INGOTS)
            .heat(SpongeIron)
            .setPulver(SpongeIron, U),
    ConverterSlag = dustdcmp(16119, "Converter Slag", SET_FLINT, 50, 50, 50, 255)
            .put(INGOTS, MORTAR, BRITTLE, GEMS)
            .heat(FerrousSlag),
    CalcinedDolomite = dustdcmp(16120, "Calcined Dolomite", SET_DULL, 200, 150, 150, 255)
            .setMcfg(0, MT.Quicklime, U, MgO, U)
            .heat((MT.Quicklime.mMeltingPoint + MgO.mMeltingPoint) / 2)
            .put(CENTRIFUGE),
    P2O5 = dustdcmp(16121, "Phosphorus Pentoxide", SET_FINE, 255, 255, 150, 255)
            .uumMcfg(0, MT.P, 2*U, MT.O, 5*U)
            .heat(613, 613),
    P_CO_Gas = registerGas(gas(16122, "Phosphorus-CO Vapour", 100, 100, 0, 50)
            .setMcfg(0, MT.P, U, MT.CO, 5*U) //TODO formula not working
            .heat(MT.CO),
            1700),
    HSST1 = alloymachine(16123, "HSS-T1", SET_METALLIC, 50, 50, 150)
            .setMcfg(0, MT.Fe, 80*U, MT.C, 4*U, MT.W, 6*U, MT.Cr, 4*U, MT.V, U)
            .heat(2086, MT.Fe.mBoilingPoint)
            .qual(3, 12.0, 6144, 4),
    HSSM2 = alloymachine(16124, "HSS-M2", SET_METALLIC, 130, 130, 150)
            .setMcfg(0, MT.Fe, 80*U, MT.C, 4*U, MT.W, 2*U, MT.Cr, 4*U, MT.V, 2*U, MT.Mo, 2*U)
            .heat(2075, MT.Fe.mBoilingPoint)
            .qual(3, 12.0, 7168, 4),

    // Ceramics
    Cement = dustdcmp(16125, "Cement", SET_STONE, 160, 160, 160, 255)
            .heat(1550+C)
            .setSmelting(MT.Stone, U2),
    Mortar = dustdcmp(16126, "Mortar", SET_FOOD, 160, 160, 160, 255)
            .heat(Cement),
    CaAlCement = dustdcmp(16127, "Refractory Cement", SET_STONE, 200, 180, 160, 255)
            .heat(MT.Al2O3),
    RefractoryMortar = dustdcmp(16128, "Refractory Mortar", SET_FOOD, 200, 180, 160, 255)
            .heat(CaAlCement),
    RefractoryCeramic = dustdcmp(16129, "Refractory Ceramic", SET_ROUGH, 255, 235, 200, 255)
            .uumMcfg(0, MT.Ceramic, 2*U, MT.Graphite, U)
            .heat(2100),
    Firebrick = create(16130, "Fire Brick", 255, 235, 200, 255, MORTAR, BRITTLE)
            .setMcfg(0, RefractoryCeramic, U)
            .setAllToTheOutputOf(RefractoryCeramic)
            .heat(RefractoryCeramic)
            .setTextures(SET_ROUGH)
            .put(INGOTS),
    Fireclay = oredustdcmp(16131, "Fireclay", SET_ROUGH, 255, 235, 200, 255, MORTAR)
            .uumMcfg(2, RefractoryCeramic, 2*U, MT.H2O, U)
            .heat(RefractoryCeramic)
            .setSmelting(RefractoryCeramic, U),

    // Electronics
    Methanol = registerLiquid(lquddcmp(16132, "Methanol", 255, 240, 240, 200)
            .setMcfg(0, MT.C, U, MT.H, 4*U, MT.O, U)
            .heat(175, 338)
            .put(FLAMMABLE)),
    Formaldehyde = registerGas(gasdcmp(16133, "Formaldehyde", 200, 255, 255, 100, "Methanal")
            .setMcfg(0, MT.C, U, MT.H, 2*U, MT.O, U)
            .heat(181, 254)),
    PF = plastic(16134, "Phenol Formaldehyde Resin", SET_DULL, 136, 73, 7, 255)
            .heat(600, 600),
    CuCl2 = dustdcmp(16135, "Cupric Chloride", SET_DULL, 184, 135, 0, 255, ELECTROLYSER)
            .uumMcfg(0, MT.Cu, U, MT.Cl, 2*U)
            .heat(771, 1266),
    CuFeClSolution = registerLiquid(lquddcmp(16136, "Cupric-Ferrous Chloride Solution", 66, 245, 206, 255)
            .setMcfg(0, MTx.CuCl2, 3*U, MT.FeCl2, 6*U, MT.H2O, 9*U)
            .heat(MT.H2O)),
    SiH4 = registerGas(gasdcmp(16137, "Silane", 150, 150, 150, 100)
            .uumMcfg(0, MT.Si, U, MT.H, 4*U)
            .heat(88, 161)),
    GeH4 = registerGas(gasdcmp(16138, "Germane", 200, 200, 220, 100)
            .uumMcfg(0, MT.Ge, U, MT.H, 4*U)
            .heat(108, 185)),
    AsH3 = registerGas(gasdcmp(16139, "Arsine", 255, 200, 255, 100, "Arsane")
            .uumMcfg(0, MT.As, U, MT.H, 3*U)
            .heat(162, 211)),
    PH3 = registerGas(gasdcmp(16140, "Phosphine", 255, 220, 150, 100, "Phosphane")
            .uumMcfg(0, MT.P, U, MT.H, 3*U)
            .heat(140, 185)),
    Mg2Si = alloymachine(16141, "Magnesium Silicide", SET_METALLIC, 102, 0, 102)
            .uumAloy(0, MT.Mg, 2*U, MT.Si, U)
            .heat(1375),
    CdS = oredustdcmp(16142, "Cadmium Sulfide", SET_DULL, 255, 204, 0, 255, "Greenockite", "Hawleyite")
            .setMcfg(0, MT.Cd, U, MT.S, U)
            .heat(1250, 1250),
    CdO = dustdcmp(16143, "Cadmium Oxide", SET_DULL, 128, 43, 0, 255)
            .setMcfg(0, MT.Cd, U, MT.O, U)
            .heat(1220, 1832),
    CdSO4 = oredustdcmp(16144, "Cadmium Sulfate", SET_CUBE, 255, 153, 102, 255)
            .setMcfg(0, MT.Cd, U, MT.S, U, MT.O, 4*U)
            .heat(1270)
            .setSmelting(CdO, U3),
    Na3PO4 = dustdcmp(16145, "Sodium Phosphate", SET_DULL, 255, 255, 255, 255, "Trisodium Phosphate")
            .setMcfg(0, MT.Na, 3*U, MT.P, U, MT.O, 4*U)
            .heat(1856),
    ZnNO3 = dustdcmp(16146, "Zinc Nitrate", SET_DULL, 255, 200, 255, 255)
            .setMcfg(0, MT.Zn, U, MT.N, 2*U, MT.O, 6*U)
            .tooltip("Zn(NO" + NUM_SUB[3] + ")" + NUM_SUB[2])
            .heat(383)
            .setSmelting(MTx.ZnO, 2*U9),
    GaAs = alloymachine(16147, "Gallium Arsenide", SET_METALLIC, 96, 96, 120, 255)
            .uumMcfg(0, MT.Ga, U, MT.As, U)
            .heat(1511),
    SiGe = alloymachine(16148, "Silicon-Germanium", SET_METALLIC, 136, 136, 146, 255)
            .uumAloy(0, MT.Si, U, MT.Ge, U)
            .heat(MT.Ge),
    LiH = dustdcmp(16149, "Lithium Hydride", SET_QUARTZ, 0, 153, 153, 255)
            .setMcfg(0, MT.Li, U, MT.H, U)
            .heat(961, 1220)
            .put(GEMS),
    LiF = dustdcmp(16150, "Lithium Fluoride", SET_DULL, 235, 255, 200, 255)
            .setMcfg(0, MT.Li, U, MT.F, U)
            .heat(1118, 1949)
            .put(ELECTROLYSER),
    BF3 = registerGas(gasdcmp(16151, "Boron Trifluoride", 255, 250, 180, 50, "Trifluoroborane")
            .setMcfg(0, MT.B, U, MT.F, 3*U)
            .heat(146, 173)),
    B2O3 = dustdcmp(16152, "Boron Trioxide", SET_DULL, 255, 230, 230, 255, "Diboron Trioxide")
            .setMcfg(0, MT.B, 2*U, MT.O, 3*U)
            .heat(723, 2130),
    Diborane = registerGas(gasdcmp(16153, "Diborane", 255, 255, 255, 100)
            .setMcfg(0, MT.B, 2*U, MT.H, 6*U)
            .heat(108, 181)),
    LiBF4 = dustdcmp(16154, "Lithium Tetrafluoroborate", SET_SHINY, 255, 255, 255, 255)
            .setMcfg(0, MT.Li, U, MT.B, U, MT.F, 4*U)
            .heat(570),
    SiGeH8 = registerGas(gasdcmp(16155, "Silane-Germane Mixture", 175, 175, 185, 100)
            .setMcfg(0, SiH4, U, GeH4, U)
            .heat(GeH4)),
    Na2O = dustdcmp(16156, "Sodium Oxide", SET_DULL, 255, 255, 200, 255)
            .setMcfg(0, MT.Na, 2*U, MT.O, U)
            .heat(1405, 2220),
    CrO3 = dustdcmp(16157, "Chromium(VI) Oxide", SET_ROUGH, 105, 31, 42, 255)
            .setMcfg(0, MT.Cr, U, MT.O, 3*U)
            .heat(470, 523),
    RedMud = oredustdcmp(16158, "Red Mud", SET_ROUGH, 179, 62, 30, 255)
            .heat(MT.Fe2O3),
    Sc2O3 = dustdcmp(16159, "Scandium(III) Oxide", SET_FINE, 255, 255, 255, 255)
            .setMcfg(0, MT.Sc, 2*U, MT.O, 3*U)
            .heat(2758),
    ScF3 = dustdcmp(16160, "Scandium Fluoride", SET_FINE, 200, 255, 200, 255)
            .setMcfg(0, MT.Sc, U, MT.F, 3*U)
            .heat(1825, 1880),
    NaGaOH4 = dustdcmp(16161, "Sodium Gallate", SET_DULL, 50, 0, 150, 255)
            .setMcfg(0, MT.Na, U, MT.Ga, U, MT.O, 4*U, MT.H, 4*U)
            .heat(MT.NaAlO2),
    Na3VO4 = create(16162, "Sodium Orthovanadate", 255, 255, 255, 0)
            .setMcfg(0, MT.Na, 3*U, MT.V, U, MT.O, 4*U),
    BayerLiquor = registerLiquid(lquddcmp(16163, "Bayer Liquor", 200, 50, 0, 255)
            .heat(MT.H2O)),
    GaAmalgam = registerLiquid(lquddcmp(16164, "Gallium Amalgam", 200, 0, 180, 255)
            .setMcfg(0, MT.Hg, 9*U, MT.Ga, U)
            .heat(MT.Hg)),
    NH4NO3 = dustdcmp(16165, "Ammonium Nitrate", SET_SHARDS, 255, 255, 255, 255)
            .setMcfg(0, NH4, U, MT.N, U, MT.O, 3*U)
            .tooltip("NH" + NUM_SUB[4] + "NO" + NUM_SUB[3])
            .heat(443, 483)
            .put(FLAMMABLE, EXPLOSIVE),
    ANFO = dustdcmp(16166, "ANFO", SET_ROUGH, 255, 200, 200, 255)
            .setMcfg(0, NH4NO3, 18*U, MT.Fuel, U)
            .heat(NH4NO3)
            .put(FLAMMABLE, EXPLOSIVE),
    PDopedSi = dopedSemiconductor(16167, "P-Doped Silicon", MT.Si),
    NDopedSi = dopedSemiconductor(16168, "N-Doped Silicon", MT.Si),
    PDopedSiGe = dopedSemiconductor(16169, "P-Doped Silicon-Germanium", SiGe),
    NDopedSiGe = dopedSemiconductor(16170, "N-Doped Silicon-Germanium", SiGe),
    AuGe = alloymachine(16171, "Gold-Germanium", SET_COPPER, 227, 182, 59)
            .uumAloy(0, MT.Au, U, MT.Ge, U)
            .heat(365 + C, (MT.Ge.mBoilingPoint + MT.Au.mBoilingPoint) / 2),
    Naphthalene = registerLiquid(lquddcmp(16172, "Naphthalene", 255, 255, 255, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 8*U)
            .heat(351, 424)
            .put(FLAMMABLE)),
    Anthracene = registerLiquid(lquddcmp(16173, "Anthracene", 225, 255, 150, 255)
            .setMcfg(0, MT.C, 14*U, MT.H, 10*U)
            .heat(489, 614)),
    Anthraquinone = dustdcmp(16174, "Anthraquinone", SET_SHINY, 225, 255, 0, 255)
            .setMcfg(0, MT.C, 14*U, MT.H, 8*U, MT.O, 2*U)
            .heat(558, 650),
    AnthraquinoneDisulfonicAcid = dustdcmp(16175, "Anthraquinone Disulfonic Acid", SET_DULL, 255, 200, 0, 255)
            .setMcfg(0, MT.C, 14*U, MT.H, 8*U, MT.S, 2*U, MT.O, 8*U)
            .heat(211+C, 498+C),
    Diaminoanthraquinone = dustdcmp(16176, "Diaminoanthraquinone", SET_DULL, 150, 0, 0, 255)
            .setMcfg(0, MT.C, 14*U, MT.H, 10*U, MT.N, 2*U, MT.O, 2*U)
            .heat(Anthraquinone),
    Chlorotoluene = dustdcmp(16177, "Monochlorotoluene", SET_DULL, 255, 255, 255, 255)
            .setMcfg(0, MT.C, 6*U, MT.H, 8*U, MT.Cl, U)
            .heat(280, 435),
    Nitrobenzene = registerLiquid(lquddcmp(16178, "Nitrobenzene", 225, 225, 0, 200)
            .setMcfg(0, MT.C, 6*U, MT.H, 5*U, MT.N, U, MT.O, 2*U)
            .heat(279, 484)),
    Aniline = registerLiquid(lqudaciddcmp(16179, "Aniline", 255, 255, 255, 200)
            .setMcfg(0, MT.C, 6*U, MT.H, 7*U, MT.N, U)
            .heat(267, 457)),
    BenzenediazoniumChloride = dustdcmp(16180, "Benzenediazonium Chloride", SET_CUBE, 255, 255, 255, 255, "Phenyldiazonium Chloride")
            .setMcfg(0, MT.C, 6*U, MT.H, 5*U, MT.N, 2*U, MT.Cl, U)
            .heat(191+C, 229+C),
    Nitronaphthalene = dustdcmp(16181, "Nitronaphthalene", SET_DULL, 255, 255, 102, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 7*U, MT.N, U, MT.O, 2*U)
            .heat(325, 400)
            .put(FLAMMABLE),
    Aminonaphthalene = dustdcmp(16182, "Aminonaphthalene", SET_DULL, 255, 255, 102, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 9*U, MT.N, U)
            .heat(320, 400)
            .put(FLAMMABLE),
    NaphthaleneSulfonicAcid = dustdcmp(16183, "Naphthalene sulfonic acid", SET_DULL, 255, 255, 255, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 8*U, MT.S, U, MT.O, 3*U)
            .heat(412),
    Naphthol = dustdcmp(16184, "Naphthol", SET_DULL, 255, 255, 255, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 8*U, MT.O, U)
            .heat(368, 552),
    DNP = dustdcmp(16185, "DNP", SET_DULL, 255, 255, 210, 255, "Dinitrophenol")
            .setMcfg(0, MT.C, 6*U, MT.H, 4*U, MT.N, 2*U, MT.O, 5*U)
            .heat(381)
            .setLocal("2,4-Dinitrophenol")
            .put(EXPLOSIVE),
    Nitronaphthol = dustdcmp(16186, "Nitronaphthol", SET_DULL, 249, 255, 69, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 7*U, MT.N, U, MT.O, 3*U)
            .heat(380),
    Aminonaphthol = dustdcmp(16187, "Aminonaphthol", SET_DULL, 210, 255, 210, 255)
            .setMcfg(0, MT.C, 10*U, MT.H, 9*U, MT.N, U, MT.O, U)
            .heat(375),
    DNQ = dustdcmp(16188, "Diazonaphthoquinone", SET_FINE, 150, 50, 180, 250, "DNQ")
            .uumMcfg(0, MT.C, 10*U, MT.H, 6*U, MT.N, 2*U, MT.O, U)
            .heat(400),
    NPhenylGlycine = dustdcmp(16189, "N-Phenylglycine", SET_CUBE, 255, 255, 255, 255, "Anilinoacetic Acid")
            .setMcfg(0, MT.C, 8*U, MT.H, 9*U, MT.N, U, MT.O, 2*U)
            .heat(400),
    QuinizarineGreen = dustdcmp(16190, "Quinizarine Green", SET_DULL, 60, 134, 57, 255)
            .uumMcfg(0, MT.C, 28*U, MT.H, 22*U, MT.N, 2*U, MT.O, 2*U)
            .heat(493),
    AlizarinRed = dustdcmp(16191, "Alizarin Red", SET_DULL, 186, 24, 47, 255)
            .uumMcfg(0, MT.C, 14*U, MT.H, 8*U, MT.O, 4*U)
            .heat(562, 703),
    SolventYellow = dustdcmp(16192, "Solvent Yellow", SET_DULL, 200, 200, 10, 255)
            .heat(124+C, 172+C),
    OrganolBrown = dustdcmp(16193, "Organol Brown", SET_DULL, 153, 51, 0, 255)
            .uumMcfg(0, MT.C, 16*U, MT.H, 12*U, MT.N, 2*U, MT.O, U)
            .heat(124+C, 172+C),
    SulfurBlack = dustdcmp(16194, "Sulfur Black", SET_DULL, 25, 25, 25, 255)
            .uumMcfg(0, MT.C, 24*U, MT.H, 16*U, MT.N, 6*U, MT.O, 8*U, MT.S, 8*U)
            .heat(450),
    N2O3 = registerLiquid(lquddcmp(16195, "Dinitrogen Trioxide", 0, 0, 150, 200))
            .setMcfg(0, MT.N, 2*U, MT.O, 3*U)
            .heat(172, 276),
    HNO2 = registerLiquid(lqudaciddcmp(16196, "Nitrous Acid", 0, 0, 200, 200)
            .uumMcfg(0, MT.H, U, MT.N, U, MT.O, 2*U)
            .heat(MT.H2O)),
    HCN = registerGas(gasdcmp(16197, "Hydrogen Cyanide", 255, 255, 255, 200, FLAMMABLE, ACID)
            .uumMcfg(0, MT.H, U, MT.C, U, MT.N, U)
            .heat(260, 299)),
    NH4SO4Solution = simpleSolution(16198, "Ammonium Sulfate Solution", 255, 255, 230, 200, NH4SO4, 3),
    NaOHSolution = simpleSolution(16199, "Sodium Hydroxide Solution", 220, 250, 220, 255, MT.NaOH, 3),
    DiluteH2SO4 = registerLiquid(lqudaciddcmp(16200, "Dilute Sulfuric Acid", 255, 192, 128, 200))
            .setMcfg(0, MT.H2SO4, 7*U, MT.H2O, 3*U)
            .heat(MT.H2SO4),
    DnqNovolacResist = registerLiquid(lquddcmp(16201, "DNQ-Novolac Photoresist", 84, 145, 84, 200)
            .heat(MT.H2O)),
    Na2SO4Solution = simpleSolution(16202, "Sodium Sulfate Solution", 190, 190, 140, 255, MT.Na2SO4, 3),
    ArF = registerGas(gasdcmp(16203, "Argon-Fluorine", 64, 255, 0, 200)
            .setMcfg(0, MT.Ar, U, MT.F, U)
            .heat(MT.Ar)),
    KrF = registerGas(gasdcmp(16204, "Krypton-Fluorine", 192, 255, 128, 200)
            .setMcfg(0, MT.Kr, U, MT.F, U)
            .heat(MT.Kr)),
    CeO2 = dustdcmp(16205, "Cerium(IV) Oxide", SET_DULL, 255, 255, 204, 255)
            .setMcfg(0, MT.Ce, U, MT.O, 2*U)
            .heat(2670, 3770),
    NitratoCericAcid = registerLiquid(lqudaciddcmp(16206, "Nitrato Ceric Acid", 255, 100, 0, 255)
            .setMcfg(0, MT.H, 2*U, MT.Ce, U, MT.N, 6*U, MT.O, 18*U)
            .tooltip("H" + NUM_SUB[2] + "Ce(" + "NO" + NUM_SUB[3] + ")" + NUM_SUB[6])
            .heat(MT.H2O)),
    CAN = dustdcmp(16207, "CAN", SET_CUBE, 255, 50, 0, 255)
            .setLocal("Ceric Ammonium Nitrate")
            .setMcfg(0, MT.Ce, U, NH4, 2*U, MT.N, 6*U, MT.O, 18*U)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "Ce(" + "NO" + NUM_SUB[3] + ")" + NUM_SUB[6])
            .heat(380),
    ChromeEtch = registerLiquid(lqudaciddcmp(16208, "Chromium Etchant", 255, 150, 0, 255)
            .setMcfg(0, MT.HNO3, U, CAN, U)
            .heat(MT.HNO3)),
    CrNO3Solution = registerLiquid(lqudaciddcmp(16209, "Chromium Nitrate-Cerous Ammonium Nitrate Solution", 50, 0, 50, 255)
            .tooltip("Cr(NO" + NUM_SUB[3] + ")" + NUM_SUB[3] + " + 3 (NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "Ce(" + "NO" + NUM_SUB[3] + ")" + NUM_SUB[5] + " + n HNO" + NUM_SUB[3])
            .heat(MT.H2O)),
    CCl4 = registerLiquid(lquddcmp(16210, "Tetrachloromethane", 200, 255, 200, 200)
            .setMcfg(0, MT.C, U, MT.Cl, 4*U)
            .heat(250, 350)),
    CF4 = registerGas(gasdcmp(16211, "Tetrafluoromethane", 200, 255, 255, 200)
            .setMcfg(0, MT.C, U, MT.F, 4*U)
            .heat(89, 145, 310)),
    Y2O3 = dustdcmp(16212, "Yttria", SET_SHINY, 255, 255, 255, 255, GEMS)
            .setMcfg(0, MT.Y, 2*U, MT.O, 3*U)
            .heat(2698, 4570),
    YAlO3 = machine(16213, "Yttria-Alumina", SET_DULL, 200, 255, 255, PIPES)
            .setMcfg(0, MT.Al2O3, U, Y2O3, U)
            .heat(Y2O3),
    SiF4 = registerGas(gasdcmp(16214, "Tetrafluorosilane", 50, 50, 100, 200)
            .uumMcfg(0, MT.Si, U, MT.F, 4*U)
            .heat(178, 182)),
    AsF3 = registerLiquid(lquddcmp(16215, "Arsenic Trifluoride", 100, 130, 100, 250)
            .setMcfg(0, MT.As, U, MT.F, 3*U)
            .heat(265, 334)),
    H3AsO3 = registerLiquid(lqudaciddcmp(16216, "Arsenous Acid", 150, 200, 175, 255)
            .setMcfg(0, MT.H, 3*U, MT.As, U, MT.O, 3*U)
            .heat(MT.H2O)),
    PiranhaEtch = registerLiquid(lqudaciddcmp(16217, "Piranha Solution", 245, 126, 66, 255)
            .uumMcfg(0, MT.H, 4*U, MT.S, U, MT.O, 6*U)
            .heat(MT.H2O)),
    NF3 = registerGas(gasdcmp(16218, "Nitrogen Trifluoride", 0, 240, 255, 200)
            .setMcfg(1, MT.N, U, MT.F, 3*U)
            .heat(66, 144, 310)),
    NH4SiF6 = dustdcmp(16219, "Ammonium Hexafluorosilicate", SET_CUBE, 245, 225, 65, 255)
            .setMcfg(0, NH4, 2*U, MT.Si, U, MT.F, 6*U)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "SiF" + NUM_SUB[6])
            .heat(373),
    NaCN = dustdcmp(16220, "Sodium Cyanide", SET_DULL, 255, 255, 255, 255)
            .setMcfg(0, MT.Na, U, MT.C, U, MT.N, U)
            .heat(837, 1769),
    KCN = dustdcmp(16221, "Potassium Cyanide", SET_FINE, 255, 255, 255, 255)
            .setMcfg(0, MT.K, U, MT.C, U, MT.N, U)
            .heat(907, 1898),
    NaCNSolution = simpleSolution(16222, "Sodium Cyanide Solution", 150, 200, 255, 250, NaCN, 3),
    KCNSolution = simpleSolution(16223, "Potassium Cyanide Solution", 200, 150, 255, 250, KCN, 3),
    NaAuC2N2 = registerLiquid(lquddcmp(16224, "Sodium Dicyanoaurate Solution", 200, 200, 100, 250)
            .setMcfg(0, MT.Na, U, MT.Au, U, MT.C, 2*U, MT.N, 2*U)
            .heat(MT.H2O)),
    KAuC2N2 = registerLiquid(lquddcmp(16225, "Potassium Dicyanoaurate Solution", 200, 200, 100, 250)
            .setMcfg(0, MT.K, U, MT.Au, U, MT.C, 2*U, MT.N, 2*U)
            .heat(MT.H2O)),
    KOHSolution = simpleSolution(16226, "Potassium Hydroxide Solution", 200, 100, 200, 250, MT.KOH, 3),
    RuCl3 = dustdcmp(16227, "Ruthenium(III) Chloride", SET_METALLIC, 20, 20, 20, 255)
            .uumMcfg(0, MT.Ru, U, MT.Cl, 3*U)
            .heat(500 + C),
    Urea = dustdcmp(16228, "Urea", SET_CUBE, 255, 255, 255, 255)
            .uumMcfg(0, MT.C, U, MT.O, U, MT.N, 2*U, MT.H, 4*U)
            .tooltip("CO(NH" + NUM_SUB[2] + ")" + NUM_SUB[2]),
    H3NSO3 = registerLiquid(lqudaciddcmp(16229, "Sulfamic Acid", 50, 150, 200, 200)
            .setMcfg(0, MT.H, 3*U, MT.N, U, MT.S, U, MT.O, 3*U)
            .heat(MT.H2O)),
    H3Ru2NCl8H4O2 = registerLiquid(lqudaciddcmp(16230, "Octachloro-μ-nitrido-diaquoruthenic Acid", 155, 255, 155, 250)
            .setMcfg(0, MT.H, 3*U, MT.Ru, 2*U, MT.N, U, MT.Cl, 8*U, MT.H2O, 6*U)
            .heat(MT.H2O)),
    RuElectrolyte = registerLiquid(lquddcmp(16231, "Ruthenium Electroplating Solution", 155, 190, 155, 250)
            .setMcfg(0, MT.N, 4*U, MT.H, 16*U, MT.Ru, U, MT.Cl, 8*U, MT.O, 2*U)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[3] + "Ru" + NUM_SUB[2]+ "NCl" + NUM_SUB[8]+ "(H" + NUM_SUB[2] + "O)"+ NUM_SUB[2])
            .heat(MT.H2O)),
    PdAg = alloymachine(16232, "Palladium-Silver", SET_SHINY, 174, 174, 192, 255)
            .uumAloy(0, MT.Pd, U, MT.Ag, U)
            .heat(1475, 1762),
    AlEtch = registerLiquid(lqudaciddcmp(16233, "Aluminium Etchant", 100, 200, 0, 255)
            .setMcfg(0, MT.HNO3, 5*U, H3PO4, 8*U)
            .heat(H3PO4)),
    AlPO4 = dustdcmp(16234, "Aluminium Phosphate", SET_DULL, 200, 225, 255, 255)
            .setMcfg(0, MT.Al, U, MT.P, U, MT.O, 4*U)
            .heat(2070),
    Na3PO4Solution = simpleSolution(16235, "Sodium Phosphate Solution", 200, 200, 255, 200, Na3PO4, 3),
    FeCl3Solution = registerLiquid(lquddcmp(16236, "Ferric Chloride Solution", 180, 180, 120, 200)
            .setMcfg(0, MT.FeCl3, 8*U, MT.H2O, 9*U)
            .heat(MT.H2O)),
    NaHSO4Solution = simpleSolution(16237, "Sodium Bisulfate Solution", 240, 240, 255, 200, MT.NaHSO4, 3),
    N2O = registerGas(gasdcmp(16238, "Nitrous Oxide", 255, 255, 255, 200)
            .uumMcfg(0, MT.N, 2*U, MT.O, U)
            .heat(182, 184)),
    Isopropanol = registerLiquid(lqudflam(16239, "Isopropanol", 255, 255, 255, 200, "2-propanol", "Isopropyl Alcohol")
            .uumMcfg(0, MT.C, 3*U, MT.H, 8*U, MT.O, U)
            .heat(184, 356)),
    SolderingPaste = registerLiquid(lquddcmp(16240, "Solder Paste", 255, 180, 0, 255)
            .heat(Isopropanol)),
    SF6 = registerGas(gasdcmp(16241, "Sulfur Hexafluoride", 240, 255, 200, 150)
            .uumMcfg(1, MT.S, U, MT.F, 6*U)
            .heat(209, 222, 310)),
    AlPO4Solution = registerLiquid(lquddcmp(16242, "Aluminium Phosphate Solution", 200, 225, 255, 200)
            .setMcfg(0, AlPO4, 6*U, MT.HNO3, 5*U)
            .heat(MT.H2O)),
    CoPtCr = alloymachine(16243, "Cobalt-Platinum-Chromium", SET_COPPER, 145, 163, 243)
            .uumAloy(0, MT.Co, 5*U, MT.Pt, U, MT.Cr, 3*U)
            .heat(1880, 3300),
    Hydroxyapatite = oredustdcmp(16244, "Hydroxyapatite", SET_DIAMOND, 150, 150, 80, 255)
            .uumMcfg(0, MT.Ca, 5*U, MT.PO4, 3*U, MT.O, U, MT.H, U)
            .heat(MT.Apatite)
            .setOreMultiplier(4)
    ;

    static {
        MT.BlueSapphire.uumMcfg(6, MT.Al2O3, 5*U, MT.Fe2O3, U);
        MT.Ruby.uumMcfg(6, MT.Al2O3, 5*U, Cr2O3, U);
        MT.GreenSapphire.uumMcfg(6, MT.Al2O3, 5*U, MgO, U);
        MT.PurpleSapphire.uumMcfg(6, MT.Al2O3, 5*U, MT.V2O5, U);
        MT.PetCoke.setMcfg(0, MT.C, U);

        addMolten(RhodiumPotassiumSulfate, 144);
        addMolten(PbCl2, 144);
        addMolten(CuCl2, 144);
        addMolten(Slag, 144);
        addMolten(FerrousSlag, 144);
        addMolten(FeCr2, 144);
        addMolten(ConverterSlag, 144);
        addMolten(Phenol, 144);
        addMolten(Epoxy, 144);
        addMolten(LiF, 144);
        addMolten(SiGe, 144);
        addMolten(GaAs, 144);

        addPlasma(CF4);
        addPlasma(NF3);
        addPlasma(SF6);

        OP.bouleGt.forceItemGeneration(GaAs, SiGe);
        OP.plate.forceItemGeneration(MT.Al2O3);
        OP.wireFine.forceItemGeneration(MT.Ta);

        OreDictManager.INSTANCE.addReRegistration("dustCobaltBlue", "dyeMixableBlue");
        OreDictManager.INSTANCE.addReRegistration("dustIndigo", "dyeMixableBlue");
        OreDictManager.INSTANCE.addReRegistration("dustQuinizarineGreen", "dyeMixableGreen");
        OreDictManager.INSTANCE.addReRegistration("dustAlizarinRed", "dyeMixableRed");
        OreDictManager.INSTANCE.addReRegistration("dustSolventYellow", "dyeMixableYellow");
        OreDictManager.INSTANCE.addReRegistration("dustOrganolBrown", "dyeMixableBrown");
        OreDictManager.INSTANCE.addReRegistration("dustSulfurBlack", "dyeMixableBlack");
        OreDictManager.INSTANCE.addReRegistration("dustSudanI", "dyeMixableOrange");

        HSST1.addEnchantmentForWeapons(Enchantment.sharpness, 4).addEnchantmentForAmmo(Enchantment.sharpness, 4).addEnchantmentForRanged(Enchantment.power, 4);
        HSSM2.addEnchantmentForWeapons(Enchantment.sharpness, 4).addEnchantmentForAmmo(Enchantment.sharpness, 4).addEnchantmentForRanged(Enchantment.power, 4);
    }
}
