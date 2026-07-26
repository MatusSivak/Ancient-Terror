package sk.sivak.eldritchhorror.core.action.impl.action;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

public class PerformActionStartAction extends AbstractDirectEventAction<Object> {

    public PerformActionStartAction() {
        super(DirectEvent.PERFORM_ACTION_START);
    }
}
