package com.thirdvive;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.CameraType;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Read at client startup. Invalid config is reported rather than silently discarded.
 * The game also writes this file (to remember your perspective), so edit it while the game is stopped.
 */
public final class ThirdViveConfig {
    /** Version 2: menus/HUD follow the camera, so the old first-person-in-menus fallback is turned off. */
    static final int VERSION = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private transient Path path;

    public int configVersion = 0; // missing in old files, which marks them for the one-time update
    public boolean enabled = true;
    public double distance = 0.0; // 0 uses Minecraft's camera_distance (including scale and vehicle)
    public boolean firstPersonInMenus = false; // true: snap to first person while a menu or keyboard is open
    public boolean hudOnCamera = true; // true: in third person the HUD floats in front of the camera, not on the wrist
    public boolean rememberPerspective = true; // keep the last perspective across restarts and VR on/off
    public String perspective = CameraType.FIRST_PERSON.name();
    public boolean radialButtonAdded = false; // set once the perspective button was put in the radial menu
    public boolean debugLog = false; // logs movement state once a second in third person

    public static ThirdViveConfig load(Path configDir) {
        Path path = configDir.resolve("thirdvive.json");
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                ThirdViveConfig defaults = new ThirdViveConfig();
                defaults.configVersion = VERSION;
                defaults.path = path;
                defaults.write();
                return defaults;
            }
            ThirdViveConfig config;
            try (Reader reader = Files.newBufferedReader(path)) {
                config = GSON.fromJson(reader, ThirdViveConfig.class);
            }
            if (config == null || !Double.isFinite(config.distance) || config.distance < 0 || (config.distance > 0 && config.distance < 0.25) || config.distance > 32) {
                throw new IllegalArgumentException("distance must be 0 (vanilla) or between 0.25 and 32 blocks");
            }
            if (config.perspective == null || parse(config.perspective) == null) {
                throw new IllegalArgumentException("perspective must be FIRST_PERSON, THIRD_PERSON_BACK or THIRD_PERSON_FRONT");
            }
            config.path = path;
            if (config.configVersion < VERSION) {
                // Old builds wrote firstPersonInMenus=true and debugLog=true as defaults; menus now work in third person.
                config.firstPersonInMenus = false;
                config.debugLog = false;
                config.configVersion = VERSION;
                config.write();
                ThirdVive.LOGGER.info("Updated {} to config version {}", path, VERSION);
            }
            return config;
        } catch (IOException | JsonSyntaxException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot load " + path + "; fix the file before starting 3rdVive", e);
        }
    }

    private static CameraType parse(String name) {
        for (CameraType type : CameraType.values()) {
            if (type.name().equals(name)) return type;
        }
        return null;
    }

    public CameraType savedPerspective() {
        CameraType type = parse(this.perspective);
        return type != null ? type : CameraType.FIRST_PERSON;
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
