package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Subscription;
import rx.functions.Func0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.assetmanager.MonsterArtwork;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterFacing;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.animation.BurstLight;
import sk.sivak.eldritchhorror.core.view.animation.BreakApartAnimation;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterUtils;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.ToughnessBar;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.SelectionPanelStyle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * Combat state of the fought monster. There is no panel any more: the actor itself is invisible and only keeps the
 * combat API and timing. What the player sees is the roaming monster portrait with a status plate (name, horror,
 * damage and health) floating above it. Bullets from the investigator hit the monster or intercept fireballs aimed at the
 * investigator. Anything related to horror / sanity burns blue.
 */
public class MonsterCombatTable extends VisTable {

    public static final int X_POSITION = 405;
    private static final float SECTION_MAX_WIDTH = 126f;
    private static final float TOKEN_SLOT = 24f;
    private static final float ROW_HEIGHT = 28f;
    /** Keeps clear of the menu / fast-forward buttons on the right edge. */
    public static final float SAFE_RIGHT = 895f;
    private static final float SAFE_TOP = 532f;
    private static final float IMAGE_SIZE = 200f;
    /** Epic monsters loom larger, glow red and throw bigger fireballs. */
    private static final float EPIC_IMAGE_SIZE = 260f;
    private static final float EPIC_GLOW_SCALE = 1.06f;
    private static final float EPIC_FIREBALL_SCALE = 1.5f;
    /** Seconds between the monster's attack fireballs. */
    private static final float ATTACK_STAGGER = 0.55f;
    /** Random gap in seconds between investigator shots. */
    private static final float DICE_SHOT_GAP_MIN = 0.15f;
    private static final float DICE_SHOT_GAP_MAX = 0.30f;
    /** Set when an intercepted volley ends with fireballs left over, so these follow immediately. */
    private boolean continuesVolley;
    private static final Color EPIC_GOLD = Color.valueOf("F2D27A");
    private static final Color EPIC_GLOW = Color.valueOf("FF3030");
    private static final float MIN_STEP = 90f;
    /** Bottom-left corner around the investigator's portrait; the monster keeps its distance. */
    public static final Rectangle INVESTIGATOR_KEEP_OUT = new Rectangle(0f, 0f, 340f, 230f);
    private static final float FADE_DURATION = 0.6f;
    /** How long {@link #remove()} takes to fade the monster away. */
    public static final float HIDE_DURATION = FADE_DURATION / 2f;
    private static final Color BRASS = Color.valueOf("DCC99F");
    private Table horrorRow;
    private Table damageRow;
    /** Name, horror, damage and health, floating above the roaming portrait. */
    private Table statusPlate;
    /** Area the portrait roams in: clear of the side menu, the right-hand buttons and the screen edges. */
    private final Rectangle wanderArea = new Rectangle();
    /** Set while fireballs fly to or from the monster, so they start / land where they were aimed. */
    private boolean holdStill;
    private float toughnessBarScale;
    private float hitShake;
    private final Vector2 recoilDirection = new Vector2();
    private boolean shattered;
    private float presence;
    private boolean interrupted;
    /** Tokens jitter around their slot on the plate; the anchor is the slot, so they never drift away. */
    private final Map<Image, Float> tokenShakeTime = new HashMap<>();
    private final Map<Image, Vector2> tokenAnchors = new HashMap<>();
    private static final float TOKEN_SHAKE_DURATION = 0.25f;
    private static final float TOKEN_SHAKE_INTENSITY = 3f;
    private TokenInFrameBar horrorBar;
    private TokenInFrameBar damageBar;
    private ToughnessBar toughnessBar;
    private MonsterCombatTableData data;

    private Integer remainingDamage;

    private boolean centered = false;
    private Cell<TokenInFrameBar> damageBarCell;
    private Cell<TokenInFrameBar> horrorBarCell;
    private Cell<Table> horrorRowCell;
    private FireballService fireballService;
    private Image monsterImage;
    private MonsterFacing facing;
    private Image groundShadow;
    private float imageSize = IMAGE_SIZE;
    /** Red rim behind epic monsters; null for ordinary ones. */
    private SilhouetteImage epicGlow;
    private float hoverTime;
    private final Vector2 wanderFrom = new Vector2();
    private final Vector2 wanderTo = new Vector2();
    private final Vector2 wanderPosition = new Vector2();
    private float wanderElapsed;
    private float wanderDuration;
    private float wanderPause;
    /** Bend of the current stroll; the monster walks a gentle arc instead of a ruler-straight line. */
    private final Vector2 wanderControl = new Vector2();
    private static final float STEP_LENGTH = 34f;
    private static final float MAX_STEPS_PER_SECOND = 3.5f;
    private static final float STEP_BOB = 6f;
    private static final float STEP_TILT = 3.5f;
    private static final float MAX_LEAN = 5f;
    /** Horizontal speed (px/s) at which the lean is strongest. */
    private static final float FULL_LEAN_SPEED = 110f;
    private static final float GAIT_BLEND_IN = 0.15f;
    private static final float GAIT_BLEND_OUT = 0.35f;
    private float stepPhase;
    private float walkBlend;
    private float walkLean;
    private float idleBlend = 1f;
    private float attackTime;
    private static final float ATTACK_PULSE_DURATION = 0.28f;
    private static final float FAR_SCALE = 0.72f;
    private static final float NEAR_SCALE = 1.12f;
    private final Vector2 artLeft = new Vector2();
    private final Vector2 artRight = new Vector2();

