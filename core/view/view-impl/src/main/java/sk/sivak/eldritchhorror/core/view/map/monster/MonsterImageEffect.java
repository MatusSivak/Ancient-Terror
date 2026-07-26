package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

public class MonsterImageEffect {

    private MonsterImageEffect() {
    }

    public static Completable tearAppart(Actor monsterImage) {

        return Completable.create(onSub -> {
            monsterImage.setOrigin(monsterImage.getWidth() / 2, monsterImage.getHeight() / 2);
            float normalActionDuration = ViewProperties.NORMAL_ACTION_DURATION;

            monsterImage.addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.parallel(
                            Actions.rotateTo(360, normalActionDuration, Interpolation.sine),
                            Actions.moveTo(
                                    ViewProperties.VIEWPORT_WIDTH / 2f - monsterImage.getWidth() / 2,
                                    ViewProperties.VIEWPORT_HEIGHT / 2f - monsterImage.getWidth() / 2,
                                    normalActionDuration, Interpolation.sine),
                            Actions.scaleTo(4, 4,
                                    normalActionDuration, Interpolation.sine)
                    ),
                    Actions.run(() -> destroyImage(monsterImage, onSub))
            )));
        });
    }

    private static void destroyImage(Actor monsterImage, CompletableSubscriber onSub) {
        MonsterImageData monsterImageData = new MonsterImageData(monsterImage);
        Image left = new Image(TornApartMonsterImageBuilder.buildLeftTextureRegion(monsterImage));
        Image right = new Image(TornApartMonsterImageBuilder.buildRightTextureRegion(monsterImage));
        Image bottom = new Image(TornApartMonsterImageBuilder.buildBottomTextureRegion(monsterImage));

        Image[] images = new Image[]{
                left,
                right,
                bottom,
        };

        initImages(images, monsterImageData);

        monsterImage.getColor().a = 0.0f;
        float distance = 150;


        left.addAction(new FastForwardAction<>(createParallelAction(-0.87f * distance, 0.5f * distance)));
        right.addAction(new FastForwardAction<>(createParallelAction(0.87f * distance, 0.5f * distance)));
        bottom.addAction(new FastForwardAction<>(createParallelAction(0, -1 * distance)));

        monsterImage.getStage().addAction(new FastForwardAction<>(Actions.sequence(
                Actions.delay(ViewProperties.NORMAL_ACTION_DURATION),
                Actions.run(() -> {
                    monsterImage.remove();
                    left.remove();
                    right.remove();
                    bottom.remove();
                    onSub.onCompleted();
                })
        )));
    }

    private static ParallelAction createParallelAction(float xMul, float yMul) {
        float actionDuration = ViewProperties.NORMAL_ACTION_DURATION;

        return Actions.parallel(
                Actions.moveBy(xMul, yMul, actionDuration, Interpolation.sineIn),
                Actions.alpha(0, actionDuration, Interpolation.linear)
        );
    }

    private static void initImages(Image[] images, MonsterImageData monsterImageData) {
        for (Image image : images) {
            image.setScaleX(monsterImageData.beforeScaleX);
            image.setOriginX(monsterImageData.originX);
            image.setOriginY(monsterImageData.originY);
            image.setScaleY(monsterImageData.beforeScaleY);
            image.setPosition(monsterImageData.beforeX, monsterImageData.beforeY);
            image.setWidth(monsterImageData.beforeWidth);
            image.setHeight(monsterImageData.beforeHeight);
            monsterImageData.stage.addActor(image);
        }
    }

    private static class MonsterImageData {
        private float beforeX;
        private float beforeY;
        private float beforeScaleX;
        private float beforeScaleY;
        private float beforeWidth;
        private float beforeHeight;
        private Stage stage;
        private float originX;
        private float originY;

        private MonsterImageData(Actor monsterImage) {
            beforeX = monsterImage.getX();
            beforeY = monsterImage.getY();
            beforeScaleX = monsterImage.getScaleX();
            beforeScaleY = monsterImage.getScaleY();
            beforeWidth = monsterImage.getWidth();
            beforeHeight = monsterImage.getHeight();
            stage = monsterImage.getStage();
            originX = monsterImage.getOriginX();
            originY = monsterImage.getOriginY();

        }


    }
}
