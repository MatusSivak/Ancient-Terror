package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

/** The round reaches its target after {@link #FLIGHT}. A few frames of muzzle flash and tracer lead into the longer spark and smoke trails. */
final class BulletTracer extends Actor {
    /** Time for the round to reach its target; hit effects should wait this long. */
    static final float FLIGHT = 0.075f;
    private static final float FLASH = 0.095f;
    private static final float DURATION = 0.34f;
    /** How long the glowing streak lingers behind the round after the hit. */
    private static final float TRAIL_FADE = 0.17f;

    private final Texture glint = CustomAssetManager.getTexture(CustomAssetManager.LIGHT);
    private final Texture smoke = CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE);
    private final Texture white = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
    private final float[] vertices = new float[20];
    private final Vector2 direction;
    private final Vector2 impact;
    private final float[] sparkAngles = new float[8];
    private final float[] sparkDistances = new float[sparkAngles.length];
    private float elapsed;

    private BulletTracer(Vector2 shooter, Vector2 impact) {
        this.impact = new Vector2(impact);
        direction = new Vector2(impact).sub(shooter);
        setPosition(shooter.x, shooter.y);
        setWidth(direction.len());
        setRotation(direction.angle());
        direction.nor();
        setName("combat-bullet-tracer");
        setTouchable(Touchable.disabled);
        for (int i = 0; i < sparkAngles.length; i++) {
            sparkAngles[i] = getRotation() + i * 360f / sparkAngles.length + MathUtils.random(-15f, 15f);
            sparkDistances[i] = MathUtils.random(17f, 42f);
        }
    }

    static void fire(Group layer, Vector2 shooter, Vector2 impact) {
        BulletTracer shot = new BulletTracer(shooter, impact);
        layer.addActor(shot);
        CombatSounds.playDiceFireballLaunch();
        shot.addAction(new FastForwardAction<>(Actions.sequence(new TemporalAction(DURATION) {
            @Override protected void update(float progress) {
                shot.elapsed = progress * DURATION;
            }
        }, Actions.removeActor())));
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float savedColor = batch.getPackedColor();
        float life = elapsed / DURATION;
        float drift = Interpolation.pow2Out.apply(life);
        float puff = 13f + 23f * drift;
        float x = getX() + direction.x * (8f + 12f * drift);
        float y = getY() + direction.y * (8f + 12f * drift) + 12f * drift;
        batch.setColor(0.71f, 0.66f, 0.57f, parentAlpha * 0.54f * (1f - life));
        sprite(batch, smoke, x, y, puff, puff * 0.8f, 35f * life);

        // Glowing parts are drawn additively so they read clearly over the dark combat backdrop.
        int src = batch.getBlendSrcFunc();
        int dst = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        drawTrail(batch, parentAlpha);
        if (elapsed < FLASH) drawMuzzle(batch, parentAlpha * (1f - elapsed / FLASH));
        if (elapsed < FLIGHT) drawRound(batch, parentAlpha);
        if (elapsed >= FLIGHT) drawImpact(batch, parentAlpha);
        batch.setBlendFunction(src, dst);
        batch.setColor(savedColor);
    }

    /** A hot streak from the muzzle to the round; its tail catches up with the impact and fades. */
    private void drawTrail(Batch batch, float alpha) {
        float head = getWidth() * Math.min(1f, elapsed / FLIGHT);
        float start = Math.min(13f, head);
        float catchUp = elapsed <= FLIGHT ? 0f : Math.min(1f, (elapsed - FLIGHT) / TRAIL_FADE);
        float tail = MathUtils.lerp(start, head, Interpolation.pow2Out.apply(catchUp));
        if (head - tail < 1f) return;
        float fade = alpha * (1f - catchUp * 0.7f);
        beam(batch, tail, head, 11f, Color.toFloatBits(1f, 0.45f, 0.1f, fade * 0.35f));
        beam(batch, tail, head, 4.5f, Color.toFloatBits(1f, 0.75f, 0.35f, fade * 0.8f));
        beam(batch, tail, head, 1.8f, Color.toFloatBits(1f, 1f, 0.9f, fade));
    }

    private void beam(Batch batch, float from, float to, float width, float color) {
        float x1 = getX() + direction.x * from, y1 = getY() + direction.y * from;
        float x2 = getX() + direction.x * to, y2 = getY() + direction.y * to;
        float nx = -direction.y * width * 0.5f, ny = direction.x * width * 0.5f;
        vertex(0, x1 + nx, y1 + ny, color);
        vertex(5, x1 - nx, y1 - ny, color);
        vertex(10, x2 - nx, y2 - ny, color);
        vertex(15, x2 + nx, y2 + ny, color);
        batch.draw(white, vertices, 0, vertices.length);
    }

    private void drawMuzzle(Batch batch, float alpha) {
        float angle = getRotation();
        float offset = Math.min(13f, getWidth() * 0.2f);
        float x = getX() + direction.x * offset;
        float y = getY() + direction.y * offset;
        batch.setColor(1f, 0.46f, 0.12f, alpha);
        sprite(batch, smoke, x, y, 33f, 21f, angle);
        sprite(batch, glint, x, y, 90f, 58f, angle + 12f);
        float flare = 0.55f + 0.45f * (1f - elapsed / FLASH);
        float amber = Color.toFloatBits(1f, 0.55f, 0.12f, alpha);
        float ivory = Color.toFloatBits(1f, 0.98f, 0.82f, alpha);
        streak(batch, x, y, angle + 26f, 22f * flare, 6f * flare, amber);
        streak(batch, x, y, angle - 24f, 25f * flare, 6f * flare, amber);
        streak(batch, x - direction.x * 5f, y - direction.y * 5f, angle, 43f * flare, 12f * flare, amber);
        streak(batch, x - direction.x * 3f, y - direction.y * 3f, angle, 30f * flare, 6f * flare, ivory);
        batch.setColor(1f, 0.87f, 0.55f, alpha);
        sprite(batch, glint, x - direction.x * 5f, y - direction.y * 5f, 34f, 26f, angle - 18f);
    }

    private void drawRound(Batch batch, float alpha) {
        float progress = elapsed / FLIGHT;
        float head = getWidth() * (0.12f + 0.88f * progress);
        float length = Math.min(head, 62f);
        float x = getX() + direction.x * (head - length * 0.5f);
        float y = getY() + direction.y * (head - length * 0.5f);
        batch.setColor(1f, 0.63f, 0.18f, alpha);
        sprite(batch, glint, x, y, length * 2.4f, 14f, getRotation());
        streak(batch, x - direction.x * length * 0.5f, y - direction.y * length * 0.5f,
                getRotation(), length, 4.5f, Color.toFloatBits(1f, 0.78f, 0.35f, alpha));
        batch.setColor(1f, 0.96f, 0.79f, alpha);
        sprite(batch, glint, getX() + direction.x * head, getY() + direction.y * head, 36f, 22f, getRotation());
    }

    private void drawImpact(Batch batch, float alpha) {
        float life = (elapsed - FLIGHT) / (DURATION - FLIGHT);
        float travel = Interpolation.pow2Out.apply(life);
        float fade = 1f - life;
        batch.setColor(1f, 0.83f, 0.55f, alpha * fade * fade);
        sprite(batch, glint, impact.x, impact.y, 68f * fade, 58f * fade, getRotation());
        float spark = Color.toFloatBits(1f, 0.50f + 0.43f * fade, 0.22f + 0.4f * fade, alpha * fade);
        for (int i = 0; i < sparkAngles.length; i++) {
            float angle = sparkAngles[i];
            float distance = 3f + sparkDistances[i] * travel;
            float x = impact.x + MathUtils.cosDeg(angle) * distance;
            float y = impact.y + MathUtils.sinDeg(angle) * distance - 9f * life * life;
            streak(batch, x, y, angle, 2f + 11f * fade, 1f + 2f * fade, spark);
        }
    }

    /** Taper both ends of a single batch quad. Its widest point sits just past the base. */
    private void streak(Batch batch, float x, float y, float angle, float length, float width, float color) {
        float dx = MathUtils.cosDeg(angle);
        float dy = MathUtils.sinDeg(angle);
        float shoulderX = x + dx * length * 0.28f;
        float shoulderY = y + dy * length * 0.28f;
        float halfWidth = width * 0.5f;
        vertex(0, x, y, color);
        vertex(5, shoulderX - dy * halfWidth, shoulderY + dx * halfWidth, color);
        vertex(10, x + dx * length, y + dy * length, color);
        vertex(15, shoulderX + dy * halfWidth, shoulderY - dx * halfWidth, color);
        batch.draw(white, vertices, 0, vertices.length);
    }

    private void vertex(int offset, float x, float y, float color) {
        vertices[offset] = x;
        vertices[offset + 1] = y;
        vertices[offset + 2] = color;
        // All four vertices sample the same solid white texel.
    }

    private static void sprite(Batch batch, Texture texture, float x, float y, float width, float height, float angle) {
        batch.draw(texture, x - width / 2f, y - height / 2f, width / 2f, height / 2f,
                width, height, 1f, 1f, angle, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }
}
