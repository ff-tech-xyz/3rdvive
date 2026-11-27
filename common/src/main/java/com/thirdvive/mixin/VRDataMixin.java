package com.thirdvive.mixin;

import com.thirdvive.ThirdViveEyes;
import com.thirdvive.ThirdViveUi;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.VRData;

@Mixin(VRData.class)
public class VRDataMixin {
    @ModifyReturnValue(method = "getEye", at = @At("RETURN"))
    private VRData.VRDevicePose thirdvive$renderEye(VRData.VRDevicePose original, RenderPass pass) {
        VRData data = (VRData) (Object) this;
        return ThirdViveEyes.shouldReplace(data, pass) ? ThirdViveEyes.replace(data, pass, original) : original;
    }

    /** Only while Vivecraft places or points at a menu in third person (see ThirdViveUi). */
    @ModifyReturnValue(method = "getController", at = @At("RETURN"))
    private VRData.VRDevicePose thirdvive$menuHand(VRData.VRDevicePose original) {
        return ThirdViveUi.hand((VRData) (Object) this, original);
    }
}
