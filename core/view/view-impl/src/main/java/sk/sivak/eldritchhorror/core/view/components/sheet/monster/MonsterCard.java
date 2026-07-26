package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.SectionWrapper;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.DAMAGE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_GOBLIN_ONE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.HORROR;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MONSTER_SHEET;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getEpicMonsterTexture;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getNonEpicMonsterTexture;

public class MonsterCard extends VisTable {

    public static final float SCALE = 0.60f;
    private final DisplayHide displayHide;
    private MonsterInfo monsterInfo;
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
        align(Align.left);
        setBackground((Drawable) null);
        getColor().a = 1f;
        this.monsterInfo = monsterInfo;
        pad(50 * SCALE, 75 * SCALE, 75 * SCALE, 75 * SCALE);
        Table nameTypeTable = createNameTypeTable(monsterInfo.getName(), monsterInfo.isEpic());
        add(nameTypeTable).growX().align(Align.topLeft).colspan(3);

        row();
        addSeparator().colspan(3).padRight(30);
        add(createToughnessTable(monsterInfo.getToughness(), monsterInfo.getCurrentHealth())).align(Align.topLeft).colspan(3);
        row();
        addSeparator().colspan(3).padRight(30);


        Texture monsterTexture;
        if (monsterInfo.isEpic()) {
            monsterTexture = getEpicMonsterTexture(monsterInfo.getClass().getSimpleName());
        } else {
            monsterTexture = getNonEpicMonsterTexture(monsterInfo.getClass().getSimpleName());
        }

