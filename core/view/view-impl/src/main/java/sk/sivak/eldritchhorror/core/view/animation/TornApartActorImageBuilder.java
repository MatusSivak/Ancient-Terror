package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/** Small, owned snapshots: capture without advancing actions or changing the live portrait. */
final class TornApartActorImageBuilder {
    static final float PADDED_SIZE = 1.12f;
    private static final int MAX_TEXTURE_SIZE = 512;
    private static final String[] MASKS = {"mm_left.png", "mm_right.png", "mm_bottom.png"};

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
            for (int i = 0; i < MASKS.length; i++) {
                FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false);
                result.buffers[i] = buffer;
                buffer.begin();
                try {
                    Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    batch.begin();
                    batch.setColor(Color.WHITE);
                    batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                            GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    subject.draw(batch, 1f);
                    batch.setColor(Color.WHITE);
                    batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
                    batch.draw(getTexture(MASKS[i]), 0, 0, bufferWidth, bufferHeight);
                    batch.end();
                } finally {
                    if (batch.isDrawing()) batch.end();
                    buffer.end(viewport.getScreenX(), viewport.getScreenY(),
                            viewport.getScreenWidth(), viewport.getScreenHeight());
                }
                buffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                result.regions[i] = new TextureRegion(buffer.getColorBufferTexture());
                result.regions[i].flip(false, true);
            }
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
        private final FrameBuffer[] buffers = new FrameBuffer[3];
        private final TextureRegion[] regions = new TextureRegion[3];

        TextureRegion get(int index) {
            return regions[index];
        }

        @Override public void dispose() {
            for (int i = 0; i < buffers.length; i++) {
                if (buffers[i] != null) {
                    buffers[i].dispose();
                    buffers[i] = null;
                }
            }
        }
    }
}