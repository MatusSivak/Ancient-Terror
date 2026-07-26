package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;

public abstract class AbstractMonsterReckoningListener extends EventListenerImpl<ReckoningFireType> {

    private boolean charged = false;

    @Override
    public final void onNotify(ReckoningFireType eventData) {
        if (eventData == ReckoningFireType.CHARGE) {
            charged = true;
            return;
        }
        if (eventData == ReckoningFireType.FIRE && !charged) {
            return;
        }
        charged = false;
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getService().hold();
            onNotify();
            ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            ServicePlatform.get().getService().release();
        });
    }

    abstract void onNotify();

    @Override
    public Class<ReckoningFireType> getDataClass() {
        return ReckoningFireType.class;
    }
}
