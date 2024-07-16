package com.harleylizard.dynamicecosystem.mixin.client;

import com.harleylizard.dynamicecosystem.chunk.ClientNutrients;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTallGrass.class)
public final class BlockTallGrassMixin implements ClientNutrients.AffectsColor {

    @Inject(method = "colorMultiplier", at = @At("RETURN"), cancellable = true)
    public void colorMultiplier(IBlockAccess worldIn, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ClientNutrients.AffectsColor.changeColor(cir.getReturnValueI(), x, y, z));
    }
}
