package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;



public class TokenInFrame extends Group {

    private final Image background;
    private final Image image;

    public static final int ACTUAL_TOKEN_SIZE = 43;

    public TokenInFrame(Texture tokenTexture) {
        this(tokenTexture, ACTUAL_TOKEN_SIZE, ACTUAL_TOKEN_SIZE + 10);
    }

    public TokenInFrame(Texture tokenTexture, float size) {
        this(tokenTexture, size, size);
    }

    private TokenInFrame(Texture tokenTexture, float size, float imageSize) {
        setSize(size, size);
        setOrigin(Align.center);
        // Compact combat rows have a shared highlight instead of a box behind every icon.
        background = size == imageSize ? new Image()
                : new Image(CustomAssetManager.getTexture(CustomAssetManager.GRAY_BACKGROUND));
        background.getColor().a =0.8f;
        background.setSize(size, size);
        addActor(background);

        image = new Image(tokenTexture);
        image.getColor().a = 0.75f;
        image.setScaling(Scaling.fit);
        image.setSize(imageSize, imageSize);
        image.setPosition((size - imageSize) / 2, (size - imageSize) / 2);
        addActor(image);
    }

    public void hideTokenImage() {
        image.setVisible(false);
    }

    public Image getImage() {
        return image;
    }

    public Image getBackground() {
        return background;
    }
}
