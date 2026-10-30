"""Prepare downloaded walking and phase cues. See assets/sounds/RELEASE_AUDIO.md.

Requires numpy and soundfile; retained originals make regeneration network-free.
"""
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "source_assets/sounds/release"
OUTPUT = ROOT / "assets/sounds"
RATE = 44100
NAMES = {"travel_walk", "travel_depart", "travel_arrive", "action_phase", "encounter_phase", "mythos_phase",
         "card_disable", "monster_sheet_tear"}


def clip(name, speed=1.0):
    samples, rate = sf.read(SOURCE / name, always_2d=True)
    samples = samples.mean(axis=1)
    active = np.flatnonzero(np.abs(samples) > .003)
    if not len(active):
        raise ValueError(f"Silent recording: {name}")
    samples = samples[max(0, active[0] - round(rate * .012)):active[-1] + 1]
    count = round(len(samples) * RATE / (rate * speed))
    return np.interp(np.arange(count) * rate * speed / RATE, np.arange(len(samples)), samples)


def prepare():
    # Crunch and scuff on loose ground; alternating timbres avoid repeated wooden knocks.
    left = clip("gravel.ogg", 1.08)[:round(.40 * RATE)]
    right = clip("leaves01.ogg", .96)[:round(.40 * RATE)]
    steps = np.zeros(round(.68 * RATE) + max(len(left), len(right)))
    for offset, step, gain in [(0, left, .9), (.34, right, 1), (.68, left, .8)]:
        step = step.copy()
        fade = min(round(.045 * RATE), len(step) // 3)
        step[-fade:] *= np.linspace(1, 0, fade)
        start = round(offset * RATE)
        steps[start:start + len(step)] += step * gain
    phase = clip("gong_01.ogg", 1.08)
    cues = {
        "travel_walk": steps,
        "travel_depart": steps,
        "travel_arrive": right,
        "action_phase": phase,
        "encounter_phase": phase,
        "mythos_phase": phase,
        "card_disable": clip("paper_sound_-_1.mp3", .92),
        "monster_sheet_tear": clip("paper_ripped_-_1.mp3", .90),
    }
    for name, samples in cues.items():
        samples = samples.copy() - samples.mean()
        attack = min(round(.006 * RATE), len(samples))
        tail = min(round((.06 if name.startswith("travel") else .22) * RATE), len(samples) // 3)
        samples[:attack] *= np.linspace(0, 1, attack)
        samples[-tail:] *= np.linspace(1, 0, tail) ** 2
        target = -21 if name.startswith("travel") else -19
        rms = np.sqrt(np.mean(samples ** 2))
        samples *= min(10 ** (target / 20) / max(rms, 1e-9), .88 / max(np.abs(samples)))
        samples[0] = samples[-1] = 0
        assert len(samples) * 2 < 1_000_000
        sf.write(OUTPUT / f"{name}.wav", samples, RATE, subtype="PCM_16")
        print(f"{name}: {len(samples) / RATE:.2f}s, peak {max(np.abs(samples)):.3f}")


if __name__ == "__main__":
    prepare()
