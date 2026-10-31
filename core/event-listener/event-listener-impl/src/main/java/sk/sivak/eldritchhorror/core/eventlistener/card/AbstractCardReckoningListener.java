package sk.sivak.eldritchhorror.core.eventlistener.card;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;

public abstract class AbstractCardReckoningListener extends EventListenerImpl<ReckoningFireType> {

    private boolean charged = false;
    private int generation;

    /** Removing a card must also cancel reckoning work already inserted in the command queue. */
    final void cancelPendingReckoning() {
        charged = false;
        generation++;
    }

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
        final int queuedGeneration = generation;
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (queuedGeneration != generation) return;
            // Investigators Lost in Time and Space are not on board; their reckoning effects do not resolve.
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
