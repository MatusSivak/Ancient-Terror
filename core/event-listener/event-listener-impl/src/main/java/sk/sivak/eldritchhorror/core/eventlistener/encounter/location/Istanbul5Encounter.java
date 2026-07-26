package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul5Encounter extends AbstractLocationEncounter {

    public Istanbul5Encounter() {
        super(5, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new BecomeDelayedTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
        }).execute();
    }

}
