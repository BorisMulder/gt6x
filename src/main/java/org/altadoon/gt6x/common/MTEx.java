package org.altadoon.gt6x.common;

import gregapi.block.MaterialMachines;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.MD;
import gregapi.util.CR;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.*;

public class MTEx {
    public static void touch() {}
    public static MultiTileEntityRegistry gt6xMTEReg = new MultiTileEntityRegistry("gt6x.multitileentity");
    public static MultiTileEntityRegistry gt6Registry = MultiTileEntityRegistry.getRegistry("gt.multitileentity");

    public static final int gt6xMTERegId = Block.getIdFromBlock(MTEx.gt6xMTEReg.mBlock);

    public static MultiTileEntityBlock PlasticBlock = MultiTileEntityBlock.getOrCreate(Gt6xMod.MOD_ID, "redstoneLight", Material.redstoneLight, Block.soundTypeWood, TOOL_saw, 0, 0, 15, false, false);
    public static MultiTileEntityBlock MachineBlock = MultiTileEntityBlock.getOrCreate(Gt6xMod.MOD_ID, "machine", MaterialMachines.instance, Block.soundTypeMetal, TOOL_wrench, 0, 0, 15, false, false);
    public static MultiTileEntityBlock StoneBlock = MultiTileEntityBlock.getOrCreate(MD.GT.mID, "rock", Material.rock, Block.soundTypeStone, TOOL_pickaxe, 0, 0, 15, false, false);

    public enum IDs {
        /// Available: -32768 to 32767 (shorts)
        // Oil industry: 0-99
        PVCTubes(0), // 0-19
        PTFETubes(20), // 20-39
        PVCCan(40),
        PTFECan(41),
        Hydrocracker1(50),
        Hydrocracker2(51),
        Hydrocracker3(52),
        Hydrocracker4(53),

        // Ceramics: 100-199
        AluminaBricks(100),
        AluminaCheckerBricks(101),
        SiCBricks(110),
        SiCWall(111),
        SiCCrucible(112),
        SiCCrucibleLarge(113),
        SicFaucet(114),
        SiCMold(115),
        SiCBasin(116),
        SiCCrossing(117),
        MgOCBricks(120),
        MgOCWall(121),
        MgOCCrucible(122),
        MgOCCrucibleLarge(123),
        MgOCFaucet(124),
        MgOCMold(125),
        MgOCBasin(126),
        MgOCCrossing(127),

        // Metallurgy: 200-299
        BFPartIron(200),
        BFIron(201),
        BFPartSteel(202),
        BFSteel(203),
        Sintering1(220),
        Sintering2(221),
        Sintering3(222),
        Sintering4(223),
        CowperStove(230),
        ShaftFurnace(235),
        EAF(240),
        EAFElectrodes(241),
        BOF(250),
        BOFWall(251),
        BOFLance(252),

        // Vertical Mixers: 300-319
        VertMixer1(300),
        VertMixer2(301),
        VertMixer3(302),
        VertMixer4(303),
        VertMixerElectric1(310),
        VertMixerElectric2(311),
        VertMixerElectric3(312),
        VertMixerElectric4(313),
        VertMixerElectric5(314),

        // Thermolysis Ovens: 320-329
        ThermolysisOven1(320),
        ThermolysisOven2(321),
        ThermolysisOven3(322),
        ThermolysisOven4(323),

        // Electronics: 400-420
        Photolithography1(400),
        Photolithography2(401),
        Photolithography3(402),
        Photolithography4(403),
        Photolithography5(404),
        ;

        private final int id;

        IDs(int num) {
            id = num;
        }

        public int get() {
            return id;
        }
    }

    public void disableGT6MTE(short id) {
        if (gt6Registry.mRegistry.containsKey(id)) {
            ItemStack it = gt6Registry.getItem(id);
            ST.hide(it);
            CR.BUFFER.removeIf(r -> ST.equal(r.getRecipeOutput(), it));
        }
    }
}
