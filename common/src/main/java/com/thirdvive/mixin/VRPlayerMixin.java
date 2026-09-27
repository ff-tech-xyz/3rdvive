package com.thirdvive.mixin;

import com.thirdvive.ThirdViveAnchor;
import com.thirdvive.ThirdViveState;
import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.gameplay.VRPlayer;

@Mixin(VRPlayer.class)
public class VRPlayerMixin {
    @Inject(method = "doPlayerMoveInRoom", at = @At("HEAD"), cancellable = true)
    private void thirdvive$noRoomscaleWalk(LocalPlayer player, CallbackInfo ci) {
        if (ThirdViveState.locked()) ci.cancel();
    }

    @Inject(method = "preTick", at = @At("HEAD"))
    private void thirdvive$restoreOnExit(CallbackInfo ci) {
        ThirdViveAnchor.beforePreTick((VRPlayer) (Object) this);
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

    @Inject(method = "preRender", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
        target = "Lorg/vivecraft/client_vr/gameplay/VRPlayer;vrdata_world_render:Lorg/vivecraft/client_vr/VRData;",
        shift = At.Shift.AFTER))
    private void thirdvive$pinRender(float partialTick, CallbackInfo ci) {
        ThirdViveAnchor.onRenderDataBuilt((VRPlayer) (Object) this, partialTick);
    }
}
