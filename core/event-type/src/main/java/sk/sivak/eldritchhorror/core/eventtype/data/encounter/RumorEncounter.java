package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

public class RumorEncounter extends Encounter {

    private String firstLine;
    private String secondLine;


    public RumorEncounter(String secondLine) {
        super(EncounterType.RUMOR);
        this.firstLine = "Ongoing Rumor";
        this.secondLine = secondLine;
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
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setFirstLine(firstLine);
        encounterButtonData.setSecondLine(secondLine);
        encounterButtonData.setButtonIcon("STORM");
        return encounterButtonData;
    }
}
