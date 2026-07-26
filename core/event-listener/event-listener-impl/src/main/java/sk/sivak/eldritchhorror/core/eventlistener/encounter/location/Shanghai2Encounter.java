package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Shanghai2Encounter extends AbstractLocationEncounter{

    public Shanghai2Encounter() {
        super(2, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                () -> {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                },
                () -> ServicePlatform.get().getTokenService().loseHealth(1)
        ).withoutPassFlavor().execute();
    }
}
