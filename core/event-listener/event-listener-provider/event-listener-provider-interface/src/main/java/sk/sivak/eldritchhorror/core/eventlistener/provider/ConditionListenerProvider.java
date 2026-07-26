package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.condition.AbstractConditionListener;

/**
 * @author msivak
 */
public interface ConditionListenerProvider {

    AbstractConditionListener<? extends ConditionInfo> getConditionListener(ConditionInfo conditionInfo);

    void unregister(ConditionInfo conditionInfo);

    void init();
}
