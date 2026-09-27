package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/** Translates world-space tracking so the head pivot follows the character's eye. */
public final class ThirdViveAnchor {
    private static boolean wasLocked;

    private ThirdViveAnchor() {}

    /** The same room +Z head pivot used by the third-person camera. */
    public static Vec3 headCenter(VRData data) {
        Vector3f back = data.hmd.getMatrix().transformDirection(new Vector3f(0, 0, 0.1F * data.worldScale));
        return data.hmd.getPosition().add(back.x, back.y, back.z);
    }

    private static Vec3 pin(VRData data, Vec3 eye) {
        data.origin = data.origin.add(eye.subtract(headCenter(data)));
        return data.origin;
    }

    /** Restore Vivecraft's floor alignment before it constructs the next tick's poses. */
    public static void beforePreTick(VRPlayer vr) {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean locked = player != null && ThirdViveState.locked();
        if (wasLocked && !locked && player != null
            && ClientDataHolderVR.getInstance().sneakTracker.sneakCounter <= 0) {
            vr.snapRoomOriginToPlayerEntity(player, true, true);
            wasLocked = false;
        }
        if (locked) wasLocked = true;
    }

    public static void onPreTick(VRPlayer vr) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ThirdViveState.locked()) return;
        Vec3 origin = pin(vr.vrdata_world_pre, player.getEyePosition());
        vr.setRoomOrigin(origin.x, origin.y, origin.z, false);
    }

    public static void onPostTick(VRPlayer vr) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ThirdViveState.locked()) return;
        Vec3 origin = pin(vr.vrdata_world_post, player.getEyePosition());
        vr.setRoomOrigin(origin.x, origin.y, origin.z, false);
    }

    /** Only the render copy follows the interpolated eye; the room-space input stays physical. */
    public static void onRenderDataBuilt(VRPlayer vr, float partialTick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ThirdViveState.locked()) return;
        pin(vr.vrdata_world_render, player.getEyePosition(partialTick));
    }
}
