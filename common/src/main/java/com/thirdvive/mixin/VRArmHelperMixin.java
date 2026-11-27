package com.thirdvive.mixin;

import com.thirdvive.ThirdViveState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.helpers.VRArmHelper;

@Mixin(VRArmHelper.class)
public class VRArmHelperMixin {
    /** No floating first-person hands in third person, except the menu hands while a menu is open. */
    @ModifyReturnValue(method = "shouldRenderHands", at = @At("RETURN"))
    private static boolean thirdvive$hideFloatingHands(boolean original) {
        RenderPass pass = ClientDataHolderVR.getInstance().currentPass;
        return original && (!ThirdViveState.activeForPass(pass) || ThirdViveState.uiShowingForPass(pass));
    }
}
