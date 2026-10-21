"""Synthesize an original, gentle winding-back / rising chime cue (no samples)."""
import math
import struct
import wave
from pathlib import Path

RATE = 44100
DURATION = 1.35
samples = []
for i in range(round(RATE * DURATION)):
    t = i / RATE
    # A short rising mechanical shimmer, followed by two warm bell notes.
    envelope = min(1, t / .025) * max(0, 1 - t / DURATION) ** 2
    winding = math.sin(2 * math.pi * (190 * t + 85 * t * t))
    winding *= .15 * math.exp(-5 * t)
    bells = 0
    for start, frequency in ((.06, 523.25), (.22, 783.99)):
        u = t - start
        if u >= 0:
            attack = min(1, u / .008)
            bells += attack * math.exp(-3.8 * u) * (
                .38 * math.sin(2 * math.pi * frequency * u)
                + .07 * math.sin(2 * math.pi * frequency * 2.01 * u))
    samples.append((winding + bells) * envelope)
peak = max(map(abs, samples))
target = Path(__file__).resolve().parents[1] / 'assets/sounds/doom_retreat.wav'
with wave.open(str(target), 'wb') as output:
    output.setparams((1, 2, RATE, 0, 'NONE', 'not compressed'))
    output.writeframes(b''.join(struct.pack('<h', round(s / peak * .72 * 32767)) for s in samples))
print(f'{target.name}: {DURATION}s, mono PCM16, peak 0.72')
