package sk.sivak.eldritchhorror.core.constants.condition;

import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.card.CardIdUtils;

public enum ConditionId implements CardId {
    DEBT,
    BLESSED,
    RIGHTEOUS,
    LEG_INJURY,
    INTERNAL_INJURY,
    BACK_INJURY,
    HEAD_INJURY,
    HALLUCINATIONS,
    AMNESIA,
    PARANOIA,
    DETAINED,
    CURSED,
    LOST_IN_TIME_AND_SPACE,
    DARK_PACT,
    POISONED;

    @Override
    public String asString() {
        return CardIdUtils.asString(this);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println(LEG_INJURY.asString());
        for (ConditionId conditionId : ConditionId.values()) {
            String className = conditionId.getConditionClassName();
            String backConditionClassName = conditionId.getBackConditionClassName(3);
            Class.forName(className).newInstance();
            System.out.println(className);
            System.out.println(backConditionClassName);
        }
    }

    public String getConditionClassName() {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.condition.")
                .append(name().replaceAll("_", "").toLowerCase())
                .append(".");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        }
        stringBuilder.append("Condition");
        return stringBuilder.toString();
    }

    public String getBackConditionClassName(int suffix) {
        String[] split = name().toLowerCase().split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("sk.sivak.eldritchhorror.core.constants.condition.")
                .append(name().replaceAll("_", "").toLowerCase())
                .append(".");
        for (String s : split) {
            stringBuilder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        }
        stringBuilder.append("ConditionBack");
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
}
