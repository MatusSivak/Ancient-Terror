package sk.sivak.eldritchhorror.core.view.monster;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.controller.MonsterController;
import sk.sivak.eldritchhorror.core.view.MonsterView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.select.SelectSingleComponent;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCardEffect;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper;
import sk.sivak.eldritchhorror.core.view.map.monster.EpicMonsterImage;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterImage;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterImageEffect;
import sk.sivak.eldritchhorror.core.view.map.monster.MonsterUtils;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper.getDistance;
import static sk.sivak.eldritchhorror.core.view.map.helper.MoveActorHelper.moveActor;

public class MonsterViewImpl implements MonsterView {

    private MonsterController monsterController;

    private GameController gameController;

    @Override
    public Completable defeatMonster(MonsterInfo monsterInfo) {
        return Completable.create(onSub -> defeatMonster(monsterInfo, onSub));
    }

    @Override
    public Single<MonsterInfo> selectSingleMonster(List<? extends MonsterInfo> availableMonsters, String titleText, String hideText) {
        return Single.create(onSub -> {
            SelectSingleComponent<? extends MonsterInfo> selectSingleComponent = new SelectSingleComponent<>(availableMonsters, onSub);
            selectSingleComponent.init(hideText, titleText, alpha -> new Color(0.0f, 0.0f, 0.0f, 0.5f * alpha), PURE_WHITE_BACKGROUND);
            selectSingleComponent.show();
        });
    }

    @Override
    public Completable discardMonster(MonsterInfo input) {
        return Completable.create(onSub -> {
            InfoStage.displayText("Discarding Monster");
            MapUtils.moveCameraToLocation(input.getCurrentLocation()).subscribe(() -> {
                MonsterUtils.discardMonster(input).subscribe(onSub::onCompleted);
            });
        });
    }

    @Override
    public Completable highlightSpecialText(MonsterInfo monsterInfo) {
        return MonsterCardEffect.highlightSpecialText(monsterInfo, gameController, Completable::complete);
    }

    @Override
    public Completable highlightReckoningText(MonsterInfo monsterInfo) {
        return MonsterCardEffect.highlightReckoningText(monsterInfo, gameController, Completable::complete);
    }

    @Override
    public Completable highlightSpawnText(MonsterInfo monsterInfo) {
        return MonsterCardEffect.highlightSpawnText(monsterInfo, gameController, Completable::complete);
    }

