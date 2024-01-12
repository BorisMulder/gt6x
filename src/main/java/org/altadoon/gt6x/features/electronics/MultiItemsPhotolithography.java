package org.altadoon.gt6x.features.electronics;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.FL;
import gregapi.data.MT;
import gregapi.data.RM;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.old.Textures;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.event.OreDictListenerEvent_Names;
import gregapi.util.ST;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.items.ILx;

import java.util.BitSet;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static org.altadoon.gt6x.features.electronics.Electronics.oxidizedWafer;

public class MultiItemsPhotolithography extends MultiItemRandom {
    public static MultiItemsPhotolithography instance;

    public MultiItemsPhotolithography(String aModID, String aUnlocalized) {
        super(aModID, aUnlocalized);
    }

    public final BitSet enabledIcons = new BitSet(32768);

    static final String tooltipNeedsDevelopment = "Needs development";
    static final String tooltipNeedsEtching = "Ready to be etched";
    static final String tooltipNeedsCleaning = "Needs cleaning";
    static final String tooltipNeedsDoping = "Could use some extra dopant";
    static final String tooltipNeedsMetal = "Needs conductive metal layer";
    static final String tooltipDicing = "Interconnected, can be diced";
    static final String tooltipBonding = "Needs bonding & packaging";

    static final String photomask = "Photomask";

    static final String waferPatterned = "Wafer (Patterned)";
    static final String waferDeveloped = "Wafer (Developed)";
    static final String waferEtched = "Wafer (Etched)";
    static final String waferCleaned = "Wafer (Cleaned)";
    static final String waferDoped = "Wafer (Doped)";
    static final String waferMetal = "Wafer (Metal)";
    static final String wafer = "Wafer";
    static final String die = "Die";

    static final String[] CHIP_TIER_NAMES = { "PMOS", "NMOS", "CMOS" };
    static final String[] CHIP_TYPE_NAMES = { "IC", "CPU", "DRAM", "GPU", "NAND Flash", "SoC", "GaAs FET" };
    public static final int MESFET_IDX = 6;

    static final int PM_FINISHED = 3;
    static final String[] MASK_STAGE_NAMES = { ", Patterned", ", Developed", ", Etched", "" };
    static final String[] PL_STAGE_NAMES_SINGLE = { waferPatterned, waferDeveloped, waferEtched, waferDoped, waferCleaned, waferMetal, wafer, die };
    static final String[] PL_STAGE_NAMES_DOUBLE = { "Wafer (Patterned, Stage I)", "Wafer (Developed, Stage I)", "Wafer (Etched, Stage I)", "Wafer (N-Wells)", "Wafer (Cleaned, Stage I)", "Wafer (Oxidised, Stage II)", "Wafer (Patterned, Stage II)", "Wafer (Developed, Stage II)", "Wafer (Etched, Stage II)", "Wafer (PNP-Junctions)", "Wafer (Cleaned, Stage II)", waferMetal, wafer, die };
    static final String[] PL_STAGE_NAMES_MESFET = { waferPatterned, waferDeveloped, waferEtched, waferDoped, "Wafer (Metal 1)", "Wafer (Metal 2)", wafer, "" };

    static final String[] PHOTOMASK_TOOLTIPS = {tooltipNeedsDevelopment, tooltipNeedsEtching, tooltipNeedsCleaning, "Used in Photolithography"};
    static final String[] WAFER_TOOLTIPS_SINGLE = {tooltipNeedsDevelopment, tooltipNeedsEtching, tooltipNeedsDoping, tooltipNeedsCleaning, tooltipNeedsMetal, tooltipNeedsEtching, tooltipDicing, tooltipBonding };
    static final String[] WAFER_TOOLTIPS_DOUBLE = {tooltipNeedsDevelopment, tooltipNeedsEtching, tooltipNeedsDoping, tooltipNeedsCleaning, "Needs additional oxide layer", "Needs junctions", tooltipNeedsDevelopment, tooltipNeedsEtching, tooltipNeedsDoping, tooltipNeedsCleaning, tooltipNeedsMetal, tooltipNeedsEtching, tooltipDicing, tooltipBonding };
    static final String[] WAFER_TOOLTIPS_MESFET = {tooltipNeedsDevelopment, tooltipNeedsEtching, tooltipNeedsDoping, tooltipNeedsMetal, "Needs Schottky Gate Metal Layer", tooltipNeedsCleaning, tooltipDicing, tooltipBonding };

