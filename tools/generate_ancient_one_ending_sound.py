"""Master Robin Lamb's CC0 gameoverdark.wav for the Ancient One defeat dialogs.

Source: https://opengameart.org/content/game-over-dark-short-music-clips
Uses only Python's standard library. Original download is retained unchanged.
"""
from array import array
from pathlib import Path
import math
import sys
import wave

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "source_assets/sounds/ancient_one_ending/gameoverdark.wav"
TARGET = ROOT / "assets/sounds/ancient_one_ending.wav"


def main():
    with wave.open(str(SOURCE), "rb") as source:
        assert source.getsampwidth() == 2 and source.getnchannels() == 2
        rate = source.getframerate()
        stereo = array("h", source.readframes(source.getnframes()))
    if sys.byteorder != "little":
        stereo.byteswap()
    mono = [(stereo[i] + stereo[i + 1]) / 2 for i in range(0, len(stereo), 2)]
    # Keep the whole descending gong cue, with short click-free boundary fades.
    for i in range(len(mono)):
        mono[i] *= min(1.0, i / (rate * .005), (len(mono) - 1 - i) / (rate * .5))
    gain = .92 * 32767 / max(abs(sample) for sample in mono)
    pcm = array("h", (round(sample * gain) for sample in mono))
    assert len(pcm) * 2 < 1_000_000  # Android SoundPool decoded sample budget.
    assert pcm[0] == pcm[-1] == 0
    if sys.byteorder != "little":
        pcm.byteswap()
    with wave.open(str(TARGET), "wb") as target:
        target.setparams((1, 2, rate, len(pcm), "NONE", "not compressed"))
        target.writeframes(pcm.tobytes())
    rms = math.sqrt(sum((sample * gain / 32768) ** 2 for sample in mono) / len(mono))
    print(f"{TARGET.name}: {len(mono) / rate:.2f}s, mono PCM16 {rate}Hz, "
          f"peak=-0.72dBFS, RMS={20 * math.log10(rms):.2f}dBFS, {TARGET.stat().st_size} bytes")


if __name__ == "__main__":
    main()
