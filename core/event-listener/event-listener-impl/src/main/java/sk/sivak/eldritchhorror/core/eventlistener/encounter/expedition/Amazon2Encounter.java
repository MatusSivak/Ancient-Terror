package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Amazon2Encounter extends AbstractExpeditionEncounter {

    public Amazon2Encounter() {
        super(2, LocationId.THE_AMAZON);
    }

    @Override
    protected void execute() {
        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::gainArtifact,
                () -> loseSanity(2)).withExpeditionFlavor();

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                this::gainTwoClues,
                () -> gainCondition(ConditionId.BACK_INJURY)).withExpeditionFlavor();

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, 0, passTemplate, failTemplate).execute();
    }
}
