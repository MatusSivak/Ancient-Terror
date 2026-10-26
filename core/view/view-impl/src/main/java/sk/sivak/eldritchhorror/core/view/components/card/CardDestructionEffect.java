package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** A short lift, a visible jagged tear, then paper fragments that tumble and dissolve. */
public final class CardDestructionEffect extends Group {
    private final CardSnapshot snapshot;
    private final CardTearGeometry geometry;
    private final PaperPiece whole;
    private final PaperPiece[] pieces;
    private final Image halo;
    private final Color accent;
    private final boolean mystery;
    private final float chargeTime, splitTime, flightTime, duration;
    private final float startX, startY, startRotation, targetScale;
    private float charge, flight, separation;

    public static Completable discard(Actor card) { return play(card, false); }
    public static Completable resolveMystery(Actor card) { return play(card, true); }

    private static Completable play(Actor card, boolean mystery) {
        return Completable.create(subscriber -> {
            if (card == null || card.getStage() == null) { subscriber.onCompleted(); return; }
            Stage stage = card.getStage();
            // CardTemplate draws in fixed paper coordinates even when its WidgetGroup has no layout size.
            float width = card instanceof CardTemplate ? CardTemplate.CARD_WIDTH : card.getWidth();
            float height = card instanceof CardTemplate ? CardTemplate.CARD_HEIGHT : card.getHeight();
            if (width <= 0 || height <= 0) { card.remove(); subscriber.onCompleted(); return; }
            Vector2 center = card.localToStageCoordinates(new Vector2(width / 2, height / 2));
            Vector2 origin = card.localToStageCoordinates(new Vector2());
            Vector2 right = card.localToStageCoordinates(new Vector2(width, 0)).sub(origin);
            Vector2 up = card.localToStageCoordinates(new Vector2(0, height)).sub(origin);
            CardSnapshot snapshot = new CardSnapshot(card, width, height);
            CardDestructionEffect effect;
            try {
                effect = new CardDestructionEffect(snapshot, center, Math.max(1f, right.len()), Math.max(1f, up.len()), right.angle(), mystery);
            } catch (RuntimeException | Error failure) { snapshot.dispose(); throw failure; }
            card.clearActions(); card.setTouchable(Touchable.disabled); card.remove();
            stage.addActor(effect);
            effect.addAction(new FastForwardAction<>(new TemporalAction(effect.duration * NORMAL_ACTION_DURATION) {
                @Override protected void update(float percent) { effect.animate(percent * effect.duration); }
                @Override protected void end() {
                    effect.remove();
                    subscriber.onCompleted();
                }
            }));
        });
    }

