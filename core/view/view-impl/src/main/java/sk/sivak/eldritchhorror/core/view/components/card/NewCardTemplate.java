package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.Trait;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_CARD_TEMPLATE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureAsync;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;

public class NewCardTemplate extends WidgetGroup {

	private NewCardTemplate(String textureId,
							String title,
							Color titleBackgroundColor,
							Color titleFontColor,
							Color descriptionBackgroundColor,
							Integer cost,
							Trait[] traits,
							String description,
							String cardTypeTexture,
							String reckoningText,
							boolean isDisabled) {


		loadTemplate(textureId);
	}

	private void loadTemplate(String textureId) {
		getTextureAsync(NEW_CARD_TEMPLATE).subscribe(ct -> {
			addActor(new Image(getTextureRegionDrawable(NEW_CARD_TEMPLATE)));
			loadPicture(textureId);
		});
	}

	private void loadPicture(String textureId) {
		getTextureAsync(textureId).subscribe(t -> {
			addActor(new Image(getTextureRegionDrawable(textureId)));
		});
	}

	public static NewCardTemplate buildCard(AssetInfo assetInfo) {
		Trait[] traits;
		if (assetInfo.getTraits() == null || assetInfo.getTraits().size() == 0) {
			traits = null;
		} else {
			traits = assetInfo.getTraits().toArray(new Trait[assetInfo.getTraits().size()]);
		}
		NewCardTemplate cardTemplate = new NewCardTemplate("card/asset/" + assetInfo.getId().name() + ".jpg",
				assetInfo.getName(),
				Color.valueOf("8C6935"),
				Color.valueOf("F3E7C8"),
				Color.valueOf("DDD0AF"),
				assetInfo.getCost(),
				traits,
				assetInfo.getDescription(),
				"card/card_type_asset.png",
				null,
				assetInfo.isDisabled());
//		cardTemplate.setAssetInfo(assetInfo);
//		cardTemplate.updateDisabledPositionAndScale();
		return cardTemplate;
	}
}
