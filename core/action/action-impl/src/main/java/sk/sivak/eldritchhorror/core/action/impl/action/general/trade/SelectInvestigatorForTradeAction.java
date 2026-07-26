package sk.sivak.eldritchhorror.core.action.impl.action.general.trade;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;

import java.util.List;

public class SelectInvestigatorForTradeAction extends AbstractHookableAction<List<InvestigatorId>, InvestigatorId> {

    public SelectInvestigatorForTradeAction() {
        super(BeforeAfterEvent.SELECT_INVESTIGATOR_FOR_TRADE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
        investigatorRestriction.addAllowedInvestigators(input);
        Single<InvestigatorId> selectInvestigatorSingle = ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction);
        selectInvestigatorSingle.subscribe(this::onSelect);
        ss.onSuccess(null);

    }

    private void onSelect(InvestigatorId investigatorId) {
        // as output from selectInvestigator (spellOwnerId) is same as output from (SelectInvestigatorForTrade), I don't need to do any conversion...
    }
}
