package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class GreenMythosCard8 implements MythosCardEventListener{

    @Override
    public void execute() {
        InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
        if (leadInvestigator == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
        ServicePlatform.get().getService().release();
    }

}
