package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;

public class AdjustCharterFlightAction implements Action<Object, Object> {

    private BeforeSelectTravelLocation beforeSelectTravelLocation;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            beforeSelectTravelLocation = new BeforeSelectTravelLocation();
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSelectTravelLocation, BeforeAfterEvent.SELECT_TRAVEL_LOCATION);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(beforeSelectTravelLocation);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class BeforeSelectTravelLocation extends EventListenerImpl<TravelData> {

        @Override
        public void onNotify(TravelData eventData) {
            eventData.setOptional(false);
        }

        @Override
        public Class<TravelData> getDataClass() {
            return TravelData.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }
}
