package sk.sivak.eldritchhorror.core.view.map.clue;

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
public class ClueUtils {

    private final static float CLUE_TEXTURE_SCALE = 7;

    public static Completable spawnClue(LocationId spawnLocationId, LocationId currentLocationId) {

        Texture clueTexture = CustomAssetManager.getTexture(CustomAssetManager.CLUE_TOKEN);
        ClueSpawnConfig spawnConfig = new ClueSpawnConfig(clueTexture.getWidth() / CLUE_TEXTURE_SCALE, clueTexture.getHeight() / CLUE_TEXTURE_SCALE);

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> {
            clueTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Image clueImage = new Image(clueTexture);
            clueImage.setTouchable(Touchable.disabled);
            clueImage.setWidth(clueImage.getWidth() / CLUE_TEXTURE_SCALE);
            clueImage.setHeight(clueImage.getHeight() / CLUE_TEXTURE_SCALE);
            clueImage.setOrigin(clueImage.getWidth() / 2, clueImage.getHeight() / 2);
            return clueImage;
        };

        return new Spawner().spawn(actorSpawner, spawnConfig, new ClueIdLayerResolver(spawnLocationId), currentLocationId);
    }

    public static Completable discardClue(LocationId spawnLocationId) {
        return new Spawner().unspawn(new ClueIdLayerResolver(spawnLocationId));
    }

    public static class ClueIdLayerResolver implements MapStage.IdLayerResolver<LocationId> {

        private LocationId spawnLocationId;

        public ClueIdLayerResolver(LocationId spawnLocationId) {
            this.spawnLocationId = spawnLocationId;
        }

        @Override
        public LocationId getId() {
            return spawnLocationId;
        }

        @Override
        public Group getLayer() {
            return MapStage.getClueLayer();
        }
    }

    private static class ClueSpawnConfig implements Spawner.SpawnConfig {

        private float offsetRotation;
        private float positionRotation;
        private float actorRotation;
        private float actorWidth;
        private float actorHeight;

        public ClueSpawnConfig(float actorWidth, float actorHeight) {
            actorRotation = MathUtils.random(360f);
            positionRotation = MathUtils.random(360f);
            offsetRotation = MathUtils.random();
            this.actorWidth = actorWidth;
            this.actorHeight = actorHeight;
        }

        @Override
        public float getActorRotation() {
            return actorRotation;
        }

        @Override
        public float getOffsetX() {
            return MathUtils.cosDeg(positionRotation) * actorWidth / 2 * offsetRotation;
        }

        @Override
        public float getOffsetY() {
            return MathUtils.sinDeg(positionRotation) * actorHeight / 2 * offsetRotation;
        }

        @Override
        public float getFinalAlpha() {
            return 1;
        }

        @Override
        public void postSpawnConfig(Actor leftActor, Actor centerActor, Actor rigthActor) {

        }

        @Override
        public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rightActor) {

        }
    }

}