    @Override
    public void addItems() {
        // Raw Photomask
        ILx.Photomask_Raw.set(addItemWithIcon(0, "Raw Photomask", "Needs Design", new OreDictItemData(MT.Glass, U, MT.Cr, U)));
        RM.Laminator.addRecipe2(true, 16, 128, plate.mat(MT.Glass, 1), plate.mat(MT.Cr, 1), ILx.Photomask_Raw.get(1));

        // SiN CVD on GaAs Wafer
        ILx.Wafer_GaAs_SiN_layered.set(addItemWithIcon(32766, "SiN-capped GaAs wafer", "Gallium Arsenide Wafer with Silicon Nitride cap layer", new OreDictItemData(MTx.GaAs, U)));
        RM.Drying.addRecipe1(true, 64, 256, plateGem.mat(MTx.GaAs, 1), FL.array(MTx.SiH4.gas(15*U1000, true), MT.NH3.gas(4*U1000, true)), MT.H.gas(24*U1000, false), ILx.Wafer_GaAs_SiN_layered.get(1));

        // Photoresist
        RM.Mixer.addRecipe2(true, 16, 256, dust.mat(MTx.DNQ, 1), dust.mat(MTx.PF, 1), MTx.Toluene.liquid(2*U, true), MTx.DnqNovolacResist.liquid(4*U, false), NI);

        // Add tiered Wafers and Photomasks
        for (int type = 0; type < MESFET_IDX; type++) {
            for (int tier = 0; tier < ILx.NUM_WAFER_TIERS; tier++) {
                addPhotomasks(type, tier);
                addWafers(type, tier);
            }
            addPMOSWaferRecipes(type);
            addNMOSWaferRecipes(type);
            addCMOSWaferRecipes(type);
        }

        // Non-tiered stuff
        addPhotomasks(MESFET_IDX, 0);
        addWafers(MESFET_IDX, 0);
        addMESFETWaferRecipes();
    }


    private void addPhotomask(int type, int tier, int plStage, int stage) {
        String name;
        if (type == MESFET_IDX) {
            name = String.format("%s (%s%s)",
                    photomask,
                    CHIP_TYPE_NAMES[type],
                    MASK_STAGE_NAMES[stage]
            ); // e.g. Photomask (MESFET, Developed)
        } else {
            if (tier < 2) {
                name = String.format("%s (%s %s%s)",
                        photomask,
                        CHIP_TIER_NAMES[tier],
                        CHIP_TYPE_NAMES[type],
                        MASK_STAGE_NAMES[stage]
                ); // e.g. Photomask (PMOS IC, Developed)
            } else {
                name = String.format("%s (%s %s Stage %s%s)",
                        photomask,
                        CHIP_TIER_NAMES[tier],
                        CHIP_TYPE_NAMES[type],
                        new String(new char[plStage + 1]).replace("\0", "I"),
                        MASK_STAGE_NAMES[stage]
                ); // e.g. Photomask (CMOS IC Stage I, Etched)
            }
        }

        ILx.Photomasks[type][tier][plStage][stage].set(addItemWithIcon(
                type * 1000 + tier * 100 + plStage * 10 + stage + 1,
                name,
                PHOTOMASK_TOOLTIPS[stage],
                new OreDictItemData(MT.Glass, U, MT.Cr, stage < 2 ? U : U2)
        ));
    }

