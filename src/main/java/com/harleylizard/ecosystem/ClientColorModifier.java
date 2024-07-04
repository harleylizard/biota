package com.harleylizard.ecosystem;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public interface ClientColorModifier {

    default int modifyColor(int vanilla, int x, int y, int z) {
        Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(x, z);
        Ecosystem ecosystem = Ecosystem.get(chunk);
        if (ecosystem == null) {
            return vanilla;
        }

        float nourishment = (float) ecosystem.getNourishmentForClient(x, y, z) / 16.0F;
        return DynamicEcosystem.mix(0xEA9F69, vanilla, nourishment);
    }
}
