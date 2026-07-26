package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney15Encounter extends AbstractLocationEncounter{

    public Sydney15Encounter() {
        super(15, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
        new BecomeDelayedPassFlavorInfoTemplate(getTextBuilder(),() ->
                ServicePlatform.get().getInvestigatorService().improveSkill(Stat.STRENGTH)
        ).withoutPassFlavor().execute();
    }

}
