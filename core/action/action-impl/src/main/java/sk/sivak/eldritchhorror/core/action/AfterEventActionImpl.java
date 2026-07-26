package sk.sivak.eldritchhorror.core.action;

import rx.Single;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class AfterEventActionImpl<InOut> implements AfterEventAction<InOut> {

    private BeforeAfterEvent beforeAfterEvent;

    private InOut input;

    public void setBeforeAfterEvent(BeforeAfterEvent beforeAfterEvent) {
        this.beforeAfterEvent = beforeAfterEvent;
    }

    @Override
    public Single<InOut> execute() {
        Single<InOut> single = Single.create(sub -> {
            ServicePlatform.get().getEventQueue().fireAfterEvent(beforeAfterEvent, input);
            sub.onSuccess(input);
        });
        return single.cache();
    }

    @Override
    public void setInput(InOut input) {
        this.input = input;
    }

}
