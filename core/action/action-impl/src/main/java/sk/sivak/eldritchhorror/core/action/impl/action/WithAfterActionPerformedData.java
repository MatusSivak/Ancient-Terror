package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.PerformedActionsRead;

import java.util.List;

/**
 * @author msivak
 */
public class WithAfterActionPerformedData extends AbstractHookableAction<AfterActionPerformedData, AfterActionPerformedData> {

    private static final Logger logger = LogManager.getLogger(WithAfterActionPerformedData.class);

    public WithAfterActionPerformedData() {
        super(BeforeAfterEvent.WITH_AFTER_ACTION_PERFORMED_DATA);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super AfterActionPerformedData> ss) {
        if (input.canPerformAction()) {
            ServicePlatform.get().getGameService().performAction();
            ss.onSuccess(input);
            return;
        }
        List<InvestigatorId> turnOrder = ServicePlatform.get().getInvestigators().getInvestigatorIdsInTurnOrder();
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        InvestigatorId next = activeInvestigatorId == null ? null
                : findNextWithActions(ServicePlatform.get().getInvestigators().getInvestigatorOrder(), turnOrder,
                activeInvestigatorId, ServicePlatform.get().getPerformedActions());
        if (next != null) {
            ServicePlatform.get().getGameService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(next);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getGameService().performAction();
            ServicePlatform.get().getGameService().release();
        } else {
            ServicePlatform.get().getGameService().hold();
            if (activeInvestigatorId != null && !turnOrder.isEmpty()) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(turnOrder.get(0));
            }
            ServicePlatform.get().getGameService().changeAndShowPhase();
            ServicePlatform.get().getGameService().initPhase();
            ServicePlatform.get().getGameService().release();
        }
        ss.onSuccess(input);
    }

    /**
     * Next investigator in game after the active one in turn order (wrapping around) that still has actions to perform.
     *
     * @param order         raw turn order, contains the active investigator even when it was just defeated
     * @param inGame        investigators still in game
     */
    static InvestigatorId findNextWithActions(List<InvestigatorId> order, List<InvestigatorId> inGame,
                                              InvestigatorId activeInvestigatorId, PerformedActionsRead performedActions) {
        int activeIndex = order.indexOf(activeInvestigatorId);
        for (int i = 1; i <= order.size(); i++) {
            InvestigatorId candidate = order.get((activeIndex + i + order.size()) % order.size());
            if (candidate != activeInvestigatorId
                    && inGame.contains(candidate)
                    && performedActions.canPerformAction(candidate)
                    && performedActions.wantsToPerformAction(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
