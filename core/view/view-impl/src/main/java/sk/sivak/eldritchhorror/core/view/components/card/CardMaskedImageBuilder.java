package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class CardMaskedImageBuilder {

    public static TextureRegion buildMaskedTextureRegion(Texture cardTexture) {
        return buildMaskedTextureRegion(cardTexture, false, false);
    }

    public static TextureRegion buildMaskedInvestigatorTextureRegion(Texture portraitTexture) {
        return buildMaskedTextureRegion(portraitTexture, true, false);
    }

    public static TextureRegion buildCircularMaskedTextureRegion(Texture cardTexture) {
        return buildMaskedTextureRegion(cardTexture, false, true);
    }

    private static TextureRegion buildMaskedTextureRegion(Texture cardTexture, boolean investigatorPortrait, boolean circular) {
        Texture shadowTexture = getTexture(ACTION_BUTTON_CARD_MASK_SHADOW);

        int imageWidth = 320;
        int imageHeight = circular ? imageWidth : 340;
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
        if (investigatorPortrait) {
            // Crop the top of the full-length portrait to show the face and upper body.
            int cropHeight = Math.min(cardTexture.getHeight(), cardTexture.getWidth() * imageHeight / imageWidth);
            int cropWidth = cropHeight * imageWidth / imageHeight;
            TextureRegion portrait = new TextureRegion(cardTexture,
                    (cardTexture.getWidth() - cropWidth) / 2, 0, cropWidth, cropHeight);
            spriteBatch.draw(portrait, 0, 0, imageWidth, imageHeight);
        } else {
            float offsetX = -(cardTexture.getWidth() - imageWidth) / 2f;
            spriteBatch.draw(cardTexture,offsetX, 0,cardTexture.getWidth(),cardTexture.getHeight());
        }

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        Texture maskTexture = circular ? createCircleMask(imageWidth) : getTexture(ACTION_BUTTON_CARD_MASK);
        spriteBatch.draw(maskTexture, 0, 0, maskTexture.getWidth(), maskTexture.getHeight());

        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (!circular) {
            spriteBatch.draw(shadowTexture,0,0,imageWidth,imageHeight);
        }

        spriteBatch.end();
        frameBuffer.end();
        spriteBatch.dispose();
        if (circular) maskTexture.dispose();
        TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        textureRegion.flip(false, true);
        return textureRegion;
    }
    private static Texture createCircleMask(int size) {
        Pixmap mask = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        mask.setBlending(Pixmap.Blending.None);
        float radius = size / 2f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x + 0.5f - radius, dy = y + 0.5f - radius;
                float coverage = Math.max(0f, Math.min(1f, radius - (float) Math.sqrt(dx * dx + dy * dy)));
                mask.setColor(coverage, coverage, coverage, coverage);
                mask.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(mask);
        mask.dispose();
        return texture;
    }
}
