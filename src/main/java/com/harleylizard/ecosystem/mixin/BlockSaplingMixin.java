package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.Ecosystem;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockSapling.class)
public final class BlockSaplingMixin {

    @Inject(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/WorldGenerator;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z", shift = At.Shift.AFTER), remap = false)
    public void growTree(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);

        Ecosystem ecosystem = Ecosystem.getOrCreate(chunk);

        Ecosystem.toClient(chunk, ecosystem);
    }
}
