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
    public double distance = 4.0;
    public boolean firstPersonInMenus = true;

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
                if (config == null || !Double.isFinite(config.distance) || config.distance < 0.25 || config.distance > 32) {
                    throw new IllegalArgumentException("distance must be between 0.25 and 32 blocks");
                }
                return config;
            }
        } catch (IOException | JsonSyntaxException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load " + path + "; fix the file before starting 3rdVive", e);
        }
    }
}
