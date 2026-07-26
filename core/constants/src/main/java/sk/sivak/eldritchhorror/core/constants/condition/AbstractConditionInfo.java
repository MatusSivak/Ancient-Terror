package sk.sivak.eldritchhorror.core.constants.condition;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractConditionInfo implements ConditionInfo {

    protected Set<ConditionTrait> traits = new HashSet<>();
    private ConditionBack conditionBack;
    private boolean disabled;

    public Set<ConditionTrait> getTraits() {
        return traits;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }

    @Override
    public ConditionBack getConditionBack() {
        return conditionBack;
    }

    public void setConditionBack(ConditionBack conditionBack) {
        this.conditionBack = conditionBack;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

}
