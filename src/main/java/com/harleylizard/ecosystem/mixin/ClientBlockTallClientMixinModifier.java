package com.harleylizard.ecosystem.mixin;

import com.harleylizard.ecosystem.ClientColorModifier;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTallGrass.class)
public final class ClientBlockTallClientMixinModifier implements ClientColorModifier {

    @Inject(method = "colorMultiplier", at = @At("RETURN"), cancellable = true)
    public void colorMultiplier(IBlockAccess worldIn, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(modifyColor(cir.getReturnValueI(), x, y, z));
    }
}
