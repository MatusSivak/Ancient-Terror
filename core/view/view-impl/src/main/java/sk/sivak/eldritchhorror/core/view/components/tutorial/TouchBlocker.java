package sk.sivak.eldritchhorror.core.view.components.tutorial;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class TouchBlocker extends Group {

    private final Image[] images;

    public TouchBlocker(int width, int height) {
        setSize(width, height);
        images = new Image[8];
        for (int i = 0; i < 8; i++) {
            images[i] = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
            images[i].getColor().a = 0f;
            images[i].setSize(width, height);
            addActor(images[i]);
        }
        setTouchable(Touchable.childrenOnly);
    }

    public void enableWindow(Rectangle visibleArea) {
        for (Image image : images) {
            image.setSize(0,0);
        }
        images[0].setPosition(0,0);
        images[0].setSize(visibleArea.getX(), visibleArea.getY());

        images[1].setPosition(visibleArea.getX(), 0);
        images[1].setSize(visibleArea.getWidth(), visibleArea.getY());

        images[2].setPosition(visibleArea.getX() + visibleArea.getWidth(), 0);
        images[2].setSize(getWidth() - visibleArea.getWidth() - visibleArea.getX(), visibleArea.getY());

        images[3].setPosition(0, visibleArea.getY());
        images[3].setSize(visibleArea.getX(), visibleArea.getHeight());

        images[4].setPosition(visibleArea.getX() + visibleArea.getWidth(), visibleArea.getY());
        images[4].setSize(getWidth()- visibleArea.getWidth() - visibleArea.getX(), visibleArea.getHeight());

        images[5].setPosition(0, 0 + visibleArea.getY() + visibleArea.getHeight());
        images[5].setSize(visibleArea.getX(), getHeight() - visibleArea.getY() - visibleArea.getHeight());

        images[6].setPosition(visibleArea.getX(), visibleArea.getY() + visibleArea.getHeight());
        images[6].setSize(visibleArea.getWidth(), getHeight() - visibleArea.getY() - visibleArea.getHeight());

        images[7].setPosition(visibleArea.getX() + visibleArea.getWidth(), visibleArea.getY() + visibleArea.getHeight());
        images[7].setSize(getWidth() - visibleArea.getWidth() - visibleArea.getX(), getHeight() - visibleArea.getY() - visibleArea.getHeight());
    }

    public void setSemiTransparent() {
        for (Image image : images) {
            image.setColor(0,0,0,0.66f);
        }
    }

    public void setClickable() {
        setTouchable(Touchable.disabled);
    }
}
