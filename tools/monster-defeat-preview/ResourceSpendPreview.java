import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.view.action.focus.TokenView;
import sk.sivak.eldritchhorror.core.view.action.travel.TravelActionView;
import sk.sivak.eldritchhorror.core.view.components.ResourceSpendEffect;
import sk.sivak.eldritchhorror.core.view.components.hud.*;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class ResourceSpendPreview extends ApplicationAdapter {
    private Stage stage;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new ResourceSpendPreview(), config);
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
            for (ResourceSpendEffect.Kind kind : ResourceSpendEffect.Kind.values()) {
                for (int mode = 0; mode < 5; mode++) check(kind, mode);
            }
            checkCallers();
            require(tracked() == 0, "Leaked fast-forward actions");
            System.out.println("PASS: clue/focus/train/ship; lazy start; callbacks; normal/fast/speed-change/large-delta; interruption; real HUD depletion and empty slots");
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { FastForwardAction.turnOff(); Gdx.app.exit(); }
    }

    private void check(ResourceSpendEffect.Kind kind, int mode) throws Exception {
        FastForwardAction.turnOff();
        AtomicInteger leave = new AtomicInteger(), split = new AtomicInteger(), done = new AtomicInteger();
        String name = "resource-spend-" + kind.name().toLowerCase(java.util.Locale.ROOT);
        Completable effect = ResourceSpendEffect.play(stage, new Vector2(120, 440), 27, 27, kind,
                leave::incrementAndGet, split::incrementAndGet);
        require(leave.get() == 0 && stage.getRoot().findActor(name) == null, "Eager spend");
        effect.subscribe(() -> {
            require(stage.getRoot().findActor(name) == null, "Completed before debris cleared"); done.incrementAndGet();
        });
        require(leave.get() == 1, "Source not depleted exactly once");
        for (int frame = 0; frame < 100; frame++) {
            if (mode == 1 || (mode == 2 && frame >= 18)) FastForwardAction.turnOn();
            if (mode == 3 && frame == 44) stage.getRoot().findActor(name).remove();
            stage.act(mode == 4 ? 3 : 1f / 60);
            if (mode == 0 && frame == 32) require(split.get() == 0 && done.get() == 0, "Premature burst");
            if (mode == 0 && frame == 45) require(split.get() == 1 && done.get() == 0, "Burst completion too early");
            if (mode == 0 && frame == 45 && (kind == ResourceSpendEffect.Kind.TRAIN || kind == ResourceSpendEffect.Kind.SHIP)) {
                Group tearing = stage.getRoot().findActor(name);
                require(tearing.findActor("resource-fragment-0").isVisible()
                        && tearing.findActor("resource-fragment-1").isVisible()
                        && tearing.findActor("resource-fragment-2") == null, "Ticket must tear into exactly two pieces");
                for (Actor child : tearing.getChildren()) {
                    require(!(child instanceof sk.sivak.eldritchhorror.core.view.animation.BurstLight), "Ticket has explosion particles");
                }
            }
            if (mode == 0 && frame % 2 == 0) draw(kind.name().toLowerCase(java.util.Locale.ROOT) + "/frame-" + String.format("%03d", frame / 2));
        }
        require(split.get() == 1 && done.get() == (mode == 3 ? 0 : 1), "Incorrect callback count");
        require(stage.getRoot().findActor(name) == null && tracked() == 0, "Leaked effect");
    }

    private void checkCallers() {
        FastForwardAction.turnOff();
        TokenView tokens = new TokenView();
        for (boolean clue : new boolean[]{true, false}) {
            ContainerBar bar = clue ? InfoStage.getInvestigatorHud().getClueBar() : InfoStage.getInvestigatorHud().getFocusBar();
            bar.setTotalEmptyContainers(3); bar.setCurrentValue(2); bar.validate();
            Completable loss = clue ? tokens.loseClue() : tokens.loseFocus();
            require(bar.getCurrentValue() == 2, "Eager HUD mutation");
            AtomicInteger done = new AtomicInteger(); loss.subscribe(done::incrementAndGet);
            require(bar.getCurrentValue() == 1, "Wrong resource count");
            tick(95); require(done.get() == 1, "Resource did not finish");
            (clue ? tokens.loseClue() : tokens.loseFocus()).subscribe(done::incrementAndGet); tick(95);
            (clue ? tokens.loseClue() : tokens.loseFocus()).subscribe(done::incrementAndGet);
            require(bar.getCurrentValue() == 0 && done.get() == 3, "Empty resource loss");
        }
        TicketContainerBar bar = InfoStage.getInvestigatorHud().getTicketBar();
        TravelActionView travel = new TravelActionView(null, null);
        bar.clearTickets(); bar.addTicket(PathType.TRAIN); bar.addTicket(PathType.SHIP); bar.validate();
        for (PathType path : new PathType[]{PathType.TRAIN, PathType.SHIP}) {
            Completable loss = travel.loseTicket(path);
            require(bar.getTicketImage(path) != null, "Eager ticket mutation");
            AtomicInteger done = new AtomicInteger(); loss.subscribe(done::incrementAndGet);
            require(bar.getTicketImage(path) == null, "Ticket remained");
            tick(95); require(done.get() == 1, "Ticket did not finish");
            travel.loseTicket(path).subscribe(done::incrementAndGet);
            require(done.get() == 2, "Empty ticket loss stalled");
        }
        bar.clearTickets(); bar.addTicket(PathType.TRAIN); bar.clearTickets();
        require(bar.getTicketImage(PathType.TRAIN) == null, "Stale ticket after HUD refresh");
    }

    private void tick(int frames) { for (int i = 0; i < frames; i++) stage.act(1f / 60); }
    private int tracked() throws Exception {
        Field field = InfoStage.getFastForwardButton().getClass().getDeclaredField("fastForwardActions");
        field.setAccessible(true); return ((Map<?, ?>) field.get(InfoStage.getFastForwardButton())).size();
    }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f, .1f, .1f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/resource-spend-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
