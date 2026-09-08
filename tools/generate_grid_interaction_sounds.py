"""Original, sample-free GridTest foley and restrained arcane accents.

Run with Python's standard library only. Each filename owns a stable random
seed, so regenerating a cue never depends on gameplay or generation order.
Levels below are linear full-scale peak/RMS ceilings, before playback volume.
Shared chess/token recordings are deliberately neither read nor overwritten.
"""

from dataclasses import dataclass
import hashlib
import math
from pathlib import Path
import random
import struct
import wave


SAMPLE_RATE = 44100
OUTPUT_DIRECTORY = Path(__file__).resolve().parents[1] / "assets" / "sounds"
TAU = 2.0 * math.pi


@dataclass(frozen=True)
class Cue:
    name: str
    duration: float
    peak: float
    rms: float


CUES = (
    Cue("action_unavailable", 0.20, 0.43, 0.105),
    Cue("swap_select", 0.13, 0.40, 0.095),
    Cue("swap_deselect", 0.14, 0.34, 0.080),
    Cue("swap", 0.28, 0.49, 0.115),
    Cue("shift", 0.30, 0.51, 0.115),
    Cue("grid_explosion", 0.48, 0.61, 0.140),
    Cue("grid_implosion", 0.52, 0.49, 0.115),
    Cue("grid_spawn", 0.48, 0.40, 0.090),
    Cue("grid_reroll", 0.29, 0.49, 0.115),
    Cue("grid_super_reroll", 0.54, 0.57, 0.130),
    Cue("grid_pickup", 0.26, 0.43, 0.105),
    Cue("grid_cascade", 0.23, 0.38, 0.090),
    Cue("grid_bonus_shift", 0.34, 0.43, 0.105),
    Cue("grid_test_start", 0.58, 0.49, 0.115),
    Cue("grid_test_complete", 0.84, 0.50, 0.120),
    Cue("grid_setting", 0.08, 0.22, 0.045),
)

# Inharmonic, rapidly damped modes evoke solid objects rather than UI beeps.
WOOD = ((1.0, 1.0, 1.0), (2.63, 0.38, 0.55), (4.17, 0.15, 0.28))
STONE = ((1.0, 1.0, 1.0), (1.49, 0.46, 0.70), (3.82, 0.20, 0.32))
CHIME = ((1.0, 1.0, 1.0), (2.0, 0.20, 0.62), (3.01, 0.09, 0.36))


def smoothstep(value):
    value = min(1.0, max(0.0, value))
    return value * value * (3.0 - 2.0 * value)


class Synth:
    def __init__(self, cue):
        self.cue = cue
        self.samples = [0.0] * round(cue.duration * SAMPLE_RATE)
        seed = int.from_bytes(hashlib.sha256(cue.name.encode("ascii")).digest()[:8], "big")
        self.random = random.Random(seed)

    def mix(self, start, duration, voice):
        """Mix an edge-tapered voice, including a zero-valued final sample."""
        count = max(2, round(duration * SAMPLE_RATE))
        offset = round(start * SAMPLE_RATE)
        for index in range(count):
            value = voice(index / SAMPLE_RATE)
            edge = smoothstep(index / (0.0015 * SAMPLE_RATE))
            edge *= smoothstep((count - 1 - index) / (0.012 * SAMPLE_RATE))
            target = offset + index
            if 0 <= target < len(self.samples):
                self.samples[target] += value * edge

    def modal(self, start, frequency, level, decay=0.030, modes=WOOD, duration=None):
        duration = duration if duration is not None else min(decay * 7.0, 0.42)
        detune = self.random.uniform(0.992, 1.008)

        def voice(age):
            attack = smoothstep(age / 0.0025)
            return level * attack * sum(
                amplitude * math.exp(-age / (decay * damping))
                * math.sin(TAU * frequency * detune * ratio * age)
                for ratio, amplitude, damping in modes
            )

        self.mix(start, duration, voice)

    def air(self, start, duration, level, low=150.0, high=2300.0, shape="swell"):
        """Quiet band-limited friction; never mix raw white noise."""
        fast = slow = 0.0
        fast_alpha = 1.0 - math.exp(-TAU * high / SAMPLE_RATE)
        slow_alpha = 1.0 - math.exp(-TAU * low / SAMPLE_RATE)

        def voice(age):
            nonlocal fast, slow
            noise = self.random.uniform(-1.0, 1.0)
            fast += fast_alpha * (noise - fast)
            slow += slow_alpha * (noise - slow)
            progress = min(age / duration, 1.0)
            if shape == "grain":
                envelope = math.exp(-age / (duration * 0.19))
            elif shape == "suction":
                envelope = progress ** 1.8 * smoothstep((1.0 - progress) / 0.16)
            else:
                envelope = math.sin(math.pi * progress) ** 2
            return level * (fast - slow) * envelope

        self.mix(start, duration, voice)

    def sweep(self, start, duration, first, last, level, falling=False):
        def voice(age):
            progress = min(age / duration, 1.0)
            phase = TAU * (first * age + (last - first) * age * age / (2.0 * duration))
            envelope = (smoothstep(age / 0.005) * math.exp(-age / 0.065)
                        if falling else math.sin(math.pi * progress) ** 2)
            return level * envelope * (math.sin(phase) + 0.12 * math.sin(2.03 * phase))

        self.mix(start, duration, voice)

    def knock(self, start, frequency, level, decay=0.025, stone=False):
        self.modal(start, frequency, level, decay, STONE if stone else WOOD)
        self.air(start, 0.035, level * 0.30, 500.0, 3400.0, "grain")

    def chime(self, start, frequency, level, decay=0.075):
        self.modal(start, frequency, level, decay, CHIME)
        # Two dark, quiet reflections add space without a long reverb tail.
        self.modal(start + 0.033, frequency, level * 0.10, decay * 0.62, CHIME)
        self.modal(start + 0.061, frequency, level * 0.045, decay * 0.45, CHIME)

    def finish(self):
        """Remove DC/ultrasonic edge, taper, then enforce both loudness limits."""
        previous = high_pass = low_pass = 0.0
        dc_decay = math.exp(-TAU * 18.0 / SAMPLE_RATE)
        low_alpha = 1.0 - math.exp(-TAU * 6200.0 / SAMPLE_RATE)
        result = []
        for index, sample in enumerate(self.samples):
            high_pass = sample - previous + dc_decay * high_pass
            previous = sample
            low_pass += low_alpha * (high_pass - low_pass)
            envelope = smoothstep(index / (0.003 * SAMPLE_RATE))
            envelope *= smoothstep((len(self.samples) - 1 - index) / (0.025 * SAMPLE_RATE))
            result.append(low_pass * envelope)
        if not all(math.isfinite(sample) for sample in result):
            raise ValueError(f"{self.cue.name}: non-finite synthesis")
        peak = max(abs(sample) for sample in result)
        rms = math.sqrt(sum(sample * sample for sample in result) / len(result))
        if peak == 0.0 or rms == 0.0:
            raise ValueError(f"{self.cue.name}: silent synthesis")
        gain = min(self.cue.peak / peak, self.cue.rms / rms)
        return [round(sample * gain * 32767) for sample in result]


