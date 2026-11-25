package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/** Test-build logging: one line per second while third person is active, to diagnose movement. */
public final class ThirdViveDebug {
    private static int ticks;

    private ThirdViveDebug() {}

    public static void tick(VRPlayer vr) {
        if (!ThirdVive.config.debugLog || !ThirdViveState.locked() || ++ticks < 20) return;
        ticks = 0;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        ClientDataHolderVR dh = ClientDataHolderVR.getInstance();
        ThirdVive.LOGGER.info(
            "debug pos={} delta={} zza={} xxa={} jumping={} onGround={} shift={} pose={} freeMove={} teleportOn={} jumpTracker={} sneak={} camEntityIsPlayer={} paused={} screen={} room={}",
            p.position(), p.getDeltaMovement(), p.zza, p.xxa, p.input.keyPresses.jump(), p.onGround(), p.isShiftKeyDown(),
            p.getPose(), vr.getFreeMove(), vr.isTeleportEnabled(), dh.jumpTracker.isjumping(),
            dh.sneakTracker.sneakCounter, mc.getCameraEntity() == p, mc.isPaused(), mc.gui.screen(), vr.roomOrigin);
    }
}
