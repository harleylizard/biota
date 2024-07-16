package com.harleylizard.ecosystem.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockGrass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDeadBush.class)
public final class BlockDeadBushMixin {

    @Inject(method = "canPlaceBlockOn", at = @At("RETURN"), cancellable = true)
    public void canPlaceBlockOn(Block ground, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || ground instanceof BlockGrass);
    }
}
