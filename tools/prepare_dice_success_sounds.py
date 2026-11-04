"""Convert downloaded CC0 recordings into the per-skill dice success cues.

Requires numpy and soundfile. Sources and licenses: assets/sounds/DICE_SUCCESS_AUDIO.md.
No synthesis or pitch shifts: silence trim, optional length cap with fade-out, mono 44.1 kHz.
"""
from pathlib import Path

import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "source_assets/sounds/dice_success"
OUTPUT = ROOT / "assets/sounds"
RATE = 44100

# output name: (source recording, maximum seconds or None)
SOURCES = {
    "dice_success_strength": ("gunfire.wav", .70),
    "dice_success_influence": ("coin_drop.wav", None),
    "dice_success_observation": ("photo.ogg", None),
    "dice_success_lore": ("magical_2.ogg", 1.10),
    "dice_success_will": ("pleasing-bell.wav", None),
}


def recording(name):
    data, rate = sf.read(SOURCE / name, always_2d=True)
    data = data.mean(axis=1)
    active = np.flatnonzero(np.abs(data) > .002)
    if not len(active):
        raise ValueError(f"Silent recording: {name}")
    data = data[max(0, active[0] - round(.005 * rate)):min(len(data), active[-1] + round(.03 * rate))]
    if rate != RATE:
        data = np.interp(np.arange(round(len(data) * RATE / rate)) * rate / RATE,
                         np.arange(len(data)), data)
    return data


def prepare(name):
    source, max_seconds = SOURCES[name]
    data = recording(source)
    if max_seconds is not None and len(data) > max_seconds * RATE:
        data = data[:round(max_seconds * RATE)]
        tail = round(.25 * RATE)
        data[-tail:] *= np.linspace(1, 0, tail) ** 2
    edge = min(round(.003 * RATE), len(data) // 3)
    data[:edge] *= np.linspace(0, 1, edge)
    data[-edge:] *= np.linspace(1, 0, edge)
    rms = np.sqrt(np.mean(data ** 2))
    data *= min(10 ** (-20 / 20) / max(rms, 1e-9), .80 / max(np.abs(data)))
    sf.write(OUTPUT / f"{name}.wav", data, RATE, subtype="PCM_16")
    print(f"{name}.wav  {len(data) / RATE:.2f}s  peak {np.abs(data).max():.2f}")


if __name__ == "__main__":
    for cue in SOURCES:
        prepare(cue)
