"""Trim downloaded CC0 recordings into spawn and card-category cues; credits in CATEGORY_AUDIO.md."""
from io import BytesIO
from zipfile import ZipFile
import numpy as np
import soundfile as sf
from prepare_presentation_sounds import ROOT, RATE, mix, write as write_wav

SOURCE = ROOT / "source_assets/sounds/categories"


def write(name, data, rms=-17, fade=.6):
    """Every trimmed recording ends with a smooth cosine fade (at most 40% of the clip), never a cut."""
    data = data[:int(2.4 * RATE)].copy()
    tail = int(min(fade, .4 * len(data) / RATE) * RATE)
    if tail: data[-tail:] *= .5 * (1 + np.cos(np.linspace(0, np.pi, tail)))
    write_wav(name, data, rms)


def recording(name, start=0, seconds=None, pitch=1):
    if name.startswith("portal/"):
        with ZipFile(SOURCE / "4-space-portal-sounds.zip") as pack:
            data, rate = sf.read(BytesIO(pack.read(name[len("portal/"):])), always_2d=True)
    else:
        data, rate = sf.read(SOURCE / name, always_2d=True)
    data = data.mean(axis=1)[int(start * rate):]
    data -= data.mean()
    data = np.interp(np.arange(int(len(data) * RATE / rate / pitch)) * rate * pitch / RATE,
                     np.arange(len(data)), data)
    if seconds: data = data[:int(seconds * RATE)]
    return data / max(.001, np.max(np.abs(data)))


if __name__ == "__main__":
    # Spawn landings replace the outputs of prepare_landing_sounds / prepare_presentation_sounds.
    write("map_investigator_impact", recording("jumping-on-wooden-pontoon-1-s1841.mp3", .52, .55, .8), -14)
    write("gate_resonance", recording("portal/1.mp3", .45, 2.4), -16, .9)
    write("clue_spawn", mix((0, recording("449955__breviceps__wooden-thud.mp3", 0, .45, .9), .8),
          (0, recording("556387__zachrau__moving-piece-on-a-boardgame.mp3", .03, .3), .6)), -17)

    # Token pickups replace prepare_gameplay_sounds / generate_token_sounds outputs.
    # Dice dragged back into order after a throw: a quiet wooden drag with no rattle.
    write("dice_sort", recording("403653__lamamakesmusic__wood-sliding-various-objects_excerpt-0-4s.flac", .7, .9), -27)
    write("card_discard", recording("272028__aldenroth2__ripping-paper.mp3", .3, 1.4), -18, .3)
    write("clue_gain", recording("85703__tmkappelt__pencil-scribble.mp3", .6, .7), -20)
    write("focus_gain", recording("696440__itinerantmonk108__aum-in-concrete-pipe.mp3", .1, 2.4), -25, 1.3)
    write("ticket_gain", recording("447927__breviceps__hole-punch.mp3", .8, .45), -18)
    write("health_gain", recording("273150__michorvath__heart-beating.mp3", .25, .8), -16)
    write("sanity_gain", recording("242909__reitanna__relieved-sigh.mp3"), -19)

    # HUD landings replace prepare_tabletop_sounds / prepare_gameplay_sounds outputs.
    write("token_land", recording("540369__za-games__poker-chip-drop.mp3", 0, .5), -18)
    write("clue_land", mix((0, recording("546119__el-boss__piece-placement.mp3"), .8),
          (0, recording("449955__breviceps__wooden-thud.mp3", 0, .25, 1.1), .35)), -18)
    write("focus_land", recording("560698__el-boss__puzzle-piece-placed.mp3"), -18)
    write("ticket_land", recording("502658__eggdeng__place-card.mp3", .95, .35), -18)

    # Investigator detention and monster defeat replace generate_detained_sound / prepare_oct30_sounds.
    write("investigator_detained", recording("80449__turtlelg__jaildoorclose.mp3", 1.15, 2.3), -15)
    write("monster_defeat_fly", recording("264777__shinplaster__swoosh.mp3", 4.0, 1.1), -17)
    write("monster_defeat", mix((0, recording("641046__magnuswaker__gore-impact.mp3"), .8),
          (.03, recording("414296__thefitzyg__gore-splat.mp3", 1.0, 1.2), .9)), -14)

    # Doom and omen track zoom; replaces generate_omen_zoom_sounds.
    write("track_zoom_in", recording("683101__florianreichelt__quick-woosh.mp3", .15, .4), -22)
    write("track_zoom_out", recording("60013__qubodup__whoosh.mp3", 0, .43, .85), -22)

    # Monster travel replaces prepare_presentation_sounds' stone steps: lowered mud squelches with a wet layer.
    steps = ((2.42, 5.92), (3.42, 8.56), (4.68, 1.30), (7.02, 1.56))
    for i, (step, squish) in enumerate(steps, 1):
        write(f"monster_prowl_{i}", mix((0, recording("683520__eqavox__feet-squelch.mp3", step, .5, .7), .85),
              (.01, recording("834075__federicoy__wet-slimy-footsteps_excerpt-0-10s.flac", squish, .32, .8), .4)), -18)
    write("monster_sheet_growl", recording("763026__celldroid__bear-angry-growl.mp3", 2.45, 2.2, .9), -17)
    write("epic_monster_sheet_roar", recording("263115__lauramellis__tiger-roar.mp3", .95, 2.4, .72), -15)

    write("ui_card_reveal_spell_incantation", recording("146143__cycro__demon-chant-latin.mp3", 1.15, 2.3), -18)
    write("ui_card_reveal_spell_ritual", mix((0, recording("gong-strong-1-s1483.mp3", .05, 2.4), .7),
          (.05, recording("98845__djgriffin__ganze-gompa-monks_excerpt-8-14s.flac", 1.3, 2.35), .8)), -17)

    write("ui_card_reveal_asset_weapon", recording("829499__harrisonlace__equip-weapon-metallic.mp3", 1.28, .8), -18)
    write("ui_card_reveal_asset_tome", recording("great-page-that-turns-1-s0362.mp3", 0, 1.6), -18)
    write("ui_card_reveal_asset_ally", recording("436107__drfortyseven__ahem-1.mp3"), -18)
    write("ui_card_reveal_asset_service", recording("counter-bell-3-s2531.mp3", .15, 1.6), -20)
    write("ui_card_reveal_asset_trinket", recording("chimes-dream-8-s2086.mp3", .1, 2.1), -20, .9)

    write("ui_card_reveal_condition_boon", recording("church-bell-s0135.mp3", 0, 2.4), -19, 1.2)
    write("ui_card_reveal_condition_madness", recording("delirious-laughter-3-s2093.mp3", .15, 2.3), -18)
    write("ui_card_reveal_condition_injury", mix((0, recording("punch-5-s2460.mp3", .08, .4), .8),
          (.04, recording("bones-cracking-2-s1408.mp3", .18, .5), .6)), -17)
    write("ui_card_reveal_condition_illness", recording("man-cough-5-s2183.mp3", .08, 1.4), -18)
    write("ui_card_reveal_condition_deal", recording("pencil-signature-2-s3237.mp3", .2, 1.2), -19)
    write("ui_card_reveal_condition_bane", recording("thunder-2-s3113.mp3", 3.95, 1.15), -16)
