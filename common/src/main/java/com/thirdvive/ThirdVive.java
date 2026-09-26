package com.thirdvive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared client initialization; Vivecraft gameplay hooks will live in common. */
public final class ThirdVive {
    public static final String MOD_ID = "thirdvive";
    public static final Logger LOGGER = LoggerFactory.getLogger("3rdVive");

    public static ThirdViveConfig config;

    private ThirdVive() {}

    public static void init() {
        config = ThirdViveConfig.load();
        LOGGER.info("3rdVive initialized (enabled={}, distance={})", config.enabled, config.distance);
    }
}
