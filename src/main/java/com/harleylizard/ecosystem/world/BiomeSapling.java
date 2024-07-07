package com.harleylizard.ecosystem.world;

import com.harleylizard.ecosystem.DynamicEcosystem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.*;

public final class BiomeSapling {
    public static final SaplingInfo OAK = sapling(0,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.extremeHills,
            BiomeGenBase.extremeHillsPlus,
            BiomeGenBase.extremeHillsEdge
    );
    public static final SaplingInfo BIRCH = sapling(2,
            BiomeGenBase.forest,
            BiomeGenBase.forestHills,
            BiomeGenBase.birchForest,
            BiomeGenBase.birchForestHills
    );
    public static final SaplingInfo SPRUCE = sapling(1,
            BiomeGenBase.taiga,
            BiomeGenBase.taigaHills,
            BiomeGenBase.coldTaiga,
            BiomeGenBase.coldTaigaHills
    );
    public static final SaplingInfo JUNGLE = sapling(3,
            BiomeGenBase.jungle,
            BiomeGenBase.jungleEdge,
            BiomeGenBase.jungleHills
    );

    private BiomeSapling() {
    }

    public static SaplingInfo getSaplingInfo(int meta) {
        switch (meta) {
            case 0: return OAK;
            case 2: return BIRCH;
            case 1: return SPRUCE;
            case 3: return JUNGLE;
        }
        return null;
    }

    public static boolean canSpread(World world, SaplingInfo info, int x, int y, int z, Random random) {
        if (info.block.canBlockStay(world, x, y, z) && world.canBlockSeeTheSky(x, y, z)) {
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            int biomeID = biome.biomeID;
            for (int comparing : info.blacklist) {
                if (biomeID == comparing) {
                    return false;
                }
            }
            boolean hasBiome = false;
            for (BiomeGenBase comparing : info.biomes) {
                if (biomeID == comparing.biomeID) {
                    hasBiome = true;
                    break;
                }
            }
            return random.nextInt(hasBiome ? 25 : 5) == 0;
        }
        return false;
    }

    private static SaplingInfo sapling(int meta, BiomeGenBase... biomes) {
        return new SaplingInfo(Blocks.sapling, meta, Collections.unmodifiableList(Arrays.asList(biomes)));
    }

    public static final class SaplingInfo {
        private final List<Integer> blacklist = new ArrayList<>();

        private final Block block;
        private final int meta;
        private final List<BiomeGenBase> biomes;

        private SaplingInfo(Block block, int meta, List<BiomeGenBase> biomes) {
            this.block = block;
            this.meta = meta;
            this.biomes = biomes;

            if (DynamicEcosystem.THAUMCRAFT) {
                blacklist.add(193);
                blacklist.add(194);
                blacklist.add(195);
            }
        }

        public boolean placeSapling(World world, int x, int y, int z) {
            return world.setBlock(x, y, z, block, meta, 4);
        }
    }
}
