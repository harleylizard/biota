package com.harleylizard.ecosystem.proxy;

import com.harleylizard.ecosystem.DynamicEcosystem;
import com.harleylizard.ecosystem.world.message.SetBiomeMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public interface Proxy {

    default void setBiome(World world, BiomeGenBase biome, int x, int y, int z) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);

        byte[] biomes = chunk.getBiomeArray();
        int biomeX = x & 0x0F;
        int biomeZ = z & 0x0F;
        biomes[biomeZ << 4 | biomeX] = (byte) biome.biomeID;

        chunk.setBiomeArray(biomes);
        chunk.setChunkModified();

        if (!world.isRemote) {
            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(world.getWorldInfo().getDimension(), x, y, z, 24.0F);
            DynamicEcosystem.NETWORK_WRAPPER.sendToAllAround(new SetBiomeMessage(biome, x, y, z), targetPoint);
        }
    }
}
