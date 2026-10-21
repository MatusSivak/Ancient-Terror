import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.Proxy;
import java.lang.StringBuilder;
import java.util.*;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.HolyWaterAsset;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.NecronomiconArtifact;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.wither.WitherSpell;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.components.typewriter.*;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;

/** Exercises actual paper/backdrop lifetimes and clock sound direction offscreen. */
public class CardResolutionPreview extends ApplicationAdapter {
    private Stage stage;
    private final java.util.List<String> sounds = new ArrayList<>();

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new CardResolutionPreview(), config);
    }

    @Override public void create() {
        try {
            Gdx.audio = (Audio) Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class[]{Audio.class},
                    (p, m, a) -> {
                        String path = a == null ? "" : String.valueOf(a[0]);
                        return Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class[]{Sound.class}, (s, method, args) -> {
                            if (method.getName().equals("hashCode")) return System.identityHashCode(s);
                            if (method.getName().equals("equals")) return s == args[0];
                            if (method.getName().equals("play")) sounds.add(path);
                            return method.getReturnType() == long.class ? 0L : null;
                        });
                    });
            VisUI.load(); stage = InfoStage.getStageSafe();
            sk.sivak.eldritchhorror.core.controller.GameController controller =
                    (sk.sivak.eldritchhorror.core.controller.GameController) Proxy.newProxyInstance(
                            sk.sivak.eldritchhorror.core.controller.GameController.class.getClassLoader(),
                            new Class[]{sk.sivak.eldritchhorror.core.controller.GameController.class}, (p, m, a) -> null);
            new sk.sivak.eldritchhorror.core.view.game.HudButtons(controller).init();
            checkDoomSound();
            for (SpellId id : SpellId.values()) require(Gdx.files.internal("card/spell/" + id.name() + ".jpg").exists(), "Missing spell art " + id);
            for (ConditionId id : ConditionId.values()) require(Gdx.files.internal("card/condition/" + id.name() + ".jpg").exists(), "Missing condition art " + id);
            for (AssetId id : AssetId.values()) require(Gdx.files.internal("card/asset/" + id.name() + ".jpg").exists(), "Missing asset art " + id);
            for (ArtifactId id : ArtifactId.values()) require(Gdx.files.internal("card/artifact/" + id.name() + ".jpg").exists(), "Missing artifact art " + id);
            for (boolean fast : new boolean[]{false, true}) {
                checkCardPaper(fast);
                checkPossessionPaper(new HolyWaterAsset(), "asset", fast);
                checkPossessionPaper(new NecronomiconArtifact(), "artifact", fast);
            }
            System.out.println("PASS: retreat/advance audio direction; setup/zero/fast-forward silence; all spell/condition/asset/artifact artwork; nested paper backgrounds; hide/resume/finish; flip and success glyphs");
        } catch (Throwable e) { e.printStackTrace(); System.exit(1); }
        finally { Gdx.app.exit(); }
    }

    private void checkDoomSound() {
        DoomTrackWidget clock = new DoomTrackWidget(); stage.addActor(clock);
        sounds.clear(); clock.updateDoom(15); tick(200);
        require(sounds.isEmpty(), "Setup played doom sound");
        clock.updateDoom(16); tick(200);
        require(sounds.equals(Collections.singletonList(CustomAssetManager.DOOM_RETREAT_SOUND)), "Retreat cue: " + sounds);
        sounds.clear(); clock.updateDoom(15); tick(200);
        require(sounds.equals(Collections.singletonList(CustomAssetManager.DOOM_ADVANCE_SOUND)), "Advance cue: " + sounds);
        sounds.clear(); clock.updateDoom(15); tick(200);
        FastForwardAction.turnOn(); clock.updateDoom(16); tick(200); FastForwardAction.turnOff();
        require(sounds.isEmpty(), "Unchanged/fast-forward played sound");
        clock.remove();
    }

    private void checkCardPaper(boolean fast) throws Exception {
        if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
        Texture spellTexture = CustomAssetManager.getTexture(CardResolutionArtwork.textureName(new WitherSpell()));
        Texture conditionTexture = CustomAssetManager.getTexture(CardResolutionArtwork.textureName(new BlessedCondition()));
        TypewriterViewImpl view = new TypewriterViewImpl();
        int[] completed = {0};
        view.showCardResolutionPaper(new WitherSpell()).subscribe(() -> completed[0]++);
        tick(180); require(completed[0] == 1, "Spell paper did not show");
        Image spellBackground = topBackground();
        require(((TextureRegionDrawable) spellBackground.getDrawable()).getRegion().getTexture() == spellTexture, "Wrong spell background");
        require(spellBackground.getWidth() == 960 && spellBackground.getHeight() == 540, "Background not viewport sized");
        Table spellPaper = view.getTable();
        view.typeHeader("Wither").andThen(view.typeInfo("Then flip this card.\n4, 5, and 6's count as successes.\n[4] [5] [6]"))
                .subscribe(() -> completed[0]++);
        tick(2400); require(completed[0] == 2, "Rules did not finish");
        String labels = labels(spellPaper);
        require(labels.contains(String.valueOf(FontGlyphEnricher.getGlyph("flip this card"))), "Typewriter icon missing");
        for (int value : new int[]{4, 5, 6}) require(labels.contains(String.valueOf(FontGlyphEnricher.getDiceGlyph(value))), "Missing die " + value);
        draw("card-resolution-spell" + (fast ? "-fast" : ""));

        view.hidePaper().subscribe(() -> completed[0]++); tick(180);
        require(spellBackground.getParent() == null, "Hidden paper retained backdrop");
        view.showPaper(false).subscribe(() -> completed[0]++); tick(180);
        require(topBackground() == spellBackground, "Resumed paper lost its backdrop");

        // A nested condition keeps the outer artwork intact and restores it on finish.
        view.showCardResolutionPaper(new BlessedCondition()).subscribe(() -> completed[0]++); tick(180);
        require(InfoStage.getCardResolutionBackgrounds().getChildren().size == 2, "Nested background replaced parent");
        Image conditionBackground = topBackground();
        require(((TextureRegionDrawable) conditionBackground.getDrawable()).getRegion().getTexture() == conditionTexture, "Wrong condition artwork");
        view.typeHeader("Blessed").andThen(view.typeInfo("4, 5, and 6's count as successes.")).subscribe(); tick(1800);
        draw("card-resolution-condition" + (fast ? "-fast" : ""));
        view.finishPaper().subscribe(() -> completed[0]++); tick(180);
        require(conditionBackground.getParent() == null && topBackground() == spellBackground, "Nested finish did not restore outer scene");
        require(view.getTable() == spellPaper, "Nested finish lost outer paper");
        view.finishPaper().subscribe(() -> completed[0]++); tick(180);
        require(InfoStage.getCardResolutionBackgrounds().getChildren().size == 0, "Finished card leaked background");
        require(completed[0] == 7, "Paper completion count " + completed[0]);
        FastForwardAction.turnOff();
    }

    private Image topBackground() { return (Image) InfoStage.getCardResolutionBackgrounds().getChildren().peek(); }

    private void checkPossessionPaper(CardInfo card, String category, boolean fast) throws Exception {
        if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
        Texture texture = CustomAssetManager.getTexture(CardResolutionArtwork.textureName(card));
        TypewriterViewImpl view = new TypewriterViewImpl();
        int[] completed = {0};
        view.showCardResolutionPaper(card).subscribe(() -> completed[0]++);
        tick(180);
        require(completed[0] == 1, category + " paper did not show");
        Image background = topBackground();
        require(((TextureRegionDrawable) background.getDrawable()).getRegion().getTexture() == texture, "Wrong " + category + " artwork");
        view.typeHeader(card.getName()).andThen(view.typeInfo(card.getDescription())).subscribe();
        tick(2400);
        draw("card-resolution-" + category + (fast ? "-fast" : ""));
        view.hidePaper().subscribe(() -> completed[0]++); tick(180);
        require(background.getParent() == null, category + " hide retained backdrop");
        view.showPaper(false).subscribe(() -> completed[0]++); tick(180);
        require(topBackground() == background, category + " resume lost backdrop");
        view.finishPaper().subscribe(() -> completed[0]++); tick(180);
        require(completed[0] == 4 && InfoStage.getCardResolutionBackgrounds().getChildren().size == 0,
                category + " finish leaked backdrop or failed to complete");
        FastForwardAction.turnOff();
    }
    private String labels(Group group) {
        StringBuilder out = new StringBuilder();
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label) out.append(((Label) actor).getText());
            if (actor instanceof Group) out.append(labels((Group) actor));
        }
        return out.toString();
    }
    private void tick(int frames) { for (int i = 0; i < frames; i++) stage.act(1f / 60); }
    private void draw(String name) throws Exception {
        Gdx.gl.glClearColor(.08f, .12f, .1f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/report-fixes-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
    @Override public void dispose() { if (stage != null) stage.dispose(); VisUI.dispose(); }
}
