package com.thirdvive;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Read once at client startup. Invalid config is reported rather than silently discarded. */
public final class ThirdViveConfig {
    private static final Gson GSON = new Gson();
    public boolean enabled = true;
    public double distance = 0.0; // 0 uses Minecraft's camera_distance (including scale and vehicle)
    public boolean firstPersonInMenus = true;
    public boolean debugLog = true; // test builds: movement state once a second, plus every snap-back with its cause
    public Follow follow = Follow.BODY; // BODY: camera trails the character; ROOM: turns only when you snap/smooth turn
    public double followSeconds = 0.3; // how slowly the camera swings round behind the character (0 = instantly)
    public float cameraPitch = 15.0F; // degrees the camera sits above the character, looking down

    public enum Follow { BODY, ROOM }

    public static ThirdViveConfig load(Path configDir) {
        Path path = configDir.resolve("thirdvive.json");
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                ThirdViveConfig defaults = new ThirdViveConfig();
                try (Writer writer = Files.newBufferedWriter(path)) {
                    GSON.toJson(defaults, writer);
                }
                return defaults;
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                ThirdViveConfig config = GSON.fromJson(reader, ThirdViveConfig.class);
                if (config == null || !Double.isFinite(config.distance) || config.distance < 0 || (config.distance > 0 && config.distance < 0.25) || config.distance > 32) {
                    throw new IllegalArgumentException("distance must be 0 (vanilla) or between 0.25 and 32 blocks");
                }
                if (config.follow == null) config.follow = Follow.BODY;
                if (!Double.isFinite(config.followSeconds) || config.followSeconds < 0 || config.followSeconds > 5) {
                    throw new IllegalArgumentException("followSeconds must be between 0 and 5");
                }
                if (!Float.isFinite(config.cameraPitch) || config.cameraPitch < -45 || config.cameraPitch > 75) {
                    throw new IllegalArgumentException("cameraPitch must be between -45 and 75 degrees");
                }
                return config;
            }
        } catch (IOException | JsonSyntaxException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load " + path + "; fix the file before starting 3rdVive", e);
        }
    }
}
