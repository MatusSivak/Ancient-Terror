package sk.sivak.eldritchhorror.core.eventlistener.condition;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventlistener.monster.AbstractMonsterReckoningListener;
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

public class BlessedListener extends AbstractConditionListener<BlessedCondition> {


    private BeforeFindMinSuccessListener beforeFindMinSuccessListener;
    private BeforeGainCursed beforeGainCursed;
    private BeforeGainBlessed beforeGainBlessed;
    private ReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeFindMinSuccessListener, beforeGainCursed, beforeGainBlessed, reckoningListener);
    }

    @Override
    protected void register() {
        beforeFindMinSuccessListener = new BeforeFindMinSuccessListener();
        getEventQueue().addBeforeEventListener(beforeFindMinSuccessListener, BeforeAfterEvent.FIND_MIN_SUCCESS_DICE_VALUE);

        beforeGainCursed = new BeforeGainCursed();
        getEventQueue().addBeforeEventListener(beforeGainCursed, BeforeAfterEvent.REGISTER_GAINED_CARD);

        beforeGainBlessed = new BeforeGainBlessed();
        getEventQueue().addBeforeEventListener(beforeGainBlessed, BeforeAfterEvent.SELECT_CARD_TO_GAIN);

        reckoningListener = new ReckoningListener();
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_3);


    }

    private class BeforeGainBlessed extends EventListenerImpl<GainCardData> {

        @Override
        public void onNotify(GainCardData eventData) {
            if (!isOwner()) {
                return;
            }
            if (eventData.getConditionId() == null) {
                return;
            }
            if (eventData.getConditionId() != ConditionId.BLESSED) {
                return;
            }
            eventData.setNoCardToGain();
            displayBlessedCondition(eventData);
        }

        private void displayBlessedCondition(GainCardData eventData) {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.RETURN);
            request.setConditionInfo(conditionInfo);
            request.setTitle("Already Blessed. Flip Blessed Condition.");
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

    private class BeforeGainCursed extends EventListenerImpl<GainedCardData> {

        private GainedCardData input;
        private ConditionInfo cursedCondition;

        @Override
        public void onNotify(GainedCardData eventData) {
            this.input = eventData;
            if (!isOwner()) {
                return;
            }
            if (eventData.getCardType() != GainedCardData.CardType.CONDITION) {
                return;
            }
            cursedCondition = eventData.getCardToGain();
            if (!cursedCondition.getId().equals(ConditionId.CURSED)) {
                return;
            }
            eventData.setCardToGain(null);
            displayCursedCondition();
        }

        private void displayCursedCondition() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISCARD_ALWAYS);
            request.setConditionInfo(cursedCondition);
            request.setTitle("Cursed negates Blessed");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            ServicePlatform.get().getGameService().showCard(request).subscribe(response -> displayBlessedCondition());
        }

        private void displayBlessedCondition() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.DISCARD_ALWAYS);
            request.setConditionInfo(conditionInfo);
            request.setTitle("Discard Blessed Condition");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            ServicePlatform.get().getGameService().showCard(request).subscribe(this::onDiscardConditionConfirm);
        }

        private void onDiscardConditionConfirm(ShowCardResponse showCardResponse) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, conditionInfo);
            ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, cursedCondition);
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

            if (!investigatorId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId())) {
                return;
            }

            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
            showCardRequest.setTitle("4, 5, and 6's count as successes");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            eventData.setValue(4);
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
                    .withTitle("Roll 1 die. On a 1 or 2, discard this card.")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
        }

        private void onConfirm() {
            RollData rollData = new RollData();
            rollData.setMinSuccessful(3);
            rollData.setMaxFailed(2);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer rolledValue) {
            if (rolledValue <= 2) {
                discardThisCard();
            }
        }

        private void discardThisCard() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard Blessed Condition");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displayCondition.subscribe(response -> {
                ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), conditionInfo);
            });
        }
    }
}
