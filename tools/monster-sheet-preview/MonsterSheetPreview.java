import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.monster.ElderThingMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;

/** Renders real game components and checks all bundled monsters without touching save data. */
public class MonsterSheetPreview extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960;
        config.height = 540;
        config.x = -10000;
        config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new MonsterSheetPreview(), config);
    }

    @Override public void create() {
        try {
            VisUI.load();
            stage = new Stage(new FitViewport(960, 540));
            MonsterCard card = new MonsterCard();
            stage.addActor(card);
            int count = 0;
            for (String directory : new String[]{"monster", "monster/epic"}) {
                for (com.badlogic.gdx.files.FileHandle file : Gdx.files.internal(directory).list(".png")) {
                    String className = "sk.sivak.eldritchhorror.core.constants.monster."
                            + (directory.endsWith("epic") ? "epic." : "") + file.nameWithoutExtension() + "Monster";
                    MonsterInfo monster = (MonsterInfo) Class.forName(className).getDeclaredConstructor().newInstance();
                    render(card, monster, file.nameWithoutExtension());
                    if (card.getHeight() > 510) throw new AssertionError("Off-screen card: " + monster.getName());
                    checkLabels(card);
                    checkRule(card, card.getSpawnTextTable(), 0);
                    checkRule(card, card.getSpecialTextTable(), 1);
                    checkRule(card, card.getReckoningTextTable(), 2);
                    int expected = monster.getToughness() == null ? 0 : monster.getToughness();
                    if (card.getToughnessBar().getChildren().size != expected) {
                        throw new AssertionError("Incorrect health tokens: " + monster.getName());
                    }
                    count++;
                }
            }
            ElderThingMonster lengthy = new ElderThingMonster();
            lengthy.setSpecialText(String.join(" ", java.util.Collections.nCopies(12,
                    "A long monster rule must remain readable and accessible.")));
            render(card, lengthy, "long-rules-top");
            ScrollPane scroll = findScroll(card);
            if (scroll == null || scroll.getMaxY() <= 0) throw new AssertionError("Long rules cannot scroll");
            if (!scroll.isAscendantOf(stage.hit(scroll.localToStageCoordinates(
                    new com.badlogic.gdx.math.Vector2(20, 20)).x,
                    scroll.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(20, 20)).y, true))) {
                throw new AssertionError("Hit overlay blocks scrolling");
            }
            scroll.setScrollPercentY(1);
            scroll.updateVisualScroll();
            draw("long-rules-bottom");
            render(card, new ElderThingMonster(), "ElderThing");
            if (card.getSpecialTextTable() != null || card.getReckoningTextTable() != null) {
                throw new AssertionError("Reused card retained stale rule rows");
            }
            System.out.println("PASS: " + count + " monsters, label bounds, rule reattachment, health tokens, scrolling and reuse");
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        } finally {
            Gdx.app.exit();
        }
    }

    private void render(MonsterCard card, MonsterInfo monster, String filename) throws Exception {
        card.init(monster, null);
        card.setPosition((960 - card.getWidth()) / 2, (540 - card.getHeight()) / 2);
        card.validate();
        stage.act(0);
        draw(filename);
    }

    private void draw(String filename) throws Exception {
        Gdx.gl.glClearColor(0.12f, 0.15f, 0.14f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG();
        png.setFlipY(true);
        png.write(Gdx.files.local("../build/monster-sheet-preview/" + filename + ".png"), pixels);
        png.dispose();
        pixels.dispose();
    }

    private void checkRule(MonsterCard card, Table rule, int type) {
        if (rule == null) return;
        Group original = rule.getParent();
        stage.addActor(rule);
        if (type == 0) card.reattachSpawnTable();
        if (type == 1) card.reattachSpecialTable();
        if (type == 2) card.reattachReckoningTable();
        if (rule.getParent() != original) throw new AssertionError("Rule reattachment failed");
    }

    private ScrollPane findScroll(Group group) {
        for (Actor child : group.getChildren()) {
            if (child instanceof ScrollPane) return (ScrollPane) child;
            if (child instanceof Group) {
                ScrollPane result = findScroll((Group) child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private void checkLabels(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label) {
                Label label = (Label) actor;
                label.validate();
                if (label.getPrefHeight() > label.getHeight() + 1
                        || label.getPrefWidth() > label.getWidth() + 1) {
                    throw new AssertionError("Clipped text: " + label.getText());
                }
            }
            if (actor instanceof Group) checkLabels((Group) actor);
        }
    }

    @Override public void dispose() {
        stage.dispose();
        VisUI.dispose();
    }
}
