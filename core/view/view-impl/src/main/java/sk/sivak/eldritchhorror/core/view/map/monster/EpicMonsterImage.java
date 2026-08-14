package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.shader.BlurShadowShader;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.EPIC_MONSTER_SIZE;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

/**
 * @author msivak
 */
public class EpicMonsterImage extends Image {
    private static final float RECKONING_PULSE_SPEED = 4f;
    private static final float RECKONING_PULSE_MIN_ALPHA = 0.0f;
    private static final float RECKONING_PULSE_MAX_ALPHA = 0.75f;
    private static final float EPIC_MONSTER_SCALE_FACTOR = 0.9f;
    private static final float SQUISH_STRETCH_DURATION = 0.9f;
    private static final float SQUISH_STRETCH_SCALE_DELTA = 0.08f;

    private final Vector2 position;
    private MonsterInfo monsterInfo;
    private GameController gameController;
    private final boolean hasReckoning;
    private Image borderImage;
    private boolean drawReckoningPulse = false;
    private float reckoningPulseTime = 0f;

    public EpicMonsterImage(MonsterInfo monsterInfo, GameController gameController, boolean isCenter, boolean hasReckoning, LocationId location, Texture texture) {
        this(monsterInfo, gameController, hasReckoning, LocationPositionResolver.resolve(location), texture);
    }

    public EpicMonsterImage(MonsterInfo monsterInfo, GameController gameController, boolean hasReckoning, Vector2 position, Texture texture) {
        super(texture);
        this.monsterInfo = monsterInfo;
        this.gameController = gameController;
        this.hasReckoning = hasReckoning;
        this.position = position;
        setWidth(EPIC_MONSTER_SIZE * EPIC_MONSTER_SCALE_FACTOR);
        setHeight(EPIC_MONSTER_SIZE * EPIC_MONSTER_SCALE_FACTOR);
        setOrigin(getWidth() / 2, getHeight() / 2);
        if (hasReckoning) {
            drawReckoningPulse = true;
        }

        addClickListener(this, this::displayMonsterCard);
        addSquishStretchAnimation();
    }

    private void displayMonsterCard() {
        gameController.displayMonsterCard(monsterInfo, () -> gameController.hideMonsterCard(monsterInfo, null), null);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (drawReckoningPulse) {
            reckoningPulseTime += delta;
        }
        if (borderImage != null) {
            borderImage.act(delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw blurred shadow using Gaussian blur shader
        if (getDrawable() instanceof TextureRegionDrawable) {
            TextureRegionDrawable trd = (TextureRegionDrawable) getDrawable();
            ShaderProgram prevShader = batch.getShader();
            Color prevColor = batch.getColor().cpy();

            batch.setShader(BlurShadowShader.get());
            BlurShadowShader.get().setUniformf("u_texelSize",
                    1f / trd.getRegion().getTexture().getWidth(),
                    1f / trd.getRegion().getTexture().getHeight());

            batch.setColor(1f, 1f, 1f, 0.65f * getColor().a * parentAlpha);
            ((TransformDrawable) trd).draw(batch,
                    getX(), getY() + getHeight() * 0.08f,
                    getOriginX(), getOriginY(),
                    getWidth(), getHeight(),
                    getScaleX() * 1.18f, getScaleY() * 1.18f,
                    getRotation());
            batch.setColor(prevColor);
            batch.setShader(prevShader);
        }

        super.draw(batch, parentAlpha);

        if (drawReckoningPulse) {
            drawReckoningPulse(batch, parentAlpha);
        }



    }

    private void drawReckoningPulse(Batch batch, float parentAlpha) {
        if (!(getDrawable() instanceof TextureRegionDrawable)) {
            return;
        }

        float pulseProgress = (MathUtils.sin(reckoningPulseTime * RECKONING_PULSE_SPEED) + 1f) / 2f;
        float pulseAlpha = RECKONING_PULSE_MIN_ALPHA +
                (RECKONING_PULSE_MAX_ALPHA - RECKONING_PULSE_MIN_ALPHA) * pulseProgress;
        TextureRegionDrawable drawable = (TextureRegionDrawable) getDrawable();
        TransformDrawable transformDrawable = (TransformDrawable) drawable;
        Color previousColor = batch.getColor().cpy();
        batch.setColor(1f, 0.15f, 0.15f, pulseAlpha * getColor().a * parentAlpha);
        transformDrawable.draw(batch,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());
        batch.setColor(previousColor);
    }

    public void removeReckoningImage() {
        drawReckoningPulse = false;
    }

    private void addSquishStretchAnimation() {
        float baseScaleX = getScaleX();
        float baseScaleY = getScaleY();
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.scaleTo(baseScaleX + SQUISH_STRETCH_SCALE_DELTA, baseScaleY - SQUISH_STRETCH_SCALE_DELTA,
                        SQUISH_STRETCH_DURATION),
                Actions.scaleTo(baseScaleX - SQUISH_STRETCH_SCALE_DELTA, baseScaleY + SQUISH_STRETCH_SCALE_DELTA,
                        SQUISH_STRETCH_DURATION)
        )));
    }
}
