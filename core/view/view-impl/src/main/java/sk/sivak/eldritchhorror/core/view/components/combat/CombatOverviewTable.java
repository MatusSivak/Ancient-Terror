package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class CombatOverviewTable extends VisTable {

    private static final float SIDE_WIDTH = 150f;
    private static final float CENTER_WIDTH = 84f;
    private static final float PORTRAIT_HEIGHT = 122f;
    private static final float NAME_HEIGHT = 22f;
    private static final float STATS_ROW_HEIGHT = 38f;
    private static final float ICON_SIZE = 30f;
    private static final float VALUE_WIDTH = 24f;
    private static final float CARD_PAD = 6f;
    private static final float HEALTH_SCALE = 0.44f;
    private static final float HEALTH_HEIGHT = 53 * HEALTH_SCALE;
    /** Width of one heart at scale 1 (ToughnessBar token width 282 * 0.15). */
    private static final float HEART_WIDTH = 42.3f;
    private static final Color BRASS = Color.valueOf("DCC99F");
    private static final Color TEXT = Color.valueOf("E6E1D3");
    private static final Color MUTED = Color.valueOf("8E978F");
    private static final Color HORROR_COLOR = Color.valueOf("AEC2C9");
    private static final Color DAMAGE_COLOR = Color.valueOf("D9AF9E");
    private static final Color POSITIVE = Color.valueOf("8FD694");
    private static final Color NEGATIVE = Color.valueOf("E57373");

    public CombatOverviewTable() {

    }

    /**
     * Investigator and monster cards share the same row heights, so each investigator stat lines up with the monster
     * stat it is tested against (Will vs Horror, Strength vs Damage), linked by the centre column.
     */
    public void init(CombatOverviewTableData combatOverviewTableData) {
        setBackground(SelectionPanelStyle.panel("0E1415EE", "87734E", 6, 6));
        // Both cards stretch to the same height; the monster's extra health strip sits below its stats.
        add(createInvestigatorTable(combatOverviewTableData)).width(SIDE_WIDTH).top().fillY();
        add(createCenterColumn()).width(CENTER_WIDTH).top();
        add(createMonsterTable(combatOverviewTableData)).width(SIDE_WIDTH).top().fillY();
        pack();
    }

    private VisTable createInvestigatorTable(CombatOverviewTableData data) {
        VisTable investigatorTable = createSideCard();
        Image investigatorImage = new Image(CustomAssetManager.getInvestigatorTexture(data.getInvestigatorId()));
        investigatorImage.setScaling(Scaling.fit);
        investigatorTable.add(investigatorImage).height(PORTRAIT_HEIGHT).growX().row();
        addNameAndDivider(investigatorTable, data.getInvestigatorId().toString());
        investigatorTable.add(createStatTable(data.getHorrorRowData())).height(STATS_ROW_HEIGHT).growX().row();
        investigatorTable.add(createStatTable(data.getDamageRowData())).height(STATS_ROW_HEIGHT).growX().row();
        investigatorTable.add().expandY();
        return investigatorTable;
    }

    private VisTable createMonsterTable(CombatOverviewTableData data) {
        VisTable monsterTable = createSideCard();
        if (data.isMonsterEpic()) {
            // Epic monsters get a gold card.
            monsterTable.setBackground(SelectionPanelStyle.panel("1F1A0E", "D4AF37", CARD_PAD, 8));
        }
        Image monsterImage;
        if (data.isMonsterEpic()) {
            monsterImage = new Image(CustomAssetManager.getEpicMonsterTexture(data.getMonsterClassName()));
        } else {
            monsterImage = new Image(CustomAssetManager.getNonEpicMonsterTexture(data.getMonsterClassName()));
        }
        monsterImage.setScaling(Scaling.fit);

        int toughness = data.getToughness() == null ? 0 : data.getToughness();
        // Tough monsters get smaller hearts, so the whole row always fits inside the card.
        float healthScale = Math.min(HEALTH_SCALE, (SIDE_WIDTH - 2 * CARD_PAD - 4f) / (HEART_WIDTH * Math.max(1, toughness)));
        ToughnessBar toughnessBar = new ToughnessBar();
        toughnessBar.init(toughness, data.getCurrentHealth() == null ? 0 : data.getCurrentHealth(), healthScale);
        monsterTable.add(monsterImage).height(PORTRAIT_HEIGHT).growX().row();
        addNameAndDivider(monsterTable, data.isMonsterEpic() ? get("combat.epic") + " \u2022 " + data.getMonsterName() : data.getMonsterName());
        // A monster without horror leaves its row empty; the row itself keeps the "vs" lines aligned.
        Integer horror = data.getHorror() != null && data.getHorror() > 0 ? data.getHorror() : null;
        monsterTable.add(createHorrorOrDamageTable(horror, CustomAssetManager.HORROR))
                .height(STATS_ROW_HEIGHT).growX().row();
        monsterTable.add(createHorrorOrDamageTable(data.getDamage(), CustomAssetManager.DAMAGE))
                .height(STATS_ROW_HEIGHT).growX().row();
        // Health gets its own strip below the stats instead of covering the artwork.
        monsterTable.add(createLine(Color.valueOf("87734E99"))).height(1).growX().padTop(2).padBottom(4).row();
        monsterTable.add(toughnessBar).height(HEALTH_HEIGHT).padBottom(2).row();
        return monsterTable;
    }

    private Table createCenterColumn() {
        Table center = new Table();
        Image versusImage = new Image(CustomAssetManager.getTexture("combat/versus.png"));
        versusImage.setScaling(Scaling.fit);
        // Same vertical rhythm as the side cards: top padding, portrait + name + divider, then one cell per stat row.
        center.add(versusImage).width(CENTER_WIDTH - 4).height(PORTRAIT_HEIGHT + NAME_HEIGHT + 7).padTop(CARD_PAD).row();
        center.add(createVsLink(HORROR_COLOR)).height(STATS_ROW_HEIGHT).growX().row();
        center.add(createVsLink(DAMAGE_COLOR)).height(STATS_ROW_HEIGHT).growX().row();
        return center;
    }

    private Table createVsLink(Color color) {
        Table link = new Table();
        Color lineColor = new Color(color.r, color.g, color.b, 0.45f);
        link.add(createLine(lineColor)).height(1).growX();
        link.add(createLabel("vs", color, 0.3f)).padLeft(4).padRight(4);
        link.add(createLine(lineColor)).height(1).growX();
        return link;
    }

    private VisTable createSideCard() {
        VisTable card = new VisTable();
        card.setBackground(SelectionPanelStyle.panel("1A2324", "45504A", CARD_PAD, 8));
        return card;
    }

    private void addNameAndDivider(Table table, String name) {
        table.add(createNameLabel(name)).height(NAME_HEIGHT).growX().row();
        table.add(createLine(Color.valueOf("87734E99"))).height(1).growX().padTop(3).padBottom(3).row();
    }

    private Table createStatTable(CombatOverviewTableData.StatRowData data) {
        Table statTable = new Table();
        if (data == null) {
            return statTable;
        }
        statTable.add(createGlyph("glyphs/" + data.getStat().name().toLowerCase() + ".png")).size(ICON_SIZE);
        statTable.add().growX();
        if (data.getBonus() != 0 || data.getModifier() != 0) {
            // Small breakdown ("3 +1 -1 =") before the final value, so the total stays the eye-catcher.
            statTable.add(createLabel("" + data.getBase(), MUTED, 0.36f, Align.right));
            if (data.getBonus() != 0) {
                statTable.add(createLabel(signed(data.getBonus()), data.getBonus() > 0 ? POSITIVE : NEGATIVE, 0.36f, Align.right)).padLeft(2);
            }
            if (data.getModifier() != 0) {
                statTable.add(createLabel(signed(data.getModifier()), data.getModifier() > 0 ? POSITIVE : NEGATIVE, 0.36f, Align.right)).padLeft(2);
            }
            statTable.add(createLabel("=", MUTED, 0.36f, Align.right)).padLeft(3).padRight(4);
        }
        int total = Math.max(1, data.getBase() + data.getBonus() + data.getModifier());
        statTable.add(createLabel("" + total, TEXT, 0.64f, Align.right)).width(VALUE_WIDTH);
        return statTable;
    }

    private Table createHorrorOrDamageTable(Integer horrorOrDamage, String imageTexture) {
        Table horrorOrDamageTable = new Table();
        if (horrorOrDamage == null) {
            return horrorOrDamageTable;
        }
        // Mirrors the investigator row: value next to the centre "vs", followed by its glyph.
        horrorOrDamageTable.add(createLabel("" + horrorOrDamage, TEXT, 0.64f, Align.left)).width(VALUE_WIDTH);
        horrorOrDamageTable.add(createGlyph(imageTexture)).size(ICON_SIZE).padLeft(4);
        horrorOrDamageTable.add().growX();
        return horrorOrDamageTable;
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : "" + value;
    }

    private static Image createGlyph(String texture) {
        Image image = new Image(CustomAssetManager.getTexture(texture));
        image.setScaling(Scaling.fit);
        return image;
    }

    private Image createLine(Color color) {
        return new Image(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.PURE_WHITE_BACKGROUND).tint(color));
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 42), BRASS);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.32f);
        // Shrink long names (e.g. "Expedition Leader") to fit the narrow card before falling back to an ellipsis.
        float available = SIDE_WIDTH - 16f;
        if (label.getPrefWidth() > available) {
            label.setFontScale(Math.max(0.24f, 0.32f * available / label.getPrefWidth()));
        }
        label.setEllipsis(true);
        return label;
    }

    private Label createLabel(String text, Color color, float scale) {
        return createLabel(text, color, scale, Align.center);
    }

    private Label createLabel(String text, Color color, float scale, int align) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(scale);
        label.setAlignment(align);
        return label;
    }
}