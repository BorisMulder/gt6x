package org.altadoon.gt6x.features.worldgen;

import appeng.api.AEApi;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.util.WD;
import gregapi.worldgen.WorldgenObject;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static gregapi.data.CS.*;

public class WorldgenMeteor extends WorldgenObject {
	Block baseBlock = BlocksGT.GraniteBlack;
	Block crustBlock = BlocksGT.GraniteBlack;

	@SafeVarargs
	public WorldgenMeteor(String aName, boolean aDefault, List<WorldgenObject>... aLists) {
		super(aName, aDefault, aLists);
		if (MD.AE.mLoaded) {
			crustBlock = AEApi.instance().definitions().blocks().skyStone().maybeBlock().or(BlocksGT.GraniteBlack);
			if (crustBlock == BlocksGT.GraniteBlack)
				System.out.println("> ERROR, AE is present but SkyStone is not accessible.");
		}
	}

	@SafeVarargs
	public WorldgenMeteor(String aName, Block aMainBlock, boolean aDefault, List<WorldgenObject>... aLists) {
		this(aName, aDefault, aLists);
		baseBlock = aMainBlock;
//		crustBlock = baseBlock;
	}

	@SafeVarargs
	public WorldgenMeteor(String aName, Block aMainBlock, Block aCrust, boolean aDefault, List<WorldgenObject>... aLists) {
		this(aName,aMainBlock, aDefault, aLists);
		crustBlock = aCrust;
	}

	@Override
	public boolean generate(World aWorld, Chunk aChunk, int aDimType, int aMinX, int aMinZ, int aMaxX, int aMaxZ, Random aRandom, BiomeGenBase[][] aBiomes, Set<String> aBiomeNames) {
		if (aRandom.nextInt(50) == 0 || ((aDimType==DIM_MOON||aDimType==DIM_MARS||aDimType==DIM_ASTEROIDS||aDimType==DIM_PLANETS)&&aRandom.nextInt(10)==0)) {
			int radius = 3 + aRandom.nextInt(7);
			int centerX = aMinX + aRandom.nextInt(15);
			int centerZ = aMinZ + aRandom.nextInt(15);
			int centerY = aWorld.getChunkFromBlockCoords(centerX, centerZ).getHeightValue(centerX & 15, centerZ & 15);
			// prevent floating in water
			while (WD.anywater(aWorld, centerX, centerY - 1, centerZ)) {
				centerY--;
				if (centerY == 0) break;
			}

			centerY = centerY - radius - aRandom.nextInt(50); // let's burry the metorite under the ground slightly
			if (centerY < 5) centerY = radius; // prevent from spawing under the bedrock
			// make an Impact creator if the asteroid is above ground
			int radius2 = radius + 5 + aRandom.nextInt(5);
			int creatorY = centerY + radius2 - radius + aRandom.nextInt(3);
			if (centerY > WD.waterLevel(aWorld) && aWorld.canBlockSeeTheSky(centerX, centerY + radius2, centerZ))
				for (int i = -radius2; i <= radius2; i++) {
					for (int j = -radius2; j <= radius2; j++) {
						for (int k = -radius2; k < radius2; k++) {
							var x = centerX + i;
							var y = creatorY + j;
							var z = centerZ + k;
							var dis = ((x - centerX) * (x - centerX) + (y - creatorY) * (y - creatorY) + (z - centerZ) * (z - centerZ));
							if (dis <= radius2 * radius2) {
								if (!WD.anywater(aWorld, x, y, z)) {
									WD.set(aWorld, x, y, z, Blocks.air, 0, 0);
								} else if (aBiomeNames.contains(BiomeGenBase.ocean.biomeName) || WD.anywater(aWorld, centerX, centerY, centerZ)) {
									WD.set(aWorld, x, y, z, BlocksGT.Ocean, 0, 0);
								}
							}
						}
					}
				}

			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					for (int k = -radius; k < radius; k++) {
						var x = centerX + i;
						var y = centerY + j;
						var z = centerZ + k;
						var dis = ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ));
						if (dis <= radius * radius) {
							// it's in sphere
							if (WD.bedrock(aWorld, x, y, z)) continue; // dont replace bedrock duh
							boolean edge = false;
							for (byte l = 0; l < 6; l++) if (!WD.obstructed(aWorld, x, y, z, l)) edge = true;
							if(edge) WD.set(aWorld,x,y,z,crustBlock,0,0);
							else WD.set(aWorld, x, y, z, baseBlock, 0, 0, T);
							if (aRandom.nextInt(4) == 0) WD.setSmallOre(aWorld, x, y, z, MT.MeteoricIron);
							else if (aRandom.nextInt(5) == 0) WD.setSmallOre(aWorld, x, y, z, MT.Ni);
							else if (aRandom.nextInt(8) == 0) WD.setOre(aWorld, x, y, z, MT.MeteoricIron);
							else if (aRandom.nextInt(20) == 0) WD.setOre(aWorld, x, y, z, MT.Ir);
						}
					}
				}
			}
			return T;
		}
		return F;
	}
}
