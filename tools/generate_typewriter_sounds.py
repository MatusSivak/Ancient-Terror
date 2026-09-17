"""Prepare CC0 recorded typewriter keys; no synthesis or network at build/runtime.
Source recordings and attribution: source_assets/sounds/typewriter/README.md.
"""
from pathlib import Path
import struct
import wave

ROOT = Path(__file__).resolve().parents[1]
for variant, original in enumerate(range(2, 8), 1):
    source = ROOT / 'source_assets/sounds/typewriter' / f'typewriter{original}.wav'
    with wave.open(str(source)) as wav:
        rate, channels = wav.getframerate(), wav.getnchannels()
        assert wav.getsampwidth() == 2
        raw = struct.unpack('<' + 'h' * (wav.getnframes() * channels), wav.readframes(wav.getnframes()))
    mono = [sum(raw[i:i + channels]) / channels / 32768 for i in range(0, len(raw), channels)]
    # Remove leading silence; retain the recorded attack and mechanical release.
    peak = max(map(abs, mono))
    onset = next(i for i, s in enumerate(mono) if abs(s) > peak * .12)
    start = max(0, onset - int(rate * .002))
    samples = mono[start:start + int(rate * .18)]
    gain = .7 / max(map(abs, samples))
    samples = [s * gain * min(1, i / 8) * min(1, (len(samples) - i - 1) / (rate * .015))
               for i, s in enumerate(samples)]
    target = ROOT / 'assets/sounds' / f'typewriter_key_{variant}.wav'
    with wave.open(str(target), 'wb') as wav:
        wav.setparams((1, 2, rate, 0, 'NONE', 'not compressed'))
        wav.writeframes(struct.pack('<' + 'h' * len(samples), *(round(s * 32767) for s in samples)))
    print(target.name, len(samples) / rate, 'seconds', target.stat().st_size, 'bytes')
