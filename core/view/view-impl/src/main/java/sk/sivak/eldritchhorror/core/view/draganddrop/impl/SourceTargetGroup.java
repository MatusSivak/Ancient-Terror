package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/**
 * @author msivak
 */
public class SourceTargetGroup extends Group {

    public final static float PADDING = VIEWPORT_HEIGHT * 0.01f;
    protected final TargetActor targetActor;
    protected final SourceDockActor sourceDockActor;
    protected final Image upDownImage;
    private boolean removed = false;

    public SourceTargetGroup() {
        targetActor = new TargetActor();
        sourceDockActor = new SourceDockActor();
        Sprite upDownSprite = createUpDownSprite();

        upDownImage = new Image(upDownSprite);
        upDownImage.setOrigin(upDownImage.getWidth() / 2, upDownImage.getHeight() / 2);
        addActor(upDownImage);
        addActor(targetActor);
        addActor(sourceDockActor);
    }

    protected Sprite createUpDownSprite() {
        Sprite sprite = new Sprite(CustomAssetManager.getTexture(CustomAssetManager.DOWN_UP));
        sprite.flip(true, false);
        return sprite;
    }

    public TargetActor getTargetActor() {
        return targetActor;
    }

    public SourceDockActor getSourceDockActor() {
        return sourceDockActor;
    }

    @Override
    public boolean remove() {
        boolean remove = super.remove();
        removed = true;
        removeActor(upDownImage);
        removeActor(targetActor);
        removeActor(sourceDockActor);
        return remove;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        upDownImage.setScale(0.25f);
        upDownImage.setPosition(getWidth() / 2 - upDownImage.getWidth() / 2,
                getHeight() / 2 - upDownImage.getHeight() / 2);

    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        sourceDockActor.setX(0);
        sourceDockActor.setY(0);
        targetActor.setX(0);
        targetActor.setY(getHeight() - targetActor.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (removed) {
            return;
        }
        batch.setColor(0, 0, 0, 0.5f * getColor().a * parentAlpha);
        batch.draw(getTexture(PURE_WHITE_BACKGROUND),
                getX() - PADDING,
                getY() - PADDING,
                getWidth() + PADDING * 2,
                getHeight() + PADDING * 2);
        super.draw(batch, parentAlpha);
    }
}
