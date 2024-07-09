package com.harleylizard.ecosystem.proxy;

import com.harleylizard.ecosystem.world.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class ClientProxy implements Proxy {
    @Override
    public void setBiome(World world, BiomeGenBase biomeGenBase, BlockPos blockPos) {
        Proxy.super.setBiome(world, biomeGenBase, blockPos);
        world.markBlockForRenderUpdate(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        );
    }
}
