package com.thirdvive.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thirdvive.ThirdVive;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.VRState;

/** Adds a perspective button to the pause menu, since the perspective key is hard to reach in VR. */
@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    // Same anchor Vivecraft uses for its VR buttons: just before the disconnect button is added.
    @Inject(method = "createPauseMenu", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
        target = "Lnet/minecraft/client/gui/screens/PauseScreen;disconnectButton:Lnet/minecraft/client/gui/components/Button;",
        shift = At.Shift.BY, by = -3))
    private void thirdvive$addPerspectiveButton(CallbackInfo ci, @Local GridLayout.RowHelper rowHelper) {
        if (!VRState.VR_RUNNING || ThirdVive.config == null || !ThirdVive.config.enabled) return;
        Minecraft mc = Minecraft.getInstance();
        rowHelper.addChild(Button.builder(thirdvive$label(mc.options.getCameraType()), button -> {
            CameraType next = mc.options.getCameraType().cycle();
            mc.options.setCameraType(next);
            button.setMessage(thirdvive$label(next));
        }).width(98).build());
    }

    @Unique
    private static Component thirdvive$label(CameraType type) {
        String name = switch (type) {
            case FIRST_PERSON -> "First Person";
            case THIRD_PERSON_BACK -> "Third: Back";
            case THIRD_PERSON_FRONT -> "Third: Front";
        };
        return Component.literal("View: " + name);
    }
}