    public boolean isCentered() {
        return centered;
    }

    public void init(MonsterCombatTableData data) {
        clear();
        this.data = data;
        setTouchable(Touchable.disabled);
        setSize(0, 0);

        if (fireballService != null) {
            fireballService.dispose();
        }
        fireballService = new FireballService(getStage());

        if (monsterImage != null) {
            monsterImage.remove();
        }
        if (groundShadow != null) {
            groundShadow.remove();
        }
        if (statusPlate != null) {
            statusPlate.remove();
        }
        if (epicGlow != null) {
            epicGlow.remove();
            epicGlow = null;
        }
        imageSize = data.isMonsterEpic() ? EPIC_IMAGE_SIZE : IMAGE_SIZE;
        statusPlate = createStatusPlate(data);
        ButtonUtils.addClickListener(statusPlate, () -> displayMonsterCard(data));
        shattered = false;
        holdStill = false;
        stepPhase = 0f;
        walkBlend = 0f;
        walkLean = 0f;
        idleBlend = 1f;
        presence = 0f;
        attackTime = 0f;
        hitShake = 0f;
        monsterImage = createMonsterImage(data);
        facing = new MonsterFacing(MonsterArtwork.facesRight(data.getMonsterClassName(), data.isMonsterEpic()));
        // The investigator's portrait occupies the lower-left corner of combat.
        facing.face(false, false);
        groundShadow = new Image(CustomAssetManager.getTexture("dice_shadow.png"));
        groundShadow.setName("combat-ground-shadow");
        groundShadow.setTouchable(Touchable.disabled);
        groundShadow.setColor(0.025f, 0.015f, 0.035f, 0f);
        // Panels, cards and dice are layered above the creature layer, so they still get their touches first.
        monsterImage.setTouchable(Touchable.enabled);
        ButtonUtils.addClickListener(monsterImage, () -> displayMonsterCard(data));
        if (data.isMonsterEpic()) {
            epicGlow = new SilhouetteImage(monsterImage.getDrawable());
            epicGlow.setScaling(Scaling.fit);
            epicGlow.setSize(imageSize * EPIC_GLOW_SCALE, imageSize * EPIC_GLOW_SCALE);
            epicGlow.setOrigin(Align.center);
            epicGlow.setTouchable(Touchable.disabled);
            epicGlow.setColor(EPIC_GLOW);
        }
        attachMonsterImage();

        float margin = motionMargin();
        wanderArea.set(150f + margin, 60f, SAFE_RIGHT - imageSize - 150f - 2f * margin,
                Math.max(1f, SAFE_TOP - imageSize - statusPlate.getHeight() - 60f - margin - STEP_BOB));
        // Emerge at the top centre of the screen, then roam.
        wanderPosition.set(
                MathUtils.clamp(Math.max(INVESTIGATOR_KEEP_OUT.width + margin + 5f,
                        ViewProperties.VIEWPORT_WIDTH / 2f - imageSize / 2f), wanderArea.x, wanderArea.x + wanderArea.width),
                wanderArea.y + wanderArea.height);
        monsterImage.setPosition(wanderPosition.x, wanderPosition.y);
        wanderPause = MathUtils.random(0.5f, 1.2f);
        wanderDuration = 0f;
        act(0f);
    }

    /** Portrait and status plate live on the creature layer, behind panels, cards and dice. */
    private void attachMonsterImage() {
        if (getStage() != null && groundShadow != null && groundShadow.getParent() == null) {
            InfoStage.getCreatureLayer().addActorAt(0, groundShadow);
        }
        // The glow goes in first, so it is drawn behind the portrait.
        if (getStage() != null && epicGlow != null && epicGlow.getParent() == null) {
            if (monsterImage.getParent() != null) {
                monsterImage.getParent().addActorBefore(monsterImage, epicGlow);
            } else {
                InfoStage.getCreatureLayer().addActor(epicGlow);
            }
        }
        if (getStage() != null && monsterImage.getParent() == null) {
            InfoStage.getCreatureLayer().addActor(monsterImage);
        }
        if (getStage() != null && statusPlate.getParent() == null) {
            InfoStage.getCreatureLayer().addActor(statusPlate);
        }
    }

    public Image getMonsterImage() {
        return monsterImage;
    }

    public Table getStatusPlate() {
        return statusPlate;
    }

    public Image getEpicGlow() {
        return epicGlow;
    }

