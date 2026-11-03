package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class GreenMythosCard8 extends RandomInvestigatorMythosEffect{

    @Override
    protected void executeFor(InvestigatorRead targetInvestigator) {
        if (targetInvestigator == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
        ServicePlatform.get().getService().release();
    }

}
