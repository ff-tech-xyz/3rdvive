package com.thirdvive.mixin;

import com.thirdvive.ThirdViveCameraRig;
import com.thirdvive.ThirdViveDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/** Read-only hooks: 3rdVive no longer changes the room origin or the player here. */
@Mixin(VRPlayer.class)
public class VRPlayerMixin {
    @Inject(method = "postTick", at = @At("TAIL"))
    private void thirdvive$debugTick(CallbackInfo ci) {
        ThirdViveDebug.tick((VRPlayer) (Object) this);
    }

    // vrdata_world_render is built for this frame by now, and no VR view has been drawn yet.
    @Inject(method = "preRender", at = @At("TAIL"))
    private void thirdvive$updateCamera(float partialTick, CallbackInfo ci) {
        ThirdViveCameraRig.update((VRPlayer) (Object) this, partialTick);
        ThirdViveDebug.frame(partialTick);
    }
}
