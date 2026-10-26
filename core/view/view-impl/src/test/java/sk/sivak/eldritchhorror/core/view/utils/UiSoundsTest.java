package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager.BigActorKey;
import sk.sivak.eldritchhorror.core.view.components.SoundDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiSounds.Cue.*;

public class UiSoundsTest {
    private Application previousApp;
    private Graphics previousGraphics;
    private Files previousFiles;
    private boolean previousFastForward;
    private Object previousAssets;
    private CustomAssetManager assets;
    private Field assetsField;
    private final List<String> played = new ArrayList<>();

    @Before public void setup() throws Exception {
        previousApp = Gdx.app;
        previousGraphics = Gdx.graphics;
        previousFiles = Gdx.files;
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> null);
        Gdx.app = null;
        previousFastForward = FastForwardAction.isOn();
        FastForwardAction.turnOff();
        assets = new CustomAssetManager() {
            @Override public synchronized boolean isLoaded(String path, Class type) { return true; }
            @Override public synchronized <T> T get(String path, Class<T> type) {
                return type.cast(Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class}, (proxy, method, args) -> {
                    if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                    if (method.getName().equals("equals")) return proxy == args[0];
                    if (method.getName().equals("play")) { played.add(path); return 1L; }
                    throw new AssertionError(method.getName());
                }));
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
        Gdx.graphics = previousGraphics;
        Gdx.files = previousFiles;
        if (previousFastForward) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
    }

    @Test public void factoryAndBindingInstallOnlyOneClickAndStateUpdatesAreSilent() {
        Button button = button();
        UiSounds.attachButton(button);
        ButtonUtils.addClickListener(button, () -> {});
        UiSounds.attachButtons(button);
        assertEquals(1, button.getCaptureListeners().size);
        button.setChecked(true);
        button.setChecked(false);
        assertTrue(played.isEmpty());
        click(button, button);
        assertEquals(Arrays.asList(BUTTON_CLICK.path), played);
    }

    @Test public void disabledHiddenAndBlockedControlsStayQuiet() {
        Button button = button();
        button.setDisabled(true);
        click(button, button);
        button.setDisabled(false);
        button.setVisible(false);
        click(button, button);
        button.setVisible(true);
        Group parent = new Group();
        parent.addActor(button);
        parent.setTouchable(Touchable.disabled);
        click(button, button);
        assertTrue(played.isEmpty());
    }

    @Test public void nestedButtonsOnlySoundTheInnermostControl() {
        Button outer = button(), inner = button();
        outer.addActor(inner);
        click(outer, inner);
        click(inner, inner);
        assertEquals(Arrays.asList(BUTTON_CLICK.path), played);
    }

    @Test public void cancelledAndOutsideReleasesDoNotClick() {
        Button button = button();
        ClickListener listener = listener(button);
        InputEvent event = event(button, button);
        listener.touchDown(event, 10, 10, 0, 0);
        listener.cancel();
        listener.touchUp(event, 10, 10, 0, 0);
        listener.touchDown(event, 10, 10, 0, 0);
        listener.touchUp(event, 300, 300, 0, 0);
        assertTrue(played.isEmpty());
    }

    @Test public void userClicksAndSheetsStayAudibleWhileAutomaticTransitionsSkip() {
        FastForwardAction.turnOn();
        UiSounds.transition(ANCIENT_OPEN);
        assertTrue(played.isEmpty());
        Button button = button();
        click(button, button);
        ButtonUtils.addClickListener(button, () -> UiSounds.transition(ANCIENT_OPEN)).clicked(event(button, button), 10, 10);
        UiSounds.transition(SHEET_CLOSE);
        assertEquals(Arrays.asList(BUTTON_CLICK.path, ANCIENT_OPEN.path), played);
    }

    @Test public void failedClickDoesNotLeaveAutomaticAudioEnabled() {
        FastForwardAction.turnOn();
        try {
            UiSounds.userAction(() -> { throw new IllegalStateException("failed action"); });
            fail("expected failure");
        } catch (IllegalStateException expected) { }
        UiSounds.transition(MYSTERY_OPEN);
        assertTrue(played.isEmpty());
    }

    @Test public void mysteryAncientAndMonsterSheetsUseTheirOwnCuePairs() {
        assertEquals(MYSTERY_OPEN, UiSounds.sheetCue(BigActorKey.MYSTERY_CARD, true));
        assertEquals(MYSTERY_CLOSE, UiSounds.sheetCue(BigActorKey.MYSTERY_CARD, false));
        assertEquals(ANCIENT_OPEN, UiSounds.sheetCue(BigActorKey.ANCIENT_ONE, true));
        assertEquals(ANCIENT_CLOSE, UiSounds.sheetCue(BigActorKey.ANCIENT_ONE, false));
        assertEquals(CARD_OPEN, UiSounds.sheetCue(BigActorKey.MONSTER_CARD, true));
        assertEquals(SHEET_CLOSE, UiSounds.sheetCue(BigActorKey.PASSPORT, false));
        assertEquals(ENCOUNTER_OPEN, UiSounds.sheetCue(BigActorKey.ENCOUNTER_TABLE, true));
        assertEquals(ENCOUNTER_CLOSE, UiSounds.sheetCue(BigActorKey.ENCOUNTER_TABLE, false));
    }

    @Test public void dialogAnnouncesEachVisibilityChangeOnlyOnce() {
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(), new Class[]{Graphics.class},
                (proxy, method, args) -> method.getName().equals("getWidth") ? 800 : 600);
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class[]{Batch.class},
                (proxy, method, args) -> null);
        Stage stage = new Stage(new ScreenViewport() {
            @Override public void update(int width, int height, boolean centerCamera) {
                setWorldSize(width, height);
                setScreenBounds(0, 0, width, height);
            }
        }, batch);
        BitmapFont font = new BitmapFont(new BitmapFont.BitmapFontData(), new TextureRegion(), false) {
            @Override protected void load(BitmapFontData data) { }
        };
        try {
            SoundDialog dialog = new SoundDialog("", new Window.WindowStyle(font, Color.WHITE, null));
            dialog.show(stage, null);
            dialog.show(stage, null);
            dialog.hide(null);
            dialog.hide(null);
            dialog.show(stage, null);
            assertEquals(Arrays.asList(POPUP_OPEN.path, POPUP_CLOSE.path, POPUP_OPEN.path), played);
        } finally {
            stage.dispose();
            font.dispose();
        }
    }

    private Button button() {
        Button button = new Button(new Button.ButtonStyle());
        button.setSize(100, 40);
        UiSounds.attachButton(button);
        return button;
    }

    private ClickListener listener(Button button) { return (ClickListener) button.getCaptureListeners().first(); }

    private InputEvent event(Actor listener, Actor target) {
        InputEvent event = new InputEvent();
        event.setListenerActor(listener);
        event.setTarget(target);
        return event;
    }

    private void click(Button button, Actor target) {
        InputEvent event = event(button, target);
        ClickListener listener = listener(button);
        listener.touchDown(event, 10, 10, 0, 0);
        listener.touchUp(event, 10, 10, 0, 0);
    }
}
