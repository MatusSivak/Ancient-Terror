package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.HashMap;
import java.util.Map;

public class BoltAnimationActor extends Actor {

    private float stateTime;
    private BoltAnimation boltAnimation;
    private Bolt bolt;
    private FrameBuffer frameBuffer;
    private TextureRegion textureRegion;
    private boolean redrawNeeded;

    public BoltAnimationActor(BoltAnimation boltAnimation) {
        this.boltAnimation = boltAnimation;
        this.stateTime = MathUtils.random(0.2f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta * MathUtils.random(2);
        Bolt keyFrame = boltAnimation.getKeyFrame(stateTime);
        if (keyFrame != bolt) {
            redrawNeeded = true;
        }
        bolt = keyFrame;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        long start = System.currentTimeMillis();
        Viewport viewport;
        if (getStage() == null) {
            viewport = InfoStage.getStageSafe().getViewport();
        } else {
            viewport = getStage().getViewport();
        }

        if (textureRegion == null) {
            if (frameBuffer == null) {
                frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                        viewport.getScreenWidth(),
                        viewport.getScreenHeight(), true);
            }
            drawToBuffer(viewport, batch, parentAlpha);
        }
        if (redrawNeeded) {
            redrawNeeded = false;
            drawToBuffer(viewport, batch, parentAlpha);
        }

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(textureRegion, 0, 0, ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (start != System.currentTimeMillis()) {
            System.out.println("Duration: " + (System.currentTimeMillis() - start));
        }
    }

    private void drawToBuffer(Viewport viewport, Batch batch, float parentAlpha) {
        frameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        bolt.draw(batch, parentAlpha);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        frameBuffer.end(
                viewport.getLeftGutterWidth(),
                viewport.getBottomGutterHeight(),
                viewport.getScreenWidth(),
                viewport.getScreenHeight());
        System.out.println("Creating new texture region");
        textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        textureRegion.flip(false, true);

    }

    /*
    @Override
    public void draw(Batch batch, float parentAlpha) {
        long start = System.currentTimeMillis();
        bolt.draw(batch, parentAlpha);
        System.out.println("Duration: " + (System.currentTimeMillis() - start));

    }
    */
}
