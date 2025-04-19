package org.altadoon.gt6x.features.metallurgy;

import gregapi.code.ICondition;
import gregapi.data.*;
import gregapi.item.prefixitem.PrefixItem;
import gregapi.oredict.*;
import gregapi.oredict.configurations.IOreDictConfigurationComponent;
import gregapi.oredict.configurations.OreDictConfigurationComponent;
import gregapi.recipes.Recipe;
import gregapi.tileentity.machines.MultiTileEntityBasicMachine;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import gregapi.worldgen.WorldgenObject;
import gregapi.worldgen.WorldgenOresLarge;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.common.OMx;
import org.altadoon.gt6x.common.RMx;
import org.altadoon.gt6x.common.utils.Code;
import org.altadoon.gt6x.features.GT6XFeature;
import org.altadoon.gt6x.features.electronics.MultiItemsElectronics;
import org.altadoon.gt6x.features.metallurgy.multiblocks.*;
import org.altadoon.gt6x.features.metallurgy.utils.RecipeMapHandlerPrefixSintering;
import org.altadoon.gt6x.features.metallurgy.utils.RecyclingProcessingSublimation;

import java.util.*;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.data.TD.Prefix.*;
import static gregapi.data.TD.Processing.BLACKLISTED_SMELTER;
import static gregapi.data.TD.Processing.EXTRUDER;
import static gregapi.oredict.OreDictMaterialCondition.fullforge;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Metallurgy extends GT6XFeature {
    public static final String FEATURE_NAME = "Metallurgy";

    public static OreDictPrefix sinter = null;

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void preInit() {
        createPrefixes();
        changeMaterialProperties();
        changeByProducts();
    }

    @Override
    public void afterGt6PreInit() {
        changeAlloySmeltingRecipes();
        OreDictManager.INSTANCE.addListener(new RecyclingProcessingSublimation());
    }

    @Override
    public void init() {
        addMTEs();
    }

    @Override
    public void beforeGt6PostInit() {
        addOverrideRecipes();
    }

    @Override
    public void postInit() {
        addRecipes();
        changePrefixNames();
        overrideWorldgen();
    }

    @Override
    public void afterGt6PostInit() {
        changeRecipes();
    }

    private void createPrefixes() {
        sinter = OreDictPrefix.createPrefix("sinter")
            .setCategoryName("Sinters")
            .setLocalItemName("", " Sinter")
            .setCondition(ICondition.FALSE)
            .setMaterialStats(-1, U)
            .forceItemGeneration(MT.Fe2O3, MT.OREMATS.Magnetite, MTx.FeO, MT.OREMATS.Garnierite, MT.OREMATS.Cassiterite, MT.OREMATS.Chromite, MT.OREMATS.Malachite, MTx.PbO, MTx.ZnO, MT.MnO2, MTx.MnO, MTx.Co3O4, MTx.CoO, MT.SiO2, MTx.Sb2O3)
            .add(TOOLTIP_MATERIAL, UNIFICATABLE);
        PrefixItem item = new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.sinter" , sinter); if (COMPAT_FR != null) COMPAT_FR.addToBackpacks("miner", ST.make(item, 1, W));
    }

    private void changeMaterialProperties() {
        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.PigIron, MT.OREMATS.Cassiterite, MT.OREMATS.Garnierite, MT.OREMATS.Cobaltite, MT.OREMATS.Barite, MT.OREMATS.Celestine, MT.MnO2 }) {
            mat.setSmelting(mat, U);
        }

        MT.PigIron.setPulver(MT.PigIron, U);

        for (OreDictMaterial removeElectro : new OreDictMaterial[] { MT.Olivine, MT.OREMATS.Garnierite, MT.OREMATS.Smithsonite, MT.OREMATS.Cassiterite, MT.OREMATS.Wollastonite, MT.Phosphorite, MT.Apatite, MT.OREMATS.Sperrylite, MT.OREMATS.Malachite, MT.Azurite, MT.OREMATS.Barite, MT.OREMATS.Celestine, MT.OREMATS.Pollucite, MT.OREMATS.Lepidolite }) {
            removeElectro.remove(TD.Processing.ELECTROLYSER);
        }
        for (OreDictMaterial removeCent : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusWhite, MT.PhosphorusRed }) {
            removeCent.remove(TD.Processing.CENTRIFUGE);
        }

        // to make smelting bloom in crucibles easier
        for (OreDictMaterial magnetite : new OreDictMaterial[] { MT.OREMATS.Magnetite, MT.OREMATS.BasalticMineralSand, MT.OREMATS.GraniticMineralSand })
            magnetite.heat(MT.Fe2O3);

        // to prevent smelting As in a smelter to liquid (it should sublime)
        MT.As.put(BLACKLISTED_SMELTER);
        MT.OREMATS.Realgar.put(BLACKLISTED_SMELTER);
    }

    private void changeByProducts() {
        MT.OREMATS.Cobaltite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Co.mID);
        MT.OREMATS.Stibnite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Sb.mID);
        MT.OREMATS.Sphalerite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Zn.mID);
        MT.OREMATS.Garnierite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Ni.mID);
        MT.OREMATS.Galena.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Pb.mID);
        MT.Lignite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Ge.mID);

        // Oxide byproducts
        for (OreDictMaterial mat : new OreDictMaterial[] { MT.OREMATS.Stolzite, MT.OREMATS.Pinalite, MT.OREMATS.Pitchblende, MT.OREMATS.Uraninite }) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (byproduct.mID == MT.Ni.mID) {
                    it.set(MT.OREMATS.Garnierite);
                } else if (byproduct.mID == MT.Pb.mID) {
                    it.set(MTx.Massicot);
                } else if (byproduct.mID == MT.Zn.mID) {
                    it.set(MTx.Zincite);
                }
            }
        }

        // Sulfides e.a.
        for (OreDictMaterial mat : new OreDictMaterial[] {MT.OREMATS.BrownLimonite, MT.OREMATS.Sperrylite, MT.OREMATS.Tetrahedrite, MT.Cu, MT.OREMATS.Cooperite, MT.Cu, MT.Ga, MT.Ag, MT.Au, MT.Pt, MT.Se, MT.OREMATS.YellowLimonite, MT.OREMATS.Chalcopyrite, MT.OREMATS.Cobaltite, MT.OREMATS.Sphalerite, MT.OREMATS.Stannite, MT.OREMATS.Kesterite, MT.Alduorite, MT.Ignatius, MT.OREMATS.Celestine, MT.OREMATS.Lepidolite, MT.OREMATS.Pollucite }) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (byproduct.mID == MT.As.mID) {
                    it.set(MT.OREMATS.Realgar);
                } else if (byproduct.mID == MT.Ni.mID) {
                    it.set(MT.OREMATS.Pentlandite);
                } else if (byproduct.mID == MT.Pb.mID) {
                    it.set(MT.OREMATS.Galena);
                } else if (byproduct.mID == MT.Zn.mID) {
                    it.set(MT.OREMATS.Sphalerite);
                } else if (byproduct.mID == MT.Sb.mID) {
                    it.set(MT.OREMATS.Stibnite);
                } else if (byproduct.mID == MT.Co.mID) {
                    it.set(MT.OREMATS.Cobaltite);
                } else if (byproduct.mID == MT.Cd.mID ||
                           byproduct.mID == MT.Se.mID ||
                           byproduct.mID == MT.Ga.mID ||
                           byproduct.mID == MT.In.mID ||
                           byproduct.mID == MT.Ge.mID ||
                           byproduct.mID == MT.Rb.mID ||
                           byproduct.mID == MT.Cs.mID
                ) {
                    it.remove();
                }
            }
        }

        MT.OREMATS.Barite.addOreByProducts(MT.OREMATS.Celestine, MT.Gypsum);
        MT.OREMATS.Celestine.addOreByProducts(MT.Gypsum);
    }

    private void removeAlloySmeltingRecipe(OreDictMaterial mat) {
        mat.remove(TD.Processing.CRUCIBLE_ALLOY);

        for (IOreDictConfigurationComponent comp : mat.mAlloyCreationRecipes) {
            for (OreDictMaterialStack tMaterial : comp.getUndividedComponents()) {
                tMaterial.mMaterial.mAlloyComponentReferences.remove(mat);
            }
        }
        mat.mAlloyCreationRecipes.clear();
    }

    private void changeAlloySmeltingRecipes() {
        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.Si, MT.Fe, MT.WroughtIron, MT.Steel, MT.MeteoricSteel, MT.StainlessSteel, MT.TungstenCarbide, MT.Ta4HfC5, MT.SiC, MT.Vibramantium, MT.YttriumBariumCuprate }) {
            removeAlloySmeltingRecipe(mat);
        }

        MTx.Alusil.addAlloyingRecipe(new OreDictConfigurationComponent(16, OM.stack(MT.Al, 6*U), OM.stack(MT.Si, 3*U), OM.stack(MT.AluminiumBrass, 4*U), OM.stack(MT.Magnalium, 3*U)));
        if (!MT.AluminiumAlloy.mHidden) {
            MTx.Alusil.addAlloyingRecipe(new OreDictConfigurationComponent(80, OM.stack(MT.AluminiumAlloy, 55*U), OM.stack(MT.Si, 124*U9), OM.stack(MT.Cu, 5*U), OM.stack(MT.Mg, 5*U)));
            MTx.Alusil.addAlloyingRecipe(new OreDictConfigurationComponent(80, OM.stack(MT.AluminiumAlloy, 30*U), OM.stack(MT.AluminiumBrass, 20*U), OM.stack(MT.Magnalium, 15*U), OM.stack(MT.Si, 129*U9)));
        }
        MTx.Hastelloy.addAlloyingRecipe(new OreDictConfigurationComponent(45, OM.stack(MT.Nichrome, 30*U), OM.stack(MT.Ultimet, 9*U), OM.stack(MT.Mo, 4*U), OM.stack(MT.Cr, 2*U)));
        MT .Ultimet  .addAlloyingRecipe(new OreDictConfigurationComponent(45, OM.stack(MT.Co, 24*U), OM.stack(MTx.Hastelloy, 9*U), OM.stack(MT.Cr, 8*U), OM.stack(MT.Mo, 4*U)));
        MTx.Ti6Al4V  .addAlloyingRecipe(new OreDictConfigurationComponent(28*7, OM.stack(MT.Ti, 159*U), OM.stack(MT.TitaniumAluminide, 9*U), OM.stack(MT.V, 7*U)));
        if (!MT.AluminiumAlloy.mHidden)
            MTx.A6061    .addAlloyingRecipe(new OreDictConfigurationComponent(100, OM.stack(MT.Al, 46*U), OM.stack(MT.AluminiumAlloy, 45*U), OM.stack(MT.AluminiumBrass, 4*U), OM.stack(MT.Magnalium, 3*U), OM.stack(MT.Cr, U)));
        MTx.A6061    .addAlloyingRecipe(new OreDictConfigurationComponent(100, OM.stack(MT.Al, 91*U), OM.stack(MT.Si, U), OM.stack(MT.AluminiumBrass, 4*U), OM.stack(MT.Magnalium, 3*U), OM.stack(MT.Cr, U)));
        MTx.A6061    .addAlloyingRecipe(new OreDictConfigurationComponent(300, OM.stack(MT.Al, 233*U), OM.stack(MTx.Alusil, 45*U), OM.stack(MT.AluminiumBrass, 8*U), OM.stack(MT.Magnalium, 6*U), OM.stack(MT.Cr, 3*U)));

        MT.Sn.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.OREMATS.Cassiterite, 4*U), OM.stack(MT.C, 4*U)));
        MT.Pb.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MTx.PbO, 4*U), OM.stack(MT.C, 2*U)));
        MT.Zn.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MTx.ZnO, 4*U), OM.stack(MT.C, 2*U)));

        // Bloomery
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MTx.FeO                       , 4 *U), OM.stack(MT.C, 4 *U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.Fe2O3                      , 5 *U), OM.stack(MT.C, 4 *U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent(9, OM.stack(MT.OREMATS.Magnetite          , 14*U), OM.stack(MT.C, 12*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent(9, OM.stack(MT.OREMATS.BasalticMineralSand, 14*U), OM.stack(MT.C, 12*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent(9, OM.stack(MT.OREMATS.GraniticMineralSand, 14*U), OM.stack(MT.C, 12*U)));

        // Carburisation of Meteoric Iron
        MTx.MeteoricCementite.addAlloyingRecipe(new OreDictConfigurationComponent(1, OM.stack(MT.MeteoricIron, U), OM.stack(MT.C, U3)));

        // Pig Iron to Cast Iron without air
        MT.IronCast.addAlloyingRecipe(new OreDictConfigurationComponent(8 , OM.stack(MT.PigIron, 5*U), OM.stack(MT.Fe, 3*U)));
        MT.IronCast.addAlloyingRecipe(new OreDictConfigurationComponent(8 , OM.stack(MT.PigIron, 5*U), OM.stack(MT.WroughtIron, 3*U)));
//        MT.IronCast.addAlloyingRecipe(new OreDictConfigurationComponent(38, OM.stack(MT.PigIron, 23*U), OM.stack(MT.Steel, 15*U)));

        // Cast Iron to Wrought Iron
//        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(9 , OM.stack(MT.IronCast, 8*U), OM.stack(MTx.FeO, 2*U)));
//        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(26, OM.stack(MT.IronCast, 24*U), OM.stack(MT.Fe2O3, 5*U)));
//        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(35, OM.stack(MT.IronCast, 32*U), OM.stack(MT.OREMATS.Magnetite, 7*U)));

        // Steel to Wrought Iron
        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(101, OM.stack(MT.Steel, 100*U), OM.stack(MTx.FeO, 2*U)));
        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(302, OM.stack(MT.Steel, 300*U), OM.stack(MT.Fe2O3, 5*U)));
        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(403, OM.stack(MT.Steel, 400*U), OM.stack(MT.OREMATS.Magnetite, 7*U)));

        // Crucible steel (damascus)
        MT.DamascusSteel.addAlloyingRecipe(new OreDictConfigurationComponent(100, OM.stack(MT.IronCast, 8*U), OM.stack(MT.WroughtIron, 92*U)));
        MT.DamascusSteel.addAlloyingRecipe(new OreDictConfigurationComponent(100, OM.stack(MT.C, U), OM.stack(MT.WroughtIron, 100*U)));

        // Steel Alloys
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.Steel,16*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Cr, 4*U), OM.stack(MT.Mn, 4*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.Steel,24*U), OM.stack(MT.Nichrome, 5*U), OM.stack(MT.Cr, 3*U), OM.stack(MT.Mn, 4*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.Steel,14*U), OM.stack(MTx.FeCr2, 6*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Mn, 4*U)));

        MT.Invar.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.Steel,2*U), OM.stack(MT.Ni, U)));
        MT.TinAlloy.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.Steel, U), OM.stack(MT.Sn, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.Steel, U), OM.stack(MT.Al, U), OM.stack(MT.Cr, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(6, OM.stack(MT.Steel, U), OM.stack(MT.Al, 2*U), OM.stack(MTx.FeCr2, 3*U)));
        MT.Angmallen.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.Steel, U), OM.stack(MT.Au, U)));

        // Other alloys
        MT.YttriumBariumCuprate.addAlloyingRecipe(new OreDictConfigurationComponent(20, OM.stack(MT.AnnealedCopper, 6*U), OM.stack(MTx.BaO, 4*U), OM.stack(MTx.Y2O3, 2*U)));
        MT.YttriumBariumCuprate.addAlloyingRecipe(new OreDictConfigurationComponent(20, OM.stack(MT.Cu            , 6*U), OM.stack(MTx.BaO, 4*U), OM.stack(MTx.Y2O3, 2*U)));

        MTx.BaS.addAlloyingRecipe(new OreDictConfigurationComponent(1, OM.stack(MT.OREMATS.Barite, 3*U), OM.stack(MT.C, U)));
        MTx.SrS.addAlloyingRecipe(new OreDictConfigurationComponent(1, OM.stack(MT.OREMATS.Celestine, 3*U), OM.stack(MT.C, U)));
    }

    private void changePrefixNames() {
        LH.add("oredict." + ingot.dat(MTx.HBI) + ".name", MTx.HBI.getLocal());
    }

    private void addMTEs() {
        OreDictMaterial mat;
        mat = MT.WroughtIron;    MTEx.gt6xMTEReg.add("Blast Furnace Part ("+mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFPartIron   .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', plate.dat(mat), 'F', MTEx.gt6MTEReg.getItem(18000)); // fire bricks
                                 MTEx.gt6xMTEReg.add("Blast Furnace ("     +mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFIron       .get(), 17101, MultiTileEntityBlastFurnace  .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnace"     , NBT_DESIGN, MTEx.IDs.BFPartIron .get(), NBT_INPUT, 32, NBT_INPUT_MIN, 8 , NBT_INPUT_MAX, 32, NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, RMx.BlastFurnace, NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_L|SBIT_R|SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', plateCurved.dat(mat), 'R', stickLong.dat(mat), 'F', MTEx.gt6xMTEReg.getItem(MTEx.IDs.BFPartIron.get()));
        mat = MT.Steel;          MTEx.gt6xMTEReg.add("Blast Furnace Part ("+mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFPartSteel  .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', plate.dat(mat), 'F', MTEx.gt6MTEReg.getItem(18000)); // fire bricks
                                 MTEx.gt6xMTEReg.add("Blast Furnace ("     +mat.getLocal()+")", "Multiblock Machines", MTEx.IDs.BFSteel      .get(), 17101, MultiTileEntityBlastFurnace  .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "blastfurnace"     , NBT_DESIGN, MTEx.IDs.BFPartSteel.get(), NBT_INPUT, 32, NBT_INPUT_MIN, 8 , NBT_INPUT_MAX, 32, NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, RMx.BlastFurnace, NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT, NBT_TANK_SIDE_OUT, SBIT_L|SBIT_R|SBIT_U, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', plateCurved.dat(mat), 'R', stickLong.dat(mat), 'F', MTEx.gt6xMTEReg.getItem(MTEx.IDs.BFPartSteel.get()));

        mat = MT.Al2O3;          MTEx.gt6xMTEReg.add("Cowper Stove"                           , "Multiblock Machines", MTEx.IDs.CowperStove  .get(), 17101, MultiTileEntityCowperStove   .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "cowperstove"      , NBT_INPUT, 32, NBT_INPUT_MIN, 16, NBT_INPUT_MAX, 64, NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.CowperStove, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_PARALLEL, 64, NBT_PARALLEL_DURATION, true), "IPI", "PSP", "wIh", 'I', ingot.dat(mat), 'P', pipeMedium.dat(MT.StainlessSteel), 'S', MTEx.gt6xMTEReg.getItem(MTEx.IDs.AluminaCheckerBricks.get()));

        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        mat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering1   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_INPUT,   32, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateDouble   .dat(ANY.Cu), 'P', plate.dat(MT.Ceramic), 'B', Blocks.brick_block, 'U', MTEx.gt6MTEReg.getItem(1005));
        mat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering2   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  4.0F, NBT_RESISTANCE,  4.0F, NBT_INPUT,  128, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wPh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateTriple   .dat(ANY.Cu), 'P', plateTriple.dat(MT.Ta     ), 'B', Blocks.brick_block);
        mat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering3   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  9.0F, NBT_RESISTANCE,  9.0F, NBT_INPUT,  512, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wPh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateQuadruple.dat(ANY.Cu), 'P', plateTriple.dat(MT.W      ), 'B', Blocks.brick_block);
        mat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Sintering Oven ("+mat.getLocal()+")"    , "Basic Machines"     , MTEx.IDs.Sintering4   .get(), 20001, aClass                             , mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.5F, NBT_RESISTANCE, 12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.Sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wPh", "PMP", "BCB", 'M', casingMachineDouble.dat(mat), 'C', plateQuintuple.dat(ANY.Cu), 'P', plateTriple.dat(MT.Ta4HfC5), 'B', Blocks.brick_block);

        mat = MT.SiC;            MTEx.gt6xMTEReg.add("Shaft Furnace"                          , "Multiblock Machines", MTEx.IDs.ShaftFurnace .get(), 17101, MultiTileEntityShaftFurnace  .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  6.0F, NBT_RESISTANCE,  6.0F, NBT_TEXTURE, "shaftfurnace"     , NBT_INPUT, 64 , NBT_INPUT_MIN, 64 , NBT_INPUT_MAX, 1024 , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.DirectReduction, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true), "IPI", "PSP", "wIh", 'I', plate.dat(mat), 'P', pipeMedium.dat(MT.StainlessSteel), 'S', MTEx.gt6xMTEReg.getItem(MTEx.IDs.SiCBricks.get()));

        mat = MT.Graphite;       MTEx.gt6xMTEReg.add("Graphite Electrodes"                    , "Multiblock Machines", MTEx.IDs.EAFElectrodes.get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "eafelectrodes"    , NBT_DESIGNS, 1), " h ", "RRR", "   ", 'R', stick.dat(mat));

        mat = MTx.MgOC;          MTEx.gt6xMTEReg.add("Electric Arc Furnace"                   , "Multiblock Machines", MTEx.IDs.EAF          .get(), 17101, MultiTileEntityEAF           .class, mat.mToolQuality, 16, MTEx.StoneBlock  , UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F,                                   NBT_INPUT, 512, NBT_INPUT_MIN, 512, NBT_INPUT_MAX, 16384, NBT_ENERGY_ACCEPTED, TD.Energy.EU, NBT_RECIPEMAP, RMx.EAF.fakeRecipes, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_GASPROOF, true, NBT_ACIDPROOF, true), "TGW", "CSC", "PhP", 'C', OD_CIRCUITS[3], 'P', Blocks.piston, 'S', MTEx.gt6xMTEReg.getItem(MTEx.IDs.MgOCWall.get()), 'T', MTEx.gt6MTEReg.getItem(31000), 'G', MultiItemsElectronics.SCREEN_NAMES[0], 'W', MTEx.gt6MTEReg.getItem(31012));

        mat = MT.Steel;          MTEx.gt6xMTEReg.add("Steel-lined MgO-C Wall"                 , "Multiblock Machines", MTEx.IDs.BOFWall      .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.0F, NBT_RESISTANCE, 12.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 3), "hP ", "PF ", "   ", 'P', plate.dat(mat), 'F', MTEx.gt6xMTEReg.getItem(MTEx.IDs.MgOCWall.get()));
                                 MTEx.gt6xMTEReg.add("Basic Oxygen Furnace"                   , "Multiblock Machines", MTEx.IDs.BOF          .get(), 17101, MultiTileEntityBOF           .class, mat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS, 12.0F, NBT_RESISTANCE, 12.0F,                                   NBT_INPUT,  32, NBT_INPUT_MIN,  1, NBT_INPUT_MAX, 1024 , NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, RMx.BOF.fakeRecipes, NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT, NBT_CHEAP_OVERCLOCKING, true, NBT_GASPROOF, true, NBT_ACIDPROOF, true, NBT_TEMPERATURE+".max", MTx.MgOC.mMeltingPoint), "P P", "U U", "hWw", 'P', plate.dat(mat), 'W', MTEx.gt6xMTEReg.getItem(MTEx.IDs.BOFWall.get()), 'U', pipeMedium.dat(mat));
        mat = MT.StainlessSteel; MTEx.gt6xMTEReg.add("Oxygen Lance"                           , "Multiblock Machines", MTEx.IDs.BOFLance     .get(), 17101, MultiTileEntityMultiBlockPart.class, mat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, mat, NBT_HARDNESS,  8.0F, NBT_RESISTANCE,  8.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 2), " S ", "wP ", " P ", 'P', pipeTiny.dat(mat), 'S', plate.dat(mat));

        // Allow to run BOF recipes in EAF
        RMx.BOF.fakeRecipes.mRecipeMachineList.addAll(RMx.EAF.fakeRecipes.mRecipeMachineList);
    }

    private static void mix(String aInput1, int aIn1Amount, String aInput2, int aIn2Amount, String aOutput, int aOutAmount) {
        RM.Mixer.addRecipe1(T, 16, Math.max(aOutAmount, aIn1Amount+aIn2Amount), ST.tag(2), FL.array(FL.make_(aInput1, aIn1Amount), FL.make_(aInput2, aIn2Amount)), FL.make_(aOutput, aOutAmount), ZL_IS);
    }
    private static void mix(String aInput1, int aIn1Amount, String aInput2, int aIn2Amount, String aInput3, int aIn3Amount, String aOutput, int aOutAmount) {
        RM.Mixer.addRecipe1(T, 16, Math.max(aOutAmount, aIn1Amount+aIn2Amount+aIn3Amount), ST.tag(3), FL.array(FL.make_(aInput1, aIn1Amount), FL.make_(aInput2, aIn2Amount), FL.make_(aInput3, aIn3Amount)), FL.make_(aOutput, aOutAmount), ZL_IS);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ICondition<OreDictMaterial>
            lowHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmax(3300), fullforge()),
            highHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmin(3300), fullforge());

    private void addRecipes() {
        // slag melts @ 1540
        OreDictMaterialStack[][] smeltLiquidSlag = new OreDictMaterialStack[][] {
                {new OreDictMaterialStack(MT.Fe2O3, 5), new OreDictMaterialStack(MT.PigIron, 2)},
                {new OreDictMaterialStack(MT.OREMATS.Magnetite, 7), new OreDictMaterialStack(MT.PigIron, 3)},
                {new OreDictMaterialStack(MTx.FeO, 2), new OreDictMaterialStack(MT.PigIron, 1)},
                {new OreDictMaterialStack(MT.OREMATS.Chromite, 7), new OreDictMaterialStack(MTx.FeCr2, 3)},
        };
        OreDictMaterialStack[][] smeltSolidSlag = new OreDictMaterialStack[][] {
                {new OreDictMaterialStack(MT.OREMATS.Garnierite, 1), new OreDictMaterialStack(MT.Ni, 1)},
                {new OreDictMaterialStack(MT.OREMATS.Cassiterite, 1), new OreDictMaterialStack(MT.Sn, 1)},
                {new OreDictMaterialStack(MT.OREMATS.Malachite, 5), new OreDictMaterialStack(MT.Cu, 1)},
                {new OreDictMaterialStack(MTx.PbO, 1), new OreDictMaterialStack(MT.Pb, 1)},
                {new OreDictMaterialStack(MTx.MnO, 1), new OreDictMaterialStack(MT.Mn, 1)},
                {new OreDictMaterialStack(MT.MnO2, 1), new OreDictMaterialStack(MT.Mn, 1)},
                {new OreDictMaterialStack(MTx.CoO, 2), new OreDictMaterialStack(MT.Co, 1)},
                {new OreDictMaterialStack(MTx.Co3O4, 7), new OreDictMaterialStack(MT.Co, 3)},
                {new OreDictMaterialStack(MTx.Sb2O3, 5), new OreDictMaterialStack(MT.Sb, 2)}
        };

        FluidStack blast = MTx.HotBlast.gas(U1000, true);
        RMx.CowperStove.addRecipe0(false, 16, 128, FL.Air.make(1000), FL.mul(blast, 1000), ZL_IS);

        for (OreDictMaterialStack[] stacks : smeltLiquidSlag) {
            long mult = stacks[1].mAmount % 2 == 0 ? 1 : 2;

            for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.CoalCoke, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.Charcoal, 2), dust.mat(MT.C, 1), gem.mat(MT.PetCoke, 1), gem.mat(MT.CoalCoke, 1), gem.mat(MT.LigniteCoke, 3), gem.mat(MT.Charcoal, 2))) {
                RMx.Sintering.addRecipeX(true, 16, 32 * mult, ST.array(ST.tag(3), dust.mat(stacks[0].mMaterial, stacks[0].mAmount), ST.mul(mult, coal), dust.mat(MT.CaCO3, mult)), sinter.mat(stacks[0].mMaterial, stacks[0].mAmount));

                for (OreDictMaterial flux : new OreDictMaterial[] {MT.CaCO3, MT.STONES.Limestone, MT.Chalk}) {
                    // ore dusts (least efficent)
                    RMx.BlastFurnace.addRecipeX(true, 8, 512 * mult,
                            ST.array(dust.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(4 * mult, coal), dust.mat(flux, mult)),
                            FL.Air.make(4 * 2500 * mult),
                            FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.Slag.liquid(mult * U, false), MTx.BlastFurnaceGas.gas(mult * 4 * 5 * U, false))
                    );

                    // ore dusts with blast
                    RMx.BlastFurnace.addRecipeX(true, 8, 256 * mult,
                            ST.array(dust.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(3 * mult, coal), dust.mat(flux, mult)),
                            FL.mul(blast, 3 * 2500 * mult),
                            FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.Slag.liquid(mult * U, false), MTx.BlastFurnaceGas.gas(mult * 3 * 5 * U, false))
                    );
                }
                // sinters
                RMx.BlastFurnace.addRecipeX(true, 8, 256 * mult,
                        ST.array(sinter.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(2 * mult, coal)),
                        FL.Air.make(3 * 2500 * mult),
                        FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.Slag.liquid(mult * U, false), MTx.BlastFurnaceGas.gas(mult*3*5*U, false))
                );
                // sinters with blast (most efficient)
                RMx.BlastFurnace.addRecipeX(true, 8, 128 * mult,
                        ST.array(sinter.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(mult, coal)),
                        FL.mul(blast, 2 * 2500 * mult),
                        FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.Slag.liquid(mult * U, false), MTx.BlastFurnaceGas.gas(mult*2*5*U, false))
                );
            }
        }
        for (OreDictMaterialStack[] stacks : smeltSolidSlag) {
            long mult = stacks[1].mAmount % 2 == 0 ? 1 : 2;

            for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.CoalCoke, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.Charcoal, 2), dust.mat(MT.C, 1), gem.mat(MT.PetCoke, 1), gem.mat(MT.CoalCoke, 1), gem.mat(MT.LigniteCoke, 3), gem.mat(MT.Charcoal, 2))) {
                RMx.Sintering.addRecipeX(true, 16, 32 * mult, ST.array(ST.tag(3), dust.mat(stacks[0].mMaterial, stacks[0].mAmount), ST.mul(mult, coal), dust.mat(MT.CaCO3, mult)), sinter.mat(stacks[0].mMaterial, stacks[0].mAmount));

                for (OreDictMaterial flux : new OreDictMaterial[] {MT.CaCO3, MT.STONES.Limestone, MT.Chalk}) {
                    // ore dusts (least efficent)
                    RMx.BlastFurnace.addRecipeX(true, 8, 512 * mult,
                            ST.array(dust.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(4 * mult, coal), dust.mat(flux, mult)),
                            FL.array(FL.Air.make(4 * 2500 * mult)),
                            FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.BlastFurnaceGas.gas(mult * 4 * 5 * U, false)),
                            gem.mat(MTx.Slag, mult)
                    );
                    // ore dusts with blast
                    RMx.BlastFurnace.addRecipeX(true, 8, 256 * mult,
                            ST.array(dust.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(3 * mult, coal), dust.mat(flux, mult)),
                            FL.array(FL.mul(blast, 3 * 2500 * mult)),
                            FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.BlastFurnaceGas.gas(mult * 3 * 5 * U, false)),
                            gem.mat(MTx.Slag, mult)
                    );
                }
                // sinters
                RMx.BlastFurnace.addRecipeX(true, 8, 256 * mult,
                        ST.array(sinter.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(2 * mult, coal)),
                        FL.array(FL.Air.make(3 * 2500 * mult)),
                        FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.BlastFurnaceGas.gas(mult*3*5*U, false)),
                        gem.mat(MTx.Slag, mult)
                );
                // sinters with blast (most efficient)
                RMx.BlastFurnace.addRecipeX(true, 8, 128 * mult,
                        ST.array(sinter.mat(stacks[0].mMaterial, stacks[0].mAmount * mult), ST.mul(mult, coal)),
                        FL.array(FL.mul(blast, 2 * 2500 * mult)),
                        FL.array(stacks[1].mMaterial.liquid(stacks[1].mAmount * mult * U, false), MTx.BlastFurnaceGas.gas(mult*2*5*U, false)),
                        gem.mat(MTx.Slag, mult)
                );
            }
        }

        for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.CoalCoke, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.Charcoal, 2), dust.mat(MT.C, 1), gem.mat(MT.PetCoke, 1), gem.mat(MT.CoalCoke, 1), gem.mat(MT.LigniteCoke, 3), gem.mat(MT.Charcoal, 2))) {
            // Zn is special since it goes into the off gas and produces different slag
            for (OreDictMaterial flux : new OreDictMaterial[] {MT.CaCO3, MT.STONES.Limestone, MT.Chalk}) {
                RMx.Sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MTx.ZnO, 2), coal, dust.mat(flux, 1)), sinter.mat(MTx.ZnO, 2));
                RMx.BlastFurnace.addRecipeX(true, 8, 512, ST.array(dust  .mat(MTx.ZnO, 2), ST.mul(4, coal), dust.mat(flux, 1)), FL.Air.make  (10000), MTx.ZnBlastFurnaceGas.gas(14*U, false), gem.mat(MTx.ZnSlag, 1));
                RMx.BlastFurnace.addRecipeX(true, 8, 256, ST.array(dust  .mat(MTx.ZnO, 2), ST.mul(3, coal), dust.mat(flux, 1)), FL.mul(blast, 7500 ), MTx.ZnBlastFurnaceGas.gas(14*U, false), gem.mat(MTx.ZnSlag, 1));

                RMx.Sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.OREMATS.YellowLimonite     , 8 ), ST.mul(1, coal), dust.mat(flux, 1)), sinter.mat(MT.Fe2O3, 5));
                RMx.Sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.OREMATS.BrownLimonite      , 8 ), ST.mul(1, coal), dust.mat(flux, 1)), sinter.mat(MT.Fe2O3, 5));
            }

            RMx.BlastFurnace.addRecipeX(true, 8, 256, ST.array(sinter.mat(MTx.ZnO, 2), ST.mul(2, coal)                       ), FL.Air.make  (7500 ), MTx.ZnBlastFurnaceGas.gas(14*U, false), gem.mat(MTx.ZnSlag, 1));
            RMx.BlastFurnace.addRecipeX(true, 8, 128, ST.array(sinter.mat(MTx.ZnO, 2), ST.mul(1, coal)                       ), FL.mul(blast, 5000 ), MTx.ZnBlastFurnaceGas.gas(14*U, false), gem.mat(MTx.ZnSlag, 1));
            // Si is special since it does not use calcite (Otherwise calcium silicate would form instead of silicon)
            RMx.Sintering.addRecipeX(true, 16, 32, ST.array(ST.tag(3), dust.mat(MT.SiO2, 6), coal), sinter.mat(MT.SiO2, 6));
            RMx.BlastFurnace.addRecipe2(true, 8, 512, dust  .mat(MT.SiO2, 6), ST.mul(4, coal), FL.array(FL.Air.make  (10000)), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)));
            RMx.BlastFurnace.addRecipe2(true, 8, 256, dust  .mat(MT.SiO2, 6), ST.mul(3, coal), FL.array(FL.mul(blast, 7500 )), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)));
            RMx.BlastFurnace.addRecipe2(true, 8, 256, sinter.mat(MT.SiO2, 6), ST.mul(2, coal), FL.array(FL.Air.make  (7500 )), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)));
            RMx.BlastFurnace.addRecipe2(true, 8, 128, sinter.mat(MT.SiO2, 6), ST.mul(1, coal), FL.array(FL.mul(blast, 5000 )), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)));
        }

        // Pidgeon Process
        RMx.BlastFurnace.addRecipeX(true, 8, 640, ST.array(dust.mat(MTx.CalcinedDolomite, 8), dust.mat(MT.Si, 1), dust.mat(MT.SiO2, 3)), ZL_FS, FL.array(MT.Mg.gas(2*U, false)), gem.mat(MTx.Slag, 10));
        RMx.BlastFurnace.addRecipeX(true, 8, 384, ST.array(dust.mat(MTx.MgO, 4), dust.mat(MTx.CaO, 2), dust.mat(MT.Si, 1)), ZL_FS, FL.array(MT.Mg.gas(2*U, false)), gem.mat(MTx.Slag, 5));

        // blast furnace gases
        RM.CryoDistillationTower.addRecipe0(true, 64,  128, FL.array(MTx.BlastFurnaceGas.gas(4*U10, true)), FL.array(MT.N.gas(177*U1000, false), MT.CO.gas(4*20*U1000, true), MT.CO2.gas(4*30*U1000, true), MT.H.gas(4*5*U1000, true), MT.He.gas(U1000, true), MT.Ne.gas(U1000, true), MT.Ar.gas(U1000, true)));
        FM.Burn.addRecipe0(true, -16, 1, MTx.BlastFurnaceGas.gas(3*U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        RM.Distillery.addRecipe1(true, 16, 64, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Cd.liquid(U9, false), MTx.BlastFurnaceGas.gas(6*U, false)), OM.dust(MT.Zn, 8*U9));

        // Wrought Iron
        RM.Anvil.addRecipe2(false, 64, 192, scrapGt.mat(MTx.SpongeIron, 9), scrapGt.mat(MTx.SpongeIron, 9), ingot.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.FerrousSlag, 8));

        // Precipitation of Zn, P containing off-gases
        RM.Freezer.addRecipe1(true, 16, 16, ST.tag(0), MTx.ZnBlastFurnaceGas.gas(7*U, true), MTx.BlastFurnaceGas.gas(6*U, false), OM.ingot(MT.Zn, U));
        RM.Freezer.addRecipe1(true, 16, 16, ST.tag(0), FL.array(MTx.P_CO_Gas.gas(6*U, true)), FL.array(MT.CO.gas(5*U, false)), gem.mat(MT.P, 1));
        RM.Bath   .addRecipe1(true, 0, 128, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Zn.liquid(U, false), MTx.BlastFurnaceGas.gas(6*U, false)));
        RM.Bath   .addRecipe1(true, 0, 256, ST.tag(0), FL.array(MTx.P_CO_Gas.gas(6*U, true)), FL.array(MT.CO.gas(5*U, false)), gem.mat(MT.P, 1));

        // Acidic leaching for electrowinning
        RM.Bath.addRecipe1(true, 0, 256, new long[] { 1000 }, dust.mat(MTx.ZnO, 1), MT.H2SO4.liquid(7*U, true), MTx.ZnLeachingSolution.liquid(9*U, false), crushedCentrifugedTiny.mat(MTx.ZRR, 9));
        RM.Bath.addRecipe1(true, 0, 256, new long[] { 1000 }, dust.mat(MT.OREMATS.Smithsonite, 5), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MTx.ZnLeachingSolution.liquid(9*U, false), MT.CO2.gas(3*U, false)), crushedCentrifugedTiny.mat(MTx.ZRR, 9));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CdO, 2), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.H2O.liquid(3*U, false)), dust.mat(MTx.CdSO4, 6));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.Cd, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.H.gas(2*U, false)), dust.mat(MTx.CdSO4, 6));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CdSO4, 6), FL.array(MT.H2S.gas(3*U, true)), FL.array(MT.H2SO4.liquid(7*U, false)), dust.mat(MTx.CdS, 2));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Garnierite, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.CyanVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CoO, 2), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.RedVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.MnO2, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.GrayVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false), MT.O.gas(U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Cassiterite, 1), FL.array(MT.HCl.gas(8*U, true)), FL.array(MT.StannicChloride.liquid(5*U, false), MT.H2O.liquid(6*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Fayalite, 7), MT.HCl.gas(8*U, true), MTx.FeCl2Solution.liquid(12*U, false), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.Olivine, 7), FL.array(MT.HCl.gas(8*U, true)), FL.array(MTx.FeCl2Solution.liquid(6*U, false), MTx.MgCl2Solution.liquid(6*U, false)), dust.mat(MT.SiO2, 3));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Malachite, 5), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.BlueVitriol.liquid(6*U, false), MT.H2O.liquid(6*U2, false), MT.CO2.gas(3*U2, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT        .Azurite  , 5), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.BlueVitriol.liquid(6*U, false), MT.H2O.liquid(12*U3, false), MT.CO2.gas(6*U3, false)));

        // Gold Cyanidation
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Au, 2), FL.array(MTx.NaCNSolution.liquid(24*U, true), MT.O.gas(U, true)), FL.array(MTx.NaAuC2N2.liquid(12*U, false), MTx.NaOHSolution.liquid(12*U, false)));
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MT.Au, 2), FL.array(MTx.KCNSolution.liquid(24*U, true), MT.O.gas(U, true)), FL.array(MTx.KAuC2N2.liquid(12*U, false), MTx.KOHSolution.liquid(12*U, false)));

        RM.Bath.addRecipe1(true,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurified    .mat(MT.Au, 1), FL.array(MTx.NaCNSolution.liquid(6*U, true)), FL.array(MTx.NaAuC2N2.liquid(3*U, false), MTx.NaOHSolution.liquid(3*U, false), MT.H2O.liquid(3*U4, false)), crushedCentrifuged.mat(MT.Au, 1), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2));
        RM.Bath.addRecipe1(true,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurifiedTiny.mat(MT.Au, 9), FL.array(MTx.NaCNSolution.liquid(6*U, true)), FL.array(MTx.NaAuC2N2.liquid(3*U, false), MTx.NaOHSolution.liquid(3*U, false), MT.H2O.liquid(3*U4, false)), crushedCentrifuged.mat(MT.Au, 1), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2));
        RM.Bath.addRecipe1(true,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurified    .mat(MT.Au, 1), FL.array(MTx.KCNSolution .liquid(6*U, true)), FL.array(MTx.KAuC2N2 .liquid(3*U, false), MTx.KOHSolution .liquid(3*U, false), MT.H2O.liquid(3*U4, false)), crushedCentrifuged.mat(MT.Au, 1), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2));
        RM.Bath.addRecipe1(true,  0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurifiedTiny.mat(MT.Au, 9), FL.array(MTx.KCNSolution .liquid(6*U, true)), FL.array(MTx.KAuC2N2 .liquid(3*U, false), MTx.KOHSolution .liquid(3*U, false), MT.H2O.liquid(3*U4, false)), crushedCentrifuged.mat(MT.Au, 1), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2), crushedCentrifugedTiny.mat(MT.Au, 2));

        for (FluidStack water : FL.waters(6000)) {
            RM.Electrolyzer.addRecipe1(true, 32, 64, ST.tag(0), FL.array(MTx.NaAuC2N2.liquid(6*U, true), water), FL.array(MTx.HCN.gas(6*U, false), MT.H.gas(U, false), MT.O.gas(U, false)), dust.mat(MT.Au, 1), dust.mat(MT.NaOH, 3));
            RM.Electrolyzer.addRecipe1(true, 32, 64, ST.tag(0), FL.array(MTx.KAuC2N2 .liquid(6*U, true), water), FL.array(MTx.HCN.gas(6*U, false), MT.H.gas(U, false), MT.O.gas(U, false)), dust.mat(MT.Au, 1), dust.mat(MT.KOH , 3));
        }

        // Reduction of As, Sb with Fe
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MTx.As2O3, 5), dust.mat(MT.Fe, 2), ZL_FS, FL.array(MT.As.gas(2*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Realgar, 2), dust.mat(MT.Fe, 1), ZL_FS, FL.array(MT.As.gas(U, false)), dust.mat(MTx.FeS, 2));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Stibnite, 5), dust.mat(MT.Fe, 3), ZL_FS, FL.array(MT.Sb.liquid(2*U, false)), dust.mat(MTx.FeS, 6));

        // Ba, Sr chemistry
        for (OreDictMaterial coal : ANY.Coal.mToThis) if (coal != MT.Graphene) {
            RM.BurnMixer.addRecipe2(true, 16, 32, dust.mat(MT.OREMATS.Barite, 6), dust.mat(coal, 2), NF, MT.CO2.gas(6*U, false), dust.mat(MTx.BaS, 1));
            RM.BurnMixer.addRecipe2(true, 16, 32, dust.mat(MT.OREMATS.Celestine, 6), dust.mat(coal, 2), NF, MT.CO2.gas(6*U, false), dust.mat(MTx.SrS, 1));
        }
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.BaS, 1), MT.NitricAcid.liquid(10*U, true), MT.H2S.gas(3*U, false), dust.mat(MTx.BaNO3, 1));
        for (FluidStack water : FL.waters(3000)) {
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.BaS, 1), FL.array(water, MT.CO2.gas(3 * U, true)), FL.array(MT.H2S.gas(3 * U, false)), dust.mat(MTx.BaCO3, 1));
            RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.SrS, 1), FL.array(water, MT.CO2.gas(3 * U, true)), FL.array(MT.H2S.gas(3 * U, false)), dust.mat(MTx.SrCO3, 1));
        }
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.BaO, 3), MT.Al.liquid(2*U, true), MT.Ba.liquid(3*U, false), dust.mat(MT.Al2O3, 5));
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MTx.SrO, 3), MT.Al.liquid(2*U, true), MT.Sr.liquid(3*U, false), dust.mat(MT.Al2O3, 5));

        // Metal oxides from thermal decomposition
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.BaCO3, 1), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.BaO, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.BaNO3, 1), ZL_FS, FL.array(MT.NO2.gas(6*U, false), MT.O.gas(U, false)), dust.mat(MTx.BaO, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.SrCO3, 1), NF, MT.CO2.gas(3*U, false), dust.mat(MTx.SrO, 1));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.Co3O4, 7), NF, MT.O.gas(U, false), dust.mat(MTx.CoO, 6));
        RMx.Thermolysis.addRecipe0(true, 16, 512, FL.array(MT.GreenVitriol.liquid(12*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.SO3.gas(4*U, false)), dust.mat(MT.Fe2O3, 5));
        RMx.Thermolysis.addRecipe0(true, 16, 256, FL.array(MT.PinkVitriol.liquid(6*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.O.gas(U, false)), dust.mat(MTx.MgO, 2));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MTx.HgO, 1), ZL_FS, FL.array(MT.Hg.liquid(U2, false), MT.O.gas(U2, false)));
        RMx.Thermolysis.addRecipe1(true, 16, 256, dust.mat(MT.OREMATS.Smithsonite, 5), ZL_FS, FL.array(MT.CO2.gas(3*U, false)), dust.mat(MTx.ZnO, 1));

        // Bayer waste products processing
        RM.Centrifuge.addRecipe1(true, 16, 256, new long[] {10000, 3000, 2000, 1000, 1000, 500, 50}, dust.mat(MTx.RedMud, 1), dustTiny.mat(MT.Fe2O3, 9), dustTiny.mat(MT.Sodalite, 9), dustTiny.mat(MT.OREMATS.Wollastonite, 9), dustTiny.mat(MT.TiO2, 9), dustTiny.mat(MT.AlO3H3, 9), dustTiny.mat(MTx.Sc2O3, 9));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.Sc2O3, 5), MT.HF.gas(12*U, true), MT.H2O.liquid(9*U, false), dust.mat(MTx.ScF3, 8));

        RM.Drying.addRecipe0(true, 16, 6000, MTx.BayerLiquor.liquid(6*U, true), FL.DistW.make(3000), dust.mat(MT.NaOH, 3));
        RM.Electrolyzer.addRecipe1(true, 64, 128, new long[] {10000, 2000}, ST.tag(1), FL.array(MTx.BayerLiquor.liquid(6*U, true), MT.Hg.liquid(9*U8, true)), FL.array(MTx.GaAmalgam.liquid(10*U8, false), MT.H.gas(U2, false), FL.Water.make(2250)), dust.mat(MT.NaOH, 3), dustSmall.mat(MT.V2O5, 7));

        for (FluidStack water : FL.waters(1500)) {
            RM.Mixer.addRecipe0(true, 16, 192, FL.array(MTx.GaAmalgam.liquid(10*U, true), MTx.NaOHSolution.liquid(6*U, true), water, FL.Oxygen.make(1500)), FL.array(MT.Hg.liquid(9*U, false)), dust.mat(MTx.NaGaOH4, 10));
            RM.Mixer.addRecipe1(true, 16, 192, dust.mat(MT.NaOH, 3), FL.array(MTx.GaAmalgam.liquid(10*U, true), FL.mul(water, 3), FL.Oxygen.make(1500)), FL.array(MT.Hg.liquid(9*U, false)), dust.mat(MTx.NaGaOH4, 10));
        }
        RM.Electrolyzer.addRecipe2(true, 64, 512, ST.tag(1), dust.mat(MTx.NaGaOH4, 10), ZL_FS, FL.array(FL.Water.make(1500), FL.Oxygen.make(1500), MTx.NaOHSolution.liquid(6*U, false)), dust.mat(MT.Ga, 1));
        RM.Bath.addRecipe1(true, 0, 100, dust.mat(MTx.NaGaOH4, 20), MT.HCl.gas(4*U, true), FL.Saltwater.make(16000), dust.mat(MTx.Ga2O3, 5));
        RM.Roasting.addRecipe1(true, 16, 64, dust.mat(MT.Ga, 2), MT.O.gas(3*U, true), NF, dust.mat(MTx.Ga2O3, 5));

        // Zn byproducts
        RM.Bath.addRecipe1(true, 0, 256  , dustTiny.mat(MT.Zn, 1), FL.array(MTx.ZnLeachingSolution.liquid(9  *U, true)), FL.array(MT.WhiteVitriol.liquid(6  *U, false), MT.H2O.liquid(3  *U, false)), dustTiny.mat(MT.Cd, 1));
        RM.Bath.addRecipe1(true, 0, 256*9, dust    .mat(MT.Zn, 1), FL.array(MTx.ZnLeachingSolution.liquid(9*9*U, true)), FL.array(MT.WhiteVitriol.liquid(6*9*U, false), MT.H2O.liquid(3*9*U, false)), dust    .mat(MT.Cd, 1));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MTx.ZnSlag, 1), MT.H2SO4.liquid(8*U, true), MTx.GeGaInSulfateSolution.liquid(7*U, false), dust.mat(MT.SiO2, 1), dust.mat(MT.CaSO4, 1));
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MTx.ZRR, 1), MT.H2SO4.liquid(8*U, true), MTx.GeGaInSulfateSolution.liquid(7*U, false), dust.mat(MTx.Tl2SO4, 1));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.TannicAcid, 1), MTx.GeGaInSulfateSolution.liquid(7*U, true), MTx.GaInSulfateSolution.liquid(7*U, false), dust.mat(MTx.GeTannate, 1));
        for (FluidStack water : FL.waters(1000)) {
            RM.Electrolyzer.addRecipe1(true, 64, 64, dust.mat(MTx.Tl2SO4, 7), FL.array(FL.mul(water, 3)), FL.array(MT.H2SO4.liquid(7*U, false), MT.O.gas(U, false)), dust.mat(MT.Tl, 2));
            RM.Bath.addRecipe1(true, 0, 512, dust.mat(MTx.GeTannate, 1), FL.mul(water, 2), MTx.Tannin.liquid(2*U, false), dust.mat(MTx.GeO2, 1));
        }
        RM.Bath.addRecipe1(true, 0, 64, dust.mat(MT.NaOH, 2*3), MTx.GaInSulfateSolution.liquid(7*U, true), MTx.GaOHNa2SO4Solution.liquid(11*U, false), dust.mat(MTx.InO3H3, 2), dustTiny.mat(MTx.InO3H3, 3));
        RM.Drying.addRecipe1(true, 16, 18000, dust.mat(MTx.InO3H3, 14), NF, FL.DistW.make(9000), dust.mat(MTx.In2O3, 5));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.NaOH, 1), MTx.GaOHNa2SO4Solution.liquid(11*U, true), MTx.Na2SO4Solution.liquid(10*U, false), dust.mat(MTx.NaGaOH4, 3), dustTiny.mat(MTx.NaGaOH4, 3));

        // Coal Ash byproducts
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{ 10000, 500, 500, 500, 500, 500 }, dust.mat(MT.DarkAsh, 1), dustSmall.mat(MTx.CoalAshNonmagResidue, 3), dustSmall.mat(MT.OREMATS.Magnetite, 4), dustSmall.mat(MT.OREMATS.Ilmenite, 4), dustSmall.mat(MT.ClayRed, 4), dustSmall.mat(MT.ClayBrown, 4), dustSmall.mat(MT.Bentonite, 4));
        RM.Bath.addRecipe1(true, 0, 64, new long[]{ 2500, 2500, 2500 }, dust     .mat(MTx.CoalAshNonmagResidue, 3), MT.H2SO4.liquid(7*U , true), MTx.CoalAshLeachingSolution.liquid(8*U , false), dustSmall.mat(MT.OREMATS.Wollastonite, 12), dustSmall.mat(MT.SiO2, 6), dustSmall.mat(MT.C, 6));
        RM.Bath.addRecipe1(true, 0, 64, new long[]{ 625 , 625 , 625  }, dustSmall.mat(MTx.CoalAshNonmagResidue, 3), MT.H2SO4.liquid(7*U4, true), MTx.CoalAshLeachingSolution.liquid(8*U4, false), dustSmall.mat(MT.OREMATS.Wollastonite, 12), dustSmall.mat(MT.SiO2, 6), dustSmall.mat(MT.C, 6));
        // 1/4 Ge(SO4)2 + 2/4 H2C2O4 -> 1/4 Ge(C2O4)2 + 2/4 H2SO4
        RM.Bath.addRecipe1(true, 0, 1080, OM.dust(MTx.OxalicAcid, 4*8*U), FL.array(MTx.CoalAshLeachingSolution.liquid(216*U, true)), FL.array(MT.VitriolOfClay.liquid(7*17*U, false), MT.H2O.liquid(21*3*U, false), MTx.DiluteH2SO4.liquid(4*10*U, false)), OM.dust(MTx.GeOxalate, 2*13*U));
        RMx.Thermolysis.addRecipe1(true, 16, 64, dust.mat(MTx.GeOxalate, 13), ZL_FS, FL.array(MT.CO.gas(4*U, false), MT.CO2.gas(6*U, false)), dust.mat(MTx.GeO2, 3));
        RMx.Thermolysis.addRecipe1(true, 16, 16, dustSmall.mat(MTx.GeOxalate, 13), ZL_FS, FL.array(MT.CO.gas(U, false), MT.CO2.gas(6*U4, false)), dustSmall.mat(MTx.GeO2, 3));

        // GeO2 reduction in mixer
        RM.Mixer.addRecipe1(true, 16, 64, dust.mat(MTx.GeO2, 3), MT.H.gas(4*U, true), MT.H2O.liquid(6*U, false), dust.mat(MT.Ge, 1));

        // Pollucite, Lepidolite
        for (FluidStack water : FL.waters(3000)) {
            RM.Bath.addRecipeX(true, 0, 520, ST.array(dust.mat(MT.OREMATS.Pollucite, 26), dust.mat(MTx.CaO, 8), dust.mat(MT.CaCl2, 3)), water, MTx.CsRbClSolution.liquid(5 * U, false), dust.mat(MT.OREMATS.Wollastonite, 20), dust.mat(MT.Al2O3, 5));
        }
        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.OREMATS.Lepidolite, 21), MT.H2SO4.liquid(21*U, true), MTx.LepidoliteLeachingSolution.liquid(28*U, false), dust.mat(MT.SiO2, 12));
        RM.Bath.addRecipe1(true, 0, 128, dust.mat(MT.KOH, 9), MTx.LepidoliteLeachingSolution.liquid(28*U, true), MTx.LiKRbSulfateSolution.liquid(30*U, false), dust.mat(MT.AlO3H3, 7));
        //TODO ion-exchange or LLE of RbCl from CsCl and sulfates

        // Sintering dusts into chunks
        RMx.Sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 16, 0, 0, NF, ingot , 1, ST.tag(1), NI, true, false, false, lowHeatSintering));
        RMx.Sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 16, 0, 0, NF, chunk , 1, ST.tag(1), NI, true, false, false, lowHeatSintering));
        RMx.Sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 16, 0, 0, NF, nugget, 1, ST.tag(1), NI, true, false, false, lowHeatSintering));
        RMx.Sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 96, 0, 0, NF, ingot , 1, ST.tag(1), NI, true, false, false, highHeatSintering));
        RMx.Sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 96, 0, 0, NF, chunk , 1, ST.tag(1), NI, true, false, false, highHeatSintering));
        RMx.Sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 96, 0, 0, NF, nugget, 1, ST.tag(1), NI, true, false, false, highHeatSintering));

        // misc sintering
        RMx.Sintering.addRecipeX(true, 16, 64  , ST.array(ST.tag(2), dust.mat(MTx.CoO, 2), dust.mat(MT.Al2O3, 5)), dust.mat(MTx.CobaltBlue, 7));
        RMx.Sintering.addRecipeX(true, 96, 2048, ST.array(ST.tag(2), dust.mat(MT.Ad, 3), dust.mat(MT.Vb, 1)), ingot.mat(MT.Vibramantium, 4));

        // Sintered carbides and limonite to hematite
        for (ItemStack coal : ST.array(dust.mat(MT.PetCoke, 1), dust.mat(MT.LigniteCoke, 3),  dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1) )) {
            RMx.Sintering.addRecipeX(true, 96, 334, ST.array(ST.tag(2), dust.mat(MT.W, 1), ST.copy(coal)), ingot.mat(MT.TungstenCarbide, 2));
            RMx.Sintering.addRecipeX(true, 96, 1400, ST.array(ST.tag(3), dust.mat(MT.Ta, 4), dust.mat(MT.Hf, 1), ST.mul(5, coal)), ingot.mat(MT.Ta4HfC5, 10));
            RMx.Sintering.addRecipeX(true, 96, 1746, ST.array(ST.tag(3), dust.mat(MT.Ke, 6), dust.mat(MT.Nq, 2), ST.mul(1, coal)), ingot.mat(MT.Trinaquadalloy, 9));
        }

        // mixing from/to molten ferrochrome and steel
        for (String iron : new String[] {"molten.iron", "molten.wroughtiron", "molten.meteoriciron", "molten.steel"}) {
            mix(iron, 1, "molten.gold", 1, "molten.angmallen", 2);
            mix(iron, 1, "molten.tin", 1,"molten.tinalloy", 2);
            mix(iron, 2, "molten.nickel", 1, "molten.invar", 3);
            mix(iron, 1, "molten.chromium", 2, "molten.ferrochrome", 3);
            mix(iron, 1, "molten.chromium", 1, "molten.aluminium" , 1, "molten.kanthal", 3);
            mix(iron, 1, "molten.ferrochrome", 3, "molten.aluminium" , 2, "molten.kanthal", 6);
        }

        // DRI and Fe3C
        RMx.DirectReduction.addRecipe2(true, 64, 64, ST.tag(0), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(73  * 2 * U25 , true)), FL.array(MT.H2O.liquid(73  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        RMx.DirectReduction.addRecipe2(true, 64, 64, ST.tag(1), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(81 * 2 * U100, true), MT.H.gas(243 * 2 * U100, true)), FL.array(MT.H2O.liquid(243 * 3 * U100, false), MT.CO2.gas(49 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 3));
        RMx.DirectReduction.addRecipe2(true, 64, 64, ST.tag(2), dust.mat(MT.Fe2O3, 5), FL.array(MT.CO .gas(81 * 2 * U50 , true), MT.H.gas(81  * 2 * U50 , true)), FL.array(MT.H2O.liquid(81  * 3 * U50 , false), MT.CO2.gas(65 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 3));
        RMx.DirectReduction.addRecipe2(true, 64, 32, ST.tag(3), dust.mat(MT.Fe2O3, 5), FL.array(MT.CH4.gas(8  *     U15 , true), MT.H.gas(23  * 2 * U15 , true)), FL.array(MT.H2O.liquid(39  * 3 * U15 , false)), dust.mat(MTx.ImpureCementite, 3));

        RMx.DirectReduction.addRecipe2(true, 64, 64, ST.tag(0), dust.mat(MTx.FeO , 4), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(48  * 2 * U25 , true)), FL.array(MT.H2O.liquid(63  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        RMx.DirectReduction.addRecipe2(true, 64, 64, ST.tag(1), dust.mat(MTx.FeO , 4), FL.array(MT.CO .gas(14 * 2 * U25 , true), MT.H.gas(42  * 2 * U25 , true)), FL.array(MT.H2O.liquid(42  * 3 * U25 , false), MT.CO2.gas(6  * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        RMx.DirectReduction.addRecipe2(true, 64, 64, ST.tag(2), dust.mat(MTx.FeO , 4), FL.array(MT.CO .gas(28 * 2 * U25 , true), MT.H.gas(28  * 2 * U25 , true)), FL.array(MT.H2O.liquid(28  * 3 * U25 , false), MT.CO2.gas(20 * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 3));
        RMx.DirectReduction.addRecipe2(true, 64, 32, ST.tag(3), dust.mat(MTx.FeO , 4), FL.array(MT.CH4.gas(8  *     U15 , true), MT.H.gas(8   * 2 * U15 , true)), FL.array(MT.H2O.liquid(24  * 3 * U15 , false)), dust.mat(MTx.ImpureCementite, 3));

        for(OreDictMaterial limonite : new OreDictMaterial[] {MT.OREMATS.BrownLimonite, MT.OREMATS.YellowLimonite }) {
            RMx.DirectReduction.addRecipe2(true, 64, 96, ST.tag(0), dust.mat(limonite, 8), FL.array(MT.CO .gas(8  * 2 * U25 , true), MT.H.gas(73  * 2 * U25 , true)), FL.array(MT.H2O.liquid(73  * 3 * U25  + 3 * U, false)), dust.mat(MTx.SpongeIron, 3));
            RMx.DirectReduction.addRecipe2(true, 64, 96, ST.tag(1), dust.mat(limonite, 8), FL.array(MT.CO .gas(81 * 2 * U100, true), MT.H.gas(243 * 2 * U100, true)), FL.array(MT.H2O.liquid(243 * 3 * U100 + 3 * U, false), MT.CO2.gas(49 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 3));
            RMx.DirectReduction.addRecipe2(true, 64, 96, ST.tag(2), dust.mat(limonite, 8), FL.array(MT.CO .gas(81 * 2 * U50 , true), MT.H.gas(81  * 2 * U50 , true)), FL.array(MT.H2O.liquid(81  * 3 * U50  + 3 * U, false), MT.CO2.gas(65 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 3));
            RMx.DirectReduction.addRecipe2(true, 64, 48, ST.tag(3), dust.mat(limonite, 8), FL.array(MT.CH4.gas(8  *     U15 , true), MT.H.gas(23  * 2 * U15 , true)), FL.array(MT.H2O.liquid(39  * 3 * U15  + 3 * U, false)), dust.mat(MTx.ImpureCementite, 3));
        }

        for(OreDictMaterial magnetite : new OreDictMaterial[] {MT.OREMATS.Magnetite, MT.OREMATS.GraniticMineralSand, MT.OREMATS.BasalticMineralSand }) {
            RMx.DirectReduction.addRecipe2(true, 64, 64*3, ST.tag(0), dust.mat(magnetite, 14), FL.array(MT.CO .gas(24  * 2 * U25 , true), MT.H.gas(254 * 2 * U25 , true)), FL.array(MT.H2O.liquid(254 * 3 * U25 , false)), dust.mat(MTx.SpongeIron, 9));
            RMx.DirectReduction.addRecipe2(true, 64, 64*3, ST.tag(1), dust.mat(magnetite, 14), FL.array(MT.CO .gas(218 * 2 * U100, true), MT.H.gas(654 * 2 * U100, true)), FL.array(MT.H2O.liquid(654 * 3 * U100, false), MT.CO2.gas(122 * 3 * U100, false)), dust.mat(MTx.SpongeIron, 9));
            RMx.DirectReduction.addRecipe2(true, 64, 64*3, ST.tag(2), dust.mat(magnetite, 14), FL.array(MT.CO .gas(218 * 2 * U50 , true), MT.H.gas(218 * 2 * U50 , true)), FL.array(MT.H2O.liquid(218 * 3 * U50 , false), MT.CO2.gas(170 * 3 * U50 , false)), dust.mat(MTx.SpongeIron, 9));
            RMx.DirectReduction.addRecipe2(true, 64, 32*3, ST.tag(3), dust.mat(magnetite, 14), FL.array(MT.CH4.gas(8   *     U5  , true), MT.H.gas(18  * 2 * U5  , true)), FL.array(MT.H2O.liquid(34  * 3 * U5  , false)), dust.mat(MTx.ImpureCementite, 9));
        }

        // Misc direct reduction
        RMx.DirectReduction.addRecipe1(true, 16, 64 , dust.mat(MTx.PbO  , 1), MT.H.gas(2*U, true), MT.H2O.liquid(3*U, false), dust.mat(MT.Pb, 1));
        RMx.DirectReduction.addRecipe1(true, 16, 64 , dust.mat(MTx.MnO  , 1), MT.H.gas(2*U, true), MT.H2O.liquid(3*U, false), dust.mat(MT.Mn, 1));
        RMx.DirectReduction.addRecipe1(true, 16, 64 , dust.mat(MTx.GeO2 , 3), MT.H.gas(4*U, true), MT.H2O.liquid(6*U, false), dust.mat(MT.Ge, 1));
        RMx.DirectReduction.addRecipe1(true, 16, 128, dust.mat(MTx.In2O3, 5), MT.H.gas(6*U, true), MT.H2O.liquid(9*U, false), dust.mat(MT.In, 2));
        RMx.DirectReduction.addRecipe1(true, 16, 128, dust.mat(MTx.Bi2O3, 5), MT.H.gas(6*U, true), MT.H2O.liquid(9*U, false), dust.mat(MT.Bi, 2));
        RMx.DirectReduction.addRecipe1(true, 16, 128, dust.mat(MTx.BiCl3, 4), MT.H.gas(3*U, true), MT.HCl.gas   (6*U, false), dust.mat(MT.Bi, 1));
        RMx.DirectReduction.addRecipe1(true, 16, 64 , dust.mat(MT.WO3   , 4), MT.H.gas(6*U, true), MT.H2O.liquid(9*U, false), dust.mat(MT.W , 1));
        RMx.DirectReduction.addRecipe1(true, 16, 64 , dust.mat(MTx.MoO3 , 4), MT.H.gas(6*U, true), MT.H2O.liquid(9*U, false), dust.mat(MT.Mo, 1));

        RM.Compressor.addRecipe1(true, 16, 32, dust.mat(MTx.SpongeIron, 1), ingot.mat(MTx.HBI, 1));

        // Aluminothermic reduction
        RMx.Thermite.addRecipe(0, 10, OMx.stacks(MT.Al, 10*U, MT .V2O5 , 21*U), 600, OM.stack(MT.V , 6*U), OM.stack(MT.Al2O3, 25*U));
        RMx.Thermite.addRecipe(0, 10, OMx.stacks(MT.Al, 10*U, MT .Nb2O5, 21*U), 600, OM.stack(MT.Nb, 6*U), OM.stack(MT.Al2O3, 25*U));
        RMx.Thermite.addRecipe(0, 10, OMx.stacks(MT.Al, 10*U, MT .Ta2O5, 21*U), 600, OM.stack(MT.Ta, 6*U), OM.stack(MT.Al2O3, 25*U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MTx.Cr2O3, 5 *U), 600, OM.stack(MT.Cr, 2*U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MTx.MoO3 , 4 *U), 600, OM.stack(MT.Mo,   U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MT .WO3  , 4 *U), 600, OM.stack(MT.W ,   U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 4 , OMx.stacks(MT.Al, 4 *U, MT .MnO2 , 3 *U), 600, OM.stack(MT.Mn, 3*U), OM.stack(MT.Al2O3, 10*U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MTx.MnO  , 3 *U), 600, OM.stack(MT.Mn, 3*U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MT .Fe2O3, 5 *U), 600, OM.stack(MT.Fe, 2*U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 8 , OMx.stacks(MT.Al, 8 *U, MT.OREMATS.Magnetite, 21*U), 600, OM.stack(MT.Fe, 9*U), OM.stack(MT.Al2O3, 20*U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MTx.FeO  , 4 *U), 600, OM.stack(MT.Fe, 2*U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 8 , OMx.stacks(MT.Al, 8 *U, MTx.Co3O4, 21*U), 600, OM.stack(MT.Co, 9*U), OM.stack(MT.Al2O3, 20*U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MTx.BaO  , 6 *U), 600, OM.stack(MT.Ba, 3*U), OM.stack(MT.Al2O3, 5 *U));
        RMx.Thermite.addRecipe(0, 2 , OMx.stacks(MT.Al, 2 *U, MTx.SrO  , 6 *U), 600, OM.stack(MT.Sr, 3*U), OM.stack(MT.Al2O3, 5 *U));

        // Calcinothermic reduction
        RMx.Thermite.addRecipe(0, 5, OMx.stacks(MT.Ca, 5*U, MT .V2O5 , 7*U), 600, OM.stack(MT.V , 2*U), OM.stack(MTx.CaO, 10*U));
        RMx.Thermite.addRecipe(0, 5, OMx.stacks(MT.Ca, 5*U, MT .Nb2O5, 7*U), 600, OM.stack(MT.Nb, 2*U), OM.stack(MTx.CaO, 10*U));
        RMx.Thermite.addRecipe(0, 5, OMx.stacks(MT.Ca, 5*U, MT .Ta2O5, 7*U), 600, OM.stack(MT.Ta, 2*U), OM.stack(MTx.CaO, 10*U));
        RMx.Thermite.addRecipe(0, 3, OMx.stacks(MT.Ca, 3*U, MTx.Cr2O3, 5*U), 600, OM.stack(MT.Cr, 2*U), OM.stack(MTx.CaO, 6 *U));
        RMx.Thermite.addRecipe(0, 3, OMx.stacks(MT.Ca, 3*U, MTx.MoO3 , 4*U), 600, OM.stack(MT.Mo,   U), OM.stack(MTx.CaO, 6 *U));
        RMx.Thermite.addRecipe(0, 3, OMx.stacks(MT.Ca, 3*U, MT .WO3  , 4*U), 600, OM.stack(MT.W ,   U), OM.stack(MTx.CaO, 6 *U));
        RMx.Thermite.addRecipe(0, 2, OMx.stacks(MT.Ca, 2*U, MT .MnO2 , 3*U), 600, OM.stack(MT.Mn,   U), OM.stack(MTx.CaO, 4 *U));
        RMx.Thermite.addRecipe(0, 1, OMx.stacks(MT.Ca, U  , MTx.MnO  , U  ), 600, OM.stack(MT.Mn,   U), OM.stack(MTx.CaO, 2 *U));
        RMx.Thermite.addRecipe(0, 3, OMx.stacks(MT.Ca, 3*U, MT .Fe2O3, 5*U), 600, OM.stack(MT.Fe, 2*U), OM.stack(MTx.CaO, 6 *U));
        RMx.Thermite.addRecipe(0, 4, OMx.stacks(MT.Ca, 4*U, MT.OREMATS.Magnetite, 7*U), 600, OM.stack(MT.Fe, 3*U), OM.stack(MTx.CaO, 8*U));
        RMx.Thermite.addRecipe(0, 2, OMx.stacks(MT.Ca, U  , MTx.FeO  , 2*U), 600, OM.stack(MT.Fe,   U), OM.stack(MTx.CaO, 2 *U));
        RMx.Thermite.addRecipe(0, 4, OMx.stacks(MT.Ca, 4*U, MTx.Co3O4, 7*U), 600, OM.stack(MT.Co, 3*U), OM.stack(MTx.CaO, 8 *U));
        RMx.Thermite.addRecipe(0, 3, OMx.stacks(MT.Ca, 3*U, MTx.ScF3 , 8*U), 600, OM.stack(MT.Sc, 2*U), OM.stack(MT.CaF2     , 9 *U));

        // (Acidic/simple) Bessemer Process
        RMx.Bessemer.addRecipe(0, 1, OMx.stacks(OM.stack(MT .PigIron          , 4*U), OM.stack(MT.Air, 2*U)), MT .PigIron          .mMeltingPoint, OM.stack(MT.Steel        , 3*U));
        RMx.Bessemer.addRecipe(0, 1, OMx.stacks(OM.stack(MT .IronCast         , 4*U), OM.stack(MT.Air, 2*U)), MT .IronCast         .mMeltingPoint, OM.stack(MT.Steel        , 3*U));
        RMx.Bessemer.addRecipe(0, 2, OMx.stacks(OM.stack(MTx.Cementite        , 4*U), OM.stack(MT.Air, 3*U)), MTx.Cementite        .mMeltingPoint, OM.stack(MT.Steel        , 3*U));
        RMx.Bessemer.addRecipe(0, 2, OMx.stacks(OM.stack(MTx.MeteoricCementite, 4*U), OM.stack(MT.Air, 3*U)), MTx.MeteoricCementite.mMeltingPoint, OM.stack(MT.MeteoricSteel, 3*U));

        OreDictMaterialStack[][] fluxes = new OreDictMaterialStack[][] {
            OMx.stacks(MTx.MgO, U, MTx.CaO, U),
            OMx.stacks(MTx.CalcinedDolomite, 2*U),
        };
        for (OreDictMaterialStack[] flux : fluxes) {
            // Basic Bessemer Process
            RMx.Bessemer.addRecipe(0, 1, Code.concatArrays(OMx.stacks(OM.stack(MT .PigIron          , 6*U), OM.stack(MT.Air, 3*U)), flux), MT .PigIron          .mMeltingPoint, OM.stack(MT.Steel        , 5*U), OM.stack(MTx.ConverterSlag, 2*U));
            RMx.Bessemer.addRecipe(0, 1, Code.concatArrays(OMx.stacks(OM.stack(MT .IronCast         , 6*U), OM.stack(MT.Air, 2*U)), flux), MT .IronCast         .mMeltingPoint, OM.stack(MT.Steel        , 5*U), OM.stack(MTx.ConverterSlag, 2*U));
            RMx.Bessemer.addRecipe(0, 2, Code.concatArrays(OMx.stacks(OM.stack(MTx.Cementite        , 6*U), OM.stack(MT.Air, 4*U)), flux), MTx.Cementite        .mMeltingPoint, OM.stack(MT.Steel        , 5*U), OM.stack(MTx.ConverterSlag, 2*U));
            RMx.Bessemer.addRecipe(0, 2, Code.concatArrays(OMx.stacks(OM.stack(MTx.MeteoricCementite, 6*U), OM.stack(MT.Air, 4*U)), flux), MTx.MeteoricCementite.mMeltingPoint, OM.stack(MT.MeteoricSteel, 5*U), OM.stack(MTx.ConverterSlag, 2*U));

            // Basic Oxygen Process
            RMx.BOF.addRecipe(0, 4, Code.concatArrays(OMx.stacks(OM.stack(MT .PigIron          , 18*U), OM.stack(MT.O, 4*U)), OMx.mul(2, flux)), MT .PigIron          .mMeltingPoint, OM.stack(MT.Steel        , 17*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 7*2*U));
            RMx.BOF.addRecipe(0, 3, Code.concatArrays(OMx.stacks(OM.stack(MT .IronCast         , 18*U), OM.stack(MT.O, 3*U)), OMx.mul(2, flux)), MT .IronCast         .mMeltingPoint, OM.stack(MT.Steel        , 17*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 9*U));
            RMx.BOF.addRecipe(0, 6, Code.concatArrays(OMx.stacks(OM.stack(MTx.Cementite        , 18*U), OM.stack(MT.O, 6*U)), OMx.mul(2, flux)), MTx.Cementite        .mMeltingPoint, OM.stack(MT.Steel        , 17*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 10*2*U));
            RMx.BOF.addRecipe(0, 6, Code.concatArrays(OMx.stacks(OM.stack(MTx.MeteoricCementite, 18*U), OM.stack(MT.O, 6*U)), OMx.mul(2, flux)), MTx.MeteoricCementite.mMeltingPoint, OM.stack(MT.MeteoricSteel, 17*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 10*2*U));

            RMx.BOF.addRecipe(0, 4, Code.concatArrays(OMx.stacks(OM.stack(MT .PigIron          , 10*U), OM.stack(MTx.HBI, 15*U), OM.stack(MT.O, 4*U)), OMx.mul(2, flux)), MT .PigIron          .mMeltingPoint, OM.stack(MT.Steel        , 24*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 23*2*U5));
            RMx.BOF.addRecipe(0, 3, Code.concatArrays(OMx.stacks(OM.stack(MT .IronCast         , 10*U), OM.stack(MTx.HBI, 15*U), OM.stack(MT.O, 3*U)), OMx.mul(2, flux)), MT .IronCast         .mMeltingPoint, OM.stack(MT.Steel        , 24*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 2*2*U+8*2*U5));
            RMx.BOF.addRecipe(0, 5, Code.concatArrays(OMx.stacks(OM.stack(MTx.Cementite        , 10*U), OM.stack(MTx.HBI, 15*U), OM.stack(MT.O, 6*U)), OMx.mul(2, flux)), MTx.Cementite        .mMeltingPoint, OM.stack(MT.Steel        , 24*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 5*2*U+8*2*U5));
            RMx.BOF.addRecipe(0, 5, Code.concatArrays(OMx.stacks(OM.stack(MTx.MeteoricCementite, 10*U), OM.stack(MTx.HBI, 15*U), OM.stack(MT.O, 6*U)), OMx.mul(2, flux)), MTx.MeteoricCementite.mMeltingPoint, OM.stack(MT.MeteoricSteel, 24*U), OMx.stacks(MTx.ConverterSlag, 4*U, MT.CO, 5*2*U+8*2*U5));
        }

        // Converter slag reprocessing
        RM.MagneticSeparator.addRecipe1(true, 512, 128, new long[]{500, 1000, 2500, 5000, 400, 400}, dust.mat(MTx.ConverterSlag, 1), dustSmall.mat(MTx.CaO, 4), dustSmall.mat(MTx.MgO, 4), dustSmall.mat(MT.OREMATS.Wollastonite, 4), dustSmall.mat(MTx.FeO, 4), dustSmall.mat(MT.Phosphorus, 4), dustSmall.mat(MTx.MnO, 4));
        RM.MagneticSeparator.addRecipe1(true, 512, 128, new long[]{100, 1000, 6250, 2500, 75 , 75 }, dust.mat(MTx.DRISlag      , 1), dustSmall.mat(MTx.CaO, 4), dustSmall.mat(MTx.MgO, 4), dustSmall.mat(MT.OREMATS.Wollastonite, 4), dustSmall.mat(MTx.FeO, 4), dustSmall.mat(MT.Phosphorus, 4), dustSmall.mat(MTx.MnO, 4));

        // EAF steelmaking
        RMx.EAF.addRecipe(0, 15, OMx.stacks(MTx.ImpureCementite, 75*U, MT.O, U*15, MTx.CaO  , U*10, MTx.MgO             , U*4), MTx.FerrousSlag.mMeltingPoint, OM.stack(MT.Steel, 45*U), OM.stack(MTx.DRISlag, 40*U), OM.stack(MT.CO, 53*U2));
        RMx.EAF.addRecipe(0, 15, OMx.stacks(MTx.ImpureCementite, 75*U, MT.O, U*15, MTx.CaO  , U*6 , MTx.CalcinedDolomite, U*8), MTx.FerrousSlag.mMeltingPoint, OM.stack(MT.Steel, 45*U), OM.stack(MTx.DRISlag, 40*U), OM.stack(MT.CO, 53*U2));
        RMx.EAF.addRecipe(0, 10, OMx.stacks(MTx.SpongeIron     , 75*U, MT.O, U*10, MTx.CaO  , U*10, MTx.MgO             , U*4), MTx.FerrousSlag.mMeltingPoint, OM.stack(MT.Steel, 45*U), OM.stack(MTx.DRISlag, 40*U), OM.stack(MT.CO, 8*2*U));
        RMx.EAF.addRecipe(0, 10, OMx.stacks(MTx.SpongeIron     , 75*U, MT.O, U*10, MTx.CaO  , U*6 , MTx.CalcinedDolomite, U*8), MTx.FerrousSlag.mMeltingPoint, OM.stack(MT.Steel, 45*U), OM.stack(MTx.DRISlag, 40*U), OM.stack(MT.CO, 8*2*U));

        // Pure phosphorus production in EAF
        RMx.EAF.addRecipe(0, OMx.stacks(MT.Apatite, 2*3*U, MT.SiO2, 9*U, MT.C, 5*U), 1850, OM.stack(MTx.Slag, 3*5*U), OM.stack(MT.CaCl2, U), OM.stack(MTx.P_CO_Gas, 12*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MTx.Hydroxyapatite, 4*U, MT.SiO2, 6*U, MT.C, 3*U), 1850, OM.stack(MTx.Slag, 10*U), OM.stack(MTx.P_CO_Gas, 36*U5));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.Phosphorite, 2*3*U, MT.SiO2, 9*U, MT.C, 5*U), 1850, OM.stack(MTx.Slag, 3*5*U), OM.stack(MT.CaF2, U), OM.stack(MTx.P_CO_Gas, 12*U));
        for (OreDictMaterial phosphorus : new OreDictMaterial[] { MT.Phosphorus, MT.PhosphorusBlue, MT.PhosphorusRed, MT.PhosphorusWhite}) {
            RMx.EAF.addRecipe(0, OMx.stacks(phosphorus, 13*U, MT.SiO2, 3*3*U, MT.C, 5*U), 1850, OM.stack(MTx.Slag, 3*5*U), OM.stack(MTx.P_CO_Gas, 12*U));
        }
        RMx.EAF.addRecipe(0, OMx.stacks(MTx.P2O5, 7*U, MT.C, 5*U), 1850, OM.stack(MTx.P_CO_Gas, 12*U));

        // HSS-T1
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 16*U, MT.Steel, 60*U, MT.W            , 6 *U, MT .Cr   , 4*U, MT.VanadiumSteel, 5*U), MTx.HSST1.mMeltingPoint, OM.stack(MTx.HSST1, 91*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 16*U, MT.Steel, 58*U, MT.W            , 6 *U, MTx.FeCr2, 6*U, MT.VanadiumSteel, 5*U), MTx.HSST1.mMeltingPoint, OM.stack(MTx.HSST1, 91*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 16*U, MT.Steel, 54*U, MT.TungstenSteel, 12*U, MT .Cr   , 4*U, MT.VanadiumSteel, 5*U), MTx.HSST1.mMeltingPoint, OM.stack(MTx.HSST1, 91*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 16*U, MT.Steel, 52*U, MT.TungstenSteel, 12*U, MTx.FeCr2, 6*U, MT.VanadiumSteel, 5*U), MTx.HSST1.mMeltingPoint, OM.stack(MTx.HSST1, 91*U));
        // HSS-M2
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 8 *U, MT.Steel, 28*U, MT.W            , U  , MT .Cr   , 2*U, MT.VanadiumSteel, 5*U, MT.Mo, U), MTx.HSSM2.mMeltingPoint, OM.stack(MTx.HSSM2, 45*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 8 *U, MT.Steel, 27*U, MT.W            , U  , MTx.FeCr2, 3*U, MT.VanadiumSteel, 5*U, MT.Mo, U), MTx.HSSM2.mMeltingPoint, OM.stack(MTx.HSSM2, 45*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 8 *U, MT.Steel, 27*U, MT.TungstenSteel, 2*U, MT .Cr   , 2*U, MT.VanadiumSteel, 5*U, MT.Mo, U), MTx.HSSM2.mMeltingPoint, OM.stack(MTx.HSSM2, 45*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.PigIron, 8 *U, MT.Steel, 26*U, MT.TungstenSteel, 2*U, MTx.FeCr2, 3*U, MT.VanadiumSteel, 5*U, MT.Mo, U), MTx.HSSM2.mMeltingPoint, OM.stack(MTx.HSSM2, 45*U));
        // TMS-196
        RMx.EAF.addRecipe(0, OMx.stacks(MT.Ni, 42*U, MT.Cr      , 4 *U, MT.Al, 8*U, MT.Co, 4*U, MT.Ru, 2*U, MT.Ta, U, MT .Mo       , U  , MT.W, U, MT.Re, U), MTx.TMS196.mMeltingPoint, OM.stack(MTx.TMS196, 64*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.Ni, 26*U, MT.Nichrome, 20*U, MT.Al, 8*U, MT.Co, 4*U, MT.Ru, 2*U, MT.Ta, U, MT .Mo       , U  , MT.W, U, MT.Re, U), MTx.TMS196.mMeltingPoint, OM.stack(MTx.TMS196, 64*U));
        RMx.EAF.addRecipe(0, OMx.stacks(MT.Ni, 29*U, MT.Nichrome, 10*U, MT.Al, 8*U, MT.Co, 3*U, MT.Ru, 2*U, MT.Ta, U, MTx.Hastelloy, 9*U, MT.W, U, MT.Re, U), MTx.TMS196.mMeltingPoint, OM.stack(MTx.TMS196, 64*U));
    }

    private void addOverrideRecipes() {
        RM.Centrifuge.addRecipe0(true, 64, 16, new long[]{9640, 100, 100, 100, 100, 100}, FL.Sluice.make(100), FL.Water.make(50), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Cu, 2), dustTiny.mat(MT.OREMATS.Cassiterite, 1), dustTiny.mat(MTx.ZnO, 1), dustTiny.mat(MTx.Sb2O3, 2), dustTiny.mat(MT.OREMATS.Chromite, 3));
        RM.MagneticSeparator.addRecipe1(true, 16, 16, new long[]{9640, 72, 72, 72, 72, 72}, dustTiny.mat(MT.SluiceSand, 1), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(true, 16, 36, new long[]{9640, 162, 162, 162, 162, 162}, dustSmall.mat(MT.SluiceSand, 1), dustSmall.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{9640, 648, 648, 648, 648, 648}, dust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(true, 16, 1296, new long[]{9640, 5832, 5832, 5832, 5832, 5832}, blockDust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 9), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));

        // Copper anode sludge: M2X (M = Cu, Pb, Bi, Ag, Au, Pt; X = Se, Te (O, S, As, Sb, Ba, SO4: mostly ignored))
        for (FluidStack water : FL.waters(3000)) {
            RM.Electrolyzer.addRecipe1(true, 64, 64, new long[]{10000, 150}, ST.tag(1), FL.array(MT.BlueVitriol.liquid(6 * U, true), water), FL.array(MT.H2SO4.liquid(7 * U, true), MT.O.gas(U, false)), OM.dust(MT.Cu), dustTiny.mat(MTx.CuAnodeSludge, 9));
            RM.Bath.addRecipe1(true, 16, 48, new long[]{4000, 2500, 2000, 1000, 300, 200}, dust.mat(MTx.CuAnodeSludgeRoast, 4), water, MTx.Na2TeSeO3Solution.liquid(6*U, false), dustTiny.mat(MT.Cu, 9), dustTiny.mat(MTx.PbO, 9), dustTiny.mat(MTx.Bi2O3, 9), dustTiny.mat(MT.Ag, 9), dustTiny.mat(MT.Au, 9), dustTiny.mat(MT.PlatinumGroupSludge, 9));
            RM.Mixer.addRecipe1(true, 16, 32, dust.mat(MTx.TeO2, 1), FL.array(MT.SO2.gas(6*U, true), FL.mul(water, 2)), MT.H2SO4.liquid(14*U, false), dust.mat(MT.Te, 1));
        }
        RM.BurnMixer.addRecipeX(true, 16, 48, ST.array(dust.mat(MTx.CuAnodeSludge, 3), dust.mat(MT.Na2CO3, 6)), MT.O.gas(2*U, true), MT.CO2.gas(3*U, false), dust.mat(MTx.CuAnodeSludgeRoast, 8));
        RM.Mixer.addRecipe1(true, 16, 168, ST.tag(0), FL.array(MTx.Na2TeSeO3Solution.liquid(24*U, true), MT.H2SO4.liquid(14*U, true)), MTx.Na2SO4H2SeO3Solution.liquid(35*U, false), dust.mat(MTx.TeO2, 1));
        RM.Mixer.addRecipe1(true, 16, 280, ST.tag(0), FL.array(MTx.Na2SO4H2SeO3Solution.liquid(35*U, true), MT.SO2.gas(6*U, true)), MTx.NaHSO4Solution.liquid(40*U, false), OM.dust(MT.Se));

        // Roasting
        for (String tOxygen : FluidsGT.OXYGEN)
            if (FL.exists(tOxygen)) {
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Realgar), FL.make(tOxygen, 1750), MT.SO2.gas(3 * U2, false), OM.dust(MTx.As2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stibnite), FL.make(tOxygen, 1800), MT.SO2.gas(9 * U5, false), OM.dust(MTx.Sb2O3, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Tetrahedrite), FL.make(tOxygen, 1125), MT.SO2.gas(9 * U8, false), OM.dust(MT.Cu, 3 * U8), OM.dust(MTx.Sb2O3, 5 * U16), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Arsenopyrite), FL.make(tOxygen, 1667), MT.SO2.gas(3 * U3, false), OM.dust(MT.Fe2O3, 5 * U6), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MTx.CdS), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.CdO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Molybdenite), FL.make(tOxygen, 2334), MTx.MoS2RoastingGas.gas(6 * U3, false), OM.dust(MTx.MoO3, 4 * U3));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Pentlandite), FL.make(tOxygen, 1471), MT.SO2.gas(24 * U17, false), OM.dust(MT.OREMATS.Garnierite, 9 * U17));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cobaltite), FL.make(tOxygen, 1611), MT.SO2.gas(3 * U3, false), OM.dust(MTx.Co3O4, 7 * U9), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Galena), FL.make(tOxygen, 875), MT.SO2.gas(6 * U8, false), OM.dust(MT.Ag, 3 * U8), OM.dust(MTx.PbO, 3 * U8));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cooperite), FL.make(tOxygen, 500), MT.SO2.gas(3 * U6, false), OM.dust(MT.PlatinumGroupSludge, 4 * U6), OM.dust(MT.OREMATS.Garnierite, U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stannite), FL.make(tOxygen, 1313), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MT.OREMATS.Cassiterite, U8), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Kesterite), FL.make(tOxygen, 1250), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MTx.ZnO, U8), OM.dust(MT.OREMATS.Cassiterite, U8));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Cinnabar), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.HgO, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MTx.FeS), FL.make(tOxygen, 1083), MT.SO2.gas(3 * U2, false), OM.dust(MT.Fe2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, OP.dust.mat(MT.OREMATS.Magnetite          , 7), FL.make(tOxygen, 500), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), FL.make(tOxygen, 500), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, OP.dust.mat(MT.OREMATS.BasalticMineralSand, 7), FL.make(tOxygen, 500), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.Se), FL.make(tOxygen, 2000), NF, OM.dust(MTx.SeO2, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sperrylite), FL.make(tOxygen, 1000), NF, OM.dust(MT.Pt, U3), OM.dust(MTx.As2O3, 5*U3));
            }

        final long[] tChances = new long[]{8000, 8000, 8000};

        for (String tAir : FluidsGT.AIR) {
            if (FL.exists(tAir)) {
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Realgar), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.As2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Stibnite), FL.make(tAir, 6000), MT.SO2.gas(9 * U5, false), OM.dust(MTx.Sb2O3, U));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Tetrahedrite), FL.make(tAir, 4000), MT.SO2.gas(9 * U8, false), OM.dust(MT.Cu, 3 * U8), OM.dust(MTx.Sb2O3, 5 * U16), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Arsenopyrite), FL.make(tAir, 4000), MT.SO2.gas(3 * U3, false), OM.dust(MT.Fe2O3, 5 * U6), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Sphalerite), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Molybdenite), FL.make(tAir, 6000), MT.SO2.gas(6 * U3, false), OM.dust(MTx.MoO3, 4 * U3));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Pentlandite), FL.make(tAir, 4000), MT.SO2.gas(24 * U17, false), OM.dust(MT.OREMATS.Garnierite, 9 * U17));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cobaltite), FL.make(tAir, 4000), MT.SO2.gas(3 * U3, false), OM.dust(MTx.Co3O4, 7 * U9), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Galena), FL.make(tAir, 2000), MT.SO2.gas(6 * U8, false), OM.dust(MT.Ag, 3 * U8), OM.dust(MTx.PbO, 3 * U8));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cooperite), FL.make(tAir, 2000), MT.SO2.gas(3 * U6, false), OM.dust(MT.PlatinumGroupSludge, 4 * U6), OM.dust(MT.OREMATS.Garnierite, U6));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Stannite), FL.make(tAir, 5000), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MT.OREMATS.Cassiterite, U8), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Kesterite), FL.make(tAir, 4000), MT.SO2.gas(12 * U8, false), OM.dust(MT.Cu, 2 * U8), OM.dust(MTx.ZnO, U8), OM.dust(MT.OREMATS.Cassiterite, U8));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Cinnabar), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MTx.HgO, U));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MTx.FeS), FL.make(tAir, 4000), MT.SO2.gas(3 * U2, false), OM.dust(MT.Fe2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.Magnetite, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15 * U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15 * U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OP.dust.mat(MT.OREMATS.BasalticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15 * U2));
                RM.Roasting.addRecipe1(true, 16, 512, tChances, OM.dust(MT.OREMATS.Sperrylite), FL.make(tAir, 1000), NF, OM.dust(MT.Pt, U3), OM.dust(MTx.As2O3, 5*U3));
            }
        }

        // Advanced Bayer
        // 2*Bauxite is interpreted as AlO(OH), so the equation is AlO(OH) + (Na,K)OH -> (Na,K)AlO2 + H2O
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dust     .mat(MT.OREMATS.Bauxite, 1), OP.dustSmall.mat(MT.KOH , 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.KAlO2 , 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustSmall.mat(MT.OREMATS.Bauxite, 4), OP.dustSmall.mat(MT.KOH , 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.KAlO2 , 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustTiny .mat(MT.OREMATS.Bauxite, 9), OP.dustSmall.mat(MT.KOH , 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.KAlO2 , 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dust     .mat(MT.OREMATS.Bauxite, 2), OP.dust     .mat(MT.KOH , 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.KAlO2 , 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustSmall.mat(MT.OREMATS.Bauxite, 8), OP.dust     .mat(MT.KOH , 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.KAlO2 , 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustTiny .mat(MT.OREMATS.Bauxite,18), OP.dust     .mat(MT.KOH , 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.KAlO2 , 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dust     .mat(MT.OREMATS.Bauxite, 1), OP.dustSmall.mat(MT.NaOH, 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.NaAlO2, 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustSmall.mat(MT.OREMATS.Bauxite, 4), OP.dustSmall.mat(MT.NaOH, 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.NaAlO2, 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 1500, OP.dustTiny .mat(MT.OREMATS.Bauxite, 9), OP.dustSmall.mat(MT.NaOH, 6), FL.Steam.make(48000), FL.DistW.make(300+ 750), OP.dust.mat(MT.NaAlO2, 2), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dust     .mat(MT.OREMATS.Bauxite, 2), OP.dust     .mat(MT.NaOH, 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.NaAlO2, 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustSmall.mat(MT.OREMATS.Bauxite, 8), OP.dust     .mat(MT.NaOH, 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.NaAlO2, 4), crushedCentrifuged.mat(MTx.RedMud, 1));
        RM.Autoclave.addRecipe2(true,  0, 3000, OP.dustTiny .mat(MT.OREMATS.Bauxite,18), OP.dust     .mat(MT.NaOH, 3), FL.Steam.make(96000), FL.DistW.make(600+1500), OP.dust.mat(MT.NaAlO2, 4), crushedCentrifuged.mat(MTx.RedMud, 1));

        RM.Bath.addRecipe1(true, 0, 512, dust.mat(MT.NaAlO2, 12), FL.array(MT.HF.gas(12*U, true)), FL.array(MT.Na3AlF6.liquid(10*U, false), MT.H2O.liquid(9*U, false)), dust.mat(MT.Al2O3, 5));
        for (FluidStack water : FL.waters(1000)) {
            RM.Bath.addRecipe1(true, 0, 2048, OP.dust.mat(MT.KAlO2, 4), FL.mul(water, 6), NF, OP.dust.mat(MT.AlO3H3, 4), OP.dust.mat(MT.AlO3H3, 3), OP.dust.mat(MT.KOH, 3));
            RM.Bath.addRecipe1(true, 0, 2048, OP.dust.mat(MT.NaAlO2, 4), FL.mul(water, 9), MTx.BayerLiquor.liquid(6*U, false), OP.dust.mat(MT.AlO3H3, 7));
        }

        for (OreDictMaterial mat : new OreDictMaterial[] {MT.Zn, MT.OREMATS.Sphalerite, MT.OREMATS.Smithsonite, MTx.ZnO}) {
            RM.Bath.addRecipe1(true, 0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurified    .mat(mat, 1), MT.H2SO4.liquid(7*U2, true), MTx.ZnLeachingSolution.fluid(9*U2, false), crushedCentrifuged.mat(mat, 1), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2));
            RM.Bath.addRecipe1(true, 0,  256, new long[] {10000, 5000, 5000, 5000, 5000, 5000}, crushedPurifiedTiny.mat(mat, 9), MT.H2SO4.liquid(7*U2, true), MTx.ZnLeachingSolution.fluid(9*U2, false), crushedCentrifuged.mat(mat, 1), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2), crushedCentrifugedTiny.mat(MTx.ZRR, 2));
        }

        RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate   , 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.MnO2, 6), nugget.mat(MT.OREMATS.Cassiterite, 12), nugget.mat(MTx.ZnO, 3));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate   , 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.MnO2, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 6));
        RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate   , 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 3));
        RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro      , 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro      , 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
        RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro      , 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Ag, 6), nugget.mat(MTx.PbO, 12), nugget.mat(MTx.Co3O4, 7));
        RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Ag, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 14));
        RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 7));
        if (IL.PFAA_Sands.exists()) {
            RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt , 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Au, 8), nugget.mat(MT.Cu, 16), nugget.mat(MT.OREMATS.Garnierite, 4));
            RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt , 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Au, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 8));
            RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt , 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 4));
            RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Ag, 8), nugget.mat(MTx.PbO, 16), nugget.mat(MTx.Co3O4, 9));
            RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Ag, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 18));
            RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 9));
        }
        if (IL.TROPIC_Sand_Black.exists()) {
            RM.Sifting          .addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
            RM.MagneticSeparator.addRecipe1(true, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
            RM.Centrifuge       .addRecipe1(true, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mFluidOutputs.length >= 1 && (
                    r.mFluidOutputs[0].getFluid().getName().equals("molten.stainlesssteel") ||
                    r.mFluidOutputs[0].getFluid().getName().equals("molten.arseniccopper") ||
                    r.mFluidOutputs[0].getFluid().getName().equals("molten.arsenicbronze")
            )) {
                r.mEnabled = false;
            } else if (r.mFluidInputs.length == 1 && r.mOutputs.length == 1 && (
                    (r.mFluidInputs[0].getFluid().getName().equals("chlorine") && r.mOutputs[0].isItemEqual(dust.mat(MT.FeCl3, 4)) && r.mFluidOutputs.length == 0) ||
                    (r.mFluidInputs[0].getFluid().getName().equals("hydrochloricacid") && r.mOutputs[0].isItemEqual(dust.mat(MT.FeCl2, 3)) && r.mFluidOutputs.length == 1 && r.mFluidOutputs[0].getFluid().getName().equals("hydrogen"))
            )) { // fixes infinite silica from Fe -> FeCl2/FeCl3 -> Fe2O3 -> Fe + slag
                r.mEnabled = false;
            } else if (r.mFluidInputs.length > 1 && r.mFluidInputs[0].getFluid().getName().equals("molten.pigiron")) {
                r.mEnabled = false;
            }
        }

        // Mixing arsenic alloys using vapour instead of liquid arsenic
        mix("molten.copper"        ,  3, "molten.tin", 1, "vapor.arsenic",  8, "molten.arsenicbronze",  5);
        mix("molten.annealedcopper",  3, "molten.tin", 1, "vapor.arsenic",  8, "molten.arsenicbronze",  5);
        mix("molten.bronze"        ,  4, "vapor.arsenic",  8, "molten.arsenicbronze",  5);
        mix("molten.copper"        ,  4, "vapor.arsenic",  8, "molten.arseniccopper",  5);
        mix("molten.annealedcopper",  4, "vapor.arsenic",  8, "molten.arseniccopper",  5);

        for (Recipe r : RM.Centrifuge.mRecipeList) {
            if (r.mInputs.length == 1 && r.mInputs[0].isItemEqual(dust.mat(MT.DarkAsh, 2))) {
                r.mEnabled = false;
            }
        }

        List<String> disableElectrolysisFluids = Arrays.asList(MT.GrayVitriol.mLiquid.getUnlocalizedName(), MT.GreenVitriol.mLiquid.getUnlocalizedName(), MT.PinkVitriol.mLiquid.getUnlocalizedName(), MT.BlackVitriol.mLiquid.getUnlocalizedName());

        for (Recipe r : RM.Electrolyzer.mRecipeList) {
            if (r.mFluidInputs.length > 1) {
                for (FluidStack input : r.mFluidInputs) {
                    if (disableElectrolysisFluids.contains(input.getUnlocalizedName()))
                        r.mEnabled = false;
                }
            }
        }
        for (FluidStack tWater : FL.waters(3000)) {
            RM.Electrolyzer.addRecipe2(true, 64, 128, dust.mat(MTx.NH4SO4, 7), dust.mat(MTx.SeO2, 1), FL.array(MT.GrayVitriol.liquid(6 * U, true), tWater), FL.array(MT.H2SO4.liquid(14 * U, false), MT.NH3.gas(2 * U, false), MT.O.gas(3 * U, false)), dust.mat(MT.Mn, 1), dust.mat(MT.Se, 1));
            RM.Electrolyzer.addRecipe1(true, 64, 128, dust.mat(MTx.CdSO4, 6), FL.array(tWater), FL.array(MT.H2SO4.liquid(7 * U, false), MT.O.gas(U, false)), dust.mat(MT.Cd, 1));
        }
        RM.Electrolyzer.addRecipe1(true, 64, 128, dust.mat(MTx.SeO2, 1), FL.array(MT.GrayVitriol.liquid(6 * U, true), MTx.NH4SO4Solution.liquid(10*U, true)), FL.array(MT.H2SO4.liquid(14 * U, false), MT.NH3.gas(2 * U, false), MT.O.gas(3 * U, false)), dust.mat(MT.Mn, 1), dust.mat(MT.Se, 1));

        Recipe x = RM.Centrifuge.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, OM.dust(MT.OREMATS.Cinnabar)); if (x != null) x.mEnabled = false;

        // Hints
        for (Recipe r : RM.DidYouKnow.mRecipeList) {
            if (r.mOutputs.length >= 1 && r.mOutputs[0] != null && (
                r.mOutputs[0].isItemEqual(dust.mat(MT.Steel, 1)) ||
                r.mOutputs[0].isItemEqual(IL.Bottle_Mercury.get(1))
            ))
                r.mEnabled = false;
        }

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                ST.make(dust.mat(MT.Fe2O3, 5), "Throw some Iron Ore into a crucible.")
                , ST.make(gem.mat(MT.Charcoal, 6), "Add some coke.")
                , IL.Ceramic_Crucible.getWithName(1, "Heat up the crucible using a burning box and wait until it all turns into sponge iron")
                , ST.make(scrapGt.mat(MTx.SpongeIron, 1), "Get the bloom out with a shovel")
                , ST.make(MTEx.gt6MTEReg.getItem(32028), "Get rid of slag and excess carbon by hammering the sponge iron scrap on any anvil to make wrought iron")
        ), ST.array(nugget.mat(MT.WroughtIron, 1), ingot.mat(MT.WroughtIron, 1), plate.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.FerrousSlag, 1), stick.mat(MT.WroughtIron, 1), gearGtSmall.mat(MT.WroughtIron, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                ST.make(ingot.mat(MT.PigIron, 1), "Throw some Pig Iron into a crucible. Do not forget to leave space for air!")
                , ST.make(dust.mat(MT.Dolomite, 1), "Add some lime and magnesia or dolomite.")
                , ST.make(MTEx.gt6MTEReg.getItem(1199), "Heat up the crucible using a Burning Box")
                , ST.make(MTEx.gt6MTEReg.getItem(1302), "Point a running engine into the crucible to blow air")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it all turns into Steel and pour it into a mold")
                , ST.make(MTEx.gt6xMTEReg.getItem(MTEx.IDs.BOF.get()), "Build a Basic Oxygen Converter or Electric Arc Furnace if you want this process to be more efficient")
        ), ST.array(dust.mat(MT.Steel, 1), ingot.mat(MT.Steel, 1), plate.mat(MT.Steel, 1), scrapGt.mat(MT.Steel, 1), stick.mat(MT.Steel, 1), gearGt.mat(MT.Steel, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                ST.make(dust.mat(MT.OREMATS.Cinnabar, 3), "Throw three units of Cinnabar into a crucible")
                , ST.make(dust.mat(MTx.HgO, 2), "Or two units of Mercuric Oxide produced by roasting the cinnabar first!")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it melts into Mercury")
                , IL.Bottle_Empty.getWithName(1, "Rightclick the crucible with an empty bottle")
                , ST.make(MTEx.gt6MTEReg.getItem(1199), "Heat up the crucible using a Burning Box")
                , ST.make(Blocks.redstone_ore, 1, 0, "Using a Club to mine vanilla Redstone Ore gives Cinnabar")
        ), ST.array(IL.Bottle_Mercury.get(1), ST.make(ingot.mat(MT.Hg, 1), "Pouring this into molds only works with additional cooling!"), ST.make(nugget.mat(MT.Hg, 1), "Pouring this into molds only works with additional cooling!")), null, ZL_LONG, FL.array(MT.Hg.liquid(1, true)), FL.array(MT.Hg.liquid(1, true)), 0, 0, 0);
    }

    private void overrideWorldgen() {
        HashSet<String> toDisable = new HashSet<>(Arrays.asList(
                "ore.small.zinc",
                "ore.small.tin",
                "ore.small.lead",
                "ore.large.lead",
                "ore.large.nickel"
        ));

        for (WorldgenObject obj : CS.ORE_OVERWORLD) {
            if (toDisable.contains(obj.mName)) {
                obj.mEnabled = false;
            }
        }

        new WorldgenOresLarge("ore.large.galena2"      , true, true, 30,  60,  40, 5, 16, MT.OREMATS.Galena               , MT.OREMATS.Galena              , MT.Ag                          , MT.OREMATS.Galena     , ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS, ORE_PLANETS);
        new WorldgenOresLarge("ore.large.nickel2"      , true, true, 10,  40,  40, 3, 16, MT.OREMATS.Garnierite           , MT.OREMATS.Garnierite          , MT.OREMATS.Cobaltite           , MT.OREMATS.Pentlandite, ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM, ORE_BETWEENLANDS, ORE_MARS);
        new WorldgenOresLarge("ore.large.graphite2"    , true, true, 50,  80,  80, 6, 32, MT.Coal                         , MT.Coal                        , MT.Graphite                    , MT.Graphite           , ORE_OVERWORLD, ORE_A97, ORE_ENVM, ORE_CW2_AquaCavern, ORE_CW2_Caveland, ORE_CW2_Cavenia, ORE_CW2_Cavern, ORE_CW2_Caveworld, ORE_EREBUS, ORE_ATUM);
    }
}
