package sk.sivak.eldritchhorror.core.view.draganddrop.impl.reserve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.SourceTargetGroup;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_BLACK_CHANCERY;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class ReserveSourceTargetGroup extends SourceTargetGroup {

    private final RemainingValueDisplay remainingValueDisplay;
    private final Label reserveLabel;
    private final Label investigatorLabel;

    public ReserveSourceTargetGroup() {
        remainingValueDisplay = new RemainingValueDisplay();
        remainingValueDisplay.setOrigin(remainingValueDisplay.getWidth() / 2,
                remainingValueDisplay.getHeight() / 2);
        addActor(remainingValueDisplay);
        reserveLabel = createLabel(get("reserve.label"), new Color(0.75f, 0f, 0f, 1f));
        addActor(reserveLabel);
        investigatorLabel = createLabel(get("reserve.investigatorLabel"), new Color(0f, 0.75f, 0f, 1f));
        addActor(investigatorLabel);
        upDownImage.remove();


    }

    private Label createLabel(String text, Color color) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFont(FONT_BLACK_CHANCERY);
        style.fontColor = new Color(color);
        Label label = new Label(text, style);
        label.setFontScale(0.6f);
        return label;
    }

    public void initValueLimit(int valueLimit) {
        remainingValueDisplay.updateRemainingValue(valueLimit);
    }

    public void updateRemainingValue(int remainingValue) {
        remainingValueDisplay.updateRemainingValue(remainingValue);
    }

    @Override
    protected Sprite createUpDownSprite() {
        Sprite upDownSprite = super.createUpDownSprite();
        upDownSprite.flip(false, true);
        return upDownSprite;
    }

    @Override
    protected void sizeChanged() {
        reserveLabel.setPosition(0,
                getHeight() / 2 - reserveLabel.getPrefHeight() / 2 + 8);
        investigatorLabel.setPosition(0,
                getHeight() / 2 - reserveLabel.getPrefHeight() / 2 - 20);

        remainingValueDisplay.setScale(0.4f);
        remainingValueDisplay.setPosition(
                getWidth() / 2 - remainingValueDisplay.getWidth() / 2,
                getHeight() / 2 - remainingValueDisplay.getHeight() / 2);

    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        targetActor.setX(0);
        targetActor.setY(0);
        sourceDockActor.setX(0);
        sourceDockActor.setY(getHeight() - sourceDockActor.getHeight());
    }
}
