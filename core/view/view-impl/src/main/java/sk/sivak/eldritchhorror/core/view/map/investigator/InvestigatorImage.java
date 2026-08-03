package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import rx.Completable;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.hourglass.HourglassComponent;
import sk.sivak.eldritchhorror.core.view.components.investigator.InvestigatorPuzzleEffect;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Collections;
import java.util.List;

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

    private List<Image> puzzleImages = Collections.emptyList();
    private boolean defeatedByHealth;


    public InvestigatorImage(InvestigatorId investigatorId, GameController gameController) {
        super(CustomAssetManager.getInvestigatorTexture(investigatorId));

        this.gameController = gameController;
        this.investigatorId = investigatorId;
        setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        createBorderImage();
        setOrigin(getWidth() / 2, 10);

        setTouchable(Touchable.enabled);
        addClickListener(this, this::displayInvestigatorPassport);
        positionChanged();
    }

    private void createAsteroid() {
        asteroid = new Image(CustomAssetManager.getTexture(CustomAssetManager.ASTEROID));
        asteroid.setScaling(Scaling.fit);
        asteroid.setRotation(30);
        asteroid.setSize(950 * 0.2f,841 * 0.2f);
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

    private void createBorderImage() {
        borderImage = new Image(CustomAssetManager.getTexture(CustomAssetManager.INVESTIGATOR_BORDER)) {

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
        borderImage.setColor(0, 0, 0, 1);
        borderImage.setWidth(getWidth() * BACKGROUND_SCALE);
        borderImage.setHeight(getHeight() * BACKGROUND_SCALE);
        borderImage.setOrigin(borderImage.getWidth() / 2, 10);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (asteroidVisible) {
            asteroid.getColor().a = getColor().a;
            asteroid.setPosition(
                    getX() - asteroid.getWidth() / 2 + getWidth() / 2f,
                    getY() - asteroid.getHeight() / 2 - getHeight() / 4f);
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

    void highlight(boolean active) {
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
        for (Image puzzleImage : puzzleImages) {
            MoveToAction puzzleImagemoveToAction = new MoveToAction();
            puzzleImagemoveToAction.setPosition(puzzleImage.getX() - offsetX + newOffsetX, puzzleImage.getY() - offsetY + newOffsetY);
            puzzleImagemoveToAction.setDuration(FAST_ACTION_DURATION);
            puzzleImagemoveToAction.setActor(puzzleImage);
            puzzleImage.addAction(new FastForwardAction<>(puzzleImagemoveToAction));
        }
    }

    public void setPuzzleImages(List<Image> puzzleImages) {
        this.puzzleImages = puzzleImages;
    }

    public Completable destroyPuzzleImages() {
        for (Image puzzleImage : puzzleImages) {
            puzzleImage.clearActions();
            if (defeatedByHealth) {
                puzzleImage.getColor().r = 1 * puzzleImage.getColor().a;
                puzzleImage.getColor().g = 0;
                puzzleImage.getColor().b = 0;
            } else {
                puzzleImage.getColor().r = 0;
                puzzleImage.getColor().g = 0;
                puzzleImage.getColor().b = 1f * puzzleImage.getColor().a;
            }
        }
        PublishSubject<Object> objectPublishSubject = PublishSubject.create();
        addAction(Actions.sequence(
                Actions.alpha(0, 1.5f),
                Actions.removeActor()
                ));
        InvestigatorPuzzleEffect.destroyPuzzleImages(puzzleImages, objectPublishSubject);
        return objectPublishSubject.toCompletable();
    }

    public void setDefeatedByHealth(boolean defeatedByHealth) {
        this.defeatedByHealth = defeatedByHealth;
    }
}
