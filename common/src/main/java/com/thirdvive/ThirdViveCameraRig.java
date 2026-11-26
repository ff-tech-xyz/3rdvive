package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/**
 * The third-person camera, worked out once per frame before any VR view is drawn.
 *
 * It only decides where the eyes are drawn from. It never moves the player or Vivecraft's room,
 * so Vivecraft's movement, roomscale walking, aiming and controllers all work like first person.
 *
 * - Anchor: the character's interpolated position plus a smoothed eye height (no pop when sneaking).
 * - Boom: a smoothed yaw (character body, or the room's facing) with a fixed downward angle.
 *   Turning your head turns the view 1:1 but never swings the camera around.
 * - Distance: the vanilla 8-ray wall check. Pulls in at once, eases back out slowly.
 * - Head movement: your real head offset since the last recenter moves the camera 1:1, so leaning
 *   still gives depth.
 */
public final class ThirdViveCameraRig {
    private static final double MAX_HEAD_OFFSET = 0.75;
    private static final double ZOOM_OUT_SECONDS = 0.35;
    private static final double EYE_HEIGHT_SECONDS = 0.1;

    private static boolean active;
    private static boolean recenterRequested;
    private static long lastNanos;
    private static float yaw;
    private static float distance;
    private static float eyeHeight;
    private static Vec3 headBaseline = Vec3.ZERO;
    private static Vec3 center = Vec3.ZERO;
    private static Matrix4f flip = new Matrix4f();

    private ThirdViveCameraRig() {}

    /** Snap the camera behind the character and reset the lean baseline on the next frame. */
    public static void recenter() { recenterRequested = true; }

    public static Vec3 center() { return center; }

    /** Extra rotation applied to the head for the camera (180 degrees for the front view). */
    public static Matrix4f flip() { return flip; }

    /** Head center: the same pivot the eye offsets are measured from. */
    public static Vec3 headCenter(VRData data) {
        Vector3f back = data.hmd.getMatrix().transformDirection(new Vector3f(0, 0, 0.1F * data.worldScale));
        return data.hmd.getPosition().add(back.x, back.y, back.z);
    }

    /** Called right after Vivecraft builds vrdata_world_render for this frame. */
    public static void update(VRPlayer vr, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || vr.vrdata_world_render == null || !ThirdViveState.locked()) {
            active = false;
            return;
        }
        VRData data = vr.vrdata_world_render;
        ThirdViveConfig cfg = ThirdVive.config;

        long now = System.nanoTime();
        double dt = active ? Mth.clamp((now - lastNanos) / 1.0E9, 0.0, 0.1) : 0.0;
        lastNanos = now;

        Vec3 feet = player.getPosition(partialTick);
        Vec3 head = headCenter(data);
        float targetYaw = targetYaw(player, data, partialTick);
        float targetEye = player.getEyeHeight();
        boolean snap = !active || recenterRequested;

        if (snap) {
            yaw = targetYaw;
            eyeHeight = targetEye;
            headBaseline = head.subtract(feet);
            distance = -1;
            recenterRequested = false;
        } else {
            yaw += Mth.wrapDegrees(targetYaw - yaw) * follow(dt, cfg.followSeconds);
            eyeHeight += (targetEye - eyeHeight) * follow(dt, EYE_HEIGHT_SECONDS);
        }
        active = true;

        Vec3 anchor = feet.add(0, eyeHeight, 0);

        boolean front = ThirdViveState.front();
        float boomYaw = front ? yaw + 180.0F : yaw;
        Vec3 boom = boomDirection(boomYaw, cfg.cameraPitch);

        float wanted = VanillaThirdPerson.distance(player, cfg.distance);
        float clear = VanillaThirdPerson.maxZoom(mc.level, player, anchor, boom, wanted);
        if (distance < 0 || clear < distance) {
            distance = clear; // never let the camera sit inside a wall
        } else {
            distance += (clear - distance) * follow(dt, ZOOM_OUT_SECONDS);
        }

        Vec3 lean = head.subtract(feet).subtract(headBaseline);
        if (lean.length() > MAX_HEAD_OFFSET) lean = lean.normalize().scale(MAX_HEAD_OFFSET);
        // The front view turns the whole view around, so a lean to your left is the world's right.
        if (front) lean = new Vec3(-lean.x, lean.y, -lean.z);

        center = anchor.add(boom.scale(distance)).add(lean);
        flip = front ? new Matrix4f().rotationY((float) Math.PI) : new Matrix4f();
    }

    private static float targetYaw(LocalPlayer player, VRData data, float partialTick) {
        if (ThirdVive.config.follow == ThirdViveConfig.Follow.ROOM) {
            // Room facing only changes when you snap or smooth turn, so the camera never orbits.
            Vector3f forward = new Vector3f(0, 0, -1).rotateY(data.rotation_radians);
            return (float) Math.toDegrees(Math.atan2(-forward.x, forward.z));
        }
        return Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
    }

    /** Unit vector from the anchor out to the camera: behind the yaw and raised by the pitch. */
    private static Vec3 boomDirection(float yawDegrees, float pitchDegrees) {
        double y = Math.toRadians(yawDegrees);
        double p = Math.toRadians(pitchDegrees);
        // Minecraft forward for a yaw is (-sin, 0, cos); the camera goes the other way, and up.
        return new Vec3(Math.sin(y) * Math.cos(p), Math.sin(p), -Math.cos(y) * Math.cos(p));
    }

    /** Frame-rate independent smoothing factor. */
    private static float follow(double dt, double seconds) {
        if (seconds <= 0) return 1.0F;
        return (float) (1.0 - Math.exp(-dt / seconds));
    }
}
