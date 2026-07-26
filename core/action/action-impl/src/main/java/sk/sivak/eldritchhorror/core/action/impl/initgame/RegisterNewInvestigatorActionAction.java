package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class RegisterNewInvestigatorActionAction implements Action<InvestigatorId, InvestigatorId> {

    private static final Logger logger = LogManager.getLogger(RegisterNewInvestigatorActionAction.class);

    private InvestigatorId input;

    @Override
    public Single<InvestigatorId> execute() {
        return Single.create(sub -> {
            logger.info("Registering special action for: " + input);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(
                    ServicePlatform.get().getActionListenerProvider().getInvestigatorActionListener(input), BeforeAfterEvent.FIND_ACTIONS);
            sub.onSuccess(input);
        });
    }

    @Override
    public void setInput(InvestigatorId input) {
        this.input = input;
    }
}
