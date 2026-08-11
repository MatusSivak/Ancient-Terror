package sk.sivak.eldritchhorror.core.constants.action;

public interface ActionButtonData {
    ActionButtonId getActionButtonId();
    String getTexturePath();
    String getActionName();
    float getScaleDownPercentage();
    boolean isEnabled();
    boolean needsMask();

    enum ActionButtonId {
        INVESTIGATOR,
        TRAVEL,
        REST,
        FOCUS,
        ACQUIRE_ASSETS,
        TICKET,
        TRADE,
        SKIP
    }
}
