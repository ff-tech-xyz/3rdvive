package com.thirdvive;

import net.minecraft.client.Minecraft;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
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
}
