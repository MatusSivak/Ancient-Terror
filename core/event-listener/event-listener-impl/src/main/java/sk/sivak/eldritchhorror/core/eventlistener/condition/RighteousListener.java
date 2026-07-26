package sk.sivak.eldritchhorror.core.eventlistener.condition;

import sk.sivak.eldritchhorror.core.constants.condition.righteous.RighteousCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.YES;

public class RighteousListener extends AbstractConditionListener<RighteousCondition> {


    private AfterRestOrFocusListener afterRestOrFocusListener;
    private AfterRestListener afterRestListener;
    private AfterFocusListener afterFocusListener;
    private ConditionReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(reckoningListener, afterRestListener, afterFocusListener);
    }

    @Override
    protected void register() {
        afterRestOrFocusListener = new AfterRestOrFocusListener();
        afterRestListener = new AfterRestListener();
        afterFocusListener = new AfterFocusListener();
        getEventQueue().addAfterEventListener(afterRestListener, BeforeAfterEvent.REST);
        getEventQueue().addAfterEventListener(afterFocusListener, BeforeAfterEvent.FOCUS);

        reckoningListener = new ConditionReckoningListener(RighteousListener.this);
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class AfterRestListener extends EventListenerImpl<RestData> {

        @Override
        public void onNotify(RestData eventData) {
            afterRestOrFocusListener.onNotify(eventData);
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }

    private class AfterFocusListener extends EventListenerImpl<FocusData> {

        @Override
        public void onNotify(FocusData eventData) {
            afterRestOrFocusListener.onNotify(eventData);
        }

        @Override
        public Class<FocusData> getDataClass() {
            return FocusData.class;
        }
    }

    private class AfterRestOrFocusListener {

        private Object eventData;

        public void onNotify(Object eventData) {
            this.eventData = eventData;
            if (!isOwner()) {
                return;
            }
            if (canGainFocus()) {
                askToGainFocus();
            } else if (canGainSanity()) {
                askToGainSanity();
            }
        }

        private void askToGainFocus() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setTitle("Gain Focus?");
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(this::onGainFocusAnswer);
        }

        private void askToGainSanity() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setConditionInfo(conditionInfo);
            showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
            showCardRequest.setHideType(HideType.RETURN);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            showCardRequest.setTitle("Recover Sanity?");
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(this::onRecoverSanityAnswer);
        }

        private void onGainFocusAnswer(ShowCardResponse response) {
            if (response == YES) {
                gainFocus();
            } else {
                if (canGainSanity()) {
                    askToGainSanity();
                } else {
                    ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
                }
            }
        }

        private void onRecoverSanityAnswer(ShowCardResponse response) {
            if (response == YES) {
                gainSanity();
            } else {
                ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            }
        }

        private void gainSanity() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainSanity(1);
            ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        private void gainFocus() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainFocus();
            ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        private boolean canGainFocus() {
            return ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getFocusTokens() < 2;
        }

        private boolean canGainSanity() {
            if (eventData instanceof RestData && !((RestData) eventData).isSanityEnabled()) {
                return false;
            }
            InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId);
            return investigator.getCurrentSanity() < investigator.getInfo().getMaxSanity();
        }
    }


}
