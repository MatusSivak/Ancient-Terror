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
import sk.sivak.eldritchhorror.core.view.action.TokenSounds;

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
                if (type == Sound.class) assertTrue("Queued twice: " + name, queuedSounds.add(name));
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
        for (String path : cuePaths()) {
            assertTrue("Not preloaded: " + path, queuedSounds.contains(path));
        }
    }

    @Test public void packagedCuesAreShortMonoPcmWithinSoundPoolBudget() throws Exception {
        Path root = Paths.get("").toAbsolutePath();
        while (root != null && !Files.isDirectory(root.resolve("assets/sounds"))) root = root.getParent();
        assertNotNull("Cannot find game assets", root);
        for (String cue : cuePaths()) {
            Path path = root.resolve("assets").resolve(cue);
            assertTrue("Missing cue: " + path, Files.isRegularFile(path));
            AudioFileFormat wav = AudioSystem.getAudioFileFormat(path.toFile());
            assertEquals(cue, 1, wav.getFormat().getChannels());
            assertEquals(cue, 16, wav.getFormat().getSampleSizeInBits());
            assertEquals(cue, 44100f, wav.getFormat().getSampleRate(), 0f);
            assertTrue(cue, wav.getFrameLength() > 0);
            assertTrue(cue, wav.getFrameLength() * wav.getFormat().getFrameSize() < 1_000_000);
        }
    }

    private Set<String> cuePaths() {
        Set<String> paths = new HashSet<>();
        for (GameplaySounds.Cue cue : GameplaySounds.Cue.values()) paths.add(cue.path);
        for (UiSounds.Cue cue : UiSounds.Cue.values()) paths.add(cue.path);
        for (TokenSounds.Cue cue : TokenSounds.Cue.values()) paths.add(cue.path);
        paths.addAll(Arrays.asList(sk.sivak.eldritchhorror.core.view.map.monster.MonsterFootsteps.SOUNDS));
        return paths;
    }

    @Test public void travelUsesTheSelectedTransportCue() {
        assertEquals(TRAVEL_SEA, GameplaySounds.travelCue(sk.sivak.eldritchhorror.core.constants.location.PathType.SHIP));
        assertEquals(TRAVEL_RAIL, GameplaySounds.travelCue(sk.sivak.eldritchhorror.core.constants.location.PathType.TRAIN));
        assertEquals(TRAVEL_WALK, GameplaySounds.travelCue(sk.sivak.eldritchhorror.core.constants.location.PathType.WALK));
        assertNotEquals(TRAVEL_SEA.path, TRAVEL_RAIL.path);
        assertNotEquals(TRAVEL_RAIL.path, TRAVEL_WALK.path);
    }

    @Test public void healthAndSanityHaveTheirOwnPickups() {
        assertNotEquals(TokenSounds.Cue.HEALTH_GAIN.path, TokenSounds.Cue.SANITY_GAIN.path);
    }

    @Test public void pickupsAndHudArrivalsHaveDistinctMatchingSounds() {
        TokenSounds.Cue[] pickups = {TokenSounds.Cue.HEALTH_GAIN, TokenSounds.Cue.SANITY_GAIN, TokenSounds.Cue.CLUE_GAIN,
                TokenSounds.Cue.FOCUS_GAIN, TokenSounds.Cue.TICKET_GAIN};
        TokenSounds.Cue[] arrivals = {TokenSounds.Cue.TOKEN_LAND, TokenSounds.Cue.TOKEN_LAND, TokenSounds.Cue.CLUE_LAND,
                TokenSounds.Cue.FOCUS_LAND, TokenSounds.Cue.TICKET_LAND};
        for (int i = 0; i < pickups.length; i++) {
            events.clear();
            TokenSounds.playGain(pickups[i], 0);
            assertEquals(Arrays.asList(pickups[i].path), events);
            TokenSounds.playLand(pickups[i], 0);
            assertNotEquals(pickups[i].path, arrivals[i].path);
            assertEquals(Arrays.asList(pickups[i].path, arrivals[i].path), events);
        }
    }

    @Test public void fastForwardAndMissingAudioSilenceBothTokenPhases() {
        for (TokenSounds.Cue cue : TokenSounds.Cue.values()) {
            FastForwardAction.turnOn();
            TokenSounds.playGain(cue, 0);
            TokenSounds.playLand(cue, 0);
            FastForwardAction.turnOff();
            loaded = false;
            TokenSounds.playGain(cue, 0);
            TokenSounds.playLand(cue, 0);
            loaded = true;
        }
        assertTrue(events.isEmpty());
        assertTrue(queuedSounds.isEmpty());
    }

    @Test public void clickedMonstersVoiceByEpicnessEvenDuringFastForward() {
        assertEquals(MONSTER_SHEET_GROWL, GameplaySounds.monsterSheetCue(false));
        assertEquals(EPIC_MONSTER_SHEET_ROAR, GameplaySounds.monsterSheetCue(true));
        assertNotEquals(MONSTER_SHEET_GROWL.path, EPIC_MONSTER_SHEET_ROAR.path);
        FastForwardAction.turnOn();
        try {
            GameplaySounds.playMonsterVoice(false);
            GameplaySounds.playMonsterVoice(true);
        } finally {
            FastForwardAction.turnOff();
        }
        assertEquals(Arrays.asList(MONSTER_SHEET_GROWL.path, EPIC_MONSTER_SHEET_ROAR.path), events);
    }
}
