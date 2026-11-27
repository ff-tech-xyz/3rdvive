package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.VRPlayer;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

/**
 * Test-build logging (config debugLog).
 * - Once a second in third person: movement state.
 * - Every frame: if the drawn player position jumps more than SNAP_BLOCKS, logs "SNAP" with the
 *   last few position changes and the code that made each one.
 * Moves made while a VR view is being drawn are skipped: Vivecraft moves the player to the eye
 * and back during every view on purpose.
 */
public final class ThirdViveDebug {
    private static final double SNAP_BLOCKS = 0.6;
    private static final int HISTORY = 8;
    private static final StackWalker WALKER = StackWalker.getInstance();

    private record Move(long frame, Vec3 from, Vec3 to, String by) {}

    private static final ArrayDeque<Move> MOVES = new ArrayDeque<>();
    private static int ticks;
    private static Vec3 lastDrawn;

    private ThirdViveDebug() {}

    private static boolean on() {
        return ThirdVive.config != null && ThirdVive.config.debugLog && ThirdViveState.locked();
    }

    public static void recordMove(Entity entity, double x, double y, double z) {
        if (!on() || ThirdViveEyes.inPass() || entity != Minecraft.getInstance().player) return;
        Vec3 from = entity.position();
        if (from.distanceToSqr(x, y, z) < 0.01 * 0.01) return;
        String by = WALKER.walk(s -> s.skip(2).limit(6)
            .map(f -> f.getClassName().replaceAll(".*\\.", "") + "." + f.getMethodName() + ":" + f.getLineNumber())
            .collect(Collectors.joining(" <- ")));
        if (MOVES.size() == HISTORY) MOVES.removeFirst();
        MOVES.addLast(new Move(ClientDataHolderVR.getInstance().frameIndex, from, new Vec3(x, y, z), by));
    }

    public static void frame(float partialTick) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (!on() || p == null) {
            lastDrawn = null;
            return;
        }
        Vec3 drawn = p.getPosition(partialTick);
        if (lastDrawn != null && drawn.distanceTo(lastDrawn) > SNAP_BLOCKS) {
            ThirdVive.LOGGER.warn("SNAP frame={} {} -> {} ({} blocks). Recent moves, oldest first:",
                ClientDataHolderVR.getInstance().frameIndex, lastDrawn, drawn,
                String.format("%.2f", drawn.distanceTo(lastDrawn)));
            for (Move m : MOVES) {
                ThirdVive.LOGGER.warn("  frame={} {} -> {} by {}", m.frame(), m.from(), m.to(), m.by());
            }
        }
        lastDrawn = drawn;
    }

    public static void tick(VRPlayer vr) {
        if (!on() || ++ticks < 20) return;
        ticks = 0;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        ClientDataHolderVR dh = ClientDataHolderVR.getInstance();
        ThirdVive.LOGGER.info(
            "debug pos={} delta={} zza={} xxa={} onGround={} pose={} freeMove={} jumpTracker={} sneak={} room={}",
            p.position(), p.getDeltaMovement(), p.zza, p.xxa, p.onGround(), p.getPose(), vr.getFreeMove(),
            dh.jumpTracker.isjumping(), dh.sneakTracker.sneakCounter, vr.roomOrigin);
    }
}
