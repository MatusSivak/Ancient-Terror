"""November report audio. Run last after legacy audio generators; credits: NOV3_AUDIO.md.

Uses retained CC0 recordings, numpy and soundfile. Outputs mono PCM16, 44.1 kHz.
"""
from pathlib import Path
from io import BytesIO
from zipfile import ZipFile
import numpy as np
import soundfile as sf
from prepare_presentation_sounds import ROOT, RATE, mix, echo
from prepare_category_sounds import recording as category

SOURCE = ROOT / 'source_assets/sounds/nov3'


def recording(name, start=0, duration=None):
    if name.startswith('swish-'):
        with ZipFile(SOURCE / 'swishes.zip') as archive:
            data, rate = sf.read(BytesIO(archive.read('swishes/' + name)), always_2d=True)
    else:
        data, rate = sf.read(SOURCE / name, always_2d=True)
    data = data.mean(axis=1)
    data = data[round(start * rate):round((start + duration) * rate) if duration else None]
    active = np.flatnonzero(np.abs(data) > .001)
    if not len(active):
        raise ValueError('Silent source: ' + name)
    data = data[max(0, active[0] - round(.008 * rate)):active[-1] + 1]
    data = np.interp(np.arange(round(len(data) * RATE / rate)) * rate / RATE, np.arange(len(data)), data)
    return data / max(.001, np.max(np.abs(data)))


def write(name, data, target=-19, fade=.15):
    data = data[:round(3.2 * RATE)].copy()
    data -= data.mean()
    attack = min(round(.008 * RATE), len(data) // 3)
    tail = min(round(fade * RATE), len(data) - attack)
    data[:attack] *= np.linspace(0, 1, attack)
    data[-tail:] *= .5 * (1 + np.cos(np.linspace(0, np.pi, tail)))
    data *= 10 ** (target / 20) / max(1e-6, np.sqrt(np.mean(data ** 2)))
    data = .94 * np.tanh(data / .94)
    data[0] = data[-1] = 0
    assert len(data) * 2 < 1_000_000
    sf.write(ROOT / 'assets/sounds' / (name + '.wav'), data, RATE, subtype='PCM_16')
    print(f'{name}: {len(data)/RATE:.2f}s; RMS {20*np.log10(np.sqrt(np.mean(data**2))):.1f} dBFS; peak {max(abs(data)):.3f}')


def main():
    write('focus_gain', recording('om.mp3', 2.0, 3.2), -19, 2.1)
    write('health_gain', recording('magical_1.ogg'), -19, .45)
    write('sanity_gain', recording('magical_3.ogg'), -19, .5)
    write('token_land', category('560698__el-boss__puzzle-piece-placed.mp3'), -17, .08)
    write('omen_change', recording('magical_4.ogg'), -18, .8)
    write('ui_reserve_open', recording('cash_drawer.mp3', .24, 1.20), -18, .12)
    swish = recording('swish-7.wav')
    write('card_collect', mix((0, swish, 1), (.24, swish, .65), (.48, swish, .35)), -24, .2)
    write('monster_discard', echo(recording('swish-10.wav'), .13, .28), -17, .4)
    write('rumor_spawn', mix((0, recording('swish-10.wav'), .4),
          (.08, category('thunder-2-s3113.mp3', 3.95, 1.15), .65)), -18, .5)
    # A stronger wood contact and a clear low gate impact remain audible on phone speakers.
    write('clue_spawn', category('449955__breviceps__wooden-thud.mp3', 0, .45), -11, .1)
    write('gate_resonance', mix((0, category('portal/1.mp3', .45, 2.4), .65),
          (0, category('jumping-on-wooden-pontoon-1-s1841.mp3', .52, .55, .8), 1)), -11, .7)


if __name__ == '__main__':
    main()
