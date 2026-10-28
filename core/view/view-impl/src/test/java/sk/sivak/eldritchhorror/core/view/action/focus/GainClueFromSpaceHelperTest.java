package sk.sivak.eldritchhorror.core.view.action.focus;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

public class GainClueFromSpaceHelperTest {
    @Test public void space19UsesVisibleLeftCopyWhenResearchCameraWraps() {
        Vector2 location = LocationPositionResolver.resolve(LocationId.SPACE_19);
        List<Actor> copies = copies(location);
        Vector3 camera = new Vector3((location.x + 310) % MAP_WIDTH, location.y + 150, 0);
        assertSame(copies.get(0), GainClueFromSpaceHelper.nearestClueCopy(copies, camera));
    }

    @Test public void centeredCameraUsesCanonicalCopyAndRightEdgeUsesRightCopy() {
        Vector2 location = LocationPositionResolver.resolve(LocationId.SPACE_1);
        List<Actor> copies = copies(location);
        assertSame(copies.get(1), GainClueFromSpaceHelper.nearestClueCopy(copies,
                new Vector3(location.x, location.y, 0)));
        assertSame(copies.get(2), GainClueFromSpaceHelper.nearestClueCopy(copies,
                new Vector3(MAP_WIDTH - 20, location.y, 0)));
    }

    @Test public void selectionUsesStageCoordinatesAndDoesNotChangeTheTokens() {
        Vector2 location = LocationPositionResolver.resolve(LocationId.SPACE_19);
        List<Actor> copies = copies(location);
        Group layer = new Group(); layer.setPosition(-MAP_WIDTH, 25);
        for (Actor actor : copies) layer.addActor(actor);
        Collections.reverse(copies);
        Actor nearest = GainClueFromSpaceHelper.nearestClueCopy(copies, new Vector3(location.x, location.y + 25, 0));
        assertSame(copies.get(0), nearest);
        assertEquals(35, nearest.getRotation(), 0);
        assertEquals(1.2f, nearest.getScaleX(), 0);
        assertSame(layer, nearest.getParent());
    }

    private List<Actor> copies(Vector2 location) {
        List<Actor> result = new ArrayList<>();
        for (int wrap = -1; wrap <= 1; wrap++) {
            Actor actor = new Actor();
            actor.setBounds(location.x + wrap * MAP_WIDTH - 20, location.y - 20, 40, 40);
            actor.setOrigin(20, 20); actor.setScale(1.2f); actor.setRotation(35);
            result.add(actor);
        }
        return result;
    }
}