    private void addWafer(int type, int tier, int stage) {
        String name;
        String tooltip;
        if (type == MESFET_IDX) {
            name = String.format("%s %s",
                    CHIP_TYPE_NAMES[type],
                    PL_STAGE_NAMES_MESFET[stage]
            ); // e.g. GaAs FET Wafer (Developed)
            tooltip = WAFER_TOOLTIPS_MESFET[stage];
        } else {
            if (tier < 2) {
                name = String.format("%s %s %s",
                        CHIP_TIER_NAMES[tier],
                        CHIP_TYPE_NAMES[type],
                        PL_STAGE_NAMES_SINGLE[stage]
                ); // e.g. PMOS IC Wafer (Patterned)
                tooltip = WAFER_TOOLTIPS_SINGLE[stage];
            } else {
                name = String.format("%s %s %s",
                        CHIP_TIER_NAMES[tier],
                        CHIP_TYPE_NAMES[type],
                        PL_STAGE_NAMES_DOUBLE[stage]
                ); // e.g. CMOS CPU Wafer (Patterned, Stage I)
                tooltip = WAFER_TOOLTIPS_DOUBLE[stage];
            }
        }
        ILx.Wafers[type][tier][stage].set(addItemWithIcon(
                10000 + type * 1000 + tier * 100 + stage,
                name,
                tooltip
        ));
    }

    private void addPhotomasks(int type, int tier) {
        for (int stage = 0; stage < ILx.NUM_PHOTOMASK_STAGES; stage++) {
            if (tier >= 2) for (int plStage = 0; plStage < 2; plStage++) {
                addPhotomask(type, tier, plStage, stage);
            } else {
                addPhotomask(type, tier, 0, stage);
            }
        }
        addPhotomaskRecipes(type, tier, 0);
        if (tier >= 2) addPhotomaskRecipes(type, tier, 1);
    }

    private void addWafers(int type, int tier) {
        if (type == MESFET_IDX) {
            for (int stage = 0; stage < ILx.NUM_WAFER_STAGES_MESFET; stage++) {
                addWafer(type, tier, stage);
            }
        } else {
            if (tier < 2) for (int stage = 0; stage < ILx.NUM_WAFER_STAGES_SINGLE; stage++) {
                addWafer(type, tier, stage);
            } else for (int stage = 0; stage < ILx.NUM_WAFER_STAGES_DOUBLE; stage++) {
                addWafer(type, tier, stage);
            }
        }
    }

    private void addPhotomaskRecipes(int type, int tier, int number) {
        long LUt = switch (type) {
            case 0 -> 32 * (long) Math.pow(4, tier);
            case MESFET_IDX -> 128;
            default -> 32 * (long) Math.pow(4, tier + 1);
        };
        int pcbTier = type == 0 ? tier + 3 : tier + 4;
        int lensColorIndex = switch (type) {
            case 0 -> DYE_INDEX_White;
            case 1 -> DYE_INDEX_Yellow;
            case 2 -> DYE_INDEX_Orange;
            case 3 -> DYE_INDEX_Red;
            case 4 -> DYE_INDEX_Green;
            case 5 -> DYE_INDEX_Blue;
            case 6 -> DYE_INDEX_Lime;
            default -> DYE_INDEX_Black;
        };

        // PL
        lens.addListener(event -> new OreDictListenerEvent_Names() { @Override public void addAllListeners() {
            addListener(DYE_OREDICTS_LENS[lensColorIndex] , lens ->
                RMx.Photolithography.addRecipeX(false, LUt, 2000,
                    ST.array(ILx.PCBs[pcbTier][0].get(0), ILx.Photomask_Raw.get(1), ST.amount(0, lens.mStack)),
                    MTx.DnqNovolacResist.liquid(U200, true), NF,
                    ILx.Photomasks[type][tier][number][0].get(1)
                )
            );
        }});
        // Development
        for (FluidStack developer : FL.array(MTx.NaOHSolution.liquid(U10, true), MTx.Na2CO3Solution.liquid(U10, true)))
            RM.Bath.addRecipe1(false, 0, 128, ILx.Photomasks[type][tier][number][0].get(1), developer, NF, ILx.Photomasks[type][tier][number][1].get(1));
        // Etching
        RM.Bath.addRecipe1(false, 0, 128, ILx.Photomasks[type][tier][number][1].get(1), MTx.ChromeEtch.liquid(3*U, true), MTx.CrNO3Solution.liquid(7*U2, false), ILx.Photomasks[type][tier][number][2].get(1));
        // Cleaning
        RM.Bath.addRecipe1(false, 0, 256, ILx.Photomasks[type][tier][number][2].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Photomasks[type][tier][number][3].get(1));
    }

