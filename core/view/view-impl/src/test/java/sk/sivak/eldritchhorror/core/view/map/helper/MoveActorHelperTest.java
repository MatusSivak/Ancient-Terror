package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

public class MoveActorHelperTest {
    @Test public void buenosAiresToHimalayasUsesSameDirectionAsNearestCameraCopy() {
        Vector2 origin = LocationPositionResolver.resolve(LocationId.BUENOS_AIRES);
        Vector2 target = LocationPositionResolver.resolve(LocationId.THE_HIMALAYAS);
        Vector2 move = MoveActorHelper.shortestDisplacement(origin, target);
        assertTrue("Report crosses the old Euclidean threshold", origin.dst(target) > MAP_WIDTH / 2f);
        assertEquals(target.x - origin.x, move.x, 0.01f);
        assertEquals(target.y - origin.y, move.y, 0.01f);
    }

    @Test public void everyMapRouteUsesTheShortestHorizontalCopyAndKeepsLatitude() {
        for (LocationId from : LocationId.values()) for (LocationId to : LocationId.values()) {
            Vector2 origin = LocationPositionResolver.resolve(from), target = LocationPositionResolver.resolve(to);
            if (origin == null || target == null) continue;
            Vector2 move = MoveActorHelper.shortestDisplacement(origin, target);
            float direct = target.x - origin.x;
            float shortest = Math.min(Math.abs(direct), Math.min(Math.abs(direct - MAP_WIDTH), Math.abs(direct + MAP_WIDTH)));
            assertEquals(from + " to " + to, shortest, Math.abs(move.x), 0.01f);
            assertEquals(target.y - origin.y, move.y, 0.01f);
        }
    }
}
