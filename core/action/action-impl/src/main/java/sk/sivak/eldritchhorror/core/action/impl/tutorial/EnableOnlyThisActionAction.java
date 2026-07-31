package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;

import java.util.Iterator;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;

public class EnableOnlyThisActionAction implements Action<Object, Object> {
    private final ActionButtonData.ActionButtonId actionButtonId;
    private DisableOrRemoveActionsListener disableOrRemoveActionsListener;

    public EnableOnlyThisActionAction(ActionButtonData.ActionButtonId actionButtonId) {
        this.actionButtonId = actionButtonId;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            disableOrRemoveActionsListener = new DisableOrRemoveActionsListener();
            ServicePlatform.get().getEventQueue().addDirectEventListener(disableOrRemoveActionsListener, DirectEvent.DISABLE_OR_REMOVE_ACTIONS);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(disableOrRemoveActionsListener);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }


    private class DisableOrRemoveActionsListener extends EventListenerImpl<CollectAvailableActionsData> {

        @Override
        public void onNotify(CollectAvailableActionsData eventData) {
            Iterator<ActionPhaseAction> iterator = eventData.getActionPhaseActions().iterator();
            while (iterator.hasNext()) {
                ActionPhaseAction next = iterator.next();
                if (next.getActionButtonData().getActionButtonId() == actionButtonId) {
                    continue;
                }
                ((AbstractActionPhaseAction)next).setDisabled(true);
                ((AbstractActionPhaseAction)next).setDisabledReason("tutorial.disabled.notAvailable");
            }
            ServicePlatform.get().getEventQueue().unregisterListener(this);
        }

        @Override
        public Class<CollectAvailableActionsData> getDataClass() {
            return CollectAvailableActionsData.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }
}