    public Rectangle getWanderArea() {
        return wanderArea;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (monsterImage == null || shattered) {
            return;
        }
        attachMonsterImage();
        hoverTime += delta;
        presence = interrupted
                ? Math.max(0f, presence - delta / FADE_DURATION)
                : Math.min(1f, presence + delta / FADE_DURATION);
        boolean shown = presence > 0f;
        monsterImage.setVisible(shown);
        groundShadow.setVisible(shown);
        statusPlate.setVisible(shown);
        if (epicGlow != null) {
            epicGlow.setVisible(shown);
        }
        float previousX = wanderPosition.x;
        float previousY = wanderPosition.y;
        boolean walking = !holdStill && updateWander(delta);
        updateGait(delta, walking, wanderPosition.x - previousX, wanderPosition.y - previousY);
        if (walking) facing.followMovement(wanderPosition.x - previousX);
        else facing.face(false, true);
        facing.update(delta * (FastForwardAction.isOn() ? 20f : 1f));
        // Waddle with a stepping bounce while walking; breathe gently while standing; settle down while fighting.
        // Gaits cross-fade, so starting and stopping never snaps.
        float breathing = (1f - walkBlend) * idleBlend;
        float bob = Math.abs(MathUtils.sin(stepPhase)) * STEP_BOB * walkBlend
                + MathUtils.sin(hoverTime * 2f) * 2f * breathing;
        float tilt = (MathUtils.sin(stepPhase) * STEP_TILT + walkLean) * walkBlend
                + MathUtils.sin(hoverTime * 1.3f) * 1.5f * breathing;
        float shake = 0f;
        float kick = 0f;
        if (hitShake > 0f) {
            hitShake = Math.max(0f, hitShake - delta * (FastForwardAction.isOn() ? 20f : 1f));
            float remaining = hitShake / 0.3f;
            kick = 16f * remaining * remaining;
            shake = MathUtils.sin(hoverTime * 70f) * 4f * remaining;
            tilt -= recoilDirection.x * 8f * remaining;
        }
        monsterImage.setPosition(wanderPosition.x + shake + recoilDirection.x * kick, wanderPosition.y + bob + recoilDirection.y * kick);
        monsterImage.setRotation(tilt);
        // Higher on the floor is farther away. Foreshorten the portrait slightly as it turns into a stroll.
        float depth = 1f - MathUtils.clamp((wanderPosition.y - wanderArea.y) / wanderArea.height, 0f, 1f);
        float perspective = MathUtils.lerp(FAR_SCALE, NEAR_SCALE, Interpolation.sine.apply(depth));
        attackTime = Math.max(0f, attackTime - delta * (FastForwardAction.isOn() ? 20f : 1f));
        float attackPulse = attackTime > 0f ? 0.12f * MathUtils.sin(MathUtils.PI * attackTime / ATTACK_PULSE_DURATION) : 0f;
        float scale = perspective * (1f + attackPulse + 0.012f * MathUtils.sin(hoverTime * 2f) * breathing);
        monsterImage.setScale(scale * (1f - 0.12f * Math.abs(walkLean) / MAX_LEAN * walkBlend)
                * facing.scaleX(), scale);
        monsterImage.getColor().a = presence * getColor().a;
        if (epicGlow != null) {
            float glowOffset = imageSize * (EPIC_GLOW_SCALE - 1f) / 2f;
            epicGlow.setPosition(monsterImage.getX() - glowOffset, monsterImage.getY() - glowOffset);
            epicGlow.setRotation(tilt);
            epicGlow.setScale(monsterImage.getScaleX(), monsterImage.getScaleY());
            epicGlow.getColor().a = presence * getColor().a * (0.7f + 0.3f * MathUtils.sin(hoverTime * 3f));
        }

        // The plate rides just above the visible part of the (fit-scaled) artwork; it does not tilt with it.
        monsterImage.validate();
        float left = monsterImage.getImageX(), bottom = monsterImage.getImageY();
        float right = left + monsterImage.getImageWidth(), top = bottom + monsterImage.getImageHeight();
        monsterImage.localToParentCoordinates(artLeft.set(left, top));
        monsterImage.localToParentCoordinates(artRight.set(right, top));
        float artTop = Math.max(artLeft.y, artRight.y);
        statusPlate.setPosition(
                Math.round(wanderPosition.x + imageSize / 2f - statusPlate.getWidth() / 2f),
                Math.round(Math.min(artTop + 2f, SAFE_TOP + 6f - statusPlate.getHeight())));
        statusPlate.getColor().a = presence * getColor().a;
        monsterImage.localToParentCoordinates(artLeft.set((left + right) / 2f, bottom));
        float shadowWidth = monsterImage.getImageWidth() * perspective * 0.85f;
        float shadowHeight = imageSize * perspective * 0.16f;
        groundShadow.setBounds(artLeft.x - shadowWidth / 2f, artLeft.y - bob - shadowHeight / 2f,
                shadowWidth, shadowHeight);
        groundShadow.getColor().a = presence * getColor().a * (0.28f + depth * 0.22f);
        updateTokenShakes(delta);
    }

    private void shakeToken(Image token) {
        if (!tokenAnchors.containsKey(token)) {
            tokenAnchors.put(token, new Vector2(token.getX(), token.getY()));
        }
        tokenShakeTime.put(token, TOKEN_SHAKE_DURATION);
    }

    private void updateTokenShakes(float delta) {
        if (tokenShakeTime.isEmpty()) {
            return;
        }
        float step = delta * (FastForwardAction.isOn() ? 20f : 1f);
        for (Image token : new LinkedList<>(tokenShakeTime.keySet())) {
            float left = tokenShakeTime.get(token) - step;
            Vector2 anchor = tokenAnchors.get(token);
            if (left <= 0f) {
                token.setPosition(anchor.x, anchor.y);
                tokenShakeTime.remove(token);
                tokenAnchors.remove(token);
            } else {
                float strength = TOKEN_SHAKE_INTENSITY * (0.4f + 0.6f * left / TOKEN_SHAKE_DURATION);
                token.setPosition(anchor.x + MathUtils.random(-strength, strength), anchor.y + MathUtils.random(-strength, strength));
                tokenShakeTime.put(token, left);
            }
        }
    }

