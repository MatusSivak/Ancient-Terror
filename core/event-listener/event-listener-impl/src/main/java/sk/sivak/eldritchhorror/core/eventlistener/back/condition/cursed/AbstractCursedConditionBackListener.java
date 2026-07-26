package sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;

public abstract class AbstractCursedConditionBackListener extends AbstractConditionBackListener {

    public AbstractCursedConditionBackListener(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    public void executeWhole() {
        onFlip(null);
    }
}
