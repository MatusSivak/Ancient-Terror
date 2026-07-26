package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class CardMaskedImageBuilder {

    public static TextureRegion buildMaskedTextureRegion(Texture cardTexture) {
        Texture shadowTexture = getTexture(ACTION_BUTTON_CARD_MASK_SHADOW);

        int imageWidth = shadowTexture.getWidth();
        int imageHeight = shadowTexture.getHeight();
        SpriteBatch spriteBatch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera(imageWidth, imageHeight);
        camera.position.x = imageWidth / 2;
        camera.position.y = imageHeight / 2;
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, imageWidth, imageHeight, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        int offsetY = imageHeight-cardTexture.getHeight();
        spriteBatch.draw(cardTexture,0, offsetY,cardTexture.getWidth(),cardTexture.getHeight());

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        Texture maskTexture = getTexture(ACTION_BUTTON_CARD_MASK);
        spriteBatch.draw(maskTexture, 0, offsetY, maskTexture.getWidth(), maskTexture.getHeight());


        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.draw(shadowTexture,0,0,imageWidth,imageHeight);

        spriteBatch.end();
        frameBuffer.end();
        TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        textureRegion.flip(false, true);
        return textureRegion;
    }
}
