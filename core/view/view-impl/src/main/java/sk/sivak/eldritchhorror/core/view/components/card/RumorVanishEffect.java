package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/** The rumor's ink and paper break into a spiral, leaving no sheet or snapshot behind. */
public final class RumorVanishEffect extends Actor {
    private final CardSnapshot snapshot;
    private final float[] vertices = new float[20];
    private final float rotation;
    private float progress;
    private boolean disposed;

    public static Completable play(Actor card) {
        return Completable.create(subscriber -> {
            if (card == null || card.getStage() == null) { subscriber.onCompleted(); return; }
            Stage stage = card.getStage();
            float width = card.getWidth(), height = card.getHeight();
            if (width <= 0 || height <= 0) { card.remove(); subscriber.onCompleted(); return; }
            Vector2 center = card.localToStageCoordinates(new Vector2(width / 2, height / 2));
            Vector2 origin = card.localToStageCoordinates(new Vector2());
            Vector2 right = card.localToStageCoordinates(new Vector2(width, 0)).sub(origin);
            Vector2 up = card.localToStageCoordinates(new Vector2(0, height)).sub(origin);
            CardSnapshot snapshot = new CardSnapshot(card, width, height);
            RumorVanishEffect effect = new RumorVanishEffect(snapshot, center, right.len(), up.len(), right.angle());
            card.clearActions();
            card.setTouchable(Touchable.disabled);
            card.remove();
            stage.addActor(effect);
            effect.addAction(new FastForwardAction<>(new TemporalAction(1.25f * NORMAL_ACTION_DURATION) {
                @Override protected void update(float percent) { effect.progress = percent; }
                @Override protected void end() {
                    effect.remove();
                    subscriber.onCompleted();
                }
            }));
        });
    }

    private RumorVanishEffect(CardSnapshot snapshot, Vector2 center, float width, float height, float rotation) {
        this.snapshot = snapshot;
        this.rotation = rotation;
        setName("rumor-vanish");
        setTouchable(Touchable.disabled);
        setBounds(center.x - width / 2, center.y - height / 2, width, height);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (disposed) return;
        float packed = batch.getPackedColor();
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
        int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        int columns = 10, rows = 8;
        float tileWidth = getWidth() / columns, tileHeight = getHeight() / rows;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                float dx = (column + .5f) * tileWidth - getWidth() / 2;
                float dy = (row + .5f) * tileHeight - getHeight() / 2;
                float delay = ((column * 3 + row * 7) % 11) * .012f;
                float p = MathUtils.clamp((progress - delay) / (1f - delay), 0f, 1f);
                float collapse = Interpolation.pow2In.apply(p);
                float turn = rotation + 155f * collapse;
                float cos = MathUtils.cosDeg(turn), sin = MathUtils.sinDeg(turn);
                // Contract from the first frame so the turning corners stay inside the viewport.
                float cx = getX() + getWidth() / 2 + (dx * cos - dy * sin) * (1f - p);
                float cy = getY() + getHeight() / 2 + (dx * sin + dy * cos) * (1f - p);
                float scale = 1f - Interpolation.pow2Out.apply(p);
                float alpha = (1f - p * p) * parentAlpha * getColor().a;
                float tint = Color.toFloatBits(alpha * (1f - .40f * p), alpha * (1f - .08f * p), alpha, alpha);
                for (int corner = 0; corner < 4; corner++) {
                    boolean right = corner >= 2, top = corner == 1 || corner == 2;
                    float x = (right ? .5f : -.5f) * tileWidth * scale;
                    float y = (top ? .5f : -.5f) * tileHeight * scale;
                    int v = corner * 5;
                    vertices[v] = cx + x * cos - y * sin;
                    vertices[v + 1] = cy + x * sin + y * cos;
                    vertices[v + 2] = tint;
                    vertices[v + 3] = (column + (right ? 1f : 0f)) / columns;
                    vertices[v + 4] = (row + (top ? 1f : 0f)) / rows;
                }
                batch.draw(snapshot.texture(), vertices, 0, vertices.length);
            }
        }
        batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
        batch.setColor(packed);
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) {
            if (!disposed) { snapshot.dispose(); disposed = true; }
            clearActions();
        }
    }
}
