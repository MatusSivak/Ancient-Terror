"""Master the Azathoth defeat cue from shelbyshark's CC0 "HorrorSting1" (Freesound 513332)."""
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'source_assets/sounds/azathoth_ending/HorrorSting1.mp3'
TARGET = ROOT / 'assets/sounds/azathoth_ending.wav'
RATE = 44100
START, DURATION, FADE = 0.9, 9.5, 2.5
PEAK = 0.98

samples, rate = sf.read(str(SOURCE), always_2d=True)
assert rate == RATE
mono = samples.mean(axis=1)[round(START * RATE):round((START + DURATION) * RATE)]
mono *= np.minimum(1, np.arange(len(mono)) / (0.005 * RATE))
fade = round(FADE * RATE)
mono[-fade:] *= np.linspace(1, 0, fade) ** 2
# Soft saturation lifts the rumbling tail so the boom reads louder than a plain peak normalize.
mono = np.tanh(2.2 * mono / np.abs(mono).max())
mono *= PEAK / np.abs(mono).max()
mono[0] = mono[-1] = 0.0
# Keep decoded PCM below Android SoundPool's per-sound memory limit.
assert len(mono) * 2 < 1000000
sf.write(str(TARGET), mono, RATE, subtype='PCM_16')
rms = 20 * np.log10(np.sqrt(np.mean(mono[:RATE * 2] ** 2)))
print(f'{TARGET.name}: {len(mono) / RATE:.1f}s, peak={PEAK}, first 2s RMS={rms:.1f} dBFS, '
      f'{TARGET.stat().st_size} bytes')
