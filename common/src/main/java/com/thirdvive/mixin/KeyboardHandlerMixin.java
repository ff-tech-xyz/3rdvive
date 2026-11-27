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
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;

/** The VR keyboard (laser or physical) opens in front of the camera and is typed on from there. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @WrapMethod(method = "processGui")
    private static void thirdvive$typeFromCamera(Operation<Void> original) {
        ThirdViveUi.processKeyboard(ThirdViveState.uiOnCamera(), () -> original.call());
    }

    @WrapMethod(method = "orientOverlay")
    private static void thirdvive$openAtCamera(boolean guiRelative, Operation<Void> original) {
        ThirdViveUi.openKeyboard(ThirdViveState.uiOnCamera(), () -> original.call(guiRelative));
    }

    @WrapOperation(method = "orientOverlay", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
        target = "Lorg/vivecraft/client_vr/VRData;hmd:Lorg/vivecraft/client_vr/VRData$VRDevicePose;"))
    private static VRData.VRDevicePose thirdvive$cameraAsHead(VRData data, Operation<VRData.VRDevicePose> original) {
        return ThirdViveUi.head(data, original.call(data));
    }
}
