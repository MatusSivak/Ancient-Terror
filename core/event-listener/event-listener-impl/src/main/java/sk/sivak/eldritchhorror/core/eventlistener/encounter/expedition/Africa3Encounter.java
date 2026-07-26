package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Africa3Encounter extends AbstractExpeditionEncounter {

    public Africa3Encounter() {
        super(3, LocationId.THE_HEART_OF_AFRICA);
    }

    @Override
    protected void execute() {
        EncounterTemplate passInnerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> loseSanity(1)).withExpeditionFlavor();
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                this::gainArtifact, passInnerTemplate);

        EncounterTemplate failInnerTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                this::gainArtifact,
                () -> loseHealth(2)).withExpeditionFlavor();

        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> gainCondition(ConditionId.LEG_INJURY), failInnerTemplate);

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
