package com.thirdvive;

import net.minecraft.client.CameraType;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.settings.VRSettings;

/**
 * The perspective is Minecraft's own: 3rdVive doesn't store it. It only stops Vivecraft from resetting it
 * when VR turns on or off (see MinecraftPerspectiveMixin), and offers a "Toggle Perspective" radial menu button.
 */
public final class ThirdVivePerspective {
    /** Minecraft's own "Toggle Perspective" key; the radial menu presses keys by this name. */
    public static final String KEY_NAME = "key.togglePerspective";

    private static boolean radialChecked;

    private ThirdVivePerspective() {}

    /** Start of every client tick. */
    public static void tick() {
        ThirdViveConfig config = ThirdVive.config;
        if (config == null || !config.enabled || radialChecked || config.radialButtonAdded) return;
        addRadialButton(config);
    }

    /** Every perspective change, from any source: with hudOnCamera, move open VR menus to the new view. */
    public static void onChanged(CameraType type) {
        ThirdViveConfig config = ThirdVive.config;
        if (config == null || !config.enabled || !config.hudOnCamera) return;
        if (VRState.VR_RUNNING && ClientDataHolderVR.getInstance().vrPlayer != null) {
            ThirdViveUi.reorient();
        }
    }

    /**
     * First launch only: puts "Toggle Perspective" in an empty radial menu slot if there is one. If the radial
     * menu is full nothing changes; the button can always be picked in Vivecraft's radial menu settings, like any key.
     * Vivecraft's settings may load after 3rdVive, so this waits until they exist.
     */
    private static void addRadialButton(ThirdViveConfig config) {
        VRSettings settings = ClientDataHolderVR.getInstance().vrSettings;
        if (settings == null) return;
        radialChecked = true;
        if (!contains(settings.vrRadialItems) && !contains(settings.vrRadialItemsAlt)) {
            if (place(settings.vrRadialItems, settings.vrRadialButtons)) {
                settings.saveOptions();
                ThirdVive.LOGGER.info("Added Toggle Perspective to the Vivecraft radial menu");
            } else {
                ThirdVive.LOGGER.info("Radial menu is full; Toggle Perspective can be added in Vivecraft's radial menu settings");
            }
        }
        config.radialButtonAdded = true;
        config.save();
    }

    private static boolean contains(String[] items) {
        if (items == null) return false;
        for (String item : items) {
            if (KEY_NAME.equals(item)) return true;
        }
        return false;
    }

    private static boolean place(String[] items, int buttons) {
        if (items == null) return false;
        for (int i = 0; i < Math.min(items.length, buttons); i++) {
            if (items[i] == null || items[i].isEmpty()) {
                items[i] = KEY_NAME;
                return true;
            }
        }
        return false;
    }
}
