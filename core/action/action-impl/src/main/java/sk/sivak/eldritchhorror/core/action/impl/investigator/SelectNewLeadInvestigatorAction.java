package sk.sivak.eldritchhorror.core.action.impl.investigator;

import java8.features.stream.Stream;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;

import java.util.Collection;

public class SelectNewLeadInvestigatorAction implements Action<Object, InvestigatorId> {

    private final boolean reorder;

    public SelectNewLeadInvestigatorAction(boolean reorder) {
        this.reorder = reorder;
    }

    @Override
    public Single<InvestigatorId> execute() {
        return Single.create(onSub -> {
            InvestigatorRestriction restriction = new InvestigatorRestriction(true);
            restriction.setTitle("Select new Lead Investigator:");
            Collection<InvestigatorId> investigatorIds = Stream.map(ServicePlatform.get().getInvestigators().getOnBoardInvestigators(),
                    investigator -> investigator.getInfo().getInvestigatorId());
            if (investigatorIds.isEmpty()) {
                onSub.onSuccess(null);
                return;
            }
            restriction.addAllowedInvestigators(investigatorIds);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(restriction).subscribe(this::setNewLeadInvestigator);
            onSub.onSuccess(null);
        });
    }

    private void setNewLeadInvestigator(InvestigatorId investigatorId) {
        ServicePlatform.get().getInvestigators().setLeadInvestigator(investigatorId, reorder);
        ServicePlatform.get().getService().convertTo(InvestigatorId.class, () -> investigatorId);
    }

    @Override
    public void setInput(Object input) {

    }
}
