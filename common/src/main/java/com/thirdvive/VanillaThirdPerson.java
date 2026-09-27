package com.thirdvive;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Minecraft 26.2 Camera.alignWithEntity distance and getMaxZoom, using a VR pivot and direction. */
public final class VanillaThirdPerson {
    private VanillaThirdPerson() {}

    public static float distance(Entity entity, double override) {
        if (override > 0) return (float) override;
        float distance = 4.0F;
        float scale = 1.0F;
        if (entity instanceof LivingEntity living) {
            scale = living.getScale();
            distance = (float) living.getAttributeValue(Attributes.CAMERA_DISTANCE);
        }
        float vehicleScale = scale;
        float vehicleDistance = distance;
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity vehicle) {
            vehicleScale = vehicle.getScale();
            vehicleDistance = (float) vehicle.getAttributeValue(Attributes.CAMERA_DISTANCE);
        }
        return Math.max(scale * distance, vehicleScale * vehicleDistance);
    }

    public static float maxZoom(Level level, Entity entity, Vec3 from, Vec3 dir, float distance) {
        for (int i = 0; i < 8; i++) {
            float x = (float) ((i & 1) * 2 - 1);
            float y = (float) (((i >> 1) & 1) * 2 - 1);
            float z = (float) (((i >> 2) & 1) * 2 - 1);
            Vec3 start = from.add((double) (x * 0.1F), (double) (y * 0.1F), (double) (z * 0.1F));
            Vec3 end = start.add(dir.scale(distance));
            HitResult hit = level.clip(new ClipContext(start, end,
                ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (hit.getType() != HitResult.Type.MISS) {
                float squared = (float) hit.getLocation().distanceToSqr(from);
                if (squared < Mth.square(distance)) distance = Mth.sqrt(squared);
            }
        }
        return distance;
    }
}
