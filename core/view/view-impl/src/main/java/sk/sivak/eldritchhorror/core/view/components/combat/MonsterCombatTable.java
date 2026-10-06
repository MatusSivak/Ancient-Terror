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
import rx.functions.Func0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
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
 * damage and health) floating above it. Fireballs from the dice hit the monster; fireballs from the monster hit the
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
    /** Epic monsters loom larger, glow gold and throw bigger fireballs. */
    private static final float EPIC_IMAGE_SIZE = 260f;
    private static final float EPIC_GLOW_SCALE = 1.06f;
    private static final float EPIC_FIREBALL_SCALE = 1.5f;
    /** Seconds between the monster's attack fireballs. */
    private static final float ATTACK_STAGGER = 0.55f;
    private static final Color EPIC_GOLD = Color.valueOf("F2D27A");
    private static final Color EPIC_GLOW = Color.valueOf("FFC94A");
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

    private Integer remainingHorror;
    private Integer remainingDamage;

    private boolean centered = false;
    private Cell<TokenInFrameBar> damageBarCell;
    private Cell<TokenInFrameBar> horrorBarCell;
    private Cell<Table> horrorRowCell;
    private FireballService fireballService;
    private Image monsterImage;
    private float imageSize = IMAGE_SIZE;
    /** Gold rim behind epic monsters; null for ordinary ones. */
    private SilhouetteImage epicGlow;
    private CombatPhaseIndicator phaseIndicator;
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

    public boolean isCentered() {
        return centered;
    }

    public void init(MonsterCombatTableData data) {
        clear();
        this.data = data;
        setTouchable(Touchable.disabled);
        setSize(0, 0);

        fireballService = new FireballService(getStage());

        if (monsterImage != null) {
            monsterImage.remove();
        }
        if (statusPlate != null) {
            statusPlate.remove();
        }
        if (epicGlow != null) {
            epicGlow.remove();
            epicGlow = null;
        }
        if (phaseIndicator != null) {
            phaseIndicator.remove();
        }
        imageSize = data.isMonsterEpic() ? EPIC_IMAGE_SIZE : IMAGE_SIZE;
        phaseIndicator = new CombatPhaseIndicator(data.isMonsterEpic());
        statusPlate = createStatusPlate(data);
        ButtonUtils.addClickListener(statusPlate, () -> displayMonsterCard(data));
        holdStill = false;
        stepPhase = 0f;
        walkBlend = 0f;
        walkLean = 0f;
        idleBlend = 1f;
        presence = 0f;
        monsterImage = createMonsterImage(data);
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

        wanderArea.set(150f, 60f, SAFE_RIGHT - imageSize - 150f, SAFE_TOP - imageSize - statusPlate.getHeight() - 60f);
        // Emerge at the top centre of the screen, then roam.
        wanderPosition.set(
                MathUtils.clamp(ViewProperties.VIEWPORT_WIDTH / 2f - imageSize / 2f, wanderArea.x, wanderArea.x + wanderArea.width),
                wanderArea.y + wanderArea.height);
        monsterImage.setPosition(wanderPosition.x, wanderPosition.y);
        wanderPause = MathUtils.random(0.5f, 1.2f);
        wanderDuration = 0f;
        act(0f);
    }

    /** Portrait and status plate live on the creature layer, above panels, cards and dice. */
    private void attachMonsterImage() {
        // The glow goes in first, so it is drawn behind the portrait.
        if (getStage() != null && epicGlow != null && epicGlow.getParent() == null) {
            if (monsterImage.getParent() != null) {
                monsterImage.getParent().addActorBefore(monsterImage, epicGlow);
            } else {
                InfoStage.getCreatureLayer().addActor(epicGlow);
            }
        }
        if (getStage() != null && phaseIndicator.getParent() == null) {
            InfoStage.getCreatureLayer().addActor(phaseIndicator);
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

    public CombatPhaseIndicator getPhaseIndicator() {
        return phaseIndicator;
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
        if (monsterImage == null) {
            return;
        }
        attachMonsterImage();
        hoverTime += delta;
        presence = interrupted
                ? Math.max(0f, presence - delta / FADE_DURATION)
                : Math.min(1f, presence + delta / FADE_DURATION);
        boolean shown = presence > 0f;
        monsterImage.setVisible(shown);
        statusPlate.setVisible(shown);
        phaseIndicator.setVisible(shown);
        if (epicGlow != null) {
            epicGlow.setVisible(shown);
        }
        float previousX = wanderPosition.x;
        float previousY = wanderPosition.y;
        boolean walking = !holdStill && updateWander(delta);
        updateGait(delta, walking, wanderPosition.x - previousX, wanderPosition.y - previousY);
        // Waddle with a stepping bounce while walking; breathe gently while standing; settle down while fighting.
        // Gaits cross-fade, so starting and stopping never snaps.
        float breathing = (1f - walkBlend) * idleBlend;
        float bob = Math.abs(MathUtils.sin(stepPhase)) * STEP_BOB * walkBlend
                + MathUtils.sin(hoverTime * 2f) * 2f * breathing;
        float tilt = (MathUtils.sin(stepPhase) * STEP_TILT + walkLean) * walkBlend
                + MathUtils.sin(hoverTime * 1.3f) * 1.5f * breathing;
        float shake = 0f;
        if (hitShake > 0f) {
            hitShake = Math.max(0f, hitShake - delta);
            shake = MathUtils.sin(hoverTime * 70f) * 5f * (hitShake / 0.3f);
        }
        monsterImage.setPosition(wanderPosition.x + shake, wanderPosition.y + bob);
        monsterImage.setRotation(tilt);
        monsterImage.getColor().a = presence * getColor().a;
        if (epicGlow != null) {
            float glowOffset = imageSize * (EPIC_GLOW_SCALE - 1f) / 2f;
            epicGlow.setPosition(wanderPosition.x + shake - glowOffset, wanderPosition.y + bob - glowOffset);
            epicGlow.setRotation(tilt);
            epicGlow.setScale(monsterImage.getScaleX(), monsterImage.getScaleY());
            epicGlow.getColor().a = presence * getColor().a * (0.55f + 0.3f * MathUtils.sin(hoverTime * 2.4f));
        }
        phaseIndicator.setPosition(8f, ViewProperties.VIEWPORT_HEIGHT - 8f - phaseIndicator.getHeight());
        phaseIndicator.getColor().a = presence * getColor().a;

        // The plate rides just above the visible part of the (fit-scaled) artwork; it does not tilt with it.
        monsterImage.validate();
        float artTop = wanderPosition.y + bob + monsterImage.getImageY() + monsterImage.getImageHeight();
        statusPlate.setPosition(
                Math.round(wanderPosition.x + imageSize / 2f - statusPlate.getWidth() / 2f),
                Math.round(Math.min(artTop + 2f, SAFE_TOP + 6f - statusPlate.getHeight())));
        statusPlate.getColor().a = presence * getColor().a;
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
        return new Vector2(
                wanderPosition.x + monsterImage.getImageX() + monsterImage.getImageWidth() / 2f,
                wanderPosition.y + monsterImage.getImageY() + monsterImage.getImageHeight() / 2f);
    }

    private Vector2 randomBodyPoint(Vector2 body) {
        float spread = monsterImage.getImageWidth() * 0.18f;
        return new Vector2(body.x + MathUtils.random(-spread, spread), body.y + MathUtils.random(-spread, spread));
    }

    private void playHitReaction() {
        hitShake = 0.3f;
        monsterImage.clearActions();
        monsterImage.addAction(Actions.sequence(
                Actions.color(new Color(1f, 0.4f, 0.4f, 1f), 0.06f),
                Actions.color(Color.WHITE, 0.35f)));
    }

    private void playAttackReaction() {
        monsterImage.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.scaleTo(1.08f, 1.08f, 0.08f, Interpolation.sineOut),
                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.sineIn))));
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
        Rectangle box = new Rectangle(0, 0, imageSize, imageSize);
        Vector2 point = new Vector2();
        for (int i = 0; i <= 24; i++) {
            pointOnStroll(i / 24f, point);
            box.setPosition(point.x, point.y);
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
        Rectangle spot = new Rectangle(position.x, position.y, imageSize, imageSize);
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

    /** The monster fades out instead of vanishing; the invisible combat actor is removed right away. */
    @Override
    public boolean remove() {
        settleTokenShakes();
        if (monsterImage != null && monsterImage.getParent() != null) {
            monsterImage.setTouchable(Touchable.disabled);
            monsterImage.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        for (Actor extra : new Actor[]{epicGlow, phaseIndicator}) {
            if (extra != null && extra.getParent() != null) {
                extra.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
            }
        }
        if (statusPlate != null && statusPlate.getParent() != null) {
            statusPlate.setTouchable(Touchable.disabled);
            statusPlate.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        return super.remove();
    }

    private void displayMonsterCard(MonsterCombatTableData data) {
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
            onSub.onCompleted();
            return;
        }
        addAction(new FastForwardAction<>(Actions.sequence(
                Actions.delay(0.6f),
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

    public Completable destroyHorror(List<Vector2> dicePositions) {
        return Completable.create(onSub -> {
            remainingHorror = data.getHorror() - dicePositions.size();
            shootDownTokens(dicePositions, onSub, horrorBar, true);
        });
    }

    /** Dice beyond the monster's damage are not used here; they hit its health later. */
    public Completable destroyDamage(List<Vector2> dicePositions) {
        return Completable.create(onSub -> {
            remainingDamage = data.getDamage() - dicePositions.size();
            shootDownTokens(dicePositions, onSub, damageBar, false);
        });
    }

    /**
     * Defence comes first: every successful die fires at one of the monster's horror / damage icons on the plate and
     * burns it away. Only the icons left afterwards are thrown at the investigator ({@link #destroySanity} / {@link #destroyHealth}).
     */
    private void shootDownTokens(List<Vector2> dicePositions, CompletableSubscriber onSub, TokenInFrameBar bar, boolean horror) {
        if (bar.getChildren().size == 1 && bar.getChildren().get(0) instanceof Container) {
            onSub.onCompleted();
            return;
        }
        int count = bar.getChildren().size;
        bar.setCoveredByDice(dicePositions.size());
        int shotDown = Math.min(dicePositions.size(), count);
        if (shotDown == 0) {
            onSub.onCompleted();
            return;
        }
        // The plate rides on the monster; keep it still while the icons are targeted.
        holdStill = true;
        act(0f);
        // Every success fires; surplus fireballs strike icons that are already burning.
        List<Vector2> sources = new LinkedList<>(dicePositions);
        List<Vector2> targets = new LinkedList<>();
        Set<Image> burned = new HashSet<>();
        fireballService.reset();
        fireballService.setTargetHealth(1);
        fireballService.setBlue(horror);
        // Cancelled icons are the last ones in the row; they burn away in their own slots so nothing shifts.
        for (int i = count - 1; i >= count - shotDown; i--) {
            Image token = bar.getTokenImage(i);
            token.setOrigin(Align.center);
            Vector2 target = token.localToStageCoordinates(new Vector2(token.getWidth() / 2f, token.getHeight() / 2f));
            targets.add(target);
            fireballService.setOnLandAction(target, () -> {
                CombatSounds.playMonsterTokenHit();
                shakeToken(token);
                if (!burned.add(token)) {
                    return;
                }
                token.addAction(new FastForwardAction<>(Actions.sequence(
                        Actions.scaleTo(0f, 0f, 0.3f, Interpolation.sineIn),
                        Actions.visible(false),
                        Actions.scaleTo(1f, 1f))));
            });
        }
        fireballService.setOnLandLastAction(() -> addAction(new FastForwardAction<>(Actions.delay(0.4f, Actions.run(() -> {
            holdStill = false;
            onSub.onCompleted();
        })))));
        fireballService.setMidpointDisplacementDirection(-1);
        fireballService.launchFireballs(sources, targets);
    }
    public Completable destroyMonsterHealth(List<Vector2> dicePositions) {
        return Completable.create(onSub -> {
            int targetsCount = Math.min(dicePositions.size(), toughnessBar.getCurrentHealth());
            int blankHealth = toughnessBar.getChildren().size - toughnessBar.getCurrentHealth();
            List<Actor> healthIcons = new LinkedList<>();
            int from = toughnessBar.getChildren().size - 1 - blankHealth;
            int to = from - targetsCount;
            for (int i = from; i > to; i--) {
                healthIcons.add(toughnessBar.getChildren().get(i));
            }

            fireballService.weakReset();
            // Every success fires; one heart is torn off per heart left, surplus fireballs just strike the body.
            fireballService.setTargetHealth(1);
            Set<Actor> torn = new HashSet<>();
            // The monster stops roaming and every fireball strikes its body; each hit tears a heart off its health bar.
            holdStill = true;
            Vector2 body = getMonsterBodyCenter();
            List<Vector2> targets = new LinkedList<>();
            for (Actor child : healthIcons) {
                Vector2 target = randomBodyPoint(body);
                targets.add(target);
                fireballService.setOnLandAction(target, () -> {
                    playHitReaction();
                    if (!torn.add(child)) {
                        return;
                    }
                    float tokenWidth = 100;
                    float baseOffset = - (healthIcons.size() - 1) * tokenWidth / 2f;
                    float offsetX = baseOffset + healthIcons.indexOf(child) * tokenWidth;
                    toughnessBar.loseToughnessNew(child, offsetX).subscribe();
                });
            }
            fireballService.setOnLandLastAction(() -> {
                holdStill = false;
                onSub.onCompleted();
            });
            fireballService.setMidpointDisplacementDirection(-1);
            List<Vector2> sources = targets.isEmpty() ? new LinkedList<>() : new LinkedList<>(dicePositions);
            fireballService.launchFireballs(sources, targets);
        });
    }

    public void highlightHorror() {
        phaseIndicator.setPhase(CombatPhaseIndicator.Phase.HORROR);
        setRowActive(horrorRow, true);
        setRowActive(damageRow, false);
        horrorBar.showBackground(1f);
        damageBar.hideBackground(1f);

        horrorBar.addAction(new FastForwardAction<>(Actions.alpha(1, 1f)));
        damageBar.addAction(new FastForwardAction<>(Actions.alpha(0.55f, 1f)));
        toughnessBar.addAction(new FastForwardAction<>(Actions.alpha(0.55f, 1f)));
    }

    public void highlightDamageAndToughness() {
        phaseIndicator.setPhase(CombatPhaseIndicator.Phase.DAMAGE);
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