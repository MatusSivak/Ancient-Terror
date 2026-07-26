package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class ReadInputAction extends AbstractHookableAction<Object, String> {

    private final String label;

    public ReadInputAction(String label) {
        super(BeforeAfterEvent.ENCOUNTER_READ_INPUT);
        this.label = label;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super String> ss) {
        ServicePlatform.get().getTypewriterController().readInput(label).subscribe(ss::onSuccess);
    }
}
