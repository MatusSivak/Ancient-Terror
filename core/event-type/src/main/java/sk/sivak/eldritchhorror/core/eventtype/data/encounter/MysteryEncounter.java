package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

public class MysteryEncounter extends Encounter {

    private String firstLine;
    private String secondLine;
    private String buttonIcon;


    public MysteryEncounter(String secondLine) {
        super(EncounterType.MYSTERY);
        this.firstLine = "Active Mystery";
        this.secondLine = secondLine;
        this.buttonIcon = "encounter/redpin.png";
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

    public void setButtonIcon(String buttonIcon) {
        this.buttonIcon = buttonIcon;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setFirstLine(firstLine);
        encounterButtonData.setSecondLine(secondLine);
        encounterButtonData.setButtonIcon(buttonIcon);
        return encounterButtonData;
    }
}
