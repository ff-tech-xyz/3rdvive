package com.thirdvive.mixin;

import com.thirdvive.ThirdVive;
import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Target Vivecraft's wrapper, not vanilla's key handling. */
@Mixin(value = Minecraft.class, priority = 1200)
public class MinecraftToggleMixin {
    @TargetHandler(mixin = "org.vivecraft.mixin.client_vr.MinecraftVRMixin", name = "vivecraft$changeVrMirror")
    @ModifyExpressionValue(method = "@MixinSquared:Handler",
        at = @At(value = "FIELD", target = "Lorg/vivecraft/client_vr/VRState;VR_RUNNING:Z"))
    private boolean thirdvive$usePerspectiveKey(boolean vrRunning) {
        return vrRunning && (ThirdVive.config == null || !ThirdVive.config.enabled);
    }
}
