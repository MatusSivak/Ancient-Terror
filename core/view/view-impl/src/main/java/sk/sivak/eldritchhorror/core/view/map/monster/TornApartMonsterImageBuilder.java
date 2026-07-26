package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

import java.util.UUID;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

public class TornApartMonsterImageBuilder {

    public static TextureRegion buildBottomTextureRegion(Actor monsterImage) {
        return buildTextureRegion(monsterImage, getTexture("mm_bottom.png"));
    }

    public static TextureRegion buildLeftTextureRegion(Actor monsterImage) {
        return buildTextureRegion(monsterImage, getTexture("mm_left.png"));
    }

    public static TextureRegion buildRightTextureRegion(Actor monsterImage) {
        return buildTextureRegion(monsterImage, getTexture("mm_right.png"));
    }

    private static TextureRegion buildTextureRegion(Actor monsterImage, Texture maskTexture) {

        float viewportWidth = ViewProperties.VIEWPORT_WIDTH;
        float viewportHeight = ViewProperties.VIEWPORT_HEIGHT;
        Viewport viewport = monsterImage.getStage().getViewport();
        int frameBufferWidth = viewport.getScreenWidth();
        int frameBufferHeight = viewport.getScreenHeight();

        monsterImage.setPosition(0, 0);
        monsterImage.setScale(1f);
        monsterImage.setWidth(400);
        monsterImage.setHeight(400);

        SpriteBatch spriteBatch = new SpriteBatch();
        spriteBatch.setColor(Color.WHITE);
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
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        monsterImage.act(0);
        monsterImage.setColor(Color.WHITE);
        spriteBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        monsterImage.draw(spriteBatch, 1f);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.end();
        spriteBatch.begin();

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        spriteBatch.draw(maskTexture, 0, 0, monsterImage.getWidth(), monsterImage.getHeight());
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatch.end();
        frameBuffer.end(
                viewport.getLeftGutterWidth(),
                viewport.getBottomGutterHeight(),
                viewport.getScreenWidth(),
                viewport.getScreenHeight());
        TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture(),
                (int) (monsterImage.getWidth() * (frameBufferWidth / viewportWidth)),
                (int) (monsterImage.getHeight() * (frameBufferHeight / viewportHeight)));
        textureRegion.flip(false, true);

        return textureRegion;
    }
}
