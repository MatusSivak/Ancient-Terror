"""Build the six action confirmation cues from the retained recordings."""
import soundfile as sf
from prepare_presentation_sounds import ROOT, convert, mix, write


def sample(path, duration, pitch=1):
    data, rate = sf.read(ROOT / 'source_assets/sounds' / path, always_2d=True)
    return convert(data, rate, pitch, duration)


if __name__ == '__main__':
    travel = mix((0, sample('action-confirm/cloth-heavy.wav', .38), .40),
                 (.08, sample('release/gravel.ogg', .45), 1))
    rest = mix((0, sample('action-confirm/cloth.wav', .52, .86), .65),
               (.12, sample('categories/242909__reitanna__relieved-sigh.mp3', .75), .55))
    ticket = mix((0, sample('release/paper_sound_-_1.mp3', .34), .55),
                 (.21, sample('action-confirm/metal-small1.wav', .35), .85))
    trade = sample('action-confirm/wood-small.wav', .30)
    write('ui_confirm_travel', travel, -19)
    write('ui_confirm_rest', rest, -20)
    write('ui_confirm_focus', sample('action-confirm/metal-ringing.wav', 1.05, .82), -22)
    write('ui_confirm_acquire_assets', sample('action-confirm/coin2.wav', .85), -20)
    write('ui_confirm_ticket', ticket, -19)
    write('ui_confirm_trade', mix((0, trade, .85), (.27, trade[::-1], .5)), -19)
