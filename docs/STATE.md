# Project state

2026-09-26: First test implementation on `build-v1.0` after the scaffold. Eight incremental commits added loader-shared config, perspective preservation, headset-relative camera positions with wall clipping, front-view rotation, and local-avatar rendering. Fabric and NeoForge both compile and reach the main menu with Vivecraft 26.2-1.3.15 under a virtual display. The Fabric mixin export confirms the two VR-transition camera-type invocations are intercepted; headset rendering and the full VR checklist remain unverified. An initial Fabric launch failed on mixin priority and an initial NeoForge launch failed because config read `Minecraft.getInstance()` before construction; both faults were corrected and each loader subsequently reached the menu. Do not describe this as a release or as hardware-tested.

The branch is pushed only when a testable build is ready for Elijah. `main` remains untouched; Elijah decides merging and releases.
