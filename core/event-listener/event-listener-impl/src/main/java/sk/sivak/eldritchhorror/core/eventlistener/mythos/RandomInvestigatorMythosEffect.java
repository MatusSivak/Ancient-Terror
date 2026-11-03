package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

/** A narrated target belongs only to this execution, never to a round or a reusable listener. */
public abstract class RandomInvestigatorMythosEffect implements MythosCardEventListener {
    @Override public final void execute() {
        executeFor(ServicePlatform.get().getInvestigators().getRandomInvestigator());
    }

    @Override public final void execute(InvestigatorId narratedTarget) {
        executeFor(narratedTarget == null ? null
                : ServicePlatform.get().getInvestigators().getInvestigator(narratedTarget));
    }

    protected abstract void executeFor(InvestigatorRead targetInvestigator);
}
