package com.thirdvive;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.VRData;

/** Per-eye camera relocation. Collision is evaluated from the headset so both eyes share a distance. */
public final class ThirdViveCamera {
    public record Pose(Vec3 position, float yaw, float pitch) {}

    private ThirdViveCamera() {}

    public static Pose compute(Level level, Entity entity, VRData data, RenderPass pass, double distance, boolean front) {
        Vec3 head = data.hmd.getPosition();
        Vec3 forward = new Vec3(data.hmd.getDirection());
        Vec3 up = new Vec3(data.hmd.getCustomVector(new Vector3f(0, 1, 0))).normalize();
        Vec3 eyeOffset = data.getEye(pass).getPosition().subtract(head);
        if (front) {
            eyeOffset = up.scale(2 * eyeOffset.dot(up)).subtract(eyeOffset);
        }
        Vec3 direction = front ? forward : forward.scale(-1);
        double actualDistance = clip(level, entity, head, direction, distance);
        return new Pose(head.add(direction.scale(actualDistance)).add(eyeOffset),
            data.hmd.getYaw() + (front ? 180 : 0), front ? data.hmd.getPitch() : -data.hmd.getPitch());
    }

    static double clip(Level level, Entity entity, Vec3 head, Vec3 direction, double distance) {
        double allowed = distance;
        for (int i = 0; i < 8; i++) {
            Vec3 corner = new Vec3((i & 1) * 2 - 1, ((i >> 1) & 1) * 2 - 1,
                ((i >> 2) & 1) * 2 - 1).scale(0.1);
            Vec3 start = head.add(corner);
            HitResult hit = level.clip(new ClipContext(start, start.add(direction.scale(distance)),
                ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (hit.getType() != HitResult.Type.MISS) {
                allowed = Math.min(allowed, hit.getLocation().distanceTo(start));
            }
        }
        return allowed;
    }
}
