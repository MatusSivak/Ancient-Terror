package sk.sivak.eldritchhorror.core.constants.encounter;

import com.badlogic.gdx.math.Vector2;

public class EncounterButtonData {
    private String uuid;
    private String buttonIcon;
    private String firstLine;
    private String secondLine;
    private boolean enabled = true;
    private String disabledReason;
    private boolean needsMask = false;
    private Vector2 offset = new Vector2();
    private float scaleDownPercentage = 1.10f;

    public EncounterButtonData(String uuid) {
        this.uuid = uuid;
    }

    public float getScaleDownPercentage() {
        return scaleDownPercentage;
    }

    public void setScaleDownPercentage(float scaleDownPercentage) {
        this.scaleDownPercentage = scaleDownPercentage;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }

    public boolean isNeedsMask() {
        return needsMask;
    }

    public void setNeedsMask(boolean needsMask) {
        this.needsMask = needsMask;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable(String disabledReason) {
        this.enabled = false;
        this.disabledReason = disabledReason;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public String getButtonIcon() {
        return buttonIcon;
    }

    public void setButtonIcon(String buttonIcon) {
        this.buttonIcon = buttonIcon;
    }

    public boolean isMultiline() {
        return secondLine != null;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    @Override
    public String toString() {
        return "EncounterButtonData{" +
                "enabled=" + enabled +
                ", disabledReason='" + disabledReason + '\'' +
                '}';
    }
}
