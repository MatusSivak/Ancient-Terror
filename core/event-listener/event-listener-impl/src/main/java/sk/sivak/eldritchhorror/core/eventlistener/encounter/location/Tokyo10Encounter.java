package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class Tokyo10Encounter extends AbstractLocationEncounter{

    public Tokyo10Encounter() {
        super(10, LocationEncounter.LocationEncounterType.TOKYO);
    }

    @Override
    protected void execute() {
        new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                () -> {
                    ServicePlatform.get().getTokenService().loseSanity(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
                }
        ).withTwoFailInfos().execute();
    }

}
