package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class HideLostInTimeAndSpaceAction extends AbstractHookableAction<InvestigatorId, InvestigatorId> {

    public HideLostInTimeAndSpaceAction() {
        super(BeforeAfterEvent.HIDE_LOST_IN_TIME_AND_SPACE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        ServicePlatform.get().getGameController().hideInvestigatorLostInTimeAndSpace(input).subscribe(() -> {
            ss.onSuccess(input);
        });
    }
}
