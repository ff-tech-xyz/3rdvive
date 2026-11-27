package com.thirdvive.mixin;

import com.thirdvive.ThirdVivePerspective;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftStartupMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void thirdvive$perspectiveStartup(CallbackInfo ci) {
        ThirdVivePerspective.tick();
    }
}
