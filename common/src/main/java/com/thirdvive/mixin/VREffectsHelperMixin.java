package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.helpers.VREffectsHelper;

@Mixin(VREffectsHelper.class)
public class VREffectsHelperMixin {
    @ModifyReturnValue(method = "isFirstPersonEntityPass", at = @At("RETURN"))
    private static boolean thirdvive$showHead(boolean original) {
        RenderPass pass = ClientDataHolderVR.getInstance().currentPass;
        return original && !ThirdViveState.activeForPass(pass);
    }
}
