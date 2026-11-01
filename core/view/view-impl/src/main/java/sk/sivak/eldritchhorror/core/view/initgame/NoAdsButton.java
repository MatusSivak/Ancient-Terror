package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class NoAdsButton extends ImageButton {

    public NoAdsButton() {
        this(CustomAssetManager.getTextureRegionDrawable("no_ads.png"));
        new InAppPurchaseManager().restoreFullGame().subscribe(
                purchased -> Gdx.app.postRunnable(this::refreshVisibility),
                error -> Gdx.app.postRunnable(this::refreshVisibility));
    }

    NoAdsButton(Drawable icon) {
        super(icon);
        getImage().setScaling(Scaling.fit);
        getImageCell().size(75f);
        pad(0f);
        setSize(75f, 75f);
        refreshVisibility();
        addClickListener(this, () -> FullGamePurchaseDialog.show(getStage(), this::refreshVisibility));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        refreshVisibility();
    }

    private void refreshVisibility() {
        boolean adsDisabled = InAppPurchaseManager.isFullGamePurchased();
        setTouchable(adsDisabled ? Touchable.disabled : Touchable.enabled);
        setVisible(!adsDisabled);
    }
}
