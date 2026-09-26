package com.thirdvive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/** Shared client initialization and config ownership. */
public final class ThirdVive {
    public static final String MOD_ID = "thirdvive";
    public static final Logger LOGGER = LoggerFactory.getLogger("3rdVive");

    public static ThirdViveConfig config;

    private ThirdVive() {}

    public static void init(Path configDir) {
        config = ThirdViveConfig.load(configDir);
        LOGGER.info("3rdVive initialized (enabled={}, distance={})", config.enabled, config.distance);
    }
}
