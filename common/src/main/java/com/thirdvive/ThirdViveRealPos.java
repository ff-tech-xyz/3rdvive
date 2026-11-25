package com.thirdvive;

import com.thirdvive.mixin.EntityEyeHeightAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.vivecraft.client_vr.extensions.GameRendererExtension;

/**
 * Keeps the player's real position safe while Vivecraft draws a VR view.
 *
 * To draw a view, Vivecraft saves the player's position, moves the player to the eye, and puts it
 * back afterward. In third person that save/put-back pair gets out of step, so the player kept being
 * put back at an old position (stuck in place, snapping back, no jumping). Here 3rdVive takes its own
 * copy at the start of every view and forces it back at the end, so it can never drift.
 *
 * Vivecraft marks a moved player with an eye height of 0.0001, which is how "already moved" is detected.
 */
public final class ThirdViveRealPos {
    private static final float MOVED_EYE_HEIGHT = 0.001F;

    private static Entity entity;
    private static double x, y, z, xo, yo, zo, xOld, yOld, zOld;
    private static float eyeHeight;

    private ThirdViveRealPos() {}

    private static boolean isMoved(Entity e) {
        return ((EntityEyeHeightAccessor) e).thirdvive$getEyeHeight() < MOVED_EYE_HEIGHT;
    }

    /** Start of a VR view: remember where the player really is. */
    public static void save() {
        Entity e = Minecraft.getInstance().getCameraEntity();
        if (e == null || !ThirdViveState.locked() || isMoved(e)) return;
        entity = e;
        x = e.getX(); y = e.getY(); z = e.getZ();
        xo = e.xo; yo = e.yo; zo = e.zo;
        xOld = e.xOld; yOld = e.yOld; zOld = e.zOld;
        eyeHeight = ((EntityEyeHeightAccessor) e).thirdvive$getEyeHeight();
    }

    /** End of a VR view, and start of each tick: put the player back where it really is. */
    public static void restore() {
        Minecraft mc = Minecraft.getInstance();
        Entity e = mc.getCameraEntity();
        if (e == null || e != entity || !ThirdViveState.locked()) return;
        // Let Vivecraft finish its own put-back first so its "saved" flag is cleared for the next view.
        ((GameRendererExtension) mc.gameRenderer).vivecraft$restoreRVEPos(e);
        e.setPosRaw(x, y, z);
        e.xo = xo; e.yo = yo; e.zo = zo;
        e.xOld = xOld; e.yOld = yOld; e.zOld = zOld;
        ((EntityEyeHeightAccessor) e).thirdvive$setEyeHeight(eyeHeight);
    }

    /** Start of a tick: only fix the player if a view left it moved to the eye. */
    public static void restoreIfMoved() {
        Entity e = Minecraft.getInstance().getCameraEntity();
        if (e != null && isMoved(e)) restore();
    }

    /** Forget the copy when the world changes, so an old position is never applied. */
    public static void clear() { entity = null; }
}
