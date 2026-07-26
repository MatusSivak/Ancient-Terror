package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardSpellFromInvestigatorData;

/**
 * @author msivak
 */
public class DiscardSpellFromInvestigatorAction extends AbstractHookableAction<DiscardSpellFromInvestigatorData, DiscardSpellFromInvestigatorData> {

    private static final Logger logger = LogManager.getLogger(DiscardSpellFromInvestigatorAction.class);

    public DiscardSpellFromInvestigatorAction() {
        super(BeforeAfterEvent.DISCARD_SPELL_FROM_INVESTIGATOR);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DiscardSpellFromInvestigatorData> ss) {
        ServicePlatform.get().getSpellsDeck().discard(input.getInvestigatorId(), input.getSpellInfo());
        ServicePlatform.get().getSpellListenerProvider().unregister(input.getSpellInfo());
        ss.onSuccess(input);
    }
}
