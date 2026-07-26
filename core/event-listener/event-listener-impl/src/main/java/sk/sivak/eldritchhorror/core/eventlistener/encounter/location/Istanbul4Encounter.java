package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul4Encounter extends AbstractLocationEncounter {

    public Istanbul4Encounter() {
        super(4, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                },
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.DEBT))
                .withoutPassFlavor().execute();
    }

}
