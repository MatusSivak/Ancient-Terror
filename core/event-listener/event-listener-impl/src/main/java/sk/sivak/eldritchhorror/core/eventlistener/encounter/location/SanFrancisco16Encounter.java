package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco16Encounter extends AbstractLocationEncounter {

    public SanFrancisco16Encounter() {
        super(16, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                () -> {
                    ServicePlatform.get().getInvestigatorService().improveSkill(Stat.OBSERVATION);
                },
                () -> {
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
                })
                .withTwoFailInfos().execute();
    }
}
