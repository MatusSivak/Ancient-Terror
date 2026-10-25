package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.components.combat.SilhouetteImage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/** A single clock keeps the zoom, recoil and debris synchronized at any fast-forward speed. */
final class MonsterDefeatAnimation extends Group {
    private static final float ZOOM_TIME = 0.72f;
    private static final float HOLD_TIME = 0.26f;
    private static final float BURST_TIME = 0.90f;
    private static final float TOTAL_TIME = ZOOM_TIME + HOLD_TIME + BURST_TIME;
    private static final float[][] DIRECTIONS = {{-0.87f, 0.5f, -32f}, {0.87f, 0.5f, 38f}, {0f, -1f, -18f}};

    private final Actor monster;
    private final Image flash;
    private final float startX, startY, startScaleX, startScaleY, startAlpha, startRotation;
    private final float targetX, targetY, targetScale, extent;
    private TornApartMonsterImageBuilder.Fragments fragments;
    private final Image[] pieces = new Image[3];
    private final Image[] sparks = new Image[12];

    static void start(Actor monster, CompletableSubscriber subscriber) {
        Stage stage = monster.getStage();
        Vector2 center = monster.localToStageCoordinates(new Vector2(monster.getWidth() / 2, monster.getHeight() / 2));
        monster.clearActions();
        monster.clearListeners();
        monster.setTouchable(Touchable.disabled);
        monster.setOrigin(monster.getWidth() / 2, monster.getHeight() / 2);
        monster.setPosition(center.x - monster.getWidth() / 2, center.y - monster.getHeight() / 2);
        if (monster instanceof MonsterImage) {
            ((MonsterImage) monster).hideShadow();
            ((MonsterImage) monster).removeReckoningImage();
        } else if (monster instanceof EpicMonsterImage) {
            ((EpicMonsterImage) monster).hideShadow();
            ((EpicMonsterImage) monster).removeReckoningImage();
        }
        MonsterDefeatAnimation animation = new MonsterDefeatAnimation(monster);
        // The small-actor layer fades during other UI transitions; defeat owns its visibility.
        stage.addActor(animation);
        animation.addAction(new FastForwardAction<>(new TemporalAction(TOTAL_TIME * ViewProperties.NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) {
                animation.animate(percent * TOTAL_TIME);
            }
            @Override protected void end() {
                animation.remove();
                subscriber.onCompleted();
            }
        }));
    }

    private MonsterDefeatAnimation(Actor monster) {
        this.monster = monster;
        setName("monster-defeat");
        setTransform(false);
        setTouchable(Touchable.disabled);
        startX = monster.getX();
        startY = monster.getY();
        startScaleX = monster.getScaleX();
        startScaleY = monster.getScaleY();
        startAlpha = monster.getColor().a;
        startRotation = monster.getRotation();
        targetX = ViewProperties.VIEWPORT_WIDTH / 2f - monster.getWidth() / 2;
        targetY = ViewProperties.VIEWPORT_HEIGHT / 2f - monster.getHeight() / 2;
        targetScale = MonsterImageEffect.defeatScale(monster.getWidth(), monster.getHeight());
        extent = Math.max(monster.getWidth(), monster.getHeight()) * targetScale;
        addActor(monster);
        flash = monster instanceof Image ? new SilhouetteImage(((Image) monster).getDrawable()) : null;
        if (flash != null) {
            flash.setName("defeat-flash");
            flash.setBounds(targetX, targetY, monster.getWidth(), monster.getHeight());
            flash.setOrigin(monster.getOriginX(), monster.getOriginY());
            flash.setColor(1f, 0.78f, 0.48f, 0f);
            addActor(flash);
        }
    }

    private void animate(float elapsed) {
        if (elapsed < ZOOM_TIME) {
            float progress = Interpolation.sineOut.apply(elapsed / ZOOM_TIME);
            monster.setPosition(MathUtils.lerp(startX, targetX, progress), MathUtils.lerp(startY, targetY, progress));
            monster.setScale(MathUtils.lerp(startScaleX, targetScale, progress),
                    MathUtils.lerp(startScaleY, targetScale, progress));
            monster.setRotation(MathUtils.lerp(startRotation, 0f, progress) - 8f * MathUtils.sin(progress * MathUtils.PI));
            monster.getColor().a = MathUtils.lerp(startAlpha, 1f, Math.min(elapsed / 0.22f, 1f));
        } else if (elapsed < ZOOM_TIME + HOLD_TIME) {
            float progress = (elapsed - ZOOM_TIME) / HOLD_TIME;
            float pulse = MathUtils.sin(progress * MathUtils.PI);
            monster.setPosition(targetX, targetY);
            monster.setScale(targetScale * (1f + 0.035f * pulse));
            monster.setRotation(2f * MathUtils.sin(progress * MathUtils.PI * 4f) * pulse);
            monster.getColor().a = 1f;
            if (flash != null) {
                flash.setScale(monster.getScaleX());
                flash.setRotation(monster.getRotation());
                flash.getColor().a = 0.72f * progress;
            }
        } else {
            if (fragments == null) split();
            float progress = Math.min((elapsed - ZOOM_TIME - HOLD_TIME) / BURST_TIME, 1f);
            float travel = Interpolation.pow3Out.apply(progress);
            // Preserve readable pieces briefly, then fade instead of immediately shrinking to zero.
            float opacity = 1f - Interpolation.pow2In.apply(MathUtils.clamp((progress - 0.12f) / 0.88f, 0f, 1f));
            float paddedWidth = monster.getWidth() * TornApartMonsterImageBuilder.PADDED_SIZE;
            float paddedHeight = monster.getHeight() * TornApartMonsterImageBuilder.PADDED_SIZE;
            for (int i = 0; i < pieces.length; i++) {
                pieces[i].setPosition(ViewProperties.VIEWPORT_WIDTH / 2f - paddedWidth / 2 + DIRECTIONS[i][0] * extent * 0.52f * travel,
                        ViewProperties.VIEWPORT_HEIGHT / 2f - paddedHeight / 2 + DIRECTIONS[i][1] * extent * 0.48f * travel
                                - extent * 0.12f * progress * progress);
                pieces[i].setScale(targetScale * (1f - 0.18f * progress));
                pieces[i].setRotation(DIRECTIONS[i][2] * travel);
                pieces[i].getColor().a = opacity;
            }
            if (flash != null) {
                flash.setRotation(0f);
                flash.setScale(targetScale * (1f + 0.18f * travel));
                flash.getColor().a = 0.72f * Math.max(0f, 1f - progress * 7f);
            }
            for (int i = 0; i < sparks.length; i++) {
                float angle = i * 30f + 12f;
                float radius = extent * (0.08f + (0.48f + (i % 3) * 0.05f) * travel);
                sparks[i].setPosition(ViewProperties.VIEWPORT_WIDTH / 2f + MathUtils.cosDeg(angle) * radius,
                        ViewProperties.VIEWPORT_HEIGHT / 2f + MathUtils.sinDeg(angle) * radius - 25f * progress * progress);
                sparks[i].setScale(1f - 0.65f * progress);
                sparks[i].getColor().a = (1f - progress) * (1f - progress);
            }
        }
    }

    private void split() {
        monster.setPosition(targetX, targetY);
        monster.setScale(targetScale);
        monster.setRotation(0f);
        monster.getColor().a = 1f;
        fragments = TornApartMonsterImageBuilder.build(monster);
        monster.setVisible(false);
        for (int i = 0; i < pieces.length; i++) {
            Image piece = new Image(fragments.get(i));
            piece.setName("defeat-fragment-" + i);
            piece.setSize(monster.getWidth() * TornApartMonsterImageBuilder.PADDED_SIZE,
                    monster.getHeight() * TornApartMonsterImageBuilder.PADDED_SIZE);
            piece.setOrigin(piece.getWidth() / 2, piece.getHeight() / 2);
            addActor(piece);
            pieces[i] = piece;
        }
        for (int i = 0; i < sparks.length; i++) {
            Image spark = new Image(getTexture(PURE_WHITE_BACKGROUND));
            spark.setSize(7f + (i % 3) * 3f, 2f);
            spark.setOrigin(spark.getWidth() / 2f, 1f);
            spark.setRotation(i * 30f + 12f);
            spark.setColor(1f, 0.62f + (i % 3) * 0.1f, 0.25f, 1f);
            addActor(spark);
            sparks[i] = spark;
        }
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) {
            // Also release GPU snapshots when the screen closes partway through the burst.
            clearActions();
            if (fragments != null) fragments.dispose();
        }
    }
}
