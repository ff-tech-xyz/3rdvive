package com.thirdvive;

import net.minecraft.client.Minecraft;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.client_vr.gameplay.screenhandlers.RadialHandler;
import org.vivecraft.client_vr.MethodHolder;

/** Third-person tracking lock and the separate render-pass camera decision. */
public final class ThirdViveState {
    private ThirdViveState() {}

    public static boolean locked() {
        Minecraft mc = Minecraft.getInstance();
        return ThirdVive.config != null && ThirdVive.config.enabled && VRState.VR_RUNNING
            && mc.level != null && mc.player != null
            && !mc.options.getCameraType().isFirstPerson() && !MethodHolder.isInMenuRoom();
    }

    public static boolean active() {
        Minecraft mc = Minecraft.getInstance();
        return locked()
            && (!ThirdVive.config.firstPersonInMenus || (mc.gui.screen() == null && !KeyboardHandler.SHOWING));
    }

    public static boolean activeForPass(RenderPass pass) {
        return active() && RenderPass.isFirstPerson(pass)
            && ClientDataHolderVR.getInstance().vrPlayer != null;
    }

    public static boolean front() {
        return Minecraft.getInstance().options.getCameraType().isMirrored();
    }

    /** Menus and the keyboard open in front of the camera (instead of snapping back to first person). */
    public static boolean uiOnCamera() {
        return locked() && !ThirdVive.config.firstPersonInMenus;
    }

    /** The radial menu never snaps to first person, so it follows the camera whenever the view does. */
    public static boolean radialOnCamera() {
        return active();
    }

    /** The no-menu HUD floats in front of the camera instead of on your wrist. */
    public static boolean hudOnCamera() {
        return active() && ThirdVive.config.hudOnCamera;
    }

    /** A menu, keyboard or radial menu is open and drawn at the camera, so the menu hands go there too. */
    public static boolean uiShowingForPass(RenderPass pass) {
        return activeForPass(pass)
            && (Minecraft.getInstance().gui.screen() != null || KeyboardHandler.SHOWING || RadialHandler.isShowing());
    }
}
