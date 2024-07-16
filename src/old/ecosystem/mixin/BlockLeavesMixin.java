package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.world.BiomeSapling;
import net.minecraft.block.BlockLeaves;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockLeaves.class)
public abstract class BlockLeavesMixin {

    @Inject(method = "updateTick", at = @At("HEAD"))
    public void updateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
        if (!worldIn.isRemote && random.nextInt(100) == 0) {
            BiomeSapling.SaplingInfo info = BiomeSapling.getSaplingInfo(worldIn.getBlockMetadata(x, y, z) & 3);
            if (info != null) {
                int range = 5;
                for (int i = -range; i < range; i++) for (int j = -range; j < range; j++) {
                    if (random.nextInt(10) == 0) {
                        int k = x + i;
                        int l = z + j;

                        int height = worldIn.getHeightValue(k, l);
                        if (BiomeSapling.canSpread(worldIn, info, k, height, l, random)) {
                            info.placeSapling(worldIn, k, height, l);
                            break;
                        }
                    }
                }
            }
        }
    }
}