    private void settleTokenShakes() {
        for (Map.Entry<Image, Vector2> entry : tokenAnchors.entrySet()) {
            entry.getKey().setPosition(entry.getValue().x, entry.getValue().y);
        }
        tokenShakeTime.clear();
        tokenAnchors.clear();
    }

    /** Point fireballs aim at / start from: the middle of the drawn artwork. */
    private Vector2 getMonsterBodyCenter() {
        monsterImage.validate();
        return monsterImage.localToStageCoordinates(new Vector2(
                monsterImage.getImageX() + monsterImage.getImageWidth() / 2f,
                monsterImage.getImageY() + monsterImage.getImageHeight() / 2f));
    }

    private Vector2 randomBodyPoint(Vector2 body) {
        float spread = Math.min(monsterImage.getImageWidth() * Math.abs(monsterImage.getScaleX()),
                monsterImage.getImageHeight() * Math.abs(monsterImage.getScaleY())) * 0.18f;
        return new Vector2(body.x + MathUtils.random(-spread, spread), body.y + MathUtils.random(-spread, spread));
    }

    private void playHitReaction(Vector2 investigator, Vector2 impact) {
        recoilDirection.set(impact).sub(investigator).nor();
        hitShake = 0.3f;
        monsterImage.clearActions();
        monsterImage.setColor(1f, 0.30f, 0.20f, 1f);
        monsterImage.addAction(new FastForwardAction<>(Actions.color(Color.WHITE, 0.32f)));
        BurstLight burst = new BurstLight(new Color(1f, 0.55f, 0.21f, 1f),
                data.isMonsterEpic() ? 138f : 104f);
        burst.setName("combat-hit-burst");
        burst.setPosition(impact.x, impact.y);
        getStage().addActor(burst);
        burst.progress(0f);
        burst.addAction(new FastForwardAction<>(Actions.sequence(new TemporalAction(0.32f) {
            @Override protected void update(float progress) { burst.progress(progress); }
        }, Actions.removeActor())));
    }

    private void playAttackReaction() {
        // A relative lunge preserves the current perspective instead of resetting the monster to full size.
        attackTime = ATTACK_PULSE_DURATION;
    }
    /** Steps follow the distance actually covered, so the feet never skate while the stroll speeds up or slows down. */
    private void updateGait(float delta, boolean walking, float movedX, float movedY) {
        if (delta <= 0f) {
            return;
        }
        float moved = (float) Math.sqrt(movedX * movedX + movedY * movedY);
        float targetLean = 0f;
        if (walking) {
            stepPhase += Math.min(moved / STEP_LENGTH, MAX_STEPS_PER_SECOND * delta) * MathUtils.PI;
            // Positive rotation is counter-clockwise, so walking right leans the top to the right.
            targetLean = -MathUtils.clamp(movedX / delta / FULL_LEAN_SPEED, -1f, 1f) * MAX_LEAN;
        }
        walkLean += (targetLean - walkLean) * Math.min(1f, delta * 5f);
        walkBlend = MathUtils.clamp(walkBlend + (walking ? delta / GAIT_BLEND_IN : -delta / GAIT_BLEND_OUT), 0f, 1f);
        idleBlend = MathUtils.clamp(idleBlend + (holdStill ? -delta : delta) / GAIT_BLEND_OUT, 0f, 1f);
        if (walkBlend == 0f) {
            stepPhase = 0f;
        }
    }

    /** Random walk: stroll to a random spot, linger a moment, pick the next one. Returns true while moving. */
    private boolean updateWander(float delta) {
        if (wanderPause > 0f) {
            wanderPause -= delta;
            if (wanderPause <= 0f) {
                pickWanderTarget();
            }
            return false;
        }
        if (wanderDuration <= 0f) {
            pickWanderTarget();
        }
        wanderElapsed += delta;
        float progress = Math.min(1f, wanderElapsed / wanderDuration);
        pointOnStroll(Interpolation.smooth.apply(progress), wanderPosition);
        if (progress >= 1f) {
            wanderDuration = 0f;
            wanderPause = MathUtils.random(0.8f, 2.5f);
            return false;
        }
        return true;
    }

    /** Quadratic Bézier from {@link #wanderFrom} through {@link #wanderControl} to {@link #wanderTo}. */
    private void pointOnStroll(float t, Vector2 out) {
        float u = 1f - t;
        out.set(
                u * u * wanderFrom.x + 2f * u * t * wanderControl.x + t * t * wanderTo.x,
                u * u * wanderFrom.y + 2f * u * t * wanderControl.y + t * t * wanderTo.y);
    }

    /** Pushes the arc's control point sideways; inside the wander area, so the whole arc stays inside it too. */
    private void bendStroll() {
        float length = wanderTo.dst(wanderFrom);
        float bend = MathUtils.random(-0.3f, 0.3f) * length;
        wanderControl.set(wanderFrom).lerp(wanderTo, 0.5f);
        if (length > 0f) {
            wanderControl.add(-(wanderTo.y - wanderFrom.y) / length * bend, (wanderTo.x - wanderFrom.x) / length * bend);
        }
        wanderControl.x = MathUtils.clamp(wanderControl.x, wanderArea.x, wanderArea.x + wanderArea.width);
        wanderControl.y = MathUtils.clamp(wanderControl.y, wanderArea.y, wanderArea.y + wanderArea.height);
    }

