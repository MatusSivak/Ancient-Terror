package sk.sivak.eldritchhorror.core.view.map.storm;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

public class StormImageUtils {


    public static Completable spawnStorm(String stormId, LocationId locationId) {
        return spawnStorm(stormId, locationId, null);
    }

    public static Completable spawnStorm(String stormId, LocationId locationId, Runnable onContact) {
        return spawnStorm(stormId, locationId, onContact, null);
    }

    public static Completable spawnStorm(String stormId, LocationId locationId, Runnable onContact, Runnable onClick) {

        StormSpawnConfig spawnConfig = new StormSpawnConfig();

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> {
            StormImage stormImage = new StormImage();
            float scale = 1.75f;
            stormImage.setWidth(400/scale);
            stormImage.setHeight(400/scale);
            stormImage.setOrigin(200/scale,(200)/scale);
            stormImage.setTouchable(Touchable.disabled);
            if (onClick != null) ButtonUtils.addClickListener(stormImage, onClick);
            return stormImage;
        };

        return new Spawner().spawn(actorSpawner, spawnConfig, new StormIdLayerResolver(stormId), locationId, onContact)
                .doOnCompleted(() -> {
                    if (onClick != null) for (Actor actor : MapStage.getActor(new StormIdLayerResolver(stormId)))
                        actor.setTouchable(Touchable.enabled);
                });
    }

    public static Completable discardStorm(String stormId) {
        return new Spawner().unspawn(new StormIdLayerResolver(stormId));
    }

    private static class StormIdLayerResolver implements MapStage.IdLayerResolver<String> {

        private String stormId;

        StormIdLayerResolver(String stormId) {
            this.stormId = stormId;
        }

        @Override
        public String getId() {
            return stormId;
        }

        @Override
        public Group getLayer() {
            return MapStage.getStormLayer();
        }
    }

    private static class StormSpawnConfig implements Spawner.SpawnConfig {

        @Override public Spawner.SpawnStyle getSpawnStyle() { return Spawner.SpawnStyle.STORM; }


        @Override
        public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {

        }

        public StormSpawnConfig() {
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
        public void postSpawnConfig(Actor leftActor, Actor centerActor, Actor rigthActor) {

        }
    }
}
