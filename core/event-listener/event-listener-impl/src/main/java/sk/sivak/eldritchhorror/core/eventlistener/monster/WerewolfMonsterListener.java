package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.Collections;

import static sk.sivak.eldritchhorror.core.eventlistener.monster.MonsterAbilities.*;

public class WerewolfMonsterListener extends AbstractComposedMonsterListener {

    @Override
    protected void registerListeners() {
        resistance(false);
        reckoning(() -> {
            FindNearestData nearest = findNearestInvestigator(monsterInfo.getCurrentLocation());
            if (nearest == null) {
                return;
            }
            announceReckoning(monsterInfo);
            LocationId target = stepsToward(nearest, 1);
            moveMonster(monsterInfo, target);
            damageEach(investigatorsAt(Collections.singletonList(target)), 1, 0);
        });
    }
}