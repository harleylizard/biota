package com.harleylizard.ecosystem.world;

import com.harleylizard.ecosystem.DynamicEcosystem;
import com.harleylizard.ecosystem.DynamicEcosystemHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BiomeInfluence {
    private static final List<Influence> PLAINS = listOf(
            new Influence(Blocks.grass, 0, 1),
            new Influence(Blocks.tallgrass, 1, 1),
            new Influence(Blocks.deadbush, 0, -2)
    );

    private static final List<Influence> FOREST = listOf(
            new Influence(Blocks.log, 0, 2),
            new Influence(Blocks.leaves, 0, 2),
            new Influence(Blocks.log, 1, 1),
            new Influence(Blocks.leaves, 1, 1)
    );

    private static final List<Influence> BIRCH_FOREST = listOf(
            new Influence(Blocks.log, 2, 3),
            new Influence(Blocks.leaves, 2, 3)
    );

    private static final List<Influence> TAIGA = listOf(
            new Influence(Blocks.log, 1, 3),
            new Influence(Blocks.leaves, 1, 3),
            new Influence(Blocks.tallgrass, 2, 1)
    );

    private static final List<Influence> JUNGLE = listOf(
            new Influence(Blocks.log, 0, 1),
            new Influence(Blocks.leaves, 0, 1),
            new Influence(Blocks.log, 3, 3),
            new Influence(Blocks.leaves, 3, 3),
            new Influence(Blocks.tallgrass, 2, 1),
            new Influence(Blocks.vine, 0, 3)
    );

    private static final List<Influence> SWAMP = listOf(
            new Influence(Blocks.vine, 0, 1),
            new Influence(Blocks.waterlily, 0, 3),
            new Influence(Blocks.red_flower, 1, 4)
    );

    private static List<Influence> TAINT = null;
    private static List<Influence> MAGICAL_FOREST = null;

    static {
        if (DynamicEcosystem.THAUMCRAFT) {
            TAINT = listOf(
                    new Influence(ConfigBlocks.blockTaint, 0, 2),
                    new Influence(ConfigBlocks.blockTaint, 1, 2),
                    new Influence(ConfigBlocks.blockTaintFibres, 0, 6),
                    new Influence(ConfigBlocks.blockTaintFibres, 1, 1),
                    new Influence(ConfigBlocks.blockTaintFibres, 3, 1)
            );
            MAGICAL_FOREST = listOf(
                    new Influence(ConfigBlocks.blockMagicalLog, 1, 3),
                    new Influence(ConfigBlocks.blockCustomPlant, 2, 3)
            );
        }
    }

    private BiomeInfluence() {
    }

    public static void setBiomeFromInfluence(World world, int x, int y, int z) {
        DynamicEcosystemHelper.setBiome(world, x, z, getBiomeFromInfluence(world, x, y, z));
    }

    private static BiomeGenBase getBiomeFromInfluence(World world, int x, int y, int z) {
        if (getInfluenceFor(world, PLAINS, x, y, z) >= 25) {
            if (getInfluenceFor(world, FOREST, x, y, z) >= 18) {
                return BiomeGenBase.forest;
            }
            if (getInfluenceFor(world, BIRCH_FOREST, x, y, z) >= 18) {
                return BiomeGenBase.birchForest;
            }
            if (getInfluenceFor(world, TAIGA, x, y, z) >= 18) {
                return BiomeGenBase.taiga;
            }
            if (getInfluenceFor(world, JUNGLE, x, y, z) >= 20) {
                return BiomeGenBase.jungle;
            }
            if (getInfluenceFor(world, SWAMP, x, y, z) >= 20) {
                return BiomeGenBase.swampland;
            }
            if (DynamicEcosystem.THAUMCRAFT) {
                if (getInfluenceFor(world, TAINT, x, y, z) >= 18) {
                    return ThaumcraftWorldGenerator.biomeTaint;
                }
                if (getInfluenceFor(world, MAGICAL_FOREST, x, y, z) >= 20) {
                    return ThaumcraftWorldGenerator.biomeMagicalForest;
                }
            }

            return BiomeGenBase.plains;
        }
        return BiomeGenBase.desert;
    }

    private static int getInfluenceFor(World world, List<Influence> list, int x, int y, int z) {
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

    private static List<Influence> listOf(Influence... influences) {
        return Collections.unmodifiableList(Arrays.asList(influences));
    }

    private static final class Influence {
        private final Block block;
        private final int meta;
        private final int weight;

        private Influence(Block block, int meta, int weight) {
            this.block = block;
            this.meta = meta;
            this.weight = weight;
        }

        public boolean matches(Block block, int meta) {
            return this.block == block && this.meta == meta;
        }
    }
}
