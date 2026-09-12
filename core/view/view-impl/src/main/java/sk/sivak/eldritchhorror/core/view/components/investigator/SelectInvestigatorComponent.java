package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import java8.features.function.Consumer;
import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.initgame.InAppPurchaseManager;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class SelectInvestigatorComponent extends Table {

    private final InvestigatorSketches sketches;
    private final Label titleLabel;
    private final Table sketchesAndUnlockNewTable;
    private InvestigatorId selectedInvestigatorId;
    private Runnable updateAvailableInvestigatorsAction;
    private final Table teamStrip = new Table();
    private final Cell<Table> teamCell;
    private final ScrollPane scrollPane;
    private final TextButton previousButton;
    private final TextButton nextButton;

    SelectInvestigatorComponent(float widthPercentage) {
        sketches = new InvestigatorSketches();
        titleLabel = createLabel(get("investigator.select"), new Color(0.91f, 0.85f, 0.69f, 1f));
        add(titleLabel).pad(5);
        row();
        teamCell = add(teamStrip).height(0f);
        row();

        sketchesAndUnlockNewTable = new Table();
        sketchesAndUnlockNewTable.add(sketches);
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.hScroll = getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(new Color(0.12f, 0.16f, 0.14f, 1f));
        scrollStyle.hScrollKnob = getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(new Color(0.65f, 0.55f, 0.32f, 1f));
        scrollStyle.hScroll.setMinHeight(5f);
        scrollStyle.hScrollKnob.setMinHeight(5f);
        scrollStyle.hScrollKnob.setMinWidth(35f);
        scrollPane = new ScrollPane(sketchesAndUnlockNewTable, scrollStyle);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).width(VIEWPORT_WIDTH * widthPercentage).height(408f).pad(5);
        row();
        previousButton = browseButton("<", -1);
        nextButton = browseButton(">", 1);
        Table navigation = new Table();
        navigation.setBackground(navigationBackground("101B18"));
        navigation.add(previousButton).size(32f, 26f);
        Label browseHint = createLabel(get("investigator.browse"), new Color(0.75f, 0.72f, 0.63f, 1f));
        browseHint.setFontScale(0.24f);
        navigation.add(browseHint).padLeft(12f).padRight(12f);
        navigation.add(nextButton).size(32f, 26f);
        add(navigation).width(VIEWPORT_WIDTH * widthPercentage).padBottom(4f).row();
        pack();

        sketches.addObserver(new SelectObserver());
    }

    public void showUnlockNewImage() {
        Image unlockNewImage = new Image(CustomAssetManager.getTexture("investigator/unlock_new.jpg"));
        unlockNewImage.setScaling(Scaling.fit);
        sketchesAndUnlockNewTable.add(unlockNewImage).size(398).align(Align.left);
        ButtonUtils.addClickListener(unlockNewImage, () -> {
            new InAppPurchaseManager().purchaseProduct("investigators_1").subscribe(purchaseResult -> {
                if (purchaseResult) {
                    Gdx.app.postRunnable(updateAvailableInvestigatorsAction);

                }
            });
        });
    }

    public void disable(InvestigatorId investigatorId) {
        sketches.disable(investigatorId);
    }

    private TextButton browseButton(String text, int direction) {
        // Compact controls need plain backgrounds, not the wide menu frame's fixed corners.
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4);
        style.up = navigationBackground("1D3028");
        style.over = navigationBackground("344A38");
        style.down = navigationBackground("4C563A");
        style.disabled = navigationBackground("101B18");
        style.fontColor = Color.valueOf("E8D9B0");
        style.overFontColor = Color.valueOf("FFF0C9");
        style.disabledFontColor = Color.valueOf("536059");
        TextButton button = new TextButton(text, style);
        button.getLabel().setFontScale(0.3f);
        AncientTerrorMenuStyles.makeMomentary(button);
        AncientTerrorMenuStyles.addFocusHighlight(button);
        ButtonUtils.addClickListener(button, () -> {
            if (!button.isDisabled()) {
                scrollPane.setScrollX(Math.max(0f, Math.min(scrollPane.getMaxX(), scrollPane.getScrollX() + direction * 364f)));
            }
        });
        return button;
    }

    private Drawable navigationBackground(String color) {
        Drawable drawable = getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(Color.valueOf(color));
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        return drawable;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        previousButton.setDisabled(scrollPane.getScrollX() <= 1f);
        nextButton.setDisabled(scrollPane.getScrollX() >= scrollPane.getMaxX() - 1f);
    }

    public void updateTeam(List<InvestigatorInfo> selected, int total) {
        teamStrip.clearChildren();
        Label teamLabel = createLabel(get("investigator.team"), new Color(0.91f, 0.85f, 0.69f, 1f));
        teamLabel.setFontScale(0.25f);
        teamStrip.add(teamLabel).padRight(10f);
        for (int i = 0; i < total; i++) {
            Table slot = new Table();
            slot.setBackground(getTextureRegionDrawable(PURE_WHITE_BACKGROUND).tint(new Color(0.12f, 0.16f, 0.14f, 1f)));
            if (i < selected.size()) {
                Image icon = new Image(getTexture("investigator/" + selected.get(i).getInvestigatorId().name() + ".png"));
                icon.setScaling(Scaling.fit);
                slot.add(icon).size(28f);
            } else {
                Label number = createLabel(Integer.toString(i + 1), Color.GRAY);
                number.setFontScale(0.25f);
                slot.add(number);
            }
            teamStrip.add(slot).size(32f).padRight(5f);
        }
        teamCell.height(36f);
        pack();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    protected void hideOkButton() {
    }

    protected void showOkButton() {
    }

    public InvestigatorId getSelectedInvestigatorId() {
        return selectedInvestigatorId;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f * getColor().a * parentAlpha));
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), getX(), getY(), getPrefWidth(), getPrefHeight());
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.4f);
        label.setAlignment(Align.center, Align.center);
        return label;
    }

    public void init(InvestigatorId... investigatorIds) {
        sketches.init(investigatorIds);
        pack();
    }

    public void setUpdateAvailableInvestigatorsAction(Runnable updateAvailableInvestigatorsAction) {
        this.updateAvailableInvestigatorsAction = updateAvailableInvestigatorsAction;
    }

    private class SelectObserver implements Consumer<InvestigatorId> {

        @Override
        public void accept(InvestigatorId investigatorId) {
            selectedInvestigatorId = investigatorId;
            if (investigatorId == null) {
                hideOkButton();
            } else {
                showOkButton();
            }
        }
    }
}
