#!/usr/bin/env python3
"""Generate the 3rdVive icon (simple VR headset with "3rd" on the front).

Matches the PyreHaven Modrinth icon style: flat shapes on a dark navy
square, teal / orange / cream palette.

Usage:  python3 make_icon.py            -> 3rdvive-icon.svg (+ .png if cairosvg is installed)
"""
from pathlib import Path

# PyreHaven palette (sampled from the existing Modrinth icons)
BG = "#141b2b"      # dark navy background
OUTLINE = "#0b0f12" # near-black outline
CREAM = "#f3e7c4"   # headset shell
CREAM_SHADE = "#d9c99c"
TEAL = "#4fd6d2"    # front visor
TEAL_DARK = "#2a9f9c"
ORANGE = "#f5821f"  # strap + text
ORANGE_DARK = "#d84a1b"

SIZE = 512

SVG = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {SIZE} {SIZE}" width="{SIZE}" height="{SIZE}">
  <rect width="{SIZE}" height="{SIZE}" fill="{BG}"/>

  <!-- head strap (behind the headset) -->
  <rect x="36" y="226" width="440" height="60" rx="20" fill="{ORANGE_DARK}" stroke="{OUTLINE}" stroke-width="12"/>

  <!-- headset shell -->
  <rect x="86" y="146" width="340" height="220" rx="56" fill="{CREAM}" stroke="{OUTLINE}" stroke-width="14"/>
  <!-- shell bottom shading -->
  <path d="M100 300 H412 V310 Q412 352 370 352 H142 Q100 352 100 310 Z" fill="{CREAM_SHADE}"/>
  <!-- nose cut-out -->
  <path d="M216 366 Q256 300 296 366 Z" fill="{BG}" stroke="{OUTLINE}" stroke-width="14" stroke-linejoin="round"/>

  <!-- front visor -->
  <rect x="122" y="178" width="268" height="132" rx="30" fill="{TEAL}" stroke="{OUTLINE}" stroke-width="12"/>
  <path d="M134 270 H378 V280 Q378 298 360 298 H152 Q134 298 134 280 Z" fill="{TEAL_DARK}"/>

  <!-- "3rd" label -->
  <text x="256" y="272" text-anchor="middle"
        font-family="DejaVu Sans, Arial Black, Helvetica, sans-serif" font-weight="900" font-size="104"
        fill="{ORANGE}" stroke="{OUTLINE}" stroke-width="10" paint-order="stroke"
        letter-spacing="-2">3rd</text>
</svg>
"""


def main() -> None:
    out = Path(__file__).with_name("3rdvive-icon.svg")
    out.write_text(SVG)
    print(f"wrote {out}")
    try:
        import cairosvg
    except ImportError:
        print("cairosvg not installed; skipping PNG (pip install cairosvg)")
        return
    png = out.with_suffix(".png")
    cairosvg.svg2png(bytestring=SVG.encode(), write_to=str(png), output_width=SIZE, output_height=SIZE)
    print(f"wrote {png}")


if __name__ == "__main__":
    main()