    private CardDestructionEffect(CardSnapshot snapshot, Vector2 center, float width, float height,
                                  float rotation, boolean mystery) {
        this.snapshot = snapshot; this.mystery = mystery;
        chargeTime = mystery ? .55f : .26f;
        splitTime = mystery ? .24f : .18f;
        flightTime = mystery ? 1.15f : .90f;
        duration = chargeTime + splitTime + flightTime;
        accent = Color.valueOf(mystery ? "F5D98A" : "F3D6AC");
        setName(mystery ? "mystery-resolution" : "card-destruction");
        setTouchable(Touchable.disabled);
        setSize(width, height); setOrigin(width / 2, height / 2);
        startX = center.x - width / 2; startY = center.y - height / 2;
        startRotation = rotation > 180 ? rotation - 360 : rotation;
        targetScale = Math.min(mystery ? 1.06f : 1.035f,
                Math.min(VIEWPORT_WIDTH * .86f / width, VIEWPORT_HEIGHT * .86f / height));
        setPosition(startX, startY); setRotation(startRotation);
        geometry = new CardTearGeometry(width, height, mystery);
        halo = new Image(getTexture("dice_shadow.png"));
        halo.setBounds(-width * .22f, -height * .22f, width * 1.44f, height * 1.44f);
        halo.setColor(accent.r, accent.g, accent.b, 0);
        addActor(halo);
        whole = new PaperPiece(new float[]{0, 0, width, 0, width, height, 0, height});
        whole.setName("intact-card"); addActor(whole);
        pieces = new PaperPiece[geometry.pieces.length];
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = new PaperPiece(geometry.pieces[i]);
            pieces[i].setName("paper-fragment-" + i);
            pieces[i].setVisible(false); addActor(pieces[i]);
        }
        addActor(new TearLight());
    }

    private void animate(float time) {
        charge = MathUtils.clamp(time / chargeTime, 0, 1);
        float lift = Interpolation.sineOut.apply(charge);
        setPosition(MathUtils.lerp(startX, (VIEWPORT_WIDTH - getWidth()) / 2, lift),
                MathUtils.lerp(startY, (VIEWPORT_HEIGHT - getHeight()) / 2, lift));
        setScale(MathUtils.lerp(1f, targetScale, lift));
        setRotation(MathUtils.lerp(startRotation, 0, lift) + MathUtils.sin(charge * MathUtils.PI2) * (mystery ? .7f : 1.4f));
        halo.getColor().a = (mystery ? .24f : .10f) * lift;
        separation = MathUtils.clamp((time - chargeTime) / splitTime, 0, 1);
        flight = MathUtils.clamp((time - chargeTime - splitTime) / flightTime, 0, 1);
        if (time < chargeTime) return;
        whole.setVisible(false);
        float travel = Interpolation.pow2Out.apply(flight);
        halo.getColor().a *= Math.max(0, 1f - flight * 4f);
        for (int i = 0; i < pieces.length; i++) {
            PaperPiece piece = pieces[i];
            piece.setVisible(true);
            float dx = piece.getOriginX() - getWidth() / 2, dy = piece.getOriginY() - getHeight() / 2;
            float distance = Vector2.len(dx, dy);
            dx /= distance; dy /= distance;
            float spread = Interpolation.sineIn.apply(separation) * 9f
                    + travel * (mystery ? 150f : 105f) * (1f + (i % 3) * .12f);
            piece.setPosition(dx * spread, dy * spread - (mystery ? 25f : 100f) * flight * flight);
            float turn = (i % 2 == 0 ? -1 : 1) * (22f + (i % 3) * 9f);
            piece.setRotation(turn * travel + MathUtils.sin(flight * 11f + i) * flight * 5f);
            // Foreshortening makes each piece turn through the air instead of sliding flat across the screen.
            piece.setScale(1f - .32f * MathUtils.sin(flight * MathUtils.PI), 1f - .12f * travel);
            float fade = MathUtils.clamp((flight - .18f) / .82f, 0, 1);
            piece.getColor().a = 1f - Interpolation.pow2In.apply(fade);
        }
    }

    /** Triangle fans rendered through the existing SpriteBatch: all fragments share the same snapshot. */
    private final class PaperPiece extends Actor {
        private final float[] polygon;
        private final short[] indices;
        private final float[] vertices = new float[20];

        PaperPiece(float[] polygon) {
            this.polygon = polygon;
            indices = new EarClippingTriangulator().computeTriangles(polygon).toArray();
            Vector2 center = GeometryUtils.polygonCentroid(polygon, 0, polygon.length, new Vector2());
            setOrigin(center.x, center.y); setTouchable(Touchable.disabled);
        }

        @Override public void draw(Batch batch, float parentAlpha) {
            float alpha = getColor().a * parentAlpha;
            float packed = Color.toFloatBits(alpha, alpha, alpha, alpha);
            int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
            int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
            // Framebuffer colours are premultiplied. Fade RGB and alpha together to keep translucent edges clean.
            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
            float cos = MathUtils.cosDeg(getRotation()), sin = MathUtils.sinDeg(getRotation());
            for (int triangle = 0; triangle < indices.length; triangle += 3) {
                for (int corner = 0; corner < 4; corner++) {
                    int index = indices[triangle + Math.min(corner, 2)] * 2;
                    float x = polygon[index], y = polygon[index + 1];
                    float dx = (x - getOriginX()) * getScaleX(), dy = (y - getOriginY()) * getScaleY();
                    int v = corner * 5;
                    vertices[v] = getX() + getOriginX() + dx * cos - dy * sin;
                    vertices[v + 1] = getY() + getOriginY() + dx * sin + dy * cos;
                    vertices[v + 2] = packed;
                    vertices[v + 3] = x / CardDestructionEffect.this.getWidth();
                    vertices[v + 4] = y / CardDestructionEffect.this.getHeight();
                }
                batch.draw(snapshot.texture(), vertices, 0, vertices.length);
            }
            batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
        }
    }

    private final class TearLight extends Actor {
        private final Texture white = getTexture(PURE_WHITE_BACKGROUND);

        TearLight() { setTouchable(Touchable.disabled); }

        @Override public void draw(Batch batch, float parentAlpha) {
            float previous = batch.getPackedColor();
            float light = MathUtils.clamp((charge - .25f) / .75f, 0, 1) * Math.max(0, 1 - flight * 6f);
            for (float[] path : geometry.cracks) {
                float revealed = light * (path.length / 2 - 1);
                for (int j = 0; j < path.length - 2; j += 2) {
                    float fraction = MathUtils.clamp(revealed - j / 2f, 0, 1);
                    float x = path[j], y = path[j + 1];
                    float dx = (path[j + 2] - x) * fraction, dy = (path[j + 3] - y) * fraction;
                    line(batch, x, y, dx, dy, mystery ? 7f : 4f, light * .18f * parentAlpha);
                    line(batch, x, y, dx, dy, 1.3f, light * .95f * parentAlpha);
                }
            }
            if (flight > 0) {
                if (mystery) {
                    float radius = 35f + Interpolation.pow3Out.apply(flight)
                            * Math.max(CardDestructionEffect.this.getWidth(), CardDestructionEffect.this.getHeight()) * .65f;
                    for (int i = 0; i < 64; i++) {
                        float a = i * MathUtils.PI2 / 64, b = (i + 1) * MathUtils.PI2 / 64;
                        line(batch, CardDestructionEffect.this.getWidth() / 2 + MathUtils.cos(a) * radius,
                                CardDestructionEffect.this.getHeight() / 2 + MathUtils.sin(a) * radius * .8f,
                                (MathUtils.cos(b) - MathUtils.cos(a)) * radius,
                                (MathUtils.sin(b) - MathUtils.sin(a)) * radius * .8f, 2f,
                                Math.max(0, 1 - flight * 2.5f) * .5f * parentAlpha);
                    }
                }
                int count = mystery ? 32 : 20;
                for (int i = 0; i < count; i++) {
                    float angle = i * 137.5f;
                    float distance = 18f + (65f + (i % 5) * 19f) * Interpolation.pow2Out.apply(flight);
                    float x = CardDestructionEffect.this.getWidth() / 2 + MathUtils.cosDeg(angle) * distance;
                    float y = CardDestructionEffect.this.getHeight() / 2 + MathUtils.sinDeg(angle) * distance
                            + (mystery ? 50 : -65) * flight * flight;
                    line(batch, x, y, MathUtils.cosDeg(angle + flight * 160) * (3f + i % 4),
                            MathUtils.sinDeg(angle + flight * 160) * (3f + i % 4), 1.8f,
                            (1 - flight) * (1 - flight) * parentAlpha);
                }
            }
            batch.setColor(previous);
        }

        private void line(Batch batch, float x, float y, float dx, float dy, float thickness, float alpha) {
            batch.setColor(accent.r, accent.g, accent.b, alpha);
            batch.draw(white, x, y - thickness / 2, 0, thickness / 2, Vector2.len(dx, dy), thickness,
                    1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees,
                    0, 0, white.getWidth(), white.getHeight(), false, false);
        }
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) { clearActions(); snapshot.dispose(); }
    }
}
