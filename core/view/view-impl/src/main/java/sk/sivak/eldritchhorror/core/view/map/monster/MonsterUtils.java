package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper.getDistance;

/**
 * @author msivak
 */
public class MonsterUtils {

    public static Completable spawnMonster(MonsterInfo monsterInfo, GameController gameController) {
        return Completable.create(onSub -> {
            String textureName;
            String monsterId = monsterInfo.getClass().getSimpleName();
            String fileName = monsterId.substring(0, monsterId.length() - "Monster".length());
            if (monsterInfo.isEpic()) {
                textureName = "monster/epic/" + fileName + ".png";
            } else {
                textureName = "monster/" + fileName + ".png";
            }
            CustomAssetManager.getTextureAsync(textureName).subscribe(texture -> {

                LocationId location = monsterInfo.getCurrentLocation();
                boolean hasReckoning = monsterInfo.hasReckoning();

                Spawner.ActorSpawner actorSpawner;
                MonsterSpawnConfig spawnConfig;
                if (monsterInfo.isEpic()) {
                    spawnConfig = new MonsterSpawnConfig(EPIC_MONSTER_SIZE, EPIC_MONSTER_SIZE);
                    actorSpawner = (boolean isCenter) -> new EpicMonsterImage(monsterInfo, gameController, isCenter, hasReckoning, location, texture);
                } else {
                    spawnConfig = new MonsterSpawnConfig(MONSTER_SIZE, MONSTER_SIZE);
                    actorSpawner = (boolean isCenter) -> new MonsterImage(monsterInfo, gameController, isCenter, hasReckoning, location, texture);
                }

                new Spawner().spawn(actorSpawner, spawnConfig, new MonsterIdLayerResolver(monsterInfo), location).subscribe(onSub::onCompleted);

            });
        });
    }

    public static Completable discardMonster(MonsterInfo input) {
        return new Spawner().unspawn(new MonsterIdLayerResolver(input));

    }

    public static class MonsterIdLayerResolver implements MapStage.IdLayerResolver<MonsterInfo> {

        private MonsterInfo monsterInfo;

        public MonsterIdLayerResolver(MonsterInfo monsterInfo) {
            this.monsterInfo = monsterInfo;
        }

        @Override
        public MonsterInfo getId() {
            return monsterInfo;
        }

        @Override
        public Group getLayer() {
            return monsterInfo.isEpic() ? MapStage.getEpicMonsterLayer() :
                    (monsterInfo.hasReckoning() ? MapStage.getReckoningMonsterLayer() : MapStage.getMonsterLayer());
        }
    }

    private static class MonsterSpawnConfig implements Spawner.SpawnConfig<Actor> {

        public MonsterSpawnConfig(float actorWidth, float actorHeight) {
        }

        @Override
        public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {

        }

        @Override
        public float getActorRotation() {
            return 0;
        }

        @Override
        public float getOffsetX() {
            return 0;
        }

        @Override
        public float getOffsetY() {
            return 0;
        }

        @Override
        public float getFinalAlpha() {
            return 1;
        }

        @Override
        public void postSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {
            postSpawnConfig(leftActor, centerActor, rightActor, 0, 0);
        }

        private void postSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor, float offsetX, float offsetY) {
            float movementDirection = MathUtils.random(360);
            float movementDistanceWeight = MathUtils.random(1f);
            float diffX = MathUtils.cosDeg(movementDirection) * MONSTER_SIZE * 0.45f * movementDistanceWeight;
            float diffY = MathUtils.sinDeg(movementDirection) * MONSTER_SIZE * 0.45f * movementDistanceWeight;

            float distance = (float) getDistance(new Vector2(offsetX, offsetY), new Vector2(diffX, diffY));
            float duration = distance / MONSTER_SPEED;

            leftActor.addAction(createMoveByAction(duration, diffX - offsetX, diffY - offsetY));
            rightActor.addAction(createMoveByAction(duration, diffX - offsetX, diffY - offsetY));
            centerActor.addAction(Actions.sequence(
                    createMoveByAction(duration, diffX - offsetX, diffY - offsetY),
                    Actions.run(() -> {
                        postSpawnConfig(leftActor, centerActor, rightActor, diffX, diffY);
                    })
            ));
        }

        private Action createMoveByAction(float duration, float diffX, float diffY) {
            MoveByAction action = new MoveByAction();
            action.setDuration(duration);
            action.setAmount(diffX, diffY);
            action.setInterpolation(Interpolation.sine);
            return action;
        }
    }
}
