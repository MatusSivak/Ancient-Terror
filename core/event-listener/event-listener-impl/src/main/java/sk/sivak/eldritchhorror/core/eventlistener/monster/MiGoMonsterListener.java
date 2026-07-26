package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DEFEAT_MONSTER;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class MiGoMonsterListener extends AbstractMonsterListener {

    private BeforeDefeatMonsterListener beforeDefeatMonsterListener;
    private ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, DEFEAT_MONSTER);

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);

    }

    private class BeforeDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (!eventData.isInCombat()) {
                return;
            }
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getGameService().gainArtifact();
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            List<? extends ClueInfo> spawnedClues = ServicePlatform.get().getCluePool().getSpawnedClues();
            if (spawnedClues.isEmpty()) {
                return;
            }

            FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(monsterInfo.getCurrentLocation(), locationInfo -> {
                for (ClueInfo spawnedClue : spawnedClues) {
                    if (spawnedClue.getCurrentLocationId() == locationInfo.getLocationId()) {
                        return true;
                    }
                }
                return false;
            });

            LocationId nearestLocationId = nearest.getLocationId();

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, nearestLocationId);
            ServicePlatform.get().getTokenService().discardClue(nearestLocationId);
        }
    }


    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDefeatMonsterListener);
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
