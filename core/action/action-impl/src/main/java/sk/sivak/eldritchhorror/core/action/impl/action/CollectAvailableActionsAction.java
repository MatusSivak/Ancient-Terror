package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;

/**
 * @author msivak
 */
public class CollectAvailableActionsAction extends AbstractHookableAction<CollectAvailableActionsData, CollectAvailableActionsData> {

    private static final Logger logger = LogManager.getLogger(CollectAvailableActionsAction.class);

    public CollectAvailableActionsAction() {
        super(BeforeAfterEvent.FIND_ACTIONS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CollectAvailableActionsData> ss) {
        for (ActionPhaseAction actionPhaseAction : input.getActionPhaseActions()) {
            logger.info("Found action: " + actionPhaseAction.getName().replace('\n', ' ') + (actionPhaseAction.isDisabled() ? " (disabled)" : ""));
        }
        ss.onSuccess(input);
    }
}
