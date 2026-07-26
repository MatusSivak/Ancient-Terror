package sk.sivak.eldritchhorror.core.action.impl.initgame;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.provider.ActionListenerProvider;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueWrite;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class RegisterBasicActionsAction implements Action<Void, Void> {

    @Override
    public Single<Void> execute() {
        return Single.fromCallable(() -> {
            EventQueueWrite eventQueue = ServicePlatform.get().getEventQueue();
            ActionListenerProvider actionListenerProvider = ServicePlatform.get().getActionListenerProvider();
            eventQueue.addBeforeEventListener(actionListenerProvider.getRestActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            eventQueue.addBeforeEventListener(actionListenerProvider.getTravelActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            eventQueue.addBeforeEventListener(actionListenerProvider.getAcquireAssetsActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            eventQueue.addBeforeEventListener(actionListenerProvider.getFocusActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            eventQueue.addBeforeEventListener(actionListenerProvider.getSkipActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            eventQueue.addBeforeEventListener(actionListenerProvider.getTicketActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            eventQueue.addBeforeEventListener(actionListenerProvider.getTradeActionListener(), BeforeAfterEvent.FIND_ACTIONS);
            return null;
        });
    }

    @Override
    public void setInput(Void input) {
        // ignored
    }
}
