package com.thirdvive;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;

import java.util.EnumMap;

/**
 * Substitute only the rendered eye; tracked devices and tick-time poses stay real.
 * The camera position comes from {@link ThirdViveCameraRig}; this class only builds the per-eye
 * poses around it, keeping the real head rotation and the real distance between your eyes.
 */
public final class ThirdViveEyes {
    private static RenderPass renderingPass;
    private static int bypassDepth;
    private static long frame = -1;
    private static VRData frameData;
    private static final EnumMap<RenderPass, VRData.VRDevicePose> CACHE = new EnumMap<>(RenderPass.class);

    private ThirdViveEyes() {}

    public static void beginPass(RenderPass pass) { renderingPass = pass; }
    public static void endPass() { renderingPass = null; }

    /** True only while Vivecraft is drawing one VR view. */
    public static boolean inPass() { return renderingPass != null; }

    public static void runBypassed(Runnable action) {
        bypassDepth++;
        try { action.run(); } finally { bypassDepth--; }
    }

    public static boolean shouldReplace(VRData data, RenderPass pass) {
        ClientDataHolderVR holder = ClientDataHolderVR.getInstance();
        return bypassDepth == 0 && renderingPass == pass
            && RenderPass.isFirstPerson(pass)
            && holder.vrPlayer != null && data == holder.vrPlayer.vrdata_world_render
            && ThirdViveState.activeForPass(pass);
    }

    public static VRData.VRDevicePose replace(VRData data, RenderPass pass, VRData.VRDevicePose realEye) {
        long currentFrame = ClientDataHolderVR.getInstance().frameIndex;
        if (currentFrame != frame || data != frameData) {
            frame = currentFrame;
            frameData = data;
            CACHE.clear();
        }
        return CACHE.computeIfAbsent(pass, ignored -> build(data, realEye));
    }

    private static VRData.VRDevicePose build(VRData data, VRData.VRDevicePose real) {
        Matrix4f flip = ThirdViveCameraRig.flip();
        Vec3 pivot = ThirdViveCameraRig.headCenter(data);
        Vec3 realPos = real.getPosition();
        // Where this eye sits relative to the head center, turned with the camera.
        Vector3f eyeOffset = new Vector3f((float) (realPos.x - pivot.x),
            (float) (realPos.y - pivot.y), (float) (realPos.z - pivot.z));
        flip.transformDirection(eyeOffset);
        Vec3 world = ThirdViveCameraRig.center().add(eyeOffset.x, eyeOffset.y, eyeOffset.z);
        Matrix4f worldRot = new Matrix4f(flip).mul(real.getMatrix());
        Vector3f worldDir = flip.transformDirection(real.getDirection());

        // VRDevicePose stores room coordinates, transformed by VRData on every world-space read.
        float rotation = data.rotation_radians;
        Vector3f roomPos = new Vector3f((float) (world.x - data.origin.x),
            (float) (world.y - data.origin.y), (float) (world.z - data.origin.z))
            .rotateY(-rotation).div(data.worldScale);
        Matrix4f roomRot = new Matrix4f().rotationY(-rotation).mul(worldRot);
        Vector3f roomDir = worldDir.rotateY(-rotation);
        return data.new VRDevicePose(data, roomRot, roomPos, roomDir);
    }
}
