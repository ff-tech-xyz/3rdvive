package com.thirdvive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.vivecraft.client_vr.gameplay.screenhandlers.RadialHandler;
import org.vivecraft.client_vr.provider.ControllerType;

@Mixin(RadialHandler.class)
public interface RadialHandlerAccessor {
    /** The hand that opened the radial menu, so it can be placed again after a perspective change. */
    @Accessor("ACTIVE_CONTROLLER")
    static ControllerType thirdvive$getActiveController() {
        throw new AssertionError();
    }
}
