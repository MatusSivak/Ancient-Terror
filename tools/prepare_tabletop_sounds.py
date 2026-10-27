"""Convert downloaded CC0 foley to game WAVs. No synthesis, layering or pitch shifts.

Requires numpy and soundfile. Sources and licenses: assets/sounds/TABLETOP_AUDIO.md.
The original ZIP downloads are retained so this also works on a clean checkout.
"""
from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "source_assets/sounds/tabletop"
OUTPUT = ROOT / "assets/sounds"
RATE = 44100

# Each output is one complete recording, with only surrounding silence removed.
SOURCES = {
    "ui_button_click": "card-place-1.ogg",
    "ui_hud_click": "chip-lay-1.ogg",
    "ui_sheet_open": "book_flip.5.ogg",
    "ui_sheet_close": "card-shove-2.ogg",
    "ui_popup_open": "card-slide-1.ogg",
    "ui_popup_close": "card-place-2.ogg",
    "ui_ancient_open": "book_flip.2.ogg",
    "ui_mystery_open": "book_flip.2.ogg",
    "ui_ancient_close": "book_flip.6.ogg",
    "ui_mystery_close": "book_flip.6.ogg",
    "investigator_spawn": "chip-lay-3.ogg",
    "clue_spawn": "chip-lay-2.ogg",
    "expedition_spawn": "card-shove-3.ogg",
    "health_gain": "chips-handle-1.ogg",
    "token_land": "chip-lay-1.ogg",
    "clue_gain": "chips-handle-3.ogg",
    "clue_land": "chip-lay-2.ogg",
    "focus_gain": "chips-handle-2.ogg",
    "focus_land": "chip-lay-3.ogg",
    "dice_sort": "dice-grab-1.ogg",
    "ui_card_open_spell": "spell_01.ogg",
    "ui_card_open_artifact": "item_gem_03.ogg",
    "ui_card_open_condition": "chain_02.ogg",
    "ui_card_open_asset": "handleCoins.ogg",
    "monster_step_1": "footstep00.ogg",
    "monster_step_2": "footstep01.ogg",
    "monster_step_3": "footstep02.ogg",
    "monster_step_4": "footstep03.ogg",
}


def recording(name):
    found = []
    for archive in sorted(SOURCE.glob("*.zip")):
        with ZipFile(archive) as pack:
            found += [(archive, entry) for entry in pack.namelist() if Path(entry).name == name]
    if len(found) != 1:
        raise ValueError(f"Expected exactly one {name} across {SOURCE}, found {len(found)}")
    archive, entry = found[0]
    with ZipFile(archive) as pack:
        data, rate = sf.read(BytesIO(pack.read(entry)), always_2d=True)
    data = data.mean(axis=1)
    active = np.flatnonzero(np.abs(data) > .001)
    if not len(active):
        raise ValueError(f"Silent recording: {name}")
    data = data[max(0, active[0] - round(.005 * rate)):min(len(data), active[-1] + round(.03 * rate))]
    if rate != RATE:
        data = np.interp(np.arange(round(len(data) * RATE / rate)) * rate / RATE,
                         np.arange(len(data)), data)
    return data


def prepare(name):
    data = recording(SOURCES[name])
    # Short edge fades prevent clicks; preserve the recording's natural attack and decay.
    edge = min(round(.003 * RATE), len(data) // 3)
    data[:edge] *= np.linspace(0, 1, edge)
    data[-edge:] *= np.linspace(1, 0, edge)
    rms = np.sqrt(np.mean(data ** 2))
    target = -23 if name.startswith("ui_") else -20
    data *= min(10 ** (target / 20) / max(rms, 1e-9), .80 / max(np.abs(data)))
    data[0] = data[-1] = 0
    assert len(data) * 2 < 1_000_000, "Android SoundPool decoded buffer limit"
    OUTPUT.mkdir(parents=True, exist_ok=True)
    sf.write(OUTPUT / (name + ".wav"), data, RATE, subtype="PCM_16")
    print(f"{name}: {SOURCES[name]}, {len(data) / RATE:.3f}s")


def main():
    for name in SOURCES:
        prepare(name)


if __name__ == "__main__":
    main()
