package com.thirdvive;

import net.minecraft.client.CameraType;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;

/**
 * The perspective is Minecraft's own: 3rdVive doesn't store it. It only stops Vivecraft from resetting it
 * when VR turns on or off (see MinecraftPerspectiveMixin). Minecraft's "Toggle Perspective" key can be put in
 * Vivecraft's radial menu from Vivecraft's radial menu settings, like any other key.
 */
public final class ThirdVivePerspective {
    private ThirdVivePerspective() {}

    /** Every perspective change, from any source: with hudOnCamera, move open VR menus to the new view. */
    public static void onChanged(CameraType type) {
        ThirdViveConfig config = ThirdVive.config;
        if (config == null || !config.hudOnCamera) return;
        if (VRState.VR_RUNNING && ClientDataHolderVR.getInstance().vrPlayer != null) {
            ThirdViveUi.reorient();
        }
    }
}
