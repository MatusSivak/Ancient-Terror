package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

public class SelectInvestigatorComponent extends Table {

    private final InvestigatorSketches sketches;
    private final Label titleLabel;
    private final Table sketchesAndUnlockNewTable;
    private InvestigatorId selectedInvestigatorId;
    private Runnable updateAvailableInvestigatorsAction;

    SelectInvestigatorComponent(float widthPercentage) {
        sketches = new InvestigatorSketches();
        titleLabel = createLabel("Select Investigator", Color.GREEN);
        add(titleLabel).pad(5);
        row();

        sketchesAndUnlockNewTable = new Table();
        sketchesAndUnlockNewTable.add(sketches);
        ScrollPane scrollPane = new ScrollPane(sketchesAndUnlockNewTable);
        add(scrollPane).width(VIEWPORT_WIDTH * widthPercentage).pad(5);
        row();
        pack();

        sketches.addObserver(new SelectObserver());
    }

    public void showUnlockNewImage() {
        Image unlockNewImage = new Image(CustomAssetManager.getTexture("investigator/unlock_new.jpg"));
        unlockNewImage.setScaling(Scaling.fit);
        sketchesAndUnlockNewTable.add(unlockNewImage).size(460).align(Align.left);
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
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_BLACK_CHANCERY), color);
        Label label = new Label(text, labelStyle);
        label.setFontScale(0.9f);
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
