package sk.sivak.eldritchhorror.core.view.map.monster;

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
public class TornApartMonsterImageBuilder {
    static final float PADDED_SIZE = 1.12f;
    private static final int MAX_TEXTURE_SIZE = 512;
    private static final String[] MASKS = {"mm_left.png", "mm_right.png", "mm_bottom.png"};

    static Fragments build(Actor monster) {
        float x = monster.getX(), y = monster.getY(), width = monster.getWidth(), height = monster.getHeight();
        float scaleX = monster.getScaleX(), scaleY = monster.getScaleY(), rotation = monster.getRotation();
        float originX = monster.getOriginX(), originY = monster.getOriginY();
        Color color = new Color(monster.getColor());
        Viewport viewport = monster.getStage().getViewport();
        float captureScale = MAX_TEXTURE_SIZE / (Math.max(width, height) * PADDED_SIZE);
        int bufferWidth = Math.max(1, Math.round(width * captureScale * PADDED_SIZE));
        int bufferHeight = Math.max(1, Math.round(height * captureScale * PADDED_SIZE));
        Fragments result = new Fragments();
        SpriteBatch batch = new SpriteBatch();
        try {
            float captureWidth = bufferWidth / PADDED_SIZE, captureHeight = bufferHeight / PADDED_SIZE;
            monster.setBounds((bufferWidth - captureWidth) / 2, (bufferHeight - captureHeight) / 2,
                    captureWidth, captureHeight);
            monster.setOrigin(captureWidth / 2, captureHeight / 2);
            monster.setScale(1f);
            monster.setRotation(0f);
            monster.setColor(Color.WHITE);
            OrthographicCamera camera = new OrthographicCamera(bufferWidth, bufferHeight);
            camera.position.set(bufferWidth / 2f, bufferHeight / 2f, 0f);
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
                    monster.draw(batch, 1f);
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
            monster.setBounds(x, y, width, height);
            monster.setOrigin(originX, originY);
            monster.setScale(scaleX, scaleY);
            monster.setRotation(rotation);
            monster.setColor(color);
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