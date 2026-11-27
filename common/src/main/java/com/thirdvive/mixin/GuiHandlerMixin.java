package com.thirdvive.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thirdvive.ThirdViveCamera;
import com.thirdvive.ThirdViveState;
import com.thirdvive.ThirdViveUi;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.client_vr.settings.VRSettings;

/** Screens open in front of the camera, your hands point at them from the camera, and the HUD sits there too. */
@Mixin(GuiHandler.class)
public class GuiHandlerMixin {
    // --- menus (screens) ---

    @WrapMethod(method = "processGui")
    private static void thirdvive$pointFromCamera(Operation<Void> original) {
        ThirdViveUi.processScreen(ThirdViveState.uiOnCamera(), () -> original.call());
    }

    @WrapMethod(method = "onScreenChanged(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/screens/Screen;ZZ)V")
    private static void thirdvive$openAtCamera(Screen previous, Screen next, boolean unpressKeys, boolean infrontOfHand,
                                               Operation<Void> original) {
        ThirdViveUi.openScreen(ThirdViveState.uiOnCamera(), next,
            () -> original.call(previous, next, unpressKeys, infrontOfHand));
    }

    @WrapOperation(method = "onScreenChanged(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/screens/Screen;ZZ)V",
        at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
            target = "Lorg/vivecraft/client_vr/VRData;hmd:Lorg/vivecraft/client_vr/VRData$VRDevicePose;"))
    private static VRData.VRDevicePose thirdvive$cameraAsHead(VRData data, Operation<VRData.VRDevicePose> original) {
        return ThirdViveUi.head(data, original.call(data));
    }

    // --- HUD (no screen open): head-locked style, but locked to the camera ---

    @ModifyExpressionValue(method = "extractGui", at = @At(value = "FIELD",
        target = "Lorg/vivecraft/client_vr/settings/VRSettings;vrHudLockMode:Lorg/vivecraft/client_vr/settings/VRSettings$HUDLock;"))
    private static VRSettings.HUDLock thirdvive$hudOnCamera(VRSettings.HUDLock mode) {
        return ThirdViveState.uiOnCamera() ? VRSettings.HUDLock.HEAD : mode;
    }

    @WrapOperation(method = "extractGui", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
        target = "Lorg/vivecraft/client_vr/VRData;hmd:Lorg/vivecraft/client_vr/VRData$VRDevicePose;"))
    private static VRData.VRDevicePose thirdvive$hudPosition(VRData data, Operation<VRData.VRDevicePose> original) {
        VRData.VRDevicePose real = original.call(data);
        return ThirdViveState.uiOnCamera() ? ThirdViveCamera.camera(data) : real;
    }

    @ModifyExpressionValue(method = "extractGui", at = @At(value = "FIELD",
        target = "Lorg/vivecraft/client_vr/provider/MCVR;hmdRotation:Lorg/joml/Matrix4f;"))
    private static Matrix4f thirdvive$hudTurn(Matrix4f real) {
        if (!ThirdViveState.uiOnCamera()) return real;
        VRData data = ClientDataHolderVR.getInstance().vrPlayer.vrdata_world_render;
        return ThirdViveCamera.roomMatrix(data, ThirdViveCamera.camera(data));
    }
}
