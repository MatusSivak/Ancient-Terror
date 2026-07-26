package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul9Encounter extends AbstractLocationEncounter{

    public Istanbul9Encounter() {
        super(9, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.INFLUENCE),
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.LEG_INJURY)).execute();
    }

}
