package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/** Gathering motes and a soft flash, driven by the token's reveal action. */
public final class TokenMaterializeEffect {
    private final Texture glow = getTexture("dice_shadow.png");
    private final Color accent;

    public TokenMaterializeEffect(Color accent) {
        this.accent = new Color(accent);
    }

    public void draw(Batch batch, float x, float y, float size, float progress, float parentAlpha) {
        float saved = batch.getPackedColor();
        float gather = MathUtils.clamp(progress / .62f, 0, 1);
        float fade = MathUtils.clamp((1 - progress) / .38f, 0, 1);
        float flash = MathUtils.sin(MathUtils.PI * progress);
        float glowSize = size * (.65f + flash * .8f);
        batch.setColor(accent.r, accent.g, accent.b, parentAlpha * flash * .7f);
        batch.draw(glow, x - glowSize / 2, y - glowSize / 2, glowSize, glowSize);
        for (int i = 0; i < 26; i++) {
            float angle = i * 137.508f + gather * 80f;
            float radius = size * (.15f + (1 - gather) * (.4f + (i % 5) * .08f));
            float px = x + MathUtils.cosDeg(angle) * radius;
            float py = y + MathUtils.sinDeg(angle) * radius * .75f;
            float particle = size * (.025f + (i % 3) * .012f);
            float alpha = Math.min(1, progress * 9) * fade * parentAlpha;
            batch.setColor(accent.r, accent.g, accent.b, alpha);
            batch.draw(glow, px - particle, py - particle, particle * 2, particle * 2);
            batch.setColor(1, 1, 1, alpha * .9f);
            batch.draw(glow, px - particle * .3f, py - particle * .3f, particle * .6f, particle * .6f);
        }
        batch.setColor(saved);
    }
}
