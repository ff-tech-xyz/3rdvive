# 3rdVive

A client-side add-on for Vivecraft that makes Minecraft's third person work in VR, as close to vanilla as possible: the camera sits where vanilla's would (behind your character's eyes, vanilla distance, pulled in by walls) and turns with your head. Aiming and controllers work like normal Vivecraft. With `lockMovement` (off by default) turned on, walking around your real room doesn't move your character in third person; you move with the stick. In VR, the configured perspective key cycles first person, back view and front view; aiming, interactions and network pose still use the real headset and controllers. This is experimental and may be uncomfortable.

**Status:** experimental headset test build. Both loader jars compile and both revised clients reach the main menu under a virtual display. Headset behavior (stereo depth, avatar items, clipping, multiplayer interactions) has **not** been validated. This is not a release.

## Requirements

- Minecraft 26.3 and Java 25.
- Fabric Loader 0.19.5 or newer, **or** NeoForge 26.3.0.118 (NeoForge jar not yet launch-tested on 26.3).
- Vivecraft 26.3-1.3.18, installed on the client. The supported range is 26.3-1.3.18 up to (but not including) 26.3-1.4.0. Older 26.3 Vivecraft builds are not checked.
- No server-side install.

Build with `./gradlew clean build mergedJar`. Install `build/libs/thirdvive-26.3-0.5.5.jar`; the same jar works on Fabric and NeoForge.

## Switching perspective

Use Minecraft's normal perspective key (F5 by default) in or out of VR, or **Toggle Perspective** in Vivecraft's radial menu. To put it in the radial menu, pick Toggle Perspective in Vivecraft's radial menu settings, like any other key. The perspective is Minecraft's own: 3rdVive only stops Vivecraft from resetting it when VR turns on or off. Like vanilla, it starts in first person after a restart. While the add-on is installed the perspective key no longer cycles Vivecraft's desktop mirror mode; change that in Vivecraft's settings.

## Menus and HUD in third person

The view never snaps back to first person. Where menus go depends on `hudOnCamera`:

- **On (default):** menus (including chests and your inventory), the VR keyboard and the radial menu open in front of the third-person camera. Grey menu hands appear next to the camera, in the same spot relative to the camera that your real hands have relative to your head, and you point and type from there. An open menu slides along with the camera so it stays in reach. Container screens shown over their block (Vivecraft's "appear over block" option) stay over the block. With no menu open, the HUD floats in front of the camera, without changing your Vivecraft HUD setting.
- **Off:** menus, the keyboard, the radial menu and the HUD appear where they would in first person (at your character), and you point at them with your real hands.

## Configuration

`config/thirdvive.json` is created on first launch. Changes apply on the next launch. An invalid file stops loading with an error instead of silently resetting it.

- `lockMovement` (default false): in third person, your head stays pinned to your character, so real walking and leaning don't move you. Off: roomscale walking moves your character like normal Vivecraft.
- `hudOnCamera` (default true): see above.

Settings from older versions (`enabled`, `radialButtonAdded`, `distance`, `firstPersonInMenus`, `lockToCharacter` (now `lockMovement`), `debugLog`, `rememberPerspective`, `perspective`, `configVersion`) are no longer used and are ignored.

[Requirements](docs/REQUIREMENTS.md), [build plan and VR test checklist](docs/BUILD_PLAN.md), and the original [developer proposal](docs/reference/developer-spec.txt) are retained. The current implementation substitutes LEFT/RIGHT/CENTER poses only during the matching Vivecraft render pass and leaves real headset/controller/tick poses alone; the original camera relocation is removed. The proposal describes an earlier design, not the current build.

## License

CC0-1.0, consistent with other PyreHaven mods. Vivecraft is a separate required dependency under its own LGPLv3 license; its code is not included here. The upstream MultiLoader-Template is CC0-1.0.
