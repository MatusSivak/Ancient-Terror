package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.map.vortex.VortexImage;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ANCIENT_ONE_LABEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MONSTER_SHEET;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class ShubNiggurathCard extends VisTable implements AncientOneCard{

    public static final float SCALE = 0.70f;

    private final DisplayHide displayHide;
    private AncientOneInfo ancientOneInfo;
    private Image hitImage;
    private Action0 beforeDisplayAction;
    private VisTable powerTable;

    public ShubNiggurathCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.ANCIENT_ONE);
        displayHide.setActorKey(OnScreenActors.ActorKey.ANCIENT_ONE_CARD);
        setTransform(true);
    }

    @Override
    public void init(AncientOneInfo ancientOneInfo) {
        if (this.ancientOneInfo == ancientOneInfo) {
            updatePowerTable();
            return;
        }

        this.ancientOneInfo = ancientOneInfo;
        getColor().a = 1f;
        clear();
        align(Align.bottom);
        pad(50 * SCALE, 50 * SCALE, 50 * SCALE, 20 * SCALE);
        Table topLeftPart = createTopLeftPart(ancientOneInfo);

        Image splashImage = new Image(CustomAssetManager.getTexture("ancient_one/SHUB_NIGGURATH.png"));
        splashImage.setScaling(Scaling.fit);
        add(splashImage).colspan(2).height(180).row();
        add(topLeftPart).align(Align.topLeft);
        add(createFlavorPart(ancientOneInfo)).growX();
        row();
        addSeparator().colspan(2);
        row();
        add(createPowerTable()).align(Align.left).growX().padRight(10).colspan(2);
        row();
        add(createReckoningTable(ancientOneInfo)).align(Align.left).growX().padRight(10).colspan(2);
        row();
        add(createEndGameTable(ancientOneInfo)).align(Align.left).growX().padRight(10).colspan(2);

        pack();
        TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable(MONSTER_SHEET);
        setBackground(background);
        setHeight(748 * SCALE);
        setWidth(1067 * 0.9f * SCALE);

        addHitImage();
    }

    private Table createPowerTable() {
        powerTable = new VisTable();
        powerTable.align(Align.left);
        Label powerLabel = createTextLabel(get("ancientOne.monsters"));
        powerTable.add(powerLabel).padLeft(5).height(60).padRight(5);

        for (int i = 0; i < 10; i++) {
            Image actor = new Image(CustomAssetManager.getTexture("combat/horror.png"));
            actor.setOrigin(25,25);
            actor.setScaling(Scaling.fit);
            if (i >= ancientOneInfo.getPower()) {
                actor.setScale(0.5f);
                actor.getColor().a = 0.5f;
            }
            actor.setAlign(Align.center);
            powerTable.add(new Container<>(actor).size(50));
        }

        return powerTable;
    }

    private void updatePowerTable() {
        for (int i = 0; i < 10; i++) {
            Image actor = ((Container<Image>) powerTable.getCells().get(i+1).getActor()).getActor();
            if (i >= ancientOneInfo.getPower()) {
                actor.setScale(0.5f);
                actor.getColor().a = 0.5f;
            } else {
                actor.setScale(1f);
                actor.getColor().a = 1f;
            }
        }
    }

    private Table createReckoningTable(AncientOneInfo ancientOneInfo) {
        Table table = new Table();

        Image reckoningImage = new Image(CustomAssetManager.getTexture(CustomAssetManager.RECKONING));

        Label reckoningValue = createValue(FONT_MINYA, 0.5f);
        reckoningValue.setWrap(true);
        reckoningValue.setText("[BLACK]" + ancientOneInfo.getReckoningText() + "[]");

        table.add(reckoningImage).padLeft(10).padRight(10).width(53).height(53).align(Align.left);
        table.add(reckoningValue).growX().padLeft(5);
        return table;
    }

    private Table createEndGameTable(AncientOneInfo ancientOneInfo) {
        Table table = new Table();

        DoomTrackWidget midnightDoom = new DoomTrackWidget();
        midnightDoom.updateDoom(0);
        midnightDoom.setScale(53/593f);


        Label midnightValue = createValue(FONT_MINYA, 0.5f);
        midnightValue.setWrap(true);
        midnightValue.setText("[BLACK]" + ancientOneInfo.getMidnightText() + "[]");

        Label winLabel = createTextLabel(get("ancientOne.victory"));

        Label winValue = createValue(FONT_MINYA, 0.5f);
        winValue.setWrap(true);
        winValue.setText("[BLACK]" + ancientOneInfo.getWinText() + "[]");

        table.add(midnightDoom).padLeft(10).padRight(10).width(53).height(53).align(Align.left);
        table.add(midnightValue).width(230).padLeft(5);
        table.add(winLabel).padLeft(2).width(73);
        table.add(winValue).growX().padLeft(5);


        table.align(Align.left);
        table.padTop(5);
        return table;
    }


    private void addHitImage() {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.getColor().a = 0.0f;
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void setAfterHideAction(Action0 afterHideAction) {
        displayHide.setAfterHideAction(afterHideAction);
    }

    public void displayOrHide() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2f - getHeight() / 2);

        if (beforeDisplayAction == null) {
            displayHide.setBeforeDisplayAction(() -> {
                InfoStage.getInvestigatorHud().hide().subscribe();
                HudButtons.getTrackHud().hide().subscribe();
            });
        } else {
            displayHide.setBeforeDisplayAction(beforeDisplayAction);
        }

        displayHide.setBeforeHideAction(() -> {
            InfoStage.getInvestigatorHud().show().subscribe();
            HudButtons.getTrackHud().show().subscribe();
        });
        displayHide.displayOrHide().subscribe();
    }

    @Override
    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    private Table createTopLeftPart(AncientOneInfo ancientOneInfo) {
        VisTable table = new VisTable();

        Label nameLabel = createNameLabel(ancientOneInfo.getName());
        Label altNameLabel = createNiceLabel("-"+ancientOneInfo.getAltName()+"-");

        Image ancientOneLabel = new Image(CustomAssetManager.getTexture(ANCIENT_ONE_LABEL));
        ancientOneLabel.setScaling(Scaling.fit);

        float width = 420;
        table.add(ancientOneLabel).width(width* SCALE).height(171 * (width*SCALE / 896f)).align(Align.bottom);
        table.row();
        table.add(nameLabel).width(width * SCALE);
        table.row();
        table.add(altNameLabel).width(width * SCALE);
        table.row();
        return table;
    }

    private Label createFlavorPart(AncientOneInfo ancientOneInfo) {
        Label flavorLabel = createNiceLabel(ancientOneInfo.getFlavorText());
        flavorLabel.setAlignment(Align.bottom);
        return flavorLabel;
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

    private Label createValue(String fontName, float fontScale) {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFont(fontName), Color.WHITE);
        style.background = new ValueFieldNinePatch();

        Label label = new Label("0", style);
        style.font.getData().markupEnabled = true;
        label.setAlignment(Align.left);
        label.setFontScale(fontScale);
        return label;
    }

    private Label createTextLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.BLACK);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.5f);
        return label;
    }

    @Override
    public Completable increaseAncientOnePower(int increment) {
        return null;
    }

    @Override
    public void setAfterDisplayAction(Action0 input) {

    }
}
