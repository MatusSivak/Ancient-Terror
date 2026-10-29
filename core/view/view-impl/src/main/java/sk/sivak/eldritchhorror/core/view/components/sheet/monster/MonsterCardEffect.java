package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java8.features.function.Function;
import java8.features.function.Supplier;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

public class MonsterCardEffect {

    private MonsterCardEffect() {
    }

    public static Completable tearAppart(Actor someActor) {

        MonsterCardData monsterCardData = new MonsterCardData(someActor);

        Image bottomLeft = new Image(TornApartMonsterCardBuilder.buildBottomLeftTextureRegion(someActor));
        Image bottomRight = new Image(TornApartMonsterCardBuilder.buildBottomRightTextureRegion(someActor));
        Image topLeft = new Image(TornApartMonsterCardBuilder.buildTopLeftTextureRegion(someActor));
        Image topRight = new Image(TornApartMonsterCardBuilder.buildTopRightTextureRegion(someActor));

        Image[] images = new Image[]{
                bottomLeft,
                bottomRight,
                topLeft,
                topRight
        };

        initImages(images, monsterCardData);

        bottomLeft.setOrigin(0, 0);
        bottomRight.setOrigin(682, 0);
        topLeft.setOrigin(0, 454);
        topRight.setOrigin(682, 454);

        if (someActor instanceof MonsterCard) {
            BigActorsManager.displayOrHideMonsterCard();
            BigActorsManager.unlock(BigActorsManager.BigActorKey.MONSTER_CARD);
        } else if (someActor instanceof MysteryCard) {
//            BigActorsManager.displayOrHideCurrentMystery();
//            BigActorsManager.unlock(BigActorsManager.BigActorKey.MYSTERY_CARD);
        } else if (someActor instanceof RumorCard) {
            BigActorsManager.displayOrHideRumor();
            BigActorsManager.unlock(BigActorsManager.BigActorKey.RUMOR_CARD);
        }
        someActor.getColor().a = 0.0f;

        bottomLeft.addAction(new FastForwardAction<>(createParallelAction(-1, -1, -1)));
        bottomRight.addAction(new FastForwardAction<>(createParallelAction(1, -1, 1)));
        topLeft.addAction(new FastForwardAction<>(createParallelAction(-1, 1, 1)));
        topRight.addAction(new FastForwardAction<>(createParallelAction(1, 1, -1)));

        return Completable.create(onSub -> {
            InfoStage.addActionToInfoStage(new FastForwardAction<>(Actions.sequence(
                    Actions.delay(ViewProperties.NORMAL_ACTION_DURATION),
                    Actions.run(() -> {
                        bottomLeft.remove();
                        topLeft.remove();
                        bottomRight.remove();
                        topRight.remove();
                        onSub.onCompleted();
                    })
            )));
        });
    }

    private static ParallelAction createParallelAction(int xMul, int yMul, int rotationMul) {
        float actionDuration = ViewProperties.NORMAL_ACTION_DURATION;

        return Actions.parallel(
                Actions.moveBy(200 * xMul, 50 * yMul, actionDuration, Interpolation.sineIn),
                Actions.rotateBy(60 * rotationMul, actionDuration, Interpolation.sineIn),
                Actions.alpha(0, actionDuration, Interpolation.sineIn)
        );
    }

    private static void initImages(Image[] images, MonsterCardData monsterCardData) {
        for (int i = 0; i < images.length; i++) {
            Image image = images[i];
            image.setScaleX(monsterCardData.beforeScaleX);
            image.setScaleY(monsterCardData.beforeScaleY);
            image.setPosition(monsterCardData.beforeX, monsterCardData.beforeY);
            image.setWidth(monsterCardData.beforeWidth);
            image.setHeight(monsterCardData.beforeHeight);
            image.setOriginX(monsterCardData.originX);
            image.setOriginY(monsterCardData.originY);
            monsterCardData.stage.getRoot().addActorAt(i, image);
        }
    }

    private static class MonsterCardData {
        private float beforeX;
        private float beforeY;
        private float beforeScaleX;
        private float beforeScaleY;
        private float beforeWidth;
        private float beforeHeight;
        private Stage stage;
        private float originX;
        private float originY;

        private MonsterCardData(Actor monsterCard) {
            beforeX = monsterCard.getX();
            beforeY = monsterCard.getY();
            beforeScaleX = monsterCard.getScaleX();
            beforeScaleY = monsterCard.getScaleY();
            beforeWidth = monsterCard.getWidth();
            beforeHeight = monsterCard.getHeight();
            stage = monsterCard.getStage();
            originX = monsterCard.getOriginX();
            originY = monsterCard.getOriginY();

        }
    }

    public static Completable highlightSpecialText(MonsterInfo monsterInfo,
                                                   GameController gameController,
                                                   Supplier<Completable> beforeHideAction) {
        return highlightSpecialOrReckoningText(monsterInfo, gameController,
                MonsterCard::getSpecialTextTable,
                beforeHideAction);
    }

    public static Completable highlightReckoningText(MonsterInfo monsterInfo,
                                                   GameController gameController,
                                                   Supplier<Completable> beforeHideAction) {
        return highlightSpecialOrReckoningText(monsterInfo, gameController,
                MonsterCard::getReckoningTextTable,
                beforeHideAction);
    }

    public static Completable highlightSpawnText(MonsterInfo monsterInfo,
                                                     GameController gameController,
                                                     Supplier<Completable> beforeHideAction) {
        return highlightSpecialOrReckoningText(monsterInfo, gameController,
                MonsterCard::getSpawnTextTable,
                beforeHideAction);
    }

    private static Completable highlightSpecialOrReckoningText(MonsterInfo monsterInfo, GameController gameController,
                                                               Function<MonsterCard, Table> ruleSelector,
                                                               Supplier<Completable> beforeHideAction) {
        return Completable.create(onSub -> {
            MonsterAbilityHighlight[] highlight = {null};
            boolean[] dismissing = {false};
            Action0 onDisplayHideAction = () -> {
                MonsterCard monsterCard = BigActorsManager.getMonsterCard();
                if (monsterCard.getStage() == null) {
                    return;
                }
                Table textTable = ruleSelector.apply(monsterCard);
                if (textTable == null) {
                    return;
                }
                highlight[0] = new MonsterAbilityHighlight(monsterCard, textTable);
                highlight[0].getColor().a = 0;
                highlight[0].addAction(Actions.fadeIn(.5f));
            };
            Action0 onClickAction = () -> {
                if (dismissing[0]) return;
                dismissing[0] = true;
                BigActorsManager.getMonsterCard().setOnDisplayHideAction(onSub::onCompleted);
                Runnable hide = () -> beforeHideAction.get().subscribe(BigActorsManager::displayOrHideMonsterCard, onSub::onError);
                if (highlight[0] == null) hide.run();
                else {
                    highlight[0].clearActions();
                    highlight[0].addAction(Actions.sequence(Actions.fadeOut(.25f), Actions.run(() -> {
                        highlight[0].remove();
                        hide.run();
                    })));
                }
            };
            if (BigActorsManager.isMonsterCardDisplayed()) {
                onDisplayHideAction.call();
                ButtonUtils.addClickListener(BigActorsManager.getMonsterCard(), onClickAction::call);
                return;
            }
            gameController.displayMonsterCard(monsterInfo, onClickAction, onDisplayHideAction);
        });
    }
}
