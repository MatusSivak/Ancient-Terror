package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class NoAdsButton extends ImageButton {

    public NoAdsButton() {
        super(CustomAssetManager.getTextureRegionDrawable("no_ads.png"));
        disableIfAdsDisabled();
        setSize(75,75);
        addClickListener(this, () -> {
            new InAppPurchaseManager().purchaseProduct("no_ads").subscribe(purchaseResult -> {
                if (purchaseResult) {
                    remove();
                }
            });
        });
    }

    private void disableIfAdsDisabled() {
        new InAppPurchaseManager().isProductPurchased("no_ads").subscribe(isPurchased -> {
            if (isPurchased) {
                setTouchable(Touchable.disabled);
                setVisible(false);
            }
        });
    }
}
