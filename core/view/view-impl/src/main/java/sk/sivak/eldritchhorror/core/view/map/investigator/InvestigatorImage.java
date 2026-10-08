package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.hourglass.HourglassComponent;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FADING_EFFECT_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

/**
 * @author msivak
 */
public class InvestigatorImage extends Image {

    public static final float BACKGROUND_SCALE = 1.07f;
    private static final float SOURCE_IMAGE_WIDTH = 1024f;
    private static final float SOURCE_IMAGE_HEIGHT = 1536f;
    private static final float IMAGE_HEIGHT = 100f;
    private static final float IMAGE_WIDTH = IMAGE_HEIGHT * SOURCE_IMAGE_WIDTH / SOURCE_IMAGE_HEIGHT;
    private final InvestigatorId investigatorId;
    private final GameController gameController;
    private Image asteroid;
    private Image borderImage;
    private float offsetX;
    private float offsetY;

    private boolean asteroidVisible = false;

    private HourglassComponent hourglassComponent;

    private boolean defeated;
    private boolean defeatedByHealth;
    private boolean highlighted;

    private static final float HIT_SHAKE_DURATION = 0.3f;
    private static final float HIT_SHAKE_DISTANCE = 5f;
    private static final float HIT_FLASH_IN = 0.06f;
    private static final float HIT_FLASH_OUT = 0.35f;
    private static final Color HEALTH_HIT_TINT = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color SANITY_HIT_TINT = new Color(0.45f, 0.55f, 1f, 1f);
    private float hitShake;
    private float hitTime;
    private float hitFlashElapsed = -1f;
    private final Color hitTint = new Color();
    private final Color savedColor = new Color();


