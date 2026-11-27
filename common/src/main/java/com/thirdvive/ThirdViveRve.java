package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.vivecraft.client_vr.extensions.GameRendererExtension;

/**
 * Tracks Vivecraft's "player saved and moved to the eye" state (the RVE trick).
 *
 * Vivecraft moves the player to the eye while it draws a view, then puts it back. Other mods (seen:
 * Physics Mod Pro) also call this outside a view. If a save is left open, the next tick runs with
 * the player at eye height and a later put-back drags it to an old spot: the character jumps up and
 * down and loses movement. closeOpenSave() puts the player back before that can happen.
 */
public final class ThirdViveRve {
    /** Mirrors Vivecraft's private "cached" flag, updated by RveRestoreGuardMixin. */
    public static boolean saved;

    private ThirdViveRve() {}

    /** Called at the start of each tick, each frame and each view: nothing should be saved then. */
    public static void closeOpenSave() {
        if (!saved) return;
        Minecraft mc = Minecraft.getInstance();
        Entity e = mc.getCameraEntity();
        if (e != null) ((GameRendererExtension) mc.gameRenderer).vivecraft$restoreRVEPos(e);
    }
}
