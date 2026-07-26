package sk.sivak.eldritchhorror.core.eventlistener.condition;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.amnesia.AmnesiaCondition;
import sk.sivak.eldritchhorror.core.constants.condition.paranoia.ParanoiaCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParanoiaListener extends AbstractConditionListener<ParanoiaCondition> {

    private AfterRestListener afterRestListener;
    private ConditionReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(afterRestListener, reckoningListener);
    }

    @Override
    protected void register() {
        afterRestListener = new AfterRestListener();
        getEventQueue().addAfterEventListener(afterRestListener, BeforeAfterEvent.REST);

        reckoningListener = new ConditionReckoningListener(ParanoiaListener.this);
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class AfterRestListener extends EventListenerImpl<RestData> {

        private RestData eventData;

        @Override
        public void onNotify(RestData eventData) {
            if (!isOwner()) {
                return;
            }
            this.eventData = eventData;
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setTitle("Roll 1 die?");
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(this::onAnswer);
        }

        private boolean isOwner() {
            return investigatorId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        }

        private void onAnswer(ShowCardResponse response) {
            if (response != ShowCardResponse.YES) {
                ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> eventData);
                return;
            }

            RollData rollData = new RollData();
            rollData.setMinSuccessful(5);
            rollData.setMaxFailed(0);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer diceRoll) {
            if (diceRoll >= 5) {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setConditionInfo(conditionInfo);
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setTitle("Discard Paranoia Condition");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, conditionInfo);
                    ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> eventData);
                    ServicePlatform.get().getService().release();
                });
            } else {
                ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> eventData);
            }
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }

}
