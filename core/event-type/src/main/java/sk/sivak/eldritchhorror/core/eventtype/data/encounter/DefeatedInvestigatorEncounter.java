package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class DefeatedInvestigatorEncounter extends Encounter{

    private final InvestigatorId investigatorId;
    private final boolean health;

    public DefeatedInvestigatorEncounter(InvestigatorId investigatorId, boolean health) {
        super(EncounterType.DEFEATED_INVESTIGATOR);
        this.investigatorId = investigatorId;
        this.health = health;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public boolean isHealth() {
        return health;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setButtonIcon("investigator/"+investigatorId.name()+".png");
        encounterButtonData.setFirstLine("The " + investigatorId.toString());
        encounterButtonData.setSecondLine(health ? "Crippled" : "Insane");
        return encounterButtonData;
    }
}
