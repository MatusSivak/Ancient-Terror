package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

class TornApartMonsterCardBuilder {

    private final static int MONSTER_CARD_WIDTH = 1067;
    private final static int MONSTER_CARD_HEIGHT = 748;

    public static TextureRegion buildBottomLeftTextureRegion(Actor someActor) {
        return buildTextureRegion(someActor, getTexture("ms_bottom_left.png"));
    }

    public static TextureRegion buildTopLeftTextureRegion(Actor someActor) {
        return buildTextureRegion(someActor, getTexture("ms_top_left.png"));
    }

    public static TextureRegion buildBottomRightTextureRegion(Actor someActor) {
        return buildTextureRegion(someActor, getTexture("ms_bottom_right.png"));
    }

    public static TextureRegion buildTopRightTextureRegion(Actor someActor) {
        return buildTextureRegion(someActor, getTexture("ms_top_right.png"));
    }

    private static TextureRegion buildTextureRegion(Actor someActor, Texture maskTexture) {

        float viewportWidth = ViewProperties.VIEWPORT_WIDTH;
        float viewportHeight = ViewProperties.VIEWPORT_HEIGHT;
        Viewport viewport = someActor.getStage().getViewport();
        int frameBufferWidth = viewport.getScreenWidth();
        int frameBufferHeight = viewport.getScreenHeight();

        someActor.setPosition(0, 0);
        someActor.setScale(1f);
        SpriteBatch spriteBatch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.position.x = viewportWidth / 2f;
        camera.position.y = viewportHeight / 2f;
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, frameBufferWidth, frameBufferHeight, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0f, 0f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        someActor.act(0);
        someActor.draw(spriteBatch, 1f);

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        spriteBatch.draw(maskTexture, 0, 0, someActor.getWidth(), someActor.getHeight());

        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.end();
        frameBuffer.end(
                viewport.getLeftGutterWidth(),
                viewport.getBottomGutterHeight(),
                viewport.getScreenWidth(),
                viewport.getScreenHeight());
        TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture(),
                (int) (someActor.getWidth() * (frameBufferWidth / viewportWidth)),
                (int) (someActor.getHeight() * (frameBufferHeight / viewportHeight)));
        textureRegion.flip(false, true);
        return textureRegion;
    }
}
