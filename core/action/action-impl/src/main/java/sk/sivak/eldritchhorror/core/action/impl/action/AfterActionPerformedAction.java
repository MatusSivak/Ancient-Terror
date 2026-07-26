package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class AfterActionPerformedAction extends AbstractHookableAction<Object, AfterActionPerformedData> {

    private static final Logger logger = LogManager.getLogger(AfterActionPerformedAction.class);

    public AfterActionPerformedAction() {
        super(BeforeAfterEvent.AFTER_ACTION_PERFORMED);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super AfterActionPerformedData> ss) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        if (activeInvestigatorId == null) {
            AfterActionPerformedData afterActionPerformedData = new AfterActionPerformedData();
            afterActionPerformedData.setCanPerformAction(false);
            afterActionPerformedData.setLast(true);
            ss.onSuccess(afterActionPerformedData);
            return;
        }
        boolean canPerformAction = ServicePlatform.get().getPerformedActions().canPerformAction(activeInvestigatorId);
        boolean wantsToPerformAction = ServicePlatform.get().getPerformedActions().wantsToPerformAction(activeInvestigatorId);
        boolean activeLast = ServicePlatform.get().getInvestigators().isActiveLast();

        ServicePlatform.get().getGameController().showCityInfoLabels();
        AfterActionPerformedData afterActionPerformedData = new AfterActionPerformedData();
        afterActionPerformedData.setCanPerformAction(canPerformAction && wantsToPerformAction);
        afterActionPerformedData.setLast(activeLast);

        ss.onSuccess(afterActionPerformedData);
    }
}
