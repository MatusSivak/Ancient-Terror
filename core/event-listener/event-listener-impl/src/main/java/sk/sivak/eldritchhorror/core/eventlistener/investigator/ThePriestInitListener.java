package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import java.util.ArrayList;
import java.util.List;

public class ThePriestInitListener extends AbstractInvestigatorInitListener {
    private final List<EventListener> listeners = new ArrayList<>();

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_PRIEST; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.KING_JAMES_BIBLE);

        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override public void justRegisterListeners() {
        EventListener<CloseGateData> listener = new EventListenerImpl<CloseGateData>() {
            @Override public Class<CloseGateData> getDataClass() { return CloseGateData.class; }
            @Override public void onNotify(CloseGateData data) {
                if (data == null || !data.isOtherworldEncounter()
                        || ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != getInvestigatorId()
                        || ServicePlatform.get().getConditionsDeck().hasTrait(getInvestigatorId(), ConditionTrait.BOON)) return;
                getService().hold();
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.BOON);
                getService().convertTo(CloseGateData.class, () -> data);
                getService().release();
            }
        };
        listeners.add(listener);
        getEventQueue().addAfterEventListener(listener, BeforeAfterEvent.CLOSE_GATE);
    }

    @Override public void unregisterInvestigator() {
        for (EventListener listener : listeners) getEventQueue().unregisterListener(listener);
        listeners.clear();
        getEventQueue().unregisterListener(this);
    }

}

