package com.harleylizard.ecosystem.config;

import com.harleylizard.ecosystem.config.blocklist.BlockList;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.List;

public final class SaplingInfo {
    private final BlockWithMeta sapling;
    private final BlockList leaves;
    private final List<BiomeGenBase> nativeBiomes;
    private final Blacklist blacklist;

    private SaplingInfo(BlockWithMeta sapling, BlockList leaves, List<BiomeGenBase> nativeBiomes, Blacklist blacklist) {
        this.sapling = sapling;
        this.leaves = leaves;
        this.nativeBiomes = nativeBiomes;
        this.blacklist = blacklist;
    }
}
