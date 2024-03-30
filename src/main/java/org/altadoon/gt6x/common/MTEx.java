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

        // Electronics: 400-499
        Photolithography1(400),
        Photolithography2(401),
        Photolithography3(402),
        Photolithography4(403),
        Photolithography5(404),
        IonBombardment1(410),
        IonBombardment2(411),
        IonBombardment3(412),
        IonBombardment4(413),
        IonBombardment5(414),
        YAlOTubes(420), // 420-439
        Soldering1(440),
        Soldering2(441),
        Soldering3(442),
        Soldering4(443),
        Soldering5(444),
        Ionizer1(450),
        Ionizer2(451),
        Ionizer3(452),
        Ionizer4(453),
        Ionizer5(454),
        VacuumChamber1(460),
        VacuumChamber2(461),
        VacuumChamber3(462),
        VacuumChamber4(463),
        VacuumChamber5(464),
        ;

        public static final IDs[] Hydrocracker = { null, Hydrocracker1, Hydrocracker2, Hydrocracker3, Hydrocracker4 };
        public static final IDs[] VertMixer = { null, VertMixer1, VertMixer2, VertMixer3, VertMixer4 };
        public static final IDs[] VertMixerElectric = { null, VertMixerElectric1, VertMixerElectric2, VertMixerElectric3, VertMixerElectric4, VertMixerElectric5 };
        public static final IDs[] Photolithography = { null, Photolithography1, Photolithography2, Photolithography3, Photolithography4, Photolithography5 };
        public static final IDs[] IonBombardment = { null, IonBombardment1, IonBombardment2, IonBombardment3, IonBombardment4, IonBombardment5 };
        public static final IDs[] Soldering = { null, Soldering1, Soldering2, Soldering3, Soldering4, Soldering5 };
        public static final IDs[] Ionizer = { null, Ionizer1, Ionizer2, Ionizer3, Ionizer4, Ionizer5 };
        public static final IDs[] VacuumChamber = { null, VacuumChamber1, VacuumChamber2, VacuumChamber3, VacuumChamber4, VacuumChamber5 };

        private final int id;

        IDs(int num) {
            id = num;
        }

        public int get() {
            return id;
        }
    }

    public static final float[] HARDNESS_KINETIC = { 6.0F, 7.0F, 6.0F, 9.0F, 12.5F };
    public static final float[] HARDNESS_HEAT = { 4.0F, 6.0F, 4.0F, 9.0F, 12.5F };
    public static final float[] HARDNESS_ELECTRIC = { 4.0F, 4.0F, 4.0F, 4.0F, 4.0F, 4.0F };

    public void disableGT6MTE(short id) {
        if (gt6Registry.mRegistry.containsKey(id)) {
            ItemStack it = gt6Registry.getItem(id);
            ST.hide(it);
            CRx.disableGt6(it);
        }
    }
}
