package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.components.card.CardMaskedImageBuilder.*;

public class ActionButton extends ImageButton {

    protected Image icon;
    protected boolean disabled;

    protected float scaleMax = 1f;
    protected float scaleMin = 0.85f;
    protected ActionButtonData actionButtonData;
    private Vector2 iconOffset = new Vector2();

    public static ActionButton build(ActionButtonData actionButtonData) {
        if (actionButtonData.isEnabled()) {
            return buildEnabled(actionButtonData);
        } else {
            return buildDisabled(actionButtonData);
        }
    }

    private static ActionButton buildEnabled(ActionButtonData actionButtonData) {
        ActionButton actionButton = new ActionButton(
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_PRESSED),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_CHECKED));
        actionButton.initIcon(actionButtonData.getTexturePath(), actionButtonData.needsMask());
        actionButton.setIconOffset(actionButtonData.getOffset());

        actionButton.disabled = false;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.getScaleDownPercentage(), actionButton);
        return actionButton;
    }

    private static ActionButton buildDisabled(ActionButtonData actionButtonData) {
        ActionButton actionButton = new ActionButton(
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_PRESSED),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_CHECKED));
        actionButton.initIcon(actionButtonData.getTexturePath(), actionButtonData.needsMask());
        actionButton.setIconOffset(actionButtonData.getOffset());

        actionButton.disabled = true;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.getScaleDownPercentage(), actionButton);
        return actionButton;
    }

    private void setIconOffset(Vector2 offset) {
        this.iconOffset = offset;

    }

    public ActionButtonData getActionButtonData() {
        return actionButtonData;
    }

    protected static void init(float scaleDownPercentage, ActionButton actionButton) {
        actionButton.scaleMax *= scaleDownPercentage;
        actionButton.scaleMin *= scaleDownPercentage;
    }

    protected ActionButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        icon.act(delta);
    }

    private void initIcon(String texturePath, boolean needsMask) {
        if (needsMask) {
            icon = new Image(buildMaskedTextureRegion(getTexture(texturePath)));
        } else {
            icon = new Image(getTextureRegion(texturePath));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        drawIcon(batch, parentAlpha);
    }

    private void drawIcon(Batch batch, float parentAlpha) {
        if (icon == null) {
            return;
        }
        float widthHeightRatio = icon.getPrefWidth() / icon.getPrefHeight();
        if (widthHeightRatio < 1) {
            icon.setSize(getWidth() * widthHeightRatio, getHeight());
            icon.setPosition(
                    iconOffset.x + getX() + (getWidth() - widthHeightRatio * getWidth()) / 2,
                    iconOffset.y + getY());
        } else {
            icon.setSize(getWidth(), getHeight() * (1/widthHeightRatio));
            icon.setPosition(
                    iconOffset.x + getX(),
                    iconOffset.y + getY()  + (getHeight() - (1/widthHeightRatio) * getHeight()) / 2 );
        }

        if (!disabled) {
            if (isPressed()) {
                icon.setColor(Color.GREEN);
                icon.setScale(scaleMin * 0.85f);
            } else if (isChecked()) {
                icon.setColor(Color.GREEN);
                icon.setScale(scaleMin);
            } else {
                icon.setColor(Color.WHITE);
                icon.setOrigin(Align.center);
                icon.setScale(scaleMin);
            }
        } else {
            if (isPressed()) {
                batch.setShader(null);
                icon.setColor(Color.RED);
                icon.setScale(scaleMin * 0.85f);
            } else if (isChecked()) {
                batch.setShader(null);
                icon.setColor(Color.RED);
                icon.setScale(scaleMin);
            } else {
                icon.setColor(Color.WHITE);
                batch.setShader(GrayscaleShader.get());
                icon.setOrigin(Align.center);
                icon.setScale(scaleMin);
            }
        }

        icon.draw(batch, parentAlpha);
        batch.setShader(null);
    }

    public Image getIcon() {
        return icon;
    }

    private boolean pressedOverride = false;

    @Override
    public boolean isPressed() {
        return pressedOverride || super.isPressed();
    }

    public void setPressedOverride(boolean pressedOverride) {
        this.pressedOverride = pressedOverride;
    }
}




