package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

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
        } else if (!input.isLast()) {
            ServicePlatform.get().getGameService().hold();
            ServicePlatform.get().getInvestigatorService().changeAndShowActiveInvestigator();
            ServicePlatform.get().getGameService().performAction();
            ServicePlatform.get().getGameService().release();
        } else {
            ServicePlatform.get().getGameService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator();
            ServicePlatform.get().getGameService().changeAndShowPhase();
//                investigatorService.showActiveInvestigator();
            ServicePlatform.get().getGameService().initPhase();
            ServicePlatform.get().getGameService().release();
        }
        ss.onSuccess(input);
    }
}
