package sk.sivak.eldritchhorror.core.action.impl.action.general.trade;

import java8.features.stream.Stream;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.ArrayList;
import java.util.List;

public class FindInvestigatorsForTradeAction extends AbstractHookableAction<Object, List<InvestigatorId>> {

    public FindInvestigatorsForTradeAction() {
        super(BeforeAfterEvent.FIND_INVESTIGATORS_FOR_TRADE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super List<InvestigatorId>> ss) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        LocationId activeLocationId = ServicePlatform.get().getInvestigators().getInvestigator(activeInvestigatorId).getCurrentLocationId();

        List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<? extends InvestigatorRead> investigatorsOnSameSpace = Stream.collectToList(selectedInvestigators,
                selected -> selected.getCurrentLocationId() == activeLocationId &&
                        selected.getInfo().getInvestigatorId() != activeInvestigatorId);

        ss.onSuccess(new ArrayList<>(Stream.map(investigatorsOnSameSpace, investigator -> investigator.getInfo().getInvestigatorId())));
    }
}
