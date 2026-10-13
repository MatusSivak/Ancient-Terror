package sk.sivak.eldritchhorror.core.view.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class NewMusicBox {

    public static final String TRACK_HORROR_GAME_INTRO = "music/horror_game_intro.mp3";
    public static final String PREPARE_BOARD_OR_TYPEWRITER = "music/prepare_board.mp3";
    public static final String TRACK_CITY_OF_DREAD = "music/City_of_Dread.mp3";
    public static final String TRACK_COMBAT = "music/combat.mp3";
    private static final float FADE_IN_DURATION = 3;
    private static final float FADE_OUT_DURATION = 1.5f;

    private static NewMusicBox instance;
    private float volume;
    private final Map<Music, Float> trackVolumes = new HashMap<>();

    private Stack<String> musicInfoStack;
    private FastForwardAction<?> ongoingFadeAction;

    private final Map<String, Music> preloadedMusicMap;

    public NewMusicBox() {
        preloadedMusicMap = new HashMap<>();
        musicInfoStack = new Stack<>();

        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        volume = Math.max(0f, Math.min(1f, preferences.getFloat("music_volume",
                preferences.getBoolean("music_disabled", false) ? 0f : 1f)));

    }

    public static NewMusicBox getInstance() {
        if (instance == null) {
            instance = new NewMusicBox();
        }
        return instance;
    }

    public void setEnabled(boolean enabled) {
        setVolume(enabled ? 1f : 0f);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
        for (Map.Entry<Music, Float> track : trackVolumes.entrySet()) {
            track.getKey().setVolume(track.getValue() * this.volume);
        }
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        preferences.putFloat("music_volume", this.volume);
        preferences.putBoolean("music_disabled", this.volume == 0f);
        preferences.flush();
    }

    void setTrackVolume(Music music, float fadeVolume) {
        trackVolumes.put(music, fadeVolume);
        music.setVolume(fadeVolume * volume);
    }

    public void changeMusic(String newTrack) {
        if (!musicInfoStack.isEmpty() && musicInfoStack.peek().equals(newTrack)) {
            return;
        }

        resetOngoingFadeAction();
        Music newMusic = findMusic(newTrack);
        if (!musicInfoStack.isEmpty()) {
            ongoingFadeAction = new FastForwardAction<>(Actions.parallel(
                    new MusicFadeOutAction(findMusic(musicInfoStack.peek()), FADE_OUT_DURATION),
                    new MusicFadeInAction(newMusic, FADE_IN_DURATION)
            ));
            InfoStage.addActionToInfoStage(ongoingFadeAction);
        } else {
            ongoingFadeAction = new FastForwardAction<>(
                    new MusicFadeInAction(newMusic, FADE_IN_DURATION));
            InfoStage.addActionToInfoStage(ongoingFadeAction);
        }

        musicInfoStack.push(newTrack);

        cleanUp();
    }

    private void resetOngoingFadeAction() {
        if (ongoingFadeAction == null) {
            return;
        }
        if (ongoingFadeAction.getActor() != null) {
            ongoingFadeAction.getActor().removeAction(ongoingFadeAction);
        }
        InfoStage.getFastForwardButton().unregisterAction(ongoingFadeAction);
        ongoingFadeAction.reset();
        if (!musicInfoStack.isEmpty()) {
            setTrackVolume(findMusic(musicInfoStack.peek()), 1f);
        }
        if (musicInfoStack.size() >= 2) {
            setTrackVolume(findMusic(musicInfoStack.get(musicInfoStack.size() - 2)), 0f);
        }
        ongoingFadeAction = null;
    }

    private void cleanUp() {
        if (musicInfoStack.size() > 10) {
            for (int i = 0; i < 5; i++) {
                musicInfoStack.remove(0);
            }
        }
    }

    private Music findMusic(String trackName) {
        if (preloadedMusicMap.get(trackName) != null) {
            return preloadedMusicMap.get(trackName);
        }

        Music newMusic = Gdx.audio.newMusic(Gdx.files.internal(trackName));
        preloadedMusicMap.put(trackName, newMusic);
        setTrackVolume(newMusic, 0f);
        newMusic.setLooping(true);
        newMusic.play();

        return preloadedMusicMap.get(trackName);
    }

    public void backToPreviousTrack() {
        if (musicInfoStack.size() < 2) {
            changeMusic(TRACK_HORROR_GAME_INTRO);
            return;
        }
        String newTrack = musicInfoStack.get(musicInfoStack.size() - 2);

        changeMusic(newTrack);

        musicInfoStack.remove(musicInfoStack.size()-2);
        musicInfoStack.remove(musicInfoStack.size()-2);
    }

    public void reset() {
        resetOngoingFadeAction();
        if (!musicInfoStack.isEmpty()) {
            setTrackVolume(findMusic(musicInfoStack.peek()), 0f);
        }
        musicInfoStack.clear();
        changeMusic(TRACK_HORROR_GAME_INTRO);
    }

    public boolean isEnabled() {
        return volume > 0f;
    }
}
