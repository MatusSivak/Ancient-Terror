import argparse
import json
import math
import random
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw, ImageFont, ImageFilter


def lerp(a, b, t):
    return int(a + (b - a) * t)


def interpolate_stops(stops, t):
    if t <= stops[0][0]:
        return stops[0][1]
    if t >= stops[-1][0]:
        return stops[-1][1]
    for i in range(len(stops) - 1):
        t0, c0 = stops[i]
        t1, c1 = stops[i + 1]
        if t0 <= t <= t1:
            local_t = (t - t0) / (t1 - t0)
            return [lerp(c0[j], c1[j], local_t) for j in range(4)]
    return stops[-1][1]


def best_font(draw, text, preset):
    font_cfg = preset["font"]
    size = int(font_cfg["base_size"])
    font = ImageFont.truetype(font_cfg["path"], size)
    bbox = draw.textbbox((0, 0), text, font=font)
    width = bbox[2] - bbox[0]
    target = int(preset["canvas"]["width"] * float(font_cfg["target_width_ratio"]))
    if width > 0:
        size = max(int(font_cfg["min_size"]), int(size * target / width))
    font = ImageFont.truetype(font_cfg["path"], size)
    return font


def text_position(draw, text, font, preset):
    w, h = preset["canvas"]["width"], preset["canvas"]["height"]
    bbox = draw.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = (w - tw) // 2 - bbox[0]
    y = (h - th) // 2 - bbox[1] + int(preset["font"].get("y_offset", 0))
    return x, y


