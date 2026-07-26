package sk.sivak.eldritchhorror.core.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

/**
 * @author msivak
 */
public class AbstractDirectEventAction<InOut> implements DirectEventAction<InOut> {

    private static final Logger logger = LogManager.getLogger(AbstractDirectEventAction.class);

    protected InOut input;
    protected DirectEvent directEvent;

    public AbstractDirectEventAction(DirectEvent directEvent) {
        this.directEvent = directEvent;
    }

    @Override
    public Single<InOut> execute() {
        Single<InOut> single = Single.create(sub -> {
            ServicePlatform.get().getService().hold();
            if (input == null) {
                ServicePlatform.get().getEventQueue().fireDirectEvent(directEvent, null);
            } else if (input.getClass().isArray()) {

                Object[] inputAsArray = (Object[]) this.input;
                for (Object arrayElement : inputAsArray) {
                    ServicePlatform.get().getEventQueue().fireDirectEvent(directEvent, arrayElement);
                }
            } else {
                ServicePlatform.get().getEventQueue().fireDirectEvent(directEvent, this.input);
            }

            beforeOnSuccess();
            ServicePlatform.get().getService().release();
            sub.onSuccess(this.input);
        });
        return single.cache();
    }

    public void beforeOnSuccess() {
    }

    @Override
    public void setInput(InOut input) {
        this.input = input;
    }

    @Override
    public DirectEvent getDirectEvent() {
        return directEvent;
    }
}
