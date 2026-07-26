package sk.sivak.eldritchhorror.core.eventlistener.condition;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.cursed.CursedCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.PrimitiveWrapper;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class CursedListener extends AbstractConditionListener<CursedCondition> {


    private BeforeFindMinSuccessListener beforeFindMinSuccessListener;
    private BeforeGainBlessed beforeGainBlessed;
    private BeforeGainCursed beforeGainCursed;
    private ReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeFindMinSuccessListener, beforeGainBlessed, beforeGainCursed, reckoningListener);
    }

    @Override
    protected void register() {
        beforeFindMinSuccessListener = new BeforeFindMinSuccessListener();
        getEventQueue().addBeforeEventListener(beforeFindMinSuccessListener, BeforeAfterEvent.FIND_MIN_SUCCESS_DICE_VALUE);

        beforeGainBlessed = new BeforeGainBlessed();
        getEventQueue().addBeforeEventListener(beforeGainBlessed, BeforeAfterEvent.REGISTER_GAINED_CARD);

        beforeGainCursed = new BeforeGainCursed();
        getEventQueue().addBeforeEventListener(beforeGainCursed, BeforeAfterEvent.SELECT_CARD_TO_GAIN);

        reckoningListener = new ReckoningListener();
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_1);
    }


    private class BeforeGainCursed extends EventListenerImpl<GainCardData> {

        private GainCardData eventData;

        @Override
        public void onNotify(GainCardData eventData) {
            this.eventData = eventData;
            if (!isOwner()) {
                return;
            }
            if (eventData.getConditionId() == null) {
                return;
            }
            if (eventData.getConditionId() != ConditionId.CURSED) {
                return;
            }
            eventData.setNoCardToGain();
            displayCursedCondition(eventData);
        }

        private void displayCursedCondition(GainCardData eventData) {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.RETURN);
            request.setConditionInfo(conditionInfo);
            request.setTitle("Already Cursed. Flip Cursed Condition.");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            ServicePlatform.get().getGameService().showCard(request).subscribe(ok -> onFlip(eventData));
        }

        private void onFlip(GainCardData eventData) {
            ServicePlatform.get().getService().hold();
            findConditionBackListener().executeWhole();
            ServicePlatform.get().getService().convertTo(GainCardData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<GainCardData> getDataClass() {
            return GainCardData.class;
        }
    }


    private class BeforeGainBlessed extends EventListenerImpl<GainedCardData> {

        private GainedCardData input;
        private ConditionInfo blessedCondition;

        @Override
        public void onNotify(GainedCardData eventData) {
            this.input = eventData;
            if (!isOwner()) {
                return;
            }
            if (eventData.getCardType() != GainedCardData.CardType.CONDITION) {
                return;
            }
            blessedCondition = eventData.getCardToGain();
            if (!blessedCondition.getId().equals(ConditionId.BLESSED)) {
                return;
            }
            eventData.setCardToGain(null);
            displayBlessedCondition();
        }

        private void displayBlessedCondition() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISCARD_ALWAYS);
            request.setConditionInfo(blessedCondition);
            request.setTitle("Blessed negates Cursed");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            ServicePlatform.get().getGameService().showCard(request).subscribe(response -> displayCursedCondition());
        }

        private void displayCursedCondition() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISCARD_ALWAYS);
            request.setConditionInfo(conditionInfo);
            request.setTitle("Discard Cursed Condition");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            ServicePlatform.get().getGameService().showCard(request).subscribe(this::onDiscardConditionConfirm);
        }

        private void onDiscardConditionConfirm(ShowCardResponse showCardResponse) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, conditionInfo);
            ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, blessedCondition);
            ServicePlatform.get().getService().convertTo(Object.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<GainedCardData> getDataClass() {
            return GainedCardData.class;
        }
    }

    private class BeforeFindMinSuccessListener extends EventListenerImpl<PrimitiveWrapper<Integer>> {

        @Override
        public void onNotify(PrimitiveWrapper<Integer> eventData) {
            if (!isOwner()) {
                return;
            }

            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
            showCardRequest.setTitle("Only 6's count as successes");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            eventData.setValue(6);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> {
                ServicePlatform.get().getService().convertTo(PrimitiveWrapper.class, () -> eventData);
            });
        }

        @Override
        public Class<PrimitiveWrapper<Integer>> getDataClass() {
            return (Class<PrimitiveWrapper<Integer>>) (Class<?>) PrimitiveWrapper.class;
        }
    }

    private class ReckoningListener extends AbstractCardReckoningListener {

        @Override
        protected InvestigatorId getCardOwnerId() {
            return investigatorId;
        }

        @Override
        protected void onNotify() {
            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(conditionInfo)
                    .withTitle("Roll 1 die. On a 4, 5 or 6, discard this card.")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
        }

        private void onConfirm() {
            RollData rollData = new RollData();
            rollData.setMinSuccessful(4);
            rollData.setMaxFailed(3);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer rolledValue) {
            if (rolledValue >= 4) {
                discardThisCard();
            }
        }

        private void discardThisCard() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard Cursed Condition");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displayCondition.subscribe(response -> {
                ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), conditionInfo);
            });
        }
    }
}
