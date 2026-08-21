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
        setWidth(ACTUAL_TOKEN_SIZE);
        setHeight(ACTUAL_TOKEN_SIZE);
        setOrigin(Align.center);
        background = new Image(CustomAssetManager.getTexture(CustomAssetManager.GRAY_BACKGROUND));
        background.getColor().a =0.8f;
        background.setSize(ACTUAL_TOKEN_SIZE, ACTUAL_TOKEN_SIZE);
        addActor(background);

        image = new Image(tokenTexture);
        image.getColor().a = 0.75f;
        image.setScaling(Scaling.fit);
        image.setSize(ACTUAL_TOKEN_SIZE+10, ACTUAL_TOKEN_SIZE+10);
        image.setPosition(-5,-5);
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