def draw_text_layer(size, text, font, xy, fill=None, stroke_width=0, stroke_fill=None):
    layer = Image.new("RGBA", size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    d.text(
        xy,
        text,
        font=font,
        fill=fill if fill is not None else (0, 0, 0, 0),
        stroke_width=stroke_width,
        stroke_fill=stroke_fill,
    )
    return layer


def text_mask(size, text, font, xy):
    mask = Image.new("L", size, 0)
    md = ImageDraw.Draw(mask)
    md.text(xy, text, font=font, fill=255)
    return mask


def build_metal_texture(size, cfg):
    w, h = size
    seed = int(cfg.get("seed", 1337))
    rnd = random.Random(seed)

    tex = Image.new("L", size, int(cfg.get("base", 200)))
    px = tex.load()
    brush_amp = float(cfg.get("horizontal_brush_strength", 18))
    diag_amp = float(cfg.get("diagonal_brush_strength", 12))
    grain = float(cfg.get("grain", 12))

    # brushed + diagonal anisotropic metallic strokes
    for y in range(h):
        y_t = y / float(max(1, h - 1))
        row_bias = int(8 * math.sin(2 * math.pi * (y_t * 2.2)))
        for x in range(w):
            x_t = x / float(max(1, w - 1))
            horiz = brush_amp * math.sin(2 * math.pi * (x_t * 16.0 + y_t * 0.65))
            diag = diag_amp * math.sin(2 * math.pi * (x_t * 9.2 + y_t * 11.8))
            noise = rnd.uniform(-grain, grain)
            value = int(200 + row_bias + horiz + diag + noise)
            px[x, y] = max(30, min(245, value))

    blur = float(cfg.get("blur_radius", 0.5))
    if blur > 0:
        tex = tex.filter(ImageFilter.GaussianBlur(blur))

    contrast = float(cfg.get("contrast", 1.15))
    if abs(contrast - 1.0) > 0.001:
        mid = Image.new("L", size, 128)
        tex = ImageChops.add(
            ImageChops.multiply(tex, Image.new("L", size, int(128 * contrast))),
            ImageChops.multiply(mid, Image.new("L", size, int(128 * (1.0 - contrast)))),
            scale=128,
        )
    return tex


def gradient_fill_text(size, text, font, xy, stops, effects):
    w, h = size
    mask = text_mask(size, text, font, xy)

    grad = Image.new("RGBA", size, (0, 0, 0, 0))
    gd = ImageDraw.Draw(grad)
    for yy in range(h):
        t = yy / float(max(1, h - 1))
        color = interpolate_stops(stops, t)
        gd.line((0, yy, w, yy), fill=tuple(color), width=1)

    # optional texture modulation for a richer metallic surface
    if "metal_texture" in effects:
        cfg = effects["metal_texture"]
        texture = build_metal_texture(size, cfg)
        texture_rgb = Image.merge("RGB", (texture, texture, texture))
        base_rgb = grad.convert("RGB")
        modulated = ImageChops.multiply(base_rgb, texture_rgb)
        amount = float(cfg.get("amount", 0.5))
        amount = max(0.0, min(1.0, amount))
        mixed = Image.blend(base_rgb, modulated, amount)
        grad = mixed.convert("RGBA")

    # optional shiny streaks to push the 3D/specular feel
    if "specular_bands" in effects:
        band_layer = Image.new("RGBA", size, (0, 0, 0, 0))
        bd = ImageDraw.Draw(band_layer)
        for band in effects["specular_bands"]:
            y_ratio = float(band.get("y_ratio", 0.3))
            thickness = int(band.get("thickness", 8))
            alpha = int(band.get("alpha", 90))
            slant = float(band.get("slant", 0.12))
            yy = int(h * y_ratio)
            x0 = int(-w * 0.1)
            x1 = int(w * 1.1)
            dy = int(h * slant)
            bd.polygon(
                [
                    (x0, yy - thickness),
                    (x1, yy - thickness - dy),
                    (x1, yy + thickness - dy),
                    (x0, yy + thickness),
                ],
                fill=(255, 255, 255, alpha),
            )
        band_blur = float(effects.get("specular_blur", 2.0))
        if band_blur > 0:
            band_layer = band_layer.filter(ImageFilter.GaussianBlur(band_blur))
        grad.alpha_composite(band_layer)

    grad.putalpha(mask)
    return grad


def draw_extrusion(size, text, font, xy, extrusion):
    depth = int(extrusion.get("depth", 0))
    if depth <= 0:
        return Image.new("RGBA", size, (0, 0, 0, 0))
    sx, sy = extrusion.get("step", [1, 1])
    c0 = extrusion["start_rgba"]
    c1 = extrusion["end_rgba"]
    layer = Image.new("RGBA", size, (0, 0, 0, 0))
    for i in range(depth, 0, -1):
        t = i / float(max(1, depth))
        color = tuple(lerp(c0[j], c1[j], 1 - t) for j in range(4))
        offset = (xy[0] + int(i * sx), xy[1] + int(i * sy))
        layer.alpha_composite(draw_text_layer(size, text, font, offset, fill=color))
    blur = float(extrusion.get("blur_radius", 0))
    if blur > 0:
        layer = layer.filter(ImageFilter.GaussianBlur(blur))
    return layer


def generate_label(text, output_path, preset):
    w, h = preset["canvas"]["width"], preset["canvas"]["height"]
    img = Image.new("RGBA", (w, h), tuple(preset["canvas"]["background_rgba"]))
    d = ImageDraw.Draw(img)
    font = best_font(d, text, preset)
    x, y = text_position(d, text, font, preset)
    effects = preset["effects"]
    size = (w, h)

    if "extrusion" in effects:
        img.alpha_composite(draw_extrusion(size, text, font, (x, y), effects["extrusion"]))

    sp = effects["drop_shadow_primary"]
    shadow1 = draw_text_layer(
        size,
        text,
        font,
        (x + int(sp["offset"][0]), y + int(sp["offset"][1])),
        fill=tuple(sp["fill_rgba"]),
    ).filter(ImageFilter.GaussianBlur(float(sp["blur_radius"])))
    img.alpha_composite(shadow1)

    ss = effects["drop_shadow_secondary"]
    shadow2 = draw_text_layer(
        size,
        text,
        font,
        (x + int(ss["offset"][0]), y + int(ss["offset"][1])),
        fill=tuple(ss["fill_rgba"]),
    ).filter(ImageFilter.GaussianBlur(float(ss["blur_radius"])))
    img.alpha_composite(shadow2)

    eo = effects["edge_glow"]
    glow = draw_text_layer(size, text, font, (x, y), fill=tuple(eo["fill_rgba"])).filter(
        ImageFilter.GaussianBlur(float(eo["blur_radius"]))
    )
    img.alpha_composite(glow)

    oo = effects["outline_outer"]
    img.alpha_composite(
        draw_text_layer(
            size,
            text,
            font,
            (x, y),
            stroke_width=int(oo["stroke_width"]),
            stroke_fill=tuple(oo["stroke_rgba"]),
        )
    )

    oi = effects["outline_inner"]
    img.alpha_composite(
        draw_text_layer(
            size,
            text,
            font,
            (x, y),
            stroke_width=int(oi["stroke_width"]),
            stroke_fill=tuple(oi["stroke_rgba"]),
        )
    )

    img.alpha_composite(
        gradient_fill_text(size, text, font, (x, y), effects["metal_gradient"]["stops"], effects)
    )

    tg = effects["top_gloss"]
    gloss = draw_text_layer(
        size,
        text,
        font,
        (x + int(tg["offset"][0]), y + int(tg["offset"][1])),
        fill=tuple(tg["fill_rgba"]),
    ).filter(ImageFilter.GaussianBlur(float(tg["blur_radius"])))
    img.alpha_composite(gloss)

    bis = effects["bottom_inner_shadow"]
    inner_shadow = draw_text_layer(
        size,
        text,
        font,
        (x + int(bis["offset"][0]), y + int(bis["offset"][1])),
        fill=tuple(bis["fill_rgba"]),
    )
    img.alpha_composite(inner_shadow)

    img.save(output_path)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--preset", required=True, help="Path to preset JSON")
    parser.add_argument("--text", required=True, help="Label text")
    parser.add_argument("--output", required=True, help="Output PNG path")
    parser.add_argument("--width", type=int, default=None, help="Optional canvas width override")
    parser.add_argument("--height", type=int, default=None, help="Optional canvas height override")
    args = parser.parse_args()

    preset_path = Path(args.preset)
    output_path = Path(args.output)
    with preset_path.open("r", encoding="utf-8") as f:
        preset = json.load(f)

    if args.width is not None:
        preset["canvas"]["width"] = int(args.width)
    if args.height is not None:
        preset["canvas"]["height"] = int(args.height)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    generate_label(args.text, str(output_path), preset)
    print(f"generated: {output_path}")


if __name__ == "__main__":
    main()
