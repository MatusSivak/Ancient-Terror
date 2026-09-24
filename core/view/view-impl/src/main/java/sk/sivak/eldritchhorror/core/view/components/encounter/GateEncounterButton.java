package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.action.ActionButton;
import sk.sivak.eldritchhorror.core.view.map.gate.NewGateAnimatedImage;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_DISABLED_CHECKED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_DISABLED_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_CHECKED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_PRESSED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegion;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.components.card.CardMaskedImageBuilder.buildMaskedTextureRegion;

public class GateEncounterButton extends ActionButton{

    private static final float GLOW_SCALE = 0.95f;
    private String colorName;
    private Color glowColor;
    private int previousBlendSrc = -1;
    private int previousBlendDst = -1;

    private GateEncounterButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
    }

    public static ActionButton build(ActionButtonData actionButtonData, String colorName) {
        if (actionButtonData.isEnabled()) {
            return buildEnabled(actionButtonData, colorName);
        } else {
            return buildDisabled(actionButtonData, colorName);
        }
    }


    private static GateEncounterButton buildEnabled(ActionButtonData actionButtonData, String colorName) {
        GateEncounterButton actionButton = new GateEncounterButton(
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_PRESSED),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_CHECKED));

        actionButton.colorName = colorName;
        actionButton.initAnimation();

        actionButton.disabled = false;
        actionButton.actionButtonData = actionButtonData;
        init(1.4f, actionButton);
        return actionButton;
    }

    private static GateEncounterButton buildDisabled(ActionButtonData actionButtonData, String colorName) {
        GateEncounterButton actionButton = new GateEncounterButton(
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_CHECKED));
        actionButton.colorName = colorName;
        actionButton.initAnimation();

        actionButton.disabled = true;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.getScaleDownPercentage(), actionButton);
        return actionButton;
    }

    private void initAnimation() {
        Color tint = NewGateAnimatedImage.toLibgdxColor(GateColor.valueOf(colorName));
        glowColor = new Color(tint.r, tint.g, tint.b, 0.55f);
        icon = new AnimatedImage(new Animation<>(0.04f, CustomAssetManager.getGateAnimation(), Animation.PlayMode.LOOP));
        icon.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(360f, 8f)));
        iconTint = tint;
    }

    @Override
    protected void drawBehindIcon(Batch batch, float parentAlpha) {
        float size = Math.min(getWidth(), getHeight()) * GLOW_SCALE;
        Color before = batch.getColor();
        float r = before.r, g = before.g, b = before.b, a = before.a;
        ShaderProgram previousShader = batch.getShader();
        if (disabled) {
            batch.setShader(GrayscaleShader.get());
        }
        try {
            batch.setColor(glowColor.r, glowColor.g, glowColor.b, glowColor.a * parentAlpha);
            batch.draw(EncounterIconStyle.glow(), getX() + (getWidth() - size) / 2, getY() + (getHeight() - size) / 2, size, size);
        } finally {
            batch.setColor(r, g, b, a);
            batch.setShader(previousShader);
        }
        previousBlendSrc = batch.getBlendSrcFunc();
        previousBlendDst = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try {
            super.draw(batch, parentAlpha);
        } finally {
            if (previousBlendSrc != -1) {
                batch.setBlendFunction(previousBlendSrc, previousBlendDst);
                previousBlendSrc = -1;
            }
        }
    }
}
