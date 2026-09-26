package sk.sivak.eldritchhorror.core.view.components.sheet.mystery;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.DisplayHide;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.components.sheet.mystery.ProgressTokenBar.ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class MysteryCard extends VisTable {

    private static final float CARD_WIDTH = 620f;
    private static final float SIDE_PADDING = 28f;
    private static final float CONTENT_WIDTH = CARD_WIDTH - SIDE_PADDING * 2;
    private static final Color TEXT = Color.valueOf("EEE6D5");
    private static final Color MUTED_TEXT = Color.valueOf("B8C4BE");
    private static final Color BRASS = Color.valueOf("DCC99F");

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
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        if (hitImage != null) {
            hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        }
    }

    public void init(MysteryCardInfo mysteryCardInfo, int mysteryNumber, int mysteriesRequired) {
        clear();
        hitImage = null;
        getColor().a = 1f;
        this.mysteryCardInfo = mysteryCardInfo;
        TextureRegionDrawable background = new TextureRegionDrawable(
                CustomAssetManager.getTextureRegion(CustomAssetManager.RESERVE_BACKGROUND));
        background.setMinWidth(0);
        background.setMinHeight(0);
        setBackground(background);
        pad(24, SIDE_PADDING, 24, SIDE_PADDING);
        defaults().width(CONTENT_WIDTH).left();

        add(createSectionLabel(get("mystery.current", Math.min(mysteryNumber, mysteriesRequired), mysteriesRequired))).padBottom(8);
        row();
        add(createNameLabel(resolveLocalizedText(mysteryCardInfo.getName()))).padBottom(16);
        row();
        add(createDivider()).height(1).padBottom(16);
        row();
        String flavorText = resolveLocalizedText(mysteryCardInfo.getFlavorText());
        if (flavorText != null && !flavorText.trim().isEmpty()) {
            add(createFlavorLabel(flavorText)).padBottom(18);
            row();
        }
        Table objective = new Table();
        objective.setBackground(SelectionPanelStyle.panel("1C302F", "415851"));
        objective.add(createMysteryText(getProcessedMysteryText(mysteryCardInfo)))
                .width(CONTENT_WIDTH - 36).pad(16, 18, 16, 18);
        add(objective);
        row();

        add(createProgressLabel()).padTop(18).padBottom(8);
        row();

        progressTokenBar = createProgressTokenBar(mysteryCardInfo.getMysteryComplexity(), Math.min(mysteryCardInfo.getProgress(), mysteryCardInfo.getMysteryComplexity()));
        add(progressTokenBar).fillX();

        // Measure wrapped text at the final width, including localized titles and rules.
        setWidth(CARD_WIDTH);
        invalidateHierarchy();
        setHeight(getPrefHeight());
        validate();
        setHeight(getPrefHeight());
        validate();

        addHitImage();
    }

    private String getProcessedMysteryText(MysteryCardInfo mysteryCardInfo) {
        return resolveLocalizedText(mysteryCardInfo.getMysteryText())
                .replaceAll("COMPLEXITY", mysteryCardInfo.getMysteryComplexity().toString());
    }

    private ProgressTokenBar createProgressTokenBar(Integer mysteryComplexity, Integer progress) {
        ProgressTokenBar progressTokenBar = new ProgressTokenBar();
        progressTokenBar.init(mysteryComplexity, progress);
        return progressTokenBar;
    }

    private Label createNameLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), BRASS);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        label.setWrap(true);
        label.setFontScale(0.65f);
        return label;
    }

    private Label createFlavorLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 44), MUTED_TEXT);
        Label label = new Label(reflowText(text), labelStyle);
        label.setAlignment(Align.left);
        label.setWrap(true);
        label.setFontScale(0.35f);
        return label;
    }

    private Label createMysteryText(String text) {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), TEXT);
        Label label = new Label(reflowText(text), style);
        label.setWrap(true);
        label.setAlignment(Align.left);
        label.setFontScale(0.46f);
        return label;
    }

    private String reflowText(String text) {
        // Legacy copy has hard-wrapped lines. Let the label wrap to the card width,
        // while retaining explicit blank lines between paragraphs.
        return text.replace("\r\n", "\n").replaceAll("(?<!\n)\n(?!\n)", " ");
    }

    private Label createProgressLabel() {
        return createSectionLabel(get("mystery.progress"));
    }

    private Label createSectionLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 42), BRASS);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        label.setFontScale(0.35f);
        label.setWrap(true);
        return label;
    }

    private Image createDivider() {
        Image divider = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        divider.setColor(Color.valueOf("8E7953"));
        return divider;
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
            if (mysteryCardInfo.getPinLocations() != null && !mysteryCardInfo.getPinLocations().isEmpty() && moveCamera) {
                int pinLocationIndex = MathUtils.random(0, mysteryCardInfo.getPinLocations().size() - 1);
                MapUtils.moveCameraToLocation(mysteryCardInfo.getPinLocations().get(pinLocationIndex)).subscribe();
            }
            if (beforeDisplayAction != null) {
                beforeDisplayAction.call();
            }
        });



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
