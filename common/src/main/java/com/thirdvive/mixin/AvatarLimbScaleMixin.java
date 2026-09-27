package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.client_vr.ClientDataHolderVR;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

@Mixin(value = AvatarRenderer.class, priority = 1200)
public class AvatarLimbScaleMixin {
    @Dynamic("merged from Vivecraft AvatarRendererMixin")
    @ModifyExpressionValue(method = "vivecraft$extractVRModelData", at = @At(value = "INVOKE",
        target = "Lorg/vivecraft/api/client/data/RenderPass;isFirstPerson(Lorg/vivecraft/api/client/data/RenderPass;)Z"))
    private boolean thirdvive$fullSizeLimbs(boolean firstPersonPass) {
        return firstPersonPass && !ThirdViveState.activeForPass(ClientDataHolderVR.getInstance().currentPass);
    }
}
