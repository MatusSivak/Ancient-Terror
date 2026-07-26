package sk.sivak.eldritchhorror.core.eventlistener.condition;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueRead;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;

import java.util.List;

public abstract class AbstractConditionListener<T extends ConditionInfo> implements ConditionListener {

    private static final Logger logger = LogManager.getLogger(AbstractConditionListener.class);

    protected T conditionInfo;
    protected InvestigatorId investigatorId;
    protected GainedCardData gainedCardData;

    protected abstract List<EventListener> getEventListeners();

    public void register(ConditionInfo conditionInfo, InvestigatorId investigatorId) {
        this.conditionInfo = (T) conditionInfo;
        this.investigatorId = investigatorId;
        logger.info("Registering condition '" + conditionInfo.getName() + "' for " + investigatorId);
        register();
    }

    protected EventQueueRead getEventQueue() {
        return ServicePlatform.get().getEventQueue();
    }

    protected abstract void register();

    public void unregister() {
        for (EventListener eventListener : getEventListeners()) {
            getEventQueue().unregisterListener(eventListener);
        }
        logger.info("Listeners for condition '" + conditionInfo.getId() + "' and investigator '" + investigatorId + "' unregistered");
    }

    protected boolean isOwner() {
        return investigatorId.equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
    }

    protected AbstractConditionBackListener findConditionBackListener() {
        String conditionBackClassName = conditionInfo.getId().getBackConditionClassName(conditionInfo.getConditionBack().getConditionBackId());
        String conditionBackListenerClassName = conditionBackClassName
                .replace(
                        "sk.sivak.eldritchhorror.core.constants.condition",
                        "sk.sivak.eldritchhorror.core.eventlistener.back.condition")
                .replace(
                        "ConditionBack",
                        "ConditionBackListener");
        try {
//            conditionBackListenerClassName = "sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned.PoisonedConditionBackListener4";
            return (AbstractConditionBackListener) Class.forName(conditionBackListenerClassName)
                    .getConstructor(ConditionInfo.class).newInstance(conditionInfo);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setEventData(GainedCardData gainedCardData) {
        this.gainedCardData = gainedCardData;
    }
}
