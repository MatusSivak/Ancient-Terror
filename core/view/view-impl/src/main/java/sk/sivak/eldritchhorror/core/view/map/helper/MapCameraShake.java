package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.SoundPlayer;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/**
 * Briefly jolts the map camera. Only the offset added in the previous frame is undone, so dragging the
 * map during the shake keeps working and the camera ends exactly where it would have been.
 */
public final class MapCameraShake extends Actor {
    private float offsetX, offsetY;

    private MapCameraShake(float amplitude, float duration) {
        setName("map-camera-shake");
        setTouchable(Touchable.disabled);
        addAction(new FastForwardAction<>(new TemporalAction(duration) {
            @Override protected void update(float percent) {
                float decay = (1f - percent) * (1f - percent);
                float time = percent * getDuration();
                apply(amplitude * decay * MathUtils.sin(time * 71f),
                        amplitude * decay * .8f * MathUtils.sin(time * 89f + 1.3f));
            }

            @Override protected void end() { remove(); }
        }));
    }

    /** Shakes the map (scaled by zoom) and vibrates the device, honoring the reduced-effects settings. */
    public static void impact(float amplitude) {
        if (Gdx.app == null) return;
        if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("vibration_disabled", false) && Gdx.input != null) {
            SoundPlayer.runInBackground(() -> Gdx.input.vibrate(70));
        }
        if (Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled", false)) return;
        MapStage.getSkyEffectLayer().addActor(
                new MapCameraShake(amplitude * MapStage.getCamera().zoom, .5f * NORMAL_ACTION_DURATION));
    }

    private void apply(float x, float y) {
        MapStage.getCamera().position.add(x - offsetX, y - offsetY, 0f);
        offsetX = x;
        offsetY = y;
    }

    @Override protected void setStage(Stage stage) {
        if (stage == null && getStage() != null) {
            apply(0f, 0f);
            clearActions();
        }
        super.setStage(stage);
    }
}
