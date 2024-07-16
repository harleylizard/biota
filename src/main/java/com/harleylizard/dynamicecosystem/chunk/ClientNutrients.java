package com.harleylizard.dynamicecosystem.chunk;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public final class ClientNutrients implements Nutrients {
    public static final Map<Chunk, ClientNutrients> MAP = new WeakHashMap<>();

    private final List<Palette> palettes;

    private ClientNutrients(List<Palette> palettes) {
        this.palettes = palettes;
    }

    private float get(int x, int y, int z) {
        Palette palette = palettes.get(y >> 4);
        return palette == Palette.EMPTY ? 0.0F : (float) palette.ints[MutableNutrients.Palette.indexOf(x, y, z)];
    }

    public static ClientNutrients readFrom(ByteBuf buf) {
        List<Palette> list = Palette.listOf();
        for (int i = 0; i < 16; i++) {
            boolean empty = buf.readBoolean();
            if (empty) {
                continue;
            }

            int[] ints = new int[SIZE];
            for (int j = 0; j < SIZE; j++) {
                ints[j] = buf.readByte();
            }
            list.set(i, new Palette(ints));
        }
        return new ClientNutrients(Collections.unmodifiableList(list));
    }

    private static final class Palette {
        private static final int SIZE = 16;

        private static final Palette EMPTY = new Palette(new int[0]);

        private final int[] ints;

        private Palette(int[] ints) {
            this.ints = ints;
        }

        private static List<Palette> listOf() {
            List<Palette> list = new ArrayList<>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                list.add(EMPTY);
            }
            return list;
        }
    }

    public interface AffectsColor {

        static int changeColor(int vanilla, int x, int y, int z) {
            World world = Minecraft.getMinecraft().theWorld;
            if (!world.blockExists(x, y, z)) {
                return vanilla;
            }
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            ClientNutrients nutrients = MAP.get(chunk);
            if (nutrients == null) {
                return vanilla;
            }
            float d = Math.min(nutrients.get(x & 0x0F, y & 0x0F, z & 0x0F), 16.0F) / 16.0F;
            return mix(0xEA9F69, vanilla, d);
        }

        static int mix(int x, int  y, float t) {
            int xR = (x >> 16) & 0xFF;
            int xG = (x >> 8) & 0xFF;
            int xB = (x >> 0) & 0xFF;
            int yR = (y >> 16) & 0xFF;
            int yG = (y >> 8) & 0xFF;
            int yB = (y >> 0) & 0xFF;

            int zR = imix(xR, yR, t);
            int zG = imix(xG, yG, t);
            int zB = imix(xB, yB, t);
            return zR << 16 | zG << 8 | zB;
        }

        static int imix(float x, float y, float t) {
            return (int) Math.floor(x + t * (y - x)) & 0xFF;
        }
    }
}
