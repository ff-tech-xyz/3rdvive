package com.thirdvive.neoforge;

import com.thirdvive.ThirdVive;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = ThirdVive.MOD_ID, dist = Dist.CLIENT)
public final class ThirdViveNeoForge {
    public ThirdViveNeoForge() {
        ThirdVive.init();
    }
}
