package sk.sivak.eldritchhorror.core.view.components.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class HudButton extends ImageButton {

    private Image icon;
    private boolean disabled;

    private float scalePressedOrChecked = 0.7f;
    private float scaleDefault = 0.85f;

    private HudButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked, String texturePath) {
        super(imageUp, imageDown, imageChecked);
        icon = new Image(getTextureRegion(texturePath));
        icon.setOrigin(200,200);
        icon.setAlign(Align.center);
    }

    public static HudButton buildEnabled(String texturePath) {
        HudButton hudButton = new HudButton(
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_PRESSED),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_CHECKED),
                texturePath);

        hudButton.disabled = false;
        return hudButton;
    }

    public static HudButton buildDisabled(String texturePath) {
        HudButton hudButton = new HudButton(
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_CHECKED),
                texturePath);
        hudButton.disabled = true;
        return hudButton;
    }

    public void disable() {
        getStyle().imageUp = getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL);
        getStyle().imageDown = getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL);
        getStyle().imageChecked = getTextureRegionDrawable(ACTION_BUTTON_DISABLED_CHECKED);
        disabled = true;
    }

    public void enable() {
        getStyle().imageUp = getTextureRegionDrawable(ACTION_BUTTON_ENABLED_NORMAL);
        getStyle().imageDown = getTextureRegionDrawable(ACTION_BUTTON_ENABLED_PRESSED);
        getStyle().imageChecked = getTextureRegionDrawable(ACTION_BUTTON_ENABLED_CHECKED);
        disabled = false;
    }

    public boolean looksDisabled() {
        return disabled;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);
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
            icon.setSize(getWidth(), getHeight() * (1 / widthHeightRatio));
            icon.setPosition(
                    getX(),
                    getY() + (getHeight() - (1 / widthHeightRatio) * getHeight()) / 2);
        }

        icon.setSize(getWidth() * 1.5f, getHeight() * 1.5f);
        icon.setPosition(icon.getX()-5, icon.getY() - 24);
        if (!disabled) {
            if (isPressed()) {
                icon.setColor(Color.GREEN);
                icon.setScale(scalePressedOrChecked);
            } else if (isChecked()) {
                icon.setColor(Color.GREEN);
                icon.setScale(scaleDefault);
            } else {
                icon.setColor(Color.WHITE);
                icon.setOrigin(Align.center);
                icon.setScale(scaleDefault);
            }
        } else {
            icon.setColor(Color.WHITE);
            batch.setShader(GrayscaleShader.get());
            icon.setOrigin(Align.center);
            icon.setScale(scaleDefault);
        }
        icon.draw(batch, parentAlpha);
        batch.setShader(null);
    }
}