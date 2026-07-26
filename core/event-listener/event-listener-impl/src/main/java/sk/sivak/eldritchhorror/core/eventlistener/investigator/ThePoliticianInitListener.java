package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;

import java.util.List;

/**
 * @author msivak
 */
public class ThePoliticianInitListener extends AbstractInvestigatorInitListener {

    private boolean investigatorChanged = false;
    private boolean registeringAssets = false;
    private AssetInfo lastAsset;
    private List<AssetInfo> selectedAssets;
    private BeforeRegisteringListener beforeRegisteringListener;
    private BeforeGainListener beforeGainListener;
    private AfterGainListener afterGainListener;
    private AfterRegisteringListener afterRegisteringListener;


    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_POLITICIAN;
    }

    @Override
    protected void initInvestigator() {

        beforeRegisteringListener = new BeforeRegisteringListener();
        beforeGainListener = new BeforeGainListener();
        afterGainListener = new AfterGainListener();
        afterRegisteringListener = new AfterRegisteringListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRegisteringListener, BeforeAfterEvent.REGISTER_SELECTED_ASSETS);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeGainListener, BeforeAfterEvent.SELECT_CARD_TO_GAIN);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterGainListener, BeforeAfterEvent.REGISTER_GAINED_CARD);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterRegisteringListener, BeforeAfterEvent.REGISTER_SELECTED_ASSETS);

        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.PERSONAL_ASSISTANT);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        beforeRegisteringListener = new BeforeRegisteringListener();
        beforeGainListener = new BeforeGainListener();
        afterGainListener = new AfterGainListener();
        afterRegisteringListener = new AfterRegisteringListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRegisteringListener, BeforeAfterEvent.REGISTER_SELECTED_ASSETS);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeGainListener, BeforeAfterEvent.SELECT_CARD_TO_GAIN);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterGainListener, BeforeAfterEvent.REGISTER_GAINED_CARD);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterRegisteringListener, BeforeAfterEvent.REGISTER_SELECTED_ASSETS);

    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeRegisteringListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeGainListener);
        ServicePlatform.get().getEventQueue().unregisterListener(afterGainListener);
        ServicePlatform.get().getEventQueue().unregisterListener(afterRegisteringListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class BeforeRegisteringListener extends EventListenerImpl<ShowReserveDragAndDropOutput> {

        @Override
        public void onNotify(ShowReserveDragAndDropOutput eventData) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_POLITICIAN) {
                return;
            }
            if (eventData.getSelectedAssets().isEmpty()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getOnBoardInvestigators().size() == 1) {
                return;
            }

            lastAsset = eventData.getSelectedAssets().get(eventData.getSelectedAssets().size() - 1);
            selectedAssets = eventData.getSelectedAssets();
            registeringAssets = true;
        }

        @Override
        public Class<ShowReserveDragAndDropOutput> getDataClass() {
            return ShowReserveDragAndDropOutput.class;
        }
    }

    private class BeforeGainListener extends EventListenerImpl<GainCardData> {

        @Override
        public void onNotify(GainCardData eventData) {
            if (!registeringAssets) {
                return;
            }
            if (!selectedAssets.contains(eventData.getAssetInfo())) {
                return;
            }

            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setHideType(HideType.INVENTORY_ON_NO);
            showCardRequest.setAssetInfo(eventData.getAssetInfo());
            showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setPortraitBeforeTitle(InvestigatorId.THE_POLITICIAN);
            showCardRequest.setTitle("Give "+eventData.getAssetInfo().getName()+" to another investigator?");
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> {
                if (ShowCardResponse.YES.equals(response)) {
                    ServicePlatform.get().getInvestigatorService().selectInvestigator(new InvestigatorRestriction(false).addDisabledInvestigator(InvestigatorId.THE_POLITICIAN))
                            .subscribe(investigatorId -> onSelect(investigatorId, eventData));
                } else {
                    eventData.setConfirmRequired(false);
                    ServicePlatform.get().getService().convertTo(GainCardData.class, () -> eventData);
                }
            });
        }


        private void onSelect(InvestigatorId investigatorId, GainCardData eventData) {
            investigatorChanged = true;
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getInvestigatorService().convertTo(GainCardData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<GainCardData> getDataClass() {
            return GainCardData.class;
        }
    }

    private class AfterGainListener extends EventListenerImpl<GainedCardData> {

        @Override
        public void onNotify(GainedCardData eventData) {
            if (!investigatorChanged) {
                return;
            }
            if (eventData.getCardToGain() instanceof ConditionInfo) {
                return;
            }
            if (eventData.getCardToGain() instanceof SpellInfo) {
                changeBackToPolitician(eventData, eventData.getCardToGain());
                return;
            }
            AssetInfo cardToGain = eventData.getCardToGain();
            if (!selectedAssets.contains(cardToGain)) {
                return;
            }
            changeBackToPolitician(eventData, cardToGain);
        }

        private void changeBackToPolitician(GainedCardData eventData, CardInfo cardToGain) {
            investigatorChanged = false;
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_POLITICIAN);
            boolean canPerformAction = ServicePlatform.get().getPerformedActions().canPerformAction(InvestigatorId.THE_POLITICIAN);
            if (cardToGain != lastAsset || canPerformAction) {
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            } else {

            }
            ServicePlatform.get().getInvestigatorService().convertTo(GainedCardData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<GainedCardData> getDataClass() {
            return GainedCardData.class;
        }
    }

    private class AfterRegisteringListener extends EventListenerImpl<ShowReserveDragAndDropOutput> {

        @Override
        public void onNotify(ShowReserveDragAndDropOutput eventData) {
            if (!registeringAssets) {
                return;
            }
            registeringAssets = false;
        }

        @Override
        public Class<ShowReserveDragAndDropOutput> getDataClass() {
            return ShowReserveDragAndDropOutput.class;
        }
    }
}
