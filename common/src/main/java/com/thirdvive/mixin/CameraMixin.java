package com.thirdvive.mixin;

import com.thirdvive.ThirdVive;
import com.thirdvive.ThirdViveCamera;
import com.thirdvive.ThirdViveState;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private @Nullable Level level;
    @Shadow private Entity entity;
    @Shadow protected abstract void setPosition(Vec3 position);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    // alignWithEntity has already run Vivecraft's cancellable VR orientation hook here.
    // update has not yet prepared its culling frustum from the camera position.
    @Inject(method = "update", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/Camera;alignWithEntity(F)V", shift = At.Shift.AFTER))
    private void thirdvive$relocate(DeltaTracker delta, CallbackInfo ci) {
        ClientDataHolderVR holder = ClientDataHolderVR.getInstance();
        RenderPass pass = holder.currentPass;
        if (!ThirdViveState.activeForPass(pass) || level == null || entity == null) return;
        VRData data = holder.vrPlayer.getVRDataWorld();
        ThirdViveCamera.Pose pose = ThirdViveCamera.compute(level, entity, data, pass,
            ThirdVive.config.distance * data.worldScale, ThirdViveState.front());
        setPosition(pose.position());
        setRotation(pose.yaw(), pose.pitch());
    }
}
