package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;

import java.util.EnumMap;

/** Substitute only the rendered eye; tracked devices and tick-time poses stay real. */
public final class ThirdViveEyes {
    private static RenderPass renderingPass;
    private static int bypassDepth;
    private static long frame = -1;
    private static VRData frameData;
    private static boolean frameFront;
    private static Vec3 cameraCenter;
    private static Matrix4f flip = new Matrix4f();
    private static final EnumMap<RenderPass, VRData.VRDevicePose> CACHE = new EnumMap<>(RenderPass.class);

    private ThirdViveEyes() {}

    public static void beginPass(RenderPass pass) { renderingPass = pass; }
    public static void endPass() { renderingPass = null; }

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
        boolean front = ThirdViveState.front();
        if (currentFrame != frame || data != frameData || front != frameFront) {
            computeFrame(data, front);
            frame = currentFrame;
            frameData = data;
            frameFront = front;
            CACHE.clear();
        }
        return CACHE.computeIfAbsent(pass, ignored -> build(data, realEye));
    }

    private static void computeFrame(VRData data, boolean front) {
        Minecraft mc = Minecraft.getInstance();
        Matrix4f headRot = data.hmd.getMatrix();
        Vec3 pivot = ThirdViveAnchor.headCenter(data);
        Vec3 look = new Vec3(data.hmd.getDirection()).normalize();
        Vec3 dir = front ? look : look.scale(-1);
        float wanted = VanillaThirdPerson.distance(mc.player, ThirdVive.config.distance);
        float clipped = VanillaThirdPerson.maxZoom(mc.level, mc.player, pivot, dir, wanted);
        cameraCenter = pivot.add(dir.scale(clipped));
        Vector3f up = headRot.transformDirection(new Vector3f(0, 1, 0)).normalize();
        flip = front ? new Matrix4f().rotation((float) Math.PI, up) : new Matrix4f();
    }

    private static VRData.VRDevicePose build(VRData data, VRData.VRDevicePose real) {
        Vec3 hmd = data.hmd.getPosition();
        Vec3 realPos = real.getPosition();
        Vector3f eyeOffset = new Vector3f((float) (realPos.x - hmd.x),
            (float) (realPos.y - hmd.y), (float) (realPos.z - hmd.z));
        flip.transformDirection(eyeOffset);
        Vec3 world = cameraCenter.add(eyeOffset.x, eyeOffset.y, eyeOffset.z);
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
