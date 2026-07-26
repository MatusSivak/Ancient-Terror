package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;

public class ResearchCity9Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity9Encounter() {
        super(9, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.CULTIST, this::gainThisClue, () -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA);
            moveToTheNearestSea();
            ServicePlatform.get().getService().release();
        }).withTwoFailInfos().withoutPassFlavor().execute();
    }
}
