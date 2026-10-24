package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TrackEventSoundsTest {
    private Application previousApp;
    private boolean previousFastForward;
    private Object previousAssets;
    private CustomAssetManager assets;
    private Field assetsField;
    private boolean loaded = true;
    private final List<String> played = new ArrayList<>();
    private final List<Float> times = new ArrayList<>();
    private float time;

    @Before public void setup() throws Exception {
        previousApp = Gdx.app;
        Gdx.app = null;
        previousFastForward = FastForwardAction.isOn();
        FastForwardAction.turnOff();
        assets = new CustomAssetManager() {
            @Override public synchronized boolean isLoaded(String path, Class type) { return loaded; }
            @Override public synchronized <T> T get(String path, Class<T> type) {
                return type.cast(Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class}, (proxy, method, args) -> {
                    if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                    if (method.getName().equals("equals")) return proxy == args[0];
                    if (method.getName().equals("play")) {
                        played.add(path);
                        times.add(time);
                        assertTrue("Track cues must use the louder mix", (Float) args[0] >= .8f);
                        return 1L;
                    }
                    throw new AssertionError(method.getName());
                }));
            }
            @Override public synchronized <T> void load(String path, Class<T> type) {
                fail("Playback must not decode on the render thread: " + path);
            }
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

    @Test public void doomAdvanceWaitsForAnimationAndTollsOncePerStep() {
        Actor clock = new Actor();
        clock.addAction(TrackEventSounds.doomSteps(7, 4, 1.5f));
        assertTrue(played.isEmpty());
        runFor(clock, 4f);
        String cue = GameplaySounds.Cue.DOOM_ADVANCE.path;
        assertEquals(Arrays.asList(cue, cue, cue), played);
        assertEquals(1.5f, times.get(1) - times.get(0), .1f);
        assertEquals(1.5f, times.get(2) - times.get(1), .1f);
        assertEquals(0, clock.getActions().size);
    }

    @Test public void genuineRetreatFromMidnightIsAudible() {
        Actor clock = new Actor();
        clock.addAction(TrackEventSounds.doomSteps(0, 2, 1.5f));
        runFor(clock, 3f);
        assertEquals(Arrays.asList(GameplaySounds.Cue.DOOM_RETREAT.path, GameplaySounds.Cue.DOOM_RETREAT.path), played);
    }

    @Test public void zeroChangeAndClampedAdvanceStaySilent() {
        Actor clock = new Actor();
        clock.addAction(TrackEventSounds.doomSteps(5, 5, 1.5f));
        clock.addAction(TrackEventSounds.doomSteps(0, -2, 1.5f));
        runFor(clock, 1f);
        assertTrue(played.isEmpty());
        assertEquals(0, clock.getActions().size);
    }

    @Test public void turningOnFastForwardSilencesAlreadyQueuedSteps() {
        Actor clock = new Actor();
        clock.addAction(TrackEventSounds.doomSteps(5, 2, 1.5f));
        runFor(clock, .2f);
        assertEquals(1, played.size());
        FastForwardAction.turnOn();
        runFor(clock, 4f);
        assertEquals(1, played.size());
        assertEquals(0, clock.getActions().size);
    }

    @Test public void missingSoundsDoNotHoldUpTheClock() {
        loaded = false;
        Actor clock = new Actor();
        clock.addAction(TrackEventSounds.doomSteps(2, 1, 1.5f));
        runFor(clock, 2f);
        assertTrue(played.isEmpty());
        assertEquals(0, clock.getActions().size);
    }

    @Test public void omenUsesPositionChangesIncludingTheTwoBlueSpaces() {
        TrackEventSounds.omenChanged(OmenId.EAST, OmenId.EAST);
        assertTrue(played.isEmpty());
        TrackEventSounds.omenChanged(OmenId.EAST, OmenId.WEST);
        assertEquals(Arrays.asList(GameplaySounds.Cue.OMEN_CHANGE.path), played);
        FastForwardAction.turnOn();
        TrackEventSounds.omenChanged(OmenId.WEST, OmenId.NORTH);
        assertEquals(1, played.size());
    }

    private void runFor(Actor actor, float seconds) {
        for (int frame = 0; frame < Math.round(seconds / .05f); frame++) {
            time += .05f;
            actor.act(.05f);
        }
    }
}
