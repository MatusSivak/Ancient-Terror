package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome12Encounter extends AbstractLocationEncounter{

    public Rome12Encounter() {
        super(12, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED),
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED))
                .execute();
    }

}
