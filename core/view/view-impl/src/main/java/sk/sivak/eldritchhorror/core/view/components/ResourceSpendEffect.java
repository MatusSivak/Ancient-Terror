package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.animation.BurstLight;
import sk.sivak.eldritchhorror.core.view.animation.ShatterGeometry;
import sk.sivak.eldritchhorror.core.view.animation.TextureShard;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Tokens burst into coloured debris; tickets gently tear into two pieces. */
public final class ResourceSpendEffect extends Group {
    public enum Kind { CLUE, FOCUS, TRAIN, SHIP }
    private static final float LIFT = .48f, CHARGE = .18f, FLIGHT = .78f, DURATION = LIFT + CHARGE + FLIGHT;
    private final Image whole, halo;
    private final Actor[] pieces;
    private final BurstLight burst;
    private final boolean ticket;
    private final float startX, startY, startScaleX, startScaleY;

    public static Completable play(Stage stage, Vector2 position, float width, float height,
                                   Kind kind, Action0 onLeave, Action0 onBreak) {
        return Completable.create(subscriber -> {
            ResourceSpendEffect effect = new ResourceSpendEffect(position, width, height, kind);
            stage.addActor(effect);
            if (onLeave != null) onLeave.call();
            effect.addAction(new FastForwardAction<>(new TemporalAction(DURATION * NORMAL_ACTION_DURATION) {
                private boolean broken;
                @Override protected void update(float percent) {
                    float time = percent * DURATION;
                    effect.animate(time);
                    if (!broken && time >= LIFT + CHARGE) {
                        broken = true;
                        if (onBreak != null) onBreak.call();
                    }
                }
                @Override protected void end() { effect.remove(); subscriber.onCompleted(); }
            }));
        });
    }

    private ResourceSpendEffect(Vector2 position, float width, float height, Kind kind) {
        ticket = kind == Kind.TRAIN || kind == Kind.SHIP;
        Texture texture = getTexture(kind == Kind.CLUE ? CLUE_TOKEN : kind == Kind.FOCUS ? FOCUS_TOKEN
                : kind == Kind.TRAIN ? TICKET_TRAIN_DOWN : TICKET_SHIP_DOWN);
        Color accent = Color.valueOf(kind == Kind.CLUE ? "8EECC5" : kind == Kind.FOCUS ? "B9A1FF"
                : kind == Kind.TRAIN ? "FFD28C" : "8FD9FF");
        float targetHeight = ticket ? 152 : 108;
        setSize(targetHeight * texture.getWidth() / texture.getHeight(), targetHeight);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setName("resource-spend-" + kind.name().toLowerCase(java.util.Locale.ROOT));
        setTouchable(Touchable.disabled);
        startX = position.x + width / 2 - getOriginX(); startY = position.y + height / 2 - getOriginY();
        startScaleX = width / getWidth(); startScaleY = height / getHeight();
        halo = new Image(getTexture("dice_shadow.png"));
        halo.setBounds(-getWidth() * .4f, -getHeight() * .5f, getWidth() * 1.8f, getHeight() * 2);
        halo.setColor(accent.r, accent.g, accent.b, 0); addActor(halo);
        whole = new Image(texture); whole.setSize(getWidth(), getHeight()); addActor(whole);
        if (ticket) {
            pieces = new Actor[]{
                    new Image(getTexture(kind == Kind.TRAIN ? TICKET_TRAIN_LEFT : TICKET_SHIP_LEFT)),
                    new Image(getTexture(kind == Kind.TRAIN ? TICKET_TRAIN_RIGHT : TICKET_SHIP_RIGHT))};
            for (int i = 0; i < pieces.length; i++) {
                pieces[i].setSize(getWidth(), getHeight());
                pieces[i].setOrigin(getWidth() * (i == 0 ? .4f : .85f), getHeight() / 2);
            }
            halo.setVisible(false);
            burst = null;
        } else {
            float[][] triangles = ShatterGeometry.triangles(3, 3);
            pieces = new Actor[triangles.length];
            for (int i = 0; i < pieces.length; i++) {
                pieces[i] = new TextureShard(texture, getWidth(), getHeight(), triangles[i], false);
            }
            burst = new BurstLight(accent, Math.max(getWidth(), getHeight()) * 1.35f);
            burst.setPosition(getOriginX(), getOriginY());
        }
        for (int i = 0; i < pieces.length; i++) {
            pieces[i].setName("resource-fragment-" + i); pieces[i].setVisible(false); addActor(pieces[i]);
        }
        if (burst != null) addActor(burst);
        animate(0);
    }

    private void animate(float time) {
        float lift = MathUtils.clamp(time / LIFT, 0, 1), travel = Interpolation.pow3Out.apply(lift);
        setPosition(MathUtils.lerp(startX, VIEWPORT_WIDTH / 2f - getOriginX(), travel),
                MathUtils.lerp(startY, VIEWPORT_HEIGHT * .54f - getOriginY(), travel) + MathUtils.sin(lift * MathUtils.PI) * 32);
        float charge = MathUtils.clamp((time - LIFT) / CHARGE, 0, 1);
        float swell = ticket ? 1 : 1 + .09f * charge;
        setScale(MathUtils.lerp(startScaleX, 1, travel) * swell,
                MathUtils.lerp(startScaleY, 1, travel) * swell);
        setRotation(MathUtils.sin(lift * MathUtils.PI) * (ticket ? -8 : 22)
                + (ticket ? 0 : MathUtils.sin(charge * MathUtils.PI * 4) * 2 * charge));
        halo.getColor().a = .12f * travel + .35f * charge;
        if (time < LIFT + CHARGE) return;
        whole.setVisible(false);
        float flight = MathUtils.clamp((time - LIFT - CHARGE) / FLIGHT, 0, 1);
        if (ticket) {
            float pull = Interpolation.sine.apply(Math.min(flight / .65f, 1));
            float fall = MathUtils.clamp((flight - .3f) / .7f, 0, 1);
            for (int i = 0; i < pieces.length; i++) {
                Actor piece = pieces[i]; piece.setVisible(true);
                float side = i == 0 ? -1 : 1;
                piece.setPosition(side * 52 * pull, side * 14 * pull - 84 * fall * fall);
                piece.setRotation(-side * 12 * pull);
                piece.getColor().a = 1 - Interpolation.pow2In.apply(MathUtils.clamp((flight - .45f) / .55f, 0, 1));
            }
            return;
        }
        float spread = Interpolation.pow3Out.apply(flight);
        halo.getColor().a *= Math.max(0, 1 - flight * 6);
        burst.progress(flight);
        for (int i = 0; i < pieces.length; i++) {
            Actor piece = pieces[i]; piece.setVisible(true);
            float dx = piece.getOriginX() - getOriginX(), dy = piece.getOriginY() - getOriginY();
            float length = Math.max(1, Vector2.len(dx, dy));
            float distance = 78 + (i % 4) * 13;
            piece.setPosition(dx / length * distance * spread, dy / length * distance * spread
                    + 24 * flight * flight);
            piece.setRotation((i % 2 == 0 ? -1 : 1) * (55 + (i % 5) * 24) * spread);
            piece.setScale(1 - .45f * flight, 1 - .25f * flight);
            piece.getColor().a = 1 - Interpolation.pow2In.apply(MathUtils.clamp((flight - .12f) / .88f, 0, 1));
        }
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) clearActions();
    }
}
