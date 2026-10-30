"""Build short, distinct game cues from retained CC0 recordings. See PRESENTATION_AUDIO.md."""
from io import BytesIO
from pathlib import Path
from zipfile import ZipFile
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
RATE = 44100

def clip(name, pitch=1, seconds=None):
    matches = []
    for archive in (ROOT / "source_assets/sounds").rglob("*.zip"):
        with ZipFile(archive) as pack:
            for entry in pack.namelist():
                if Path(entry).name == name:
                    matches.append(pack.read(entry))
    if len(matches) != 1:
        raise ValueError((name, len(matches)))
    data, rate = sf.read(BytesIO(matches[0]), always_2d=True)
    return convert(data, rate, pitch, seconds)

def convert(data, rate, pitch=1, seconds=None):
    data = data.mean(axis=1)
    active = np.flatnonzero(np.abs(data) > .001)
    if len(active): data = data[max(0, active[0]-int(.005*rate)):active[-1]+1]
    data = np.interp(np.arange(int(len(data)*RATE/rate/pitch))*rate*pitch/RATE, np.arange(len(data)), data)
    if seconds: data = data[:int(seconds*RATE)]
    data /= max(.001, np.max(np.abs(data)))
    return data

def mix(*layers):
    result = np.zeros(max(int(t*RATE)+len(data) for t, data, gain in layers))
    for t, data, gain in layers:
        start = int(t*RATE); result[start:start+len(data)] += data*gain
    return result

def echo(data, delay=.11, gain=.20):
    return mix((0, data, 1), (delay, data, gain), (delay*2, data, gain*.5))

def write(name, data, rms=-17):
    data = data[:int(2.4*RATE)]
    edge = min(int(.006*RATE),len(data)//3)
    tail = min(int(.09*RATE),len(data)//3)
    data[:edge] *= np.linspace(0,1,edge); data[-tail:] *= np.linspace(1,0,tail)
    data *= 10**(rms/20)/max(1e-6,np.sqrt(np.mean(data**2)))
    data = .94*np.tanh(data/.94)
    data[0]=data[-1]=0
    sf.write(ROOT / "assets/sounds" / (name+".wav"),data,RATE,subtype="PCM_16")
    print(f"{name}: {len(data)/RATE:.2f}s, peak={max(abs(data)):.3f}")

if __name__ == "__main__":
    write("gate_resonance", echo(mix((0,clip("impactMetal_heavy_002.ogg",.65),.9),
          (.015,clip("stones_03.ogg",.7,.55),.4)),.14,.28), -13)
    write("ui_card_reveal_spell", echo(mix((0,clip("spell_02.ogg",.9,1.1),.8),
          (.08,clip("bookOpen.ogg"),.25))), -16)
    write("ui_card_reveal_artifact", echo(mix((0,clip("item_gem_01.ogg",.85,1.0),.8),
          (.10,clip("item_gem_04.ogg",1.2,.6),.25)),.13,.22), -16)
    write("ui_card_reveal_condition", echo(mix((0,clip("chain_03.ogg",.75,.8),.65),
          (.04,clip("creature_misc_03.ogg",.7,.65),.2)),.10,.16), -18)
    write("ui_card_reveal_asset", mix((0,clip("handleSmallLeather2.ogg"),.55),
          (.07,clip("metalLatch.ogg",.95),.65)), -17)
    write("ui_action_click", mix((0,clip("chip-lay-3.ogg"),.8),
          (.025,clip("metalClick.ogg",.85,.18),.35)), -18)
    for i in range(1,5):
        write(f"monster_prowl_{i}", mix((0,clip(f"stones_0{i}.ogg",.65,.36),.8),
              (.015,clip(f"creature_monster_0{i}.ogg",.7,.33),.24)), -18)
    from prepare_release_sounds import prepare as prepare_release
    prepare_release()
    clack = clip("impactMetal_light_002.ogg",.85,.13)
    write("travel_rail", mix(*[(t,clack,g) for t,g in [(0,.8),(.11,.55),(.31,.75),(.42,.5),(.62,.55),(.73,.4)]]), -17)
    water, rate = sf.read(ROOT / "source_assets/sounds/presentation/wave_01.flac", always_2d=True)
    write("travel_sea", mix((0,convert(water,rate,1,1.7),.8),(.05,clip("creak2.ogg",.85,.7),.25)), -17)
