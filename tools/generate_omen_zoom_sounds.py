"""Cut the omen track zoom cues from two CC0 Freesound whooshes.

Maximize: "Spectral Ghost Whisper Breeze Whoosh" by brktkrgll (Freesound 856169).
Minimize: "Ghostly Whoosh - Horror Game UI" by TommasoMotteran (Freesound 850767).
"""
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCES = ROOT / 'source_assets/sounds/omen_zoom'
RATE = 44100
# The 0.75 s zoom should land on the loudest part of the swell.
CUES = (('Spectral Ghost Whisper Breeze Whoosh.mp3', 'omen_maximize.wav', 0.45, 1.9, 0.8),
        ('Ghostly Whoosh - Horror Game UI.mp3', 'omen_minimize.wav', 0.05, 1.6, 0.7))
TARGET_RMS_DB = -18.0
PEAK_LIMIT = 0.95


def rms_db(x):
    return 20 * np.log10(np.sqrt(np.mean(x ** 2)))


for source, target, start, duration, fade in CUES:
    samples, rate = sf.read(str(SOURCES / source), always_2d=True)
    assert rate == RATE
    mono = samples.mean(axis=1)[round(start * RATE):round((start + duration) * RATE)]
    mono *= np.minimum(1, np.arange(len(mono)) / (0.01 * RATE))
    tail = round(fade * RATE)
    mono[-tail:] *= np.linspace(1, 0, tail) ** 2
    # Match loudness so both directions of the same zoom feel balanced.
    mono *= 10 ** ((TARGET_RMS_DB - rms_db(mono)) / 20)
    mono *= min(1, PEAK_LIMIT / np.abs(mono).max())
    mono[0] = mono[-1] = 0.0
    path = ROOT / 'assets/sounds' / target
    sf.write(str(path), mono, RATE, subtype='PCM_16')
    print(f'{target}: {duration:.2f}s, RMS={rms_db(mono):.1f} dBFS, peak={np.abs(mono).max():.2f}')
