package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
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
    private int frameSize;
    private TextureRegion textureRegion;
    private float textureRegionRotation;
    private int diceNumber;
    private DiceRoll.Score score;

    private FastForwardAction<FloatAction> rollRotationActionX;
    private FastForwardAction<FloatAction> rollRotationActionY;
    private FastForwardAction<FloatAction> rollRotationActionZ;
    private boolean rolling = false;
    private FastForwardAction<TemporalAction> throwAction;

    private Rectangle area;
    private int diceValue;
    private Action1<DiceImage> onRollEndAction = diceImage -> {
    };
    private Action1<DiceImage> onAddOneEndAction = diceImage -> {
    };
    private RepeatAction highlightAction;
    private DiceRollAudio rollAudio;
    private int audibleDiceCount = 1;
    private Texture shadowTexture;
    private float flightHeight;

    void setShadowTexture(Texture texture) {
        shadowTexture = texture;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (shadowTexture != null) {
            float packedColor = batch.getPackedColor();
            float altitude = MathUtils.clamp(flightHeight / 70f, 0f, 1f);
            // The soft mask's outer edge is nearly transparent. Its footprint must
            // extend beyond the opaque die so the contact shadow remains visible.
            float width = ACTUAL_DICE_SIZE * (1.65f + altitude * 0.45f) * getScaleX();
            float height = ACTUAL_DICE_SIZE * (1.40f + altitude * 0.40f) * getScaleY();
            batch.setColor(0f, 0f, 0f, (0.72f - altitude * 0.42f) * getColor().a * parentAlpha);
            batch.draw(shadowTexture, getX() + getWidth() / 2f - width / 2f + 8f * getScaleX(),
                    getY() + getHeight() / 2f - flightHeight - height / 2f - 10f * getScaleY(), width, height);
            batch.setColor(packedColor);
        }
        super.draw(batch, parentAlpha);
    }

    void setRollAudio(DiceRollAudio audio) {
        this.rollAudio = audio;
    }

    void setAudibleDiceCount(int count) {
        audibleDiceCount = count;
    }


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
        if (texture.getWidth() % 16 != 0 || texture.getHeight() != texture.getWidth() / 16 * 9) {
            throw new IllegalArgumentException("Dice texture must contain 16 by 9 square frames");
        }
        frameSize = texture.getWidth() / 16;
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
        if (throwAction != null) {
            throwAction.act(delta);
            return;
        }
        rollRotationActionX.act(delta);
        rollRotationActionY.act(delta);
        rollRotationActionZ.act(delta);
        modelRotation.set(rollRotationActionX.getTypedAction().getValue(), rollRotationActionY.getTypedAction().getValue(), rollRotationActionZ.getTypedAction().getValue());
        updateTextureRegion();

    }

    private void updateTextureRegion() {
        modelRotation.x = (modelRotation.x % 360 + 360) % 360;
        modelRotation.y = (modelRotation.y % 360 + 360) % 360;
        modelRotation.z = (modelRotation.z % 360 + 360) % 360;

        int x = Math.round(modelRotation.x / 22.5f) % 16;
        int y = Math.round(modelRotation.y / 22.5f) % 16;

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

        if (textureRegion == null) {
            textureRegion = new TextureRegion(texture, gridX * frameSize, gridY * frameSize, frameSize, frameSize);
            setDrawable(new TextureRegionDrawable(textureRegion));
        } else {
            textureRegion.setRegion(gridX * frameSize, gridY * frameSize, frameSize, frameSize);
        }

        setOrigin(Align.center);
        setRotation(textureRegionRotation);
    }

    void setArea(Rectangle area) {
        this.area = area;
    }

    public void roll() {
        if (rolling) {
            return;
        }
        Group parent = getParent();
        remove();
        parent.addActor(this);
        clearActions();
        setColor(Color.WHITE);
        rolling = true;

        final Vector3 landing = findModelRotationBasedOnDiceValue(diceValue);
        final Vector3 launch = new Vector3(
                landing.x + MathUtils.random(480f, 840f),
                landing.y + MathUtils.random(480f, 840f),
                landing.z + MathUtils.random(180f, 420f));
        final float startX = VIEWPORT_WIDTH / 2f - getWidth() / 2f + MathUtils.random(-45f, 45f);
        final float startY = -getHeight() * 1.3f;
        final float margin = getWidth() * ACTUAL_SIZE_RATIO * 0.207f;
        final float destinationX = MathUtils.random(area.x + margin,
                Math.max(area.x + margin, area.x + area.width - getWidth() * ACTUAL_SIZE_RATIO * 1.207f));
        final float destinationY = MathUtils.random(area.y + margin,
                Math.max(area.y + margin, area.y + area.height - getHeight() * ACTUAL_SIZE_RATIO * 1.207f));
        final float lift = MathUtils.random(45f, 70f);
        final float curve = MathUtils.random(-28f, 28f);
        final DiceRollAudio.Roll sound = rollAudio == null ? null : rollAudio.newRoll(audibleDiceCount);
        TemporalAction motion = new TemporalAction() {
            @Override
            protected void update(float progress) {
                float travel = DiceThrowMotion.travel(progress);
                float height = DiceThrowMotion.height(progress);
                float tumble = DiceThrowMotion.tumble(progress);
                float wobble = DiceThrowMotion.wobble(progress);
                flightHeight = lift * height;
                setPosition(MathUtils.lerp(startX, destinationX, travel)
                                + curve * 4f * travel * (1f - travel),
                        MathUtils.lerp(startY, destinationY, travel) + lift * height);
                float scale = 1f + 0.35f * height + 0.2f * (1f - travel);
                setScale(scale, scale * (1f - Math.abs(wobble) * 0.035f));
                modelRotation.set(
                        MathUtils.lerp(launch.x, landing.x, tumble),
                        MathUtils.lerp(launch.y, landing.y, tumble),
                        MathUtils.lerp(launch.z, landing.z, travel) + wobble * 5f);
                updateTextureRegion();
                if (sound != null) {
                    sound.update(progress, ((getX() + getWidth() / 2f) / VIEWPORT_WIDTH * 2f - 1f) * 0.65f);
                }
            }

            @Override
            protected void end() {
                // Publish completion only after the final pose and position have been applied.
                rolling = false;
                throwAction = null;
                onRollEndAction.call(DiceImage.this);
            }
        };
        motion.setDuration(MathUtils.random(1.1f, 1.45f));
        throwAction = new FastForwardAction<>(motion);
        setPosition(startX, startY);
        setScale(1.2f);
        modelRotation.set(launch);
        updateTextureRegion();
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
