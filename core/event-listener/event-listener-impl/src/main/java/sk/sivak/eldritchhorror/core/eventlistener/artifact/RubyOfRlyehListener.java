package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.RubyOfRlyehArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

public class RubyOfRlyehListener extends AbstractArtifactListener<RubyOfRlyehArtifact> {

    private static final Logger logger = LogManager.getLogger(RubyOfRlyehListener.class);
    private AfterActionPerformedListener afterActionPerformedListener;
    private EnableCardListener enableCardListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(afterActionPerformedListener, enableCardListener);

    }

    @Override
    protected void register() {
        afterActionPerformedListener = new AfterActionPerformedListener();
        getEventQueue().addAfterEventListener(afterActionPerformedListener, BeforeAfterEvent.AFTER_ACTION_PERFORMED);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class AfterActionPerformedListener extends AbstractArtifactEventListener<AfterActionPerformedData> {

        public AfterActionPerformedListener() {
            super(RubyOfRlyehListener.this);
        }

        @Override
        protected Runnable getEventAction(AfterActionPerformedData input) {
            return () -> {
                InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                if (!ServicePlatform.get().getPerformedActions().wantsToPerformAction(activeInvestigatorId)) {
                    return;
                }
                if (activeInvestigatorId != getOwner()) {
                    return;
                }
                if (!enabled) {
                    return;
                }
                if (input.canPerformAction()) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 1)
                        .subscribe(spendData -> onCanSpend(spendData, input));
                ServicePlatform.get().getService().release();
            };
        }

        private void onCanSpend(SpendData spendData, AfterActionPerformedData input) {
            if (spendData.hasEnough()) {
                showArtifact(input);
            } else {
                end(input);
            }
        }

        private void showArtifact(AfterActionPerformedData input) {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISABLE_ON_YES);
            request.setArtifactInfo(getArtifactInfo());
            request.setTitle("Spend 1 Sanity to perform 1 additional action?");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            ServicePlatform.get().getGameService().showCard(request)
                    .subscribe(showCardResponse -> onShowCard(showCardResponse, input));
        }

        private void onShowCard(ShowCardResponse showCardResponse, AfterActionPerformedData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTokenService().spend(0, 0, 0, 1)
                        .subscribe(spendData -> onSpend(spendData, input));
            } else {
                end(input);
            }
        }

        private void onSpend(SpendData spendData, AfterActionPerformedData input) {
            if (spendData.hasEnough()) {
                input.setCanPerformAction(true);
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                spendData.pay();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                end(input);
                ServicePlatform.get().getService().release();
            } else {
                end(input);
            }
        }

        private void end(AfterActionPerformedData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().convertTo(AfterActionPerformedData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<AfterActionPerformedData> getDataClass() {
            return AfterActionPerformedData.class;
        }
    }
}
