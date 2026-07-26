package sk.sivak.eldritchhorror.core.eventlistener.card;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public abstract class AbstractCardReckoningListener extends EventListenerImpl<ReckoningFireType> {

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
        // TODO if lost in time and space, don't trigger
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (!ServicePlatform.get().getInvestigators().isOnBoard(getCardOwnerId())) {
                return;
            }
            if (!shouldTrigger()) {
                return;
            }
            ServicePlatform.get().getService().hold();

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getCardOwnerId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            onNotify();
            ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            ServicePlatform.get().getService().release();
        });
    }

    protected boolean shouldTrigger() {
        return true;
    }

    protected abstract InvestigatorId getCardOwnerId();

    protected abstract void onNotify();

    @Override
    public Class<ReckoningFireType> getDataClass() {
        return ReckoningFireType.class;
    }

}
