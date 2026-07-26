package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;

public class CollectEncountersAction implements Action<Object, AvailableEncounters> {


    @Override
    public Single<AvailableEncounters> execute() {

        return Single.create(sub -> {
            AvailableEncounters availableEncounters = new AvailableEncounters();
            ServicePlatform.get().getEventQueue().fireDirectEvent(DirectEvent.COLLECT_COMMON_ENCOUNTERS, availableEncounters);
            sub.onSuccess(availableEncounters);
        });

    }

    @Override
    public void setInput(Object input) {
        // ignore
    }
}
