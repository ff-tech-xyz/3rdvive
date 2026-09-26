package com.thirdvive.neoforge;

import com.thirdvive.ThirdVive;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

@Mod(value = ThirdVive.MOD_ID, dist = Dist.CLIENT)
public final class ThirdViveNeoForge {
    public ThirdViveNeoForge() {
        ThirdVive.init(FMLPaths.CONFIGDIR.get());
    }
}
