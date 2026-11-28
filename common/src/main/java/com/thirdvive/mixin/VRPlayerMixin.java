package com.thirdvive.mixin;

import com.thirdvive.ThirdViveAnchor;
import com.thirdvive.ThirdViveCamera;
import com.thirdvive.ThirdViveRve;
import com.thirdvive.ThirdViveState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/**
 * Makes sure the player is never left at the eye, and (with lockMovement) moves Vivecraft's room so your head
 * stays on the character.
 */
@Mixin(VRPlayer.class)
public class VRPlayerMixin {
    @Inject(method = "preTick", at = @At("HEAD"))
    private void thirdvive$realPositionForTick(CallbackInfo ci) {
        if (ThirdViveState.active()) ThirdViveRve.closeOpenSave(); // never tick with the player at the eye
        ThirdViveAnchor.beforePreTick((VRPlayer) (Object) this);
    }

    /**
     * With lockMovement, roomscale walking is off: real steps never move the character. Without this, Vivecraft
     * moves the character to your feet (full-body tracking) or head each tick, the lock moves the room after it,
     * and you get dragged along forever.
     */
    @Inject(method = "doPlayerMoveInRoom", at = @At("HEAD"), cancellable = true)
    private void thirdvive$noRoomscaleWhenLocked(CallbackInfo ci) {
        if (ThirdViveState.movementLocked()) ci.cancel();
    }

    // Pin before Vivecraft snapshots vrdata_world_pre into its pose history.
    @Inject(method = "preTick", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
        target = "Lorg/vivecraft/client_vr/gameplay/VRPlayer;vrdata_world_pre:Lorg/vivecraft/client_vr/VRData;",
        shift = At.Shift.AFTER))
    private void thirdvive$pinPre(CallbackInfo ci) {
        ThirdViveAnchor.onPreTick((VRPlayer) (Object) this);
    }

    @Inject(method = "postTick", at = @At(value = "INVOKE",
        target = "Lorg/vivecraft/client_vr/gameplay/VRPlayer;doPermanentLookOverride(Lnet/minecraft/client/player/LocalPlayer;Lorg/vivecraft/client_vr/VRData;)V"))
    private void thirdvive$pinPost(CallbackInfo ci) {
        ThirdViveAnchor.onPostTick((VRPlayer) (Object) this);
    }

    @Inject(method = "preRender", at = @At("HEAD"))
    private void thirdvive$realPositionForFrame(float partialTick, CallbackInfo ci) {
        if (ThirdViveState.active()) ThirdViveRve.closeOpenSave();
        ThirdViveCamera.setPartialTick(partialTick);
    }

    @Inject(method = "preRender", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
        target = "Lorg/vivecraft/client_vr/gameplay/VRPlayer;vrdata_world_render:Lorg/vivecraft/client_vr/VRData;",
        shift = At.Shift.AFTER))
    private void thirdvive$pinRender(float partialTick, CallbackInfo ci) {
        ThirdViveAnchor.onRenderDataBuilt((VRPlayer) (Object) this, partialTick);
    }
}
