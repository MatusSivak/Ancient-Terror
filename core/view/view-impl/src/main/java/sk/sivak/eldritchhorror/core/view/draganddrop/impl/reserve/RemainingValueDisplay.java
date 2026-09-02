package sk.sivak.eldritchhorror.core.view.draganddrop.impl.reserve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_GOBLIN_ONE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;

public class RemainingValueDisplay extends Group {

    public static final float SCALE = 0.45f; // don't touch
    private final Label costLabel;
    private final Image background;

    public RemainingValueDisplay() {
        background = new Image(CustomAssetManager.getTexture(CustomAssetManager.ACQUIRE_ASSETS_REMAINING_BG));
        costLabel = createCostLabel();
        background.setWidth(background.getWidth() * SCALE);
        background.setHeight(background.getHeight() * SCALE);
        addActor(background);
        addActor(costLabel);
        updateRemainingValue(0);
        costLabel.setPosition(128 * SCALE - costLabel.getWidth() / 2,
                128 * SCALE - costLabel.getHeight() / 2);
    }

    public void updateRemainingValue(int remainingValue) {
        costLabel.setText("" + remainingValue);
    }

    @Override
    public float getWidth() {
        return background.getWidth();
    }

    @Override
    public float getHeight() {
        return background.getHeight();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

    }

    private Label createCostLabel() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = getBitmapFont(FONT_GOBLIN_ONE);
        style.fontColor = new Color(0xf6c06fff);
        Label label = new Label("0", style);
        label.setAlignment(Align.center);
        return label;
    }
}
