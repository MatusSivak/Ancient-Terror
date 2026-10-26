package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

/** One owned texture for the entire tear; preserve text resolution and every live actor property. */
final class CardSnapshot implements Disposable {
    private FrameBuffer buffer;

    CardSnapshot(Actor card, float width, float height) {
        if (card instanceof Layout) ((Layout) card).validate();
        float x = card.getX(), y = card.getY(), sx = card.getScaleX(), sy = card.getScaleY();
        float ox = card.getOriginX(), oy = card.getOriginY(), rotation = card.getRotation();
        Color color = new Color(card.getColor());
        Viewport viewport = card.getStage().getViewport();
        Group parent = card.getParent();
        int index = card.getZIndex();
        float resolution = 1024f / Math.max(width, height);
        SpriteBatch batch = new SpriteBatch();
        try {
            buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Math.max(1, Math.round(width * resolution)),
                    Math.max(1, Math.round(height * resolution)), false);
            // Group.draw composes its parent's world transform even when called outside Stage.draw.
            // Detach through the parent so an overridden Actor.remove() cannot run dismissal logic.
            parent.removeActor(card, false);
            card.setPosition(0, 0); card.setOrigin(0, 0); card.setScale(1); card.setRotation(0);
            card.setColor(Color.WHITE);
            OrthographicCamera camera = new OrthographicCamera(width, height);
            camera.position.set(width / 2, height / 2, 0); camera.update();
            batch.setProjectionMatrix(camera.combined);
            buffer.begin();
            try {
                Gdx.gl.glClearColor(0, 0, 0, 0); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                        GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                // Never act(0): a snapshot must not run callbacks or change a reusable Mystery card.
                card.draw(batch, 1f);
                batch.end();
            } finally {
                if (batch.isDrawing()) batch.end();
                buffer.end(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
            }
            texture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } catch (RuntimeException | Error failure) {
            dispose(); throw failure;
        } finally {
            batch.dispose();
            card.setPosition(x, y); card.setOrigin(ox, oy); card.setScale(sx, sy);
            card.setRotation(rotation); card.setColor(color);
            parent.addActorAt(index, card);
        }
    }

    Texture texture() { return buffer.getColorBufferTexture(); }

    @Override public void dispose() {
        if (buffer != null) { buffer.dispose(); buffer = null; }
    }
}
