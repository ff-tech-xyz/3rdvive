package com.thirdvive;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Read at client startup. Invalid config is reported rather than silently discarded.
 * The game also writes this file (once, after adding the radial menu button), so edit it while the game is stopped.
 */
public final class ThirdViveConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private transient Path path;

    public boolean enabled = true;
    public boolean lockMovement = true; // true: in third person real walking/leaning don't move you (stick only); false: normal Vivecraft roomscale
    public boolean hudOnCamera = true; // true: HUD, menus, keyboard and radial menu sit in front of the camera; false: where they would be in first person
    public boolean radialButtonAdded = false; // set once the perspective button was put in the radial menu

    public static ThirdViveConfig load(Path configDir) {
        Path path = configDir.resolve("thirdvive.json");
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                ThirdViveConfig defaults = new ThirdViveConfig();
                defaults.path = path;
                defaults.write();
                return defaults;
            }
            ThirdViveConfig config;
            try (Reader reader = Files.newBufferedReader(path)) {
                config = GSON.fromJson(reader, ThirdViveConfig.class);
            }
            if (config == null) throw new IllegalArgumentException("file is empty");
            config.path = path;
            return config;
        } catch (IOException | JsonSyntaxException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load " + path + "; fix the file before starting 3rdVive", e);
        }
    }

    /** Save while the game runs. A failed save is logged; it never crashes the game. */
    public void save() {
        try {
            write();
        } catch (IOException e) {
            ThirdVive.LOGGER.warn("Could not save {}", this.path, e);
        }
    }

    private void write() throws IOException {
        Path temp = this.path.resolveSibling(this.path.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp)) {
            GSON.toJson(this, writer);
        }
        Files.move(temp, this.path, StandardCopyOption.REPLACE_EXISTING);
    }
}
