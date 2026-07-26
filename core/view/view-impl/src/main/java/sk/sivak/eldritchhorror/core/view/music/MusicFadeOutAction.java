package sk.sivak.eldritchhorror.core.view.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class MusicFadeOutAction extends TemporalAction {

    private final Music music;

    public MusicFadeOutAction(Music music, float duration) {
        super(duration);
        this.music = music;
    }

    @Override
    protected void update(float v) {
        if (NewMusicBox.getInstance().isEnabled()) {
            music.setVolume(1 - v);
        } else {
            music.setVolume(0);
        }

    }

    @Override
    protected void end() {
        super.end();
    }
}
