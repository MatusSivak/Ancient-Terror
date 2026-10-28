package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Dust at the landing point, shared by all map token spawns. */
public final class MapSpawnEffect extends Actor {
    private final Actor token;
    private final Texture smoke = getTexture(GATE_SMOKE);
    private final float x, y;
    private float progress;

    public MapSpawnEffect(Actor token) {
        this.token = token;
        x = token.getX() + token.getOriginX();
        y = token.getY() + token.getOriginY();
        setName("map-spawn-effect");
        setTouchable(Touchable.disabled);
        addAction(new FastForwardAction<>(new TemporalAction(.65f * NORMAL_ACTION_DURATION) {
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
        batch.setColor(previous);
    }
}
