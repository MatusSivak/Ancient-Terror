package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Antarctica2Encounter extends AbstractExpeditionEncounter {

    public Antarctica2Encounter() {
        super(2, LocationId.ANTARCTICA);
    }

    @Override
    protected void execute() {
        TestPassFlavorInfoFailFlavorInfoTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                this::gainArtifact,
                () -> gainCondition(ConditionId.HALLUCINATIONS)).withExpeditionFlavor();

        TestPassFlavorInfoFailFlavorInfoTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                this::gainTwoClues,
                () -> gainCondition(ConditionId.LEG_INJURY)).withExpeditionFlavor();

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, 0, passTemplate, failTemplate).execute();
    }
}
