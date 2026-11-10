package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Colors and drawing helpers shared by combat fireballs, their trails and their impacts. */
final class FireballPalette {

    private static final FireballPalette FIRE = new FireballPalette(false,
            new Color(1f, .9f, .7f, 1f), new Color(1f, .55f, .2f, 1f), new Color(.85f, .25f, .08f, 1f),
            new Color(.9f, .5f, .25f, 1f), new Color(.24f, .21f, .2f, 1f));
    // Blue flames are drawn through the channel-swapping shader, so their tints stay light.
    private static final FireballPalette HORROR = new FireballPalette(true,
            new Color(.9f, .95f, 1f, 1f), new Color(.45f, .65f, 1f, 1f), new Color(.6f, .7f, 1f, 1f),
            new Color(.4f, .5f, .85f, 1f), new Color(.19f, .2f, .26f, 1f));

    final boolean blue;
    /** White-hot core, flame body, cooling flame, freshly scorched smoke and cold smoke. */
    final Color hot, fire, ember, scorched, smoke;

    private FireballPalette(boolean blue, Color hot, Color fire, Color ember, Color scorched, Color smoke) {
        this.blue = blue;
        this.hot = hot;
        this.fire = fire;
        this.ember = ember;
        this.scorched = scorched;
        this.smoke = smoke;
    }

    static FireballPalette of(boolean blue) {
        return blue ? HORROR : FIRE;
    }

    /** Glow color for additive halos; unlike flame art, these are not channel-swapped. */
    Color glow() {
        return blue ? Fireball.HORROR_LIGHT : Fireball.FIRE_LIGHT;
    }

    /** Switches to the blue flame shader if needed; the caller restores the previous shader. */
    void beginFlames(Batch batch) {
        ShaderProgram shader = blue ? FireballImage.blueShader() : null;
        if (shader != null) batch.setShader(shader);
    }

    static TextureRegion[] frames() {
        TextureRegionDrawable[] drawables = CustomAssetManager.getFireballFrames();
        TextureRegion[] frames = new TextureRegion[drawables.length];
        for (int i = 0; i < drawables.length; i++) frames[i] = drawables[i].getRegion();
        return frames;
    }

    static void draw(Batch batch, Texture texture, float cx, float cy, float w, float h, float rotation) {
        batch.draw(texture, cx - w / 2f, cy - h / 2f, w / 2f, h / 2f, w, h, 1f, 1f, rotation,
                0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    static void draw(Batch batch, TextureRegion region, float cx, float cy, float w, float h, float rotation) {
        batch.draw(region, cx - w / 2f, cy - h / 2f, w / 2f, h / 2f, w, h, 1f, 1f, rotation);
    }
}
