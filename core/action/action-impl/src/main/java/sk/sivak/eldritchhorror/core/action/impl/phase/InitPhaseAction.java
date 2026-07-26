package sk.sivak.eldritchhorror.core.action.impl.phase;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.*;

public class InitPhaseAction extends AbstractHookableAction<Object, Void> {

    public InitPhaseAction() {
        super(BeforeAfterEvent.INIT_PHASE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        PhaseType phaseType = ServicePlatform.get().getPhase().getPhaseType();
        switch (phaseType) {
            case ACTION:
                initActionPhase();
                break;
            case ENCOUNTER:
                initEncounterPhase();
                break;
            case MYTHOS:
                initMythosPhase();
                break;
        }
    }

    private void initActionPhase() {
        GoogleServicesHolder.getAnalyticsTracker().trackScreenName("Phase: Action ("+ServicePlatform.get().getModel().getCurrentRound()+")");
        ServicePlatform.get().getPerformedActions().reset();
        ServicePlatform.get().getGameService().performAction();
        ss.onSuccess(null);
    }

    private void initEncounterPhase() {
        GoogleServicesHolder.getAnalyticsTracker().trackScreenName("Phase: Encounter ("+ServicePlatform.get().getModel().getCurrentRound()+")");
        ServicePlatform.get().getService().hold();
        if (ServicePlatform.get().getInvestigators().getActiveInvestigator() == null) {
            ServicePlatform.get().getGameService().changeAndShowPhase();
            ServicePlatform.get().getGameService().initPhase();
            ServicePlatform.get().getService().release();
            ss.onSuccess(null);
            return;
        }
        ServicePlatform.get().getPerformedEncounters().reset();
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getEncounterService().encounter();
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);
    }

    private void initMythosPhase() {
        ServicePlatform.get().getDoomOmenService().initMythosPhase();
        ss.onSuccess(null);
    }
}
