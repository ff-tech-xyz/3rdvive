package com.thirdvive;

import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;

import java.util.EnumMap;

/** Substitute only the rendered eye; tracked devices and tick-time poses stay real. */
public final class ThirdViveEyes {
    private static RenderPass renderingPass;
    private static int bypassDepth;
    private static long frame = -1;
    private static VRData frameData;
    private static boolean frameFront;
    private static final EnumMap<RenderPass, VRData.VRDevicePose> CACHE = new EnumMap<>(RenderPass.class);

    private ThirdViveEyes() {}

    public static void beginPass(RenderPass pass) { renderingPass = pass; }
    public static void endPass() { renderingPass = null; }

    /** True only while Vivecraft is drawing one VR view (between its position save and restore). */
    public static boolean inPass() { return renderingPass != null; }

    public static void runBypassed(Runnable action) {
        bypassDepth++;
        try { action.run(); } finally { bypassDepth--; }
    }

    public static boolean shouldReplace(VRData data, RenderPass pass) {
        ClientDataHolderVR holder = ClientDataHolderVR.getInstance();
        return bypassDepth == 0 && renderingPass == pass
            && RenderPass.isFirstPerson(pass)
            && holder.vrPlayer != null && data == holder.vrPlayer.vrdata_world_render
            && ThirdViveState.activeForPass(pass);
    }

    /** Each eye keeps its real offset from the headset, moved along with the camera. Cached per frame. */
    public static VRData.VRDevicePose replace(VRData data, RenderPass pass, VRData.VRDevicePose realEye) {
        long currentFrame = ClientDataHolderVR.getInstance().frameIndex;
        boolean front = ThirdViveState.front();
        if (currentFrame != frame || data != frameData || front != frameFront) {
            frame = currentFrame;
            frameData = data;
            frameFront = front;
            CACHE.clear();
        }
        return CACHE.computeIfAbsent(pass, ignored -> ThirdViveCamera.shift(data, realEye));
    }
}
