package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/**
 * lockMovement (on by default): in third person, moves Vivecraft's room every tick and frame so your real head
 * stays on the character's eyes. Real walking and leaning then don't move you; you move with the stick.
 * When off, roomscale walking moves your character as in normal Vivecraft.
 */
public final class ThirdViveAnchor {
    private static boolean wasLocked;

    private ThirdViveAnchor() {}

    /**
     * Moves the room so the head sits on the character's eye. Sideways it lines up Vivecraft's own
     * head pivot, the same point roomscale walking moves the player to, so the two never fight.
     * Height uses the camera's head center so the view height is unchanged.
     */
    private static Vec3 pin(VRData data, Vec3 eye) {
        Vec3 pivot = data.getHeadPivot();
        Vec3 center = ThirdViveCamera.headCenter(data);
        data.origin = data.origin.add(eye.x - pivot.x, eye.y - center.y, eye.z - pivot.z);
        return data.origin;
    }

    /** Restore Vivecraft's floor alignment before it constructs the next tick's poses. */
    public static void beforePreTick(VRPlayer vr) {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean locked = player != null && ThirdViveState.movementLocked();
        if (wasLocked && !locked && player != null
            && ClientDataHolderVR.getInstance().sneakTracker.sneakCounter <= 0) {
            vr.snapRoomOriginToPlayerEntity(player, true, true);
            wasLocked = false;
        }
        if (locked) wasLocked = true;
    }

    public static void onPreTick(VRPlayer vr) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ThirdViveState.movementLocked()) return;
        Vec3 origin = pin(vr.vrdata_world_pre, player.getEyePosition());
        vr.setRoomOrigin(origin.x, origin.y, origin.z, false);
    }

    public static void onPostTick(VRPlayer vr) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ThirdViveState.movementLocked()) return;
        Vec3 origin = pin(vr.vrdata_world_post, player.getEyePosition());
        vr.setRoomOrigin(origin.x, origin.y, origin.z, false);
    }

    /** Only the render copy follows the interpolated eye; the room-space input stays physical. */
    public static void onRenderDataBuilt(VRPlayer vr, float partialTick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ThirdViveState.movementLocked()) return;
        pin(vr.vrdata_world_render, player.getEyePosition(partialTick));
    }
}
