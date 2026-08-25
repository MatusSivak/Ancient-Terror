package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.action.ActionButton;

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

    private String colorName;

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
        icon = new Image(getTextureRegion("gate/gate_border.png"));
        icon.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(360f, 8f)));
        switch (colorName) {
            case "BLUE" :
                icon.setColor(new Color(0.33f,0.33f,1,1f));
                break;
            case "GREEN" :
                icon.setColor(new Color(0.33f,1f,0.33f,1f));
                break;
            case "RED" :
                icon.setColor(new Color(1f,0.33f,0.33f,1f));
                break;

        }

    }
}
