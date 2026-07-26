package sk.sivak.eldritchhorror.core.view.map.redpin;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;

/**
 * @author msivak
 */
public class RedPinUtils {

    private final static float RED_PIN_TEXTURE_SCALE = 5f;

    public static Completable spawnRedPin(LocationId location) {

        Texture redPinTexture = CustomAssetManager.getTexture(CustomAssetManager.RED_PIN);
        RedPinSpawnConfig spawnConfig = new RedPinSpawnConfig();

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> {
            redPinTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Image redPinImage = new Image(redPinTexture);
            redPinImage.setTouchable(Touchable.disabled); // TODO hmm...
            redPinImage.setWidth(redPinImage.getWidth() / RED_PIN_TEXTURE_SCALE);
            redPinImage.setHeight(redPinImage.getHeight() / RED_PIN_TEXTURE_SCALE);
            redPinImage.setOrigin(113 / RED_PIN_TEXTURE_SCALE, 0);
            return redPinImage;
        };

        return new Spawner().spawn(actorSpawner, spawnConfig, new RedPinIdLayerResolver(location), location);
    }

    public static Completable discardRedPin(LocationId location) {
        return new Spawner().unspawn(new RedPinIdLayerResolver(location));
    }

    private static class RedPinIdLayerResolver implements MapStage.IdLayerResolver<LocationId> {

        private LocationId locationId;

        RedPinIdLayerResolver(LocationId locationId) {
            this.locationId = locationId;
        }

        @Override
        public LocationId getId() {
            return locationId;
        }

        @Override
        public Group getLayer() {
            return MapStage.getRedPinLayer();
        }
    }

    private static class RedPinSpawnConfig implements Spawner.SpawnConfig {

        private float offsetX;
        private float offsetY;
        private float actorRotation;


        @Override
        public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {

        }

        public RedPinSpawnConfig() {
            actorRotation = MathUtils.random(-30, 30);
            this.offsetX = 0;
            this.offsetY = 0;
        }

        @Override
        public float getActorRotation() {
            return actorRotation;
        }

        @Override
        public float getOffsetX() {
            return offsetX;
        }

        @Override
        public float getOffsetY() {
            return offsetY;
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
