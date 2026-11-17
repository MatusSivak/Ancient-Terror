import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import sk.sivak.eldritchhorror.core.constants.mythos.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.model.rumor.*;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;
import sk.sivak.eldritchhorror.core.view.game.*;
import sk.sivak.eldritchhorror.core.view.utils.*;

/** Uses real fonts, layouts, snapshots and dismissal callbacks without loading or saving a game. */
public class RumorPreview extends ApplicationAdapter {
    private Stage stage;
    private final GameViewImpl view = new GameViewImpl();

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new RumorPreview(), config);
    }

    @Override public void create() {
        try {
            Sound silent = (Sound)Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class},
                    (p,m,a) -> m.getReturnType() == long.class ? 0L : null);
            Gdx.audio = (Audio)Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class[]{Audio.class}, (p,m,a) -> silent);
            VisUI.load(); stage = InfoStage.getStageSafe();
            GameController controller = (GameController)Proxy.newProxyInstance(GameController.class.getClassLoader(),
                    new Class[]{GameController.class}, (p,m,a) -> null);
            new HudButtons(controller).init();
            String original = UiText.getLanguage();
            try {
                for (String lang : new String[]{"en", "sk"}) {
                    language(lang);
                    for (int id = 1; id <= 5; id++) {
                        checkCard(id, lang, false);
                        checkCard(id, lang, true);
                    }
                }
            } finally { language(original); }
            checkInterruptedDissolve();
            System.out.println("PASS: all five rumors in EN/SK; matching Mythos prose, compact layout, glyphs, normal/fast dissolve, single callbacks, snapshot cleanup.");
        } catch (Throwable failure) { failure.printStackTrace(); System.exit(1); }
        finally { FastForwardAction.turnOff(); Gdx.app.exit(); }
    }

    private AbstractRumorCardInfo rumor(int id) {
        switch (id) {
            case 1: return new TheWindWalkerRumorCard();
            case 2: return new StarsAlignedRumorCard();
            case 3: return new FracturedRealityRumorCard();
            case 4: return new LostKnowledgeRumorCard();
            default: return new GrowingMadnessRumorCard();
        }
    }

    private void checkCard(int id, String lang, boolean fast) throws Exception {
        AbstractRumorCardInfo info = rumor(id);
        info.setRumorLocation(null); // Keep this sheet preview from moving the map camera.
        if (id == 2 || id == 5) info.setCluesRequired(fast ? 1 : 4);
        String flavor = UiText.get(info.getFlavorText());
        require(flavor.length() < 180, "Rumor flavor is not brief");
        if (lang.equals("en")) {
            MythosCard mythos = (MythosCard)Class.forName("sk.sivak.eldritchhorror.core.constants.mythos.BlueMythosCard" + id).newInstance();
            require(flavor.equals(mythos.getFlavor()), "Rumor / Mythos story mismatch");
            require(UiText.get(info.getTitleText()).equals(mythos.getTitle()), "Rumor / Mythos title mismatch");
        }
        FastForwardAction.turnOff();
        AtomicInteger hidden = new AtomicInteger(), removed = new AtomicInteger();
        view.showRumorCard(info).subscribe(hidden::incrementAndGet);
        tick(100);
        RumorCard card = BigActorsManager.getRumorCard();
        draw(lang + "-rumor-" + id);
        require(card.getStage() == stage && card.isDisplayed(), "Rumor did not open");
        require(card.getHeight() <= 516, "Rumor needs scaling: " + card.getHeight());
        Vector2 center = card.localToStageCoordinates(new Vector2(card.getWidth()/2, card.getHeight()/2));
        require(center.dst(480,270) < .1f, "Rumor not centered");
        checkLabels(card);
        String allText = labels(card);
        if (id == 1) require(allText.indexOf(FontGlyphEnricher.getGlyph("Health")) >= 0, "Missing Health glyph");
        if (id == 4) require(allText.indexOf(FontGlyphEnricher.getGlyph("Clues")) >= 0, "Missing Clue glyph");
        if (id == 2 || id == 5) {
            require(allText.indexOf(FontGlyphEnricher.getGlyph("Observation")) >= 0, "Missing Observation glyph");
            require(allText.indexOf(FontGlyphEnricher.getGlyph("Clues")) >= 0, "Missing objective Clue glyph");
        }
        if (id == 5) require(allText.indexOf(FontGlyphEnricher.getGlyph("Sanity")) >= 0, "Missing Sanity glyph");
        if (!fast) draw(lang + "-rumor-" + id);
        if (fast) FastForwardAction.turnOn();
        card.destroyWithEffect().subscribe(removed::incrementAndGet);
        Actor effect = stage.getRoot().findActor("rumor-vanish");
        require(effect != null && card.getStage() == null, "Dissolve did not replace sheet");
        for (int frame = 0; frame < 110; frame++) {
            stage.act(1f/60);
            if (!fast && lang.equals("en") && id == 5 && (frame == 12 || frame == 34 || frame == 55)) draw("vanish-" + frame);
        }
        require(removed.get() == 1 && hidden.get() == 1, "Dissolve callbacks must run once");
        require(stage.getRoot().findActor("rumor-vanish") == null && !card.isDisplayed() && !BigActorsManager.isLocked(), "Dissolve leaked sheet state");
        Field disposed = effect.getClass().getDeclaredField("disposed"); disposed.setAccessible(true);
        require((Boolean)disposed.get(effect), "Snapshot was not disposed");
        Field actions = InfoStage.getFastForwardButton().getClass().getDeclaredField("fastForwardActions"); actions.setAccessible(true);
        require(((Map<?,?>)actions.get(InfoStage.getFastForwardButton())).isEmpty(), "Dissolve leaked fast-forward actions");
        FastForwardAction.turnOff();
    }

    private void checkInterruptedDissolve() throws Exception {
        RumorCard card = new RumorCard(); card.init(rumor(1)); stage.addActor(card);
        card.destroyWithEffect().subscribe(); tick(2);
        Actor effect = stage.getRoot().findActor("rumor-vanish");
        stage.getRoot().removeActor(effect); // Exercise stage detachment, not only effect.remove().
        Field disposed = effect.getClass().getDeclaredField("disposed"); disposed.setAccessible(true);
        require((Boolean)disposed.get(effect) && effect.getActions().size == 0, "Interrupted dissolve leaked snapshot/actions");
    }

    private String labels(Group parent) {
        StringBuilder text = new StringBuilder();
        for (Actor actor : parent.getChildren()) {
            if (actor instanceof Label) text.append(((Label)actor).getText()).append(' ');
            if (actor instanceof Group) text.append(labels((Group)actor));
        }
        return text.toString();
    }

    private void checkLabels(Group parent) {
        for (Actor actor : parent.getChildren()) {
            if (actor instanceof Label) {
                Label label = (Label)actor;
                for (int i=0; i<label.getText().length; i++) {
                    char c = label.getText().charAt(i);
                    require(Character.isWhitespace(c) || label.getStyle().font.getData().getGlyph(c) != null, "Missing font glyph: " + c);
                }
                // Scene2D tables round cell sizes to whole pixels.
                require(label.getPrefHeight() <= label.getHeight() + 1f, "Text clipped: " + label.getText()
                        + " preferred=" + label.getPrefHeight() + " actual=" + label.getHeight());
            }
            if (actor instanceof Group) checkLabels((Group)actor);
        }
    }

    private void language(String language) {
        Application app = Gdx.app;
        try { Gdx.app = null; UiText.setLanguage(language); } finally { Gdx.app = app; }
    }
    private void tick(int frames) { for (int i=0; i<frames; i++) stage.act(1f/60); }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f,.11f,.10f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0,0,960,540);
        if (name.startsWith("vanish-")) {
            int background = pixels.getPixel(0,0);
            for (int x=150; x<810; x++) for (int y=0; y<4; y++) {
                require(pixels.getPixel(x,y)==background && pixels.getPixel(x,539-y)==background,
                        "Dissolving fragments crossed the viewport edge");
            }
        }
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/rumor-preview/" + name + ".png"), pixels); png.dispose(); pixels.dispose();
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
