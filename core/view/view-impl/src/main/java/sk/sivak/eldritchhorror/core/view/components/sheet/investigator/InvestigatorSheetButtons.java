package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class InvestigatorSheetButtons extends Group {

    void initButtons() {
        Label basicInfoLabel = createLabel("Basic Info", Color.BLACK);
        Label cardsLabel = createLabel("Cards", Color.BLACK);
        Label miscLabel = createLabel("Background", Color.BLACK);

        basicInfoLabel.setX(20);
        basicInfoLabel.setY(480);
        basicInfoLabel.setWidth(200);
        basicInfoLabel.setHeight(basicInfoLabel.getPrefHeight());
        addActor(basicInfoLabel);

        cardsLabel.setX(275);
        cardsLabel.setY(490);
        cardsLabel.setWidth(200);
        cardsLabel.setHeight(cardsLabel.getPrefHeight());
        addActor(cardsLabel);

        miscLabel.setX(530);
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
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFont(FONT_ADLER), color);
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(0.35f);
        return label;
    }

    public void highlightBasicInfo() {
        Image underline = new Image(getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        underline.setColor(Color.BLACK);
        underline.setWidth(125);
        underline.setHeight(5);
        underline.setPosition(20 + 37.5f, 475);
        addActor(underline);
    }

    public void highlightCards() {
        Image underline = new Image(getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        underline.setColor(Color.BLACK);
        underline.setWidth(125);
        underline.setHeight(5);
        underline.setPosition(275 + 37.5f, 485);
        addActor(underline);
    }

    public void highlightBio() {
        Image underline = new Image(getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        underline.setColor(Color.BLACK);
        underline.setWidth(125);
        underline.setHeight(5);
        underline.setPosition(530 + 37.5f, 485);
        addActor(underline);
    }
}
