package sk.sivak.eldritchhorror.core.view.components.sheet.mystery;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.shader.GrayscaleShader;

public class ProgressToken extends Image {
    private boolean activated;

    public ProgressToken() {
        super(CustomAssetManager.getTexture(CustomAssetManager.GATE));
        deactivateImmediately();
    }

    public void activateImmediately() {
        activated = true;
        getColor().a = 1f;
    }

    public void deactivateImmediately() {
        activated = false;
        getColor().a = 0.66f;
    }

    public void activate() {
        addAction(Actions.sequence(
                Actions.color(new Color(1f, 1f, 1f, 1f), ProgressTokenBar.ACTION_DURATION),
                Actions.run(() -> activated = true)
        ));
    }

    public void deactivate() {
        addAction(Actions.sequence(
                Actions.color(new Color(1f, 1f, 1f, 0.66f), ProgressTokenBar.ACTION_DURATION),
                Actions.run(() -> activated = false)
        ));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!activated) {
            batch.setShader(GrayscaleShader.get());
        }
        super.draw(batch, parentAlpha);
        batch.setShader(null);

    }
}
