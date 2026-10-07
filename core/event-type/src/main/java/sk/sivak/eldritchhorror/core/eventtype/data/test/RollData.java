package sk.sivak.eldritchhorror.core.eventtype.data.test;

/**
 * Rolling a single die
 *
 * @author msivak
 */
public class RollData {
    private sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo condition;
    public sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo getCondition() { return condition; }
    public void setCondition(sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo condition) { this.condition = condition; }
    private Integer minSuccessful;
    private Integer maxFailed;

    public Integer getMinSuccessful() {
        return minSuccessful;
    }

    public void setMinSuccessful(Integer minSuccessful) {
        this.minSuccessful = minSuccessful;
    }

    public Integer getMaxFailed() {
        return maxFailed;
    }

    public void setMaxFailed(Integer maxFailed) {
        this.maxFailed = maxFailed;
    }
}
