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

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class AwakenShubNiggurathCultistMonsterListener extends AbstractMonsterListener {



    private ReckoningListener reckoningListener;

    @Override
    public void beforeSpawnMonsterInit(MonsterInfo monsterInfo) {
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setReckoning(true);
    }


    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        commonRegister(monsterInfo);
        ((AbstractMonsterInfo)monsterInfo).setCurrentHealth(monsterInfo.getToughness());
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        commonRegister(monsterInfo);
    }

    private void commonRegister(MonsterInfo monsterInfo) {
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(1);
        editableMonsterInfo.setDamageTestModifier(-2);
        editableMonsterInfo.setDamageTestType(Stat.STRENGTH);
        editableMonsterInfo.setHorror(null);
        editableMonsterInfo.setToughness(2);
        editableMonsterInfo.setReckoning(true);
        editableMonsterInfo.setReckoningText("Moves one space toward Shub-Niggurath Epic Monster.");

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

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
            ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
        });

    }
}
