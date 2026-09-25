"""Original, sample-free cues for gaining and losing Health and Sanity tokens.
Health and Sanity share the same cues (health_gain / health_loss / token_leave).

Run with Python's standard library only (reuses helpers from generate_travel_sounds).
Gains play while each token blooms in the centre of the screen; losses play once,
when the discarded token(s) split in half, after a short shared token_leave whoosh
as the token lifts out of the HUD bar. Gains are warm, losses are visceral.
Each filename owns a stable random seed, so regeneration is deterministic.
"""

import math

from generate_travel_sounds import (SAMPLE_RATE, TAU, finish, seeded_random, smoothstep,
                                    swept_breath, write)


def blank(duration):
    return [0.0] * round(duration * SAMPLE_RATE)


def add(target, source, start=0.0, gain=1.0):
    offset = round(start * SAMPLE_RATE)
    for index, value in enumerate(source):
        if 0 <= offset + index < len(target):
            target[offset + index] += value * gain


def tone(duration, frequency, level, attack, decay, glide=1.0, phase=0.0, vibrato=0.0):
    """Sine partial with exponential decay and optional exponential pitch glide."""
    count = round(duration * SAMPLE_RATE)
    out = [0.0] * count
    for index in range(count):
        t = index / SAMPLE_RATE
        position = index / max(1, count - 1)
        current = frequency * glide ** position * (1.0 + vibrato * math.sin(TAU * 5.2 * t))
        phase += TAU * current / SAMPLE_RATE
        out[index] = level * smoothstep(t / attack) * math.exp(-t / decay) * math.sin(phase)
    return out


def thump(frequency, level, decay, duration=0.4):
    """Low body hit whose pitch drops quickly, like a muffled drum or heartbeat."""
    count = round(duration * SAMPLE_RATE)
    out = [0.0] * count
    phase = 0.0
    for index in range(count):
        t = index / SAMPLE_RATE
        current = frequency * (1.0 + 0.9 * math.exp(-t / 0.018))
        phase += TAU * current / SAMPLE_RATE
        out[index] = level * smoothstep(t / 0.003) * math.exp(-t / decay) * math.sin(phase)
    return out


def lowpass(samples, cutoff):
    alpha = 1.0 - math.exp(-TAU * cutoff / SAMPLE_RATE)
    state = 0.0
    out = []
    for value in samples:
        state += alpha * (value - state)
        out.append(state)
    return out


def room(samples, mix=0.22, size=1.0):
    """Tiny Schroeder reverb: parallel feedback combs, then one all-pass."""
    delays = [round(d * size * SAMPLE_RATE) for d in (0.0297, 0.0371, 0.0411, 0.0437)]
    tail = [0.0] * len(samples)
    for delay in delays:
        buffer = [0.0] * delay
        position = 0
        for index, value in enumerate(samples):
            echoed = buffer[position]
            buffer[position] = value + echoed * 0.72
            position = (position + 1) % delay
            tail[index] += echoed / len(delays)
    delay = round(0.005 * SAMPLE_RATE)
    buffer = [0.0] * delay
    position = 0
    for index, value in enumerate(tail):
        delayed = buffer[position]
        output = delayed - 0.5 * value
        buffer[position] = value + 0.5 * delayed
        position = (position + 1) % delay
        tail[index] = output
    return [dry + wet * mix for dry, wet in zip(samples, tail)]


def fade_tail(samples, start_fraction):
    count = len(samples)
    return [value * (1.0 - smoothstep((i / (count - 1) - start_fraction) / (1.0 - start_fraction)))
            for i, value in enumerate(samples)]


def health_gain():
    name = "health_gain"
    rng = seeded_random(name)
    out = blank(1.05)
    # Soft double pulse, like a calm heartbeat returning.
    add(out, lowpass(thump(62.0, 0.55, 0.080), 900.0), 0.02)
    add(out, lowpass(thump(58.0, 0.38, 0.090), 900.0), 0.24)
    # Warm swell rising a major third: F3 -> A3 with a soft octave.
    for frequency, level, start in ((174.61, 0.34, 0.05), (220.00, 0.30, 0.30), (349.23, 0.12, 0.30)):
        add(out, tone(0.75, frequency, level, 0.16, 0.30, glide=1.006, phase=rng.uniform(0, TAU)), start)
    breath_env = lambda p: 0.10 * smoothstep(p / 0.35) * (1.0 - smoothstep((p - 0.3) / 0.5))
    add(out, swept_breath(rng, len(out), 380.0, 1100.0, breath_env, q=0.8))
    return name, fade_tail(room(out, 0.18), 0.7)


