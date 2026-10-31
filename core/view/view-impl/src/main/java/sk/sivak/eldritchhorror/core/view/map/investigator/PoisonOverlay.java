package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Sickly green vapour rising around the stand's edges, leaving the face readable. */
final class PoisonOverlay {
    private Image wisp;
    private Image skull;

    void draw(Batch batch, float parentAlpha, Actor stand, float time) {
        if (wisp == null) wisp = new Image(CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE));
        int source = batch.getBlendSrcFunc(), destination = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < 9; i++) {
            float life = (time * .32f + i / 9f) % 1f;
            float side = i % 2 == 0 ? .12f : .88f;
            float size = stand.getWidth() * (.3f + .25f * life);
            float x = stand.getWidth() * (side + .07f * MathUtils.sin(time * 2f + i)) - size / 2f;
            float y = stand.getHeight() * (.02f + .72f * life) - size / 2f;
            wisp.setBounds(stand.getX() + x, stand.getY() + y, size, size);
            wisp.setOrigin(stand.getOriginX() - x, stand.getOriginY() - y);
            wisp.setScale(stand.getScaleX(), stand.getScaleY());
            wisp.setRotation(stand.getRotation());
            wisp.setColor(.43f + .2f * life, .85f, .12f,
                    stand.getColor().a * .7f * MathUtils.sin(life * MathUtils.PI) * Math.min(1f, time / .3f));
            wisp.draw(batch, parentAlpha);
        }
        batch.setBlendFunction(source, destination);
        if (skull == null) skull = new Image(CustomAssetManager.getTexture("icon/skull.png"));
        float width = stand.getWidth() * .32f;
        float height = width * 64f / 60f;
        float x = (stand.getWidth() - width) / 2f, y = stand.getHeight() * .12f;
        skull.setBounds(stand.getX() + x, stand.getY() + y, width, height);
        skull.setOrigin(stand.getOriginX() - x, stand.getOriginY() - y);
        skull.setScale(stand.getScaleX(), stand.getScaleY());
        skull.setRotation(stand.getRotation());
        skull.setColor(.6f, 1f, .2f, stand.getColor().a * (.85f + .15f * MathUtils.sin(time * 3f)));
        skull.draw(batch, parentAlpha);
    }
}
