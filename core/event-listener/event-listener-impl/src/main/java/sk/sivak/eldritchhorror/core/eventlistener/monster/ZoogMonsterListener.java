package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

import static sk.sivak.eldritchhorror.core.eventlistener.monster.MonsterAbilities.*;

public class ZoogMonsterListener extends AbstractComposedMonsterListener {

    @Override
    protected void registerListeners() {
        onTestFailed(false, done -> discardOne(possessions(AssetTrait.ITEM), "Discard 1 Item", done));
        reckoning(() -> {
            FindNearestData nearest = findNearestInvestigator(monsterInfo.getCurrentLocation());
            if (nearest == null) {
                return;
            }
            announceReckoning(monsterInfo);
            moveMonster(monsterInfo, nearest.getLocationId());
            makeActive(firstInvestigatorAt(nearest.getLocationId()));
            // The active investigator changes only when the queued command executes.
            ServicePlatform.get().getService().addEventCommand(ignored -> {
                discardOne(possessions(AssetTrait.ITEM), "Discard 1 Item", () -> { });
            });
        });
    }

}
