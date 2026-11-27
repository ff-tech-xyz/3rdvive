package com.thirdvive.mixin;

import com.thirdvive.ThirdViveCamera;
import com.thirdvive.ThirdViveRve;
import com.thirdvive.ThirdViveState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/** 3rdVive never moves Vivecraft's room; it only makes sure the player is never left at the eye. */
@Mixin(VRPlayer.class)
public class VRPlayerMixin {
    @Inject(method = "preTick", at = @At("HEAD"))
    private void thirdvive$realPositionForTick(CallbackInfo ci) {
        if (ThirdViveState.active()) ThirdViveRve.closeOpenSave(); // never tick with the player at the eye
    }

    @Inject(method = "preRender", at = @At("HEAD"))
    private void thirdvive$realPositionForFrame(float partialTick, CallbackInfo ci) {
        if (ThirdViveState.active()) ThirdViveRve.closeOpenSave();
        ThirdViveCamera.setPartialTick(partialTick);
    }
}
