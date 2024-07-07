package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.Direction;
import com.harleylizard.ecosystem.world.BiomeInfluence;
import com.harleylizard.ecosystem.world.EcosystemWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
            if (((EcosystemWorld) worldIn).removeNourishment(x, y, z, 5)) {
                ((EcosystemWorld) worldIn).addNourishment(i1, j1, k1, 4);
            }
        }
    }

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), cancellable = true)
    public void shouldSpread(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            int nourishment = ((EcosystemWorld) worldIn).getNourishment(x, y, z);
            if (nourishment - 4 < 0) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "updateTick", at = @At(value = "HEAD"), cancellable = true)
    public void transferNourishment(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            int nourishment = ((EcosystemWorld) worldIn).getNourishment(x, y, z);
            if (nourishment == 0 && random.nextInt(10) == 0) {
                boolean podzol = shouldBecomePodzol(worldIn, x, y, z);
                worldIn.setBlock(x, y, z, Blocks.dirt, podzol ? 2 : 0, 4);
                ci.cancel();
                return;
            }
            if (!worldIn.canBlockSeeTheSky(x, y + 1, z) && worldIn.getBlockLightValue(x, y + 1, z) < 1) {
                ((EcosystemWorld) worldIn).removeNourishment(x, y, z, random.nextInt(5) == 0 ? 8 : 4);
                ci.cancel();
                return;
            }
            if (worldIn.isRainingAt(x, y + 1, z) && random.nextInt(5) == 0) {
                ((EcosystemWorld) worldIn).addNourishment(x, y, z, 2);
            }

            if (nourishment - 1 > 0) {
                for (Direction direction : Direction.HORIZONTAL) {
                    int i = direction.getX() + x;
                    int j = direction.getZ() + z;

                    if (worldIn.getBlock(i, y, j) == Blocks.grass) {
                        int comparing = ((EcosystemWorld) worldIn).getNourishment(i, y, j);
                        if (nourishment > comparing) {
                            ((EcosystemWorld) worldIn).removeNourishment(x, y, z, 1);
                            ((EcosystemWorld) worldIn).addNourishment(i, y, j, 1);

                            if (random.nextInt(5) == 0) {
                                BiomeInfluence.setBiomeFromInfluence(worldIn, x, y, z);
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    private boolean shouldBecomePodzol(World world, int x, int y, int z) {
        int next = y + 1;
        int steps = 0;
        while (world.getBlock(x, next, z) == Blocks.air) {
            if (steps > 5) {
                break;
            }
            next++;
            steps++;
        }
        Block block = world.getBlock(x, next, z);
        return block instanceof BlockLeavesBase;
    }
}
