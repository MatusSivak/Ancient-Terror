package sk.sivak.eldritchhorror.core.view.music;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import java.lang.reflect.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class MusicVolumeTest {
    private Application previousApp;
    private Object previousInstance;
    private Field instance;
    private final Map<String, Object> saved = new HashMap<>();

    @Before public void setup() throws Exception {
        previousApp = Gdx.app;
        instance = NewMusicBox.class.getDeclaredField("instance");
        instance.setAccessible(true);
        previousInstance = instance.get(null);
        instance.set(null, null);
        Preferences preferences = (Preferences) Proxy.newProxyInstance(Preferences.class.getClassLoader(),
                new Class[]{Preferences.class}, (p, method, args) -> {
                    String name = method.getName();
                    if (name.startsWith("get")) return saved.containsKey(args[0]) ? saved.get(args[0]) : args[1];
                    if (name.startsWith("put")) { saved.put((String) args[0], args[1]); return p; }
                    return null;
                });
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(),
                new Class[]{Application.class}, (p, method, args) -> preferences);
    }

    @After public void cleanup() throws Exception {
        Gdx.app = previousApp;
        instance.set(null, previousInstance);
    }

    @Test public void oldMutePreferenceMigratesAndVolumeSurvivesRestart() {
        saved.put("music_disabled", true);
        NewMusicBox box = new NewMusicBox();
        assertEquals(0f, box.getVolume(), 0f);
        box.setVolume(0.37f); // Works before any track has started.
        assertEquals(0.37f, new NewMusicBox().getVolume(), 0f);
        assertEquals(false, saved.get("music_disabled"));
    }

    @Test public void volumeChangesRescaleBothFadingTracksAndUnmuteAtCurrentFadePosition() {
        NewMusicBox box = NewMusicBox.getInstance();
        float[] incoming = {0f}, outgoing = {0f};
        MusicFadeInAction fadeIn = new MusicFadeInAction(music(incoming), 1f);
        MusicFadeOutAction fadeOut = new MusicFadeOutAction(music(outgoing), 1f);
        fadeIn.act(0.25f);
        fadeOut.act(0.25f);
        box.setVolume(0.4f);
        assertEquals(0.1f, incoming[0], 0.001f);
        assertEquals(0.3f, outgoing[0], 0.001f);
        box.setVolume(0f);
        fadeIn.act(0.25f);
        fadeOut.act(0.25f);
        assertEquals(0f, incoming[0], 0f);
        assertEquals(0f, outgoing[0], 0f);
        box.setVolume(0.6f);
        assertEquals(0.3f, incoming[0], 0.001f);
        assertEquals(0.3f, outgoing[0], 0.001f);
        fadeIn.act(0.5f);
        fadeOut.act(0.5f);
        assertEquals(0.6f, incoming[0], 0.001f);
        assertEquals(0f, outgoing[0], 0f);
    }

    private Music music(float[] volume) {
        return (Music) Proxy.newProxyInstance(Music.class.getClassLoader(), new Class[]{Music.class},
                (p, method, args) -> {
                    if (method.getName().equals("hashCode")) return System.identityHashCode(p);
                    if (method.getName().equals("equals")) return p == args[0];
                    if (method.getName().equals("setVolume")) volume[0] = (Float) args[0];
                    return null;
                });
    }
}
