package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.CAMERA_MOVE_SPEED;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper.getDistance;

public class MoveActorHelper {

    public static Completable moveActor(LocationId currentLocationId, LocationId targetLocationId, MapStage.IdLayerResolver idLayerResolver) {
        return Completable.create(onSub -> {
            Vector2 targetLocation = LocationPositionResolver.resolve(targetLocationId);
            Vector2 currentLocation = LocationPositionResolver.resolve(currentLocationId);


            List<Actor> actors = MapStage.getActor(idLayerResolver);
            Actor centerActor = actors.get(1);


            double distance = getDistance(
                    new Vector2(currentLocation.x, currentLocation.y),
                    new Vector2(targetLocation.x, targetLocation.y)
            );

            Vector2 moveByVector = new Vector2(targetLocation.x - currentLocation.x, targetLocation.y - currentLocation.y);
            if (distance > MAP_WIDTH/2f) {
                if (targetLocation.x > MAP_WIDTH/2f) { // Arkham to Sydney
                    for (Actor actor : actors) {
                        actor.setX(actor.getX() + MAP_WIDTH);
                    }
                    moveByVector = new Vector2(-currentLocation.x - MAP_WIDTH + targetLocation.x,targetLocation.y - currentLocation.y);
                } else { // Sydney to arkham
                    for (Actor actor : actors) {
                        actor.setX(actor.getX() - MAP_WIDTH);
                    }
                    moveByVector = new Vector2(targetLocation.x + MAP_WIDTH-currentLocation.x,targetLocation.y - currentLocation.y);
                }
                distance = getDistance(
                        new Vector2(centerActor.getX() - centerActor.getWidth() / 2, centerActor.getY() - centerActor.getHeight() / 2),
                        new Vector2(targetLocation.x, targetLocation.y)
                );
            }


            float speed = CAMERA_MOVE_SPEED;
            float duration = (float) Math.sqrt (distance / speed);


            for (Actor actor : actors) {
                actor.addAction(new FastForwardAction<>(Actions.moveBy(moveByVector.x, moveByVector.y, duration * 1.3f, Interpolation.sine)));
            }
            MapStage.getStage().addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.delay(duration * 0.15f),
                    Actions.run(() -> MoveCameraToLocationHelper.moveCameraToPosition(targetLocation, onSub))
            )));
        });

    }

}
