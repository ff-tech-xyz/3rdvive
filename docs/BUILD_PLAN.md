# Build plan

## Phase 1 — scaffold (this branch)

Bootstrap the official MultiLoader-Template 26.2 layout; rename to 3rdVive; set project and loader metadata, required Vivecraft dependency and CC0-1.0 license; keep common initialization and client-only entrypoints minimal. Verify both loader builds and processed metadata. The initial scaffold does not register mixins that do not exist.

## Phase 2 — third-person camera

Inspect Vivecraft 26.2-1.3.15 bytecode and merged mixins. Add common state/config and hooks for F5 cycling and camera-type preservation. Implement per-eye offset and vanilla-like clipping, then front-view orientation with correct stereo. Keep Vivecraft gameplay poses untouched. Check missing optional hooks explicitly at startup rather than treating `require = 0` as a complete warning mechanism.

## Phase 3 — self rendering and client validation

Show local posed head, body, controller arms, and held items; suppress floating VR hands in third person. Build both loaders. Validate the supplied checklist on Elijah's VR client: F5, VR transitions, back/front stereo and pitch, wall clipping, self rendering, interactions, menus, mirror, multiplayer poses, and shaders. Place test jars in `/colab` only when testable. Elijah decides any release; do not publish this scaffold as one.

The full acceptance checklist and suggested hooks are preserved verbatim in `docs/reference/developer-spec.txt`. Treat the original code snippets as hypotheses until compilation and in-game tests prove them.
