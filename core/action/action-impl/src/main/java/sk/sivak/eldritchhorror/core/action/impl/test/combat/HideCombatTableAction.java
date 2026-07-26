package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.HIDE_COMBAT_TABLE;

public class HideCombatTableAction extends AbstractHookableAction<Object, Void> {


    public HideCombatTableAction() {
        super(HIDE_COMBAT_TABLE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        ServicePlatform.get().getTestController().hideCombatTable().subscribe(() -> ss.onSuccess(null));
    }

    @Override
    public void setInput(Object input) {

    }
}
