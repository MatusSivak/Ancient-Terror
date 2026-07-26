package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome11Encounter extends AbstractLocationEncounter{

    public Rome11Encounter() {
        super(11, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL);
                },
                () -> ServicePlatform.get().getTokenService().loseSanity(2))
                .withTwoPassInfos().execute();
    }

}
