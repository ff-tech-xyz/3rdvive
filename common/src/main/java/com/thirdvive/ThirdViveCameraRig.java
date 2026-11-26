package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/**
 * The third-person camera, worked out once per frame before any VR view is drawn.
 *
 * Same feel as the original 3rdVive: the camera sits on a direct stick off your head, pivoting on
 * the character's eye. Look around and the camera swings with your head, with no smoothing.
 * The difference is only under the hood: it is computed for drawing only, and never moves the
 * player or Vivecraft's room, so movement works.
 */
public final class ThirdViveCameraRig {
    private static Vec3 center = Vec3.ZERO;
    private static Matrix4f flip = new Matrix4f();

    private ThirdViveCameraRig() {}

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
        if (player == null || mc.level == null || vr.vrdata_world_render == null || !ThirdViveState.locked()) return;
        VRData data = vr.vrdata_world_render;

        boolean front = ThirdViveState.front();
        Vec3 pivot = player.getEyePosition(partialTick);
        Vec3 look = new Vec3(data.hmd.getDirection()).normalize();
        Vec3 dir = front ? look : look.scale(-1);
        float wanted = VanillaThirdPerson.distance(player, ThirdVive.config.distance);
        float clipped = VanillaThirdPerson.maxZoom(mc.level, player, pivot, dir, wanted);
        center = pivot.add(dir.scale(clipped));

        Vector3f up = data.hmd.getMatrix().transformDirection(new Vector3f(0, 1, 0)).normalize();
        flip = front ? new Matrix4f().rotation((float) Math.PI, up) : new Matrix4f();
    }
}
