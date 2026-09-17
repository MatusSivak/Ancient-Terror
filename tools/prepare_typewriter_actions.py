"""Trim recorded CC0 typewriter actions; requires soundfile and numpy.
No synthesis. See source_assets/sounds/typewriter/README.md for provenance.
"""
from pathlib import Path
import sys
ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / 'output/audio-tools'))
import soundfile as sf
import numpy as np

# Distinct recorded gestures, shortened for the corresponding UI animations.
CLIPS = {
    'space': ('space.mp3', .08, .38),
    'button_stamp': ('stamp.mp3', .49, .91),
    'paper_out': ('paper.mp3', 1.08, 1.60),
}
for name, (source, start, end) in CLIPS.items():
    samples, rate = sf.read(ROOT / 'source_assets/sounds/typewriter' / source, always_2d=True)
    samples = samples[int(start * rate):int(end * rate)].mean(axis=1)
    samples -= samples.mean()
    samples = np.interp(np.arange(round(len(samples) / rate * 22050)) * rate / 22050,
                        np.arange(len(samples)), samples)
    samples *= .65 / np.max(np.abs(samples))
    fade = min(220, len(samples)//4)
    samples[:44] *= np.linspace(0,1,44)
    samples[-fade:] *= np.linspace(1,0,fade)
    path = ROOT / 'assets/sounds' / ('typewriter_' + name + '.wav')
    sf.write(path, samples, 22050, subtype='PCM_16')
    print(path.name, round(len(samples)/22050,3), 'seconds', path.stat().st_size, 'bytes')
