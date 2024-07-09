package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.world.BlockPos;
import com.harleylizard.ecosystem.world.DynamicEcosystemWorld;
import com.harleylizard.ecosystem.world.RandomTicking;
import net.minecraft.block.BlockLeavesBase;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(BlockLeavesBase.class)
public final class BlockLeavesBaseMixin implements RandomTicking {
    @Override
    public void randomTick(DynamicEcosystemWorld world, BlockPos blockPos, int meta, Random random) {

    }
}
