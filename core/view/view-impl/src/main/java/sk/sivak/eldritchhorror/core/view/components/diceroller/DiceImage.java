package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class DiceImage extends Image {
    public static final int DICE_SIZE = 77;
    public static final float ACTUAL_SIZE_RATIO = 0.75f;
    public static final float ACTUAL_DICE_SIZE = ACTUAL_SIZE_RATIO * DICE_SIZE;
    private Vector3 modelRotation;
    private final Texture texture;
    private TextureRegion textureRegion;
    private float textureRegionRotation;
    private int diceNumber;
    private DiceRoll.Score score;

    private FastForwardAction<FloatAction> rollRotationActionX;
    private FastForwardAction<FloatAction> rollRotationActionY;
    private FastForwardAction<FloatAction> rollRotationActionZ;
    private boolean rolling = false;
    private FastForwardAction rollScaleToAction;
    private FastForwardAction rollMoveToAction;
    private Rectangle area;
    private int diceValue;
    private Action1<DiceImage> onRollEndAction = diceImage -> {
    };
    private Action1<DiceImage> onAddOneEndAction = diceImage -> {
    };
    private RepeatAction highlightAction;


    public DiceImage(Texture texture) {
        this.texture = texture;
        modelRotation = new Vector3(
                (float) (Math.random() * 365),
                (float) (Math.random() * 365),
                (float) (Math.random() * 365));
        commonInit();
    }

    public DiceImage(Texture texture, int diceValue) {
        this.texture = texture;
        this.diceValue = diceValue;
        modelRotation = findModelRotationBasedOnDiceValue(diceValue);
        commonInit();
    }

    public int getDiceNumber() {
        return diceNumber;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceNumber(int diceNumber) {
        this.diceNumber = diceNumber;
    }

    public void setScore(DiceRoll.Score score) {
        this.score = score;
    }

    public DiceRoll.Score getScore() {
        return score;
    }

    void setOnRollEndAction(Action1<DiceImage> onRollEndAction) {
        this.onRollEndAction = onRollEndAction;
    }

    public void setOnAddOneEndAction(Action1<DiceImage> onAddOneEndAction) {
        this.onAddOneEndAction = onAddOneEndAction;
    }

    private void commonInit() {
        updateTextureRegion();
        setWidth(DICE_SIZE);
        setHeight(DICE_SIZE);
        setPosition(VIEWPORT_WIDTH / 2 - getWidth() / 2, -getHeight() * 1.207F);
    }

    private Vector3 findModelRotationBasedOnDiceValue(int diceValue) {
        if (diceValue == 1) {
            return new Vector3(0, 0, (float) (Math.random() * 365));
        } else if (diceValue == 2) {
            return new Vector3(90, 0, (float) (Math.random() * 365));
        } else if (diceValue == 3) {
            return new Vector3((float) (Math.random() * 365), 90, (float) (Math.random() * 365));
        } else if (diceValue == 4) {
            return new Vector3((float) (Math.random() * 365), 270, (float) (Math.random() * 365));
        } else if (diceValue == 5) {
            return new Vector3(270, 0, (float) (Math.random() * 365));
        } else if (diceValue == 6) {
            return new Vector3(180, 0, (float) (Math.random() * 365));
        } else {
            throw new IllegalArgumentException();
        }
    }


    public Vector3 getModelRotation() {
        return modelRotation;
    }

    public void setModelRotation(Vector3 modelRotation) {
        this.modelRotation = modelRotation;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!rolling) {
            return;
        }
        rollRotationActionX.act(delta);
        rollRotationActionY.act(delta);
        rollRotationActionZ.act(delta);
        rollMoveToAction.act(delta);
        rollScaleToAction.act(delta);
        setModelRotation(new Vector3(rollRotationActionX.getTypedAction().getValue(), rollRotationActionY.getTypedAction().getValue(), rollRotationActionZ.getTypedAction().getValue()));
        updateTextureRegion();

    }

    private void updateTextureRegion() {
        modelRotation.x = modelRotation.x % 360;
        modelRotation.y = modelRotation.y % 360;
        modelRotation.z = modelRotation.z % 360;

        int x = (int) (modelRotation.x / 22.5f);
        int y = (int) (modelRotation.y / 22.5f);

        textureRegionRotation = 0;
        int gridX = x;
        int gridY = 0;
        if (y >= 0 && y <= 4) {
            gridY = y + 4;
            if (y == 4) {
                gridX = 0;
                textureRegionRotation = -modelRotation.x;
            }
        } else if (y >= 5 && y <= 12) {
            textureRegionRotation = 180;
            gridX = (gridX + 8) % 16;
            gridY = 12 - y;
            if (y == 12) {
                gridX = 0;
                textureRegionRotation = modelRotation.x;
            }
        } else {
            gridY = y - 12;
        }
        textureRegionRotation += modelRotation.z;

        textureRegion = new TextureRegion(texture, gridX * 46, gridY * 46, 46, 46);

        setOrigin(Align.center);
        setDrawable(new TextureRegionDrawable(textureRegion));
        setRotation(textureRegionRotation);
    }

    void setArea(Rectangle area) {
        this.area = area;
    }

    public void roll() {
        Group parent = getParent();
        remove();
        parent.addActor(this);
        clearActions();
        setColor(Color.WHITE);
        if (rolling) {
            return;
        }
        modelRotation = findModelRotationBasedOnDiceValue(diceValue);
        rolling = true;
        float duration = (float) (Math.random() * 0.5 + 1.0);

        float rollDestinationX = MathUtils.random(area.getX() + getWidth() * ACTUAL_SIZE_RATIO * 0.207F,
                area.getX() + area.getWidth() - getWidth() * ACTUAL_SIZE_RATIO * 1.207F);
        float rollDestinationY = MathUtils.random(area.getY() + getHeight() * ACTUAL_SIZE_RATIO * 0.207F,
                area.getY() + area.getHeight() - getHeight() * ACTUAL_SIZE_RATIO * 1.207F);

        setScale(5f);
        setPosition(VIEWPORT_WIDTH / 2 - getWidth() / 2, -getHeight() * getScaleY() * 0.25f);

        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(rollDestinationX, rollDestinationY);
        moveToAction.setTarget(this);
        moveToAction.setInterpolation(Interpolation.sineOut);
        moveToAction.setDuration(duration);
        this.rollMoveToAction = new FastForwardAction<>(moveToAction);

        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setScale(1.0f);
        scaleToAction.setTarget(this);
        scaleToAction.setInterpolation(Interpolation.sineOut);
        scaleToAction.setDuration(duration / 2f);
        this.rollScaleToAction = new FastForwardAction<>(scaleToAction);

        FloatAction floatActionX = new FloatAction((float) (Math.random() * 365) + (365), getModelRotation().x);
        floatActionX.setDuration(duration);
        floatActionX.setInterpolation(Interpolation.sineOut);
        rollRotationActionX = new FastForwardAction<>(floatActionX);

        FloatAction floatActionY = new FloatAction((float) (Math.random() * 365) + (365), getModelRotation().y);
        floatActionY.setDuration(duration);
        floatActionY.setInterpolation(Interpolation.sineOut);
        rollRotationActionY = new FastForwardAction<>(floatActionY);


        FloatAction floatActionZ = new FloatAction((float) (Math.random() * 365) + (365), getModelRotation().z) {
            @Override
            protected void end() {
                onRollEndAction.call(DiceImage.this);
                rolling = false;
            }
        };
        floatActionZ.setDuration(duration);
        floatActionZ.setInterpolation(Interpolation.sineOut);
        rollRotationActionZ = new FastForwardAction<>(floatActionZ);
    }


    public void highlight() {
        Color colorIn;
        Color colorOut;
        switch (score) {
            case BAD:
                colorIn = new Color(0xffc0c0ff);
                colorOut = new Color(0xff8080ff);
                break;
            case GOOD:
                colorIn = new Color(0xc0ffc0ff);
                colorOut = new Color(0x80ff80ff);
                break;
            case VERY_GOOD:
                colorIn = new Color(0x60ff60ff);
                colorOut = new Color(0x20ff20ff);
                break;
            default:
                throw new IllegalArgumentException("" + score);
        }

        ColorAction toInColorAction = new ColorAction();
        toInColorAction.setEndColor(colorIn);
        toInColorAction.setActor(this);
        toInColorAction.setDuration(0.5f);

        ColorAction toOutColorAction = new ColorAction();
        toOutColorAction.setEndColor(colorOut);
        toOutColorAction.setActor(this);
        toOutColorAction.setDuration(0.5f);

        if (highlightAction != null) {
            removeAction(highlightAction);
        }
        highlightAction = Actions.forever(Actions.sequence(toOutColorAction, toInColorAction));
        addAction(highlightAction);
    }

    public void addOneToResult() {
        addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveBy(
                                (VIEWPORT_WIDTH / 2 - getWidth() / 2 - getX()) / 2f,
                                (VIEWPORT_HEIGHT / 2 - getHeight() / 2 - getY()) / 2f,
                                0.25f, Interpolation.sine),
                        Actions.scaleTo(2f, 2f, 0.25f, Interpolation.sine)
                ),
                Actions.run(this::recreateRollRotationActions)
        ));
    }

    private void recreateRollRotationActions() {
        Vector3 newModelRotation;
        if (diceValue == 1) {
            newModelRotation = new Vector3(90, 0, modelRotation.z);
        } else if (diceValue == 2) {
            newModelRotation = new Vector3(modelRotation.x + MathUtils.random(-90f, 90f), 90, modelRotation.z);
        } else if (diceValue == 3) {
            newModelRotation = new Vector3(modelRotation.x + MathUtils.random(-90f, 90f), 270, modelRotation.z);
        } else if (diceValue == 4) {
            newModelRotation = new Vector3(270, 360, modelRotation.z);
        } else if (diceValue == 5) {
            newModelRotation = new Vector3(180, 0, modelRotation.z);
        } else {
            return;
        }

        if (newModelRotation.x < 0f) {
            newModelRotation.x += 360;
        }
        if (newModelRotation.y < 0f) {
            newModelRotation.y += 360;
        }

        float addOneToResultDuration = 0.5f;


        FloatAction floatActionX = new FloatAction(getModelRotation().x, newModelRotation.x);
        floatActionX.setDuration(addOneToResultDuration);
        floatActionX.setInterpolation(Interpolation.sine);
        rollRotationActionX = new FastForwardAction(floatActionX);

        FloatAction floatActionY = new FloatAction(getModelRotation().y, newModelRotation.y);
        floatActionY.setDuration(addOneToResultDuration);
        floatActionY.setInterpolation(Interpolation.sine);
        rollRotationActionY = new FastForwardAction(floatActionY);

        FloatAction floatActionZ = new FloatAction(getModelRotation().z, newModelRotation.z) {
            @Override
            protected void end() {
                onAddOneEndAction.call(DiceImage.this);
                rolling = false;
            }
        };
        floatActionZ.setDuration(addOneToResultDuration);
        floatActionZ.setInterpolation(Interpolation.sine);
        rollRotationActionZ = new FastForwardAction(floatActionZ);

        this.diceValue++;
        rolling = true;
    }

    @Override
    public boolean remove() {
        return super.remove();
    }
}
