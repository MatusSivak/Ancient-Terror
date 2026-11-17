"""Build the rumor arrival cue from the retained CC0 download (see RUMOR_AUDIO.md).

Requires numpy and soundfile. No network calls; output is mono PCM16, 44.1 kHz.
"""
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
RATE = 44100


def main():
    samples, rate = sf.read(ROOT / 'source_assets/sounds/rumor/magical_7.ogg', always_2d=True)
    samples = samples.mean(axis=1)
    active = np.flatnonzero(np.abs(samples) > .002)
    if not len(active):
        raise ValueError('Silent rumor source')
    samples = samples[max(0, active[0] - round(rate * .01)):active[-1] + 1]
    # Lower the original magic phrase, with a short reverse intake followed by its attack.
    speed = .78
    length = min(round(len(samples) * RATE / (rate * speed)), round(RATE * 1.25))
    body = np.interp(np.arange(length) * rate * speed / RATE, np.arange(len(samples)), samples)
    intake = body[:round(.32 * RATE)][::-1].copy()
    intake *= np.linspace(0, .5, len(intake)) ** 2
    result = np.zeros(round(RATE * 1.65))
    result[:len(intake)] += intake
    start = round(.18 * RATE)
    result[start:start + len(body)] += body
    # Soft echo broadens the spiral without obscuring the initial cue.
    echo = round(.12 * RATE)
    result[echo:] += result[:-echo].copy() * .18
    result -= result.mean()
    result[:round(.015 * RATE)] *= np.linspace(0, 1, round(.015 * RATE))
    result[-round(.4 * RATE):] *= np.linspace(1, 0, round(.4 * RATE)) ** 2
    result *= min(10 ** (-18 / 20) / max(1e-9, np.sqrt(np.mean(result ** 2))), .90 / np.max(np.abs(result)))
    result[0] = result[-1] = 0
    sf.write(ROOT / 'assets/sounds/rumor_spawn.wav', result, RATE, subtype='PCM_16')
    assert len(result) * 2 < 1_000_000
    print(f'rumor_spawn: {len(result)/RATE:.2f}s, mono PCM16/{RATE}Hz; peak {max(abs(result)):.3f}; '
          f'RMS {20*np.log10(np.sqrt(np.mean(result**2))):.1f} dBFS')


if __name__ == '__main__':
    main()
