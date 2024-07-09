package com.harleylizard.ecosystem.proxy;

import com.harleylizard.ecosystem.DynamicEcosystem;
import com.harleylizard.ecosystem.message.SetBiomeMessage;
import com.harleylizard.ecosystem.world.BlockPos;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class CommonProxy implements Proxy {
    @Override
    public void setBiome(World world, BiomeGenBase biomeGenBase, BlockPos blockPos) {
        Proxy.super.setBiome(world, biomeGenBase, blockPos);
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(
                world.getWorldInfo().getDimension(),
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                24.0F
        );
        DynamicEcosystem.INSTANCE.getNetworkWrapper().sendToAllAround(new SetBiomeMessage(biomeGenBase, blockPos), targetPoint);
    }
}
