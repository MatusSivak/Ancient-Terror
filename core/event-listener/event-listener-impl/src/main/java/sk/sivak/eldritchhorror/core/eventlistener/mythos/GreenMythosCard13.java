package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.ArrayList;
import java.util.List;

/**
 * No Peace For the Fallen: discard all defeated investigators (possessions and tokens),
 * then a random investigator gains a Cursed Condition.
 */
public class GreenMythosCard13 extends RandomInvestigatorMythosEffect {

    @Override
    protected void executeFor(InvestigatorRead targetInvestigator) {
        List<InvestigatorRead> defeatedInvestigators = new ArrayList<>();
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getDefeatedInvestigators()) {
            if (investigator.getCurrentLocationId() != null) {
                defeatedInvestigators.add(investigator);
            }
        }

        ServicePlatform.get().getService().hold();
        for (InvestigatorRead defeatedInvestigator : defeatedInvestigators) {
            ServicePlatform.get().getInvestigatorService().discardDefeatedInvestigator(defeatedInvestigator.getInfo().getInvestigatorId());
        }
        if (targetInvestigator != null) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
        }
        ServicePlatform.get().getService().release();
    }
}
