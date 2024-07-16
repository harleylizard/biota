package com.harleylizard.dynamicecosystem.chunk;

import com.harleylizard.dynamicecosystem.DynamicEcosystem;
import com.harleylizard.dynamicecosystem.message.UpdateNutrientsMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Map;
import java.util.WeakHashMap;

public final class MutableNutrients implements Nutrients, NutrientsAccessor {
    public static final Map<Chunk, MutableNutrients> MAP = new WeakHashMap<>();

    private final Palette[] palettes = new Palette[16];

    public void writeTo(ByteBuf buf) {
        for (int i = 0; i < 16; i++) {
            Palette palette = palettes[i];
            buf.writeBoolean(palette == null);

            if (palette != null) {
                for (int j = 0; j < SIZE; j++) {
                    buf.writeByte(palette.ints[j]);
                }
            }
        }
    }

    @Override
    public int get(int x, int y, int z) {
        Palette palette = getPalette(y);
        return palette == null ? 0 : palette.ints[Palette.indexOf(x, y, z)];
    }

    @Override
    public void set(int x, int y, int z, int nutrients) {
        Palette palette = getPalette(y);
        if (palette != null) {
            palette.ints[Palette.indexOf(x, y, z)] = nutrients;
        }
    }

    @Override
    public void take(int x, int y, int z, int amount) {
        Palette palette = getPalette(y);
        if (palette != null) {
            int i = Palette.indexOf(x, y, z);
            palette.ints[i] -= amount;
            palette.ints[i] = Math.max(palette.ints[i], 0);
        }
    }

    @Override
    public void add(int x, int y, int z, int amount) {
        Palette palette = getPalette(y);
        if (palette != null) {
            int i = Palette.indexOf(x, y, z);
            palette.ints[i] += amount;
            palette.ints[i] = Math.min(palette.ints[i], 16);
        }
    }

    private Palette getPalette(int y) {
        return palettes[y >> 4];
    }

    public void push(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return;
        }
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int i = y >> 4;
        Palette palette = palettes[i];
        if (palette == null) {
            palette = new Palette();

            ExtendedBlockStorage blockStorage = chunk.getBlockStorageArray()[i];
            if (blockStorage != null) {
                for (x = 0; x < 16; x++) {
                    for (y = 0; y < 16; y++) {
                        for (z = 0; z < 16; z++) {
                            Block block = blockStorage.getBlockByExtId(x, y, z);

                            if (block instanceof BlockGrass || block instanceof BlockLeavesBase || block instanceof BlockBush) {
                                palette.ints[Palette.indexOf(x, y, z)] = 16;
                            }
                        }
                    }
                }
            }
            palettes[i] = palette;
        }
    }

    public void toClient(Chunk chunk) {
        MAP.put(chunk, this);
        DynamicEcosystem.INSTANCE.getWrapper().sendToAll(new UpdateNutrientsMessage(this, chunk));
    }

    public static MutableNutrients getOrCreate(Chunk chunk) {
        return MAP.computeIfAbsent(chunk, ignored -> new MutableNutrients());
    }

    public static final class Palette {
        private final int[] ints = new int[SIZE];

        public static int indexOf(int x, int y, int z) {
            return x + y * 16 + z * 16 * 16;
        }
    }
}
