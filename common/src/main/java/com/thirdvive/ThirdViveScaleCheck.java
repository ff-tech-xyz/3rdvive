package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.client_vr.VRData;

/**
 * TEMPORARY: tracking down "the back view makes everything look smaller". Once every 2 seconds in third person
 * it logs the numbers that decide how big the world looks. Remove when the bug is found.
 */
public final class ThirdViveScaleCheck {
    private static long lastLog;

    private ThirdViveScaleCheck() {}

    public static void check(VRData data, VRData.VRDevicePose left, VRData.VRDevicePose right) {
        long now = System.currentTimeMillis();
        if (now - lastLog < 2000) return;
        lastLog = now;
        Vec3 realL = data.eye0.getPosition(), realR = data.eye1.getPosition();
        Vec3 camL = left.getPosition(), camR = right.getPosition();
        Vec3 hmd = data.hmd.getPosition();
        Vec3 cam = ThirdViveCamera.center(data);
        Vec3 pivot = ThirdViveCamera.pivot(data);
        Vec3 eye = Minecraft.getInstance().player.getEyePosition();
        ThirdVive.LOGGER.info(
            "[scale-check] view={} worldScale={} distance={} realEyeGap={} cameraEyeGap={} cameraToPivot={} "
                + "headToCharacterEye={} realEyesMid-hmd={} cameraEyesMid-camera={} realLook={} camLook={}",
            ThirdViveState.front() ? "FRONT" : "BACK", f(data.worldScale), f(ThirdViveCamera.distance(data)),
            f(realL.distanceTo(realR)), f(camL.distanceTo(camR)), f(cam.distanceTo(pivot)),
            f(hmd.distanceTo(eye)), v(realL.add(realR).scale(0.5).subtract(hmd)),
            v(camL.add(camR).scale(0.5).subtract(cam)),
            v(new Vec3(data.eye0.getDirection())), v(new Vec3(left.getDirection())));
    }

    private static String f(double d) { return String.format("%.3f", d); }
    private static String v(Vec3 p) { return String.format("(%.3f %.3f %.3f)", p.x, p.y, p.z); }
}
