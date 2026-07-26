package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;

public class ResearchWilderness1Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness1Encounter() {
        super(1, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.GOAT_SPAWN, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA);
        }).withTwoPassInfos().withoutPassFlavor().execute();
    }
}
