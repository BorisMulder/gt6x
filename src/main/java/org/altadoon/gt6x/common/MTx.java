package org.altadoon.gt6x.common;

import gregapi.code.HashSetNoNulls;
import gregapi.code.TagData;
import gregapi.data.*;
import gregapi.old.Textures;
import gregapi.oredict.OreDictManager;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.render.IIconContainer;
import gregapi.render.TextureSet;
import gregapi.util.UT;
import net.minecraft.enchantment.Enchantment;
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
    public static final HashSetNoNulls<OreDictMaterial> ALL_MATERIALS_REGISTERED_HERE = new HashSetNoNulls<>();

    public static void touch() {}

    public static final TagData POLYMER = TagData.createTagData("PROPERTIES.Polymer", "Polymer");
    public static final TagData SIMPLE_SOLUTION = TagData.createTagData("PROPERTIES.SimpleSolution", "SimpleSolution");
    public static final TagData VAPORIZING = TagData.createTagData("PROCESSING.Vaporizing", "Vaporizable");
    public static final TagData IONIZING = TagData.createTagData("PROCESSING.Ionizing", "Ionizable");
    public static final TagData ANION = TagData.createTagData("PROPERTIES.Anion", "Anion");
    public static final TagData CATION = TagData.createTagData("PROPERTIES.Cation", "Cation");

    private static void addMolten(OreDictMaterial mat) {
        addMolten(mat, mat.mMeltingPoint <= 0 ? 1000 : mat.mMeltingPoint < 300 ? Math.min(300, mat.mBoilingPoint - 1) : mat.mMeltingPoint);
    }

	private static void addMolten(OreDictMaterial mat, long temperature) {
		mat.put(MELTING, MOLTEN);
		FL.create(
			"molten." + mat.mNameInternal.toLowerCase(),
			mat.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_MOLTEN),
			"Molten " + mat.mNameLocal,
			mat, mat.mRGBaLiquid, STATE_LIQUID, 144, temperature, null, null, 0
		).setLuminosity(10);
	}

    private static void addVapour(OreDictMaterial mat) {
        mat.put(VAPORIZING);
        long temperature = mat.mBoilingPoint;
        if (temperature <= 0) temperature = 3000;
        else if (temperature < 300) temperature = Math.min(300, mat.mPlasmaPoint - 1);

        FL.create("vapor." + mat.mNameInternal.toLowerCase(), mat.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_GAS),
            "Vaporized " + mat.mNameLocal, mat, mat.mRGBaGas, STATE_GASEOUS, 1000,
                temperature, null, null, 0);
    }

    public static void addPlasma(OreDictMaterial mat) {
        addPlasma(mat, false, Math.max(2000, mat.mBoilingPoint + 200)); //TODO change the plasma point into something better
    }

    public static void addPlasma(OreDictMaterial mat, boolean customTexture, long temperature) {
        if (mat.mGas == null && !mat.contains(GASES)) {
            addVapour(mat);
        }
        mat.mPlasmaPoint = temperature;
        mat.put(IONIZING);
        if (!customTexture) {
            mat.setRGBaPlasma(UT.Code.bind8(mat.mRGBaGas[0] + 50),
                              UT.Code.bind8(mat.mRGBaGas[1] + 50),
                              UT.Code.bind8(mat.mRGBaGas[2] + 50),
            100);
        }

        IIconContainer texture = customTexture ?
                new Textures.BlockIcons.CustomIcon(Gt6xMod.MOD_ID + ":fluids/" + "plasma."+mat.mNameInternal.toLowerCase()) :
                mat.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_PLASMA);
        FL.create("plasma."+mat.mNameInternal.toLowerCase(), texture,
                mat.mNameLocal + " Plasma", mat, mat.mRGBaPlasma, STATE_PLASMA, 2000,
                temperature, null, null, 0).setLuminosity(15);
    }

    static {
        // change some properties of vanilla GT6 materials
        MT.NH3      .uumMcfg(1, MT.N, U, MT.H, 3*U);
        MT.Ethanol  .setMcfg(0, MT.C, 2*U, MT.H, 6*U, MT.O, U).heat(159, 351);
        MT.Ethylene .setMcfg(0, MT.C, 2*U, MT.H, 4*U).heat(104, 169);
        MT.Propylene.setMcfg(0, MT.C, 3*U, MT.H, 6*U).heat(88, 225);
        MT.Propane  .setMcfg(0, MT.C, 3*U, MT.H, 8*U).heat(86, 231);
        MT.Butane   .setMcfg(0, MT.C, 4*U, MT.H, 10*U).heat(136, 273);

        MT.Petrol.heat(220, 70+C);
        MT.Kerosine.heat(240, 200+C);
        MT.Diesel.heat(250, 300+C);
        MT.Fuel.heat(260, 400+C).setLocal("Fuel Oil");

        MT.CH4.heat(91, 112);
        MT.CO.heat(68, 82);
        MT.CO2.heat(195, 195);

        MT.OREMATS.Wollastonite.setMcfg(0, MT.Quicklime, 2*U, MT.SiO2, 3*U).tooltip("CaSiO" + NUM_SUB[3]);
        MT.PigIron .uumMcfg(5, MT.Fe, 5*U, MT.C, U).heat(1445, MT.Fe.mBoilingPoint).qual(3, 4.0, 128, 2).setAllToTheOutputOf(MT.PigIron);
        MT.PigIron.remove(MOLTEN);
        addMolten(MT.PigIron, C+1510);
        MT.IronCast.setMcfg(8, MT.Fe, 8*U, MT.C, U).heat(1260+C, MT.Fe.mBoilingPoint).qual(3, 5.0, 256, 2).setAllToTheOutputOf(MT.IronCast).hide(false).add(DUSTS, PIPES);
        MT.Fe.mReRegistrations.remove(MT.IronCast); MT.IronCast.mToThis.remove(MT.Fe);
        MT.WroughtIron.heat(MT.Fe.mMeltingPoint, MT.Fe.mBoilingPoint);
        MT.Steel        .uumMcfg(100, MT.Fe, 100*U, MT.C, U).heat(1780, MT.Fe.mBoilingPoint);
        ANY.Steel.heat(MT.Steel);
        MT.DamascusSteel.uumMcfg(100, MT.Fe, 100*U, MT.C, U).heat(MT.Steel).qual(3,  6.0, 480, 2);
        MT.StainlessSteel.heat(1800, MT.Fe.mBoilingPoint);
        MT.Sodalite.uumMcfg(0, MT.Na, 8*U, MT.Al, 6*U, MT.Si, 6*U, MT.O, 24*U, MT.Cl, 2*U);
        MT.Lazurite.uumMcfg(0, MT.Na, 7*U, MT.Ca, U, MT.Al, 6*U, MT.Si, 6*U, MT.S, 4*U, MT.O, 28*U, MT.H2O, 3*U)
                .tooltip("Na" + NUM_SUB[7] + "CaAl" + NUM_SUB[6] + "Si" + NUM_SUB[6] + "O" + NUM_SUB[24] + "(SO" + NUM_SUB[4] + ")S" + NUM_SUB[3] + "(H" + NUM_SUB[2] + "O)");
        MT.VitriolOfClay.tooltip("Al" + NUM_SUB[2] + "(SO" + NUM_SUB[4] + ")" + NUM_SUB[3]);
        MT.OREMATS.Lepidolite.setMcfg(0, MT.K, U, MT.Li, U, MT.Rb, U, MT.Al, U, MT.Si, 4*U, MT.O, 11*U, MT.H, U, MT.F, U);

        MT.Olivine.uumMcfg(0, MT.Mg, U, MT.Fe, U, MT.Si, U, MT.O, 4*U);

        MT.Apatite.uumMcfg( 0, MT.Ca, 5*U, MT.PO4, 3*5*U, MT.Cl, U).heat(1900).setLocal("Chlorapatite");
        MT.Phosphorite.uumMcfg( 0, MT.Ca, 5*U, MT.PO4, 3*5*U, MT.F, U).heat(1900).setLocal("Fluorapatite");
        for (OreDictMaterial phosphorus : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusRed, MT.PhosphorusWhite}) {
            phosphorus.uumMcfg(0, MT.Ca, 3*U, MT.P, 2*U, MT.O, 8*U).tooltip("Ca"+NUM_SUB[3]+"(PO"+NUM_SUB[4]+")"+NUM_SUB[2]);
            phosphorus.setLocal("Calcium Phosphate");
            if (phosphorus.mID != MT.Phosphorus.mID) phosphorus.hide(true);
        }
        MT.PO4.hide(true);
        for (OreDictMaterial clay : ANY.Clay.mToThis) {
            clay.heat(1550);
        }
        ANY.Fluorite.addReRegistrationToThis(MT.FluoriteYellow, MT.FluoriteBlack, MT.FluoriteBlue, MT.FluoriteGreen, MT.FluoriteMagenta, MT.FluoriteOrange, MT.FluoritePink, MT.FluoriteRed, MT.FluoriteWhite);
        MT.Kaolinite.heat(2000);
        MT.NaHCO3.heat(C+80).setSmelting(MT.Na2CO3, U2);

        MT.Plastic.put(POLYMER);
        MT.Rubber.put(POLYMER);
        MT.Teflon.put(POLYMER, PIPES).uumMcfg(10, MT.C, U, MT.F, 2*U)
                .heat(C+327).setRGBa(200, 255, 255, 255)
                .hide(false);
        MT.PVC.put(POLYMER, PIPES).uumMcfg(10, MT.C, 2*U, MT.H, 3*U, MT.Cl, U)
                .heat(C+100).setRGBa(125, 125, 125, 255)
                .hide(false);
        MT.Polycarbonate.put(POLYMER).uumMcfg( 10, MT.C, 16*U, MT.H, 18*U, MT.O, 3*U)
                .setLocal("Polycarbonate")
                .heat(C+302)
                .hide(false);
        for (OreDictMaterial mat : new OreDictMaterial[]{MT.N, MT.P, MT.As, MT.O, MT.S, MT.Se, MT.Te, MT.F, MT.Cl, MT.Br, MT.I, MT.At, MT.Ts, MT.CO3, MT.PO4}) {
            mat.put(ANION);
        }
        for (OreDictMaterial mat : MT.ALL_MATERIALS_REGISTERED_HERE) {
            if (mat.containsAll(TD.Atomic.ELEMENT, TD.Atomic.METAL))
                mat.put(CATION);
        }
        MT.H.put(CATION); MT.Ge.put(CATION); MT.Sb.put(CATION);

        MT.Indigo.uumMcfg(1, MT.C, 16*U, MT.H, 10*U, MT.N, 2*U, MT.O, 2*U)
                .heat(391).setRGBa(75, 0, 130, 255);
        MT.Sc.hide(false);
        MT.Eu.hide(false);
        MT.Te.hide(false);
        MT.Tl.hide(false);
        registerLiquid(MT.BioFuel.hide(false));

        MT.OREMATS.Wolframite.setLocal("Magnesium Tungstate").addSourceOf(MT.Mg);
        MT.OREMATS.Tungstate.setLocal("Lithium Tungstate");
        MT.OREMATS.Huebnerite.setLocal("Hübnerite");
        MT.Glyceryl.setLocal("Nitroglycerin");
        MT.HCl.setLocal("Hydrogen Chloride");
        MT.H3BO3.setLocal("Boric Acid");
        MT.FeO3H3.setLocal("Ferric Hydroxide");
        MT.DarkAsh.setLocal("Coal Ash");
        for (OreDictMaterial volcanic : new OreDictMaterial[] { MT.STONES.Komatiite, MT.STONES.Pumice, MT.STONES.Gabbro, MT.STONES.Basalt}) {
            for (int i = 0; i < volcanic.mComponents.getComponents().size(); i++) {
                OreDictMaterialStack mat = volcanic.mComponents.getComponents().get(i);
                OreDictMaterialStack matUndiv = volcanic.mComponents.getUndividedComponents().get(i);
                if (mat.mMaterial.mID == MT.DarkAsh.mID) {
                    mat.mMaterial = MT.VolcanicAsh;
                }
                if (matUndiv.mMaterial.mID == MT.DarkAsh.mID) {
                    matUndiv.mMaterial = MT.VolcanicAsh;
                }
            }
        }
        MT.AluminiumAlloy.setLocal("Aluminium Alloy 4015");

        addMolten(MT.IronCast);
        addMolten(MT.NaCl);
        addMolten(MT.KCl);
        addMolten(MT.K2S2O7);
        addMolten(MT.Na2S2O7);
        addMolten(MT.Quicklime);
        addMolten(MT.Ga);
        addMolten(MT.Nb);
        addMolten(MT.Cd);

        for (OreDictMaterial mat : new OreDictMaterial[] { MT.C, MT.C_13, MT.C_14, MT.As }) {
            mat.remove(MELTING);
        }

        addVapour(MT.C.heat(3915, 3915));
        ANY.C.heat(MT.C);
        addVapour(MT.As.heat(887, 887));
        addVapour(MT.Zn);
        addVapour(MT.Mg);
        addVapour(MT.K);
        addVapour(MT.P.setLocal("Phosphorus"));

        MT.Graphene.heat(MT.C).put(DUSTS);
        MT.Graphite.heat(MT.C);
        MT.C_13.heat(MT.C);
        MT.C_14.heat(MT.C);
        MT.SiC.heat(3100, MT.Si.mBoilingPoint).setSmelting(MT.Graphite, U2);

        for (OreDictMaterial mat : new OreDictMaterial[] { MT.H, MT.D, MT.T, MT.He, MT.He_3, MT.Li, MT.Li_6, MT.Be, MT.Be_7, MT.Be_8, MT.B, MT.B_11, MT.C, MT.C_13, MT.C_14, MT.N, MT.O }) {
            addPlasma(mat);
        }
    }

    public static OreDictMaterial create(int id, String name) {
        OreDictMaterial result = OreDictMaterial.createMaterial(id, name, name);
        if (id > 0) {
            result.setOriginalMod(Gt6xMod.MOD_DATA);
            ALL_MATERIALS_REGISTERED_HERE.add(result);
        }
        return result;
    }

    public static OreDictMaterial create(int id, String name, long r, long g, long b, long a, Object... randomData) {
        if (id <= 16000 || id > 16999) {
            throw new IllegalArgumentException(name + ": GT6X materials should have IDs in the 16001-16999 range");
        }

        return create(id, name).setRGBa(r, g, b, a).put(randomData);
    }

    public static OreDictMaterial liquid(int id, String name, long r, long g, long b, long a, Object... randomData) { return create(id, name, r, g, b, a, LIQUID, randomData).setTextures(SET_FLUID).put(G_CONTAINERS, CONTAINERS_FLUID); }
    public static OreDictMaterial gas (int id, String name, long r, long g, long b, long a, Object... randomData) { return create(id, name, r, g, b, a, GASES, randomData).setTextures(SET_GAS).put(G_CONTAINERS, CONTAINERS_GAS); }
    public static OreDictMaterial dustdcmp(int id, String name, TextureSet[] aSets, long r, long g, long b, long a, Object... randomData) { return create(id, name, r, g, b, a, randomData).setTextures(aSets).put(DECOMPOSABLE, G_DUST, MORTAR); }
    public static OreDictMaterial oredustdcmp(int id, String name, TextureSet[] aSets, long r, long g, long b, long a, Object... randomData) { return create(id, name, r, g, b, a, randomData).setTextures(aSets).put(DECOMPOSABLE, G_DUST_ORES, MORTAR); }
    public static OreDictMaterial lquddcmp (int id, String name, long r, long g, long b, long a, Object... randomData) { return liquid(id, name, r, g, b, a, randomData).put(DECOMPOSABLE); }
    public static OreDictMaterial lqudaciddcmp (int id, String name, long r, long g, long b, long a, Object... randomData) { return lquddcmp(id, name, r, g, b, a, randomData).put(ACID); }
    public static OreDictMaterial gasdcmp(int id, String name, long r, long g, long b, long a, Object... randomData) { return gas(id, name, r, g, b, a, randomData).put(DECOMPOSABLE); }
    public static OreDictMaterial lqudexpl(int id, String name, long r, long g, long b, long a, Object... randomData) {return liquid(id, name, r, g, b, a, randomData).put(FLAMMABLE, EXPLOSIVE);}
    public static OreDictMaterial lqudflam(int id, String name, long r, long g, long b, long a, Object... randomData) {return liquid(id, name, r, g, b, a, randomData).put(FLAMMABLE);}
    public static OreDictMaterial machine(int id, String nameOreDict, TextureSet[] aSets, long r, long g, long b, long a, Object... randomData) { return create(id, nameOreDict, r, g, b, a, randomData).setTextures(aSets).put(DECOMPOSABLE, G_INGOT_MACHINE, SMITHABLE, MELTING, EXTRUDER); }
    public static OreDictMaterial alloy(int id, String nameOreDict, TextureSet[] aSets, long r, long g, long b, Object... randomData) { return create(id, nameOreDict, r, g, b, 255, randomData).setTextures(aSets).put(DECOMPOSABLE, ALLOY, G_DUST, INGOTS, MELTING, EXTRUDER); }
    public static OreDictMaterial alloymachine(int id, String nameOreDict, TextureSet[] aSets, long r, long g, long b, Object... randomData) { return machine(id, nameOreDict, aSets, r, g, b, 255, randomData).put(ALLOY); }
    public static OreDictMaterial plastic(int id, String nameOreDict, TextureSet[] aSets, long r, long g, long b, long a, Object... randomData) { return create(id, nameOreDict, r, g, b, a, randomData).setTextures(aSets).put(G_INGOT_MACHINE, MELTING, EXTRUDER, EXTRUDER_SIMPLE, MORTAR, FURNACE, POLYMER).addReRegistrationToThis(MT.Plastic); }

    public static OreDictMaterial semiconductor(int id, String nameOreDict, long r, long g, long b, boolean genBoules) {
        return semiconductor(id, nameOreDict, r, g, b, true, true, genBoules);
    }

    public static OreDictMaterial semiconductor(int id, String nameOreDict, long r, long g, long b, boolean genDusts, boolean genIngots, boolean genBoules, Object... randomData) {
        OreDictMaterial mat = create(id, nameOreDict, r, g, b, 255, randomData);
        mat.setTextures(SET_METALLIC).put(DECOMPOSABLE);
        if (genDusts) mat.put(DUSTS);
        if (genIngots) mat.put(INGOTS);
        if (genBoules) {
            OP.bouleGt.forceItemGeneration(mat);
            OP.plateGem.forceItemGeneration(mat);
            OP.plateGemTiny.forceItemGeneration(mat);
        }
        return mat;
    }

    public static OreDictMaterial dopedSemiconductor(int id, String nameOreDict, OreDictMaterial mainMaterial, boolean genItems) {
        return dopedSemiconductor(id, nameOreDict, mainMaterial, genItems, genItems);
    }

    public static OreDictMaterial dopedSemiconductor(int id, String nameOreDict, OreDictMaterial mainMaterial, boolean genDusts, boolean genBoules, Object... randomData) {
        return semiconductor(id, nameOreDict, 0, 0, 0, genDusts, false, genBoules, randomData)
            .setMcfg(0, mainMaterial, U)
            .setAllToTheOutputOf(mainMaterial)
            .steal(mainMaterial)
            .stealLooks(mainMaterial);
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

    public static OreDictMaterial simpleSolution(int id, OreDictMaterial solute, long waterUnits , Object... randomData) {
        return simpleSolution(id, solute.mNameLocal + " Solution",
                solute.fRGBaSolid[0], solute.fRGBaSolid[1], solute.fRGBaSolid[2], solute.fRGBaSolid[3],
                solute, waterUnits, SIMPLE_SOLUTION, randomData
        );
    }

    static OreDictMaterial unused(String name) {return create(-1, name).put(UNUSED_MATERIAL, DONT_SHOW_THIS_COMPONENT);}

    public static OreDictMaterial registerLiquid(OreDictMaterial mat) {
        FL.createLiquid(mat);
        return mat;
    }

    public static OreDictMaterial registerLiquid(OreDictMaterial mat, int temperature, long amountPerUnit) {
        FL.create(mat.mNameInternal.toLowerCase(), mat.mTextureSetsBlock.get(IconsGT.INDEX_BLOCK_MOLTEN), mat.mNameLocal, mat, mat.mRGBaLiquid, STATE_LIQUID, amountPerUnit, temperature, null, null, 0);
        return mat;
    }

    public static OreDictMaterial registerLiquid(OreDictMaterial mat, int temperature) {
        return registerLiquid(mat, temperature, 1000);
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
    CaO = MT.Quicklime.put("CalciumOxide"),
    NH4 = create(16001, "Ammonium", 0, 100, 255, 255, CATION)
            .setMcfg(1, MT.N, U, MT.H, 4*U),
    AmmoniumHexachloroplatinate = dustdcmp(16002, "Ammonium Hexachloroplatinate", SET_FINE, 255, 220, 10, 255)
            .setMcfg(0, NH4, U*2, MT.Pt, U, MT.Cl, U*6)
            .heat(653)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "PtCl" + NUM_SUB[6]),
    PdCl2 = dustdcmp(16003, "Palladium Chloride", SET_FINE, 90, 70, 50, 255)
            .uumMcfg(0, MT.Pd, U, MT.Cl, U*2)
            .heat(952),
    PtPdLeachingSolution = registerLiquid(lqudaciddcmp(16004, "Platinum Palladium Leaching Solution", 255, 100, 70, 255)
            .uumMcfg(0, MT.ChloroplatinicAcid, 5*9*U, PdCl2, 2*3*U, MT.H2O, U*3*16)
            .heat(MT.H2O)),
    PdCl2Solution = registerLiquid(lqudaciddcmp(16005, "Palladium Chloride Solution", 255, 180, 90, 255)
            .uumMcfg(0, PdCl2, 3*U, MT.H2O, U*3*8, MT.HCl, U*2*4)
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
    RuO4 = dustdcmp(16012, "Ruthenium Tetroxide", SET_CUBE_SHINY, 25, 40, 80, 255)
            .uumMcfg(0, MT.Ru, U, MT.O, U*4)
            .heat(298, 313),
    RuOsO4 = registerGas(gasdcmp(16013, "Ruthenium Osmium Tetroxide", 100, 140, 180, 255)
            .uumMcfg(0, RuO4, U, OsO4, U)
            .heat(305, 358)),
    H2RuCl6 = registerLiquid(lqudaciddcmp(16014, "Hexachlororuthenic Acid", 255, 150, 90, 255)
            .uumMcfg(0, MT.H, U*2, MT.Ru, U, MT.Cl, U*6, MT.H2O, U*6)
            .heat( 200,  400)),
    NH4_2_RuCl6 = dustdcmp(16015,  "Ammonium Hexachlororuthenate", SET_FINE, 255, 120, 10, 255)
            .setMcfg(0, NH4, U*2, MT.Ru, U, MT.Cl, U*6)
            .tooltip("(NH" + NUM_SUB[4] + ")" + NUM_SUB[2] + "RuCl" + NUM_SUB[6]),
    IrRhOxide = dustdcmp(16016,  "Iridium-Rhodium Oxide Mixture", SET_FINE, 200, 200, 200, 255)
            .uumMcfg(0, MT.Ir, U, MT.Rh, U, MT.O, U*4),
    NH4Cl = dustdcmp(16017, "Ammonium Chloride", SET_CUBE, 250, 250, 250, 255)
            .setMcfg(1, MT.N, U, MT.H, 4*U, MT.Cl, U)
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
    PGMResidue = oredustdcmp(16021, "Platinum Group Leaching Residue", SET_SHINY, 160, 170, 200, 255)
            .uumMcfg(0, MT.Ru, U, MT.Rh, U, MT.Os, U, MT.Ir, U)
            .heat(2900),

    // Oil industry
    LPG = registerLiquid(lqudflam(16022, "LPG", 216, 150, 150, 100)
            .heat(110, 252)
            .setLocal("Liquefied Petroleum Gas")),
    Naphtha = registerLiquid(lqudexpl( 16023, "Naphtha", 255, 227, 0, 255)
            .heat(C-60, C+90)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    NaphthaLowSulfur = registerLiquid(lqudexpl( 16024, "Low-Sulfur Naphtha", 0, 0, 0, 255)
            .stealLooks(Naphtha)
            .heat(Naphtha)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    JetFuel = registerLiquid(lqudflam(16025, "Jet Fuel", 150, 150, 255, 255)
            .heat(MT.Kerosine)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)),
    LAGO = registerLiquid(lqudflam(16026, "LAGO", 0, 0, 0, 0)
            .stealLooks(MT.Diesel)
            .heat(MT.Diesel)
            .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1)
            .setLocal("Gas Oil")),
    ResidueOil = registerLiquid(lqudflam(16028, "Atmospheric Residue Oil", 30, 20, 10, 255)
            .heat(MT.Oil)),
    VDUFeed = registerLiquid(lqudflam(16029, "Depressurized Residue Oil", 150, 140, 130, 200)
            .heat(100, 300)),
    Ethane = registerGas(gasdcmp( 16030, "Ethane", 255, 0, 100, 25)
            .uumMcfg(1, MT.C, U*2, MT.H, U*6)
            .heat(90, 185)
            .put(FLAMMABLE)),
    Benzene = registerLiquid(lqudflam( 16031, "Benzene", 150, 150, 100, 255)
            .uumMcfg(1, MT.C, U*6, MT.H, U*6)
            .heat(278, 353)),
    Toluene = registerLiquid(lqudflam( 16032, "Toluene", 150, 150, 150, 255)
            .uumMcfg(1, MT.C, U*7, MT.H, U*8)
            .heat(178, 384)),
    Isoprene = registerLiquid(lqudflam( 16033, "Isoprene", 200, 200, 200, 255)
            .uumMcfg(1, MT.C, U*5, MT.H, U*8)
            .heat(129, 307)),
    Cumene = registerLiquid(lqudflam( 16034, "Cumene", 200, 150, 100, 255)
            .uumMcfg(1, MT.C, U*9, MT.H, U*12)
            .heat(177, 425)),
    Phenol = dustdcmp( 16035, "Phenol", SET_CUBE_SHINY, 200, 150, 100, 255, INGOTS)
            .uumMcfg(1, MT.C, U*6, MT.H, U*6, MT.O, U)
            .heat(314, 455),
    Acetone = registerLiquid(lqudflam( 16036, "Acetone", 200, 150, 100, 255)
            .uumMcfg(1, MT.C, U*3, MT.H, U*6, MT.O, U)
            .heat(179, 329)),
    BPA = dustdcmp( 16037, "Bisphenol A", SET_CUBE_SHINY, 200, 200, 200, 255)
            .uumMcfg(1, MT.C, U*15, MT.H, U*16, MT.O, U*2)
            .heat(428, 524),
    AllylChloride = registerLiquid(lqudflam( 16038, "Allyl Chloride", 100, 200, 100, 255)
            .uumMcfg(1, MT.C, U*3, MT.H, U*5, MT.Cl, U)
            .heat(138, 318)),
    Dichloropropanol = registerLiquid(lqudflam( 16039, "Dichloropropanol", 100, 200, 100, 255)
            .uumMcfg(1, MT.C, U*3, MT.H, U*6, MT.Cl, U*2, MT.O, U)
            .heat(138, 318)),
    ECH = registerLiquid(lqudaciddcmp( 16040, "Epichlorohydrin", 100, 255, 100, 255)
            .uumMcfg(1, MT.C, U*3, MT.H, U*5, MT.Cl, U, MT.O, U)
            .heat(248, 391)
            .put(FLAMMABLE)),
    Epoxy = plastic( 16041, "Epoxy", SET_DULL, 9, 86, 0, 255)
            .heat(400),
    VinylChloride = registerGas(gasdcmp(16042, "Vinyl Chloride", 150, 255, 150, 50)
            .uumMcfg(1, MT.C, 2*U, MT.H, U*3, MT.Cl, U)
            .heat(119, 260)
            .put(FLAMMABLE)),
    Phosgene = registerGas(gasdcmp(16043, "Phosgene", 255, 255, 255, 50, "Carbonyl Dichloride")
            .setMcfg(1, MT.C, U, MT.O, U, MT.Cl, 2*U)
            .heat(155, 281)),
    CHCl3 = registerLiquid(lquddcmp( 16044, "Chloroform", 150, 255, 200, 255)
            .uumMcfg(1, MT.C, U, MT.H, U, MT.Cl, U*3)
            .heat(210, 334)),
    CHClF2 = registerGas(gasdcmp( 16045, "Chlorodifluoromethane", 150, 200, 255, 255)
            .uumMcfg(1, MT.C, U, MT.H, U, MT.Cl, U, MT.F, U*2)
            .heat(97, 233)),
    C2F4 = registerGas(gasdcmp( 16046, "Tetrafluoroethylene", 150, 255, 255, 255)
            .uumMcfg(1, MT.C, U*2, MT.F, U*4)
            .heat(131, 197)),
    DnqNovolacResist = registerLiquid(lquddcmp(16047, "Photoresist", 84, 145, 84, 200)
            .heat(MT.H2O)),
    NaFSolution = simpleSolution(16048, MT.NaF, 3),
    CrackerGas = registerGas(gasdcmp(16049, "Olefins", 150, 0, 150, 255)
            .heat(MT.Propylene)
            .put(FLAMMABLE)
            .setLocal("Cracker Gas")),
    EthyleneDichloride = registerLiquid(lqudflam( 16050, "Ethylene Dichloride", 100, 255, 100, 255)
            .uumMcfg(1, MT.C, 2*U, MT.H, U*4, MT.Cl, U*2)
            .heat( 238,  357)),
    H3PO4 = registerLiquid(lqudaciddcmp(16051, "Phosphoric Acid", 150, 200, 0, 255)
            .uumMcfg(0, MT.H, 3*U, MT.P, U, MT.O, 4*U)
            .heat(290, C + 212)),
    TNT = dustdcmp(16052, "Trinitrotoluene", SET_DULL, 225, 198, 153, 255)
            .uumMcfg(3, MT.C, 7*U, MT.H, 5*U, MT.N, 3*U, MT.O, 6*U)
            .put(FLAMMABLE, EXPLOSIVE, MD.MC)
            .heat(354, 513),
    LNG = registerLiquid(liquid(16053, "LNG", 250, 250, 250, 200)
            .put(FLAMMABLE, EXPLOSIVE)
            .heat(91, 112)
            .setLocal("Liquefied Natural Gas")),
    Synoil = registerLiquid(lqudflam( 16054, "Synoil", 210, 210, 0, 255)
            .heat(100, 400)
            .setLocal("Fischer-Tropsch Syncrude")),

    // Metallurgy
    Slag = create( 16055, "Slag", 255, 240, 200, 255)
            .setMcfg( 0, CaO, U, MT.SiO2, 3*U)
            .setTextures(SET_FLINT)
            .put(INGOTS, MORTAR, BRITTLE, GEMS)
            .heat(1720, 3000)
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
            .heat(1161, 1750),
    ZnO = oredustdcmp(16059, "Zinc Oxide", SET_DULL, 255, 180, 100, 255)
            .uumMcfg(1, MT.Zn, U, MT.O, U)
            .heat(2247, 2630),
    FeCr2 = alloymachine(16060, "Ferrochrome", SET_SHINY, 160, 150, 150)
            .uumAloy(0, MT.Fe, U, MT.Cr, 2*U)
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
    CoO = dustdcmp(16065, "Cobalt Monoxide", SET_DULL, 50, 50, 100, 255)
            .uumMcfg(0, MT.Co, U, MT.O, U)
            .heat(2206),
    //TODO 16066 free
    Na2CrO4 = dustdcmp(16067, "Sodium Chromate", SET_CUBE, 255, 255, 0, 255)
            .uumMcfg(0, MT.Na, 2*U, MT.Cr, U, MT.O, 4*U)
            .heat(1065),
    Na2Cr2O7 = dustdcmp(16068, "Sodium Dichromate", SET_DULL, 255, 125, 0, 255)
            .uumMcfg(0, MT.Na, 2*U, MT.Cr, 2*U, MT.O, 7*U)
            .heat(629, 673),
    Na2CrO4Solution = simpleSolution(16069, "Sodium Chromate Solution", 255, 255, 0, 255, Na2CrO4, 3),
    DichromateSoda = registerLiquid(lquddcmp(16070, "Sodium Dichromate-Bicarbonate Solution", 255, 125, 0, 255)
            .uumMcfg(0, Na2Cr2O7, 11*U, MT.NaHCO3, 12*U, MT.H2O, 9*U)
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
            .uumMcfg(0, MT.Fe, U, MT.O, U)
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
            .uumMcfg(1, MT.Se, U, MT.O, 2*U)
            .heat(613, 623),

    // Refractory Metals
    ZrCl4 = registerLiquid(lqudaciddcmp(16088, "Zirconium Tetrachloride", 255, 0, 255, 200)
            .uumMcfg(0, MT.Zr, U, MT.Cl, 4*U)
            .heat(604, 604)),
    HfCl4 = registerLiquid(lqudaciddcmp(16089, "Hafnium Tetrachloride", 200, 0, 255, 200)
            .uumMcfg(0, MT.Hf, U, MT.Cl, 4*U)
            .heat(705, 705)),
    ZrHfCl4 = registerLiquid(lqudaciddcmp(16090, "Zirconium-Hafnium Tetrachloride", 228, 0, 255, 200)
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
            .setSmelting(CaO, 2*U5),
    CaCl2Solution = simpleSolution(16103, "Calcium Chloride Solution", 235, 235, 255, 200, MT.CaCl2, 3),
    FeCl2Solution = simpleSolution(16104, "Ferrous Chloride Solution", 235, 255, 235, 200, MT.FeCl2, 3),
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
            .setMcfg(0, MT.PigIron, 8*U, FerrousSlag, 7*U)
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
    MnO = dustdcmp(16112, "Manganese(II) Oxide", SET_DULL, 82, 122, 82, 255)
            .uumMcfg(1, MT.Mn, U, MT.O, U)
            .heat(2218),
    Cementite = alloy(16113, "Cementite", SET_METALLIC, 50, 50, 0, "Iron Carbide")
            .uumMcfg(3, MT.Fe, 3*U, MT.C, U)
            .heat(1500),
    MeteoricCementite = alloy(16114, "Meteoric Cementite", SET_METALLIC, 50, 50, 0, 255, "Meteoric Iron Carbide")
            .setMcfg(3, MT.MeteoricIron, 3*U, MT.C, U)
            .heat(1500),
    ImpureCementite = dustdcmp(16115, "Slag-rich Cementite", SET_METALLIC, 100, 50, 0, 255)
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
            .setMcfg(0, CaO, U, MgO, U)
            .heat((CaO.mMeltingPoint + MgO.mMeltingPoint) / 2)
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
            .qual(3, 12.0, 6144, 5),
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
            .uumMcfg(0, MT.Ca, U, MT.Al2O3, 5*U, MT.SiO2, 12*U, MT.C, U)
            .heat(2100),
    Firebrick = create(16130, "Fire Brick", 255, 235, 200, 255, MORTAR, BRITTLE)
            .setMcfg(0, RefractoryCeramic, U)
            .setAllToTheOutputOf(RefractoryCeramic)
            .heat(RefractoryCeramic)
            .setTextures(SET_ROUGH)
            .put(INGOTS),
    Fireclay = oredustdcmp(16131, "Fireclay", SET_ROUGH, 255, 235, 200, 255, MORTAR)
            .uumMcfg(0, MT.Kaolinite, 2*U, MT.Graphite, U)
            .heat(RefractoryCeramic)
            .setSmelting(RefractoryCeramic, U),

    // Electronics
    Methanol = registerLiquid(lquddcmp(16132, "Methanol", 255, 240, 240, 200)
            .setMcfg(1, MT.C, U, MT.H, 4*U, MT.O, U)
            .heat(175, 338)
            .put(FLAMMABLE)),
    Formaldehyde = registerGas(gasdcmp(16133, "Formaldehyde", 200, 255, 255, 100, "Methanal")
            .setMcfg(1, MT.C, U, MT.H, 2*U, MT.O, U)
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
            .uumMcfg(1, MT.Si, U, MT.H, 4*U)
            .heat(88, 161)),
    GeH4 = registerGas(gasdcmp(16138, "Germane", 200, 200, 220, 100)
            .uumMcfg(1, MT.Ge, U, MT.H, 4*U)
            .heat(108, 185)),
    AsH3 = registerGas(gasdcmp(16139, "Arsine", 255, 200, 255, 100, "Arsane")
            .uumMcfg(1, MT.As, U, MT.H, 3*U)
            .heat(162, 211)),
    PH3 = registerGas(gasdcmp(16140, "Phosphine", 255, 220, 150, 100, "Phosphane")
            .uumMcfg(1, MT.P, U, MT.H, 3*U)
            .heat(140, 185)),
    Mg2Si = alloy(16141, "Magnesium Silicide", SET_METALLIC, 102, 0, 102)
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
            .setSmelting(ZnO, 2*U5),
    GaAs = semiconductor(16147, "Gallium Arsenide", 96, 96, 120, true)
            .uumMcfg(1, MT.Ga, U, MT.As, U)
            .heat(1511),
    SiGe = semiconductor(16148, "Silicon-Germanium", 136, 136, 146, true)
            .uumMcfg(0, MT.Si, U, MT.Ge, U)
            .heat(MT.Ge),
    LiH = dustdcmp(16149, "Lithium Hydride", SET_QUARTZ, 0, 153, 153, 255)
            .setMcfg(0, MT.Li, U, MT.H, U)
            .heat(961, 1220)
            .put(GEMS)
            .setSmelting(MT.Li, U2),
    LiF = dustdcmp(16150, "Lithium Fluoride", SET_DULL, 235, 255, 200, 255)
            .setMcfg(0, MT.Li, U, MT.F, U)
            .heat(1118, 1949)
            .put(ELECTROLYSER),
    BF3 = registerGas(gasdcmp(16151, "Boron Trifluoride", 255, 250, 180, 50, "Trifluoroborane")
            .setMcfg(1, MT.B, U, MT.F, 3*U)
            .heat(146, 173)),
    B2O3 = dustdcmp(16152, "Boron Trioxide", SET_DULL, 255, 230, 230, 255, "Diboron Trioxide")
            .setMcfg(0, MT.B, 2*U, MT.O, 3*U)
            .heat(723, 2130),
    B2H6 = registerGas(gasdcmp(16153, "Diborane", 255, 255, 255, 100)
            .setMcfg(1, MT.B, 2*U, MT.H, 6*U)
            .heat(108, 181)),
    NaH = dustdcmp(16154, "Sodium Hydride", SET_DULL, 150, 150, 150, 255)
            .uumMcfg(0, MT.Na, U, MT.H, U)
            .heat(911)
            .setSmelting(MT.Na, U2),
    InGa = alloy(16155, "Indium-Gallium", SET_METALLIC, 142, 110, 192, 255)
            .uumAloy(0, MT.In, U, MT.Ga, U),
    Na2O = dustdcmp(16156, "Sodium Oxide", SET_DULL, 255, 255, 200, 255)
            .setMcfg(0, MT.Na, 2*U, MT.O, U)
            .heat(1405, 2220),
    CrO3 = dustdcmp(16157, "Chromium Trioxide", SET_ROUGH, 105, 31, 42, 255, "Chromium(VI) Oxide")
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
    BayerLiquor = registerLiquid(lquddcmp(16162, "Bayer Liquor", 200, 50, 0, 255)
            .heat(MT.H2O)),
    GaAmalgam = registerLiquid(lquddcmp(16163, "Gallium Amalgam", 200, 0, 180, 255)
            .setMcfg(0, MT.Hg, 9*U, MT.Ga, U)
            .heat(MT.Hg)),
    NH4NO3 = dustdcmp(16164, "Ammonium Nitrate", SET_SHARDS, 255, 255, 255, 255)
            .setMcfg(5, NH4, U, MT.N, U, MT.O, 3*U)
            .tooltip("NH" + NUM_SUB[4] + "NO" + NUM_SUB[3])
            .heat(443, 483)
            .put(FLAMMABLE, EXPLOSIVE),
    ANFO = dustdcmp(16165, "ANFO", SET_ROUGH, 255, 200, 200, 255)
            .setMcfg(0, NH4NO3, 10*U, MT.Fuel, U)
            .heat(NH4NO3)
            .put(FLAMMABLE, EXPLOSIVE),
    PDopedSi = dopedSemiconductor(16166, "P-Doped Silicon", MT.Si, false, true),
    NDopedSi = dopedSemiconductor(16167, "N-Doped Silicon", MT.Si, false, true),
    PDopedGe = dopedSemiconductor(16168, "P-Doped Germanium", MT.Ge, false, true),
    NDopedGe = dopedSemiconductor(16169, "N-Doped Germanium", MT.Ge, false, true),
    PDopedSiGe = dopedSemiconductor(16170, "P-Doped Silicon-Germanium", SiGe, false, true),
    NDopedSiGe = dopedSemiconductor(16171, "N-Doped Silicon-Germanium", SiGe, false, true),
    Naphthalene = registerLiquid(lquddcmp(16172, "Naphthalene", 255, 255, 255, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 8*U)
            .heat(351, 424)
            .put(FLAMMABLE)),
    Anthracene = registerLiquid(lquddcmp(16173, "Anthracene", 225, 255, 150, 255)
            .setMcfg(1, MT.C, 14*U, MT.H, 10*U)
            .heat(489, 614)),
    Anthraquinone = dustdcmp(16174, "Anthraquinone", SET_SHINY, 225, 255, 0, 255)
            .setMcfg(1, MT.C, 14*U, MT.H, 8*U, MT.O, 2*U)
            .heat(558, 650),
    AnthraquinoneDisulfonicAcid = dustdcmp(16175, "Anthraquinone Disulfonic Acid", SET_DULL, 255, 200, 0, 255)
            .setMcfg(1, MT.C, 14*U, MT.H, 8*U, MT.S, 2*U, MT.O, 8*U)
            .heat(211+C, 498+C),
    Diaminoanthraquinone = dustdcmp(16176, "Diaminoanthraquinone", SET_DULL, 150, 0, 0, 255)
            .setMcfg(1, MT.C, 14*U, MT.H, 10*U, MT.N, 2*U, MT.O, 2*U)
            .heat(Anthraquinone),
    Chlorotoluene = dustdcmp(16177, "Monochlorotoluene", SET_DULL, 255, 255, 255, 255)
            .setMcfg(1, MT.C, 6*U, MT.H, 8*U, MT.Cl, U)
            .heat(280, 435),
    Nitrobenzene = registerLiquid(lquddcmp(16178, "Nitrobenzene", 225, 225, 0, 200)
            .setMcfg(1, MT.C, 6*U, MT.H, 5*U, MT.N, U, MT.O, 2*U)
            .heat(279, 484)),
    Aniline = registerLiquid(lqudaciddcmp(16179, "Aniline", 255, 255, 255, 200)
            .setMcfg(1, MT.C, 6*U, MT.H, 7*U, MT.N, U)
            .heat(267, 457)),
    BenzenediazoniumChloride = dustdcmp(16180, "Benzenediazonium Chloride", SET_CUBE, 255, 255, 255, 255, "Phenyldiazonium Chloride")
            .setMcfg(1, MT.C, 6*U, MT.H, 5*U, MT.N, 2*U, MT.Cl, U)
            .heat(191+C, 229+C),
    Nitronaphthalene = dustdcmp(16181, "Nitronaphthalene", SET_DULL, 255, 255, 102, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 7*U, MT.N, U, MT.O, 2*U)
            .heat(325, 400)
            .put(FLAMMABLE),
    Aminonaphthalene = dustdcmp(16182, "Aminonaphthalene", SET_DULL, 255, 255, 102, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 9*U, MT.N, U)
            .heat(320, 400)
            .put(FLAMMABLE),
    NaphthaleneSulfonicAcid = dustdcmp(16183, "Naphthalene Sulfonic Acid", SET_DULL, 255, 255, 255, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 8*U, MT.S, U, MT.O, 3*U)
            .heat(412),
    Naphthol = dustdcmp(16184, "Naphthol", SET_DULL, 255, 255, 255, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 8*U, MT.O, U)
            .heat(368, 552),
    DNP = dustdcmp(16185, "DNP", SET_DULL, 255, 255, 210, 255, "Dinitrophenol")
            .setMcfg(1, MT.C, 6*U, MT.H, 4*U, MT.N, 2*U, MT.O, 5*U)
            .heat(381)
            .setLocal("2,4-Dinitrophenol")
            .put(EXPLOSIVE),
    Nitronaphthol = dustdcmp(16186, "Nitronaphthol", SET_DULL, 249, 255, 69, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 7*U, MT.N, U, MT.O, 3*U)
            .heat(380),
    Aminonaphthol = dustdcmp(16187, "Aminonaphthol", SET_DULL, 210, 255, 210, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 9*U, MT.N, U, MT.O, U)
            .heat(375),
    DNQ = dustdcmp(16188, "Diazonaphthoquinone", SET_FINE, 150, 50, 180, 250, "DNQ")
            .uumMcfg(1, MT.C, 10*U, MT.H, 6*U, MT.N, 2*U, MT.O, U)
            .heat(400),
    NPhenylGlycine = dustdcmp(16189, "N-Phenylglycine", SET_CUBE, 255, 255, 255, 255, "Anilinoacetic Acid")
            .setMcfg(1, MT.C, 8*U, MT.H, 9*U, MT.N, U, MT.O, 2*U)
            .heat(400),
    QuinizarineGreen = dustdcmp(16190, "Quinizarine Green", SET_DULL, 60, 134, 57, 255)
            .uumMcfg(1, MT.C, 28*U, MT.H, 22*U, MT.N, 2*U, MT.O, 2*U)
            .heat(493),
    AlizarinRed = dustdcmp(16191, "Alizarin Red", SET_DULL, 186, 24, 47, 255)
            .uumMcfg(1, MT.C, 14*U, MT.H, 8*U, MT.O, 4*U)
            .heat(562, 703),
    SolventYellow = dustdcmp(16192, "Solvent Yellow", SET_DULL, 200, 200, 10, 255)
            .heat(124+C, 172+C),
    OrganolBrown = dustdcmp(16193, "Organol Brown", SET_DULL, 153, 51, 0, 255)
            .uumMcfg(1, MT.C, 16*U, MT.H, 12*U, MT.N, 2*U, MT.O, U)
            .heat(124+C, 172+C),
    SulfurBlack = dustdcmp(16194, "Sulfur Black", SET_DULL, 25, 25, 25, 255)
            .uumMcfg(1, MT.C, 24*U, MT.H, 16*U, MT.N, 6*U, MT.O, 8*U, MT.S, 8*U)
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
    K2SO4Solution = simpleSolution(16201, "Potassium Sulfate Solution", 255, 255, 255, 200, MT.K2SO4, 3),
    Na2SO4Solution = simpleSolution(16202, "Sodium Sulfate Solution", 190, 190, 140, 255, MT.Na2SO4, 3),
    ArF = registerGas(gasdcmp(16203, "Argon-Fluorine", 64, 255, 0, 200)
            .setMcfg(0, MT.Ar, U, MT.F, U)
            .heat(MT.Ar)),
    KrF = registerGas(gasdcmp(16204, "Krypton-Fluorine", 192, 255, 128, 200)
            .setMcfg(0, MT.Kr, U, MT.F, U)
            .heat(MT.Kr)),
    CeO2 = dustdcmp(16205, "Cerium(IV) Oxide", SET_DULL, 255, 255, 204, 255)
            .setMcfg(1, MT.Ce, U, MT.O, 2*U)
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
            .setMcfg(1, MT.C, U, MT.Cl, 4*U)
            .heat(250, 350)),
    CF4 = registerGas(gasdcmp(16211, "Tetrafluoromethane", 200, 255, 255, 200)
            .setMcfg(1, MT.C, U, MT.F, 4*U)
            .heat(89, 145)),
    Y2O3 = dustdcmp(16212, "Yttria", SET_SHINY, 255, 255, 255, 255, GEMS)
            .setMcfg(2, MT.Y, 2*U, MT.O, 3*U)
            .heat(2698, 4570),
    YAlO3 = machine(16213, "Yttria-Alumina", SET_DULL, 200, 255, 255, 255, PIPES)
            .setMcfg(0, MT.Al2O3, 5*U, Y2O3, 2*U)
            .heat(Y2O3),
    SiF4 = registerGas(gasdcmp(16214, "Tetrafluorosilane", 50, 50, 100, 200)
            .uumMcfg(1, MT.Si, U, MT.F, 4*U)
            .heat(178, 182)),
    Alusil = alloymachine(16215, "Alusil", SET_COPPER, 149, 168, 173, PIPES, "A390")
            .uumAloy(0, MT.Al, 11*U, MT.Si, 3*U, MT.Cu, U, MT.Mg, U),
    H3AsO3 = registerLiquid(lqudaciddcmp(16216, "Arsenous Acid", 150, 200, 175, 255)
            .setMcfg(0, MT.H, 3*U, MT.As, U, MT.O, 3*U)
            .heat(MT.H2O)),
    PiranhaEtch = registerLiquid(lqudaciddcmp(16217, "Piranha Solution", 245, 126, 66, 255)
            .uumMcfg(0, MT.H, 4*U, MT.S, U, MT.O, 6*U)
            .heat(MT.H2O)),
    NF3 = registerGas(gasdcmp(16218, "Nitrogen Trifluoride", 0, 240, 255, 200)
            .setMcfg(1, MT.N, U, MT.F, 3*U)
            .heat(66, 144)),
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
            .setMcfg(0, MT.N, 4*U, MT.H, 16*U, MT.Ru, 2*U, MT.Cl, 8*U, MT.O, 2*U)
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
    Na3PO4Solution = simpleSolution(16235, "Sodium Phosphate Solution", 200, 200, 255, 200, Na3PO4, 6),
    FeCl3Solution = registerLiquid(lquddcmp(16236, "Ferric Chloride Solution", 180, 180, 120, 200)
            .setMcfg(0, MT.FeCl3, 8*U, MT.H2O, 9*U)
            .heat(MT.H2O)),
    NaHSO4Solution = simpleSolution(16237, "Sodium Bisulfate Solution", 240, 240, 255, 200, MT.NaHSO4, 3),
    N2O = registerGas(gasdcmp(16238, "Nitrous Oxide", 255, 255, 255, 200)
            .uumMcfg(0, MT.N, 2*U, MT.O, U)
            .heat(182, 184)),
    Isopropanol = registerLiquid(lqudflam(16239, "Isopropanol", 255, 255, 255, 200, "2-propanol", "Isopropyl Alcohol")
            .uumMcfg(1, MT.C, 3*U, MT.H, 8*U, MT.O, U)
            .heat(184, 356)),
    SolderingPaste = registerLiquid(lquddcmp(16240, "Solder Paste", 255, 180, 0, 255)
            .heat(Isopropanol)),
    SF6 = registerGas(gasdcmp(16241, "Sulfur Hexafluoride", 240, 255, 200, 150)
            .uumMcfg(1, MT.S, U, MT.F, 6*U)
            .heat(209, 222)),
    AlPO4Solution = simpleSolution(16242, "Aluminium Phosphate Solution", 200, 225, 255, 200, AlPO4, 6),
    CoPtCr = alloymachine(16243, "Cobalt-Platinum-Chromium", SET_COPPER, 145, 163, 243)
            .uumAloy(0, MT.Co, 5*U, MT.Pt, U, MT.Cr, 3*U)
            .heat(1880, 3300),
    Hydroxyapatite = oredustdcmp(16244, "Hydroxyapatite", SET_DIAMOND, 150, 150, 80, 255)
            .uumMcfg(0, MT.Ca, 5*U, MT.PO4, 3*5*U, MT.O, U, MT.H, U)
            .heat(MT.Apatite)
            .setOreMultiplier(4),
    LiquidCrystal5CB = create(16245, "Crystal5CBLiquid", 0, 0, 0, 255, "4-Cyano-4'-pentylbiphenyl")
            .setMcfg(1, MT.C, 18*U, MT.H, 19*U, MT.N, U)
            .setLocal("5CB liquid crystal"),
    Biphenyl = dustdcmp(16246, "Biphenyl", SET_CUBE_SHINY, 255, 255, 200, 255)
            .setMcfg(1, MT.C, 12*U, MT.H, 10*U)
            .heat(342, 528),
    Bromo4pentylbiphenyl = dustdcmp(16247, "Bromo-4-pentylbiphenyl", SET_DULL, 200, 100, 0, 255)
            .setMcfg(1, MT.C, 17*U, MT.H, 19*U, MT.Br, U)
            .heat(Biphenyl)
            .setLocal("4-Bromo-4'-pentylbiphenyl"),
    AlCl3 = dustdcmp(16248, "Aluminium Chloride", SET_CUBE_SHINY, 255, 255, 255, 250)
            .uumMcfg(1, MT.Al, U, MT.Cl, 3*U)
            .heat(453, 453)
            .put(ELECTROLYSER),
    Pentanol = registerLiquid(lquddcmp(16249, "Pentanol", 255, 0, 150, 200)
            .setMcfg(1, MT.C, 5*U, MT.H, 12*U, MT.O, U)
            .setLocal("1-Pentanol")
            .heat(195, 411)),
    Chloropentane = registerLiquid(lquddcmp(16250, "Chloropentane", 255, 255, 100, 200)
            .setMcfg(1, MT.C, 5*U, MT.H, 11*U, MT.Cl, U)
            .heat(174, 381)),
    Butylene = registerGas(gasdcmp(16251, "Butylene", 110, 80, 180, 200)
            .setMcfg(1, MT.C, 4*U, MT.H, 8*U)
            .heat(88, 267)),
    AceticAcid = registerLiquid(lqudaciddcmp(16252, "Acetic Acid", 255, 255, 255, 200)
            .setMcfg(1, MT.C, 2*U, MT.H, 4*U, MT.O, 2*U)
            .heat(289, 391)),
    VinylAcetate = registerLiquid(lquddcmp(16253, "Vinyl Acetate", 255, 255, 255, 200)
            .setMcfg(1, MT.C, 4*U, MT.H, 6*U, MT.O, 2*U)
            .heat(180, 346)),
    PolyvinylAcetate = dustdcmp(16254, "Polyvinyl Acetate", SET_CUBE_SHINY, 250, 250, 250, 255, POLYMER)
            .setMcfg(1, MT.C, 4*U, MT.H, 6*U, MT.O, 2*U)
            .heat(310, 385),
    PVA = plastic(16255, "Polyvinyl Alcohol", SET_GLASS, 128, 128, 128, 150)
            .setMcfg(1, MT.C, 2*U, MT.H, 4*U, MT.O, U)
            .heat(473),
    MethylAcetate = registerLiquid(lquddcmp(16256, "Methyl Acetate", 255, 255, 255, 200)
            .setMcfg(1, MT.C, 3*U, MT.H, 6*U, MT.O, 2*U)
            .heat(175, 330)),
    SilicoTungsticAcid = dustdcmp(16257, "Silicotungstic Acid", SET_CUBE, 255, 255, 200, 255)
            .uumMcfg(0, MT.H, 4*U, MT.Si, U, MT.W, 12*U, MT.O, 40*U, MT.H2O, 13*3*U)
            .heat(326),
    Na4SiO4 = create(16258, "Sodium Orthosilicate", 255, 255, 255, 255)
            .setMcfg(0, MT.Na, 4*U, MT.Si, U, MT.O, 4*U),
    Na4SiO4Solution = solution(16259, "Sodium Orthosilicate Solution", 240, 240, 255, 200, Na4SiO4, 9)
            .tooltip("(Na" + NUM_SUB[4] + "SiO" + NUM_SUB[4] + ")(H" + NUM_SUB[2] + "O)" + NUM_SUB[3]),
    HBr = registerGas(gasdcmp(16260, "Hydrogen Bromide", 150, 50, 0, 150)
            .uumMcfg(0, MT.H, U, MT.Br, U)
            .heat(186, 206)),
    CuCN = dustdcmp(16261, "Copper Cyanide", SET_DULL, 255, 255, 200, 255)
            .uumMcfg(0, MT.Cu, U, MT.C, U, MT.N, U)
            .heat(747),
    CuBr = dustdcmp(16262, "Copper Bromide", SET_DULL, 255, 255, 255, 255)
            .uumMcfg(0, MT.Cu, U, MT.Br, U)
            .heat(765, 1618),
    NaBr = dustdcmp(16263, "Sodium Bromide", SET_CUBE, 255, 255, 255, 255)
            .uumMcfg(0, MT.Na, U, MT.Br, U)
            .heat(1020, 1660),
    NaBrSolution = simpleSolution(16264, "Sodium Bromide Solution", 255, 255, 255, 255, NaBr, 3),
    C2N2 = registerGas(gasdcmp(16265, "Cyanogen", 255, 255, 255, 150)
            .setMcfg(0, MT.C, 2*U, MT.N, 2*U)
            .heat(245, 252)),
    ITO = machine(16266, "Indium Tin Oxide", SET_QUARTZ, 255, 200, 200, 150)
            .uumMcfg(0, MT.In, 4*U, MT.Sn, U, MT.O, 8*U)
            .heat(1800+C),
    In2O3 = dustdcmp(16267, "Indium(III) Oxide", SET_CUBE_SHINY, 240, 255, 0, 255, "Indium Trioxide")
            .uumMcfg(0, MT.In, 2*U, MT.O, 3*U)
            .heat(2180),
    ZnLeachingSolution = registerLiquid(lqudaciddcmp(16268, "Zinc Leaching Solution", 222, 222, 222, 255)
            .setMcfg(0, MT.WhiteVitriol, 8*6*U, CdSO4, 6*U, MT.H2O, 9*3*U)
            .heat(MT.H2O)),
    ZRR = oredustdcmp(16269, "Zinc Refinery Residue", SET_DULL, 235, 153, 51, 255)
            .heat(700),
    Tl2SO4 = dustdcmp(16270, "Thallium Sulfate", SET_CUBE, 220, 220, 0, 255)
            .uumMcfg(0, MT.Tl, 2*U, MT.S, U, MT.O, 4*U)
            .heat(905),
    GeGaInSulfateSolution = registerLiquid(lqudaciddcmp(16271, "Ge-Ga-In Sulfate Solution", 255, 150, 50, 255)
            .setMcfg(0, MT.Ga, U, MT.Ge, U, MT.In, U, MT.S, 5*U, MT.O, 20*U, MT.H2O, 6*3*U)
            .heat(MT.H2O))
            .tooltip("(Ga, In, Ge)ₘ(SO" + NUM_SUB[4] + ")ₙ"),
    Tannin = registerLiquid(lquddcmp(16272, "Tannin", 100, 50, 0, 250)
            .heat(MT.H2O)),
    TannicAcid = dustdcmp(16273, "Tannic Acid", SET_QUARTZ, 220, 130, 0, 255)
            .heat(200+C),
    GeTannate = dustdcmp(16274, "Germanium Tannate", SET_QUARTZ, 150, 100, 50, 255)
            .heat(TannicAcid),
    GeO2 = dustdcmp(16275, "Germanium Dioxide", SET_CUBE, 255, 255, 255, 255)
            .uumMcfg(0, MT.Ge, U, MT.O, 2*U),
    GaInSulfateSolution = registerLiquid(lqudaciddcmp(16276, "Ga-In Sulfuric Acid Solution", 255, 128, 0, 255)
            .setMcfg(0, MT.Ga, U, MT.In, U, MT.S, 3*U, MT.O, 12*U, MT.H2SO4, 14*U, MT.H2O, 18*U))
            .heat(MT.H2SO4),
    InO3H3 = dustdcmp(16277, "Indium Hydroxide", SET_DULL, 210, 200, 255, 255)
            .uumMcfg(0, MT.In, U, MT.O, 3*U, MT.H, 3*U)
            .tooltip("In(OH)" + NUM_SUB[3])
            .heat(423)
            .setSmelting(In2O3, 5*U14),
    GaOHNa2SO4Solution = registerLiquid(lquddcmp(16278, "Gallium Hydroxide - Sodium Sulfate Solution", 190, 190, 140, 255)
            .setMcfg(0, MT.Ga, U, MT.O, 3*U, MT.H, 3*U, MT.Na2SO4, 7*5*U, MT.H2O, 3*10*U)
            .heat(MT.H2O)),
    ZnSlag = dustdcmp(16279, "Zinc Smelting Slag", SET_FLINT, 200, 100, 0, 255, GEMS)
            .heat(Slag),
    CoalAshNonmagResidue = dustdcmp(16280, "Coal ash non-magnetic residue", SET_DULL, 100, 100, 100, 255)
            .heat(MT.DarkAsh),
    CoalAshLeachingSolution = registerLiquid(lquddcmp(16281, "Coal ash leaching solution", 150, 150, 150, 200)
            .heat(MT.H2O)),
    Hastelloy = alloymachine(16282, "Hastelloy", SET_COPPER, 230, 210, 180, PIPES)
            .uumAloy(0, MT.Ni, 5*U, MT.Cr, 2*U, MT.Mo, U, MT.Co, U),
    OxalicAcid = dustdcmp(16283, "Oxalic Acid", SET_CUBE, 220, 235, 255, 200)
            .uumMcfg(0, MT.H, 2*U, MT.C, 2*U, MT.O, 4*U)
            .heat(463),
    GlycolicAcid = dustdcmp(16284, "Glycolic Acid", SET_CUBE, 255, 255, 255, 255)
            .setMcfg(1, MT.C, 2*U, MT.H, 4*U, MT.O, 3*U)
            .heat(348),
    GeOxalate = dustdcmp(16285, "Germanium Oxalate", SET_FINE, 255, 255, 255, 255)
            .setMcfg(0, MT.Ge, U, MT.C, 4*U, MT.O, 8*U)
            .tooltip("Ge(C" + NUM_SUB[2] + "O" + NUM_SUB[4] + ")" + NUM_SUB[2]),
    CuAnodeSludge = dustdcmp(16286, "Copper Anode Sludge", SET_FINE, 61, 50, 40, 255)
            .heat(550+C),
    Y2O2S = create(16288, "Yttrium Oxide Sulfide", 255, 255, 0, 255)
            .uumMcfg(0, MT.Y, 2*U, MT.O, 2*U, MT.S, U),
    RedPhosphor = dustdcmp(16289, "Red Phosphor", SET_RAD, 255, 0, 0, 255)
            .uumMcfg(1, Y2O2S, U, MT.Eu, U9)
            .heat(2500, 4500)
            .tooltip("Y" + NUM_SUB[2] + "O" + NUM_SUB[2] + "S:Eu"),
    BluePhosphor = dustdcmp(16290, "Blue Phosphor", SET_RAD, 0, 0, 255, 255)
            .setMcfg(1, MT.OREMATS.Sphalerite, U, MT.Ag, U9)
            .heat(MT.OREMATS.Sphalerite)
            .tooltip("ZnS:Ag"),
    GreenPhosphor = dustdcmp(16291, "Green Phosphor", SET_RAD, 0, 255, 0, 255)
            .setMcfg(1, MT.OREMATS.Sphalerite, U, MT.Cu, U9)
            .tooltip("ZnS:Cu")
            .heat(MT.OREMATS.Sphalerite),
    YellowPhosphor = dustdcmp(16292, "Yellow Phosphor", SET_RAD, 255, 255, 0, 255)
            .heat(CdS),
    WhitePhosphor = dustdcmp(16293, "White Phosphor", SET_RAD, 255, 255, 255, 255)
            .heat(MT.PhosphorusWhite),
    Eu2O3 = dustdcmp(16294, "Europium Oxide", SET_FINE, 255, 240, 253, 255)
            .setLocal("Europium (III) Oxide")
            .uumMcfg(0, MT.Eu, 2*U, MT.O, 3*U)
            .heat(2620, 4391),
    BaS = dustdcmp(16295, "Barium Sulfide", SET_DULL, 255, 255, 255, 255)
            .uumMcfg(1, MT.Ba, U, MT.S, U)
            .heat(2508),
    BaO = dustdcmp(16296, "Barium Oxide", SET_FINE, 255, 255, 255, 255)
            .uumMcfg(1, MT.Ba, U, MT.O, U)
            .heat(2196, 2270),
    BaCO3 = dustdcmp(16297, "Barium Carbonate", SET_CUBE, 255, 255, 255, 255)
            .uumMcfg(1, MT.Ba, U, MT.C, U, MT.O, 3*U)
            .heat(1360+C)
            .setSmelting(BaO, 2*U5),
    BaNO3 = dustdcmp(16298, "Barium Nitrate", SET_CUBE, 255, 255, 255, 255)
            .uumMcfg(1, MT.Ba, U, MT.N, 2*U, MT.O, 6*U)
            .tooltip("Ba(NO" + NUM_SUB[3] + ")" + NUM_SUB[2])
            .heat(865)
            .setSmelting(BaO, 2*U5),
    SrS = dustdcmp(16299, "Strontium Sulfide", SET_DULL, 255, 255, 255, 255)
            .uumMcfg(1, MT.Sr, U, MT.S, U)
            .heat(2275),
    SrO = dustdcmp(16300, "Strontium Oxide", SET_FINE, 255, 255, 255, 255)
            .uumMcfg(1, MT.Sr, U, MT.O, U)
            .heat(2804, 3470),
    SrCO3 = dustdcmp(16301, "Strontium Carbonate", SET_CUBE, 255, 255, 255, 255)
            .uumMcfg(1, MT.Sr, U, MT.C, U, MT.O, 3*U)
            .heat(1767)
            .setSmelting(SrO, 2*U5),
    BaSrCaCO3 = dustdcmp(16302, "Barium-Strontium-Calcium Carbonate", SET_CUBE, 255, 255, 255, 255)
            .setMcfg(1, BaCO3, 6*U, MT.CaCO3, 2*U, SrCO3, U)
            .heat(BaCO3),
    BaSrCaO3 = dustdcmp(16303, "Barium-Strontium-Calcium Oxide", SET_CUBE, 255, 255, 255, 255)
            .setMcfg(1, BaO, 6*U, CaO, 2*U, SrO, U)
            .heat(BaO)
            .put(CENTRIFUGE),
    Aquadag = dustdcmp(16304, "Aquadag", SET_FOOD, 10, 10, 10, 255)
            .heat(MT.H2O),
    Ga2O3 = dustdcmp(16305, "Gallium(III) Oxide", SET_METALLIC, 225, 210, 255, 255)
            .setMcfg(0, MT.Ga, 2*U, MT.O, 3*U)
            .heat(1998),
    InF3 = dustdcmp(16306, "Indium Trifluoride", SET_DULL, 255, 255, 255, 255)
            .setMcfg(0, MT.In, U, MT.F, 3*U)
            .heat(1445)
            .put(ELECTROLYSER),
    Si3N4 = semiconductor(16307, "Silicon Nitride",  70, 70, 70, true, false, false)
            .setMcfg(0, MT.Si, 3*U, MT.N, 4*U)
            .heat(2170)
            .setSmelting(MT.Si, 3*U7),
    BeCl2 = dustdcmp(16308, "Beryllium Chloride", SET_SHINY, 255, 255, 170, 255)
            .uumMcfg(1, MT.Be, U, MT.Cl, 2*U)
            .heat(672, 755),
    GaCl3 = dustdcmp(16309, "Gallium Chloride", SET_CUBE_SHINY, 255, 255, 255, 255)
            .setMcfg(1, MT.Ga, U, MT.Cl, 3*U)
            .heat(351, 474),
    InCl3 = dustdcmp(16310, "Indium Chloride", SET_CUBE_SHINY, 255, 255, 255, 255)
            .setMcfg(1, MT.In, U, MT.Cl, 3*U)
            .heat(859, 1070),
    CH3Cl = registerGas(gasdcmp(16311, "Chloromethane", 200, 230, 200, 200)
            .setMcfg(1, MT.C, U, MT.H, 3*U, MT.Cl, U)
            .heat(175, 249)),
    Ether = registerLiquid(lquddcmp(16312, "Diethyl Ether", 220, 220, 220, 220, "Ether", "Ethoxyethane")
            .setMcfg(1, MT.C, 4*U, MT.H, 10*U, MT.O, U)
            .heat(258, 329)),
    CH3Li = registerLiquid(lquddcmp(16313, "Methyllithium Solution", 200, 200, 200, 220)
            .setMcfg(3, MT.Li, U, MT.C, U, MT.H, 3*U, Ether, 2*U)
            .heat(Ether)),
    GaMe3 = registerLiquid(lquddcmp(16314, "Trimethylgallium Solution", 150, 150, 150, 200)
            .setMcfg(3, MT.Ga, U, MT.C, 3*U, MT.H, 9*U, Ether, 6*U)
            .tooltip("Ga(CH" + NUM_SUB[3] + ")" + NUM_SUB[3])
            .heat(Ether)),
    InMe3 = registerLiquid(lquddcmp(16315, "Trimethylindium Solution", 255, 255, 255, 255)
            .setMcfg(3, MT.In, U, MT.C, 3*U, MT.H, 9*U, Ether, 6*U)
            .tooltip("In(CH" + NUM_SUB[3] + ")" + NUM_SUB[3])
            .heat(Ether)),
    AlMe3 = registerLiquid(lquddcmp(16316, "Trimethylaluminium Solution", 200, 200, 200, 200)
            .setMcfg(3, MT.Al, 2*U, MT.C, 6*U, MT.H, 18*U, Ether, 6*U)
            .tooltip("Al" + NUM_SUB[2] + "(CH" + NUM_SUB[3] + ")" + NUM_SUB[6])
            .heat(Ether)),
    Cyclopentadiene = registerLiquid(lquddcmp(16317, "Cyclopentadiene", 255, 255, 255, 180)
            .setMcfg(1, MT.C, 5*U, MT.H, 6*U)
            .heat(183, 314)),
    Magnesocene = dustdcmp(16318, "Magnesocene", SET_ROUGH, 255, 255, 255, 255)
            .setMcfg(1, MT.Mg, U, MT.C, 10*U, MT.H, 10*U)
            .tooltip("Mg(C" + NUM_SUB[5] + "H" + NUM_SUB[5] + ")" + NUM_SUB[2])
            .heat(449),
    BeMe2 = registerLiquid(lquddcmp(16319, "Dimethylberyllium Solution", 200, 200, 200, 200)
            .setMcfg(3, MT.Be, U, MT.C, 2*U, MT.H, 6*U, Ether, 4*U)
            .tooltip("Be(CH" + NUM_SUB[3] + ")" + NUM_SUB[2])
            .heat(Ether)),
    H2Se = registerGas(gasdcmp(16320, "Hydrogen Selenide", 255, 210, 60, 150)
            .setMcfg(1, MT.H, 2*U, MT.Se, U)
            .heat(207, 232)),
    GaP = semiconductor(16321, "Gallium Phosphide", 63, 94, 83, false)
            .setMcfg(1, MT.Ga, U, MT.P, U),
    GaN = semiconductor(16322, "Gallium Nitride", 100, 120, 120, false)
            .setMcfg(1, MT.Ga, U, MT.N, U),
    GaAsP = semiconductor(16323, "Gallium Arsenide Phosphide", 66, 122, 95, false)
            .setMcfg(2, MT.Ga, 2*U, MT.As, U, MT.P, U),
    AlGaP = semiconductor(16324, "Aluminium Gallium Phosphide", 66, 100, 110, false)
            .setMcfg(2, MT.Al, U, MT.Ga, U, MT.P, 2*U),
    InGaN = semiconductor(16325, "Indium Gallium Nitride", 73, 66, 110, false)
            .setMcfg(1, InGa, U, MT.N, U),
    NDopedGaP = dopedSemiconductor(16326, "N-Doped Gallium Phosphide", GaP, false),
    PDopedGaP = dopedSemiconductor(16327, "P-Doped Gallium Phosphide", GaP, false),
    NDopedGaN = dopedSemiconductor(16328, "N-Doped Gallium Nitride", GaN, false),
    PDopedGaN = dopedSemiconductor(16329, "P-Doped Gallium Nitride", GaN, false),
    NDopedGaAs = dopedSemiconductor(16330, "N-Doped Gallium Arsenide", GaAs, true),
    PDopedGaAs = dopedSemiconductor(16331, "P-Doped Gallium Arsenide", GaAs, true),
    ColorResistRed = registerLiquid(lquddcmp(16332, "Red Color Resist", 255, 0, 0, 200)
            .setMcfg(2, DnqNovolacResist, U)),
    ColorResistGreen = registerLiquid(lquddcmp(16333, "Green Color Resist", 0, 255, 0, 200)
            .setMcfg(2, DnqNovolacResist, U)),
    ColorResistBlue = registerLiquid(lquddcmp(16334, "Blue Color Resist", 0, 0, 255, 200)
            .setMcfg(2, DnqNovolacResist, U)),
    CuAnodeSludgeRoast = dustdcmp(16335, "Roasted Copper Anode Sludge", SET_SHINY, 255, 200, 200, 255)
            .heat(CuAnodeSludge),
    Na2TeSeO3Solution = registerLiquid(lquddcmp(16336, "Sodium Selenite-Tellurite solution", 255, 200, 200, 200)
            .setMcfg(0, MT.Na, 4*U, MT.Se, U, MT.Te, U, MT.O, 6*U, MT.H2O, 12*U)
            .heat(MT.H2O)),
    Na2SO4H2SeO3Solution = registerLiquid(lquddcmp(16337, "Selenous Acid - Sodium Sulfate solution", 255, 240, 240, 200)
            .setMcfg(0, MT.H, 2*U, MT.Se, U, MT.O, 3*U, MT.Na2SO4, 14*U, MT.H2O, 15*U)),
    Bi2O3 = dustdcmp(16338, "Bismuth(III) Oxide", SET_DULL, 255, 255, 210, 255)
            .setMcfg(0, MT.Bi, 2*U, MT.O, 3*U)
            .heat(1090, 2160),
    TeO2 = dustdcmp(16339, "Tellurium Dioxide", SET_FINE, 255, 255, 225, 255)
            .uumMcfg(1, MT.Te, U, MT.O, 2*U)
            .heat(1005, 1518)
            .put(ELECTROLYSER),
    GaH3 = registerGas(gasdcmp(16340, "Gallane", 230, 230, 230, 150)
            .setMcfg(1, MT.Ga, U, MT.H, 3*U)
            .heat(223, 241)),
    NaNO3Solution = simpleSolution(16341, MT.NaNO3, 3),
    IGZO = dustdcmp(16342, "Indium Gallium Zinc Oxide", SET_METALLIC, 255, 50, 255, 255)
            .uumMcfg(0, MT.In, 2*U, MT.Ga, 2*U, MT.Zn, U, MT.O, 7*U)
            .heat(850+C),
    BiCl3 = dustdcmp(16343, "Bismuth Chloride", SET_ROUGH, 255, 255, 200, 255)
            .setMcfg(0, MT.Bi, U, MT.Cl, 3*U)
            .heat(500, 720),
    ConcHCl = registerLiquid(lqudaciddcmp(16344, "Concentrated Hydrochloric Acid", 50, 255, 178, 200)
            .setMcfg(0, MT.HCl, 2*U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    PbCl2Solution = simpleSolution(16345, PbCl2, 3),
    BiCl3Solution = simpleSolution(16346, BiCl3, 3),
    MnCl2Solution = simpleSolution(16347, MT.MnCl2, 3),
    LiClSolution = simpleSolution(16348, MT.LiCl, 3),
    HCl3Si = registerGas(gasdcmp(16349, "Trichlorosilane", 190, 190, 190, 150)
            .setMcfg(1, MT.H, U, MT.Cl, 3*U, MT.Si, U)
            .heat(146, 305)),
    PCl3 = registerLiquid(lquddcmp(16350, "Phosphorus Trichloride", 255, 255, 220, 200)
            .setMcfg(0, MT.P, U, MT.Cl, 3*U)
            .heat(180, 349)),
    POCl3 = registerLiquid(lquddcmp(16351, "Phosphoryl Trichloride", 210, 210, 210, 200)
            .setMcfg(0, MT.P, U, MT.O, U, MT.Cl, 3*U)
            .heat(274, 379)),
    H3PO3 = dustdcmp(16352, "Phosphorous Acid", SET_CUBE, 255, 255, 255, 255)
            .setMcfg(0, MT.H, 3*U, MT.P, U, MT.O, 3*U)
            .heat(346, 473),
    K2SiO3 = dustdcmp(16353, "Potassium Metasilicate", SET_CUBE, 255, 255, 255, 255)
            .setMcfg(0, MT.K, 2*U, MT.Si, U, MT.O, 3*U)
            .heat(600+C, 800+C),
    KFSolution = simpleSolution(16354, MT.KF, 3),
    SnF2 = dustdcmp(16355, "Tin(II) Fluoride", SET_FINE, 200, 255, 200, 255)
            .uumMcfg(1, MT.Sn, U, MT.F, 2*U)
            .heat(486, 1120),
    FTO = dustdcmp(16356, "Fluorine-doped Tin Oxide", SET_METALLIC, 0, 0, 0, 255)
            .stealLooks(MT.OREMATS.Cassiterite)
            .heat(MT.OREMATS.Cassiterite)
            .setMcfg(1, MT.OREMATS.Cassiterite, U, SnF2, U32)
            .tooltip("SnO" + NUM_SUB[2] + ":F"),
    NDopedCdS = dopedSemiconductor(16357, "N-doped Cadmium Sulfide", CdS, true, false),
    CdTe = semiconductor(16358, "Cadmium Telluride", 50, 50, 50, true, true, false)
            .uumMcfg(0, MT.Cd, U, MT.Te, U)
            .heat(1314, 1320),
    PDopedCdTe = dopedSemiconductor(16359, "P-doped Cadmium Telluride", CdTe, true, false),
    Re2O7 = create(16360, "Rhenium Heptoxide", 230, 255, 0, 255)
            .setMcfg(0, MT.Re, 2*U, MT.O, 7*U)
            .heat(633, 633),
    MoS2RoastingGas = registerGas(gasdcmp(16361, "Molybdenite Roasting Gas", 255, 200, 100, 255)
            .setMcfg(0, MT.SO2, U, Re2O7, U)
            .heat(MT.SO2)),
    HReO4 = registerLiquid(lqudaciddcmp(16362, "Perrhenic Acid", 255, 255, 200, 150)
            .setMcfg(0, MT.H, U, MT.Re, U, MT.O, 4*U)
            .heat(MT.H2O)),
    NH4ReO4 = dustdcmp(16363, "Ammonium Perrhenate", SET_GLASS, 255, 255, 255, 255)
            .setMcfg(0, NH4, U, MT.Re, U, MT.O, 4*U)
            .tooltip("NH" + NUM_SUB[4] + "ReO" + NUM_SUB[4])
            .heat(200+C)
            .setSmelting(HReO4, U),
    PtRe = alloy(16364, "Platinum-Rhenium", SET_SHINY, 200, 210, 220)
            .uumAloy(0, MT.Pt, U, MT.Re, U)
            .heat(2350),
    AlGaAs = semiconductor(16365, "Aluminium Gallium Arsenide", 107, 131, 156, false)
            .uumMcfg(2, MT.Al, U, MT.Ga, U, MT.As, 2*U),
    CuInGa = alloy(16366, "Copper-Indium-Gallium", SET_COPPER, 142, 110, 192, 255)
            .uumAloy(0, MT.Cu, U, InGa, U),
    CIGS = semiconductor(16367, "Copper Indium Gallium Selenide", 14, 53, 53, false)
            .uumMcfg(0, MT.Cu, U, InGa, U, MT.Se, 2*U),
    InGaP = semiconductor(16368, "Indium Gallium Phosphide", 93, 113, 93, false)
            .uumMcfg(1, InGa, U, MT.P, U),
    InGaAs = semiconductor(16369, "Indium Gallium Arsenide", 205, 205, 205, false)
            .uumMcfg(1, InGa, U, MT.As, U),
    AlInP = semiconductor(16370, "Aluminium Indium Phosphide", 160, 160, 140, false)
            .uumMcfg(2, MT.Al, U, MT.In, U, MT.P, 2*U),
    NDopedInGaP = dopedSemiconductor(16371, "N-doped Indium Gallium Phosphide", InGaP, false),
    PDopedInGaP = dopedSemiconductor(16372, "P-doped Indium Gallium Phosphide", InGaP, false),
    NDopedAlGaAs = dopedSemiconductor(16373, "N-doped Aluminium Gallium Arsenide", AlGaAs, false),
    PDopedAlGaAs = dopedSemiconductor(16374, "P-doped Aluminium Gallium Arsenide", AlGaAs, false),
    NDopedAlInP = dopedSemiconductor(16375, "N-doped Aluminium Indium Phosphide", AlInP, false),
    PDopedAlInP = dopedSemiconductor(16376, "P-doped Aluminium Indium Phosphide", AlInP, false),
    GaCl3Solution = simpleSolution(16377, GaCl3, 1),
    InCl3Solution = simpleSolution(16378, InCl3, 1),
    Na2TiO3 = dustdcmp(16379, "Sodium Metatitanate", SET_DULL, 255, 255, 200, 255)
            .uumMcfg(1, MT.Na, 2*U, MT.Ti, U, MT.O, 3*U)
            .heat(1128+C),
    Na2SiO3 = dustdcmp(16380, "Sodium Metasilicate", SET_GLASS, 255, 255, 255, 255)
            .setMcfg(0, MT.Na, 2*U, MT.Si, U, MT.O, 3*U)
            .heat(1361),
    Na2SiO3Solution = simpleSolution(16381, Na2SiO3, 6),
    RbCl = dustdcmp(16382, "Rubidium Chloride", SET_FINE, 255, 255, 255, 255, ELECTROLYSER)
            .setMcfg(1, MT.Rb, U, MT.Cl, U)
            .heat(991, 1660),
    CsCl = dustdcmp(16383, "Caesium Chloride", SET_FINE, 255, 255, 255, 255, ELECTROLYSER)
            .setMcfg(1, MT.Cs, U, MT.Cl, U)
            .heat(919, 1570),
    CsRbClSolution = registerLiquid(lquddcmp(16382, "Caesium-Rubidium Chloride Solution", 255, 200, 0, 200)
            .setMcfg(0, CsCl, 5*U, RbCl, U, MT.H2O, 3*3*U)),

    // Engines, more oil processing
    Ti6Al4V = alloymachine(16383, "Ti-6Al-4V", SET_COPPER, 191, 145, 255, "Titanium Alloy", PIPES)
            .uumAloy(0, MT.Ti, 24*U, MT.Al, 3*U, MT.V, U)
            .heat(1632+C),
    TMS196 = alloymachine(16384, "TMS-196", SET_SHINY, 255, 255, 204, PIPES)
            .uumMcfg(0, MT.Ni, 42*U, MT.Al, 8*U, MT.Cr, 4*U, MT.Co, 4*U, MT.Ru, 2*U, MT.Ta, U, MT.Mo, U, MT.W, U, MT.Re, U)
            .setLocal("TMS-196 Superalloy"),
    Xylene = registerLiquid(lqudflam(16386, "Xylene", 255, 255, 255, 255)
            .setMcfg(1, MT.C, 8*U, MT.H, 10*U)
            .heat(226, 412)),
    MTBE = registerLiquid(lqudflam(16387, "MTBE", 255, 255, 255, 200)
            .setMcfg(1, MT.C, 5*U, MT.H, 12*U, MT.O, U)
            .heat(165, 328)
            .setLocal("Methyl Tert-Butyl Ether")),
    Super95E10 = registerLiquid(lqudflam(16388, "Super 95 E10", 0, 121, 88, 255)
            .heat(MT.Petrol)),
    SuperPlus98E5 = registerLiquid(lqudflam(16389, "SuperPlus 98 E5", 0, 85, 85, 255)
            .heat(MT.Petrol)),
    E85 = registerLiquid(lqudflam(16390, "E85", 255, 100, 0, 255)
            .heat(MT.Ethanol)),

    VacuumResidue = create(16392, "Vacuum Residue Oil", 22, 22, 22, 255).setTextures(SET_ROUGH).put(DECOMPOSABLE, INGOTS)
            .heat(MT.Asphalt),
    DAO = registerLiquid(lqudflam(16393, "DAO", 200, 150, 0, 255)
            .setLocal("De-Asphalted Oil")
            .heat(MT.Fuel)),
    LVGO = registerLiquid(lqudflam(16394, "LVGO", 110, 105, 0, 255)
            .setLocal("Light Vacuum Gas Oil")
            .heat(200, 400)),
    HVGO = registerLiquid(lqudflam(16395, "HVGO", 150, 100, 0, 255)
            .setLocal("Heavy Vacuum Gas Oil")
            .heat(225, 525)),
    CGO = registerLiquid(lqudflam(16396, "CGO", 100, 100, 0, 200)
            .setLocal("Coker Gas Oil")),
    Butanone = registerLiquid(lquddcmp(16397, "Butanone", 255, 255, 255, 150, "MEK", "Methyl Ethyl Ketone", "Ethyl Methyl Ketone")
            .heat(187, 353)
            .setLocal("Methyl Ethyl Ketone")), MEK = Butanone,
    DewaxedMEKSolution = registerLiquid(lquddcmp(16398, "Solvent-dewaxed MEK Solution", 255, 230, 200, 255)
            .heat(MEK)),
    Butadiene = registerGas(gasdcmp(16399, "Butadiene", 120, 70, 150, 255, FLAMMABLE)
            .setMcfg(1, MT.C, 4*U, MT.H, 6*U)
            .heat(164, 269)),
    Styrene = registerLiquid(lquddcmp(16400, "Styrene", 230, 255, 200, 255, FLAMMABLE)
            .setMcfg(1, MT.C, 8*U, MT.H, 8*U)
            .heat(243, 418)),
    Ethylbenzene = registerLiquid(lquddcmp(16401, "Ethylbenzene", 255, 230, 200, 255, FLAMMABLE)
            .setMcfg(1, MT.C, 8*U, MT.H, 10*U)
            .heat(178, 409)),
    Polystyrene = plastic(16402, "Polystyrene", SET_CUBE, 255, 255, 255, 255, "Styrofoam")
            .setMcfg(1, Styrene, U)
            .heat(513, 703),
    Sulfolane = registerLiquid(lquddcmp(16404, "Sulfolane", 255, 255, 210, 255)
            .setMcfg(1, MT.C, 4*U, MT.H, 8*U, MT.S, U, MT.O, 2*U)
            .heat(300, 558)),
    Pygas = registerLiquid(lqudflam(16405, "Pygas", 255, 255, 255, 255, "Pyrolysis Gasoline")
            .setLocal("Pyrolysis Gasoline")
            .heat(C-62, C+204)),
    AromaticsMix = registerLiquid(lqudflam(16406, "Aromatics-Rich Distillate", 255, 255, 255, 255)
            .heat(Toluene)),
    Reformate = registerLiquid(lqudflam(16407, "Reformate", 255, 50, 0, 255)),
    BTXSolution = registerLiquid(lquddcmp(16408, "BTX Solution", 255, 255, 230, 255)
            .heat(Sulfolane)),
    DistillersGrains = dustdcmp(16409, "Distillers Grains", SET_POWDER, 255, 255, 120, 255)
            .put(FOOD, MORTAR, ANY.FlourGrains, ANY.Flour, FLAMMABLE)
            .aspects(TC.MESSIS, 2).setBurning(MT.Ash, U9),
    Nitromethane = registerLiquid(lquddcmp(16410, "Nitromethane", 220, 255, 100, 255, FLAMMABLE, EXPLOSIVE)
            .heat(245, 374)),
    A6061 = alloymachine(16411, "A6061", SET_COPPER, 179, 208, 230, PIPES)
            .uumAloy(0, MT.Al, 96*U, MT.Si, U, MT.Mg, U, MT.Cr, U, MT.Cu, U)
            .setLocal("Aluminium Alloy 6061"),
    ZrO2 = dustdcmp(16412, "Zirconia", SET_ROUGH, 255, 255, 255, 255)
            .uumMcfg(1, MT.Zr, U, MT.O, 2*U)
            .heat(2988, 4570),
    ZrOCl2 = dustdcmp(16413, "Zirconyl Chloride", SET_COPPER, 255, 255, 255, 255)
            .setMcfg(0, MT.Zr, U, MT.O, U, MT.Cl, 2*U)
            .heat(400+C),
    ZrO4H4 = dustdcmp(16414, "Zirconium Hydroxide", SET_DULL, 255, 255, 255, 255)
            .setMcfg(2, MT.Zr, U, MT.O, 4*U, MT.H, 4*U)
            .tooltip("Zr(OH)" + NUM_SUB[4])
            .setSmelting(ZrO2, U2)
            .heat(550+C),
    YZrOH = dustdcmp(16415, "Yttrium-Zirconium Hydroxide", SET_DULL, 255, 255, 255, 255)
            .setMcfg(15, MT.Zr, 5*U, MT.Y, U, MT.O, 23*U, MT.H, 23*U)
            .tooltip("(Zr(OH)" + NUM_SUB[4] +")" + NUM_SUB[15] + "Y(OH)" + NUM_SUB[3])
            .heat(ZrO4H4),
    YSZ = dustdcmp(16416, "YSZ", SET_CUBE_SHINY, 255, 255, 255, 255)
            .setMcfg(15, ZrO2, 10*U, Y2O3, 2*U)
            .heat(ZrO2)
            .setLocal("Yttria-Stabilized Zirconia"),
    La2O3 = dustdcmp(16417, "Lanthana", SET_ROUGH, 255, 255, 255, 255)
            .setMcfg(2, MT.La, 2*U, MT.O, 3*U)
            .setDensity(6.51)
            .heat(2588, 4470),
    LaNO3 = dustdcmp(16418, "Lanthanum Nitrate", SET_ROUGH, 255, 255, 255, 255)
            .setMcfg(10, MT.La, U, MT.N, 3*U, MT.O, 9*U)
            .tooltip("La(NO" + NUM_SUB[3] + ")" + NUM_SUB[3])
            .heat(313, 399),
    LaZrOH = dustdcmp(16419, "Lanthanum-Zirconium Hydroxide", SET_DULL, 255, 255, 255, 255)
            .setMcfg(8, ZrO4H4, 2*U, MT.La, U, MT.O, 3*U, MT.H, 3*U)
            .tooltip("LaZr(OH)" + NUM_SUB[7])
            .heat(ZrO4H4),
    La2Zr2O7 = dustdcmp(16420, "Lanthanum Zirconate", SET_CUBE_SHINY, 255, 255, 255, 255)
            .setMcfg(8, MT.La, 2*U, MT.Zr, 2*U, MT.O, 7*U)
            .heat(2788, 4520),
    NH4NO3Solution = simpleSolution(16421, NH4NO3, 3),
    Massicot = oredustdcmp(16422, "Massicot", SET_CUBE, 230, 230, 50, 255)
            .setMcfg(1, PbO, U)
            .heat(400)
            .setAllToTheOutputOf(PbO),
    Zincite = oredustdcmp(16423, "Zincite", SET_COPPER, 255, 150, 0, 255)
            .setMcfg(1, ZnO, U)
            .heat(400)
            .setAllToTheOutputOf(ZnO),
    DRISlag = dustdcmp(16424, "DRI-Slag", SET_FLINT, 100, 100, 50, 255)
            .put(INGOTS, MORTAR, BRITTLE, GEMS)
            .heat(ConverterSlag),
    Li2SO4 = create(16425, "Lithium Sulfate", 255, 255, 255, 255)
            .setMcfg(0, MT.Li, 2*U, MT.S, U, MT.O, 4*U),
    Rb2SO4 = create(16426, "Rubidium Sulfate", 255, 255, 255, 255)
            .setMcfg(0, MT.Rb, 2*U, MT.S, U, MT.O, 4*U),

    LepidoliteLeachingSolution = registerLiquid(lquddcmp(16427, "Lepidolite Leaching Solution", 255, 255, 255, 255)
            .setMcfg(0, MT.K2SO4, 7*U, Li2SO4, 7*U, Rb2SO4, 7*U, MT.VitriolOfClay, 17*U, MT.H2O, 6*3*U)
            .heat(MT.H2O)
            .stealLooks(MT.OREMATS.Lepidolite)),
    LiKRbSulfateSolution = registerLiquid(lquddcmp(16428, "Lithium-Rubidium-Potassium Sulfate Solution", 255, 255, 255, 255)
            .setMcfg(0, MT.K2SO4, 28*U, Li2SO4, 7*U, Rb2SO4, 7*U, MT.H2O, 6*3*U)
            .heat(MT.H2O)
            .stealLooks(K2SO4Solution)),
    Diethylbenzene = registerLiquid(lquddcmp(16429, "Diethylbenzene", 255, 255, 255, 255)
            .setMcfg(1, MT.C, 10*U, MT.H, 14*U)
            .heat(230, 457)),
    DVB = registerLiquid(lquddcmp(16430, "DVB", 230, 230, 200, 255)
            .setLocal("Divinylbenzene")
            .setMcfg(1, MT.C, 10*U, MT.H, 10*U)
            .heat(210, 468)),
    StyDVB = plastic(16431, "Sty-DVB", SET_RUBBER, 255, 240, 200, 255)
            .setLocal("Styrene-Divinylbenzene Copolymer")
            .setMcfg(0, Styrene, 10*U, DVB, U)
            .heat(Polystyrene),
    Chloromethylstyrene = create(16432, "Chloromethylstyrene", 240, 255, 240, 255)
            .setMcfg(1, MT.C, 9*U, MT.H, 9*U, MT.Cl, U),
    ChloromethylStyDVB = plastic(16433, "Chloromethylated Sty-DVB", SET_RUBBER, 240, 255, 240, 255)
            .setMcfg(0, Chloromethylstyrene, 10*U, DVB, U)
            .heat(Polystyrene),
    Trimethylamine = registerGas(gasdcmp(16434, "Trimethylamine", 200, 200, 255, 150)
            .setMcfg(1, MT.C, 3*U, MT.H, 9*U, MT.N, U)
            .heat(156, 278)),
    ZnCl2 = dustdcmp(16435, "Zinc Chloride", SET_DIAMOND, 255, 255, 255, 255)
            .uumMcfg(0, MT.Zn, U, MT.Cl, 2*U)
            .heat(564, 1005)
            .put(ELECTROLYSER),
    ZnCl2Solution = simpleSolution(16436, ZnCl2, 3),
    OH = create(16435, "Hydroxide").setMcfg(0, MT.O, U, MT.H, U).put(ANION, DECOMPOSABLE),
    SO4 = create(16436, "Sulfate").setMcfg(0, MT.S, U, MT.O, 4*U).put(ANION, DECOMPOSABLE),
    NO3 = create(16437, "Nitrate").setMcfg(0, MT.N, U, MT.O, 3*U).put(ANION, DECOMPOSABLE),

    DiluteHCl = registerLiquid(lqudaciddcmp(16439, "Dilute Hydrochloric Acid", 100, 255, 200, 200)
            .setMcfg(0, MT.HCl, U, MT.H2O, 3*U)
            .heat(MT.H2O)),
    RbClSolution = simpleSolution(16440, RbCl, 3),
    CsClSolution = simpleSolution(16441, CsCl, 3),
    DecatMnWtr = registerLiquid(lqudaciddcmp(16442, "Decationized Mineral Water", 240, 255, 220, 255)
            .heat(MT.H2O)),
    DecatWater = registerLiquid(lqudaciddcmp(16443, "Decationized Water", 248, 255, 240, 255)
            .heat(MT.H2O)),
    REE2O3 = dustdcmp(16444, "Rare-Earth Oxide", SET_ROUGH, 255, 255, 255, 255)
            .setMcfg(0, MT.RareEarth, 2*U, MT.O, 3*U)
            .heat(La2O3),
    REORoasted = dustdcmp(16445, "Roasted Rare-Earth Oxide", SET_ROUGH, 255, 255, 255, 255)
            .setMcfg(0, CeO2, 3*U, REE2O3, 2*U),
    REECl3 = create(16446, "Rare-Earth Chloride", 200, 255, 210, 255)
            .setMcfg(0, MT.RareEarth, U, MT.Cl, 3*U),
    REECl3Solution = solution(16447, "Rare-Earth Chloride Solution", 200, 255, 210, 255, REECl3, 3),
    REEHydroxide = dustdcmp(16448, "Rare-Earth Hydroxide Residue", SET_ROUGH, 180, 200, 100, 255)
            .setMcfg(0, MT.RareEarth, 4*U, MT.Th, U, OH, 15*U)
            .heat(330+C),
    ThO2 = dustdcmp(16449, "Thorium Dioxide", SET_RAD, 180, 220, 0, 255)
            .uumMcfg(0, MT.Th, U, MT.O, 2*U)
            .heat(3620, 4670)
    ;

    @SuppressWarnings("unused")
    public static class UNUSED {
        public static OreDictMaterial
                AsF3 = unused("Arsenic Trifluoride")
                    .setRGBa(100, 130, 100, 250)
                    .setMcfg(1, MT.As, U, MT.F, 3 * U)
                    .heat(265, 334),
                Na3VO4 = unused("Sodium Orthovanadate")
                        .setMcfg(0, MT.Na, 3 * U, MT.V, U, MT.O, 4 * U),
                AuGe = unused("Gold-Germanium")
                        .setTextures(SET_COPPER)
                        .setRGBa(227, 182, 59, 255)
                        .uumAloy(0, MT.Au, U, MT.Ge, U)
                        .heat(365 + C, (MT.Ge.mBoilingPoint + MT.Au.mBoilingPoint) / 2),
                Sulfolene = unused("Sulfolene")
                        .setTextures(SET_CUBE)
                        .setMcfg(1, MT.C, 4 * U, MT.H, 6 * U, MT.S, U, MT.O, 2 * U)
                        .heat(338),
                LightNaphtha = unused("Light Naphtha")
                        .stealLooks(MT.Petrol)
                        .heat(MT.Petrol)
                        .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1),
                HeavyNaphtha = unused("Heavy Naphtha")
                        .setRGBa(255, 200, 0, 255)
                        .heat(C - 40, C + 120)
                        .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1),
                HeavyNaphthaLowSulfur = unused("Low-Sulfur Heavy Naphtha")
                        .stealLooks(HeavyNaphtha)
                        .heat(HeavyNaphtha)
                        .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1),
                HAGO = unused("HAGO")
                        .setLocal("Heavy Atmospheric Gas Oil")
                        .stealLooks(MT.Fuel)
                        .heat(260, 350 + C)
                        .aspects(TC.MORTUUS, 1, TC.POTENTIA, 1),
                HCO3 = unused("Hydrogencarbonate")
                        // .put(ANION, DECOMPOSABLE)
                        .setMcfg(0, MT.H, U, MT.C, U, MT.O, 3*U)
                ;
    }

    static {
        Zincite.remove(DUSTS); Zincite.remove(PLANTS);
        Massicot.remove(DUSTS); Massicot.remove(PLANTS);

        MT.BlueSapphire  .uumMcfg(6, MT.Al2O3, 5*U, MT.Fe2O3, U);
        MT.Ruby          .uumMcfg(6, MT.Al2O3, 5*U, Cr2O3, U);
        MT.GreenSapphire .uumMcfg(6, MT.Al2O3, 5*U, MgO, U);
        MT.PurpleSapphire.uumMcfg(6, MT.Al2O3, 5*U, MT.V2O5, U);
        MT.Almandine  .uumMcfg( 0, MT.Al2O3, 5*U, FeO, 6*U, MT.SiO2, 9*U);
        MT.Grossular  .uumMcfg( 0, MT.Al2O3, 5*U, CaO, 6*U, MT.SiO2, 9*U);
        MT.Pyrope     .uumMcfg( 0, MT.Al2O3, 5*U, MgO, 6*U, MT.SiO2, 9*U);
        MT.Spessartine.uumMcfg( 0, MT.Al2O3, 5*U, MnO, 3*U, MT.SiO2, 9*U);
        MT.Andradite  .uumMcfg( 0, MT.Fe2O3, 5*U, CaO, 6*U, MT.SiO2, 9*U);
        MT.Uvarovite  .uumMcfg( 0,    Cr2O3, 5*U, CaO, 6*U, MT.SiO2, 9*U);

        MT.PetCoke.setMcfg(0, MT.C, U);
        MT.Bone.uumMcfg(2, Hydroxyapatite, U).setSmelting(Hydroxyapatite, U2).heat(1000+C);
        MT.SlimyBone.uumMcfg(0, MT.Bone, U);
        MT.VolcanicAsh.setMcfg( 0, MT.Flint, 6*U, MT.Fe2O3, U, MTx.MgO, U);

        // ZSM-5 with n=16
        MT.OREMATS.Zeolite.setMcfg(0, Na2O, 3*U, MT.Al2O3, 5*U, MT.SiO2, 10*3*U, MT.H2O, 6*U)
                .put(GEMS);

        addMolten(RhodiumPotassiumSulfate);
        addMolten(AlCl3);
        addMolten(PbCl2);
        addMolten(CuCl2);
        addMolten(ZnCl2);
        addMolten(Slag);
        addMolten(FerrousSlag);
        addMolten(FeCr2);
        addMolten(ConverterSlag);
        addMolten(DRISlag);
        addMolten(Phenol);
        addMolten(Epoxy);
        addMolten(LiF);
        addMolten(SiGe);
        addMolten(GaAs);
        addMolten(InF3);

        addVapour(RuO4);
        addVapour(OsO4);

        addPlasma(CF4, false, 310);
        addPlasma(NF3, false, 310);
        addPlasma(SF6, false, 310);

        OP.plate.forceItemGeneration(MT.Al2O3);
        OP.wireFine.forceItemGeneration(MT.Ta);
        OP.casingMachine.forceItemGeneration(MT.Plastic, MT.Polycarbonate);
        OP.foil.forceItemGeneration(MT.Glass);

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
