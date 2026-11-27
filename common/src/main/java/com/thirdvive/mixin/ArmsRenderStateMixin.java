package com.thirdvive.mixin;

import com.thirdvive.ThirdViveCamera;
import com.thirdvive.ThirdViveState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.render.renderstates.ArmsRenderState;

/**
 * While a menu is open in third person, draw Vivecraft's grey menu hands next to the camera, where your hands
 * are pointing from, instead of at the character a few blocks away.
 */
@Mixin(ArmsRenderState.class)
public class ArmsRenderStateMixin {
    @Inject(method = "extract", at = @At("RETURN"))
    private void thirdvive$menuHandsAtCamera(LocalPlayer player, Vec3 headPos, CallbackInfo ci) {
        ClientDataHolderVR holder = ClientDataHolderVR.getInstance();
        if (holder.vrPlayer == null || holder.vrPlayer.vrdata_world_render == null
            || !ThirdViveState.uiShowingForPass(holder.currentPass)) return;
        ArmsRenderState self = (ArmsRenderState) (Object) this;
        if (self.mainHandWorldPos == null || self.offHandWorldPos == null) return;
        VRData data = holder.vrPlayer.vrdata_world_render;
        Vec3 center = ThirdViveCamera.center(data);
        Matrix4f flip = ThirdViveCamera.flip(data);
        self.mainHandWorldPos = ThirdViveCamera.shiftPoint(data, center, flip, self.mainHandWorldPos);
        self.offHandWorldPos = ThirdViveCamera.shiftPoint(data, center, flip, self.offHandWorldPos);
        self.mainHandWorldRot.set(new Matrix4f(flip).mul(self.mainHandWorldRot));
        self.offHandWorldRot.set(new Matrix4f(flip).mul(self.offHandWorldRot));
        self.menuHandMain = true;
        self.menuHandOff = true;
    }
}
