package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class EndOfMoveSpacesAction extends AbstractHookableAction<Object, Void> {

    public EndOfMoveSpacesAction() {
        super(BeforeAfterEvent.END_OF_MOVE_SPACES);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        ss.onSuccess(null);
    }
}
