package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** A brief smoke bloom and expanding summoning ripple, shared by all map token spawns. */
public final class MapSpawnEffect extends Actor {
    private final Actor token;
    private final Texture smoke = getTexture(GATE_SMOKE);
    private final TextureRegion pixel = new TextureRegion(getTexture(PURE_WHITE_BACKGROUND));
    private float progress;

    public MapSpawnEffect(Actor token) {
        this.token = token;
        setName("map-spawn-effect");
        setTouchable(Touchable.disabled);
        addAction(new FastForwardAction<>(new TemporalAction(NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) { progress = percent; }
            @Override protected void end() { remove(); }
        }));
    }

    @Override public void act(float delta) {
        super.act(delta);
        if (token.getStage() == null) remove();
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) clearActions();
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float previous = batch.getPackedColor();
        float x = token.getX() + token.getOriginX();
        float y = token.getY() + token.getOriginY();
        float radius = MathUtils.clamp(Math.max(token.getWidth(), token.getHeight()) * .8f, 48, 100);
        float fade = (1 - progress) * (1 - progress) * parentAlpha * getColor().a;
        // Fixed angles keep the three wrapped copies in sync without per-frame randomness.
        for (int i = 0; i < 8; i++) {
            float angle = i * 45f + progress * 32f;
            float distance = radius * (.3f + progress * 1.1f);
            float size = radius * (.8f + progress * 1.2f);
            float px = x + MathUtils.cosDeg(angle) * distance;
            float py = y + MathUtils.sinDeg(angle) * distance * .70f;
            batch.setColor(.9f, .94f, .88f, fade * .75f);
            batch.draw(smoke, px - size / 2, py - size / 2, size / 2, size / 2,
                    size, size, 1, 1, angle, 0, 0, smoke.getWidth(), smoke.getHeight(), false, false);
        }
        float ringRadius = radius * (.55f + progress * 1.35f);
        batch.setColor(.92f, .82f, .52f, fade * .9f);
        for (int i = 0; i < 48; i++) {
            float angle = i * 7.5f;
            batch.draw(pixel, x + MathUtils.cosDeg(angle) * ringRadius,
                    y + MathUtils.sinDeg(angle) * ringRadius, 0, 1,
                    ringRadius * .132f, 2, 1, 1, angle + 90);
        }
        for (int i = 0; i < 10; i++) {
            float angle = i * 36f + 18f;
            float distance = radius * (.4f + progress * 1.65f);
            batch.draw(pixel, x + MathUtils.cosDeg(angle) * distance,
                    y + MathUtils.sinDeg(angle) * distance, 0, 1,
                    3 + 7 * (1 - progress), 2, 1, 1, angle);
        }
        batch.setColor(previous);
    }
}
