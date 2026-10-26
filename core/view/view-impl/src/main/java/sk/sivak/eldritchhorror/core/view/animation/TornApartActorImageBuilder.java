package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

/** One padded snapshot shared by every shard; capture never advances the live actor. */
final class TornApartActorImageBuilder {
    static final float PADDED_SIZE = 1.12f;
    private static final int MAX_TEXTURE_SIZE = 512;

    static Fragments build(Actor subject) {
        float x = subject.getX(), y = subject.getY(), width = subject.getWidth(), height = subject.getHeight();
        float scaleX = subject.getScaleX(), scaleY = subject.getScaleY(), rotation = subject.getRotation();
        float originX = subject.getOriginX(), originY = subject.getOriginY();
        Color color = new Color(subject.getColor());
        Viewport viewport = subject.getStage().getViewport();
        float captureScale = MAX_TEXTURE_SIZE / (Math.max(width, height) * PADDED_SIZE);
        int bufferWidth = Math.max(1, Math.round(width * captureScale * PADDED_SIZE));
        int bufferHeight = Math.max(1, Math.round(height * captureScale * PADDED_SIZE));
        Fragments result = new Fragments();
        SpriteBatch batch = new SpriteBatch();
        try {
            float worldWidth = bufferWidth / captureScale, worldHeight = bufferHeight / captureScale;
            // Keep the actor's size intact: groups may contain independently sized layers.
            subject.setPosition((worldWidth - width) / 2, (worldHeight - height) / 2);
            subject.setOrigin(width / 2, height / 2);
            subject.setScale(1f);
            subject.setRotation(0f);
            subject.setColor(Color.WHITE);
            OrthographicCamera camera = new OrthographicCamera(worldWidth, worldHeight);
            camera.position.set(worldWidth / 2f, worldHeight / 2f, 0f);
            camera.update();
            batch.setProjectionMatrix(camera.combined);
            FrameBuffer buffer = result.buffer = new FrameBuffer(Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false);
            buffer.begin();
            try {
                Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                batch.setColor(Color.WHITE);
                batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                        GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                subject.draw(batch, 1f);
                batch.end();
            } finally {
                if (batch.isDrawing()) batch.end();
                buffer.end(viewport.getScreenX(), viewport.getScreenY(),
                        viewport.getScreenWidth(), viewport.getScreenHeight());
            }
            buffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return result;
        } catch (RuntimeException | Error failure) {
            result.dispose();
            throw failure;
        } finally {
            batch.dispose();
            subject.setBounds(x, y, width, height);
            subject.setOrigin(originX, originY);
            subject.setScale(scaleX, scaleY);
            subject.setRotation(rotation);
            subject.setColor(color);
        }
    }

    static final class Fragments implements Disposable {
        private FrameBuffer buffer;

        Texture texture() { return buffer.getColorBufferTexture(); }

        @Override public void dispose() {
            if (buffer != null) { buffer.dispose(); buffer = null; }
        }
    }
}
