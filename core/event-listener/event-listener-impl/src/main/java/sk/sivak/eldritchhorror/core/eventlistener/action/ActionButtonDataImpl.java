package sk.sivak.eldritchhorror.core.eventlistener.action;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;

public class ActionButtonDataImpl implements ActionButtonData {
    private ActionButtonId actionButtonId;
    private String texturePath;
    private String actionName;
    private float scaleDownPercentage;
    private boolean enabled;
    private boolean needsMask;

    @Override
    public ActionButtonId getActionButtonId() {
        return actionButtonId;
    }

    public void setActionButtonId(ActionButtonId actionButtonId) {
        this.actionButtonId = actionButtonId;
    }

    @Override
    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public float getScaleDownPercentage() {
        return scaleDownPercentage;
    }

    public void setScaleDownPercentage(float scaleDownPercentage) {
        this.scaleDownPercentage = scaleDownPercentage;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean needsMask() {
        return needsMask;
    }

    public void setNeedsMask(boolean needsMask) {
        this.needsMask = needsMask;
    }
}
