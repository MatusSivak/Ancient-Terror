package sk.sivak.eldritchhorror.core.view.map.expedition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;

/**
 * @author msivak
 */
public class ExpeditionUtils {
    private final static String ID = "expedition";

    public static Completable spawnExpedition(LocationId location) {
        Texture compassTexture = CustomAssetManager.getTexture(CustomAssetManager.COMPASS);
        Spawner.SpawnConfigImpl spawnConfig = new Spawner.SpawnConfigImpl(
                0,
                0,
                0, 1f);

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> {
            Image compassImage = new RotatingImage(compassTexture);
            compassImage.setTouchable(Touchable.disabled);
            compassImage.setWidth(compassImage.getWidth() / 5);
            compassImage.setHeight(compassImage.getHeight() / 5);
            compassImage.setOrigin(compassImage.getWidth() / 2, compassImage.getHeight() / 2);
            return compassImage;
        };


        return new Spawner().spawn(actorSpawner, spawnConfig, new ExpeditionIdLayerResolver(), location);
    }

    public static Completable discardExpeditionToken() {
        return new Spawner().unspawn(new ExpeditionIdLayerResolver());
    }

    private static class ExpeditionIdLayerResolver implements MapStage.IdLayerResolver<String> {

        @Override
        public String getId() {
            return ID;
        }

        @Override
        public Group getLayer() {
            return MapStage.getExpeditionLayer();
        }
    }

    private static class RotatingImage extends Image {

        private static final float DIRECTION = MathUtils.random(0);

        RotatingImage(Texture texture) {
            super(texture);
            addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                    Actions.rotateTo(DIRECTION - 15, 1f, Interpolation.sine),
                    Actions.rotateTo(DIRECTION + 15, 1f, Interpolation.sine)
            )));
        }

    }

}
