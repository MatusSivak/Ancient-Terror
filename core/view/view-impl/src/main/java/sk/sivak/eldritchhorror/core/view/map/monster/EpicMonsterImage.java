package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
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

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.EPIC_MONSTER_SIZE;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FADING_EFFECT_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MONSTER_FILTER;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

/**
 * @author msivak
 */
public class EpicMonsterImage extends Image {
    private final Vector2 position;
    private Texture monsterFilter;
    private MonsterInfo monsterInfo;
    private GameController gameController;
    private final boolean hasReckoning;
    private Image reckoningImage;
    private Image borderImage;
    private boolean drawReckoningImage = false;

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
        reckoningImage = createReckoningImage();

        CustomAssetManager.getTextureAsync("monster/epic/epic_monster_border_sheet.png").subscribe(xxx -> {
            borderImage = createBorderImage();
        });

        addClickListener(this, this::displayMonsterCard);
        CustomAssetManager.getTextureAsync(CustomAssetManager.MONSTER_FILTER).subscribe(monsterFilter -> {
            this.monsterFilter = monsterFilter;
        });
    }

    private void displayMonsterCard() {
        gameController.displayMonsterCard(monsterInfo, () -> gameController.hideMonsterCard(monsterInfo, null), null);
    }

    private Image createReckoningImage() {
        Image image = new Image(CustomAssetManager.getTexture(CustomAssetManager.RECKONING)) {

            @Override
            public float getX() {
                return EpicMonsterImage.this.getX();
            }

            @Override
            public float getY() {
                return EpicMonsterImage.this.getY();
            }

            @Override
            public float getScaleX() {
                return EpicMonsterImage.this.getScaleX();
            }

            @Override
            public float getScaleY() {
                return EpicMonsterImage.this.getScaleY();
            }
        };

        image.setWidth(getWidth());
        image.setHeight(getHeight());
        image.getColor().a = 0f;
        image.setOrigin(getWidth() / 2, getHeight() / 2);

        if (hasReckoning) {
            addFadeInOutActions(image);
            drawReckoningImage = true;
        }
        return image;
    }

    private Image createBorderImage() {
        Texture texture = CustomAssetManager.getTexture("monster/epic/epic_monster_border_sheet.png");
        List<TextureRegion> textureRegions = new LinkedList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 10; x++) {
                textureRegions.add(new TextureRegion(texture, 800 * x, 600*y, 800, 600));
            }
        }

        float borderScale = 0.215f;
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
                getColor().a *= EpicMonsterImage.this.getColor().a;
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

        image.addAction(Actions.sequence(Actions.delay(1), fadeInAction, fadeOutAction, Actions.run(() -> {
            addFadeInOutActions(image);
        })));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        reckoningImage.act(delta);
        if (borderImage != null) {
            borderImage.act(delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (drawReckoningImage) {
            reckoningImage.draw(batch, parentAlpha);
        }

        batch.setColor(new Color(1f, 1f, 1f, 0.5f * getColor().a * parentAlpha));
        if (monsterFilter != null) {
            batch.draw(monsterFilter,
                    getX() + (1 - getScaleX()) * getWidth() / 2,
                    getY() + (1 - getScaleY()) * getHeight() / 2,
                    getWidth() * getScaleX(),
                    getHeight() * getScaleY());
        }
        if (borderImage != null) {
            borderImage.draw(batch, parentAlpha);
        }

    }

    public void removeReckoningImage() {
        drawReckoningImage = false;
    }
}
