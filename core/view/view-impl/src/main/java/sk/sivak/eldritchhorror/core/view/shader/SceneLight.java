package sk.sivak.eldritchhorror.core.view.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

/**
 * A point light that brightens the {@link LitImage} backgrounds around it (fireballs, gunshots).
 * Position and radius are in coordinates of {@link #stage}; {@link LitImage} maps them onto its own stage.
 * The ambient part lights the whole room evenly, like a camera flash.
 */
public final class SceneLight {

    static final Array<SceneLight> ACTIVE = new Array<>();

    final Color color = new Color();
    final Stage stage;
    float x, y, radius, intensity, ambient;

    private SceneLight(Stage stage, Color color, float radius) {
        this.stage = stage;
        this.color.set(color);
        this.radius = radius;
    }

    public static SceneLight add(Stage stage, Color color, float radius) {
        SceneLight light = new SceneLight(stage, color, radius);
        ACTIVE.add(light);
        return light;
    }

    /** Position in stage coordinates. */
    public SceneLight setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public SceneLight setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }

    public SceneLight setAmbient(float ambient) {
        this.ambient = ambient;
        return this;
    }

    public void remove() {
        ACTIVE.removeValue(this, true);
    }

    static boolean anyLit() {
        for (SceneLight light : ACTIVE) {
            if (light.intensity > 0.001f) return true;
        }
        return false;
    }

    /** A light at {@code at} (in {@code layer} coordinates) that flares up and dies out over {@code duration}. */
    public static void flash(Group layer, Vector2 at, Color color, float radius, float intensity, float ambient,
                             float delay, float duration) {
        if (layer == null || layer.getStage() == null) return;
        if (Gdx.app != null && Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled", false)) {
            intensity *= 0.35f;
            ambient = 0f;
        }
        Vector2 stagePosition = layer.localToStageCoordinates(new Vector2(at));
        FlashActor owner = new FlashActor(SceneLight.add(layer.getStage(), color, radius)
                .setPosition(stagePosition.x, stagePosition.y));
        layer.addActor(owner);
        SceneLight light = owner.light;
        float peak = intensity, peakAmbient = ambient;
        owner.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.delay(delay),
                new TemporalAction(duration) {
                    @Override
                    protected void update(float progress) {
                        float fade = 1f - Interpolation.pow2Out.apply(progress);
                        light.setIntensity(peak * fade).setAmbient(peakAmbient * fade);
                    }
                },
                Actions.removeActor())));
    }

    /** Owns a flash light; dropping it from the stage (finished, cleared, reset) also removes the light. */
    private static final class FlashActor extends Actor {
        private final SceneLight light;

        private FlashActor(SceneLight light) {
            this.light = light;
            setTouchable(Touchable.disabled);
        }

        @Override
        protected void setStage(Stage stage) {
            super.setStage(stage);
            if (stage == null) light.remove();
        }
    }
}
