package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul6Encounter extends AbstractLocationEncounter {

    public Istanbul6Encounter() {
        super(6, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE)).execute();
    }

}
