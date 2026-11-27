package com.thirdvive;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.settings.VRSettings;

/**
 * One perspective everywhere: the same in and out of VR, kept across restarts, and switchable with the
 * normal perspective key or from Vivecraft's radial menu.
 */
public final class ThirdVivePerspective {
    /** Minecraft's own "Toggle Perspective" key; the radial menu presses keys by this name. */
    public static final String KEY_NAME = "key.togglePerspective";
    private static final int MAX_RADIAL_BUTTONS = 14;

    private static boolean restored;
    private static boolean radialChecked;

    private ThirdVivePerspective() {}

    /** Start of every client tick. Each step runs once, as soon as the game is ready for it. */
    public static void tick() {
        ThirdViveConfig config = ThirdVive.config;
        if (config == null || !config.enabled) return;
        Minecraft mc = Minecraft.getInstance();
        if (!restored && mc.options != null) {
            restored = true;
            if (config.rememberPerspective && mc.options.getCameraType() != config.savedPerspective()) {
                mc.options.setCameraType(config.savedPerspective());
            }
        }
        if (!radialChecked && !config.radialButtonAdded) {
            addRadialButton(config);
        }
    }

    /** Every perspective change, from any source: remember it and move open VR menus to the new view. */
    public static void onChanged(CameraType type) {
        ThirdViveConfig config = ThirdVive.config;
        if (config == null || !config.enabled || !restored) return;
        if (config.rememberPerspective && !type.name().equals(config.perspective)) {
            config.perspective = type.name();
            config.save();
        }
        if (VRState.VR_RUNNING && ClientDataHolderVR.getInstance().vrPlayer != null) {
            ThirdViveUi.reorient();
        }
    }

    /**
     * Puts "Toggle Perspective" in the first free radial menu slot, once. If you remove it later it stays removed.
     * Vivecraft's settings may load after 3rdVive, so this waits until they exist.
     */
    private static void addRadialButton(ThirdViveConfig config) {
        VRSettings settings = ClientDataHolderVR.getInstance().vrSettings;
        if (settings == null) return;
        radialChecked = true;
        if (!contains(settings.vrRadialItems) && !contains(settings.vrRadialItemsAlt)) {
            int buttons = settings.vrRadialButtons;
            boolean placed = place(settings.vrRadialItems, buttons);
            // The default circle is full (8 of 8), so grow it by one step (Vivecraft allows 4-14, in twos)
            if (!placed && buttons + 2 <= MAX_RADIAL_BUTTONS && place(settings.vrRadialItems, buttons + 2)) {
                settings.vrRadialButtons = buttons + 2;
                placed = true;
            }
            if (placed || place(settings.vrRadialItemsAlt, buttons)) {
                settings.saveOptions();
                ThirdVive.LOGGER.info("Added Toggle Perspective to the Vivecraft radial menu");
            } else {
                ThirdVive.LOGGER.warn("No free radial menu slot for Toggle Perspective; add it in Vivecraft's radial menu settings");
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
