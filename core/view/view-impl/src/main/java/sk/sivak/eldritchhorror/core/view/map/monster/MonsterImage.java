package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCardEffect;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.shader.BlurShadowShader;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FADING_EFFECT_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MONSTER_SIZE;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

/**
 * @author msivak
 */
public class MonsterImage extends Image {

    private static final float RECKONING_ICON_SIZE_MULTIPLIER = 0.35f;

    private final LocationId location;
    private final boolean hasReckoning;
    private MonsterInfo monsterInfo;
    private GameController gameController;
    private final boolean isCenter;
    private Image reckoningImage;

    private boolean drawReckoningImage = false;

    public MonsterImage(MonsterInfo monsterInfo, GameController gameController, boolean isCenter, boolean hasReckoning, LocationId location, Texture texture) {
        super(texture);
        this.monsterInfo = monsterInfo;
        this.gameController = gameController;
        this.isCenter = isCenter;
        this.hasReckoning = hasReckoning;
        this.location = location;
        setWidth(MONSTER_SIZE);
        setHeight(MONSTER_SIZE);
        setOrigin(getWidth() / 2, getHeight() / 2);
        reckoningImage = createReckoningImage();
        addClickListener(this, this::displayMonsterCard);
    }

    private void displayMonsterCard() {
        gameController.displayMonsterCard(monsterInfo, () -> gameController.hideMonsterCard(monsterInfo, null), null);
    }

    private Image createReckoningImage() {
        Image image = new Image(CustomAssetManager.getTexture(CustomAssetManager.RECKONING)) {

            @Override
            public float getX() {
                return MonsterImage.this.getX();
            }

            @Override
            public float getY() {
                return MonsterImage.this.getY();
            }

            @Override
            public float getScaleX() {
                return MonsterImage.this.getScaleX();
            }

            @Override
            public float getScaleY() {
                return MonsterImage.this.getScaleY();
            }
        };

        image.setWidth(getWidth());
        image.setHeight(getHeight());
        image.getColor().a = 0f;
        image.setOrigin(getWidth() / 2, getHeight() / 2);

        if (hasReckoning) {
            drawReckoningImage = true;
            addFadeInOutActions(image);
        }
        return image;
    }

    private void addFadeInOutActions(Image image) {
        AlphaAction fadeInAction = new AlphaAction();
        fadeInAction.setAlpha(0.5f);
        fadeInAction.setDuration(FADING_EFFECT_DURATION * 2);
        fadeInAction.setActor(image);
        fadeInAction.setInterpolation(Interpolation.sine);

        AlphaAction fadeOutAction = new AlphaAction();
        fadeOutAction.setAlpha(0.0f);
        fadeOutAction.setDuration(FADING_EFFECT_DURATION * 2);
        fadeOutAction.setActor(image);
        fadeOutAction.setInterpolation(Interpolation.sine);

        image.addAction(Actions.sequence(Actions.delay(1.0f), fadeInAction, fadeOutAction, Actions.run(() -> {
            addFadeInOutActions(image);
        })));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        reckoningImage.act(delta);

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

            batch.setColor(1f, 1f, 1f, 0.75f * getColor().a * parentAlpha);
            ((TransformDrawable) trd).draw(batch,
                    getX(), getY() + getHeight() * 0.05f,
                    getOriginX(), getOriginY(),
                    getWidth(), getHeight(),
                    getScaleX() * 1.05f, getScaleY() * 1.05f,
                    getRotation());
            batch.setColor(prevColor);
            batch.setShader(prevShader);
        }

        super.draw(batch, parentAlpha);

        if (drawReckoningImage) {
            drawReckoningImage(batch, parentAlpha);
        }
    }

    private void drawReckoningImage(Batch batch, float parentAlpha) {
        if (!(reckoningImage.getDrawable() instanceof TextureRegionDrawable)) {
            return;
        }

        OrthographicCamera camera = MapStage.getCamera();
        float drawWidth = getWidth() * getScaleX() * camera.zoom * RECKONING_ICON_SIZE_MULTIPLIER;
        float drawHeight = getHeight() * getScaleY() * camera.zoom * RECKONING_ICON_SIZE_MULTIPLIER;
        Vector2 center = localToStageCoordinates(new Vector2(getWidth() / 2f, getHeight() / 2f));
        float x = center.x - drawWidth / 2f;
        float y = center.y - drawHeight / 2f;

        TextureRegionDrawable drawable = (TextureRegionDrawable) reckoningImage.getDrawable();
        Color previousColor = new Color(batch.getColor());
        Color iconColor = reckoningImage.getColor();
        batch.setColor(iconColor.r, iconColor.g, iconColor.b, parentAlpha * iconColor.a);
        batch.draw(drawable.getRegion(), x, y, drawWidth, drawHeight);
        batch.setColor(previousColor);
    }

    public void removeReckoningImage() {
        drawReckoningImage = false;
    }
}
