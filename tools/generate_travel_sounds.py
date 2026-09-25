"""Original, sample-free cues for investigator tokens on the map.

Run with Python's standard library only. When travelling, the token fades out,
the camera pans, then the token fades in (FAST_ACTION_DURATION = 0.5 s each), so
those cues are short, airy and restrained: a dissolving breath on departure and a
gathering breath that settles into a soft felt-on-board touch on arrival.
Each filename owns a stable random seed, so regeneration is deterministic.
"""

import hashlib
import math
from pathlib import Path
import random
import struct
import wave


SAMPLE_RATE = 44100
OUTPUT_DIRECTORY = Path(__file__).resolve().parents[1] / "assets" / "sounds"
TAU = 2.0 * math.pi


def smoothstep(value):
    value = min(1.0, max(0.0, value))
    return value * value * (3.0 - 2.0 * value)


def seeded_random(name):
    seed = int.from_bytes(hashlib.sha256(name.encode("ascii")).digest()[:8], "big")
    return random.Random(seed)


def swept_breath(rng, count, start_hz, end_hz, envelope, q=0.9):
    """White noise through a state-variable band-pass whose centre glides exponentially."""
    low = band = 0.0
    out = [0.0] * count
    for index in range(count):
        position = index / (count - 1)
        centre = start_hz * (end_hz / start_hz) ** position
        f = 2.0 * math.sin(math.pi * min(centre, SAMPLE_RATE / 6.0) / SAMPLE_RATE)
        noise = rng.uniform(-1.0, 1.0)
        high = noise - low - q * band
        band += f * high
        low += f * band
        out[index] = band * envelope(position)
    return out


def shimmer(rng, count, start_hz, end_hz, envelope, partials=3):
    """A few detuned, glass-like partials gliding together; very quiet colour."""
    out = [0.0] * count
    voices = [(1.0 + rng.uniform(-0.004, 0.004) + k * 0.5, rng.uniform(0, TAU), 1.0 / (k + 1.5))
              for k in range(partials)]
    phases = [phase for _, phase, _ in voices]
    for index in range(count):
        position = index / (count - 1)
        base = start_hz * (end_hz / start_hz) ** position
        value = 0.0
        for voice, (ratio, _, level) in enumerate(voices):
            phases[voice] += TAU * base * ratio / SAMPLE_RATE
            value += math.sin(phases[voice]) * level
        out[index] = value * envelope(position)
    return out


def felt_touch(count, start, frequency, level, decay):
    """Soft, damped low body: a token placed on a cloth board, not a click."""
    out = [0.0] * count
    first = round(start * SAMPLE_RATE)
    for index in range(max(0, first), count):
        t = (index - first) / SAMPLE_RATE
        attack = smoothstep(t / 0.006)
        body = math.sin(TAU * frequency * t * (1.0 - 0.12 * min(1.0, t / 0.08)))
        overtone = 0.35 * math.sin(TAU * frequency * 2.71 * t) * math.exp(-t / (decay * 0.35))
        out[index] = level * attack * math.exp(-t / decay) * (body + overtone)
    return out


def depart():
    name = "travel_depart"
    rng = seeded_random(name)
    count = round(0.55 * SAMPLE_RATE)
    # Swell quickly, then dissolve as the token fades away.
    breath_env = lambda p: smoothstep(p / 0.18) * (1.0 - smoothstep((p - 0.25) / 0.75))
    shimmer_env = lambda p: 0.10 * smoothstep(p / 0.10) * (1.0 - smoothstep(p / 0.9))
    layers = (
        swept_breath(rng, count, 2600.0, 480.0, breath_env),
        shimmer(rng, count, 1320.0, 880.0, shimmer_env),
        felt_touch(count, 0.0, 118.0, 0.30, 0.050),
    )
    return name, [sum(values) for values in zip(*layers)]


def arrive():
    name = "travel_arrive"
    rng = seeded_random(name)
    count = round(0.58 * SAMPLE_RATE)
    # Gather in as the token appears, then let a soft touch settle it.
    breath_env = lambda p: smoothstep(p / 0.55) * (1.0 - smoothstep((p - 0.55) / 0.35))
    shimmer_env = lambda p: 0.09 * smoothstep(p / 0.5) * (1.0 - smoothstep((p - 0.5) / 0.45))
    layers = (
        swept_breath(rng, count, 520.0, 2400.0, breath_env),
        shimmer(rng, count, 880.0, 1320.0, shimmer_env),
        felt_touch(count, 0.30, 104.0, 0.42, 0.070),
    )
    return name, [sum(values) for values in zip(*layers)]


def finish(samples, peak):
    count = len(samples)
    edge = round(0.004 * SAMPLE_RATE)
    for index in range(min(edge, count)):
        samples[index] *= smoothstep(index / edge)
        samples[count - 1 - index] *= smoothstep(index / edge)
    samples[-1] = 0.0
    loudest = max(abs(value) for value in samples) or 1.0
    return [value * peak / loudest for value in samples]


def write(name, samples):
    path = OUTPUT_DIRECTORY / (name + ".wav")
    with wave.open(str(path), "wb") as output:
        output.setnchannels(1)
        output.setsampwidth(2)
        output.setframerate(SAMPLE_RATE)
        output.writeframes(b"".join(
            struct.pack("<h", round(max(-1.0, min(1.0, value)) * 32767)) for value in samples))
    print("wrote", path.name, len(samples) / SAMPLE_RATE, "s")


def main():
    OUTPUT_DIRECTORY.mkdir(parents=True, exist_ok=True)
    for build, peak in ((depart, 0.50), (arrive, 0.50)):
        name, samples = build()
        write(name, finish(samples, peak))


if __name__ == "__main__":
    main()
