package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney6Encounter extends AbstractLocationEncounter{

    public Sydney6Encounter() {
        super(6, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.STRENGTH),
                () -> ServicePlatform.get().getTokenService().loseSanity(1)
        ).withoutFailFlavor().execute();
    }

}
