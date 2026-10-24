package sk.sivak.eldritchhorror.core.view.components.sheet.rumor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class RumorCard extends VisTable {
    private static final int CARD_WIDTH = 620;
    private static final int CONTENT_PADDING = 28;
    private static final int CONTENT_WIDTH = CARD_WIDTH - 2 * CONTENT_PADDING;
    private static final int LABEL_WIDTH = 95;
    private static final int COLUMN_GAP = 10;
    private static final int ROW_GAP = 10;
    private static final Color TEXT = Color.valueOf("EEE6D5");
    private static final Color MUTED_TEXT = Color.valueOf("B8C4BE");
    private static final Color BRASS = Color.valueOf("DCC99F");
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
        if (hitImage != null) hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    public void init(RumorCardInfo rumorCardInfo) {
        clear();
        hitImage = null;
        getColor().a = 1f;
        setBackground((Drawable) null);
        this.rumorCardInfo = rumorCardInfo;

        padTop(26);
        padBottom(26);
        add(createNameLabel(resolveLocalizedText(rumorCardInfo.getTitleText()))).pad(0, CONTENT_PADDING, 0, CONTENT_PADDING).width(CONTENT_WIDTH).padBottom(12);
        row();
        Image divider = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        divider.setColor(BRASS);
        add(divider).width(CONTENT_WIDTH).height(1).padBottom(14);
        row();
        Label flavorLabel = createFlavorLabel(resolveLocalizedText(rumorCardInfo.getFlavorText()));
        add(flavorLabel).pad(0, CONTENT_PADDING, 6, CONTENT_PADDING).width(CONTENT_WIDTH);
        row();

        objectiveCell = createOneGenericRow(get("rumor.objective"), rumorCardInfo.getObjectiveText());
        createRequiresCluesRow(rumorCardInfo.getCluesRequired());
        failureCell = createOneGenericRow(get("rumor.failure"), rumorCardInfo.getFailureText());
        reckoningCell = createReckoningRow(rumorCardInfo.getReckoningText());
        createCountdownRow(rumorCardInfo.getTimeRemaining());

        TextureRegionDrawable background = new TextureRegionDrawable(CustomAssetManager.getTextureRegion(CustomAssetManager.RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        setBackground(background);
        padTop(26);
        padBottom(26);
        setWidth(CARD_WIDTH);
        setHeight(VIEWPORT_HEIGHT);
        invalidateHierarchy();
        // First assign widths to nested wrapped labels, then measure their actual height.
        validate();
        setHeight(getPrefHeight());
        validate();
        addHitImage();
    }

    private Cell<Table> createOneGenericRow(String labelText, String text) {
        if (text == null) {
            return new Cell<>();
        }
        Label resolveLabel = createJustLabel(labelText);
        Label resolveTextLabel = createResolveLabel(replacePlaceholders(resolveLocalizedText(text)));

        Table resolveRow = new Table();
        resolveRow.setBackground(SelectionPanelStyle.panel("1C302F", "415851", 10, 12));
        resolveRow.add(resolveLabel).width(LABEL_WIDTH).align(Align.right).padRight(COLUMN_GAP);
        resolveRow.add(resolveTextLabel).growX();
        Cell<Table> mysteryLabelCell = add(resolveRow).pad(ROW_GAP, CONTENT_PADDING, 0, CONTENT_PADDING).width(CONTENT_WIDTH);
        row();
        return mysteryLabelCell;
    }

    private Cell<Table> createRequiresCluesRow(Integer cluesRequired) {
        if (cluesRequired == null) {
            return new Cell<>();
        }
        Label resolveLabel = createJustLabel(get("rumor.requires"));

        Table resolveRow = new Table();
        resolveRow.setBackground(SelectionPanelStyle.panel("1C302F", "415851", 10, 12));
        resolveRow.add(resolveLabel).width(LABEL_WIDTH).align(Align.right).padRight(COLUMN_GAP);
        for (int i = 0; i < cluesRequired; i++) {
            Image actor = new Image(CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN));
            actor.setScaling(Scaling.fit);
            actor.setAlign(Align.left);
            resolveRow.add(actor).size(36).padLeft(5).align(Align.left);
        }
        resolveRow.add().growX();
        Cell<Table> mysteryLabelCell = add(resolveRow).pad(ROW_GAP, CONTENT_PADDING, 0, CONTENT_PADDING).width(CONTENT_WIDTH);



        row();
        return mysteryLabelCell;
    }

    private int createCountdownRow(Integer countdown) {
        if (countdown == null) {
            this.countdownCell = new Cell<>();
            return 0;
        }
        com.badlogic.gdx.graphics.Texture texture = CustomAssetManager.getTexture("token/watch_2.png");
            Label resolveLabel = createJustLabel(get("rumor.timeLeft"));

            Table resolveRow = new Table();
        resolveRow.setBackground(SelectionPanelStyle.panel("1C302F", "415851", 10, 12));
            resolveRow.add(resolveLabel).width(LABEL_WIDTH).align(Align.right).padRight(COLUMN_GAP);
            for (int i = 0; i < countdown; i++) {
                Image actor = new Image(texture);
                actor.setScaling(Scaling.fit);
                actor.setAlign(Align.left);
                resolveRow.add(actor).height(36).width(89/100f* 36).padLeft(5).align(Align.left);
            }
            resolveRow.add().growX();
            Cell<Table> mysteryLabelCell = add(resolveRow).pad(ROW_GAP, CONTENT_PADDING, 0, CONTENT_PADDING).width(CONTENT_WIDTH);



            row();
            this.countdownCell = mysteryLabelCell;
        return 36;
    }

    private Cell<Table> createReckoningRow(String text) {
        Label resolveTextLabel = createResolveLabel(replacePlaceholders(resolveLocalizedText(text)));

        Table resolveRow = new Table();
        resolveRow.setBackground(SelectionPanelStyle.panel("1C302F", "415851", 10, 12));
        Image actor = new Image(CustomAssetManager.getTexture(CustomAssetManager.RECKONING));
        actor.setScaling(Scaling.fit);
        actor.setAlign(Align.right);
        resolveRow.add(actor).height(36).width(LABEL_WIDTH).padRight(COLUMN_GAP).align(Align.right);
        resolveRow.add(resolveTextLabel).growX();
        Cell<Table> mysteryLabelCell = add(resolveRow).pad(ROW_GAP, CONTENT_PADDING, 0, CONTENT_PADDING).width(CONTENT_WIDTH);
        row();
        return mysteryLabelCell;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 48), BRASS);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.5f);
        label.setWrap(true);
        return label;
    }

    private Label createFlavorLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 44), MUTED_TEXT);
        Label label = new Label(text.replaceAll("\\s*\\n\\s*", " "), labelStyle);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setFontScale(0.45f);
        return label;
    }

    private Label createJustLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), BRASS);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.right);
        label.setWrap(true);
        label.setFontScale(0.45f);
        return label;
    }

    private Label createResolveLabel(String text) {
        BitmapFont bitmapFont = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40);
        bitmapFont.getData().markupEnabled = true;
        Label.LabelStyle style = new Label.LabelStyle(bitmapFont, TEXT);
        Label label = new Label(text, style);
        label.setWrap(true);
        label.setAlignment(Align.left);
        label.setFontScale(0.45f);
        label.getStyle().font.getData().markupEnabled = true;
        return label;
    }

    private Label createProgressLabel() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), BRASS);
        Label label = new Label(get("rumor.progress"), labelStyle);
        label.setAlignment(Align.left);
        label.setFontScale(0.45f);
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


        displayHide.setBeforeHideAction(() -> InfoStage.hideLabel(get("rumor.ongoing")));

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
                .replaceAll("\\[#BAD]", "[#EF9B8F]")
                .replaceAll("\\[#GOOD]", "[#A7D8AF]");
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
        if (amount <= 0) return Completable.complete();
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
            // Keep intercepting input while the countdown resolves, without accepting another click.
            darkFront.clearListeners();
            GameplaySounds.play(GameplaySounds.Cue.RUMOR_COUNTDOWN);
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
        return GameplaySounds.withCue(GameplaySounds.Cue.RUMOR_FAILURE,
                Completable.defer(() -> highlightRumorSomething(failureCell)));
    }

    public Completable highlightRumorObjective() {
        return highlightRumorSomething(objectiveCell);
    }

    public Completable highlightRumorReckoning() {
        return GameplaySounds.withCue(GameplaySounds.Cue.RECKONING,
                Completable.defer(() -> highlightRumorSomething(reckoningCell)));
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
        Image darkFront = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        darkFront.setColor(new Color(0f, 0f, 0f, 0.0f));
        darkFront.setPosition(getX(), getY());
        darkFront.setSize(getWidth(), getHeight());
        getStage().addActor(darkFront);
        darkFront.addAction(new FastForwardAction<>(Actions.alpha(0.75f, 0.5f)));
        return darkFront;
    }

    private void highlightLabel(Label label) {
        animateLabelColor(label, Color.WHITE);
    }

    private void darkenLabel(Label label) {
        animateLabelColor(label, BRASS);
    }

    private void animateLabelColor(Label label, Color targetColor) {
        if (getStage() == null) return;
        Color from = new Color(label.getStyle().fontColor);
        label.addAction(new FastForwardAction<>(new TemporalAction(0.5f) {
            @Override protected void update(float percent) {
                label.getStyle().fontColor = new Color(from).lerp(targetColor, percent);
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
