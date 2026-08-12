package sk.sivak.eldritchhorror.core.view.map.gate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.Spawner;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/**
 * @author msivak
 */
public class GateUtils {

    public static Completable spawnGate(GateInfo gateInfo, OmenColor omenColor) {
        Spawner.SpawnConfigImpl spawnConfig = new Spawner.SpawnConfigImpl() {
            @Override
            public void preSpawnConfig(Actor leftActor, Actor centerActor, Actor rigthActor) {
                leftActor.addAction(Actions.moveBy(0, 0, NORMAL_ACTION_DURATION));
                centerActor.addAction(Actions.moveBy(0, 0, NORMAL_ACTION_DURATION));
                rigthActor.addAction(Actions.moveBy(0, 0, NORMAL_ACTION_DURATION));
            }
        };
        spawnConfig.setFinalAlpha(1.0f);

        Spawner.ActorSpawner actorSpawner = (boolean isCenter) -> {
            NewGateAnimatedImage gateAnimatedImage = new NewGateAnimatedImage(gateInfo.getGateColor(), omenColor);
            gateAnimatedImage.setSize(135, 135);
            gateAnimatedImage.setOrigin(gateAnimatedImage.getWidth()/2f, gateAnimatedImage.getHeight()/2);
            return gateAnimatedImage;
        };

        return new Spawner().spawn(actorSpawner, spawnConfig, new GateIdLayerResolver(gateInfo.getLocationId()), gateInfo.getLocationId());

    }

    public static class GateIdLayerResolver implements MapStage.IdLayerResolver<LocationId> {

        private LocationId locationId;

        public GateIdLayerResolver(LocationId locationId) {
            this.locationId = locationId;
        }

        @Override
        public LocationId getId() {
            return locationId;
        }

        @Override
        public Group getLayer() {
            return MapStage.getGateLayer();
        }
    }
}
