package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.view.components.card.CardMaskedImageBuilder;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.components.card.CardMaskedImageBuilder.*;

public class ActionButton extends ImageButton {

    protected Image icon;
    protected boolean disabled;

    protected float scaleMax = 1f;
    protected float scaleMin = 0.85f;
    protected ActionButtonData actionButtonData;

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

        actionButton.disabled = false;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.needsScaleDown(), actionButton);
        return actionButton;
    }

    private static ActionButton buildDisabled(ActionButtonData actionButtonData) {
        ActionButton actionButton = new ActionButton(
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_CHECKED));
        actionButton.initIcon(actionButtonData.getTexturePath(), actionButtonData.needsMask());

        actionButton.disabled = true;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.needsScaleDown(), actionButton);
        return actionButton;
    }

    public ActionButtonData getActionButtonData() {
        return actionButtonData;
    }

    protected static void init(boolean needsScaleDown, ActionButton actionButton) {
        if (needsScaleDown) {
            actionButton.scaleMax *= 0.9f;
            actionButton.scaleMin *= 0.9f;
        }
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
                    getX() + (getWidth() - widthHeightRatio * getWidth()) / 2,
                    getY());
        } else {
            icon.setSize(getWidth(), getHeight() * (1/widthHeightRatio));
            icon.setPosition(
                    getX(),
                    getY()  + (getHeight() - (1/widthHeightRatio) * getHeight()) / 2 );
        }

        if (!disabled) {
            if (!isPressed() && !isChecked()) {
                icon.setOrigin(Align.center);
                icon.setScale(scaleMin);
            } else {
                icon.setScale(scaleMax);
            }
        } else {
            batch.setShader(GrayscaleShader.get());
            icon.setOrigin(Align.center);
            icon.setScale(scaleMin);
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




