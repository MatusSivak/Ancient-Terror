package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import org.junit.*;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static org.junit.Assert.*;

public class TrackZoomSoundsTest {
    private Application previousApp;
    private Object previousAssets;
    private Field assetsField;
    private CustomAssetManager assets;
    private boolean previousFastForward;
    private int plays;

    @Before public void setup() throws Exception {
        previousApp = Gdx.app;
        Gdx.app = null;
        previousFastForward = FastForwardAction.isOn();
        Sound sound = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                    if (method.getName().equals("equals")) return proxy == args[0];
                    if (method.getName().equals("play")) { plays++; return 1L; }
                    throw new AssertionError(method.getName());
                });
        assets = new CustomAssetManager() {
            @Override public synchronized boolean isLoaded(String name, Class type) { return true; }
            @Override public synchronized <T> T get(String name, Class<T> type) { return type.cast(sound); }
        };
        assetsField = CustomAssetManager.class.getDeclaredField("instance");
        assetsField.setAccessible(true);
        previousAssets = assetsField.get(null);
        assetsField.set(null, assets);
    }
    @After public void cleanup() throws Exception {
        assetsField.set(null, previousAssets);
        assets.dispose();
        Gdx.app = previousApp;
        if (previousFastForward) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
    }

    @Test public void manuallyOpeningOmenStillPlaysWhenFastForwardIsEnabled() {
        FastForwardAction.turnOn();
        Actor omen = new Actor(); omen.setSize(100, 100);
        new MaxMinComponent(omen, BigActorsManager.BigActorKey.OMEN_TRACK)
                .withSounds(CustomAssetManager.OMEN_MAXIMIZE_SOUND, CustomAssetManager.OMEN_MINIMIZE_SOUND)
                .maximizeOrMinimize();
        assertEquals(1, plays);
    }
    @Test public void acceleratedAutomaticZoomStaysSilent() {
        FastForwardAction.turnOn();
        TrackZoomSounds.play(CustomAssetManager.OMEN_MAXIMIZE_SOUND);
        assertEquals(0, plays);
        FastForwardAction.turnOff();
        TrackZoomSounds.play(CustomAssetManager.OMEN_MAXIMIZE_SOUND);
        assertEquals(1, plays);
    }
}
