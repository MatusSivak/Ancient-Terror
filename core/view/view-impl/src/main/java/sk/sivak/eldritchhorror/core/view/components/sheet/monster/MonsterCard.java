package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

import java.util.Locale;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class MonsterCard extends VisTable {
    public static final float SCALE = 0.60f;
    private static final float CARD_WIDTH = 740;
    private static final float CONTENT_WIDTH = CARD_WIDTH - 64;
    private static final Color IVORY = new Color(0xeee1c5ff);
    private static final Color BRASS = new Color(0xb29860ff);
    private static final Color BLUE = new Color(0x8cb9d5ff);
    private static final Color RED = new Color(0xe07870ff);
    private final DisplayHide displayHide;
    private Image hitImage;
    private ToughnessBar toughnessBar;
    private Action0 onDisplayHideAction;
    private Table reckoningTextTable;
    private Table specialTextTable;
    private Table spawnTextTable;
    private Cell<Table> reckoningCell;
    private Cell<Table> specialCell;
    private Cell<Table> spawnCell;

    public MonsterCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.MONSTER_CARD);
        displayHide.setActorKey(OnScreenActors.ActorKey.MONSTER_CARD);
        setTransform(true);
    }

    public void init(MonsterInfo monsterInfo, Action0 onDisplayHideAction) {
        this.onDisplayHideAction = onDisplayHideAction;
        clear();
        hitImage = null;
        reckoningTextTable = specialTextTable = spawnTextTable = null;
        reckoningCell = specialCell = spawnCell = null;
        getColor().a = 1f;
        top();
        setBackground(createSheetBackground());
        pad(8, 32, 24, 32);

        Table heading = new Table();
        Label name = label(monsterInfo.getName().toUpperCase(Locale.ROOT), NEW_FONT_LIBRE_BASKERVILLE, 30, IVORY);
        name.setWrap(true);
        heading.add(name).growX();
        if (monsterInfo.isEpic()) {
            heading.add(label("EPIC", NEW_FONT_LIBRE_BASKERVILLE, 14, BRASS)).padLeft(12);
        }
        add(heading).width(CONTENT_WIDTH).minHeight(36).padBottom(18).row();

        Texture texture = monsterInfo.isEpic()
                ? getEpicMonsterTexture(monsterInfo.getClass().getSimpleName())
                : getNonEpicMonsterTexture(monsterInfo.getClass().getSimpleName());
        Image picture = new Image(texture);
        picture.setScaling(Scaling.fit);
        Table body = new Table();
        body.add(picture).width(204).height(214).padRight(16);
        Table stats = new Table();
        stats.add(createToughnessTable(monsterInfo.getToughness(), monsterInfo.getCurrentHealth()))
                .growX().height(44).padBottom(8).row();
        Table tests = new Table();
        tests.left();
        float testWidth = (CONTENT_WIDTH - 220 - 10) / 2;
        if (monsterInfo.getHorror() != null) {
            tests.add(createTestTable("Horror", monsterInfo.getHorrorTestType(), monsterInfo.getHorrorTestModifier(),
                    monsterInfo.getHorror(), getTexture(HORROR), BLUE)).width(testWidth).height(162);
        }
        if (monsterInfo.getDamage() != null) {
            tests.add(createTestTable("Damage", monsterInfo.getDamageTestType(), monsterInfo.getDamageTestModifier(),
                    monsterInfo.getDamage(), getTexture(DAMAGE), RED)).width(testWidth).height(162)
                    .padLeft(monsterInfo.getHorror() == null ? 0 : 10);
        }
        stats.add(tests).growX();
        body.add(stats).width(CONTENT_WIDTH - 220);
        add(body).width(CONTENT_WIDTH).padBottom(14).row();

        // Keep rule rows detachable for the existing spawn/special/reckoning highlights.
        Table rules = new Table();
        if (monsterInfo.getSpawnText() != null) {
            spawnTextTable = createRuleTable("SPAWN", monsterInfo.getSpawnText(), false);
            spawnCell = rules.add(spawnTextTable).width(CONTENT_WIDTH).padBottom(6);
            rules.row();
        }
        if (monsterInfo.getSpecialText() != null) {
            specialTextTable = createRuleTable("SPECIAL", monsterInfo.getSpecialText(), false);
            specialCell = rules.add(specialTextTable).width(CONTENT_WIDTH).padBottom(6);
            rules.row();
        }
        if (monsterInfo.hasReckoning()) {
            reckoningTextTable = createRuleTable("RECKONING", monsterInfo.getReckoningText(), true);
            reckoningCell = rules.add(reckoningTextTable).width(CONTENT_WIDTH).padBottom(6);
            rules.row();
        }
        rules.pack();
        float ruleHeight = rules.getPrefHeight();
        float availableHeight = Math.max(60, VIEWPORT_HEIGHT - 30 - getPrefHeight());
        if (ruleHeight > availableHeight) {
            ScrollPane scroll = new ScrollPane(rules, new ScrollPane.ScrollPaneStyle());
            scroll.setScrollingDisabled(true, false);
            scroll.setOverscroll(false, false);
            scroll.setFadeScrollBars(false);
            add(scroll).width(CONTENT_WIDTH).height(availableHeight - 20).row();
            add(label("Scroll for more", NEW_FONT_SOURCE_SERIF_4, 14, BRASS)).height(20).row();
        } else if (ruleHeight > 0) {
            add(rules).width(CONTENT_WIDTH).row();
        }
        pack();
        setSize(CARD_WIDTH, Math.max(380, getHeight()));
        addHitImage();
    }

    static Drawable createSheetBackground() {
        NinePatch patch = createMenuDialogPatch();
        patch.scale(0.5f, 0.5f);
        return new NinePatchDrawable(patch);
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        if (hitImage != null) {
            hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        }
    }

    private void addHitImage() {
        hitImage = new Image(getTextureRegion(PURE_WHITE_BACKGROUND));
        hitImage.getColor().a = 0;
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        // Catch outside taps without covering rule scrolling or the live health tokens.
        addActorAt(0, hitImage);
    }

    private Table createToughnessTable(Integer toughness, Integer currentHealth) {
        Table table = new Table();
        table.add(label("TOUGHNESS", NEW_FONT_LIBRE_BASKERVILLE, 16, IVORY)).padRight(12);
        int maximum = toughness == null ? 0 : toughness;
        int health = currentHealth == null ? 0 : currentHealth;
        toughnessBar = new ToughnessBar();
        float scale = Math.min(0.72f, 250f / (Math.max(1, maximum) * 42.3f));
        toughnessBar.init(maximum, health, scale);
        toughnessBar.left();
        table.add(toughnessBar).growX().height(40);
        if (toughness == null) {
            table.add(label("—", NEW_FONT_LIBRE_BASKERVILLE, 24, IVORY)).padRight(24);
        }
        return table;
    }

    private Table createRuleTable(String title, String text, boolean reckoning) {
        Table table = new Table();
        table.setBackground(new PanelDrawable());
        table.pad(10, 12, 10, 12);
        if (reckoning) {
            Image icon = new Image(getTexture(RECKONING));
            icon.setScaling(Scaling.fit);
            table.add(icon).width(82).height(32).padRight(12).top();
        } else {
            table.add(label(title, NEW_FONT_LIBRE_BASKERVILLE, 14, BRASS)).width(82).padRight(12).top();
        }
        Label rule = label(text == null ? "" : text, NEW_FONT_SOURCE_SERIF_4, 20, IVORY);
        rule.setWrap(true);
        rule.setAlignment(Align.left);
        table.add(rule).width(CONTENT_WIDTH - 118).growY();
        return table;
    }

    private static Label label(String text, String font, int size, Color color) {
        Label result = new Label(text, new Label.LabelStyle(getBitmapFontNew(font, size), color));
        result.setAlignment(Align.center);
        return result;
    }

    public static Table createTestTable(String checkName, Stat testType, int testModifier,
                                        Integer rating, Texture imageTexture, Color titleColor) {
        Table table = new Table();
        table.setBackground(new PanelDrawable());
        table.pad(8);
        table.add(label(checkName.toUpperCase(Locale.ROOT), NEW_FONT_LIBRE_BASKERVILLE, 21, titleColor))
                .colspan(2).growX().padBottom(6).row();
        if (testType == null) {
            table.add(label("—", NEW_FONT_SOURCE_SERIF_4, 24, IVORY)).uniformX().growX().height(54);
        } else {
            Image attributeGlyph = new Image(getTexture("glyphs/" + testType.name().toLowerCase(Locale.ROOT) + ".png"));
            attributeGlyph.setScaling(Scaling.fit);
            table.add(attributeGlyph).uniformX().growX().height(46).padTop(4).padBottom(4);
        }
        Image icon = new Image(imageTexture);
        icon.setScaling(Scaling.fit);
        table.add(icon).uniformX().growX().height(46).row();
        String modifierText = testType == null ? "—" : (testModifier > 0 ? "+" : "") + testModifier;
        Color modifierColor = testType == null || testModifier == 0 ? IVORY
                : testModifier < 0 ? RED : new Color(0x9ac99bff);
        table.add(label(modifierText, NEW_FONT_LIBRE_BASKERVILLE, 38, modifierColor)).growX().height(52);
        table.add(label(rating == null ? "—" : rating.toString(),
                NEW_FONT_LIBRE_BASKERVILLE, 38, IVORY)).growX().height(52);
        return table;
    }

    /** Lightweight scalable dark inset; uses the asset manager's shared white texture. */
    private static class PanelDrawable extends BaseDrawable {
        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            Color tint = batch.getColor();
            float r = tint.r, g = tint.g, b = tint.b, a = tint.a;
            Texture white = getTexture(PURE_WHITE_BACKGROUND);
            batch.setColor(r * 0.48f, g * 0.40f, b * 0.25f, a);
            batch.draw(white, x, y, width, height);
            batch.setColor(r * 0.055f, g * 0.071f, b * 0.064f, a);
            batch.draw(white, x + 1, y + 1, width - 2, height - 2);
            batch.setColor(r, g, b, a);
        }
    }

    public void setOnDisplayHideAction(Action0 onDisplayHideAction) {
        this.onDisplayHideAction = onDisplayHideAction;
    }

    public void displayOrHide() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2 - getHeight() / 2);
        displayHide.displayOrHide().subscribe(() -> {
            if (onDisplayHideAction != null) {
                onDisplayHideAction.call();
                onDisplayHideAction = null;
            }
        });
    }

    public Completable loseHealth(int amount) {
        return toughnessBar.loseToughness(amount);
    }

    public Table getReckoningTextTable() { return reckoningTextTable; }
    public Table getSpecialTextTable() { return specialTextTable; }
    public Table getSpawnTextTable() { return spawnTextTable; }
    public ToughnessBar getToughnessBar() { return toughnessBar; }
    public void reattachReckoningTable() { reckoningCell.setActor(reckoningTextTable); }
    public void reattachSpecialTable() { specialCell.setActor(specialTextTable); }
    public void reattachSpawnTable() { spawnCell.setActor(spawnTextTable); }
}
