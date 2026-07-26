package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class ShowLostInTimeAndSpaceAction extends AbstractHookableAction<InvestigatorId, InvestigatorId> {

    public ShowLostInTimeAndSpaceAction() {
        super(BeforeAfterEvent.SHOW_LOST_IN_TIME_AND_SPACE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        ServicePlatform.get().getGameController().showInvestigatorLostInTimeAndSpace(input).subscribe(() -> {
            BackgroundData backgroundData = new BackgroundData(BackgroundModelRead.BackgroundType.LOST_IN_TIME_AND_SPACE, null,
                    "encounter/background/lost_in_time_and_space.jpg");
            ServicePlatform.get().getModel().getBackgroundModel().pushBackground(input, backgroundData);
            ss.onSuccess(input);
        });
    }
}
