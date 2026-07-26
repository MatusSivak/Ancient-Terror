package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

public class ConditionEncounter extends Encounter{

    private ConditionInfo conditionInfo;

    public ConditionEncounter(ConditionInfo conditionInfo) {
        super(EncounterType.CONDITION);
        this.conditionInfo = conditionInfo;
    }

    public ConditionInfo getConditionInfo() {
        return conditionInfo;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setButtonIcon("card/condition/"+conditionInfo.getId()+".jpg");
        encounterButtonData.setFirstLine(conditionInfo.getName());
        encounterButtonData.setSecondLine(null);
        return encounterButtonData;
    }

    @Override
    public String toString() {
        return "ConditionEncounter [" +
                "conditionInfo=" + conditionInfo +
                "] " + super.toString();
    }
}
