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
import java.util.ArrayList;
import java.util.Collections;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.CAMERA_MOVE_SPEED;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

public class MoveActorHelper {

    public static Completable moveActor(LocationId currentLocationId, LocationId targetLocationId, MapStage.IdLayerResolver idLayerResolver) {
        return moveActors(currentLocationId,targetLocationId,Collections.singletonList(idLayerResolver));
    }

    public static Completable moveActors(LocationId currentLocationId, LocationId targetLocationId, List<? extends MapStage.IdLayerResolver> resolvers) {
        return Completable.create(onSub -> {
            if(resolvers.isEmpty() || currentLocationId==targetLocationId) { onSub.onCompleted(); return; }
            Vector2 targetLocation = LocationPositionResolver.resolve(targetLocationId);
            Vector2 currentLocation = LocationPositionResolver.resolve(currentLocationId);


            List<Actor> actors = new ArrayList<>();
            for(MapStage.IdLayerResolver resolver:resolvers)actors.addAll(MapStage.getActor(resolver));
            Vector2 moveByVector = shortestDisplacement(currentLocation, targetLocation);
            // Rebase the repeated tokens so they finish in their canonical map copies.
            float wrapOffset = targetLocation.x - currentLocation.x - moveByVector.x;
            for (Actor actor : actors) actor.setX(actor.getX() + wrapOffset);
            double distance = moveByVector.len();
            float speed = CAMERA_MOVE_SPEED;
            float duration = (float) Math.sqrt (distance / speed);

            // The next queued move must wait for both the camera and every token.
            int[] remaining = {actors.size() + 1};
            Runnable arrived = () -> { if (--remaining[0] == 0) onSub.onCompleted(); };
            for (Actor actor : actors) {
                actor.addAction(new FastForwardAction<>(Actions.sequence(
                        Actions.moveBy(moveByVector.x, moveByVector.y, duration * 1.3f, Interpolation.sine),
                        Actions.run(arrived))));
            }
            MapStage.getStage().addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.delay(duration * 0.15f),
                    Actions.run(() -> Completable.create(camera -> MoveCameraToLocationHelper.moveCameraToPosition(targetLocation, camera))
                            .subscribe(arrived::run, onSub::onError))
            )));
        });

    }

    static Vector2 shortestDisplacement(Vector2 origin, Vector2 destination) {
        Vector2 delta = new Vector2(destination).sub(origin);
        // Only horizontal distance determines wrapping; latitude must not reverse the route.
        if (delta.x > MAP_WIDTH / 2f) delta.x -= MAP_WIDTH;
        else if (delta.x < -MAP_WIDTH / 2f) delta.x += MAP_WIDTH;
        return delta;
    }

}
