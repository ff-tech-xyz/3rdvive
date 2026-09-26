package com.thirdvive;

import net.minecraft.client.Minecraft;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.client_vr.MethodHolder;

/** Render-only perspective decision; never substitutes the headset pose used for gameplay. */
public final class ThirdViveState {
    private ThirdViveState() {}

    public static boolean active() {
        Minecraft mc = Minecraft.getInstance();
        return ThirdVive.config != null && ThirdVive.config.enabled && VRState.VR_RUNNING
            && !mc.options.getCameraType().isFirstPerson() && mc.level != null
            && !MethodHolder.isInMenuRoom()
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
