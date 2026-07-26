package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco12Encounter extends AbstractLocationEncounter{

    public SanFrancisco12Encounter() {
        super(12, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.OBSERVATION),
                () -> {
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getTokenService().loseSanity(1);
                }).withTwoFailInfos().execute();
    }
}
