package com.thirdvive.fabric;

import com.thirdvive.ThirdVive;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class ThirdViveFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ThirdVive.init(FabricLoader.getInstance().getConfigDir());
    }
}
