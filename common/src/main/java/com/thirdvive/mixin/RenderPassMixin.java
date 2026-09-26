package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.client.data.RenderPass;

@Mixin(RenderPass.class)
public class RenderPassMixin {
    @ModifyReturnValue(method = "renderPlayer", at = @At("RETURN"))
    private static boolean thirdvive$renderSelf(boolean original, RenderPass pass) {
        return original || ThirdViveState.activeForPass(pass);
    }
}
