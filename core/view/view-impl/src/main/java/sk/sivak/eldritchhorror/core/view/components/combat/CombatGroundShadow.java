package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

final class CombatGroundShadow extends Image {
    CombatGroundShadow() {
        super(CustomAssetManager.getTexture("dice_shadow.png"));
        setTouchable(Touchable.disabled);
        setColor(0.025f, 0.015f, 0.035f, 0f);
    }

    void place(float footX, float footY, float width, float height, float opacity) {
        setBounds(footX - width / 2f, footY - height / 2f, width, height);
        getColor().a = opacity;
    }
}
