package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class DisplayButtonsAction extends AbstractHookableAction<Object, Integer>{

    private String[] buttonTexts;

    public DisplayButtonsAction(String... buttonTexts) {
        super(BeforeAfterEvent.TW_DISPLAY_BUTTONS);
        this.buttonTexts = buttonTexts;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Integer> ss) {
        ServicePlatform.get().getTypewriterController().displayButtons(buttonTexts).subscribe(ss::onSuccess);
    }
}