def synthesize(cue):
    sound = Synth(cue)
    name = cue.name
    if name == "action_unavailable":
        sound.knock(0.003, 173, 0.70, 0.018, stone=True)
        sound.knock(0.081, 127, 0.55, 0.022, stone=True)
    elif name == "swap_select":
        sound.knock(0.003, 320, 0.58, 0.021)
        sound.modal(0.014, 640, 0.075, 0.022, CHIME)
    elif name == "swap_deselect":
        sound.knock(0.003, 238, 0.48, 0.019)
        sound.air(0.009, 0.078, 0.14, 250, 1800)
    elif name == "swap":
        sound.knock(0.003, 205, 0.43)
        sound.air(0.022, 0.183, 0.62, 190, 2300)
        sound.knock(0.151, 281, 0.63, 0.023)
        sound.knock(0.182, 183, 0.22, 0.018)
    elif name == "shift":
        sound.knock(0.004, 151, 0.26, 0.018, stone=True)
        sound.air(0.012, 0.213, 0.72, 130, 2000)
        for start, frequency in ((0.049, 260), (0.093, 231), (0.143, 214)):
            sound.knock(start, frequency, 0.065, 0.011)
        sound.knock(0.218, 119, 0.68, 0.018, stone=True)
    elif name == "grid_explosion":
        sound.sweep(0.002, 0.27, 137, 47, 0.93, falling=True)
        sound.knock(0.007, 188, 0.46, 0.024, stone=True)
        sound.air(0.006, 0.13, 0.53, 340, 3400, "grain")
        for start, frequency, level in (
                (0.036, 740, 0.16), (0.062, 490, 0.21), (0.102, 910, 0.12),
                (0.151, 340, 0.13), (0.217, 570, 0.075), (0.292, 230, 0.040)):
            sound.knock(start, frequency, level, 0.014, stone=True)
        sound.air(0.055, 0.32, 0.14, 90, 1000)
    elif name == "grid_implosion":
        sound.air(0.002, 0.224, 0.67, 180, 2000, "suction")
        sound.sweep(0.023, 0.195, 210, 380, 0.095)
        sound.knock(0.214, 192, 0.28, 0.026, stone=True)
        sound.chime(0.220, 392, 0.33, 0.078)
        sound.chime(0.258, 588, 0.15, 0.064)
    elif name == "grid_spawn":
        sound.air(0.004, 0.315, 0.48, 110, 1750)
        sound.sweep(0.040, 0.28, 196, 294, 0.070)
        sound.modal(0.140, 392, 0.10, 0.060, CHIME)
        sound.knock(0.301, 164, 0.29, 0.031)
        sound.knock(0.327, 246, 0.11, 0.022)
    elif name == "grid_reroll":
        sound.air(0.006, 0.216, 0.45, 260, 2450)
        for start, frequency, level in (
                (0.006, 246, 0.40), (0.044, 311, 0.25), (0.083, 185, 0.32),
                (0.128, 277, 0.26), (0.190, 147, 0.52)):
            sound.knock(start, frequency, level, 0.016)
    elif name == "grid_super_reroll":
        sound.air(0.008, 0.365, 0.60, 130, 2650)
        sound.sweep(0.060, 0.265, 130, 260, 0.13)
        for index, frequency in enumerate((164, 247, 196, 330, 220, 294, 185)):
            sound.knock(0.008 + index * 0.039, frequency, 0.22 + index * 0.022, 0.019)
        sound.sweep(0.280, 0.19, 117, 67, 0.48, falling=True)
        sound.chime(0.295, 392, 0.16, 0.055)
        sound.chime(0.325, 588, 0.095, 0.050)
    elif name == "grid_pickup":
        sound.knock(0.003, 294, 0.43, 0.019)
        sound.chime(0.016, 588, 0.17, 0.043)
        sound.air(0.013, 0.164, 0.25, 220, 2100, "suction")
        sound.sweep(0.016, 0.135, 294, 440, 0.065)
    elif name == "grid_cascade":
        sound.air(0.004, 0.16, 0.28, 200, 1950, "suction")
        sound.modal(0.003, 294, 0.24, 0.026, WOOD)
        sound.chime(0.055, 392, 0.22, 0.033)
        sound.chime(0.100, 494, 0.14, 0.027)
    elif name == "grid_bonus_shift":
        sound.knock(0.004, 196, 0.29, 0.024)
        sound.air(0.005, 0.206, 0.20, 170, 1550)
        sound.chime(0.025, 392, 0.23, 0.049)
        sound.chime(0.121, 494, 0.19, 0.052)
        sound.chime(0.176, 588, 0.13, 0.046)
    elif name == "grid_test_start":
        sound.air(0.003, 0.352, 0.36, 110, 1750)
        sound.knock(0.013, 147, 0.35, 0.030)
        for start, frequency, level in ((0.043, 294, 0.21), (0.139, 392, 0.24),
                                         (0.247, 588, 0.18)):
            sound.chime(start, frequency, level, 0.068)
        sound.modal(0.246, 196, 0.20, 0.071, WOOD)
    elif name == "grid_test_complete":
        sound.air(0.005, 0.55, 0.22, 100, 1400)
        sound.knock(0.006, 196, 0.26, 0.030)
        for start, frequency, level in ((0.025, 494, 0.20), (0.158, 440, 0.16),
                                         (0.312, 392, 0.24), (0.326, 588, 0.12)):
            sound.chime(start, frequency, level, 0.093)
        sound.modal(0.310, 196, 0.31, 0.100, WOOD, duration=0.49)
    elif name == "grid_setting":
        sound.knock(0.003, 410, 0.24, 0.008)
    else:
        raise ValueError(f"Unknown cue: {name}")
    return sound.finish()


