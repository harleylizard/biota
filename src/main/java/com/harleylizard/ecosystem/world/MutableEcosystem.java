package com.harleylizard.ecosystem.world;

import com.harleylizard.ecosystem.DynamicEcosystem;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;

import java.util.Map;
import java.util.WeakHashMap;

public final class MutableEcosystem implements Ecosystem {
    private static final Map<Chunk, MutableEcosystem> MAP = new WeakHashMap<>();

    private final Palette[] palettes = new Palette[16];

    private MutableEcosystem(Chunk chunk) {
    }

    private MutableEcosystem(NBTTagCompound compound) {
        for (int i = 0; i < 16; i++) {
            String key = "" + i;
            if (compound.hasKey(key, Constants.NBT.TAG_INT_ARRAY)) {
                Palette palette = new Palette();

                int[] intArray = compound.getIntArray(key);
                System.arraycopy(intArray, 0, palette.nourishment, 0, 16 * 16 * 16);
                palettes[i] = palette;
            }
        }
    }

    @Override
    public int getNourishment(int x, int y, int z) {
        Palette palette = palettes[y >> 4];
        return palette == null ? 0 : palette.nourishment[Palette.indexOf(x, y, z)];
    }

    public boolean removeNourishment(int x, int y, int z, int amount) {
        Palette palette = palettes[y >> 4];
        if (palette != null) {
            int[] nourishment = palette.nourishment;
            int i = Palette.indexOf(x, y, z);
            int j = nourishment[i];

            int k = Math.max(j - amount, 0);
            nourishment[i] = k;
            return k > 0;
        }
        return false;
    }

    public void addNourishment(int x, int y, int z, int amount) {
        Palette palette = palettes[y >> 4];
        if (palette != null) {
            int[] nourishment = palette.nourishment;
            int i = Palette.indexOf(x, y, z);
            int j = nourishment[i];

            nourishment[i] = Math.min(j + amount, 24);
        }
    }

    public void toClient(ByteBuf buf) {
        for (int i = 0; i < 16; i++) {
            Palette palette = palettes[i];
            if (palette == null) {
                buf.writeByte(0);
                continue;
            }
            buf.writeByte(1);

            for (int j = 0; j < 16 * 16 * 16; j++) {
                buf.writeByte(palette.nourishment[j]);
            }
        }
    }

    public NBTTagCompound getCompoundTag() {
        NBTTagCompound compound = new NBTTagCompound();
        for (int i = 0; i < 16; i++) {
            Palette palette = palettes[i];
            if (palette != null) {
                compound.setTag("" + i, new NBTTagIntArray(palette.nourishment));
            }
        }
        return compound;
    }

    private MutableEcosystem spawnPalette(Chunk chunk, int y) {
        int i = y >> 4;
        Palette palette = palettes[i];
        if (palette == null) {
            palette = new Palette();
            ExtendedBlockStorage storage = chunk.getBlockStorageArray()[i];
            for (int j = 0; j < 16; j++) for (int k = 0; k < 16; k++) for (int l = 0; l < 16; l++) {
                Block block = storage.getBlockByExtId(j, k, l);

                if (DynamicEcosystem.isPlant(block)) {
                    palette.nourishment[Palette.indexOf(j, k, l)] = 16;
                }
            }
            palettes[i] = palette;
        }
        return this;
    }

    public static MutableEcosystem get(Chunk chunk, int y) {
        return MAP.computeIfAbsent(chunk, MutableEcosystem::new).spawnPalette(chunk, y);
    }

    public static MutableEcosystem maybeGet(Chunk chunk) {
        return MAP.get(chunk);
    }

    public static MutableEcosystem load(Chunk chunk, NBTTagCompound compound) {
        MutableEcosystem ecosystem = new MutableEcosystem(compound);
        MAP.put(chunk, ecosystem);
        return ecosystem;
    }

    public static final class Palette {
        private final int[] nourishment = new int[16 * 16 * 16];

        public static int indexOf(int x, int y, int z) {
            x = Math.floorMod(x, 16);
            y = Math.floorMod(y, 16);
            z = Math.floorMod(z, 16);
            return (z * 16 * 16) + (y * 16) + x;
        }
    }
}
