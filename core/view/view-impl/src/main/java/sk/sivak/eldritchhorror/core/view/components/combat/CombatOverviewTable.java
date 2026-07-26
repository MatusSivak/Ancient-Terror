package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class CombatOverviewTable extends VisTable {

    private static final int COLUMNS_WIDTH = 200;
    private static final int STATS_ROW_HEIGHT = 50;

    public CombatOverviewTable() {

    }



    public void init(CombatOverviewTableData combatOverviewTableData) {

        pad(5);
        VisTable investigatorTable = createInvestigatorTable(combatOverviewTableData);
        add(investigatorTable).width(COLUMNS_WIDTH + 10).height(305);
        Image versusImage = new Image(CustomAssetManager.getTexture("combat/versus.png"));
        versusImage.setScaling(Scaling.fit);
        add(versusImage).width(COLUMNS_WIDTH+10).height(305);
        VisTable monsterTable = createMonsterTable(combatOverviewTableData);
        add(monsterTable).width(COLUMNS_WIDTH + 10).height(305);

        pack();
        setBackground(CustomAssetManager.getTextureRegionDrawable(WHITE_BACKGROUND));
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        Color colorBefore = new Color(getColor());
        setColor(new Color(0f, 0f, 0f, colorBefore.a));
        super.drawBackground(batch, parentAlpha * 0.8f, x, y);
        setColor(colorBefore);
    }

    private VisTable createInvestigatorTable(CombatOverviewTableData combatOverviewTableData) {
        VisTable investigatorTable = new VisTable();
        investigatorTable.pad(5);

        Image investigatorImage = new Image(CustomAssetManager.getInvestigatorTexture(combatOverviewTableData.getInvestigatorId()));
        investigatorImage.setScaling(Scaling.fit);
        investigatorTable.add(investigatorImage).height(COLUMNS_WIDTH - 30).width(COLUMNS_WIDTH - 30).row();
        investigatorTable.add(createNameLabel(combatOverviewTableData.getInvestigatorId().toString())).width(COLUMNS_WIDTH).row();
        investigatorTable.addSeparator().width(COLUMNS_WIDTH);
        investigatorTable.add(createStatTable(combatOverviewTableData.getHorrorRowData(), new Color(0.4f,0.4f,1f,1f))).height(STATS_ROW_HEIGHT).width(COLUMNS_WIDTH).row();
        investigatorTable.add(createStatTable(combatOverviewTableData.getDamageRowData(), new Color(1f,0.4f,0.4f,1f))).height(STATS_ROW_HEIGHT).width(COLUMNS_WIDTH).row();

        investigatorTable.setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));
        return investigatorTable;
    }

    private VisTable createMonsterTable(CombatOverviewTableData data) {
        VisTable monsterTable = new VisTable();
        monsterTable.pad(5);

        Table monsterImageHealthNameTable = new Table();
        Image monsterImage;
        if (data.isMonsterEpic()) {
            monsterImage = new Image(CustomAssetManager.getEpicMonsterTexture(data.getMonsterClassName()));
        } else {
            monsterImage = new Image(CustomAssetManager.getNonEpicMonsterTexture(data.getMonsterClassName()));
        }
        monsterImage.setScaling(Scaling.fit);

        ToughnessBar toughnessBar = new ToughnessBar();
        toughnessBar.init(
                data.getToughness() == null ? 0 : data.getToughness(),
                data.getCurrentHealth() == null ? 0 : data.getCurrentHealth(), 0.45f);

        monsterImageHealthNameTable.add(monsterImage).row();
        monsterImageHealthNameTable.add(toughnessBar).height(53 * 0.45f).padTop(5).align(Align.top).row();
        monsterImageHealthNameTable.add(createNameLabel(data.getMonsterName())).width(COLUMNS_WIDTH);


        monsterTable.add(monsterImageHealthNameTable).height(COLUMNS_WIDTH -8).width(COLUMNS_WIDTH-30).align(Align.top).row();
        monsterTable.addSeparator().width(COLUMNS_WIDTH);
        monsterTable.add(createHorrorOrDamageTable("Horror: ", data.getHorror(), new Color(0.4f,0.4f,1f,1f), CustomAssetManager.HORROR))
                .height(STATS_ROW_HEIGHT).width(COLUMNS_WIDTH).row();
        monsterTable.add(createHorrorOrDamageTable("Damage: ", data.getDamage(), new Color(1,0.4f,0.4f,1f), CustomAssetManager.DAMAGE))
                .height(STATS_ROW_HEIGHT).width(COLUMNS_WIDTH).row();

        monsterTable.setBackground(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND));
        return monsterTable;
    }

    private Table createStatTable(CombatOverviewTableData.StatRowData data, Color statColor) {
        Table statTable = new Table();
        if (data == null) {
            statTable.add().height(STATS_ROW_HEIGHT);
            return statTable;
        }
        int padRight = 5;
        statTable.add(createStatLabel(data.getStat().prettyString()+": ", Align.left, statColor)).growX();
        if (data.getBonus() == 0 && data.getModifier()== 0) {
            statTable.add(createStatLabel("" + data.getBase(), Align.right, Color.WHITE, 0.8f));
        } else {
            statTable.add(createStatLabel("" + data.getBase(), Align.right, Color.WHITE));
        }
        if (data.getBonus() > 0) {
            statTable.add(createStatLabel("+" + data.getBonus(), Align.right, Color.GREEN));
        }
        if (data.getModifier() < 0) {
            statTable.add(createStatLabel(""+data.getModifier(), Align.right, Color.RED));
        } else if (data.getModifier() > 0) {
            statTable.add(createStatLabel("+"+data.getModifier(), Align.right, Color.GREEN));
        }

        if (data.getBonus()!= 0 || data.getModifier()!= 0) {
            int horrorTotal = Math.max(1, data.getBase() + data.getBonus()+ data.getModifier());
            statTable.add(createStatLabel("= ", Align.right, Color.WHITE));
            statTable.add(createStatLabel(""+horrorTotal, Align.right, Color.WHITE, 0.8f)).padRight(padRight);
        } else {
            statTable.add().width(padRight);

        }
        return statTable;
    }

    private Table createHorrorOrDamageTable(String labelText, Integer horrorOrDamage, Color color, String imageTexture) {
        Table horrorOrDamageTable = new Table();
        if (horrorOrDamage == null) {
            horrorOrDamageTable.add().height(STATS_ROW_HEIGHT);
            return horrorOrDamageTable;
        }

        horrorOrDamageTable.add(createStatLabel(labelText, Align.left, color)).growX();
        Image image = new Image(CustomAssetManager.getTexture(imageTexture));
        image.setScaling(Scaling.fit);
        image.setSize(STATS_ROW_HEIGHT, STATS_ROW_HEIGHT);
        image.setAlign(Align.right);
        horrorOrDamageTable.add(image).align(Align.right).width(STATS_ROW_HEIGHT).height(STATS_ROW_HEIGHT);
        horrorOrDamageTable.add(createStatLabel(""+horrorOrDamage, Align.right, Color.WHITE, 0.8f)).padRight(5);
        return horrorOrDamageTable;
    }

    private Label createStatLabel(String text, int align, Color color, float scale) {
        Label label = createLabel(text, color, scale);
        label.setAlignment(align);
        return label;
    }

    private Label createStatLabel(String text, int align, Color color) {
        Label label = createLabel(text, color, 0.5f);
        label.setAlignment(align);
        return label;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.LIGHT_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    private Label createLabel(String text, Color color, float scale) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(scale);
        label.setAlignment(Align.center, Align.center);
        return label;
    }



}
