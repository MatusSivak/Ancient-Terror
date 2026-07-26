package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

import java.util.UUID;

public class Encounter {
    private EncounterType encounterType;
    private String uuid;
    private EncounterButtonData encounterButtonData;
    private boolean enabled = true;
    private String disabledReason;

    public Encounter(EncounterType encounterType) {
        this.encounterType = encounterType;
        this.uuid = UUID.randomUUID().toString();
    }

    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    public void disable(String disabledReason) {
        this.enabled = false;
        this.disabledReason = disabledReason;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public EncounterButtonData getEncounterButtonData() {
        return encounterButtonData;
    }

    public void setEncounterButtonData(EncounterButtonData encounterButtonData) {
        this.encounterButtonData = encounterButtonData;
    }

    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = new EncounterButtonData(getUuid());
        if (!enabled) {
            encounterButtonData.disable(disabledReason);
        }
        return encounterButtonData;
    }

    protected String getDisabledReason() {
        return disabledReason;
    }

    protected boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "Encounter{" +
                "encounterType=" + encounterType +
                ", encounterButtonData=" + encounterButtonData +
                '}';
    }
}
