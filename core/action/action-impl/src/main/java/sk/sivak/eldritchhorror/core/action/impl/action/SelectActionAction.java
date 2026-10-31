package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.basic.SkipActionListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author msivak
 */
public class SelectActionAction extends AbstractHookableAction<CollectAvailableActionsData, ActionPhaseAction> {

    private static final Logger logger = LogManager.getLogger(SelectActionAction.class);

    public SelectActionAction() {
        super(BeforeAfterEvent.SELECT_ACTION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super ActionPhaseAction> ss) {
        ActionPhaseAction automatic = automaticChoice(input.getActionPhaseActions());
        if (automatic != null) {
            ss.onSuccess(automatic);
            return;
        }
        Single<ActionPhaseAction> actionPhaseActionSingle = ServicePlatform.get().getGameController().selectAction(input.getActionPhaseActions());
        actionPhaseActionSingle
                .retry()
                .subscribe(ss::onSuccess);
    }

    /** The only enabled action, when it is the sole action or skipping the turn is all that is left. */
    static ActionPhaseAction automaticChoice(List<ActionPhaseAction> actions) {
        ActionPhaseAction enabled = null;
        for (ActionPhaseAction action : actions) {
            if (action.isDisabled()) continue;
            if (enabled != null) return null;
            enabled = action;
        }
        return enabled != null && (actions.size() == 1 || enabled instanceof SkipActionListener.SkipAction) ? enabled : null;
    }
}
