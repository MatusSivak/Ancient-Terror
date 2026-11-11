package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.utils.EffectTextures;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.SoundPlayer;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * Full-screen feedback when the investigator is hit: the HUD and the map jolt and the screen border flares.
 * Health hits are a hard, short jolt with a blood-red border; sanity hits a slower sway with a throbbing blue one.
 * Only the camera offset added in the previous frame is undone, so the cameras end exactly where they would have been.
 */
public final class ScreenHitEffect extends Actor {
    public static final String NAME = "screen-hit-effect";

    private static final Color HEALTH_EDGE = new Color(.5f, 0f, 0f, 1f);
    private static final Color HEALTH_GLOW = new Color(1f, .14f, .06f, 1f);
    private static final Color SANITY_EDGE = new Color(.02f, .05f, .3f, 1f);
    private static final Color SANITY_GLOW = new Color(.3f, .55f, 1f, 1f);
    private static final float HEALTH_DURATION = .6f, SANITY_DURATION = .95f;
    private static final float HEALTH_SHAKE = 7f, SANITY_SHAKE = 4.5f;
    // The rim is drawn slightly past the screen so the shaking HUD never uncovers its edge.
    private static final float PAD = 9f;

    private final Camera hudCamera;
    private final OrthographicCamera mapCamera;
    private boolean sanity, reduced;
    private float time, duration;
    private float hudX, hudY, mapX, mapY;

    private ScreenHitEffect(Camera hudCamera, OrthographicCamera mapCamera) {
        this.hudCamera = hudCamera;
        this.mapCamera = mapCamera;
        setName(NAME);
        setTouchable(Touchable.disabled);
    }

    /** Plays (or restarts) the hit feedback on top of the info stage. */
    public static void play(boolean sanity) {
        Stage stage = InfoStage.getStageSafe();
        ScreenHitEffect effect = stage.getRoot().findActor(NAME);
        if (effect == null) {
            effect = new ScreenHitEffect(stage.getCamera(), MapStage.getCamera());
            stage.addActor(effect);
        } else {
            effect.toFront();
        }
        effect.restart(sanity);
    }

    private void restart(boolean sanity) {
        this.sanity = sanity;
        time = 0f;
        duration = sanity ? SANITY_DURATION : HEALTH_DURATION;
        reduced = false;
        if (Gdx.app == null) return;
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        reduced = preferences.getBoolean("strobe_disabled", false);
        if (!preferences.getBoolean("vibration_disabled", false) && Gdx.input != null) {
            int millis = sanity ? 35 : 60;
            SoundPlayer.runInBackground(() -> Gdx.input.vibrate(millis));
        }
    }

    @Override public void act(float delta) {
        super.act(delta);
        time += delta * FastForwardAction.getSpeed();
        if (time >= duration) {
            remove();
            return;
        }
        if (reduced) return;
        float decay = (1f - time / duration) * (1f - time / duration);
        if (sanity) {
            shake(SANITY_SHAKE * decay * MathUtils.sin(time * 23f), SANITY_SHAKE * decay * .6f * MathUtils.sin(time * 31f + .7f));
        } else {
            shake(HEALTH_SHAKE * decay * MathUtils.sin(time * 73f + .4f), HEALTH_SHAKE * decay * .8f * MathUtils.sin(time * 97f + 1.3f));
        }
    }

    private void shake(float x, float y) {
        hudCamera.position.add(x - hudX, y - hudY, 0f);
        hudX = x;
        hudY = y;
        if (mapCamera == null) return;
        // Same on-screen distance at every zoom level.
        float zoom = mapCamera.zoom;
        mapCamera.position.add(x * zoom - mapX, y * zoom - mapY, 0f);
        mapX = x * zoom;
        mapY = y * zoom;
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float strength = Math.min(1f, time / .05f) * (float) Math.pow(1f - Math.min(1f, time / duration), 1.6f) * parentAlpha;
        if (sanity) strength *= .68f + .32f * MathUtils.cos(time * MathUtils.PI2 * 2.4f);
        if (reduced) strength *= .55f;
        if (strength <= 0f) return;
        Color edge = sanity ? SANITY_EDGE : HEALTH_EDGE;
        Color glow = sanity ? SANITY_GLOW : HEALTH_GLOW;
        float w = VIEWPORT_WIDTH + 2f * PAD, h = VIEWPORT_HEIGHT + 2f * PAD;
        float x = hudCamera.position.x - w / 2f, y = hudCamera.position.y - h / 2f;
        Texture vignette = EffectTextures.vignette();
        float r = batch.getColor().r, g = batch.getColor().g, b = batch.getColor().b, a = batch.getColor().a;

        batch.setColor(edge.r, edge.g, edge.b, .75f * strength);
        batch.draw(vignette, x, y, w, h);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.setColor(glow.r, glow.g, glow.b, .6f * strength);
        batch.draw(vignette, x, y, w, h);
        float flash = 1f - time / .14f;
        if (!reduced && flash > 0f) {
            // A broad glow reads as an almost even wash over the whole screen.
            batch.setColor(glow.r, glow.g, glow.b, .07f * flash * flash * parentAlpha);
            batch.draw(EffectTextures.glow(), hudCamera.position.x - w * 1.3f, hudCamera.position.y - w * 1.3f, w * 2.6f, w * 2.6f);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(r, g, b, a);
    }

    @Override protected void setStage(Stage stage) {
        if (stage == null && getStage() != null) shake(0f, 0f);
        super.setStage(stage);
    }
}
