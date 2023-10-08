package org.altadoon.gt6x;

import cpw.mods.fml.common.IWorldGenerator;
import gregapi.api.Abstract_Proxy;
import gregapi.data.CS;
import gregapi.worldgen.WorldgenObject;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;


public class ProxyServer extends Abstract_Proxy implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
//		if(world.provider.dimensionId == CS.DIM_OVERWORLD){
//			new WorldgenObject().generate(world,world.getChunkFromChunkCoords(chunkX,chunkZ),)
//		}
	}
	// Insert your Serverside-only implementation of Stuff here
}