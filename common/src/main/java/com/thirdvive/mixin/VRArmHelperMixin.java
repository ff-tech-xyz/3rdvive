package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.helpers.VRArmHelper;

@Mixin(VRArmHelper.class)
public class VRArmHelperMixin {
    @ModifyReturnValue(method = "shouldRenderHands", at = @At("RETURN"))
    private static boolean thirdvive$hideFloatingHands(boolean original) {
        return original && !ThirdViveState.activeForPass(ClientDataHolderVR.getInstance().currentPass);
    }
}
