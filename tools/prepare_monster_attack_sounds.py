"""Prepare the monster attack cries (one per volley). See assets/sounds/MONSTER_ATTACK_AUDIO.md.

Requires numpy and soundfile; originals in source_assets/sounds/monster_attack make this network-free.
"""
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "source_assets/sounds/monster_attack"
OUTPUT = ROOT / "assets/sounds"
RATE = 44100

# name: (source file, start s, end s, speed)
CUES = {
    "monster_attack_damage": ("monster-snarl-attack.ogg", 0.0, 1.25, 1.0),
    "monster_attack_sanity": ("qubodup-GhostMoan05.wav", 0.0, 1.73, 0.92),
}


def prepare():
    for name, (file, start, end, speed) in CUES.items():
        samples, rate = sf.read(SOURCE / file, always_2d=True)
        samples = samples.mean(axis=1)[round(start * rate):round(end * rate)]
        active = np.flatnonzero(np.abs(samples) > .004)
        samples = samples[max(0, active[0] - round(rate * .01)):active[-1] + 1]
        count = round(len(samples) * RATE / (rate * speed))
        samples = np.interp(np.arange(count) * rate * speed / RATE, np.arange(len(samples)), samples)
        samples -= samples.mean()
        attack, tail = round(.008 * RATE), min(round(.25 * RATE), len(samples) // 3)
        samples[:attack] *= np.linspace(0, 1, attack)
        samples[-tail:] *= np.linspace(1, 0, tail) ** 2
        rms = np.sqrt(np.mean(samples ** 2))
        samples *= min(10 ** (-18 / 20) / max(rms, 1e-9), .9 / max(np.abs(samples)))
        assert len(samples) * 2 < 1_000_000
        sf.write(OUTPUT / f"{name}.wav", samples, RATE, subtype="PCM_16")
        print(f"{name}: {len(samples) / RATE:.2f}s, peak {max(np.abs(samples)):.3f}")


if __name__ == "__main__":
    prepare()
