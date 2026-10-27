"""Prepare the CC0 gameplay cues; see assets/sounds/GAMEPLAY_AUDIO.md.

Requires numpy and soundfile. Originals are preserved in source_assets/sounds/gameplay.
No network access is needed. Output is 44.1 kHz mono PCM16 for desktop and SoundPool.
"""
from pathlib import Path
import numpy as np
import soundfile as sf
from prepare_tabletop_sounds import SOURCES as TABLETOP_SOURCES, prepare as prepare_tabletop

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "source_assets/sounds/gameplay"
OUTPUT = ROOT / "assets/sounds"
RATE = 44100


def clip(name, speed=1.0, duration=2.0, reverse=False):
    samples, rate = sf.read(SOURCE / name, always_2d=True)
    samples = samples.mean(axis=1)
    active = np.flatnonzero(np.abs(samples) > 0.002)
    if not len(active):
        raise ValueError(f"Silent source: {name}")
    samples = samples[max(0, active[0] - int(rate * .008)):active[-1] + 1]
    if reverse:
        samples = samples[::-1]
    length = min(round(len(samples) * RATE / (rate * speed)), round(duration * RATE))
    samples = np.interp(np.arange(length) * rate * speed / RATE,
                        np.arange(len(samples)), samples)
    return samples


def mix(*layers):
    """Layers are (samples, start_seconds, gain)."""
    result = np.zeros(max(round(start * RATE) + len(x) for x, start, gain in layers))
    for samples, start, gain in layers:
        offset = round(start * RATE)
        result[offset:offset + len(samples)] += samples * gain
    return result


def bell(speed=1.0, duration=1.0):
    return clip("bell_ding1.wav", speed, duration)


def cues():
    return {
        "clue_spawn": bell(.85, 1.4),
        "clue_gain": mix((clip("item_gem_04.ogg"), 0, .55), (bell(1.25, .85), 0, .7)),
        "clue_land": mix((clip("item_gem_01.ogg", 1.4, .22), 0, 1), (bell(1.6, .25), .025, .35)),
        "focus_gain": bell(.65, .85),
        "focus_land": mix((clip("item_gem_01.ogg", .95, .2), 0, 1), (bell(.95, .24), .015, .3)),
        "resource_spend": clip("item_gem_01.ogg", .9),
        "ticket_gain": clip("book_01.ogg"),
        "ticket_land": mix((clip("book_02.ogg", 1.8, .18), 0, 1),
                           (clip("item_gem_01.ogg", .8, .14), 0, .45)),
        "ticket_spend": clip("book_02.ogg"),
        "card_gain": clip("book_03.ogg"),
        "card_discard": clip("book_04.ogg", 1.15),
        "gate_open": clip("teleport.wav", .85, 1.6),
        "gate_close": clip("teleport.wav", 1.1, 1.3, reverse=True),
        "monster_spawn": clip("creature_monster_03.ogg"),
        "epic_monster_spawn": clip("creature_roar_03.ogg", .8, 1.5),
        "monster_defeat": clip("creature_die_01.ogg", .95),
        "mystery_progress": bell(1.05, .65),
        "mystery_solved": mix((bell(.8, 1.2), 0, 1), (bell(1.0, 1.2), .18, .8),
                              (bell(1.2, 1.4), .36, .7)),
        "skill_gain": mix((bell(1.0, .8), 0, 1), (bell(1.5, .9), .14, .65)),
        "skill_loss": bell(.55, .9),
        "hazard_spawn": clip("spell_02.ogg", .65, 1.0),
        "investigator_spawn": mix((clip("book_01.ogg", .9, .36), 0, .45),
                                  (bell(.7, .75), .12, .5), (clip("teleport.wav", 1.8, .65), 0, .2)),
        "investigator_defeat": mix((bell(.4, 1.7), 0, 1), (bell(.37, 1.4), .2, .55)),
        "investigator_devoured": clip("teleport.wav", .6, 1.8, reverse=True),
        "investigator_delayed": mix((clip("item_gem_01.ogg", .65, .3), 0, .8), (bell(.6, .65), .08, .5)),
        "investigator_ready": mix((clip("item_gem_01.ogg", 1.2, .18), 0, .6), (bell(1.15, .6), .06, .5)),
        "investigator_disappear": clip("teleport.wav", 1.35, .8, reverse=True),
        "omen_change": mix((clip("spell_02.ogg", 1.25, .75), 0, .55), (bell(.72, .85), .12, .7)),
        "omen_token_add": mix((clip("item_gem_01.ogg", .8, .26), 0, .8), (clip("spell_02.ogg", .9, .6), .02, .5)),
        "omen_token_remove": mix((clip("item_gem_01.ogg", 1.1, .2), 0, .7), (clip("spell_02.ogg", 1.4, .5, reverse=True), 0, .4)),
        "rumor_countdown": mix((clip("item_gem_01.ogg", .75, .22), 0, 1), (bell(.48, .7), .05, .5)),
        "rumor_failure": mix((bell(.42, 1.1), 0, 1), (bell(.39, .9), .16, .6), (clip("spell_02.ogg", .7, .7), 0, .25)),
        "reckoning": mix((bell(.55, .7), 0, 1), (bell(.52, .8), .23, .6)),
        "ancient_power": mix((clip("spell_02.ogg", .55, 1.1), 0, .6), (bell(.35, 1.0), .08, .6)),
        "action_phase": mix((clip("book_01.ogg", 1.2, .35), 0, .7), (bell(1.0, .7), .08, .6)),
        "encounter_phase": mix((clip("book_03.ogg", 1.1, .45), 0, .7), (bell(.75, .8), .08, .6)),
        "mythos_phase": bell(.5, 1.3),
    }


def main():
    OUTPUT.mkdir(parents=True, exist_ok=True)
    for name, samples in cues().items():
        if name in TABLETOP_SOURCES:
            prepare_tabletop(name)
            continue
        samples = samples - samples.mean()
        attack = min(round(.008 * RATE), len(samples))
        tail = min(round(.12 * RATE), len(samples) // 3)
        samples[:attack] *= np.linspace(0, 1, attack)
        samples[-tail:] *= np.linspace(1, 0, tail) ** 2
        rms = np.sqrt(np.mean(samples ** 2))
        samples *= min(10 ** (-16 / 20) / max(rms, 1e-9), .92 / max(np.abs(samples)))
        samples[0] = samples[-1] = 0
        sf.write(OUTPUT / f"{name}.wav", samples, RATE, subtype="PCM_16")
        assert len(samples) * 2 < 1_000_000, "Exceeds Android SoundPool decoded buffer budget"
        print(f"{name}: {len(samples) / RATE:.2f}s, peak {max(np.abs(samples)):.3f}")
    prepare_tabletop("expedition_spawn")


if __name__ == "__main__":
    main()
