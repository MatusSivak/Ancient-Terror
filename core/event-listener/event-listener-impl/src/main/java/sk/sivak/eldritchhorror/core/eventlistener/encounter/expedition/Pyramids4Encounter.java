package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.GainConditionTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Pyramids4Encounter extends AbstractExpeditionEncounter {

    public Pyramids4Encounter() {
        super(4, LocationId.THE_PYRAMIDS);
    }

    @Override
    protected void execute() {
        TestPassFlavorInfoFailFlavorInfoTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::gainArtifact,
                this::becomeDelayed).withExpeditionFlavor();

        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> loseHealth(1),
                new GainConditionTemplate(getTextBuilder(), ConditionId.DARK_PACT, this::improveSkillOfChoice));

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, -1, passTemplate, failTemplate).execute();
    }
}
