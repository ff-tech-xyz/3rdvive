package com.thirdvive.mixin;

import com.thirdvive.ThirdVivePerspective;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Every perspective change goes through here: the key, the radial menu, or other mods. */
@Mixin(Options.class)
public class OptionsCameraMixin {
    @Inject(method = "setCameraType", at = @At("TAIL"))
    private void thirdvive$perspectiveChanged(CameraType type, CallbackInfo ci) {
        ThirdVivePerspective.onChanged(type);
    }
}
