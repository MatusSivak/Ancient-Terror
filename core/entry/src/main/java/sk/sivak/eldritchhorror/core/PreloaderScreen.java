package sk.sivak.eldritchhorror.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sk.sivak.eldritchhorror.core.util.ServiceLocator;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * First screen after launch: draws a loading percentage while the heavy startup work and the
 * menu/game textures are prepared, then hands over to {@code onFinished}.
 */
public class PreloaderScreen extends ScreenAdapter {

    private static final int LOAD_BUDGET_MS = 25;
    private static final float TASKS_SHARE = 0.15f;
    private static final float BAR_WIDTH = 360f;
    private static final float BAR_HEIGHT = 6f;
    private static final float TITLE_SCALE = 0.6f;
    private static final Color BAR_BACK = new Color(0x2a2620ff);
    private static final Color BAR_FILL = new Color(0xbda16aff);
    private static final Color TEXT = new Color(0xeee1c5ff);

    private final Runnable onFinished;
    private final Runnable[] tasks = {
            ServiceLocator::getDefaultSkin,
            CustomAssetManager::createMenuDialogPatch,
            CustomAssetManager::queueStartupAssets
    };
    private final GlyphLayout layout = new GlyphLayout();

    private Viewport viewport;
    private SpriteBatch batch;
    private Texture white;
    private BitmapFont font;
    private int nextTask;
    private boolean firstFrameDrawn;
    private boolean finished;
    private float shownProgress;

    public PreloaderScreen(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    @Override
    public void show() {
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        white = new Texture(pixmap);
        pixmap.dispose();
        font = CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 24);
    }

    @Override
    public void render(float delta) {
        if (finished) {
            return;
        }
        // Draw before working so the percentage is on screen while a blocking step runs.
        if (firstFrameDrawn) {
            advance();
            if (finished) {
                return;
            }
        }
        draw();
        firstFrameDrawn = true;
    }

    private void advance() {
        float progress;
        if (nextTask < tasks.length) {
            tasks[nextTask++].run();
            progress = TASKS_SHARE * nextTask / tasks.length;
        } else {
            float assets = CustomAssetManager.pumpLoading(LOAD_BUDGET_MS);
            progress = TASKS_SHARE + (1f - TASKS_SHARE) * assets;
            if (assets >= 1f) {
                finished = true;
                onFinished.run();
                return;
            }
        }
        shownProgress = Math.max(shownProgress, progress);
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        float centerX = VIEWPORT_WIDTH / 2f;
        float barY = VIEWPORT_HEIGHT * 0.22f;
        if (CustomAssetManager.isAssetLoaded(CustomAssetManager.SPLASH_TITLE)) {
            Texture title = CustomAssetManager.getTexture(CustomAssetManager.SPLASH_TITLE);
            float width = title.getWidth() * TITLE_SCALE;
            float height = title.getHeight() * TITLE_SCALE;
            batch.draw(title, centerX - width / 2f, VIEWPORT_HEIGHT - 20f - height, width, height);
        }
        batch.setColor(BAR_BACK);
        batch.draw(white, centerX - BAR_WIDTH / 2f, barY, BAR_WIDTH, BAR_HEIGHT);
        batch.setColor(BAR_FILL);
        batch.draw(white, centerX - BAR_WIDTH / 2f, barY, BAR_WIDTH * shownProgress, BAR_HEIGHT);
        batch.setColor(Color.WHITE);
        font.setColor(TEXT);
        layout.setText(font, Math.round(shownProgress * 100) + "%");
        font.draw(batch, layout, centerX - layout.width / 2f, barY + BAR_HEIGHT + 14f + layout.height);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (white != null) {
            white.dispose();
            white = null;
        }
    }
}
