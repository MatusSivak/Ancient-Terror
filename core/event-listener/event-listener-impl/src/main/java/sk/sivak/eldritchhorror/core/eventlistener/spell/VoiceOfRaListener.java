package sk.sivak.eldritchhorror.core.eventlistener.spell;

import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.voiceofra.VoiceOfRaSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

public class VoiceOfRaListener extends AbstractSpellListener<VoiceOfRaSpell> {

    private SpellReckoningListener reckoningListener;

    public VoiceOfRaListener() {

    }

    private AfterActionPerformedListener afterActionPerformedListener;

    private EnableCardListener enableCardListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(afterActionPerformedListener, enableCardListener, reckoningListener);
    }

    @Override
    protected void register() {
        afterActionPerformedListener = new AfterActionPerformedListener();
        getEventQueue().addAfterEventListener(afterActionPerformedListener, BeforeAfterEvent.AFTER_ACTION_PERFORMED);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        reckoningListener = new SpellReckoningListener(this);
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class AfterActionPerformedListener extends EventListenerImpl<AfterActionPerformedData> {

        private AfterActionPerformedData eventData;

        @Override
        public void onNotify(AfterActionPerformedData eventData) {
            this.eventData = eventData;

            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            if (!ServicePlatform.get().getPerformedActions().wantsToPerformAction(activeInvestigatorId)) {
                return;
            }
            if (activeInvestigatorId != spellOwnerId) {
                return;
            }
            if (!enabled) {
                return;
            }
            if (eventData.canPerformAction()) {
                return;
            }
            TestFlavorRequest testFlavorRequest = new TestFlavorRequest();
            testFlavorRequest.setType(TestFlavorType.SPELL);
            testFlavorRequest.setData(spellInfo);
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTestService().setTestFlavor(testFlavorRequest);
            ServicePlatform.get().getTokenService().canSpend(0, 0, 1, 1).subscribe(this::onCanSpend);
            ServicePlatform.get().getService().release();
        }

        private void onCanSpend(SpendData spendData) {
            if (spendData.hasEnough()) {
                showSpell();
            } else {
                end();
            }
        }

        private void showSpell() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISABLE_ON_YES);
            request.setSpellInfo(spellInfo);
            request.setTitle("Spend 1 Health and 1 Sanity to perform 1 additional action?");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            ServicePlatform.get().getGameService().showCard(request).subscribe(this::onShowCard);
        }

        private void onShowCard(ShowCardResponse showCardResponse) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTokenService().spend(0, 0, 1, 1).subscribe(this::onSpend);
            } else {
                end();
            }
        }

        private void onSpend(SpendData spendData) {
            if (spendData.hasEnough()) {
                eventData.setCanPerformAction(true);
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(spellInfo);
                spendData.pay();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                end();
                ServicePlatform.get().getService().release();
            } else {
                end();
            }
        }

        private void end() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTestService().unsetTestFlavor();
            ServicePlatform.get().getService().convertTo(AfterActionPerformedData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<AfterActionPerformedData> getDataClass() {
            return AfterActionPerformedData.class;
        }
    }


}
