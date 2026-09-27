package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.render.renderstates.VRRenderState;

@Mixin(VRRenderState.class)
public class VRRenderStateMixin {
    @Shadow public RenderPass currentPass;
    @Shadow public boolean inBlock;
    @Shadow public boolean inWater;
    @Shadow public boolean firstPersonFire;

    @Inject(method = "extract", at = @At("RETURN"))
    private void thirdvive$noFaceOverlays(CallbackInfo ci) {
        if (ThirdViveState.activeForPass(this.currentPass)) {
            this.inBlock = false;
            this.inWater = false;
            this.firstPersonFire = false;
        }
    }
}
