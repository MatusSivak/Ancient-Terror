package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class HoundOfTindalosMonsterListener extends AbstractMonsterListener {

    private ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            FindNearestData nearest = MonsterAbilities.findNearestInvestigator(monsterInfo.getCurrentLocation());
            if (nearest == null || nearest.getLocationId() == null) {
                return;
            }
            LocationId nearestLocationId = nearest.getLocationId();
            InvestigatorId nearestInvestigatorId = MonsterAbilities.firstInvestigatorAt(nearestLocationId);
            if (nearestInvestigatorId == null) {
                return;
            }

            MonsterAbilities.announceReckoning(monsterInfo);
            MonsterAbilities.moveMonster(monsterInfo, nearestLocationId);
            MonsterAbilities.makeActive(nearestInvestigatorId);
            ServicePlatform.get().getTestService().combat(monsterInfo);
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
