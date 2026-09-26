# 3rdVive requirements

Client-only. In VR, the configured perspective key must cycle vanilla CameraType first/back/front. Preserve that selection through VR on/off, put the stereo camera about four scaled blocks along the headset's look axis, and pull it in at walls. The back view follows physical head turns and pitch; the front view turns toward the player without reversing stereo. Render the local full-body Vivecraft pose and held items, not floating first-person hands. Keep aiming, interactions, teleport, menus, roomscale behavior, and network pose attached to real headset and controller data. Flat-screen behavior is unchanged.

The motivation is experiencing true third-person VR, even if it is uncomfortable. Motion sickness is not a design blocker; a configurable distance and a first-person menu fallback are still useful controls. There is no server component. Elijah tests on his own machine using files placed in `/colab` when a playable build exists.

The companion reference specs are proposals, **not validated code**. In particular, their Vivecraft mixin targets, MixinSquared syntax, matrix order, version ranges, and stereo math must be checked against the actual merged classes and in-game behavior. Do not advertise this scaffold as implementing the camera.
