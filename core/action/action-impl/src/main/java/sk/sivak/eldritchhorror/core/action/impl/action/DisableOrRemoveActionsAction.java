package sk.sivak.eldritchhorror.core.action.impl.action;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;

public class DisableOrRemoveActionsAction extends AbstractDirectEventAction<CollectAvailableActionsData> {

    public DisableOrRemoveActionsAction() {
        super(DirectEvent.DISABLE_OR_REMOVE_ACTIONS);
    }
}
