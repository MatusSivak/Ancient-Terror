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
        setWidth(EPIC_MONSTER_SIZE);
        setHeight(EPIC_MONSTER_SIZE);
        setOrigin(getWidth() / 2, getHeight() / 2);
        if (hasReckoning) {
            drawReckoningPulse = true;
        }

        CustomAssetManager.getTextureAsync("monster/epic/epic_monster_border_sheet.png").subscribe(xxx -> {
            borderImage = createBorderImage();
        });

        addClickListener(this, this::displayMonsterCard);
    }

    private void displayMonsterCard() {
        gameController.displayMonsterCard(monsterInfo, () -> gameController.hideMonsterCard(monsterInfo, null), null);
    }

    private Image createBorderImage() {
        Texture texture = CustomAssetManager.getTexture("monster/epic/epic_monster_border_sheet.png");
        List<TextureRegion> textureRegions = new LinkedList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 10; x++) {
                textureRegions.add(new TextureRegion(texture, 800 * x, 600*y, 800, 600));
            }
        }

        float borderScale = 0.4f;
        AnimatedImage animatedBorder = new AnimatedImage(
                new Animation<>(0.04f, textureRegions.toArray(new TextureRegion[0]))) {

            @Override
            public float getX() {
                return EpicMonsterImage.this.getX() + EpicMonsterImage.this.getWidth()/2f
                        - (EpicMonsterImage.this.getWidth()/EPIC_MONSTER_SIZE * 800 * borderScale)/2;

            }

            @Override
            public float getY() {
                return EpicMonsterImage.this.getY() + EpicMonsterImage.this.getHeight()/2f
                        - (EpicMonsterImage.this.getHeight()/EPIC_MONSTER_SIZE * 600 * borderScale) /2;
            }

            @Override
            public float getScaleX() {
                return EpicMonsterImage.this.getScaleX();
            }

            @Override
            public float getScaleY() {
                return EpicMonsterImage.this.getScaleY();
            }

            @Override
            public float getWidth() {
                return 800 * borderScale * EpicMonsterImage.this.getWidth()/EPIC_MONSTER_SIZE;
            }

            @Override
            public float getHeight() {
                return 600 * borderScale * EpicMonsterImage.this.getHeight()/EPIC_MONSTER_SIZE;
            }

            @Override
            public float getOriginX() {
                return getWidth() / 2;
            }

            @Override
            public float getOriginY() {
                return getHeight() / 2;
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                float alpha = getColor().a;
                getColor().a *= EpicMonsterImage.this.getColor().a * 0.5f;
                super.draw(batch, parentAlpha);
                getColor().a = alpha;
            }
        };

        animatedBorder.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(-1f)));
        if (monsterInfo.getName().equals("Cthulhu") || monsterInfo.getName().equals("Shub-Niggurath")) {
            animatedBorder.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.color(new Color(0x9b0629ff), 2f),
                    Actions.color(new Color(0x430b66ff), 2f)
            )));
            animatedBorder.setColor(new Color(0x430b66ff));
        } else {
            animatedBorder.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.color(new Color(0x2f9ac4ff), 2f),
                    Actions.color(new Color(0x1d3784ff), 2f)
            )));
            animatedBorder.setColor(new Color(0x1d3784ff));
        }

        return animatedBorder;
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

        if (borderImage != null) {
            borderImage.draw(batch, parentAlpha);
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
}
