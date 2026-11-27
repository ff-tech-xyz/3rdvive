package com.thirdvive.mixin;

import com.thirdvive.ThirdViveDebug;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Debug only: remembers who moved the local player, so a snap-back can name its cause. */
@Mixin(Entity.class)
public class SnapDetectorMixin {
    @Inject(method = "setPosRaw(DDD)V", at = @At("HEAD"))
    private void thirdvive$recordMove(double x, double y, double z, CallbackInfo ci) {
        ThirdViveDebug.recordMove((Entity) (Object) this, x, y, z);
    }
}
