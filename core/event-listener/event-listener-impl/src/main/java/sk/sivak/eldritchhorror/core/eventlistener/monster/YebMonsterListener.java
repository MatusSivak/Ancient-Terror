package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class YebMonsterListener extends AbstractMonsterListener {

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getToughness());

        ServicePlatform.get().getService().addEventCommand( in -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);

            for (int i = 0; i < 2; i++) {
                ServicePlatform.get().getService().addEventCommand(in2 -> {
                    SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                    spawnMonsterData.setLocationId(monsterInfo.getCurrentLocation());
                    ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
                });
            }

            ServicePlatform.get().getService().convertTo(Object.class, () -> in);
            ServicePlatform.get().getService().release();
        });
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);
    }

    @Override
    public void unregister() {

    }
}
