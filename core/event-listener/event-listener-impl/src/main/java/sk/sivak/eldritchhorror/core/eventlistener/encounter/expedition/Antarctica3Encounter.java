package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Antarctica3Encounter extends AbstractExpeditionEncounter {

    public Antarctica3Encounter() {
        super(3, LocationId.ANTARCTICA);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                this::gainArtifact,
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                        () -> loseSanity(1)).withExpeditionFlavor());

        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> gainCondition(ConditionId.LEG_INJURY),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                        this::gainArtifact,
                        () ->  loseHealth(2)).withExpeditionFlavor());

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
