import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.action.focus.TokenView;
import sk.sivak.eldritchhorror.core.view.components.VitalityLossEffect;
import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.GameViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Real GL rendering plus loss counts, repeated damage, healing, timing and cleanup checks. */
public class VitalityLossPreview extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new VitalityLossPreview(), config);
    }

    @Override public void create() {
        try {
            Sound silent = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class<?>[]{Sound.class}, (p, m, a) -> {
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
                return m.getReturnType() == long.class ? 0L : null;
            });
            Gdx.audio = (Audio) Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class<?>[]{Audio.class}, (p, m, a) -> silent);
            VisUI.load(); stage = InfoStage.getStageSafe();
            Image background = new Image(getTexture("encounter/background/mythos.jpg"));
            background.setBounds(0, 0, 960, 540); background.setColor(.32f, .32f, .36f, 1);
            stage.getRoot().addActorAt(0, background);
            for (VitalityLossEffect.Kind kind : VitalityLossEffect.Kind.values()) {
                for (boolean fast : new boolean[]{false, true}) checkEffect(kind, fast);
            }
            checkInterruption();
            checkInvestigator();
            checkMonster();
            require(tracked() == 0, "Fast-forward actions leaked");
            System.out.println("PASS: health/sanity/monster rendering; lazy start; tear/completion timing; fast-forward and speed changes; interruption; empty bars; repeated monster damage, duplicate impacts and healing");
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { FastForwardAction.turnOff(); Gdx.app.exit(); }
    }

    private void checkEffect(VitalityLossEffect.Kind kind, boolean fast) throws Exception {
        FastForwardAction.turnOff();
        ContainerBar source = new ContainerBar(getTexture(kind == VitalityLossEffect.Kind.SANITY ? SANITY_ICON : HEALTH_ICON));
        source.setBounds(110, 430, 160, 27); source.setTotalEmptyContainers(5); source.setCurrentValue(5);
        stage.addActor(source); source.validate();
        AtomicInteger leave = new AtomicInteger(), tear = new AtomicInteger(), done = new AtomicInteger();
        Completable loss = VitalityLossEffect.play(stage, source.getFullContainerPosition(), 22, 27, 0, kind,
                () -> { source.decreaseCurrentValue(); leave.incrementAndGet(); }, tear::incrementAndGet);
        require(leave.get() == 0 && effects() == 0, "Loss began before subscription");
        if (fast) FastForwardAction.turnOn();
        loss.subscribe(() -> { require(effects() == 0, "Completed with fragments on screen"); done.incrementAndGet(); });
        require(leave.get() == 1 && source.getCurrentValue() == 4, "Source was not depleted once");
        for (int frame = 0; frame < 110; frame++) {
            stage.act(1f / 60);
            if (!fast && frame == 32) require(tear.get() == 0 && done.get() == 0, "Fracture fired before the swell");
            if (!fast && frame == 47) require(tear.get() == 1 && done.get() == 0, "Must finish after the fracture, not at it");
            if (!fast && frame % 2 == 0) draw(kind.name().toLowerCase(java.util.Locale.ROOT) + "/frame-" + String.format("%03d", frame / 2));
        }
        require(leave.get() == 1 && tear.get() == 1 && done.get() == 1 && tracked() == 0, "Loss lifecycle/fast-forward");
        source.remove();
    }

    private void checkInterruption() throws Exception {
        for (boolean interrupt : new boolean[]{false, true}) {
            FastForwardAction.turnOff();
            AtomicInteger done = new AtomicInteger(), tear = new AtomicInteger();
            VitalityLossEffect.play(stage, new Vector2(100, 430), 22, 27, 0, VitalityLossEffect.Kind.SANITY,
                    null, tear::incrementAndGet).subscribe(done::incrementAndGet);
            tick(12);
            if (interrupt) stage.getRoot().findActor("vitality-loss-sanity").remove();
            else FastForwardAction.turnOn();
            tick(30);
            require(done.get() == (interrupt ? 0 : 1) && tear.get() == (interrupt ? 0 : 1)
                    && effects() == 0 && tracked() == 0, "Interrupted or speed-changed loss leaked/replayed");
        }
    }

    private void checkInvestigator() throws Exception {
        FastForwardAction.turnOff();
        GameViewImpl view = new GameViewImpl();
        // GameView normally creates this in show(), which also starts the map and game controller.
        Field tokenView = GameViewImpl.class.getDeclaredField("tokenView");
        tokenView.setAccessible(true); tokenView.set(view, new TokenView());
        for (boolean sanity : new boolean[]{false, true}) {
            ContainerBar bar = sanity ? InfoStage.getInvestigatorHud().getSanityBar() : InfoStage.getInvestigatorHud().getHealthBar();
            bar.setTotalEmptyContainers(6); bar.setCurrentValue(6); bar.validate();
            AtomicInteger done = new AtomicInteger();
            (sanity ? view.loseSanity(6) : view.loseHealth(6)).subscribe(done::incrementAndGet);
            require(bar.getCurrentValue() == 5 && effects() == 1, "Only the first token should leave immediately");
            stage.act(.02f);
            require(bar.getCurrentValue() == 5 && effects() == 1, "Second token left before its delay");
            stage.act(.02f);
            require(bar.getCurrentValue() == 4 && effects() == 2, "Second token did not leave in sequence");
            tick(12);
            require(bar.getCurrentValue() == 0 && effects() == 6, "One animation per lost point");
            draw(sanity ? "six-sanity" : "six-health");
            tick(40); require(done.get() == 0, "Encounter advanced before fragments cleared"); tick(55);
            require(done.get() == 1 && effects() == 0, "Investigator loss did not finish");
            (sanity ? view.loseSanity(1) : view.loseHealth(1)).subscribe(done::incrementAndGet);
            require(done.get() == 2 && effects() == 0, "Empty bar started a loss");
            (sanity ? view.loseSanity(0) : view.loseHealth(0)).subscribe(done::incrementAndGet);
            require(done.get() == 3, "Zero loss stalled the encounter");
            bar.setCurrentValue(6);
            (sanity ? view.loseSanity(6) : view.loseHealth(6)).subscribe(done::incrementAndGet);
            FastForwardAction.turnOn();
            stage.act(.02f);
            require(bar.getCurrentValue() == 5, "Fast-forward collapsed the departure delay");
            stage.act(.011f);
            require(bar.getCurrentValue() == 4, "Fast-forward skipped the second departure");
            // A slow frame must not release all remaining tokens together.
            stage.act(.3f);
            require(bar.getCurrentValue() == 3, "Slow frame collapsed the remaining departures");
            tick(22); FastForwardAction.turnOff();
            require(bar.getCurrentValue() == 0 && done.get() == 4 && effects() == 0 && tracked() == 0,
                    "Fast-forward during a cascade lost tokens, completed early or leaked actions");
        }
    }

    private void checkMonster() {
        FastForwardAction.turnOff();
        ToughnessBar bar = new ToughnessBar(); bar.init(4, 4, .52f); bar.pack(); bar.setPosition(400, 440);
        stage.addActor(bar); bar.validate();
        Completable loss = bar.loseToughness(2);
        require(bar.getCurrentHealth() == 4 && effects() == 0, "Monster loss was eager");
        AtomicInteger done = new AtomicInteger(); loss.subscribe(done::incrementAndGet);
        require(bar.getCurrentHealth() == 2 && effects() == 2, "Monster lost the wrong amount");
        bar.loseToughnessNew(bar.getChildren().get(3), 0).subscribe();
        require(effects() == 2 && bar.getCurrentHealth() == 2, "Duplicate hit removed another heart");
        tick(90); require(done.get() == 1 && effects() == 0, "Monster completion");
        bar.restoreHealth(1).subscribe(); tick(90);
        require(bar.getCurrentHealth() == 3 && bar.getChildren().size == 4, "Healing after damage");
        bar.loseToughness(10).subscribe(done::incrementAndGet);
        require(bar.getCurrentHealth() == 0 && effects() == 3, "Repeated damage/overkill");
        tick(90); require(done.get() == 2 && effects() == 0, "Overkill completion");
        bar.init(2, 2, .52f); bar.pack(); bar.validate();
        require(bar.getChildren().size == 2, "Reinitialization retained stale hearts");
        bar.loseToughness(0).subscribe(done::incrementAndGet);
        require(done.get() == 3, "Zero loss stalled");
        bar.remove();
    }

    private int effects() {
        int count = 0;
        for (Actor actor : stage.getActors()) if (actor instanceof VitalityLossEffect) count++;
        return count;
    }
    private void tick(int frames) { for (int i = 0; i < frames; i++) stage.act(1f / 60); }
    private int tracked() throws Exception {
        Field field = InfoStage.getFastForwardButton().getClass().getDeclaredField("fastForwardActions");
        field.setAccessible(true); return ((Map<?, ?>) field.get(InfoStage.getFastForwardButton())).size();
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.04f, .04f, .06f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true); png.setCompression(1);
        png.write(Gdx.files.local("../build/vitality-loss-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
