package com.harleylizard.ecosystem.proxy;

import com.harleylizard.ecosystem.world.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public interface Proxy {

    default void setBiome(World world, BiomeGenBase biomeGenBase, BlockPos blockPos) {
        int x = blockPos.getX();
        int z = blockPos.getZ();
        world.getChunkFromBlockCoords(x, z).getBiomeArray()[(x & 0x0F) << 4 | (z & 0x0F)] = (byte) biomeGenBase.biomeID;
    }
}
