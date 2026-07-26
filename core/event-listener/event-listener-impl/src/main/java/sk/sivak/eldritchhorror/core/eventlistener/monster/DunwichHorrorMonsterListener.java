package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class DunwichHorrorMonsterListener extends AbstractMonsterListener {

    private ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getToughness());

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);

        int players = ServicePlatform.get().getModel().getReferenceCard().getPlayers();
        ((AbstractMonsterInfo) monsterInfo).setReckoningText("Roll one die. On "+players+" or less, one Gate is spawned.");
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);

        int players = ServicePlatform.get().getModel().getReferenceCard().getPlayers();
        ((AbstractMonsterInfo) monsterInfo).setReckoningText("Roll one die. On "+players+" or less, one Gate is spawned.");
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            RollData rollData = new RollData();
            int players = ServicePlatform.get().getModel().getReferenceCard().getPlayers();
            rollData.setMinSuccessful(players + 1);
            rollData.setMaxFailed(players);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer value) {
            if (value <= ServicePlatform.get().getModel().getReferenceCard().getPlayers()) {
                ServicePlatform.get().getGameService().spawnGates(1, 1).subscribe();
            }
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
