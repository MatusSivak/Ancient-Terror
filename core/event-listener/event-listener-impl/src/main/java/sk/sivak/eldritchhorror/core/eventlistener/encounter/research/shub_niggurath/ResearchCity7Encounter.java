package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

public class ResearchCity7Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchCity7Encounter() {
        super(7, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0, () -> {
            ServicePlatform.get().getGameService().hold();
            gainThisClue();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getGameService().release();
        }, () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
        }).withResearchFlavor().withTwoPassInfos().execute();
    }
}
