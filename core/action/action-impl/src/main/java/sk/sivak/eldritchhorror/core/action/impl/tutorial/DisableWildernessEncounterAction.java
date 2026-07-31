package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;
import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.DISABLE_ENCOUNTERS;

public class DisableWildernessEncounterAction implements Action<Object, Object> {


    private DisableEncountersListener disableEncountersListener;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getPerformedEncounters().reset();
            disableEncountersListener = new DisableEncountersListener();
            ServicePlatform.get().getEventQueue().addDirectEventListener(disableEncountersListener, DISABLE_ENCOUNTERS);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(disableEncountersListener);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });

        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class DisableEncountersListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            for (Encounter encounter : eventData.getEncounters()) {
                if (encounter.getEncounterType() == EncounterType.GENERAL) {
                    encounter.getEncounterButtonData().disable("tutorial.disabled.notAvailable");
                }
            }
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }
}
