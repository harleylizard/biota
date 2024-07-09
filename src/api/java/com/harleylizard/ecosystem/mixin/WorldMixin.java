package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.DynamicEcosystem;
import com.harleylizard.ecosystem.world.BlockPos;
import com.harleylizard.ecosystem.world.DynamicEcosystemWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(World.class)
public final class WorldMixin implements DynamicEcosystemWorld {
    @Override
    public void setBiome(BiomeGenBase biomeGenBase, BlockPos blockPos) {
        DynamicEcosystem.PROXY.setBiome((World) (Object) this, biomeGenBase, blockPos);
    }
}
