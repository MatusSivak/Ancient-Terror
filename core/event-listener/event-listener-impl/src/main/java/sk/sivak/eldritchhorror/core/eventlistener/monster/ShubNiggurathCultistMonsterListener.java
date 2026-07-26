package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import static sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId.DARK_YOUNG;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class ShubNiggurathCultistMonsterListener extends AbstractMonsterListener {


    private AfterAdvanceDoomListener afterAdvanceDoomListener;
    private AwakenReckoningListener awakenReckoningListener;
    private ReckoningListener reckoningListener;

    @Override
    public void beforeSpawnMonsterInit(MonsterInfo monsterInfo) {
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setReckoning(true);
    }

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(1);
        editableMonsterInfo.setDamageTestModifier(-1);
        editableMonsterInfo.setDamageTestType(Stat.STRENGTH);
        editableMonsterInfo.setHorror(null);
        editableMonsterInfo.setToughness(1);
        editableMonsterInfo.setCurrentHealth(monsterInfo.getToughness());
        editableMonsterInfo.setReckoning(true);
        editableMonsterInfo.setReckoningText("If possible, transforms into the\nGoat Spawn Monster or Dark Young Monster.");

        afterAdvanceDoomListener = new AfterAdvanceDoomListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            if (!spawnIfPossible(NonEpicMonsterId.GOAT_SPAWN)) {
                spawnIfPossible(DARK_YOUNG);
            }
        }
    }

    private boolean spawnIfPossible(NonEpicMonsterId monsterId) {
        if (!ServicePlatform.get().getMonsterCup().canSpawn(monsterId)) {
            return false;
        }

        ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
        ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
        ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
        ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
        SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
        spawnMonsterData.setLocationId(monsterInfo.getCurrentLocation());
        spawnMonsterData.setMonsterId(monsterId);
        ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
        return true;
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            ((AbstractMonsterInfo) monsterInfo).setToughness(2);
            ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(2);
            ((AbstractMonsterInfo) monsterInfo).setReckoningText("Moves one space toward Shub-Niggurath Epic Monster.");
            ((AbstractMonsterInfo) monsterInfo).setDamageTestModifier(-2);

            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
                ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
            });
            awakenReckoningListener = new AwakenReckoningListener();
            ServicePlatform.get().getEventQueue().addDirectEventListener(awakenReckoningListener, RECKONING_MONSTER);
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class AwakenReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            MonsterInfo shubNiggurath = findShubNiggurath();
            if (shubNiggurath == null) {
                return;
            }
            LocationId shubNiggurathLocation = shubNiggurath.getCurrentLocation();
            if (monsterInfo.getCurrentLocation() == shubNiggurathLocation) {
                return;
            }
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(monsterInfo.getCurrentLocation(),
                    locationInfo -> locationInfo.getLocationId() == shubNiggurath.getCurrentLocation());

            if (nearest.getPreviousLocations().size() == 1) {
                ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, shubNiggurathLocation);
            } else {
                ServicePlatform.get().getMonsterService().moveMonster(monsterInfo,
                        nearest.getPreviousLocations().get(1));
            }
        }

        private MonsterInfo findShubNiggurath() {
            for (MonsterInfo monsterInfo : ServicePlatform.get().getMonsterCup().getMonsters()) {
                if (monsterInfo.getMonsterId() != EpicMonsterId.SHUB_NIGGURATH) {
                    continue;
                }
                return monsterInfo;
            }
            return null;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
            ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
            if (awakenReckoningListener != null) {
                ServicePlatform.get().getEventQueue().unregisterListener(awakenReckoningListener);
            }
        });
    }
}
