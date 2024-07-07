package com.harleylizard.ecosystem.world;

import com.harleylizard.ecosystem.DynamicEcosystem;
import com.harleylizard.ecosystem.DynamicEcosystemHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.List;

public final class BiomeInfluence {

    private BiomeInfluence() {
    }

    public static void setBiomeFromInfluence(World world, int x, int y, int z) {
        BiomeGenBase biome = DynamicEcosystem.INFLUENCE_CONFIG.get().getBiomeFor(world, x, y, z);
        if (biome != null && world.getBiomeGenForCoords(x, z) != biome) {
            DynamicEcosystemHelper.setBiome(world, biome, x, y, z);
        }
    }

    public static int getInfluenceFor(World world, List<Influence> list, int x, int y, int z) {
        int range = 4;
        int i = 0;
        for (int j = -range; j <= range; j++) for (int k = -range; k <= range; k++) for (int l = -range; l <= range; l++) {
            int x1 = x + j;
            int y1 = y + k;
            int z1 = z + l;
            if (x1 == x && y1 == y && z1 == z) {
                continue;
            }
            if (world.blockExists(x1, y1, z1)) {
                Block block = world.getBlock(x1, y1, z1);
                int meta = world.getBlockMetadata(x1, y1, z1);

                for (Influence influence : list) {
                    if (influence.matches(block, meta)) {
                        i += influence.weight;
                    }
                }
            }
        }
        return i;
    }

    public static Influence influence(Block block, int meta, int weight) {
        return new Influence(block, meta, weight);
    }

    public static final class Influence {
        private final Block block;
        private final int meta;
        private final int weight;

        private Influence(Block block, int meta, int weight) {
            this.block = block;
            this.meta = meta;
            this.weight = weight;
        }

        public boolean matches(Block block, int meta) {
            return block != null && this.block == block && this.meta == meta;
        }
    }
}