    public InvestigatorImage(InvestigatorId investigatorId, GameController gameController) {
        this(investigatorId, gameController, CustomAssetManager.getInvestigatorDrawable(investigatorId),
                CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.INVESTIGATOR_BORDER));
    }

    InvestigatorImage(InvestigatorId investigatorId, GameController gameController, Drawable portrait, Drawable border) {
        super(portrait);

        this.gameController = gameController;
        this.investigatorId = investigatorId;
        setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        createBorderImage(border);
        setOrigin(getWidth() / 2, 10);

        setTouchable(Touchable.enabled);
        addClickListener(this, this::displayInvestigatorPassport);
        positionChanged();
    }

    private void createAsteroid() {
        asteroid = new Image(CustomAssetManager.getTexture(CustomAssetManager.ASTEROID));
        asteroid.setScaling(Scaling.fit);
        asteroid.setSize(120, 60);
        asteroid.setOrigin(Align.center);
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void displayHourglass() {
        createHourglassComponent();
        hourglassComponent.display();
        hourglassComponent.setPosition(
                getX() + IMAGE_WIDTH / 2 - hourglassComponent.getWidth() / 2,
                getY() + IMAGE_HEIGHT / 2 - hourglassComponent.getHeight() / 1.5f);
    }

    public void hideHourglass() {
        createHourglassComponent();
        hourglassComponent.hide();
    }

    private void createHourglassComponent() {
        if (hourglassComponent == null) {
            hourglassComponent = new HourglassComponent(getColor());
        }
        hourglassComponent.setInternalScale(0.10f);
    }

    private void displayInvestigatorPassport() {
        gameController.displayInvestigatorPassport(investigatorId);
    }

    private void createBorderImage(Drawable drawable) {
        borderImage = new Image(drawable) {

            @Override
            public float getX() {
                return InvestigatorImage.this.getX() - IMAGE_WIDTH * (BACKGROUND_SCALE - 1f) / 2f;
            }

            @Override
            public float getY() {
                return InvestigatorImage.this.getY() - IMAGE_HEIGHT * (BACKGROUND_SCALE - 1f) / 2f;
            }

            @Override
            public float getScaleX() {
                return InvestigatorImage.this.getScaleX();
            }

            @Override
            public float getScaleY() {
                return InvestigatorImage.this.getScaleY();
            }

            @Override
            public Color getColor() {
                Color color = super.getColor();
                color.a = InvestigatorImage.this.getColor().a;
                return color;
            }
        };
        borderImage.setColor(0, 0, 0, 0.78f);
        borderImage.setWidth(getWidth() * BACKGROUND_SCALE);
        borderImage.setHeight(getHeight() * BACKGROUND_SCALE);
        borderImage.setOrigin(borderImage.getWidth() / 2, 10);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (hitShake > 0f) {
            hitShake = Math.max(0f, hitShake - delta);
            hitTime += delta;
        }
        if (hitFlashElapsed >= 0f) {
            hitFlashElapsed += delta;
            if (hitFlashElapsed > HIT_FLASH_IN + HIT_FLASH_OUT) {
                hitFlashElapsed = -1f;
            }
        }
        if (asteroidVisible) {
            asteroid.getColor().a = getColor().a;
            asteroid.setPosition(
                    getX() - asteroid.getWidth() / 2 + getWidth() / 2f,
                    getY() - asteroid.getHeight() * 0.7f);
            asteroid.act(delta);
        }
        borderImage.act(delta);

        if (hourglassComponent != null) {
            hourglassComponent.act(delta);
        }
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        if (hourglassComponent != null) {
            hourglassComponent.setPosition(
                    getX() + IMAGE_WIDTH / 2 - hourglassComponent.getWidth() / 2,
                    getY() + IMAGE_HEIGHT / 2 - hourglassComponent.getHeight() / 1.5f);
        }
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // The hit shake and flash only affect drawing, so moves and offsets in progress are left alone.
        float groundX = getX();
        boolean shaking = hitShake > 0f;
        boolean flashing = hitFlashElapsed >= 0f;
        if (shaking) {
            setX(groundX + (float) Math.sin(hitTime * 70f) * HIT_SHAKE_DISTANCE * (hitShake / HIT_SHAKE_DURATION));
        }
        if (flashing || defeated) savedColor.set(getColor());
        if (flashing) {
            float flash = hitFlashElapsed < HIT_FLASH_IN
                    ? hitFlashElapsed / HIT_FLASH_IN
                    : 1f - (hitFlashElapsed - HIT_FLASH_IN) / HIT_FLASH_OUT;
            flash = Math.max(0f, Math.min(1f, flash));
            getColor().set(
                    savedColor.r * (1f + (hitTint.r - 1f) * flash),
                    savedColor.g * (1f + (hitTint.g - 1f) * flash),
                    savedColor.b * (1f + (hitTint.b - 1f) * flash),
                    savedColor.a);
        }
        // Tint the existing portrait; defeated investigators need no extra actors or texture switches.
        if (defeated) getColor().mul(defeatedByHealth ? HEALTH_HIT_TINT : SANITY_HIT_TINT);
        drawStand(batch, parentAlpha);
        if (flashing || defeated) {
            getColor().set(savedColor);
        }
        if (shaking) {
            setX(groundX);
        }
    }

    /** Shakes the stand and flashes it red (health) or blue (sanity), like a monster taking damage. */
    public void playHitReaction(boolean sanity) {
        hitShake = HIT_SHAKE_DURATION;
        hitTime = 0f;
        hitFlashElapsed = 0f;
        hitTint.set(sanity ? SANITY_HIT_TINT : HEALTH_HIT_TINT);
    }

    private void drawStand(Batch batch, float parentAlpha) {
        if (asteroidVisible) {
            asteroid.draw(batch, parentAlpha);
        }
        borderImage.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);
        if (hourglassComponent != null) {
            hourglassComponent.draw(batch, parentAlpha);
        }
    }

    public void setAsteroidVisible(boolean asteroidVisible) {
        if (asteroid == null) {
            createAsteroid();
        }
        this.asteroidVisible = asteroidVisible;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    void highlight(boolean active) {
        this.highlighted = active;
        if (active) {
            Group parent = getParent();
            remove();
            parent.addActor(this);
            borderImage.clearActions();
            addHighlightAction();
            setColor(Color.WHITE);
        } else {
            borderImage.clearActions();
            float borderImageAlpha = borderImage.getColor().a;
            float imageAlpha = getColor().a;
            borderImage.setColor(0f, 0f, 0f, borderImageAlpha);
            setColor(Color.GRAY);
            getColor().a = imageAlpha;
        }
    }

    private void addHighlightAction() {
        ColorAction toYellowAction = new ColorAction();
        toYellowAction.setEndColor(Color.YELLOW);
        toYellowAction.setActor(borderImage);
        toYellowAction.setDuration(FADING_EFFECT_DURATION);
        toYellowAction.setInterpolation(Interpolation.sine);

        ColorAction toGreenAction = new ColorAction();
        toGreenAction.setEndColor(Color.GREEN);
        toGreenAction.setActor(borderImage);
        toGreenAction.setDuration(FADING_EFFECT_DURATION);
        toGreenAction.setInterpolation(Interpolation.sine);

        borderImage.addAction(Actions.sequence(toYellowAction, toGreenAction, Actions.run(this::addHighlightAction)));
    }

    public void setOffset(float newOffsetX, float newOffsetY) {
        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(getX() - offsetX + newOffsetX, getY() - offsetY + newOffsetY);
        moveToAction.setDuration(FAST_ACTION_DURATION);
        moveToAction.setActor(this);
        addAction(new FastForwardAction<>(
                Actions.sequence(
                        moveToAction, Actions.run(() -> {
                            InvestigatorImage.this.offsetX = newOffsetX;
                            InvestigatorImage.this.offsetY = newOffsetY;
                        })
                )
        ));
    }

    public Completable fadeOutDefeated() {
        return Completable.create(onSub -> addAction(Actions.sequence(
                Actions.alpha(0, 0.25f),
                Actions.run(() -> {
                    remove();
                    onSub.onCompleted();
                }))));
    }

    public void setDefeatedByHealth(boolean defeatedByHealth) {
        this.defeated = true;
        this.defeatedByHealth = defeatedByHealth;
        highlighted = false;
        borderImage.clearActions();
        borderImage.setColor(defeatedByHealth ? HEALTH_HIT_TINT : SANITY_HIT_TINT);
    }
}
