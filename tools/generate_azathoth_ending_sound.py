"""Synthesize an original midnight clock bell: four deep tolls with a long decay."""
from pathlib import Path
import math
import random
import struct
import wave

RATE = 44100
DURATION = 10.4
TOLLS = (0.0, 1.8, 3.6, 5.4)
# Bell modes: hum, prime, minor third, fifth, nominal and inharmonic upper modes.
# The upper modes decay first, leaving the heavy bronze bell humming in the dark.
MODES = ((65.4, 0.26, 3.2), (130.8, 0.62, 2.8), (155.6, 0.28, 2.1),
         (196.2, 0.16, 1.7), (261.6, 0.72, 2.1), (353.0, 0.24, 1.3),
         (523.2, 0.25, 1.1), (714.0, 0.15, 0.7), (1085.0, 0.10, 0.35))
rng = random.Random(1200)
bell = []
for i in range(round(5 * RATE)):
    t = i / RATE
    attack = 1 - math.exp(-t / 0.002)
    tone = 0.0
    for frequency, amplitude, decay in MODES:
        # Slight mode splitting gives a natural beating bronze resonance.
        phase = 2 * math.pi * frequency * t
        tone += amplitude * math.exp(-t / decay) * (
            0.8 * math.sin(phase) + 0.2 * math.sin(phase + 2 * math.pi * 0.7 * t))
    hammer = rng.uniform(-1, 1) * 0.2 * math.exp(-t / 0.009)
    bell.append((tone + hammer) * attack * min(1, (5 - t) / 0.4))
samples = [0.0] * round(DURATION * RATE)
for strike, start in enumerate(TOLLS):
    offset = round(start * RATE)
    gain = (0.9, 0.94, 0.97, 1.0)[strike]
    for i, sample in enumerate(bell):
        if offset + i < len(samples):
            samples[offset + i] += sample * gain
# A distant clock tower's reflections, not a musical chord or an explosion.
original = samples[:]
for delay, gain in ((0.137, 0.16), (0.293, 0.12), (0.479, 0.08), (0.733, 0.05)):
    offset = round(delay * RATE)
    for i in range(offset, len(samples)):
        samples[i] += original[i - offset] * gain
for i in range(len(samples)):
    samples[i] *= min(1, (len(samples) - 1 - i) / (RATE * 1.1)) ** 2
peak = max(map(abs, samples))
samples = [s * 0.82 / peak for s in samples]
samples[0] = samples[-1] = 0.0
assert all(math.isfinite(s) and abs(s) <= 0.820001 for s in samples)
# Keep decoded PCM below Android SoundPool's per-sound memory limit.
assert len(samples) * 2 < 1000000
path = Path(__file__).resolve().parents[1] / 'assets/sounds/azathoth_ending.wav'
with wave.open(str(path), 'wb') as out:
    out.setparams((1, 2, RATE, len(samples), 'NONE', 'not compressed'))
    out.writeframes(struct.pack(f'<{len(samples)}h', *(round(s * 32767) for s in samples)))
print(f'{path.name}: {len(TOLLS)} bell tolls, {DURATION}s, peak=0.82, {path.stat().st_size} bytes')