def health_loss():
    name = "health_loss"
    rng = seeded_random(name)
    out = blank(0.85)
    # Dull body blow.
    add(out, lowpass(thump(74.0, 0.70, 0.070), 1200.0))
    add(out, lowpass(thump(140.0, 0.22, 0.030, 0.2), 1600.0), 0.004)
    # Short muffled tear: gritty noise bursts through a band that falls.
    grit = []
    for index in range(round(0.30 * SAMPLE_RATE)):
        grain = rng.uniform(-1.0, 1.0) if rng.random() < 0.55 else 0.0
        grit.append(grain)
    count = len(grit)
    tear_env = lambda p: smoothstep(p / 0.08) * (1.0 - smoothstep((p - 0.15) / 0.85))
    band = swept_breath(rng, count, 2400.0, 700.0, tear_env, q=0.5)
    add(out, [b * (0.4 + 0.6 * abs(g)) for b, g in zip(band, grit)], 0.02, 0.9)
    # A low minor second underneath gives it a sting.
    add(out, tone(0.6, 98.0, 0.16, 0.01, 0.22, glide=0.92), 0.01)
    add(out, tone(0.6, 103.8, 0.12, 0.01, 0.20, glide=0.92), 0.01)
    return name, fade_tail(room(out, 0.12, 0.8), 0.6)


def token_leave():
    """Shared by Health and Sanity: a short lift-off as the token pops out of its
    HUD slot and starts spinning towards the centre."""
    name = "token_leave"
    rng = seeded_random(name)
    duration = 0.38
    out = blank(duration)
    # Tiny unclip as the token leaves the slot.
    add(out, lowpass(thump(260.0, 0.30, 0.018, 0.08), 3000.0))
    add(out, tone(0.06, 1560.0, 0.08, 0.001, 0.010, phase=rng.uniform(0, TAU)), 0.002)
    # Quick rising whoosh with one flutter from the spin.
    count = len(out)
    swell = lambda p: 0.55 * math.sin(math.pi * min(1.0, p / 0.95)) ** 1.4
    air = swept_breath(rng, count, 600.0, 2200.0, swell, q=0.7)
    spin = [1.0 - 0.3 * (0.5 + 0.5 * math.cos(TAU * 1.5 * i / count)) for i in range(count)]
    add(out, [a * s for a, s in zip(air, spin)])
    add(out, tone(0.34, 110.0, 0.10, 0.08, 0.4, glide=1.4, phase=rng.uniform(0, TAU)), 0.02)
    return name, fade_tail(room(out, 0.08, 0.8), 0.7)


def token_land():
    """Shared by Health and Sanity: a gained token settling softly into its HUD slot."""
    name = "token_land"
    rng = seeded_random(name)
    out = blank(0.22)
    # Muffled felt-on-wood thud: low damped body, no bright partials or comb reverb (they ring like tin).
    add(out, lowpass(lowpass(thump(120.0, 0.60, 0.030, 0.2), 700.0), 900.0))
    add(out, lowpass(thump(240.0, 0.12, 0.012, 0.08), 1000.0), 0.002)
    # Very short, dark brush of air for the contact.
    brush_env = lambda p: 0.10 * smoothstep(p / 0.02) * (1.0 - smoothstep(p / 0.35))
    add(out, lowpass(swept_breath(rng, len(out), 900.0, 500.0, brush_env, q=1.2), 1200.0))
    return name, fade_tail(out, 0.5)


def main():
    for build, peak in ((health_gain, 0.48), (health_loss, 0.55), (token_leave, 0.40), (token_land, 0.40)):
        name, samples = build()
        write(name, finish(samples, peak))


if __name__ == "__main__":
    main()
