# 3rdVive

A PyreHaven client-side add-on for Vivecraft that aims to put Minecraft's normal third-person camera inside the headset. F5 will cycle first person, back view, and front view while VR gameplay continues to use the real headset and controllers. This is an experimental mod for fun, not a comfort-focused camera mode.

**Status:** scaffold only. The camera and rendering mixins are **not implemented**. The jars are not ready for VR testing or release. No server mod or test-server profile is needed.

## Targets

- Minecraft 26.2 / Java 25; Fabric Loader 0.19.3 and Fabric API 0.152.1+26.2, or NeoForge 26.2.0.1-beta.
- Vivecraft 26.2-1.3.15 is a required client dependency. The code lives in `common/`; `fabric/` and `neoforge/` provide loader entrypoints and metadata.
- Build: `./gradlew :fabric:build :neoforge:build`. Output jars appear under the respective module `build/libs/` directories. No artifact should be installed as a playable mod until the hooks are implemented and tested.

See [requirements](docs/REQUIREMENTS.md), [implementation plan](docs/BUILD_PLAN.md), and the supplied [scaffold](docs/reference/scaffold.txt) and [developer spec](docs/reference/developer-spec.txt). The reference documents contain proposed code that has **not** been compiled or validated.

When a testable build exists, copy the jar into `/colab` for Elijah to retrieve and test on his client; don't set up a test server.

## License

CC0-1.0, consistent with other PyreHaven mods. Vivecraft is a separate required dependency under its own LGPLv3 license; its code is not included in this repository. The upstream MultiLoader-Template is CC0-1.0.