    /**
     * Prefers resting spots in open space, so the portrait only passes behind panels and cards on its way.
     * Never strolls near the investigator in the bottom-left corner.
     */
    private void pickWanderTarget() {
        wanderFrom.set(wanderPosition);
        Vector2 fallback = null;
        Vector2 fallbackControl = null;
        boolean found = false;
        for (int attempt = 0; attempt < 40; attempt++) {
            wanderTo.set(wanderArea.x + MathUtils.random(wanderArea.width), wanderArea.y + MathUtils.random(wanderArea.height));
            if (wanderTo.dst(wanderFrom) < MIN_STEP) {
                continue;
            }
            bendStroll();
            if (strollEntersKeepOut()) {
                // The arc may swing into the corner where the straight line would not.
                wanderControl.set(wanderFrom).lerp(wanderTo, 0.5f);
                if (strollEntersKeepOut()) {
                    continue;
                }
            }
            if (!coversOtherActor(wanderTo)) {
                found = true;
                break;
            }
            if (fallback == null) {
                fallback = new Vector2(wanderTo);
                fallbackControl = new Vector2(wanderControl);
            }
        }
        if (!found) {
            // No good spot: settle for one behind a panel, or stay put for a moment.
            wanderTo.set(fallback != null ? fallback : wanderFrom);
            wanderControl.set(fallbackControl != null ? fallbackControl : wanderFrom);
        }
        wanderElapsed = 0f;
        wanderDuration = Math.max(0.5f, wanderTo.dst(wanderFrom) / MathUtils.random(45f, 80f));
    }

    /** The monster's box anywhere along the current stroll touches the investigator's corner. */
    private boolean strollEntersKeepOut() {
        float margin = motionMargin();
        Rectangle box = new Rectangle(0, 0, imageSize + 2f * margin, imageSize + 2f * margin);
        Vector2 point = new Vector2();
        for (int i = 0; i <= 24; i++) {
            pointOnStroll(i / 24f, point);
            box.setPosition(point.x - margin, point.y - margin);
            if (box.overlaps(INVESTIGATOR_KEEP_OUT)) {
                return true;
            }
        }
        return false;
    }

    private boolean coversOtherActor(Vector2 position) {
        if (getParent() == null) {
            return false;
        }
        float margin = motionMargin();
        Rectangle spot = new Rectangle(position.x - margin, position.y - margin,
                imageSize + 2f * margin, imageSize + 2f * margin);
        Rectangle other = new Rectangle();
        for (Actor actor : getParent().getChildren()) {
            if (actor == monsterImage || !actor.isVisible() || actor.getWidth() <= 0 || actor.getHeight() <= 0) {
                continue;
            }
            if (spot.overlaps(other.set(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight()))) {
                return true;
            }
        }
        return false;
    }

    private float motionMargin() {
        // Includes the near-plane scale, turning, recoil and epic outline.
        return imageSize * 0.2f;
    }

