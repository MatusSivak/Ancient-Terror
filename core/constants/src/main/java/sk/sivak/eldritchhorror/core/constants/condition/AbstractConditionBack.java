package sk.sivak.eldritchhorror.core.constants.condition;

public class AbstractConditionBack implements ConditionBack {
    protected String title;
    protected String flavorText;
    protected String effectText;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getFlavorText() {
        return flavorText;
    }

    @Override
    public String getEffectText() {
        return effectText;
    }

    @Override
    public int getConditionBackId() {
        return Integer.valueOf(getClass().getSimpleName().split("ConditionBack")[1]);
    }
}
