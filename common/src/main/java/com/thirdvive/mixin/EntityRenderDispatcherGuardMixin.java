package com.thirdvive.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.thirdvive.ThirdViveEyes;
import com.thirdvive.ThirdViveState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vivecraft puts the player back at its saved real position while building the player's render
 * state, then moves it to the eye again. In third person the player is also built outside a VR view,
 * when nothing fresh is saved, so it got snapped to an old position every frame and could not move.
 * Skip that step outside a VR view; the matching "move back to the eye" step then skips itself.
 */
@Mixin(value = EntityRenderDispatcher.class, priority = 1500)
public class EntityRenderDispatcherGuardMixin {
    @TargetHandler(mixin = "org.vivecraft.mixin.client_vr.renderer.entity.EntityRenderDispatcherVRMixin",
        name = "vivecraft$storeEntityAndRestorePos")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void thirdvive$onlyInsideVrView(CallbackInfo ci) {
        if (!ThirdViveEyes.inPass() && ThirdViveState.locked()) ci.cancel();
    }
}
