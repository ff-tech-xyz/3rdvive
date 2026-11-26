package com.thirdvive.mixin;

import com.thirdvive.ThirdViveCameraRig;
import com.thirdvive.ThirdViveDebug;
import com.thirdvive.ThirdViveRve;
import com.thirdvive.ThirdViveState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/** 3rdVive never changes the room origin; it only makes sure the player is not left at the eye. */
@Mixin(VRPlayer.class)
public class VRPlayerMixin {
    @Inject(method = "preTick", at = @At("HEAD"))
    private void thirdvive$realPositionForTick(CallbackInfo ci) {
        if (ThirdViveState.locked()) ThirdViveRve.closeOpenSave(); // never tick with the player at the eye
    }

    @Inject(method = "preRender", at = @At("HEAD"))
    private void thirdvive$realPositionForFrame(float partialTick, CallbackInfo ci) {
        if (ThirdViveState.locked()) ThirdViveRve.closeOpenSave();
    }

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
