package sk.sivak.eldritchhorror.core.view.components.sheet.mystery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;

import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.VALUE_LABEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.components.sheet.mystery.ProgressTokenBar.ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class MysteryCard extends VisTable {

    private final DisplayHide displayHide;
    private ProgressTokenBar progressTokenBar;
    private Image hitImage;
    private MysteryCardInfo mysteryCardInfo;
    private boolean moveCamera = false;
    private Action0 beforeDisplayAction;

    public MysteryCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.MYSTERY_CARD);
        displayHide.setActorKey(OnScreenActors.ActorKey.MYSTERY_CARD);
        setTransform(true);
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

    public void init(MysteryCardInfo mysteryCardInfo) {
        clear();
        getColor().a = 1f;
        setBackground((Drawable) null);
        this.mysteryCardInfo = mysteryCardInfo;
        int width = 495;
        int padLeft = 40;
        int padRight = 20;
        add(createNameLabel(mysteryCardInfo.getName())).pad(30, padLeft, 0, padRight).width(360);
        row();
        Label flavorLabel = createFlavorLabel(mysteryCardInfo.getFlavorText());
        Cell<Label> flavorLabelCell = add(flavorLabel).pad(5, padLeft, 0, padRight).width(width);
        row();
        Label mysteryTextLabel = createMysteryText(getProcessedMysteryText(mysteryCardInfo));
        Cell<Label> mysteryLabelCell = add(mysteryTextLabel).pad(5, padLeft, 0, padRight).width(width);
        row();
        add(createProgressLabel()).pad(5, padLeft, 0, padRight).width(width);
        row();
        progressTokenBar = createProgressTokenBar(mysteryCardInfo.getMysteryComplexity(), Math.min(mysteryCardInfo.getProgress(), mysteryCardInfo.getMysteryComplexity()));
        add(progressTokenBar).pad(10, padLeft, 40, padRight).width(width);


        TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.MYSTERY_BACKGROUND);
        setBackground(background);

        setWidth(555);
        setHeight(flavorLabelCell.getPrefHeight() + mysteryLabelCell.getPrefHeight() + progressTokenBar.getPrefHeight() + 145);


        addHitImage();
    }

    private String getProcessedMysteryText(MysteryCardInfo mysteryCardInfo) {
        return mysteryCardInfo.getMysteryText().replaceAll("COMPLEXITY", mysteryCardInfo.getMysteryComplexity().toString());
    }

    private ProgressTokenBar createProgressTokenBar(Integer mysteryComplexity, Integer progress) {
        ProgressTokenBar progressTokenBar = new ProgressTokenBar();
        progressTokenBar.init(mysteryComplexity, progress);
        return progressTokenBar;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.DARK_GRAY);
        Color color = new Color(1f, 1f, 1f, 0.25f);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                color.a = 0.25f * getColor().a;
                batch.setColor(color);
                super.draw(batch, x, y, width, height);
            }
        };
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    private Label createFlavorLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), Color.DARK_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createMysteryText(String text) {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.BLACK);
        style.background = new NinePatchDrawable(new NinePatch(CustomAssetManager.getTexture(VALUE_LABEL), 11, 11, 11, 12) {

            @Override
            public float getMiddleHeight() {
                return 0;
            }

            @Override
            public float getTopHeight() {
                return 6;
            }

            @Override
            public float getBottomHeight() {
                return 8;
            }

            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(new Color(1f, 1f, 1f, 0.5f * MysteryCard.this.getColor().a));
                super.draw(batch, x, y, width, height);
            }
        });

        Label label = new Label(text, style);
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createProgressLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.DARK_GRAY);
        Label label = new Label(get("mystery.progress"), labelStyle);
        label.setAlignment(Align.left);
        label.setFontScale(0.5f);
        return label;
    }

    private Completable activate(Integer advanceActiveMysteryAmount) {
        return Completable.create(onSub -> {
            for (int i = 0; i < advanceActiveMysteryAmount; i++) {
                progressTokenBar.activate();
            }
            onSub.onCompleted();
        }).delay((long) (ACTION_DURATION * 1000), TimeUnit.MILLISECONDS);
    }

    public void setAfterHideAction(Action0 afterHideAction) {
        displayHide.setAfterHideAction(afterHideAction);
    }

    private Integer advanceActiveMysteryAmount;

    public void advanceActiveMystery(int amount) {
        this.advanceActiveMysteryAmount = amount;
    }

    public void displayOrHide() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2 - getHeight() / 2);

        displayHide.setBeforeDisplayAction(() -> {
            InfoStage.displayTextDontHide(get("mystery.current"));
            if (mysteryCardInfo.getPinLocations() != null && !mysteryCardInfo.getPinLocations().isEmpty() && moveCamera) {
                int pinLocationIndex = MathUtils.random(0, mysteryCardInfo.getPinLocations().size() - 1);
                MapUtils.moveCameraToLocation(mysteryCardInfo.getPinLocations().get(pinLocationIndex)).subscribe();
            }
            if (beforeDisplayAction != null) {
                beforeDisplayAction.call();
            }
        });


        displayHide.setBeforeHideAction(() -> InfoStage.hideLabel(get("mystery.current")));

        if (advanceActiveMysteryAmount != null) {
            progressTokenBar.init(mysteryCardInfo.getMysteryComplexity(), mysteryCardInfo.getProgress() - advanceActiveMysteryAmount);
        }
        displayHide.displayOrHide().subscribe(() -> {
            if (advanceActiveMysteryAmount != null) {
                activate(advanceActiveMysteryAmount).subscribe();
                advanceActiveMysteryAmount = null;
            }
        });
    }

    public void setMoveCamera(boolean moveCamera) {
        this.moveCamera = moveCamera;
    }

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try {
            super.draw(batch, parentAlpha);
        } catch (Exception ex) {
            // lets suppress this one
        }
    }
}
