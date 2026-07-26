package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul15Encounter extends AbstractLocationEncounter{

    public Istanbul15Encounter() {
        super(15, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                },
                () -> {
                    ServicePlatform.get().getTokenService().loseSanity(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
                }).withTwoFailInfos().execute();
    }

}
