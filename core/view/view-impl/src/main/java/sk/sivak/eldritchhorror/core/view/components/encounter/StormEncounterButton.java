package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.view.components.action.ActionButton;
import sk.sivak.eldritchhorror.core.view.map.storm.StormImage;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_DISABLED_CHECKED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_DISABLED_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_CHECKED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.ACTION_BUTTON_ENABLED_PRESSED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;

public class StormEncounterButton extends ActionButton {

	private StormEncounterButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
		super(imageUp, imageDown, imageChecked);
	}

	public static ActionButton build(ActionButtonData actionButtonData) {
		if (actionButtonData.isEnabled()) {
			return buildEnabled(actionButtonData);
		} else {
			return buildDisabled(actionButtonData);
		}
	}


	private static StormEncounterButton buildEnabled(ActionButtonData actionButtonData) {
		StormEncounterButton actionButton = new StormEncounterButton(
				getTextureRegionDrawable(ACTION_BUTTON_ENABLED_NORMAL),
				getTextureRegionDrawable(ACTION_BUTTON_ENABLED_PRESSED),
				getTextureRegionDrawable(ACTION_BUTTON_ENABLED_CHECKED));

		actionButton.initAnimation();

		actionButton.disabled = false;
		actionButton.actionButtonData = actionButtonData;
		init(actionButtonData.getScaleDownPercentage(), actionButton);
		return actionButton;
	}

	private static StormEncounterButton buildDisabled(ActionButtonData actionButtonData) {
		StormEncounterButton actionButton = new StormEncounterButton(
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
		icon = new StormImage();
		scaleMax = 2.1f;
		scaleMin = 1.8f;
	}
}
