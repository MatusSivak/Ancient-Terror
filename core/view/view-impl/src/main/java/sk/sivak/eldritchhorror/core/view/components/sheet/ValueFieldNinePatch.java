package sk.sivak.eldritchhorror.core.view.components.sheet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class ValueFieldNinePatch extends NinePatchDrawable {

    public ValueFieldNinePatch() {
        super(new NinePatch(CustomAssetManager.getTexture(CustomAssetManager.VALUE_LABEL), 11, 11, 11, 11) {

            @Override
            public float getMiddleHeight() {
                return 0;
            }

            @Override
            public float getTopHeight() {
                return 6;
            }

            @Override
            public float getBottomHeight() {
                return 5;
            }

            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(new Color(1f, 1f, 1f, batch.getColor().a * 0.5f));
                batch.getColor().a *= 0.5f;
                super.draw(batch, x, y, width, height);
            }
        });
    }
}
