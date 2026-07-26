package sk.sivak.eldritchhorror.core.view.components.sheet.monster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java8.features.function.Consumer;
import java8.features.function.Supplier;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.GameView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MONSTER_SHEET;

public class MonsterCardEffect {

    private static Table specialTextTable;
    private static Table reckoningTextTable;
    private static Table spawnTextTable;
    private static Image blackMonsterCard;

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
                monsterCard -> specialTextTable = monsterCard.getSpecialTextTable(),
                () -> specialTextTable,
                () -> BigActorsManager.getMonsterCard().reattachSpecialTable(),
                beforeHideAction);
    }

    public static Completable highlightReckoningText(MonsterInfo monsterInfo,
                                                   GameController gameController,
                                                   Supplier<Completable> beforeHideAction) {
        return highlightSpecialOrReckoningText(monsterInfo, gameController,
                monsterCard -> reckoningTextTable = monsterCard.getReckoningTextTable(),
                () -> reckoningTextTable,
                () -> BigActorsManager.getMonsterCard().reattachReckoningTable(),
                beforeHideAction);
    }

    public static Completable highlightSpawnText(MonsterInfo monsterInfo,
                                                     GameController gameController,
                                                     Supplier<Completable> beforeHideAction) {
        return highlightSpecialOrReckoningText(monsterInfo, gameController,
                monsterCard -> spawnTextTable = monsterCard.getSpawnTextTable(),
                () -> spawnTextTable,
                () -> BigActorsManager.getMonsterCard().reattachSpawnTable(),
                beforeHideAction);
    }

    private static Completable highlightSpecialOrReckoningText(MonsterInfo monsterInfo, GameController gameController,
                                                               Consumer<MonsterCard> c1, Supplier<Table> s1,
                                                               Runnable reatttachAction,
                                                               Supplier<Completable> beforeHideAction) {
        return Completable.create(onSub -> {
            Action0 onDisplayHideAction = () -> {
                MonsterCard monsterCard = BigActorsManager.getMonsterCard();
                if (monsterCard.getStage() == null) {
                    return;
                }
                CustomAssetManager.getTextureAsync(MONSTER_SHEET).subscribe(ok -> {
                    blackMonsterCard = new Image(CustomAssetManager.getTextureRegionDrawable(MONSTER_SHEET));
                    blackMonsterCard.setTouchable(Touchable.disabled);
                    blackMonsterCard.setColor(0f, 0f, 0f, 0f);
                    blackMonsterCard.addAction(Actions.color(new Color(0f, 0f, 0f, 0.75f),0.5f));

                    blackMonsterCard.setSize(monsterCard.getWidth(), monsterCard.getHeight());
                    blackMonsterCard.setPosition(monsterCard.getX(), monsterCard.getY());
                    monsterCard.getStage().addActor(blackMonsterCard);
                });

                c1.accept(monsterCard);
                Table textTable = s1.get();
                if (textTable == null) {
                    return;
                }
                Vector2 tablePosition = textTable.localToStageCoordinates(new Vector2());
                textTable.setPosition(tablePosition.x, tablePosition.y);
                textTable.setTouchable(Touchable.disabled);
                monsterCard.getStage().addActor(textTable);
            };
            Action0 onClickAction = () -> {
                BigActorsManager.getMonsterCard().setOnDisplayHideAction(onSub::onCompleted);
                if (blackMonsterCard == null) {
                    return;
                }
                blackMonsterCard.addAction(Actions.after(Actions.sequence(
                        Actions.color(new Color(0f, 0f, 0f, 0.0f), 0.25f),
                        Actions.run(() -> {
                            reatttachAction.run();
                            beforeHideAction.get().subscribe(BigActorsManager::displayOrHideMonsterCard);
                        }),
                        Actions.removeActor()
                )));
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