    /** The monster fades out instead of vanishing; the invisible combat actor is removed right away. */
    @Override
    public boolean remove() {
        settleTokenShakes();
        if (fireballService != null) {
            fireballService.dispose();
        }
        if (!shattered && monsterImage != null && monsterImage.getParent() != null) {
            monsterImage.setTouchable(Touchable.disabled);
            monsterImage.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        if (epicGlow != null && epicGlow.getParent() != null) {
            epicGlow.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        if (groundShadow != null && groundShadow.getParent() != null) {
            groundShadow.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        if (statusPlate != null && statusPlate.getParent() != null) {
            statusPlate.setTouchable(Touchable.disabled);
            statusPlate.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        return super.remove();
    }

    private void displayMonsterCard(MonsterCombatTableData data) {
        BigActorsManager.voiceMonsterIfOpening(data.getMonsterInfo());
        BigActorsManager.initMonsterCard(data.getMonsterInfo(), BigActorsManager::displayOrHideMonsterCard, () -> {});
        BigActorsManager.displayOrHideMonsterCard();
    }

    private Image createMonsterImage(MonsterCombatTableData data) {
        Texture texture = data.isMonsterEpic()
                ? CustomAssetManager.getEpicMonsterTexture(data.getMonsterClassName())
                : CustomAssetManager.getNonEpicMonsterTexture(data.getMonsterClassName());
        Image image = new Image(texture) {
            /** Only the drawn artwork is tappable, not the empty margin the fit scaling leaves around it. */
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                if (touchable && getTouchable() != Touchable.enabled || !isVisible() || getColor().a < 0.5f) {
                    return null;
                }
                return x >= getImageX() && x < getImageX() + getImageWidth()
                        && y >= getImageY() && y < getImageY() + getImageHeight() ? this : null;
            }
        };
        image.setScaling(Scaling.fit);
        image.setSize(imageSize, imageSize);
        image.setOrigin(Align.center);
        return image;
    }

    public Completable destroySanity(List<Vector2> endPositions) {
        return destroySanity(endPositions, Completable::complete);
    }

    public Completable destroyHealth(List<Vector2> endPositions) {
        return destroyHealth(endPositions, Completable::complete);
    }

    /** @param onImpact played when each fireball lands - one lost token per impact. */
    public Completable destroySanity(List<Vector2> endPositions, Func0<Completable> onImpact) {
        return Completable.create(onSub -> addBoltsTargetingInvestigatorBar(horrorBar, endPositions, true, onImpact, onSub));
    }

    /** @param onImpact played when each fireball lands - one lost token per impact. */
    public Completable destroyHealth(List<Vector2> endPositions, Func0<Completable> onImpact) {
        return Completable.create(onSub -> addBoltsTargetingInvestigatorBar(damageBar, endPositions, false, onImpact, onSub));
    }

    /** The monster stops, its remaining tokens flare up in their slots on the plate and it hurls their fireballs at the investigator. */
    public void addBoltsTargetingInvestigatorBar(TokenInFrameBar sourceBar, List<Vector2> endPositions, boolean sanity,
                                                 Func0<Completable> onImpact, CompletableSubscriber onSub) {
        holdStill = true;
        float volleyDuration = Math.max(0, endPositions.size() - 1) * ATTACK_STAGGER;
        List<Image> tokens = sourceBar.getRemainingTokenImages();
        for (Image token : tokens) {
            float restingAlpha = token.getColor().a;
            token.setOrigin(Align.center);
            token.addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.parallel(Actions.alpha(1f, 0.4f), Actions.scaleTo(1.2f, 1.2f, 0.4f, Interpolation.sine)),
                    Actions.delay(0.2f),
                    // Settle once the volley has been thrown.
                    Actions.run(() -> token.addAction(new FastForwardAction<>(Actions.sequence(
                            Actions.delay(TOKEN_SHAKE_DURATION + volleyDuration + 0.3f),
                            Actions.parallel(Actions.alpha(restingAlpha, 0.5f), Actions.scaleTo(1f, 1f, 0.5f, Interpolation.sine))))))
            )));
        }
        if (tokens.isEmpty() || endPositions.isEmpty()) {
            holdStill = false;
            continuesVolley = false;
            onSub.onCompleted();
            return;
        }
        // Right after an intercepted volley, the surviving fireballs keep the same rhythm.
        float windUp = continuesVolley ? 0.1f : 0.6f;
        continuesVolley = false;
        addAction(new FastForwardAction<>(Actions.sequence(
                Actions.delay(windUp),
                Actions.run(() -> {
                    fireballService.reset();
                    // Exactly one fireball per point lost.
                    fireballService.setTargetHealth(1);
                    fireballService.setBlue(sanity);
                    fireballService.setFireballScale(data.isMonsterEpic() ? EPIC_FIREBALL_SCALE : 1f);
                    // One fireball after another, so every impact costs its own token.
                    fireballService.setStaggerDelay(ATTACK_STAGGER);
                    Vector2 body = getMonsterBodyCenter();
                    List<Vector2> sources = new LinkedList<>();
                    Set<Vector2> landedFireballs = new HashSet<>();
                    int[] runningImpacts = {0};
                    Runnable completeWhenDone = () -> {
                        if (landedFireballs.size() == endPositions.size() && runningImpacts[0] == 0) {
                            holdStill = false;
                            onSub.onCompleted();
                        }
                    };
                    for (int i = 0; i < endPositions.size(); i++) {
                        Image token = tokens.get(i % tokens.size());
                        // Distinct start points: the service keys its callbacks by source position.
                        Vector2 source = new Vector2(body.x + (i - (endPositions.size() - 1) / 2f) * 22f, body.y + 10f);
                        sources.add(source);
                        fireballService.setOnThrowAction(source, () -> {
                            playAttackReaction();
                            shakeToken(token);
                        });
                    }
                    for (Vector2 target : endPositions) {
                        fireballService.setOnLandAction(target, () -> {
                            // Each target is hit once; ignore any repeated landing.
                            if (landedFireballs.add(target)) {
                                runningImpacts[0]++;
                                onImpact.call().subscribe(() -> {
                                    runningImpacts[0]--;
                                    completeWhenDone.run();
                                });
                            }
                        });
                    }
                    fireballService.setMidpointDisplacementDirection(1);
                    fireballService.launchFireballs(sources, endPositions);
                })
        )));
    }

    /** No panel to slide aside any more; kept for the combat flow, completes immediately. */
    public Completable moveRight() {
        return Completable.complete();
    }

    public Completable dockTo(float x) {
        return Completable.complete();
    }

    public Completable moveLeft() {
        centered = true;
        return Completable.complete();
    }

    public Completable destroyHorror(int successes, Vector2 investigator, Runnable onShot) {
        return interceptFireballs(horrorBar, data.getHorror(), successes, investigator, true, onShot);
    }

    public Completable destroyDamage(int successes, Vector2 investigator, Runnable onShot) {
        return interceptFireballs(damageBar, data.getDamage(), successes, investigator, false, onShot);
    }

