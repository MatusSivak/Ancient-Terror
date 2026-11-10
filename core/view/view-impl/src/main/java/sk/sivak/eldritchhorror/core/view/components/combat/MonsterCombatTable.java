package sk.sivak.eldritchhorror.core.view.components.combat;

import sk.sivak.eldritchhorror.core.view.shader.LitImage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.assetmanager.MonsterArtwork;
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

import java.util.ArrayList;
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
 * combat API and timing. What the player sees is the monster portrait with a status plate (name, horror,
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
    private static final float MONSTER_SIDE_LEFT = ViewProperties.VIEWPORT_WIDTH / 2f;
    private static final float SAFE_TOP = 532f;
    private static final float FIGHTER_X_OFFSET = 30f;
    private static final float FIGHTER_FLOOR_Y = 34f;
    private static final Color MONSTER_TINT = new Color(0.78f, 0.78f, 0.78f, 1f);
    private static final float IMAGE_SIZE = 200f;
    /** Epic monsters loom larger and throw bigger fireballs. */
    private static final float EPIC_IMAGE_SIZE = 260f;
    private static final float EPIC_FIREBALL_SCALE = 1.5f;
    /** Seconds between the monster's attack fireballs. */
    private static final float ATTACK_STAGGER = 0.55f;
    /** Pause between the monster's attack cry and its first fireball. */
    private static final float ATTACK_CRY_LEAD = 0.5f;
    /** Random gap in seconds between investigator shots. */
    private static final float DICE_SHOT_GAP_MIN = 0.15f;
    private static final float DICE_SHOT_GAP_MAX = 0.30f;
    /** Set when an intercepted volley ends with fireballs left over, so these follow immediately. */
    private boolean continuesVolley;
    private static final Color EPIC_GOLD = Color.valueOf("F2D27A");
    private static final float FADE_DURATION = 0.6f;
    /** How long {@link #remove()} takes to fade the monster away. */
    public static final float HIDE_DURATION = FADE_DURATION / 2f;
    private static final Color BRASS = Color.valueOf("DCC99F");
    private Table horrorRow;
    private Table damageRow;
    /** Name, horror, damage and health, floating above the portrait. */
    private Table statusPlate;
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
    private final List<BackgroundMonster> backgroundMonsters = new ArrayList<>();
    private float facingScaleX;
    private CombatGroundShadow groundShadow;
    private float imageSize = IMAGE_SIZE;
    private float hoverTime;
    private float fighterCenterX;
    private float stanceTime;
    private float attackTime;
    private static final float ATTACK_PULSE_DURATION = 0.28f;
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
        for (BackgroundMonster monster : backgroundMonsters) {
            monster.remove();
        }
        backgroundMonsters.clear();
        imageSize = data.isMonsterEpic() ? EPIC_IMAGE_SIZE : IMAGE_SIZE;
        List<MonsterInfo> encountered = data.getEncounteredMonsters();
        List<MonsterInfo> waiting = data.getWaitingMonsters();
        int others = encountered.size() + waiting.size();
        float progress = others == 0 ? 0.5f : (float) encountered.size() / others;
        float margin = imageSize / 2f + 18f;
        fighterCenterX = MathUtils.lerp(MONSTER_SIDE_LEFT + margin, SAFE_RIGHT - margin, progress) - FIGHTER_X_OFFSET;
        float queueMargin = BackgroundMonster.EPIC_SIZE / 2f + 4f;
        addQueue(encountered, MONSTER_SIDE_LEFT + queueMargin - FIGHTER_X_OFFSET);
        addQueue(waiting, SAFE_RIGHT - queueMargin);
        statusPlate = createStatusPlate(data);
        ButtonUtils.addClickListener(statusPlate, () -> displayMonsterCard(data));
        shattered = false;
        holdStill = false;
        stanceTime = 0f;
        presence = 0f;
        attackTime = 0f;
        hitShake = 0f;
        monsterImage = createMonsterImage(data);
        facingScaleX = MonsterArtwork.facesRight(data.getMonsterClassName(), data.isMonsterEpic()) ? -1f : 1f;
        groundShadow = new CombatGroundShadow();
        groundShadow.setName("combat-ground-shadow");
        // Panels, cards and dice are layered above the creature layer, so they still get their touches first.
        monsterImage.setTouchable(Touchable.enabled);
        ButtonUtils.addClickListener(monsterImage, () -> displayMonsterCard(data));
        attachMonsterImage();

        act(0f);
    }

    private void addQueue(List<MonsterInfo> monsters, float outsideX) {
        // Reserve a gap beside the active portrait, then fit the line into the remaining floor space.
        if (monsters.isEmpty()) {
            return;
        }
        outsideX = MathUtils.lerp(fighterCenterX, outsideX, 0.88f);
        float room = Math.abs(outsideX - fighterCenterX);
        float firstGap = Math.min(imageSize * 0.5f + 8f, room / (1f + 0.55f * (monsters.size() - 1)));
        float firstX = fighterCenterX + Math.copySign(firstGap, outsideX - fighterCenterX);
        for (int i = monsters.size() - 1; i >= 0; i--) {
            float slot = monsters.size() == 1 ? 0f : (float) i / (monsters.size() - 1);
            backgroundMonsters.add(new BackgroundMonster(monsters.get(i),
                    MathUtils.lerp(firstX, outsideX, slot), 62f + 10f * slot));
        }
    }

    /** Portrait and status plate live on the creature layer, behind panels, cards and dice. */
    private void attachMonsterImage() {
        if (getStage() != null && groundShadow != null && groundShadow.getParent() == null) {
            InfoStage.getCreatureLayer().addActorAt(0, groundShadow);
        }
        if (getStage() != null && monsterImage.getParent() == null) {
            InfoStage.getCreatureLayer().addActor(monsterImage);
        }
        if (getStage() != null && statusPlate.getParent() == null) {
            InfoStage.getCreatureLayer().addActor(statusPlate);
            for (BackgroundMonster monster : backgroundMonsters) {
                InfoStage.getCreatureLayer().addActorAt(0, monster.getShadow());
                Actor inFront = monster.isEpic() ? groundShadow : monsterImage;
                InfoStage.getCreatureLayer().addActorBefore(inFront, monster);
            }
            groundShadow.toBack();
        }
    }

    public Image getMonsterImage() {
        return monsterImage;
    }

    public Table getStatusPlate() {
        return statusPlate;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (monsterImage == null) {
            return;
        }
        presence = interrupted
                ? Math.max(0f, presence - delta / FADE_DURATION)
                : Math.min(1f, presence + delta / FADE_DURATION);
        boolean shown = presence > 0f;
        for (BackgroundMonster monster : backgroundMonsters) {
            monster.setVisible(shown);
            monster.getColor().a = presence * getColor().a;
        }
        if (shattered) {
            return;
        }
        attachMonsterImage();
        hoverTime += delta;
        monsterImage.setVisible(shown);
        groundShadow.setVisible(shown);
        statusPlate.setVisible(shown);
        if (!holdStill) {
            stanceTime += delta;
        }
        float footX = fighterCenterX + 3.5f * MathUtils.sin(stanceTime);
        float shake = 0f;
        float kick = 0f;
        if (hitShake > 0f) {
            hitShake = Math.max(0f, hitShake - delta * (FastForwardAction.isOn() ? 20f : 1f));
            float remaining = hitShake / 0.3f;
            kick = 16f * remaining * remaining;
            shake = MathUtils.sin(hoverTime * 70f) * 4f * remaining;
        }
        monsterImage.setPosition(footX - imageSize / 2f + shake + recoilDirection.x * kick, FIGHTER_FLOOR_Y);
        attackTime = Math.max(0f, attackTime - delta * (FastForwardAction.isOn() ? 20f : 1f));
        float attackPulse = attackTime > 0f ? 0.12f * MathUtils.sin(MathUtils.PI * attackTime / ATTACK_PULSE_DURATION) : 0f;
        float scale = 1f + attackPulse;
        monsterImage.setScale(facingScaleX * scale, scale);
        // Keep the base of the fitted artwork on its path as the portrait changes scale.
        monsterImage.validate();
        monsterImage.localToParentCoordinates(artLeft.set(imageSize / 2f, monsterImage.getImageY()));
        monsterImage.moveBy(0f, monsterImage.getY() - artLeft.y);
        monsterImage.getColor().a = presence * getColor().a;

        // The plate rides just above the visible part of the (fit-scaled) artwork; it does not tilt with it.
        float left = monsterImage.getImageX(), bottom = monsterImage.getImageY();
        float right = left + monsterImage.getImageWidth(), top = bottom + monsterImage.getImageHeight();
        monsterImage.localToParentCoordinates(artLeft.set(left, top));
        monsterImage.localToParentCoordinates(artRight.set(right, top));
        float artTop = Math.max(artLeft.y, artRight.y);
        statusPlate.setPosition(
                Math.round(footX - statusPlate.getWidth() / 2f),
                Math.round(Math.min(artTop + 2f, SAFE_TOP + 6f - statusPlate.getHeight())));
        statusPlate.getColor().a = presence * getColor().a;
        monsterImage.localToParentCoordinates(artLeft.set((left + right) / 2f, bottom));
        // The bundled portraits have a small transparent border below the feet.
        float contactY = artLeft.y + monsterImage.getImageHeight() * scale * 0.04f;
        groundShadow.place(artLeft.x, contactY, monsterImage.getImageWidth() * scale * 1.1f,
                imageSize * scale * 0.19f, presence * getColor().a * 0.65f);
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
        monsterImage.addAction(new FastForwardAction<>(Actions.color(MONSTER_TINT, 0.32f)));
        HitBloodSplatter.spray(getStage().getRoot(), impact, recoilDirection, data.isMonsterEpic() ? 1.35f : 1f);
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
        if (groundShadow != null && groundShadow.getParent() != null) {
            groundShadow.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        if (statusPlate != null && statusPlate.getParent() != null) {
            statusPlate.setTouchable(Touchable.disabled);
            statusPlate.addAction(Actions.sequence(Actions.alpha(0f, HIDE_DURATION), Actions.removeActor()));
        }
        for (BackgroundMonster monster : backgroundMonsters) {
            monster.addAction(Actions.sequence(Actions.fadeOut(HIDE_DURATION), Actions.removeActor()));
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
        Image image = new LitImage(texture) {
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
        image.setColor(MONSTER_TINT);
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
        if (!continuesVolley) CombatSounds.playMonsterAttackCry(sanity);
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
            // The monster cries out once, then starts hurling the volley the dice shoot down.
            CombatSounds.playMonsterAttackCry(horror);
            addAction(new FastForwardAction<>(Actions.delay(ATTACK_CRY_LEAD,
                    Actions.run(() -> fireballService.launchInterceptedFireballs(sources, investigator)))));
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
                    // The previous shot's flight time already counts toward the gap.
                    volley.addAction(Actions.delay(MathUtils.random(DICE_SHOT_GAP_MIN, DICE_SHOT_GAP_MAX) - BulletTracer.FLIGHT));
                }
                Actor heart = toughnessBar.getChildren().get(hearts - 1 - i);
                float offsetX = ((hits - 1) / 2f - i) * tokenWidth;
                boolean killingShot = i == hearts - 1;
                Vector2 impact = new Vector2();
                volley.addAction(Actions.run(() -> {
                    impact.set(randomBodyPoint(getMonsterBodyCenter()));
                    fireballService.fireBullet(investigator, impact);
                    if (onShot != null) onShot.run();
                }));
                // The wound opens once the tracer round actually reaches the monster.
                volley.addAction(Actions.delay(BulletTracer.FLIGHT));
                volley.addAction(Actions.run(() -> {
                    playHitReaction(investigator, impact);
                    if (!killingShot) {
                        toughnessBar.loseToughnessNew(heart, offsetX).subscribe(finishShot::run);
                        return;
                    }
                    // The monster bursts the moment it loses its last heart; the heart's own effect plays on top.
                    int[] pendingParts = {2};
                    Runnable finishPart = () -> {
                        if (--pendingParts[0] == 0) finishShot.run();
                    };
                    // Start the heart first, so it leaves its slot before the plate is detached.
                    toughnessBar.loseToughnessNew(heart, offsetX).subscribe(finishPart::run);
                    shattered = true;
                    statusPlate.remove();
                    groundShadow.remove();
                    MonsterUtils.recordCombatDefeat(data.getMonsterInfo());
                    BreakApartAnimation.shatterOnHit(monsterImage, new CompletableSubscriber() {
                        @Override public void onCompleted() { finishPart.run(); }
                        @Override public void onError(Throwable e) { finishPart.run(); }
                        @Override public void onSubscribe(Subscription d) { }
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
        horrorBar = createCombatBar(data.getHorror(), FireballImage.Kind.HORROR);
        damageBar = createCombatBar(data.getDamage(), FireballImage.Kind.DAMAGE);

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

    private TokenInFrameBar createCombatBar(Integer amount, FireballImage.Kind kind) {
        int count = amount == null ? 0 : amount;
        float slot = Math.min(TOKEN_SLOT, SECTION_MAX_WIDTH / Math.max(1, count));
        TokenInFrameBar bar = new TokenInFrameBar(count, kind, slot * 0.9f);
        bar.space(slot * 0.1f);
        if (count == 0) {
            Label zero = new Label("0", new Label.LabelStyle(
                    getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.valueOf("9AA9A3")));
            zero.setFontScale(0.26f);
            // Without row labels, a faded token keeps an empty section recognisable.
            Image ghost = new FireballImage(kind);
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
                damageBar = createCombatBar(data.getDamage(), FireballImage.Kind.DAMAGE);
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
                horrorBar = createCombatBar(data.getHorror(), FireballImage.Kind.HORROR);
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
