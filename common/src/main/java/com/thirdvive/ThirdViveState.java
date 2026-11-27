package com.thirdvive;

import net.minecraft.client.Minecraft;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.client_vr.gameplay.screenhandlers.RadialHandler;
import org.vivecraft.client_vr.MethodHolder;

/** When the third-person view is on, and where menus and the HUD go. */
public final class ThirdViveState {
    private ThirdViveState() {}

    /** 3rdVive is enabled, VR is running, you are in a world, and the perspective is third person. */
    public static boolean active() {
        Minecraft mc = Minecraft.getInstance();
        return ThirdVive.config != null && ThirdVive.config.enabled && VRState.VR_RUNNING
            && mc.level != null && mc.player != null
            && !mc.options.getCameraType().isFirstPerson() && !MethodHolder.isInMenuRoom();
    }

    /** {@link #active()} and this is one of the headset's eye views. */
    public static boolean activeForPass(RenderPass pass) {
        return active() && RenderPass.isFirstPerson(pass)
            && ClientDataHolderVR.getInstance().vrPlayer != null;
    }

    /** Front view (the camera looks back at you). */
    public static boolean front() {
        return Minecraft.getInstance().options.getCameraType().isMirrored();
    }

    /**
     * hudOnCamera: the HUD, menus (including chests and your inventory), the keyboard and the radial menu sit in
     * front of the camera. Off: they appear where they would in first person; the view stays in third person.
     */
    public static boolean uiOnCamera() {
        return active() && ThirdVive.config.hudOnCamera;
    }

    /** A menu, keyboard or radial menu is open in front of the camera, so the menu hands are drawn there too. */
    public static boolean uiShowingForPass(RenderPass pass) {
        return activeForPass(pass) && ThirdVive.config.hudOnCamera
            && (Minecraft.getInstance().gui.screen() != null || KeyboardHandler.SHOWING || RadialHandler.isShowing());
    }
}
