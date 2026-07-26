package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Tunguska3Encounter extends AbstractExpeditionEncounter {

    public Tunguska3Encounter() {
        super(3, LocationId.TUNGUSKA);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                this::gainArtifact,
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                        this::gainClue,
                        () -> loseSanity(1)).withExpeditionFlavor());

        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> gainCondition(ConditionId.INTERNAL_INJURY),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                        this::gainArtifact,
                        () -> loseHealth(2)).withExpeditionFlavor());

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
