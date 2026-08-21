package sk.sivak.eldritchhorror.core.constants.action;

import com.badlogic.gdx.math.Vector2;

public interface ActionButtonData {
    ActionButtonId getActionButtonId();
    String getTexturePath();
    String getActionName();
    float getScaleDownPercentage();
    boolean isEnabled();
    boolean needsMask();
    Vector2 getOffset();

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
