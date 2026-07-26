package sk.sivak.eldritchhorror.core.action.impl.phase;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;

public class ShowPhaseAction extends AbstractHookableAction<Object, Void> {

    public ShowPhaseAction() {
        super(BeforeAfterEvent.SHOW_PHASE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        proceedWithPhase(ss);
    }

    private void proceedWithPhase(SingleSubscriber<? super Void> ss) {
        PhaseType phaseType = ServicePlatform.get().getPhase().getPhaseType();
        ServicePlatform.get().getGameController().showPhase(phaseType).subscribe(() -> {
            if (phaseType != PhaseType.MYTHOS) {
                if (!ServicePlatform.get().getInitGameController().hasPurchasedNoAds()) {
                    GoogleServicesHolder.getAdHandler().showInterstitialAdAfterThreeMinutes(new AdHandler.AdCallbacks()
                            .setOnAdOpenedAction(() -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "interstitial_ad", "show_opened"))
                            .setOnAdClosedAction(() -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "interstitial_ad", "show_closed"))
                            .setOnAdFailedToLoadAction(errorCode -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "interstitial_ad", "show_failed_" + errorCode)));
                }
            }
            ss.onSuccess(null);
        });
    }
}