def validate_pcm(cue, samples):
    if len(samples) != round(cue.duration * SAMPLE_RATE):
        raise ValueError(f"{cue.name}: incorrect duration")
    if not samples or any(not isinstance(sample, int) or abs(sample) >= 32767 for sample in samples):
        raise ValueError(f"{cue.name}: invalid or clipped PCM")
    peak = max(abs(sample) for sample in samples) / 32767.0
    rms = math.sqrt(sum(sample * sample for sample in samples) / len(samples)) / 32767.0
    tolerance = 1.0 / 32767
    if not (0 < peak <= cue.peak + tolerance and 0 < rms <= cue.rms + tolerance):
        raise ValueError(f"{cue.name}: loudness outside bounds")
    if samples[0] != 0 or samples[-1] != 0:
        raise ValueError(f"{cue.name}: untapered endpoints")
    return peak, rms


def write_sound(cue, samples):
    peak, rms = validate_pcm(cue, samples)
    filename = OUTPUT_DIRECTORY / (cue.name + ".wav")
    with wave.open(str(filename), "wb") as output:
        output.setnchannels(1)
        output.setsampwidth(2)
        output.setframerate(SAMPLE_RATE)
        output.writeframes(struct.pack(f"<{len(samples)}h", *samples))
    with wave.open(str(filename), "rb") as saved:
        if (saved.getnchannels(), saved.getsampwidth(), saved.getframerate(), saved.getcomptype()) != (
                1, 2, SAMPLE_RATE, "NONE"):
            raise ValueError(f"{cue.name}: invalid WAV format")
        decoded = struct.unpack(f"<{saved.getnframes()}h", saved.readframes(saved.getnframes()))
        validate_pcm(cue, decoded)
        if tuple(samples) != decoded:
            raise ValueError(f"{cue.name}: PCM round trip failed")
    print(f"{filename.name}: {cue.duration:.2f}s, peak {peak:.3f}, RMS {rms:.3f}")


def main():
    OUTPUT_DIRECTORY.mkdir(parents=True, exist_ok=True)
    for cue in CUES:
        write_sound(cue, synthesize(cue))
    print(f"Validated {len(CUES)} original mono {SAMPLE_RATE} Hz / 16-bit PCM cues.")


if __name__ == "__main__":
    main()
