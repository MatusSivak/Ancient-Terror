package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.view.utils.GameplaySounds.Cue.*;

public class GameplaySoundsTest {
    private Application previousApp;
    private boolean previousFastForward;
    private Object previousAssets;
    private Field assetsField;
    private CustomAssetManager assets;
    private boolean loaded;
    private final List<String> events = new ArrayList<>();
    private final Set<String> queuedSounds = new HashSet<>();

    @Before public void setup() throws Exception {
        previousApp = Gdx.app;
        Gdx.app = null;
        previousFastForward = FastForwardAction.isOn();
        FastForwardAction.turnOff();
        loaded = true;
        assets = new CustomAssetManager() {
            @Override public synchronized boolean isLoaded(String name, Class type) { return loaded; }
            @Override public synchronized <T> T get(String name, Class<T> type) {
                return type.cast(Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                            if (method.getName().equals("equals")) return proxy == args[0];
                            if (method.getName().equals("play")) {
                                events.add(name);
                                assertTrue((Float) args[0] > 0 && (Float) args[0] <= 1);
                                return 1L;
                            }
                            throw new AssertionError(method.getName());
                        }));
            }
            @Override public synchronized <T> void load(String name, Class<T> type) {
                if (type == Sound.class) queuedSounds.add(name);
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

    @Test public void queuedCueWaitsForCameraAndPrecedesAnimation() {
        PublishSubject<Void> camera = PublishSubject.create();
        Completable spawn = GameplaySounds.withCue(MONSTER_SPAWN,
                Completable.fromAction(() -> events.add("spawn")));
        assertTrue(events.isEmpty());
        camera.toCompletable().andThen(spawn).subscribe(() -> events.add("done"));
        assertTrue(events.isEmpty());
        camera.onCompleted();
        assertEquals(Arrays.asList(MONSTER_SPAWN.path, "spawn", "done"), events);
    }

    @Test public void fastForwardIsCheckedWhenQueuedEventActuallyRuns() {
        Completable event = GameplaySounds.withCue(CLUE_SPAWN,
                Completable.fromAction(() -> events.add("spawn")));
        FastForwardAction.turnOn();
        event.subscribe();
        assertEquals(Arrays.asList("spawn"), events);
        FastForwardAction.turnOff();
        event.subscribe();
        assertEquals(Arrays.asList("spawn", CLUE_SPAWN.path, "spawn"), events);
    }

    @Test public void missingAudioDoesNotLoadSynchronouslyOrBlockCompletion() {
        loaded = false;
        GameplaySounds.withCue(GATE_OPEN, Completable.complete()).subscribe(() -> events.add("done"));
        assertEquals(Arrays.asList("done"), events);
        assertTrue(queuedSounds.isEmpty());
    }

    @Test public void aFailedCameraMoveDoesNotAnnounceASpawn() {
        IllegalStateException failure = new IllegalStateException("camera unavailable");
        Completable.error(failure).andThen(GameplaySounds.withCue(CLUE_SPAWN,
                Completable.fromAction(() -> events.add("spawn"))))
                .subscribe(() -> fail("must propagate error"), error -> assertSame(failure, error));
        assertTrue(events.isEmpty());
    }

    @Test public void everyEventCueIsQueuedDuringStartup() throws Exception {
        Method queue = CustomAssetManager.class.getDeclaredMethod("queueTextures1");
        queue.setAccessible(true);
        queue.invoke(assets);
        for (GameplaySounds.Cue cue : GameplaySounds.Cue.values()) {
            assertTrue("Not preloaded: " + cue.path, queuedSounds.contains(cue.path));
        }
    }

    @Test public void packagedCuesAreShortMonoPcmWithinSoundPoolBudget() throws Exception {
        Path root = Paths.get("").toAbsolutePath();
        while (root != null && !Files.isDirectory(root.resolve("assets/sounds"))) root = root.getParent();
        assertNotNull("Cannot find game assets", root);
        for (GameplaySounds.Cue cue : GameplaySounds.Cue.values()) {
            Path path = root.resolve("assets").resolve(cue.path);
            assertTrue("Missing cue: " + path, Files.isRegularFile(path));
            AudioFileFormat wav = AudioSystem.getAudioFileFormat(path.toFile());
            assertEquals(cue.path, 1, wav.getFormat().getChannels());
            assertEquals(cue.path, 16, wav.getFormat().getSampleSizeInBits());
            assertEquals(cue.path, 44100f, wav.getFormat().getSampleRate(), 0f);
            assertTrue(cue.path, wav.getFrameLength() > 0);
            assertTrue(cue.path, wav.getFrameLength() * wav.getFormat().getFrameSize() < 1_000_000);
        }
    }
}
