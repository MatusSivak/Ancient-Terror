package sk.sivak.eldritchhorror.core.eventtype.data;

import java.util.List;

/**
 * @author msivak
 */
public class TestResponseData {

    private List<Integer> rolledValues;

    public List<Integer> getRolledValues() {
        return rolledValues;
    }

    public void setRolledValues(List<Integer> rolledValues) {
        this.rolledValues = rolledValues;
    }

    @Override
    public String toString() {
        return "TestResponseData{" +
                "rolledValues=" + rolledValues +
                '}';
    }
}
