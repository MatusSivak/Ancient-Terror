package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class ToughnessToken extends Image {

    public ToughnessToken(boolean empty) {
        super(empty ?
                CustomAssetManager.getTexture(CustomAssetManager.EMPTY_HEALTH_ICON) :
                CustomAssetManager.getTexture(CustomAssetManager.HEALTH_ICON));
    }

    public void deplete() {
        setDrawable(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.EMPTY_HEALTH_ICON));
    }
}
