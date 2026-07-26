package sk.sivak.eldritchhorror.core.eventlistener.condition;

import sk.sivak.eldritchhorror.core.constants.condition.darkpact.DarkPactCondition;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

import java.util.Collections;
import java.util.List;

public class DarkPactListener extends AbstractConditionListener<DarkPactCondition> {

    private ConditionReckoningListener reckoningListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(reckoningListener);
    }

    @Override
    protected void register() {
        reckoningListener = new ConditionReckoningListener(DarkPactListener.this);
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }
}
