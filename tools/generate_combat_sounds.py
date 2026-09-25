"""Original, sample-free cues for combat fireballs and monster tokens.

Run from the tools directory with Python's standard library only (reuses helpers from
generate_travel_sounds / generate_token_sounds).
- fireball_launch: a die hurls a fireball (ignition puff + roaring whoosh).
- fireball_impact: the fireball explodes on its target (deep boom + crackle).
- monster_token_hit: a monster's Horror / Damage icon is scorched and shrinks.
- monster_health_leave: a monster Health token lifts out of its bar.
- monster_health_tear: that Health token is torn in half.
Many of these overlap in a fight, so they are short and kept away from bright partials.
Each filename owns a stable random seed, so regeneration is deterministic.
"""

import math

from generate_token_sounds import add, blank, fade_tail, lowpass, room, thump, tone
from generate_travel_sounds import SAMPLE_RATE, TAU, finish, seeded_random, smoothstep, swept_breath, write


def flicker(rng, count, rate_hz, depth):
    """Smoothed random amplitude wobble, like a flame fluttering."""
    out = []
    target = value = 1.0
    step = max(1, round(SAMPLE_RATE / rate_hz))
    for index in range(count):
        if index % step == 0:
            target = 1.0 - depth * rng.random()
        value += (target - value) * 0.004
        out.append(value)
    return out


def crackle(rng, count, density, decay_fraction, cutoff):
    """Sparse, softened embers popping."""
    raw = [0.0] * count
    for index in range(count):
        position = index / (count - 1)
        if rng.random() < density * math.exp(-position / decay_fraction):
            raw[index] = rng.uniform(-1.0, 1.0)
    return lowpass(lowpass(raw, cutoff), cutoff * 1.3)


def fireball_launch():
    name = "fireball_launch"
    rng = seeded_random(name)
    out = blank(0.55)
    count = len(out)
    # Ignition puff.
    add(out, lowpass(thump(95.0, 0.45, 0.035, 0.15), 900.0))
    # Roaring whoosh that swells then trails away as the fireball flies off.
    swell = lambda p: 0.75 * smoothstep(p / 0.18) * (1.0 - smoothstep((p - 0.25) / 0.75))
    roar = swept_breath(rng, count, 350.0, 1300.0, swell, q=0.6)
    flame = flicker(rng, count, 45.0, 0.45)
    add(out, [r * f for r, f in zip(roar, flame)])
    # Low flame body underneath.
    body_env = lambda p: 0.55 * smoothstep(p / 0.1) * (1.0 - smoothstep((p - 0.2) / 0.7))
    add(out, lowpass(swept_breath(rng, count, 140.0, 260.0, body_env, q=0.9), 500.0))
    return name, fade_tail(out, 0.75)


def fireball_impact():
    name = "fireball_impact"
    rng = seeded_random(name)
    out = blank(0.70)
    count = len(out)
    # Deep boom.
    add(out, lowpass(thump(52.0, 0.80, 0.110, 0.6), 700.0))
    add(out, lowpass(thump(105.0, 0.30, 0.040, 0.25), 1100.0), 0.003)
    # Blast of air that darkens as it spreads.
    blast_env = lambda p: 0.70 * smoothstep(p / 0.01) * math.exp(-p / 0.22)
    add(out, lowpass(swept_breath(rng, count, 1600.0, 250.0, blast_env, q=0.5), 2200.0))
    # Embers crackling after the blast.
    add(out, crackle(rng, count, 0.010, 0.35, 2600.0), 0.02, 1.4)
    return name, fade_tail(room(out, 0.10, 0.9), 0.65)


def monster_token_hit():
    name = "monster_token_hit"
    rng = seeded_random(name)
    out = blank(0.36)
    count = len(out)
    # Dull strike on the icon.
    add(out, lowpass(thump(160.0, 0.50, 0.025, 0.12), 1200.0))
    # Short scorching sizzle as it shrinks.
    sizzle_env = lambda p: 0.55 * smoothstep(p / 0.04) * (1.0 - smoothstep((p - 0.1) / 0.8))
    sizzle = swept_breath(rng, count, 2600.0, 1200.0, sizzle_env, q=0.8)
    grit = flicker(rng, count, 180.0, 0.8)
    add(out, lowpass([s * g for s, g in zip(sizzle, grit)], 3200.0), 0.01)
    # Low growl from the monster losing ground.
    add(out, tone(0.30, 82.0, 0.20, 0.01, 0.12, glide=0.85, phase=rng.uniform(0, TAU)), 0.005)
    return name, fade_tail(out, 0.6)


def monster_health_leave():
    name = "monster_health_leave"
    rng = seeded_random(name)
    out = blank(0.45)
    count = len(out)
    # Heavy unclip from the bar.
    add(out, lowpass(thump(130.0, 0.40, 0.030, 0.12), 1400.0))
    # Dark rising whoosh with the spin flutter; lower than the investigator token_leave.
    swell = lambda p: 0.60 * math.sin(math.pi * min(1.0, p / 0.95)) ** 1.3
    air = swept_breath(rng, count, 300.0, 1400.0, swell, q=0.7)
    spin = [1.0 - 0.35 * (0.5 + 0.5 * math.cos(TAU * 2.0 * i / count)) for i in range(count)]
    add(out, [a * s for a, s in zip(air, spin)])
    add(out, tone(0.42, 65.0, 0.18, 0.08, 0.5, glide=1.6, phase=rng.uniform(0, TAU)), 0.01)
    return name, fade_tail(room(out, 0.08, 0.9), 0.7)


def monster_health_tear():
    name = "monster_health_tear"
    rng = seeded_random(name)
    out = blank(0.60)
    # Wet, heavy blow as the token splits.
    add(out, lowpass(thump(60.0, 0.75, 0.080, 0.4), 900.0))
    # Ripping: irregular gritty bursts through a band that falls as the halves separate.
    count = round(0.42 * SAMPLE_RATE)
    rip_env = lambda p: 0.95 * smoothstep(p / 0.05) * (1.0 - smoothstep((p - 0.2) / 0.8))
    band = swept_breath(rng, count, 2200.0, 450.0, rip_env, q=0.45)
    fibres = flicker(rng, count, 90.0, 0.95)
    grains = [0.5 + 0.5 * abs(rng.uniform(-1.0, 1.0)) if rng.random() < 0.6 else 0.25 for _ in range(count)]
    add(out, lowpass([b * f * g for b, f, g in zip(band, fibres, grains)], 2800.0), 0.015)
    # A monstrous groan: low tritone falling.
    add(out, tone(0.55, 73.4, 0.20, 0.02, 0.22, glide=0.8, phase=rng.uniform(0, TAU)), 0.02)
    add(out, tone(0.55, 103.8, 0.12, 0.02, 0.18, glide=0.8, phase=rng.uniform(0, TAU)), 0.02)
    return name, fade_tail(room(out, 0.10, 0.9), 0.6)


def main():
    for build, peak in ((fireball_launch, 0.45), (fireball_impact, 0.55), (monster_token_hit, 0.45),
                        (monster_health_leave, 0.42), (monster_health_tear, 0.55)):
        name, samples = build()
        write(name, finish(samples, peak))


if __name__ == "__main__":
    main()
