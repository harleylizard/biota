package com.harleylizard.ecosystem.mixin;

import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockMushroom.class)
public final class BlockMushroomMixin {

    @Inject(method = "updateTick", at = @At("TAIL"))
    public void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
    }
}
