package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.table.ActorFrame;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class SelectAncientOneTable extends VisTable {


    private final ScaleImageUpAction azathothScaleImageUpAction;
    private final ScaleImageDownAction azathothScaleImageDownAction;
    private final Table azathothTable;
    private final ScaleImageUpAction cthulhuScaleImageUpAction;
    private final ScaleImageDownAction cthulhuScaleImageDownAction;
    private final ScaleImageUpAction yogSothothScaleImageUpAction;
    private final ScaleImageDownAction yogSothothScaleImageDownAction;
    private final ScaleImageUpAction shubNiggurathScaleImageUpAction;
    private final ScaleImageDownAction shubNiggurathScaleImageDownAction;
    private final Table cthulhuTable;
    private final Table shubNiggurathTable;
    private final Table yogSothothTable;
    private ActorFrame frame;

    public SelectAncientOneTable(List<AncientOneInfo> availableAncientOnes,
                                 boolean isCthulhuPurchased,
                                 boolean isShubNiggurathPurchased,
                                 boolean isYogSothothPurchased,
                                 SingleSubscriber<? super AncientOneInfo> sub) {


        int imageSize = 230;

        azathothTable = new Table();
        Image buttonAzathoth = new Image(CustomAssetManager.getTexture("ancient_one/button_azathoth.jpg"));
        buttonAzathoth.setScaling(Scaling.fit);
        Cell<Image> azathothButtonCell = azathothTable.add(buttonAzathoth).width(imageSize).height(imageSize);
        azathothScaleImageUpAction = new ScaleImageUpAction(azathothButtonCell, imageSize-30, imageSize-10);
        azathothScaleImageUpAction.setDuration(0.75f);
        azathothScaleImageUpAction.setInterpolation(Interpolation.sine);
        azathothScaleImageDownAction = new ScaleImageDownAction(azathothButtonCell, imageSize-30, imageSize-10);
        azathothScaleImageDownAction.setDuration(0.75f);
        azathothScaleImageDownAction.setInterpolation(Interpolation.sine);

        azathothButtonCell.row();
        azathothTable.add(createNameLabel(get("ancientOne.azathoth.name"))).width(imageSize).padLeft(10).padRight(10).height(25).row();
        azathothTable.add(createNiceLabel(get("ancientOne.azathoth.alt"))).padBottom(5);

        // Cthulhu
        cthulhuTable = new Table();
        Image buttonCthulhu = new Image(CustomAssetManager.getTexture("ancient_one/button_cthulhu.jpg"));
        Image lockImage = new Image(CustomAssetManager.getTexture("ancient_one/lock.png"));
        lockImage.getColor().a = 1.0f;
        buttonCthulhu.setScaling(Scaling.fit);
        lockImage.setScaling(Scaling.fit);

        Cell<?> cthulhuButtonCell;
        if (isCthulhuPurchased) {
            cthulhuButtonCell = cthulhuTable.add(buttonCthulhu).width(imageSize).height(imageSize);
        } else {
            cthulhuButtonCell = cthulhuTable.add(new Stack(buttonCthulhu, lockImage)).width(imageSize).height(imageSize);
        }

        cthulhuScaleImageUpAction = new ScaleImageUpAction(cthulhuButtonCell, imageSize-30, imageSize-10);
        cthulhuScaleImageUpAction.setDuration(0.75f);
        cthulhuScaleImageUpAction.setInterpolation(Interpolation.sine);
        cthulhuScaleImageDownAction = new ScaleImageDownAction(cthulhuButtonCell, imageSize-30, imageSize-10);
        cthulhuScaleImageDownAction.setDuration(0.75f);
        cthulhuScaleImageDownAction.setInterpolation(Interpolation.sine);

        cthulhuButtonCell.row();

        cthulhuTable.add(createNameLabel(get("ancientOne.cthulhu.name"))).width(imageSize).padLeft(10).padRight(10).height(25).row();
        cthulhuTable.add(createNiceLabel(get("ancientOne.cthulhu.alt"))).padBottom(5).row();
        if (!isCthulhuPurchased) {
            cthulhuTable.add(createFeaturesSection(
                    get("ancientOne.features.sixMysteries"),
                    get("ancientOne.features.threeEpicMonsters"),
                    get("ancientOne.features.eightyEncounters"),
                    get("ancientOne.features.exploreRlyeh"),
                    get("ancientOne.features.endingRisenFromSea"),
                    get("ancientOne.features.priceCoffee")
            )).padBottom(10).align(Align.left);
        }

        // Shub Niggurath
        shubNiggurathTable = new Table();
        Image buttonShubNiggurath = new Image(CustomAssetManager.getTexture("ancient_one/button_shub_niggurath.jpg"));
        Image lockImage2 = new Image(CustomAssetManager.getTexture("ancient_one/lock.png"));
        lockImage2.getColor().a = 1.0f;
        buttonShubNiggurath.setScaling(Scaling.fit);
        lockImage2.setScaling(Scaling.fit);

        Cell<?> shubNiggurathButtonCell;
        if (isShubNiggurathPurchased) {
            shubNiggurathButtonCell = shubNiggurathTable.add(buttonShubNiggurath).width(imageSize).height(imageSize);
        } else {
            shubNiggurathButtonCell = shubNiggurathTable.add(new Stack(buttonShubNiggurath, lockImage2)).width(imageSize).height(imageSize);
        }

        shubNiggurathScaleImageUpAction = new ScaleImageUpAction(shubNiggurathButtonCell, imageSize-30, imageSize-10);
        shubNiggurathScaleImageUpAction.setDuration(0.75f);
        shubNiggurathScaleImageUpAction.setInterpolation(Interpolation.sine);
        shubNiggurathScaleImageDownAction = new ScaleImageDownAction(shubNiggurathButtonCell, imageSize-30, imageSize-10);
        shubNiggurathScaleImageDownAction.setDuration(0.75f);
        shubNiggurathScaleImageDownAction.setInterpolation(Interpolation.sine);

        shubNiggurathButtonCell.row();

        shubNiggurathTable.add(createNameLabel(get("ancientOne.shubNiggurath.name"))).width(imageSize).padLeft(10).padRight(10).height(25).row();
        shubNiggurathTable.add(createNiceLabel(get("ancientOne.shubNiggurath.alt"))).padBottom(5).row();
        if (!isShubNiggurathPurchased) {
            shubNiggurathTable.add(createFeaturesSection(
                    get("ancientOne.features.sixMysteries"),
                    get("ancientOne.features.threeEpicMonsters"),
                    get("ancientOne.features.seventyEncounters"),
                    get("ancientOne.features.combatOriented"),
                    get("ancientOne.features.endingBattleInWoods"),
                    get("ancientOne.features.priceCoffee")
            )).padBottom(10).align(Align.left);
        }

        // Yog-Sothoth
        yogSothothTable= new Table();
        Image buttonYogSothoth = new Image(CustomAssetManager.getTexture("ancient_one/button_yog_sothoth.jpg"));
        Image lockImage3 = new Image(CustomAssetManager.getTexture("ancient_one/lock.png"));
        lockImage3.getColor().a = 1.0f;
        buttonYogSothoth.setScaling(Scaling.fit);
        lockImage3.setScaling(Scaling.fit);

        Cell<?> yogSothothButtonCell;
        if (isYogSothothPurchased) {
            yogSothothButtonCell = yogSothothTable.add(buttonYogSothoth).width(imageSize).height(imageSize);
        } else {
            yogSothothButtonCell = yogSothothTable.add(new Stack(buttonYogSothoth, lockImage3)).width(imageSize).height(imageSize);
        }

        yogSothothScaleImageUpAction = new ScaleImageUpAction(yogSothothButtonCell, imageSize-30, imageSize-10);
        yogSothothScaleImageUpAction.setDuration(0.75f);
        yogSothothScaleImageUpAction.setInterpolation(Interpolation.sine);
        yogSothothScaleImageDownAction = new ScaleImageDownAction(yogSothothButtonCell, imageSize-30, imageSize-10);
        yogSothothScaleImageDownAction.setDuration(0.75f);
        yogSothothScaleImageDownAction.setInterpolation(Interpolation.sine);

        yogSothothButtonCell.row();

        yogSothothTable.add(createNameLabel(get("ancientOne.yogSothoth.name"))).width(imageSize).padLeft(10).padRight(10).height(25).row();
        yogSothothTable.add(createNiceLabel(get("ancientOne.yogSothoth.alt"))).padBottom(5).row();
        if (!isYogSothothPurchased) {
            yogSothothTable.add(createFeaturesSection(
                    get("ancientOne.features.sixMysteries"),
                    get("ancientOne.features.dunwichHorror"),
                    get("ancientOne.features.eightyEncounters"),
                    get("ancientOne.features.visitVoidBetweenWorlds"),
                    get("ancientOne.features.endingKeyAndGate"),
                    get("ancientOne.features.priceCoffee")
            )).padBottom(10).align(Align.left);
        }

        List<Table> ancientOneTables = new LinkedList<>(Arrays.asList(azathothTable, cthulhuTable, shubNiggurathTable, yogSothothTable));
        Collections.shuffle(ancientOneTables);
        add(createTitleLabel(get("init.selectAncientOne"))).colspan(7).row();
        addSeparator().colspan(7).padBottom(0).row();
        add(ancientOneTables.get(0)).align(Align.top);
        addSeparator(true).padTop(0).padLeft(5).padRight(5);
        add(ancientOneTables.get(1)).align(Align.top);
        addSeparator(true).padTop(0).padLeft(5).padRight(5);
        add(ancientOneTables.get(2)).align(Align.top);
        addSeparator(true).padTop(0).padLeft(5).padRight(5);
        add(ancientOneTables.get(3)).align(Align.top);

        pack();

        setBackground(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.GRAY_BACKGROUND));

        addClickListener(azathothTable, () -> {
            sub.onSuccess(availableAncientOnes.get(0));
            remove();
            frame.remove();
        });
        if (isCthulhuPurchased) {
            addClickListener(cthulhuTable, () -> {
                sub.onSuccess(availableAncientOnes.get(1));
                remove();
                frame.remove();
            });
        } else {
            addClickListener(cthulhuTable, () -> {
                new InAppPurchaseManager().purchaseProduct("cthulhu").subscribe(purchaseResult -> {
                    if (purchaseResult) {
                        sub.onSuccess(availableAncientOnes.get(1));
                        remove();
                        frame.remove();
                    }
                });
            });
        }

        if (isShubNiggurathPurchased) {
            addClickListener(shubNiggurathTable, () -> {
                sub.onSuccess(availableAncientOnes.get(2));
                remove();
                frame.remove();
            });
        } else {
            addClickListener(shubNiggurathTable, () -> {
                new InAppPurchaseManager().purchaseProduct("shub_niggurath").subscribe(purchaseResult -> {
                    if (purchaseResult) {
                        sub.onSuccess(availableAncientOnes.get(2));
                        remove();
                        frame.remove();
                    }
                });
            });
        }

        if (isYogSothothPurchased) {
            addClickListener(yogSothothTable, () -> {
                sub.onSuccess(availableAncientOnes.get(3));
                remove();
                frame.remove();
            });
        } else {
            addClickListener(yogSothothTable, () -> {
                new InAppPurchaseManager().purchaseProduct("yog_sothoth").subscribe(purchaseResult -> {
                    if (purchaseResult) {
                        sub.onSuccess(availableAncientOnes.get(3));
                        remove();
                        frame.remove();
                    }
                });
            });
        }
    }

    public void animate() {
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                azathothScaleImageUpAction,
                azathothScaleImageDownAction
        )));
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                cthulhuScaleImageDownAction,
                cthulhuScaleImageUpAction

        )));
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                shubNiggurathScaleImageUpAction,
                shubNiggurathScaleImageDownAction

        )));
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                yogSothothScaleImageDownAction,
                yogSothothScaleImageUpAction

        )));
    }

    private Label createFeaturesSection(String... rows) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), Color.WHITE);
        StringBuilder text = new StringBuilder();
        for (String row : rows) {
            text.append(row).append("\n");
        }
        text.delete(text.length()-1, text.length());
        Label label = new Label(text.toString(), labelStyle);
        label.setAlignment(Align.left);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), Color.WHITE);
        Color color = new Color(0f, 0f, 0f, 0.5f);
        labelStyle.background = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(PURE_WHITE_BACKGROUND)) {
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
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), Color.LIGHT_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createTitleLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), Color.GREEN);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.9f);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    public void setFrame(ActorFrame frame) {
        this.frame = frame;
    }

    private class ScaleImageUpAction extends TemporalAction {

        private Cell<?> cell;
        private final float padMin;
        private final float padMax;

        public ScaleImageUpAction(Cell<?> cell, float padMin, float padMax) {
            this.cell = cell;
            this.padMin = padMin;
            this.padMax = padMax;
        }

        @Override
        protected void update(float percent) {
            float padding = padMin + percent * (padMax - padMin);
            cell.width(padding);
            azathothTable.invalidate();
            cthulhuTable.invalidate();
            shubNiggurathTable.invalidate();
            yogSothothTable.invalidate();
        }
    }

    private class ScaleImageDownAction extends TemporalAction {

        private Cell<?> cell;
        private final float padMin;
        private final float padMax;

        public ScaleImageDownAction(Cell<?> cell, float padMin, float padMax) {
            this.cell = cell;
            this.padMin = padMin;
            this.padMax = padMax;
        }

        @Override
        protected void update(float percent) {
            float padding = padMax - percent * (padMax - padMin);
            cell.width(padding);
            azathothTable.invalidate();
            cthulhuTable.invalidate();
            shubNiggurathTable.invalidate();
            yogSothothTable.invalidate();
        }
    }

}
