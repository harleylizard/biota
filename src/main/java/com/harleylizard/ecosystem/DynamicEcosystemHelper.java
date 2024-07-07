package com.harleylizard.ecosystem;

import com.harleylizard.ecosystem.world.MutableEcosystem;
import com.harleylizard.ecosystem.world.message.EcosystemMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class DynamicEcosystemHelper {

    private DynamicEcosystemHelper() {
    }

    public static void setBiome(World world, int x, int z, BiomeGenBase base) {
        //Utils.setBiomeAt(world, x, z, base);
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
                        chunk.zPosition
                        ), targetPoint);
            }
        }
    }
}