        Image picture = new Image(monsterTexture) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setColor(Color.BLACK);
                super.draw(batch, parentAlpha);
                setColor(Color.WHITE);
                int border = 5;
                setX(getX() + border);
                setY(getY() + border);
                setWidth(getWidth() - 2 * border);
                setHeight(getHeight() - 2 * border);
                super.draw(batch, parentAlpha);
                setX(getX() - border);
                setY(getY() - border);
                setWidth(getWidth() + 2 * border);
                setHeight(getHeight() + 2 * border);

            }
        };
        add(picture).align(Align.topLeft).width(125).height(125).padLeft(10).padTop(15);


        Table horrorTable = createTestTable("Horror", monsterInfo.getHorrorTestType(), monsterInfo.getHorrorTestModifier(),
                monsterInfo.getHorror(), CustomAssetManager.getTexture(HORROR), new Color(0x4286f4ff));
        Table damageTable = createTestTable("Damage", monsterInfo.getDamageTestType(), monsterInfo.getDamageTestModifier(),
                monsterInfo.getDamage(), CustomAssetManager.getTexture(DAMAGE), new Color(0xe02323ff));
        int padLeft = 45;
        if (monsterInfo.getHorror() != null) {
            add(horrorTable).padLeft(45).width(horrorTable.getWidth()).height(horrorTable.getHeight()).align(Align.topLeft);
            padLeft = 10;
        }
        if (monsterInfo.getDamage() != null) {
            add(damageTable).padLeft(padLeft).width(damageTable.getWidth()).height(damageTable.getHeight()).align(Align.topLeft);
        }

        row();
        addSeparator().colspan(3).padTop(20).padRight(30);

        if (monsterInfo.getSpawnText() != null) {
            spawnTextTable = createSpawnTextTable(monsterInfo.getSpawnText());
            spawnCell = add(spawnTextTable).padTop(5).align(Align.topLeft).colspan(3).height(spawnTextTable.getPrefHeight());
            row();
        }
        if (monsterInfo.getSpecialText() != null) {
            specialTextTable = createSpecialTextTable(monsterInfo.getSpecialText());
            specialCell = add(specialTextTable).padTop(5).align(Align.topLeft).colspan(3).height(specialTextTable.getPrefHeight());
            row();
        }
        if (monsterInfo.hasReckoning()) {
            reckoningTextTable = createReckoningTextTable(monsterInfo.getReckoningText());
            reckoningCell = add(reckoningTextTable).padTop(5).align(Align.topLeft).colspan(3).height(reckoningTextTable.getPrefHeight());
        }

        pack();
        setHeight(748 * SCALE);
        setWidth(1067 * SCALE);
        CustomAssetManager.getTextureAsync(MONSTER_SHEET).subscribe(ok -> {
            TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable(MONSTER_SHEET);
            setBackground(background);
        });

        addHitImage();
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    private void addHitImage() {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.getColor().a = 0.0f;
    }

    private Table createToughnessTable(Integer toughness, Integer currentHealth) {
        Table table = new Table();
        Label specialLabel = createTestLabel("Toughness: ");
        specialLabel.getStyle().fontColor = Color.BLACK;
        table.add(specialLabel);

        toughnessBar = new ToughnessBar();
        toughnessBar.init(toughness == null ? 0 : toughness, currentHealth == null ? 0 : currentHealth);
        table.add(toughnessBar).width(500);
        return table;
    }

    private Table createSpecialTextTable(String specialText) {
        Table table = new Table();

        Label specialValueLabel = createValue(FONT_MINYA, 0.5f);
        specialValueLabel.setWrap(true);
        specialValueLabel.setAlignment(Align.topLeft);
        specialValueLabel.setText("[BLACK]" + specialText + "[]");

        Label specialLabel = createTestLabel("Special: ");
        specialLabel.getStyle().fontColor = Color.BLACK;
        table.add(specialLabel).width(70);

        specialValueLabel.setWidth(500);

        table.add(specialValueLabel).align(Align.left).width(500).height(specialValueLabel.getPrefHeight());
        return table;
    }

    private Table createSpawnTextTable(String spawnText) {
        Table table = new Table();

        Label specialValueLabel = createValue(FONT_MINYA, 0.5f);
        specialValueLabel.setWrap(true);
        specialValueLabel.setAlignment(Align.topLeft);
        specialValueLabel.setText("[BLACK]" + spawnText + "[]");

        Label specialLabel = createTestLabel("Spawn: ");
        specialLabel.getStyle().fontColor = Color.BLACK;
        table.add(specialLabel).width(70);

        specialValueLabel.setWidth(500);

        table.add(specialValueLabel).align(Align.left).width(500).height(specialValueLabel.getPrefHeight());
        return table;
    }

    private Table createReckoningTextTable(String specialText) {
        Table table = new Table();

        Label specialValueLabel = createValue(FONT_MINYA, 0.5f);
        specialValueLabel.setWrap(true);
        specialValueLabel.setAlignment(Align.topLeft);
        specialValueLabel.setText("[BLACK]" + specialText + "[]");

        Image picture = new Image(CustomAssetManager.getTexture(CustomAssetManager.RECKONING));

        picture.setScaling(Scaling.fit);
        table.add(picture).width(70).height(53);

        specialValueLabel.setWidth(500);

        table.add(specialValueLabel).align(Align.left).width(500).height(specialValueLabel.getPrefHeight());
        return table;
    }

    private Table createNameTypeTable(String name, boolean isEpic) {
        Table table = new Table();

        Label nameLabel = createNameLabel(name);
        table.add(nameLabel).width(nameLabel.getWidth() * 0.35f + 10);
        Label typeLabel = createNiceLabel(isEpic ? "- EPIC" : "");
        table.add(typeLabel).width(typeLabel.getWidth() * 0.5f).padLeft(5);
        return table;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.DARK_GRAY);
        Color color = new Color(1f, 1f, 1f, 0.25f);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(color);
                super.draw(batch, x, y, width, height);
            }
        };
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    private Label createNiceLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), Color.DARK_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setFontScale(0.5f);
        return label;
    }

    private static Label createTestLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.42f);
        return label;
    }

    public static Table createTestTable(String checkName, Stat testType, int testModifier, Integer rating, Texture imageTexture, Color titleColor) {
        VisTable leftTable = new VisTable();
        Label modifierLabel = createTestLabel(testType == null ? "x" : testType.prettyString() + "\nModifier");
        modifierLabel.setAlignment(Align.center, Align.center);

        Label modifierValueLabel = createValue(FONT_GOBLIN_ONE, 0.25f);
        modifierValueLabel.setAlignment(Align.center);
        if (testModifier == 0) {
            modifierValueLabel.setText("[BLACK]" + 0 + "[]");
        } else if (testModifier > 0) {
            modifierValueLabel.setText("[BLACK]" + "[#229B3C]+" + testModifier + "[]");
        } else {
            modifierValueLabel.setText("[BLACK]" + "[#e02323]" + testModifier + "[]");
        }

        leftTable.add(modifierLabel).padLeft(5).padRight(5).width(75).height(45);
        leftTable.row();
        leftTable.addSeparator();
        leftTable.add(modifierValueLabel).width(65);

        VisTable rightTable = new VisTable();

        Image image = new Image(imageTexture);
        image.setScaling(Scaling.fit);

        Label ratingValueLabel = createValue(FONT_GOBLIN_ONE, 0.25f);
        ratingValueLabel.setAlignment(Align.center);
        ratingValueLabel.setText("[BLACK]" + rating + "[]");

        rightTable.add(image).padLeft(5).padRight(5).width(75).height(45);
        rightTable.row();
        rightTable.addSeparator();
        rightTable.add(ratingValueLabel).width(65);


        VisTable table = new VisTable();
        table.add(leftTable);
        table.addSeparator(true);
        table.add(rightTable);

        table.pack();
        SectionWrapper section = new SectionWrapper().init(checkName, table);
        section.getTitleLabel().getStyle().fontColor = titleColor;

        return section;
    }

    private static Label createValue(String fontName, float fontScale) {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFont(fontName), Color.WHITE);
        style.background = new ValueFieldNinePatch();

        Label label = new Label("0", style);
        style.font.getData().markupEnabled = true;
        label.setAlignment(Align.left);
        label.setFontScale(fontScale);
        return label;
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

    public Table getReckoningTextTable() {
        return reckoningTextTable;
    }

    public Table getSpecialTextTable() {
        return specialTextTable;
    }

    public Table getSpawnTextTable() {
        return spawnTextTable;
    }

    public ToughnessBar getToughnessBar() {
        return toughnessBar;
    }

    public void reattachReckoningTable() {
        reckoningCell.setActor(reckoningTextTable);
    }

    public void reattachSpecialTable() {
        specialCell.setActor(specialTextTable);
    }

    public void reattachSpawnTable() {
        spawnCell.setActor(spawnTextTable);
    }
}
