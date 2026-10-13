package sk.sivak.eldritchhorror.core.view.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class MusicFadeInAction extends TemporalAction {

    private final Music music;

    public MusicFadeInAction(Music music, float duration) {
        super(duration);
        this.music = music;
    }

    @Override
    protected void update(float v) {
        NewMusicBox.getInstance().setTrackVolume(music, v);

    }

    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    protected void end() {
        super.end();
    }
}
