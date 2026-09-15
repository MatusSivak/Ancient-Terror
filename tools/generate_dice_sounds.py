"""Generate dry, solid dice contacts without pitched or metallic resonance.

Original sample-free foley. Mono 44.1 kHz, 16-bit PCM. Use --preview to also
render one-die and ten-dice examples with the game's bounce timing and gains.
"""
from pathlib import Path
import math
import random
import struct
import sys
import wave

SAMPLE_RATE = 44100
ROOT = Path(__file__).resolve().parents[1]


def contact(index):
    rng = random.Random(6100 + index)
    length = round(0.065 * SAMPLE_RATE)
    samples = []
    fast = middle = slow = 0.0
    # Broad filtered noise makes a short tactile tick instead of a ringing tone.
    rates = [1 - math.exp(-2 * math.pi * f / SAMPLE_RATE)
             for f in (4600 + index * 260, 1250 + index * 90, 320)]
    for i in range(length):
        t = i / SAMPLE_RATE
        noise = rng.uniform(-1, 1)
        fast += rates[0] * (noise - fast)
        middle += rates[1] * (noise - middle)
        slow += rates[2] * (noise - slow)
        attack = min(1, t / 0.0006)
        tick = (fast - middle) * math.exp(-t / (0.0028 + index * 0.00025))
        body = (middle - slow) * 0.65 * math.exp(-t / 0.0045)
        friction = (fast - middle) * 0.045 * math.exp(-t / 0.010)
        samples.append((tick + body + friction) * attack)
    # Remove DC, fade to exact silence and retain headroom for overlapping dice.
    mean = sum(samples) / len(samples)
    samples = [(s - mean) * min(1, i / 12) * min(1, (length - 1 - i) / 440)
               for i, s in enumerate(samples)]
    gain = 0.5 / max(map(abs, samples))
    return [s * gain for s in samples]


def save(path, samples, channels=1):
    assert all(math.isfinite(s) and abs(s) < 1 for s in samples)
    path.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(path), 'wb') as wav:
        wav.setparams((channels, 2, SAMPLE_RATE, len(samples) // channels, 'NONE', 'not compressed'))
        wav.writeframes(struct.pack(f'<{len(samples)}h', *(round(s * 32767) for s in samples)))


def preview(contacts, count):
    rng = random.Random(120 + count)
    left = [0.0] * (SAMPLE_RATE * 2)
    right = left.copy()
    for _ in range(count):
        duration = rng.uniform(1.1, 1.45)
        pitch = rng.uniform(0.90, 1.12)
        variant = rng.randrange(3)
        pan = rng.uniform(-0.6, 0.6) if count > 1 else 0
        for hit, (progress, strength) in enumerate(((0.52, 1), (0.76, 0.48), (0.90, 0.22))):
            source = contacts[(variant + hit) % 3]
            speed = pitch + hit * 0.025
            start = round(duration * progress * SAMPLE_RATE)
            volume = 0.5 / math.sqrt(count) * strength
            for frame in range(int((len(source) - 1) / speed)):
                position = frame * speed
                i = int(position)
                sample = (source[i] + (source[i+1] - source[i]) * (position-i)) * volume
                left[start+frame] += sample * min(1, 1-pan)
                right[start+frame] += sample * min(1, 1+pan)
    save(ROOT / 'output' / 'dice' / f'roll-{count}-dice.wav',
         [s for pair in zip(left, right) for s in pair], 2)


def main():
    contacts = [contact(i) for i in range(3)]
    for i, samples in enumerate(contacts, 1):
        save(ROOT / 'assets' / 'sounds' / f'dice_impact_{i}.wav', samples)
        energy = sum(s*s for s in samples)
        tail = sum(s*s for s in samples[round(0.025*SAMPLE_RATE):]) / energy
        assert tail < 0.001, 'Impact has an excessive ringing tail'
        assert samples[0] == samples[-1] == 0
        print(f'dice_impact_{i}: 65 ms, peak {max(map(abs, samples)):.2f}, energy after 25 ms {tail:.5%}')
    if '--preview' in sys.argv:
        for count in (1, 10):
            preview(contacts, count)


if __name__ == '__main__':
    main()