    /** @param onShot runs as each bullet is fired, e.g. to light up the die it comes from */
    private Completable interceptFireballs(TokenInFrameBar bar, int strength, int successes, Vector2 investigator, boolean horror,
                                           Runnable onShot) {
        return Completable.create(onSub -> {
            int blocked = Math.min(successes, strength);
            bar.setCoveredByDice(blocked);
            if (!horror) {
                remainingDamage = strength - blocked;
            }
            if (blocked == 0) {
                onSub.onCompleted();
                return;
            }
            holdStill = true;
            fireballService.reset();
            fireballService.setTargetHealth(1);
            fireballService.setBlue(horror);
            fireballService.setFireballScale(data.isMonsterEpic() ? EPIC_FIREBALL_SCALE : 1f);
            fireballService.setStaggerDelay(ATTACK_STAGGER);
            fireballService.setMidpointDisplacementDirection(1);
            // Intercepted fireballs are shot down at the moment they "land" halfway.
            if (onShot != null) fireballService.setOnLandAction(investigator, onShot);
            Vector2 body = getMonsterBodyCenter();
            List<Vector2> sources = new LinkedList<>();
            for (int i = 0; i < blocked; i++) {
                Image token = bar.getTokenImage(strength - 1 - i);
                Vector2 source = new Vector2(body.x + (i - (blocked - 1) / 2f) * 22f, body.y + 10f);
                sources.add(source);
                fireballService.setOnThrowAction(source, () -> {
                    playAttackReaction();
                    token.setVisible(false);
                });
            }
            boolean survivorsFollow = blocked < strength;
            fireballService.setOnLandLastAction(() -> {
                holdStill = false;
                // The unblocked fireballs should follow without a pause.
                continuesVolley = survivorsFollow;
                onSub.onCompleted();
            });
            fireballService.launchInterceptedFireballs(sources, investigator);
        });
    }

    public Completable destroyMonsterHealth(int successes, Vector2 investigator, Runnable onShot) {
        return Completable.create(onSub -> {
            int hearts = toughnessBar.getCurrentHealth();
            if (hearts == 0 || successes <= 0) {
                onSub.onCompleted();
                return;
            }
            holdStill = true;
            int hits = Math.min(hearts, successes);
            float tokenWidth = Math.min(110f, 650f / Math.max(1, hits - 1));
            int[] pendingShots = {hits};
            Runnable finishShot = () -> {
                if (--pendingShots[0] == 0) {
                    holdStill = false;
                    onSub.onCompleted();
                }
            };
            SequenceAction volley = Actions.sequence();
            for (int i = 0; i < hits; i++) {
                if (i > 0) {
                    volley.addAction(Actions.delay(MathUtils.random(DICE_SHOT_GAP_MIN, DICE_SHOT_GAP_MAX)));
                }
                Actor heart = toughnessBar.getChildren().get(hearts - 1 - i);
                float offsetX = ((hits - 1) / 2f - i) * tokenWidth;
                boolean killingShot = i == hearts - 1;
                volley.addAction(Actions.run(() -> {
                    Vector2 impact = randomBodyPoint(getMonsterBodyCenter());
                    fireballService.fireBullet(investigator, impact);
                    if (onShot != null) onShot.run();
                    playHitReaction(investigator, impact);
                    if (!killingShot) {
                        toughnessBar.loseToughnessNew(heart, offsetX).subscribe(finishShot::run);
                        return;
                    }
                    // The monster bursts only once its last heart has torn apart.
                    int[] pendingParts = {2};
                    Runnable finishPart = () -> {
                        if (--pendingParts[0] == 0) finishShot.run();
                    };
                    boolean[] burst = {false};
                    Runnable shatter = () -> {
                        if (burst[0]) return;
                        burst[0] = true;
                        shattered = true;
                        statusPlate.remove();
                        groundShadow.remove();
                        if (epicGlow != null) epicGlow.remove();
                        MonsterUtils.recordCombatDefeat(data.getMonsterInfo());
                        BreakApartAnimation.shatterOnHit(monsterImage, new CompletableSubscriber() {
                            @Override public void onCompleted() { finishPart.run(); }
                            @Override public void onError(Throwable e) { finishPart.run(); }
                            @Override public void onSubscribe(Subscription d) { }
                        });
                    };
                    toughnessBar.loseToughnessNew(heart, offsetX, shatter).subscribe(() -> {
                        // A skipped heart effect never tears; burst the monster anyway.
                        shatter.run();
                        finishPart.run();
                    });
                }));
            }
            addAction(new FastForwardAction<>(volley));
        });
    }

    public void highlightHorror() {
        setRowActive(horrorRow, true);
        setRowActive(damageRow, false);
        horrorBar.showBackground(1f);
        damageBar.hideBackground(1f);

        horrorBar.addAction(new FastForwardAction<>(Actions.alpha(1, 1f)));
        damageBar.addAction(new FastForwardAction<>(Actions.alpha(0.55f, 1f)));
        toughnessBar.addAction(new FastForwardAction<>(Actions.alpha(0.55f, 1f)));
    }

    public void highlightDamageAndToughness() {
        setRowActive(horrorRow, false);
        setRowActive(damageRow, true);
        horrorBar.hideBackground(1f);
        damageBar.showBackground(1f);

        horrorBar.addAction(new FastForwardAction<>(Actions.alpha(0.55f, 1f)));
        damageBar.addAction(new FastForwardAction<>(Actions.alpha(1, 1f)));
        toughnessBar.addAction(new FastForwardAction<>(Actions.alpha(1f, 1f)));
    }

