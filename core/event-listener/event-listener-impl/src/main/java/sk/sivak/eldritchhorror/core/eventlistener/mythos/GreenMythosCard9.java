package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class GreenMythosCard9 implements MythosCardEventListener{

    @Override
    public void execute() {
        InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
        if (leadInvestigator == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getGameService().gainArtifact();
        RollData rollData = new RollData();
        rollData.setMaxFailed(2);
        rollData.setMinSuccessful(3);
        ServicePlatform.get().getTestService().rollDie(rollData).subscribe(value -> {
            if (value < 3) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTokenService().loseHealth(2);
                ServicePlatform.get().getTokenService().loseSanity(2);
                ServicePlatform.get().getService().release();
            }
        });
        ServicePlatform.get().getService().release();
    }

}
