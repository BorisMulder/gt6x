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
import gregapi.util.CR;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import org.altadoon.gt6x.common.Config;
import org.altadoon.gt6x.common.MTEx;
import org.altadoon.gt6x.common.MTx;
import org.altadoon.gt6x.features.GT6XFeature;

import java.util.*;

import static gregapi.data.CS.*;
import static gregapi.data.OP.*;
import static gregapi.data.TD.Atomic.ANTIMATTER;
import static gregapi.data.TD.Processing.EXTRUDER;
import static gregapi.oredict.OreDictMaterialCondition.fullforge;
import static org.altadoon.gt6x.Gt6xMod.MOD_ID;

public class Metallurgy extends GT6XFeature {
    public static final String FEATURE_NAME = "Metallurgy";

    public Recipe.RecipeMap blastFurnace = null;
    public Recipe.RecipeMap sintering = null;
    public OreDictPrefix sinter = null;

    @Override
    public String name() {
        return FEATURE_NAME;
    }

    @Override
    public void configure(Config config) {}

    @Override
    public void preInit() {
        createPrefixes();
        changeMaterialProperties();
        changeByProducts();
        addRecipeMaps();
    }

    @Override
    public void afterPreInit() {
        changeAlloySmeltingRecipes();
    }

    @Override
    public void init() {}

    @Override
    public void beforePostInit() {
        addMTEs();
        addOverrideRecipes();
    }

    @Override
    public void postInit() {
        addRecipes();
    }

    @Override
    public void afterPostInit() {
        changeRecipes();
    }

    private void createPrefixes() {
        sinter = OreDictPrefix.createPrefix("sinter")
            .setCategoryName("Sinters")
            .setLocalItemName("", " Sinter")
            .aspects(gregapi.data.TC.FABRICO, 1); // Thaumcraft Aspects related to this Prefix.
        new PrefixItem(MOD_ID, MD.GT.mID, "gt6x.meta.sinter", sinter,
            MT.Empty, MT.Fe2O3, MT.OREMATS.Magnetite, MT.OREMATS.Garnierite, MT.OREMATS.Cassiterite, MT.OREMATS.Chromite, MTx.PbO, MTx.ZnO, MT.MnO2, MTx.Co3O4, MT.SiO2, MTx.Sb2O3
        );
    }

    private void changeMaterialProperties() {
        MT.PigIron.setPulver(MT.PigIron, U).setSmelting(MT.PigIron, U);
        MT.OREMATS.Garnierite.setSmelting(MT.OREMATS.Garnierite, U).remove(TD.Processing.ELECTROLYSER);
        MT.OREMATS.Cobaltite.setSmelting(MT.OREMATS.Cobaltite, U);
        MT.MnO2.setSmelting(MT.MnO2, U);
        MT.OREMATS.Smithsonite.remove(TD.Processing.ELECTROLYSER);

        // TODO increase tool stats for HSS
    }

