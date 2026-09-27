package com.thirdvive.mixin;

import com.thirdvive.ThirdViveEyes;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GameRenderer.class, priority = 1200)
public class GameRendererRveMixin {
    @Dynamic("merged from Vivecraft GameRendererVRMixin")
    @WrapMethod(method = "vivecraft$setupRVE")
    private void thirdvive$realEyeForEntity(Operation<Void> original) {
        ThirdViveEyes.runBypassed(() -> original.call());
    }
}
