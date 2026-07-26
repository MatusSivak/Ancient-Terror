package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchCity4Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchCity4Encounter() {
        super(4, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0, () -> {
            gainThisClue();
        }, () -> {
            ServicePlatform.get().getService().hold();
            moveToTheNearestWilderness();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA);
            ServicePlatform.get().getService().release();
        }).withResearchFlavor().withTwoFailInfos().execute();
    }
}
