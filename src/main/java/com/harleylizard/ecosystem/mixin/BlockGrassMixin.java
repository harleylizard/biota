package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.Direction;
import com.harleylizard.ecosystem.EcosystemGetter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(BlockGrass.class)
public final class BlockGrassMixin {

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;)Z", shift = At.Shift.BEFORE, ordinal = 1), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void takeNourishment(World worldIn, int x, int y, int z, Random random, CallbackInfo ci, int l, int i1, int j1, int k1, Block block) {
        if (!worldIn.isRemote) {
            if (((EcosystemGetter) worldIn).takeNourishment(x, y, z, 4)) {
                ((EcosystemGetter) worldIn).addNourishment(i1, j1, k1, 4);
            }
        }
    }

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), cancellable = true)
    public void shouldSpread(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            int nourishment = ((EcosystemGetter) worldIn).getNourishment(x, y, z);
            if (nourishment - 4 < 0) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "updateTick", at = @At(value = "HEAD"))
    public void transferNourishment(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            int nourishment = ((EcosystemGetter) worldIn).getNourishment(x, y, z);
            if (nourishment - 1 > 0) {
                for (Direction direction : Direction.HORIZONTAL) {
                    int i = direction.getX() + x;
                    int j = direction.getZ() + z;

                    if (worldIn.getBlock(i, y, j) == Blocks.grass) {
                        int comparing = ((EcosystemGetter) worldIn).getNourishment(i, y, j);
                        if (nourishment > comparing) {
                            ((EcosystemGetter) worldIn).takeNourishment(x, y, z, 1);
                            ((EcosystemGetter) worldIn).addNourishment(i, y, j, 1);
                        }
                    }
                }
            }
        }
    }
}
