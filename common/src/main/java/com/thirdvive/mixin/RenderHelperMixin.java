package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.render.helpers.RenderHelper;

@Mixin(RenderHelper.class)
public class RenderHelperMixin {
    @ModifyReturnValue(method = "getVRModelView", at = @At("RETURN"))
    private static Matrix4f thirdvive$frontView(Matrix4f original, RenderPass pass) {
        if (ThirdViveState.activeForPass(pass) && ThirdViveState.front()) {
            return new Matrix4f().rotationY((float) Math.PI).mul(original);
        }
        return original;
    }
}
