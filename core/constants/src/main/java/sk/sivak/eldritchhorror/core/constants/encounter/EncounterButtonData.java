package sk.sivak.eldritchhorror.core.constants.encounter;

public class EncounterButtonData {
    private String uuid;
    private String buttonIcon;
    private String firstLine;
    private String secondLine;
    private boolean enabled = true;
    private String disabledReason;

    public EncounterButtonData(String uuid) {
        this.uuid = uuid;
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
