package com.thirdvive.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thirdvive.ThirdViveState;
import com.thirdvive.ThirdViveUi;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.screenhandlers.RadialHandler;
import org.vivecraft.client_vr.provider.ControllerType;

/** The radial menu opens in front of the camera (or your camera-side hand) and is pointed at from there. */
@Mixin(RadialHandler.class)
public class RadialHandlerMixin {
    @WrapMethod(method = "processGui")
    private static void thirdvive$pointFromCamera(Operation<Void> original) {
        ThirdViveUi.processRadial(ThirdViveState.uiOnCamera(), () -> original.call());
    }

    @WrapMethod(method = "orientOverlay")
    private static void thirdvive$openAtCamera(ControllerType controller, Operation<Void> original) {
        ThirdViveUi.openRadial(ThirdViveState.uiOnCamera(), () -> original.call(controller));
    }

    @WrapOperation(method = "orientOverlay", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
        target = "Lorg/vivecraft/client_vr/VRData;hmd:Lorg/vivecraft/client_vr/VRData$VRDevicePose;"))
    private static VRData.VRDevicePose thirdvive$cameraAsHead(VRData data, Operation<VRData.VRDevicePose> original) {
        return ThirdViveUi.head(data, original.call(data));
    }
}
