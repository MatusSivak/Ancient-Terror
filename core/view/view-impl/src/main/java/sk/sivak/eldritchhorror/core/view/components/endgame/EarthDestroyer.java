package sk.sivak.eldritchhorror.core.view.components.endgame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.components.combat.FireballService;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

public class EarthDestroyer {

    private final FireballService fireballService;

    public EarthDestroyer() {
        fireballService = new FireballService(MapStage.getStage());
    }

    public void destroyEarth() {
        List<Vector2> targets = new LinkedList<>();
        List<Vector2> sources = new LinkedList<>();
        for (LocationId locationId : LocationId.values()) {
            for (int i = 0; i < 3; i++) {
                Vector2 locationPosition = LocationPositionResolver.resolve(locationId);
                targets.add(new Vector2(
                        locationPosition.x + MathUtils.random(-50, 50),
                        locationPosition.y + MathUtils.random(-50, 50)
                ));
            }
        }
        for (LocationId locationId : LocationId.values()) {
            targets.add(LocationPositionResolver.resolve(locationId));
        }

        float sourcePosition = MathUtils.random((float)MAP_WIDTH);
        for (int i = 0; i <= 3; i++) {
            sources.add(new Vector2(sourcePosition + MathUtils.random(-50,50),MAP_HEIGHT + 200));
        }

        fireballService.setTargetHealth(400);
        fireballService.launchFireballs(sources, targets);

    }
}
