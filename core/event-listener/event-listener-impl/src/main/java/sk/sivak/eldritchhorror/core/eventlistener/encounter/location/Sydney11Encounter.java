package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney11Encounter extends AbstractLocationEncounter{

    public Sydney11Encounter() {
        super(11, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
        new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                () -> {
                    ServicePlatform.get().getTokenService().loseHealth(2);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.BACK_INJURY);
                }
        ).withTwoFailInfos().execute();
    }

}
