package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class InvestigatorSheetButtons extends Group {
    private Label basicInfoLabel;
    private Label cardsLabel;
    private Label miscLabel;
    private String headerPath = "passport-dossier-top-basic.png";

    String getHeaderPath() {
        return headerPath;
    }

    void initButtons() {
        basicInfoLabel = createLabel(get("investigator.tab.basicInfo"), Color.BLACK);
        cardsLabel = createLabel(get("investigator.tab.cards"), CharacterSheetWidgets.INK);
        miscLabel = createLabel(get("investigator.tab.background"), CharacterSheetWidgets.INK);

        basicInfoLabel.setX(40);
        basicInfoLabel.setY(490);
        basicInfoLabel.setWidth(200);
        basicInfoLabel.setHeight(basicInfoLabel.getPrefHeight());
        addActor(basicInfoLabel);

        cardsLabel.setX(285);
        cardsLabel.setY(490);
        cardsLabel.setWidth(200);
        cardsLabel.setHeight(cardsLabel.getPrefHeight());
        addActor(cardsLabel);

        miscLabel.setX(540);
        miscLabel.setY(490);
        miscLabel.setWidth(200);
        miscLabel.setHeight(miscLabel.getPrefHeight());
        addActor(miscLabel);

        Image basicInfoClickArea = new Image(getTexture(PURE_WHITE_BACKGROUND));
        basicInfoClickArea.setColor(Color.CLEAR);
        basicInfoClickArea.setWidth(220);
        basicInfoClickArea.setHeight(50);
        basicInfoClickArea.setPosition(10, 460);

        Image cardsClickArea = new Image(getTexture(PURE_WHITE_BACKGROUND));
        cardsClickArea.setColor(Color.CLEAR);
        cardsClickArea.setWidth(230);
        cardsClickArea.setHeight(50);
        cardsClickArea.setPosition(260, 470);

        Image bioClickArea = new Image(getTexture(PURE_WHITE_BACKGROUND));
        bioClickArea.setColor(Color.CLEAR);
        bioClickArea.setWidth(230);
        bioClickArea.setHeight(50);
        bioClickArea.setPosition(520, 470);

        addActor(basicInfoClickArea);
        addActor(cardsClickArea);
        addActor(bioClickArea);

        addClickListener(basicInfoClickArea, this::onBasicInfoTabClick);
        addClickListener(cardsClickArea, this::onCardsTabClick);
        addClickListener(bioClickArea, this::onBackgroundTabClick);

    }

    protected void onBasicInfoTabClick() {

    }

    protected void onCardsTabClick() {

    }

    protected void onBackgroundTabClick() {

    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SPECIAL_ELITE, 42), color);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    public void highlightBasicInfo() {
        selectTab(basicInfoLabel, "passport-dossier-top-basic.png");
    }

    public void highlightCards() {
        selectTab(cardsLabel, "passport-dossier-top-cards.png");
    }

    public void highlightBio() {
        selectTab(miscLabel, "passport-dossier-top-background.png");
    }

    private void selectTab(Label selected, String artwork) {
        headerPath = artwork;
        for (Label label : new Label[]{basicInfoLabel, cardsLabel, miscLabel}) {
            Label.LabelStyle style = new Label.LabelStyle(label.getStyle());
            style.fontColor = label == selected ? Color.valueOf("29271E") : Color.valueOf("EEE6D5");
            label.setStyle(style);
        }
    }
}