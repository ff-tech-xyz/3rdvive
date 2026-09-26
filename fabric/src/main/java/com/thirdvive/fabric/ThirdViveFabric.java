package com.thirdvive.fabric;

import com.thirdvive.ThirdVive;
import net.fabricmc.api.ClientModInitializer;

public final class ThirdViveFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ThirdVive.init();
    }
}