    /** Name on top, horror and damage side by side, health below. */
    private Table createStatusPlate(MonsterCombatTableData data) {
        horrorBar = createCombatBar(data.getHorror(), "combat/horror.png");
        damageBar = createCombatBar(data.getDamage(), "combat/damage.png");

        int toughness = data.getToughness() == null ? 0 : data.getToughness();
        toughnessBarScale = Math.min(0.52f, 200f / (42.3f * Math.max(1, toughness)));
        toughnessBar = new ToughnessBar();
        toughnessBar.init(toughness, data.getCurrentHealth() == null ? 0 : data.getCurrentHealth(), toughnessBarScale);

        horrorRow = createStatRow();
        damageRow = createStatRow();
        horrorBarCell = horrorRow.add(horrorBar);
        damageBarCell = damageRow.add(damageBar);

        Table plate = new Table();
        boolean epic = data.isMonsterEpic();
        plate.setBackground(epic ? SelectionPanelStyle.panel("1A1408E6", "D4AF37") : SelectionPanelStyle.panel("121B1DDD", "87734E"));
        plate.pad(3, 5, 4, 5);
        plate.add(createNameLabel(data.getMonsterName(), epic)).colspan(2).padBottom(2).row();
        horrorRowCell = plate.add(horrorRow).height(ROW_HEIGHT);
        updateHorrorRowVisibility();
        plate.add(damageRow).height(ROW_HEIGHT).row();
        plate.add(toughnessBar).colspan(2).height(53 * toughnessBarScale).padTop(2);
        plate.pack();
        plate.setTouchable(Touchable.enabled);
        return plate;
    }

    /** Monsters without horror (e.g. Children of Yig) show no horror section at all. */
    private void updateHorrorRowVisibility() {
        boolean hasHorror = data.getHorror() != null && data.getHorror() > 0;
        if (hasHorror) {
            horrorRowCell.setActor(horrorRow).padRight(3);
        } else {
            horrorRowCell.setActor(null).padRight(0);
        }
    }

    private Label createNameLabel(String text, boolean epic) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), epic ? EPIC_GOLD : BRASS);
        Label label = new Label(epic ? get("combat.epic") + " \u2022 " + text : text, labelStyle);
        label.setAlignment(Align.center);
        label.setFontScale(epic ? 0.35f : 0.32f);
        return label;
    }

    private TokenInFrameBar createCombatBar(Integer amount, String texture) {
        int count = amount == null ? 0 : amount;
        float slot = Math.min(TOKEN_SLOT, SECTION_MAX_WIDTH / Math.max(1, count));
        TokenInFrameBar bar = new TokenInFrameBar(count, CustomAssetManager.getTexture(texture), slot * 0.9f);
        bar.space(slot * 0.1f);
        if (count == 0) {
            Label zero = new Label("0", new Label.LabelStyle(
                    getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("9AA9A3")));
            zero.setFontScale(0.26f);
            // Without row labels, a faded token keeps an empty section recognisable.
            Image ghost = new Image(CustomAssetManager.getTexture(texture));
            ghost.setScaling(Scaling.fit);
            ghost.getColor().a = 0.3f;
            Table empty = new Table();
            empty.add(ghost).size(slot * 0.8f).padRight(2);
            empty.add(zero);
            ((Container) bar.getChildren().first()).setActor(empty);
        }
        return bar;
    }

    private Table createStatRow() {
        Table row = new Table();
        row.pad(1, 4, 1, 4);
        setRowActive(row, false);
        return row;
    }

    private void setRowActive(Table row, boolean active) {
        if (row != null) row.setBackground(SelectionPanelStyle.panel(
                active ? "293530" : "192324", active ? "A99260" : "34433F"));
    }

    public int getRemainingDamage() {
        return remainingDamage != null ? remainingDamage : data.getDamage();
    }

    public void setCentered() {
        this.centered = true;
    }

    public float getCenteredX() {
        return (ViewProperties.VIEWPORT_WIDTH - getWidth()) / 2f;
    }

    public float getTopY() {
        return ViewProperties.VIEWPORT_HEIGHT - getHeight();
    }

    public Completable updateDamage(Integer damage) {
        if (damage.equals(data.getDamage())) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            data.setDamage(damage);
            damageBar.hideTokens(damage).subscribe(() -> {
                damageBar.remove();
                damageBar = createCombatBar(data.getDamage(), "combat/damage.png");
                damageBarCell.setActor(damageBar);
                statusPlate.pack();
                onSub.onCompleted();
            });
        });
    }

    public Completable updateHorror(Integer horror) {
        if (horror.equals(data.getHorror())) {
            return Completable.complete();
        }
        return Completable.create(onSub -> {
            data.setHorror(horror);
            horrorBar.hideTokens(horror).subscribe(() -> {
                horrorBar.remove();
                horrorBar = createCombatBar(data.getHorror(), "combat/horror.png");
                horrorBarCell.setActor(horrorBar);
                updateHorrorRowVisibility();
                statusPlate.pack();
                onSub.onCompleted();
            });
        });
    }

    private boolean locked = false;
    /** While interrupted (e.g. Flesh Ward, another investigator stepping in) the monster fades away. */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
