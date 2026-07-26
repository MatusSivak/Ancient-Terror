package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;
import static sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.components.card.CardTemplate.CARD_WIDTH;

public class TornApartCardBuilder {

    public static TextureRegion buildLeftTextureRegion(CardTemplate cardTemplate) {
        return buildTextureRegion(cardTemplate, true);
    }

    public static TextureRegion buildRightTextureRegion(CardTemplate cardTemplate) {
        return buildTextureRegion(cardTemplate, false);
    }

    private static TextureRegion buildTextureRegion(CardTemplate cardTemplate, boolean left) {
        SpriteBatch spriteBatch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera(CARD_WIDTH, CARD_HEIGHT);
        camera.position.x = CARD_WIDTH / 2;
        camera.position.y = CARD_HEIGHT / 2;
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, CARD_WIDTH, CARD_HEIGHT, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        cardTemplate.act(0);
        cardTemplate.draw(spriteBatch, 1f);

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        Texture texture = left ? getTexture(CARD_TEMPLATE_MASK_LEFT) : getTexture(CARD_TEMPLATE_MASK_RIGHT);
        spriteBatch.draw(texture, 0, 0, CARD_WIDTH, CARD_HEIGHT);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.end();
        frameBuffer.end();
        TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        textureRegion.flip(false, true);
        return textureRegion;
    }
}
