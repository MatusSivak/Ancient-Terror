package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * @author msivak
 */
public class TokenActor extends ItemActor {

    private Image leftSideImage;
    private Image rightSideImage;


    public TokenActor(Texture leftSideTexture, Texture rightSideTexture) {
        this.leftSideImage = createSideImage(leftSideTexture);
        this.rightSideImage = createSideImage(rightSideTexture);
        float size = VIEWPORT_HEIGHT * 0.08f;
        setWidth(size * (leftSideTexture.getWidth() / (float) leftSideTexture.getHeight()));
        setHeight(size);
        setOrigin(getWidth() / 2, getHeight() / 2);

        leftSideImage.setOrigin(getWidth() / 2, getHeight() / 2);
        rightSideImage.setOrigin(getWidth() / 2, getHeight() / 2);

        getColor().a = 0.0f;
    }

    public TokenActor(TextureRegion leftSideTexture, TextureRegion rightSideTexture) {
        this.leftSideImage = createSideImage(leftSideTexture);
        this.rightSideImage = createSideImage(rightSideTexture);
        float size = VIEWPORT_HEIGHT * 0.08f;
        setWidth(size * (leftSideTexture.getRegionWidth() / (float) leftSideTexture.getRegionHeight()));
        setHeight(size);
        setOrigin(getWidth() / 2, getHeight() / 2);

        leftSideImage.setOrigin(getWidth() / 2, getHeight() / 2);
        rightSideImage.setOrigin(getWidth() / 2, getHeight() / 2);
    }

    private Image createSideImage(Texture texture) {
        return new Image(texture) {

            @Override
            public float getWidth() {
                return TokenActor.this.getWidth();
            }

            @Override
            public float getHeight() {
                return TokenActor.this.getHeight();
            }

            @Override
            public Color getColor() {
                return TokenActor.this.getColor();
            }
        };
    }

    private Image createSideImage(TextureRegion textureRegion) {
        return new Image(textureRegion) {

            @Override
            public float getWidth() {
                return TokenActor.this.getWidth();
            }

            @Override
            public float getHeight() {
                return TokenActor.this.getHeight();
            }

            @Override
            public Color getColor() {
                return TokenActor.this.getColor();
            }
        };
    }

    public Completable discard(float offsetX) {
        return Completable.create(onSub -> discard(offsetX, onSub, null));
    }

    public Completable discard(Action0 onStartAction) {
        return Completable.create(onSub -> discard(0, onSub, onStartAction));
    }

    public Completable discard(Action0 onStartAction, float offsetX) {
        return Completable.create(onSub -> discard(offsetX, onSub, onStartAction));
    }

    private void discard(float offsetX, CompletableSubscriber onSub, Action0 onStartAction) {
        setPosition(holderLocation.x, holderLocation.y);
        MoveToAction moveToCenterAction = new MoveToAction();
        moveToCenterAction.setPosition(VIEWPORT_WIDTH / 2 + offsetX,
                VIEWPORT_HEIGHT / 2);
        float actionDuration = ViewProperties.NORMAL_ACTION_DURATION;
        moveToCenterAction.setDuration(actionDuration);
        moveToCenterAction.setInterpolation(Interpolation.sine);
        moveToCenterAction.setActor(this);

        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setScale(4f);
        scaleToAction.setDuration(actionDuration);
        scaleToAction.setInterpolation(Interpolation.sine);
        scaleToAction.setActor(this);

        RotateToAction rotateToAction = new RotateToAction();
        rotateToAction.setRotation(360);
        rotateToAction.setInterpolation(Interpolation.sine);
        rotateToAction.setDuration(actionDuration);
        rotateToAction.setActor(this);

        ParallelAction moveScaleRotateToCenter = Actions.parallel(
                moveToCenterAction,
                scaleToAction,
                rotateToAction,
                Actions.run(() -> {
                    getColor().a = 1f;
                    if (onStartAction != null) {
                        onStartAction.call();
                    }
                }));

        MoveToAction moveLeftPartAction = new MoveToAction();
        moveLeftPartAction.setPosition(VIEWPORT_WIDTH * 0.45f + offsetX,
                VIEWPORT_HEIGHT * 0.33f);
        moveLeftPartAction.setDuration(actionDuration);
        moveLeftPartAction.setActor(leftSideImage);

        MoveToAction moveRightPartAction = new MoveToAction();
        moveRightPartAction.setPosition(VIEWPORT_WIDTH * 0.55f + offsetX,
                VIEWPORT_HEIGHT *  0.33f);
        moveRightPartAction.setDuration(actionDuration);
        moveRightPartAction.setActor(rightSideImage);

        AlphaAction fadeOutLeftAction = new AlphaAction();
        fadeOutLeftAction.setAlpha(0f);
        fadeOutLeftAction.setDuration(actionDuration);
        fadeOutLeftAction.setActor(leftSideImage);

        AlphaAction fadeOutRightAction = new AlphaAction();
        fadeOutRightAction.setAlpha(0f);
        fadeOutRightAction.setDuration(actionDuration);
        fadeOutRightAction.setActor(rightSideImage);

        addAction(new FastForwardAction<>(Actions.sequence(
                moveScaleRotateToCenter,
                Actions.delay(0.25f),
                Actions.run(onSub::onCompleted),
                Actions.parallel(
                        fadeOutLeftAction,
                        fadeOutRightAction,
                        moveLeftPartAction,
                        moveRightPartAction
                ),
                Actions.run(this::remove))));
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        leftSideImage.setRotation(degrees);
        rightSideImage.setRotation(degrees);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
        leftSideImage.setScale(scaleX, scaleY);
        rightSideImage.setScale(scaleX, scaleY);
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
        leftSideImage.setPosition(x, y, alignment);
        rightSideImage.setPosition(x, y, alignment);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        leftSideImage.setPosition(x, y);
        rightSideImage.setPosition(x, y);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        leftSideImage.setX(x);
        rightSideImage.setX(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        leftSideImage.setY(y);
        rightSideImage.setY(y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (leftSideImage == null || rightSideImage == null) {
            return;
        }
        leftSideImage.setOrigin(width / 2, height / 2);
        rightSideImage.setOrigin(width / 2, height / 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        leftSideImage.act(delta);
        rightSideImage.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        leftSideImage.draw(batch, parentAlpha);
        rightSideImage.draw(batch, parentAlpha);
    }
}
