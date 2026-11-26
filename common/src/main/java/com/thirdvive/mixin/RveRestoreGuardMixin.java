package com.thirdvive.mixin;

import com.thirdvive.ThirdVive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes the root of the snap-back.
 *
 * To draw a view, Vivecraft saves the player's position (cacheRVEPos), moves the player to the eye,
 * and puts it back (restoreRVEPos). The save only happens when nothing is saved yet, but the
 * put-back always ran, so a put-back with no fresh save moved the player to an old position.
 *
 * This keeps a copy of Vivecraft's own "saved" flag, using the exact same rules, and skips a
 * put-back when nothing is saved. Every Vivecraft caller either saved first, or runs right after a
 * put-back that already restored the same position, so nothing else changes.
 */
@Mixin(value = GameRenderer.class, priority = 1200)
public class RveRestoreGuardMixin {
    @Unique
    private boolean thirdvive$saved;

    @Dynamic("merged from Vivecraft GameRendererVRMixin")
    @Inject(method = "vivecraft$cacheRVEPos", at = @At("HEAD"))
    private void thirdvive$trackSave(Entity entity, CallbackInfo ci) {
        // Same check Vivecraft uses before it saves.
        if (Minecraft.getInstance().getCameraEntity() != null) this.thirdvive$saved = true;
    }

    @Dynamic("merged from Vivecraft GameRendererVRMixin")
    @Inject(method = "vivecraft$restoreRVEPos", at = @At("HEAD"), cancellable = true)
    private void thirdvive$onlyRestoreWhatWasSaved(Entity entity, CallbackInfo ci) {
        if (entity == null) return; // Vivecraft does nothing here either
        if (!this.thirdvive$saved && ThirdVive.config != null && ThirdVive.config.enabled) {
            ci.cancel(); // nothing fresh saved: putting the player "back" would move it to an old spot
            return;
        }
        this.thirdvive$saved = false;
    }
}
