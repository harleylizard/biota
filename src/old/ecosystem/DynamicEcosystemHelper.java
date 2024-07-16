package com.harleylizard.ecosystem;

import com.harleylizard.ecosystem.config.Influence;
import com.harleylizard.ecosystem.world.MutableEcosystem;
import com.harleylizard.ecosystem.world.message.EcosystemMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class DynamicEcosystemHelper {

    private DynamicEcosystemHelper() {
    }

    public static void setBiomeFromInfluence(World world, int x, int y, int z) {
        Influence influence = DynamicEcosystem.INFLUENCE_CONFIGS.get().getInfluence(world, x, y, z);
        if (influence != null) {
            BiomeGenBase biome = influence.getBiome();
            if (world.getBiomeGenForCoords(x, z) != biome) {
                setBiome(world, biome, x, y, z);
            }
        }
    }

    public static void setBiome(World world, BiomeGenBase biome, int x, int y, int z) {
        DynamicEcosystem.PROXY.setBiome(world, biome, x, y, z);
    }

    public static void sendEcosystem(World world, int x, int y, int z) {
        if (world.getChunkProvider().chunkExists(x >> 4, z >> 4)) {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);

            MutableEcosystem ecosystem = MutableEcosystem.maybeGet(chunk);
            if (ecosystem != null) {
                int dim = world.getWorldInfo().getDimension();
                NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(dim, x, y, z, 24.0F);
                DynamicEcosystem.NETWORK_WRAPPER.sendToAllAround(new EcosystemMessage(ecosystem,
                        chunk.xPosition,
                        chunk.zPosition,
                        x, y, z
                ), targetPoint);
            }
        }
    }
}
