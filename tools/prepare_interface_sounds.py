"""Build short UI sounds from CC0 recordings; see sounds/INTERFACE_AUDIO.md.

Requires numpy and soundfile. Reuses the gameplay source processing helpers.
"""
import sys
sys.dont_write_bytecode = True
import numpy as np
import soundfile as sf
from prepare_gameplay_sounds import ROOT, RATE, OUTPUT, clip, bell, mix

SOURCE = ROOT / "source_assets/sounds/interface"
PAPER = ROOT / "source_assets/sounds/typewriter/paper.mp3"


def interface(name, speed=1.0, duration=.6):
    return clip(SOURCE / (name + ".ogg"), speed, duration)


def cues():
    return {
        "ui_button_click": interface("click_003", 1.0, .18),
        "ui_hud_click": interface("click_001", .85, .22),
        "ui_card_open": clip("book_01.ogg", 1.25, .5),
        "ui_card_close": clip("book_02.ogg", 1.4, .45),
        "ui_sheet_open": mix((clip("book_03.ogg", 1.25, .6), 0, 1),
                             (interface("maximize_001"), 0, .2)),
        "ui_sheet_close": mix((clip("book_04.ogg", 1.3, .55), 0, 1),
                              (interface("minimize_001"), 0, .2)),
        # The encounter table is a paper folio: page turn and paper slide, no synthetic layers.
        "ui_encounter_open": mix((clip("book_01.ogg", 1.0, .6), 0, 1),
                                 (clip(PAPER, 1.15, .45), .02, .35)),
        "ui_encounter_close": mix((clip("book_04.ogg", 1.15, .5), 0, 1),
                                  (clip(PAPER, 1.3, .35), 0, .3)),
        "ui_mystery_open": mix((clip("book_03.ogg", 1.1, .7), 0, 1), (bell(.85, .7), .06, .12)),
        "ui_mystery_close": clip("book_02.ogg", 1.1, .6),
        "ui_ancient_open": mix((clip("book_04.ogg", .85, .85), 0, 1), (bell(.4, .95), .04, .15)),
        "ui_ancient_close": mix((clip("book_02.ogg", .85, .7), 0, 1), (bell(.35, .65), 0, .08)),
        "ui_popup_open": interface("open_001", .95, .45),
        "ui_popup_close": interface("close_001", .95, .4),
    }


def main():
    for name, samples in cues().items():
        samples -= samples.mean()
        attack = min(round(.004 * RATE), len(samples))
        tail = min(round(.045 * RATE), len(samples) // 3)
        samples[:attack] *= np.linspace(0, 1, attack)
        samples[-tail:] *= np.linspace(1, 0, tail) ** 2
        rms = np.sqrt(np.mean(samples ** 2))
        samples *= min(10 ** (-20 / 20) / max(rms, 1e-9), .85 / max(np.abs(samples)))
        samples[0] = samples[-1] = 0
        sf.write(OUTPUT / (name + ".wav"), samples, RATE, subtype="PCM_16")
        assert len(samples) * 2 < 1_000_000
        print(f"{name}: {len(samples) / RATE:.2f}s")


if __name__ == "__main__":
    main()
