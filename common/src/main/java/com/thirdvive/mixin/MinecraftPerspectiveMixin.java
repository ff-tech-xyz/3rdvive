package com.thirdvive.mixin;

import com.thirdvive.ThirdVive;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Vivecraft adds this transition method at priority 1100; our 1000 mixin preserves perspective. */
@Mixin(value = Minecraft.class, priority = 1000)
public class MinecraftPerspectiveMixin {
    @Dynamic("vivecraft$switchVRState is merged from Vivecraft's MinecraftVRMixin")
    @WrapWithCondition(method = "vivecraft$switchVRState(Z)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    private boolean thirdvive$preservePerspective(Options options, CameraType type) {
        return ThirdVive.config == null || !ThirdVive.config.enabled;
    }
}
