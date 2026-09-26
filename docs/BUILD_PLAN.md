# Build plan

## Phase 1 — scaffold (this branch)

Bootstrap the official MultiLoader-Template 26.2 layout; rename to 3rdVive; set project and loader metadata, required Vivecraft dependency and CC0-1.0 license; keep common initialization and client-only entrypoints minimal. Verify both loader builds and processed metadata. The initial scaffold does not register mixins that do not exist.

## Phase 2 — third-person camera (implemented, VR validation pending)

Pinned Vivecraft 26.2-1.3.15 is checked by the loader. Preserve CameraType across VR transitions and restore vanilla perspective cycling in VR using fail-closed mixins. Offset each eye along the headset's look axis and clip against walls; rotate the view for front mode. Vivecraft gameplay poses are unchanged. The actual camera hook is `Camera.update` immediately after `alignWithEntity`, not the nonexistent `vivecraft$setupVRCamera` in the original proposal.

## Phase 3 — self rendering and client validation (implementation complete, VR testing pending)

Render the local posed body and held items while hiding floating first-person hands. Both loader builds and flat-screen client starts have been verified with Vivecraft installed; Elijah still needs to validate on VR hardware: F5, VR transitions, back/front stereo and pitch, wall clipping, self rendering, interactions, menus, mirror, multiplayer poses, and shaders. A green build or main-menu launch does not prove those headset behaviors. No release without Elijah's decision.

The full acceptance checklist and suggested hooks are preserved verbatim in `docs/reference/developer-spec.txt`. Treat the original code snippets as hypotheses until compilation and in-game tests prove them.
