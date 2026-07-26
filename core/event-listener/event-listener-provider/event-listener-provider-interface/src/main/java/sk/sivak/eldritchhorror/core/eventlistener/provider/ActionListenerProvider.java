package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.action.ActionPhaseListener;

/**
 * @author msivak
 */
public interface ActionListenerProvider {

    ActionPhaseListener getRestActionListener();

    ActionPhaseListener getTravelActionListener();

    ActionPhaseListener getTradeActionListener();

    ActionPhaseListener getTicketActionListener();

    ActionPhaseListener getFocusActionListener();

    ActionPhaseListener getAcquireAssetsActionListener();

    ActionPhaseListener getSkipActionListener();

    ActionPhaseListener getInvestigatorActionListener(InvestigatorId investigatorId);

    void init();
}
