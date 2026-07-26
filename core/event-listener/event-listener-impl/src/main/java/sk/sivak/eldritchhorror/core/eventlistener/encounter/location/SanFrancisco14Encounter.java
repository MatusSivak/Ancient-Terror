package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco14Encounter extends AbstractLocationEncounter{

    public SanFrancisco14Encounter() {
        super(14, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                },
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE)).execute();
    }
}
