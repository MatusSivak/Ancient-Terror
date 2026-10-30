"""Rebuild travel, artifact, exhaustion and defeat cues; credits in OCT30_AUDIO.md."""
import soundfile as sf
from prepare_presentation_sounds import ROOT, clip, convert, mix, echo, write


def recording(name, seconds):
    data, rate = sf.read(ROOT / "source_assets/sounds/presentation" / name, always_2d=True)
    return convert(data, rate, seconds=seconds)


if __name__ == "__main__":
    # Recognizable transport calls, with quiet environmental texture underneath.
    write("travel_rail", mix((0, recording("train-whistle-0225.mp3", 2.1), .85),
          (.10, clip("impactMetal_light_002.ogg", .8, .15), .12)), -19)
    write("travel_sea", mix((0, recording("ship-horn-08.wav", 2.2), .8),
          (0, recording("wave_01.flac", 2.2), .18)), -19)
    write("travel_walk", mix(*[(i*.28, clip(f"footstep_grass_{n:03}.ogg", .9), gain)
          for i, (n, gain) in enumerate([(0,.9),(2,.8),(3,.85),(1,.65)])]), -16)
    # A resonant discovery flourish instead of the small gem pickup blip.
    bell = recording("../gameplay/bell_ding1.wav", 1.5)
    artifact = echo(mix((0, clip("spell_01.ogg", .8, 1.3), .40),
                   (.06, bell, .75), (.23, clip("item_gem_04.ogg", .8, .7), .20)), .16, .22)
    write("ui_card_reveal_artifact", artifact.copy(), -17)
    write("ui_card_open_artifact", artifact[:int(44100*.65)].copy(), -19)
    write("card_disable", mix((0, clip("bookClose.ogg", .85, .35), .75),
          (.04, clip("metalLatch.ogg", .8, .3), .50)), -16)
    write("monster_defeat_fly", clip("spell_fire_01.ogg", .8, .70), -17)
    write("monster_defeat", echo(mix((0, clip("spell_fire_04.ogg", .7, .85), .85),
          (.01, clip("impactMetal_heavy_002.ogg", .65, .6), .4)), .09, .18), -15)
