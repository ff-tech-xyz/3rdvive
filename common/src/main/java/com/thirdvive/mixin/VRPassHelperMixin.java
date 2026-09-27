package com.thirdvive.mixin;

import com.thirdvive.ThirdViveEyes;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.render.helpers.VRPassHelper;

@Mixin(VRPassHelper.class)
public class VRPassHelperMixin {
    @WrapMethod(method = "renderSingleView")
    private static void thirdvive$markPass(RenderPass eye, DeltaTracker.Timer delta, boolean renderLevel,
                                             Operation<Void> original) {
        ThirdViveEyes.beginPass(eye);
        try { original.call(eye, delta, renderLevel); } finally { ThirdViveEyes.endPass(); }
    }
}
