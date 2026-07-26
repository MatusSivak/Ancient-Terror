package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class StarVampireMonsterListener extends AbstractMonsterListener {


    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        if (ServicePlatform.get().getInvestigators().getLeadInvestigator() == null) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
            ServicePlatform.get().getDoomOmenService().advanceOmen();
            ServicePlatform.get().getService().release();
            return;
        }
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        ServicePlatform.get().getService().hold();
        if (activeInvestigator == null) {
            InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
        }

        ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        ServicePlatform.get().getInvestigatorService().spendCluesAsGroup(2).subscribe(spendingSuccessful -> {
            ServicePlatform.get().getService().hold();
            if (activeInvestigator == null) {
                ServicePlatform.get().getInvestigatorService().unsetActiveInvestigator();
            }
            if (!spendingSuccessful) {
                ServicePlatform.get().getDoomOmenService().advanceOmen();
            }
            ServicePlatform.get().getService().release();

        });
        ServicePlatform.get().getService().release();
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    @Override
    public void unregister() {

    }
}
