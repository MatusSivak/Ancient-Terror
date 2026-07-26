package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchSea10Encounter extends AbstractShubNiggurathResearchEncounter {

    public ResearchSea10Encounter() {
        super(10, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION,0,this::gainThisClue,() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().loseHealth(2);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.INTERNAL_INJURY);
            ServicePlatform.get().getService().release();
        }).withResearchFlavor().withTwoFailInfos().execute();
    }
}
