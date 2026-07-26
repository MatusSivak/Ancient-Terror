package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco15Encounter extends AbstractLocationEncounter {

    public SanFrancisco15Encounter() {
        super(15, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                },
                () -> {
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getTokenService().loseSanity(1);
                })
                .withTwoFailInfos().execute();
    }
}
