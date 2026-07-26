package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.REENABLE_DISABLED_ABILITIES;

public class PrepareNewRoundAction extends AbstractHookableAction<Object, Void> {

    public PrepareNewRoundAction() {
        super(BeforeAfterEvent.PREPARE_NEW_ROUND);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getEventQueue().fireDirectEvent(REENABLE_DISABLED_ABILITIES, null);
        });

        ServicePlatform.get().getInvestigatorService().replaceInvestigators();

        ServicePlatform.get().getInvestigatorService().selectNewLeadInvestigator(true);

        ServicePlatform.get().getGameService().changeAndShowPhase();
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getInvestigators().changeActiveInvestigator(
                    ServicePlatform.get().getInvestigators().getLeadInvestigator().getInfo().getInvestigatorId());
        });

        ServicePlatform.get().getModel().newRound();
        if (!ServicePlatform.get().getInitGameController().hasPurchasedNoAds()) {
            GoogleServicesHolder.getAdHandler().showInterstitialAd(new AdHandler.AdCallbacks()
                    .setOnAdOpenedAction(() -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "interstitial_ad", "show_opened"))
                    .setOnAdClosedAction(() -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "interstitial_ad", "show_closed"))
                    .setOnAdFailedToLoadAction(errorCode -> GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "interstitial_ad", "show_failed_" + errorCode)));
        }

        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getGameService().initPhase();
        ServicePlatform.get().getGameService().release();
        ss.onSuccess(null);
    }
}
