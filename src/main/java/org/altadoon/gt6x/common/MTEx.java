package org.altadoon.gt6x.common;

import gregapi.block.MaterialMachines;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.MD;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import org.altadoon.gt6x.Gt6xMod;

import static gregapi.data.CS.*;

public class MTEx {
    public static void touch() {}
    public static MultiTileEntityRegistry gt6xMTEReg = new MultiTileEntityRegistry("gt6x.multitileentity");
    public static MultiTileEntityRegistry gt6MTEReg = MultiTileEntityRegistry.getRegistry("gt.multitileentity");

    public static final String NBT_MTE_MULTIBLOCK_PART_REG = "gt6x.mte.part.reg";
    public static final int gt6xMTERegId = Block.getIdFromBlock(gt6xMTEReg.mBlock);
    public static final int gt6MTERegId = Block.getIdFromBlock(gt6MTEReg.mBlock);

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
        DistTower(60),
        CryoDistTower(61),

        // Ceramics: 100-199
        AluminaBricks(100),
        AluminaCheckerBricks(101),
        SiCBricks(110),
        SiCWall(111),
        //SiCCrucible(112),
        //SiCCrucibleLarge(113),
        SiCCrucibleParts(114), // 114-117
        MgOCBricks(120),
        MgOCWall(121),
        //MgOCCrucible(122),
        //MgOCCrucibleLarge(123),
        MgOCCrucibleParts(124), // 124-127
        FireclayCrucibleParts(130), // 130-135
        Mortars(140), // 140-144

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
        SolarPanelPolySi(470),
        SolarPanelMonoSi(471),
        SolarPanelMJ(472),
        // Engines: 500-799
        PetrolEngine1a(500),
        PetrolEngine1b(501),
        PetrolEngine2(502),
        PetrolEngine3(503),
        PetrolEngine4(504),
        PetrolEngine5(505),
        PetrolEngine6(506),
        PetrolEngine7(507),

        DieselEngine1a(510),
        DieselEngine1b(511),
        DieselEngine2(512),
        DieselEngine3(513),
        DieselEngine4(514),
        DieselEngine5(515),
        DieselEngine6(516),
        DieselEngine7(517),
        EngineBlock1a(520),
        EngineBlock1b(521),
        EngineBlock2(522),
        EngineBlock3(523),
        EngineBlock4(524),
        EngineBlock5(525),
        EngineBlock6(526),
        EngineBlock7(527),
        EngineBlockMolds(530), // 530-579
        EngineBlockA6061(580),
        NitroEngine(581),

        // Engine Pipes and drums
        DrumCastIron(590),
        DrumHastelloy(592),
        DrumTi6Al4V(593),
        DrumTMS196(594),
        PipesCastIron(600), // 600-619
        PipesAlusil(620), // 620-639
        PipesHastelloy(640), // 640-659
        PipesTi6Al4V(660), // 660-679
        PipesTMS196(680), // 680-699
        PipesA6061(700), // 700-719

        // Gas Turbines: 800-810
        GasTurbine1(801),
        GasTurbine2(802),
        GasTurbine3(803),
        GasTurbine4(804),
        GasTurbine5(805),
        // Dense Walls: 811-820
        DenseWallHastelloy(811),
        DenseWallTi6Al4V(812),
        DenseWallTMS196(813),

        // Crucibles: 900-1199
        Crucibles(1000),
        Molds(1050),
        CrucibleFaucets(1700),
        Basins(1750),
        Crossings(1850),
        LargeCrucibles(17300),
        Taps(32700),
        Funnels(32750),
        ;

        public static final IDs[] Hydrocracker = { null, Hydrocracker1, Hydrocracker2, Hydrocracker3, Hydrocracker4 };
        public static final IDs[] VertMixer = { null, VertMixer1, VertMixer2, VertMixer3, VertMixer4 };
        public static final IDs[] VertMixerElectric = { null, VertMixerElectric1, VertMixerElectric2, VertMixerElectric3, VertMixerElectric4, VertMixerElectric5 };
        public static final IDs[] Photolithography = { null, Photolithography1, Photolithography2, Photolithography3, Photolithography4, Photolithography5 };
        public static final IDs[] IonBombardment = { null, IonBombardment1, IonBombardment2, IonBombardment3, IonBombardment4, IonBombardment5 };
        public static final IDs[] Soldering = { null, Soldering1, Soldering2, Soldering3, Soldering4, Soldering5 };
        public static final IDs[] Ionizer = { null, Ionizer1, Ionizer2, Ionizer3, Ionizer4, Ionizer5 };
        public static final IDs[] VacuumChamber = { null, VacuumChamber1, VacuumChamber2, VacuumChamber3, VacuumChamber4, VacuumChamber5 };
        public static final IDs[] EngineBlock = { EngineBlock1a, EngineBlock1b, EngineBlock2, EngineBlock3, EngineBlock4, EngineBlock5, EngineBlock6, EngineBlock7 };
        public static final IDs[] PetrolEngine = { PetrolEngine1a, PetrolEngine1b, PetrolEngine2, PetrolEngine3, PetrolEngine4, PetrolEngine5, PetrolEngine6, PetrolEngine7 };
        public static final IDs[] DieselEngine = { DieselEngine1a, DieselEngine1b, DieselEngine2, DieselEngine3, DieselEngine4, DieselEngine5, DieselEngine6, DieselEngine7 };
        public static final IDs[] GasTurbine = { GasTurbine1, GasTurbine2, GasTurbine3, GasTurbine4 };

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

    public static void disableGT6MTE(short id) {
        if (gt6MTEReg.mRegistry.containsKey(id)) {
            ItemStack it = gt6MTEReg.getItem(id);
            ST.hide(it);
            CRx.disableGt6(it);
        }
    }
}
