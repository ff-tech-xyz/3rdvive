# 3rdVive

A client-side add-on for Vivecraft that moves the headset view to Minecraft's third-person camera. In VR, the configured perspective key cycles first person, back view and front view; aiming, interactions and network pose still use the real headset and controllers. This is experimental and may be uncomfortable.

**Status:** first test build. Both loader clients reach the main menu with Vivecraft 26.2-1.3.15 under a virtual display. Headset behavior, stereo depth, avatar items, clipping and multiplayer interactions have **not** been tested on VR hardware. This is not a release.

## Requirements

- Minecraft 26.2 and Java 25.
- Fabric Loader 0.19.3 with Fabric API 0.152.1+26.2, **or** NeoForge 26.2.0.1-beta.
- Vivecraft 26.2-1.3.15, installed on the client. The supported range is 26.2-1.3.15 up to (but not including) 26.2-1.4.0.
- No server-side install.

Build with `./gradlew clean build`. The installable jars are `fabric/build/libs/thirdvive-fabric-26.2-0.1.0.jar` and `neoforge/build/libs/thirdvive-neoforge-26.2-0.1.0.jar`; don't install the sources or javadoc jars. Use the jar matching your loader.

Configuration is generated at `config/thirdvive.json` on first launch. `enabled` turns the add-on off without uninstalling it; `distance` defaults to 4 blocks and accepts 0.25–32 (scaled by Vivecraft's world scale); `firstPersonInMenus` defaults to true. Edit while the game is stopped. An invalid file stops loading with an error instead of silently resetting it.

The perspective key no longer cycles Vivecraft's desktop mirror mode while the add-on is enabled. Change the mirror mode in Vivecraft's settings instead.

[Requirements](docs/REQUIREMENTS.md), [build plan and VR test checklist](docs/BUILD_PLAN.md), and the original [developer proposal](docs/reference/developer-spec.txt) are retained. The proposal's `vivecraft$setupVRCamera` hook does not exist in the pinned Vivecraft build; this implementation uses `Camera.update` after Vivecraft aligns the eye. The proposal is not a verified description of the shipped code.

## License

CC0-1.0, consistent with other PyreHaven mods. Vivecraft is a separate required dependency under its own LGPLv3 license; its code is not included here. The upstream MultiLoader-Template is CC0-1.0.
