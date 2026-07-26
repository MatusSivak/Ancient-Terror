package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Shanghai7Encounter extends AbstractLocationEncounter{

    public Shanghai7Encounter() {
        super(7, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        new BecomeDelayedPassFlavorInfoTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
        }).execute();
    }
}
