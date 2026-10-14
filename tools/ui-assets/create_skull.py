"""Render the small Hall of Fame skull from simple vector shapes (Pillow)."""
from pathlib import Path
from PIL import Image, ImageDraw

scale = 8
mask = Image.new("L", (64 * scale, 64 * scale))
draw = ImageDraw.Draw(mask)

def ellipse(bounds, fill):
    draw.ellipse(tuple(int(v * scale) for v in bounds), fill=fill)

def polygon(points, fill):
    draw.polygon([(int(x * scale), int(y * scale)) for x, y in points], fill=fill)

ellipse((8, 3, 56, 49), 255)
polygon([(15, 30), (49, 30), (46, 49), (41, 52), (41, 60),
         (23, 60), (23, 52), (18, 49)], 255)
ellipse((15, 23, 28, 39), 0)
ellipse((36, 23, 49, 39), 0)
polygon([(32, 37), (27, 46), (37, 46)], 0)
for x in (27, 34):
    polygon([(x, 53), (x + 2, 53), (x + 2, 61), (x, 61)], 0)

image = Image.new("RGBA", mask.size, (232, 217, 176, 255))
image.putalpha(mask)
out = Path(__file__).resolve().parents[2] / "assets/icon/skull.png"
image.resize((64, 64), Image.Resampling.LANCZOS).save(out)
