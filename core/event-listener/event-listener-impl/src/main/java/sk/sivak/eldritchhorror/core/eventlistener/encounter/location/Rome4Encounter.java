package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome4Encounter extends AbstractLocationEncounter{

    public Rome4Encounter() {
        super(4, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {

        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL)).execute();
    }

}
