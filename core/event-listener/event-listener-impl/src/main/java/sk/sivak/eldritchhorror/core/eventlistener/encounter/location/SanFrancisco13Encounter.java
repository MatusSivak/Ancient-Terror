package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco13Encounter extends AbstractLocationEncounter{

    public SanFrancisco13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.OBSERVATION),
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,
                        () -> ServicePlatform.get().getTokenService().loseSanity(2)))
                .execute();

    }
}
