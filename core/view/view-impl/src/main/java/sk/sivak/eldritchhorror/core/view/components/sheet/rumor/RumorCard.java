package sk.sivak.eldritchhorror.core.view.components.sheet.rumor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_MINYA;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MYSTERY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.VALUE_LABEL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class RumorCard extends VisTable {
    private final DisplayHide displayHide;
    private Image hitImage;
    private RumorCardInfo rumorCardInfo;
    private Cell<Table> reckoningCell;
    private Cell<Table> countdownCell;
    private Action0 beforeDisplayAction;
    private Cell<Table> failureCell;
    private Cell<Table> objectiveCell;

    public RumorCard() {
        displayHide = new DisplayHide(this, BigActorsManager.BigActorKey.RUMOR_CARD);
        displayHide.setActorKey(OnScreenActors.ActorKey.RUMOR_CARD);
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

    public void init(RumorCardInfo rumorCardInfo) {
        clear();
        getColor().a = 1f;
        setBackground((Drawable) null);
        this.rumorCardInfo = rumorCardInfo;
        int width = 495;
        int padLeft = 40;
        int padRight = 20;
        padTop(30);
        padBottom(40);
        add(createNameLabel(resolveLocalizedText(rumorCardInfo.getTitleText()))).pad(0, padLeft, 0, padRight).width(350);
        row();
        Label flavorLabel = createFlavorLabel(resolveLocalizedText(rumorCardInfo.getFlavorText()));
        Cell<Label> flavorLabelCell = add(flavorLabel).pad(5, padLeft, 0, padRight).width(width);
        row();

        objectiveCell = createOneGenericRow(get("rumor.objective"), rumorCardInfo.getObjectiveText());
        Cell<Table> requiresCluesRow = createRequiresCluesRow(rumorCardInfo.getCluesRequired());
        failureCell = createOneGenericRow(get("rumor.failure"), rumorCardInfo.getFailureText());
        reckoningCell = createReckoningRow(rumorCardInfo.getReckoningText());
        int countdownRowHeight = createCountdownRow(rumorCardInfo.getTimeRemaining());

        TextureRegionDrawable background = CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.MYSTERY_BACKGROUND);
        setBackground(background);

        setWidth(570);
        setHeight(180 + flavorLabelCell.getPrefHeight() +
                objectiveCell.getPrefHeight() +
                failureCell.getPrefHeight() +
                requiresCluesRow.getPrefHeight() +
                reckoningCell.getPrefHeight()+
                countdownRowHeight);

        addHitImage();
    }

    private Cell<Table> createOneGenericRow(String labelText, String text) {
        if (text == null) {
            return new Cell<>();
        }
        Label resolveLabel = createJustLabel(labelText);
        Label resolveTextLabel = createResolveLabel(replacePlaceholders(resolveLocalizedText(text)));

        Table resolveRow = new Table();
        resolveRow.add(resolveLabel).width(80).align(Align.right).padRight(5);
        resolveRow.add(resolveTextLabel).growX();
        Cell<Table> mysteryLabelCell = add(resolveRow).pad(5, 40, 0, 20).width(495);
        row();
        return mysteryLabelCell;
    }

    private Cell<Table> createRequiresCluesRow(Integer cluesRequired) {
        if (cluesRequired == null) {
            return new Cell<>();
        }
        Label resolveLabel = createJustLabel(get("rumor.requires"));

        Table resolveRow = new Table();
        resolveRow.add(resolveLabel).width(80).align(Align.right).padRight(5);
        for (int i = 0; i < cluesRequired; i++) {
            Image actor = new Image(CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN));
            actor.setScaling(Scaling.fit);
            actor.setAlign(Align.left);
            resolveRow.add(actor).size(36).padLeft(5).align(Align.left);
        }
        resolveRow.add().growX();
        Cell<Table> mysteryLabelCell = add(resolveRow).pad(5, 40, 0, 20).width(495);



        row();
        return mysteryLabelCell;
    }

    private int createCountdownRow(Integer countdown) {
        if (countdown == null) {
            this.countdownCell = new Cell<>();
            return 0;
        }
        CustomAssetManager.getTextureAsync("token/watch_2.png").subscribe(texture -> {
            Label resolveLabel = createJustLabel(get("rumor.timeLeft"));

            Table resolveRow = new Table();
            resolveRow.add(resolveLabel).width(80).align(Align.right).padRight(5);
            for (int i = 0; i < countdown; i++) {
                Image actor = new Image(texture);
                actor.setScaling(Scaling.fit);
                actor.setAlign(Align.left);
                resolveRow.add(actor).height(36).width(89/100f* 36).padLeft(5).align(Align.left);
            }
            resolveRow.add().growX();
            Cell<Table> mysteryLabelCell = add(resolveRow).pad(5, 40, 0, 20).width(495);



            row();
            this.countdownCell = mysteryLabelCell;
        });
        return 36;
    }

    private Cell<Table> createReckoningRow(String text) {
        Label resolveTextLabel = createResolveLabel(replacePlaceholders(resolveLocalizedText(text)));

        Table resolveRow = new Table();
        Image actor = new Image(CustomAssetManager.getTexture(CustomAssetManager.RECKONING));
        actor.setScaling(Scaling.fit);
        actor.setAlign(Align.right);
        resolveRow.add(actor).height(36).width(80).padRight(5).align(Align.right);
        resolveRow.add(resolveTextLabel).growX();
        Cell<Table> mysteryLabelCell = add(resolveRow).pad(5, 40, 0, 20).width(495);
        row();
        return mysteryLabelCell;
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

    private Label createJustLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.DARK_GRAY);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setFontScale(0.5f);
        return label;
    }

    private Label createResolveLabel(String text) {
        BitmapFont bitmapFont = getBitmapFont(FONT_MINYA);
        bitmapFont.getData().markupEnabled = true;
        Label.LabelStyle style = new Label.LabelStyle(bitmapFont, Color.WHITE);
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
                batch.setColor(new Color(1f, 1f, 1f, 0.5f * RumorCard.this.getColor().a));
                super.draw(batch, x, y, width, height);
            }
        });

        Label label = new Label(text, style);
        label.setWrap(true);
        label.setAlignment(Align.left);
        label.setFontScale(0.5f);
        label.getStyle().font.getData().markupEnabled = true;
        return label;
    }

    private Label createProgressLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_MINYA), Color.DARK_GRAY);
        Label label = new Label(get("rumor.progress"), labelStyle);
        label.setAlignment(Align.left);
        label.setFontScale(0.5f);
        return label;
    }

    public void setAfterHideAction(Action0 afterHideAction) {
        displayHide.setAfterHideAction(afterHideAction);
    }


    public void displayOrHide() {
        displayHide.setDisplayedY(VIEWPORT_HEIGHT / 2 - getHeight() / 2);

        displayHide.setBeforeDisplayAction(() -> {
            InfoStage.displayTextDontHide(get("rumor.ongoing"));
            if (rumorCardInfo.getRumorLocation() != null) {
                MapUtils.moveCameraToLocation(rumorCardInfo.getRumorLocation()).subscribe();
            }
            if (beforeDisplayAction != null) {
                beforeDisplayAction.call();
            }
        });


        displayHide.setBeforeHideAction(() -> InfoStage.hideLabel(get("mystery.current")));

        displayHide.displayOrHide().subscribe(() -> {

        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try {
            super.draw(batch, parentAlpha);
        } catch (Exception ex) {
            // lets suppress this one
        }
    }

    private static String replacePlaceholders(String text) {
        return text
                .replaceAll("\\[#BAD]", "[#e02323]")
                .replaceAll("\\[#GOOD]", "[#229B3C]");
    }

    private String resolveLocalizedText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        String localized = get(text);
        String missingKey = "!" + text + "!";
        if (missingKey.equals(localized)) {
            return text;
        }
        return localized;
    }

    public Completable countdown(int amount) {
        if (countdownCell == null) {
            return Completable.create(onSub -> {
                Gdx.app.postRunnable(() -> {
                    countdown(amount).subscribe(onSub::onCompleted);
                });
            });

        }
        PublishSubject<Object> publishSubject = PublishSubject.create();
        Image darkFront = addDarkFront();
        Table reckoningTable = highlightCell(reckoningCell);
        Table countdownTable = highlightCell(countdownCell);
        highlightLabel((Label) countdownTable.getCells().get(0).getActor());

        // minus 2, because last actor is placeholder

        addClickListener(darkFront, () -> {
            for (int i = 0; i < amount; i++) {
                Image pocketWatch = (Image) countdownTable.getCells().get(countdownTable.getCells().size - 2 - i).getActor();
                pocketWatch.setOrigin(pocketWatch.getWidth()/2f, pocketWatch.getHeight()/2f);
                pocketWatch.addAction(new FastForwardAction<>(Actions.scaleTo(0,0,1f)));
            }


            darkFront.addAction(new FastForwardAction<>(Actions.delay(1f, Actions.sequence(
                    Actions.run(() -> darkenLabel((Label) countdownTable.getCells().get(0).getActor())),
                    Actions.alpha(0.0f, 0.5f),
                    Actions.run(() -> {
                        reckoningCell.setActor(reckoningTable);
                        countdownCell.setActor(countdownTable);
                        darkFront.remove();
                        publishSubject.onCompleted();
                    })
            ))));
        });

        return publishSubject.toCompletable();
    }

    public Completable highlightRumorFailure() {
        return highlightRumorSomething(failureCell);
    }

    public Completable highlightRumorObjective() {
        return highlightRumorSomething(objectiveCell);
    }

    public Completable highlightRumorReckoning() {
        return highlightRumorSomething(reckoningCell);
    }

    private Completable highlightRumorSomething(Cell<?> cell) {
        PublishSubject<Object> publishSubject = PublishSubject.create();
        Image darkFront = addDarkFront();
        Table cellTable = highlightCell(cell);
        Actor firstActor = cellTable.getCells().get(0).getActor();
        if (firstActor instanceof Label) {
            highlightLabel((Label) firstActor);
        }

        addClickListener(darkFront, () -> {

            darkFront.addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.run(() -> {
                        if (firstActor instanceof Label) {
                            darkenLabel((Label) firstActor);
                        }
                    }),
                    Actions.alpha(0.0f, 0.5f),
                    Actions.run(() -> {
                        cell.setActor(cellTable);
                        darkFront.remove();
                        publishSubject.onCompleted();
                    })
            )));
        });

        return publishSubject.toCompletable();
    }

    private Image addDarkFront() {
        Image darkFront = new Image(CustomAssetManager.getTexture(MYSTERY_BACKGROUND));
        darkFront.setColor(new Color(0f, 0f, 0f, 0.0f));
        darkFront.setPosition(getX(), getY());
        darkFront.setSize(getWidth(), getHeight());
        getStage().addActor(darkFront);
        darkFront.addAction(new FastForwardAction<>(Actions.alpha(0.75f, 0.5f)));
        return darkFront;
    }

    private void highlightLabel(Label label) {
        Label.LabelStyle labelStyle = label.getStyle();

        getStage().addAction(new FastForwardAction<>(new TemporalAction(0.5f) {
            @Override
            protected void update(float percent) {

                float rgb = Color.DARK_GRAY.r;
                float colorDiff = Color.LIGHT_GRAY.r - Color.DARK_GRAY.r;
                float v = percent * colorDiff;
                labelStyle.fontColor = new Color(rgb + v, rgb + v, rgb + v, 1f);
            }
        }));
    }

    private void darkenLabel(Label label) {
        Label.LabelStyle labelStyle = label.getStyle();

        if (getStage() == null) {
            return;
        }
        getStage().addAction(new FastForwardAction<>(new TemporalAction(0.5f) {
            @Override
            protected void update(float percent) {

                float rgb = Color.LIGHT_GRAY.r;
                float colorDiff = Color.DARK_GRAY.r - Color.LIGHT_GRAY.r;
                float v = percent * colorDiff;
                labelStyle.fontColor = new Color(rgb + v, rgb + v, rgb + v, 1f);
            }
        }));
    }

    private <T extends Actor> T highlightCell(Cell<?> cell) {
        Actor cellActor = cell.getActor();
        float cellHeight = cell.getPrefHeight();
        Vector2 tablePosition = cellActor.localToStageCoordinates(new Vector2());
        cellActor.setPosition(tablePosition.x, tablePosition.y);
        cellActor.setTouchable(Touchable.disabled);
        getStage().addActor(cellActor);
        cell.height(cellHeight);
        return (T) cellActor;
    }

    public void setBeforeDisplayAction(Action0 beforeDisplayAction) {
        this.beforeDisplayAction = beforeDisplayAction;
    }

    public void setAfterDisplayAction(Action0 afterDisplayAction) {
        displayHide.setAfterDisplayAction(afterDisplayAction);
    }

    public RumorCardInfo getRumorCardInfo() {
        return rumorCardInfo;
    }
}
