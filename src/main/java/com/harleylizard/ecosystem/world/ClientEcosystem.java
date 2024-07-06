package com.harleylizard.ecosystem.world;

import com.harleylizard.ecosystem.DynamicEcosystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thaumcraft.common.config.Config;

import java.util.Map;
import java.util.WeakHashMap;

public final class ClientEcosystem implements Ecosystem {
    private static final Map<Chunk, ClientEcosystem>  MAP = new WeakHashMap<>();

    private final int[][] palettes;

    public ClientEcosystem(int[][] palettes) {
        this.palettes = palettes;
    }

    @Override
    public int getNourishment(int x, int y, int z) {
        return palettes[y >> 4][MutableEcosystem.Palette.indexOf(x, y, z)];
    }

    public static void remap(Chunk chunk, ClientEcosystem ecosystem) {
        MAP.put(chunk, ecosystem);
    }

    public interface ColorModifier {

        default int modifyColor(int vanilla, int x, int y, int z) {
            World world = Minecraft.getMinecraft().theWorld;
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            ClientEcosystem ecosystem = MAP.get(chunk);
            if (ecosystem == null) {
                return vanilla;
            }

            float nourishment = Math.min(ecosystem.getNourishment(x, y, z), 16.0F) / 16.0F;
            return mix(DynamicEcosystem.THAUMCRAFT && world.getBiomeGenForCoords(x, z).biomeID == Config.biomeTaintID ? 0x6B5689 : 0xEA9F69, vanilla, nourishment);
        }

        static int mix(int x, int  y, float t) {
            int xR = (x >> 16) & 0xFF;
            int xG = (x >> 8) & 0xFF;
            int xB = (x >> 0) & 0xFF;
            int yR = (y >> 16) & 0xFF;
            int yG = (y >> 8) & 0xFF;
            int yB = (y >> 0) & 0xFF;

            int zR = mixInt(xR, yR, t);
            int zG = mixInt(xG, yG, t);
            int zB = mixInt(xB, yB, t);
            return zR << 16 | zG << 8 | zB;
        }

        static int mixInt(float x, float y, float t) {
            return (int) Math.floor(x + t * (y - x)) & 0xFF;
        }
    }
}
