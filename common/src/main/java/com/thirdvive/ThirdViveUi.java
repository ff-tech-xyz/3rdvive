package com.thirdvive;

import com.thirdvive.mixin.RadialHandlerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.client_vr.gameplay.screenhandlers.RadialHandler;

/**
 * Menus, the keyboard and the radial menu in third person.
 *
 * Vivecraft places these in front of your real head and points at them with your real hands. In third
 * person your real head is at the character, a few blocks from the camera. So while Vivecraft places or
 * points at one of them, 3rdVive hands it the camera instead of the head, and your hands moved along with
 * the camera. The menu then opens in front of the camera and your hands reach it as in first person.
 *
 * The camera swings around your head when you turn it, so a menu that stayed put would slide out of view
 * with tiny head turns. Instead an open menu slides along with the camera (it does not turn with it), the
 * same way your camera-side hands do, so it stays in reach. Container screens shown over their block stay
 * over the block.
 */
public final class ThirdViveUi {
    private static int shiftDepth;
    /** Camera center (room space) when each menu was last placed or moved; null when it does not follow. */
    private static Vector3f guiCamera;
    private static Vector3f keyboardCamera;
    private static Vector3f radialCamera;

    private ThirdViveUi() {}

    public static boolean shifting() { return shiftDepth > 0; }

    /** Runs Vivecraft's placing or pointing code, with head and hands moved to the camera when {@code shift}. */
    public static void run(boolean shift, Runnable action) {
        if (!shift) {
            action.run();
            return;
        }
        shiftDepth++;
        try { action.run(); } finally { shiftDepth--; }
    }

    public static VRData.VRDevicePose head(VRData data, VRData.VRDevicePose real) {
        return shifting() ? ThirdViveCamera.camera(data) : real;
    }

    public static VRData.VRDevicePose hand(VRData data, VRData.VRDevicePose real) {
        return shifting() ? ThirdViveCamera.shift(data, real) : real;
    }

    private static Vector3f roomCamera() {
        // The room copy has no offset, scale or turn, so its world positions are room positions.
        Vec3 center = ThirdViveCamera.center(ClientDataHolderVR.getInstance().vrPlayer.vrdata_room_pre);
        return new Vector3f((float) center.x, (float) center.y, (float) center.z);
    }

    /** Moves {@code pos} by how far the camera moved since {@code last}; returns the new camera spot. */
    private static Vector3f slide(Vector3f pos, Vector3f last) {
        Vector3f now = roomCamera();
        pos.add(now.x - last.x, now.y - last.y, now.z - last.z);
        return now;
    }

    // --- screens ---

    public static void openScreen(boolean shift, Screen next, Runnable original) {
        boolean overBlock = next != null && isOverBlock(next);
        run(shift, original);
        guiCamera = shift && GuiHandler.GUI_POS_ROOM != null && !overBlock ? roomCamera() : null;
    }

    /** Same test Vivecraft uses to show a container screen over its block or entity. */
    private static boolean isOverBlock(Screen screen) {
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;
        boolean target = hit != null && (hit.getType() == HitResult.Type.BLOCK
            || hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof ContainerEntity);
        return GuiHandler.GUI_APPEAR_OVER_BLOCK_ACTIVE && GuiHandler.DH.vrSettings.guiAppearOverBlock
            && screen instanceof AbstractContainerScreen && target;
    }

    public static void processScreen(boolean shift, Runnable original) {
        if (!shift || GuiHandler.GUI_POS_ROOM == null) {
            guiCamera = null;
        } else if (guiCamera != null) {
            guiCamera = slide(GuiHandler.GUI_POS_ROOM, guiCamera);
        }
        run(shift, original);
    }

    // --- keyboard ---

    public static void openKeyboard(boolean shift, Runnable original) {
        run(shift, original);
        keyboardCamera = shift && KeyboardHandler.SHOWING ? roomCamera() : null;
    }

    public static void processKeyboard(boolean shift, Runnable original) {
        if (!shift || !KeyboardHandler.SHOWING) {
            keyboardCamera = null;
        } else if (keyboardCamera != null) {
            keyboardCamera = slide(KeyboardHandler.POS_ROOM, keyboardCamera);
        }
        run(shift, original);
    }

    // --- radial menu ---

    public static void openRadial(boolean shift, Runnable original) {
        run(shift, original);
        radialCamera = shift && RadialHandler.isShowing() ? roomCamera() : null;
    }

    public static void processRadial(boolean shift, Runnable original) {
        if (!shift || !RadialHandler.isShowing()) {
            radialCamera = null;
        } else if (radialCamera != null) {
            radialCamera = slide(RadialHandler.POS_ROOM, radialCamera);
        }
        run(shift, original);
    }

    /** After a perspective change, put open menus in front of the new view. */
    public static void reorient() {
        if (Minecraft.getInstance().gui.screen() != null) {
            // Vivecraft places the open screen again on the next frame
            GuiHandler.GUI_POS_ROOM = null;
        } else if (KeyboardHandler.SHOWING) {
            KeyboardHandler.orientOverlay(KeyboardHandler.KEYBOARD_FOR_GUI);
        }
        if (RadialHandler.isShowing()) {
            RadialHandler.orientOverlay(RadialHandlerAccessor.thirdvive$getActiveController());
        }
    }
}
