import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.encounter.*;
import sk.sivak.eldritchhorror.core.constants.monster.ElderThingMonster;
import sk.sivak.eldritchhorror.core.view.components.encounter.*;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import java.util.*;
import java.util.List;

/** Standalone rendering and interaction checks; does not start a game or read save data. */
public class EncounterPreview extends ApplicationAdapter {
    private Stage stage;
    private String selected;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new EncounterPreview(), config);
    }
    @Override public void create() {
        try {
            VisUI.load();
            stage = new Stage(new FitViewport(960, 540));
            SelectEncounterTable table = new SelectEncounterTable();
            stage.addActor(table);
            List<EncounterButtonData> options = new ArrayList<>();
            options.add(option("local", "glyphs/observation.png", "Local encounter", "Explore your surroundings"));
            options.add(option("research", "glyphs/lore.png", "Research encounter", "Investigate a clue"));
            options.add(option("expedition", "token/compass.png", "Expedition", "Follow the expedition trail"));
            CombatEncounterButtonData combat = new CombatEncounterButtonData("combat", new ElderThingMonster());
            combat.setButtonIcon("monster/ElderThing.png"); combat.setFirstLine("Elder Thing");
            options.add(combat);
            EncounterButtonData disabled = option("disabled", "glyphs/will.png", "Other world", null);
            disabled.disable("encounter.disabled.detained");
            options.add(disabled);
            options.add(option("rest", "glyphs/influence.png", "Recover and prepare", null));
            render(table, options, "encounters");
            table.setOnSub(new SingleSubscriber<String>() {
                public void onSuccess(String value) { selected = value; }
                public void onError(Throwable error) { throw new AssertionError(error); }
            });
            List<EncounterButton> cards = new ArrayList<>();
            collect(table, cards);
            click(cards.get(0));
            if (!"local".equals(selected)) throw new AssertionError("Encounter selection failed");
            selected = null;
            click(cards.get(4));
            if (selected != null) throw new AssertionError("Disabled encounter selected");
            for (int i = 0; i < 8; i++) options.add(option("extra-" + i, "glyphs/lore.png", "Additional encounter " + i, null));
            render(table, options, "encounters-scroll");
            ScrollPane scroll = findScroll(table);
            if (scroll.getMaxY() <= 0) throw new AssertionError("Missing overflow scroll");
            scroll.setScrollPercentY(1); scroll.updateVisualScroll();
            draw("encounters-bottom");
            render(table, options.subList(0, 1), "encounters-single");
            render(table, Arrays.asList(
                    typed(option("city", "encounter/CITY.png", "City", null), EncounterType.GENERAL),
                    typed(option("buenos", "encounter/CITY.png", "Buenos Aires", "Gain Ritual Spells"), EncounterType.LOCATION),
                    typed(option("gate", "RED_GATE", "Other World", null), EncounterType.OTHER_WORLD),
                    typed(option("skip", "encounter/skip.png", "Skip", null), EncounterType.SKIP)), "encounters-city-skip");
            cards.clear();
            collect(table, cards);
            Vector2 point = cards.get(0).localToStageCoordinates(new Vector2(100, 30));
            stage.stageToScreenCoordinates(point);
            stage.mouseMoved((int) point.x, (int) point.y);
            draw("encounters-hover");
            stage.touchDown((int) point.x, (int) point.y, 0, 0);
            draw("encounters-pressed");
            stage.touchUp((int) point.x, (int) point.y, 0, 0);
            render(table, Arrays.asList(
                    typed(option("red", "RED_GATE", "Other World", null), EncounterType.OTHER_WORLD),
                    typed(option("green", "GREEN_GATE", "Other World", null), EncounterType.OTHER_WORLD),
                    typed(option("blue", "BLUE_GATE", "Other World", null), EncounterType.OTHER_WORLD)), "encounters-gates");
            CombatEncounterButtonData monster = new CombatEncounterButtonData("monster", new ElderThingMonster());
            monster.setButtonIcon("monster/ElderThing.png"); monster.setFirstLine("Elder Thing");
            EncounterButtonData wilderness = typed(option("wild", "encounter/WILDERNESS.png", "Wilderness", null), EncounterType.GENERAL);
            wilderness.disable("encounter.disabled.defeatMonsters");
            EncounterButtonData blockedSkip = typed(option("skip", "encounter/skip.png", "Skip", null), EncounterType.SKIP);
            blockedSkip.disable("encounter.disabled.defeatMonsters");
            render(table, Arrays.asList(wilderness, blockedSkip, monster), "encounters-required");
            cards.clear(); collect(table, cards);
            if (cards.size() != 2) throw new AssertionError("Disabled Skip should be hidden");
            if (!cards.get(0).isRequired()) throw new AssertionError("Required monster should be first");
            List<String> texts = new ArrayList<>(); labels(table, texts);
            if (!texts.contains(UiText.get("encounter.defeatTitle.one"))) throw new AssertionError("Missing defeat heading: " + texts);
            if (!texts.contains(UiText.get("encounter.defeatHint"))) throw new AssertionError("Missing defeat hint: " + texts);
            if (texts.contains(UiText.get("encounter.disabled.defeatMonsters"))) throw new AssertionError("Per-card reason should be hidden");
            if (texts.contains(UiText.get("dialog.hide"))) throw new AssertionError("Hide button should be removed");
            List<EncounterButtonData> reasons = new ArrayList<>();
            for (String key : new String[]{"defeatMonsters", "nonEpicFirst", "alreadyEncountered", "notEnoughClues",
                    "requiresClue", "requiresSpell", "detained", "tutorial"}) {
                EncounterButtonData reason = option(key, "encounter/CITY.png", "Arkham Asylum Gate", null);
                reason.disable("encounter.disabled." + key);
                reasons.add(reason);
            }
            render(table, reasons, "encounters-reasons");
            List<String> reasonTexts = new ArrayList<>(); labels(table, reasonTexts);
            if (!reasonTexts.contains(UiText.get("encounter.chooseTitle"))) throw new AssertionError("No combat option: expected choose heading");
            List<EncounterButtonData> longest = new ArrayList<>();
            longest.add(typed(option("arkham", "encounter/big_city.png", "San Francisco", "Gain Incantation Spells"), EncounterType.LOCATION));
            longest.add(typed(option("sf", "encounter/big_city.png", "San Francisco", "Improve Observation"), EncounterType.LOCATION));
            longest.add(typed(option("mystery", "encounter/redpin.png", "Active Mystery", "Seed of the Daemon Sultan"), EncounterType.MYSTERY));
            longest.add(typed(option("mystery2", "encounter/redpin.png", "Active Mystery", "Hour of the Moon Lens"), EncounterType.MYSTERY));
            longest.add(typed(option("title", "encounter/CITY.png", "Arkham Asylum Gate", null), EncounterType.GENERAL));
            CombatEncounterButtonData shambler = new CombatEncounterButtonData("shambler", new ElderThingMonster());
            shambler.setButtonIcon("monster/ElderThing.png"); shambler.setFirstLine("Dimensional Shambler");
            longest.add(shambler);
            render(table, longest, "encounters-longest");
            assertSingleLines(table);
            System.out.println("PASS: encounter layout, selection, disabled option, overflow and single option");
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { Gdx.app.exit(); }
    }
    private EncounterButtonData typed(EncounterButtonData data, EncounterType type) {
        data.setEncounterType(type); return data;
    }
    private EncounterButtonData option(String id, String icon, String first, String second) {
        EncounterButtonData data = new EncounterButtonData(id);
        data.setButtonIcon(icon); data.setFirstLine(first); data.setSecondLine(second); return data;
    }
    private void render(SelectEncounterTable table, List<EncounterButtonData> options, String filename) throws Exception {
        table.init(options);
        table.setPosition((960 - table.getWidth()) / 2, (540 - table.getHeight()) / 2);
        stage.act(0); draw(filename);
        if (table.getHeight() > 510) throw new AssertionError("Dialog outside viewport: " + table.getHeight());
        check(table);
    }
    private void draw(String filename) throws Exception {
        Gdx.gl.glClearColor(0.12f, 0.15f, 0.14f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/encounter-preview/" + filename + "-" + System.getProperty("ui.language", "en") + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private void check(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label) {
                Label label = (Label) actor;
                if (label.getPrefHeight() > label.getHeight() + 1 || label.getPrefWidth() > label.getWidth() + 1)
                    throw new AssertionError("Clipped label: " + label.getText());
            }
            if (actor instanceof Group) check((Group) actor);
        }
    }
    private void assertSingleLines(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label && ((Label) actor).getGlyphLayout().runs.size > 1)
                throw new AssertionError("Wrapped encounter text: " + ((Label) actor).getText());
            if (actor instanceof Group) assertSingleLines((Group) actor);
        }
    }
    private void labels(Group group, List<String> texts) {
        for (Actor child : group.getChildren()) {
            if (child instanceof Label) texts.add(((Label) child).getText().toString());
            else if (child instanceof TextButton) texts.add(((TextButton) child).getText().toString());
            if (child instanceof Group) labels((Group) child, texts);
        }
    }
    private void collect(Group group, List<EncounterButton> cards) {
        for (Actor child : group.getChildren()) {
            if (child instanceof EncounterButton) cards.add((EncounterButton) child);
            else if (child instanceof Group) collect((Group) child, cards);
        }
    }
    private ScrollPane findScroll(Group group) {
        for (Actor child : group.getChildren()) {
            if (child instanceof ScrollPane) return (ScrollPane) child;
            if (child instanceof Group) { ScrollPane result = findScroll((Group) child); if (result != null) return result; }
        }
        return null;
    }
    private void click(Actor actor) {
        Vector2 point = actor.localToStageCoordinates(new Vector2(actor.getWidth()/2, actor.getHeight()/2));
        stage.stageToScreenCoordinates(point);
        stage.touchDown((int)point.x, (int)point.y, 0, 0); stage.touchUp((int)point.x, (int)point.y, 0, 0);
    }
    @Override public void dispose() { stage.dispose(); VisUI.dispose(); }
}
