package sk.sivak.eldritchhorror.core.eventlistener.condition;

import sk.sivak.eldritchhorror.core.constants.condition.poisoned.PoisonedCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PoisonedListener extends AbstractConditionListener<PoisonedCondition> {

    private BeforeRestListener beforeRestListener;
    private ReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeRestListener, reckoningListener);
    }

    @Override
    protected void register() {
        beforeRestListener = new BeforeRestListener();
        getEventQueue().addBeforeEventListener(beforeRestListener, BeforeAfterEvent.REST);

        reckoningListener = new ReckoningListener();
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class BeforeRestListener extends EventListenerImpl<RestData> {

        @Override
        public void onNotify(RestData eventData) {
            if (!isOwner()) {
                return;
            }
            eventData.disableHealth();
            eventData.disableSanity();


            AbstractConditionBackListener conditionBackListener = findConditionBackListener();
            conditionBackListener.setConvertToObject(eventData);
            conditionBackListener.executeWhole();
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }

    private class ReckoningListener extends AbstractCardReckoningListener {

        @Override
        protected InvestigatorId getCardOwnerId() {
            return investigatorId;
        }

        @Override
        protected void onNotify() {
            ShowCardRequest request = new ShowCardRequest();
            request.setHideType(HideType.RETURN);
            request.setConditionInfo(conditionInfo);
            request.setTitle("Lose one Health.");
            request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            ServicePlatform.get().getGameService().showCard(request).subscribe(ok -> {
                ServicePlatform.get().getTokenService().loseHealth(1);
            });
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

}