    private void addPMOSWaferRecipes(int type) {
        int tier = 0;
        long LUt = type == 0 ? 32 : 128;
        RMx.Photolithography.addRecipe2(false, LUt, 128, oxidizedWafer.mat(MTx.NDopedSi, 1), ILx.Photomasks[type][tier][0][PM_FINISHED].get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafers[type][tier][0].get(1));
        for (FluidStack developer : FL.array(MTx.NaOHSolution.liquid(U10, true), MTx.Na2CO3Solution.liquid(U10, true)))
            RM.Bath.addRecipe1(false, 0, 128, ILx.Wafers[type][tier][0].get(1), developer, NF, ILx.Wafers[type][tier][1].get(1));
        // Wet etching
        RM.Bath.addRecipe1(false, 0, 128, ILx.Wafers[type][tier][1].get(1), FL.array(MT.HF.gas(12*U1000, true)), FL.array(MT.H2SiF6.liquid(9*U1000, false), MT.H2O.liquid(6*U1000, false)), ILx.Wafers[type][tier][2].get(1));
        // Post-growth doping using P-dopant
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][2].get(1), MTx.BF3.gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafers[type][tier][3].get(1));
        // Photoresist cleaning
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][3].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafers[type][tier][4].get(1));
        // Metallization using Al PVD
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][4].get(1), MT.Al.gas(U4, true), NF, ILx.Wafers[type][tier][5].get(1));
        // Metal etching
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][5].get(1), FL.array(MTx.AlEtch.liquid(13*U8, true)), FL.array(MTx.AlPO4Solution.liquid(11*U8, false), MT.H.gas(3*U, false)), ILx.Wafers[type][tier][6].get(1));
        // Dicing
        for (int i = 0; i < RMx.CuttingFluids.length; i++) if (RMx.CuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, RMx.CuttingMultiplier[i] * 64, ILx.Wafers[type][tier][6].get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.Wafers[type][tier][7].get(16));
        }
    }

    private void addNMOSWaferRecipes(int type) {
        int tier = 1;
        long LUt = type == 0 ? 128 : 512;
        RMx.Photolithography.addRecipe2(false, LUt, 128, oxidizedWafer.mat(MTx.PDopedSi, 1), ILx.Photomasks[type][tier][0][PM_FINISHED].get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafers[type][tier][0].get(1));
        for (FluidStack developer : FL.array(MTx.NaOHSolution.liquid(U10, true), MTx.Na2CO3Solution.liquid(U10, true)))
            RM.Bath.addRecipe1(false, 0, 128, ILx.Wafers[type][tier][0].get(1), developer, NF, ILx.Wafers[type][tier][1].get(1));
        // Plasma etching
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][1].get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafers[type][tier][2].get(1));
        // Post-growth doping using N-dopant
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][2].get(1), MTx.PH3 .gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafers[type][tier][3].get(1));
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][2].get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafers[type][tier][3].get(1));
        // Photoresist cleaning
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][3].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafers[type][tier][4].get(1));
        // Metallization using Cu Electroplating
        for (FluidStack water : FL.waters(750)) {
            RM.Electrolyzer.addRecipe1(true, 32, 16, ILx.Wafers[type][tier][4].get(1), FL.array(MT.BlueVitriol.liquid(6*U4, true), water), FL.array(MT.H2SO4.liquid(7*U4, false), MT.O.gas(U4, false)), ILx.Wafers[type][tier][5].get(1));
        }
        // Metal etching
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][5].get(1), MTx.FeCl3Solution.liquid(17*U8, true), MTx.CuFeClSolution.liquid(18*U8, false), ILx.Wafers[type][tier][6].get(1));
        // Dicing
        for (int i = 0; i < RMx.CuttingFluids.length; i++) if (RMx.CuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, RMx.CuttingMultiplier[i] * 64, ILx.Wafers[type][tier][6].get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.Wafers[type][tier][7].get(16));
        }
    }

    private void addCMOSWaferRecipes(int type) {
        int tier = 2;
        long LUt = type == 0 ? 512 : 2048;
        // Stage I
        RMx.Photolithography.addRecipe2(false, LUt, 128, oxidizedWafer.mat(MTx.PDopedSiGe, 1), ILx.Photomasks[type][tier][0][PM_FINISHED].get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafers[type][tier][0].get(1));
        for (FluidStack developer : FL.array(MTx.NaOHSolution.liquid(U10, true), MTx.Na2CO3Solution.liquid(U10, true)))
            RM.Bath.addRecipe1(false, 0, 128, ILx.Wafers[type][tier][0].get(1), developer, NF, ILx.Wafers[type][tier][1].get(1));
        // Plasma etching
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][1].get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafers[type][tier][2].get(1));
        // Post-growth doping of N-wells
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][2].get(1), MTx.PH3 .gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafers[type][tier][3].get(1));
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][2].get(1), MTx.AsH3.gas(32*U1000, true), MTx.SiH4.gas(30*U1000, false), ILx.Wafers[type][tier][3].get(1));
        // Photoresist cleaning
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][3].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafers[type][tier][4].get(1));
        // Oxide layer
        RM.Roasting.addRecipe1(true, 16, 256, ILx.Wafers[type][tier][4].get(1), MT.O.gas(U10, true), NF, ILx.Wafers[type][tier][5].get(1));

        // Stage II
        RMx.Photolithography.addRecipe2(false, LUt, 128, ILx.Wafers[type][tier][5].get(1), ILx.Photomasks[type][tier][1][PM_FINISHED].get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafers[type][tier][6].get(1));
        for (FluidStack developer : FL.array(MTx.NaOHSolution.liquid(U10, true), MTx.Na2CO3Solution.liquid(U10, true)))
            RM.Bath.addRecipe1(false, 0, 128, ILx.Wafers[type][tier][6].get(1), developer, NF, ILx.Wafers[type][tier][7].get(1));
        // Plasma etching
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][7].get(1), FL.array(MTx.CF4.plasma(15*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.CO2.gas(9*U1000, false)), ILx.Wafers[type][tier][8].get(1));
        // Post-growth doping of P-Junctions
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][8].get(1), MTx.BF3.gas(32*U1000, true), MTx.SiF4.gas(30*U1000, false), ILx.Wafers[type][tier][9].get(1));
        // Photoresist cleaning
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][9].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafers[type][tier][10].get(1));

        // Metallization using Ru Electroplating
        RM.Electrolyzer.addRecipe1(true, 64, 32, ILx.Wafers[type][tier][10].get(1), FL.array(MTx.RuElectrolyte.liquid(20*U8, true)), FL.array(MT.NH3.gas(4*U8, false), MT.Cl.gas(8*U8, false), MT.H2O.liquid(6*U8, false)), ILx.Wafers[type][tier][11].get(1));
        // Metal etching
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][11].get(1), MTx.Ozone.gas(U3, true), MTx.RuO4.gas(5*U8, false), ILx.Wafers[type][tier][12].get(1));
        // Dicing
        for (int i = 0; i < RMx.CuttingFluids.length; i++) if (RMx.CuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, RMx.CuttingMultiplier[i] * 64, ILx.Wafers[type][tier][12].get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.Wafers[type][tier][13].get(16));
        }
    }

    private void addMESFETWaferRecipes() {
        int tier = 0, type = MESFET_IDX;
        // PL
        RMx.Photolithography.addRecipe2(false, 32  , 128, ILx.Wafer_GaAs_SiN_layered.get(1), ILx.Photomasks[type][tier][0][PM_FINISHED].get(0), MTx.DnqNovolacResist.liquid(U200, true), NF, ILx.Wafers[type][tier][0].get(1));
        for (FluidStack developer : FL.array(MTx.NaOHSolution.liquid(U10, true), MTx.Na2CO3Solution.liquid(U10, true)))
            RM.Bath.addRecipe1(false, 0, 128, ILx.Wafers[type][tier][0].get(1), developer, NF, ILx.Wafers[type][tier][1].get(1));
        // Dry etching
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][1].get(1), FL.array(MTx.NF3.plasma(16*U1000, true)), FL.array(MTx.SiF4.gas(15*U1000, false), MT.N.gas(8*U1000, false)), ILx.Wafers[type][tier][2].get(1));
        // Doping with Silicon
        RMx.IonBombardment.addRecipe1(true, 16, 128, ILx.Wafers[type][tier][2].get(1), MTx.SiF4.gas(30*U1000, true), MTx.AsF3.liquid(32*U1000, false), ILx.Wafers[type][tier][3].get(1));
        // Metal 1 layer of electroplated Gold
        for (FluidStack water : FL.waters(1000)) {
            RM.Electrolyzer.addRecipe1(true, 32, 22, ILx.Wafers[type][tier][3].get(1), FL.array(MTx.NaAuC2N2.liquid(3*U2, true), FL.mul(water, 3, 2, true)), FL.array(MTx.HCN.gas(3*U2, false), MT.H.gas(U4, false), MT.O.gas(U4, false)), ILx.Wafers[type][tier][4].get(1), dustSmall.mat(MT.NaOH, 3));
        }
        // Metal 2 layer using Al PVD
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][4].get(1), MT.Al.gas(U4, true), NF, ILx.Wafers[type][tier][5].get(1));
        // Photoresist cleaning
        RM.Bath.addRecipe1(true, 0, 128, ILx.Wafers[type][tier][5].get(1), MTx.PiranhaEtch.liquid(U10, true), NF, ILx.Wafers[type][tier][6].get(1));
        // Dicing
        for (int i = 0; i < RMx.CuttingFluids.length; i++) if (RMx.CuttingFluids[i] != null) {
            RM.Cutter.addRecipe1(true, 16, RMx.CuttingMultiplier[i] * 64, ILx.Wafers[type][tier][6].get(1), FL.mul(RMx.CuttingFluids[i], RMx.CuttingMultiplier[i] * 16, 1000, true), NF, ILx.Wafers[type][tier][7].get(64));
        }
    }

    protected ItemStack addItemWithIcon(int id, String english, String toolTip, Object... randomData) {
        int idx = getIconIndex(id);
        enabledIcons.set(idx);
        return addItem(id, english, toolTip, randomData);
    }

    protected int getIconIndex(int meta) {
        if (meta < 10000) {
            // Photomask: type * 1000 + tier * 100 + plStage * 10 + stage + 1
            int stage = meta % 10;
            if (stage > 3) {
                // Finished photomask
                return meta % 1000;
            } else {
                return stage;
            }
        } else if (meta < 20000) {
            // Wafer: 10000 + type * 1000 + tier * 100 + stage
            //TODO different CPU die textures
            return 1000 + meta % 1000;
        } else {
            return meta;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        for (short index = 0; index < enabledIcons.length(); index++) if (enabledIcons.get(index)) {
            mIconList[index][0] = iconRegister.registerIcon(mModID + ":" + getUnlocalizedName() + "/" + index);
        }
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        short meta = ST.meta_(stack);
        return getIconFromDamage(meta);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        return getIconIndex(stack);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usedStack, int useRemaining) {
        return getIcon(stack, renderPass);
    }

    @Override
    public IIcon getIconFromDamage(int metaData) {
        int index = getIconIndex(metaData);
        if (!enabledIcons.get(index)) return Textures.ItemIcons.RENDERING_ERROR.getIcon(0);
        return mIconList[index][0];
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int metaData, int renderPass) {
        return getIconFromDamage(metaData);
    }
}
