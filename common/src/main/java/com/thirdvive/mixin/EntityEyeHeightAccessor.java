package com.thirdvive.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityEyeHeightAccessor {
    @Accessor("eyeHeight")
    float thirdvive$getEyeHeight();

    @Accessor("eyeHeight")
    void thirdvive$setEyeHeight(float eyeHeight);
}
