package com.thirdvive.mixin;

import com.thirdvive.ThirdViveEyes;
import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GameRenderer.class, priority = 1200)
public class GameRendererRveMixin {
    /**
     * Inside a VR view: move the player to the real eye, not the third-person one.
     * Outside a VR view in third person: don't move the player at all. Nothing is being drawn
     * from the eye there, and other mods (Physics Mod Pro) calling it left the player stuck at eye
     * height for the next tick. Aiming uses setupRVEAtDevice and is not affected.
     */
    @Dynamic("merged from Vivecraft GameRendererVRMixin")
    @WrapMethod(method = "vivecraft$setupRVE")
    private void thirdvive$realEyeForEntity(Operation<Void> original) {
        if (!ThirdViveEyes.inPass() && ThirdViveState.locked()) return;
        ThirdViveEyes.runBypassed(() -> original.call());
    }
}
