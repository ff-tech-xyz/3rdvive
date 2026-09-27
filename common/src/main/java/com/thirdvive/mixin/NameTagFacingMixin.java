package com.thirdvive.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.thirdvive.ThirdViveState;
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.client_vr.ClientDataHolderVR;

@Mixin(value = SubmitNodeCollection.class, priority = 1200)
public class NameTagFacingMixin {
    @TargetHandler(mixin = "org.vivecraft.mixin.client_vr.renderer.SubmitNodeCollectionVRMixin",
        name = "vivecraft$cameraOffset")
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE",
        target = "Lorg/vivecraft/api/client/data/RenderPass;isThirdPerson(Lorg/vivecraft/api/client/data/RenderPass;)Z"))
    private boolean thirdvive$faceCamera(boolean thirdPass) {
        return thirdPass || ThirdViveState.activeForPass(ClientDataHolderVR.getInstance().currentPass);
    }
}
