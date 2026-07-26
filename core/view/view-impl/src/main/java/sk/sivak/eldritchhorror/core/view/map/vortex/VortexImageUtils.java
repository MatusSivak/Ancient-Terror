package sk.sivak.eldritchhorror.core.view.map.vortex;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class VortexImageUtils {


    public static Completable spawnVortex(LocationId locationId) {

        VortexSpawnConfig spawnConfig = new VortexSpawnConfig();

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> {
            VortexImage vortexImage = VortexImage.build();
            float scale = 2f;
            vortexImage.setWidth(VortexImage.SIZE/scale);
            vortexImage.setHeight(VortexImage.SIZE/scale);
            vortexImage.setOrigin((VortexImage.SIZE/2f)/scale,(VortexImage.SIZE/2f)/scale);
            vortexImage.setTouchable(Touchable.disabled);
            return vortexImage;
        };

        return new Spawner().spawn(actorSpawner, spawnConfig, new VortexIdLayerResolver(locationId), locationId);
    }

    public static Completable discardVortex(LocationId locationId) {
        return new Spawner().unspawn(new VortexIdLayerResolver(locationId));
    }

    private static class VortexIdLayerResolver implements MapStage.IdLayerResolver<LocationId> {

        private LocationId locationId;

        VortexIdLayerResolver(LocationId locationId) {
            this.locationId = locationId;
        }

        @Override
        public LocationId getId() {
            return locationId;
        }

        @Override
        public Group getLayer() {
            return MapStage.getVortexLayer();
        }
    }

    private static class VortexSpawnConfig implements Spawner.SpawnConfig {


        @Override
        public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {

        }

        public VortexSpawnConfig() {
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
