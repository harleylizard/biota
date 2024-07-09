package com.harleylizard.ecosystem.world;

import net.minecraft.world.biome.BiomeGenBase;

public interface DynamicEcosystemWorld {

    void setBiome(BiomeGenBase biomeGenBase, BlockPos blockPos);
}
