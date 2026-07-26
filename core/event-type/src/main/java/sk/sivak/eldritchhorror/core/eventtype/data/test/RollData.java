package sk.sivak.eldritchhorror.core.eventtype.data.test;

/**
 * Rolling a single die
 *
 * @author msivak
 */
public class RollData {
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
