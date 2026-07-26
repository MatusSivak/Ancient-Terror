package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class DarkYoungMonsterListener extends AbstractMonsterListener {

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
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            RollData rollData = new RollData();
            rollData.setMinSuccessful(3);
            rollData.setMaxFailed(2);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer value) {
            if (value <= 2) {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            }
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
