package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome9Encounter extends AbstractLocationEncounter{

    public Rome9Encounter() {
        super(9, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                () -> {
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.LEG_INJURY);
                }
        ).withTwoFailInfos().execute();
    }

}