    private void changeByProducts() {
        MT.OREMATS.Cobaltite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Co.mID);
        MT.OREMATS.Stibnite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Sb.mID);
        MT.OREMATS.Sphalerite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Zn.mID);
        MT.OREMATS.Garnierite.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Ni.mID);
        MT.OREMATS.Galena.mByProducts.removeIf(byproduct -> byproduct.mID == MT.Pb.mID);

        for (OreDictMaterial mat : new OreDictMaterial[] {MT.OREMATS.Sperrylite, MT.OREMATS.Tetrahedrite, MT.Cu, MT.OREMATS.Cooperite, MT.MeteoricIron, MT.Cu, MT.Ga, MT.Ag, MT.Au, MT.Pt, MT.OREMATS.YellowLimonite, MT.OREMATS.Stolzite, MT.OREMATS.Pinalite }) {
            ListIterator<OreDictMaterial> it = mat.mByProducts.listIterator();
            while (it.hasNext()) {
                OreDictMaterial byproduct = it.next();
                if (byproduct.mID == MT.Ni.mID) {
                    it.set(MT.OREMATS.Garnierite);
                } else if (byproduct.mID == MT.Pb.mID) {
                    it.set(MT.OREMATS.Galena);
                } else if (byproduct.mID == MT.Zn.mID) {
                    it.set(MT.OREMATS.Sphalerite);
                } else if (byproduct.mID == MT.Sb.mID) {
                    it.set(MT.OREMATS.Stibnite);
                } else if (byproduct.mID == MT.Co.mID) {
                    it.set(MT.OREMATS.Cobaltite);
                }
            }
        }
    }

    private void addRecipeMaps() {
        blastFurnace = new Recipe.RecipeMap(null, "gt6x.recipe.blastfurnace", "Blast Furnace", null, 0, 1, RES_PATH_GUI+"machines/BlastFurnace",6 , 3, 1, 3, 3, 0, 1, 1, "", 1, "", true, true, true, true, false, true, T);
        sintering = new Recipe.RecipeMap(null, "gt6x.recipe.sintering", "Sintering", null, 0, 1, RES_PATH_GUI+"machines/Sintering",3 , 1, 1, 0, 0, 0, 1, 1, "", 1, "", true, true, true, true, false, true, true);
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
        for (OreDictMaterial mat : new OreDictMaterial[]{ MT.Si, MT.Fe, MT.Steel, MT.MeteoricSteel, MT.StainlessSteel, MT.TungstenCarbide }) {
            removeAlloySmeltingRecipe(mat);
        }

        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 3, OM.stack(MT.Fe2O3                         , 5*U), OM.stack(MT.C, 3*U), OM.stack(MT.CaCO3, U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.Magnetite             ,14*U), OM.stack(MT.C, 9*U), OM.stack(MT.CaCO3, 3*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.BasalticMineralSand   ,14*U), OM.stack(MT.C, 9*U), OM.stack(MT.CaCO3, 3*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.GraniticMineralSand   ,14*U), OM.stack(MT.C, 9*U), OM.stack(MT.CaCO3, 3*U)));
        MTx.SpongeIron.addAlloyingRecipe(new OreDictConfigurationComponent( 9, OM.stack(MT.OREMATS.Ferrovanadium         ,28*U), OM.stack(MT.C, 9*U), OM.stack(MT.CaCO3, 3*U)));

        // pig iron C content: around 4% (weight), Fe: 96% * 12u / 56u = 20,5 ==> 2 Fe20C + Fe2O3 -> 42 Fe + CO + CO2
        MT.WroughtIron.addAlloyingRecipe(new OreDictConfigurationComponent(21, OM.stack(MT.PigIron,20*U), OM.stack(MT.Fe2O3, 5*U2)));

        MT.Steel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.PigIron, U), OM.stack(MT.Air, U)));
        // TODO add steel recipe from Pig or Sponge Iron with O2 to EAF

        //MT.MeteoricSteel.addAlloyingRecipe(new OreDictConfigurationComponent( 1, OM.stack(MT.MeteoricIron, U), OM.stack(MT.C, U72), OM.stack(MT.Air, U)));
        // TODO add these two recipes to EAF (maybe use CO instead of O?)

        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,16*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Cr, 4*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,24*U), OM.stack(MT.Nichrome, 5*U), OM.stack(MT.Cr, 3*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));
        MT.StainlessSteel.addAlloyingRecipe(new OreDictConfigurationComponent(36, OM.stack(MT.PigIron,14*U), OM.stack(MTx.FeCr2, 6*U), OM.stack(MT.Invar, 12*U), OM.stack(MT.Mn, 4*U), OM.stack(MT.Air, 24*U)));

        MT.Invar.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.PigIron,2*U), OM.stack(MT.Ni, U)));
        MT.TinAlloy.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.PigIron, U), OM.stack(MT.Sn, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(3, OM.stack(MT.PigIron, U), OM.stack(MT.Al, U), OM.stack(MT.Cr, U)));
        MT.Kanthal.addAlloyingRecipe(new OreDictConfigurationComponent(6, OM.stack(MT.PigIron, U), OM.stack(MT.Al, 2*U), OM.stack(MTx.FeCr2, 3*U)));
        MT.Angmallen.addAlloyingRecipe(new OreDictConfigurationComponent(2, OM.stack(MT.PigIron, U), OM.stack(MT.Au, U)));
        // TODO add these two recipes to EAF (with and without DRI)

        //TODO Iron Carbide/Carburizing Process
        //TODO iron/wrought iron + CO -> Cementite or Steel (roasting oven?)
    }

    private void addMTEs() {
        OreDictMaterial aMat;
        aMat = MT.WroughtIron; MTEx.gt6xMTEReg.add("Blast Furnace Part", "Multiblock Machines", 60, 17101, MultiTileEntityMultiBlockPart.class, aMat.mToolQuality, 64, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_TEXTURE, "blastfurnaceparts", NBT_DESIGNS, 1), "hP ", "PF ", "   ", 'P', plate.dat(MT.WroughtIron), 'F', MTEx.gt6Registry.getItem(18000));
                               MTEx.gt6xMTEReg.add("Blast Furnace", "Multiblock Machines", 61, 17101, MultiTileEntityBlastFurnace.class, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS, 6.0F, NBT_RESISTANCE, 6.0F, NBT_INPUT, 32, NBT_INPUT_MIN, 8, NBT_INPUT_MAX, 32, NBT_TEXTURE, "blastfurnace", NBT_ENERGY_ACCEPTED, TD.Energy.KU, NBT_RECIPEMAP, blastFurnace, NBT_INV_SIDE_AUTO_OUT, SIDE_LEFT, NBT_TANK_SIDE_AUTO_OUT, SIDE_TOP, NBT_CHEAP_OVERCLOCKING, true, NBT_NEEDS_IGNITION, true), "PwP", "PRh", "yFP", 'P', plateCurved.dat(MT.WroughtIron), 'R', stickLong.dat(MT.WroughtIron), 'F', MTEx.gt6xMTEReg.getItem(60));

        Class<? extends TileEntity> aClass = MultiTileEntityBasicMachine.class;
        aMat = MT.DATA.Heat_T[1]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 62, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   6.0F, NBT_RESISTANCE,   6.0F, NBT_INPUT,   32, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateDouble   .dat(ANY.Cu), 'P', plate.dat(MT.Ceramic), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1005));
        aMat = MT.DATA.Heat_T[2]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 63, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   4.0F, NBT_RESISTANCE,   4.0F, NBT_INPUT,  128, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateTriple   .dat(ANY.Cu), 'P', plate.dat(MT.Ta     ), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1036));
        aMat = MT.DATA.Heat_T[3]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 64, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,   9.0F, NBT_RESISTANCE,   9.0F, NBT_INPUT,  512, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateQuadruple.dat(ANY.Cu), 'P', plate.dat(MT.W      ), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1024));
        aMat = MT.DATA.Heat_T[4]; MTEx.gt6xMTEReg.add("Sintering Oven ("+aMat.getLocal()+")", "Basic Machines", 65, 20001, aClass, aMat.mToolQuality, 16, MTEx.MachineBlock, UT.NBT.make(NBT_MATERIAL, aMat, NBT_HARDNESS,  12.5F, NBT_RESISTANCE,  12.5F, NBT_INPUT, 2048, NBT_TEXTURE, "sinteringoven", NBT_ENERGY_ACCEPTED, TD.Energy.HU, NBT_RECIPEMAP, sintering, NBT_INV_SIDE_IN, SBIT_L, NBT_INV_SIDE_AUTO_IN, SIDE_LEFT, NBT_INV_SIDE_OUT, SBIT_R, NBT_INV_SIDE_AUTO_OUT, SIDE_RIGHT, NBT_ENERGY_ACCEPTED_SIDES, SBIT_D), "wUh", "PMP", "BCB", 'M', casingMachineDouble.dat(aMat), 'C', plateQuintuple.dat(ANY.Cu), 'P', plate.dat(MT.Ta4HfC5), 'B', Blocks.brick_block, 'U', MTEx.gt6Registry.getItem(1043));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ICondition<OreDictMaterial>
            lowHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmax(3300), fullforge()),
            highHeatSintering = new ICondition.And(ANTIMATTER.NOT, EXTRUDER, OreDictMaterialCondition.meltmin(3300), fullforge());


    private void addRecipes() {
        // slag melts @ 1540
        OreDictMaterial[][] smeltLiquidSlag = new OreDictMaterial[][] {
                {MT.Fe2O3, MT.PigIron}, {MT.OREMATS.Magnetite, MT.PigIron}, {MT.OREMATS.Chromite, MTx.FeCr2},
        };
        OreDictMaterial[][] smeltSolidSlag = new OreDictMaterial[][] {
                {MT.OREMATS.Garnierite, MT.Ni}, {MT.OREMATS.Cassiterite, MT.Sn}, {MTx.PbO, MT.Pb}, {MT.MnO2, MT.Mn}, {MTx.Co3O4, MT.Co}, {MT.SiO2, MT.Si}, {MTx.Sb2O3, MT.Sb}
        };

        for (ItemStack coal : new ItemStack[]{dust.mat(MT.Charcoal, 1), dust.mat(MT.CoalCoke, 1), dust.mat(MT.LigniteCoke, 3), dust.mat(MT.C, 1), gem.mat(MT.Charcoal, 1), gem.mat(MT.CoalCoke, 1), gem.mat(MT.LigniteCoke, 3)}) {
            // ore dusts (less efficent)
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.Fe2O3, 5), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Magnetite, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)}, ZL_IS, null, null, FL.array(FL.Air.make(22500)), FL.array(MT.PigIron.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.BasalticMineralSand, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)}, ZL_IS, null, null, FL.array(FL.Air.make(22500)), FL.array(MT.PigIron.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.GraniticMineralSand, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)}, ZL_IS, null, null, FL.array(FL.Air.make(22500)), FL.array(MT.PigIron.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.YellowLimonite, 8), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.BrownLimonite, 8), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.PigIron.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Ni.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Sn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Chromite, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)}, ZL_IS, null, null, FL.array(FL.Air.make(22500)), FL.array(MTx.FeCr2.liquid(6*U, false), MTx.Slag.liquid(3*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MTx.PbO, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Pb.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MTx.ZnO, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(12*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.MnO2, 2), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{gem.mat(MTx.Slag, 1)}, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Mn.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MTx.Co3O4, 14), ST.mul(9, coal), dust.mat(MT.CaCO3, 3)}, new ItemStack[]{gem.mat(MTx.Slag, 3)}, null, null, FL.array(FL.Air.make(22500)), FL.array(MT.Co.liquid(6*U, false), MTx.BlastFurnaceGas.gas(36*U, false)), 768, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MT.SiO2, 6), ST.mul(3, coal)}, ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Si.liquid(2*U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);
            blastFurnace.addRecipe(true, new ItemStack[]{dust.mat(MTx.Sb2O3, 5), ST.mul(3, coal), dust.mat(MT.CaCO3, 1)}, ZL_IS, null, null, FL.array(FL.Air.make(7500)), FL.array(MT.Sb.liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(15*U, false)), 256, 8, 0);

            // sintered pellets (more efficient)
            for (OreDictMaterial[] mats : smeltLiquidSlag) {
                blastFurnace.addRecipe2(true, 8, 128, sinter.mat(mats[0], 8), ST.mul(1, coal), FL.array(FL.Air.make(5000)), FL.array(mats[1].liquid(2*U, false), MTx.Slag.liquid(U, false), MTx.BlastFurnaceGas.gas(10*U, false)));
            }
            for (OreDictMaterial[] mats : smeltSolidSlag) {
                blastFurnace.addRecipe2(true, 8, 128, sinter.mat(mats[0], 8), ST.mul(1, coal), FL.array(FL.Air.make(5000)), FL.array(mats[1].liquid(2*U, false), MTx.BlastFurnaceGas.gas(10*U, false)), gem.mat(MTx.Slag, 1));
            }
            blastFurnace.addRecipe2(true, 8, 128, sinter.mat(MTx.ZnO, 8), ST.mul(1, coal), FL.array(FL.Air.make(5000)), FL.array(MTx.ZnBlastFurnaceGas.gas(12*U, false)), gem.mat(MTx.Slag, 1));
        }

        // blast furnace gas
        RM.CryoDistillationTower.addRecipe0(true, 64,  128, FL.array(MTx.BlastFurnaceGas.gas(4*U10, true)), FL.array(MT.N.gas(177*U1000, false), MT.CO.gas(4*20*U1000, T), MT.CO2.gas(4*30*U1000, T), MT.H.gas(4*5*U1000, T), MT.He.gas(U1000, T), MT.Ne.gas(U1000, T), MT.Ar.gas(U1000, T)));
        FM.Burn.addRecipe0(true, -16, 1, MTx.BlastFurnaceGas.gas(U1000, true), FL.CarbonDioxide.make(1), ZL_IS);
        RM.Distillery.addRecipe1(true, 16, 64, ST.tag(0), FL.array(MTx.ZnBlastFurnaceGas.gas(7*U, true)), FL.array(MT.Zn.liquid(U, false), MTx.BlastFurnaceGas.gas(6*U, false)));

        //TODO solidify Zn, As gas and reduce As2O3

        // Misc metal processing
        RM.Anvil.addRecipe2(false, 64, 192, scrapGt.mat(MTx.SpongeIron, 9), scrapGt.mat(MTx.SpongeIron, 9), ingot.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.Slag, 6));

        //TODO direct reduction or electrowinning of Sn, Pb?
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.ZnO, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.WhiteVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.OREMATS.Garnierite, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.CyanVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MTx.CoO, 2), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.RedVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false)));
        RM.Bath.addRecipe1(true, 0, 256, dust.mat(MT.MnO2, 1), FL.array(MT.H2SO4.liquid(7*U, true)), FL.array(MT.GrayVitriol.liquid(6*U, false), MT.H2O.liquid(3*U, false), MT.O.gas(U, false)));

        RM.Mixer.addRecipe2(true, 16, 128, ST.tag(1), dust.mat(MT.FeCl2, 6), FL.array(MT.H2O.liquid(6*U, true), MT.O.gas(U, true)), FL.array(MT.HCl.gas(8*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Mixer.addRecipe0(true, 16, 128, FL.array(MT.H2SO4.liquid(7*U, true), MT.NH3.gas(2*U, true)), ZL_FS, dust.mat(MTx.NH4SO4, 9));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Realgar, 2), dust.mat(MT.Fe, 1), ZL_FS, FL.array(MT.As.gas(U, false)), dust.mat(MTx.FeS, 2));
        RM.Mixer.addRecipe2(true, 16, 64, dust.mat(MT.OREMATS.Stibnite, 5), dust.mat(MT.Fe, 3), ZL_FS, FL.array(MT.Sb.liquid(2*U, false)), dust.mat(MTx.FeS, 6));

        //TODO use thermolyzer
        RM.Drying.addRecipe1(true, 16, 64, dust.mat(MTx.Co3O4, 7), NF, MT.O.gas(U, false), dust.mat(MTx.CoO, 6));
        RM.Drying.addRecipe0(true, 16, 128, FL.array(MT.GreenVitriol.liquid(12*U, true)), FL.array(MT.SO2.gas(3*U, false), MT.SO3.gas(4*U, false)), dust.mat(MT.Fe2O3, 5));
        RM.Drying.addRecipe1(true, 16, 64, dust.mat(MTx.HgO, 1), ZL_FS, FL.array(MT.Hg.liquid(U2, false), MT.O.gas(U2, false)));
        RM.Drying.addRecipe1(true, 16, 64, dust.mat(MT.OREMATS.Smithsonite, 5), ZL_FS, FL.array(MT.CO2.gas(3*U, false)), dust.mat(MTx.ZnO, 1));

        for (FluidStack tWater : FL.waters(3000)) {
            RM.Electrolyzer.addRecipe2(true, 64, 128, dust.mat(MTx.NH4SO4, 9), dust.mat(MTx.SeO2, 3), FL.array(MT.GrayVitriol.liquid(6 * U, true), tWater), FL.array(MT.H2SO4.liquid(14*U, false), MT.NH3.gas(2*U, false), MT.O.gas(3*U, false)), dust.mat(MT.Mn, 1), dust.mat(MT.Se, 1));
        }

        // Sintering dusts into chunks
        sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 16, 0, 0, NF, ingot , 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 16, 0, 0, NF, chunk , 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 16, 0, 0, NF, nugget, 1, NI, NI, true, false, false, lowHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dust,      1, NF, 96, 0, 0, NF, ingot , 1, NI, NI, true, false, false, highHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustSmall, 1, NF, 96, 0, 0, NF, chunk , 1, NI, NI, true, false, false, highHeatSintering));
        sintering.add(new RecipeMapHandlerPrefixSintering(dustTiny,  1, NF, 96, 0, 0, NF, nugget, 1, NI, NI, true, false, false, highHeatSintering));

        // misc sintering
        sintering.addRecipe2(true, 16, 64, dust.mat(MTx.CoO, 2), dust.mat(MT.Al2O3, 5), dust.mat(MTx.CobaltBlue, 7));

        for (ItemStack coal : new ItemStack[]{dust.mat(MT.Charcoal, 1), dust.mat(MT.LigniteCoke, 3),  dust.mat(MT.CoalCoke, 1), dust.mat(MT.C, 1) }) {
            //TODO add cobalt
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.W, 1), ST.copy(coal)}, new ItemStack[]{ingot.mat(MT.TungstenCarbide, 2)}, null, null, ZL_FS, ZL_FS, 256, 96, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.Ta, 4), dust.mat(MT.Hf, 1), ST.mul(5, coal)}, new ItemStack[]{ingot.mat(MT.Ta4HfC5, 10)}, null, null, ZL_FS, ZL_FS, 512, 96, 0);

            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.Fe2O3, 5), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MT.Fe2O3, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Magnetite, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)}, new ItemStack[]{sinter.mat(MT.OREMATS.Magnetite, 24)}, null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.BasalticMineralSand, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)}, new ItemStack[]{sinter.mat(MT.OREMATS.Magnetite, 24)}, null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.GraniticMineralSand, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)}, new ItemStack[]{sinter.mat(MT.OREMATS.Magnetite, 24)}, null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.YellowLimonite, 8), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MT.Fe2O3, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.BrownLimonite, 8), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MT.Fe2O3, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Garnierite, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MT.OREMATS.Garnierite, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Cassiterite, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MT.OREMATS.Cassiterite, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.OREMATS.Chromite, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)}, new ItemStack[]{sinter.mat(MT.OREMATS.Chromite, 24)}, null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MTx.PbO, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MTx.PbO, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MTx.ZnO, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MTx.ZnO, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.MnO2, 2), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MT.MnO2, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MTx.Co3O4, 14), ST.mul(3, coal), dust.mat(MT.CaCO3, 3)}, new ItemStack[]{sinter.mat(MTx.Co3O4, 24)}, null, null, ZL_FS, ZL_FS, 96, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MT.SiO2, 6), ST.mul(1, coal)}, new ItemStack[]{sinter.mat(MT.SiO2, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
            sintering.addRecipe(true, new ItemStack[]{dust.mat(MTx.Sb2O3, 5), ST.mul(1, coal), dust.mat(MT.CaCO3, 1)}, new ItemStack[]{sinter.mat(MTx.Sb2O3, 8)}, null, null, ZL_FS, ZL_FS, 32, 16, 0);
        }

        // mixing from/to molten ferrochrome and pig iron
        for (String tIron : new String[] {"molten.iron", "molten.wroughtiron", "molten.pigiron", "molten.meteoriciron"}) {
            RM.Mixer.addRecipe1(T, 16, 2 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.gold",    1)), FL.make_("molten.angmallen", 2), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 2 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.tin",    1)), FL.make_("molten.tinalloy", 2), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 3 , ST.tag(2), FL.array(FL.make_(tIron, 2 ), FL.make_("molten.nickel",    1)), FL.make_("molten.invar", 3), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 3 , ST.tag(2), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.chromium",    2)), FL.make_("molten.ferrochrome", 3), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 3 , ST.tag(3), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.chromium", 1), FL.make_("molten.aluminium" , 1)), FL.make("molten.kanthal", 3), ZL_IS);
            RM.Mixer.addRecipe1(T, 16, 6 , ST.tag(3), FL.array(FL.make_(tIron, 1 ), FL.make_("molten.ferrochrome", 3), FL.make_("molten.aluminium" , 2)), FL.make("molten.kanthal", 6), ZL_IS);
        }
    }

    private void addOverrideRecipes() {
        RM.Centrifuge.addRecipe0(T, 64, 16, new long[]{9640, 100, 100, 100, 100, 100}, FL.Sluice.make(100), FL.Water.make(50), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Cu, 2), dustTiny.mat(MT.OREMATS.Cassiterite, 1), dustTiny.mat(MTx.ZnO, 1), dustTiny.mat(MT.Sb, 1), dustTiny.mat(MT.OREMATS.Chromite, 3));
        RM.MagneticSeparator.addRecipe1(T, 16, 16, new long[]{9640, 72, 72, 72, 72, 72}, dustTiny.mat(MT.SluiceSand, 1), dustTiny.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(T, 16, 36, new long[]{9640, 162, 162, 162, 162, 162}, dustSmall.mat(MT.SluiceSand, 1), dustSmall.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{9640, 648, 648, 648, 648, 648}, dust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 1), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));
        RM.MagneticSeparator.addRecipe1(T, 16, 1296, new long[]{9640, 5832, 5832, 5832, 5832, 5832}, blockDust.mat(MT.SluiceSand, 1), dust.mat(MT.Stone, 9), dustTiny.mat(MT.Fe2O3, 5), dustTiny.mat(MT.Nd, 1), dustTiny.mat(MT.OREMATS.Garnierite, 1), dustTiny.mat(MTx.Co3O4, 2), dustTiny.mat(MT.MnO2, 1));


        // Roasting
        for (String tOxygen : FluidsGT.OXYGEN)
            if (FL.exists(tOxygen)) {
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Realgar), FL.make(tOxygen, 1750), MT.SO2.gas(3 * U2, false), OM.dust(MTx.As2O3, 5 * U4));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Stibnite), FL.make(tOxygen, 1800), MT.SO2.gas(9 * U5, false), OM.dust(MTx.Sb2O3, U));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Tetrahedrite), FL.make(tOxygen, 1125), MT.SO2.gas(9 * U8, false), OM.dust(MT.Cu, 3 * U8), OM.dust(MTx.Sb2O3, 5 * U16), OM.dust(MT.Fe2O3, 5 * U16));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Arsenopyrite), FL.make(tOxygen, 1667), MT.SO2.gas(3 * U3, false), OM.dust(MT.Fe2O3, 5 * U6), OM.dust(MTx.As2O3, 5 * U6));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Sphalerite), FL.make(tOxygen, 1500), MT.SO2.gas(3 * U2, false), OM.dust(MTx.ZnO, U2));
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.OREMATS.Molybdenite), FL.make(tOxygen, 2334), MT.SO2.gas(6 * U3, false), OM.dust(MTx.MoO3, 4 * U3));
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
                RM.Roasting.addRecipe1(true, 16, 512, OM.dust(MT.Se), FL.make(tOxygen, 2000), NF, OM.dust(MTx.SeO2, 3*U));
            }

        final long[] tChances = new long[]{8000, 8000, 8000};

        for (String tAir : FluidsGT.AIR)
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
                RM.Roasting.addRecipe1(true, 16, 512,           OP.dust.mat(MT.OREMATS.Magnetite          , 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512,           OP.dust.mat(MT.OREMATS.GraniticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15*U2));
                RM.Roasting.addRecipe1(true, 16, 512,           OP.dust.mat(MT.OREMATS.BasalticMineralSand, 7), FL.make(tAir, 2000), NF, OM.dust(MT.Fe2O3, 15*U2));
            }

        RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.MnO2, 6), nugget.mat(MT.OREMATS.Cassiterite, 12), nugget.mat(MTx.ZnO, 3));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.MnO2, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 6));
        RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 0), dust.mat(MT.STONES.Deepslate, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.OREMATS.Cassiterite, 6), dustTiny.mat(MTx.ZnO, 3));
        RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
        RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 1), dust.mat(MT.STONES.Gabbro, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Ag, 6), nugget.mat(MTx.PbO, 12), nugget.mat(MTx.Co3O4, 7));
        RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Ag, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 14));
        RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, ST.make(BlocksGT.Sands, 1, 2), dust.mat(MT.STONES.GraniteBlack, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MTx.PbO, 6), dustTiny.mat(MTx.Co3O4, 7));
        if (IL.PFAA_Sands.exists()) {
            RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt, 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Au, 8), nugget.mat(MT.Cu, 16), nugget.mat(MT.OREMATS.Garnierite, 4));
            RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt, 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Au, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 8));
            RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 0), dust.mat(MT.STONES.Basalt, 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MT.Cu, 8), dustTiny.mat(MT.OREMATS.Garnierite, 4));
            RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 4), nugget.mat(MT.Ag, 8), nugget.mat(MTx.PbO, 16), nugget.mat(MTx.Co3O4, 9));
            RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 4), dust.mat(MT.RedSand, 8), dustTiny.mat(MT.Ag, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 18));
            RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.PFAA_Sands.getWithMeta(1, 3), dust.mat(MT.STONES.Granite, 8), dust.mat(MT.RedSand, 4), dustTiny.mat(MT.V2O5, 16), dustTiny.mat(MTx.PbO, 8), dustTiny.mat(MTx.Co3O4, 9));
        }
        if (IL.TROPIC_Sand_Black.exists()) {
            RM.Sifting.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 3), nugget.mat(MT.Au, 6), nugget.mat(MT.Cu, 12), nugget.mat(MT.OREMATS.Garnierite, 3));
            RM.MagneticSeparator.addRecipe1(T, 16, 144, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 3), dust.mat(MT.RedSand, 6), dustTiny.mat(MT.Au, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 6));
            RM.Centrifuge.addRecipe1(T, 16, 288, new long[]{7500, 5000, 2500, 2500, 2500}, IL.TROPIC_Sand_Black.get(1), dust.mat(MT.STONES.Basalt, 6), dust.mat(MT.RedSand, 3), dustTiny.mat(MT.V2O5, 12), dustTiny.mat(MT.Cu, 6), dustTiny.mat(MT.OREMATS.Garnierite, 3));
        }
    }

    private void changeRecipes() {
        for (Recipe r : RM.Mixer.mRecipeList) {
            if (r.mFluidOutputs.length >= 1 && r.mFluidOutputs[0].getFluid().getName().equals("molten.stainlesssteel"))
                r.mEnabled = false;
        }
        for (Recipe r : RM.Electrolyzer.mRecipeList) {
            if (r.mFluidInputs.length > 1) {
                String inputFluidName = r.mFluidInputs[1].getFluid().getName();
                if (inputFluidName.equals("greenvitriol") || inputFluidName.equals("cyanvitriol"))
                    r.mEnabled = false;
            }
        }

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
                , ST.make(dust.mat(MT.CaCO3, 1), "Add some flux.")
                , ST.make(gem.mat(MT.Charcoal, 6), "And some coke.")
                , IL.Ceramic_Crucible.getWithName(1, "Heat up the crucible using a burning box and wait until it all turns into sponge iron")
                , ST.make(scrapGt.mat(MTx.SpongeIron, 1), "Get the bloom out with a shovel")
                , ST.make(MTEx.gt6Registry.getItem(32028), "Get rid of slag and excess carbon by hammering the sponge iron scrap on any anvil to make wrought iron")
        ), ST.array(nugget.mat(MT.WroughtIron, 1), ingot.mat(MT.WroughtIron, 1), plate.mat(MT.WroughtIron, 1), scrapGt.mat(MTx.Slag, 1), stick.mat(MT.WroughtIron, 1), gearGtSmall.mat(MT.WroughtIron, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(false, ST.array(
                IL.Ceramic_Mold.getWithName(1, "Don't forget to shape the Mold to pour it")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it all turns into Steel")
                , ST.make(MTEx.gt6Registry.getItem(1302), "Point a running Engine into the Crucible to blow Air")
                , ST.make(ingot.mat(MT.PigIron, 1), "Throw some Pig Iron into Crucible. Do not forget to leave space for Air!")
                , ST.make(MTEx.gt6Registry.getItem(1199), "Heat up the Crucible using a Burning Box")
        ), ST.array(dust.mat(MT.Steel, 1), ingot.mat(MT.Steel, 1), plate.mat(MT.Steel, 1), scrapGt.mat(MT.Steel, 1), stick.mat(MT.Steel, 1), gearGt.mat(MT.Steel, 1)), null, ZL_LONG, ZL_FS, ZL_FS, 0, 0, 0);

        RM.DidYouKnow.addFakeRecipe(F, ST.array(
                ST.make(dust.mat(MT.OREMATS.Cinnabar, 3), "Throw three Units of Cinnabar into Crucible")
                , ST.make(dust.mat(MTx.HgO, 2), "Or two Units of Mercuric Oxide produced by roasting the cinnabar first!")
                , IL.Ceramic_Crucible.getWithName(1, "Wait until it melts into Mercury")
                , IL.Bottle_Empty.getWithName(1, "Rightclick the Crucible with an Empty Bottle")
                , ST.make(MTEx.gt6Registry.getItem(1199), "Heat up the Crucible using a Burning Box")
                , ST.make(Blocks.redstone_ore, 1, 0, "Using a Club to mine Vanilla Redstone Ore gives Cinnabar")
        ), ST.array(IL.Bottle_Mercury.get(1), ST.make(ingot.mat(MT.Hg, 1), "Pouring this into Molds only works with additional Cooling!"), ST.make(nugget.mat(MT.Hg, 1), "Pouring this into Molds only works with additional Cooling!")), null, ZL_LONG, FL.array(MT.Hg.liquid(1, T)), FL.array(MT.Hg.liquid(1, T)), 0, 0, 0);

        Recipe r = RM.Centrifuge.findRecipe(null, null, true, Long.MAX_VALUE, null, ZL_FS, OM.dust(MT.OREMATS.Cinnabar)); if (r != null) r.mEnabled = false;
    }
}
