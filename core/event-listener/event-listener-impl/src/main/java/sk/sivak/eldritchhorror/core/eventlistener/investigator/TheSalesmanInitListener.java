package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.TestFlavorRead;
import java.util.ArrayList;
import java.util.List;

public class TheSalesmanInitListener extends AbstractInvestigatorInitListener {
    private final List<EventListener> listeners = new ArrayList<>();
    private boolean acquireAssetsTestPending;

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_SALESMAN; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.WINCHESTER_RIFLE);
        ServicePlatform.get().getTokenService().gainClueFromPool(getInvestigatorId());
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override public void justRegisterListeners() {
        acquireAssetsTestPending = false;
        EventListener<TestData> afterReadStat = new EventListenerImpl<TestData>() {
            @Override public Class<TestData> getDataClass() { return TestData.class; }
            @Override public void onNotify(TestData data) {
                TestFlavorRead flavor = ServicePlatform.get().getTestFlavor();
                acquireAssetsTestPending = ServicePlatform.get().getInvestigators().getActiveInvestigatorId() == getInvestigatorId()
                        && data.getStat() == Stat.INFLUENCE
                        && flavor != null && flavor.getFlavorType() == TestFlavorType.ACQUIRE_ASSETS;
            }
        };
        EventListener<ShowReserveDragAndDropInput> beforeShowReserve = new EventListenerImpl<ShowReserveDragAndDropInput>() {
            @Override public Class<ShowReserveDragAndDropInput> getDataClass() { return ShowReserveDragAndDropInput.class; }
            @Override public void onNotify(ShowReserveDragAndDropInput data) {
                if (acquireAssetsTestPending && ServicePlatform.get().getInvestigators().getActiveInvestigatorId() == getInvestigatorId()) {
                    data.setCostReduction(1);
                }
                acquireAssetsTestPending = false;
            }
        };
        EventListener<Void> refresh = new EventListenerImpl<Void>() {
            @Override public Class<Void> getDataClass() { return Void.class; }
            @Override public void onNotify(Void data) { acquireAssetsTestPending = false; }
        };
        listeners.add(afterReadStat);
        listeners.add(beforeShowReserve);
        listeners.add(refresh);
        getEventQueue().addAfterEventListener(afterReadStat, BeforeAfterEvent.READ_STAT);
        getEventQueue().addBeforeEventListener(beforeShowReserve, BeforeAfterEvent.SHOW_RESERVE_DRAG_AND_DROP);
        getEventQueue().addDirectEventListener(refresh, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override public void unregisterInvestigator() {
        for (EventListener listener : listeners) getEventQueue().unregisterListener(listener);
        listeners.clear();
        getEventQueue().unregisterListener(this);
    }
}
