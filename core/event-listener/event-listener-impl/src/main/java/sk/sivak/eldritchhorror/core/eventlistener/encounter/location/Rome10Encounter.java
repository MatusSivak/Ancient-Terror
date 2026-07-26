package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome10Encounter extends AbstractLocationEncounter{

    public Rome10Encounter() {
        super(10, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL),
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED))
                .execute();
    }

}