    @Override
    public Completable restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText) {
        if (highlightSpecialText) {
            return MonsterCardEffect.highlightSpecialText(monsterInfo, gameController,
                    () -> BigActorsManager.getMonsterCard().getToughnessBar().restoreHealth(amount));
        } else if (highlightReckoningText){
            return MonsterCardEffect.highlightReckoningText(monsterInfo, gameController,
                    () -> BigActorsManager.getMonsterCard().getToughnessBar().restoreHealth(amount));
        } else {
            return Completable.create(onSub -> {
                BigActorsManager.initMonsterCard(monsterInfo, () -> {}, () -> {
                    BigActorsManager.getMonsterCard().getToughnessBar().restoreHealth(amount).subscribe(() -> {
                        BigActorsManager.getMonsterCard().setOnDisplayHideAction(onSub::onCompleted);
                        BigActorsManager.displayOrHideMonsterCard();
                    });
                });
                BigActorsManager.displayOrHideMonsterCard();
            });
        }
    }

    private void defeatMonster(MonsterInfo monsterInfo, CompletableSubscriber onSub) {

        MonsterUtils.MonsterIdLayerResolver idLayerResolver = new MonsterUtils.MonsterIdLayerResolver(monsterInfo);
        List<Actor> allMonsters = MapStage.getActor(idLayerResolver);

        if (allMonsters == null || allMonsters.isEmpty()) {
            onAmbush(monsterInfo, onSub);
            return;
        }

        int index = findIndexOfClosestActor(allMonsters);
        Actor monsterImage;
        if (monsterInfo.isEpic()) {
            monsterImage = allMonsters.get(index);
            EpicMonsterImage epicMonsterImage = (EpicMonsterImage) monsterImage;
            epicMonsterImage.clearActions();
            epicMonsterImage.removeReckoningImage();
            epicMonsterImage.clearListeners();
        } else {
            monsterImage = allMonsters.get(index);
            MonsterImage regularMonsterImage = (MonsterImage) monsterImage;
            regularMonsterImage.clearActions();
            regularMonsterImage.removeReckoningImage();
            regularMonsterImage.clearListeners();
        }

        Vector2 stageCoords = monsterImage.localToStageCoordinates(new Vector2());
        Vector2 screenCoordinates = MapStage.getStage().stageToScreenCoordinates(stageCoords);
        Vector2 infoStageCoordinates = InfoStage.getStageSafe().screenToStageCoordinates(screenCoordinates);

        MapStage.removeActor(idLayerResolver);
        monsterImage.setScale(1/ MapStage.getCamera().zoom);
        monsterImage.setPosition(
                infoStageCoordinates.x + (monsterImage.getScaleX()-1) * monsterImage.getWidth()/2f,
                infoStageCoordinates.y  + (monsterImage.getScaleY()-1) * monsterImage.getHeight()/2f);
        InfoStage.getStageSafe().addActor(monsterImage);

        MonsterImageEffect.tearAppart(monsterImage).subscribe(onSub::onCompleted);
    }

    private int findIndexOfClosestActor(List<Actor> allMonsters) {
        int index = 0;
        Float minDistanceToActor = null;
        for (int i = 0; i < allMonsters.size(); i++) {
            float distance = (float)DistanceHelper.getDistance(new Vector2(allMonsters.get(i).getX(), allMonsters.get(i).getY()), MapStage.getCamera().position);
            if (minDistanceToActor == null || distance < minDistanceToActor) {
                minDistanceToActor = distance;
                index = i;
            }
        }
        return index;
    }

    private void onAmbush(MonsterInfo monsterInfo, CompletableSubscriber onSub) {
        Texture texture = monsterInfo.isEpic() ?
                CustomAssetManager.getEpicMonsterTexture(monsterInfo.getClass().getSimpleName()) :
                CustomAssetManager.getNonEpicMonsterTexture(monsterInfo.getClass().getSimpleName());
        MonsterImage monsterImage = new MonsterImage(
                monsterInfo, gameController, true, monsterInfo.hasReckoning(), monsterInfo.getCurrentLocation(), texture);

        monsterImage.setPosition(
                ViewProperties.VIEWPORT_WIDTH/2f - monsterImage.getWidth()/2,
                ViewProperties.VIEWPORT_HEIGHT/2f - monsterImage.getHeight()/2);
        monsterImage.getColor().a = 0;
        monsterImage.setScale(0f);
        monsterImage.removeReckoningImage();

        InfoStage.addSmallActorToInfoStage(monsterImage);

        monsterImage.addAction(Actions.parallel(
                Actions.run(() -> MonsterImageEffect.tearAppart(monsterImage).subscribe(onSub::onCompleted)),
                Actions.alpha(1f, ViewProperties.NORMAL_ACTION_DURATION)
        ));
    }

    @Override
    public Completable loseHealth(int amount) {
        return BigActorsManager.getMonsterCard().loseHealth(amount);
    }

    @Override
    public Completable tearMonsterCardApart(MonsterInfo monsterInfo) {
        if (!BigActorsManager.isMonsterCardDisplayed()) {
            return Completable.create(onSub -> {
                BigActorsManager.initMonsterCard(monsterInfo, () -> {}, () -> {
                    MonsterCardEffect.tearAppart(BigActorsManager.getMonsterCard()).subscribe(onSub::onCompleted);
                });
                BigActorsManager.displayOrHideMonsterCard();
            });
        }
        return MonsterCardEffect.tearAppart(BigActorsManager.getMonsterCard());
    }

    @Override
    public Completable tearMysteryCardApart(MysteryCardInfo currentMysteryCard) {
        return Completable.create(onSub -> {
            MonsterCardEffect.tearAppart(BigActorsManager.getMysteryCard()).subscribe(onSub::onCompleted);
        });
    }

    @Override
    public Completable tearRumorCardApart() {
        return Completable.create(onSub -> {
            MonsterCardEffect.tearAppart(BigActorsManager.getRumorCard()).subscribe(onSub::onCompleted);
        });
    }

    public void setMonsterController(MonsterController monsterController) {
        this.monsterController = monsterController;
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public Completable moveMonster(MonsterInfo monsterInfo, LocationId targetLocationId) {
        return moveActor(monsterInfo.getCurrentLocation(), targetLocationId, new MonsterUtils.MonsterIdLayerResolver(monsterInfo));
    }
}
