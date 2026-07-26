package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import java8.features.function.Supplier;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.REPLACE_INVESTIGATOR;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.INIT_INVESTIGATORS;

public class SelectReplacingInvestigatorAction implements Action<Object, Void> {

    private final InvestigatorId removedInvestigator;

    public SelectReplacingInvestigatorAction(InvestigatorId removedInvestigator) {
        this.removedInvestigator = removedInvestigator;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            Supplier<List<InvestigatorInfo>> initInvestigatorsAction = () -> {
                ServicePlatform.get().getInvestigators().addLockedToAvailable();
                return ServicePlatform.get().getInvestigators().getAvailableInvestigators();
            };
            ServicePlatform.get().getGameController().selectReplacingInvestigator(removedInvestigator, initInvestigatorsAction).subscribe(selectedArray -> {
                InvestigatorInfo selected = selectedArray[0];
                initReplacingInvestigator(selected, onSub);
            });
        });

    }

    private void initReplacingInvestigator(InvestigatorInfo replacing, SingleSubscriber<? super Void> onSub) {
        ServicePlatform.get().getInvestigators().initReplacingInvestigator(removedInvestigator, replacing);
        ServicePlatform.get().getPerformedActions().init(replacing.getInvestigatorId());
        ServicePlatform.get().getEventListenerProvider().registerInvestigatorListeners(replacing.getInvestigatorId());
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(REPLACE_INVESTIGATOR, "name", replacing.getInvestigatorId().name());
        ServicePlatform.get().getEventQueue().fireDirectEvent(INIT_INVESTIGATORS, replacing.getInvestigatorId());
        ServicePlatform.get().getGameController().initReplacingInvestigator(replacing.getInvestigatorId(), replacing.getLocationId()).subscribe(() -> {
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
