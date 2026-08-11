package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.view.components.action.ActionButton;
import sk.sivak.eldritchhorror.core.view.map.storm.StormImage;
import sk.sivak.eldritchhorror.core.view.map.vortex.VortexImage;
import sk.sivak.eldritchhorror.core.view.map.vortex.VortexImageUtils;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_DISABLED_CHECKED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_DISABLED_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_CHECKED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_PRESSED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;

public class VortexEncounterButton extends ActionButton{

    private VortexEncounterButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
    }

    public static ActionButton build(ActionButtonData actionButtonData) {
        if (actionButtonData.isEnabled()) {
            return buildEnabled(actionButtonData);
        } else {
            return buildDisabled(actionButtonData);
        }
    }


    private static VortexEncounterButton buildEnabled(ActionButtonData actionButtonData) {
        VortexEncounterButton actionButton = new VortexEncounterButton(
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_PRESSED),
                getTextureRegionDrawable(ACTION_BUTTON_ENABLED_CHECKED));

        actionButton.initAnimation();

        actionButton.disabled = false;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.getScaleDownPercentage(), actionButton);
        return actionButton;
    }

    private static VortexEncounterButton buildDisabled(ActionButtonData actionButtonData) {
        VortexEncounterButton actionButton = new VortexEncounterButton(
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_NORMAL),
                getTextureRegionDrawable(ACTION_BUTTON_DISABLED_CHECKED));
        actionButton.initAnimation();

        actionButton.disabled = true;
        actionButton.actionButtonData = actionButtonData;
        init(actionButtonData.getScaleDownPercentage(), actionButton);
        return actionButton;
    }

    private void initAnimation() {
        icon = VortexImage.build();
        scaleMax = 1.7f;
        scaleMin = 1.45f;
    }
}
