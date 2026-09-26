"""Cut the doom-advance toll from 3bagbrew's CC0 "German Grandfather Clock Tick & Chime x12" (Freesound 609763)."""
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'source_assets/sounds/doom_advance/German Grandfather Clock Tick & Chime x12.mp3'
TARGET = ROOT / 'assets/sounds/doom_advance.wav'
RATE = 44100
# The twelfth (last) strike: its tail is not overlapped by a following chime.
START, DURATION, FADE = 55.59, 3.0, 1.2
PEAK = 0.9

samples, rate = sf.read(str(SOURCE), always_2d=True)
assert rate == RATE
mono = samples.mean(axis=1)[round(START * RATE):round((START + DURATION) * RATE)]
# Silence the previous strike's tail that leads into the attack.
mono *= np.minimum(1, np.arange(len(mono)) / (0.004 * RATE))
fade = round(FADE * RATE)
mono[-fade:] *= np.linspace(1, 0, fade) ** 2
mono *= PEAK / np.abs(mono).max()
mono[0] = mono[-1] = 0.0
assert len(mono) * 2 < 1000000
sf.write(str(TARGET), mono, RATE, subtype='PCM_16')
print(f'{TARGET.name}: {len(mono) / RATE:.1f}s, peak={PEAK}, {TARGET.stat().st_size} bytes')
