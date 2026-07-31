package sk.sivak.eldritchhorror.core.view.components.sheet.ancientone;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.components.sheet.ValueFieldNinePatch;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;
import sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget;
import sk.sivak.eldritchhorror.core.view.components.track.OmenTrack;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;

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

public class AzathothCard extends VisTable implements AncientOneCard{

    public static final float SCALE = 0.72f;
    private final DisplayHide displayHide;
    private AncientOneInfo ancientOneInfo;
    private Image hitImage;
    private Action0 afterHideAction;
    private Action0 beforeDisplayAction;

    public AzathothCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.ANCIENT_ONE);
        displayHide.setActorKey(OnScreenActors.ActorKey.ANCIENT_ONE_CARD);
        setTransform(true);
    }

    public void init(AncientOneInfo ancientOneInfo) {
        if (this.ancientOneInfo == ancientOneInfo) {
            return;
        }
        clear();
        align(Align.bottom);
        setBackground((Drawable) null);
        getColor().a = 1f;
        this.ancientOneInfo = ancientOneInfo;
        pad(50 * SCALE, 50 * SCALE, 75 * SCALE, 20 * SCALE);


        Table topPart = createTopPart(ancientOneInfo);
        Table leftPart = createLeftPart(ancientOneInfo);
        Table rightPart = createRightPart(ancientOneInfo);

        add(topPart).colspan(2).growY();
        row();
        add(leftPart).align(Align.topLeft);
        add(rightPart).align(Align.topRight);

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
    public Actor getActor() {
        return this;
    }

    private Table createTopPart(AncientOneInfo ancientOneInfo) {
        Table table = new Table();
        CustomAssetManager.getTextureAsync("ancient_one/" + ancientOneInfo.getAncientOneId() + ".png").subscribe(ok -> {
            Image image = new Image(CustomAssetManager.getTexture("ancient_one/" + ancientOneInfo.getAncientOneId() + ".png"));
            image.setScaling(Scaling.fit);
            table.add(image).grow();
        });

        return table;
    }

    private Table createLeftPart(AncientOneInfo ancientOneInfo) {
        VisTable table = new VisTable();

        Label nameLabel = createNameLabel(ancientOneInfo.getName());
        Label altNameLabel = createNiceLabel("-"+ancientOneInfo.getAltName()+"-");
        Label flavorLabel = createNiceLabel(ancientOneInfo.getFlavorText());
        flavorLabel.setAlignment(Align.bottom);

        Image ancientOneLabel = new Image(CustomAssetManager.getTexture(ANCIENT_ONE_LABEL));
        ancientOneLabel.setScaling(Scaling.fit);

        table.add(ancientOneLabel).width(490 * SCALE).height(171 * (490*SCALE / 896f)).align(Align.bottom);
        table.row();
        table.add(nameLabel).width(490 * SCALE);
        table.row();
        table.add(altNameLabel).width(490 * SCALE);
        table.row();
        table.addSeparator();
        table.add(flavorLabel).width(490 * SCALE).height(114); // to make both sides same height
        return table;
    }

    private Table createRightPart(AncientOneInfo ancientOneInfo) {
        Table table = new Table();

        Label setupLabel = createTextLabel(get("ancientOne.setup"));

        Label setupValue = createValue(FONT_MINYA, 0.5f);
        setupValue.setWrap(true);
        setupValue.setText("[BLACK]" + ancientOneInfo.getSetupText() + "[]");

        DoomTrackWidget midnightDoom = new DoomTrackWidget();
        midnightDoom.updateDoom(0);
        midnightDoom.setScale(73/593f);

        Label midnightValue = createValue(FONT_MINYA, 0.5f);
        midnightValue.setWrap(true);
        midnightValue.setText("[BLACK]" + ancientOneInfo.getMidnightText() + "[]");

        OmenTrack omenTrack = new OmenTrack();
        omenTrack.updateOmen(OmenId.NORTH);
        omenTrack.setScale(73 / 700f);

        Label specialValue = createValue(FONT_MINYA, 0.5f);
        specialValue.setWrap(true);
        specialValue.setText("[BLACK]" + ancientOneInfo.getSpecialText() + "[]");

        Label winLabel = createTextLabel(get("ancientOne.victory"));

        Label winValue = createValue(FONT_MINYA, 0.5f);
        winValue.setWrap(true);
        winValue.setText("[BLACK]" + ancientOneInfo.getWinText() + "[]");


        table.add(setupLabel).width(73);
        table.add(setupValue).width(490 * SCALE - 73- 5).padLeft(5);
        table.row();
        table.add(midnightDoom).width(73).padTop(5).height(73).align(Align.left);
        table.add(midnightValue).width(490 * SCALE - 73- 5).padTop(5).padLeft(5);
        table.row();
        table.add(omenTrack).width(73).padTop(5).height(73).align(Align.left);
        table.add(specialValue).width(490 * SCALE - 73- 5).padTop(5).padLeft(5);
        table.row();
        table.add(winLabel).width(73).padTop(5);
        table.add(winValue).width(490 * SCALE - 73- 5).padTop(5).padLeft(5);
        table.row();

        return table;
    }

    private Label createTextLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.BLACK);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
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

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    @Override
    public Completable increaseAncientOnePower(int increment) {
        return null;
    }

    @Override
    public void setAfterDisplayAction(Action0 input) {

    }
}
